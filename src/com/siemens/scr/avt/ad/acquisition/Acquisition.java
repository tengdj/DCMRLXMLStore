package com.siemens.scr.avt.ad.acquisition;

import java.util.Set;

import com.siemens.scr.avt.ad.dicom.GeneralSeries;

public class Acquisition {
	private Set<AVTCollection> referencingCollections;
	
	private GeneralSeries series;

	private int acquisitionNumber;
	
	public int getAcquisitionNumber() {
		return acquisitionNumber;
	}

	public void setAcquisitionNumber(int acquisitionNumber) {
		this.acquisitionNumber = acquisitionNumber;
	}

	public Set<AVTCollection> getReferencingCollections() {
		return referencingCollections;
	}

	public void setReferencingCollections(Set<AVTCollection> referencingCollections) {
		this.referencingCollections = referencingCollections;
	}

	public GeneralSeries getSeries() {
		return series;
	}

	public void setSeries(GeneralSeries series) {
		this.series = series;
	}


	
	
}


