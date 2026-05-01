package JPA_Project.gui;

import JPA_Project.entity.*;
import JPA_Project.service.BanVeService;
import JPA_Project.repository.ChuyenTauRepository;
import JPA_Project.repository.KhachHangRepository;
import JPA_Project.repository.LoaiVeRepository;
import JPA_Project.repository.KhuyenMaiRepository;
import JPA_Project.repository.HoaDonRepository;
import JPA_Project.repository.VeRepository;
import JPA_Project.repository.GaRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

public class BanVePanel extends JPanel {

    private final NhanVien nhanVien;
    private final BanVeService banVeService;
    private final ChuyenTauRepository chuyenTauRepository;
    private final KhachHangRepository khachHangRepository;
    private final LoaiVeRepository loaiVeRepository;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final HoaDonRepository hoaDonRepository;
    private final VeRepository veRepository;
    private final GaRepository gaRepository;

    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JSpinner spnNgayDi;
    private JButton btnTimChuyen;
    private JTable tblChuyenTau;
    private DefaultTableModel tableModelChuyen;

    private JPanel pnlSoDoGhe;
    private Map<String, JButton> seatButtonsMap = new HashMap<>();
    private Map<String, Double> giaVeMap = new HashMap<>();
    private String maChuyenTauChon = null;
    private String maChoDaChon = null;
    private double giaVeHienTai = 0;

    private JComboBox<LoaiVe> cbLoaiVe;
    private JComboBox<KhuyenMai> cbKhuyenMai;
    private JTextField txtGiaVe;
    private JLabel lblTongTien;
    private JLabel lblSoGhe;

    private JPanel pnlDanhSachKhach;
    private Map<String, JPanel> khachPanelsMap = new HashMap<>();
    private int soKhachDaThem = 0;

    private JButton btnThemKhach;
    private JButton btnXoaKhach;
    private JButton btnBanVe;
    private JButton btnHuy;

    private List<LoaiVe> danhSachLoaiVe;
    private List<KhuyenMai> danhSachKhuyenMai;

    public BanVePanel(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        this.banVeService = new BanVeService();
        this.chuyenTauRepository = new ChuyenTauRepository();
        this.khachHangRepository = new KhachHangRepository();
        this.loaiVeRepository = new LoaiVeRepository();
        this.khuyenMaiRepository = new KhuyenMaiRepository();
        this.hoaDonRepository = new HoaDonRepository();
        this.veRepository = new VeRepository();
        this.gaRepository = new GaRepository();

        initComponents();
        loadComboBoxData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        chia.setDividerSize(5);
        chia.setBorder(null);

        chia.setLeftComponent(taoPanelTrai());
        chia.setRightComponent(taoPanelPhai());

        SwingUtilities.invokeLater(() -> chia.setDividerLocation(0.65));

        add(chia, BorderLayout.CENTER);
        add(taoPanelSouth(), BorderLayout.SOUTH);
    }

    private JPanel taoPanelTrai() {
        JPanel panelTrai = new JPanel();
        panelTrai.setLayout(new BoxLayout(panelTrai, BoxLayout.Y_AXIS));
        panelTrai.setBackground(Color.WHITE);
        panelTrai.setBorder(new CompoundBorder(
                new LineBorder(Color.GRAY, 2, false),
                new EmptyBorder(5, 5, 5, 5)
        ));

        panelTrai.add(taoKhuVucTimKiem());
        panelTrai.add(Box.createVerticalStrut(10));
        panelTrai.add(taoKhuVucDanhSachChuyenTau());
        panelTrai.add(Box.createVerticalStrut(10));
        panelTrai.add(taoKhuVucLoaiVeVaKhuyenMai());

        return panelTrai;
    }

