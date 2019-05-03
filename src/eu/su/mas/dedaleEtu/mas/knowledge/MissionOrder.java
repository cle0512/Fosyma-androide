package eu.su.mas.dedaleEtu.mas.knowledge;
import java.io.Serializable;

public class MissionOrder implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6614345354987326112L;
	
	
	private String position;
	private Integer id;
	
	public MissionOrder(String pos, Integer i) {
		position = pos;
		id = i;
	}
	
	public String getPosition() {
		return position;
	}
	
	public Integer getId() {
		return id;
	}
}
