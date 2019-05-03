package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Target;
import jade.core.behaviours.SimpleBehaviour;

public class ToCenterBehaviour extends SimpleBehaviour {
	
	private String gravityCenter;
	private MovingAgent myMovingAgent;
	private boolean finished;
	private int endValue;

	private static final long serialVersionUID = 638863085310028432L;
	
	public ToCenterBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent);
		this.gravityCenter = null;
		this.myMovingAgent = (MovingAgent) myagent;
		this.finished = false;
		this.endValue = 1;
	}
	@Override
	public void action() {

		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		if (this.gravityCenter == null) {
			this.myMovingAgent.computeGravityCenter(); 
			this.gravityCenter = this.myMovingAgent.getGravityCenter();
			this.myMovingAgent.setObjective(this.myMovingAgent.getMap().getShortestPath(myPosition, this.gravityCenter));
			this.myMovingAgent.setTarget(new Target(this.gravityCenter));
			System.out.println("GC!!! ======> " + this.myMovingAgent.getGravityCenter());
		}

		if (this.myMovingAgent.getTarget().getNodeId().equals(myPosition)) {
			this.finished = true;
			System.out.println("Fin du ToCenter");
		}
		else {
			if (this.myMovingAgent.getObjective() == null || this.myMovingAgent.getObjective().isEmpty()) {
				this.myMovingAgent.setObjective(this.myMovingAgent.getMap().getShortestPath(myPosition, this.gravityCenter));
			}
			else {
				String nextNode = this.myMovingAgent.getObjective().get(0);
				
				
				this.myMovingAgent.moveFromTo(myPosition, nextNode, 0);
			}
		}
	}

	@Override
	public boolean done() {
		return finished;
	}
	
	public int onEnd()
	{
		return this.endValue;
	}

}
