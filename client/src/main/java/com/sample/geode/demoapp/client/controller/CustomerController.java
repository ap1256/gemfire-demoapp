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

import io.pivotal.bds.gemfire.testapp.model.Customer;

@RestController
public class CustomerController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private final Region<String, Customer> customerRegion;

	public CustomerController(@Qualifier("customer") Region<String, Customer> customerRegion) {
		super();
		this.customerRegion = customerRegion;
	}

	@GetMapping(value = "/customer/{key}", produces = "application/json")
	public Customer retrieve(@PathVariable("key") String key) {
		Customer customer = customerRegion.get(key);
		logger.info("retrieve: key={}, customer={}", key, customer);
		return customer;
	}

	@PostMapping(value = "/customer/{key}", consumes = "application/json")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void store(@PathVariable("key") String key, @RequestBody Customer customer) {
		logger.info("store: key={}, customer={}", key, customer);
		customerRegion.put(key, customer);
	}
}
