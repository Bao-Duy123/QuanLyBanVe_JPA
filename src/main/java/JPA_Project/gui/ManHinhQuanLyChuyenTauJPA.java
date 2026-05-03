package JPA_Project.gui;

import JPA_Project.entity.ChuyenTau;
import JPA_Project.entity.Ga;
import JPA_Project.entity.GaTrongTuyen;
import JPA_Project.entity.Tuyen;
import JPA_Project.entity.TrangThaiChuyenTau;
import JPA_Project.repository.ChuyenTauRepository;
import JPA_Project.repository.GaRepository;
import JPA_Project.repository.GaTrongTuyenRepository;
import JPA_Project.repository.TuyenRepository;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ManHinhQuanLyChuyenTauJPA - Quản lý chuyến tàu
 * TAB 1: Thiết lập Tuyến & Ga dừng
 * TAB 2: Quản lý Chuyến tàu & Lịch trình
 */
public class ManHinhQuanLyChuyenTauJPA extends JPanel {

    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color PRIMARY_BLUE = new Color(0, 102, 204);
    private static final Font FONT_BOLD_12 = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_10 = new Font("Segoe UI", Font.PLAIN, 10);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private JTabbedPane tabbedPane;

    // TAB 1 - Tuyến
    private JTable tbTuyen, tbGaTrongTuyen;
    private DefaultTableModel modelTuyen, modelGaTrongTuyen;
    private JTextField txtMaTuyen, txtTenTuyen, txtDonGiaKM;
    private JComboBox<Ga> cbGaDau, cbGaCuoi;
    private JComboBox<Tuyen> cbChonTuyen;

    // TAB 1 - Ga Trong Tuyến
    private JComboBox<Ga> cbChonGa;
    private JTextField txtThuTuGa, txtThoiGianDung;
    private JTable tbGaChon;

    // TAB 2 - Chuyến tàu
    private JTable tbChuyenTau;
    private DefaultTableModel modelChuyenTau;
    private JComboBox<Tuyen> cbTimTuyen;
    private JDateChooser dateTimKiem;
    private JTextField txtMaChuyenTau, txtGioKhoiHanh, txtGioDen;
    private JComboBox<String> cbTrangThai;
    private JComboBox<Ga> cbGaDi, cbGaDen;
    private JDateChooser dateNgayKhoiHanh, dateNgayDen;
    private JComboBox<String> cbTau;

    private TuyenRepository tuyenRepository;
    private GaRepository gaRepository;
    private GaTrongTuyenRepository gaTrongTuyenRepository;
    private ChuyenTauRepository chuyenTauRepository;

