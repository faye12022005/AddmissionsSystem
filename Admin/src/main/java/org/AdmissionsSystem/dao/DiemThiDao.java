package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtDiemthixettuyen;
import org.hibernate.Session;

public class DiemThiDao extends AbstractCrudDao<XtDiemthixettuyen, Integer> {

    public DiemThiDao() {
        super(XtDiemthixettuyen.class);
    }

    public XtDiemthixettuyen findByCccd(String cccd) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtDiemthixettuyen WHERE cccd = :c", XtDiemthixettuyen.class)
                    .setParameter("c", cccd == null ? "" : cccd.trim())
                    .uniqueResult();
        }
    }

    public List<XtDiemthixettuyen> findByPhuongThuc(String phuongThuc) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtDiemthixettuyen WHERE lower(dPhuongthuc) = :p",
                    XtDiemthixettuyen.class)
                    .setParameter("p", phuongThuc == null ? "" : phuongThuc.toLowerCase().trim())
                    .list();
        }
    }

    public List<XtDiemthixettuyen> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtDiemthixettuyen WHERE lower(cccd) LIKE :q OR lower(sobaodanh) LIKE :q OR lower(dPhuongthuc) LIKE :q",
                    XtDiemthixettuyen.class)
                    .setParameter("q", q)
                    .list();
        }
    }

    public int getNextId() {
        try (Session session = getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT max(iddiemthi) FROM XtDiemthixettuyen", Integer.class)
                    .uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        }
    }
}
