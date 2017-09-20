package com.siemens.scr.avt.ad.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * 
 * @author Xiang Li
 *
 */
public class StringArray2StringType implements UserType {
	public static final String DELIMITER = "\\";
	
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	
	public Serializable disassemble(Object value) throws HibernateException {
		return (String[])value;
	}

	
	public boolean equals(Object x, Object y) throws HibernateException {
		 return (x == y) 
	      || (x != null 
	        && y != null 
	        && ((String[]) x).equals((String[]) y)); 
	}

	
	public int hashCode(Object x) throws HibernateException {
		return ((String[]) x).hashCode();
	}

	
	public boolean isMutable() {
		return false;
	}

	
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		String value = rs.getString(names[0]);
		
		if(value != null && value.length() > 0){
			return value.split(DELIMITER);
		}
		    
		return null;
	}

	
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		String result = null;
		if(value != null){
			String[] values = (String[]) value;
			StringBuffer buf = new StringBuffer();
			for(String str : values){
				buf.append(str);
				buf.append(DELIMITER);
			}
			if(values.length > 0) buf.delete(buf.length() - DELIMITER.length(), buf.length());
			
			result = buf.toString();
		}
		st.setString(index, result);
	}

	
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	
	public Class returnedClass() {
		return String[].class;
	}

	
	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR }; 
	}

}
