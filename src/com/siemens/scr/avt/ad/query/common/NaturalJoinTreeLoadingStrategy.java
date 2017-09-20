/**
 * 
 */
package com.siemens.scr.avt.ad.query.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;

import com.siemens.scr.avt.ad.query.model.LoadingStrategy.JoinTreeLoadingStrategy;

class NaturalJoinTreeLoadingStrategy extends HibernateLoadingStrategy<NaturalJoinTree> implements JoinTreeLoadingStrategy<NaturalJoinTree> {

	@Override
	protected void load(NaturalJoinTree tree) {
		getConfig().buildMappings();
		Set<ForeignKey> fks = new HashSet<ForeignKey>();
		Iterator<Table> it = getConfig().getTableMappings();
		while(it.hasNext()){
			Table t = it.next();
			Iterator<ForeignKey> fkIt = t.getForeignKeyIterator();
			while(fkIt.hasNext()){
				fks.add(fkIt.next());
			}
		}
		processFKs(tree, fks);
	}

	private void processFKs(NaturalJoinTree tree, Set<ForeignKey> fks) {
		for(ForeignKey fk : fks){
			assert fk.getColumnSpan() == 1;// we do not allow composite FK for now
			String columnName = fk.getColumn(0).getName();
			Table contextTable = fk.getTable();
			String table = tableNameFromTable(contextTable);
			String entityName = fk.getReferencedEntityName();
			PersistentClass referencedPC = getConfig().getClassMapping(entityName);
			String referencedTable = tableNameFromPersistentClass(referencedPC);
			tree.handleFK(table, referencedTable, columnName);
		}
		
		
	}

	
	
}