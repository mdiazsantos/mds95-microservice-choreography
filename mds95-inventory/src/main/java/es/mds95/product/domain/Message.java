package es.mds95.product.domain;

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

	private static final long serialVersionUID = -5989345102638611924L;
	
	private String source;
	private String destination;
	private Integer productId;

}
