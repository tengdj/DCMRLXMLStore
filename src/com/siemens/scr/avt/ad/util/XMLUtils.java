package com.siemens.scr.avt.ad.util;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Utility for navigating XML.
 * 
 * @author Xiang Li
 *
 */
public class XMLUtils {
	/**
	 * Retrieving all child nodes with the given path in the given namespace.
	 * 
	 * @param parents a list of parent nodes to start with.
	 * @param path a list of labels representing a path.
	 * @param collector a collecting list.
	 * @param ns a specified namespace.
	 */
	public static void getAllChildrenByPath(List<Element> parents, LinkedList<String> path, List<Element> collector, Namespace ns){
		assert path != null : "path should never be null!";
		
		if(path.size() == 0){
			collector.addAll(parents);
			return;
		}
		
		String step = path.remove(0);

		List<Element> parentCollector = new LinkedList<Element>();
		getAllChildrenByName(parents, step, parentCollector, ns);
		getAllChildrenByPath(parentCollector, path, collector, ns);
	}
	
	@SuppressWarnings("unchecked")
	private static void getAllChildrenByName(Element parent, String name, List<Element> collector, Namespace ns){
		List<Element> children = parent.getChildren(name, ns);
		collector.addAll(children);
	}
	
	/**
	 * Retrieving all child nodes with the given name in the given namespace.
	 * 
	 * @param parents a list of parent nodes to start with.
	 * @param name the element name.
	 * @param collector a collecting list.
	 * @param ns a specified namespace.
	 */
	public static void getAllChildrenByName(List<Element> parents, String name, List<Element> collector, Namespace ns){
		for(Element parent : parents){
			getAllChildrenByName(parent, name, collector, ns);
		}
	}
	
}
