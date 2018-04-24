package com.sample.geode.demoapp.client.controller;

import org.apache.geode.cache.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.bds.gemfire.testapp.model.CustomerOrder;

@RestController
public class CustomerOrderController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerOrderController.class);

	private final Region<String, CustomerOrder> customerOrderRegion;

	public CustomerOrderController(@Qualifier("customer-order") Region<String, CustomerOrder> customerOrderRegion) {
		super();
		this.customerOrderRegion = customerOrderRegion;
	}

	@GetMapping(value = "/customer-order/{key}", produces = "application/json")
	public CustomerOrder retrieve(@PathVariable("key") String key) {
		CustomerOrder customerOrder = customerOrderRegion.get(key);
		logger.info("retrieve: key={}, customerOrder={}", key, customerOrder);
		return customerOrder;
	}

	@PostMapping(value = "/customer-order/{key}", consumes = "application/json")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void store(@PathVariable("key") String key, @RequestBody CustomerOrder customerOrder) {
		logger.info("store: key={}, customerOrder={}", key, customerOrder);
		customerOrderRegion.put(key, customerOrder);
	}
}
