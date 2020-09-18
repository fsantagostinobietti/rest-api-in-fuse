package com.redhat.fuse.boosters.rest.http.router.process;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;

import com.redhat.fuse.boosters.rest.http.model.Error;

public class InvalidInputError implements Expression {

	@Override
	public <T> T evaluate(Exchange exchange, Class<T> type) {
		// error response
		return (T) new Error("Invalid country! Hint: try 'Spain', 'Poland' or 'United Kingdom'");
	}

}
