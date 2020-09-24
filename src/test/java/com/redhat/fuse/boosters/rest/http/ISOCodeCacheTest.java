package com.redhat.fuse.boosters.rest.http;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.redhat.fuse.boosters.rest.http.service.CountryISOCodeCache;

public class ISOCodeCacheTest {

	@Test
	public void testISOCodeCache() throws Exception {
		final String COUNTRY_NAME_KEY = "country";
		final String COUNTRY_ISO_KEY = "iso_code";
		
		// build data to populate cache
		HashMap<String, String> spainEntry = new HashMap<String, String>();
		spainEntry.put(COUNTRY_NAME_KEY,"Spain");
		spainEntry.put(COUNTRY_ISO_KEY,"ES");
		List<Map<String, String>> inList = new ArrayList<Map<String,String>>();
		inList.add(spainEntry);
		
		CountryISOCodeCache cache = new CountryISOCodeCache();
		cache.populate(inList);
		
		// lookup for inserted data
		String isoCode = CountryISOCodeCache.lookupCode("Spain");
		
		assertTrue("ES".equals(isoCode));
		assertNull(CountryISOCodeCache.lookupCode("Italy"));
	}
}
