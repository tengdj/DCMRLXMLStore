package com.siemens.scr.avt.ad.query.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.JDOMException;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;

/**
 * We only support for now acyclic database schema, which is unambiguous. The
 * schema can be in fact a forest. But the query should be forming a connected
 * component of the forest.
 * 
 * @author Xiang Li
 * 
 */
public abstract class JoinTree<T extends FKEdge> {
	public static final String FK = "JoinTree.ForeignKey";
	public static final String TABLE_NAME = "JoinTree.TableName";

	private Graph graph;
	private UnweightedShortestPath shortestPath;
	private Map<String, TableVertex> vertexMap = new HashMap<String, TableVertex>();

	protected JoinTree(Graph graph) {
		this.graph = graph;
		this.shortestPath = new UnweightedShortestPath(this.getGraph());
	}

	public void expandTableSet(Set<String> tableSet) {
		Set<T> edges = findJoinTree(tableSet);
		for (T edge : edges) {
			tableSet.add(edge.getEndpoints().getFirst().toString());
			tableSet.add(edge.getEndpoints().getSecond().toString());
		}
	}

	public List<String> buildJoinConditions(Set<String> tableSet) {
		Set<T> edges = findJoinTree(tableSet);

		LinkedList<String> joinConditions = new LinkedList<String>();
		for (T edge : edges) {
			joinConditions.add(edge.toJoin());
		}

		return joinConditions;
	}

	public void addEdge(T edge) {
		getGraph().addEdge(edge);
	}

	protected Graph getGraph() {
		return graph;
	}

	public Set<String> getTables(){
		Set<String> tables = new HashSet<String>();
		Set<TableVertex> set = graph.getVertices();
		for(TableVertex v : set){
			tables.add(v.getTableName());
		}
		return tables;
	}
	protected TableVertex findVertex(String name) {
		return getVertexForName(name, false);
	}

	public TableVertex createVertex(String name) {
		return getVertexForName(name, true);
	}

	protected Set<T> findJoinTree(Set<String> tableSet) {
		if (tableSet == null || tableSet.size() <= 1) {
			return Collections.emptySet();
		}

		Set<T> edges = new HashSet<T>();
		Iterator<String> it = tableSet.iterator();
		TableVertex src = findVertex(it.next());
		while (it.hasNext()) {
			edges.addAll(shortestPath(src, findVertex(it.next())));
		}
		return edges;
	}

	/**
	 * First search in the cache. If not found, create one vertex if required.
	 * 
	 * @param name
	 *            the name of the table
	 * @param create
	 *            whether to create a new vertex if not found.
	 * @return an existing vertex with the same name in the cache; if not
	 *         existing, create a new vertex if <code>create</code> is true.
	 */
	private TableVertex getVertexForName(String name, boolean create) {
		TableVertex vertex = vertexMap.get(name);
		if (vertex == null && create) {
			vertex = new TableVertex(name);
			getGraph().addVertex(vertex);
			vertexMap.put(name, vertex);
		}
		return vertex;
	}

	private List<T> shortestPath(TableVertex src, TableVertex dest) {
		Map pathMap = shortestPath.getIncomingEdgeMap(src);

		List<T> edges = new LinkedList<T>();
		TableVertex target = dest;
		while (target != src) {
			T edge = (T) pathMap.get(target);
			edges.add(edge);
			target = (TableVertex) edge.getOpposite(target);
		}
		return edges;
	}

	public abstract void loadFromHibernateMapping(File hibernateMapping)
			throws JDOMException, IOException;

	public abstract void loadFromHibernateMapping(InputStream hibernateMapping)
			throws JDOMException, IOException;

	public abstract void loadFromHibernateMappings(List<File> mappings)
			throws JDOMException, IOException;

	public abstract void loadDefault();

}