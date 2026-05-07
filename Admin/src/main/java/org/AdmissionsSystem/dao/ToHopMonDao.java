package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtTohopMonthi;
import org.hibernate.Session;

public class ToHopMonDao extends AbstractCrudDao<XtTohopMonthi, Integer> {

    public ToHopMonDao() {
        super(XtTohopMonthi.class);
    }

    public XtTohopMonthi findByMaToHop(String maToHop) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtTohopMonthi WHERE lower(matohop) = :m", XtTohopMonthi.class)
                    .setParameter("m", maToHop == null ? "" : maToHop.toLowerCase().trim())
                    .uniqueResult();
        }
    }

    public List<XtTohopMonthi> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtTohopMonthi WHERE lower(matohop) LIKE :q OR lower(tentohop) LIKE :q OR lower(mon1) LIKE :q OR lower(mon2) LIKE :q OR lower(mon3) LIKE :q",
                    XtTohopMonthi.class)
                    .setParameter("q", q)
                    .list();
        }
    }

    public int getNextId() {
        try (Session session = getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT max(idtohop) FROM XtTohopMonthi", Integer.class)
                    .uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        }
    }
}
