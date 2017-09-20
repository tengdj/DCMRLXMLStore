package com.siemens.scr.avt.ad.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.util.StringUtils;
import org.hibernate.Hibernate;

/**
 * DICOM related utilities.
 * 
 * @author Xiang Li
 *
 */
public class DicomUtils {
	
	 public static final int ERROR  = -1;
		
	 public static int tagToInt(String tag){
		try {
            int i = (int) Long.parseLong(tag, 16);
            return i;
        } catch (NumberFormatException e) {
        	return ERROR;
        }
	}
	
	 public static String tagToString(int tag){
		StringBuffer buf = new StringBuffer(8);
		StringUtils.shortToHex(tag >> 16, buf);
		StringUtils.shortToHex(tag, buf);
		return buf.toString();
	}
	
	public static InputStream dicomObjectToInputStream(DicomObject dcmObject) throws IOException{
		return dcmToInputStreamByByteBuffer(dcmObject);
	}
	
	public static InputStream dcmToInputStreamByByteBuffer(DicomObject dcmObj) throws IOException{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		DicomOutputStream dos = new DicomOutputStream(buffer);
		dos.writeDicomFile(dcmObj);
		dos.close();
		ByteArrayInputStream bin = new ByteArrayInputStream(buffer.toByteArray());
		
		return bin;
	}
	
	public static InputStream dcmToInputStreamByPipe(final DicomObject dcmObj) throws IOException{
		  PipedInputStream in = new PipedInputStream();
		  final PipedOutputStream out = new PipedOutputStream(in);
		  new Thread(
		    new Runnable(){
		      public void run(){
		    	  try {
		    		DicomOutputStream dos = new DicomOutputStream(out);
					dos.writeDicomFile(dcmObj);
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("Error while converting dicom object to stream!", e);
				}
		      }
		    }
		  ).start();
		  return in;
	}
	
	static InputStream dcmToInputStreamByCircularBuffer(DicomObject dcmObj) {
		// XL: no open source library available, except for a GPL licensed one at ostermiller.org
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static  java.sql.Blob readBlobFromFile(File file)
	{
	      int flength = (int) file.length();
	      FileInputStream fis=null;
          byte b[]=new byte[flength];
	      int itotal=0;
      try{
          fis = new FileInputStream(file);
	      for (int i = 0; itotal < flength; itotal=i+itotal) {
	            i=fis.read(b,itotal,flength-itotal);
	         }
	      fis.close();
	      } catch (FileNotFoundException e1) {
	         e1.printStackTrace();
	         return null;
	      } catch (IOException e) {
             e.printStackTrace();
             return null;
	      }
      
        return Hibernate.createBlob(b);
	}
}
