package com.redhat.fuse.boosters.rest.http.router.process;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.redhat.fuse.boosters.rest.http.model.Country;

public class AggregateAllCountryInfo implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange==null)
			return newExchange;
		
		Error oldError = oldExchange.getIn().getBody(Error.class);
		if (oldError!=null || newExchange==null)
			return oldExchange;
		
		Country newCountry = newExchange.getIn().getBody(Country.class);
		if (newCountry!=null) {
			Country fullCountry = oldExchange.getIn().getBody(Country.class);
			if (newCountry.getIsoCode()!=null) fullCountry.setIsoCode(newCountry.getIsoCode());
			if (newCountry.getPopulation()>0) fullCountry.setPopulation(newCountry.getPopulation());
			if (newCountry.getCurrency()!=null) fullCountry.setCurrency(newCountry.getCurrency());
			oldExchange.getIn().setBody(fullCountry);
		}
		
		return oldExchange;
	}

}
