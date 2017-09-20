package com.siemens.scr.avt.ad.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerConfigurationException;


import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.siemens.scr.avt.ad.io.DicomBatchLoader;
import com.siemens.scr.avt.ad.teng.Dcm2Other;


/**
 * Yet another XML format for the header.
 * 
 * @author Xiang Li
 *
 */
public class DicomHeader2XML_WU{
	/**
	 * 
	 * @param dcmObj a DICOM object
	 * @return a string representation of the header in XML, null if error occurs.
	 */
	public String convertToXML(DicomObject dcmObj){
		
		String xml = null;
		try {
			InputStream in;
			
			in = DicomUtils.dicomObjectToInputStream(dcmObj);
			//System.out.print("Header stream: "); 
			//System.out.println(in);
			//NativeModelRunner runner = new NativeModelRunner(in);
			//xml = runner.makeXMLNativeModel();
			xml=new Dcm2Other().dcm2Xml(DicomBatchLoader.getCurFile());
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return xml;
	}
	public static void main(String[] args) throws IOException, DicomException{
		AttributeList list = new AttributeList();
		list.read(new File("/home/db2inst1/Dropbox/LoadData/TOOLS/Data/dicom/000001.dcm"));
		list.write(new File("/home/db2inst1/terry.xml"));
		
		DicomObject dcm = DicomParser.read(new File("/home/db2inst1/Dropbox/LoadData/TOOLS/Data/dicom/000001.dcm"));
		System.out.println(dcm.getString(Tag.PatientName));
		
		//System.out.println(new DicomHeader2XML_WU().convertToXML(dcm));
	}
	

}
