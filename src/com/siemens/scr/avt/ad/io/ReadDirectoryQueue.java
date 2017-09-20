package com.siemens.scr.avt.ad.io;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class ReadDirectoryQueue {
	 public static Queue<String> dirQueue = new LinkedList<String>();

	public static void main(String[] args)
	{
		String path = "F:\\dicommini";
	    new ReadDirectoryQueue().readdirPath(new File(path));
	    Iterator<String> i = ReadDirectoryQueue.dirQueue.iterator();
	    int count = 0;
	    while(i.hasNext())
	    {
	    	System.out.println(i.next());
	    	count++;
	    }
	    
	    System.out.println(count);
	    
	    
	    
	}
	
	public void readdirPath(File path)
	{
		if(path.isFile())return;
		File[] subdir = path.listFiles();
		boolean hasdcm = false;
		for(int i = 0;i<subdir.length;i++)
		{
			if(subdir[i].isFile()&&subdir[i].getName().endsWith(".dcm"))
			{
				hasdcm = true;
				break;
			}
			
		}
		if(hasdcm==true)
		{
			ReadDirectoryQueue.dirQueue.add(path.getAbsolutePath());
		}
		 
		for(int i = 0;i<subdir.length;i++)
			   {
				if(subdir[i].isDirectory())
					readdirPath(subdir[i]);
				}
		}
		
}
