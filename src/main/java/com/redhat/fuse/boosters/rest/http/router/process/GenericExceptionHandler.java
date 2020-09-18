package com.redhat.fuse.boosters.rest.http.router.process;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.fuse.boosters.rest.http.model.Error;

public class GenericExceptionHandler implements Expression {
	private static final Logger logger = LoggerFactory.getLogger(GenericExceptionHandler.class);
	
	@Override
	public <T> T evaluate(Exchange exchange, Class<T> type) {
		// retrieve the caught message
		Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		logger.error(cause.getMessage(), cause);
		// error response
		return (T) new Error( cause.toString() );
	}
}
