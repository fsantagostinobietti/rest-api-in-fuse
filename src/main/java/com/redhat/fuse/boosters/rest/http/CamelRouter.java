package com.redhat.fuse.boosters.rest.http;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.redhat.fuse.boosters.rest.http.model.Country;
import com.redhat.fuse.boosters.rest.http.model.Error;
import com.redhat.fuse.boosters.rest.http.model.GetCountryRequest;
import com.redhat.fuse.boosters.rest.http.model.Greetings;

/**
 * A simple Camel REST DSL route that implements the greetings service.
 * 
 */
@Component
public class CamelRouter extends RouteBuilder {

	@Value("${ws.uri}")
	String WS_URI;
	
    @Override
    public void configure() throws Exception {

        /*
         * endpoints REST configuration
         */
        restConfiguration()
        	// consume/produce JSON data
        	.bindingMode(RestBindingMode.json)
        	.skipBindingOnErrorCode(false) // I always want json binding 
        	// swagger definition (camel-swagger-java module)
        	.apiContextPath("/api-doc")
        		.apiProperty("base.path", "/camel");  // inform swagger of api base path
        		/*.apiProperty("api.path", "/")
                .apiProperty("api.title", "Greeting REST API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiProperty("host", "")
                .apiContextRouteId("doc-api")
            	.component("servlet")*/
        
        /*
         * global error handling 
         */
        onException(Exception.class).handled(true).transform(new Expression() {
			@Override
			public <T> T evaluate(Exchange exchange, Class<T> type) {
				// retrieve the caught message
				Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
				// error response
				return (T) new Error( cause.toString() );
			}
		})
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));
        
        /*
         * '/greetings' - hello world endpoint
         */
        rest("/greetings").description("Greeting to {name}")
            .get("/{name}").outType(Greetings.class)
                .route().routeId("greeting-api")
                .to("direct:greetingsImpl");
        from("direct:greetingsImpl").description("Greetings REST service implementation route")
            .streamCaching()
            .to("bean:greetingsService?method=getGreetings");     
        
        
        /*
         * '/country' - gets few info for the named country 
         */
        rest("/country")
        	// inject path param in header as 'country_name' 
        	.get("/{country_name}").outType(Country.class).route().log("Country name: ${header.country_name}")
        	// forward to route 'countryInfo'
        	.to("direct:countryInfo");
        
        
        from("direct:countryInfo")
        	
        	// create request bean (jaxb annotated)
        	.transform(new Expression() {
				public <T> T evaluate(Exchange exchange, Class<T> type) {
					String countryName = (String) exchange.getIn().getHeader("country_name");
					GetCountryRequest request = new GetCountryRequest();
					request.setName(countryName);
					return (T) request;
				}
			})
        	// Camel automatically translates jaxb bean into xml
        	.log("Request XML: ${body}")
        	
        	// invoke remote soap service using 'spring-ws'. It's responsible of adding 
        	// soap envelope to xml request
        	.to("spring-ws:"+WS_URI).log("Response XML: ${body}")
        	
        	// trick: convert xml response into java Map (simpler and without namespaces)  
        	.unmarshal().jacksonxml(java.util.Map.class).log("Response Map: ${body}")
        	
        	.choice()
        		// empty response produce an error message
        		.when(body().isEqualTo("{}"))
	        		.transform(new Expression() {
	        			public <T> T evaluate(Exchange exchange, Class<T> type) {
	        				// error response
	        				return (T) new Error("Invalid country! Hint: try 'Spain', 'Poland' or 'United Kingdom'");
	        			}
	        		})
        			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
	        	// valid response
        		.otherwise()
	        		// create REST response (this is actual mapping logic)
		        	.transform(new Expression() {
						public <T> T evaluate(Exchange exchange, Class<T> type) {
							Map<?,?> response = exchange.getIn().getBody(java.util.Map.class);
							Map<?,?> country = (Map<?,?>) response.get("country");
							
							Country countryBean = new Country();
							countryBean.setName((String)country.get("name"));
							countryBean.setPopulation(Long.parseLong((String) country.get("population")));
							return (T) countryBean;
						}
					});
      
    }

}