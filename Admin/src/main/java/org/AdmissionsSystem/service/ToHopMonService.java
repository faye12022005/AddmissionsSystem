package org.AdmissionsSystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.AdmissionsSystem.dao.ToHopMonDao;
import org.AdmissionsSystem.models.XtTohopMonthi;

public class ToHopMonService {
    private final ToHopMonDao dao = new ToHopMonDao();

    /**
     * Lấy tất cả tổ hợp môn
     * @return Danh sách tất cả tổ hợp môn
     */
    public List<XtTohopMonthi> layTatCa(){
        return dao.layTatCaToHopMon();
    }

    /**
     * Tìm kiếm tổ hợp môn theo tên hoặc mã
     * @param keyword Từ khóa tìm kiếm (có thể là tên hoặc mã tổ hợp)
     * @return Danh sách tổ hợp môn khớp với từ khóa
     */
    public List<XtTohopMonthi> timKiem(String keyword){
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        if(q.isEmpty()){
            return layTatCa();
        }

        List<XtTohopMonthi> filltered = new ArrayList<>();
        for(XtTohopMonthi model : layTatCa()){
            if(khopKeyword(model, q)){
                filltered.add(model);
            }
        }
        return filltered;
    }

    /**
     * Kiểm tra xem tổ hợp môn có khớp với từ khóa tìm kiếm hay không
     * @param model Tổ hợp môn cần kiểm tra
     * @param keyword Từ khóa tìm kiếm
     * @return true nếu khớp, false nếu không khớp
     */
    private boolean khopKeyword(XtTohopMonthi model, String keyword){
        String ma = layChuoi(model.getMatohop()).toLowerCase(Locale.ROOT);
        String ten = layChuoi(model.getTentohop()).toLowerCase(Locale.ROOT);
        String mon1 = layChuoi(model.getMon1()).toLowerCase(Locale.ROOT);
        String mon2 = layChuoi(model.getMon2()).toLowerCase(Locale.ROOT);
        String mon3 = layChuoi(model.getMon3()).toLowerCase(Locale.ROOT);

        return ma.contains(keyword) || ten.contains(keyword) || mon1.contains(keyword) || mon2.contains(keyword) || mon3.contains(keyword);
    }

    /**
     * chuyển đổi đối tượng thành chuỗi, trim để loại bỏ khoảng trắng
     * @param obj đối tượng cần chuyển đổi
     * @return chuỗi kết quả sau khi trim
     */
    private String layChuoi(Object obj){
        return obj != null ? obj.toString().trim() : "";
    }

    /**
     * Thêm mới tổ hợp môn
     * @param model tổ hợp môn cần thêm
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    public void them(XtTohopMonthi model){
        kiemTraThongTinBatBuoc(model);
        String ma = layChuoi(model.getMatohop());

        if(dao.timTheoMaToHop(ma) != null){
            throw new IllegalArgumentException("Mã tổ hợp đã tồn tại.");
        }

        XtTohopMonthi entity = saochepModel(model, new XtTohopMonthi());
        entity.setIdtohop(dao.layIdTiepTheo());
        dao.themToHopMon(entity);
    }

    /**
     * Cập nhật tổ hợp môn theo mã tổ hợp
     * @param maTohopCu mã tổ hợp cũ
     * @param model tổ hợp môn với thông tin cập nhật
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ hoặc không tìm thấy tổ hợp cần cập nhật 
     */
    public void sua(String maTohopCu, XtTohopMonthi model){
        kiemTraThongTinBatBuoc(model);

        XtTohopMonthi existing = dao.timTheoMaToHop(maTohopCu);
        if(existing == null){
            throw new IllegalArgumentException("Không tìm thấy tổ hợp môn cần cập nhật.");
        }

        String maToHopMoi = layChuoi(model.getMatohop());
        XtTohopMonthi duplicate = dao.timTheoMaToHop(maToHopMoi);
        if(duplicate != null && !duplicate.getIdtohop().equals(existing.getIdtohop())) {
            throw new IllegalArgumentException("Mã tổ hợp mới đã tồn tại.");
        }

        saochepModel(model, existing);
        dao.capNhatToHopMon(existing);
    }