    private JPanel taoKhuVucTimKiem() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Tìm chuyến tàu"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Ga đi:"));
        cbGaDi = new JComboBox<>();
        cbGaDi.setPreferredSize(new Dimension(200, 25));
        row1.add(cbGaDi);
        panel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Ga đến:"));
        cbGaDen = new JComboBox<>();
        cbGaDen.setPreferredSize(new Dimension(200, 25));
        row2.add(cbGaDen);
        panel.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Ngày đi:"));
        spnNgayDi = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgayDi, "dd/MM/yyyy");
        spnNgayDi.setEditor(dateEditor);
        spnNgayDi.setPreferredSize(new Dimension(120, 25));
        row3.add(spnNgayDi);
        btnTimChuyen = new JButton("Tìm chuyến");
        btnTimChuyen.setBackground(new Color(52, 152, 219));
        btnTimChuyen.setForeground(Color.WHITE);
        btnTimChuyen.addActionListener(e -> timChuyenTau());
        row3.add(btnTimChuyen);
        panel.add(row3);

        return panel;
    }

    private JPanel taoKhuVucDanhSachChuyenTau() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách chuyến tàu"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        String[] columns = {"Mã CT", "Ga đi", "Ga đến", "Giờ đi", "Ngày", "Tàu"};
        tableModelChuyen = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChuyenTau = new JTable(tableModelChuyen);
        tblChuyenTau.setRowHeight(25);
        tblChuyenTau.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblChuyenTau.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    chonChuyenTau(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblChuyenTau);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoKhuVucLoaiVeVaKhuyenMai() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin vé"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Loại vé:"));
        cbLoaiVe = new JComboBox<>();
        cbLoaiVe.setPreferredSize(new Dimension(200, 25));
        cbLoaiVe.addActionListener(e -> tinhLaiGia());
        row1.add(cbLoaiVe);
        panel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Khuyến mãi:"));
        cbKhuyenMai = new JComboBox<>();
        cbKhuyenMai.setPreferredSize(new Dimension(200, 25));
        cbKhuyenMai.addActionListener(e -> tinhLaiGia());
        row2.add(cbKhuyenMai);
        panel.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Giá vé:"));
        txtGiaVe = new JTextField(15);
        txtGiaVe.setEditable(false);
        txtGiaVe.setFont(new Font("Arial", Font.BOLD, 14));
        txtGiaVe.setForeground(new Color(192, 57, 43));
        row3.add(txtGiaVe);
        panel.add(row3);

        return panel;
    }

    private JPanel taoPanelPhai() {
        JPanel panelPhai = new JPanel();
        panelPhai.setLayout(new BoxLayout(panelPhai, BoxLayout.Y_AXIS));
        panelPhai.setBackground(Color.WHITE);
        panelPhai.setBorder(new CompoundBorder(
                new LineBorder(Color.GRAY, 2, false),
                new EmptyBorder(5, 5, 5, 5)
        ));

        panelPhai.add(taoKhuVucSoDoGhe());
        panelPhai.add(Box.createVerticalStrut(10));
        panelPhai.add(taoKhuVucDanhSachKhachHang());

        return panelPhai;
    }

    private JPanel taoKhuVucSoDoGhe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Sơ đồ ghế"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        pnlSoDoGhe = new JPanel(new GridLayout(8, 4, 5, 5));
        pnlSoDoGhe.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlSoDoGhe.setBackground(Color.WHITE);

        taoCacNutGhe();

        JScrollPane scrollPane = new JScrollPane(pnlSoDoGhe);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBackground(Color.WHITE);

        JLabel lblTrong = new JLabel("■ Trống");
        lblTrong.setForeground(new Color(46, 204, 113));
        legendPanel.add(lblTrong);

        JLabel lblDaBan = new JLabel("■ Đã bán");
        lblDaBan.setForeground(Color.RED);
        legendPanel.add(Box.createHorizontalStrut(20));
        legendPanel.add(lblDaBan);

        JLabel lblDaChon = new JLabel("■ Đã chọn");
        lblDaChon.setForeground(new Color(52, 152, 219));
        legendPanel.add(Box.createHorizontalStrut(20));
        legendPanel.add(lblDaChon);

        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void taoCacNutGhe() {
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        giaVeMap.clear();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 4; col++) {
                int soGhe = row * 4 + col + 1;
                String maCho = String.format("C%02d", soGhe);
                String label = (char) ('A' + col) + "" + (row + 1);

                JButton btnGhe = new JButton(label);
                btnGhe.setPreferredSize(new Dimension(70, 40));
                btnGhe.setBackground(new Color(200, 230, 200));
                btnGhe.setForeground(Color.DARK_GRAY);
                btnGhe.setFont(new Font("Arial", Font.BOLD, 12));
                btnGhe.setFocusPainted(false);
                btnGhe.setCursor(new Cursor(Cursor.HAND_CURSOR));

                final String maChoF = maCho;
                final String labelF = label;

                btnGhe.addActionListener(e -> chonGhe(maChoF, labelF, btnGhe));

                seatButtonsMap.put(maCho, btnGhe);
                pnlSoDoGhe.add(btnGhe);
            }
        }
    }

    private void chonGhe(String maCho, String label, JButton btnGhe) {
        Color bg = btnGhe.getBackground();
        if (bg.equals(Color.RED) || bg.equals(new Color(255, 182, 193))) {
            return;
        }

        if (maChoDaChon != null && seatButtonsMap.containsKey(maChoDaChon)) {
            seatButtonsMap.get(maChoDaChon).setBackground(new Color(200, 230, 200));
        }

        if (maChoDaChon != null && maChoDaChon.equals(maCho)) {
            maChoDaChon = null;
        } else {
            maChoDaChon = maCho;
            btnGhe.setBackground(new Color(52, 152, 219));
            btnGhe.setForeground(Color.WHITE);
        }

        tinhLaiGia();
    }

    private JPanel taoKhuVucDanhSachKhachHang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách khách hàng"));

        pnlDanhSachKhach = new JPanel();
        pnlDanhSachKhach.setLayout(new BoxLayout(pnlDanhSachKhach, BoxLayout.Y_AXIS));
        pnlDanhSachKhach.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pnlDanhSachKhach);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        btnThemKhach = new JButton("+ Thêm khách");
        btnThemKhach.setBackground(new Color(46, 204, 113));
        btnThemKhach.setForeground(Color.WHITE);
        btnThemKhach.addActionListener(e -> themKhachHangPanel());

        btnXoaKhach = new JButton("Xóa");
        btnXoaKhach.setBackground(Color.RED);
        btnXoaKhach.setForeground(Color.WHITE);
        btnXoaKhach.addActionListener(e -> xoaKhachHangPanel());

        buttonPanel.add(btnThemKhach);
        buttonPanel.add(btnXoaKhach);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        themKhachHangPanel();

        return panel;
    }

    private void themKhachHangPanel() {
        soKhachDaThem++;
        String id = "khach_" + soKhachDaThem;

        JPanel khachPanel = new JPanel();
        khachPanel.setLayout(new BoxLayout(khachPanel, BoxLayout.Y_AXIS));
        khachPanel.setBorder(BorderFactory.createTitledBorder("Khách " + soKhachDaThem));
        khachPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        khachPanel.setBackground(Color.WHITE);

        JPanel rowMa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowMa.add(new JLabel("CCCD:"));
        JTextField txtCCCD = new JTextField(15);
        JTextField txtTen = new JTextField(20);
        JTextField txtSDT = new JTextField(15);
        JButton btnTim = new JButton("Tìm");

        btnTim.addActionListener(e -> timKhachHang(txtCCCD.getText(), txtTen, txtSDT));
        rowMa.add(txtCCCD);
        rowMa.add(btnTim);
        khachPanel.add(rowMa);

        JPanel rowTen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowTen.add(new JLabel("Tên:"));
        rowTen.add(txtTen);
        khachPanel.add(rowTen);

        JPanel rowSDT = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowSDT.add(new JLabel("SDT:"));
        rowSDT.add(txtSDT);
        khachPanel.add(rowSDT);

        khachPanelsMap.put(id, khachPanel);
        pnlDanhSachKhach.add(khachPanel);
        pnlDanhSachKhach.revalidate();
        pnlDanhSachKhach.repaint();
    }

    private void xoaKhachHangPanel() {
        if (soKhachDaThem > 1) {
            String id = "khach_" + soKhachDaThem;
            JPanel panel = khachPanelsMap.remove(id);
            if (panel != null) {
                pnlDanhSachKhach.remove(panel);
                soKhachDaThem--;
                pnlDanhSachKhach.revalidate();
                pnlDanhSachKhach.repaint();
            }
        }
    }

    private void timKhachHang(String cccd, JTextField txtTen, JTextField txtSDT) {
        SwingWorker<KhachHang, Void> worker = new SwingWorker<>() {
            @Override
            protected KhachHang doInBackground() {
                return khachHangRepository.findByCccd(cccd);
            }

            @Override
            protected void done() {
                try {
                    KhachHang kh = get();
                    if (kh != null) {
                        txtTen.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
                        txtSDT.setText(kh.getSdt() != null ? kh.getSdt() : "");
                    } else {
                        JOptionPane.showMessageDialog(BanVePanel.this, "Không tìm thấy khách hàng!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BanVePanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private JPanel taoPanelSouth() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);

        leftPanel.add(new JLabel("Tổng tiền:"));
        lblTongTien = new JLabel("0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 18));
        lblTongTien.setForeground(new Color(192, 57, 43));
        leftPanel.add(lblTongTien);

        leftPanel.add(Box.createHorizontalStrut(30));
        leftPanel.add(new JLabel("Số ghế:"));
        lblSoGhe = new JLabel("0");
        lblSoGhe.setFont(new Font("Arial", Font.BOLD, 16));
        leftPanel.add(lblSoGhe);

        panel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);

        btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 35));
        btnHuy.setBackground(Color.GRAY);
        btnHuy.setForeground(Color.WHITE);
        btnHuy.addActionListener(e -> lamMoi());

        btnBanVe = new JButton("Bán vé");
        btnBanVe.setPreferredSize(new Dimension(100, 35));
        btnBanVe.setBackground(new Color(46, 204, 113));
        btnBanVe.setForeground(Color.WHITE);
        btnBanVe.addActionListener(e -> banVe());

        rightPanel.add(btnHuy);
        rightPanel.add(btnBanVe);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void loadComboBoxData() {
        try {
            List<LoaiVe> dsLoaiVe = loaiVeRepository.findAll();
            danhSachLoaiVe = dsLoaiVe;
            cbLoaiVe.removeAllItems();
            for (LoaiVe lv : dsLoaiVe) {
                cbLoaiVe.addItem(lv);
            }

            List<KhuyenMai> dsKM = khuyenMaiRepository.findConHieuLuc(LocalDateTime.now());
            danhSachKhuyenMai = dsKM;
            cbKhuyenMai.removeAllItems();
            cbKhuyenMai.addItem(null);
            for (KhuyenMai km : dsKM) {
                cbKhuyenMai.addItem(km);
            }

            List<Ga> dsGa = gaRepository.findAll();
            cbGaDi.removeAllItems();
            cbGaDen.removeAllItems();
            for (Ga ga : dsGa) {
                cbGaDi.addItem(ga);
                cbGaDen.addItem(ga);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void timChuyenTau() {
        Ga gaDi = (Ga) cbGaDi.getSelectedItem();
        Ga gaDen = (Ga) cbGaDen.getSelectedItem();

        if (gaDi == null || gaDen == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ga đi và ga đến!");
            return;
        }

        if (gaDi.getMaGa().equals(gaDen.getMaGa())) {
            JOptionPane.showMessageDialog(this, "Ga đi và ga đến không được trùng nhau!");
            return;
        }

        java.util.Date selectedDate = (java.util.Date) spnNgayDi.getValue();
        LocalDate ngayDi = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        SwingWorker<List<ChuyenTau>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChuyenTau> doInBackground() {
                return chuyenTauRepository.findByGaDiGaDenNgay(gaDi.getMaGa(), gaDen.getMaGa(), ngayDi);
            }

            @Override
            protected void done() {
                try {
                    List<ChuyenTau> ds = get();
                    tableModelChuyen.setRowCount(0);
                    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
                    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    for (ChuyenTau ct : ds) {
                        String gaDiStr = ct.getGaDi() != null ? ct.getGaDi().getTenGa() : "";
                        String gaDenStr = ct.getGaDen() != null ? ct.getGaDen().getTenGa() : "";
                        String gioDi = ct.getGioKhoiHanh() != null ? ct.getGioKhoiHanh().format(timeFmt) : "";
                        String ngay = ct.getNgayKhoiHanh() != null ? ct.getNgayKhoiHanh().format(dateFmt) : "";
                        String tau = ct.getMaTau() != null ? ct.getMaTau() : "";

                        tableModelChuyen.addRow(new Object[]{
                            ct.getMaChuyenTau(), gaDiStr, gaDenStr, gioDi, ngay, tau
                        });
                    }

                    if (ds.isEmpty()) {
                        JOptionPane.showMessageDialog(BanVePanel.this, "Không có chuyến tàu nào!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BanVePanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void chonChuyenTau(int row) {
        maChuyenTauChon = (String) tableModelChuyen.getValueAt(row, 0);
        taiTrangThaiGhe();
    }

    private void taiTrangThaiGhe() {
        for (JButton btn : seatButtonsMap.values()) {
            btn.setBackground(new Color(200, 230, 200));
            btn.setForeground(Color.DARK_GRAY);
        }

        if (maChuyenTauChon == null) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                return null;
            }

            @Override
            protected void done() {
                for (String maCho : seatButtonsMap.keySet()) {
                    List<Ve> ves = veRepository.findByMaChuyenTauVaMaCho(maChuyenTauChon, maCho);
                    boolean daBan = ves.stream().anyMatch(v -> "DA_BAN".equals(v.getTrangThai()));
                    if (daBan) {
                        seatButtonsMap.get(maCho).setBackground(Color.RED);
                        seatButtonsMap.get(maCho).setForeground(Color.WHITE);
                    }
                }
            }
        };
        worker.execute();
    }

    private void tinhLaiGia() {
        LoaiVe loaiVe = (LoaiVe) cbLoaiVe.getSelectedItem();
        KhuyenMai khuyenMai = (KhuyenMai) cbKhuyenMai.getSelectedItem();

        int soGhe = maChoDaChon != null ? 1 : 0;
        lblSoGhe.setText(String.valueOf(soGhe));

        double giaMotVe = giaVeHienTai;

        if (loaiVe != null) {
            giaMotVe = giaMotVe * (1 - loaiVe.getMucGiamGia());
        }

        if (khuyenMai != null && khuyenMai.getGiaTriGiam() != null) {
            if ("PHAN_TRAM_GIA".equals(khuyenMai.getLoaiKM())) {
                double phanTram = khuyenMai.getGiaTriGiam().doubleValue() / 100.0;
                giaMotVe = giaMotVe * (1 - phanTram);
            } else if ("CO_DINH".equals(khuyenMai.getLoaiKM())) {
                giaMotVe = giaMotVe - khuyenMai.getGiaTriGiam().doubleValue();
            }
        }

        giaMotVe = Math.max(giaMotVe, 0);
        txtGiaVe.setText(String.format("%.0f VNĐ", giaMotVe));

        double tongTien = giaMotVe * soGhe;
        lblTongTien.setText(String.format("%.0f VNĐ", tongTien));
    }

    private void banVe() {
        if (maChuyenTauChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến tàu!");
            return;
        }

        if (maChoDaChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ghế!");
            return;
        }

        LoaiVe loaiVe = (LoaiVe) cbLoaiVe.getSelectedItem();
        KhuyenMai khuyenMai = (KhuyenMai) cbKhuyenMai.getSelectedItem();

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    for (JPanel khachPanel : khachPanelsMap.values()) {
                        Component[] components = khachPanel.getComponents();
                        JTextField txtCCCD = null, txtTen = null, txtSDT = null;

                        for (Component c : components) {
                            if (c instanceof JPanel) {
                                JPanel row = (JPanel) c;
                                for (Component comp : row.getComponents()) {
                                    if (comp instanceof JTextField) {
                                        JTextField tf = (JTextField) comp;
                                        if (txtCCCD == null) txtCCCD = tf;
                                        else if (txtTen == null) txtTen = tf;
                                        else if (txtSDT == null) txtSDT = tf;
                                    }
                                }
                            }
                        }

                        String cccd = txtCCCD != null ? txtCCCD.getText().trim() : "";
                        String ten = txtTen != null ? txtTen.getText().trim() : "";
                        String sdt = txtSDT != null ? txtSDT.getText().trim() : "";

                        if (cccd.isEmpty() || ten.isEmpty() || sdt.isEmpty()) {
                            return false;
                        }

                        KhachHang kh = khachHangRepository.findByCccd(cccd);
                        if (kh == null) {
                            String maKH = khachHangRepository.taoMaKhachHangMoi();
                            kh = KhachHang.builder()
                                    .maKH(maKH)
                                    .hoTen(ten)
                                    .soCCCD(cccd)
                                    .sdt(sdt)
                                    .build();
                            khachHangRepository.save(kh);
                        }

                        BigDecimal giaVeBD = banVeService.tinhGiaVe(maChuyenTauChon, 
                                loaiVe != null ? loaiVe.getMaLoaiVe() : "VT01", 
                                null, 
                                khuyenMai != null ? khuyenMai.getMaKM() : null);
                        double giaVe = giaVeBD != null ? giaVeBD.doubleValue() : 0.0;

                        Ve ve = Ve.builder()
                                .maVe(banVeService.taoMaVe())
                                .maChuyenTau(maChuyenTauChon)
                                .maKhachHang(kh.getMaKH())
                                .maLoaiVe(loaiVe != null ? loaiVe.getMaLoaiVe() : null)
                                .maChoDat(maChoDaChon)
                                .maNV(nhanVien.getMaNV())
                                .giaVe(giaVe)
                                .trangThai("DA_BAN")
                                .build();

                        veRepository.save(ve);

                        HoaDon hoaDon = HoaDon.builder()
                                .maHD("HD" + System.currentTimeMillis())
                                .maKhachHang(kh.getMaKH())
                                .maNVLap(nhanVien.getMaNV())
                                .maKM(khuyenMai != null ? khuyenMai.getMaKM() : null)
                                .tongTien(giaVe)
                                .tongCong(giaVe)
                                .ngayLap(LocalDateTime.now())
                                .phuongThuc("TIEN_MAT")
                                .loaiHoaDon("BAN_VE")
                                .build();

                        hoaDonRepository.save(hoaDon);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(BanVePanel.this, "Bán vé thành công!");
                        lamMoi();
                    } else {
                        JOptionPane.showMessageDialog(BanVePanel.this, "Vui lòng nhập đầy đủ thông tin khách hàng!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BanVePanel.this, "Lỗi: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void lamMoi() {
        maChuyenTauChon = null;
        maChoDaChon = null;
        tableModelChuyen.setRowCount(0);
        taoCacNutGhe();
        lblTongTien.setText("0 VNĐ");
        lblSoGhe.setText("0");
        txtGiaVe.setText("");

        pnlDanhSachKhach.removeAll();
        khachPanelsMap.clear();
        soKhachDaThem = 0;
        themKhachHangPanel();
    }
}
