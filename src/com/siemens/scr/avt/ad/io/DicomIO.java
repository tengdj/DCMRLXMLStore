package com.siemens.scr.avt.ad.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.iod.module.composite.GeneralSeriesModule;
import org.dcm4che2.iod.module.composite.GeneralStudyModule;
import org.dcm4che2.iod.module.composite.PatientModule;
import org.dcm4che2.iod.module.general.SOPCommonModule;
import org.hibernate.Session;

import com.siemens.scr.avt.ad.dicom.DicomFactory;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import com.siemens.scr.avt.ad.dicom.Patient;
import com.siemens.scr.avt.ad.query.Queries;
import com.siemens.scr.avt.ad.util.DicomParser;
import com.siemens.scr.avt.ad.util.HibernateUtil;

public class DicomIO {
	private static Logger logger = Logger.getLogger(DicomIO.class);
	
	private static final byte NONE = 0x00;
	private static final byte IMAGE = 0x01;
	private static final byte SERIES = 0x02;
	private static final byte STUDY = 0x04;
	private static final byte PATIENT = 0x08;
	
	public static int saveOrUpdateDicomFromFile(Class<?> clazz, String filePath){
		return saveOrUpdateDicomFromStream(clazz.getResourceAsStream(filePath));
	}
	
	public static int saveOrUpdateDicomFromStream(InputStream in){
		DicomObject dicomObj = DicomParser.read(in);
		return saveOrUpdateDicom(dicomObj);
	}
	
	/**
	 * Saves a DICOM object with duplication detection.  
	 * 
	 * 
	 * @param dicomObj a DICOM object
	 * @return the PK_ID of the saved entity. Depending on the update level,
	 *  it can be PK_ID of a patient, a study, a series or an image. 
	 */
	public static int saveOrUpdateDicom(DicomObject dicomObj){
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		Object[] updateParams = testForUpdateLevel(dicomObj, session); 
		logger.debug("Level Testing passed");
		Byte updateLevel = (Byte) updateParams[0];
		Object parent = updateParams[1];	
		
		int id = updateAtLevelSavingOnParent(updateLevel, parent, dicomObj, session);
		
		session.close();
		
		return id;
	}
	
	public static boolean removeDicom(String SOPInstanceUID){
		Session session = HibernateUtil.getSessionFactory().openSession();
		GeneralImage image = Queries.findImage(SOPInstanceUID, session);
		if(image != null){
			session.delete(image);
		}
		session.close();
		return false;
	}
	

	/**
	 * Retrieve a DICOM object with the given SOPInstanceUID including pixel data.
	 * 
	 * @param SOPInstanceUID a UID uniquely identifying a SOPInstance.
	 * @return a DICOM object representing the SOPInstance including pixel data.
	 */
	public static DicomObject dumpDicom(String SOPInstanceUID){
		return dumpDicom(SOPInstanceUID, true);
	}
	
	/**
	 * Retrieve a DICOM object with the given SOPInstanceUID from the backend DB.
	 * 
	 * @param SOPInstanceUID a UID uniquely identifying a SOPInstance.
	 * @param includingPixel indicating whether the dumped DICOM object should include pixel data.
	 * @return a DICOM object representing the SOPInstance.
	 */
	public static DicomObject dumpDicom(String SOPInstanceUID, boolean includingPixel){
		Session session = HibernateUtil.getSessionFactory().openSession();
		GeneralImage image = Queries.findImage(SOPInstanceUID, session);

		DicomObject dcmObj = null;
		if(image != null){
			if(includingPixel){
				try {
					image.setPixelToDicomObject();
				} catch (SQLException e) {
					logger.error("Could not retrieve pixel data!");
					e.printStackTrace();
				}
			}
			dcmObj = new BasicDicomObject();
			image.getDicomObject().copyTo(dcmObj);
			GeneralSeries series = image.getSeries();
			series.getDicomObject().copyTo(dcmObj);
			GeneralStudy study = series.getStudy();
			study.getDicomObject().copyTo(dcmObj);
			Patient patient = study.getPatient();
			patient.getDicomObject().copyTo(dcmObj);
		}
		
		session.close();
		return dcmObj;
	}
	
