package com.siemens.scr.avt.ad.io;

import org.hibernate.Session;

import com.siemens.scr.avt.ad.api.User;
import com.siemens.scr.avt.ad.util.HibernateUtil;

public class UserIO {		
	public static void saveUser(User user) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		HibernateUtil.save(user, session);
		session.close();
	}
}
	

