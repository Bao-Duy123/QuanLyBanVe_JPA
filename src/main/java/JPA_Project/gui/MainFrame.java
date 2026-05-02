package JPA_Project.gui;

import JPA_Project.entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * MainFrame - Hệ thống menu phân quyền:
 * - Nhân viên bán vé (NV): Dashboard bán vé, Bán vé, Đổi vé, Trả vé, Tra cứu vé, Hóa đơn
 * - Quản lý (QL): Dashboard quản lý, QL chuyến tàu, QL nhân viên, QL giá vé, Thống kê
 */
public class MainFrame extends JFrame implements ActionListener {

    private CardLayout cardLayout;
    private JPanel panelNoiDung;
    private final NhanVien nhanVien;
    private final String chucVu;
    private final boolean isQuanLy;

    private static final Color MAU_CHINH = new Color(41, 128, 185);
    private static final Color MAU_DUOC_CHON = new Color(52, 152, 219);
    private static final Color MAU_HOVER = new Color(33, 110, 160);
    private static final Map<String, JButton> nutMenu = new HashMap<>();

    private static final int CHIEU_RONG_MENU = 220;

    public MainFrame(NhanVien nhanVien, String chucVu) {
        this.nhanVien = nhanVien;
        this.chucVu = chucVu;
        
        // Kiểm tra quyền: Quản lý nếu chucVu chứa "QUAN_LY", "QL", "ADMIN" hoặc mã NV bắt đầu bằng "NVQL", "NVTP"
        this.isQuanLy = isQuanLyRole(nhanVien, chucVu);

        setTitle("Hệ thống Bán Vé Tàu - " + nhanVien.getHoTen());
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelDieuHuong = taoPanelDieuHuong();
        add(panelDieuHuong, BorderLayout.WEST);

        khoiTaoPanelNoiDung();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Chuyển đến dashboard phù hợp với vai trò
        chuyenManHinh("trangChu");
        setVisible(true);
    }

    /**
     * Kiểm tra xem tài khoản có phải là Quản lý hay không
     */
    private boolean isQuanLyRole(NhanVien nv, String chucVu) {
        if (chucVu == null) return false;
        
        String upperChucVu = chucVu.toUpperCase();
        
        // ADMIN luôn là quản lý
        if ("ADMIN".equals(upperChucVu)) return true;
        
        // QUAN_LY hoặc QUẢN LÝ
        if (upperChucVu.contains("QUAN_LY") || upperChucVu.contains("QUẢN LÝ")) return true;
        
        // Kiểm tra mã NV bắt đầu bằng NVQL, NVTP
        if (nv != null && nv.getMaNV() != null) {
            String maNV = nv.getMaNV().toUpperCase();
            if (maNV.startsWith("NVQL") || maNV.startsWith("NVTP")) return true;
        }
        
        return false;
    }

    private JPanel taoPanelDieuHuong() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setPreferredSize(new Dimension(CHIEU_RONG_MENU, 0));
        panel.setBorder(new EmptyBorder(10, 5, 0, 5));

        // Logo
        JPanel panelTieuDe = new JPanel();
        panelTieuDe.setLayout(new BoxLayout(panelTieuDe, BoxLayout.Y_AXIS));
        panelTieuDe.setBackground(MAU_CHINH);
        panelTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanLogo = new JLabel("GA XE");
        nhanLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        nhanLogo.setForeground(Color.WHITE);
        nhanLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelTieuDe.add(nhanLogo);

        panel.add(panelTieuDe);
        panel.add(taoDuongKe());

        // ======= MENU DASHBOARD =======
        JButton nutTrangChu;
        if (isQuanLy) {
            nutTrangChu = taoMucMenu("Dashboard", "trangChu");
        } else {
            nutTrangChu = taoMucMenu("Dashboard", "trangChu");
        }
        panel.add(nutTrangChu);
        panel.add(taoDuongKe());

