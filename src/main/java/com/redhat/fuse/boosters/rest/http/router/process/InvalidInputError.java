package com.redhat.fuse.boosters.rest.http.router.process;

import com.redhat.fuse.boosters.rest.http.model.Error;

public class InvalidInputError {

	public Error evaluate() {
		// error response
		return new Error("Invalid country! Hint: try 'Spain', 'Poland' or 'United Kingdom'");
	}

}
