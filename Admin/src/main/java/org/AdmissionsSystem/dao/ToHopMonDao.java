package org.AdmissionsSystem.dao;

import java.util.List;

import org.AdmissionsSystem.models.XtTohopMonthi;

public class ToHopMonDao extends AbstractCrudDao<XtTohopMonthi, Integer> {
    public ToHopMonDao() {
        super(XtTohopMonthi.class);
    }

    /**
     * CREATE: Thêm mới tổ hợp môn

    /**
     * READ: Lấy tất cả tổ hợp môn
     * @return Danh sách tất cả tổ hợp môn
     */
    public List<XtTohopMonthi> layTatCaToHopMon() {
        return findAll();
    }

    /**
     * READ: Tìm tổ hợp môn theo ID 
     * @param idToHop ID của tổ hợp môn
     * @return Tổ hợp môn nếu tìm thấy, null nếu không tìm thấy
     */
    public XtTohopMonthi timTheoId(Integer idTohop){
        return findById(idTohop);
    }

    /**
     * READ: Tìm tổ hợp môn theo mã tổ hợp
     * @param maToHop mã tổ hợp
     * @return Tổ hợp môn nếu tìm thấy, null nếu không tìm thấy
     */
    public XtTohopMonthi timTheoMaToHop(String maTohop){
        try (var session = getSessionFactory().openSession()){
            return session.createQuery("FROM XtTohopMonthi WHERE lower(matohop) = :ma", XtTohopMonthi.class)
                    .setParameter("ma", maTohop != null ? maTohop.toLowerCase() : "")
                    .uniqueResult();
        }
    }

    /**
     * READ: Tìm tổ hợp môn theo tên tổ hợp
     * @param tenToHop tên tổ hợp
     * @return Tổ hợp môn nếu tìm thấy, null nếu không tìm thấy
     */
    public List<XtTohopMonthi> timTheoTenToHop(String tenTohop){
        try (var session = getSessionFactory().openSession()){
            return session.createQuery("FROM XtTohopMonthi WHERE lower(tentohop) LIKE :ten", XtTohopMonthi.class)
                    .setParameter("ten", "%" + (tenTohop != null ? tenTohop.toLowerCase() : "") + "%")
                    .list();
        }
    }

    /** 
     * READ : Tìm tổ hợp chứa môn học cụ thể 
     * @param tenMon môn học cần tìm
     * @return Dan sách tổ hợp môn chứa môn học đó
     */
    public List<XtTohopMonthi> timTheoMon(String monHoc){
        try (var session = getSessionFactory().openSession()){
            return session.createQuery(
                    "FROM XtTohopMonthi WHERE mon1 = :mon OR mon2 = :mon OR mon3 = :mon", XtTohopMonthi.class)
                    .setParameter("mon", monHoc)
                    .list();
        }
    }
    
    public List<XtTohopMonthi> search(String keyword) {
        try (var session = getSessionFactory().openSession()) {
            String q = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            return session.createQuery(
                    "FROM XtTohopMonthi WHERE lower(matohop) LIKE :q OR lower(tentohop) LIKE :q OR lower(mon1) LIKE :q OR lower(mon2) LIKE :q OR lower(mon3) LIKE :q",
                    XtTohopMonthi.class)
                    .setParameter("q", q)
                    .list();
        }
    }

    /**
     * UPDATE: Cập nhật thông tin tổ hợp môn 
     * @param toHopMonthi tổ hợp môn với thông tin cập nhật
     */
    public void capNhatToHopMon(XtTohopMonthi tohopMonthi){
        update(tohopMonthi);
    }

    /**
     * DELETE: Xóa tổ hợp môn theo ID
     * @param idTohop ID của tổ hợp môn cần xóa
     */
    public void xoaToHopMon(Integer idTohop) {
        deleteById(idTohop);
    }

    /**
     * DELETE: Xóa tổ hợp môn theo đối tượng
     * @param tohopMonthi tổ hợp môn cần xóa
     */
    public void xoa(XtTohopMonthi tohopMonthi) {
        delete(tohopMonthi);
    }

    /**
     * Kiểm tra tổ hợp môn có tồn tại theo ID
     * @param idTohop ID của tổ hợp môn
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTai(Integer idTohop) {
        return exists(idTohop);
    }

    /**
     * Kiểm tra tổ hợp môn có tồn tại theo mã
     * @param maTohop mã tổ hợp
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTaiTheoMa(String maTohop) {
        return timTheoMaToHop(maTohop) != null;
    }

    /**
     * Lấy tổng số tổ hợp môn
     * @return tổng số tổ hợp môn
     */
    public long demTatCa() {
        return count();
    }

    /**
     * Lấy ID tổ hợp môn tiếp theo
     * @return ID tiếp theo
     */
    public Integer layIdTiepTheo() {
        try (var session = getSessionFactory().openSession()){
            Integer maxID = session.createQuery("SELECT max(idtohop) FROM XtTohopMonthi", Integer.class)
                    .uniqueResult();
            return (maxID != null) ? maxID + 1 : 1;
        }
    }
}
