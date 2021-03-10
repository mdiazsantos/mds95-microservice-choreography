package es.mds95.product.controller;

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

import es.mds95.product.domain.Message;
import es.mds95.product.domain.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RestController
@RequestMapping(value = "products")
public class ProductController {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    @Autowired
    private ObjectMapper objectMapper;
	
	private Integer PRODUCT_ID = 1;
	private List<Product> products = new LinkedList<>();
	
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
	
	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody(required = true) Product product) {
		
		product.setId(PRODUCT_ID++);
		products.add(product);

		return ResponseEntity.ok(product);
		
	}
	
	@GetMapping
	public ResponseEntity<List<Product>> getProductList() {
		
		return ResponseEntity.ok(products);
		
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
