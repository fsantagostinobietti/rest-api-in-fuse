package com.redhat.fuse.boosters.rest.http.router.process;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.fuse.boosters.rest.http.model.Error;

public class GenericExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GenericExceptionHandler.class);
	
	public Error evaluate(@ExchangeProperty(Exchange.EXCEPTION_CAUGHT)  Exception cause) {
		// retrieve the caught message
		logger.error(cause.getMessage(), cause);
		// error response
		return new Error( cause.toString() );
	}
}
