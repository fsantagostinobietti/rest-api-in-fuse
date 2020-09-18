package com.redhat.fuse.boosters.rest.http.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.text.WordUtils;

/**
 * JAXB annotated bean to produce following XML after marshalling:
 * 
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <getCountryRequest xmlns="http://spring.io/guides/gs-producing-web-service">
 *   <name>Spain</name>
 * </getCountryRequest>
 * }
 * </pre>
 *  
 * @author US00081
 *
 */
@SuppressWarnings("deprecation")
@XmlRootElement
public class GetCountryRequest {
	
	// define namespace
	@XmlAttribute
    final String xmlns = "http://spring.io/guides/gs-producing-web-service";
	
	
	// country name
	String name;

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		// remote service requires camel-case country name values
		this.name = WordUtils.capitalizeFully(name); 
	}
	
	
}