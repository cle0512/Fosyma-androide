package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;


public class PersonalInformations implements Serializable {

	private static final long serialVersionUID = -3405402045767445343L;
	private String name;
	private String position;
	private Integer missionId;
	private Integer backPackCapacity;
	private Integer backPackCharge;
	private Integer strength;
	private Integer lockPicking;
	public enum statusType {
		tanker,toConfig,toTanker,explo,inConfig
	}
	protected statusType status;
	
	public PersonalInformations(AbstractDedaleAgent agent) {
		this.name = agent.getName();
		this.position = agent.getCurrentPosition();

		this.backPackCapacity = 0;
		
		// => conversion abstract d'un agent? ~.é
		Integer freeSpace =  agent.getBackPackFreeSpace();
		if (freeSpace != null) {
			this.backPackCapacity = freeSpace;
		}
		
		//System.out.println(((AbstractDedaleAgent) agent).getMyExpertise());
		
		
		this.backPackCharge = 0;
		for (Couple<Observation, Integer> c : agent.getMyExpertise()) {
			if (c.getLeft().getName().equals("Strength")) {
				this.strength = c.getRight();
			}
			if (c.getLeft().getName().equals("LockPicking")) {
				this.lockPicking = c.getRight();
			}
		}
		
		this.missionId = 0;
	}
	
	public PersonalInformations(String agentName) {
		this.name = agentName;
		this.missionId = 0;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPosition() {
		return this.position;
	}
	
	public void setPosition(String p) {
		this.position = p;
	}
	
	public Integer getMissionId() {
		return missionId;
	}
	
	public void setMissionId(Integer i) {
		missionId = i;
	}
	
	public Integer getCapacity() {
		return backPackCapacity;
	}
	
	public Integer getCharge() {
		return backPackCharge;
	}
	
	public void setCharge(Integer c) {
		backPackCharge = c;
	}
	
	
	public Integer getStrength() {
		return strength;
	}
	
	public Integer getLockpic() {
		return lockPicking;
	}
	
	public statusType getStatus() {
		return status;
	}
	
	public void setStatus(statusType s) {
		status = s;
	}
	
}
