package com.redhat.fuse.boosters.rest.http.router.process;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;

import com.redhat.fuse.boosters.rest.http.model.Country;

public class ResponseMapping implements Expression {

	@Override
	public <T> T evaluate(Exchange exchange, Class<T> type) {
		Map<?,?> response = exchange.getIn().getBody(java.util.Map.class);
		Map<?,?> country = (Map<?,?>) response.get("country");
		
		Country countryBean = new Country();
		String countryName = (String)country.get("name");
		countryBean.setName(countryName);
		//countryBean.setIsoCode(CountryISOCodeCache.lookupCode(countryName));
		countryBean.setPopulation(Long.parseLong((String) country.get("population")));
		return (T) countryBean;
	}

}
