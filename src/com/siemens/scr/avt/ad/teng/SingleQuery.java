package com.siemens.scr.avt.ad.teng;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class SingleQuery implements Runnable{
	
    public static Connection db2connection;
    public static String root = "f:\\dicom";
    public static int failed=0;
    Properties property = new Properties();
    private static String DRIVER_NAME = "com.ibm.db2.jcc.DB2Driver";
    static ResultSet rs;
    public SingleQuery(String URL,String username,String password)
    {
    	try {
		Class.forName(DRIVER_NAME).newInstance();
	    } catch (InstantiationException | IllegalAccessException
			| ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	property.put("user",username);
	property.put("password", password);
	try {
		db2connection = DriverManager.getConnection(URL, property);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	
	System.out.println("connect successfully!");
	
    }
    
    public SingleQuery() {
		// TODO Auto-generated constructor stub
	}

	public PreparedStatement getPstmt(String sql)
    {
    	try {
    		return db2connection.prepareStatement(sql);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    
    
    
    public static void writeBlob(InputStream ins, String filepath)
	{
        OutputStream fout = null;  
        try{  
            File file = new File(filepath);  
            fout = new FileOutputStream(file);  
            byte[] b = new byte[1024];  
            int len = 0;  
            if(ins==null)
            {
            	System.out.println(filepath+" cannot write correctly");
            	return;
            }
            while ( (len = ins.read(b)) != -1) {  
              fout.write(b, 0, len);  
            }  
            fout.close();  
            ins.close();  
        }catch(Exception e){  
        	System.out.println(filepath+" cannot write correctly");
        	new File(filepath).delete();
        	SingleQuery.failed++;
        	e.printStackTrace();
        	return;
        }  

	}

	@Override
	public void run() {
    	Long starttime = System.currentTimeMillis();
    	int count=0;
    	while(true)
    	{
    		StringBuffer filepath = new StringBuffer(SingleQuery.root);
	 	    String filename="";
	 		InputStream ins = null;
    		synchronized(rs)
    		{
    			try {
					if(rs!=null&&rs.isClosed()==false&&rs.next())
					{
					filepath.append(File.separator);
					filepath.append(rs.getString(1));
					filepath.append(File.separator);
					filepath.append(rs.getString(2));
					filepath.append(File.separator);
					filepath.append(rs.getString(3));
					filepath.append(File.separator);
					filename = rs.getString(4)+".dcm";
					ins = rs.getBlob(5).getBinaryStream();
					}
					else
						break;
				} catch (SQLException e) {
					e.printStackTrace();
					break;
				}
    		}
    		File dir = new File(filepath.toString());
    		if(dir.exists()==false)
    			 dir.mkdirs();
    		filepath.append(filename);
    		//System.out.println(Thread.currentThread()+" writing file "+filepath);
    		writeBlob(ins,filepath.toString());
    		count++;
    	  }

 		Long endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime+" files "+count);
		
	}
	
	public static void main(String[] args)
    {
		
        String URL = "jdbc:db2://pais1.cci.emory.edu:50001/testdb";
        String USERNAME = "db2inst1";
        String PASSWORD = "paisdb1234";
        String query = "select p.patient_id as patient,s.study_instance_uid as study,se.series_instance_uid as series,i.sop_instance_uid as image,i.image_object as imageobj " +//
    			"from ad.general_image i,ad.general_series se, ad.study s,ad.patient p " +
    			"where p.patient_name='1.3.6.1.4.1.9328.50.2.0155' and p.patient_pk_id=s.patient_pk_id and s.study_pk_id=se.study_pk_id and se.general_series_pk_id=i.general_series_pk_id ";
    	SingleQuery db = new SingleQuery(URL,USERNAME,PASSWORD);
    	SingleQuery.root="d:\\dicom";
		try {
			SingleQuery.rs = db.getPstmt(query).executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

    	int numberofWriter=7;
    	for(int i=0;i<numberofWriter;i++)
	    {  
	    	Thread thread = new Thread(new SingleQuery());
	    	thread.start();
	    }
    	
    	
    	System.out.println("now start write answer into file! "+numberofWriter+" writer is started!");
    	
    }

}
