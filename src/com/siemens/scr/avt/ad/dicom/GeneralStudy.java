package com.siemens.scr.avt.ad.dicom;

import java.util.HashSet;
import java.util.Set;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.iod.module.composite.GeneralStudyModule;

public class GeneralStudy extends GeneralStudyModule{
	private Patient patient;
	
	private int PK_ID;

	private Set series  = new HashSet();;
	
	GeneralStudy() {
		super(new BasicDicomObject());
	}
	
	public GeneralStudy(DicomObject dcmobj) {
		super(dcmobj);
	}
	
	public void addSeries(GeneralSeries series){
		series.setStudy(this);
		this.series.add(series);
	}

	public Patient getPatient(){
		return patient;
	}

	public int getPK_ID() {
		return PK_ID;
	}
	
	public Set getSeries() {
		return series;
	}
	
	
	public void setPatient(Patient patient){
		this.patient = patient;
	}
	
	public void setSeries(Set series) {
		this.series = series;
	}
	
}
