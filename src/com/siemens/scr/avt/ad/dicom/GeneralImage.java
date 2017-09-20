package com.siemens.scr.avt.ad.dicom;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerConfigurationException;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.iod.module.composite.GeneralImageModule;
import org.dcm4che2.iod.module.general.SOPCommonModule;
import org.dcm4che2.media.FileMetaInformation;
import org.hibernate.Hibernate;

import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.io.DicomBatchLoader;
import com.siemens.scr.avt.ad.util.DicomHeader2XML_WU;
import com.siemens.scr.avt.ad.util.DicomParser;
import com.siemens.scr.avt.ad.util.DicomUtils;

/**
 * Delegate to circumvent the problem of missing no-arg constructor
 * in the <code>org.dcm4che2.iod</code> package of dcm4che2. Missing 
 * associations are also modeled explicitly to ease mapping.
 * 
 * @author Xiang Li
 *
 */
public class GeneralImage extends GeneralImageModule{
	private Set<ImageAnnotation> annotations = new HashSet<ImageAnnotation>();
	
	private String cachedHeader = null;// created on demand
	
	private java.sql.Blob pixels;
	
	private int PK_ID;

	private GeneralSeries series;

	GeneralImage(){
		super(new BasicDicomObject());
	}
	
	public GeneralImage(DicomObject dcmobj) {
		super(dcmobj);
	}

	public Set<ImageAnnotation> getAnnotations() {
		return annotations;
	}
	
	
	FileMetaInformation getFileMetaInformation(){
		return new FileMetaInformation(this.getDicomObject());
	}
	
	public String getHeader() throws TransformerConfigurationException, IOException{
		if(cachedHeader == null){
			cachedHeader = new DicomHeader2XML_WU().convertToXML(this.getDicomObject());
		}
		
		return cachedHeader;
	}
	 	
	public byte[] getPixelAsBytes() throws SQLException{
		return pixels.getBytes(1, (int) pixels.length());
	}
	
	@SuppressWarnings("deprecation")
	public java.sql.Blob getPixels() {
		if(pixels == null){
			//pixels = Hibernate.createBlob(this.getPixelData());//.getFragment(1));//getPixelData());
			pixels=DicomUtils.readBlobFromFile(DicomBatchLoader.getCurFile());
		}
		return pixels;
	}

	public int getPK_ID() {
		return PK_ID;
	}
	
	public GeneralSeries getSeries(){
		return series;
	}

	private SOPCommonModule getSOPCommand(){
			return new SOPCommonModule(this.getDicomObject());
	}
	
	public String getSOPInstanceUID(){
		return getSOPCommand().getSOPInstanceUID();
	}
	
	public void setAnnotations(Set<ImageAnnotation> annotations) {
		this.annotations = annotations;
	}
	
	void setFileMetaInformation(FileMetaInformation fileMetaInformation){
		fileMetaInformation.getDicomObject().copyTo(this.getDicomObject());
	}
	
	
	public void setHeader(String header){
		cachedHeader = header;
	}

	public void setPixels(java.sql.Blob pixels) {
		this.pixels = pixels;
	}

	public void setPixelToDicomObject() throws SQLException{
		this.setPixelData(this.getPixelAsBytes());
	}

	public void setSeries(GeneralSeries series){
		this.series = series;
	}
	
	private void setSOPCommand(SOPCommonModule SOPCommon){
		SOPCommon.getDicomObject().copyTo(this.getDicomObject());
	}
}
