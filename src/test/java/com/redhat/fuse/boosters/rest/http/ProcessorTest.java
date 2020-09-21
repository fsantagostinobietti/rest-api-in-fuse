package com.redhat.fuse.boosters.rest.http;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;

import com.redhat.fuse.boosters.rest.http.model.Country;
import com.redhat.fuse.boosters.rest.http.model.GetCountryRequest;
import com.redhat.fuse.boosters.rest.http.router.process.AddISOCode;
import com.redhat.fuse.boosters.rest.http.router.process.PrepareRequestJAXB;
import com.redhat.fuse.boosters.rest.http.router.process.ResponseMapping;

public class ProcessorTest {
	
	@Test
	public void testRequestJAXB() throws Exception {
		final String COUNTRY_NAME = "Spain";
		PrepareRequestJAXB process = new PrepareRequestJAXB();
		GetCountryRequest req = (GetCountryRequest) process.evaluate(COUNTRY_NAME);
		assertTrue( COUNTRY_NAME.equals(req.getName()) );
	}

	@Test
	public void testResponseMapping() throws Exception {
		 // build data
		 final String COUNTRY_NAME = "Spain";
		 final long POPULATION = 1000000;
		 HashMap<String, Object> country = new HashMap<String,Object>();
		 country.put("name", COUNTRY_NAME);
		 country.put("population", ""+POPULATION);
		 HashMap<String, Object> response = new HashMap<String,Object>();
		 response.put("country", country);
		 
		 ResponseMapping process = new ResponseMapping();
		 Country bean = process.evaluate(response);
		 
		 assertTrue(COUNTRY_NAME.equals(bean.getName()));
		 assertTrue(POPULATION==bean.getPopulation());
	}
	
	@Test
	public void testAddISOCode() throws Exception {
		final String ISO_CODE = "ES";
		// build Exchange data
		DefaultCamelContext ctx = new DefaultCamelContext();
		Exchange original = new DefaultExchange(ctx);
		original.getIn().setBody( new Country() );
		Exchange added = new DefaultExchange(ctx);
		added.getIn().setBody(ISO_CODE);
		
		AddISOCode process = new AddISOCode();
		Exchange merged = process.aggregate(original, added);
		
		assertTrue( ISO_CODE.equals(merged.getIn().getBody(Country.class).getIsoCode()) );
	}
}
