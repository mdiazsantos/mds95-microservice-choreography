package es.mds95.order.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Make an abstract class for this.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1223486020425400563L;
	
	private String source;
	private String destination;
	private Integer productId;

}
