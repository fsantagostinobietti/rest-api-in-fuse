package com.redhat.fuse.boosters.rest.http.router.process;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.redhat.fuse.boosters.rest.http.model.Country;

public class AddISOCode implements AggregationStrategy{

	@Override
	public Exchange aggregate(Exchange orig, Exchange added) {
		String isoCode = added.getIn().getBody(String.class);
		Country countryBean = orig.getIn().getBody(Country.class);
		// update iso_code field
		countryBean.setIsoCode(isoCode);
		return orig;
	}

}
