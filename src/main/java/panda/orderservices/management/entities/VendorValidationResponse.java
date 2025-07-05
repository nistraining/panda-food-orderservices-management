package panda.orderservices.management.entities;

public class VendorValidationResponse {
	
	private String orderName;
	private int quantity;
	private int orderLocation;
	private boolean deliverable;
    private String messageType;
    
    
    public VendorValidationResponse() {
		super();
	}
	public VendorValidationResponse(String orderName, int quantity, int orderLocation, boolean deliverable,
			String messageType) {
		this.orderName = orderName;
		this.quantity = quantity;
		this.orderLocation = orderLocation;
		this.deliverable = deliverable;
		this.messageType = messageType;
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
	public boolean isDeliverable() {
		return deliverable;
	}
	public void setDeliverable(boolean deliverable) {
		this.deliverable = deliverable;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}	
    
    

}
