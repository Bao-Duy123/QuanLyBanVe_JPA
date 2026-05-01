package JDBC_Project.gui.MainFrame;

import control.CaLamViec;
import entity.NhanVien;
import gui.Panel.*;
import gui.Panel.ManHinhQuanLyGiaVe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;

public class QuanLyDashboard extends JFrame implements ActionListener {

    // ... (Giữ nguyên các khai báo biến, màu sắc, constructor như cũ) ...
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel navPanel;
    private final Color MAU_CHINH = new Color(0, 123, 255);
    private final Color MAU_DUOC_CHON = new Color(74, 184, 237);
    private final Color MAU_HOVER = new Color(45, 150, 215);
    private final Map<String, JButton> menuButtons = new HashMap<>();
    private static final int CHIEU_RONG_MENU = 220;
    private static final int ICON_SIZE = 20;

    private JButton btnTrangChu, btnQLChuyenTau, btnQLNV, btnQLKhuyenMai, btnDangXuat;
    private JButton btnQLGiaVe, btnTraCuuHD, btnThongKe;

    private String maNVHienThi = "N/A";
    private String tenNVHienThi = "Đang tải...";

    public QuanLyDashboard() {
        setTitle("Hệ thống Quản lý Vé Tàu");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        layThongTinNhanVien();
        navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);
        initContentPanel();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        switchToCard("trangChu");
        initEventHandlers();
        setVisible(true);
    }

    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Đảm bảo bạn đã có các lớp Panel này
        contentPanel.add(new ManHinhDashboardQuanLy(), "trangChu");
        contentPanel.add(new ManHinhQuanLyChuyenTau(), "qlChuyenTau");
        contentPanel.add(new ManHinhQuanLyNhanVien(), "qlNhanVien");
        contentPanel.add(new ManHinhQuanLyKhuyenMai(), "qlKhuyenMai");
        contentPanel.add(new ManHinhTraCuuHoaDon(), "traCuuHD");
        contentPanel.add(new ManHinhQuanLyGiaVe(), "qlGiaVe");
        contentPanel.add(new ManHinhDashboardQuanLy(), "thongKe"); // Tạm thời dùng chung Dashboard

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createNavPanel() {
        // ... (Giữ nguyên code tạo menu như phiên bản trước) ...
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setPreferredSize(new Dimension(CHIEU_RONG_MENU, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(MAU_CHINH);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel logoLabel = new JLabel("GA XE");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        panel.add(headerPanel);

        // Items
        panel.add(taoDuongKe());
        btnTrangChu = createNavItem("Dashboard", "/images/iconMenu/home.png", "trangChu");
        panel.add(btnTrangChu);
        panel.add(taoDuongKe());
        btnQLChuyenTau = createNavItem("QL chuyến tàu", "/images/iconMenu/chuyentau.png", "qlChuyenTau");
        panel.add(btnQLChuyenTau);
        panel.add(taoDuongKe());
        btnQLNV = createNavItem("QL tài khoản NV", "/images/iconMenu/nhanvien.png", "qlNhanVien");
        panel.add(btnQLNV);
        panel.add(taoDuongKe());
        btnQLGiaVe = createNavItem("QL giá vé", "/images/iconMenu/giave.png", "qlGiaVe");
        panel.add(btnQLGiaVe);
        panel.add(taoDuongKe());
        btnQLKhuyenMai = createNavItem("QL khuyến mãi", "/images/iconMenu/khuyenmai.png", "qlKhuyenMai");
        panel.add(btnQLKhuyenMai);
        panel.add(taoDuongKe());
        btnTraCuuHD = createNavItem("Tra cứu hóa đơn", "/images/iconMenu/tracuuhoadon.png", "traCuuHD");
        panel.add(btnTraCuuHD);
        panel.add(taoDuongKe());

        panel.add(Box.createVerticalGlue());
        panel.add(taoPanelThongTinNV());
        btnDangXuat = createNavItem("Đăng xuất", "/images/iconMenu/logout.png", "dangXuat");
        panel.add(btnDangXuat);

        return panel;
    }

    // ================================================================
    // PHƯƠNG THỨC BỊ THIẾU ĐÃ ĐƯỢC BỔ SUNG Ở ĐÂY
    // ================================================================
    private void highlightActiveButton(JButton active) {
        // Đặt lại màu cho tất cả các nút về màu gốc
        for (JButton button : menuButtons.values()) {
            if (button != null) {
                button.setBackground(MAU_CHINH);
            }
        }
        // Đổi màu nền cho nút đang được chọn
        if (active != null) {
            active.setBackground(MAU_DUOC_CHON);
        }
    }
    // ================================================================

    private JButton createNavItem(String text, String iconPath, String cardName) {
        JButton button = new JButton(text);
        ImageIcon icon = createIcon(iconPath);
        if (icon != null) {
            button.setIcon(icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(15);
        }
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(MAU_CHINH);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 15, 12, 15));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        menuButtons.put(cardName, button);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(MAU_CHINH)) {
                    button.setBackground(MAU_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(MAU_HOVER) && !menuButtons.get(cardName).getBackground().equals(MAU_DUOC_CHON)) {
                    button.setBackground(MAU_CHINH);
                }
            }
        });
        return button;
    }

    private ImageIcon createIcon(String path) {
        URL imageUrl = getClass().getResource(path);
        if (imageUrl == null) return null;
        return new ImageIcon(new ImageIcon(imageUrl).getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
    }

    private JPanel taoPanelThongTinNV() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel nhanTenNV = new JLabel("NV: " + tenNVHienThi);
        nhanTenNV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nhanTenNV.setForeground(Color.WHITE);
        nhanTenNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanMaNV = new JLabel("ID: " + maNVHienThi);
        nhanMaNV.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nhanMaNV.setForeground(new Color(220, 220, 220));
        nhanMaNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(nhanTenNV);
        panel.add(nhanMaNV);
        panel.add(taoDuongKe());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private JSeparator taoDuongKe() {
        JSeparator duongKe = new JSeparator(SwingConstants.HORIZONTAL);
        duongKe.setForeground(new Color(255, 255, 255, 50));
        duongKe.setBackground(MAU_CHINH);
        duongKe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return duongKe;
    }

    private void layThongTinNhanVien() {
        try {
            NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
            if (nv != null) {
                this.maNVHienThi = nv.getMaNV();
                this.tenNVHienThi = nv.getHoTen();
            }
        } catch (Exception e) {}
    }

    private void initEventHandlers() {
        for (JButton button : menuButtons.values()) {
            button.addActionListener(this);
        }
    }

    public void switchToCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
        highlightActiveButton(menuButtons.get(cardName));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        String cardName = null;
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            if (entry.getValue() == src) {
                cardName = entry.getKey();
                break;
            }
        }
        if ("dangXuat".equals(cardName)) {
            if (JOptionPane.showConfirmDialog(this, "Đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                this.dispose();
            }
            return;
        }
        if (cardName != null) switchToCard(cardName);
    }

    public static void main(String[] args) {
        try{ UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception e){}
        SwingUtilities.invokeLater(() -> new QuanLyDashboard());
    }
}