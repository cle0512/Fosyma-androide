package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import dataStructures.tuple.Couple;

import java.util.ArrayList;
import java.util.List;

public class SerializableGraph  implements Serializable {
	
	private static final long serialVersionUID = -627017747374466521L;
	private List<Couple<String,String>> nodeIds;
	private List<Couple<String,Couple<String,String>>> edgeIds;
	
	
	public SerializableGraph(Graph inputG)
	{
		List<Couple<String,String>> nodeIds = new ArrayList<Couple<String,String>>();
		for (Node node : inputG) {
			nodeIds.add(new Couple<String, String>(node.getId(), node.getAttribute("ui.class")));
		}
		
		List<Couple<String,Couple<String,String>>> edgeIds = new ArrayList<Couple<String,Couple<String,String>>>();
		for(Edge edge : inputG.getEachEdge()) {
			edgeIds.add(new Couple<String, Couple<String, String>>(edge.getId(), new Couple<String, String>(edge.getNode0().getId(), edge.getNode1().getId())));
		}
		
		this.nodeIds = nodeIds;
		this.edgeIds = edgeIds;
	}
	
	public List<Couple<String,String>> getNodes()
	{
		return nodeIds;
	}
	
	public List<Couple<String,Couple<String,String>>> getEdges()
	{
		return edgeIds;
	}
}
