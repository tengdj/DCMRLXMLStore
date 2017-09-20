package com.siemens.scr.avt.ad.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;

/**
 * A parser translating DICOM files into <code>DicomObject</code>
 * 
 * @author Xiang Li
 *
 */
public class DicomParser {
	public static DicomObject read(Class<?> clazz, String fileName){
		return read(clazz.getResourceAsStream(fileName));
	}
	
	public static DicomObject read(File file) throws IOException{
		FileInputStream fin = null;
		try{
			 fin = new FileInputStream(file);
			return read(fin);
		}
		finally{
			if(fin !=  null){
				fin.close();	
			}
			
		}
	}
	
	public static DicomObject read(InputStream input){
		DicomObject dcmObj;
		DicomInputStream din = null;
		try {
		    din = new DicomInputStream(input);
		    dcmObj = din.readDicomObject();
		}
		catch (IOException e) {
		    e.printStackTrace();
		    return null;
		}
		finally {
		    try {
		    	if (din!=null)
		    		din.close();
		    }
		    catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		//System.out.println("successfully read object");
		return dcmObj;
	}
	

}
