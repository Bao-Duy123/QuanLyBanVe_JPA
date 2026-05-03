package JPA_Project.gui;

import JPA_Project.entity.NhanVien;
import JPA_Project.service.ThongKeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.text.DecimalFormat;

/**
 * BanVeDashboardPanel - Dashboard cho nhân viên bán vé (NV).
 * Hiển thị thông tin cá nhân, thống kê bán vé, chuyến tàu sắp khởi hành.
 */
public class BanVeDashboardPanel extends JPanel {

    private static final Color MAU_NEN = Color.decode("#F0F2F5");
    private static final Color MAU_NEN_CARD = Color.WHITE;
    private static final Color MAU_CHINH = Color.decode("#3F51B5");
    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = 
            DateTimeFormatter.ofPattern("HH:mm:ss EEEE, dd/MM/yyyy", new Locale("vi", "VN"));

    private final NhanVien nhanVien;
    private final ThongKeService thongKeService;
    
    private JLabel lblSoVe, lblSoHD;
    private JLabel lblTenNV, lblMaNV, lblDongHo;
    private JPanel pnlTauSapChay;
    private DefaultTableModel modelHoatDong;
    private JTable tblHoatDong;
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("#,###");

    private BanVeDashboardPanel thisPanel;
    
    public BanVeDashboardPanel(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        this.thongKeService = new ThongKeService();
        this.thisPanel = this;
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(MAU_NEN);

        add(taoHeader(), BorderLayout.NORTH);
        add(taoContent(), BorderLayout.CENTER);
        add(taoQuickActions(), BorderLayout.SOUTH);

        SwingUtilities.invokeLater(this::loadData);
    }

