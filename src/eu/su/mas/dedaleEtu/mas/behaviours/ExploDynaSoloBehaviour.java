package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.DynamicPerception;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalInformations;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


/**
 * This behaviour allows an agent to explore the environment and learn the associated topological map.
 * The algorithm is a pseudo - DFS computationally consuming because its not optimised at all.</br>
 * 
 * When all the nodes around him are visited, the agent randomly select an open node and go there to restart its dfs.</br> 
 * This (non optimal) behaviour is done until all nodes are explored. </br> 
 * 
 * Warning, this behaviour does not save the content of visited nodes, only the topology.</br> 
 * Warning, this behaviour is a solo exploration and does not take into account the presence of other agents (or well) and indefinitely tries to reach its target node
 * @author hc
 *
 */
public class ExploDynaSoloBehaviour extends TickerBehaviour {



	/**
	 * 
	 */
	private static final long serialVersionUID = -249776434937900142L;

	private boolean finished = false;
	

	private MovingAgent myMovingAgent;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	
	// La valeur retournÃ©e Ã  la fin du behaviour pour savoir dans quel Ã©tat se rendre
	private int endValue;
	
	
	
	// ou TreeMap pour l'avoir triée
	
	private Hashtable<String, DynamicPerception> dynamic_information_to_share;
	//ajout fin

	public ExploDynaSoloBehaviour(final AbstractDedaleAgent myagent, Hashtable<String, DynamicPerception> dynamic_information_to_share) {
		super(myagent,((MovingAgent) myagent).getSpeed() / 3);
		this.dynamic_information_to_share=dynamic_information_to_share;
		this.endValue=1;
		this.myMovingAgent = (MovingAgent) myagent;
	}

