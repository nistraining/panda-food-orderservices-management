package panda.orderservices.management.services;

import java.util.Optional;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.vendor.management.entities.VendorResponseDTO;

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
	
	@Autowired
	private LogService logService;
	
	@Value("${topic.name}")
	private String topicName;
	
	@Autowired
	private CorrelationGenerationService correlationGenerationService;


	public Orders saveOrders(Orders order) {
		int orderId = order.getOrderId();
		//Checking for existing order
		Optional<Orders> existingOrder = orderRepository.findById(orderId);
		if(existingOrder.isPresent()) {
			logService.logMessageToCloudWatch("[OrderService] Duplicate orderId attempted: " + orderId);
		  return existingOrder.get();
		}
		order.setStatus("PENDING");
		Orders savedOrder = orderRepository.save(order);
		String correlationId = correlationGenerationService.generateForOrder(orderId);
		MDC.put(correlationId, correlationId);
		sendValidationRequestToVendor(savedOrder);
		logService.logMessageToCloudWatch("[OrderService] Order created: orderId=" + orderId + ", corrId=" + correlationId);
		MDC.clear();
		return savedOrder;
	}

	public void sendValidationRequestToVendor(Orders order) {
		try {
			int orderId = order.getOrderId();
			String corrId =	correlationGenerationService.getCorrelationId(orderId).orElse("UNKNOWN");

		VendorValidationRequests vendorValidationRequests = new VendorValidationRequests(
				order.getOrderName(),
				orderId,
				order.getQuantity(),
				order.getOrderLocation(),
				order.getCreatedTimeStamp()
				);
		


		
		String messageBody = objectMapper.writeValueAsString(vendorValidationRequests);
		SendMessageRequest request = SendMessageRequest.builder()
				.queueUrl("https://sqs.eu-central-1.amazonaws.com/489855987447/panda-foods-queue")
				.messageBody(messageBody)
				.build();

		sqsClient.sendMessage(request);
		logService.logMessageToCloudWatch("[OrderService] Sent to SQS: orderId=" + orderId + ", corrId=" + corrId);
		System.out.println("Successfully sent the message to the SQS");
		
		
	}catch(Exception e) {
		System.out.println("Error occurred while sending message to the SQS :"+ e.getMessage());
	}

}
		
	 @KafkaListener(topics = "vendor-validated-orders", groupId = "order-management-group")
	    public void handleVendorResponseConsumer(VendorResponseDTO vendorResponseDTO) {
		 int orderId = vendorResponseDTO.getOrderId();
		 String corrId = correlationGenerationService.getCorrelationId(orderId).orElse("Unknown");
		 MDC.put(corrId, corrId);
		 logService.logMessageToCloudWatch("Kafka message received for the order id :" +orderId+" With Correlation id :" + corrId);
		 System.out.println("Kafka Message Received: " + vendorResponseDTO);

	        try {
	        	Optional<Orders> optionalOrder = orderRepository.findById(orderId);
	        	if(optionalOrder.isPresent()) {
	        		Orders order = optionalOrder.get();
	        	
	            if (vendorResponseDTO.getDeliverable()) {
	                order.setStatus("CONFIRMED");
	                orderRepository.save(order);
	                logService.logMessageToCloudWatch("[OrderService] Order confirmed: orderId=" + orderId + ", corrId=" + corrId);
	                System.out.println("Order saved after vendor approval");
	            } else {
	            	 order.setStatus("REJECTED");
	            	 orderRepository.save(order);
	            	 logService.logMessageToCloudWatch("[OrderService] Vendor has rejected the orderId=" + orderId + ", corrId=" + corrId);
	                System.out.println("Vendor has rejected the order, please contact the vendor: " + vendorResponseDTO.getOrderName());
	            }}
	        } catch (Exception e) {
	            System.err.println("Error processing Kafka message: " + e.getMessage());
	        }finally {
	        	correlationGenerationService.removeCorrelation(orderId);
	        	MDC.clear();
	        }
	    }

	
	
}
