package com.redhat.fuse.boosters.rest.http.router;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

/**
 * Global REST configuration
 *
 */
@Component
public class RestConfiguration extends RouteBuilder {

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
		
	}

}
