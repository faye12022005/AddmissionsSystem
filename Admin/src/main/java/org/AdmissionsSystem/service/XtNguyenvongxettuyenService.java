package org.AdmissionsSystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.AdmissionsSystem.dao.XtNguyenvongxettuyenDao;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;

public class XtNguyenvongxettuyenService {

    private final XtNguyenvongxettuyenDao dao = new XtNguyenvongxettuyenDao();

    /**
     * Lấy tất cả nguyện vọng xét tuyển
     * @return Danh sách tất cả nguyện vọng
     */
    public List<XtNguyenvongxettuyen> layTatCa() {
        return dao.layTatCaNguyenVong();
    }

    /**
     * Tìm kiếm nguyện vọng theo từ khóa
     * @param keyword Từ khóa tìm kiếm (CCCD, mã ngành, kết quả, v.v.)
     * @return Danh sách nguyện vọng khớp với từ khóa
     */
    public List<XtNguyenvongxettuyen> timKiem(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        if (q.isEmpty()) {
            return layTatCa();
        }

        List<XtNguyenvongxettuyen> filtered = new ArrayList<>();
        for (XtNguyenvongxettuyen model : layTatCa()) {
            if (khopKeyword(model, q)) {
                filtered.add(model);
            }
        }
        return filtered;
    }

    /**
     * Kiểm tra xem nguyện vọng có khớp với từ khóa tìm kiếm hay không
     * @param model Nguyện vọng cần kiểm tra
     * @param keyword Từ khóa tìm kiếm
     * @return true nếu khớp, false nếu không khớp
     */
    private boolean khopKeyword(XtNguyenvongxettuyen model, String keyword) {
        String cccd = layChuoi(model.getNnCccd()).toLowerCase(Locale.ROOT);
        String manganh = layChuoi(model.getNvManganh()).toLowerCase(Locale.ROOT);
        String ketqua = layChuoi(model.getNvKetqua()).toLowerCase(Locale.ROOT);
        String phuongthuc = layChuoi(model.getTtPhuongthuc()).toLowerCase(Locale.ROOT);

        return cccd.contains(keyword) || manganh.contains(keyword) || ketqua.contains(keyword) || phuongthuc.contains(keyword);
    }

    /**
     * Chuyển đổi đối tượng thành chuỗi, trim để loại bỏ khoảng trắng
     * @param obj đối tượng cần chuyển đổi
     * @return chuỗi kết quả sau khi trim
     */
    private String layChuoi(Object obj) {
        return obj != null ? obj.toString().trim() : "";
    }

    /**
     * Thêm mới nguyện vọng xét tuyển
     * @param model nguyện vọng cần thêm
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    public void them(XtNguyenvongxettuyen model) {
        kiemTraThongTinBatBuoc(model);
        
        XtNguyenvongxettuyen entity = saochepModel(model, new XtNguyenvongxettuyen());
        entity.setIdnv(dao.layIdTiepTheo());
        dao.themNguyenVong(entity);
    }

    /**
     * Cập nhật nguyện vọng xét tuyển theo ID
     * @param idnv ID nguyện vọng cũ
     * @param model nguyện vọng với thông tin cập nhật
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ hoặc không tìm thấy nguyện vọng cần cập nhật
     */
    public void sua(Integer idnv, XtNguyenvongxettuyen model) {
        kiemTraThongTinBatBuoc(model);

        XtNguyenvongxettuyen existing = dao.timTheoId(idnv);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy nguyện vọng cần cập nhật.");
        }

        saochepModel(model, existing);
        dao.capNhatNguyenVong(existing);
    }

    /**
     * Kiểm tra thông tin bắt buộc của nguyện vọng
     * @param model nguyện vọng cần kiểm tra
     * @throws IllegalArgumentException nếu có thông tin bắt buộc bị thiếu
     */
    private void kiemTraThongTinBatBuoc(XtNguyenvongxettuyen model) {
        if (model == null) {
            throw new IllegalArgumentException("Nguyện vọng không được null.");
        }

        String cccd = layChuoi(model.getNnCccd());
        String manganh = layChuoi(model.getNvManganh());
        Integer thutua = model.getNvTt();

        if (cccd.isEmpty()) {
            throw new IllegalArgumentException("CCCD thí sinh không được rỗng.");
        }

        if (manganh.isEmpty()) {
            throw new IllegalArgumentException("Mã ngành không được rỗng.");
        }

        if (thutua == null || thutua <= 0) {
            throw new IllegalArgumentException("Thứ tự nguyện vọng phải lớn hơn 0.");
        }
    }

    /**
     * Sao chép dữ liệu từ model này sang model khác
     * @param source model nguồn
     * @param target model đích
     * @return model đích sau khi đã sao chép dữ liệu
     */
    private XtNguyenvongxettuyen saochepModel(XtNguyenvongxettuyen source, XtNguyenvongxettuyen target) {
        if (source.getNnCccd() != null) target.setNnCccd(source.getNnCccd());
        if (source.getNvManganh() != null) target.setNvManganh(source.getNvManganh());
        if (source.getNvTt() != null) target.setNvTt(source.getNvTt());
        if (source.getDiemThxt() != null) target.setDiemThxt(source.getDiemThxt());
        if (source.getDiemUtqd() != null) target.setDiemUtqd(source.getDiemUtqd());
        if (source.getDiemCong() != null) target.setDiemCong(source.getDiemCong());
        if (source.getDiemXettuyen() != null) target.setDiemXettuyen(source.getDiemXettuyen());
        if (source.getNvKetqua() != null) target.setNvKetqua(source.getNvKetqua());
        if (source.getNvKeys() != null) target.setNvKeys(source.getNvKeys());
        if (source.getTtPhuongthuc() != null) target.setTtPhuongthuc(source.getTtPhuongthuc());
        if (source.getTtThm() != null) target.setTtThm(source.getTtThm());
        return target;
    }

    /**
     * Xóa nguyện vọng theo ID
     * @param idnv ID nguyện vọng cần xóa
     * @throws IllegalArgumentException nếu không tìm thấy nguyện vọng cần xóa
     */
    public void xoa(Integer idnv) {
        XtNguyenvongxettuyen existing = dao.timTheoId(idnv);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy nguyện vọng cần xóa.");
        }

        dao.xoaNguyenVong(existing.getIdnv());
    }

    /**
     * Lấy nguyện vọng theo ID
     * @param idnv ID của nguyện vọng
     * @return nguyện vọng nếu tìm thấy, null nếu không
     */
    public XtNguyenvongxettuyen layTheoId(Integer idnv) {
        return dao.timTheoId(idnv);
    }

    /**
     * Lấy danh sách nguyện vọng của thí sinh theo CCCD
     * @param nnCccd CCCD thí sinh
     * @return danh sách nguyện vọng của thí sinh
     */
    public List<XtNguyenvongxettuyen> layTheoCccd(String nnCccd) {
        return dao.timTheoCccd(nnCccd);
    }

    /**
     * Lấy danh sách nguyện vọng theo mã ngành
     * @param nvManganh mã ngành
     * @return danh sách nguyện vọng của ngành
     */
    public List<XtNguyenvongxettuyen> layTheoMaNganh(String nvManganh) {
        return dao.timTheoMaNganh(nvManganh);
    }

    /**
     * Kiểm tra nguyện vọng có tồn tại theo ID
     * @param idnv ID nguyện vọng
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTai(Integer idnv) {
        return dao.kiemTraTonTai(idnv);
    }

    /**
     * Lấy tổng số nguyện vọng
     * @return tổng số nguyện vọng
     */
    public long demTatCa() {
        return dao.demTatCa();
    }
}
