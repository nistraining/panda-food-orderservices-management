package panda.orderservices.management.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.vendor.management.entities.VendorResponseDTO;

import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.annotation.PostConstruct;
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
	
	@Autowired
	private VendorResolver vendorResolver;
	
//	@Autowired
//	private OktaTokenClient tokenClient;
	
	@Autowired
	private RestTemplate restTemplate;
	


	public Orders saveOrders(Orders order) {
	    try {
	        // Validate payload early
	        if (order.getOrderName() == null || order.getOrderName().trim().isEmpty()
	            || order.getQuantity() <= 0 || order.getOrderLocation() == 0) {
	            String faultyPayload = objectMapper.writeValueAsString(order);
	            logService.logMessageToCloudWatch("[OrderService] Invalid order payload: " + faultyPayload);
	            throw new IllegalArgumentException("Order payload is invalid");
	        }

	        // Save the order early to get the generated ID
	        order.setStatus("PENDING");
	        String resolvedVendorId = vendorResolver.resolveVendorId(order.getVendorName().trim(), order.getOrderLocation());
	        order.setVendorId(resolvedVendorId);
	        Orders savedOrder = orderRepository.save(order);
	        int orderId = savedOrder.getOrderId();
	        // Generate correlation ID with actual order ID
	        String correlationId = correlationGenerationService.generateForOrder(orderId);
	        MDC.put(correlationId, correlationId);

	        // Dispatch validation request
	        sendValidationRequestToVendor(savedOrder, correlationId);

	        logService.logMessageToCloudWatch("[OrderService] Order created: orderId=" + orderId + ", corrId=" + correlationId);
	        return savedOrder;

	    } catch (Exception e) {
	        logService.logMessageToCloudWatch("[OrderService] Exception occurred during order creation: " + e.getMessage());
	        throw new RuntimeException("Failed to create order: " + e.getMessage());
	    } finally {
	        MDC.clear();
	    }
	}



	public void sendValidationRequestToVendor(Orders order,String correlationId) {
		try {
			int orderId = order.getOrderId();
			String corrId =	correlationGenerationService.getCorrelationId(orderId).orElse("UNKNOWN");

		VendorValidationRequests vendorValidationRequests = new VendorValidationRequests(
				order.getOrderName(),
				orderId,
				order.getQuantity(),
				order.getOrderLocation(),
				order.getCreatedTimeStamp(),
				order.getVendorId()
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
		
	    @KafkaListener(topics = "${topic.name}", groupId = "order-management-group")
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
	            	 logService.logMessageToCloudWatch("[OrderService] Status=REJECTED orderId=" + orderId + ", corrId=" + corrId);
	                System.out.println("Vendor has rejected the order, please contact the vendor: " + vendorResponseDTO.getOrderName());
	            }}
	        } catch (Exception e) {
	            System.err.println("Error processing Kafka message: " + e.getMessage());
	        }finally {
	        	correlationGenerationService.removeCorrelation(orderId);
	        	MDC.clear();
	        }
	    }
	    
	    
	    @PostConstruct
	    public void init() {
	        System.out.println("OrderServices bean initialized");
	    }


	
	
}
