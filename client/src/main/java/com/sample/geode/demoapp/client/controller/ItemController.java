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

import com.sample.geode.demoapp.model.Item;

@RestController
public class ItemController {

	private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

	private final Region<String, Item> itemRegion;

	public ItemController(@Qualifier("item") Region<String, Item> itemRegion) {
		super();
		this.itemRegion = itemRegion;
	}

	@GetMapping(value = "/item/{key}", produces = "application/json")
	public Item retrieve(@PathVariable("key") String key) {
		Item item = itemRegion.get(key);
		logger.info("retrieve: key={}, item={}", key, item);
		return item;
	}

	@PostMapping(value = "/item/{key}", consumes = "application/json")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void store(@PathVariable("key") String key, @RequestBody Item item) {
		logger.info("store: key={}, item={}", key, item);
		itemRegion.put(key, item);
	}
}