    private JPanel taoHeader() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);

        JPanel pnlTrai = new JPanel(new GridLayout(2, 1));
        pnlTrai.setOpaque(false);

        lblTenNV = new JLabel("Xin chào, " + (nhanVien != null ? nhanVien.getHoTen() : "Nhân viên") + "!");
        lblTenNV.setFont(new Font("Segoe UI", Font.BOLD, 24));

        lblMaNV = new JLabel("Mã nhân viên: " + (nhanVien != null ? nhanVien.getMaNV() : "N/A"));
        lblMaNV.setForeground(Color.GRAY);

        pnlTrai.add(lblTenNV);
        pnlTrai.add(lblMaNV);

        JPanel pnlPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlPhai.setOpaque(false);

        lblDongHo = new JLabel();
        lblDongHo.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        khoiDongDongHo();

        pnlPhai.add(lblDongHo);
        pnlPhai.add(taoAvatar());

        pnl.add(pnlTrai, BorderLayout.WEST);
        pnl.add(pnlPhai, BorderLayout.EAST);
        return pnl;
    }

    private JPanel taoContent() {
        JPanel pnl = new JPanel(new BorderLayout(0, 20));
        pnl.setOpaque(false);

        pnl.add(taoStatsSection(), BorderLayout.NORTH);
        pnl.add(taoMainContent(), BorderLayout.CENTER);
        
        return pnl;
    }

    private JPanel taoStatsSection() {
        JPanel pnl = new JPanel(new GridLayout(1, 4, 20, 0));
        pnl.setOpaque(false);
        pnl.setPreferredSize(new Dimension(0, 100));

        lblSoVe = new JLabel("0", SwingConstants.CENTER);
        lblSoHD = new JLabel("0", SwingConstants.CENTER);

        pnl.add(taoCardStat("VÉ ĐÃ BÁN", lblSoVe, MAU_CHINH));
        pnl.add(taoCardStat("HÓA ĐƠN", lblSoHD, Color.decode("#4CAF50")));
        pnl.add(taoCardStat("DOANH THU", new JLabel("0 VND", SwingConstants.CENTER), Color.decode("#FF9800")));
        pnl.add(taoCardStat("TỶ LỆ LẤP ĐẦY", new JLabel("0%", SwingConstants.CENTER), Color.decode("#E91E63")));

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

    private JPanel taoMainContent() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.7; gbc.insets = new Insets(0, 0, 0, 10);
        pnl.add(taoLeftColumn(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.3; gbc.insets = new Insets(0, 10, 0, 0);
        pnl.add(taoRightColumn(), gbc);

        return pnl;
    }

    private JPanel taoLeftColumn() {
        JPanel pnl = new JPanel(new GridLayout(2, 1, 0, 20));
        pnl.setOpaque(false);

        pnlTauSapChay = new JPanel();
        pnlTauSapChay.setBackground(MAU_NEN_CARD);
        pnlTauSapChay.setBorder(taoTieuDeBorder("CHUYẾN TÀU SẮP KHỞI HÀNH"));
        pnlTauSapChay.setLayout(new BoxLayout(pnlTauSapChay, BoxLayout.Y_AXIS));
        
        JPanel pnlHoatDong = new JPanel(new BorderLayout());
        pnlHoatDong.setBackground(MAU_NEN_CARD);
        pnlHoatDong.setBorder(taoTieuDeBorder("HOẠT ĐỘNG GẦN ĐÂY"));

        String[] cols = {"Mã HD", "Khách hàng", "Tổng tiền", "Thời gian"};
        modelHoatDong = new DefaultTableModel(cols, 0);
        tblHoatDong = new JTable(modelHoatDong);
        tblHoatDong.setRowHeight(30);
        tblHoatDong.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane sp = new JScrollPane(tblHoatDong);
        pnlHoatDong.add(sp, BorderLayout.CENTER);

        pnl.add(pnlTauSapChay);
        pnl.add(pnlHoatDong);
        return pnl;
    }

    private JPanel taoRightColumn() {
        JPanel pnl = new JPanel(new GridLayout(2, 1, 0, 20));
        pnl.setOpaque(false);

        JPanel pnlKhuyenMai = new JPanel();
        pnlKhuyenMai.setBackground(MAU_NEN_CARD);
        pnlKhuyenMai.setBorder(taoTieuDeBorder("KHUYẾN MÃI HOT"));
        pnlKhuyenMai.setLayout(new BoxLayout(pnlKhuyenMai, BoxLayout.Y_AXIS));
        
        JLabel lblNoKM = new JLabel("Không có khuyến mãi nào");
        lblNoKM.setForeground(Color.GRAY);
        lblNoKM.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlKhuyenMai.add(lblNoKM);

        JPanel pnlThongBao = new JPanel();
        pnlThongBao.setBackground(MAU_NEN_CARD);
        pnlThongBao.setBorder(taoTieuDeBorder("THÔNG BÁO NỘI BỘ"));
        pnlThongBao.add(new JLabel("<html>- Chào mừng bạn đến với hệ thống!<br>- Nhắc nhở kiểm tra két tiền trước khi kết ca.</html>"));

        pnl.add(pnlKhuyenMai);
        pnl.add(pnlThongBao);
        return pnl;
    }

    private JPanel taoQuickActions() {
        JPanel pnl = new JPanel(new GridLayout(1, 4, 20, 0));
        pnl.setOpaque(false);
        pnl.setPreferredSize(new Dimension(0, 80));

        pnl.add(taoQuickButton("BÁN VÉ MỚI", "banVe", MAU_CHINH));
        pnl.add(taoQuickButton("TRẢ VÉ", "traVe", Color.decode("#E91E63")));
        pnl.add(taoQuickButton("ĐỔI VÉ", "doiVe", Color.decode("#FF9800")));
        pnl.add(taoQuickButton("TRA CỨU VÉ", "traCuuVe", Color.decode("#009688")));

        return pnl;
    }

    private JButton taoQuickButton(String text, String action, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(thisPanel);
            if (win instanceof MainFrame) {
                ((MainFrame) win).navigateTo(action);
            }
        });
        return btn;
    }

    private TitledBorder taoTieuDeBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), title);
        b.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setTitleColor(MAU_CHINH);
        return b;
    }

    private void khoiDongDongHo() {
        new javax.swing.Timer(1000, e -> lblDongHo.setText(LocalDateTime.now().format(DINH_DANG_NGAY_GIO))).start();
    }

    private JPanel taoAvatar() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(MAU_CHINH);
                g.fillOval(0, 0, 40, 40);
                g.setColor(Color.WHITE);
                g.drawString(nhanVien != null ? nhanVien.getHoTen().substring(0, 1).toUpperCase() : "U", 15, 25);
            }
        };
        p.setPreferredSize(new Dimension(40, 40));
        p.setOpaque(false);
        return p;
    }

    private void loadData() {
        if (nhanVien == null) return;

        SwingWorker<ThongKeService.ThongKe, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeService.ThongKe doInBackground() {
                return thongKeService.getThongKeNgay();
            }

            @Override
            protected void done() {
                try {
                    ThongKeService.ThongKe tk = get();
                    lblSoVe.setText(String.valueOf(tk.soVeDaBan));
                    lblSoHD.setText(String.valueOf(tk.soHoaDon));
                } catch (Exception e) {
                    System.err.println("Lỗi load dashboard: " + e.getMessage());
                }
            }
        };
        worker.execute();

        loadRecentActivities();
        loadUpcomingTrains();
    }

    private void loadRecentActivities() {
        modelHoatDong.setRowCount(0);
        modelHoatDong.addRow(new Object[]{"---", "Chưa có dữ liệu", "---", "---"});
    }

    private void loadUpcomingTrains() {
        pnlTauSapChay.removeAll();
        pnlTauSapChay.setLayout(new BoxLayout(pnlTauSapChay, BoxLayout.Y_AXIS));
        
        JLabel lbl = new JLabel("Không có chuyến tàu nào");
        lbl.setForeground(Color.GRAY);
        lbl.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlTauSapChay.add(lbl);
        
        pnlTauSapChay.revalidate();
        pnlTauSapChay.repaint();
    }

    public static void main(String[] args) {
        NhanVien nv = new NhanVien();
        nv.setMaNV("NVBV0001");
        nv.setHoTen("Test User");
        
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Ban Ve Dashboard");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new BanVeDashboardPanel(nv));
            f.setSize(1200, 800);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
