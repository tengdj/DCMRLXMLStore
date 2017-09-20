package com.siemens.scr.avt.ad.query.common;


import com.siemens.scr.avt.ad.query.QueryBuilder;
import com.siemens.scr.avt.ad.query.model.MappingEntry;

/**
 * Never can be used to build selection.
 * 
 * @author Xiang Li
 *
 */
public class WildcardEntry extends MappingEntry {
	public static final String WILDCARD = "*";
	
	public WildcardEntry(String tableName){
		super(tableName, WILDCARD);
	}
	
	@Override
	public int getType() {
		return java.sql.Types.OTHER;
	}


	@Override
	public String buildSelectionCondition(QueryBuilder builder, Object value) {
		throw new UnsupportedOperationException("Wildcard cannot be used for selection!");
	}

	@Override
	public String toProjectionExpression(QueryBuilder builder) {
		return this.getTableName() + "." + this.getColumnName();
	}

	
}
