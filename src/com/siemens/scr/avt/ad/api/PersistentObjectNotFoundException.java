package com.siemens.scr.avt.ad.api;


import org.apache.commons.lang.StringUtils;

public class PersistentObjectNotFoundException extends Exception {
	public static final String LIST_DELIMITER = ",";
	
	public PersistentObjectNotFoundException(Class<?> objType, String[] paramTypes, Object[] params ) {
		this(objType + " with parameters <" + StringUtils.join(paramTypes, LIST_DELIMITER) +">=("+ StringUtils.join(params, LIST_DELIMITER) + ") is not found!");
	}

	public PersistentObjectNotFoundException(Class<?> objType, String[] paramTypes, Object[] params, Throwable cause ){
		this(objType + " with parameters <" + StringUtils.join(paramTypes, LIST_DELIMITER) +">=("+ StringUtils.join(params, LIST_DELIMITER) + ") is not found!", cause);
	}
	
	public PersistentObjectNotFoundException(Class<?> objType, String paramType, Object param ) {
		this(objType + " with parameter " + paramType +" = "+ param + " is not found!");
	}
	
	public PersistentObjectNotFoundException(Class<?> objType, String paramType, Object param, Throwable cause ) {
		this(objType + " with parameter " + paramType +" = "+ param + " is not found!", cause);
	}
	
	public PersistentObjectNotFoundException(String message) {
		super(message);
	}

	public PersistentObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	public PersistentObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
