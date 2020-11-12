package com.redhat.fuse.boosters.rest.http;

import static org.junit.Assert.assertTrue;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort; //org.springframework.boot.context.embedded.LocalServerPort; <-- Spring Boot 1.5
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.redhat.fuse.boosters.rest.http.model.Country;
import com.redhat.fuse.boosters.rest.http.model.Greetings;
import com.redhat.fuse.boosters.rest.http.model.Error;

/**
 * Unit tests for REST endpoints
 *
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockEndpointsAndSkip("direct:getCountryInfo|spring-ws:{{ws.uri}}")
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void greetingsShouldReturnFallbackMessage() throws Exception {
        Assert.assertEquals( "Hello, jacopo", this.restTemplate.getForObject("http://localhost:" + port + "/camel/greetings/jacopo", Greetings.class).getGreetings());
    }

    @Test
    public void healthShouldReturnOkMessage() throws Exception {
        Assert.assertEquals( "{\"status\":\"UP\"}", this.restTemplate.getForObject("http://localhost:" + port + "/actuator/health", String.class));
    }
    
    
    @EndpointInject(uri = "mock:direct:getCountryInfo")
	MockEndpoint mockGetCountryInfo;
    
    
    public void countryInfoAsREST() throws Exception {
    	mockGetCountryInfo.expectedMessageCount(2);
    	
    	mockGetCountryInfo.whenAnyExchangeReceived(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				String countryName = (String) exchange.getIn().getHeader("country_name");
				if ("Spain".equals(countryName)) {
					Country outCountry = new Country();
					outCountry.setName("Spain");
					exchange.getIn().setBody(outCountry, Country.class);
				} else {
					exchange.getIn().setBody( new Error("Invalid Country"));
					exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
				}
				
			}
		});
    	
    	// invoke endpoint with valid country
    	ResponseEntity<Country> res = restTemplate.getForEntity("http://localhost:" + port + "/camel/country/Spain", Country.class);
    	// result validaton
    	assertTrue( res.getStatusCode()==HttpStatus.OK );
    	assertTrue( "Spain".equals(res.getBody().getName()) );
    	
    	// endpoint execution with invalid country
    	ResponseEntity<Error> resFaulty = restTemplate.getForEntity("http://localhost:" + port + "/camel/country/Italy", Error.class);
    	// result validaton
    	assertTrue( resFaulty.getStatusCode()==HttpStatus.NOT_FOUND );
    	
    	mockGetCountryInfo.assertIsSatisfied();
    }
}