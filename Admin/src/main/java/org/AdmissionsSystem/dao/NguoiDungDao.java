package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtNguoidung;
import org.hibernate.Session;

public class NguoiDungDao extends AbstractCrudDao<XtNguoidung, String> {

    public NguoiDungDao() {
        super(XtNguoidung.class);
    }

    public XtNguoidung findByUsername(String username) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtNguoidung WHERE lower(username) = :u", XtNguoidung.class)
                    .setParameter("u", username == null ? "" : username.toLowerCase().trim())
                    .uniqueResult();
        }
    }

    public XtNguoidung findByEmail(String email) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtNguoidung WHERE lower(email) = :e", XtNguoidung.class)
                    .setParameter("e", email == null ? "" : email.toLowerCase().trim())
                    .uniqueResult();
        }
    }

    public List<XtNguoidung> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtNguoidung WHERE lower(username) LIKE :q OR lower(fullName) LIKE :q OR lower(email) LIKE :q",
                    XtNguoidung.class)
                    .setParameter("q", q)
                    .list();
        }
    }

    public int findMaxNumericId() {
        try (Session session = getSessionFactory().openSession()) {
            Object result = session.createNativeQuery(
                    "SELECT MAX(CAST(id AS UNSIGNED)) FROM users WHERE id REGEXP '^[0-9]+$'")
                    .uniqueResult();
            if (result == null) {
                return 0;
            }
            if (result instanceof Number) {
                return ((Number) result).intValue();
            }
            return Integer.parseInt(result.toString());
        }
    }
}
