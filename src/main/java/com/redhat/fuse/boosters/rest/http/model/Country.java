package com.redhat.fuse.boosters.rest.http.model;

public class Country {
	
	String name;
	String isoCode;
	long population;
	

	
	/*
	 * setters/getters
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getPopulation() {
		return population;
	}

	public void setPopulation(long population) {
		this.population = population;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}
	
}
