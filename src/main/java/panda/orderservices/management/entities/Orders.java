package panda.orderservices.management.entities;

import java.io.Serializable;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Orders implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int orderId;
	private String orderName;
	private int quantity;
	private int orderLocation;
	private String status;
	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private Instant createdTimeStamp;
	
	
	public Orders() {
		// TODO Auto-generated constructor stub
	}

	
	

	public Orders(int orderId, String orderName, int quantity, int orderLocation, String status,
			Instant createdTimeStamp) {
		this.orderId = orderId;
		this.orderName = orderName;
		this.quantity = quantity;
		this.orderLocation = orderLocation;
		this.status = status;
		this.createdTimeStamp = createdTimeStamp;
	}




	public int getOrderId() {
		return orderId;
	}


	public void setOrderId(int orderId) {
		this.orderId = orderId;
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


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Instant getCreatedTimeStamp() {
		return createdTimeStamp;
	}


	public void setCreatedTimeStamp(Instant createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
	}




	@Override
	public String toString() {
		return "Orders [orderId=" + orderId + ", orderName=" + orderName + ", quantity=" + quantity + ", orderLocation="
				+ orderLocation + ", status=" + status + ", createdTimeStamp=" + createdTimeStamp + "]";
	}
	
    

	
	
	
}
