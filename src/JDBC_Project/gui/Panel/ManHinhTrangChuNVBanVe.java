package JDBC_Project.gui.Panel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import control.CaLamViec;
import dao.DashboardDAO;
import entity.NhanVien;
import gui.MainFrame.BanVeDashboard;

public class ManHinhTrangChuNVBanVe extends JPanel {

    // --- HẰNG SỐ GIAO DIỆN ---
    private static final Color MAU_NEN = Color.decode("#F0F2F5");
    private static final Color MAU_NEN_CARD = Color.WHITE;
    private static final Color MAU_CHINH = Color.decode("#3F51B5");
    private static final Color MAU_NHAN = Color.decode("#FF9800");
    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("HH:mm:ss EEEE, dd/MM/yyyy", new Locale("vi", "VN"));

    // --- CÁC THÀNH PHẦN CẬP NHẬT DỮ LIỆU ---
    private JLabel lblSoVe, lblSoHD;
    private JLabel lblTenNV, lblMaNV, lblDongHo;
    private JPanel pnlTauSapChay, pnlHoatDongGanDay, pnlKhuyenMaiContainer, pnlThongBaoNoiBo;

    private BanVeDashboard mainFrame;
    private DashboardDAO dao = new DashboardDAO();

    public ManHinhTrangChuNVBanVe(BanVeDashboard mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(MAU_NEN);

        // 1. GÓC TRÁI TRÊN & ĐỒNG HỒ (NORTH)
        add(taoHeader(), BorderLayout.NORTH);

        // 2. PHẦN TRUNG TÂM (CENTER) - Chứa Stats và Cột nội dung
        JPanel pnlTrungTam = new JPanel(new BorderLayout(0, 20));
        pnlTrungTam.setOpaque(false);

        // 2a. Hàng 4 thẻ Stats
        pnlTrungTam.add(taoHangStats(), BorderLayout.NORTH);

        // 2b. Chia 2 cột: Trái (Chính) - Phải (Phụ)
        JPanel pnlNoiDungChinh = new JPanel(new GridBagLayout());
        pnlNoiDungChinh.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Cột Trái (70%)
        gbc.gridx = 0; gbc.weightx = 0.7; gbc.insets = new Insets(0, 0, 0, 10);
        pnlNoiDungChinh.add(taoCotTraiChinh(), gbc);

        // Cột Phải (30%)
        gbc.gridx = 1; gbc.weightx = 0.3; gbc.insets = new Insets(0, 10, 0, 0);
        pnlNoiDungChinh.add(taoCotPhaiPhu(), gbc);

        pnlTrungTam.add(pnlNoiDungChinh, BorderLayout.CENTER);
        add(pnlTrungTam, BorderLayout.CENTER);

        // 3. DƯỚI CÙNG / MENU NHANH (SOUTH)
        add(taoPanelLienKetNhanh(), BorderLayout.SOUTH);

        // Load dữ liệu
        capNhatDuLieuDashboard();
    }

