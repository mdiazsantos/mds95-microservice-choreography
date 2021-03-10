package es.mds95.order.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
	
	private Integer id;
	private Integer productId;
	private Integer units;
	private OrderStatus status;

}
