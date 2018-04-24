package com.sample.geode.demoapp.function;

import static org.apache.geode.cache.RegionShortcut.PARTITION_REDUNDANT;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sample.geode.demoapp.model.CustomerOrder;
import com.sample.geode.demoapp.model.Item;

public class CustomerOrderPriceFunctionIntegrationTest {

	Region<String, CustomerOrder> customerOrderRegion;

	Region<String, Item> itemRegion;

	CustomerOrderPriceFunction customerOrderPriceFunction;

	@Before
	public void setUp() throws Exception {
		setupGemFire();
		setupCustomer1Orders();
		setupCustomer2Orders();
		setUpItems();
	}

	@SuppressWarnings("unchecked")
	private void setupGemFire() throws IOException {
		CacheFactory cf = new CacheFactory();
		cf.setPdxPersistent(false);
		cf.setPdxReadSerialized(false);
		cf.setPdxSerializer(new ReflectionBasedAutoSerializer("com.sample.geode.demoapp.model.*"));

		Cache c = cf.create();
		CacheServer cs = c.addCacheServer();
		cs.start();

		RegionFactory<?, ?> rf = c.createRegionFactory(PARTITION_REDUNDANT);
		customerOrderRegion = (Region<String, CustomerOrder>) rf.create("customer-order");
		itemRegion = (Region<String, Item>) rf.create("item");

		customerOrderPriceFunction = new CustomerOrderPriceFunction();
		FunctionService.registerFunction(customerOrderPriceFunction);
	}

	private void setupCustomer1Orders() {
		Set<String> itemSet = new HashSet<String>();
		itemSet.add("pen");
		itemSet.add("paper");
		// 1.49 + 0.10 = 1.59
		CustomerOrder customerOrder = new CustomerOrder("customer1", "address1", (new Date()).getTime(), itemSet);
		customerOrderRegion.put("order1", customerOrder);

		itemSet = new HashSet<String>();
		itemSet.add("pencil");
		itemSet.add("pen");
		itemSet.add("paper");
		// 1.59 + 0.99 = 2.58
		customerOrder = new CustomerOrder("customer1", "address1", (new Date()).getTime(), itemSet);
		customerOrderRegion.put("order2", customerOrder);
	}

	private void setupCustomer2Orders() {
		Set<String> itemSet = new HashSet<String>();
		itemSet.add("pencil");
		itemSet.add("pen");
		// 0.99 + 1.49 = 2.48
		CustomerOrder customerOrder = new CustomerOrder("customer2", "address2", (new Date()).getTime(), itemSet);
		customerOrderRegion.put("order3", customerOrder);
	}

	private void setUpItems() {
		Item pencil = new Item();
		pencil.setName("pencil");
		pencil.setDescription("pencil decription");
		pencil.setPrice("0.99");

		Item pen = new Item();
		pen.setName("pen");
		pen.setDescription("pen description");
		pen.setPrice("1.49");

		Item paper = new Item();
		paper.setName("paper");
		paper.setDescription("paper description");
		paper.setPrice("0.10");

		itemRegion.put("pencil", pencil);
		itemRegion.put("pen", pen);
		itemRegion.put("paper", paper);
	}

	@Test
	public void testCustomerOrderPrice() {
		Assert.assertEquals(new BigDecimal("1.59"), getTotalPrice("order1"));
		Assert.assertEquals(new BigDecimal("2.58"), getTotalPrice("order2"));
		Assert.assertEquals(new BigDecimal("2.48"), getTotalPrice("order3"));
		Assert.assertEquals(new BigDecimal("0.00"), getTotalPrice("order4"));
	}

	@SuppressWarnings("unchecked")
	private BigDecimal getTotalPrice(String customerOrderId) {
		List<BigDecimal> result = (List<BigDecimal>) FunctionService.onRegion(customerOrderRegion)
				.withFilter(Collections.singleton(customerOrderId))
				.execute(customerOrderPriceFunction)
				.getResult();

		return result.get(0);
	}

}
