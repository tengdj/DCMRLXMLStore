package com.siemens.scr.avt.ad.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;


public class ResourceLocator {
	
	public static File createFile(Class<?> clazz, String filePath) throws FileNotFoundException{
		File file;
		try {
			file = new File(clazz.getResource(filePath).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new FileNotFoundException(filePath);
		}
		return file;	
	}
	
	
	public static String getStringFromFile(Class<?> clazz, String fileName) throws IOException{
		return getStringFromFile(createFile(clazz, fileName));
	}
	
	public static String getStringFromStream(InputStream in) throws IOException{
		StringBuffer buf = new StringBuffer();
		
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));
        String str;
        while ((str = bin.readLine()) != null) {
            buf.append(str);
        }
        bin.close();
    

        return buf.toString();
	}
	
	public static String getStringFromFile(File file) throws IOException{
		return getStringFromStream(new FileInputStream(file));
	}
	
	public static byte[] getBytesFromFile(Class<?> clazz, String fileName) throws IOException {
		return getBytesFromFile(createFile(clazz, fileName));
	}
	
	public static InputStream getResourceAsStream(Class<?> clazz, String file){
		return clazz.getResourceAsStream(file);
	}
	
	// Returns the contents of the file in a byte array.
    public static byte[] getBytesFromFile(File file) throws IOException {
    	
    	InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        return getBytesFromStream(is, length);
    }


	public static byte[] getBytesFromStream(InputStream is, long length) throws IOException {
		// Get the size of the file
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read data!");
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
	}
}
