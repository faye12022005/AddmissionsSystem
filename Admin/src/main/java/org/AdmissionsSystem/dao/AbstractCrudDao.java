package org.AdmissionsSystem.dao;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import org.AdmissionsSystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class AbstractCrudDao<T, ID extends Serializable> {

    private final Class<T> entityClass;
    protected final SessionFactory sessionFactory;

    protected AbstractCrudDao(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    public T findById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(entityClass, id);
        }
    }

    public Serializable save(T entity) {
        return executeInTransaction(session -> session.save(entity));
    }

    public void update(T entity) {
        executeInTransaction(session -> {
            session.update(entity);
            return null;
        });
    }

    public void delete(T entity) {
        executeInTransaction(session -> {
            session.delete(entity);
            return null;
        });
    }

    public void deleteById(ID id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }

    public boolean exists(ID id) {
        return findById(id) != null;
    }

    public long count() {
        try (Session session = sessionFactory.openSession()) {
            Long total = session.createQuery("SELECT COUNT(*) FROM " + entityClass.getSimpleName(), Long.class).uniqueResult();
            return total == null ? 0L : total;
        }
    }

    protected <R> R executeInTransaction(Function<Session, R> work) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            R result = work.apply(session);
            tx.commit();
            return result;
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException(ex);
        }
    }
}
