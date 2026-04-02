package org.AdmissionsSystem.gui.modules.QuanLyThiSinh;

import javax.swing.*;
import java.awt.*;

public class ToastThiSinh {
    /**
     * Hiển thị thông báo thành công (Thêm, Sửa, Import, Export)
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent, 
                message, 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Hiển thị thông báo lỗi
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent, 
                message, 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Hiển thị hộp thoại xác nhận khi Xóa
     * @return true nếu người dùng chọn Yes, false nếu chọn No
     */
    public static boolean showConfirmDelete(Component parent, String name) {
        int choice = JOptionPane.showConfirmDialog(
                parent,
                "Bạn có chắc chắn muốn xóa thí sinh: " + name + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }
}
