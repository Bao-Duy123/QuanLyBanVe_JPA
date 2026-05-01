package JDBC_Project.gui.MainFrame;

// Import các Panel cần hiển thị
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

/**
 * Lớp này tạo MainFrame cho quyền Admin (full quyền),
 * kết hợp tất cả các chức năng Quản Lý và Bán Vé.
 */
public class AdminFullDashboard extends JFrame implements ActionListener {

    // =================================================================================
    // HẰNG SỐ VÀ KHAI BÁO
    // =================================================================================
    private CardLayout boCucCard;
    private JPanel panelNoiDung;
    private JScrollPane navScrollPane; // Thay thế navPanel bằng JScrollPane
    private JPanel navContentPanel; // Panel chứa nội dung menu

    // Màu sắc
    private final Color MAU_CHINH = new Color(0, 123, 255);
    private final Color MAU_DUOC_CHON = new Color(74, 184, 237);
    private final Color MAU_HOVER = new Color(45, 150, 215);

    private final Map<String, JButton> nutMenu = new HashMap<>();
    private static final int CHIEU_RONG_MENU = 220; // Tăng chiều rộng để dễ nhìn và có chỗ cho thanh cuộn
    private static final int ICON_SIZE = 20;

    // Khai báo TẤT CẢ các nút từ 2 Dashboard
    private JButton btnTrangChu, btnDangXuat;

    // Chức năng Nhân Viên Bán Vé
    private JButton btnMoCa, btnKetCa, btnBanVe, btnDoiVe, btnTraVe, btnTraCuuVe;

    // Chức năng Quản Lý
    private JButton btnQLChuyenTau, btnQLNV, btnQLKhuyenMai, btnQLGiaVe, btnThongKe, btnTraCuuHD;

    // Dữ liệu Nhân viên
    private String maNVHienThi = "N/A";
    private String tenNVHienThi = "Đang tải...";

    // Instance của ManHinhBanVe (nếu cần truy cập lại)
    public ManHinhBanVe manHinhBanVeInstance;


    public AdminFullDashboard() {
        setTitle("Hệ thống Quản lý Vé Tàu - ADMIN TỔNG");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        layThongTinNhanVien();

        // 1. Panel Menu bên trái (Bây giờ là JScrollPane)
        navScrollPane = createNavPanelWithScroll();
        add(navScrollPane, BorderLayout.WEST);

        // 2. Panel nội dung (CardLayout)
        initContentPanel();
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Phóng to tối đa

        // Mặc định hiển thị màn hình Trang Chủ
        switchToCard("trangChuQL");

        // Thiết lập sự kiện cho các nút menu
        initEventHandlers();
        setVisible(true);
    }

    // =================================================================================
    // CÁC PHƯƠNG THỨC HELPER
    // =================================================================================

    private void layThongTinNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv != null) {
            this.maNVHienThi = nv.getMaNV();
            this.tenNVHienThi = nv.getHoTen();
        } else {
            this.maNVHienThi = "ADMIN";
            this.tenNVHienThi = "Toàn Quyền";
        }
    }
