package com.siemens.scr.avt.ad.query.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.siemens.scr.avt.ad.query.model.LoadingStrategy;
import com.siemens.scr.avt.ad.query.model.MappingDictionary;
import com.siemens.scr.avt.ad.query.model.MappingEntry;


/**
 * This is the default mapping dictionary, which stores the mapping from string typed keys to 
 * <code>MappingEntry</code> and a mapping from DICOM tags to the keys.
 * 
 * @author Xiang Li
 *
 */
public class DicomMappingDictionary implements MappingDictionary {
	private static Logger logger = Logger.getLogger(DicomMappingDictionary.class);
	
	private Map<String, MappingEntry> dictionary = new HashMap<String, MappingEntry>();

	private Map<Integer, String> tag2Key = new HashMap<Integer, String>();
	
	private LoadingStrategy<DicomMappingDictionary> loadingStrategy;

	public Set<String> keySet() {
		return dictionary.keySet();
	}

	public MappingEntry get(String key) {
		return dictionary.get(key);
	}

	public void put(String key, MappingEntry entry) {
		dictionary.put(key, entry);
	}

	


	public void loadFromHibernateMapping(InputStream mappingFile)
			throws JDOMException, IOException {
		getLoadingStrategy().loadFromInputStream(this, mappingFile);
	}
	
	



	public void loadFromHibernateMappings(List<File> files)
			throws JDOMException, IOException {
		getLoadingStrategy().loadFromFiles(this, files);
	}

	
	public void loadFromXPathMapping(File mappingFile) {
		// TODO
	}


	public void loadFromHibernateMapping(File mappingFile)
			throws JDOMException, IOException {
		getLoadingStrategy().loadFromFile(this, mappingFile);
	}


	LoadingStrategy<DicomMappingDictionary> getLoadingStrategy(){
		if(loadingStrategy == null){
			loadingStrategy =  new DicomMappingDictionaryLoadingStrategy();
		}
		return loadingStrategy;
	}

	void addMappingEntry(String key, String table, String column,
			int type) {
		dictionary.put(key, new SimpleEntry(table, column, type));
	}

	
	
	Map<String, MappingEntry> getDict() {
		return dictionary;
	}
	
	public String getKey(Integer tag){
		String key = tag2Key.get(tag);
		if(key == null || key.length() == 0){
			logger.error("unknown tag: " + tag);
			return null;
		}
		return key;
	}
	
	public MappingEntry get(Integer tag){
		String key = getKey(tag);
		return get(key);
	}
	
	void addTag2Key(Integer tag, String key){
		tag2Key.put(tag, key);
	}

	public void loadDefault() {
		getLoadingStrategy().loadDefault(this);
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for(String key : dictionary.keySet()){
			builder.append("Key " + key + " mapped to " + dictionary.get(key));
			builder.append("\r\n");
		}
		
		for(Integer tag : tag2Key.keySet()){
			builder.append("Tag " + tag + " mapped to " + tag2Key.get(tag));
			builder.append("\r\n");
		}
		return builder.toString();
	}
}
