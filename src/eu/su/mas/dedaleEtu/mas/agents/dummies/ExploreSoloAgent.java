package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.BlockBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploDynaSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.InConfigBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ToConfigBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ToTankerBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.TransmitMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalInformations;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * ExploreSolo agent. 
 * It explore the map using a DFS algorithm.
 * It stops when all nodes have been visited
 *  
 *  
 * @author hc
 *
 */

public class ExploreSoloAgent extends MovingAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	

	private  static  final  String Explo = "Explo";
	private  static  final  String ToTanker = "ToTanker";
	private  static  final  String ToConfig = "ToConfig";
	private  static  final  String InConfig = "InConfig";

	@Override
	protected void setup(){

		super.setup();
		
		
		this.setStatus(PersonalInformations.statusType.explo);
		

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		
		lb.add(new ExploDynaSoloBehaviour(this,this.dynamic_information_to_share));
		lb.add(new TransmitMapBehaviour(this, this.myMap));
		lb.add(new BlockBehaviour(this));

		
		FSMBehaviour fsm = new FSMBehaviour(this);
		
		fsm. registerFirstState (new ExploSoloBehaviour(this,this.myMap), Explo);
		fsm. registerState (new ToTankerBehaviour(this), ToTanker);
		fsm. registerState (new ToConfigBehaviour(this), ToConfig);
		fsm. registerState (new InConfigBehaviour(this), InConfig);
		
		
		fsm. registerDefaultTransition (Explo,ToTanker);
		fsm. registerDefaultTransition (ToTanker,ToConfig);
		fsm. registerDefaultTransition (ToConfig,ToTanker);
		fsm. registerDefaultTransition (ToConfig,InConfig);
		fsm. registerDefaultTransition (InConfig,ToConfig);
		fsm. registerDefaultTransition (InConfig,ToTanker);
		fsm. registerTransition (Explo,ToTanker, 1);
		fsm. registerTransition (ToTanker,ToConfig, 1);
		fsm. registerTransition (ToConfig,InConfig, 1);
		fsm. registerTransition (InConfig,ToConfig, 2);
		fsm. registerTransition (InConfig,ToTanker, 1);
		fsm. registerTransition (ToConfig,ToTanker, 2);
		lb.add(fsm);

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID()); 
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType( "explorer" ); 
		
		
		sd.setName(getLocalName() );
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd );
		} catch (FIPAException fe) { fe.printStackTrace(); }
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		
		
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
}




