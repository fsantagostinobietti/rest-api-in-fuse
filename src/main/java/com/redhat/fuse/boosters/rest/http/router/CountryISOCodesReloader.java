package com.redhat.fuse.boosters.rest.http.router;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${csvfile.uri}")
	String csvFileURI;

	@Override
	public void configure() throws Exception {
		
		from(   csvFileURI 
				+ "&noop=true"	// do not modify file
				+ "&idempotentKey=${file:name}-${file:modified}")	// reload file when changed
		.unmarshal(
				// convert csv entries into java Map
				new CsvDataFormat().setUseMaps(true)
		)
		.log("Country ISO codes:  ${body}")
		.bean(CountryISOCodeCache.class, "populate");
	}

}
