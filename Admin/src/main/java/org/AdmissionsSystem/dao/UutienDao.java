package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtUutien;
import org.hibernate.Session;

public class UutienDao extends AbstractCrudDao<XtUutien, Integer> {

    public UutienDao() {
        super(XtUutien.class);
    }

    public List<XtUutien> findByCccd(String cccd) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtUutien WHERE cccd = :c", XtUutien.class)
                    .setParameter("c", cccd == null ? "" : cccd.trim())
                    .list();
        }
    }
}

