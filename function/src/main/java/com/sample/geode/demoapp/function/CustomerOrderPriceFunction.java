package com.sample.geode.demoapp.function;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sample.geode.demoapp.model.CustomerOrder;
import com.sample.geode.demoapp.model.Item;

@SuppressWarnings("serial")
public class CustomerOrderPriceFunction implements Function<Object>, Declarable {

	private static final Logger logger = LogManager.getLogger(CustomerOrderPriceFunction.class);

	@Override
	public boolean hasResult() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(FunctionContext<Object> fc) {
		if (!(fc instanceof RegionFunctionContext)) {
			throw new FunctionException(
					"This is a data aware function, and has to be called using FunctionService.onRegion.");
		}

		try {
			RegionFunctionContext rfc = (RegionFunctionContext) fc;
			Set<String> filter = (Set<String>) rfc.getFilter();
			String customerOrderId = filter.stream().findFirst().get();

			// Send the result to function caller node.
			fc.getResultSender().lastResult(getCustomerOrderPrice(customerOrderId));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new FunctionException(e);
		}
	}

	@Override
	public String getId() {
		return getClass().getSimpleName();
	}

	@Override
	public boolean optimizeForWrite() {
		return false;
	}

	@Override
	public boolean isHA() {
		return true;
	}

	Region<?, ?> getRegion(String regionName) {
		return CacheFactory.getAnyInstance().getRegion(regionName);
	}

	Region<?, ?> getLocalRegion(String regionName) {
		return PartitionRegionHelper.getLocalData(getRegion(regionName));
	}

	@SuppressWarnings("unchecked")
	BigDecimal getCustomerOrderPrice(String customerOrderId) {
		Region<String, CustomerOrder> customerOrderRegion = (Region<String, CustomerOrder>) getLocalRegion(
				"customer-order");
		Region<String, Item> itemRegion = (Region<String, Item>) getRegion("item");

		BigDecimal totalPrice = new BigDecimal("0.00");

		CustomerOrder customerOrder = customerOrderRegion.get(customerOrderId);
		if (customerOrder != null) {
			Set<String> itemSet = customerOrder.getItemSet();
			for (String itemId : itemSet) {
				Item item = itemRegion.get(itemId);
				totalPrice = totalPrice.add(new BigDecimal(item.getPrice()));
			}
		}

		return totalPrice;
	}
}