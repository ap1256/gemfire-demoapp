package com.sample.geode.demoapp.model;

import java.util.Set;

public class CustomerOrder {

	private String customerId;
	private String shippingAddress;
	private long orderDate;
	private Set<String> itemSet;

	public CustomerOrder() {
		super();
	}

	public CustomerOrder(String customerId, String shippingAddress, long orderDate, Set<String> itemSet) {
		super();
		this.customerId = customerId;
		this.shippingAddress = shippingAddress;
		this.orderDate = orderDate;
		this.itemSet = itemSet;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public long getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(long orderDate) {
		this.orderDate = orderDate;
	}

	public Set<String> getItemSet() {
		return itemSet;
	}

	public void setItemSet(Set<String> itemSet) {
		this.itemSet = itemSet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((itemSet == null) ? 0 : itemSet.hashCode());
		result = prime * result + (int) (orderDate ^ (orderDate >>> 32));
		result = prime * result + ((shippingAddress == null) ? 0 : shippingAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerOrder other = (CustomerOrder) obj;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (itemSet == null) {
			if (other.itemSet != null)
				return false;
		} else if (!itemSet.equals(other.itemSet))
			return false;
		if (orderDate != other.orderDate)
			return false;
		if (shippingAddress == null) {
			if (other.shippingAddress != null)
				return false;
		} else if (!shippingAddress.equals(other.shippingAddress))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CustomerOrder [customerId=" + customerId + ", shippingAddress=" + shippingAddress + ", orderDate="
				+ orderDate + ", itemSet=" + itemSet + "]";
	}

}