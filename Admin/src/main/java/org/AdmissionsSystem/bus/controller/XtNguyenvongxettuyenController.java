package org.AdmissionsSystem.bus.controller;

import java.util.List;

import org.AdmissionsSystem.models.XtNguyenvongxettuyen;
import org.AdmissionsSystem.bus.service.XtNguyenvongxettuyenService;

public class XtNguyenvongxettuyenController {
    private final XtNguyenvongxettuyenService service = new XtNguyenvongxettuyenService();

    /**
     * Tải tất cả dữ liệu nguyện vọng xét tuyển
     * @return danh sách tất cả nguyện vọng
     */
    public List<XtNguyenvongxettuyen> taiDuLieu() {
        return service.layTatCa();
    }

    public List<XtNguyenvongxettuyen> taiDuLieuTheoTrang(int page, int pageSize) {
        return service.layTheoTrang(page, pageSize);
    }

    public List<XtNguyenvongxettuyen> taiDuLieuTheoTrangVaCccd(String cccdKeyword, int page, int pageSize) {
        return service.layTheoTrangVaCccd(cccdKeyword, page, pageSize);
    }

    public long demTheoCccd(String cccdKeyword) {
        return service.demTheoCccd(cccdKeyword);
    }

    /**
     * Tìm kiếm nguyện vọng theo từ khóa
     * @param keyword từ khóa tìm kiếm (CCCD, mã ngành, kết quả, v.v.)
     * @return danh sách nguyện vọng tìm thấy
     */
    public List<XtNguyenvongxettuyen> timKiem(String keyword) {
        try {
            return service.timKiem(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện thêm nguyện vọng xét tuyển
     * @param nguyenvong nguyện vọng cần thêm
     * @throws IllegalArgumentException nếu thông tin không hợp lệ
     * @throws RuntimeException nếu có lỗi khi thêm vào cơ sở dữ liệu
     */
    public void xuLySuKienThem(XtNguyenvongxettuyen nguyenvong) {
        kiemTraDuLieuDauVao(nguyenvong);

        try {
            service.them(nguyenvong);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm nguyện vọng: " + e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện cập nhật nguyện vọng xét tuyển
     * @param idnvCu ID nguyện vọng cũ (dùng để tìm bản ghi)
     * @param nguyenvong nguyện vọng với thông tin cập nhật
     * @throws IllegalArgumentException nếu thông tin không hợp lệ
     * @throws RuntimeException nếu có lỗi khi cập nhật trong cơ sở dữ liệu
     */
    public void xuLySuKienCapNhat(Integer idnvCu, XtNguyenvongxettuyen nguyenvong) {
        kiemTraDuLieuDauVao(nguyenvong);

        if (idnvCu == null || idnvCu <= 0) {
            throw new IllegalArgumentException("ID nguyện vọng cũ không hợp lệ.");
        }

        try {
            service.sua(idnvCu, nguyenvong);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật nguyện vọng: " + e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện xóa nguyện vọng xét tuyển theo ID
     * @param idnv ID nguyện vọng cần xóa
     * @throws IllegalArgumentException nếu idnv không hợp lệ
     * @throws RuntimeException nếu có lỗi khi xóa trong cơ sở dữ liệu
     */
    public void xuLySuKienXoa(Integer idnv) {
        if (idnv == null || idnv <= 0) {
            throw new IllegalArgumentException("ID nguyện vọng không hợp lệ.");
        }

        try {
            service.xoa(idnv);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa nguyện vọng: " + e.getMessage());
        }
    }

    /**
     * Lấy nguyện vọng theo ID
     * @param idnv ID của nguyện vọng
     * @return nguyện vọng nếu tìm thấy, null nếu không
     */
    public XtNguyenvongxettuyen layTheoId(Integer idnv) {
        try {
            return service.layTheoId(idnv);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy nguyện vọng theo ID: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách nguyện vọng của thí sinh theo CCCD
     * @param nnCccd CCCD thí sinh
     * @return danh sách nguyện vọng của thí sinh
     */
    public List<XtNguyenvongxettuyen> layTheoCccd(String nnCccd) {
        try {
            if (nnCccd == null || nnCccd.trim().isEmpty()) {
                return taiDuLieu();
            }
            return service.layTheoCccd(nnCccd);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy nguyện vọng theo CCCD: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách nguyện vọng theo mã ngành
     * @param nvManganh mã ngành
     * @return danh sách nguyện vọng của ngành
     */
    public List<XtNguyenvongxettuyen> layTheoMaNganh(String nvManganh) {
        try {
            if (nvManganh == null || nvManganh.trim().isEmpty()) {
                return taiDuLieu();
            }
            return service.layTheoMaNganh(nvManganh);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy nguyện vọng theo mã ngành: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra nguyện vọng có tồn tại
     * @param idnv ID nguyện vọng cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTai(Integer idnv) {
        try {
            return service.kiemTraTonTai(idnv);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại nguyện vọng: " + e.getMessage());
        }
    }

    /**
     * Lấy tổng số nguyện vọng
     * @return tổng số nguyện vọng
     */
    public long demTatCa() {
        try {
            return service.demTatCa();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm tổng nguyện vọng: " + e.getMessage());
        }
    }

    /**
     * Làm mới danh sách dữ liệu
     * @return danh sách tất cả nguyện vọng
     */
    public List<XtNguyenvongxettuyen> layDuLieu() {
        return taiDuLieu();
    }

    public XtNguyenvongxettuyenService.XetTuyenResult chayXetTuyenHeThong() {
        try {
            return service.chayXetTuyenHeThong();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi chạy xét tuyển: " + e.getMessage());
        }
    }

    public int tinhDiemXetTuyenAll() {
        try {
            return service.tinhDiemXetTuyenAll();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính điểm xét tuyển: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra dữ liệu đầu vào
     * @param model nguyện vọng cần kiểm tra
     * @throws IllegalArgumentException nếu có trường bắt buộc bị thiếu hoặc không hợp lệ
     */
    private void kiemTraDuLieuDauVao(XtNguyenvongxettuyen nguyenvong) {
        if (nguyenvong == null) {
            throw new IllegalArgumentException("Dữ liệu nguyện vọng không được để trống.");
        }

        String cccd = layChuoi(nguyenvong.getNnCccd());
        String manganh = layChuoi(nguyenvong.getNvManganh());
        Integer thutua = nguyenvong.getNvTt();

        if (cccd.isEmpty()) {
            throw new IllegalArgumentException("CCCD thí sinh là bắt buộc.");
        }

        if (manganh.isEmpty()) {
            throw new IllegalArgumentException("Mã ngành là bắt buộc.");
        }

        if (thutua == null || thutua <= 0) {
            throw new IllegalArgumentException("Thứ tự nguyện vọng phải lớn hơn 0.");
        }

        // Validate độ dài CCCD
        if (cccd.length() != 12) {
            throw new IllegalArgumentException("CCCD phải có đúng 12 ký tự.");
        }
    }

    /**
     * Chuyển đổi đối tượng sang chuỗi, xử lý null
     * @param obj đối tượng cần chuyển
     * @return chuỗi kết quả
     */
    private String layChuoi(Object obj) {
        return obj == null ? "" : obj.toString().trim();
    }
}
