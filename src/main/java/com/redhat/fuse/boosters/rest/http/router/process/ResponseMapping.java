package com.redhat.fuse.boosters.rest.http.router.process;

import java.util.Map;

import org.apache.camel.Body;

import com.redhat.fuse.boosters.rest.http.model.Country;

public class ResponseMapping  {

	public Country evaluate(@Body Map<String,?> response) {
		Map<?,?> country = (Map<?,?>) response.get("country");
		
		Country countryBean = new Country();
		String countryName = (String)country.get("name");
		countryBean.setName(countryName);
		countryBean.setPopulation(Long.parseLong((String) country.get("population")));
		return countryBean;
	}

}
