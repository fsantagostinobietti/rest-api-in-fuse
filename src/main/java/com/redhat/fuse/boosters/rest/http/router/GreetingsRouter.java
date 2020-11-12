package com.redhat.fuse.boosters.rest.http.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.redhat.fuse.boosters.rest.http.model.Greetings;

/**
 * A simple Camel REST DSL route that implements the greetings service.
 * 
 */
@Component
public class GreetingsRouter extends RouteBuilder {

	
    @Override
    public void configure() throws Exception {
       
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
        
      
    }

}