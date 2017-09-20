package com.siemens.scr.avt.ad.audit;

import com.siemens.scr.avt.ad.api.User;
import java.sql.Timestamp;

public class AuditTrail {
	
	private int auditTrailId;
	private User user;
	private String uid;
	private String comment;
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
	public int getAuditTrailId() {
		return auditTrailId;
	}
	public void setAuditTrailId(int auditTrailId) {
		this.auditTrailId = auditTrailId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}
