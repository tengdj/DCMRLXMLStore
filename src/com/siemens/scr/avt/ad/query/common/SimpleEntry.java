package com.siemens.scr.avt.ad.query.common;


import com.siemens.scr.avt.ad.query.QueryBuilder;
import com.siemens.scr.avt.ad.query.model.MappingEntry;

/**
 * Simple entries are of primitive types.
 * 
 * @author Xiang Li
 *
 */
public class SimpleEntry extends MappingEntry {
	private int type;
	
	public SimpleEntry(String tableName, String columnName, int type){
		super(tableName, columnName);
		this.type = type;
	}

	@Override
	public int getType() {
		return type;
	}


	@Override
	public String buildSelectionCondition(QueryBuilder builder, Object value) {
		return ((AbstractQueryBuilder)builder).buildAtomicSelection(value, this);
	}

	@Override
	public String toProjectionExpression(QueryBuilder builder) {
		return this.getTableName() + "." + this.getColumnName();
	}

}
