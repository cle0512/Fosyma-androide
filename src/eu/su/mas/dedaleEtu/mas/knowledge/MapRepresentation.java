package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import dataStructures.tuple.Couple;

/**
 * This simple topology representation only deals with the graph, not its content.</br>
 * The knowledge representation is not well written (at all), it is just given as a minimal example.</br>
 * The viewer methods are not independent of the data structure, and the dijkstra is recomputed every-time.
 * 
 * @author hc
 */
public class MapRepresentation implements Serializable {

	public enum MapAttribute {
		agent,open
	}

	private static final long serialVersionUID = -1333959882640838272L;

	private Graph g; //data structure
	private Viewer viewer; //ref to the display
	private Integer nbEdges;//used to generate the edges ids
	
	
	
	
	
	/*********************************
	 * Parameters for graph rendering
	 ********************************/
	
	private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
	private String nodeStyle_open = "node.agent {"+"fill-color: forestgreen;"+"}";
	private String nodeStyle_agent = "node.open {"+"fill-color: blue;"+"}";
	private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open;

	
	public MapRepresentation() {
		System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);
		this.viewer = this.g.display();
		this.nbEdges=0;
	}

	/**
	 * Associate to a node an attribute in order to identify them by type. 
	 * @param id
	 * @param mapAttribute
	 */
	public void addNode(String id,MapAttribute mapAttribute){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
		}else{
			n=this.g.getNode(id);
		}
		n.clearAttributes();
		n.addAttribute("ui.class", mapAttribute.toString());
		
		
		
		n.addAttribute("ui.label",id);
	}

	/**
	 * Add the node id if not already existing
	 * @param id
	 */
	public void addNode(String id){
		Node n=this.g.getNode(id);
		if(n==null){
			n=this.g.addNode(id);
		}else{
			n.clearAttributes();
		}
		n.addAttribute("ui.label",id);
	}

   /**
    * Add the edge if not already existing.
    * @param idNode1
    * @param idNode2
    */
	public void addEdge(String idNode1,String idNode2){
		try {
			this.nbEdges++;
			this.g.addEdge(this.nbEdges.toString(), idNode1, idNode2);
		}catch (EdgeRejectedException e){
			//Do not add an already existing one
			this.nbEdges--;
		}
		
	}
	
	public Iterable<Node> getNodes() {
		return this.g;
	}
	
	public Iterable<? extends Edge> getEdges() {
		return this.g.getEachEdge();
	}

	/**
	 * Compute the shortest Path from idFrom to IdTo. The computation is currently not very efficient
	 * 
	 * @param idFrom id of the origin node
	 * @param idTo id of the destination node
	 * @return the list of nodes to follow
	 */
	public List<String> getShortestPath(String idFrom,String idTo){
		List<String> shortestPath=new ArrayList<String>();

		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));
		dijkstra.compute();//compute the distance to all nodes from idFrom
		List<Node> path=dijkstra.getPath(g.getNode(idTo)).getNodePath(); //the shortest path from idFrom to idTo
		Iterator<Node> iter=path.iterator();
		while (iter.hasNext()){
			shortestPath.add(iter.next().getId());
		}
		dijkstra.clear();
		if (shortestPath.size()>0)
			shortestPath.remove(0);//remove the current position
		return shortestPath;
	}
	
	
	public SerializableGraph getSerializedGraph() {
		
		SerializableGraph sg = new SerializableGraph(this.g);
		return sg;
	}
	
	public void ReverseConvert(SerializableGraph sg) {
		// Cette fonction prends en argument le graph serializable reçu et le fusionne avec le graphe g
		
		// Récupération des listes du graphe donné en entrée
		List<Couple<String,String>> nodeIds = sg.getNodes();
		List<Couple<String, Couple<String,String>>> edgeIds = sg.getEdges();
		
		
		// Création des listes du graphe courant
		List<String> currentNodes = new ArrayList<String>();
		for (Node node : this.g) {
			currentNodes.add(node.getId());
		}

		List<Couple<String, String>> currentEdges = new ArrayList<Couple<String, String>>();
		for(Edge edge : this.g.getEachEdge()) {
			currentEdges.add(new Couple<String,String>(edge.getNode0().getId(), edge.getNode1().getId()));
		}
		
		// Ajout d'un noeud s'il n'est pas déjà dans le graphe
		for (Couple<String, String> nodeId : nodeIds) {
			
			if (!(currentNodes.contains(nodeId.getLeft()))) {
				
				if (nodeId.getRight() != null)
				{
					if (nodeId.getRight().equals("open")) {
						this.addNode(nodeId.getLeft(), MapAttribute.open);
					} 
				}
				else {
					this.addNode(nodeId.getLeft());
				}
				
				
			}
		}
		
		// Ajout d'une arrete si elle n'est pas déjà dans le graphe
		
		Integer newNbEdges = nbEdges;
		
		String leftId = "";
		String rightId = "";
		for (Couple<String, Couple<String,String>> edgeId : edgeIds) {
			
			leftId = edgeId.getRight().getLeft();
			rightId = edgeId.getRight().getRight();
			
			if (!(currentEdges.contains(new Couple<String, String>(leftId, rightId)) && !(currentEdges.contains(new Couple<String, String>(rightId, leftId))))) {

				try {
					newNbEdges ++;
					this.g.addEdge(newNbEdges.toString(), edgeId.getRight().getLeft(), edgeId.getRight().getRight());
				}catch (EdgeRejectedException e){
					//Do not add an already existing one
					newNbEdges--;
				}
			}
		}
		
		this.nbEdges = newNbEdges;
	}
	
	
	public List<String> getClosestNodeWay(List<String> nodeList, String myPosition) {
		
		int minDist = 100000;
		int newDist = 0;
		
		
		List<String> bestObj = new ArrayList<String>();
		List<String> currentObj = new ArrayList<String>();
		
		
		for (String node : nodeList) {
			if (!(node.equals(myPosition))) {
				currentObj = this.getShortestPath(myPosition, node);
				
				newDist = currentObj.size();
				
				
				if (newDist == 0){
					return currentObj;
				}
				
				if (newDist < minDist) {
					minDist = newDist;
					bestObj = currentObj;
				}
				
			}
		}
		
		
		return bestObj;
	}
	
	public List<String> getShortestPathExclude(String idFrom,String idTo, String excluded) {
		
		if (excluded.equals(idTo))
			return this.getShortestPath(idFrom, idTo);
		
		
		List<Couple<String, Couple<String, String>>> removedEdges = new ArrayList<Couple<String, Couple<String, String>>>();
		String edgeLeft = null;
		String edgeRight = null;
		String edgeId = null;
		//System.out.println("unBlock!");
		//System.out.println(excluded);
		for(Edge edge : this.g.getEachEdge()) {
			edgeLeft = edge.getNode0().getId();
			edgeRight = edge.getNode1().getId();
			edgeId = edge.getId();
			if (edgeLeft == excluded || edgeRight == excluded) {
				removedEdges.add(new Couple<String, Couple<String, String>>(edgeId, new Couple<String, String>(edgeLeft, edgeRight)));
				//System.out.println(edgeRight + ";" + edgeLeft);
			}
		}
		
		this.g.removeNode(excluded);
		
		List<String> path = this.getShortestPath(idFrom, idTo);
		
		this.g.addNode(excluded);
		for (Couple<String, Couple<String, String>> edge : removedEdges) {
			this.g.addEdge(edge.getLeft(), edge.getRight().getLeft(), edge.getRight().getRight());
		}
		return path;
	}
}
