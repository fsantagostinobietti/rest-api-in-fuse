package com.redhat.fuse.boosters.rest.http;

import java.util.Date;
import java.util.EventObject;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.management.event.AbstractExchangeEvent;
import org.apache.camel.management.event.ExchangeCompletedEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelRouteTimerNotifier extends EventNotifierSupport {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	
	@Override
	public boolean isEnabled(EventObject event) {
		//  event is derived from AbstractExchangeEvent
		return AbstractExchangeEvent.class.isAssignableFrom(event.getClass());
	}


	@Override
	public void notify(EventObject event) throws Exception {
		log.debug(event.getClass()+" "+event);
		if ( ExchangeCompletedEvent.class.isAssignableFrom(event.getClass()) ) {
			ExchangeCompletedEvent completedEvent = (ExchangeCompletedEvent) event;
			Exchange exchange = completedEvent.getExchange();
			long deltaMills = new Date().getTime() - exchange.getCreated().getTime();
			String exchangeId = exchange.getExchangeId();
			if ( exchangeId.equals(exchange.getProperty("CamelCorrelationId")) ) {  // root exchange message
				Endpoint fromEndpoint = exchange.getFromEndpoint();
				String fromRouteId = exchange.getFromRouteId();
				log.info("ExchangeId ["+exchangeId+"], Endpoint ["+fromEndpoint+"], RouteId ["+fromRouteId+"], ElapsedTimeInMills ["+deltaMills+"]");
			}
		}
	}

    
}
