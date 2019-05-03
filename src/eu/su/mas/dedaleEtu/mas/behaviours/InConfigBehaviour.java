package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class InConfigBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 6964557912630111478L;
	
	private MovingAgent myMovingAgent;
	private int endValue;
	private boolean finished;

	public InConfigBehaviour(Agent myagent) {
		super(myagent);
		this.endValue=1;
		this.myMovingAgent = (MovingAgent) myagent;
		this.finished = false;
		
	}

	@Override
	public void action() {
		try {
			this.myMovingAgent.doWait(((MovingAgent) myMovingAgent).getSpeed() / 2 );
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.finished = false;

		List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent) this.myMovingAgent).observe();
		
		if (lobs.get(0).getRight().size() < 3) {
			this.endValue = 1;
			this.finished = true;
			//System.out.println(this.myMovingAgent.getPersonalInfos().getName() + " repars au tanker : y a plus rien");
		}
		else {
			int quantity = 0;
			if (((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace() != null && ((AbstractDedaleAgent) this.myMovingAgent).getBackPackFreeSpace() > 0) {
				quantity = ((AbstractDedaleAgent) this.myAgent).pick();
			}
			
			if (quantity > 0) 
			{
				//System.out.println(this.myMovingAgent.getPersonalInfos().getName() + " a réussi à pick");
				this.endValue = 1;
				this.finished = true;
			}
			else {
				boolean open = false;
				for (Couple<Observation, Integer> obs : lobs.get(0).getRight()) {
					if (obs.getLeft().equals(Observation.GOLD)) {
						open = ((AbstractDedaleAgent)this.myAgent).openLock(Observation.GOLD);
					}
					if (obs.getLeft().equals(Observation.DIAMOND)) {
						open = ((AbstractDedaleAgent)this.myAgent).openLock(Observation.DIAMOND);
					}
				}
				for (Couple<Observation, Integer> obs : lobs.get(0).getRight()) {
					if (obs.getLeft().equals(Observation.LOCKSTATUS) && obs.getRight() == 1) {
						open = true;
					}
				}
				
				
				if (open) {
					if (((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace() != null && ((AbstractDedaleAgent) this.myMovingAgent).getBackPackFreeSpace() > 0) {
						((AbstractDedaleAgent) this.myAgent).pick();
						//System.out.println(this.myMovingAgent.getPersonalInfos().getName() + " a réussi à pick");
					}
					
					//System.out.println(this.myMovingAgent.getPersonalInfos().getName() + " a réussi à ouvrir");
					this.endValue = 1;
					this.finished = true;
				}
			}
		}
	}

	@Override
	public boolean done() {
		return this.finished;
	}
	
	
	public int onEnd() {
		return this.endValue;
	}
}
