package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;
import org.hibernate.Session;

public class DiemCongDao extends AbstractCrudDao<XtDiemcongxetuyen, Integer> {

    public DiemCongDao() {
        super(XtDiemcongxetuyen.class);
    }

    public List<XtDiemcongxetuyen> findByCccd(String cccd) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtDiemcongxetuyen WHERE tsCccd = :c", XtDiemcongxetuyen.class)
                    .setParameter("c", cccd == null ? "" : cccd.trim())
                    .list();
        }
    }

    public XtDiemcongxetuyen findByKeys(String dcKeys) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtDiemcongxetuyen WHERE dcKeys = :k", XtDiemcongxetuyen.class)
                    .setParameter("k", dcKeys == null ? "" : dcKeys.trim())
                    .uniqueResult();
        }
    }

    public List<XtDiemcongxetuyen> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtDiemcongxetuyen WHERE lower(tsCccd) LIKE :q OR lower(manganh) LIKE :q OR lower(matohop) LIKE :q",
                    XtDiemcongxetuyen.class)
                    .setParameter("q", q)
                    .list();
        }
    }

    public int getNextId() {
        try (Session session = getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT max(iddiemcong) FROM XtDiemcongxetuyen", Integer.class)
                    .uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        }
    }
}
