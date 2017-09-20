package com.siemens.scr.avt.ad.dicom;


import java.util.HashSet;
import java.util.Set;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.iod.module.composite.PatientModule;

public class Patient extends PatientModule{
	private int PK_ID;
	
	private Set studies = new HashSet();


	Patient(){
		super(new BasicDicomObject());
	}


	public Patient(DicomObject dob) {
		super(dob);
	}

	public void addStudy(GeneralStudy study){
		study.setPatient(this);
		studies.add(study);
	}
	
	
	public int getPK_ID() {
		return PK_ID;
	}

	public Set getStudies() {
		return studies;
	}

	
	public void setStudies(Set studies) {
		this.studies = studies;
	}
	
	
}