        if (isQuanLy) {
            // ======= MENU QUẢN LÝ =======
            
            // Dashboard (QuanLyDashboardPanel)
            panel.add(taoDuongKe());

            // QL Chuyến tàu
            JButton nutQLChuyenTau = taoMucMenu("QL Chuyến tàu", "qlChuyenTau");
            panel.add(nutQLChuyenTau);
            panel.add(taoDuongKe());

            // QL Nhân viên
            JButton nutQLNhanVien = taoMucMenu("QL Nhân viên", "quanLyNhanVien");
            panel.add(nutQLNhanVien);
            panel.add(taoDuongKe());

            // QL Giá vé
            JButton nutQLGiaVe = taoMucMenu("QL Giá vé", "qlGiaVe");
            panel.add(nutQLGiaVe);
            panel.add(taoDuongKe());

            // QL Khuyến mãi
            JButton nutQLKhuyenMai = taoMucMenu("QL Khuyến mãi", "qlKhuyenMai");
            panel.add(nutQLKhuyenMai);
            panel.add(taoDuongKe());

            // Tra cứu hóa đơn
            JButton nutTraCuuHD = taoMucMenu("Tra cứu HĐ", "traCuuHD");
            panel.add(nutTraCuuHD);
            panel.add(taoDuongKe());

            // Thống kê
            JButton nutThongKe = taoMucMenu("Thống kê", "thongKe");
            panel.add(nutThongKe);
            panel.add(taoDuongKe());

        } else {
            // ======= MENU BÁN VÉ =======
            
            // Bán vé
            JButton nutBanVe = taoMucMenu("Bán vé", "banVe");
            panel.add(nutBanVe);
            panel.add(taoDuongKe());

            // Đổi vé
            JButton nutDoiVe = taoMucMenu("Đổi vé", "doiVe");
            panel.add(nutDoiVe);
            panel.add(taoDuongKe());

            // Trả vé
            JButton nutTraVe = taoMucMenu("Trả vé", "traVe");
            panel.add(nutTraVe);
            panel.add(taoDuongKe());

            // Tra cứu vé
            JButton nutTraCuuVe = taoMucMenu("Tra cứu vé", "traCuuVe");
            panel.add(nutTraCuuVe);
            panel.add(taoDuongKe());

            // Tra cứu hóa đơn
            JButton nutTraCuuHD = taoMucMenu("Tra cứu HĐ", "traCuuHD");
            panel.add(nutTraCuuHD);
            panel.add(taoDuongKe());
        }

        // ======= MENU CHUNG =======
        panel.add(Box.createVerticalGlue());

        // Hướng dẫn
        JButton nutTroGiup = taoMucMenu("Hướng dẫn", "troGiup");
        panel.add(nutTroGiup);
        panel.add(taoDuongKe());

        // Thông tin nhân viên
        panel.add(taoPanelThongTinNV());

        // Đăng xuất
        JButton nutDangXuat = taoMucMenu("Đăng xuất", "dangXuat");
        panel.add(nutDangXuat);

