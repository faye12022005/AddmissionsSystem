package org.AdmissionsSystem.dao;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;
import org.hibernate.Session;

public class NguyenVongDao extends AbstractCrudDao<XtNguyenvongxettuyen, Integer> {

    public NguyenVongDao() {
        super(XtNguyenvongxettuyen.class);
    }

    public List<XtNguyenvongxettuyen> findByCccd(String cccd) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtNguyenvongxettuyen WHERE nnCccd = :c", XtNguyenvongxettuyen.class)
                    .setParameter("c", cccd == null ? "" : cccd.trim())
                    .list();
        }
    }

    public List<XtNguyenvongxettuyen> findByMaNganh(String maNganh) {
        try (Session session = getSessionFactory().openSession()) {
            return session
                    .createQuery("FROM XtNguyenvongxettuyen WHERE lower(nvManganh) = :m",
                            XtNguyenvongxettuyen.class)
                    .setParameter("m", maNganh == null ? "" : maNganh.toLowerCase().trim())
                    .list();
        }
    }

    public XtNguyenvongxettuyen findByKeys(String nvKeys) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtNguyenvongxettuyen WHERE nvKeys = :k", XtNguyenvongxettuyen.class)
                    .setParameter("k", nvKeys == null ? "" : nvKeys.trim())
                    .uniqueResult();
        }
    }

    public List<XtNguyenvongxettuyen> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtNguyenvongxettuyen WHERE lower(nnCccd) LIKE :q OR lower(nvManganh) LIKE :q OR lower(nvKetqua) LIKE :q",
                    XtNguyenvongxettuyen.class)
                    .setParameter("q", q)
                    .list();
        }
    }

    public Map<String, Long> countByMaNganh() {
        try (Session session = getSessionFactory().openSession()) {
            List<Object[]> rows = session.createQuery(
                    "SELECT lower(nvManganh), count(*) FROM XtNguyenvongxettuyen GROUP BY lower(nvManganh)",
                    Object[].class)
                    .list();
            Map<String, Long> result = new HashMap<>();
            for (Object[] row : rows) {
                String maNganh = row[0] == null ? "" : row[0].toString().trim();
                Long count = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;
                result.put(maNganh, count);
            }
            return result;
        }
    }

    public int getNextId() {
        try (Session session = getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT max(idnv) FROM XtNguyenvongxettuyen", Integer.class)
                    .uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        }
    }
}
