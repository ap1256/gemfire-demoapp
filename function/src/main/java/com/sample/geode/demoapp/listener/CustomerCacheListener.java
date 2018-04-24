package com.sample.geode.demoapp.listener;

import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sample.geode.demoapp.model.Customer;

public class CustomerCacheListener extends CacheListenerAdapter<String, Customer> implements Declarable {

    private static final Logger logger = LogManager.getLogger(CustomerCacheListener.class);
    
    @Override
    public void afterCreate(EntryEvent<String, Customer> event) {
        logger.info("afterCreate: event={}", event);
    }

    @Override
	public void afterDestroy(EntryEvent<String, Customer> event) {
        logger.info("afterDestroy: event={}", event);
    }    
}
