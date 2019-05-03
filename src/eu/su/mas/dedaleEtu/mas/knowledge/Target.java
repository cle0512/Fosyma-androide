package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;

public class Target implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6889366797551726501L;
	private String nodeId;
	
	public Target(String nodeId) {
		this.nodeId = new String(nodeId);
	}
	
	public String getNodeId() {
		return this.nodeId;
	}
}
