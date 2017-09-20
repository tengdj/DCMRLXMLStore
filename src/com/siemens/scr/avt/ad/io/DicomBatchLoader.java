package com.siemens.scr.avt.ad.io;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;

import com.siemens.scr.avt.ad.util.DicomParser;


/**
 * All files in the directory with suffix ".dcm" should be valid DICOM files.
 * 
 * @author Xiang Li
 *
 */
public  class DicomBatchLoader extends BatchLoader<DicomObject> implements Runnable{
	private static Logger logger = Logger.getLogger(DicomBatchLoader.class);
	private static File curFile = null;
	private File directory;
	public String stopsign = "End of the Queue!";
	public static Queue<String> dirQueue = new LinkedList<String>();
	private int type;
	
	
	
	public DicomBatchLoader(File directory)
	{
		this.directory = directory;
		type= 0;
	}
	
	public DicomBatchLoader()
	{
		type = 1;
	}
	
	@Override
	public void run() {
		if(type ==0)
		{
			readdirPath(directory);
			synchronized(dirQueue)
			{
			dirQueue.add(stopsign);
			}
			System.out.println("finished reading directory paths.");
		}
		else if(type==1)
		{
		   String pathtmp;
		   long start = System.currentTimeMillis();
		   while(true)
		   {
              synchronized(dirQueue)
              {
                 pathtmp = dirQueue.poll();
              } 
               if(pathtmp==null)
               {
            	   try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
 					e.printStackTrace();
					continue;

				}
            	   continue;
               }
               if(pathtmp.equalsIgnoreCase(stopsign))
               {
                  synchronized(dirQueue)
                  {
                	  dirQueue.add(stopsign);
                  }
                  break;         
               }
 
		     try{
		  	     System.out.println("thread "+Thread.currentThread()+" uploading "+pathtmp);
				 loadFromDirectory(new File(pathtmp));
		     } catch (Exception e) {
			 System.out.println("load directory "+pathtmp+"  failed!");
			 e.printStackTrace();
		  }
        }
		long end = System.currentTimeMillis();
	    System.out.println("Thread "+Thread.currentThread()+" totally cost "+(end-start)+" millisecond");
	  }
		
	}
	public static void main(String[] args){
				
		
		File root = new File(args[0]);
		Thread dirpathReader = new Thread(new DicomBatchLoader(root));
		dirpathReader.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//partition 30
		//thread number = 10, 79 minute, 113922-69023, 9.47 images per second
		//partition 20
		//thread number = 1,  52 minute, 595528-585847, 3.100 images per second
		//thread number = 2,  26 minute, 717559-709841, 4.950 images per second
		//thread number = 3,  10 minute, 668998-664980, 6.697 images per second 
		//thread number = 4,  22 minute, 727231-717823, 7.127 images per second
		//thread number = 5,  49 minute, 816547-794656, 7.446 images per second
		//thread number = 7,  33 minute, 833250-816547, 8.435 images per second
		//thread number = 10, 49 minute, 630699-605630, 8.530 images per second
		//thread number = 13, 106 minute, 794056-734459, 9.37 images per second
		//thread number = 15, 112 minute, 436942-377802, 8.800 images per second
		//thread number = 17, 50 minute, 703915-673862, 10.018 images per second
		//thread number = 20, 49 minute, 662789-632946, 10.150 images per second
		//partition 10
		//thread number = 10, 70 minute, 811598-775765, 8.532 images per second
		//partition 5
		//thread number = 10, 105 minute, 416243-373862, 6.73 images per second
		//partition 1
		//thread number = 
		for(int i = 0;i<Integer.parseInt(args[1]);i++)
		{
		Thread loader = new Thread(new DicomBatchLoader());
		  try {
			  loader.start();
	     	} catch (Exception e) {
			  logger.error("Error while loading:");
			  e.printStackTrace();
		   }
		}
		
	}

	@Override
	protected void loadSingleObject(DicomObject t) {
		DicomIO.saveOrUpdateDicom(t);
	}

	@Override
	protected void preprocessSingleObject(DicomObject t) {
		// do nothing
	}
	
	@Override
	protected void preprocess(Iterator<File> fileIterator) throws Exception{
		// do nothing
	}
	@Override
	protected String getSuffix() {
		return ".dcm";
	}


	@Override
	protected DicomObject readFromFile(File file) throws IOException {
		setCurFile(file);
		return DicomParser.read(file);
	}

	public static File getCurFile() {
		return curFile;
	}

	public static void setCurFile(File curFile) {
		DicomBatchLoader.curFile = curFile;
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
			dirQueue.add(path.getAbsolutePath());
			//System.out.println(path.getAbsolutePath()+" is added!");
		}
		 
		for(int i = 0;i<subdir.length;i++)
			   {
				if(subdir[i].isDirectory())
					readdirPath(subdir[i]);
				}
		}

	
	

}
