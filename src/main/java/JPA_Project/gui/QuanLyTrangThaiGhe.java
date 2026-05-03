package JPA_Project.gui;

import JPA_Project.entity.ChoDat;

import javax.swing.*;
import java.awt.*;

/**
 * QuanLyTrangThaiGhe - Hỗ trợ quản lý màu sắc và trạng thái hiển thị của các ghế (JButton).
 */
public class QuanLyTrangThaiGhe {

    public static final Color MAU_DANG_CHON = new Color(0, 123, 255);
    public static final Color MAU_DA_DAT = Color.BLACK;
    public static final Color MAU_TRONG = Color.LIGHT_GRAY;
    public static final Color MAU_CHU_TRANG = Color.WHITE;
    public static final Color MAU_CHU_DEN = Color.BLACK;

    public void thietLapTrangThai(JButton btnCho, ChoDat cho, boolean isSelected) {
        if (cho.isDaDat()) {
            btnCho.setBackground(MAU_DA_DAT);
            btnCho.setForeground(MAU_CHU_TRANG);
            btnCho.setEnabled(false);
            btnCho.setToolTipText("Ghế đã được bán (Không thể chọn)");
        } else if (isSelected) {
            btnCho.setBackground(MAU_DANG_CHON);
            btnCho.setForeground(MAU_CHU_TRANG);
            btnCho.setEnabled(true);
            btnCho.setToolTipText("Ghế đang được bạn chọn");
        } else {
            btnCho.setBackground(MAU_TRONG);
            btnCho.setForeground(MAU_CHU_DEN);
            btnCho.setEnabled(true);
            btnCho.setToolTipText("Ghế còn trống - Nhấn để chọn");
        }
    }

    public void doiMauKhiClick(JButton btnCho, boolean isSelectedNow) {
        if (isSelectedNow) {
            btnCho.setBackground(MAU_DANG_CHON);
            btnCho.setForeground(MAU_CHU_TRANG);
        } else {
            btnCho.setBackground(MAU_TRONG);
            btnCho.setForeground(MAU_CHU_DEN);
        }
    }

    public void thietLapTrangThaiKhongDat(JButton btnCho, boolean isSelected) {
        if (isSelected) {
            btnCho.setBackground(MAU_DANG_CHON);
            btnCho.setForeground(MAU_CHU_TRANG);
            btnCho.setEnabled(true);
            btnCho.setToolTipText("Ghế đang được bạn chọn");
        } else {
            btnCho.setBackground(MAU_TRONG);
            btnCho.setForeground(MAU_CHU_DEN);
            btnCho.setEnabled(true);
            btnCho.setToolTipText("Ghế còn trống - Nhấn để chọn");
        }
    }
}
