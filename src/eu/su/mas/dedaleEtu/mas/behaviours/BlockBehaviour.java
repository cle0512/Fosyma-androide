package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Objective;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


public class BlockBehaviour extends TickerBehaviour {



	private static final long serialVersionUID = -249776435937900142L;
	private DFAgentDescription[] agentList;
	
	private MovingAgent myMovingAgent;
	
	
	
	// ou TreeMap pour l'avoir triée
	//ajout fin

	public BlockBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent, ((MovingAgent) myagent).getSpeed());
		myMovingAgent = (MovingAgent) myAgent;
	}
	
	
	public void onTick() {	
		
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		
		if (myPosition != null) {
			
			if (this.agentList == null) {
				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType( "explorer" );
				dfd.addServices(sd);
				
				this.agentList = null;
				try {
					this.agentList = DFService.search(this.myAgent, dfd);
				} catch (FIPAException fe) { fe.printStackTrace(); }
				
			}
			

			this.resolveConflict(myPosition);
			
			this.transmitObjective(agentList);
			
			this.myMovingAgent.setDominated(false);
		}
	}
	
	private List<String> getAgentObjective() {
		List<String> obj = myMovingAgent.getObjective();
		
		return obj;
	}
	
	
	private void transmitObjective(DFAgentDescription[] agentList) {

		// Message de d'objectif
		ACLMessage msgObj=new ACLMessage(ACLMessage.QUERY_IF);
		msgObj.setSender(this.myAgent.getAID());
		msgObj.setProtocol("UselessProtocol");

		List<String> objective = getAgentObjective();
		Objective sentObj = new Objective(objective, this.myMovingAgent.getPersonalInfos());
		if (this.myMovingAgent.isDominated()) {
			sentObj = new Objective(objective, this.myMovingAgent.getDomInfos());
		}
		
		
		for (DFAgentDescription agentF : agentList) {
		   if (!( agentF.getName().getName().equals(this.myAgent.getName())))
			   {
			   		msgObj.addReceiver(agentF.getName());

					try {
						msgObj.setContentObject(sentObj);
					} catch (IOException e) {
						e.printStackTrace();
					}
			   		
			   }
		   	
		}
		
		((AbstractDedaleAgent)this.myAgent).sendMessage(msgObj);
	}
	
	
	
	private void resolveConflict(String myPosition) {
		
		
		List<Objective> observedObjectives = this.readMessagesObjectives();
		
		for (Objective observedObjective : observedObjectives) {
			/*System.out.println(i);
			i++;
			System.out.println(this.myMovingAgent.getPersonalInfos().getName()+ " in " + this.myMovingAgent.getPersonalInfos().getPosition() + 
					" received from " + observedObjective.getPersonalInformations().getName() + " in " + 
					observedObjective.getPersonalInformations().getPosition() +  " who wanted to go by " + observedObjective.getNodeWay());

			if (this.myMovingAgent.isDominated()) {
				System.out.println("He is dominated by  " + 
						this.myMovingAgent.getDomInfos().getName());
			}*/
			
			if (this.myMovingAgent.isConflict(observedObjective)) {
				//System.out.println(this.myMovingAgent.getPersonalInfos().getName()+ " lost against "  + observedObjective.getPersonalInformations().getName());


				this.myMovingAgent.setDominated(true);
				this.myMovingAgent.setDomInfos(observedObjective.getPersonalInformations());
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs = null;
				lobs=((AbstractDedaleAgent)this.myAgent).observe();
				Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
				
				
				
				String nextNode = null;
				
				List<String> interestingNodes = new ArrayList<String>();
				while(iter.hasNext()){
					String nodeId=iter.next().getLeft();
					if (nextNode == null && 
							!(nodeId.equals(observedObjective.getPersonalInformations().getPosition()))
							&& !(nodeId.equals(this.myMovingAgent.getPersonalInfos().getPosition()))) {
						nextNode = nodeId;
					}
					if (!(observedObjective.getNodeWay().contains(nodeId)) 
							&& !(nodeId.equals(observedObjective.getPersonalInformations().getPosition()))
							&& !(nodeId.equals(this.myMovingAgent.getPersonalInfos().getPosition()))) {
						interestingNodes.add(nodeId);
					}
				}
				if (!interestingNodes.isEmpty()) {
					int r = (int) getRandomIntegerBetweenRange(0, interestingNodes.size()-1);
					nextNode = interestingNodes.get(r);
				}
				
				/*System.out.println(this.myMovingAgent.getPersonalInfos().getName()+ " in " + this.myMovingAgent.getPersonalInfos().getPosition() +
						" decided to go by " + nextNode + " with " + interestingNodes.size() + " possibilities");
				*/
				
				if (nextNode != null) {
					List<String> objective = new ArrayList<String>();
					objective.add(nextNode);
					this.myMovingAgent.setObjective(objective);
					/*System.out.println(this.myMovingAgent.getPersonalInfos().getName()+ " in " + this.myMovingAgent.getPersonalInfos().getPosition() + 
							" decided to go with " +  objective + " because he lost against " + observedObjective.getPersonalInformations().getName() + " in " + 
							observedObjective.getPersonalInformations().getPosition() +  " who wanted to go by " + observedObjective.getNodeWay());
					*/
				}
				
				
			}
		}
	}
	
	public static double getRandomIntegerBetweenRange(double min, double max){

	    double x = (int)(Math.random()*((max-min)+1))+min;

	    return x;

	}
	
	private List<Objective> readMessagesObjectives() {
		
		
		ACLMessage msgObject = null;
		List<Objective> listObj = new ArrayList<Objective>();
		
		do {
			final MessageTemplate msgTemplateObj = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);			

			msgObject = this.myAgent.receive(msgTemplateObj);
			if (msgObject != null) {	
				
				
				try {
					Object newObj = msgObject.getContentObject();
					
					Objective receivedObjective = (Objective) newObj;
					
					listObj.add(receivedObjective);

				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				
			}
		} while (msgObject != null);
		
		return listObj;
	}
}
		
		
		