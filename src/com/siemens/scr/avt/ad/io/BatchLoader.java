package com.siemens.scr.avt.ad.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

/**
 * A two pass batch loader loading from a given directory.
 * 
 * <ul>
 * <li>Files are filtered by suffixes.</li>
 * <li>Sub-directories are recursively processed.</li>
 * </ul>
 *  
 * @author Xiang Li
 *
 * @param <T> the object type to be loaded.
 */
public abstract class BatchLoader<T> {

	private static Logger logger = Logger.getLogger(BatchLoader.class);
	
	private static FileFilter dirFilter=new FileFilter(){
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };
    
    private static FilenameFilter dcmFilter=new FilenameFilter(){

		@Override
		public boolean accept(File dir,String fname){    
		    return (fname.toLowerCase().endsWith(".dcm"));    
		   
		  }    
    	
    };

	
	public void loadFromDirectory(File directory) throws Exception {

		File[] filelist = directory.listFiles(dcmFilter);
		//preprocess(FileUtils.iterateFiles(directory, new SuffixFileFilter(getSuffix()), TrueFileFilter.INSTANCE));
		//load(FileUtils.iterateFiles(directory, new SuffixFileFilter(getSuffix()), TrueFileFilter.INSTANCE));
		preprocess1(filelist);
		load1(filelist);
	}

	protected void load(Iterator<File> fileIterator) throws Exception {
		while(fileIterator.hasNext()){
			File file = fileIterator.next();
			logger.debug("loading file:" + file.getAbsolutePath());
//			DicomObject dicomObj = DicomParser.read(file);
			T t = readFromFile(file);
			loadSingleObject(t);
//			loadDicomObject(dicomObj);
		}
	}
	
	protected abstract String getSuffix();
	protected abstract T readFromFile(File file) throws Exception;
	protected abstract void loadSingleObject(T t);
	protected abstract void preprocessSingleObject(T t);
	
	protected void preprocess(Iterator<File> fileIterator) throws Exception {
		while(fileIterator.hasNext()){
			T t = readFromFile(fileIterator.next());
			preprocessSingleObject(t);
		}
	}
	
	protected void preprocess1(File[] filelist) throws Exception {
		if(filelist==null)return;
		for(int i = 0;i<filelist.length;i++)
		{
			T t = readFromFile(filelist[i]);
			preprocessSingleObject(t);
		}
	}
	
	protected void load1(File[] filelist) throws Exception {
		if(filelist==null)return;
		for(int i = 0;i<filelist.length;i++)
		{
			logger.debug("loading file:" + filelist[i].getAbsolutePath());
//			DicomObject dicomObj = DicomParser.read(file);
			T t = readFromFile(filelist[i]);
			loadSingleObject(t);
			//System.out.println(filelist[i]);
//			loadDicomObject(dicomObj);
		}
	}
	
}