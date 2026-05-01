package JPA_Project.gui;

import JPA_Project.service.ThongKeService;
import JPA_Project.entity.HoaDon;
import JPA_Project.entity.Ve;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TrangChuPanel extends JPanel {

    private JPanel lblDoanhThu;
    private JPanel lblSoVeBan;
    private JPanel lblSoKhachHang;
    private JLabel lblGiaTriDoanhThu;
    private JLabel lblGiaTriSoVeBan;
    private JLabel lblGiaTriSoKhachHang;
    private JTable tblVeMoi;
    private JTable tblHoaDonGanNhat;

    public TrangChuPanel() {
        initComponents();
        taiDuLieu();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTieuDe = new JLabel("BẢNG ĐIỀU KHIỂN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setForeground(new Color(44, 62, 80));
        headerPanel.add(lblTieuDe);

        JLabel lblNgay = new JLabel("Hôm nay: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNgay.setForeground(Color.GRAY);
        headerPanel.add(Box.createHorizontalStrut(30));
        headerPanel.add(lblNgay);

        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topCardsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        topCardsPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        lblDoanhThu = taoCard("Doanh thu hôm nay", "0 VNĐ", new Color(52, 152, 219), Color.WHITE);
        lblSoVeBan = taoCard("Vé bán ra", "0", new Color(46, 204, 113), Color.WHITE);
        lblSoKhachHang = taoCard("Khách hàng mới", "0", new Color(155, 89, 182), Color.WHITE);
        topCardsPanel.add(lblDoanhThu);
        topCardsPanel.add(lblSoVeBan);
        topCardsPanel.add(lblSoKhachHang);
        topCardsPanel.add(taoCard("Chuyến tàu hôm nay", "0", new Color(230, 126, 34), Color.WHITE));

        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.add(topCardsPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 15));

        centerPanel.add(taoPanelVeMoi());
        centerPanel.add(taoPanelHoaDonGanNhat());

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(cardsWrapper, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel taoCard(String tieuDe, String giaTri, Color mauNen, Color mauChu) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(mauNen);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTieuDe.setForeground(new Color(255, 255, 255, 200));
        card.add(lblTieuDe, BorderLayout.NORTH);

        JLabel lblGiaTri = new JLabel(giaTri);
        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblGiaTri.setForeground(mauChu);
        card.add(lblGiaTri, BorderLayout.CENTER);

        // Store the first 3 cards' value labels for external update
        if (lblGiaTriDoanhThu == null) {
            lblGiaTriDoanhThu = lblGiaTri;
        } else if (lblGiaTriSoVeBan == null) {
            lblGiaTriSoVeBan = lblGiaTri;
        } else if (lblGiaTriSoKhachHang == null) {
            lblGiaTriSoKhachHang = lblGiaTri;
        }

        return card;
    }

    private JPanel taoPanelVeMoi() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTieuDe = new JLabel("Vé bán gần đây");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(new Color(44, 62, 80));
        lblTieuDe.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTieuDe, BorderLayout.NORTH);

        String[] columns = {"Mã vé", "Khách hàng", "Ga đi", "Ga đến", "Giá"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblVeMoi = new JTable(model);
        tblVeMoi.setRowHeight(30);
        tblVeMoi.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblVeMoi.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblVeMoi.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblVeMoi.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblVeMoi.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblVeMoi);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoPanelHoaDonGanNhat() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTieuDe = new JLabel("Hóa đơn gần đây");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(new Color(44, 62, 80));
        lblTieuDe.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTieuDe, BorderLayout.NORTH);

        String[] columns = {"Mã HD", "Khách hàng", "NV lập", "Ngày lập", "Tổng tiền"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDonGanNhat = new JTable(model);
        tblHoaDonGanNhat.setRowHeight(30);
        tblHoaDonGanNhat.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblHoaDonGanNhat.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblHoaDonGanNhat.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblHoaDonGanNhat.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblHoaDonGanNhat.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblHoaDonGanNhat);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void taiDuLieu() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                return null;
            }

            @Override
            protected void done() {
                if (lblGiaTriDoanhThu != null) lblGiaTriDoanhThu.setText("0 VNĐ");
                if (lblGiaTriSoVeBan != null) lblGiaTriSoVeBan.setText("0");
                if (lblGiaTriSoKhachHang != null) lblGiaTriSoKhachHang.setText("0");
            }
        };
        worker.execute();
    }
}
