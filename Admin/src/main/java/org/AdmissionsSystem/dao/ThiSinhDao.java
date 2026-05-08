package org.AdmissionsSystem.dao;

import org.AdmissionsSystem.models.XtThisinhxettuyen25;

public class ThiSinhDao extends AbstractCrudDao<XtThisinhxettuyen25, Integer> {

	public ThiSinhDao() {
		super(XtThisinhxettuyen25.class);
	}

	public XtThisinhxettuyen25 findByCccd(String cccd) {
		if (isBlank(cccd)) {
			return null;
		}
		try (var session = sessionFactory.openSession()) {
			return session.createQuery("FROM XtThisinhxettuyen25 WHERE lower(cccd) = :cccd", XtThisinhxettuyen25.class)
					.setParameter("cccd", cccd.toLowerCase())
					.uniqueResult();
		}
	}

	public XtThisinhxettuyen25 findBySoBaoDanh(String soBaoDanh) {
		if (isBlank(soBaoDanh)) {
			return null;
		}
		try (var session = sessionFactory.openSession()) {
			return session
					.createQuery("FROM XtThisinhxettuyen25 WHERE lower(sobaodanh) = :sbd", XtThisinhxettuyen25.class)
					.setParameter("sbd", soBaoDanh.toLowerCase())
					.uniqueResult();
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
