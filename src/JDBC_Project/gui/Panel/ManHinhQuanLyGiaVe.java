package JDBC_Project.gui.Panel;

import gui.Panel.ManHinhTab.ManHinhQuanLyDonGiaTuyen;
import gui.Panel.ManHinhTab.ManHinhQuanLyLoaiToa;
import gui.Panel.ManHinhTab.ManHinhQuanLyLoaiVe;

import javax.swing.*;
import java.awt.*;

public class ManHinhQuanLyGiaVe extends JPanel {
    private JTabbedPane tabbedPane;

    public ManHinhQuanLyGiaVe() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // 1. Tab Quản lý Loại Vé (Code bạn đã có)
        tabbedPane.addTab("Chính sách Giảm giá", new ManHinhQuanLyLoaiVe());

        // 2. Tab Quản lý Đơn giá Tuyến (Cái mình vừa gợi ý ở câu trước)
        tabbedPane.addTab("Đơn giá KM theo Tuyến", new ManHinhQuanLyDonGiaTuyen());

        // 3. Tab Hệ số Loại chỗ (Bạn có thể phát triển thêm tương tự)
        tabbedPane.addTab("Hệ số Loại chỗ (Ghế/Giường)", new ManHinhQuanLyLoaiToa());

        
        add(tabbedPane, BorderLayout.CENTER);

        // Tiêu đề phía trên
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN TRỊ THÔNG SỐ GIÁ VÉ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitle, BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hệ Thống Quản Lý Giá Vé");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Sử dụng TabbedPane để chứa 2 khu vực
            JTabbedPane tabbedPane = new JTabbedPane();

            // Tab 1: Quản lý loại vé (Code cũ của bạn)
            tabbedPane.addTab("Cấu hình Loại Vé", new ManHinhQuanLyLoaiVe());

            // Tab 2: Quản lý đơn giá theo tuyến (Code mới)
            tabbedPane.addTab("Đơn giá theo Tuyến", new ManHinhQuanLyDonGiaTuyen());

            frame.add(tabbedPane);
            frame.setSize(1000, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}