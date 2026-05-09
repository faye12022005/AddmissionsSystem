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

    protected AbstractCrudDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    public List<T> findAll() {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    public T findById(ID id) {
        try (Session session = getSessionFactory().openSession()) {
            return session.get(entityClass, id);
        }
    }

    public Serializable save(T entity) {
        return executeInTransaction(session -> session.save(entity));
    }

    public void update(T entity) {
        executeInTransaction(session -> {
            // merge is safer than update for detached entities in Swing apps
            session.merge(entity);
            return null;
        });
    }

    public void delete(T entity) {
        executeInTransaction(session -> {
            // Re-attach or fetch before delete to avoid "closed session" issues
            Object persistentInstance = session.get(entityClass, getEntityId(entity));
            if (persistentInstance != null) {
                session.delete(persistentInstance);
            }
            return null;
        });
    }
    
    // Abstract method to get ID from entity, or use reflection if possible. 
    // For simplicity, we can just try to delete the entity directly if it's attached.
    private Serializable getEntityId(T entity) {
        return (Serializable) getSessionFactory().getClassMetadata(entityClass).getIdentifier(entity, null);
    }

    public void deleteById(ID id) {
        executeInTransaction(session -> {
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.delete(entity);
            }
            return null;
        });
    }

    public boolean exists(ID id) {
        return findById(id) != null;
    }

    public long count() {
        try (Session session = getSessionFactory().openSession()) {
            Long total = session.createQuery("SELECT COUNT(*) FROM " + entityClass.getSimpleName(), Long.class).uniqueResult();
            return total == null ? 0L : total;
        }
    }

    protected <R> R executeInTransaction(Function<Session, R> work) {
        Transaction tx = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            tx = session.beginTransaction();
            R result = work.apply(session);
            tx.commit();
            return result;
        } catch (Exception ex) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            throw new RuntimeException("Lỗi thao tác Database: " + ex.getMessage(), ex);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
