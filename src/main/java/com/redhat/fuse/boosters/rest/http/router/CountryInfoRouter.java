package com.redhat.fuse.boosters.rest.http.router;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.rest.RestParamType;
import org.apache.camel.spi.DataFormatFactory;
import org.springframework.stereotype.Component;

import com.redhat.fuse.boosters.rest.http.CustomThreadPoolConfig;
import com.redhat.fuse.boosters.rest.http.model.Country;
import com.redhat.fuse.boosters.rest.http.model.CountryNotFoundException;
import com.redhat.fuse.boosters.rest.http.router.process.AddISOCode;
import com.redhat.fuse.boosters.rest.http.router.process.AggregateAllCountryInfo;
import com.redhat.fuse.boosters.rest.http.router.process.AggregateCountries;
import com.redhat.fuse.boosters.rest.http.router.process.GenericExceptionHandler;
import com.redhat.fuse.boosters.rest.http.router.process.PrepareRequestJAXB;
import com.redhat.fuse.boosters.rest.http.router.process.ResponseMapping;


/**
 *  Camel REST DSL route that implements country info service.
 * 
 */
@Component
public class CountryInfoRouter extends RouteBuilder {
	
	@Resource(name="jaxb-dataformat-factory") // defined in camel-jaxb-starter
	DataFormatFactory jaxbFactory;

    @Override
    public void configure() throws Exception {
    	
        /*
         * global error handling 
         */ 
        onException(Exception.class)
        	.handled(true)	// stop exception propagation
        	.bean(GenericExceptionHandler.class)
        	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));
		
		 
    	
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
        	.route()
        	.onException(CountryNotFoundException.class)
				.handled(true)	// stop exception propagation
				.bean(GenericExceptionHandler.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
			.end()
        	.to("direct:getCountryInfo");
        
        
        /*
         * '/full/country' - gets full country info (with additional economic info)
         */
        rest("/full/country")
	    	// inject path param in header as 'country_name' 
	    	.get("/{country_name}")
	    	// swagger definitions
	    	.id("GetInfo").description("Get full country info")
	    	.param().name("country_name").description("Country name").endParam()
	    	.outType(Country.class)
	    	.responseMessage().code(500).message("Internal error").endResponseMessage()
	    	.responseMessage().code(404).message("Country not found").endResponseMessage()
	    	// forward to route 'getCountryInfo'
	    	.route()
	    	// intercept exception after stopOnException()
	    	.onException(CountryNotFoundException.class)
				.handled(true)	// stop exception propagation
				.bean(GenericExceptionHandler.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
			.end()
        	.multicast(new AggregateAllCountryInfo())
        	.parallelProcessing().executorServiceRef(CustomThreadPoolConfig.ID).stopOnException()
        		.to("direct:getCountryInfo")
        		.pipeline()
        			.setHeader("economic_country_name", header("country_name")) // adapter
        			.to("direct:getCountryEconomicInfo")
        		.end();
        	
        
        
        /*
         * '/countries?name={}&name={}' - gets info for a list of countries 
         */
        rest()
        	// inject path param in header as 'name'
        	.get("/countries?name={}")
        	.id("GetCountriesInfo").description("Get multiple country info")
        	// swagger doc for 'ndg' parameter
	        .param().name("name").required(true).type(RestParamType.query).dataType("array").arrayType("string").endParam()
        	// forward to route 'getCountryInfo'
        	.route()
        	// mute exception from 'direct:getCountryInfo'
        	.onException(CountryNotFoundException.class)
				.handled(true)	// stop exception propagation
		    	.setBody(constant(null)) // empty body
			.end()
        	// use splitter/aggregator pattern
        	.split(header("name"), new AggregateCountries())
        	.parallelProcessing().executorServiceRef(CustomThreadPoolConfig.ID)
        		.setHeader("country_name", bodyAs(String.class))  // adapter
        		.to("direct:getCountryInfo"); 
        		
        
        
        /*
         * internal camel routes
         */
        
        
        // auto-configured JAXB data format
        JaxbDataFormat jaxb = (JaxbDataFormat) jaxbFactory.newInstance();
        
        from("direct:getCountryInfo").routeId("GetCountryInfo")
        
        	// let exception propagate to caller route
        	.errorHandler(noErrorHandler())
        	
        	// create request bean (jaxb annotated)
        	.bean(PrepareRequestJAXB.class, "evaluate")
        	// converts jaxb bean into xml
        	.marshal(jaxb)
        	
        	// invoke remote soap service using 'spring-ws'. It's responsible of adding 
        	// soap envelope to xml request
        	.to("spring-ws:{{ws.uri}}") //.log("Response XML: ${body}")
        	
        	// trick: convert xml response into java Map (simpler to parse and without namespaces)  
        	.unmarshal().jacksonxml(java.util.Map.class) //.log("Response Map: ${body}")
        	
        	// create REST response (this is actual mapping logic)
        	.bean(ResponseMapping.class, "evaluate")
        	// enrich response with info from in-memory cache
        	.enrich("bean:countryISOCodeCacheService?method=lookupCode", new AddISOCode());
        	
         	
      
        from("direct:getCountryEconomicInfo").routeId("GetCountryEconomicInfo")
        
        	// let exception propagate to caller route
        	.errorHandler(noErrorHandler())
        
	        // create request bean (jaxb annotated)
	    	.bean(PrepareRequestJAXB.class, "evaluateEconomic")
	    	// converts jaxb bean into xml
	    	.marshal(jaxb)
	    	
	    	// invoke remote soap service using 'spring-ws'. It's responsible of adding 
	    	// soap envelope to xml request
	    	.to("spring-ws:{{ws.uri}}")  //.log("Response XML: ${body}")
	    	
	    	// trick: convert xml response into java Map (simpler to parse and without namespaces)  
	    	.unmarshal().jacksonxml(java.util.Map.class) //.log("Response Map: ${body}")
	    	
	    	// create REST response (this is actual mapping logic)
        	.bean(ResponseMapping.class, "evaluateEconomic");
        
    }

}