package com.siemens.scr.avt.ad.annotation;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public class ImageAnnotationDescriptor{
	private String AIMVersion;
	
	private String authorName;
	
	private String comment;
	
	private Date dateTime;
	
	private int ID;
	
	private String imageAnnotationType;
	
	private String name;
	
	private String seriesInstanceUID;
	
	private String UID;
	
	private boolean groundTruth;

	private String studyInstanceUID;
	
	public String getAIMVersion() {
		return AIMVersion;
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getComment() {
		return comment;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public int getID() {
		return ID;
	}

	public String getImageAnnotationType() {
		return imageAnnotationType;
	}

	public String getName() {
		return name;
	}

	public String getSeriesInstanceUID() {
		return seriesInstanceUID;
	}

	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}

	public String getUID() {
		return UID;
	}

	public boolean isGroundTruth() {
		return groundTruth;
	}

	public void setAIMVersion(String aimVersion) {
		this.AIMVersion = aimVersion;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public void setGroundTruth(boolean groundTruth) {
		this.groundTruth = groundTruth;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public void setImageAnnotationType(String imageAnnotationType) {
		this.imageAnnotationType = imageAnnotationType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSeriesInstanceUID(String seriesInstanceUID) {
		this.seriesInstanceUID = seriesInstanceUID;
	}

	public void setStudyInstanceUID(String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
	}

	public void setUID(String UID) {
		this.UID = UID;
	}

	@Override
	public boolean equals(Object object){
		return EqualsBuilder.reflectionEquals(this, object);
	}
	
	@Override
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public String toString(){
		  return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
