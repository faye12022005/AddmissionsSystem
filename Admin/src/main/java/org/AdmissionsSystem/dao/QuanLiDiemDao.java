package org.AdmissionsSystem.dao;

import org.AdmissionsSystem.models.XtDiemthixettuyen;
import org.hibernate.Session;
import java.util.ArrayList;
import java.util.List;

public class QuanLiDiemDao extends AbstractCrudDao<XtDiemthixettuyen, Integer> {

	public QuanLiDiemDao() {
		super(XtDiemthixettuyen.class);
	}

	public XtDiemthixettuyen findByCccdAndPhuongThuc(String cccd, String phuongThuc) {
		if (isBlank(cccd) || isBlank(phuongThuc)) {
			return null;
		}
		try (Session session = getSessionFactory().openSession()) {
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
		try (Session session = getSessionFactory().openSession()) {
			return session.createQuery("FROM XtDiemthixettuyen WHERE lower(cccd) = :cccd", XtDiemthixettuyen.class)
					.setParameter("cccd", cccd.toLowerCase())
					.uniqueResult();
		}
	}

	public XtDiemthixettuyen findBySoBaoDanh(String soBaoDanh) {
		if (isBlank(soBaoDanh)) {
			return null;
		}
		try (Session session = getSessionFactory().openSession()) {
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
		try (Session session = getSessionFactory().openSession()) {
			return session.createQuery(
					"FROM XtDiemthixettuyen WHERE lower(sobaodanh) = :sbd AND lower(dPhuongthuc) = :pt",
					XtDiemthixettuyen.class)
					.setParameter("sbd", soBaoDanh.toLowerCase())
					.setParameter("pt", phuongThuc.toLowerCase())
					.uniqueResult();
		}
	}

		public List<XtDiemthixettuyen> findPage(String searchText, String phuongThucCode, List<String> cccdMatches,
				int page, int pageSize) {
			try (Session session = getSessionFactory().openSession()) {
				StringBuilder hql = new StringBuilder("FROM XtDiemthixettuyen WHERE 1=1");
				boolean hasSearch = !isBlank(searchText);
				boolean hasCccdMatches = cccdMatches != null && !cccdMatches.isEmpty();

				if (!isBlank(phuongThucCode)) {
					hql.append(" AND lower(dPhuongthuc) = :pt");
				}
				if (hasSearch) {
					hql.append(" AND (lower(cccd) LIKE :q OR lower(sobaodanh) LIKE :q");
					if (hasCccdMatches) {
						hql.append(" OR lower(cccd) IN (:cccds)");
					}
					hql.append(")");
				}

				hql.append(" ORDER BY iddiemthi DESC");

				var query = session.createQuery(hql.toString(), XtDiemthixettuyen.class);
				if (!isBlank(phuongThucCode)) {
					query.setParameter("pt", phuongThucCode.toLowerCase());
				}
				if (hasSearch) {
					query.setParameter("q", buildSearchToken(searchText));
					if (hasCccdMatches) {
						query.setParameterList("cccds", toLowerList(cccdMatches));
					}
				}

				return query
						.setFirstResult(Math.max(0, (page - 1) * pageSize))
						.setMaxResults(pageSize)
						.list();
			}
		}

		public long countFiltered(String searchText, String phuongThucCode, List<String> cccdMatches) {
			try (Session session = getSessionFactory().openSession()) {
				StringBuilder hql = new StringBuilder("SELECT COUNT(*) FROM XtDiemthixettuyen WHERE 1=1");
				boolean hasSearch = !isBlank(searchText);
				boolean hasCccdMatches = cccdMatches != null && !cccdMatches.isEmpty();

				if (!isBlank(phuongThucCode)) {
					hql.append(" AND lower(dPhuongthuc) = :pt");
				}
				if (hasSearch) {
					hql.append(" AND (lower(cccd) LIKE :q OR lower(sobaodanh) LIKE :q");
					if (hasCccdMatches) {
						hql.append(" OR lower(cccd) IN (:cccds)");
					}
					hql.append(")");
				}

				var query = session.createQuery(hql.toString(), Long.class);
				if (!isBlank(phuongThucCode)) {
					query.setParameter("pt", phuongThucCode.toLowerCase());
				}
				if (hasSearch) {
					query.setParameter("q", buildSearchToken(searchText));
					if (hasCccdMatches) {
						query.setParameterList("cccds", toLowerList(cccdMatches));
					}
				}

				Long total = query.uniqueResult();
				return total == null ? 0L : total;
			}
		}

		private List<String> toLowerList(List<String> values) {
			List<String> lowered = new ArrayList<>();
			for (String value : values) {
				if (!isBlank(value)) {
					lowered.add(value.trim().toLowerCase());
				}
			}
			return lowered;
		}

		private String buildSearchToken(String searchText) {
			String token = searchText == null ? "" : searchText.trim().toLowerCase();
			return "%" + token + "%";
		}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