        return panel;
    }

    private JPanel taoPanelThongTinNV() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(10, 10, 10, 15));

        JLabel nhanTenNV = new JLabel(nhanVien.getHoTen());
        nhanTenNV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nhanTenNV.setForeground(Color.WHITE);
        nhanTenNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanMaNV = new JLabel("ID: " + nhanVien.getMaNV());
        nhanMaNV.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        nhanMaNV.setForeground(Color.decode("#E0E0E0"));
        nhanMaNV.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanChucVu = new JLabel("Chức vụ: " + getVietHoaChucVu(chucVu));
        nhanChucVu.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        nhanChucVu.setForeground(Color.decode("#E0E0E0"));
        nhanChucVu.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(nhanTenNV);
        panel.add(nhanMaNV);
        panel.add(nhanChucVu);
        panel.add(taoDuongKe());

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private String getVietHoaChucVu(String chucVu) {
        if (chucVu == null) return "Nhân viên";
        switch (chucVu.toUpperCase()) {
            case "ADMIN": return "Quản trị";
            case "QUAN_LY": return "Quản lý";
            case "NHAN_VIEN_BAN_VE": return "Nhân viên bán vé";
            default: return chucVu;
        }
    }

    private JButton taoMucMenu(String vanBan, String tenCard) {
        JButton nut = new JButton(vanBan);
        nut.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nut.setForeground(Color.WHITE);
        nut.setBackground(MAU_CHINH);
        nut.setOpaque(true);
        nut.setBorderPainted(false);
        nut.setFocusPainted(false);
        nut.setHorizontalAlignment(SwingConstants.LEFT);
        nut.setBorder(new EmptyBorder(12, 15, 12, 15));

        int chieuCaoCoDinh = 45;
        Dimension kichThuocBuoc = new Dimension(CHIEU_RONG_MENU, chieuCaoCoDinh);
        nut.setPreferredSize(kichThuocBuoc);
        nut.setMinimumSize(kichThuocBuoc);
        nut.setMaximumSize(new Dimension(Integer.MAX_VALUE, chieuCaoCoDinh));

        nutMenu.put(tenCard, nut);

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

        nut.addActionListener(this);
        return nut;
    }

    private JSeparator taoDuongKe() {
        JSeparator duongKe = new JSeparator(SwingConstants.HORIZONTAL);
        duongKe.setForeground(new Color(255, 255, 255, 70));
        duongKe.setBackground(MAU_CHINH);
        duongKe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return duongKe;
    }

    private void khoiTaoPanelNoiDung() {
        cardLayout = new CardLayout();
        panelNoiDung = new JPanel(cardLayout);

        if (isQuanLy) {
            // Dashboard cho Quản lý
            panelNoiDung.add(new QuanLyDashboardPanel(nhanVien), "trangChu");
            // QL Chuyến tàu
            panelNoiDung.add(new ManHinhQuanLyChuyenTauJPA(), "qlChuyenTau");
            // QL Nhân viên
            panelNoiDung.add(new ManHinhQuanLyNhanVienJPA(), "quanLyNhanVien");
            // QL Giá vé
            panelNoiDung.add(new ManHinhQuanLyGiaVeJPA(), "qlGiaVe");
            // QL Khuyến mãi
            panelNoiDung.add(new ManHinhQuanLyKhuyenMaiJPA(), "qlKhuyenMai");
            // Tra cứu hóa đơn
            panelNoiDung.add(new TraCuuPanel(), "traCuuHD");
            // Thống kê
            panelNoiDung.add(new ThongKePanel(), "thongKe");
        } else {
            // Dashboard cho Nhân viên bán vé
            panelNoiDung.add(new BanVeDashboardPanel(nhanVien), "trangChu");
            // Bán vé
            panelNoiDung.add(new BanVePanelJPA(nhanVien), "banVe");
            // Đổi vé
            panelNoiDung.add(new ManHinhDoiVeJPA(), "doiVe");
            // Trả vé
            panelNoiDung.add(new ManHinhTraVeJPA(), "traVe");
            // Tra cứu vé
            panelNoiDung.add(new TraCuuPanel(), "traCuuVe");
            // Tra cứu hóa đơn
            panelNoiDung.add(new TraCuuPanel(), "traCuuHD");
        }

        add(panelNoiDung, BorderLayout.CENTER);
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel("Chức năng: " + title + "\n(Đang phát triển)", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(Color.GRAY);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Phương thức công khai để các panel con có thể chuyển màn hình
     */
    public void navigateTo(String tenCard) {
        if (nutMenu.containsKey(tenCard)) {
            chuyenManHinh(tenCard);
        }
    }

    private void chuyenManHinh(String tenCard) {
        cardLayout.show(panelNoiDung, tenCard);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();

        String tenCard = nutMenu.entrySet().stream()
                .filter(entry -> entry.getValue() == nguon)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if ("dangXuat".equals(tenCard)) {
            int xacNhan = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (xacNhan == JOptionPane.YES_OPTION) {
                dispose();
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
            return;
        }

        if ("troGiup".equals(tenCard)) {
            String roleInfo = isQuanLy ? "Quản lý" : "Nhân viên bán vé";
            JOptionPane.showMessageDialog(this, 
                    "Hệ thống Bán Vé Tàu\n" +
                    "Tài khoản: " + nhanVien.getMaNV() + "\n" +
                    "Vai trò: " + roleInfo + "\n\n" +
                    "Liên hệ: hotline@vietautrain.com\nHotline: 1900-xxxx", 
                    "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (tenCard != null) {
            chuyenManHinh(tenCard);
        }
    }
}
