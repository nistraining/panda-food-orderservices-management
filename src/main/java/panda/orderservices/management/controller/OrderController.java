package panda.orderservices.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import panda.orderservices.management.entities.Orders;
import panda.orderservices.management.services.LogService;
//import panda.orderservices.management.services.OktaTokenClient;
import panda.orderservices.management.services.OrderServices;


@RestController
@RequestMapping("/orders")
public class OrderController {
	
	@Autowired
	private OrderServices orderServices;
	
	
//	@Autowired
//	private OktaTokenClient tokenClient;
//	
	@Autowired
	private LogService logService;

	
	@GetMapping("/home/{name}")
	public String welcome(@PathVariable String name) {
		return "Hello " +name+" " +"Please Place Your Order Here";
	}
	
	@PostMapping("/save")
	public ResponseEntity<Orders> saveOrders(@RequestBody Orders order) throws JsonProcessingException {
		logService.logMessageToCloudWatch("Inside Order Save controller");
		Orders savedOrder = orderServices.saveOrders(order);
	    return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
	}
	
//	
//	@GetMapping("/test-token")
//	public ResponseEntity<String> testToken() {
//	    try {
//	        String token = tokenClient.fetchAccessToken();  // Injected OktaTokenClient
//	        logService.logMessageToCloudWatch("Token received successfully: " + token);
//	        return ResponseEntity.ok(token);
//	    } catch (Exception e) {
//	        logService.logMessageToCloudWatch("Fetching token failed: " + e.getMessage());
//	        System.out.println("Fetching token failed:" + e.getMessage());
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//	                .body("Token fetch failed: " + e.getMessage());
//	    }
//	}
//	@GetMapping("/find")
//	public ResponseEntity<List<Orders>> findAllOrders() {
//	    List<Orders> findAllOrders = orderServices.findAllOrders();
//	    return ResponseEntity.ok(findAllOrders);
//	}

}