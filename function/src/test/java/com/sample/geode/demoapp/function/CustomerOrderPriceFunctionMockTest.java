package com.sample.geode.demoapp.function;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.geode.cache.Region;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.sample.geode.demoapp.model.CustomerOrder;
import com.sample.geode.demoapp.model.Item;

@RunWith(MockitoJUnitRunner.class)
public class CustomerOrderPriceFunctionMockTest {
	@Mock
	Region<String, CustomerOrder> customerOrderRegion;

	@Mock
	Region<String, Item> itemRegion;

	@Before
	public void setUp() throws Exception {
		setupCustomer1Orders();
		setupCustomer2Orders();
		setUpItems();
	}

	private void setupCustomer1Orders() {
		Set<String> itemSet = new HashSet<String>();
		itemSet.add("pen");
		itemSet.add("paper");
		// 1.49 + 0.10 = 1.59
		CustomerOrder customerOrder = new CustomerOrder("customer1", "address1", (new Date()).getTime(), itemSet);
		given(customerOrderRegion.get("order1")).willReturn(customerOrder);

		itemSet = new HashSet<String>();
		itemSet.add("pencil");
		itemSet.add("pen");
		itemSet.add("paper");
		// 1.59 + 0.99 = 2.58
		customerOrder = new CustomerOrder("customer1", "address1", (new Date()).getTime(), itemSet);
		given(customerOrderRegion.get("order2")).willReturn(customerOrder);
	}

	private void setupCustomer2Orders() {
		Set<String> itemSet = new HashSet<String>();
		itemSet.add("pencil");
		itemSet.add("pen");
		// 0.99 + 1.49 = 2.48
		CustomerOrder customerOrder = new CustomerOrder("customer2", "address2", (new Date()).getTime(), itemSet);
		given(customerOrderRegion.get("order3")).willReturn(customerOrder);
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

		given(itemRegion.get("pencil")).willReturn(pencil);
		given(itemRegion.get("pen")).willReturn(pen);
		given(itemRegion.get("paper")).willReturn(paper);
	}

	@Test
	public void testCustomerOrderPrice() {
		CustomerOrderPriceFunction customerOrderPriceFunction = new CustomerOrderPriceFunction();
		CustomerOrderPriceFunction customerOrderPriceFunctionSpy = Mockito.spy(customerOrderPriceFunction);

		doReturn(customerOrderRegion).when(customerOrderPriceFunctionSpy).getLocalRegion("customer-order");
		doReturn(itemRegion).when(customerOrderPriceFunctionSpy).getRegion("item");

		Assert.assertEquals(new BigDecimal("1.59"), customerOrderPriceFunctionSpy.getCustomerOrderPrice("order1"));

	}

}
