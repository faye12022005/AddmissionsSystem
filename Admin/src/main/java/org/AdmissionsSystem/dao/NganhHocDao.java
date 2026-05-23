package org.AdmissionsSystem.dao;
import java.util.List;
import org.AdmissionsSystem.models.XtNganh;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class NganhHocDao extends AbstractCrudDao<XtNganh, Integer> {

    public NganhHocDao() {
        super(XtNganh.class);
    }

    public XtNganh findByMaNganh(String maNganh) {
        try (org.hibernate.Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtNganh WHERE lower(manganh) = :ma", XtNganh.class)
                    .setParameter("ma", maNganh == null ? "" : maNganh.toLowerCase())
                    .uniqueResult();
        }
    }

    public int getNextId() {
        try (org.hibernate.Session session = getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT max(idnganh) FROM XtNganh", Integer.class).uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        }
    }

    /**
     * UPDATE batch ngành trong một transaction để giảm round-trip DB.
     * @param danhSach danh sách ngành cần cập nhật
     * @return số bản ghi đã xử lý
     */
    public int capNhatNganhHangLoat(List<XtNganh> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            return 0;
        }

        Transaction tx = null;
        try (Session session = getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            int processed = 0;
            int batchSize = 100;

            for (XtNganh nganh : danhSach) {
                if (nganh == null || nganh.getIdnganh() == null) {
                    continue;
                }
                session.update(nganh);
                processed++;

                if (processed % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }

            tx.commit();
            return processed;
        } catch (Exception ex) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            throw new RuntimeException("Lỗi cập nhật hàng loạt ngành: " + ex.getMessage(), ex);
        }
    }
}