//    @Override // Ghi đè từ CardSwitchable
//    public void chuyenManHinh(String tenCard) {
//        cardLayout.show(panelNoiDung, tenCard);
//        highlightActiveButton(menuButtons.get(tenCard));
//    }

    /**
     * Helper: Tải, điều chỉnh kích thước và trả về ImageIcon.
     */
    private ImageIcon createIcon(String path) {
        // Tải icon từ class path.
        URL imageUrl = getClass().getResource(path);
        if (imageUrl == null) {
            System.err.println("Không tìm thấy tài nguyên icon: " + path);
            return null;
        }
        try {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải hoặc điều chỉnh icon: " + path + " - " + e.getMessage());
            return null;
        }
    }

    // =================================================================================
    // KHU VỰC MENU (CÓ THANH CUỘN)
    // =================================================================================

    /**
     * Tạo JScrollPane chứa Panel Menu
     */
    private JScrollPane createNavPanelWithScroll() {
        navContentPanel = new JPanel();
        navContentPanel.setLayout(new BoxLayout(navContentPanel, BoxLayout.Y_AXIS));
        navContentPanel.setBackground(MAU_CHINH);
        navContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Border cho nội dung

        // --- Phần Header (Logo và ID) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(MAU_CHINH);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoLabel = new JLabel("HỆ THỐNG GA XE");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        navContentPanel.add(headerPanel);

        // --- MỤC: CHỨC NĂNG CHUNG ---
        navContentPanel.add(taoTieuDeMenu("TỔNG QUAN"));
        btnTrangChu = createNavItem("Dashboard", "/images/iconMenu/home.png", "trangChuQL");
        navContentPanel.add(btnTrangChu);
        navContentPanel.add(taoDuongKe());

        // --- MỤC: CHỨC NĂNG BÁN VÉ/CA ---



        btnBanVe = createNavItem("Bán vé mới", "/images/iconMenu/banve.png", "banVeMoi");
        navContentPanel.add(btnBanVe);
        btnDoiVe = createNavItem("Đổi vé", "/images/iconMenu/doive.png", "doiVe");
        navContentPanel.add(btnDoiVe);
        btnTraVe = createNavItem("Trả vé", "/images/iconMenu/trave.png", "traVe");
        navContentPanel.add(btnTraVe);
        navContentPanel.add(taoDuongKe());

        btnTraCuuVe = createNavItem("Tra cứu vé", "/images/iconMenu/tracuu.png", "traCuuVe");
        navContentPanel.add(btnTraCuuVe);
        btnTraCuuHD = createNavItem("Tra cứu HĐ", "/images/iconMenu/tracuuhoadon.png", "traCuuHD");
        navContentPanel.add(btnTraCuuHD);
        navContentPanel.add(taoDuongKe());
        // --- MỤC: CHỨC NĂNG QUẢN LÝ ---

        btnQLChuyenTau = createNavItem("QL Chuyến tàu", "/images/iconMenu/chuyentau.png", "qlChuyenTau");
        navContentPanel.add(btnQLChuyenTau);

        btnQLNV = createNavItem("QL Tài khoản NV", "/images/iconMenu/nhanvien.png", "qlNhanVien");
        navContentPanel.add(btnQLNV);

        btnQLGiaVe = createNavItem("QL Giá vé", "/images/iconMenu/giave.png", "qlGiaVe");
        navContentPanel.add(btnQLGiaVe);

        btnQLKhuyenMai = createNavItem("QL Khuyến mãi", "/images/iconMenu/khuyenmai.png", "qlKhuyenMai");
        navContentPanel.add(btnQLKhuyenMai);
        navContentPanel.add(taoDuongKe());


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(MAU_CHINH);
        bottomPanel.add(Box.createVerticalGlue());
        bottomPanel.add(taoPanelThongTinNV());

        // --- Nút Đăng xuất ---
        btnDangXuat = createNavItem("Đăng xuất", "/images/iconMenu/logout.png", "dangXuat");
        bottomPanel.add(btnDangXuat);
        bottomPanel.setBorder(new EmptyBorder(0, 10, 10, 10)); // Border cho phần dưới cùng

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(navContentPanel, BorderLayout.NORTH);



        navContentPanel.add(Box.createVerticalGlue());
        // --- THÔNG TIN NV & Đăng xuất -
        navContentPanel.add(taoPanelThongTinNV());
        navContentPanel.add(btnDangXuat);
        navContentPanel.add(Box.createVerticalStrut(10));

        // Khởi tạo JScrollPane
        JScrollPane scrollPane = new JScrollPane(navContentPanel);
        scrollPane.setPreferredSize(new Dimension(CHIEU_RONG_MENU, 0));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Không cần thanh cuộn ngang
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Xóa border mặc định của JScrollPane
        scrollPane.getVerticalScrollBar().setUnitIncrement(10); // Tăng tốc độ cuộn

        return scrollPane;
    }


    private Component taoTieuDeMenu(String tieuDe) {
        JLabel label = new JLabel(tieuDe);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.decode("#FFFFFF"));
        label.setBorder(new EmptyBorder(15, 5, 5, 0)); // Giảm padding trái vì navContentPanel đã có 10
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel taoPanelThongTinNV() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(10, 5, 10, 5)); // Giảm padding trái vì navContentPanel đã có 10

        JLabel nhanTenNV = new JLabel("QUYỀN: **ADMIN**");
        nhanTenNV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nhanTenNV.setForeground(Color.WHITE);
        nhanTenNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanMaNV = new JLabel("ID: " + maNVHienThi + " (" + tenNVHienThi + ")");
        nhanMaNV.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        nhanMaNV.setForeground(Color.decode("#E0E0E0"));
        nhanMaNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(nhanTenNV);
        panel.add(nhanMaNV);
        panel.add(taoDuongKe());

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    /**
     * Phương thức tạo nút menu
     */
    private JButton createNavItem(String text, String iconPath, String cardName) {
        JButton button = new JButton(text);

        ImageIcon icon = createIcon(iconPath);
        if (icon != null) {
            button.setIcon(icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(10);
        }

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(MAU_CHINH);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.setBorder(new EmptyBorder(10, 15, 10, 15)); // Giữ padding nội bộ
        button.setOpaque(true);

        int fixedHeight = 45;
        Dimension itemSize = new Dimension(CHIEU_RONG_MENU, fixedHeight);

        // Đảm bảo nút chiếm toàn bộ chiều rộng có sẵn trong BoxLayout (trừ padding)
        button.setPreferredSize(itemSize);
        button.setMinimumSize(itemSize);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, fixedHeight));

        nutMenu.put(cardName, button);

        // Xử lý hiệu ứng hover/màu sắc
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Chỉ đổi màu hover nếu nút không phải là nút đang được chọn
                if (button.getBackground().equals(MAU_CHINH)) {
                    button.setBackground(MAU_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Chỉ đổi màu về MAU_CHINH nếu nó đang là MAU_HOVER và KHÔNG phải là nút đang được chọn
                if (button.getBackground().equals(MAU_HOVER)) {
                    // Kiểm tra lại màu nếu cần thiết, vì nó có thể đã bị đổi thành MAU_DUOC_CHON trong quá trình xử lý sự kiện khác
                    if (!nutMenu.get(cardName).getBackground().equals(MAU_DUOC_CHON)) {
                        button.setBackground(MAU_CHINH);
                    }
                }
            }
        });
        return button;
    }

    /**
     * Tạo gạch chân
     */
    private JSeparator taoDuongKe() {
        JSeparator duongKe = new JSeparator(SwingConstants.HORIZONTAL);
        duongKe.setForeground(new Color(255, 255, 255, 70));
        duongKe.setBackground(MAU_CHINH);
        duongKe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return duongKe;
    }


    // =================================================================================
    // KHU VỰC CONTENT PANEL & CARDLAYOUT
    // =================================================================================

    /**
     * Khởi tạo Panel chứa CardLayout với TẤT CẢ các Panel chức năng
     */
    private void initContentPanel() {
        boCucCard = new CardLayout();
        panelNoiDung = new JPanel(boCucCard);

        // --- Chức năng Quản Lý (từ QuanLyDashboard) ---
        panelNoiDung.add(new ManHinhDashboardQuanLy(), "trangChuQL"); // Đổi tên để tránh trùng với trang chủ NV
        panelNoiDung.add(new ManHinhQuanLyChuyenTau(), "qlChuyenTau");
        panelNoiDung.add(new ManHinhQuanLyNhanVien(), "qlNhanVien");
        panelNoiDung.add(new ManHinhQuanLyKhuyenMai(), "qlKhuyenMai");
        panelNoiDung.add(new ManHinhQuanLyGiaVe(), "qlGiaVe");
        panelNoiDung.add(new JPanel(), "thongKe");

        // --- Chức năng Bán Vé (từ BanVeDashboard) ---

        manHinhBanVeInstance = new ManHinhBanVe();
        panelNoiDung.add(manHinhBanVeInstance, "banVeMoi");

        panelNoiDung.add(new ManHinhDoiVe(), "doiVe");
        panelNoiDung.add(new ManHinhTraVe(), "traVe");
        panelNoiDung.add(new ManHinhTraCuuVe(), "traCuuVe");
        // Tra Cứu Hóa Đơn dùng chung
        panelNoiDung.add(new ManHinhTraCuuHoaDon(), "traCuuHD");


        add(panelNoiDung, BorderLayout.CENTER);
    }

    /**
     * Thiết lập Action Listener cho tất cả các nút menu
     */
    private void initEventHandlers() {
        for (JButton button : nutMenu.values()) {
            button.addActionListener(this);
        }
    }

    /**
     * Chuyển đổi màn hình trong CardLayout và highlight nút menu tương ứng
     */
    public void switchToCard(String cardName) {
        boCucCard.show(panelNoiDung, cardName);
        danhDauNutDangChon(nutMenu.get(cardName));
    }

    /**
     * Đổi màu nền của nút menu đang được chọn
     */
    private void danhDauNutDangChon(JButton active) {
        for (JButton button : nutMenu.values()) {
            if (button != null) {
                button.setBackground(MAU_CHINH);
            }
        }
        if (active != null) {
            active.setBackground(MAU_DUOC_CHON);
        }
    }

    // =================================================================================
    // XỬ LÝ SỰ KIỆN CHUNG
    // =================================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        String cardName = null;

        // Tìm tên card dựa trên nút được click
        for (Map.Entry<String, JButton> entry : nutMenu.entrySet()) {
            if (entry.getValue() == src) {
                cardName = entry.getKey();
                break;
            }
        }

        if ("dangXuat".equals(cardName)) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất Admin?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                CaLamViec.getInstance().ketThucCa(); // Dù là Admin vẫn nên gọi hàm kết thúc ca
                this.dispose();
                // TODO: Thêm logic chuyển về màn hình đăng nhập
            }
            return;
        }

        // Thêm xử lý đặc biệt cho Mở Ca/Kết Ca nếu cần
        if ("moCa".equals(cardName)) {
            // Logic riêng cho Mở ca
        }
        if ("ketCa".equals(cardName)) {
            // Logic riêng cho Kết ca
        }

        if (cardName != null) {
            switchToCard(cardName);
        }
    }

    public void themHoacCapNhatCard(JPanel panelMoi, String tenCard) {
        Component thanhPhanCu = null;

        for (Component comp : panelNoiDung.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(tenCard)) {
                thanhPhanCu = comp;
                break;
            }
        }
        if (thanhPhanCu != null) {
            panelNoiDung.remove(thanhPhanCu);
        }
        panelMoi.setName(tenCard);
        panelNoiDung.add(panelMoi, tenCard);

        panelNoiDung.revalidate();
        panelNoiDung.repaint();
    }
    public void chuyenManHinh(String tenCard) {
        boCucCard.show(panelNoiDung, tenCard);
        danhDauNutDangChon(nutMenu.get(tenCard));
    }



    // =================================================================================
    // MAIN
    // =================================================================================
    public static void main(String[] args) {

        try {
            // Set Metal Look and Feel
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            System.err.println("Không set được Metal LAF: " + e.getMessage());
        }

        try {
            // MOCKUP NhanVien Admin
            NhanVien nvMock = new NhanVien("ADMIN001", "Admin Tổng", "0999888777");
            CaLamViec.getInstance().batDauCa(nvMock);
        } catch (Exception e) {
            System.err.println("Lỗi MOCKUP NhanVien/CaLamViec: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new AdminFullDashboard();
        });
    }
}