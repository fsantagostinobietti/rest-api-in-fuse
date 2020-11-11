package com.redhat.fuse.boosters.rest.http.router.process;

import java.util.Map;

import org.apache.camel.Body;

import com.redhat.fuse.boosters.rest.http.model.Country;

public class EconomicResponseMapping  {

	public Country evaluate(@Body Map<String,?> response) {
		Map<?,?> country = (Map<?,?>) response.get("country");
		if (country==null)
			return null;
		
		Country countryBean = new Country();
		String countryName = (String)country.get("name");
		countryBean.setName(countryName);
		countryBean.setCurrency((String) country.get("currency"));
		return countryBean;
	}

}
