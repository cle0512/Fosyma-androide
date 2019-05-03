package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.TankerAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Configuration;
import eu.su.mas.dedaleEtu.mas.knowledge.DynamicPerception;
import eu.su.mas.dedaleEtu.mas.knowledge.MissionOrder;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalInformations;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class CalculConfigBehaviour extends TickerBehaviour {

	private static final long serialVersionUID = 745216071567625975L;
	
	private DFAgentDescription[] agentList;
	private List<Configuration> configList;
	private Hashtable<String, Integer> missionList;
	private TankerAgent myTankerAgent;
	private List<PersonalInformations> agentInfosList; 
	private boolean finished;

	public CalculConfigBehaviour(Agent myagent) {
		super(myagent, ((MovingAgent) myagent).getSpeed());
		this.myTankerAgent = (TankerAgent) myagent;
	}

	@Override
	protected void onTick() {
		
		if (this.agentList == null) {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType( "explorer" );
			dfd.addServices(sd);
			try {
				agentList = DFService.search(this.myAgent, dfd);
			} catch (FIPAException fe) { fe.printStackTrace(); }
		}
		
		
		// La liste est remplie artificiellement pour l'instant, il faudra la remplir par écoute ensuite
		if (agentInfosList == null) {
			agentInfosList = this.myTankerAgent.getAgentInfoList();
		}
		
		if (!(this.hasTreasures())) {
			System.out.println("Collecte terminée");
		}
		else {


			if (missionList == null) {
				missionList = new Hashtable<String, Integer>();
			}
			if (configList == null || configList.isEmpty()) {
				this.calculConfigs();
			}
			
			ACLMessage msgConfig=new ACLMessage(ACLMessage.PROPOSE);
			msgConfig.setSender(this.myAgent.getAID());
			msgConfig.setProtocol("UselessProtocol");
			
			
			for (DFAgentDescription agentF : agentList) 
			{
				msgConfig.addReceiver(agentF.getName());
			   		
		   		 String agentName = agentF.getName().getName();
		   		 
		   		 Integer missionId = missionList.get(agentName);
		   		 
		   		 Configuration config = this.getNthConfig(missionId);
		   		
		   		 if (config != null) {
			   		 MissionOrder content = new MissionOrder(config.getList().get(agentName), missionId);
				   		
					 try {
					 	msgConfig.setContentObject(content);
					 } catch (IOException e) {
				 		e.printStackTrace();
					 }

					 ((AbstractDedaleAgent)this.myAgent).sendMessage(msgConfig);
		   		 }

		   		 msgConfig.removeReceiver(agentF.getName());
			}
			
			
			
			
			final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);			

			final ACLMessage msg = this.myAgent.receive(msgTemplate);
			
			if (msg != null) {	
				
				//System.out.println(this.myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
				
				Integer id = -1;
				try {
					Object missionId = msg.getContentObject();
					id = (Integer) missionId;
				} 
				catch (UnreadableException e) {
					e.printStackTrace();
				}
				
				if (id != -1) {
					String agent = msg.getSender().getLocalName()+"@Ithaq";
					if (missionList.get(agent).compareTo(id) == 0) {

						//System.out.println("Reception d'id de mission correct : "+id+";"+missionList.get(agent));
						missionList.replace(agent, id+1);
						
						boolean pop = true;
						Integer minValue = configList.get(0).getId();
						
						for (String agentName : missionList.keySet()) {
							if (missionList.get(agentName) <= minValue) {
								pop = false;
							}
						}
						
						if (pop) {
							configList.remove(0);
							//System.out.println("Mise à jour des missions!!");
							if (configList.isEmpty()) {
								finished = true;
								System.out.println("Collecte terminée!!");
							}
						}
					}
				}
			}
		}
	}
	
	private void calculConfigs() {
		for (PersonalInformations agentInfos : agentInfosList) {
			missionList.put(agentInfos.getName(), 1);
		}
		
		Hashtable<String, DynamicPerception> dynamicInformations = this.myTankerAgent.getDynamicInfos();
		
		List<Couple<String, Couple<String, Integer>>> treasuresPos = this.myTankerAgent.getTreasuresPos();
		System.out.println(treasuresPos);
		
		Hashtable<String, PersonalInformations> allAgentsStats = new Hashtable<String, PersonalInformations>();
		for (PersonalInformations agentInfos : agentInfosList) {
			allAgentsStats.put(agentInfos.getName(), agentInfos);
		}
		
		List<String> treasuresToGo = new ArrayList<String>();
		for (Couple<String, Couple<String, Integer>> t : treasuresPos) {
			treasuresToGo.add(t.getLeft());
		}
		int missionId = 0;
		configList = new ArrayList<Configuration>();
			
		while (!treasuresToGo.isEmpty()) {
			List<String> freeAgents = new ArrayList<String>();
			for (PersonalInformations agentInfos : agentInfosList) {
				freeAgents.add(agentInfos.getName());
			}
			
			missionId ++;

			Configuration config = new Configuration(missionId);
			
			List<String> checkedTreasures = new ArrayList<String>();
			
			
			while (!freeAgents.isEmpty() && !treasuresToGo.isEmpty()) {
				for (String treasure : treasuresToGo) {
					List<String> selectedAgents = new ArrayList<String>();
					Treasure tStats = dynamicInformations.get(treasure).getTreasure();
					int nessLp = tStats.getLockpicking();
					int nessSt = tStats.getStrenght();
					int nessCp = tStats.getQuantity();
					
					
					//System.out.println("Before " + nessLp + ", "  + nessSt + ", " + nessCp);
					
					for (String ag : freeAgents) {
						PersonalInformations agentInfos = allAgentsStats.get(ag);
						if (nessLp > 0 && agentInfos.getLockpic() > 0) {
							selectedAgents.add(ag);
							nessLp -= agentInfos.getLockpic();
							nessSt -= agentInfos.getStrength();
							nessCp -= agentInfos.getCapacity();
						}
						if (nessSt > 0 && agentInfos.getStrength() > 0 && !selectedAgents.contains(ag)) {
							selectedAgents.add(ag);
							nessLp -= agentInfos.getLockpic();
							nessSt -= agentInfos.getStrength();
							nessCp -= agentInfos.getCapacity();
						}
						if (nessCp > 0 && agentInfos.getCapacity() > 0 && !selectedAgents.contains(ag)) {
							selectedAgents.add(ag);
							nessLp -= agentInfos.getLockpic();
							nessSt -= agentInfos.getStrength();
							nessCp -= agentInfos.getCapacity();
						}
					}
					
					//System.out.println("After " + nessLp + ", "  + nessSt + ", " + nessCp);

					
					for (String ag : selectedAgents) {
						freeAgents.remove(ag);
					}
					//System.out.println("freeAgents " + freeAgents);
					
					if (nessLp <= 0 && nessSt <= 0 && nessCp <= 0) {
						for (String ag : selectedAgents) {
							config.addAssociation(ag, treasure);
							//System.out.println(ag + " associated to " + treasure + " for mission "+missionId);
						}
						checkedTreasures.add(treasure);
						//System.out.println("checkedTreasures " + checkedTreasures);
					}
				}

				for (String t : checkedTreasures) {
					treasuresToGo.remove(t);
				}
				//System.out.println("treasuresToGo " + treasuresToGo);
				
			}
			
			if (!freeAgents.isEmpty()) {
				for (String ag : freeAgents) {
					config.addAssociation(ag, "NOMISSION");
				}
			}

			configList.add(config);
		}
	}
	
	
	
	private boolean hasTreasures() {
		List<Couple<String, Couple<String, Integer>>> treasuresPos = this.myTankerAgent.getTreasuresPos();
		return (treasuresPos != null && !treasuresPos.isEmpty());
	}
	
	private Configuration getNthConfig(Integer n) {
		for (Configuration config : configList) {
			if (config.getId() == n) {
				return config;
			}
		}
		return null;
	}
	
	

}
