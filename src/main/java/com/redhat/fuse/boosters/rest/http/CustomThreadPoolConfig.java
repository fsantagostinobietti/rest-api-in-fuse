package com.redhat.fuse.boosters.rest.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global ThreadPool to be useb by Camel split/multicast 
 */
@Configuration
public class CustomThreadPoolConfig {
	
	static public final String ID = "myCustomThreadPool";

    @Bean(ID)
    public ExecutorService createCustomThreadPool() {
        return Executors.newCachedThreadPool();
    }
}
