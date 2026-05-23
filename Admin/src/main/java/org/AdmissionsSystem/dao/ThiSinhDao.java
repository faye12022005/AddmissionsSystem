package org.AdmissionsSystem.dao;

import org.AdmissionsSystem.models.XtThisinhxettuyen25;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.Session;

public class ThiSinhDao extends AbstractCrudDao<XtThisinhxettuyen25, Integer> {
    private static final Pattern FIRST_NUMBER_PATTERN = Pattern.compile("(\\d+)");

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
            Map<Integer, Long> groupedByNumber = new TreeMap<>();
            long unknownCount = 0L;

            for (Object[] row : rows) {
                String raw = row[0] == null ? "" : row[0].toString().trim();
                Long value = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;
                if (value == null || value <= 0) {
                    continue;
                }

                Integer doiTuongSo = extractFirstInteger(raw);
                if (doiTuongSo != null) {
                    groupedByNumber.merge(doiTuongSo, value, Long::sum);
                } else {
                    unknownCount += value;
                }
            }

            Map<String, Long> result = new LinkedHashMap<>();
            for (Map.Entry<Integer, Long> entry : groupedByNumber.entrySet()) {
                result.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            if (unknownCount > 0) {
                result.put("(Chưa khai báo)", unknownCount);
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
            Map<String, Long> aggregated = new LinkedHashMap<>();
            for (Object[] row : rows) {
                String raw = row[0] == null ? "" : row[0].toString().trim();
                Long value = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;
                if (value == null || value <= 0) {
                    continue;
                }

                String normalized = normalizeKhuVuc(raw);
                aggregated.merge(normalized, value, Long::sum);
            }

            Map<String, Long> result = new LinkedHashMap<>();
            String[] orderedKeys = { "1", "2", "2NT", "3" };
            for (String key : orderedKeys) {
                if (aggregated.containsKey(key)) {
                    result.put(key, aggregated.remove(key));
                }
            }
            for (Map.Entry<String, Long> entry : aggregated.entrySet()) {
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }
    }

    private Integer extractFirstInteger(String value) {
        if (value == null) {
            return null;
        }
        Matcher matcher = FIRST_NUMBER_PATTERN.matcher(value.trim());
        if (!matcher.find()) {
            return null;
        }
        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeKhuVuc(String rawValue) {
        if (rawValue == null) {
            return "(Chưa khai báo)";
        }
        String trimmed = rawValue.trim();
        if (trimmed.isEmpty()) {
            return "(Chưa khai báo)";
        }

        String normalized = trimmed.toLowerCase()
                .replaceAll("\\s+", "")
                .replace("-", "");
        if (normalized.startsWith("kv")) {
            normalized = normalized.substring(2);
        }

        if (normalized.startsWith("2nt")) {
            return "2NT";
        }

        Integer number = extractFirstInteger(normalized);
        if (number != null) {
            return String.valueOf(number);
        }

        return trimmed.toUpperCase();
    }

    public int getNextId() {
        try (Session session = getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT max(idthisinh) FROM XtThisinhxettuyen25", Integer.class)
                    .uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        }
    }
}
