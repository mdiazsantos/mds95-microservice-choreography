package es.mds95.order.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.mds95.order.controller.OrderController;
import es.mds95.order.domain.Message;
import es.mds95.order.domain.OrderStatus;

@Component
@Transactional
@RabbitListener(queues = {"ORDER_PRODUCT_QUEUE"})
public class RabbitReceiver {

	// Substitute because service is not implemented
	@Autowired
	private OrderController orderController;
	
	@Autowired
	private ObjectMapper objectMapper;

	@RabbitHandler
	public void receiveMessage(String messageAsString) throws JsonMappingException, JsonProcessingException {
		
		Message message = objectMapper.readValue(messageAsString, Message.class);
		
		if(message.getSource().equals("PRODUCT") && message.getDestination().equals("ORDER")) {
			
			// If productId is null it means that product does not exists
			orderController.getOrders().stream().filter(order -> order.getStatus().equals(OrderStatus.PENDING))
			.forEach(order -> {
				
				if(message.getProductId() == null) {
					
					order.setStatus(OrderStatus.REJECTED);
					
				} else {
					
					order.setStatus(OrderStatus.ACCEPTED);
					
				}
				
			});
		
		}

	}

}