package com.siemens.scr.avt.ad.teng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class Test {

	public static void main(String[] args)
	{
		System.out.println(System.getProperty("user.dir")+"\\src\\dicom_dict.txt");
		try {
    		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+"\\src\\dicom_dict.txt")));
    		String line=null;
    		while((line = reader.readLine())!=null)
    		{
    			System.out.println(line.split(" ")[0]+line.split(" ")[1]);
    	    }
    		reader.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
