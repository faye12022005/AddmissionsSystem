package org.AdmissionsSystem.dao;

import java.util.List;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class XtNguyenvongxettuyenDao extends AbstractCrudDao<XtNguyenvongxettuyen, Integer> {

    public XtNguyenvongxettuyenDao() {
        super(XtNguyenvongxettuyen.class);
    }

    /**
     * CREATE: Thêm mới nguyện vọng xét tuyển
     * 
     * @param nguyenvong nguyện vọng cần thêm
     * @return ID của nguyện vọng vừa thêm
     */
    public Integer themNguyenVong(XtNguyenvongxettuyen nguyenvong) {
        return (Integer) save(nguyenvong);
    }

    /**
     * READ: Lấy tất cả nguyện vọng xét tuyển
     * 
     * @return Danh sách tất cả nguyện vọng
     */
    public List<XtNguyenvongxettuyen> layTatCaNguyenVong() {
        return findAll();
    }

    public List<XtNguyenvongxettuyen> layNguyenVongTheoTrang(int page, int pageSize) {
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, pageSize);
        try (var session = getSessionFactory().openSession()) {
            return session.createQuery("FROM XtNguyenvongxettuyen ORDER BY idnv", XtNguyenvongxettuyen.class)
                    .setFirstResult((safePage - 1) * safePageSize)
                    .setMaxResults(safePageSize)
                    .setFetchSize(safePageSize)
                    .setReadOnly(true)
                    .list();
        }
    }

    public List<XtNguyenvongxettuyen> findPageByCccd(String cccdKeyword, int page, int pageSize) {
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, pageSize);
        String keyword = cccdKeyword == null ? "" : cccdKeyword.trim().toLowerCase();
        boolean hasKeyword = !keyword.isEmpty();
        try (var session = getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("FROM XtNguyenvongxettuyen");
            if (hasKeyword) {
                hql.append(" WHERE lower(nnCccd) LIKE :q");
            }
            hql.append(" ORDER BY nnCccd, nvTt");

            var query = session.createQuery(hql.toString(), XtNguyenvongxettuyen.class);
            if (hasKeyword) {
                query.setParameter("q", "%" + keyword + "%");
            }

            return query
                    .setFirstResult((safePage - 1) * safePageSize)
                    .setMaxResults(safePageSize)
                    .setFetchSize(safePageSize)
                    .setReadOnly(true)
                    .list();
        }
    }

    public long countByCccd(String cccdKeyword) {
        String keyword = cccdKeyword == null ? "" : cccdKeyword.trim().toLowerCase();
        boolean hasKeyword = !keyword.isEmpty();
        try (var session = getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("SELECT COUNT(*) FROM XtNguyenvongxettuyen");
            if (hasKeyword) {
                hql.append(" WHERE lower(nnCccd) LIKE :q");
            }
            var query = session.createQuery(hql.toString(), Long.class);
            if (hasKeyword) {
                query.setParameter("q", "%" + keyword + "%");
            }
            Long total = query.uniqueResult();
            return total == null ? 0L : total;
        }
    }

    /**
     * READ: Tìm nguyện vọng theo ID
     * 
     * @param idnv ID của nguyện vọng
     * @return Nguyện vọng nếu tìm thấy, null nếu không
     */
    public XtNguyenvongxettuyen timTheoId(Integer idnv) {
        return findById(idnv);
    }

    /**
     * READ: Tìm nguyện vọng theo CCCD thí sinh
     * 
     * @param nnCccd CCCD thí sinh
     * @return Danh sách nguyện vọng của thí sinh
     */
    public List<XtNguyenvongxettuyen> timTheoCccd(String nnCccd) {
        try (var session = getSessionFactory().openSession()) {
            return session
                    .createQuery("FROM XtNguyenvongxettuyen WHERE lower(nnCccd) = :cccd", XtNguyenvongxettuyen.class)
                    .setParameter("cccd", nnCccd != null ? nnCccd.toLowerCase() : "")
                    .list();
        }
    }

    /**
     * READ: Tìm nguyện vọng theo mã ngành
     * 
     * @param nvManganh mã ngành
     * @return Danh sách nguyện vọng theo ngành
     */
    public List<XtNguyenvongxettuyen> timTheoMaNganh(String nvManganh) {
        try (var session = getSessionFactory().openSession()) {
            return session
                    .createQuery("FROM XtNguyenvongxettuyen WHERE lower(nvManganh) = :manganh",
                            XtNguyenvongxettuyen.class)
                    .setParameter("manganh", nvManganh != null ? nvManganh.toLowerCase() : "")
                    .list();
        }
    }

    /**
     * READ: Tìm nguyện vọng theo kết quả xét tuyển
     * 
     * @param nvKetqua kết quả xét tuyển (Đạt, Không đạt, v.v.)
     * @return Danh sách nguyện vọng theo kết quả
     */
    public List<XtNguyenvongxettuyen> timTheoKetQua(String nvKetqua) {
        try (var session = getSessionFactory().openSession()) {
            return session
                    .createQuery("FROM XtNguyenvongxettuyen WHERE lower(nvKetqua) = :ketqua",
                            XtNguyenvongxettuyen.class)
                    .setParameter("ketqua", nvKetqua != null ? nvKetqua.toLowerCase() : "")
                    .list();
        }
    }

    /**
     * UPDATE: Cập nhật thông tin nguyện vọng
     * 
     * @param nguyenvong nguyện vọng với thông tin cập nhật
     */
    public void capNhatNguyenVong(XtNguyenvongxettuyen nguyenvong) {
        update(nguyenvong);
    }

    /**
     * UPDATE batch: Cập nhật hàng loạt nguyện vọng trong một transaction để tránh N+1 query.
     * Dùng Session.update thay vì merge để tránh SELECT lại từng bản ghi trước UPDATE.
     *
     * @param danhSach danh sách nguyện vọng cần cập nhật
     * @return số lượng bản ghi đã xử lý
     */
    public int capNhatNguyenVongHangLoat(List<XtNguyenvongxettuyen> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            return 0;
        }

        Transaction tx = null;
        try (Session session = getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            int processed = 0;
            int batchSize = 200;

            for (XtNguyenvongxettuyen nv : danhSach) {
                if (nv == null || nv.getIdnv() == null) {
                    continue;
                }
                session.update(nv);
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
            throw new RuntimeException("Lỗi cập nhật hàng loạt nguyện vọng: " + ex.getMessage(), ex);
        }
    }

    /**
     * UPDATE batch riêng cho cột nv_ketqua để đảm bảo trạng thái xét tuyển luôn ghi xuống DB.
     * Dùng JDBC batch trực tiếp để tránh rủi ro từ detached entity khi chỉ cần cập nhật 1 cột.
     *
     * @param danhSach danh sách nguyện vọng chứa idnv + nvKetqua
     * @return số bản ghi được DB xác nhận cập nhật
     */
    public int capNhatKetQuaHangLoat(List<XtNguyenvongxettuyen> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            return 0;
        }

        Transaction tx = null;
        try (Session session = getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            final int[] updated = new int[] {0};

            session.doWork(connection -> {
                String sql = "UPDATE xt_nguyenvongxettuyen SET nv_ketqua = ? WHERE idnv = ?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    int batched = 0;
                    for (XtNguyenvongxettuyen nv : danhSach) {
                        if (nv == null || nv.getIdnv() == null) {
                            continue;
                        }
                        ps.setString(1, nv.getNvKetqua());
                        ps.setInt(2, nv.getIdnv());
                        ps.addBatch();
                        batched++;

                        if (batched % 500 == 0) {
                            int[] rs = ps.executeBatch();
                            updated[0] += demBatchAffectRows(rs);
                        }
                    }
                    int[] rs = ps.executeBatch();
                    updated[0] += demBatchAffectRows(rs);
                }
            });

            tx.commit();
            return updated[0];
        } catch (Exception ex) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            throw new RuntimeException("Lỗi cập nhật batch nv_ketqua: " + ex.getMessage(), ex);
        }
    }

    private int demBatchAffectRows(int[] batchResult) {
        if (batchResult == null || batchResult.length == 0) {
            return 0;
        }
        int sum = 0;
        for (int value : batchResult) {
            if (value == Statement.SUCCESS_NO_INFO) {
                sum += 1;
            } else if (value > 0) {
                sum += value;
            }
        }
        return sum;
    }

    /**
     * DELETE: Xóa nguyện vọng theo ID
     * 
     * @param idnv ID của nguyện vọng cần xóa
     */
    public void xoaNguyenVong(Integer idnv) {
        deleteById(idnv);
    }

    /**
     * DELETE: Xóa nguyện vọng theo đối tượng
     * 
     * @param nguyenvong nguyện vọng cần xóa
     */
    public void xoa(XtNguyenvongxettuyen nguyenvong) {
        delete(nguyenvong);
    }

    /**
     * Kiểm tra nguyện vọng có tồn tại theo ID
     * 
     * @param idnv ID của nguyện vọng
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTai(Integer idnv) {
        return exists(idnv);
    }

    /**
     * Lấy tổng số nguyện vọng
     * 
     * @return tổng số nguyện vọng
     */
    public long demTatCa() {
        return count();
    }

    /**
     * Lấy ID nguyện vọng tiếp theo
     * 
     * @return ID tiếp theo
     */
    public Integer layIdTiepTheo() {
        try (var session = getSessionFactory().openSession()) {
            Integer maxId = session.createQuery("SELECT max(idnv) FROM XtNguyenvongxettuyen", Integer.class)
                    .uniqueResult();
            return (maxId != null) ? maxId + 1 : 1;
        }
    }
}
