package com.redhat.fuse.boosters.rest.http.router.process;


import org.apache.camel.Header;

import com.redhat.fuse.boosters.rest.http.model.GetCountryRequest;

public class PrepareRequestJAXB {

	public GetCountryRequest evaluate(@Header("country_name") String countryName) {
		GetCountryRequest request = new GetCountryRequest();
		request.setName(countryName);
		return request;
	}

}
