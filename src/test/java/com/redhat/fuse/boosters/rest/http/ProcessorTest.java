package com.redhat.fuse.boosters.rest.http;

import static org.junit.Assert.*;

import org.junit.Test;

import com.redhat.fuse.boosters.rest.http.model.GetCountryRequest;
import com.redhat.fuse.boosters.rest.http.router.process.PrepareRequestJAXB;

public class ProcessorTest {
	
	@Test
	public void testRequestJAXB() throws Exception {
		PrepareRequestJAXB process = new PrepareRequestJAXB();
		GetCountryRequest req = (GetCountryRequest) process.evaluate("Spain");
		assertTrue( "Spain".equals(req.getName()) );
	}

}
