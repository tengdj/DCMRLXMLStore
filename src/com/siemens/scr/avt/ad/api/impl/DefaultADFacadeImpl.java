package com.siemens.scr.avt.ad.api.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.iod.module.general.SOPCommonModule;
import org.hibernate.Session;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.SessionImplementor;

import com.siemens.scr.avt.ad.acquisition.AVTCollection;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.annotation.ImageAnnotationDescriptor;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.api.User;
import com.siemens.scr.avt.ad.audit.AuditTrail;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import com.siemens.scr.avt.ad.dicom.Patient;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.io.AuditTrailIO;
import com.siemens.scr.avt.ad.io.DicomIO;
import com.siemens.scr.avt.ad.query.Queries;
import com.siemens.scr.avt.ad.util.HibernateUtil;

public class DefaultADFacadeImpl implements ADFacade{
	private static Logger logger = Logger.getLogger(DefaultADFacadeImpl.class);

	
	public List<String> findAnnotations(Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findbyCriteria(dicomCriteria, annotationCriteria, "com.siemens.scr.avt.ad.annotation.ImageAnnotation.descriptor.UID", String.class);
	}

	
	public List<String> findDicomObjs(Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findbyCriteria(dicomCriteria, annotationCriteria, "com.siemens.scr.avt.ad.dicom.GeneralImage.SOPCommand.SOPInstanceUID", String.class);
	}

	
	public ImageAnnotation getAnnotation(String annotationUID) {
		return Queries.findAnnotation(annotationUID);
	}

	
	public DicomObject getDicomObject(String SOPInstanceUID) {
		return DicomIO.dumpDicom(SOPInstanceUID);
	}

	
	public List<ImageAnnotationDescriptor> listAnnotationsInSeries(String seriesInstanceUID) {
		throw new UnsupportedOperationException("not implemented for now.");
	}

	
	public boolean removeAnnotation(String annotationUID) {
		return AnnotationIO.removeAnnotation(annotationUID);
	}

	
	public List<ImageAnnotation> retrieveAnnotationsInSeries(String seriesInstanceUID) {
		throw new UnsupportedOperationException("not implemented for now.");
	}

	
	public void saveDicom(DicomObject image, User user, String comment) {
		if(image == null){
			logger.warn("DicomObject is null!");
			return;
		}
		DicomIO.saveOrUpdateDicom(image);
		
		String SOPInstanceUID = new SOPCommonModule(image).getSOPInstanceUID(); //get the uid for the page
		AuditTrail audittrail = new AuditTrail();
		audittrail.setUser(user);
		audittrail.setComment(comment);
		audittrail.setUid(SOPInstanceUID);
		AuditTrailIO.saveAuditTrail(audittrail);		
	}


	
	public boolean saveAnnotation(ImageAnnotation annotation, User user, String comment) {
		AnnotationIO.saveOrUpdateAnnotation(annotation);
		String annotationUID = annotation.getDescriptor().getUID();
		AuditTrail audittrail = new AuditTrail();
		audittrail.setUser(user);
		audittrail.setComment(comment);
		audittrail.setUid(annotationUID);
		AuditTrailIO.saveAuditTrail(audittrail);	
		
		return true;// XL: we inherit the method signature from AD Phase I. It does not seem to make sense any more.
	}
	
	
	
	public boolean saveAnnotations(List<ImageAnnotation> annotations, User user, String comment) {
		boolean flag = true;
		for(ImageAnnotation annotation : annotations){
			flag &= saveAnnotation(annotation, user, comment);
		}
		return flag;
	}


	
	public void saveDicoms(List<DicomObject> images, User user, String comment) {
		for(DicomObject dicom : images){
			saveDicom(dicom, user, comment);
		}
	}

	
	public void saveOrUpdateCollection(AVTCollection collection) {
		throw new UnsupportedOperationException("not implemented for now.");
	}

	
	public boolean updateAnnotation(ImageAnnotation annotation, User user, String comment,
			String referenceUID) {
		throw new UnsupportedOperationException("not implemented for now.");
	}

	
	public void updateDicom(DicomObject image, User user, String comment, String referenceUID) {
		throw new UnsupportedOperationException("not implemented for now.");
	}

	
	public AVTCollection retrieveCollection(String collectionUID) {
		throw new UnsupportedOperationException("not implemented for now.");
	}

	
	public List<Patient> findPatientByCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria){
		return Queries.findEntityByCriteria(dicomCriteria, annotationCriteria, Patient.class);
	}

	
	public Set<ImageAnnotation> retrieveAnnotationsOf(GeneralImage image) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		image = (GeneralImage) session.merge(image);
		Set<ImageAnnotation> result = image.getAnnotations();
		((SessionImplementor)session).initializeCollection((PersistentCollection) result, false);
		session.close();
		return result;
	}

	
	public Set<GeneralImage> retrieveImagesOf(GeneralSeries series) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		series = (GeneralSeries) session.merge(series);
		Set<GeneralImage> result = series.getImages();
		((SessionImplementor)session).initializeCollection((PersistentCollection) result, false);
		session.close();
		return result;
	}

	
	public Set<GeneralSeries> retrieveSeriesOf(GeneralStudy study) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		study = (GeneralStudy) session.merge(study);
		Set<GeneralSeries> result = study.getSeries();
		((SessionImplementor)session).initializeCollection((PersistentCollection) result, false);
		session.close();
		return result;
	}

	
	public Set<GeneralStudy> retrieveStudiesOf(Patient patient) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		patient = (Patient) session.merge(patient);
		Set<GeneralStudy> result = patient.getStudies();
		((SessionImplementor)session).initializeCollection((PersistentCollection) result, false);
		session.close();
		return result;
	}

	
	public List<DicomObject> retrieveDicomObjs(
			Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findDicomByCriteria(dicomCriteria, annotationCriteria);
	}

	
	public List<ImageAnnotation> retrieveAnnotations(
			Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findEntityByCriteria(dicomCriteria, annotationCriteria, ImageAnnotation.class);
	}

	
	public DicomObject getDicomObjectWithoutPixel(String SOPInstanceUID) {
		return DicomIO.dumpDicom(SOPInstanceUID, false);
	}

	
	public List<DicomObject> retrieveDicomObjsWithoutPixel(Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findDicomByCriteria(dicomCriteria, annotationCriteria, true);
	}

}
