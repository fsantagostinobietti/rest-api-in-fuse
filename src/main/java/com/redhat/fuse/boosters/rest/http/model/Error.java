package com.redhat.fuse.boosters.rest.http.model;

public class Error {
	String errorMsg;

	public Error(String msg) {
		this.errorMsg = msg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
