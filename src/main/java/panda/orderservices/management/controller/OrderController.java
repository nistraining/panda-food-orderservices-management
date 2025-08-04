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
import com.google.gson.Gson;

import panda.orderservices.management.entities.Orders;
import panda.orderservices.management.entities.TestOrder;
import panda.orderservices.management.services.LogService;
//import panda.orderservices.management.services.OktaTokenClient;
import panda.orderservices.management.services.OrderServices;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;


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
	
//	 private SqsClient sqsClient;
//	 
//	 @Autowired
//	    public OrderController(SqsClient sqsClient) {
//	        this.sqsClient = sqsClient;
//	    }




	
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
	
	
//	 @PostMapping("/sendtestMessage")
//	    public ResponseEntity<String> sendTestOrder(@RequestBody TestOrder order) {
//		 
//		 
//	        try {
//	            String messageBody = new Gson().toJson(order);
//
//	            SendMessageResponse response = sqsClient.sendMessage(SendMessageRequest.builder()
//	                .queueUrl("https://sqs.eu-central-1.amazonaws.com/489855987447/panda-foods-queue")
//	                .messageBody(messageBody)
//	                .build());
//
//	            return ResponseEntity.ok("✅ Message sent. ID: " + response.messageId());
//	        } catch (Exception e) {
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body("❌ Failed to send message: " + e.getMessage());
//	        }
//	    }

	
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