	public static void dumpDicom2File(String SOPInstanceUID, String fileName) throws IOException{
		DicomObject dicomObj = dumpDicom(SOPInstanceUID);
		DicomOutputStream dout = new DicomOutputStream(new FileOutputStream(fileName));
		dout.writeDicomFile(dicomObj);
		dout.close();
	}
	
	
	private static Object[] testForUpdateLevel(DicomObject dcm, Session session){
		Byte updateLevel;
		Object parent = null;
		
		String SOPInstanceUID = new SOPCommonModule(dcm).getSOPInstanceUID();
		String seriesInstanceUID = new GeneralSeriesModule(dcm).getSeriesInstanceUID();
		String studyInstanceUID = new GeneralStudyModule(dcm).getStudyInstanceUID();
		String patientID = new PatientModule(dcm).getPatientID();
		Date patientBirthDate = new PatientModule(dcm).getPatientBirthDate();
		
		
		
		if((parent = Queries.findImage(SOPInstanceUID, session)) != null){
			updateLevel = NONE;
			System.out.println(DicomBatchLoader.getCurFile()+" none level");
		}
		else if((parent = Queries.findSeries(seriesInstanceUID, session)) != null){
			updateLevel = IMAGE;
			System.out.println(DicomBatchLoader.getCurFile()+" image level   ");
		}
		else if((parent = Queries.findStudy(studyInstanceUID, session)) != null){
			updateLevel = SERIES;
			System.out.println("series level");
		}
		else if((parent = Queries.findPatient(patientID, patientBirthDate, session)) != null){
			updateLevel = STUDY;
			System.out.println("study level");
		}
		else{
			updateLevel = PATIENT;
			System.out.println("patient level, parent is null");
		}
		
		return new Object[] {updateLevel, parent};
	}
	
	
	private static int updateAtLevelSavingOnParent(byte updateLevel, Object parent, DicomObject dcm, Session session){
		
		switch(updateLevel){
		case STUDY :
			Patient patient = (Patient) parent;
			GeneralStudy newStudy = DicomFactory.getInstance().createStudy(dcm); 
			patient.addStudy(newStudy);
			HibernateUtil.saveOrUpdate(patient, session);
			return newStudy.getPK_ID();
		case SERIES :
			GeneralStudy study = (GeneralStudy) parent;
			GeneralSeries newSeries = DicomFactory.getInstance().createSeries(dcm); 
//			newSeries.setStudy(study);
			study.addSeries(newSeries);
			HibernateUtil.saveOrUpdate(study, session);
			return newSeries.getPK_ID();
		case IMAGE :
			GeneralSeries series = (GeneralSeries) parent;
			GeneralImage image = DicomFactory.getInstance().createImage(dcm);
//			image.setSeries(series);
			series.addImage(image);
			HibernateUtil.saveOrUpdate(series, session);
			return image.getPK_ID();
		default: case PATIENT:
			Patient newPatient = DicomFactory.getInstance().createPatient(dcm);
			HibernateUtil.save(newPatient, session);
			return newPatient.getPK_ID();
		case NONE:
			return -1;
		}
		
	}
	
//	private static int updateAtLevel(byte updateLevel, Object parent, DicomObject dcm, Session session){
//		
//		switch(updateLevel){
//		case STUDY :
//			Patient patient = (Patient) parent;
//			GeneralStudy newStudy = DicomFactory.getInstance().createStudy(dcm); 
//			newStudy.setPatient(patient);
//			HibernateUtil.save(newStudy, session);
//			return newStudy.getPK_ID();
//		case SERIES :
//			GeneralStudy study = (GeneralStudy) parent;
//			GeneralSeries newSeries = DicomFactory.getInstance().createSeries(dcm); 
//			newSeries.setStudy(study);
//			HibernateUtil.save(newSeries, session);
//			return newSeries.getPK_ID();
//		case IMAGE :
//			GeneralSeries series = (GeneralSeries) parent;
//			GeneralImage image = DicomFactory.getInstance().createImage(dcm);
//			image.setSeries(series);
//			HibernateUtil.save(image, session);
//			return image.getPK_ID();
//		default: case PATIENT:
//			Patient newPatient = DicomFactory.getInstance().createPatient(dcm);
//			HibernateUtil.save(newPatient, session);
//			return newPatient.getPK_ID();
//		case NONE:
//			return -1;
//		}
//		
//	}
}
