package JPA_Project.gui;

import JPA_Project.service.ThongKeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class ThongKePanel extends JPanel {
    private final ThongKeService thongKeService;

    private JSpinner spnThang;
    private JSpinner spnNam;
    private JButton btnThongKeThang;
    private JButton btnThongKeNgay;
    private JButton btnThongKeNam;

    private JLabel lblDoanhThu;
    private JLabel lblSoVe;
    private JTable tblTopGa;

    private LocalDate ngayHienTai;

    public ThongKePanel() {
        this.thongKeService = new ThongKeService();
        this.ngayHienTai = LocalDate.now();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblTieuDe = new JLabel("THỐNG KÊ DOANH THU");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(new Color(44, 62, 80));
        headerPanel.add(lblTieuDe);

        add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(5);
        splitPane.setDividerLocation(450);

        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Chọn thời gian"));
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Tháng:"));
        spnThang = new JSpinner(new SpinnerNumberModel(ngayHienTai.getMonthValue(), 1, 12, 1));
        spnThang.setPreferredSize(new Dimension(60, 25));
        row1.add(spnThang);
        row1.add(new JLabel("  Năm:"));
        spnNam = new JSpinner(new SpinnerNumberModel(ngayHienTai.getYear(), 2020, 2030, 1));
        spnNam.setPreferredSize(new Dimension(80, 25));
        row1.add(spnNam);
        filterPanel.add(row1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnThongKeThang = new JButton("Thống kê tháng");
        btnThongKeThang.setBackground(new Color(52, 152, 219));
        btnThongKeThang.setForeground(Color.WHITE);
        btnThongKeThang.addActionListener(e -> thongKeTheoThang());
        buttonPanel.add(btnThongKeThang);

        btnThongKeNgay = new JButton("Thống kê hôm nay");
        btnThongKeNgay.setBackground(new Color(46, 204, 113));
        btnThongKeNgay.setForeground(Color.WHITE);
        btnThongKeNgay.addActionListener(e -> thongKeTheoNgay());
        buttonPanel.add(btnThongKeNgay);

        btnThongKeNam = new JButton("Thống kê năm");
        btnThongKeNam.setBackground(new Color(155, 89, 182));
        btnThongKeNam.setForeground(Color.WHITE);
        btnThongKeNam.addActionListener(e -> thongKeTheoNam());
        buttonPanel.add(btnThongKeNam);

        filterPanel.add(buttonPanel);
        panel.add(filterPanel);
        panel.add(Box.createVerticalStrut(15));

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createTitledBorder("Kết quả thống kê"));
        resultPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JPanel doanhThuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        doanhThuPanel.add(new JLabel("Doanh thu:"));
        doanhThuPanel.add(Box.createHorizontalStrut(10));
        lblDoanhThu = new JLabel("0 VNĐ");
        lblDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblDoanhThu.setForeground(new Color(46, 204, 113));
        doanhThuPanel.add(lblDoanhThu);
        resultPanel.add(doanhThuPanel);

        JPanel soVePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        soVePanel.add(new JLabel("Số vé bán ra:"));
        soVePanel.add(Box.createHorizontalStrut(10));
        lblSoVe = new JLabel("0");
        lblSoVe.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSoVe.setForeground(new Color(52, 152, 219));
        soVePanel.add(lblSoVe);
        resultPanel.add(soVePanel);

        panel.add(resultPanel);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTieuDe = new JLabel("Top ga đến nhiều nhất");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(new Color(44, 62, 80));
        panel.add(lblTieuDe, BorderLayout.NORTH);

        String[] columns = {"STT", "Ga", "Doanh thu"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTopGa = new JTable(model);
        tblTopGa.setRowHeight(30);
        tblTopGa.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblTopGa.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblTopGa.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(tblTopGa);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void thongKeTheoThang() {
        int thang = (int) spnThang.getValue();
        int nam = (int) spnNam.getValue();
        btnThongKeThang.setEnabled(false);

        SwingWorker<ThongKeService.ThongKe, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeService.ThongKe doInBackground() {
                return thongKeService.thongKeThang(thang, nam);
            }

            @Override
            protected void done() {
                btnThongKeThang.setEnabled(true);
                try {
                    ThongKeService.ThongKe tk = get();
                    hienThiKetQua(tk);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ThongKePanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void thongKeTheoNgay() {
        btnThongKeNgay.setEnabled(false);

        SwingWorker<ThongKeService.ThongKe, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeService.ThongKe doInBackground() {
                return thongKeService.thongKeNgay(ngayHienTai.atStartOfDay());
            }

            @Override
            protected void done() {
                btnThongKeNgay.setEnabled(true);
                try {
                    ThongKeService.ThongKe tk = get();
                    hienThiKetQua(tk);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ThongKePanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void thongKeTheoNam() {
        int nam = (int) spnNam.getValue();
        btnThongKeNam.setEnabled(false);

        SwingWorker<ThongKeService.ThongKe, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeService.ThongKe doInBackground() {
                double tongDoanhThu = 0;
                int tongSoVe = 0;
                Map<String, Double> topGa = new HashMap<>();

                for (int thang = 1; thang <= 12; thang++) {
                    ThongKeService.ThongKe tk = thongKeService.thongKeThang(thang, nam);
                    tongDoanhThu += tk.getDoanhThu();
                    tongSoVe += tk.getSoVe();
                    for (Map.Entry<String, Double> entry : tk.getTopGa().entrySet()) {
                        String key = entry.getKey();
                        Double val = entry.getValue();
                        topGa.put(key, topGa.getOrDefault(key, 0.0) + val);
                    }
                }
                return new ThongKeService.ThongKe(tongDoanhThu, tongSoVe, topGa);
            }

            @Override
            protected void done() {
                btnThongKeNam.setEnabled(true);
                try {
                    ThongKeService.ThongKe tk = get();
                    hienThiKetQua(tk);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ThongKePanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void hienThiKetQua(ThongKeService.ThongKe tk) {
        lblDoanhThu.setText(String.format("%.0f VNĐ", tk.getDoanhThu()));
        lblSoVe.setText(String.valueOf(tk.getSoVe()));

        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblTopGa.getModel();
        model.setRowCount(0);

        int stt = 1;
        for (Map.Entry<String, Double> entry : tk.getTopGa().entrySet()) {
            if (stt > 10) break;
            model.addRow(new Object[]{stt++, entry.getKey(), String.format("%.0f VNĐ", entry.getValue())});
        }
    }
}
