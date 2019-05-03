package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.MovingAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.Target;
import jade.core.behaviours.SimpleBehaviour;


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
public class ExploSoloBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;

	/**
	 * Nodes known but not yet visited
	 */
	private List<String> openNodes;
	/**
	 * Visited nodes
	 */
	private Set<String> closedNodes;
	
	private MovingAgent myMovingAgent;
	
	
	
	// La valeur retournée à la fin du behaviour pour savoir dans quel état se rendre
	private int endValue;


	public ExploSoloBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap) {
		super(myagent);
		this.myMap=myMap;
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		this.endValue=1;
	}

	@Override
	public void action() {

		if(this.myMap==null)
			this.myMap = new MapRepresentation();
		
		if (this.myMovingAgent==null) {
			this.myMovingAgent = (MovingAgent) this.myAgent;
		}
		
		
		if (this.myMovingAgent.getObjective() == null)
			this.myMovingAgent.setObjective(new ArrayList<String>());
		
		
		
		// ======================================================================
		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
	
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs = null;
			lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */  
			

			//1) remove the current node from openlist and add it to closedNodes.
			this.closedNodes.add(myPosition);
			this.openNodes.remove(myPosition);
			this.myMap.addNode(myPosition);
			
			
			
			String nextNode = null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter = lobs.iterator();

			while(iter.hasNext()){
				String nodeId=iter.next().getLeft();
				if (!this.closedNodes.contains(nodeId)){
					if (!this.openNodes.contains(nodeId)){
						this.openNodes.add(nodeId);
						this.myMap.addNode(nodeId, MapAttribute.open);
						this.myMap.addEdge(myPosition, nodeId);	
					}else{
						this.myMap.addEdge(myPosition, nodeId);
					}
				}

				if ((this.myMovingAgent.getObjective() == null || this.myMovingAgent.getObjective().isEmpty()) && this.openNodes.contains(nodeId) && !(this.myMovingAgent.isDominated())) {
					nextNode=nodeId;
					List<String> newObj = new ArrayList<String>();
					newObj.add(nodeId);
					this.myMovingAgent.setObjective(newObj);
				}
			}
			

			this.myMovingAgent.setOpenNodes(this.openNodes);
			this.myMovingAgent.setClosedNodes(this.closedNodes);
			this.myMovingAgent.setMap(this.myMap);
			
			if (this.openNodes.isEmpty()) {
				finished = true;
				System.out.println("Exploration successufully done, behaviour removed.");
			}
			else {
				if (nextNode==null){
					if ((this.myMovingAgent.getObjective() == null || this.myMovingAgent.getObjective().isEmpty())) {
						this.myMovingAgent.setObjective(this.myMap.getClosestNodeWay(openNodes, myPosition));
					}
					if ((this.myMovingAgent.getObjective() != null && !this.myMovingAgent.getObjective().isEmpty())) {
						nextNode = this.myMovingAgent.getObjective().get(0);
					}
				}
				if (nextNode != null) {
					Target tar = new Target(nextNode);
					if (this.myMovingAgent.getObjective() != null && !(this.myMovingAgent.getObjective().isEmpty())) {
						tar = new Target(this.myMovingAgent.getObjective().get(this.myMovingAgent.getObjective().size()-1));
					}
					this.myMovingAgent.setTarget(tar);
					
					// ================================ Preuve qu'il n'y a pas de saut ===============================================
					//System.out.println(this.myMap.getShortestPath(myPosition, nextNode).size());
					this.myMovingAgent.moveFromTo(myPosition, nextNode, 0);
					
					//System.out.println(this.myMovingAgent.getTarget().getNodeId() + " by " +  this.myMovingAgent.getObjective());
				}
			}
		}
	}
			
	@Override
	public boolean done() {
		return finished;
	}
	
	@Override
	public int onEnd()
	{
		return this.endValue;
	}

}
