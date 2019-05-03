package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MissionOrder;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalInformations;
import eu.su.mas.dedaleEtu.mas.knowledge.Target;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ToTankerBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 48044161455416377L;
	private String gravityCenter;
	private MovingAgent myMovingAgent;
	private boolean finished;
	private int endValue;
	private DFAgentDescription tankerAdress;
	
	
	public ToTankerBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent);
		this.gravityCenter = null;
		this.myMovingAgent = (MovingAgent) myagent;
		this.finished = false;
		this.endValue = 1;
		this.tankerAdress = null;
		
	}
	
	
	
	
	
	@Override
	public void action() {
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition != null) {
			
			if (this.myMovingAgent.getGravityCenter() == null) {
				if (this.gravityCenter == null) {
					this.myMovingAgent.computeGravityCenter(); 
					this.gravityCenter = this.myMovingAgent.getGravityCenter();
					//System.out.println("GC!!! ======> " + this.myMovingAgent.getGravityCenter());
				}
			}
			
			if (this.myMovingAgent.getStatus().equals(PersonalInformations.statusType.explo) || 
					this.myMovingAgent.getStatus().equals(PersonalInformations.statusType.toConfig)) {
				
				//System.out.println("Fixation du gc pour cible pour " +  this.myAgent.getLocalName());
				this.myMovingAgent.setObjective(this.myMovingAgent.getMap().getShortestPath(myPosition, this.gravityCenter));
				this.myMovingAgent.setTarget(new Target(this.gravityCenter));
				
				this.myMovingAgent.setStatus(PersonalInformations.statusType.toTanker);
				this.finished = false;
			}
			
			if (this.tankerAdress == null) {
				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType( "tanker" );
				dfd.addServices(sd);
				DFAgentDescription[] agentList = null;
				try {
					agentList = DFService.search(this.myAgent, dfd);
				} catch (FIPAException fe) { fe.printStackTrace(); }
				
				
				for (DFAgentDescription agentF : agentList) 
				{
					
				    if (( agentF.getName().getName().equals("Tanker@Ithaq")))
				    {
				   		this.tankerAdress = agentF;
				    }
				}
				if (this.tankerAdress == null) {
					System.out.println("Cant find Tanker adress!");
				}
			}
			
			receiveConfig(myPosition);
				
			if (this.myMovingAgent.getMap().getShortestPath(this.myMovingAgent.getGravityCenter(), myPosition).size() < 2 
					&& this.myMovingAgent.getPersonalInfos().getMissionId() > 0
					&& ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace() != null
					&&	((AbstractDedaleAgent) this.myMovingAgent).getBackPackFreeSpace() != 
							this.myMovingAgent.getPersonalInfos().getCapacity()) {
				((AbstractDedaleAgent)this.myAgent).emptyMyBackPack("Tanker");
			}
			else {
				if ((this.myMovingAgent.getObjective() == null || this.myMovingAgent.getObjective().isEmpty())) {
					this.myMovingAgent.setObjective(this.myMovingAgent.getMap().getShortestPath(myPosition, this.gravityCenter));
				}
				if (!(this.myMovingAgent.getObjective().isEmpty())) {
					String nextNode = this.myMovingAgent.getObjective().get(0);
					
					this.myMovingAgent.moveFromTo(myPosition, nextNode, 0);
				}
			}
		}
	}
	
	private void receiveConfig(String myPosition) {
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
		if (msg != null) {	
			
			
			try {
				Object receivedMission = msg.getContentObject();
				
				MissionOrder mission = (MissionOrder) receivedMission;
				
				
				if (mission.getId() == 0) {
					
					ACLMessage msgPersonalInfos = new ACLMessage(ACLMessage.SUBSCRIBE);
					msgPersonalInfos.setSender(this.myAgent.getAID());
					msgPersonalInfos.setProtocol("UselessProtocol");
					
					msgPersonalInfos.addReceiver(tankerAdress.getName());
			   		
			   		
			   		try {
			   			msgPersonalInfos.setContentObject(this.myMovingAgent.getPersonalInfos());
					} catch (IOException e) {
						e.printStackTrace();
					}

			   		//System.out.println("Réponse de "+this.myAgent.getName());
					((AbstractDedaleAgent)this.myAgent).sendMessage(msgPersonalInfos);
					
				}
				else {
					
					
					
					
					if (mission.getPosition().equals("NOMISSION")) {
						this.myMovingAgent.setTarget(new Target(this.myMovingAgent.getCurrentPosition()));
						this.myMovingAgent.setMissionId(mission.getId());
						this.myMovingAgent.setObjective(new ArrayList<String>());
						this.finished = true;
						this.endValue = 1;
						System.out.println("No mission reçue par " + this.myAgent.getLocalName());
					}
					
					
					
					if (mission.getId() == this.myMovingAgent.getPersonalInfos().getMissionId() + 1) {

						//System.out.println("Nouvelle mission reçue par " + this.myAgent.getLocalName());
						
						if (((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace() == null || 
								((AbstractDedaleAgent) this.myMovingAgent).getBackPackFreeSpace().equals(this.myMovingAgent.getPersonalInfos().getCapacity())
								|| this.myMovingAgent.getPersonalInfos().getCapacity() == 0
								|| this.myMovingAgent.getPersonalInfos().getMissionId() == 1) {

							
							this.myMovingAgent.setTarget(new Target(mission.getPosition()));
							this.myMovingAgent.setMissionId(mission.getId());
							this.myMovingAgent.setObjective(this.myMovingAgent.getMap().getShortestPath(myPosition, this.myMovingAgent.getTarget().getNodeId()));
							this.finished = true;
							this.endValue = 1;
							
							//System.out.println("Fin du ToTanker pour " + this.myAgent.getLocalName());
						}
					}
					else {
						ACLMessage msgMissionComplete = new ACLMessage(ACLMessage.CONFIRM);
						msgMissionComplete.setSender(this.myAgent.getAID());
						msgMissionComplete.setProtocol("UselessProtocol");
						
						msgMissionComplete.addReceiver(tankerAdress.getName());
				   		
				   		
				   		try {
				   			msgMissionComplete.setContentObject(this.myMovingAgent.getPersonalInfos().getMissionId());
						} catch (IOException e) {
							e.printStackTrace();
						}

						((AbstractDedaleAgent)this.myAgent).sendMessage(msgMissionComplete);
					}
				}

			} catch (UnreadableException e) {
				e.printStackTrace();
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
