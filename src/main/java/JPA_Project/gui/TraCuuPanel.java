package JPA_Project.gui;

import JPA_Project.entity.Ve;
import JPA_Project.entity.HoaDon;
import JPA_Project.entity.ChiTietHoaDon;
import JPA_Project.repository.VeRepository;
import JPA_Project.repository.HoaDonRepository;
import JPA_Project.repository.KhachHangRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TraCuuPanel extends JPanel {
    private final VeRepository veRepository;
    private final HoaDonRepository hoaDonRepository;
    private final KhachHangRepository khachHangRepository;

    private JTabbedPane tabbedPane;

    private JComboBox<String> cbLoaiTimKiem;
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JButton btnTaiLai;
    private JTable tblKetQua;

    private JComboBox<String> cbLoaiTimKiemHD;
    private JTextField txtTimKiemHD;
    private JButton btnTimKiemHD;
    private JButton btnTaiLaiHD;
    private JTable tblHoaDon;
    private JButton btnXemChiTiet;

    public TraCuuPanel() {
        this.veRepository = new VeRepository();
        this.hoaDonRepository = new HoaDonRepository();
        this.khachHangRepository = new KhachHangRepository();

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblTieuDe = new JLabel("TRA CỨU");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTieuDe.setForeground(new Color(44, 62, 80));
        headerPanel.add(lblTieuDe);

        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        tabbedPane.addTab("Tra cứu vé", taoPanelTraCuuVe());
        tabbedPane.addTab("Tra cứu hóa đơn", taoPanelTraCuuHoaDon());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel taoPanelTraCuuVe() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        searchPanel.add(new JLabel("Tìm theo:"));
        cbLoaiTimKiem = new JComboBox<>(new String[]{"Mã vé", "CCCD", "Số điện thoại", "Họ tên"});
        cbLoaiTimKiem.setPreferredSize(new Dimension(150, 25));
        searchPanel.add(cbLoaiTimKiem);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(200, 25));
        searchPanel.add(txtTimKiem);

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(new Color(52, 152, 219));
        btnTimKiem.setForeground(Color.WHITE);

        btnTimKiem.setOpaque(true);
        btnTimKiem.setBorderPainted(false);

        btnTimKiem.addActionListener(e -> timKiemVe());
        searchPanel.add(btnTimKiem);

        btnTaiLai = new JButton("Tải lại");
        btnTaiLai.addActionListener(e -> taiLaiDanhSachVe());
        searchPanel.add(btnTaiLai);

        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"Mã vé", "Tên khách hàng", "CCCD", "SĐT", "Mã chuyến tàu", "Ga đi", "Ga đến", "Giá vé", "Trạng thái"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKetQua = new JTable(model);
        tblKetQua.setRowHeight(25);
        tblKetQua.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblKetQua.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblKetQua.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblKetQua.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblKetQua.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblKetQua.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblKetQua.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblKetQua.getColumnModel().getColumn(7).setPreferredWidth(100);
        tblKetQua.getColumnModel().getColumn(8).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblKetQua);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoPanelTraCuuHoaDon() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        searchPanel.add(new JLabel("Tìm theo:"));
        cbLoaiTimKiemHD = new JComboBox<>(new String[]{"Mã hóa đơn", "CCCD", "Số điện thoại"});
        cbLoaiTimKiemHD.setPreferredSize(new Dimension(150, 25));
        searchPanel.add(cbLoaiTimKiemHD);

        txtTimKiemHD = new JTextField(20);
        txtTimKiemHD.setPreferredSize(new Dimension(200, 25));
        searchPanel.add(txtTimKiemHD);

        btnTimKiemHD = new JButton("Tìm kiếm");
        btnTimKiemHD.setBackground(new Color(52, 152, 219));
        btnTimKiemHD.setForeground(Color.WHITE);
        btnTimKiemHD.setOpaque(true);
        btnTimKiemHD.setBorderPainted(false);

        btnTimKiemHD.addActionListener(e -> timKiemHoaDon());
        searchPanel.add(btnTimKiemHD);

        btnTaiLaiHD = new JButton("Tải lại");
        btnTaiLaiHD.addActionListener(e -> taiLaiDanhSachHD());
        searchPanel.add(btnTaiLaiHD);

        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"Mã HD", "Khách hàng", "NV lập", "Ngày lập", "Tổng cộng", "Tổng tiền", "Phương thức", "Khuyến mãi", "Chi tiết"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDon = new JTable(model);
        tblHoaDon.setRowHeight(25);
        tblHoaDon.getColumnModel().getColumn(0).setPreferredWidth(130);
        tblHoaDon.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblHoaDon.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblHoaDon.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblHoaDon.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblHoaDon.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblHoaDon.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblHoaDon.getColumnModel().getColumn(7).setPreferredWidth(120);
        tblHoaDon.getColumnModel().getColumn(8).setPreferredWidth(80);

        MouseAdapter buttonClickHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblHoaDon.rowAtPoint(e.getPoint());
                int col = tblHoaDon.columnAtPoint(e.getPoint());
                if (col == 8 && row >= 0) {
                    String maHD = (String) tblHoaDon.getValueAt(row, 0);
                    if (maHD != null && !maHD.isEmpty()) {
                        hienThiChiTietHoaDon(maHD);
                    }
                }
            }
        };
        tblHoaDon.addMouseListener(buttonClickHandler);

        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void timKiemVe() {
        String loaiTimKiem = (String) cbLoaiTimKiem.getSelectedItem();
        String giaTri = txtTimKiem.getText().trim();

        if (giaTri.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin tìm kiếm!");
            return;
        }

        SwingWorker<List<Ve>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Ve> doInBackground() {
                String hoTen = null, sdt = null, cccd = null, maVe = null;

                switch (loaiTimKiem) {
                    case "Mã vé":
                        maVe = giaTri;
                        break;
                    case "CCCD":
                        cccd = giaTri;
                        break;
                    case "Số điện thoại":
                        sdt = giaTri;
                        break;
                    case "Họ tên":
                        hoTen = giaTri;
                        break;
                }

                return veRepository.timVeTheoKhachHangDetailed(hoTen, sdt, cccd, maVe);
            }

            @Override
            protected void done() {
                try {
                    List<Ve> dsVe = get();
                    hienThiDanhSachVe(dsVe);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TraCuuPanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void hienThiDanhSachVe(List<Ve> dsVe) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblKetQua.getModel();
        model.setRowCount(0);

        for (Ve ve : dsVe) {
            String tenKH = "", cccd = "", sdt = "", gaDi = "", gaDen = "";

            if (ve.getKhachHang() != null) {
                tenKH = ve.getKhachHang().getHoTen() != null ? ve.getKhachHang().getHoTen() : "";
                cccd = ve.getKhachHang().getSoCCCD() != null ? ve.getKhachHang().getSoCCCD() : "";
                sdt = ve.getKhachHang().getSdt() != null ? ve.getKhachHang().getSdt() : "";
            }

            if (ve.getChuyenTau() != null) {
                if (ve.getChuyenTau().getGaDi() != null) {
                    gaDi = ve.getChuyenTau().getGaDi().getTenGa() != null ? ve.getChuyenTau().getGaDi().getTenGa() : "";
                }
                if (ve.getChuyenTau().getGaDen() != null) {
                    gaDen = ve.getChuyenTau().getGaDen().getTenGa() != null ? ve.getChuyenTau().getGaDen().getTenGa() : "";
                }
            }

            model.addRow(new Object[]{
                ve.getMaVe() != null ? ve.getMaVe() : "",
                tenKH, cccd, sdt,
                ve.getMaChuyenTau() != null ? ve.getMaChuyenTau() : "",
                gaDi, gaDen,
                ve.getGiaVe() != null ? String.format("%.0f VNĐ", ve.getGiaVe()) : "0 VNĐ",
                ve.getTrangThai() != null ? ve.getTrangThai() : ""
            });
        }
    }

    private void taiLaiDanhSachVe() {
        SwingWorker<List<Ve>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Ve> doInBackground() {
                return veRepository.findByTrangThai("DA_BAN");
            }

            @Override
            protected void done() {
                try {
                    hienThiDanhSachVe(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TraCuuPanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void timKiemHoaDon() {
        String loaiTimKiem = (String) cbLoaiTimKiemHD.getSelectedItem();
        String giaTri = txtTimKiemHD.getText().trim();

        if (giaTri.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin tìm kiếm!");
            return;
        }

        SwingWorker<List<HoaDon>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HoaDon> doInBackground() {
                switch (loaiTimKiem) {
                    case "CCCD":
                        return hoaDonRepository.findByCccdDetailed(giaTri);
                    case "Số điện thoại":
                        return hoaDonRepository.findBySoDienThoaiDetailed(giaTri);
                    default:
                        return hoaDonRepository.findByMaHDDetailed(giaTri);
                }
            }

            @Override
            protected void done() {
                try {
                    List<HoaDon> dsHD = get();
                    hienThiDanhSachHD(dsHD);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TraCuuPanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void hienThiDanhSachHD(List<HoaDon> dsHD) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblHoaDon.getModel();
        model.setRowCount(0);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (HoaDon hd : dsHD) {
            String tenKH = "";
            if (hd.getKhachHang() != null) {
                tenKH = hd.getKhachHang().getHoTen() != null ? hd.getKhachHang().getHoTen() : "";
            }

            String tenKM = "";
            if (hd.getKhuyenMai() != null) {
                tenKM = hd.getKhuyenMai().getTenKM() != null ? hd.getKhuyenMai().getTenKM() : "";
            }

            model.addRow(new Object[]{
                hd.getMaHD() != null ? hd.getMaHD() : "",
                tenKH,
                hd.getMaNVLap() != null ? hd.getMaNVLap() : "",
                hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "",
                String.format("%.0f", hd.getTongCong()),
                String.format("%.0f", hd.getTongTien()),
                hd.getPhuongThuc() != null ? hd.getPhuongThuc() : "",
                tenKM,
                "Xem"
            });
        }
    }

    private void taiLaiDanhSachHD() {
        SwingWorker<List<HoaDon>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HoaDon> doInBackground() {
                return hoaDonRepository.findAllWithDetails();
            }

            @Override
            protected void done() {
                try {
                    hienThiDanhSachHD(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TraCuuPanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void hienThiChiTietHoaDon(String maHD) {
        SwingWorker<HoaDon, Void> worker = new SwingWorker<>() {
            @Override
            protected HoaDon doInBackground() {
                return hoaDonRepository.findByMaHDWithDetails(maHD).orElse(null);
            }

            @Override
            protected void done() {
                try {
                    HoaDon hd = get();
                    if (hd != null) {
                        hienThiPopupChiTiet(hd);
                    } else {
                        JOptionPane.showMessageDialog(TraCuuPanel.this, "Không tìm thấy hóa đơn!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TraCuuPanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void hienThiPopupChiTiet(HoaDon hd) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn " + hd.getMaHD(), true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(Color.WHITE);

        // Panel thông tin hóa đơn
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        addInfoRow(infoPanel, "Mã hóa đơn:", hd.getMaHD() != null ? hd.getMaHD() : "");
        addInfoRow(infoPanel, "Khách hàng:", hd.getKhachHang() != null ? hd.getKhachHang().getHoTen() : "");
        addInfoRow(infoPanel, "NV lập:", hd.getMaNVLap() != null ? hd.getMaNVLap() : "");
        addInfoRow(infoPanel, "Ngày lập:", hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "");
        addInfoRow(infoPanel, "Phương thức TT:", hd.getPhuongThuc() != null ? hd.getPhuongThuc() : "");
        String tenKM = "";
        if(hd.getKhuyenMai() != null) {
            tenKM = hd.getKhuyenMai().getTenKM() != "" ? hd.getKhuyenMai().getTenKM() : "Không có";
        }
        addInfoRow(infoPanel, "Khuyến mãi:", hd.getKhuyenMai() != null ?  tenKM : "Không có");

        contentPanel.add(infoPanel, BorderLayout.NORTH);

        // Panel chi tiết các vé
        JPanel chiTietPanel = new JPanel(new BorderLayout());
        chiTietPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết vé"));
        chiTietPanel.setBackground(Color.WHITE);

        String[] columns = {"Mã vé", "Mã chuyến tàu", "Mã chỗ", "Giá vé"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tblChiTiet = new JTable(model);
        tblChiTiet.setRowHeight(25);
        tblChiTiet.setFont(new Font("Arial", Font.PLAIN, 12));

        // Load chi tiết
        if (hd.getChiTietHoaDons() != null && !hd.getChiTietHoaDons().isEmpty()) {
            for (ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
                String maVe = ct.getMaVe() != null ? ct.getMaVe() : "";
                String maChuyen = "";
                String maCho = ct.getMaVe() != null ? ct.getMaVe() : "";
                
                // Lấy thông tin từ Ve nếu có
                if (ct.getVe() != null) {
                    maChuyen = ct.getVe().getMaChuyenTau() != null ? ct.getVe().getMaChuyenTau() : "";
                    maCho = ct.getVe().getMaChoDat() != null ? ct.getVe().getMaChoDat() : "";
                }
                
                model.addRow(new Object[]{
                    maVe,
                    maChuyen,
                    maCho,
                    ct.getDonGia() > 0 ? String.format("%.0f VNĐ", ct.getDonGia()) : "N/A"
                });
            }
        } else {
            model.addRow(new Object[]{"Không có chi tiết vé", "", "", ""});
        }

        JScrollPane scrollPane = new JScrollPane(tblChiTiet);
        chiTietPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(chiTietPanel, BorderLayout.CENTER);

        // Panel tổng tiền
        JPanel tongTienPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tongTienPanel.setBackground(Color.WHITE);
        tongTienPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblTongCong = new JLabel("Tổng cộng: " + String.format("%.0f VNĐ", hd.getTongCong() != null ? hd.getTongCong() : 0));
        lblTongCong.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongCong.setForeground(new Color(0, 102, 204));

        JLabel lblTongTien = new JLabel("Tổng tiền: " + String.format("%.0f VNĐ", hd.getTongTien()));
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTien.setForeground(new Color(220, 53, 69));

        tongTienPanel.add(lblTongCong);
        tongTienPanel.add(Box.createHorizontalStrut(30));
        tongTienPanel.add(lblTongTien);

        contentPanel.add(tongTienPanel, BorderLayout.SOUTH);

        dialog.setContentPane(contentPanel);
        dialog.setVisible(true);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lbl);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(valueLabel);
    }
}
