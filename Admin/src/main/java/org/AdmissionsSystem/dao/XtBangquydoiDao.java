package org.AdmissionsSystem.dao;

import java.util.List;
import org.AdmissionsSystem.models.XtBangquydoi;

public class XtBangquydoiDao extends AbstractCrudDao<XtBangquydoi, Integer> {

    public XtBangquydoiDao() {
        super(XtBangquydoi.class);
    }

    /**
     * CREATE: Thêm mới bảng quy đổi
     * @param bangquydoi bảng quy đổi cần thêm
     * @return ID của bảng quy đổi vừa thêm
     */
    public Integer themBangQuydoi(XtBangquydoi bangquydoi) {
        return (Integer) save(bangquydoi);
    }

    /**
     * READ: Lấy tất cả bảng quy đổi
     * @return Danh sách tất cả bảng quy đổi
     */
    public List<XtBangquydoi> layTatCaBangQuydoi() {
        return findAll();
    }

    /**
     * READ: Tìm bảng quy đổi theo ID
     * @param idqd ID của bảng quy đổi
     * @return Bảng quy đổi nếu tìm thấy, null nếu không
     */
    public XtBangquydoi timTheoId(Integer idqd) {
        return findById(idqd);
    }

    /**
     * READ: Tìm bảng quy đổi theo phương thức
     * @param dPhuongthuc phương thức xét tuyển
     * @return Danh sách bảng quy đổi theo phương thức
     */
    public List<XtBangquydoi> timTheoPhương(String dPhuongthuc) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("FROM XtBangquydoi WHERE lower(d_phuongthuc) = :phuongthuc", XtBangquydoi.class)
                    .setParameter("phuongthuc", dPhuongthuc != null ? dPhuongthuc.toLowerCase() : "")
                    .list();
        }
    }

    /**
     * READ: Tìm bảng quy đổi theo tổ hợp
     * @param dTohop tổ hợp môn
     * @return Danh sách bảng quy đổi theo tổ hợp
     */
    public List<XtBangquydoi> timTheoTohop(String dTohop) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("FROM XtBangquydoi WHERE lower(d_tohop) = :tohop", XtBangquydoi.class)
                    .setParameter("tohop", dTohop != null ? dTohop.toLowerCase() : "")
                    .list();
        }
    }

    /**
     * READ: Tìm bảng quy đổi theo mã quy đổi
     * @param dMaquydoi mã quy đổi
     * @return Bảng quy đổi nếu tìm thấy, null nếu không
     */
    public XtBangquydoi timTheoMaQuydoi(String dMaquydoi) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("FROM XtBangquydoi WHERE lower(d_maquydoi) = :maquydoi", XtBangquydoi.class)
                    .setParameter("maquydoi", dMaquydoi != null ? dMaquydoi.toLowerCase() : "")
                    .uniqueResult();
        }
    }

    /**
     * READ: Tìm bảng quy đổi theo môn
     * @param dMon tên môn
     * @return Danh sách bảng quy đổi theo môn
     */
    public List<XtBangquydoi> timTheoMon(String dMon) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("FROM XtBangquydoi WHERE lower(d_mon) LIKE :mon", XtBangquydoi.class)
                    .setParameter("mon", "%" + (dMon != null ? dMon.toLowerCase() : "") + "%")
                    .list();
        }
    }

    /**
     * UPDATE: Cập nhật thông tin bảng quy đổi
     * @param bangquydoi bảng quy đổi với thông tin cập nhật
     */
    public void capNhatBangQuydoi(XtBangquydoi bangquydoi) {
        update(bangquydoi);
    }

    /**
     * DELETE: Xóa bảng quy đổi theo ID
     * @param idqd ID của bảng quy đổi cần xóa
     */
    public void xoaBangQuydoi(Integer idqd) {
        deleteById(idqd);
    }

    /**
     * DELETE: Xóa bảng quy đổi theo đối tượng
     * @param bangquydoi bảng quy đổi cần xóa
     */
    public void xoa(XtBangquydoi bangquydoi) {
        delete(bangquydoi);
    }

    /**
     * Kiểm tra bảng quy đổi có tồn tại theo ID
     * @param idqd ID của bảng quy đổi
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTai(Integer idqd) {
        return exists(idqd);
    }

    /**
     * Kiểm tra bảng quy đổi có tồn tại theo mã
     * @param dMaquydoi mã quy đổi
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTaiTheoMa(String dMaquydoi) {
        return timTheoMaQuydoi(dMaquydoi) != null;
    }

    /**
     * Lấy tổng số bảng quy đổi
     * @return tổng số bảng quy đổi
     */
    public long demTatCa() {
        return count();
    }

    /**
     * Lấy ID bảng quy đổi tiếp theo
     * @return ID tiếp theo
     */
    public Integer layIdTiepTheo() {
        try (var session = sessionFactory.openSession()) {
            Integer maxId = session.createQuery("SELECT max(idqd) FROM XtBangquydoi", Integer.class)
                    .uniqueResult();
            return (maxId != null) ? maxId + 1 : 1;
        }
    }
}
