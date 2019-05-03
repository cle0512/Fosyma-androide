package eu.su.mas.dedaleEtu.mas.behaviours;


import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalInformations;
import jade.core.behaviours.SimpleBehaviour;

public class ToConfigBehaviour extends SimpleBehaviour {
	
	private MovingAgent myMovingAgent;
	private boolean finished;
	private int endValue;
	private static final long serialVersionUID = -8309045786154962614L;

	public ToConfigBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent);
		this.myMovingAgent = (MovingAgent) myagent;
		this.finished = false;
		this.endValue = 1;
	}
	
	
	@Override
	public void action() {
		
		this.myMovingAgent.setStatus(PersonalInformations.statusType.toConfig);
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		this.finished = false;
		
		if (myPosition != null) {
			
			if (this.myMovingAgent.getMap().getShortestPath(this.myMovingAgent.getTarget().getNodeId(), myPosition).size() < 1) {
				
				this.finished = true;
				this.endValue = 1;
				System.out.println("Fin du ToConfig " + this.myMovingAgent.getPersonalInfos().getName() + " in " + this.myMovingAgent.getTarget().getNodeId());
			}
			else {
				if ((this.myMovingAgent.getObjective() == null || this.myMovingAgent.getObjective().isEmpty())) {
					this.myMovingAgent.setObjective(this.myMovingAgent.getMap().getShortestPath(myPosition, this.myMovingAgent.getTarget().getNodeId()));
				}
				else {
					if (!(this.myMovingAgent.getObjective().isEmpty())) {
						/*System.out.println(this.myMovingAgent.getObjective() + " de " + this.myMovingAgent.getPersonalInfos().getName()
								+ " qui vise " + this.myMovingAgent.getTarget().getNodeId());*/
						String nextNode = this.myMovingAgent.getObjective().get(0);
						
						this.myMovingAgent.moveFromTo(myPosition, nextNode, 0);
					}
				}
			}
		}
	}

	public boolean done() {
		return finished;
	}
	
	public int onEnd()
	{
		return this.endValue;
	}

}
