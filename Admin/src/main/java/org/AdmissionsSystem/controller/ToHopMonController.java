package org.AdmissionsSystem.controller;

import java.util.List;

import org.AdmissionsSystem.models.XtTohopMonthi;
import org.AdmissionsSystem.service.ToHopMonService;

public class ToHopMonController {
    private final ToHopMonService service = new ToHopMonService();

    /**
     * Tải tất cẩ dự liệu tổ hợp môn 
     * @return danh sách tất cả tổ hợp môn
     */
    public List<XtTohopMonthi> taiDuLieu(){
        return service.layTatCa();
    }

    /**
     * Tìm kiếm theo tổ hợp môn 
     * @param keyword từ khóa tìm kiếm (mã, tên, hoặc môn)
     * @return danh sách tổ hợp môn tìm thấy
     */
    public List<XtTohopMonthi> timKiem(String keyword){
        try {
            return service.timKiem(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện thêm tổ hợp môn
     * @param tohopMonthi tổ hợp môn cần thêm
     * @throws IllegalArgumentException nếu thông tin không hợp lệ hoặc mã tổ hợp đã tồn tại
     * @throws RuntimeException nếu có lỗi khi thêm vào cơ sở dữ liệu
     */
    public void xuLySuKienThem(XtTohopMonthi tohopMonthi){
        kiemTraDuLieuDauVao(tohopMonthi);

        try {
            service.them(tohopMonthi);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm tổ hợp môn: " + e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện cập nhật tổ hợp môn
     * @param maToHopCu mã tổ hợp cũ (dùng để tìm bản ghi)
     * @param tohopMonthi tổ hợp môn với thông tin cập nhật
     * @throws IllegalArgumentException nếu thông tin không hợp lệ
     * @throws RuntimeException nếu có lỗi khi cập nhật trong cơ sở dữ liệu
     */
    public void xuLySuKienCapNhat(String maToHopCu, XtTohopMonthi tohopMonthi){
        kiemTraDuLieuDauVao(tohopMonthi);

        if(maToHopCu == null || maToHopCu.trim().isEmpty()){
            throw new IllegalArgumentException("Mã tổ hợp cũ không được để trống.");
        }

        try {
            service.sua(maToHopCu, tohopMonthi);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Lỗi khi cập nhật tổ hợp môn: " + e.getMessage());
        }
    }

    /** 
     * Xử lý sự kiện khi xóa tổ hợp môn
     * @param maToHop ma tổ hợp cần xóa
     * @throws IllegalArgumentException nếu maToHop không hợp lệ
     * @throws RuntimeException nếu có lỗi khi xóa trong cơ sở dữ liệu
     */
    public void xuLySuKienXoa(String maToHop) {
        if(maToHop == null || maToHop.trim().isEmpty()){
            throw new IllegalArgumentException("Mã tổ hợp không hợp lệ.");
        }

        try {
            service.xoa(maToHop);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa tổ hợp môn: " + e.getMessage());
        } 
    }

    /** 
     * Xử lý sự kiện khi xóa tổ hợp môn
     * @param idTohop ID tổ hợp cần xóa
     * @throws IllegalArgumentException nếu idTohop không hợp lệ
     * @throws RuntimeException nếu có lỗi khi xóa trong cơ sở dữ liệu
     */
    public void xuLySuKienXoaTheoId(Integer idTohop) {
        if(idTohop == null || idTohop <= 0){
            throw new IllegalArgumentException("ID tổ hợp không hợp lệ.");
        }

        try {
            service.layTheoId(idTohop);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa tổ hợp môn: " + e.getMessage());
        } 
    }

    /**
     * Lấy tổ hợp môn theo ID
     * @param idTohop ID của tổ hợp
     * @return tổ hợp môn nếu tìm thấy, null nếu không
     */
    public XtTohopMonthi layTheoId(Integer idTohop){
        try {
            return service.layTheoId(idTohop);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổ hợp môn theo ID: " + e.getMessage());
        }
    }

    /** 
     * Lấy tổ hợp môn theo mã
     * @param maTohop mã tổ hợp
     * @return tổ hợp môn nếu tìm thấy, null nếu không
     */
    public XtTohopMonthi layTheoMaTohop(String maTohop){
        try {
            return service.layTheoMaTohop(maTohop);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổ hợp môn theo mã: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra tổ hợp môn có tồn tại
     * @param maTohop mã tổ hợp cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTai(String maTohop){
        try {
            return service.kiemTraTonTaiTheoMa(maTohop);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại của tổ hợp môn: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách tổ hợp chứa một môn học
     * @param monHoc môn học cần tìm
     * @return danh sách tổ hợp môn chứa môn học đó
     */
    public List<XtTohopMonthi> layTohopTheoMon(String monHoc){
        try {
            if(monHoc == null || monHoc.trim().isEmpty()){
                return layDuLieu();
            }
            return service.layToHopTheoMon(monHoc);
        }catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổ hợp môn theo môn học: " + e.getMessage());
        }
    }

    /** 
     * Làm  mới danh sách dữ liệu 
     * @return danh sách tất cả tổ hợp môn
     */
    public List<XtTohopMonthi> layDuLieu(){
        return taiDuLieu();
    }

    /**
     * Kiểm tra dữ liệu đầu vào
     * @param model tổ hợp môn cần kiểm tra
     * @throws IllegalArgumentException nếu có trường bắt buộc bị thiếu hoặc không hợp lệ
     */
    private void kiemTraDuLieuDauVao(XtTohopMonthi tohopMonthi){
        if(tohopMonthi == null){
            throw new IllegalArgumentException("Dữ liệu tổ hợp môn không được để trống.");
        }

        String ma = layChuoi(tohopMonthi.getMatohop());
        String mon1 = layChuoi(tohopMonthi.getMon1());
        String mon2 = layChuoi(tohopMonthi.getMon2());
        String mon3 = layChuoi(tohopMonthi.getMon3());

        if(ma.isEmpty()){
            throw new IllegalArgumentException("Mã tổ hợp môn là bắt buộc.");
        }
        if(mon1.isEmpty() || mon2.isEmpty() || mon3.isEmpty()){
            throw new IllegalArgumentException("Cả 3 môn trong tổ hợp đều là bắt buộc.");
        }

        //Validate các môn không trùng lặp
        if(mon1.equalsIgnoreCase(mon2) || mon1.equalsIgnoreCase(mon3) || mon2.equalsIgnoreCase(mon3)){
            throw new IllegalArgumentException("Các môn trong tổ hợp không được trùng lặp.");
        }

        // Validate độ dài mã
        if(ma.length() > 10){
            throw new IllegalArgumentException("Mã tổ hợp môn không được vượt quá 10 ký tự.");
        }
    }

    /**
     * Chuyển đổi đối tượng sang chuỗi, xử lý null
     * @param obj đối tượng cần chuyển 
     * @return chuỗi kết quả
     */
    private String layChuoi(Object obj){
        return obj == null ? "" : obj.toString().trim();
    }
}
