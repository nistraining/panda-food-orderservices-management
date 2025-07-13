package panda.orderservices.management.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import panda.orderservices.management.ExceptionHandler.VendorNotFoundException;
import panda.orderservices.management.entities.Vendor;

@Service
public class VendorResolver {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${vendor.base.url}")
	private String vendorBaseUrl;

	
	@Autowired
	private LogService logService;
	
	public String resolveVendorId(String vendorName, int location) {
	   // String encodedVendorName = URLEncoder.encode(vendorName,StandardCharsets.UTF_8);
		String url = UriComponentsBuilder
	        .fromHttpUrl(vendorBaseUrl + "/vendors/resolve")
	        .queryParam("vendorName", vendorName)
	        .queryParam("location", location)
	        .toUriString();

	    try {
	        ResponseEntity<Vendor> response = restTemplate.getForEntity(url, Vendor.class);
	        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
	            String vendorId = response.getBody().getVendorId();
	            logService.logMessageToCloudWatch("[VendorResolver] Found: Vendor id=" + vendorId + " Vendor Name=" + vendorName);
	            return vendorId; 
	        }
	    } catch (Exception e) {
	        logService.logMessageToCloudWatch("[VendorResolver] Error during resolution: " + e.getMessage());
	    }

	    throw new VendorNotFoundException("Vendor could not be resolved, please contact your administrator");
	}
}
