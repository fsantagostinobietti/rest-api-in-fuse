package com.redhat.fuse.boosters.rest.http.router;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.stereotype.Component;

import com.redhat.fuse.boosters.rest.http.service.CountryISOCodeCache;

/**
 * 
 * Reads from CSV file and update an in-memory cache.
 * Operation is triggered at every file change.
 * 
 */
@Component
public class CountryISOCodesReloader extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("file:src/test/resources?fileName=available_countries.csv"
				+ "&noop=true"	// do not modify file
				+ "&idempotentKey=${file:name}-${file:modified}")	// reload fie when changed
			.unmarshal(
					// convert csv entries into java Map
					new CsvDataFormat().setUseMaps(true)
			)
			.log("Country ISO codes:  ${body}")
			.bean(CountryISOCodeCache.class, "populate");
	}

}
