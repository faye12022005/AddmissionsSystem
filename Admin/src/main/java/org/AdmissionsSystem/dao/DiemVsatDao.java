package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtDiemVsat;
import org.hibernate.Session;

public class DiemVsatDao extends AbstractCrudDao<XtDiemVsat, Integer> {

    public DiemVsatDao() {
        super(XtDiemVsat.class);
    }

    public List<XtDiemVsat> findByCccd(String cccd) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtDiemVsat WHERE cccd = :c", XtDiemVsat.class)
                    .setParameter("c", cccd == null ? "" : cccd.trim())
                    .list();
        }
    }

    public List<XtDiemVsat> findByDotThi(String dotThi) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtDiemVsat WHERE lower(dotThi) = :d", XtDiemVsat.class)
                    .setParameter("d", dotThi == null ? "" : dotThi.toLowerCase().trim())
                    .list();
        }
    }

    public List<XtDiemVsat> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtDiemVsat WHERE lower(cccd) LIKE :q OR lower(dotThi) LIKE :q",
                    XtDiemVsat.class)
                    .setParameter("q", q)
                    .list();
        }
    }
}
