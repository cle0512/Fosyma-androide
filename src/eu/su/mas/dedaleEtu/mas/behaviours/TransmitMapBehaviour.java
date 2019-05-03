package eu.su.mas.dedaleEtu.mas.behaviours;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalInformations;
import eu.su.mas.dedaleEtu.mas.knowledge.SerializableGraph;
import eu.su.mas.dedaleEtu.mas.knowledge.Target;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class TransmitMapBehaviour extends TickerBehaviour {
	
	
	private MapRepresentation myMap;
	private List<String> openNodes;
	private Set<String> closedNodes;
	private MovingAgent myMovingAgent;
	
	
	private static final long serialVersionUID = 2673390070760966391L;

	public TransmitMapBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap) {
		super(myagent,((MovingAgent) myagent).getSpeed() - 1);
		this.myMap=myMap;
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		this.myMovingAgent = (MovingAgent) myagent;
	}

	
	protected void onTick() {
		
		
		this.openNodes = myMovingAgent.getOpenNodes();
		this.closedNodes = myMovingAgent.getClosedNodes();
		this.myMap = myMovingAgent.getMap();
		
		if (this.openNodes.isEmpty()) {
			//System.out.println("Plus de transmission de carte!");
			//this.myAgent.removeBehaviour(this);
		}
		else {
			
			
			ACLMessage msg = null;
			
			do {
				MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			

				msg = this.myAgent.receive(msgTemplate);
				if (msg != null) {	
					
					//System.out.println(this.myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
					
					try {
						Object sentMap = msg.getContentObject();
						
						SerializableGraph sgMap = (SerializableGraph) sentMap;
						
						myMap.ReverseConvert(sgMap);
						
						
						// Si un noeud actuel est ouvert alors qu'il ne l'est plus après, on l'enlève de la liste
						for (Couple<String,String> nodeId:sgMap.getNodes()) {
							
							if (nodeId.getRight() != null) {
								for (String openNode: this.openNodes) {
									if (nodeId.getLeft().equals(openNode)) {
										if (!(nodeId.getRight().equals("open"))) {
											this.openNodes.remove(openNode);
											this.closedNodes.add(openNode);
										}
									}
								}
								// Si un noeud n'est pas encore ouvert on l'ouvre
								if (!(this.openNodes.contains(nodeId.getLeft()))) {
									if (nodeId.getRight().equals("open")) {
										this.openNodes.add(nodeId.getLeft());
									}
								}
							}
							else {
								// Si un noued n'est pas fermé on le ferme
								if (!(this.closedNodes.contains(nodeId.getLeft())))
								{
									this.closedNodes.add(nodeId.getLeft());
								}
							}
						}
						
						
						//System.out.println(sgMap.getNodes());

					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
				}
			} while (msg != null);
			
			/*if (this.myMovingAgent.getStatus().equals(PersonalInformations.statusType.tanker)) {
				System.out.println("Je lis " + i + " messages");
			}*/
			
			
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType( "explorer" );
			dfd.addServices(sd);
			DFAgentDescription[] agentList = null;
			try {
				agentList = DFService.search(this.myAgent, dfd);
			} catch (FIPAException fe) { fe.printStackTrace(); }


			DFAgentDescription dfd2 = new DFAgentDescription();
			ServiceDescription sd2 = new ServiceDescription();
			sd2.setType( "tanker" );
			dfd2.addServices(sd2);
			DFAgentDescription[] agentList2 = null;
			try {
				agentList2 = DFService.search(this.myAgent, dfd2);
			} catch (FIPAException fe) { fe.printStackTrace(); }
			
			
			this.transmitMap(agentList);
			this.transmitMap(agentList2);
			
			this.myMovingAgent.setOpenNodes(this.openNodes);
			this.myMovingAgent.setClosedNodes(this.closedNodes);
			this.myMovingAgent.setMap(this.myMap);
		}
	}
	
	

	
	private void transmitMap(DFAgentDescription[] agentList) {
		// Message de contenu de carte
		ACLMessage msgMap=new ACLMessage(ACLMessage.INFORM);
		msgMap.setSender(this.myAgent.getAID());
		msgMap.setProtocol("UselessProtocol");
		
		
		
		
		SerializableGraph sg = myMap.getSerializedGraph();
		
		// Selction des recepteurs, il sera possible de faire une selection avancee ici
		for (DFAgentDescription agentF : agentList) {
		   if (!( agentF.getName().getName().equals(this.myAgent.getName())))
			   {
			   		
			   		msgMap.addReceiver(agentF.getName());
					try {
						msgMap.setContentObject(sg);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
			   		
			   		//System.out.println("Agent "+myAgent.getName()+" send message "+agentF.getName().getName());
			   }
		   	
		}
		
		((AbstractDedaleAgent)this.myAgent).sendMessage(msgMap);
					
	}
	
}
