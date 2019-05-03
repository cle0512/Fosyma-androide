package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.DynamicPerception;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.Objective;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalInformations;
import eu.su.mas.dedaleEtu.mas.knowledge.Target;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;

public class MovingAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -8585386615629319743L;
	
	protected Target targetPt;
	protected List<String> objective;
	protected PersonalInformations personalInfos;
	
	protected boolean isDominated;
	protected PersonalInformations dominantInfos;
	protected List<String> dominantObjective;
	
	protected MapRepresentation myMap;
	protected Hashtable<String, DynamicPerception> dynamic_information_to_share;
	protected List<String> openNodes;
	protected Set<String> closedNodes;
	
	private long currentTime;
	
	protected String gravityCenter;
	
	
	protected Integer blockCount;
	
	
	protected Integer speed;
	
	protected void setup(){
		super.setup();
		
		this.personalInfos = new PersonalInformations(this);
		this.isDominated = false;
		this.speed = 100;
		this.blockCount = 0;
		this.currentTime = 0;
		this.dynamic_information_to_share=new Hashtable<String, DynamicPerception>();
	}
	
	// ======================== Constructeurs =======================
	public void setObjective(List<String> objective) {
		this.objective = objective;
	}
	
	public List<String> getObjective() {
		return this.objective;
	}
	
	public void setTarget(Target target) {
		this.targetPt = target;
	}
	
	public Target getTarget() {
		return this.targetPt;
	}
	
	public void setDominated(boolean dom) {
		this.isDominated = dom;
	}
	
	public boolean isDominated() {
		return this.isDominated;
	}
	
	public void setDomObjective(List<String> obj) {
		this.dominantObjective = obj;
	}
	
	public List<String> getDomObjective() {
		return this.dominantObjective;
	}
	
	public void setDomInfos(PersonalInformations domInfos) {
		this.dominantInfos = domInfos;
	}
	
	public PersonalInformations getDomInfos() {
		return this.dominantInfos;
	}
	
	public void setOpenNodes(List<String> nodes) {
		this.openNodes = nodes;
	}
	
	public List<String> getOpenNodes() {
		return this.openNodes;
	}
	
	public void setClosedNodes(Set<String> nodes) {
		this.closedNodes = nodes;
	}
	
	public Set<String> getClosedNodes() {
		return this.closedNodes;
	}
	
	public void setMap(MapRepresentation map) {
		this.myMap = map;
	}
	
	public MapRepresentation getMap() {
		return this.myMap;
	}
	
	public Integer getSpeed() {
		return this.speed;
	}
	
	public Hashtable<String, DynamicPerception> getDynamicInfos() {
		return this.dynamic_information_to_share;
	}
	
	public void setDynamicInfos(Hashtable<String, DynamicPerception> infos) {
		this.dynamic_information_to_share = infos;
	}
	
	public PersonalInformations getPersonalInfos() {
		return this.personalInfos;
	}
	
	public void setMissionId(Integer i) {
		this.personalInfos.setMissionId(i);
	}
	
	public PersonalInformations.statusType getStatus() {
		return this.personalInfos.getStatus();
	}
	
	public void setStatus(PersonalInformations.statusType s) {
		this.personalInfos.setStatus(s);
	}
	
	public String getGravityCenter() {
		return this.gravityCenter;
	}
	
	
	// ============================= Interblocages =========================
	public boolean isConflict(Objective obj) {
		if (obj != null) {
			String myPosition=this.getCurrentPosition();
			
			//boolean lsm = this.objective.contains(obj.getPersonalInformations().getPosition());
			boolean msl = obj.getNodeWay().contains(myPosition);
			//boolean mo = this.objective.isEmpty();
			boolean los = !(this.betterThan(obj));
			
			
			
			//if ((msl && lsm && los) || (msl && mo && los)) {
			if ((msl && los)) {
				//System.out.println("Conflit entre "+this.personalInfos.getName()+" et "+obj.getPersonalInformations().getName());
				return true;
			}
		}
		return false;
	}
	

	private boolean betterThan(Objective obj) {
		PersonalInformations comparedInfos = this.personalInfos;
		if (this.isDominated && this.dominantInfos != null) {
			//System.out.println("Comparaison dominée");
			comparedInfos = this.dominantInfos;
		}
		if (this.personalInfos.getName().equals(obj.getPersonalInformations().getName())) {
			//System.out.println("Victoire de "+this.personalInfos.getName()+" contre "+obj.getPersonalInformations().getName()+"Par susnommage");
			return true;
		}
		if (obj.getPersonalInformations().getStatus().equals(PersonalInformations.statusType.tanker)) {
			//System.out.println("Défaite de "+this.personalInfos.getName()+" contre "+obj.getPersonalInformations().getName()+"Par tankage");
			return false;			
		}
		/*if (this.blockCount > 8) {
			//System.out.println("Défaite de "+this.personalInfos.getName()+" contre "+obj.getPersonalInformations().getName()+"Par surblocage");
			return false;
		}*/
		if (obj.getPersonalInformations().getStatus().equals(PersonalInformations.statusType.toTanker) 
				&& comparedInfos.getStatus().equals(PersonalInformations.statusType.toConfig)) {
			//System.out.println("Défaite de "+this.personalInfos.getName()+" contre "+obj.getPersonalInformations().getName()+"Par surblocage");
			return false;
		}
		/*if (comparedInfos.getCharge() > obj.getPersonalInformations().getCharge()) {
			//System.out.println("Victoire de "+this.personalInfos.getName()+" contre "+obj.getPersonalInformations().getName()+"Par outcharge");
			return true;
		}*/
		if (comparedInfos.getName().compareTo(obj.getPersonalInformations().getName()) > 0) {
			//System.out.println("Victoire de "+this.personalInfos.getName()+" contre "+obj.getPersonalInformations().getName()+"Par nommage");
			return true;
		}
		//System.out.println("Défaite de "+this.personalInfos.getName()+" contre "+obj.getPersonalInformations().getName()+"Par defaut");
		return false;
	}
	
	
	// Ajouter la gestion du wumpus
	public void unBlock(String myPosition, String myNewPosition, boolean onlyCheckWumpus) {
		if (myPosition.equals(myNewPosition)) {

			this.blockCount += 1;
			if (!(this.objective == null) && !(this.objective.isEmpty())) {
				if ((this.gravityCenter != null &&  this.gravityCenter == this.objective.get(0) && (this.blockCount > 1))
						|| (this.blockCount > 4)) {
					String nextNode = this.objective.get(0);
					if (!(this.targetPt.getNodeId().equals(nextNode))) {
						this.objective = this.myMap.getShortestPathExclude(myPosition, this.targetPt.getNodeId(), nextNode);
						if (!this.openNodes.isEmpty() && this.objective.isEmpty()) {
							this.targetPt = new Target(this.openNodes.get((int) getRandomIntegerBetweenRange(0, this.openNodes.size()-1)));
							this.objective = this.myMap.getShortestPath(myPosition, this.targetPt.getNodeId());
							System.out.println("a");
						}
					}
					else {
						if (!this.openNodes.isEmpty()) {
							this.targetPt = new Target(this.openNodes.get((int) getRandomIntegerBetweenRange(0, this.openNodes.size()-1)));
							this.objective = this.myMap.getShortestPath(myPosition, this.targetPt.getNodeId());
						}
					}
				}
			}
		}
		else {
			this.blockCount = 0;
		}
	}
	
	public static double getRandomIntegerBetweenRange(double min, double max){

	    double x = (int)(Math.random()*((max-min)+1))+min;

	    return x;

	}
	
	public void moveFromTo(String myPosition, String nextNode, Integer speed) { 
		
		
		/*
		System.out.println(myPosition);
		System.out.println(this.personalInfos.getPosition());
		System.out.println(this.objective);
		System.out.println(nextNode);
		*/
		List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent) this).observe();
		//System.out.println("Voici lobs => " + lobs);
		
		
		if (lobs != null && lobs.size() > 0) { 
			Couple<String,List<Couple<Observation,Integer>>> np = lobs.get(0);
			gestion_noeud_principal(np);
		}
		
		
		String nodeToGo = nextNode;
		
		
		if (nextNode == null) {
			if (this.objective != null && this.objective.isEmpty() == false)
				nodeToGo = objective.get(0);
		}
		
		((AbstractDedaleAgent) this).moveTo(nodeToGo);
		
		this.currentTime = System.currentTimeMillis();
	
		String myNewPosition=((AbstractDedaleAgent) this).getCurrentPosition();
		if (!(targetPt.getNodeId() == null))
		{
			this.unBlock(myPosition, myNewPosition, false);
		}
		if (myNewPosition != myPosition)
		{
			objective.remove(nodeToGo);
			this.personalInfos.setPosition(myNewPosition);
		}
		
		int usedSpeed = this.speed;
		if (speed != 0)
			usedSpeed = speed;
		try {
			this.doWait(usedSpeed - (System.currentTimeMillis() - currentTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

		
	
	public void gestion_noeud_principal(Couple<String,List<Couple<Observation,Integer>>> noeud_principal) {
		
		
		String cle = new String(noeud_principal.getLeft());
		boolean existence_dico = this.dynamic_information_to_share.containsKey(cle);
		DynamicPerception element = new DynamicPerception();
		
		
		if(existence_dico) {
			element = this.dynamic_information_to_share.get(cle);
			this.dynamic_information_to_share.remove(cle);
		}
		
	
		//on examine d'abord la liste du noeud
		List<Couple<Observation,Integer>> l= noeud_principal.getRight();
		
	
		if(l.size() == 1 ) {
			// on a un unique element, c'est une odeur
			
			if(l.get(0).getLeft().getName().equals(new String("Stench"))) {
				element.set_stench(true);
			}
		}
		
		if(l.size() == 4 ) {
			
			if (this.extractValue(l, new String("Gold")) > 0) {
				element = new DynamicPerception(false, 
						new String("Gold"),
						this.extractValue(l, new String("Gold")),
						this.extractValue(l, new String("LockPicking")),
						this.extractValue(l, new String("Strength")));
			}
			
			if (this.extractValue(l, new String("Diamond")) > 0) {
				element = new DynamicPerception(false, 
						new String("Diamond"),
						this.extractValue(l, new String("Gold")),
						this.extractValue(l, new String("LockPicking")),
						this.extractValue(l, new String("Strength")));
			}
		}
		
			
		if(l.size() == 5 ) {
			
			if (this.extractValue(l, new String("Gold")) > 0) {
				element = new DynamicPerception(true, 
						new String("Gold"),
						this.extractValue(l, new String("Gold")),
						this.extractValue(l, new String("LockPicking")),
						this.extractValue(l, new String("Strength")));
			}
			
			if (this.extractValue(l, new String("Diamond")) > 0) {
				element = new DynamicPerception(true, 
						new String("Diamond"),
						this.extractValue(l, new String("Gold")),
						this.extractValue(l, new String("LockPicking")),
						this.extractValue(l, new String("Strength")));
			}					
		}
		
		
		//on ajoute dans tous les cas
		this.dynamic_information_to_share.put(cle,element);
		
		
	}		
	
	public Integer extractValue(List<Couple<Observation,Integer>> l, String s) {
		for (Couple<Observation, Integer> c : l) {
			if (c.getLeft().getName().equals(s)) {
				return c.getRight();
			}
		}
		
		return 0;
	}
	
	
	
	
	
	public List<Couple<String, Couple<String, Integer>>> getTreasuresPos() {
		
		Hashtable<String, DynamicPerception> mapInfos = this.dynamic_information_to_share;
		List<Couple<String, Couple<String, Integer>>> treasuresPos = new ArrayList<Couple<String, Couple<String, Integer>>>();
		
		
		for (String pos : mapInfos.keySet()) {
			Treasure t = mapInfos.get(pos).getTreasure();
			
			if (t != null && !(t.isEmpty()) && t.getQuantity() > 0) {
				treasuresPos.add(new Couple<String, Couple<String, Integer>>(pos, new Couple<String, Integer>(t.getType(), t.getQuantity())));
			}
		}
		
		return treasuresPos;
	}
	
	
	
	
	
	public void computeGravityCenter() {
		
		//this.gravityCenter = "56";
		
		List<Couple<String, Couple<Integer, Integer>>> scores = new ArrayList<Couple<String, Couple<Integer, Integer>>>();
		

		
		List<Couple<String, Couple<String, Integer>>> treasuresPos = this.getTreasuresPos();
		

		System.out.println(treasuresPos);
		
		//int maxConnexity = 0;
		for (Node node : this.myMap.getNodes()) {
			Integer dist = 0;
			Integer connexity = 0;
			Integer currentDist = 0;
			for (Couple<String, Couple<String, Integer>> treasure : treasuresPos) {
				currentDist = this.myMap.getShortestPath(node.getId(), treasure.getLeft()).size();
				if (currentDist < 3) {
					dist += 10000000;
				}
				else {
					dist += currentDist * currentDist;
				}
			}
			for (Edge edge : this.myMap.getEdges()) {
				if (edge.getNode0().getId().equals(node.getId()) || edge.getNode1().getId().equals(node.getId())) {
					connexity ++;
				}
			}
			/*if (maxConnexity < connexity && (this.gravityCenter == null || this.gravityCenter.compareTo(node.getId()) < 0)) {
				this.gravityCenter = node.getId();
				maxConnexity = connexity;
			}*/
			
			
			scores.add(new Couple<String, Couple<Integer, Integer>>(node.getId(), new Couple<Integer, Integer>(dist, connexity)));
		}
		
		String gravityCenter = new String();
		double bestScore = 0;
		
		for (int i = 0; i < scores.size(); i++) {
			Couple<String, Couple<Integer, Integer>> score = scores.get(i);
			
			double computedScore = score.getRight().getRight() * 100000 / ((double) score.getRight().getLeft()); //score.getRight().getRight() * 1000 / score.getRight().getLeft();

			if (bestScore < computedScore || (bestScore == computedScore && score.getLeft().compareTo(gravityCenter) > 0 )) {
				bestScore = computedScore;
				gravityCenter = score.getLeft();
				
			}
		}
		
		this.gravityCenter = gravityCenter;
	}
	
	/*
	Hashtable<String, Integer> connexities = new Hashtable<String, Integer>();
	
	for (Edge edge : this.myMap.getEdges()) {
		String left = edge.getNode0().getId();
		if (connexities.containsKey(left)) {
			Integer c = connexities.get(left);
			connexities.remove(left);
			c++;
			connexities.put(left, c);
		}
		else {
			connexities.put(left, 1);
		}
		

		String right = edge.getNode1().getId();
		if (connexities.containsKey(right)) {
			Integer c = connexities.get(right);
			connexities.remove(right);
			c++;
			connexities.put(right, c);
		}
		else {
			connexities.put(right, 1);
		}
	}
	

	int maxConnexity = 0;
	for (String nodeId : connexities.keySet()) {
		Integer connexity = connexities.get(nodeId);
		
		if (maxConnexity < connexity && (this.gravityCenter == null || this.gravityCenter.compareTo(nodeId) < 0)) {
			this.gravityCenter = nodeId;
			maxConnexity = connexity;
		}
	}*/
}
