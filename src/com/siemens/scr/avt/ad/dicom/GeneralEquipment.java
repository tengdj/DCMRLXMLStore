package com.siemens.scr.avt.ad.dicom;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.iod.module.composite.GeneralEquipmentModule;

public class GeneralEquipment extends GeneralEquipmentModule{
	GeneralEquipment() {
		super(new BasicDicomObject());
	}
	
	GeneralEquipment(DicomObject dcmobj) {
		super(dcmobj);
	}

}
