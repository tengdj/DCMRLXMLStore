package com.siemens.scr.avt.ad.hibernate;

import java.io.Serializable;
import java.sql.SQLXML;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Note: hibernate's built-in tool does not support XML type yet.
 * Specifying this customized type has the effect to disable automatic
 * schema update.
 * 
 * @author Xiang Li
 *
 */
public class StringXMLType implements UserType{

	 public int[] sqlTypes() 
	  { 
	    return new int[] { Types.SQLXML }; 
	  }

	  public Class returnedClass() 
	  { 
	    return String.class; 
	  } 

	  public boolean equals(Object x, Object y) 
	  { 
	    return (x == y); 
	       
	  } 

	  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) 
	  throws HibernateException, SQLException 
	  { 
		  SQLXML xml = rs.getSQLXML(names[0]); 
			  
	    return xml.getString(); 
	  } 

	  public void nullSafeSet(PreparedStatement st, Object value, int index) 
	  throws HibernateException, SQLException 
	  { 
		  st.setString(index, (String)value);
	  } 

	  public Object deepCopy(Object value) 
	  { 
	    if (value == null) return null; 

	    return value;
	  } 

	  public boolean isMutable() 
	  { 
	    return false; 
	  }

		
		public Object assemble(Serializable cached, Object owner)
				throws HibernateException {
			return cached;
		}

		
		public Serializable disassemble(Object value) throws HibernateException {
			return (String) value;
		}
		
		
		public int hashCode(Object x) throws HibernateException {
			return new HashCodeBuilder().append( (String) x).toHashCode();
			
			
		}
		
		
		public Object replace(Object original, Object target, Object owner)
				throws HibernateException {
			return original;
		} 

}
