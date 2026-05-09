package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.Users;
import org.hibernate.Session;

public class UsersDao extends AbstractCrudDao<Users, String> {

    public UsersDao() {
        super(Users.class);
    }

    public Users findByUsername(String username) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM Users WHERE lower(username) = :u", Users.class)
                    .setParameter("u", username == null ? "" : username.toLowerCase().trim())
                    .uniqueResult();
        }
    }

    public Users findByEmail(String email) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM Users WHERE lower(email) = :e", Users.class)
                    .setParameter("e", email == null ? "" : email.toLowerCase().trim())
                    .uniqueResult();
        }
    }

    public List<Users> search(String keyword) {
        try (Session session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM Users WHERE lower(username) LIKE :q OR lower(full_name) LIKE :q OR lower(email) LIKE :q",
                    Users.class)
                    .setParameter("q", q)
                    .list();
        }
    }
}
