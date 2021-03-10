package es.mds95.order.controller;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.mds95.order.domain.Message;
import es.mds95.order.domain.Order;
import es.mds95.order.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RestController
@RequestMapping(value = "orders")
public class OrderController {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Integer ORDER_ID = 1;
	private List<Order> orders = new LinkedList<>();
	
	@PostConstruct
    protected void init() {
		// Simple exchange
        rabbitAdmin.declareExchange(new TopicExchange("ORDER_PRODUCT_EXCHANGE", true, false));
        
        // Queue
        rabbitAdmin.declareQueue(new Queue("ORDER_PRODUCT_QUEUE", true, false, true, null));
        
        // Binding
        rabbitAdmin.declareBinding(new Binding("ORDER_PRODUCT_QUEUE", DestinationType.QUEUE, "ORDER_PRODUCT_EXCHANGE", 
        		"ORDER_PRODUCT_ROUTING_KEY", null));
        
    }
	
	/**
	 * Creates an order
	 * 
	 * @param order The order
	 * @return The created order
	 * @throws JsonProcessingException 
	 */
	@PostMapping
	public ResponseEntity<Order> createOrder(@RequestBody(required = true) Order order) throws JsonProcessingException {
		
		order.setId(ORDER_ID++);
		order.setStatus(OrderStatus.PENDING);
		orders.add(order);
		
		send(new Message("ORDER", "PRODUCT", order.getProductId()));

		return ResponseEntity.ok(order);
		
	}
	
	/**
	 * Gets all orders.
	 * 
	 * @return The order list
	 */
	@GetMapping
	public ResponseEntity<List<Order>> getOrderList() {
		
		return ResponseEntity.ok(orders);
		
	}
	
	/**
	 * Custom method for sending a message to a RabbitMQ queue with asked productId.
	 * 
	 * @param The message sent
	 * @throws JsonProcessingException 
	 */
    @Transactional
    public void send(Message message) throws JsonProcessingException {
    	
    	// Message object to JSON string
    	String messageAsString = objectMapper.writeValueAsString(message);
    	
        rabbitTemplate.convertAndSend("ORDER_PRODUCT_EXCHANGE", "ORDER_PRODUCT_ROUTING_KEY", messageAsString);
        
    }

}
