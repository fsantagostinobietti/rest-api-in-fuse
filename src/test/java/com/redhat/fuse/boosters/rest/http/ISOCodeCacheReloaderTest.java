package com.redhat.fuse.boosters.rest.http;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.redhat.fuse.boosters.rest.http.service.CountryISOCodeCache;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE) // do not provide any web environment
//If we want to reset the Camel context and mock endpoints between test methods automatically:
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ISOCodeCacheReloaderTest {
	
	@Autowired
    protected CamelContext camelContext;
	
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
}
