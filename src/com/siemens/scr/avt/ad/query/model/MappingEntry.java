/**
 * 
 */
package com.siemens.scr.avt.ad.query.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.siemens.scr.avt.ad.query.QueryBuilder;




public abstract class MappingEntry{
	private String tableName;
	private String columnName;
	
	public String toString(){
		return getTableName() + "." + getColumnName();
	}
	
	public MappingEntry(String table, String column){
		tableName = table;
		columnName = column;
	}
	
	/**
	 * The codes are defined by <code>java.sql.Types</code>.
	 * 
	 * @return type of the entry according to <code>java.sql.Types</code>.
	 */
	public abstract int getType();

	public abstract String buildSelectionCondition(QueryBuilder builder, Object value);
	
	public abstract String toProjectionExpression(QueryBuilder builder);
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName() {
		return columnName;
	}
	
	public boolean equals(Object obj){
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this);
	}
}