package JDBC_Project.control;

import entity.ChoDat;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Lớp hỗ trợ quản lý màu sắc và trạng thái hiển thị của các ghế (JButton)
 */
public class QuanLyTrangThaiGhe {

    // Định nghĩa các hằng số màu sắc để dễ dàng thay đổi đồng bộ
    public static final Color MAU_DANG_CHON = new Color(0, 123, 255); // Xanh dương
    public static final Color MAU_DA_DAT = Color.BLACK;                // Đen
    public static final Color MAU_TRONG = Color.LIGHT_GRAY;           // Xám nhạt
    public static final Color MAU_CHU_TRANG = Color.WHITE;
    public static final Color MAU_CHU_DEN = Color.BLACK;

    /**
     * Cập nhật màu sắc và trạng thái của nút ghế dựa trên tình trạng hiện tại
     * * @param btnCho Nút JButton đại diện cho ghế
     * @param cho Thực thể ChoDat (chứa thông tin đã đặt hay chưa)
     * @param isSelected Trạng thái ghế có đang nằm trong danh sách đang chọn hay không
     */
    public void thietLapTrangThai(JButton btnCho, ChoDat cho, boolean isSelected) {
        if (cho.isDaDat()) {
            // Trường hợp ghế đã được bán trong CSDL
            btnCho.setBackground(MAU_DA_DAT);
            btnCho.setForeground(MAU_CHU_TRANG);
            btnCho.setEnabled(false);
            btnCho.setToolTipText("Ghế đã được bán (Không thể chọn)");
        } else if (isSelected) {
            // Trường hợp ghế đang được người dùng nhấn chọn (tạm thời)
            btnCho.setBackground(MAU_DANG_CHON);
            btnCho.setForeground(MAU_CHU_TRANG);
            btnCho.setEnabled(true);
            btnCho.setToolTipText("Ghế đang được bạn chọn");
        } else {
            // Trường hợp ghế còn trống
            btnCho.setBackground(MAU_TRONG);
            btnCho.setForeground(MAU_CHU_DEN);
            btnCho.setEnabled(true);
            btnCho.setToolTipText("Ghế còn trống - Nhấn để chọn");
        }
    }

    /**
     * Phương thức đổi màu nhanh khi click (Dành cho Logic xử lý sự kiện)
     */
    public void doiMauKhiClick(JButton btnCho, boolean isSelectedNow) {
        if (isSelectedNow) {
            btnCho.setBackground(MAU_DANG_CHON);
            btnCho.setForeground(MAU_CHU_TRANG);
        } else {
            btnCho.setBackground(MAU_TRONG);
            btnCho.setForeground(MAU_CHU_DEN);
        }
    }
}