    public ManHinhQuanLyChuyenTauJPA() {
        tuyenRepository = new TuyenRepository();
        gaRepository = new GaRepository();
        gaTrongTuyenRepository = new GaTrongTuyenRepository();
        chuyenTauRepository = new ChuyenTauRepository();

        khoiTaoGiaoDien();
        taiDuLieuTuyen();
        loadDuLieuGa();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel tieuDe = new JLabel("QUẢN LÝ CHUYẾN TÀU");
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tieuDe.setForeground(PRIMARY_BLUE);
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        tieuDe.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(tieuDe, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_BOLD_12);

        // TAB 1: Thiết lập Tuyến & Ga dừng
        JPanel pnlTab1 = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlTab1.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlTab1.add(taoPanelTuyen());
        pnlTab1.add(taoPanelGaTrongTuyen());
        tabbedPane.addTab("1. Thiết lập Tuyến & Ga dừng", pnlTab1);

        // TAB 2: Quản lý Chuyến tàu & Lịch trình
        JPanel pnlTab2 = new JPanel(new BorderLayout(0, 10));
        pnlTab2.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlTab2.add(taoPanelTimKiemChuyenTau(), BorderLayout.CENTER);
        pnlTab2.add(taoPanelTaoLichTrinh(), BorderLayout.SOUTH);
        tabbedPane.addTab("2. Quản lý Chuyến tàu & Lịch trình", pnlTab2);

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ================================================================================
    // TAB 1: PANEL TUYẾN
    // ================================================================================

    private JPanel taoPanelTuyen() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin Tuyến"));
        panel.setBackground(Color.WHITE);

        // Form nhập liệu
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        pnlForm.add(new JLabel("Mã Tuyến:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtMaTuyen = new JTextField(15);
        pnlForm.add(txtMaTuyen, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        pnlForm.add(new JLabel("Tên Tuyến:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtTenTuyen = new JTextField(15);
        pnlForm.add(txtTenTuyen, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        pnlForm.add(new JLabel("Ga Đầu:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cbGaDau = new JComboBox<>();
        pnlForm.add(cbGaDau, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        pnlForm.add(new JLabel("Ga Cuối:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cbGaCuoi = new JComboBox<>();
        pnlForm.add(cbGaCuoi, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        pnlForm.add(new JLabel("Đơn giá/KM:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDonGiaKM = new JTextField(15);
        pnlForm.add(txtDonGiaKM, gbc);

        // Nút chức năng
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnThem = taoNutBam("Thêm Tuyến", PRIMARY_BLUE);
        JButton btnXoa = taoNutBam("Xóa Tuyến", new Color(220, 53, 69));
        JButton btnCapNhat = taoNutBam("Cập nhật", new Color(40, 167, 69));
        JButton btnLamMoi = taoNutBam("Làm mới", Color.GRAY);

        pnlNut.add(btnLamMoi);
        pnlNut.add(btnThem);
        pnlNut.add(btnCapNhat);
        pnlNut.add(btnXoa);

        // Bảng danh sách tuyến
        modelTuyen = new DefaultTableModel(
                new String[]{"Mã Tuyến", "Tên Tuyến", "Ga Đầu", "Ga Cuối", "Đơn giá/KM"}, 0
        );
        tbTuyen = new JTable(modelTuyen);
        tbTuyen.setRowHeight(25);
        tbTuyen.setFont(FONT_10);
        tbTuyen.getTableHeader().setFont(FONT_BOLD_12);
        tbTuyen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chonTuyenTuBang();
            }
        });

        // Sự kiện nút
        btnThem.addActionListener(e -> logicThemTuyen());
        btnXoa.addActionListener(e -> logicXoaTuyen());
        btnCapNhat.addActionListener(e -> logicCapNhatTuyen());
        btnLamMoi.addActionListener(e -> xoaTrangFormTuyen());

        panel.add(pnlForm, BorderLayout.NORTH);
        panel.add(new JScrollPane(tbTuyen), BorderLayout.CENTER);
        panel.add(pnlNut, BorderLayout.SOUTH);

        return panel;
    }

    // ================================================================================
    // TAB 1: PANEL GA TRONG TUYẾN
    // ================================================================================

    private JPanel taoPanelGaTrongTuyen() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Ga trong Tuyến"));
        panel.setBackground(Color.WHITE);

        // Chọn tuyến để quản lý Ga
        JPanel pnlChonTuyen = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlChonTuyen.add(new JLabel("Chọn Tuyến:"));
        cbChonTuyen = new JComboBox<>();
        cbChonTuyen.setPreferredSize(new Dimension(200, 25));
        pnlChonTuyen.add(cbChonTuyen);

        JButton btnTaiGa = taoNutBam("Tải Ga", Color.GRAY); // Sử dụng helper để có giao diện đồng bộ
        btnTaiGa.addActionListener(e -> taiGaTrongTuyen());
        pnlChonTuyen.add(btnTaiGa);

        // Bảng Ga trong tuyến đã chọn
        modelGaTrongTuyen = new DefaultTableModel(
                new String[]{"STT", "Mã Ga", "Tên Ga", "Thứ tự", "Thời gian dừng"}, 0
        );
        tbGaTrongTuyen = new JTable(modelGaTrongTuyen);
        tbGaTrongTuyen.setRowHeight(25);
        tbGaTrongTuyen.setFont(FONT_10);
        tbGaTrongTuyen.getTableHeader().setFont(FONT_BOLD_12);

        // Panel thêm Ga mới vào tuyến
        JPanel pnlThemGa = new JPanel(new GridBagLayout());
        pnlThemGa.setBorder(BorderFactory.createTitledBorder("Thêm Ga vào Tuyến"));
        pnlThemGa.setBackground(Color.WHITE);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(3, 3, 3, 3);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        gbc2.gridx = 0; gbc2.gridy = 0;
        pnlThemGa.add(new JLabel("Chọn Ga:"), gbc2);
        gbc2.gridx = 1;
        cbChonGa = new JComboBox<>();
        cbChonGa.setPreferredSize(new Dimension(150, 22));
        pnlThemGa.add(cbChonGa, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1;
        pnlThemGa.add(new JLabel("Thứ tự:"), gbc2);
        gbc2.gridx = 1;
        txtThuTuGa = new JTextField(10);
        txtThuTuGa.setPreferredSize(new Dimension(80, 22));
        pnlThemGa.add(txtThuTuGa, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 2;
        pnlThemGa.add(new JLabel("TG Dừng (phút):"), gbc2);
        gbc2.gridx = 1;
        txtThoiGianDung = new JTextField("10", 10);
        txtThoiGianDung.setPreferredSize(new Dimension(80, 22));
        pnlThemGa.add(txtThoiGianDung, gbc2);

        gbc2.gridx = 1; gbc2.gridy = 3;
        JButton btnThemGa = taoNutBam("Thêm Ga", PRIMARY_BLUE);
        btnThemGa.addActionListener(e -> logicThemGaVaoTuyen());
        pnlThemGa.add(btnThemGa, gbc2);

        // Nút xóa Ga
        JPanel pnlNutGa = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnXoaGa = taoNutBam("Xóa Ga khỏi Tuyến", new Color(220, 53, 69));
        btnXoaGa.addActionListener(e -> logicXoaGaKhoiTuyen());
        pnlNutGa.add(btnXoaGa);

        // Sự kiện chọn tuyến
        cbChonTuyen.addActionListener(e -> taiGaTrongTuyen());

        panel.add(pnlChonTuyen, BorderLayout.NORTH);
        panel.add(new JScrollPane(tbGaTrongTuyen), BorderLayout.CENTER);
        panel.add(pnlThemGa, BorderLayout.SOUTH);
        panel.add(pnlNutGa, BorderLayout.PAGE_END);

        return panel;
    }

    // ================================================================================
    // TAB 2: PANEL TÌM KIẾM CHUYẾN TÀU
    // ================================================================================

    private JPanel taoPanelTimKiemChuyenTau() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Tra cứu Chuyến tàu"));
        panel.setBackground(Color.WHITE);

        // Filter tìm kiếm
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFilter.setBackground(Color.WHITE);

        pnlFilter.add(new JLabel("Tuyến:"));
        cbTimTuyen = new JComboBox<>();
        cbTimTuyen.setPreferredSize(new Dimension(180, 25));
        pnlFilter.add(cbTimTuyen);

        pnlFilter.add(new JLabel("Ngày:"));
        dateTimKiem = new JDateChooser();
        dateTimKiem.setPreferredSize(new Dimension(120, 25));
        pnlFilter.add(dateTimKiem);

        JButton btnTimKiem = taoNutBam("Tìm kiếm", PRIMARY_BLUE);
        pnlFilter.add(btnTimKiem);

        JButton btnTaiTatCa = taoNutBam("Tải tất cả", Color.GRAY);
        pnlFilter.add(btnTaiTatCa);

        // Bảng kết quả
        modelChuyenTau = new DefaultTableModel(
                new String[]{"Mã Chuyến", "Ngày KH", "Giờ KH", "Ga Đi", "Ga Đến", "Tàu", "Trạng Thái"}, 0
        );
        tbChuyenTau = new JTable(modelChuyenTau);
        tbChuyenTau.setRowHeight(25);
        tbChuyenTau.setFont(FONT_10);
        tbChuyenTau.getTableHeader().setFont(FONT_BOLD_12);
        tbChuyenTau.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chonChuyenTauTuBang();
            }
        });

        // Sự kiện
        btnTimKiem.addActionListener(e -> logicTimKiemChuyenTau());
        btnTaiTatCa.addActionListener(e -> taiTatCaChuyenTau());

        panel.add(pnlFilter, BorderLayout.NORTH);
        panel.add(new JScrollPane(tbChuyenTau), BorderLayout.CENTER);

        return panel;
    }

    // ================================================================================
    // TAB 2: PANEL TẠO LỊCH TRÌNH
    // ================================================================================

    private JPanel taoPanelTaoLichTrinh() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Tạo / Cập nhật Chuyến tàu"));
        panel.setBackground(Color.WHITE);

        // Form nhập liệu
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Hàng 1
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Mã Chuyến:"), gbc);
        gbc.gridx = 1;
        txtMaChuyenTau = new JTextField(12);
        pnlForm.add(txtMaChuyenTau, gbc);

        gbc.gridx = 2;
        pnlForm.add(new JLabel("Tuyến:"), gbc);
        gbc.gridx = 3;
        JComboBox<Tuyen> cbTuyenTao = new JComboBox<>();
        cbTuyenTao.setPreferredSize(new Dimension(150, 25));
        pnlForm.add(cbTuyenTao, gbc);

        // Hàng 2
        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(new JLabel("Ga Đi:"), gbc);
        gbc.gridx = 1;
        cbGaDi = new JComboBox<>();
        cbGaDi.setPreferredSize(new Dimension(150, 25));
        pnlForm.add(cbGaDi, gbc);

        gbc.gridx = 2;
        pnlForm.add(new JLabel("Ga Đến:"), gbc);
        gbc.gridx = 3;
        cbGaDen = new JComboBox<>();
        cbGaDen.setPreferredSize(new Dimension(150, 25));
        pnlForm.add(cbGaDen, gbc);

        // Hàng 3
        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(new JLabel("Ngày KH:"), gbc);
        gbc.gridx = 1;
        dateNgayKhoiHanh = new JDateChooser();
        dateNgayKhoiHanh.setPreferredSize(new Dimension(120, 25));
        pnlForm.add(dateNgayKhoiHanh, gbc);

        gbc.gridx = 2;
        pnlForm.add(new JLabel("Giờ KH:"), gbc);
        gbc.gridx = 3;
        txtGioKhoiHanh = new JTextField("08:00", 12);
        pnlForm.add(txtGioKhoiHanh, gbc);

        // Hàng 4
        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(new JLabel("Ngày Đến:"), gbc);
        gbc.gridx = 1;
        dateNgayDen = new JDateChooser();
        dateNgayDen.setPreferredSize(new Dimension(120, 25));
        pnlForm.add(dateNgayDen, gbc);

        gbc.gridx = 2;
        pnlForm.add(new JLabel("Giờ Đến:"), gbc);
        gbc.gridx = 3;
        txtGioDen = new JTextField("12:00", 12);
        pnlForm.add(txtGioDen, gbc);

        // Hàng 5
        gbc.gridx = 0; gbc.gridy = 4;
        pnlForm.add(new JLabel("Mã Tàu:"), gbc);
        gbc.gridx = 1;
        cbTau = new JComboBox<>();
        cbTau.setPreferredSize(new Dimension(150, 25));
        pnlForm.add(cbTau, gbc);

        gbc.gridx = 2;
        pnlForm.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3;
        cbTrangThai = new JComboBox<>(new String[]{"CHO_KHOI_HANH", "DANG_CHAY", "DA_HOAN_THANH", "DA_HUY"});
        cbTrangThai.setPreferredSize(new Dimension(150, 25));
        pnlForm.add(cbTrangThai, gbc);

        // Nút chức năng
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnThemChuyen = taoNutBam("Tạo Chuyến tàu", PRIMARY_BLUE);
        JButton btnCapNhatChuyen = taoNutBam("Cập nhật", new Color(40, 167, 69));
        JButton btnXoaChuyen = taoNutBam("Xóa Chuyến", new Color(220, 53, 69));
        JButton btnLamMoiChuyen = taoNutBam("Làm mới", Color.GRAY);

        pnlNut.add(btnLamMoiChuyen);
        pnlNut.add(btnThemChuyen);
        pnlNut.add(btnCapNhatChuyen);
        pnlNut.add(btnXoaChuyen);

        // Sự kiện
        btnThemChuyen.addActionListener(e -> logicTaoChuyenTau());
        btnCapNhatChuyen.addActionListener(e -> logicCapNhatChuyenTau());
        btnXoaChuyen.addActionListener(e -> logicXoaChuyenTau());
        btnLamMoiChuyen.addActionListener(e -> xoaTrangFormChuyenTau());

        panel.add(pnlForm, BorderLayout.CENTER);
        panel.add(pnlNut, BorderLayout.SOUTH);

        return panel;
    }

    // ================================================================================
    // LOAD DỮ LIỆU
    // ================================================================================

    private void taiDuLieuTuyen() {
        modelTuyen.setRowCount(0);
        cbTimTuyen.removeAllItems();

        List<Tuyen> ds = tuyenRepository.findAll();
        for (Tuyen t : ds) {
            modelTuyen.addRow(new Object[]{
                    t.getMaTuyen(),
                    t.getTenTuyen(),
                    t.getGaDau(),
                    t.getGaCuoi(),
                    t.getDonGiaKM()
            });
            cbTimTuyen.addItem(t);
            cbChonTuyen.addItem(t);
        }
    }

    private void loadDuLieuGa() {
        cbGaDau.removeAllItems();
        cbGaCuoi.removeAllItems();
        cbChonGa.removeAllItems();
        cbGaDi.removeAllItems();
        cbGaDen.removeAllItems();

        List<Ga> dsGa = gaRepository.findAll();
        for (Ga g : dsGa) {
            cbGaDau.addItem(g);
            cbGaCuoi.addItem(g);
            cbChonGa.addItem(g);
            cbGaDi.addItem(g);
            cbGaDen.addItem(g);
        }
    }

    private void taiGaTrongTuyen() {
        modelGaTrongTuyen.setRowCount(0);
        Tuyen tuyen = (Tuyen) cbChonTuyen.getSelectedItem();
        if (tuyen == null) return;

        try {
            List<GaTrongTuyen> ds = gaTrongTuyenRepository.findByMaTuyen(tuyen.getMaTuyen());
            for (GaTrongTuyen gt : ds) {
                String tenGa = gt.getGa() != null ? gt.getGa().getTenGa() : gt.getMaGa();
                modelGaTrongTuyen.addRow(new Object[]{
                        gt.getThuTuGa(),
                        gt.getMaGa(),
                        tenGa,
                        gt.getThuTuGa(),
                        gt.getThoiGianDung() + " phút"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải Ga: " + e.getMessage());
        }
    }

    private void taiTatCaChuyenTau() {
        modelChuyenTau.setRowCount(0);
        try {
            List<ChuyenTau> ds = chuyenTauRepository.findAll();
            for (ChuyenTau ct : ds) {
                modelChuyenTau.addRow(new Object[]{
                        ct.getMaChuyenTau(),
                        ct.getNgayKhoiHanh(),
                        ct.getGioKhoiHanh(),
                        ct.getGaDi() != null ? ct.getGaDi().getTenGa() : ct.getMaTuyen(),
                        ct.getGaDen() != null ? ct.getGaDen().getTenGa() : "",
                        ct.getMaTau(),
                        ct.getThct() != null ? ct.getThct().toString() : "Đang chờ"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    // ================================================================================
    // LOGIC TUYẾN
    // ================================================================================

    private void logicThemTuyen() {
        String ma = txtMaTuyen.getText().trim();
        String ten = txtTenTuyen.getText().trim();
        Ga gaDau = (Ga) cbGaDau.getSelectedItem();
        Ga gaCuoi = (Ga) cbGaCuoi.getSelectedItem();

        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã và Tên tuyến!");
            return;
        }

        try {
            int donGia = Integer.parseInt(txtDonGiaKM.getText().trim());

            Tuyen tuyen = Tuyen.builder()
                    .maTuyen(ma)
                    .tenTuyen(ten)
                    .gaDau(gaDau != null ? gaDau.getMaGa() : "")
                    .gaCuoi(gaCuoi != null ? gaCuoi.getMaGa() : "")
                    .donGiaKM(donGia)
                    .build();

            tuyenRepository.save(tuyen);
            JOptionPane.showMessageDialog(this, "Thêm Tuyến thành công!");
            taiDuLieuTuyen();
            xoaTrangFormTuyen();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá/KM phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logicXoaTuyen() {
        int row = tbTuyen.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tuyến cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tuyến này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            String maTuyen = (String) modelTuyen.getValueAt(row, 0);
            Tuyen tuyen = tuyenRepository.findById(maTuyen);
            if (tuyen != null) {
                tuyenRepository.delete(tuyen);
            }
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            taiDuLieuTuyen();
            xoaTrangFormTuyen();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logicCapNhatTuyen() {
        int row = tbTuyen.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tuyến cần cập nhật!");
            return;
        }

        try {
            String ma = txtMaTuyen.getText().trim();
            String ten = txtTenTuyen.getText().trim();
            Ga gaDau = (Ga) cbGaDau.getSelectedItem();
            Ga gaCuoi = (Ga) cbGaCuoi.getSelectedItem();
            int donGia = Integer.parseInt(txtDonGiaKM.getText().trim());

            Tuyen tuyen = Tuyen.builder()
                    .maTuyen(ma)
                    .tenTuyen(ten)
                    .gaDau(gaDau != null ? gaDau.getMaGa() : "")
                    .gaCuoi(gaCuoi != null ? gaCuoi.getMaGa() : "")
                    .donGiaKM(donGia)
                    .build();

            tuyenRepository.save(tuyen);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            taiDuLieuTuyen();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chonTuyenTuBang() {
        int row = tbTuyen.getSelectedRow();
        if (row < 0) return;

        txtMaTuyen.setText((String) modelTuyen.getValueAt(row, 0));
        txtMaTuyen.setEditable(false);
        txtTenTuyen.setText((String) modelTuyen.getValueAt(row, 1));
        txtDonGiaKM.setText(String.valueOf(modelTuyen.getValueAt(row, 4)));
    }

    private void xoaTrangFormTuyen() {
        txtMaTuyen.setText("");
        txtMaTuyen.setEditable(true);
        txtTenTuyen.setText("");
        txtDonGiaKM.setText("");
        if (cbGaDau.getItemCount() > 0) cbGaDau.setSelectedIndex(0);
        if (cbGaCuoi.getItemCount() > 0) cbGaCuoi.setSelectedIndex(0);
        tbTuyen.clearSelection();
    }

    // ================================================================================
    // LOGIC GA TRONG TUYẾN
    // ================================================================================

    private void logicThemGaVaoTuyen() {
        Tuyen tuyen = (Tuyen) cbChonTuyen.getSelectedItem();
        Ga ga = (Ga) cbChonGa.getSelectedItem();

        if (tuyen == null || ga == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Tuyến và Ga!");
            return;
        }

        try {
            int thuTu = Integer.parseInt(txtThuTuGa.getText().trim());
            int thoiGianDung = Integer.parseInt(txtThoiGianDung.getText().trim());

            GaTrongTuyen gt = GaTrongTuyen.builder()
                    .maTuyen(tuyen.getMaTuyen())
                    .maGa(ga.getMaGa())
                    .thuTuGa(thuTu)
                    .thoiGianDung(thoiGianDung)
                    .build();

            gaTrongTuyenRepository.save(gt);
            JOptionPane.showMessageDialog(this, "Thêm Ga vào Tuyến thành công!");
            taiGaTrongTuyen();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Thứ tự và thời gian dừng phải là số!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void logicXoaGaKhoiTuyen() {
        int row = tbGaTrongTuyen.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ga cần xóa!");
            return;
        }

        Tuyen tuyen = (Tuyen) cbChonTuyen.getSelectedItem();
        if (tuyen == null) return;

        try {
            String maGa = (String) modelGaTrongTuyen.getValueAt(row, 1);
            gaTrongTuyenRepository.delete(tuyen.getMaTuyen(), maGa);
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            taiGaTrongTuyen();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    // ================================================================================
    // LOGIC CHUYẾN TÀU
    // ================================================================================

    private void logicTimKiemChuyenTau() {
        Tuyen tuyen = (Tuyen) cbTimTuyen.getSelectedItem();
        java.util.Date utilDate = dateTimKiem.getDate();

        if (tuyen == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Tuyến!");
            return;
        }
        if (utilDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày!");
            return;
        }

        LocalDate ngayTim = utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        modelChuyenTau.setRowCount(0);

        try {
            List<ChuyenTau> ds = chuyenTauRepository.findByGaDiGaDenNgay(
                    tuyen.getGaDau(), tuyen.getGaCuoi(), ngayTim);

            if (ds == null || ds.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến tàu nào!");
            } else {
                for (ChuyenTau ct : ds) {
                    modelChuyenTau.addRow(new Object[]{
                            ct.getMaChuyenTau(),
                            ct.getNgayKhoiHanh(),
                            ct.getGioKhoiHanh(),
                            ct.getGaDi() != null ? ct.getGaDi().getTenGa() : "",
                            ct.getGaDen() != null ? ct.getGaDen().getTenGa() : "",
                            ct.getMaTau(),
                            ct.getThct() != null ? ct.getThct().toString() : "Đang chờ"
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logicTaoChuyenTau() {
        String ma = txtMaChuyenTau.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã chuyến tàu!");
            return;
        }

        try {
            java.util.Date ngayKH = dateNgayKhoiHanh.getDate();
            java.util.Date ngayDen = dateNgayDen.getDate();

            if (ngayKH == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày khởi hành!");
                return;
            }

            LocalDate ngayKhoiHanh = ngayKH.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate ngayDenDuKien = ngayDen != null ? ngayDen.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : ngayKhoiHanh;

            LocalTime gioKH = LocalTime.parse(txtGioKhoiHanh.getText().trim(), TIME_FORMATTER);
            LocalTime gioDen = LocalTime.parse(txtGioDen.getText().trim(), TIME_FORMATTER);

            Ga gaDi = (Ga) cbGaDi.getSelectedItem();
            Ga gaDen = (Ga) cbGaDen.getSelectedItem();
            String maTau = cbTau.getSelectedItem() != null ? cbTau.getSelectedItem().toString() : null;
            String trangThai = (String) cbTrangThai.getSelectedItem();

            ChuyenTau chuyenTau = ChuyenTau.builder()
                    .maChuyenTau(ma)
                    .maTuyen(gaDi != null && gaDen != null ? gaDi.getMaGa() + "_" + gaDen.getMaGa() : "")
                    .ngayKhoiHanh(ngayKhoiHanh)
                    .gioKhoiHanh(gioKH)
                    .ngayDenDuKien(ngayDenDuKien)
                    .gioDenDuKien(gioDen)
                    .maTau(maTau)
                    .thct(TrangThaiChuyenTau.valueOf(trangThai))
                    .build();

            if (gaDi != null) chuyenTau.setGaDi(gaDi);
            if (gaDen != null) chuyenTau.setGaDen(gaDen);

            chuyenTauRepository.save(chuyenTau);
            JOptionPane.showMessageDialog(this, "Tạo chuyến tàu thành công!");
            taiTatCaChuyenTau();
            xoaTrangFormChuyenTau();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void logicCapNhatChuyenTau() {
        String ma = txtMaChuyenTau.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã chuyến tàu!");
            return;
        }

        try {
            java.util.Date ngayKH = dateNgayKhoiHanh.getDate();
            java.util.Date ngayDen = dateNgayDen.getDate();

            LocalDate ngayKhoiHanh = ngayKH.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate ngayDenDuKien = ngayDen != null ? ngayDen.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : ngayKhoiHanh;

            LocalTime gioKH = LocalTime.parse(txtGioKhoiHanh.getText().trim(), TIME_FORMATTER);
            LocalTime gioDen = LocalTime.parse(txtGioDen.getText().trim(), TIME_FORMATTER);

            Ga gaDi = (Ga) cbGaDi.getSelectedItem();
            Ga gaDen = (Ga) cbGaDen.getSelectedItem();
            String maTau = cbTau.getSelectedItem() != null ? cbTau.getSelectedItem().toString() : null;
            String trangThai = (String) cbTrangThai.getSelectedItem();

            ChuyenTau chuyenTau = ChuyenTau.builder()
                    .maChuyenTau(ma)
                    .maTuyen(gaDi != null && gaDen != null ? gaDi.getMaGa() + "_" + gaDen.getMaGa() : "")
                    .ngayKhoiHanh(ngayKhoiHanh)
                    .gioKhoiHanh(gioKH)
                    .ngayDenDuKien(ngayDenDuKien)
                    .gioDenDuKien(gioDen)
                    .maTau(maTau)
                    .thct(TrangThaiChuyenTau.valueOf(trangThai))
                    .build();

            if (gaDi != null) chuyenTau.setGaDi(gaDi);
            if (gaDen != null) chuyenTau.setGaDen(gaDen);

            chuyenTauRepository.save(chuyenTau);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            taiTatCaChuyenTau();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logicXoaChuyenTau() {
        String ma = txtMaChuyenTau.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã chuyến tàu!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa chuyến tàu này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            ChuyenTau chuyenTau = chuyenTauRepository.findById(ma);
            if (chuyenTau != null) {
                chuyenTauRepository.delete(chuyenTau);
            }
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            taiTatCaChuyenTau();
            xoaTrangFormChuyenTau();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chonChuyenTauTuBang() {
        int row = tbChuyenTau.getSelectedRow();
        if (row < 0) return;

        txtMaChuyenTau.setText((String) modelChuyenTau.getValueAt(row, 0));
        txtMaChuyenTau.setEditable(false);

        Object ngayKH = modelChuyenTau.getValueAt(row, 1);
        if (ngayKH instanceof LocalDate) {
            dateNgayKhoiHanh.setDate(java.sql.Date.valueOf((LocalDate) ngayKH));
        }

        txtGioKhoiHanh.setText(modelChuyenTau.getValueAt(row, 2).toString());
    }

    private void xoaTrangFormChuyenTau() {
        txtMaChuyenTau.setText("");
        txtMaChuyenTau.setEditable(true);
        txtGioKhoiHanh.setText("08:00");
        txtGioDen.setText("12:00");
        dateNgayKhoiHanh.setDate(null);
        dateNgayDen.setDate(null);
        if (cbGaDi.getItemCount() > 0) cbGaDi.setSelectedIndex(0);
        if (cbGaDen.getItemCount() > 0) cbGaDen.setSelectedIndex(0);
        if (cbTrangThai.getItemCount() > 0) cbTrangThai.setSelectedIndex(0);
        tbChuyenTau.clearSelection();
    }

    // ================================================================================
    // UTILITY
    // ================================================================================

    private JButton taoNutBam(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(FONT_BOLD_12);

        btn.setOpaque(true);
        btn.setBorderPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ================================================================================
    // MAIN
    // ================================================================================

    // ================================================================================
    // MAIN TEST
    // ================================================================================

    public static void main(String[] args) {
        // Sử dụng Look and Feel của hệ thống (Windows/MacOS) để giao diện mượt mà hơn
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hệ Thống Quản Lý Chuyến Tàu - JPA Project");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Khởi tạo Panel chính
            ManHinhQuanLyChuyenTauJPA mainPanel = new ManHinhQuanLyChuyenTauJPA();

            // Thêm vào JFrame
            frame.add(mainPanel);

            // Thiết lập kích thước (Tối ưu cho màn hình Full HD hoặc HD+)
            frame.setSize(1300, 800);

            // Canh giữa màn hình
            frame.setLocationRelativeTo(null);

            // Hiển thị
            frame.setVisible(true);

            // Ghi chú: Nếu bạn sử dụng JPA, hãy đảm bảo database đã được khởi chạy
            // và file persistence.xml đã cấu hình đúng để không bị lỗi treo UI khi load dữ liệu.
        });
    }
}
