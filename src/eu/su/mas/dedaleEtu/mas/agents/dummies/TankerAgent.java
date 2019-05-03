package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.BlockBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.CalculConfigBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploDynaSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.GetAgentInfosBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ToCenterBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.TransmitMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalInformations;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class TankerAgent extends MovingAgent  {

	private static final long serialVersionUID = -3907606830898164193L;
	

	private  static  final  String Explo = "Explo";
	private  static  final  String ToCenter = "ToCenter";
	private  static  final  String getAgentInfos = "GetAgentInfos";
	private  static  final  String CalculConfig = "CalculConfig";
	

	private List<PersonalInformations> agentInfosList; 
	
	
	protected void setup(){

		super.setup();
		
		this.agentInfosList = new ArrayList<PersonalInformations>();
		
		this.setStatus(PersonalInformations.statusType.tanker);
		

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		lb.add(new ExploDynaSoloBehaviour(this,this.dynamic_information_to_share));
		lb.add(new TransmitMapBehaviour(this, this.myMap));
		lb.add(new BlockBehaviour(this));
		
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		// Define the  different  states and behaviours
		
		fsm.registerFirstState (new ExploSoloBehaviour(this,this.myMap), Explo);
		fsm.registerState (new ToCenterBehaviour(this), ToCenter);
		fsm.registerState (new GetAgentInfosBehaviour(this), getAgentInfos);
		fsm.registerState(new CalculConfigBehaviour(this), CalculConfig);

		fsm. registerDefaultTransition (Explo,ToCenter);
		fsm. registerDefaultTransition (ToCenter,CalculConfig);
		fsm. registerTransition (Explo,ToCenter, 1);
		fsm. registerTransition (ToCenter,getAgentInfos, 1);
		fsm. registerTransition (getAgentInfos,CalculConfig, 1);
		
		lb.add(fsm);
		
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID()); 
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType( "tanker" ); 

		sd.setName(getLocalName() );
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd );
		} catch (FIPAException fe) { fe.printStackTrace(); }
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		
		
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");
		
	}
	
	public List<PersonalInformations> getAgentInfoList() {
		return this.agentInfosList;
	}
	
	public void addAgentInfos(PersonalInformations info) {
		this.agentInfosList.add(info);
	}
}
