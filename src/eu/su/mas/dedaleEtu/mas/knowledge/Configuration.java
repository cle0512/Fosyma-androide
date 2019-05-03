package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.Hashtable;

public class Configuration implements Serializable  {

	private static final long serialVersionUID = -333313929680701861L;
	
	// Association agentID -> position du tresor
	private Hashtable<String, String> associationList;
	private Integer id;

	public Configuration(Integer id) {
		this.associationList = new Hashtable<String, String>();
		this.id = id;
	}
	
	public void addAssociation(String agentId, String treasurePos) {
		this.associationList.put(agentId, treasurePos);
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public Hashtable<String,String> getList() {
		return this.associationList;
	}
}

