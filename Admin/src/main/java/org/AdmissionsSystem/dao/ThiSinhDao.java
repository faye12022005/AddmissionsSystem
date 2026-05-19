package org.AdmissionsSystem.dao;

import org.AdmissionsSystem.models.XtThisinhxettuyen25;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;

public class ThiSinhDao extends AbstractCrudDao<XtThisinhxettuyen25, Integer> {

    public ThiSinhDao() {
        super(XtThisinhxettuyen25.class);
    }

    public XtThisinhxettuyen25 findByCccd(String cccd) {
        if (isBlank(cccd)) {
            return null;
        }
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtThisinhxettuyen25 WHERE lower(cccd) = :cccd", XtThisinhxettuyen25.class)
                    .setParameter("cccd", cccd.toLowerCase())
                    .uniqueResult();
        }
    }

    public XtThisinhxettuyen25 findBySoBaoDanh(String soBaoDanh) {
        if (isBlank(soBaoDanh)) {
            return null;
        }
        try (Session session = getSessionFactory().openSession()) {
            return session
                    .createQuery("FROM XtThisinhxettuyen25 WHERE lower(sobaodanh) = :sbd", XtThisinhxettuyen25.class)
                    .setParameter("sbd", soBaoDanh.toLowerCase())
                    .uniqueResult();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public List<XtThisinhxettuyen25> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtThisinhxettuyen25 WHERE lower(cccd) LIKE :q OR lower(ho) LIKE :q OR lower(ten) LIKE :q OR lower(sobaodanh) LIKE :q",
                    XtThisinhxettuyen25.class)
                    .setParameter("q", q)
                    .setFetchSize(1000)
                    .setReadOnly(true)
                    .list();
        }
    }

    public List<XtThisinhxettuyen25> findAllPaginated(int page, int pageSize) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtThisinhxettuyen25 ORDER BY idthisinh", XtThisinhxettuyen25.class)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .setFetchSize(pageSize)
                    .setReadOnly(true)
                    .list();
        }
    }

    public List<XtThisinhxettuyen25> searchPaginated(String keyword, int page, int pageSize) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtThisinhxettuyen25 WHERE lower(cccd) LIKE :q OR lower(ho) LIKE :q OR lower(ten) LIKE :q OR lower(sobaodanh) LIKE :q ORDER BY idthisinh",
                    XtThisinhxettuyen25.class)
                    .setParameter("q", q)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .setFetchSize(pageSize)
                    .setReadOnly(true)
                    .list();
        }
    }

    public long countByKeyword(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            if (keyword == null || keyword.trim().isEmpty()) {
                return count();
            }
            String q = "%" + keyword.trim().toLowerCase() + "%";
            Long total = session.createQuery(
                    "SELECT COUNT(*) FROM XtThisinhxettuyen25 WHERE lower(cccd) LIKE :q OR lower(ho) LIKE :q OR lower(ten) LIKE :q OR lower(sobaodanh) LIKE :q",
                    Long.class)
                    .setParameter("q", q)
                    .uniqueResult();
            return total == null ? 0L : total;
        }
    }

    public Map<String, Long> countByDoiTuong(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String hql = "SELECT coalesce(doiTuong, ''), count(*) FROM XtThisinhxettuyen25";
            boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
            if (hasKeyword) {
                hql += " WHERE lower(cccd) LIKE :q OR lower(ho) LIKE :q OR lower(ten) LIKE :q OR lower(sobaodanh) LIKE :q";
            }
            hql += " GROUP BY coalesce(doiTuong, '') ORDER BY count(*) DESC";

            var query = session.createQuery(hql, Object[].class);
            if (hasKeyword) {
                String q = "%" + keyword.trim().toLowerCase() + "%";
                query.setParameter("q", q);
            }

            List<Object[]> rows = query.list();
            Map<String, Long> result = new LinkedHashMap<>();
            for (Object[] row : rows) {
                String key = row[0] == null ? "" : row[0].toString().trim();
                if (key.isEmpty()) {
                    key = "(Chưa khai báo)";
                }
                Long value = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;
                result.put(key, value);
            }
            return result;
        }
    }

    public Map<String, Long> countByKhuVuc(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String hql = "SELECT coalesce(khuVuc, ''), count(*) FROM XtThisinhxettuyen25";
            boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
            if (hasKeyword) {
                hql += " WHERE lower(cccd) LIKE :q OR lower(ho) LIKE :q OR lower(ten) LIKE :q OR lower(sobaodanh) LIKE :q";
            }
            hql += " GROUP BY coalesce(khuVuc, '') ORDER BY count(*) DESC";

            var query = session.createQuery(hql, Object[].class);
            if (hasKeyword) {
                String q = "%" + keyword.trim().toLowerCase() + "%";
                query.setParameter("q", q);
            }

            List<Object[]> rows = query.list();
            Map<String, Long> result = new LinkedHashMap<>();
            for (Object[] row : rows) {
                String key = row[0] == null ? "" : row[0].toString().trim();
                if (key.isEmpty()) {
                    key = "(Chưa khai báo)";
                }
                Long value = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;
                result.put(key, value);
            }
            return result;
        }
    }

    public int getNextId() {
        try (Session session = getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT max(idthisinh) FROM XtThisinhxettuyen25", Integer.class)
                    .uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        }
    }
}
