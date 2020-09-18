package com.redhat.fuse.boosters.rest.http.router;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.redhat.fuse.boosters.rest.http.model.Country;
import com.redhat.fuse.boosters.rest.http.model.Error;
import com.redhat.fuse.boosters.rest.http.model.GetCountryRequest;
import com.redhat.fuse.boosters.rest.http.model.Greetings;
import com.redhat.fuse.boosters.rest.http.router.process.AddISOCode;
import com.redhat.fuse.boosters.rest.http.router.process.GenericExceptionHandler;
import com.redhat.fuse.boosters.rest.http.router.process.InvalidInputError;
import com.redhat.fuse.boosters.rest.http.router.process.PrepareRequestJAXB;
import com.redhat.fuse.boosters.rest.http.router.process.ResponseMapping;
import com.redhat.fuse.boosters.rest.http.service.CountryISOCodeCache;

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
        onException(Exception.class)
        	.handled(true)	// stop exception propagation
        	.transform(new GenericExceptionHandler())
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
        	.get("/{country_name}")
        	// swagger definitions
        	.id("GetInfo").description("Get country related Info")
        	.param().name("country_name").description("Country name").endParam()
        	.outType(Country.class)
        	.responseMessage().code(500).message("Internal error").endResponseMessage()
        	.responseMessage().code(404).message("Country not found").endResponseMessage()
        	// forward to route 'getCountryInfo'
        	.route().log("Country name: ${header.country_name}")
        	.to("direct:getCountryInfo");
        
        
        from("direct:getCountryInfo")
        	
        	// create request bean (jaxb annotated)
        	.transform(new PrepareRequestJAXB())
        	// Camel automatically translates jaxb bean into xml
        	.log("Request XML: ${body}")
        	
        	// invoke remote soap service using 'spring-ws'. It's responsible of adding 
        	// soap envelope to xml request
        	.to("spring-ws:"+WS_URI).log("Response XML: ${body}")
        	
        	// trick: convert xml response into java Map (simpler to parse and without namespaces)  
        	.unmarshal().jacksonxml(java.util.Map.class).log("Response Map: ${body}")
        	
        	.choice()
        		// empty response produce an error message
        		.when(body().isEqualTo("{}"))
	        		.transform(new InvalidInputError())
        			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
	        	// valid response
        		.otherwise()
	        		// create REST response (this is actual mapping logic)
		        	.transform(new ResponseMapping())
		        	// enrich response with info from in-memory cache
		        	.enrich("bean:countryISOCodeCacheService?method=lookupCode", new AddISOCode());
      
    }

}