package JDBC_Project.gui.MainFrame;

import control.CaLamViec;
import entity.NhanVien;
import gui.Panel.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI; // Import cần thiết để mở web
import java.util.HashMap;
import java.util.Map;
import java.net.URL;

/**
 * Lớp này tạo ManFrame cho quyền Nhân viên Bán Vé, chứa Menu cố định và CardLayout.
 */
public class BanVeDashboard extends JFrame implements ActionListener {

    // --- HẰNG SỐ VÀ KHAI BÁO VIỆT HÓA ---
    private CardLayout boCucCard;
    private JPanel panelNoiDung;
    private final Color MAU_CHINH = new Color(0, 123, 255); // Màu xanh
    private final Color MAU_DUOC_CHON = new Color(74, 184, 237); // Màu xanh sáng
    private final Color MAU_HOVER = new Color(45, 150, 215);
    private final Map<String, JButton> nutMenu = new HashMap<>();

    // Các nút menu cần quản lý
    private JButton nutTrangChu, nutMoCa, nutKetCa, nutBanVe, nutDoiVe, nutTraCuuVe, nutTraCuuHD, nutDangXuat, nutTraVe;

    // [THÊM MỚI] Nút Trợ giúp
    private JButton nutTroGiup;

    // Dữ liệu Nhân viên
    private String maNVHienThi = "N/A";
    private String tenNVHienThi = "Đang tải...";

    // Hằng số cho chiều rộng menu
    private static final int CHIEU_RONG_MENU = 220;
    // Hằng số cho kích thước icon
    private static final int ICON_SIZE = 20;

    // Đường dẫn đến trang hướng dẫn (GitHub Pages)
    private static final String LINK_HUONG_DAN = "https://transon-code.github.io/TrainTicketManagement_Guide/html/NhanVienBanVe/DangNhap.html";

    public ManHinhBanVe manHinhBanVeInstance;

    public static BanVeDashboard instance;

    public BanVeDashboard() {
        instance= this;
        setTitle("Hệ thống Bán Vé Tàu - Nhân viên Bán Vé");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        layThongTinNhanVien();

        JPanel panelDieuHuong = taoPanelDieuHuong();
        add(panelDieuHuong, BorderLayout.WEST);

        khoiTaoPanelNoiDung();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        chuyenManHinh("trangChuNV");
        dangKiSuKien();
        setVisible(true);
    }

