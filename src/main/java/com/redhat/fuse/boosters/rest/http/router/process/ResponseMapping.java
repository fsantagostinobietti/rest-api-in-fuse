package com.redhat.fuse.boosters.rest.http.router.process;

import java.util.Map;

import org.apache.camel.Body;

import com.redhat.fuse.boosters.rest.http.model.Country;
import com.redhat.fuse.boosters.rest.http.model.CountryNotFoundException;

public class ResponseMapping  {
	
	final String INVALID_COUNTRY = "Invalid country! Hint: try 'Spain', 'Poland' or 'United Kingdom'";

	public Country evaluate(@Body Map<String,?> response) throws Exception {
		if (response.isEmpty())
			throw new CountryNotFoundException(INVALID_COUNTRY);
		Map<?,?> country = (Map<?,?>) response.get("country");
		
		Country countryBean = new Country();
		String countryName = (String)country.get("name");
		countryBean.setName(countryName);
		countryBean.setPopulation(Long.parseLong((String) country.get("population")));
		return countryBean;
	}

	public Country evaluateEconomic(@Body Map<String,?> response) throws Exception {
		if (response.isEmpty())
			throw new CountryNotFoundException(INVALID_COUNTRY);
		Map<?,?> country = (Map<?,?>) response.get("country");
		
		Country countryBean = new Country();
		String countryName = (String)country.get("name");
		countryBean.setName(countryName);
		countryBean.setCurrency((String) country.get("currency"));
		return countryBean;
	}
}
