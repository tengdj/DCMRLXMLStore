package com.siemens.scr.avt.ad.annotation;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;



public class AnnotationAttachment {
	private Blob attachmentObject;
	
	private String name;
	
	private ImageAnnotation referencedAnnotation;
	
	public Blob getAttachmentObject() {
		return attachmentObject;
	}

	public String getName() {
		return name;
	}

	public ImageAnnotation getReferencedAnnotation() {
		return referencedAnnotation;
	}

	public void setAttachmentObject(Blob object) {
		this.attachmentObject = object;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	public void setReferencedAnnotation(ImageAnnotation annotation) {
		this.referencedAnnotation = annotation;
	}

	public InputStream getAttachmentStream() throws SQLException {
		if (getAttachmentObject() == null)
			return null;

		return getAttachmentObject().getBinaryStream();

	}
	
}
