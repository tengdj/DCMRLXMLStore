package com.siemens.scr.avt.ad.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdom.JDOMException;


import com.siemens.scr.avt.ad.annotation.AnnotationAttachment;
import com.siemens.scr.avt.ad.annotation.AnnotationFactory;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.query.Queries;
import com.siemens.scr.avt.ad.util.HibernateUtil;

public class AnnotationIO {
	private static AnnotationFactory factory = new AnnotationFactory();
	
	public static AnnotationFactory getFactory(){
		return factory;
	}
	
	public static ImageAnnotation loadAnnotationFromFile(File aimFile) throws IOException, JDOMException {
		FileInputStream fin = new FileInputStream(aimFile);
		try{
			return loadAnnotationFromStream(fin);	
		}
		finally{
			fin.close();
		}
		
	}
	
	public static ImageAnnotation loadAnnotationFromStream(InputStream in) throws IOException, JDOMException {
		return AnnotationFactory.createAnnotationFromStream(in);
	}

	public static void saveOrUpdateAnnotationFromStream(InputStream in) throws IOException, JDOMException{
		ImageAnnotation annotation = loadAnnotationFromStream(in);
		saveOrUpdateAnnotation(annotation);
	}
	
	public static void saveOrUpdateAnnotationFromFile(File file) throws IOException, JDOMException{
		InputStream fin = new FileInputStream(file);
		try{
			saveOrUpdateAnnotationFromStream(fin);
		}
		finally{
			fin.close();
		}
	}
	
	public static boolean removeAnnotation(String annotationUID){
		Session session = HibernateUtil.getSessionFactory().openSession();
		ImageAnnotation annotation = Queries.findAnnotation(annotationUID, session);
		if(annotation != null){
			session.delete(annotation);
		}
		session.close();
		return false;
	}
	
	public static void saveOrUpdateAnnotation(ImageAnnotation annotation) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		saveOrUpdateAnnotation(annotation, session);
		session.close();
	}
	
	public static void saveOrUpdateAnnotation(ImageAnnotation annotation, org.hibernate.Session session) {
		try {
			List<String> refs = factory.parseReferencedImageUIDsFromString(annotation.getAIM());
			for(String imageUID : refs){
				GeneralImage image = Queries.findImage(imageUID, session);
				annotation.getReferencedImages().add(image);
				image.getAnnotations().add(annotation);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		HibernateUtil.saveOrUpdate(annotation, session);
	}
	
	/**
	 * A shortcut method to load an annotation with a given attachment.
	 * 
	 * @param aimFile the AIM annotation file
	 * @param attachmentFile an attachment file
	 * @return a <code>ImageAnnotation</code>
	 * @throws IOException
	 * @throws JDOMException when errors occur when parsing the annotation file
	 */
	public static ImageAnnotation loadAnnotationWithAttachment(File aimFile, File attachmentFile) throws IOException, JDOMException {
		
		ImageAnnotation annotation = loadAnnotationFromFile(aimFile);
	
		AnnotationAttachment attachment = factory.createAttachmentFromFile(attachmentFile);
		
		annotation.addAttachment(attachment);
		
		
		
		return annotation;
	}
}
