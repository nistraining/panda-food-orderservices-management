package panda.orderservices.management.entities;

public class Vendor {
	
	private String vendorId;
	private String vendorName;
	private int vendorLocation;
	
	
	public Vendor() {
	}
	public Vendor(String vendorId, String vendorName, int vendorLocation) {
		super();
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.vendorLocation = vendorLocation;
	}
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public int getVendorLocation() {
		return vendorLocation;
	}
	public void setVendorLocation(int vendorLocation) {
		this.vendorLocation = vendorLocation;
	}
	
	

}