	@Override
	public void onTick() {	
		if(this.dynamic_information_to_share==null)
			this.dynamic_information_to_share=new Hashtable<String, DynamicPerception>();
		
		if (this.myMovingAgent.getDynamicInfos() != null)
			this.dynamic_information_to_share = this.myMovingAgent.getDynamicInfos();
		
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		
		if (myPosition!=null){
			/*List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();
			if (myPosition.equals("90") || myPosition.equals("9") || myPosition.equals("55") || myPosition.equals("1") || myPosition.equals("99")) {
				System.out.println("Voici lobs => " + lobs);
			}
			
			if (lobs != null) { 
				if (lobs.size() > 0) {
					Couple<String,List<Couple<Observation,Integer>>> np = lobs.get(0);
					gestion_noeud_principal(np);
				}
				*/
				// Noeud voisin existe
				/*
				for (int i=1;i<lobs.size(); i++) {
					Couple<String,List<Couple<Observation,Integer>>> nv = lobs.get(i);
					gestion_noeud_voisin(nv);
					
				
				}*/		
/*				}	
			}
	*/	
			
			
			
			// =========================================================================
			//final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol("Dynamique"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));			
			
			ACLMessage msg = null;
			do {
				MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
				msg = this.myAgent.receive(msgTemplate);
				if (msg != null) {	
					
					//System.out.println(this.myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName());
					
					try {
						Hashtable<String, DynamicPerception> sgDyna = (Hashtable<String, DynamicPerception>) msg.getContentObject();
						//System.out.println(" dico dyna recu " + sgDyna.toString());
						
						
						fusion_dico(sgDyna);

					} catch (UnreadableException e) {
						e.printStackTrace();
					}

					
				}
			} while (msg != null);
					
					
					
					
					
					
			// ==========================================================================
			// Envoie de message
			ACLMessage msgSend=new ACLMessage(ACLMessage.PROPAGATE);
			msgSend.setSender(this.myAgent.getAID());
			msgSend.setProtocol("Dynamique");
			msgSend.setContent("Hello World, voici la Dynamique ");
			
			// ajout date pour envoi
					
			Date tt =new Date(java.lang.System.currentTimeMillis()) ;
			
			msgSend.setReplyByDate(tt);
			
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


			for (DFAgentDescription agentF : agentList) {
			   if (!( agentF.getName().getName().equals(this.myAgent.getName())))
				   {
				   		msgSend.addReceiver(agentF.getName());
						try {
							msgSend.setContentObject(dynamic_information_to_share);
						} catch (IOException e) {
							e.printStackTrace();
						}
				   }
			}
			
			for (DFAgentDescription agentF : agentList2) {
			   if (!( agentF.getName().getName().equals(this.myAgent.getName())))
				   {
				   		msgSend.addReceiver(agentF.getName());
						try {
							msgSend.setContentObject(dynamic_information_to_share);
						} catch (IOException e) {
							e.printStackTrace();
						}
				   }
			}
			

			
			((AbstractDedaleAgent)this.myAgent).sendMessage(msgSend);


			this.myMovingAgent.setDynamicInfos(dynamic_information_to_share);

		}
	}


	
	/*public void maj_odeur_voisin(Couple<String,List<Couple<Observation,Integer>>> noeud_voisin) {
		
		String cle = new String(noeud_voisin.getLeft());
		
		
		// si existence dans dico
		if (this.dynamic_information_to_share.containsKey(cle))
				{
			// on recupère
			DynamicPerception element = this.dynamic_information_to_share.get(cle);
			
			//on clone : necessaire ?
			DynamicPerception clone_element = new DynamicPerception(
					(noeud_voisin.getRight().size()!= 0),
					element.getTreasure().getType(),
					element.getTreasure().getQuantity(),
					element.getTreasure().getLockpicking(),
					element.getTreasure().getStrenght()
					);
			
			//on supprime
			this.dynamic_information_to_share.remove(cle);
			
			//on ajoute
			this.dynamic_information_to_share.put(cle,clone_element);	
			
			
				}
	}
		
		// maj de l'odeur au niv voisinage
		public void creation_voisin(Couple<String,List<Couple<Observation,Integer>>> noeud_voisin) {
			
			String cle = new String(noeud_voisin.getLeft());
			
			
			// si inexistence dans dico
			if (!this.dynamic_information_to_share.containsKey(cle))
					{	
				//on cree
				DynamicPerception element = new DynamicPerception();

				element.set_stench((noeud_voisin.getRight().size()!= 0));
				element.set_time_observe(new Date(java.lang.System.currentTimeMillis()));
						
				//on ajoute
				this.dynamic_information_to_share.put(cle,element);	
				
				
					}
			
		}
		
		
		public void gestion_noeud_voisin(Couple<String,List<Couple<Observation,Integer>>> noeud_voisin) {
			
			String cle = new String(noeud_voisin.getLeft());
			
			DynamicPerception clone_element = new DynamicPerception();
			
			// si existence dans dico
			if (this.dynamic_information_to_share.containsKey(cle))
					{
				// on recupère
				DynamicPerception element = this.dynamic_information_to_share.get(cle);
				
				clone_element = new DynamicPerception( 
						(noeud_voisin.getRight().size()!= 0),
						element.getTreasure().getType(),
						element.getTreasure().getQuantity(),
						element.getTreasure().getLockpicking(),
						element.getTreasure().getStrenght()
						);
				
				//on supprime
				this.dynamic_information_to_share.remove(cle);
				
				//on ajoute
				this.dynamic_information_to_share.put(cle,clone_element);	
				
				
					}	else {
						
						clone_element.set_stench((noeud_voisin.getRight().size()!= 0));
						clone_element.set_time_observe(new Date(java.lang.System.currentTimeMillis()));
								
						//on ajoute
						this.dynamic_information_to_share.put(cle,clone_element);
						
						
						
					}
			
			
			
			
			
			
		}*/
		
		
		
		
		
		
		
		
		
		
		

		public void fusion_dico(Hashtable<String, DynamicPerception> d) {	
			Set<String> keys = d.keySet(); 
			Iterator<String> it = keys.iterator(); 
			String cle;
			

			
			while(it.hasNext()){ 
				cle = new String(it.next());
				if(!  this.dynamic_information_to_share.containsKey(cle))
				{
					this.dynamic_information_to_share.put(cle,d.get(cle));
				} else {
					if ((d.get(cle).get_time_observe().compareTo(this.dynamic_information_to_share.get(cle).get_time_observe())) > 0 ) {
						//on ajoute dans tous les cas
						
						/*
						System.out.println("date avant fusion " + this.dynamic_information_to_share.size());
						System.out.println("date dico recu- "+ cle + " " + d.get(cle).get_time_observe().toString());
						System.out.println("date mon dico - "+ this.dynamic_information_to_share.get(cle).get_time_observe().toString());
						System.out.println("date après fusion " + this.dynamic_information_to_share.size());
						*/
						
						this.dynamic_information_to_share.remove(cle);
						this.dynamic_information_to_share.put(cle,d.get(cle));
					}
				}
			} 
			
		}		
			
			
		
		
		
		
	
	
	

	
}
