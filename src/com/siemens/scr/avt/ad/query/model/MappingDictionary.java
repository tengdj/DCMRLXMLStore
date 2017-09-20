package com.siemens.scr.avt.ad.query.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.jdom.JDOMException;


public interface MappingDictionary {

	public abstract Set<String> keySet();

	public abstract MappingEntry get(String key);

	public abstract void put(String key, MappingEntry entry);

	public abstract void loadFromHibernateMappings(List<File> mappings)
			throws JDOMException, IOException;

	public abstract void loadFromHibernateMapping(File mappingFile)
			throws JDOMException, IOException;

	public abstract void loadFromXPathMapping(File mappingFile);
	
	

}