package es.mds95.product.listener;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.mds95.product.controller.ProductController;
import es.mds95.product.domain.Message;
import es.mds95.product.domain.Product;

@Component
@Transactional
@RabbitListener(queues = {"ORDER_PRODUCT_QUEUE"})
public class RabbitReceiver {

	// Substitute because service is not implemented
	@Autowired
	private ProductController productController;
	
	@Autowired
	private ObjectMapper objectMapper;

	@RabbitHandler
	public void receiveMessage(String messageAsString) throws JsonMappingException, JsonProcessingException {
		
		Message message = objectMapper.readValue(messageAsString, Message.class);

		if(message.getSource().equals("ORDER") && message.getDestination().equals("PRODUCT")) {
			
			// Look for a product with the selected id
			Optional<Product> productAux = productController.getProducts().stream()
					.filter(product -> product.getId().equals(message.getProductId())).findAny();

			if(productAux.isPresent()) {

				productController.send(new Message("PRODUCT", "ORDER", productAux.get().getId()));

			} else {

				productController.send(new Message("PRODUCT", "ORDER", null));

			}
			
		}

	}

}