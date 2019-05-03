package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.TankerAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MissionOrder;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalInformations;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class GetAgentInfosBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 4338440962187911200L;
	
	private TankerAgent myTankerAgent;
	private List<String> agents;
	private boolean finished;
	private int endValue;
	DFAgentDescription[] agentList;

	public GetAgentInfosBehaviour(Agent myagent) {
		super(myagent);
		this.myTankerAgent = (TankerAgent) myagent;
		this.finished = false;
		this.endValue = 1;
	}

	public void action() {
		
		try {
			this.myTankerAgent.doWait(((MovingAgent) myTankerAgent).getSpeed() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (this.agents == null || this.agents.isEmpty()) {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType( "explorer" );
			dfd.addServices(sd);
			try {
				agentList = DFService.search(this.myAgent, dfd);
			} catch (FIPAException fe) { fe.printStackTrace(); }
			
			
			this.agents = new ArrayList<String>();
			for (DFAgentDescription agentF : agentList) {
				this.agents.add(agentF.getName().getName());
			}
			System.out.println(agents);
		}
		
		boolean dataGathered = true;
		for (String agentName : agents) {
			boolean isGathered = false;
			if (!this.myTankerAgent.getAgentInfoList().isEmpty()) {
				for (PersonalInformations infos : this.myTankerAgent.getAgentInfoList()) {
					if (infos.getName().equals(agentName)) {
						isGathered = true;
					}
				}
			}
			
			if (!isGathered) {
				dataGathered = false;
			}
		}
		
		if (dataGathered) {
			this.finished = true;
			System.out.println("Fin de la récolte des données : nombre d'agents -> " + this.myTankerAgent.getAgentInfoList().size());
		}
		else {
			
			ACLMessage msgConfig=new ACLMessage(ACLMessage.PROPOSE);
			msgConfig.setSender(this.myAgent.getAID());
			msgConfig.setProtocol("UselessProtocol");
			
			
			for (DFAgentDescription agentF : agentList) 
			{
				boolean alreadyRegister = false;
				for (PersonalInformations infos : this.myTankerAgent.getAgentInfoList()) {
					if (infos.getName().equals(agentF.getName().getName()))
						alreadyRegister = true;
				}
				
				if (!alreadyRegister) {
					msgConfig.addReceiver(agentF.getName());
			   		 
					MissionOrder content = new MissionOrder("NeedInfos", 0);
					   		
					try {
						msgConfig.setContentObject(content);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					((AbstractDedaleAgent)this.myAgent).sendMessage(msgConfig);
					
					System.out.println("Demande envoyée à " + agentF.getName().getName());
					
					msgConfig.removeReceiver(agentF.getName());
				}
			}
			
			
			final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);			

			final ACLMessage msg = this.myAgent.receive(msgTemplate);
			
			if (msg != null) {	
				
				System.out.println(this.myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName());
				PersonalInformations agentInfos = null;
				try {
					Object newContent = msg.getContentObject();
					agentInfos = (PersonalInformations) newContent;
				} 
				catch (UnreadableException e) {
					e.printStackTrace();
				}
				
				if (agentInfos != null) {
					boolean alreadyGathered = false;
					for (PersonalInformations infos : this.myTankerAgent.getAgentInfoList()) {
						if (infos.getName().equals(agentInfos.getName())) {
							alreadyGathered = true;
						}
					}
					if (!alreadyGathered) {
						this.myTankerAgent.addAgentInfos(agentInfos);
						System.out.println(agentInfos+" added");
					}
					else {
						System.out.println(agentInfos+" not added");
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
