package com.redhat.fuse.boosters.rest.http;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.EnableRouteCoverage;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.redhat.fuse.boosters.rest.http.model.Country;
import com.redhat.fuse.boosters.rest.http.model.Greetings;
import com.redhat.fuse.boosters.rest.http.service.CountryISOCodeCache;


/**
 * Unit test for (non-rest) camel routes
 *
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE) // do not provide any web environment
//If we want to reset the Camel context and mock endpoints between test methods automatically:
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@EnableRouteCoverage	// enable coverage stats for camel routes
@MockEndpointsAndSkip("spring-ws:*")
public class RoutesTest {
	
	//@Autowired
    //protected CamelContext camelContext;
	
	@Produce
	protected ProducerTemplate start;
	
	@Value("${csvfile.uri}")
	String csvFileURI;
	
	@Test
    public void testCacheReloaderRoute() throws Exception {
		start.sendBody(csvFileURI,
				"country,iso_code\n" + 
				"Italy,IT");
		
		TimeUnit.SECONDS.sleep(1); // wait for the route to execute
		
		assertTrue( "IT".equals(CountryISOCodeCache.lookupCode("Italy")) );
		assertNull( CountryISOCodeCache.lookupCode("Russia") );	// not found
		
		start.sendBody(csvFileURI,
				"country,iso_code\n" 
			  + "Italy,IT\n"
			  + "Russia,RU");
		
		TimeUnit.SECONDS.sleep(1); // wait for the route to execute
		
		assertTrue( "RU".equals(CountryISOCodeCache.lookupCode("Russia")) );
	}
	
	@Test
	public void testGreetingsRoute() throws Exception {
		Object empybody = null;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("name", "Adam");
		Greetings resp = start.requestBodyAndHeaders("direct:greetingsImpl", empybody , headers, Greetings.class );
		assertTrue( "Hello, Adam".equals(resp.getGreetings()) );
	}
	
	
	
	@EndpointInject(uri = "mock:spring-ws:{{ws.uri}}")
	MockEndpoint mockWs;
	
	@Test
	public void testCountryInfoRoute() throws Exception {
		
		mockWs.expectedMessageCount(1);
		
		// define mock response
		mockWs.whenAnyExchangeReceived(new Processor() {	
			@Override
			public void process(Exchange exchange) throws Exception {
				// mock response
				String outBody = "<ns2:getCountryResponse xmlns:ns2=\"http://spring.io/guides/gs-producing-web-service\">"
									+ "<ns2:country>"
										+ "<ns2:name>Spain</ns2:name>"
										+ "<ns2:population>46704316</ns2:population>"
										+ "<ns2:capital>Madrid</ns2:capital>"
										+ "<ns2:currency>EUR</ns2:currency>"
									+ "</ns2:country>"
								+ "</ns2:getCountryResponse>";
				exchange.getIn().setBody(outBody, String.class);
			}
		});
		
		// trigger route sending a message
		Object empybody = null;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("country_name", "Spain");
		Country country = start.requestBodyAndHeaders("direct:getCountryInfo", empybody, headers, Country.class);
		
		// result validaton
		mockWs.assertIsSatisfied();
		assertTrue( country.getPopulation()==46704316 );
	}
	
}
