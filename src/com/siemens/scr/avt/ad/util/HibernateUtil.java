package com.siemens.scr.avt.ad.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;


/**
 * Hibernate utilities.
 * 
 * <p>
 * I first try to load a file named connection.properties from current context directory. If not found, the root directory ("/") is searched.
 * Finally, I try to load the connection properties from the system properties.
 * </p>
 * 
 * @author Xiang Li
 *
 */
@SuppressWarnings("deprecation")
public class HibernateUtil {
	private static Logger logger = Logger.getLogger(HibernateUtil.class);
	private static Configuration config = new Configuration();
	private static SessionFactory sessionFactory;
	public static final String CONNECTION_PROP_FILE = "connection.properties";
	public static final String URL = "hibernate.connection.url";
	public static final String USER = "hibernate.connection.username";
	public static final String PWD = "hibernate.connection.password";
	public static final String[] CONNECTION_KEYS = new String[]{URL, USER, PWD};
	
	static{
		try{
			Properties connectionProp = loadConnectionProperties();
			
			config.configure();
			config.addProperties(connectionProp);
			sessionFactory = config.buildSessionFactory();
		}
		catch(Throwable t){
			throw new ExceptionInInitializerError(t);
		}
	}
	
	private  static Properties loadConnectionProperties() {
		Properties connectionProp = new Properties();
		InputStream inStream = null;
		try{
			inStream = new FileInputStream(CONNECTION_PROP_FILE);	// current directory
		}
		catch (IOException e){
			if(inStream == null){
				inStream = HibernateUtil.class.getResourceAsStream("/" + CONNECTION_PROP_FILE);
			}
		}
		finally{
			if(inStream != null){
				try {
					connectionProp.load(inStream);
					inStream.close();	
				} catch (IOException e) {
					e.printStackTrace();
				}
					
			}
			else{
				logger.debug("try loading from system properties");
				for (String key : CONNECTION_KEYS){
					String value = System.getProperty(key);
					if(value != null){
						connectionProp.put(key, value);
					}
				}
			}
		}
		
		return connectionProp;
	}
	
	
	public static Configuration getConfiguration(){
		return config;
	}
	
	public static SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
	public static void shutdown(){
		getSessionFactory().close();
	}
	
	
	public static Serializable save(Object obj){
		Session session = getSessionFactory().openSession();
		Serializable id = save(obj, session);
		session.close();
		
		return id;
	}
	
	public static void saveOrUpdate(Object obj, Session session){
		try{
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(obj);
			tx.commit();
		}
		catch(HibernateException e){
			Throwable t = e.getCause();
			if(t instanceof SQLException){
				SQLException exp = (SQLException)t;
				while(exp != null){
					exp.printStackTrace();
					exp = exp.getNextException();
				}
			}
		}
	}
	
	 public static Serializable save(Object obj, Session session){
		Transaction tx = session.beginTransaction();
		Serializable id = session.save(obj);
		tx.commit();
		
		return id;
	}
	
	public static Object load(Class<?> clazz, Serializable id){
		Session session = getSessionFactory().openSession();

		Object obj = load(clazz, id, session);
		
		session.close();
		
		return obj;
	}

	public static Object load(Class<?> clazz, Serializable id, Session session){

		Transaction tx = session.beginTransaction();
		
		Object obj = session.get(clazz, id);
		
		tx.commit();

		
		return obj;
	}
	
	public static void update(Object obj, Session session) {
		Transaction tx = session.beginTransaction();
		session.update(obj);
		tx.commit();
	}
	
	public static void delete(String id){
		Session session = getSessionFactory().openSession();
		
		session.delete(id);
		
		session.close();
	}
	
	public static void delete(Object obj, Session session){
		Transaction tx = session.beginTransaction();
		session.delete(obj);
		tx.commit();
	}

	public static void main(String[] args){
		Configuration cfg = new Configuration().configure();
		SchemaExport schemaExport = new SchemaExport(cfg);
		schemaExport.setDelimiter(";");
		schemaExport.setFormat(true);
		schemaExport.setOutputFile("createSchema.sql");
		schemaExport.create(true, false);
	}
	
	
}
