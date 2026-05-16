package org.AdmissionsSystem.dao;

import org.AdmissionsSystem.models.XtDiemVsat;
import org.hibernate.Session;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class QuanLiDiemVsatDao extends AbstractCrudDao<XtDiemVsat, Integer> {

	public QuanLiDiemVsatDao() {
		super(XtDiemVsat.class);
	}

	public XtDiemVsat findByCccdAndDotThi(String cccd, String dotThi) {
		if (isBlank(cccd) || isBlank(dotThi)) {
			return null;
		}
		try (Session session = getSessionFactory().openSession()) {
			return session.createQuery(
					"FROM XtDiemVsat WHERE lower(cccd) = :cccd AND lower(dotThi) = :dotThi",
					XtDiemVsat.class)
					.setParameter("cccd", cccd.toLowerCase())
					.setParameter("dotThi", dotThi.toLowerCase())
					.uniqueResult();
		}
	}

	public List<BigDecimal> fetchScores(String property) {
		if (isBlank(property)) {
			return List.of();
		}
		try (Session session = getSessionFactory().openSession()) {
			String hql = "SELECT e." + property
					+ " FROM XtDiemVsat e WHERE e." + property + " IS NOT NULL";
			return session.createQuery(hql, BigDecimal.class).list();
		}
	}

	public List<XtDiemVsat> findPage(String searchText, List<String> cccdMatches, int page, int pageSize) {
		try (Session session = getSessionFactory().openSession()) {
			StringBuilder hql = new StringBuilder("FROM XtDiemVsat WHERE 1=1");
			boolean hasSearch = !isBlank(searchText);
			boolean hasCccdMatches = cccdMatches != null && !cccdMatches.isEmpty();

			if (hasSearch) {
				hql.append(" AND (lower(cccd) LIKE :q OR lower(dotThi) LIKE :q");
				if (hasCccdMatches) {
					hql.append(" OR lower(cccd) IN (:cccds)");
				}
				hql.append(")");
			}

			hql.append(" ORDER BY idVsat DESC");

			var query = session.createQuery(hql.toString(), XtDiemVsat.class);
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

	public long countFiltered(String searchText, List<String> cccdMatches) {
		try (Session session = getSessionFactory().openSession()) {
			StringBuilder hql = new StringBuilder("SELECT COUNT(*) FROM XtDiemVsat WHERE 1=1");
			boolean hasSearch = !isBlank(searchText);
			boolean hasCccdMatches = cccdMatches != null && !cccdMatches.isEmpty();

			if (hasSearch) {
				hql.append(" AND (lower(cccd) LIKE :q OR lower(dotThi) LIKE :q");
				if (hasCccdMatches) {
					hql.append(" OR lower(cccd) IN (:cccds)");
				}
				hql.append(")");
			}

			var query = session.createQuery(hql.toString(), Long.class);
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
