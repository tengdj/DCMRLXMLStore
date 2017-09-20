package com.siemens.scr.avt.ad.query.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.siemens.scr.avt.ad.query.QueryBuilder;
import com.siemens.scr.avt.ad.query.model.MappingEntry;

/**
 * Build conjunctive queries without self-joins. An assumption employed is we
 * are performing natural joins.
 * 
 * @author Xiang Li
 * 
 */
public abstract class AbstractQueryBuilder implements QueryBuilder {
	public static final String AND = "AND";

	public static final String FROM = "FROM";

	private static Logger logger = Logger.getLogger(AbstractQueryBuilder.class);

	public static final String NEWLINE = "\r\n";

	public static final String SELECT = "SELECT";

	public static final String SEPARATOR = ",";

	public static final String SPACE = " ";

	public static final String WHERE = "WHERE";

	private static String conjunctionOf(List<String> conditions) {
		return StringUtils.join(conditions, SPACE + AND + SPACE);
	}

	private ModelManager modelManager;

	private Map<String, Object> params;

	private List<String> projectionList = new LinkedList<String>();


	private Set<String> tableSet = new HashSet<String>();

	protected String buildAtomicSelection(String key, Object value) {
		com.siemens.scr.avt.ad.query.model.MappingEntry entry = getModelManager()
				.getMappingEntry(key);
		
		return entry.buildSelectionCondition(this, value);
	}

	/**
	 * The impl. does not allow self join
	 */
	protected String buildCartesionProduct() {
		return FROM + SPACE + StringUtils.join(tableSet, SEPARATOR);
	}
	
	public String buildFullDicomQuery(Map<Integer, Object> intKeyedParams, Map<String, Object> params){
		ModelManager manager = getModelManager();
		Map<String, Object> allParams = new HashMap<String, Object>();
		allParams.putAll(manager.tag2Key(intKeyedParams));
		if(params != null){
			allParams.putAll(params);			
		}

		return buildFullDicomQuery(allParams);
	}

	
	public String buildFullDicomQuery(Map<String, Object> params){
		// set up
		this.params = params;
		projectionList.add("*");
		modelManager.expandTableSetToAllTables(tableSet);
		
		return buildQueryInternal();
	}
	
	protected String buildJoin() {
		return conjunctionOf(getModelManager().buildJoinConditions(tableSet));
	}
	
	protected String buildProjection() {
		return SELECT + SPACE + StringUtils.join(projectionList, SEPARATOR);
	}
	
	public String buildQuery(Map<Integer, Object> intKeyedParams, Map<String, Object> params, List<String> out){
		ModelManager manager = getModelManager();
		Map<String, Object> allParams = new HashMap<String, Object>();
		allParams.putAll(manager.tag2Key(intKeyedParams));
		if(params != null){
			allParams.putAll(params);			
		}

		return buildQuery(allParams, out);
	}

	public final String buildQuery(Map<String, Object> params, List<String> out) {
		preprocess(out, params);

		return buildQueryInternal();
	}
	

	private String buildQueryInternal(){
		String query = buildProjection() + SPACE + NEWLINE 
		+ buildCartesionProduct() + SPACE + NEWLINE + buildWhereClause();
		
		return query;
	}

	protected String buildSelection() {
		ArrayList<String> conditions = new ArrayList<String>();
		for (String key : params.keySet()) {
			conditions.add(buildAtomicSelection(key, params.get(key)));
		}
		return conjunctionOf(conditions);

	}

	public abstract String buildAtomicSelection(Object value,
			SimpleEntry entry);
	
	public abstract String buildAtomicSelection(Object value,
			XPathEntry entry);
	
	protected String buildWhereClause() {
		ArrayList<String> components = new ArrayList<String>();
		String joinCondition = buildJoin();
		if(joinCondition != null && joinCondition.length() > 0){
			components.add(joinCondition);	
		}
		
		String selection = buildSelection();
		if(selection != null && selection.length() > 0){
			components.add(selection);	
		}
		
		String condition = conjunctionOf(components);
		if(condition == null || condition.length() == 0){
			return StringUtils.EMPTY;
		}
		
		return WHERE + SPACE + condition;
	}



	public ModelManager getModelManager() {
		return modelManager;
	}

	protected void preprocess(List<String> output, Map<String, Object> allParams){
		this.params = allParams;
		setUpProjectionList(output);
		setUpTableSet(output);
	}

	public void setModelManager(ModelManager modelManager) {
		this.modelManager = modelManager;
	}

	private void setUpProjectionList(List<String> output) {
		// TODO: extract XML fragments?
		for (String key : output) {
			MappingEntry entry = getModelManager().getMappingEntry(key);
			projectionList.add(entry.toProjectionExpression(this));
		}
	}

	private void setUpTableSet(List<String> out) {
		for (String key : params.keySet()) {
			MappingEntry entry = getModelManager().getMappingEntry(key);
			if (entry == null) {
				logger.error("Unknown Search Key:" + key + "!");
			} else {
				tableSet.add(entry.getTableName());
			}
		}
		for(String key : out){
			MappingEntry entry = getModelManager().getMappingEntry(key);
			if (entry == null) {
				logger.error("Unknown Search Key:" + key + "!");
			} else {
				tableSet.add(entry.getTableName());
			}
		}

		modelManager.expandTableSet(tableSet);
	}

}
