package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtNganhTohop;
import org.hibernate.Session;

public class NganhToHopDao extends AbstractCrudDao<XtNganhTohop, Integer> {

    public NganhToHopDao() {
        super(XtNganhTohop.class);
    }

    public List<XtNganhTohop> findByMaNganh(String maNganh) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtNganhTohop WHERE lower(manganh) = :m", XtNganhTohop.class)
                    .setParameter("m", maNganh == null ? "" : maNganh.toLowerCase().trim())
                    .list();
        }
    }

    public List<XtNganhTohop> findByMaToHop(String maToHop) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtNganhTohop WHERE lower(matohop) = :m", XtNganhTohop.class)
                    .setParameter("m", maToHop == null ? "" : maToHop.toLowerCase().trim())
                    .list();
        }
    }

    public XtNganhTohop findByKeys(String tbKeys) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtNganhTohop WHERE tbKeys = :k", XtNganhTohop.class)
                    .setParameter("k", tbKeys == null ? "" : tbKeys.trim())
                    .uniqueResult();
        }
    }

    /**
     * Một dòng (ngành × mã tổ hợp đăng ký), dùng để đọc {@code dolech} khi xét tuyển.
     */
    public XtNganhTohop findByManganhAndMatohop(String maNganh, String maToHop) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM XtNganhTohop WHERE lower(manganh) = :m AND lower(matohop) = :t",
                    XtNganhTohop.class)
                    .setParameter("m", maNganh == null ? "" : maNganh.toLowerCase().trim())
                    .setParameter("t", maToHop == null ? "" : maToHop.toLowerCase().trim())
                    .uniqueResult();
        }
    }

    public List<XtNganhTohop> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtNganhTohop WHERE lower(manganh) LIKE :q OR lower(matohop) LIKE :q OR lower(tbKeys) LIKE :q",
                    XtNganhTohop.class)
                    .setParameter("q", q)
                    .list();
        }
    }


}
