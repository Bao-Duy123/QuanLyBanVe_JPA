package JPA_Project.gui;

import JPA_Project.entity.NhanVien;
import JPA_Project.service.ThongKeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * QuanLyDashboardPanel - Dashboard cho quản lý.
 * Hiển thị thống kê tổng quan, biểu đồ doanh thu.
 */
public class QuanLyDashboardPanel extends JPanel {

    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color COL_REVENUE = new Color(40, 167, 69);
    private static final Color COL_TICKET = new Color(0, 123, 255);
    private static final Color COL_RETURN = new Color(220, 53, 69);
    private static final Color COL_GROWTH = new Color(255, 193, 7);

    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_VALUE = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TREND = new Font("Segoe UI", Font.ITALIC, 13);

    private final NhanVien nhanVien;
    private final ThongKeService thongKeService;
    
    private JLabel lblDoanhThu, lblVeBan, lblVeTra, lblTyLeLapDay;
    private JLabel lblTrendDoanhThu, lblTrendVeBan;
    private JPanel chartsContainer;

    public QuanLyDashboardPanel(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        this.thongKeService = new ThongKeService();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 30, 30, 30));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::loadData);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitle = new JLabel("Tổng quan hoạt động kinh doanh");
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(new Color(60, 60, 60));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);

        JLabel lblFilter = new JLabel("Thời gian: ");
        lblFilter.setFont(FONT_LABEL);
        filterPanel.add(lblFilter);

        String[] periods = {"Hôm nay", "Tuần này", "Tháng này"};
        JComboBox<String> cboThoiGian = new JComboBox<>(periods);
        cboThoiGian.setSelectedIndex(1);
        cboThoiGian.setFont(FONT_LABEL);
        cboThoiGian.setPreferredSize(new Dimension(130, 35));
        filterPanel.add(cboThoiGian);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(FONT_LABEL);

        btnRefresh.setOpaque(true);
        btnRefresh.setBorderPainted(false);

        btnRefresh.setPreferredSize(new Dimension(100, 35));
        btnRefresh.addActionListener(e -> loadData());
        filterPanel.add(cboThoiGian);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(btnRefresh);

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane createBodyPanel() {
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(BG_COLOR);

        bodyPanel.add(createKPISection());
        bodyPanel.add(Box.createVerticalStrut(40));
        
        chartsContainer = new JPanel(new BorderLayout());
        chartsContainer.setOpaque(false);
        chartsContainer.setPreferredSize(new Dimension(1200, 550));
        chartsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 550));
        chartsContainer.setBackground(CARD_BG);
        chartsContainer.setBorder(BorderFactory.createTitledBorder("Phân bố loại vé bán ra"));
        
        JLabel chartPlaceholder = new JLabel("Biểu đồ thống kê sẽ được hiển thị ở đây", SwingConstants.CENTER);
        chartPlaceholder.setFont(FONT_LABEL);
        chartPlaceholder.setForeground(Color.GRAY);
        chartsContainer.add(chartPlaceholder, BorderLayout.CENTER);

        bodyPanel.add(chartsContainer);
        bodyPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(bodyPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        return scrollPane;
    }

    private JPanel createKPISection() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 25, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 160));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel cardRevenue = createCard("Doanh thu ước tính", COL_REVENUE);
        lblDoanhThu = (JLabel) cardRevenue.getClientProperty("value");
        lblTrendDoanhThu = (JLabel) cardRevenue.getClientProperty("trend");
        panel.add(cardRevenue);

        JPanel cardSold = createCard("Số vé đã bán", COL_TICKET);
        lblVeBan = (JLabel) cardSold.getClientProperty("value");
        lblTrendVeBan = (JLabel) cardSold.getClientProperty("trend");
        panel.add(cardSold);

        JPanel cardReturn = createCard("Số vé bị trả", COL_RETURN);
        lblVeTra = (JLabel) cardReturn.getClientProperty("value");
        ((JLabel) cardReturn.getClientProperty("trend")).setText("Cần theo dõi sát");
        panel.add(cardReturn);

        JPanel cardRate = createCard("Tỷ lệ lấp đầy (Hôm nay)", COL_GROWTH);
        lblTyLeLapDay = (JLabel) cardRate.getClientProperty("value");
        ((JLabel) cardRate.getClientProperty("trend")).setText("Hiệu suất vận hành");
        panel.add(cardRate);

        return panel;
    }

    private JPanel createCard(String title, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, borderColor),
                new EmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_LABEL);
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel("0");
        lblValue.setFont(FONT_VALUE);
        lblValue.setForeground(Color.DARK_GRAY);

        JLabel lblTrend = new JLabel("---");
        lblTrend.setFont(FONT_TREND);
        lblTrend.setForeground(Color.GRAY);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblTrend, BorderLayout.SOUTH);

        card.putClientProperty("value", lblValue);
        card.putClientProperty("trend", lblTrend);
        return card;
    }

    private void loadData() {
        SwingWorker<ThongKeService.ThongKe, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeService.ThongKe doInBackground() {
                return thongKeService.getThongKeNgay();
            }

            @Override
            protected void done() {
                try {
                    ThongKeService.ThongKe tk = get();
                    
                    NumberFormat vnMoney = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    double doanhThu = tk.soVeDaBan * 350000;
                    
                    lblDoanhThu.setText(vnMoney.format(doanhThu));
                    lblVeBan.setText(String.valueOf(tk.soVeDaBan));
                    lblVeTra.setText(String.valueOf(tk.soVeTra));
                    lblTyLeLapDay.setText(String.format("%.1f%%", tk.tyLeLapDay));
                    
                    if (tk.tyLeLapDay >= 80) lblTyLeLapDay.setForeground(COL_REVENUE);
                    else if (tk.tyLeLapDay >= 50) lblTyLeLapDay.setForeground(COL_GROWTH);
                    else lblTyLeLapDay.setForeground(COL_RETURN);
                    
                    formatTrend(lblTrendDoanhThu, tk.tangTruong);
                    formatTrend(lblTrendVeBan, tk.tangTruong);
                    
                } catch (Exception e) {
                    System.err.println("Lỗi load dashboard: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void formatTrend(JLabel lbl, double percent) {
        if (percent > 0) {
            lbl.setText("▲ Tăng " + String.format("%.1f", percent) + "%");
            lbl.setForeground(new Color(30, 130, 76));
        } else if (percent < 0) {
            lbl.setText("▼ Giảm " + String.format("%.1f", Math.abs(percent)) + "%");
            lbl.setForeground(COL_RETURN);
        } else {
            lbl.setText("▬ Ổn định");
            lbl.setForeground(Color.GRAY);
        }
    }

    public static void main(String[] args) {
        NhanVien nv = new NhanVien();
        nv.setMaNV("NVQL0001");
        nv.setHoTen("Quan Ly");
        
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Quan Ly Dashboard");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new QuanLyDashboardPanel(nv));
            f.setSize(1400, 900);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
