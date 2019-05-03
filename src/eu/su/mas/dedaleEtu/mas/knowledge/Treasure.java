package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;

public class Treasure implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5548802624743482681L;
	private Integer lockpicking;
	private Integer strenght;
	private Integer quantity;
	private String type;
	private boolean isEmpty;
	
	
	public Treasure() {
		isEmpty = true;
		quantity = 0;
	}
	
	public Treasure(String ty, Integer str, Integer lp, Integer qt) {
		isEmpty = false;
		lockpicking = lp;
		strenght = str;
		quantity = qt;
		type = ty;
	}
	
	public void setQuantity(Integer qt) {
		quantity = qt;
	}
	
	public Integer getQuantity() {
		return quantity;
	}


	public Integer getLockpicking() {
		return lockpicking;
	}


	public Integer getStrenght() {
		return strenght;
	}


	public String getType() {
		return type;
	}
	
	public void setEmpty() {
		isEmpty = true;
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}


}


