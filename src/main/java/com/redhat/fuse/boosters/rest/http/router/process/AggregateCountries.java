package com.redhat.fuse.boosters.rest.http.router.process;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.redhat.fuse.boosters.rest.http.model.Country;

public class AggregateCountries implements AggregationStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		ArrayList<Country> countries = null;
		if (oldExchange==null)
			countries = new ArrayList<Country>();
		else
			countries = oldExchange.getIn().getBody(ArrayList.class);
		
		Country newCountry = newExchange.getIn().getBody(Country.class);
		if (newCountry!=null) 
			countries.add(newCountry);
		
		newExchange.getIn().setBody(countries);
		return newExchange;
	}

}