    // =========================================================================
    // PHẦN 1: HEADER (Chào hỏi & Đồng hồ)
    // =========================================================================
    private JPanel taoHeader() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);

        // Trái: Lời chào & ID
        JPanel pnlTrai = new JPanel(new GridLayout(2, 1));
        pnlTrai.setOpaque(false);

        lblTenNV = new JLabel("Xin chào, Đang tải...");
        lblTenNV.setFont(new Font("Segoe UI", Font.BOLD, 24));

        lblMaNV = new JLabel("Mã nhân viên: N/A");
        lblMaNV.setForeground(Color.GRAY);

        pnlTrai.add(lblTenNV);
        pnlTrai.add(lblMaNV);

        // Phải: Đồng hồ & Avatar
        JPanel pnlPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlPhai.setOpaque(false);

        lblDongHo = new JLabel();
        lblDongHo.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        khoiDongDongHo();

        pnlPhai.add(lblDongHo);
        pnlPhai.add(taoAvatar("U"));

        pnl.add(pnlTrai, BorderLayout.WEST);
        pnl.add(pnlPhai, BorderLayout.EAST);
        return pnl;
    }

    // =========================================================================
    // PHẦN 2: HÀNG 4 THẺ THỐNG KÊ (STATS)
    // =========================================================================
    private JPanel taoHangStats() {
        JPanel pnl = new JPanel(new GridLayout(1, 4, 20, 0));
        pnl.setOpaque(false);
        pnl.setPreferredSize(new Dimension(0, 100));

        lblSoVe = new JLabel("0", SwingConstants.CENTER);
        lblSoHD = new JLabel("0", SwingConstants.CENTER);

        pnl.add(taoCardStat("VÉ ĐÃ BÁN", lblSoVe, MAU_CHINH));
        pnl.add(taoCardStat("HÓA ĐƠN", lblSoHD, Color.decode("#4CAF50")));

        return pnl;
    }

    private JPanel taoCardStat(String tieuDe, JLabel lblGiaTri, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MAU_NEN_CARD);
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        JLabel lblTieuDe = new JLabel(tieuDe, SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTieuDe.setForeground(Color.GRAY);

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGiaTri.setForeground(color);

        card.add(lblTieuDe, BorderLayout.NORTH);
        card.add(lblGiaTri, BorderLayout.CENTER);
        return card;
    }
    private DefaultTableModel modelHoatDong;
    private JTable tblHoatDong;
    private static final java.text.DecimalFormat VND_FORMAT = new java.text.DecimalFormat("#,###");

    // =========================================================================
    // PHẦN 3: CỘT TRÁI CHÍNH (Tàu chạy & Hoạt động)
    // =========================================================================
    private JPanel taoCotTraiChinh() {
        JPanel pnl = new JPanel(new GridLayout(2, 1, 0, 20));
        pnl.setOpaque(false);

        // 1. Panel Tàu sắp khởi hành
        pnlTauSapChay = new JPanel();
        pnlTauSapChay.setBackground(MAU_NEN_CARD);
        pnlTauSapChay.setBorder(taoTieuDeBorder("CHUYẾN TÀU SẮP KHỞI HÀNH"));

        // 2. Panel Hoạt động gần đây (Placeholder cho Table)
        pnlHoatDongGanDay = new JPanel(new BorderLayout());
        pnlHoatDongGanDay.setBackground(MAU_NEN_CARD);
        pnlHoatDongGanDay.setBorder(taoTieuDeBorder("HOẠT ĐỘNG GẦN ĐÂY"));

        String[] cols = {"Mã HD", "Khách hàng", "Tổng tiền", "Thời gian"};
        modelHoatDong = new DefaultTableModel(cols, 0); // Khởi tạo model
        tblHoatDong = new JTable(modelHoatDong);

        // Tùy chỉnh bảng cho đẹp
        tblHoatDong.setRowHeight(30);
        tblHoatDong.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane sp = new JScrollPane(tblHoatDong);
        pnlHoatDongGanDay.add(sp, BorderLayout.CENTER);

        pnl.add(pnlTauSapChay);
        pnl.add(pnlHoatDongGanDay);
        return pnl;
    }
    private void capNhatHoatDongGanDay() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv == null) return;

        List<Map<String, Object>> ds = dao.getHoaDonGanDay(nv.getMaNV());
        modelHoatDong.setRowCount(0); // Xóa dữ liệu cũ

        DateTimeFormatter fmtGio = DateTimeFormatter.ofPattern("HH:mm dd/MM");

        for (Map<String, Object> hd : ds) {
            // Chuyển Timestamp sang LocalDateTime để format
            java.sql.Timestamp ts = (java.sql.Timestamp) hd.get("ngayLap");
            String thoiGian = ts.toLocalDateTime().format(fmtGio);

            modelHoatDong.addRow(new Object[]{
                    hd.get("maHD"),
                    hd.get("tenKH"),
                    VND_FORMAT.format(hd.get("tongTien")) + " VND",
                    thoiGian
            });
        }
    }


    // =========================================================================
    // PHẦN 4: CỘT PHẢI PHỤ (Khuyến mãi & Thông báo)
    // =========================================================================
    private JPanel taoCotPhaiPhu() {
        JPanel pnl = new JPanel(new GridLayout(2, 1, 0, 20));
        pnl.setOpaque(false);

        // 1. Panel Khuyến mãi
        pnlKhuyenMaiContainer = new JPanel();
        pnlKhuyenMaiContainer.setBackground(MAU_NEN_CARD);
        JScrollPane scrollKM = new JScrollPane(pnlKhuyenMaiContainer);
        scrollKM.setBorder(taoTieuDeBorder("KHUYẾN MÃI HOT"));

        // 2. Panel Thông báo nội bộ
        pnlThongBaoNoiBo = new JPanel();
        pnlThongBaoNoiBo.setBackground(MAU_NEN_CARD);
        pnlThongBaoNoiBo.setBorder(taoTieuDeBorder("THÔNG BÁO NỘI BỘ"));
        pnlThongBaoNoiBo.add(new JLabel("<html>- Cập nhật phần mềm v2.0 vào tối nay.<br>- Nhắc nhở kiểm tra két tiền trước khi kết ca.</html>"));

        pnl.add(scrollKM);
        pnl.add(pnlThongBaoNoiBo);
        return pnl;
    }

    // =========================================================================
    // PHẦN 5: MENU NHANH DƯỚI CÙNG (SOUTH)
    // =========================================================================
    private JPanel taoPanelLienKetNhanh() {
        JPanel pnl = new JPanel(new GridLayout(1, 4, 20, 0));
        pnl.setOpaque(false);
        pnl.setPreferredSize(new Dimension(0, 80));

        pnl.add(taoNutNhanh("BÁN VÉ MỚI", "banVeMoi", MAU_CHINH));
        pnl.add(taoNutNhanh("TRẢ VÉ", "traVe", Color.decode("#E91E63")));
        pnl.add(taoNutNhanh("ĐỔI VÉ", "doiVe", Color.decode("#FF9800")));
        pnl.add(taoNutNhanh("TRA CỨU VÉ", "traCuuVe", Color.decode("#009688")));

        return pnl;
    }

    private JButton taoNutNhanh(String text, String cardName, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            BanVeDashboard.instance.chuyenManHinh(cardName);
        });

        return btn;
    }

    // =========================================================================
    // LOGIC CẬP NHẬT DỮ LIỆU
    // =========================================================================
    private void capNhatDuLieuDashboard() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv == null) return;

        // Cập nhật Header
        lblTenNV.setText("Xin chào, " + nv.getHoTen() + "!");
        lblMaNV.setText("Mã nhân viên: " + nv.getMaNV());

        // Cập nhật Stats
        Map<String, Object> stats = dao.getThongKeTrongNgay(nv.getMaNV());


        lblSoVe.setText(stats.get("soVe").toString());
        lblSoHD.setText("N/A");

        // Cập nhật Tàu sắp chạy
        List<String[]> dsTau = dao.getChuyenTauSapChay();
        pnlTauSapChay.removeAll();
        pnlTauSapChay.setLayout(new BoxLayout(pnlTauSapChay, BoxLayout.Y_AXIS));
        for (String[] t : dsTau) {
            JLabel lb = new JLabel("🚂 " + t[0] + ": " + t[1] + " -> " + t[2] + " (" + t[3] + ")");
            lb.setBorder(new EmptyBorder(5,10,5,10));
            pnlTauSapChay.add(lb);
        }

        // Cập nhật Khuyến mãi
        List<Map<String, String>> dsKM = dao.getKhuyenMaiHienNay();
        pnlKhuyenMaiContainer.removeAll();
        pnlKhuyenMaiContainer.setLayout(new BoxLayout(pnlKhuyenMaiContainer, BoxLayout.Y_AXIS));
        for (Map<String, String> km : dsKM) {
            pnlKhuyenMaiContainer.add(taoCardKhuyenMaiChiTiet(km.get("ten"), km.get("dieukien"), ""));
            pnlKhuyenMaiContainer.add(Box.createVerticalStrut(5));
        }
        capNhatHoatDongGanDay();
        revalidate(); repaint();
    }

    // --- HELPER METHODS ---
    private TitledBorder taoTieuDeBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), title);
        b.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setTitleColor(MAU_CHINH);
        return b;
    }

    private void khoiDongDongHo() {
        new Timer(1000, e -> lblDongHo.setText(LocalDateTime.now().format(DINH_DANG_NGAY_GIO))).start();
    }

    private JPanel taoAvatar(String text) {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(MAU_CHINH);
                g.fillOval(0, 0, 40, 40);
                g.setColor(Color.WHITE);
                g.drawString(text, 15, 25);
            }
        };
        p.setPreferredSize(new Dimension(40, 40));
        p.setOpaque(false);
        return p;
    }

    private JPanel taoCardKhuyenMaiChiTiet(String ten, String dk, String giam) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBackground(Color.decode("#E3F2FD"));
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel l1 = new JLabel(ten); l1.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel l2 = new JLabel("ĐK: " + dk); l2.setFont(new Font("Arial", Font.PLAIN, 10));
        p.add(l1); p.add(l2);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(3);
            f.add(new ManHinhTrangChuNVBanVe(null));
            f.setSize(1200, 800);
            f.setVisible(true);
        });
    }
}