    private void layThongTinNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv != null) {
            this.maNVHienThi = nv.getMaNV();
            this.tenNVHienThi = nv.getHoTen();
        } else {
            this.maNVHienThi = "Lỗi Phiên";
            this.tenNVHienThi = "Không tìm thấy";
        }
    }

    /**
     * Helper: Tải, điều chỉnh kích thước và trả về ImageIcon.
     * @param path Đường dẫn tương đối từ gốc classpath (VD: "/images/home.png")
     * @return ImageIcon đã resize, hoặc null nếu lỗi.
     */
    private ImageIcon TaoIcon(String path) {
        URL imageUrl = getClass().getResource(path);
        if (imageUrl == null) {
            System.err.println("Không tìm thấy tài nguyên icon: " + path);
            return null;
        }
        try {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image image = originalIcon.getImage();
            // Điều chỉnh kích thước
            Image scaledImage = image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải hoặc điều chỉnh icon: " + path + " - " + e.getMessage());
            return null;
        }
    }


    private JPanel taoPanelDieuHuong() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setPreferredSize(new Dimension(CHIEU_RONG_MENU, 0));
        panel.setBorder(new EmptyBorder(10, 5, 0, 5)); // Padding bên trong panel

        // --- Phần Header (Logo và ID) ---
        JPanel panelTieuDe = new JPanel();
        panelTieuDe.setLayout(new BoxLayout(panelTieuDe, BoxLayout.Y_AXIS));
        panelTieuDe.setBackground(MAU_CHINH);
        panelTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanLogo = new JLabel("GA XE");
        nhanLogo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        nhanLogo.setForeground(Color.WHITE);
        nhanLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelTieuDe.add(nhanLogo);
        panel.add(panelTieuDe);

        // --- Phần các mục menu ---

        panel.add(taoDuongKe());
        // [1. Trang chủ]
        nutTrangChu = taoMucMenu("Dashboard", "/images/iconMenu/home.png", "trangChuNV");
        panel.add(nutTrangChu);
        panel.add(taoDuongKe());

        // [4. Bán vé mới]
        nutBanVe = taoMucMenu("Bán vé", "/images/iconMenu/banve.png", "banVeMoi");
        panel.add(nutBanVe);
        panel.add(taoDuongKe());

        // [5. Đổi vé]
        nutDoiVe = taoMucMenu("Đổi vé", "/images/iconMenu/doive.png", "doiVe");
        panel.add(nutDoiVe);
        panel.add(taoDuongKe());

        // [5.1. Trả vé]
        nutTraVe = taoMucMenu("Trả vé", "/images/iconMenu/trave.png", "traVe");
        panel.add(nutTraVe);
        panel.add(taoDuongKe());

        // [6. Tra cứu vé]
        nutTraCuuVe = taoMucMenu("Tra cứu vé", "/images/iconMenu/tracuu.png", "traCuuVe");
        panel.add(nutTraCuuVe);
        panel.add(taoDuongKe());

        // [7. Tra cứu hóa đơn]
        nutTraCuuHD = taoMucMenu("Tra cứu hóa đơn", "/images/iconMenu/tracuuhoadon.png", "traCuuHD");
        panel.add(nutTraCuuHD);
        panel.add(taoDuongKe());

        // ====================================================================
        // [8. THÊM MỚI] NÚT TRỢ GIÚP
        // Lưu ý: Hãy đảm bảo bạn có file icon "help.png" trong thư mục images/iconMenu
        // ====================================================================
        nutTroGiup = taoMucMenu("Hướng dẫn sử dụng", "/images/iconMenu/help.png", "troGiup");
        panel.add(nutTroGiup);
        panel.add(taoDuongKe());


        panel.add(Box.createVerticalGlue());

        // --- THÔNG TIN NV ---
        panel.add(taoPanelThongTinNV());

        // Nút Đăng xuất
        nutDangXuat = taoMucMenu("Đăng xuất", "/images/iconMenu/logout.png", "dangXuat");
        panel.add(nutDangXuat);

        return panel;
    }

    private JPanel taoPanelThongTinNV() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(10, 10, 10, 15)); // Padding bên trong panel

        JLabel nhanTenNV = new JLabel("**" + tenNVHienThi + "**");
        nhanTenNV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nhanTenNV.setForeground(Color.WHITE);
        nhanTenNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanMaNV = new JLabel("ID: " + maNVHienThi);
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
     * Phương thức tạo nút menu sử dụng ImageIcon.
     * @param vanBan Văn bản của nút (VD: "Dashboard")
     * @param iconPath Đường dẫn tương đối đến icon (VD: "/images/home.png")
     * @param tenCard Tên card trong CardLayout
     */
    private JButton taoMucMenu(String vanBan, String iconPath, String tenCard) {
        JButton nut = new JButton(vanBan);

        // Tải icon bằng phương thức helper
        ImageIcon icon = TaoIcon(iconPath);
        if (icon != null) {
            nut.setIcon(icon);
            // Đặt vị trí của icon so với văn bản
            nut.setHorizontalTextPosition(SwingConstants.RIGHT);
            nut.setIconTextGap(10); // Khoảng cách giữa icon và text
        }

        // Thiết lập font
        nut.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nut.setForeground(Color.WHITE);
        nut.setBackground(MAU_CHINH);
        nut.setFocusPainted(false);
        nut.setHorizontalAlignment(SwingConstants.LEFT);

        nut.setBorder(new EmptyBorder(10, 10, 10, 15)); // Padding bên trong nút
        nut.setOpaque(true);

        int chieuCaoCoDinh = 45;
        Dimension kichThuocBuoc = new Dimension(CHIEU_RONG_MENU, chieuCaoCoDinh);

        nut.setPreferredSize(kichThuocBuoc);
        nut.setMinimumSize(kichThuocBuoc);
        nut.setMaximumSize(new Dimension(Integer.MAX_VALUE, chieuCaoCoDinh));

        nutMenu.put(tenCard, nut);

        // Xử lý hiệu ứng hover/màu sắc
        nut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (nut.getBackground().equals(MAU_CHINH)) {
                    nut.setBackground(MAU_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (nut.getBackground().equals(MAU_HOVER)) {
                    nut.setBackground(MAU_CHINH);
                }
            }
        });
        return nut;
    }

    private JSeparator taoDuongKe() {
        JSeparator duongKe = new JSeparator(SwingConstants.HORIZONTAL);
        duongKe.setForeground(new Color(255, 255, 255, 70));
        duongKe.setBackground(MAU_CHINH);
        duongKe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return duongKe;
    }


    // KHU VỰC CONTENT PANEL & CARDLAYOUT (Giữ nguyên)
    private void khoiTaoPanelNoiDung() {
        boCucCard = new CardLayout();
        panelNoiDung = new JPanel(boCucCard);

        panelNoiDung.add(new ManHinhTrangChuNVBanVe(this), "trangChuNV");

        manHinhBanVeInstance = new ManHinhBanVe();
        panelNoiDung.add(manHinhBanVeInstance, "banVeMoi");

        panelNoiDung.add(new ManHinhDoiVe(), "doiVe");
        panelNoiDung.add(new ManHinhTraVe(), "traVe");
        panelNoiDung.add(new ManHinhTraCuuVe(), "traCuuVe");
        panelNoiDung.add(new ManHinhTraCuuHoaDon(), "traCuuHD");

        add(panelNoiDung, BorderLayout.CENTER);
    }

    private void dangKiSuKien() {
        for (JButton button : nutMenu.values()) {
            button.addActionListener(this);
        }
    }

    public void chuyenManHinh(String tenCard) {
        boCucCard.show(panelNoiDung, tenCard);
        danhDauNutDangChon(nutMenu.get(tenCard));
    }

    private void danhDauNutDangChon(JButton nutHoatDong) {
        for (JButton nut : nutMenu.values()) {
            if (nut != null) {
                nut.setBackground(MAU_CHINH);
            }
        }
        if (nutHoatDong != null) {
            nutHoatDong.setBackground(MAU_DUOC_CHON);
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


    // XỬ LÝ SỰ KIỆN CHUNG
    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();

        String tenCard = nutMenu.entrySet().stream()
                .filter(entry -> entry.getValue() == nguon)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        // --- [XỬ LÝ NÚT TRỢ GIÚP] ---
        if ("troGiup".equals(tenCard)) {
            // Mở trình duyệt web và dừng lại (không chuyển Card)
            moTrinhDuyet(LINK_HUONG_DAN);
            return;
        }
        // -----------------------------

        if ("dangXuat".equals(tenCard)) {
            int xacNhan = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (xacNhan == JOptionPane.YES_OPTION) {
                CaLamViec.getInstance().ketThucCa();
                this.dispose();
            }
            return;
        }

        if (tenCard != null) {
            chuyenManHinh(tenCard);
        }
    }

    // --- [HÀM MỚI] MỞ TRÌNH DUYỆT ---
    private void moTrinhDuyet(String urlString) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(urlString));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Máy tính không hỗ trợ tự động mở trình duyệt.\nVui lòng truy cập: " + urlString,
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể mở đường dẫn: " + e.getMessage());
        }
    }

    // MAIN
    // HÀM MAIN ÉP SANG GIAO DIỆN METAL CHO MACOS (để dùng cho cả win và mac)
    public static void main(String[] args) {
        // 1. Phần Mockup dữ liệu (Giữ nguyên)
        try {
            NhanVien nvMock = new NhanVien("NVBV0001", "Trần Đức Nam","0123456789");
            CaLamViec.getInstance().batDauCa(nvMock);
        } catch (Exception e) {
            System.err.println("Lỗi MOCKUP NhanVien/CaLamViec: " + e.getMessage());
        }

        // 2. Cài đặt giao diện METAL (Cross-platform)
        try {
            // Đây là dòng lệnh gọi giao diện Metal chuẩn
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ex) {
            System.err.println("Lỗi cài đặt giao diện.");
        }

        // 3. Khởi chạy giao diện
        SwingUtilities.invokeLater(() -> {
            new BanVeDashboard();
        });
    }

    public Component layCardTheoTen(String tenCard) {
        for (Component comp : panelNoiDung.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(tenCard)) {
                return comp;
            }
        }
        return null;
    }
}