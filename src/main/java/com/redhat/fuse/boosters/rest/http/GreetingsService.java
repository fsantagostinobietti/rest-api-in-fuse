package com.redhat.fuse.boosters.rest.http;

import com.redhat.fuse.boosters.rest.http.model.Greetings;

/**
 * Service interface for name service.
 * 
 */
public interface GreetingsService {

    /**
     * Generate Greetings
     *
     * @return a string greetings
     */
    Greetings getGreetings( String name);

}