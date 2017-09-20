package com.siemens.scr.avt.ad.query;

import java.util.List;
import java.util.Map;

/**
 * An interface for assemblying queries.
 * 
 * @author Xiang Li
 *
 */
public interface QueryBuilder {
	String buildQuery(Map<String, Object> params, List<String> out);
	String buildQuery(Map<Integer, Object> intKeyedParams, Map<String, Object> params, List<String> out);
}
