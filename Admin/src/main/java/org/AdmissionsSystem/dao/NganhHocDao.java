package org.AdmissionsSystem.dao;
import org.AdmissionsSystem.models.XtNganh;

public class NganhHocDao extends AbstractCrudDao<XtNganh, Integer> {

    public NganhHocDao() {
        super(XtNganh.class);
    }

    public XtNganh findByMaNganh(String maNganh) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("FROM XtNganh WHERE lower(manganh) = :ma", XtNganh.class)
                    .setParameter("ma", maNganh == null ? "" : maNganh.toLowerCase())
                    .uniqueResult();
        }
    }

    public int getNextId() {
        try (var session = sessionFactory.openSession()) {
            Integer maxId = session.createQuery("SELECT max(idnganh) FROM XtNganh", Integer.class).uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        }
    }
}
