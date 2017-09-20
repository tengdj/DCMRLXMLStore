package com.siemens.scr.avt.ad.hibernate;


import java.io.Serializable;
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Types; 
import java.sql.Blob; 

import org.hibernate.Hibernate; 
import org.hibernate.HibernateException; 
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import org.apache.commons.lang.builder.HashCodeBuilder;


public class BinaryBlobType implements UserType 
{ 
  public int[] sqlTypes() 
  { 
    return new int[] { Types.BLOB }; 
  }

  @SuppressWarnings("rawtypes")
public Class returnedClass() 
  { 
    return byte[].class; 
  } 

  public boolean equals(Object x, Object y) 
  { 
    return (x == y) 
      || (x != null 
        && y != null 
        && java.util.Arrays.equals((byte[]) x, (byte[]) y)); 
  } 

  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) 
  throws HibernateException, SQLException 
  { 
    Blob blob = rs.getBlob(names[0]); 
    return blob.getBytes(1, (int) blob.length()); 
  } 

  public void nullSafeSet(PreparedStatement st, Object value, int index) 
  throws HibernateException, SQLException 
  { 
    st.setBlob(index, Hibernate.createBlob((byte[]) value)); 
  } 

  public Object deepCopy(Object value) 
  { 
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
		return (byte[]) value;
	}
	
	public int hashCode(Object x) throws HibernateException {
		return new HashCodeBuilder().append( (byte[]) x).toHashCode();
		
		
	}
	
	
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	public Object nullSafeGet(ResultSet arg0, String[] arg1,
			SessionImplementor arg2, Object arg3) throws HibernateException,
			SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2,
			SessionImplementor arg3) throws HibernateException, SQLException {
		// TODO Auto-generated method stub
		
	} 

}

