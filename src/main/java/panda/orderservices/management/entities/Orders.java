package panda.orderservices.management.entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Orders implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private int orderId;
	private String orderName;
	private int quantity;
	private int orderLocation;
	private String status;
	
	
	public Orders() {
		// TODO Auto-generated constructor stub
	}


	public Orders(int orderId, String orderName, int quantity, int orderLocation, String status) {
		super();
		this.orderId = orderId;
		this.orderName = orderName;
		this.quantity = quantity;
		this.orderLocation = orderLocation;
		this.status = status;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public int getOrderId() {
		return orderId;
	}



	public String getOrderName() {
		return orderName;
	}



	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}



	public int getQuantity() {
		return quantity;
	}



	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}



	public int getOrderLocation() {
		return orderLocation;
	}



	public void setOrderLocation(int orderLocation) {
		this.orderLocation = orderLocation;
	}



	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}



	@Override
	public String toString() {
		return "Orders [orderId=" + orderId + ", orderName=" + orderName + ", quantity=" + quantity + ", orderLocation="
				+ orderLocation + "]";
	}
	
	
	
}
