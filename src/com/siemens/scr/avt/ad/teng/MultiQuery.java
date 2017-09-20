package com.siemens.scr.avt.ad.teng;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class MultiQuery implements Runnable{
	
    public static Connection db2connection;
    Properties property = new Properties();
    private static String DRIVER_NAME = "com.ibm.db2.jcc.DB2Driver";
	int start,end;
	public String root;
    public MultiQuery(String URL,String username,String password,String root)
    {
    	this.root = root;
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
		
	
    }
    
    public MultiQuery(int start,int end,String root)
    {
        this.root=root;
    	this.start=start;
    	this.end=end;
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
            while ( (len = ins.read(b)) != -1) {  
              fout.write(b, 0, len);  
            }  
            fout.close();  
            ins.close();  
        }catch(Exception e){  
            e.printStackTrace();  
        }  

	}

	@Override
	public void run() {
    	String query = "select patient,study,series,image,imageobj from" +
    			"(select row_number() over(order by p.patient_id) as rn,p.patient_id as patient,s.study_instance_uid as study,se.series_instance_uid as series,i.sop_instance_uid as image,i.image_object as imageobj " +//
    			"from ad.general_image i,ad.general_series se, ad.study s,ad.patient p " +
    			"where p.patient_name='1.3.6.1.4.1.9328.50.2.0155' and p.patient_pk_id=s.patient_pk_id and s.study_pk_id=se.study_pk_id and se.general_series_pk_id=i.general_series_pk_id ) as t " +
    			"where t.rn>="+start+" and t.rn<= "+end;
    	PreparedStatement ps = this.getPstmt(query);
    	ResultSet rs=null;;
    	Long starttime = System.currentTimeMillis();
    	int count=0;
    	try {
    		rs = ps.executeQuery();
    		while(rs.next())
    		{
    			StringBuffer filepath = new StringBuffer(this.root);
    	 	    String filename="";
    	 		InputStream ins = null;
    			filepath.append(File.separator);
    			filepath.append(rs.getString(1));
    			filepath.append(File.separator);
    			filepath.append(rs.getString(2));
    			filepath.append(File.separator);
    			filepath.append(rs.getString(3));
    			filepath.append(File.separator);
    			filename = rs.getString(4)+".dcm";
    			ins = rs.getBlob(5).getBinaryStream();
    			File dir = new File(filepath.toString());
    			if(dir.exists()==false)
    				 dir.mkdirs();
    			 filepath.append(filename);
    			 //System.out.println(Thread.currentThread()+" writing file "+filepath);
    			 writeBlob(ins,filepath.toString());
    			 count++;
    	    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
 		Long endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime+" files "+count);
		
	}
	
	public static void main(String[] args)
    {
        String URL = "jdbc:db2://pais1.cci.emory.edu:50001/testdb";
        String USERNAME = "db2inst1";
        String PASSWORD = "paisdb1234";
        String root = "d:\\dicom\\";
    	int numberofWriter=14;

        String query = "select count(*) " +//
    			"from ad.general_image i,ad.general_series se, ad.study s,ad.patient p " +
    			"where p.patient_name='1.3.6.1.4.1.9328.50.2.0155' and p.patient_pk_id=s.patient_pk_id and s.study_pk_id=se.study_pk_id and se.general_series_pk_id=i.general_series_pk_id ";
    	MultiQuery db = new MultiQuery(URL,USERNAME,PASSWORD,root);
    	ResultSet rs;
    	int count = 0;

		try {
			rs = db.getPstmt(query).executeQuery();
	    	rs.next();
	    	count = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	System.out.println("totally "+count+" files waiting for extract!");
    	int gap = count/numberofWriter;
    	int start=1;
    	int end=0;
    	while(true)
	    {  
    		end=start+gap-1;
    		if(end>=count)
    			end=count;
	    	Thread thread = new Thread(new MultiQuery(start,end,root));
	    	thread.start();
	    	System.out.println("one thread is started to extract file from "+start+" to "+end+".");
	    	start=start+gap;
	    	if(start>count)
	    		break;
	    }
    	
    	System.out.println("now start write answer into file! "+numberofWriter+" writer is started!");
    	
    }

}
