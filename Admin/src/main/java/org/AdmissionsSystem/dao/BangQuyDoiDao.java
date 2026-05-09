package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtBangquydoi;
import org.hibernate.Session;

public class BangQuyDoiDao extends AbstractCrudDao<XtBangquydoi, Integer> {

    public BangQuyDoiDao() {
        super(XtBangquydoi.class);
    }

    public List<XtBangquydoi> findByPhuongThuc(String phuongThuc) {
        try (Session session = getSessionFactory().openSession()) {
            return session
                    .createQuery("FROM XtBangquydoi WHERE lower(dPhuongthuc) = :p", XtBangquydoi.class)
                    .setParameter("p", phuongThuc == null ? "" : phuongThuc.toLowerCase().trim())
                    .list();
        }
    }

    public XtBangquydoi findByMaQuyDoi(String maQuyDoi) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtBangquydoi WHERE dMaquydoi = :m", XtBangquydoi.class)
                    .setParameter("m", maQuyDoi == null ? "" : maQuyDoi.trim())
                    .uniqueResult();
        }
    }

    public List<XtBangquydoi> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtBangquydoi WHERE lower(dPhuongthuc) LIKE :q OR lower(dTohop) LIKE :q OR lower(dMon) LIKE :q OR lower(dMaquydoi) LIKE :q",
                    XtBangquydoi.class)
                    .setParameter("q", q)
                    .list();
        }
    }

    public int getNextId() {
        try (Session session = getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT max(idqd) FROM XtBangquydoi", Integer.class).uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        }
    }
}
