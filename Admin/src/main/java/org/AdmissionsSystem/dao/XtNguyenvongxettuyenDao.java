package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;

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
