package org.AdmissionsSystem.dao;

import org.AdmissionsSystem.models.XtDiemthixettuyen;

public class QuanLiDiemDao extends AbstractCrudDao<XtDiemthixettuyen, Integer> {

	public QuanLiDiemDao() {
		super(XtDiemthixettuyen.class);
	}

	public XtDiemthixettuyen findByCccdAndPhuongThuc(String cccd, String phuongThuc) {
		if (isBlank(cccd) || isBlank(phuongThuc)) {
			return null;
		}
		try (var session = sessionFactory.openSession()) {
			return session.createQuery(
					"FROM XtDiemthixettuyen WHERE lower(cccd) = :cccd AND lower(dPhuongthuc) = :pt",
					XtDiemthixettuyen.class)
					.setParameter("cccd", cccd.toLowerCase())
					.setParameter("pt", phuongThuc.toLowerCase())
					.uniqueResult();
		}
	}

	public XtDiemthixettuyen findByCccd(String cccd) {
		if (isBlank(cccd)) {
			return null;
		}
		try (var session = sessionFactory.openSession()) {
			return session.createQuery("FROM XtDiemthixettuyen WHERE lower(cccd) = :cccd", XtDiemthixettuyen.class)
					.setParameter("cccd", cccd.toLowerCase())
					.uniqueResult();
		}
	}

	public XtDiemthixettuyen findBySoBaoDanh(String soBaoDanh) {
		if (isBlank(soBaoDanh)) {
			return null;
		}
		try (var session = sessionFactory.openSession()) {
			return session.createQuery("FROM XtDiemthixettuyen WHERE lower(sobaodanh) = :sbd",
					XtDiemthixettuyen.class)
					.setParameter("sbd", soBaoDanh.toLowerCase())
					.uniqueResult();
		}
	}

	public XtDiemthixettuyen findBySoBaoDanhAndPhuongThuc(String soBaoDanh, String phuongThuc) {
		if (isBlank(soBaoDanh) || isBlank(phuongThuc)) {
			return null;
		}
		try (var session = sessionFactory.openSession()) {
			return session.createQuery(
					"FROM XtDiemthixettuyen WHERE lower(sobaodanh) = :sbd AND lower(dPhuongthuc) = :pt",
					XtDiemthixettuyen.class)
					.setParameter("sbd", soBaoDanh.toLowerCase())
					.setParameter("pt", phuongThuc.toLowerCase())
					.uniqueResult();
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
