package panda.orderservices.management.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.awspring.cloud.sqs.annotation.SqsListener;
import panda.orderservices.management.entities.Orders;
import panda.orderservices.management.entities.VendorValidationRequests;
import panda.orderservices.management.entities.VendorValidationResponse;
import panda.orderservices.management.repository.OrderRepository;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class OrderServices {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private SqsAsyncClient sqsClient;

	public Orders saveOrders(Orders order) {
		sendValidationRequestToVendor(order);
		order.setStatus("PENDING");
		return order;
	}

	public void sendValidationRequestToVendor(Orders order) {
		try {
		VendorValidationRequests vendorValidationRequests = new VendorValidationRequests(
				order.getOrderName(),
				order.getOrderId(),
				order.getQuantity(),
				order.getOrderLocation()
				);
		
		String messageBody = objectMapper.writeValueAsString(vendorValidationRequests);
		SendMessageRequest request = SendMessageRequest.builder()
				.queueUrl("https://sqs.eu-central-1.amazonaws.com/489855987447/panda-foods-queue")
				.messageBody(messageBody)
				.build();

		sqsClient.sendMessage(request);
		System.out.println("Successfully sent the message to the SQS");
		
		
	}catch(Exception e) {
		System.out.println("Error occurred while sending message to the SQS :"+ e.getMessage());
	}

}
	
	@SqsListener("https://sqs.eu-central-1.amazonaws.com/489855987447/nisith-tech-queue")
	public void handleVendorResponse(@Payload String rawJson) {
		try {
			VendorValidationResponse response = objectMapper.readValue(rawJson, VendorValidationResponse.class);
			if(response.isDeliverable()) {
				Orders orders = new Orders();
				orders.setOrderName(response.getOrderName());
				orders.setQuantity(response.getQuantity());
				orders.setOrderLocation(response.getOrderLocation());
				orders.setStatus("CONFIRMED");
				orderRepository.save(orders);
				System.out.println("Order saved after vendor approval");
			}else {
				System.out.println("Vendor has rejected the order");
			}
		}catch(Exception e) {
			System.out.println("Error processing SQS message : " + e.getMessage());
		}
		
	}
}
