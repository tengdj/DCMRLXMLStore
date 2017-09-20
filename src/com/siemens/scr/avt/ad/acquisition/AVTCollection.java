package com.siemens.scr.avt.ad.acquisition;

import java.util.HashSet;
import java.util.Set;

import com.siemens.scr.avt.ad.dicom.GeneralSeries;

public class AVTCollection {
	private Set<GeneralSeries> series = new HashSet<GeneralSeries>();;
	
	private String UID;
	
	private String name;
	
	public String getUID() {
		return UID;
	}

	public void setUID(String UID) {
		this.UID = UID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addSeries(GeneralSeries series) {
		this.getSeries().add(series);
//		series.setAVTCollection(this);
	}

	public Set<GeneralSeries> getSeries(){
		return series;
	}


	public void setSeries(Set<GeneralSeries> series) {
		this.series = series;
	}
}
