package com.redhat.fuse.boosters.rest.http.router.process;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;

import com.redhat.fuse.boosters.rest.http.model.GetCountryRequest;

public class PrepareRequestJAXB implements Expression {

	@Override
	public <T> T evaluate(Exchange exchange, Class<T> type) {
		String countryName = (String) exchange.getIn().getHeader("country_name");
		GetCountryRequest request = new GetCountryRequest();
		request.setName(countryName);
		return (T) request;
	}

}
