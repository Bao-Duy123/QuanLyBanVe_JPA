package JPA_Project.gui;

import JPA_Project.entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame implements ActionListener {

    private CardLayout cardLayout;
    private JPanel panelNoiDung;
    private final NhanVien nhanVien;
    private final String chucVu;

    private static final Color MAU_CHINH = new Color(41, 128, 185);
    private static final Color MAU_DUOC_CHON = new Color(52, 152, 219);
    private static final Color MAU_HOVER = new Color(33, 110, 160);
    private static final Map<String, JButton> nutMenu = new HashMap<>();

    private JButton nutTrangChu, nutBanVe, nutTraCuuVe, nutTraCuuHD, nutDangXuat, nutTroGiup;
    private JButton nutQuanLy, nutThongKe;

    private static final int CHIEU_RONG_MENU = 220;
    private static final int ICON_SIZE = 20;

    public MainFrame(NhanVien nhanVien, String chucVu) {
        this.nhanVien = nhanVien;
        this.chucVu = chucVu;

        setTitle("Hệ thống Bán Vé Tàu- " + nhanVien.getHoTen());
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelDieuHuong = taoPanelDieuHuong();
        add(panelDieuHuong, BorderLayout.WEST);

        khoiTaoPanelNoiDung();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        chuyenManHinh("trangChu");
        setVisible(true);
    }

    private JPanel taoPanelDieuHuong() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_CHINH);
        panel.setPreferredSize(new Dimension(CHIEU_RONG_MENU, 0));
        panel.setBorder(new EmptyBorder(10, 5, 0, 5));

        JPanel panelTieuDe = new JPanel();
        panelTieuDe.setLayout(new BoxLayout(panelTieuDe, BoxLayout.Y_AXIS));
        panelTieuDe.setBackground(MAU_CHINH);
        panelTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nhanLogo = new JLabel("Nha Ga ...");
        nhanLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nhanLogo.setForeground(Color.WHITE);
        nhanLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelTieuDe.add(nhanLogo);

        panel.add(panelTieuDe);
        panel.add(taoDuongKe());

        nutTrangChu = taoMucMenu("Trang chủ", "trangChu");
        panel.add(nutTrangChu);
        panel.add(taoDuongKe());

        nutBanVe = taoMucMenu("Bán vé", "banVe");
        panel.add(nutBanVe);
        panel.add(taoDuongKe());

        nutTraCuuVe = taoMucMenu("Tra cứu vé", "traCuuVe");
        panel.add(nutTraCuuVe);
        panel.add(taoDuongKe());

        nutTraCuuHD = taoMucMenu("Tra cứu hóa đơn", "traCuuHD");
        panel.add(nutTraCuuHD);
        panel.add(taoDuongKe());

        nutThongKe = taoMucMenu("Thống kê", "thongKe");
        panel.add(nutThongKe);
        panel.add(taoDuongKe());

        if ("QUAN_LY".equals(chucVu) || "ADMIN".equals(chucVu)) {
            nutQuanLy = taoMucMenu("Quản lý", "quanLy");
            panel.add(nutQuanLy);
            panel.add(taoDuongKe());
        }

        nutTroGiup = taoMucMenu("Hướng dẫn", "troGiup");
        panel.add(nutTroGiup);
        panel.add(taoDuongKe());

        panel.add(Box.createVerticalGlue());

        panel.add(taoPanelThongTinNV());

        nutDangXuat = taoMucMenu("Đăng xuất", "dangXuat");
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

        JLabel nhanChucVu = new JLabel("Chức vụ: " + chucVu);
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

    private JButton taoMucMenu(String vanBan, String tenCard) {
        JButton nut = new JButton(vanBan);
        nut.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nut.setForeground(Color.WHITE);
        nut.setBackground(MAU_CHINH);

        // --- Hai phương thức bạn yêu cầu ---
        nut.setOpaque(true);               // Đảm bảo hiển thị màu nền
        nut.setBorderPainted(false);       // Loại bỏ đường viền mặc định của JButton
        // -----------------------------------

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

        panelNoiDung.add(new TrangChuPanel(), "trangChu");
        panelNoiDung.add(new BanVePanel(nhanVien), "banVe");
        panelNoiDung.add(new TraCuuPanel(), "traCuuVe");
        panelNoiDung.add(new TraCuuPanel(), "traCuuHD");
        panelNoiDung.add(new ThongKePanel(), "thongKe");
        panelNoiDung.add(new QuanLyPanel(), "quanLy");

        add(panelNoiDung, BorderLayout.CENTER);
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
            JOptionPane.showMessageDialog(this, "Liên hệ: hotline@vietautrain.com\nHotline: 1900-xxxx", "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (tenCard != null) {
            chuyenManHinh(tenCard);
        }
    }
}
