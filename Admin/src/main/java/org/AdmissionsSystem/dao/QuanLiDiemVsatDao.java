package org.AdmissionsSystem.dao;

import org.AdmissionsSystem.models.XtDiemVsat;

public class QuanLiDiemVsatDao extends AbstractCrudDao<XtDiemVsat, Integer> {

	public QuanLiDiemVsatDao() {
		super(XtDiemVsat.class);
	}

	public XtDiemVsat findByCccdAndDotThi(String cccd, String dotThi) {
		if (isBlank(cccd) || isBlank(dotThi)) {
			return null;
		}
		try (var session = sessionFactory.openSession()) {
			return session.createQuery(
					"FROM XtDiemVsat WHERE lower(cccd) = :cccd AND lower(dotThi) = :dotThi",
					XtDiemVsat.class)
					.setParameter("cccd", cccd.toLowerCase())
					.setParameter("dotThi", dotThi.toLowerCase())
					.uniqueResult();
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
