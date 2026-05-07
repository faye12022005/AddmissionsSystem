package org.AdmissionsSystem.bus.controller;

import java.util.List;

import org.AdmissionsSystem.models.XtBangquydoi;
import org.AdmissionsSystem.bus.service.XtBangquydoiService;

public class XtBangquydoiController {
    private final XtBangquydoiService service = new XtBangquydoiService();

    /**
     * Tải tất cả dữ liệu bảng quy đổi
     * @return danh sách tất cả bảng quy đổi
     */
    public List<XtBangquydoi> taiDuLieu() {
        return service.layTatCa();
    }

    /**
     * Tìm kiếm bảng quy đổi theo từ khóa
     * @param keyword từ khóa tìm kiếm (phương thức, tổ hợp, mã quy đổi, v.v.)
     * @return danh sách bảng quy đổi tìm thấy
     */
    public List<XtBangquydoi> timKiem(String keyword) {
        try {
            return service.timKiem(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện thêm bảng quy đổi
     * @param bangquydoi bảng quy đổi cần thêm
     * @throws IllegalArgumentException nếu thông tin không hợp lệ hoặc mã quy đổi đã tồn tại
     * @throws RuntimeException nếu có lỗi khi thêm vào cơ sở dữ liệu
     */
    public void xuLySuKienThem(XtBangquydoi bangquydoi) {
        kiemTraDuLieuDauVao(bangquydoi);

        try {
            service.them(bangquydoi);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm bảng quy đổi: " + e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện cập nhật bảng quy đổi
     * @param idqdCu ID bảng quy đổi cũ (dùng để tìm bản ghi)
     * @param bangquydoi bảng quy đổi với thông tin cập nhật
     * @throws IllegalArgumentException nếu thông tin không hợp lệ
     * @throws RuntimeException nếu có lỗi khi cập nhật trong cơ sở dữ liệu
     */
    public void xuLySuKienCapNhat(Integer idqdCu, XtBangquydoi bangquydoi) {
        kiemTraDuLieuDauVao(bangquydoi);

        if (idqdCu == null || idqdCu <= 0) {
            throw new IllegalArgumentException("ID bảng quy đổi cũ không hợp lệ.");
        }

        try {
            service.sua(idqdCu, bangquydoi);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật bảng quy đổi: " + e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện xóa bảng quy đổi theo ID
     * @param idqd ID bảng quy đổi cần xóa
     * @throws IllegalArgumentException nếu idqd không hợp lệ
     * @throws RuntimeException nếu có lỗi khi xóa trong cơ sở dữ liệu
     */
    public void xuLySuKienXoa(Integer idqd) {
        if (idqd == null || idqd <= 0) {
            throw new IllegalArgumentException("ID bảng quy đổi không hợp lệ.");
        }

        try {
            service.xoa(idqd);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa bảng quy đổi: " + e.getMessage());
        }
    }

    /**
     * Lấy bảng quy đổi theo ID
     * @param idqd ID của bảng quy đổi
     * @return bảng quy đổi nếu tìm thấy, null nếu không
     */
    public XtBangquydoi layTheoId(Integer idqd) {
        try {
            return service.layTheoId(idqd);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy bảng quy đổi theo ID: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách bảng quy đổi theo phương thức
     * @param dPhuongthuc phương thức xét tuyển
     * @return danh sách bảng quy đổi của phương thức
     */
    public List<XtBangquydoi> layTheoPhương(String dPhuongthuc) {
        try {
            if (dPhuongthuc == null || dPhuongthuc.trim().isEmpty()) {
                return taiDuLieu();
            }
            return service.layTheoPhương(dPhuongthuc);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy bảng quy đổi theo phương thức: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách bảng quy đổi theo tổ hợp
     * @param dTohop tổ hợp môn
     * @return danh sách bảng quy đổi của tổ hợp
     */
    public List<XtBangquydoi> layTheoTohop(String dTohop) {
        try {
            if (dTohop == null || dTohop.trim().isEmpty()) {
                return taiDuLieu();
            }
            return service.layTheoTohop(dTohop);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy bảng quy đổi theo tổ hợp: " + e.getMessage());
        }
    }

    /**
     * Lấy bảng quy đổi theo mã
     * @param dMaquydoi mã quy đổi
     * @return bảng quy đổi nếu tìm thấy, null nếu không
     */
    public XtBangquydoi layTheoMaQuydoi(String dMaquydoi) {
        try {
            return service.layTheoMaQuydoi(dMaquydoi);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy bảng quy đổi theo mã: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra bảng quy đổi có tồn tại theo ID
     * @param idqd ID bảng quy đổi cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTai(Integer idqd) {
        try {
            return service.kiemTraTonTai(idqd);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại bảng quy đổi: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra bảng quy đổi có tồn tại theo mã
     * @param dMaquydoi mã quy đổi cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    public boolean kiemTraTonTaiTheoMa(String dMaquydoi) {
        try {
            return service.kiemTraTonTaiTheoMa(dMaquydoi);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại bảng quy đổi theo mã: " + e.getMessage());
        }
    }

    /**
     * Lấy tổng số bảng quy đổi
     * @return tổng số bảng quy đổi
     */
    public long demTatCa() {
        try {
            return service.demTatCa();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm tổng bảng quy đổi: " + e.getMessage());
        }
    }

    /**
     * Làm mới danh sách dữ liệu
     * @return danh sách tất cả bảng quy đổi
     */
    public List<XtBangquydoi> layDuLieu() {
        return taiDuLieu();
    }

    /**
     * Kiểm tra dữ liệu đầu vào
     * @param model bảng quy đổi cần kiểm tra
     * @throws IllegalArgumentException nếu có trường bắt buộc bị thiếu hoặc không hợp lệ
     */
    private void kiemTraDuLieuDauVao(XtBangquydoi bangquydoi) {
        if (bangquydoi == null) {
            throw new IllegalArgumentException("Dữ liệu bảng quy đổi không được để trống.");
        }

        String phuongthuc = layChuoi(bangquydoi.getDPhuongthuc());
        String tohop = layChuoi(bangquydoi.getDTohop());
        String mon = layChuoi(bangquydoi.getDMon());
        String maquydoi = layChuoi(bangquydoi.getDMaquydoi());

        if (phuongthuc.isEmpty()) {
            throw new IllegalArgumentException("Phương thức xét tuyển là bắt buộc.");
        }

        if (tohop.isEmpty()) {
            throw new IllegalArgumentException("Tổ hợp môn là bắt buộc.");
        }

        if (mon.isEmpty()) {
            throw new IllegalArgumentException("Môn học là bắt buộc.");
        }

        if (maquydoi.isEmpty()) {
            throw new IllegalArgumentException("Mã quy đổi là bắt buộc.");
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
