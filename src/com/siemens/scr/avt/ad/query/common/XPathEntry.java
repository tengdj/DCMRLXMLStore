package com.siemens.scr.avt.ad.query.common;

import java.sql.Types;

import com.siemens.scr.avt.ad.query.QueryBuilder;
import com.siemens.scr.avt.ad.query.model.MappingEntry;

/**
 * A mapping entry encapsulating all XPath related information.
 * 
 * @author Xiang Li
 *
 */
public class XPathEntry extends MappingEntry{
	private String path;

	private String nodeTestStep;


	XPathEntry(String table, String column, String path, String nodeTestStep){
		super(table, column);
		this.path = path;
		this.nodeTestStep = nodeTestStep;
	}
	

	@Override
	public int getType() {
		return Types.SQLXML;
	}


	public String getPath() {
		return path;
	}


	public String getNodeTestStep() {
		return nodeTestStep;
	}



	@Override
	public String buildSelectionCondition(QueryBuilder builder, Object value) {
		return ((AbstractQueryBuilder)builder).buildAtomicSelection(value, this);
	}


	@Override
	public String toProjectionExpression(QueryBuilder builder) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}
}
