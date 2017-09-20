package com.siemens.scr.avt.ad.query.common;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.siemens.scr.avt.ad.query.model.JoinTree;
import com.siemens.scr.avt.ad.query.model.LoadingStrategy;
import com.siemens.scr.avt.ad.query.model.TableVertex;

import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;

/**
 * Natural join means FK column has the same name as in the table it references.
 * 
 * In fact it can be a forest, not only a tree.
 * 
 * @author Xiang Li
 * 
 */
public class NaturalJoinTree extends JoinTree<NaturalFKEdge> {
	private static Logger logger = Logger.getLogger(NaturalJoinTree.class);

	private LoadingStrategy<NaturalJoinTree> loadingStrategy;
	
	public NaturalJoinTree(){
		super(new UndirectedSparseGraph());
	}
	
	NaturalFKEdge createEdge(String label, String[] ends) {
		TableVertex v0  =	createVertex(ends[0]);
		TableVertex v1 = createVertex(ends[1]);
		return new NaturalFKEdge(v0, v1, label);
	}
	
	NaturalFKEdge addEdge(String label, String[] ends) {
		NaturalFKEdge edge = createEdge(label, ends);
		addEdge(edge);
		return edge;
	}

	void handleFK(String table, String referencedTable, String fk){
		if(table == null)logger.error("Empty table");
		if(referencedTable == null)logger.error("Empty referenced table");
		if(fk == null)logger.error("Empty FK");
		
		this.addEdge(fk, new String[]{table, referencedTable});
	}

	LoadingStrategy<NaturalJoinTree> getLoadingStrategy() {
		if(loadingStrategy == null){
			loadingStrategy = new NaturalJoinTreeLoadingStrategy();
		}
		
		return loadingStrategy;
	}


	public void loadFromHibernateMapping(File hibernateMapping)
			throws JDOMException, IOException {
		getLoadingStrategy().loadFromFile(this, hibernateMapping);
	}

	public void loadFromHibernateMapping(InputStream hibernateMapping)
			throws JDOMException, IOException {
		getLoadingStrategy().loadFromInputStream(this, hibernateMapping);
	}

	public void loadFromHibernateMappings(List<File> mappings)
			throws JDOMException, IOException {
		getLoadingStrategy().loadFromFiles(this, mappings);
	}

	@Override
	public void loadDefault() {
		getLoadingStrategy().loadDefault(this);
	}
	
}
