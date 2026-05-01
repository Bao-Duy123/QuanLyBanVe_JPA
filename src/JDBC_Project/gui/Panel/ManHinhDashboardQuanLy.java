package JDBC_Project.gui.Panel;

import dao.ThongKeDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

// Cần thư viện JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class ManHinhDashboardQuanLy extends JPanel {

    // --- MÀU SẮC & FONT ---
    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color COL_REVENUE = new Color(40, 167, 69);
    private static final Color COL_TICKET  = new Color(0, 123, 255);
    private static final Color COL_RETURN  = new Color(220, 53, 69);
    private static final Color COL_GROWTH  = new Color(255, 193, 7);

    // <--- THAY ĐỔI: Tăng kích thước font lên một chút cho rõ ràng hơn
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_VALUE  = new Font("Segoe UI", Font.BOLD, 32); // Số to hơn
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TREND  = new Font("Segoe UI", Font.ITALIC, 13);


    private ThongKeDAO thongKeDAO;
    private JComboBox<String> cboThoiGian;
    private JPanel chartsContainer;

    private JLabel lblDoanhThu, lblVeBan, lblVeTra, lblTyLeLapDay;
    private JLabel lblTrendDoanhThu, lblTrendVeBan;

    public ManHinhDashboardQuanLy() {
        setLayout(new BorderLayout(20, 20)); // <--- THAY ĐỔI: Tăng khoảng cách viền ngoài
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 30, 30, 30)); // <--- THAY ĐỔI: Padding dày hơn cho thoáng

        thongKeDAO = new ThongKeDAO();

        // 1. Header & Bộ lọc
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Nội dung chính (Scrollable)
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(BG_COLOR);

        // KPI Section
        bodyPanel.add(createKPISection());
        bodyPanel.add(Box.createVerticalStrut(40)); // <--- THAY ĐỔI: Khoảng cách giữa KPI và Biểu đồ rộng hơn

        // Biểu đồ Section
        chartsContainer = new JPanel(new BorderLayout());
        chartsContainer.setOpaque(false);
        // <--- THAY ĐỔI QUAN TRỌNG: Đặt tỉ lệ biểu đồ đẹp hơn (cao hơn)
        // Chiều rộng để 1200 để nó không quá bè trên màn hình lớn, chiều cao tăng lên 550
        chartsContainer.setPreferredSize(new Dimension(1200, 550));
        chartsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 550));

        bodyPanel.add(chartsContainer);

        // Đẩy nội dung lên trên
        bodyPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(bodyPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // Cuộn mượt hơn
        add(scrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::loadData);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0)); // <--- THAY ĐỔI: Khoảng cách dưới header

        JLabel lblTitle = new JLabel("Tổng quan hoạt động kinh doanh");
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(new Color(60, 60, 60));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);

        JLabel lblFilter = new JLabel("Thời gian: ");
        lblFilter.setFont(FONT_LABEL);
        filterPanel.add(lblFilter);

        String[] periods = {"Hôm nay", "Tuần này", "Tháng này"};
        cboThoiGian = new JComboBox<>(periods);
        cboThoiGian.setSelectedIndex(1);
        cboThoiGian.setFont(FONT_LABEL);
        cboThoiGian.setPreferredSize(new Dimension(130, 35)); // <--- THAY ĐỔI: ComboBox to hơn chút
        cboThoiGian.addActionListener(e -> loadData());

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(FONT_LABEL);
        btnRefresh.setPreferredSize(new Dimension(100, 35)); // <--- THAY ĐỔI: Nút to hơn chút
        btnRefresh.addActionListener(e -> loadData());

        filterPanel.add(cboThoiGian);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(btnRefresh);

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createKPISection() {
        // <--- THAY ĐỔI: Tăng khoảng cách giữa các thẻ (hgap = 25)
        JPanel panel = new JPanel(new GridLayout(1, 4, 25, 0));
        panel.setOpaque(false);
        // <--- THAY ĐỔI QUAN TRỌNG: Tăng chiều cao thẻ KPI lên 160px cho thoáng
        panel.setPreferredSize(new Dimension(0, 160));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Card 1: Doanh thu
        JPanel cardRevenue = createCard("Doanh thu ước tính", COL_REVENUE);
        lblDoanhThu = (JLabel) cardRevenue.getClientProperty("value");
        lblTrendDoanhThu = (JLabel) cardRevenue.getClientProperty("trend");
        panel.add(cardRevenue);

        // Card 2: Vé bán
        JPanel cardSold = createCard("Số vé đã bán", COL_TICKET);
        lblVeBan = (JLabel) cardSold.getClientProperty("value");
        lblTrendVeBan = (JLabel) cardSold.getClientProperty("trend");
        panel.add(cardSold);

        // Card 3: Vé trả
        JPanel cardReturn = createCard("Số vé bị trả", COL_RETURN);
        lblVeTra = (JLabel) cardReturn.getClientProperty("value");
        ((JLabel)cardReturn.getClientProperty("trend")).setText("Cần theo dõi sát");
        panel.add(cardReturn);

        // Card 4: Tỷ lệ lấp đầy
        JPanel cardRate = createCard("Tỷ lệ lấp đầy (Hôm nay)", COL_GROWTH);
        lblTyLeLapDay = (JLabel) cardRate.getClientProperty("value");
        ((JLabel)cardRate.getClientProperty("trend")).setText("Hiệu suất vận hành");
        panel.add(cardRate);

        return panel;
    }

    private JPanel createCard(String title, Color borderColor) {
        // <--- THAY ĐỔI: Tăng padding bên trong thẻ (20,25,20,25)
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, borderColor), // Viền màu dày hơn chút (6px)
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
        // ... (Phần logic loadData giữ nguyên như cũ) ...
        Map<String, Double> stats;
        Map<String, Integer> chartData;
        double tyLeLapDay = 0.0;

        try {
            stats = thongKeDAO.getThongKeTuan();
            chartData = thongKeDAO.getSoLuongTheoLoaiVe();
            tyLeLapDay = thongKeDAO.getTyLeLapDayHomNay();
        } catch (Exception e) {
            e.printStackTrace();
            stats = new HashMap<>();
            chartData = new HashMap<>();
        }

        NumberFormat vnMoney = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        double doanhThu = stats.getOrDefault("BanTuanNay", 0.0) * 350000;

        lblDoanhThu.setText(vnMoney.format(doanhThu));
        formatTrend(lblTrendDoanhThu, stats.getOrDefault("TangTruongBan", 0.0));

        lblVeBan.setText(String.format("%,.0f", stats.getOrDefault("BanTuanNay", 0.0)));
        formatTrend(lblTrendVeBan, stats.getOrDefault("TangTruongBan", 0.0));

        lblVeTra.setText(String.format("%,.0f", stats.getOrDefault("TraTuanNay", 0.0)));

        lblTyLeLapDay.setText(String.format("%.1f%%", tyLeLapDay));
        if (tyLeLapDay >= 80) lblTyLeLapDay.setForeground(COL_REVENUE);
        else if (tyLeLapDay >= 50) lblTyLeLapDay.setForeground(COL_GROWTH);
        else lblTyLeLapDay.setForeground(COL_RETURN);

        updateChart(chartData);
    }

    private void updateChart(Map<String, Integer> data) {
        chartsContainer.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (data.isEmpty()) {
            dataset.addValue(0, "Số lượng", "Chưa có dữ liệu");
        } else {
            data.forEach((k, v) -> dataset.addValue(v, "Số lượng", k));
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Phân bố loại vé bán ra theo đối tượng",
                "Loại đối tượng", "Số lượng vé",
                dataset, PlotOrientation.VERTICAL, false, true, false
        );

        // --- TINH CHỈNH BIỂU ĐỒ CHO ĐẸP ---
        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        barChart.setAntiAlias(true);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220)); // Màu đường lưới nhạt hơn
        plot.setOutlineVisible(false); // Bỏ viền xung quanh biểu đồ

        // <--- THAY ĐỔI QUAN TRỌNG: Chỉnh trục Y chỉ hiện số nguyên
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLabelFont(FONT_LABEL);
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Chỉnh trục X
        plot.getDomainAxis().setLabelFont(FONT_LABEL);
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Chỉnh renderer (cột)
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, COL_TICKET);
        // <--- THAY ĐỔI: Tăng độ rộng cột lên cho đỡ bị "ốm" (0.25)
        renderer.setMaximumBarWidth(0.25);
        renderer.setDrawBarOutline(false);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 13));

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding trong biểu đồ

        chartsContainer.add(chartPanel, BorderLayout.CENTER);

        chartsContainer.revalidate();
        chartsContainer.repaint();
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
}