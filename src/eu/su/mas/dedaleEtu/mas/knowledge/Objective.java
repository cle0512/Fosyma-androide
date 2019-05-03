package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;

public class Objective implements Serializable {
	private static final long serialVersionUID = 4963054083578820899L;
	private List<String> nodeWay;
	private PersonalInformations personalInformations;
	private Target target;
	
	
	public Objective(List<String> nodeWay, AbstractDedaleAgent agent) {
		this.nodeWay = nodeWay;
		if (nodeWay == null || nodeWay.isEmpty())
			this.target = null;
		else
			this.target = new Target(nodeWay.get(nodeWay.size() - 1));
		this.personalInformations = new PersonalInformations(agent);
	}
	
	public Objective(List<String> nodeW, PersonalInformations personalInfos) {
		this.nodeWay = nodeW;
		if (nodeW == null || nodeW.isEmpty())
			this.target = null;
		else
			this.target = new Target(nodeW.get(nodeW.size() - 1));
		this.personalInformations = personalInfos;
	}
	
	public List<String> getNodeWay() {
		return this.nodeWay;
	}
	
	public PersonalInformations getPersonalInformations() {
		return this.personalInformations;
	}
	
	public Target getTarget() {
		return this.target;
	}
}
