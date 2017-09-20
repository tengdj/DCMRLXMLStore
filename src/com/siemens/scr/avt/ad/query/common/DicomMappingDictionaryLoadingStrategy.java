/**
 * 
 */
package com.siemens.scr.avt.ad.query.common;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.type.Type;

import com.siemens.scr.avt.ad.query.model.LoadingStrategy.MappingDictionaryLoadingStrategy;

class DicomMappingDictionaryLoadingStrategy extends HibernateLoadingStrategy<DicomMappingDictionary> implements
		MappingDictionaryLoadingStrategy<DicomMappingDictionary> {
	
	public static final String DICOM_HEADER = "dicom-header";
	public static final String TAG = "tag";

	private void loadPersistentClass(DicomMappingDictionary dict, PersistentClass pc){
		Table table = pc.getTable();
		Iterator<Property> it = pc.getPropertyIterator();
		while(it.hasNext()){
			Property prop = it.next();
			loadProperty(dict, prop, table, pc.getClassName());
		}
		String tableName = table.getSchema() + "." + table.getName();
		String key = pc.getClassName();
		dict.put(key, new WildcardEntry(tableName));
	}
	
	private void loadProperty(DicomMappingDictionary dict, Property prop, Table table, String classPrefix){
		String key =  classPrefix + "." + prop.getName();
		Type type = prop.getType();
		String tableName = table.getSchema() + "." + table.getName();
		if(type.isAssociationType() || type.isCollectionType()){
			// do nothing
		}
		else if(type.isComponentType()){
			Component component = (Component) prop.getValue();
			Iterator<Property> it = component.getPropertyIterator();
			while(it.hasNext()){
				Property subProp = it.next();
				loadProperty(dict, subProp, table, component.getRoleName());
			}
		}
		else{ 
			int sqltype = sqlTypes(type);
			
			assert prop.getColumnSpan() == 1;
			Iterator<Column> it = prop.getColumnIterator();
			String column = it.next().getName();
			
			dict.addMappingEntry(key, tableName, column, sqltype);
			
			loadTag(dict, prop, key);	
			loadDicomHeaderMetadata(dict, tableName, column, prop);
		}
		
	}
	
	private void loadDicomHeaderMetadata(DicomMappingDictionary dict, String table, String column, Property prop){
		MetaAttribute isHeader = prop.getMetaAttribute(DICOM_HEADER);
		if(isHeader != null)
		if (isHeader != null && Boolean.parseBoolean(isHeader.getValue())) {
			DefaultDicomHeaderEntry.setHeaderTable(table);
			DefaultDicomHeaderEntry.setHeaderColumn(column);
		}
	}
	
	
	private void loadTag(DicomMappingDictionary dict, Property prop, String key) {
		MetaAttribute tag = prop.getMetaAttribute(TAG);

		if (tag != null) {
			dict.addTag2Key(parseHex(tag.getValue()), key);
		}
	}

	@Override
	protected void load(DicomMappingDictionary t) {
		Iterator<PersistentClass> it = getConfig().getClassMappings();
		while(it.hasNext()){
			PersistentClass pc = it.next();
			loadPersistentClass(t, pc);
		}
	}
	




	

}