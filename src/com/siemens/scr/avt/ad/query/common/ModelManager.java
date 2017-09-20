package com.siemens.scr.avt.ad.query.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.JDOMException;

import com.siemens.scr.avt.ad.query.model.JoinTree;
import com.siemens.scr.avt.ad.query.model.MappingEntry;
import com.siemens.scr.avt.ad.util.DicomUtils;

public class ModelManager {

	private JoinTree<?> joinTree;

	private DicomMappingDictionary dictionary;
	

	public ModelManager() {
		this.dictionary = new DicomMappingDictionary();
		this.joinTree = new NaturalJoinTree();
	}

	public ModelManager(JoinTree<?> joinTree, DicomMappingDictionary dictionary,
			List<File> mappings, File XPathMappingFile) throws JDOMException,
			IOException {
		this.dictionary = dictionary;
		this.joinTree = joinTree;
		this.loadModelFromFiles(mappings, XPathMappingFile);
	}

	public static String getReferencedClassFullName(String packageName,
			String className) {
		if (!(className.indexOf('.') == -1)) {
			return className;
		} else {
			return packageName + "." + className;
		}
	}

//	public static boolean isSimple(MappingEntry entry) {
//		return entry.getType() != java.sql.Types.SQLXML && entry.getType() != java.sql.Types.OTHER;
//	}

	public void loadModelFromFiles(List<File> mappings, File XPathMappingFile)
			throws JDOMException, IOException {
		dictionary.loadFromHibernateMappings(mappings);
		joinTree.loadFromHibernateMappings(mappings);
		dictionary.loadFromXPathMapping(XPathMappingFile);
	}
	
	public void loadDefault(File XPathMappingFile){
		dictionary.loadDefault();
		joinTree.loadDefault();
		dictionary.loadFromXPathMapping(XPathMappingFile);
	}

	public List<String> buildJoinConditions(Set<String> tableSet) {
		return joinTree.buildJoinConditions(tableSet);
	}

	public MappingEntry getMappingEntry(String key) {
		MappingEntry entry = dictionary.get(key);
		if(entry == null){
			int tag = DicomUtils.tagToInt(key);
			if(tag != DicomUtils.ERROR){
				return DefaultDicomHeaderEntry.createEntry(tag);
			}
		}
		return entry;
	}

	public void expandTableSet(Set<String> tableSet) {
		joinTree.expandTableSet(tableSet);
	}
	
	public void expandTableSetToAllTables(Set<String> tables){
		tables.addAll(joinTree.getTables());	
	}
	
	public Map<String, Object> tag2Key(Map<Integer, Object> arguments){
		HashMap<String, Object> result = new HashMap<String, Object>();
		for(Integer i : arguments.keySet()){
			String key = dictionary.getKey(i);
			Object value = arguments.get(i);
			if(key != null){
				result.put(key, value);	
			}
			else{
				result.put(DicomUtils.tagToString(i), value);
			}
			
		}
		return result;
	}
	
	
	
	public String printToString(){
		return dictionary.toString();
	}
}
