package com.siemens.scr.avt.ad.io;

import org.hibernate.Session;

import com.siemens.scr.avt.ad.audit.AuditTrail;
import com.siemens.scr.avt.ad.util.HibernateUtil;



public class AuditTrailIO {
	
	public static void saveAuditTrail(AuditTrail audittrail) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		HibernateUtil.save(audittrail, session);
		session.close();
	}
}

