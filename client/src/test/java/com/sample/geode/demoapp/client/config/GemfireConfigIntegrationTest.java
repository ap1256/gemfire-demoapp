package com.sample.geode.demoapp.client.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.sample.geode.demoapp.client.ClientApplication;
import io.pivotal.bds.gemfire.testapp.model.Customer;
import io.pivotal.bds.gemfire.testapp.model.CustomerOrder;
import io.pivotal.bds.gemfire.testapp.model.Item;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ClientApplication.class })
@DirtiesContext
public abstract class GemfireConfigIntegrationTest {

	@Autowired
	protected GemFireCache cache;

	@Autowired
	@Qualifier("customer")
	protected Region<String, Customer> customerRegion;

	@Autowired
	@Qualifier("customer-order")
	protected Region<String, CustomerOrder> customerOrderRegion;

	@Autowired
	@Qualifier("item")
	protected Region<String, Item> itemRegion;

	@TestPropertySource(properties = { "gemfire.locators[0].host=localhost",
			"gemfire.locators[0].port=10334" })
	public static class WithLocatorTests extends GemfireConfigIntegrationTest {
		@Test
		public void testConfigurations() {
			assertThat(cache).isNotNull();

			assertThat(customerRegion).isNotNull();
			assertThat(customerRegion.getAttributes().getDataPolicy().withStorage()).isFalse();
			assertThat(customerRegion.getAttributes().getDataPolicy().withPartitioning()).isFalse();
			assertThat(customerRegion.getAttributes().getDataPolicy().withReplication()).isFalse();
			assertThat(customerRegion.getAttributes().getDataPolicy().withPersistence()).isFalse();

			assertThat(itemRegion).isNotNull();
			assertThat(itemRegion.getAttributes().getDataPolicy().withStorage()).isFalse();
			assertThat(itemRegion.getAttributes().getDataPolicy().withPartitioning()).isFalse();
			assertThat(itemRegion.getAttributes().getDataPolicy().withReplication()).isFalse();
			assertThat(itemRegion.getAttributes().getDataPolicy().withPersistence()).isFalse();

			assertThat(customerOrderRegion).isNotNull();
			assertThat(customerOrderRegion.getAttributes().getDataPolicy().withStorage()).isFalse();
			assertThat(customerOrderRegion.getAttributes().getDataPolicy().withPartitioning()).isFalse();
			assertThat(customerOrderRegion.getAttributes().getDataPolicy().withReplication()).isFalse();
			assertThat(customerOrderRegion.getAttributes().getDataPolicy().withPersistence()).isFalse();
		}
	}

	public static class WithoutLocatorTests extends GemfireConfigIntegrationTest {
		@Before
		public void setUp() throws Exception {
			clearRegions();
		}

		@After
		public void tearDown() throws Exception {
			clearRegions();
		}

		@Test
		public void testConfigurations() {
			assertThat(cache).isNotNull();

			assertThat(customerRegion).isNotNull();
			assertThat(customerRegion.getAttributes().getDataPolicy().withStorage()).isTrue();
			assertThat(customerRegion.getAttributes().getDataPolicy().withPartitioning()).isFalse();
			assertThat(customerRegion.getAttributes().getDataPolicy().withReplication()).isFalse();
			assertThat(customerRegion.getAttributes().getDataPolicy().withPersistence()).isFalse();

			assertThat(itemRegion).isNotNull();
			assertThat(itemRegion.getAttributes().getDataPolicy().withStorage()).isTrue();
			assertThat(itemRegion.getAttributes().getDataPolicy().withPartitioning()).isFalse();
			assertThat(itemRegion.getAttributes().getDataPolicy().withReplication()).isFalse();
			assertThat(itemRegion.getAttributes().getDataPolicy().withPersistence()).isFalse();

			assertThat(customerOrderRegion).isNotNull();
			assertThat(customerOrderRegion.getAttributes().getDataPolicy().withStorage()).isTrue();
			assertThat(customerOrderRegion.getAttributes().getDataPolicy().withPartitioning()).isFalse();
			assertThat(customerOrderRegion.getAttributes().getDataPolicy().withReplication()).isFalse();
			assertThat(customerOrderRegion.getAttributes().getDataPolicy().withPersistence()).isFalse();
		}

		@Test
		public void testRegionOperations() {
			Customer expectedCustomer = new Customer("Krikor Garegin");
			customerRegion.put("c1", expectedCustomer);
			assertThat(customerRegion.get("c1")).isEqualTo(expectedCustomer);

			Item expectedItem = new Item("pencil", "description", "0.99");
			itemRegion.put("i1", expectedItem);
			assertThat(itemRegion.get("i1")).isEqualTo(expectedItem);

			String[] itemKeys = { "i1" };
			Set<String> itemSet = new HashSet<String>(Arrays.asList(itemKeys));
			CustomerOrder expectedCustomerOrder = new CustomerOrder("c1", "address", (new Date()).getTime(), itemSet);
			customerOrderRegion.put("co1", expectedCustomerOrder);
			assertThat(customerOrderRegion.get("co1")).isEqualTo(expectedCustomerOrder);

			clearRegions();

			assertThat(customerOrderRegion.keySet().size()).isEqualTo(0);
			assertThat(customerRegion.keySet().size()).isEqualTo(0);
			assertThat(itemRegion.keySet().size()).isEqualTo(0);
		}

		private void clearRegions() {
			for (String key : customerOrderRegion.keySet()) {
				customerOrderRegion.remove(key);
			}

			for (String key : customerRegion.keySet()) {
				customerRegion.remove(key);
			}

			for (String key : itemRegion.keySet()) {
				itemRegion.remove(key);
			}
		}
	}

}