    /**
     * Kiểm tra thông tin bắt buộc của tổ hợp môn
     * @param model tổ hợp môn cần kiểm tra
     * @throws IllegalArgumentException nếu có thông tin bắt buộc bị thiếu
     */
    private void kiemTraThongTinBatBuoc(XtTohopMonthi model){
        if(model == null){
            throw new IllegalArgumentException("Tổ hợp môn không được null.");
        }

        String ma = layChuoi(model.getMatohop());
        String mon1 = layChuoi(model.getMon1());
        String mon2 = layChuoi(model.getMon2());
        String mon3 = layChuoi(model.getMon3());

        if(ma.isEmpty()){
            throw new IllegalArgumentException("Mã tổ hợp khong được rỗng");
        }

        if(mon1.isEmpty() || mon2.isEmpty() || mon3.isEmpty()){
            throw new IllegalArgumentException("Môn học trong tổ hợp không được rỗng");
        }

        // Kiểm tra các môn học không được trùng lặp
        if(mon1.equals(mon2) || mon1.equals(mon3) || mon2.equals(mon3)){
            throw new IllegalArgumentException("Các môn học trong tổ hợp không được trùng nhau");
        }
    }

    /**
     * Sao chép dữ liệu từ model này sang model khác
     * @param source model nguồn
     * @param target model đích
     * @return model đích sau khi đã sao chép dữ liệu
     */
    private XtTohopMonthi saochepModel(XtTohopMonthi source, XtTohopMonthi target){
        target.setMatohop(layChuoi(source.getMatohop()));
        target.setTentohop(layChuoi(source.getTentohop()));
        target.setMon1(layChuoi(source.getMon1()));
        target.setMon2(layChuoi(source.getMon2()));
        target.setMon3(layChuoi(source.getMon3()));
        return target;
    }

    /**
     * Xóa tổ hợp theo mã
     * @param maTohop mã tổ hợp cần xóa
     * @throws IllegalArgumentException nếu không tìm thấy tổ hợp cần xóa
     */
    public void xoa(String maTohop){
        XtTohopMonthi existing = dao.timTheoMaToHop(maTohop);
        if(existing == null){
            throw new IllegalArgumentException("Không tìm thấy tổ hợp môn cần xóa.");
        }

        dao.xoaToHopMon(existing.getIdtohop());
    }

    /**
     * Xóa tổ hợp theo ID
     * @param idTohop ID tổ hợp cần xóa
     * @throws IllegalArgumentException nếu không tìm thấy tổ hợp cần xóa
     */
    public void xoaTheoID(Integer idTohop){
        XtTohopMonthi existing = dao.timTheoId(idTohop);
        if(existing == null){
            throw new IllegalArgumentException("Không tìm thấy tổ hợp môn cần xóa.");
        }

        dao.xoaToHopMon(existing.getIdtohop());
    }

    /**
     * Lấy tổ hợp môn theo id
     * @param idTohop ID của tổ hợp môn
     * @param maTohop
     * @return
     */
    public XtTohopMonthi layTheoId(Integer idTohop){
        return dao.timTheoId(idTohop);
    }

    /**
     * Lấy tổ hợp theo ma
     * @param maTohop mã tổ hợp 
     * @return tổ hợp môn nếu tìm thấy, null nếu không
     */
    public XtTohopMonthi layTheoMaTohop(String maTohop){
        return dao.timTheoMaToHop(maTohop);
    }

    /**
     * Kiểm tra tổ hợp môn có tồn tại theo mã 
     * @param maTohop mã tổ hợp
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTaiTheoMa(String maTohop){
        return dao.timTheoMaToHop(maTohop) != null;
    }

    /** 
     * Lấy danh sách tổ hợp môn chứa một môn học cụ thể 
     * @param monHoc môn học cần tìm 
     * @return danh sách tổ hợp môn chứa môn học đó
     */
    public List<XtTohopMonthi> layToHopTheoMon(String monHoc){
        return dao.timTheoMon(monHoc);
    }
}
