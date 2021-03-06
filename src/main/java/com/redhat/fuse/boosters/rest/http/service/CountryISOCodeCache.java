package com.redhat.fuse.boosters.rest.http.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simple cache to contain country/iso_code info
 * 
 */
@Service("countryISOCodeCacheService")
public class CountryISOCodeCache {
	private static final Logger logger = LoggerFactory.getLogger(CountryISOCodeCache.class);
		
	private static List<Map<String, String>> countryIsoCache = Collections.emptyList();
		
	public void populate(List<Map<String, String>> inList) {
		// replace old cache with new one
		countryIsoCache = Collections.unmodifiableList(inList);
		logger.info("CountryISOCodeCache updated.");
	}
	
	public static String lookupCode(@Header("country_name") String country) {
		if (country != null)
			for (Map<String, String> m : countryIsoCache) {
				if ( country.equalsIgnoreCase(m.get("country")) )
					return m.get("iso_code");
			}
		return null;
	}
}
