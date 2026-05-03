package JPA_Project.gui;

import javax.swing.*;
import java.awt.*;

public class ManHinhQuanLyGiaVeJPA extends JPanel {

    private LoaiVeJPAPanel loaiVePanel;
    private LoaiToaJPAPanel loaiToaPanel;
    private DonGiaTuyenJPAPanel donGiaTuyenPanel;

    public ManHinhQuanLyGiaVeJPA() {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Chính sách Giảm giá (Loại Vé)
        loaiVePanel = new LoaiVeJPAPanel();
        tabbedPane.addTab("Chính sách Giảm giá", loaiVePanel);

        // Tab 2: Đơn giá KM theo Tuyến
        donGiaTuyenPanel = new DonGiaTuyenJPAPanel();
        tabbedPane.addTab("Đơn giá KM theo Tuyến", donGiaTuyenPanel);

        // Tab 3: Hệ số Loại chỗ (Ghế/Giường)
        loaiToaPanel = new LoaiToaJPAPanel();
        tabbedPane.addTab("Hệ số Loại chỗ (Ghế/Giường)", loaiToaPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Tiêu đề phía trên
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN TRỊ THÔNG SỐ GIÁ VÉ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitle, BorderLayout.NORTH);
    }

    public void shutdown() {
        if (loaiVePanel != null) loaiVePanel.shutdown();
        if (loaiToaPanel != null) loaiToaPanel.shutdown();
        if (donGiaTuyenPanel != null) donGiaTuyenPanel.shutdown();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hệ Thống Quản Lý Giá Vé - JPA");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ManHinhQuanLyGiaVeJPA panel = new ManHinhQuanLyGiaVeJPA();
            frame.setContentPane(panel);

            frame.setSize(1200, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    panel.shutdown();
                }
            });
        });
    }
}
