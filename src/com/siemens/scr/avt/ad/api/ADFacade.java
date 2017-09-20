package com.siemens.scr.avt.ad.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dcm4che2.data.DicomObject;

import com.siemens.scr.avt.ad.acquisition.AVTCollection;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.annotation.ImageAnnotationDescriptor;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import com.siemens.scr.avt.ad.dicom.Patient;

/**
 * This class is a facade accumulating methods considered useful for end users.
 * 
 * @author Xiang Li
 *
 */
public interface ADFacade {
	public List<ImageAnnotationDescriptor> listAnnotationsInSeries(String seriesInstanceUID);
	public List<ImageAnnotation> retrieveAnnotationsInSeries(String seriesInstanceUID);
	public ImageAnnotation getAnnotation(String annotationUID);
	public boolean saveAnnotation(ImageAnnotation annotation, User user, String comment);
	public boolean saveAnnotations(List<ImageAnnotation> annotations, User user, String comment); 
	public boolean removeAnnotation(String annotationUID);
	public List<String> findAnnotations(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria);  
	public List<ImageAnnotation> retrieveAnnotations(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria);
	
	public AVTCollection retrieveCollection(String collectionUID);
	public void saveOrUpdateCollection(AVTCollection collection);
	
	public void saveDicom(DicomObject image, User user, String comment);
	public void saveDicoms(List<DicomObject> images, User user, String comment);
	public DicomObject getDicomObject(String SOPInstanceUID);
	public DicomObject getDicomObjectWithoutPixel(String SOPInstanceUID);
	public List<String> findDicomObjs(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria);
	public List<DicomObject> retrieveDicomObjs(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria);
	public List<DicomObject> retrieveDicomObjsWithoutPixel(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria);
	
	public void updateDicom(DicomObject image, User user, String comment, String referenceUID);
	public boolean updateAnnotation(ImageAnnotation annotation, User user, String comment, String referenceUID);
	
	public List<Patient> findPatientByCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria);
	public Set<GeneralStudy> retrieveStudiesOf(Patient patient);
	public Set<GeneralSeries> retrieveSeriesOf(GeneralStudy study);
	public Set<GeneralImage> retrieveImagesOf(GeneralSeries series);
	public Set<ImageAnnotation> retrieveAnnotationsOf(GeneralImage image);
}
