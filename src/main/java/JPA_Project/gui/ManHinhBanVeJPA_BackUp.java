package JPA_Project.gui;

import JPA_Project.dto.*;
import JPA_Project.entity.Ga;
import JPA_Project.entity.LoaiVe;
import JPA_Project.entity.NhanVien;
import JPA_Project.repository.GaRepository;
import JPA_Project.service.BanVeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * ManHinhBanVeJPA - Phiên bản JPA của màn hình bán vé tàu.
 * Hoạt động độc lập trong project JPA, sử dụng BanVeService.
 * 
 * Luồng hoạt động:
 * 1. Tìm kiếm chuyến tàu theo ga đi, ga đến, ngày đi
 * 2. Chọn chuyến tàu -> Load danh sách toa
 * 3. Chọn toa -> Vẽ sơ đồ ghế (SwingWorker)
 * 4. Chọn ghế -> Nhập thông tin hành khách
 * 5. Tính giá vé tự động
 * 6. Chuyển sang màn hình xác nhận
 */
public class ManHinhBanVeJPA_BackUp extends JPanel implements ActionListener {

    // ================================================================================
    // SERVICES & REPOSITORIES
    // ================================================================================
    private final BanVeService banVeService;
    private final GaRepository gaRepository;
    private final NhanVien nhanVienHienTai;

    // ================================================================================
    // UI COMPONENTS - TÌM KIẾM
    // ================================================================================
    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JSpinner spnNgayDi;
    private JButton btnTimChuyen;
    private JPanel pnlDanhSachChuyen;

    // ================================================================================
    // UI COMPONENTS - CHUYẾN TÀU & TOA
    // ================================================================================
    private JPanel pnlChuyenTau;
    private JPanel pnlToa;
    private ChuyenTauDTO chuyenTauHienTai;
    private ToaDTO toaHienTai;
    private String maToaDaChon = null;

    // ================================================================================
    // UI COMPONENTS - SƠ ĐỒ GHẾ
    // ================================================================================
    private JPanel pnlSoDoGhe;
    private JScrollPane scrSoDoGhe;
    private Map<String, JButton> seatButtonsMap = new HashMap<>();
    private Map<String, GheDTO> gheDTOMap = new HashMap<>();

    // ================================================================================
    // UI COMPONENTS - THÔNG TIN KHÁCH HÀNG
    // ================================================================================
    private JPanel pnlDanhSachKhachHang;
    private JScrollPane scrDanhSachKhachHang;
    private Map<String, PassengerDTO> khachHangMap = new LinkedHashMap<>();
    private Map<String, TicketDTO> ticketMap = new LinkedHashMap<>();

    // ================================================================================
    // UI COMPONENTS - TỔNG KẾT
    // ================================================================================
    private JLabel lblTongTien;
    private JLabel lblSoGheDaChon;
    private JButton btnHuy;
    private JButton btnTiepTheo;

    // ================================================================================
    // MÀU SẮC
    // ================================================================================
    private static final Color COLOR_GHE_TRONG = new Color(200, 230, 200);
    private static final Color COLOR_GHE_DA_DAT = Color.RED;
    private static final Color COLOR_GHE_DANG_CHON = new Color(52, 152, 219);
    private static final Color COLOR_GHE_GIUONG = new Color(255, 200, 100);
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);
    private static final Color COLOR_ORANGE = new Color(255, 165, 0);

    // ================================================================================
    // DANH SÁCH LOẠI VÉ
    // ================================================================================
    private List<LoaiVe> danhSachLoaiVe;
    private DefaultComboBoxModel<LoaiVe> loaiVeComboModel;

    // ================================================================================
    // CALLBACK
    // ================================================================================
    private Consumer<List<TicketDTO>> onTiepTheoCallback;
    private JButton btnXacNhan;

    // ================================================================================
    // CONSTRUCTOR
    // ================================================================================
    public ManHinhBanVeJPA_BackUp() {
        this(null);
    }

    public ManHinhBanVeJPA_BackUp(NhanVien nhanVien) {
        this.nhanVienHienTai = nhanVien;
        this.banVeService = new BanVeService();
        this.gaRepository = new GaRepository();
        this.danhSachLoaiVe = banVeService.getDanhSachLoaiVe();
        
        khoiTaoGiaoDien();
        taiDanhSachGa();
    }

    /**
     * Khởi tạo giao diện chính.
     */
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Tiêu đề
        JLabel tieuDe = new JLabel("Bán vé tàu", SwingConstants.CENTER);
        tieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        tieuDe.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(tieuDe, BorderLayout.NORTH);

        // Split pane chính
        JSplitPane splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitMain.setResizeWeight(0.6);
        splitMain.setDividerSize(5);
        splitMain.setBorder(null);

        // Panel bên trái: Tìm kiếm, chuyến tàu, toa, sơ đồ ghế
        JPanel panelTrai = taoPanelTrai();
        splitMain.setLeftComponent(panelTrai);

        // Panel bên phải: Thông tin khách hàng
        JPanel panelPhai = taoPanelPhai();
        splitMain.setRightComponent(panelPhai);

        add(splitMain, BorderLayout.CENTER);

        // Footer với nút Hủy và Tiếp theo
        JPanel footer = taoFooter();
        add(footer, BorderLayout.SOUTH);

        // Đặt vị trí divider
        SwingUtilities.invokeLater(() -> splitMain.setDividerLocation(0.6));
    }

    /**
     * Tạo panel bên trái.
     */
    private JPanel taoPanelTrai() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // 1. Khu vực tìm kiếm
        panel.add(taoPanelTimKiem());
        panel.add(Box.createVerticalStrut(10));

        // 2. Khu vực danh sách chuyến tàu
        panel.add(taoPanelDanhSachChuyenTau());
        panel.add(Box.createVerticalStrut(10));

        // 3. Khu vực chọn toa
        panel.add(taoPanelChonToa());
        panel.add(Box.createVerticalStrut(10));

        // 4. Khu vực sơ đồ ghế
        panel.add(taoPanelSoDoGhe());

        return panel;
    }

    /**
     * Tạo panel bên phải (thông tin khách hàng).
     */
    private JPanel taoPanelPhai() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Thông tin hành khách"));

        // Panel chứa danh sách khách hàng
        pnlDanhSachKhachHang = new JPanel();
        pnlDanhSachKhachHang.setLayout(new BoxLayout(pnlDanhSachKhachHang, BoxLayout.Y_AXIS));
        pnlDanhSachKhachHang.setBackground(Color.WHITE);

        scrDanhSachKhachHang = new JScrollPane(pnlDanhSachKhachHang);
        scrDanhSachKhachHang.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrDanhSachKhachHang.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JLabel placeholder = new JLabel("Chọn ghế để nhập thông tin hành khách");
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        placeholder.setForeground(Color.GRAY);
        pnlDanhSachKhachHang.add(placeholder);

        panel.add(scrDanhSachKhachHang, BorderLayout.CENTER);

        // Panel tổng tiền
        JPanel pnlTongTien = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTongTien.setBackground(Color.WHITE);
        
        lblSoGheDaChon = new JLabel("Đã chọn: 0 ghế");
        lblSoGheDaChon.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblTongTien = new JLabel("Tổng tiền: 0 VND");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(COLOR_ORANGE);

        pnlTongTien.add(lblSoGheDaChon);
        pnlTongTien.add(Box.createHorizontalStrut(20));
        pnlTongTien.add(lblTongTien);

        panel.add(pnlTongTien, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel footer với nút Hủy và Tiếp theo.
     */
    private JPanel taoFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 40));
        btnHuy.setBackground(new Color(220, 53, 69));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.addActionListener(this);

        btnTiepTheo = new JButton("Tiếp theo >");
        btnTiepTheo.setPreferredSize(new Dimension(120, 40));
        btnTiepTheo.setBackground(new Color(46, 204, 113));
        btnTiepTheo.setForeground(Color.WHITE);
        btnTiepTheo.setFocusPainted(false);
        btnTiepTheo.addActionListener(this);
        btnTiepTheo.setEnabled(false);

        panel.add(btnHuy);
        panel.add(btnTiepTheo);

        return panel;
    }

    /**
     * Tạo panel tìm kiếm.
     */
    private JPanel taoPanelTimKiem() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm chuyến tàu"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.setBackground(Color.WHITE);
        row1.add(new JLabel("Ga đi:"));
        cbGaDi = new JComboBox<>();
        cbGaDi.setPreferredSize(new Dimension(200, 25));
        row1.add(cbGaDi);
        panel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.setBackground(Color.WHITE);
        row2.add(new JLabel("Ga đến:"));
        cbGaDen = new JComboBox<>();
        cbGaDen.setPreferredSize(new Dimension(200, 25));
        row2.add(cbGaDen);
        panel.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row3.setBackground(Color.WHITE);
        row3.add(new JLabel("Ngày đi:"));
        
        spnNgayDi = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgayDi, "dd/MM/yyyy");
        spnNgayDi.setEditor(dateEditor);
        spnNgayDi.setValue(new Date());
        spnNgayDi.setPreferredSize(new Dimension(120, 25));
        row3.add(spnNgayDi);

        btnTimChuyen = new JButton("Tìm chuyến");
        btnTimChuyen.setBackground(COLOR_BLUE_LIGHT);
        btnTimChuyen.setForeground(Color.WHITE);
        btnTimChuyen.setFocusPainted(false);
        btnTimChuyen.setOpaque(true);
        btnTimChuyen.setBorderPainted(false);
        btnTimChuyen.addActionListener(this);
        row3.add(btnTimChuyen);
        panel.add(row3);

        return panel;
    }

    /**
     * Tạo panel danh sách chuyến tàu.
     */
    private JPanel taoPanelDanhSachChuyenTau() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách chuyến tàu"));

        pnlChuyenTau = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlChuyenTau.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pnlChuyenTau);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 80));

        JLabel placeholder = new JLabel("Chọn Ga đi, Ga đến và Ngày đi để tìm kiếm");
        placeholder.setForeground(Color.GRAY);
        pnlChuyenTau.add(placeholder);

        panel.add(scrollPane, BorderLayout.CENTER);
        this.pnlDanhSachChuyen = panel;

        return panel;
    }

    /**
     * Tạo panel chọn toa.
     */
    private JPanel taoPanelChonToa() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Chọn toa"));

        JPanel pnlToaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlToaRow.setBackground(Color.WHITE);
        pnlToaRow.add(new JLabel("Toa:"));

        pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlToa.setBackground(Color.WHITE);

        JScrollPane scrollToa = new JScrollPane(pnlToa);
        scrollToa.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollToa.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollToa.setPreferredSize(new Dimension(500, 50));
        scrollToa.setBorder(null);

        pnlToaRow.add(scrollToa);
        panel.add(pnlToaRow);

        // Chú thích loại toa
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legend.setBackground(Color.WHITE);
        legend.add(taoChuGiai(COLOR_GHE_TRONG, "Ghế ngồi"));
        legend.add(taoChuGiai(COLOR_GHE_GIUONG, "Giường nằm"));
        legend.add(taoChuGiai(COLOR_GHE_DANG_CHON, "Toa đã chọn"));
        panel.add(legend);

        return panel;
    }

    /**
     * Tạo panel sơ đồ ghế.
     */
    private JPanel taoPanelSoDoGhe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Sơ đồ ghế"));

        pnlSoDoGhe = new JPanel();
        pnlSoDoGhe.setLayout(new BoxLayout(pnlSoDoGhe, BoxLayout.Y_AXIS));
        pnlSoDoGhe.setBackground(Color.WHITE);

        scrSoDoGhe = new JScrollPane(pnlSoDoGhe);
        scrSoDoGhe.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrSoDoGhe.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrSoDoGhe.setPreferredSize(new Dimension(600, 200));

        JLabel placeholder = new JLabel("Chọn một toa để xem sơ đồ ghế");
        placeholder.setForeground(Color.GRAY);
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSoDoGhe.add(placeholder);

        panel.add(scrSoDoGhe, BorderLayout.CENTER);

        // Chú thích màu ghế
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legend.setBackground(Color.WHITE);
        legend.add(taoChuGiai(COLOR_GHE_TRONG, "Ghế trống"));
        legend.add(taoChuGiai(COLOR_GHE_DA_DAT, "Ghế đã đặt"));
        legend.add(taoChuGiai(COLOR_GHE_DANG_CHON, "Ghế đang chọn"));
        panel.add(legend, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo nhãn chú giải.
     */
    private JLabel taoChuGiai(Color mau, String text) {
        JLabel label = new JLabel("  " + text + "  ");
        label.setOpaque(true);
        label.setBackground(mau);
        label.setForeground(mau == COLOR_GHE_DA_DAT ? Color.WHITE : Color.BLACK);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        return label;
    }

    // ================================================================================
    // TẢI DỮ LIỆU
    // ================================================================================

    /**
     * Tải danh sách ga vào combobox.
     */
    private void taiDanhSachGa() {
        try {
            List<Ga> allGa = gaRepository.findAll();
            
            cbGaDi.removeAllItems();
            cbGaDen.removeAllItems();
            
            for (Ga ga : allGa) {
                cbGaDi.addItem(ga);
                cbGaDen.addItem(ga);
            }

            // Đặt mặc định
            if (allGa.size() > 1) {
                cbGaDi.setSelectedIndex(0);
                cbGaDen.setSelectedIndex(1);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh sách ga: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================================
    // XỬ LÝ TÌM KIẾM CHUYẾN TÀU (SWINGWORKER)
    // ================================================================================

    /**
     * Tìm kiếm chuyến tàu sử dụng SwingWorker.
     */
    private void timKiemChuyenTau() {
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

        Date selectedDate = (Date) spnNgayDi.getValue();
        LocalDate ngayDi = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        // Hiển thị loading
        pnlChuyenTau.removeAll();
        pnlChuyenTau.add(new JLabel("Đang tìm kiếm..."));
        pnlChuyenTau.revalidate();
        pnlChuyenTau.repaint();

        // Sử dụng SwingWorker để tìm kiếm
        SwingWorker<List<ChuyenTauDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChuyenTauDTO> doInBackground() {
                return banVeService.findChuyenTau(gaDi.getMaGa(), gaDen.getMaGa(), ngayDi);
            }

            @Override
            protected void done() {
                try {
                    List<ChuyenTauDTO> ketQua = get();
                    hienThiDanhSachChuyenTau(ketQua);
                } catch (Exception e) {
                    System.err.println("Lỗi khi tìm chuyến tàu: " + e.getMessage());
                    e.printStackTrace();
                    pnlChuyenTau.removeAll();
                    pnlChuyenTau.add(new JLabel("Lỗi khi tìm kiếm!"));
                    pnlChuyenTau.revalidate();
                }
            }
        };
        worker.execute();
    }

    /**
     * Hiển thị danh sách chuyến tàu.
     */
    private void hienThiDanhSachChuyenTau(List<ChuyenTauDTO> danhSach) {
        pnlChuyenTau.removeAll();

        if (danhSach == null || danhSach.isEmpty()) {
            pnlChuyenTau.add(new JLabel("Không tìm thấy chuyến tàu nào!"));
        } else {
            for (ChuyenTauDTO ct : danhSach) {
                JPanel card = taoCardChuyenTau(ct);
                pnlChuyenTau.add(card);
            }
        }

        pnlChuyenTau.revalidate();
        pnlChuyenTau.repaint();
    }

    /**
     * Tạo card hiển thị chuyến tàu.
     */
    private JPanel taoCardChuyenTau(ChuyenTauDTO ct) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(8, 12, 8, 12)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Thông tin chuyến tàu
        JLabel lblTieuDe = new JLabel(ct.maChuyenTau());
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 12));
        lblTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTieuDe);

        String thongTin = String.format("%s → %s | %s | %s",
                ct.tenGaDi(), ct.tenGaDen(),
                ct.gioKhoiHanh() != null ? ct.gioKhoiHanh().toString() : "--:--",
                ct.tenTuyen());
        JLabel lblThongTin = new JLabel(thongTin);
        lblThongTin.setFont(new Font("Arial", Font.PLAIN, 11));
        lblThongTin.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblThongTin);

        // Sự kiện click
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chonChuyenTau(ct);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(220, 230, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(245, 245, 250));
            }
        });

        card.setMaximumSize(new Dimension(200, 60));

        return card;
    }

    // ================================================================================
    // XỬ LÝ CHỌN CHUYẾN TÀU
    // ================================================================================

    /**
     * Xử lý khi chọn một chuyến tàu.
     */
    private void chonChuyenTau(ChuyenTauDTO ct) {
        this.chuyenTauHienTai = ct;
        System.out.println("Đã chọn chuyến tàu: " + ct.maChuyenTau());

        // Tải danh sách toa
        taiDanhSachToa(ct.maChuyenTau());
    }

    /**
     * Tải danh sách toa của chuyến tàu.
     */
    private void taiDanhSachToa(String maChuyenTau) {
        pnlToa.removeAll();

        // Hiển thị loading
        pnlToa.add(new JLabel("Đang tải toa..."));
        pnlToa.revalidate();
        pnlToa.repaint();

        SwingWorker<List<ToaDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ToaDTO> doInBackground() {
                return banVeService.getDanhSachToaByChuyenTau(maChuyenTau);
            }

            @Override
            protected void done() {
                try {
                    List<ToaDTO> danhSachToa = get();
                    hienThiDanhSachToa(danhSachToa);
                } catch (Exception e) {
                    System.err.println("Lỗi khi tải toa: " + e.getMessage());
                    pnlToa.removeAll();
                    pnlToa.add(new JLabel("Lỗi khi tải toa!"));
                    pnlToa.revalidate();
                }
            }
        };
        worker.execute();
    }

    /**
     * Hiển thị danh sách toa.
     */
    private void hienThiDanhSachToa(List<ToaDTO> danhSachToa) {
        pnlToa.removeAll();

        if (danhSachToa == null || danhSachToa.isEmpty()) {
            pnlToa.add(new JLabel("Không có toa nào!"));
        } else {
            for (ToaDTO toa : danhSachToa) {
                JButton btnToa = taoNutToa(toa);
                pnlToa.add(btnToa);
            }
        }

        pnlToa.revalidate();
        pnlToa.repaint();
    }

    /**
     * Tạo nút chọn toa.
     */
    private JButton taoNutToa(ToaDTO toa) {
        JButton btn = new JButton(toa.maToa());
        btn.setPreferredSize(new Dimension(70, 35));
        btn.setFocusPainted(false);

        // Màu dựa vào loại toa
        if (toa.loaiToa() != null && toa.loaiToa().contains("NGAM")) {
            btn.setBackground(COLOR_GHE_GIUONG);
        } else {
            btn.setBackground(COLOR_GHE_TRONG);
        }

        btn.addActionListener(e -> chonToa(toa));

        return btn;
    }

    // ================================================================================
    // XỬ LÝ CHỌN TOA VÀ VẼ SƠ ĐỒ GHẾ (SWINGWORKER)
    // ================================================================================

    /**
     * Xử lý khi chọn một toa.
     */
    private void chonToa(ToaDTO toa) {
        this.toaHienTai = toa;
        this.maToaDaChon = toa.maToa();
        System.out.println("Đã chọn toa: " + toa.maToa());

        // Đổi màu nút toa
        for (Component comp : pnlToa.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equals(toa.maToa())) {
                    btn.setBackground(COLOR_GHE_DANG_CHON);
                    btn.setForeground(Color.WHITE);
                } else {
                    // Khôi phục màu gốc
                    if (toa.loaiToa() != null && toa.loaiToa().contains("NGAM")) {
                        btn.setBackground(COLOR_GHE_GIUONG);
                    } else {
                        btn.setBackground(COLOR_GHE_TRONG);
                    }
                    btn.setForeground(Color.BLACK);
                }
            }
        }

        // Tải sơ đồ ghế
        taiSoDoGhe(toa.maToa());
    }

    /**
     * Tải sơ đồ ghế sử dụng SwingWorker.
     */
    private void taiSoDoGhe(String maToa) {
        // Hiển thị loading
        pnlSoDoGhe.removeAll();
        JLabel loading = new JLabel("Đang tải sơ đồ ghế...");
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSoDoGhe.add(loading);
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();

        String maChuyenTau = chuyenTauHienTai != null ? chuyenTauHienTai.maChuyenTau() : null;

        SwingWorker<List<GheDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<GheDTO> doInBackground() {
                return banVeService.getSoDoGheByToa(maToa, maChuyenTau);
            }

            @Override
            protected void done() {
                try {
                    List<GheDTO> danhSachGhe = get();
                    veSoDoGhe(danhSachGhe);
                } catch (Exception e) {
                    System.err.println("Lỗi khi tải sơ đồ ghế: " + e.getMessage());
                    pnlSoDoGhe.removeAll();
                    pnlSoDoGhe.add(new JLabel("Lỗi khi tải sơ đồ ghế!"));
                    pnlSoDoGhe.revalidate();
                }
            }
        };
        worker.execute();
    }

    /**
     * Vẽ sơ đồ ghế.
     */
    private void veSoDoGhe(List<GheDTO> danhSachGhe) {
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        gheDTOMap.clear();

        if (danhSachGhe == null || danhSachGhe.isEmpty()) {
            pnlSoDoGhe.add(new JLabel("Toa này không có ghế nào!"));
            pnlSoDoGhe.revalidate();
            return;
        }

        // Xác định loại toa (ghế ngồi hay giường nằm)
        boolean laToaGiuongNam = toaHienTai != null && 
                toaHienTai.loaiToa() != null && 
                toaHienTai.loaiToa().contains("NGAM");

        // Nhóm ghế theo tầng
        Map<Integer, List<GheDTO>> gheTheoTang = danhSachGhe.stream()
                .collect(Collectors.groupingBy(GheDTO::tang));

        for (Map.Entry<Integer, List<GheDTO>> entry : gheTheoTang.entrySet()) {
            JPanel tangPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            tangPanel.setBackground(Color.WHITE);

            JLabel lblTang = new JLabel("Tầng " + entry.getKey() + ":");
            lblTang.setFont(new Font("Arial", Font.BOLD, 12));
            lblTang.setPreferredSize(new Dimension(60, 30));
            tangPanel.add(lblTang);

            for (GheDTO ghe : entry.getValue()) {
                JButton btnGhe = taoNutGhe(ghe, laToaGiuongNam);
                tangPanel.add(btnGhe);
                seatButtonsMap.put(ghe.maChoDat(), btnGhe);
                gheDTOMap.put(ghe.maChoDat(), ghe);
            }

            pnlSoDoGhe.add(tangPanel);
        }

        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }

    /**
     * Tạo nút ghế.
     */
    private JButton taoNutGhe(GheDTO ghe, boolean laToaGiuongNam) {
        JButton btn = new JButton(ghe.soCho());
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 11));

        // Màu dựa vào trạng thái
        if (ghe.daDat()) {
            btn.setBackground(COLOR_GHE_DA_DAT);
            btn.setForeground(Color.WHITE);
            btn.setEnabled(false);
        } else {
            // Ghế trống - kiểm tra có đang chọn không
            if (ticketMap.containsKey(ghe.maChoDat())) {
                btn.setBackground(COLOR_GHE_DANG_CHON);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(laToaGiuongNam ? COLOR_GHE_GIUONG : COLOR_GHE_TRONG);
                btn.setForeground(Color.BLACK);
            }
        }

        // Tooltip
        String status = ghe.daDat() ? "Đã đặt" : "Trống";
        btn.setToolTipText(ghe.maChoDat() + " - " + ghe.soCho() + " - " + status);

        // Sự kiện click
        btn.addActionListener(e -> xuLyClickGhe(ghe));

        return btn;
    }

    /**
     * Xử lý click vào ghế.
     */
    private void xuLyClickGhe(GheDTO ghe) {
        if (ghe.daDat()) {
            return;
        }

        String maCho = ghe.maChoDat();

        if (ticketMap.containsKey(maCho)) {
            // Bỏ chọn ghế
            ticketMap.remove(maCho);
            khachHangMap.remove(maCho);
        } else {
            // Chọn ghế - mở dialog nhập thông tin
            moDialogNhapThongTinKhach(maCho);
        }

        // Cập nhật lại màu nút ghế
        JButton btn = seatButtonsMap.get(maCho);
        if (btn != null) {
            boolean laToaGiuongNam = toaHienTai != null && 
                    toaHienTai.loaiToa() != null && 
                    toaHienTai.loaiToa().contains("NGAM");
            
            if (ticketMap.containsKey(maCho)) {
                btn.setBackground(COLOR_GHE_DANG_CHON);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(laToaGiuongNam ? COLOR_GHE_GIUONG : COLOR_GHE_TRONG);
                btn.setForeground(Color.BLACK);
            }
        }

        // Cập nhật danh sách khách hàng
        capNhatDanhSachKhachHang();

        // Cập nhật tổng tiền
        tinhVaHienThiTongTien();
    }

    // ================================================================================
    // XỬ LÝ THÔNG TIN KHÁCH HÀNG
    // ================================================================================

    /**
     * Mở dialog nhập thông tin khách hàng.
     */
    private void moDialogNhapThongTinKhach(String maCho) {
        GheDTO ghe = gheDTOMap.get(maCho);
        if (ghe == null) return;

        // Tạo dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Thông tin hành khách - Ghế " + ghe.soCho(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ComboBox loại vé
        JPanel pnlLoaiVe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLoaiVe.add(new JLabel("Loại vé:"));
        JComboBox<LoaiVe> cbLoaiVe = new JComboBox<>();
        for (LoaiVe lv : danhSachLoaiVe) {
            cbLoaiVe.addItem(lv);
        }
        cbLoaiVe.setPreferredSize(new Dimension(250, 25));
        pnlLoaiVe.add(cbLoaiVe);
        content.add(pnlLoaiVe);
        content.add(Box.createVerticalStrut(10));

        // Họ tên
        JPanel pnlHoTen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlHoTen.add(new JLabel("Họ tên:"));
        JTextField txtHoTen = new JTextField(25);
        pnlHoTen.add(txtHoTen);
        content.add(pnlHoTen);
        content.add(Box.createVerticalStrut(10));

        // CCCD
        JPanel pnlCCCD = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlCCCD.add(new JLabel("CCCD:"));
        JTextField txtCCCD = new JTextField(25);
        pnlCCCD.add(txtCCCD);
        content.add(pnlCCCD);
        content.add(Box.createVerticalStrut(10));

        // SĐT
        JPanel pnlSDT = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlSDT.add(new JLabel("SĐT:"));
        JTextField txtSDT = new JTextField(25);
        pnlSDT.add(txtSDT);
        content.add(pnlSDT);
        content.add(Box.createVerticalStrut(10));

        // Ngày sinh
        JPanel pnlNgaySinh = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNgaySinh.add(new JLabel("Ngày sinh:"));
        JSpinner spnNgaySinh = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditorNgaySinh = new JSpinner.DateEditor(spnNgaySinh, "dd/MM/yyyy");
        spnNgaySinh.setEditor(dateEditorNgaySinh);
        spnNgaySinh.setPreferredSize(new Dimension(120, 25));
        pnlNgaySinh.add(spnNgaySinh);
        content.add(pnlNgaySinh);
        content.add(Box.createVerticalStrut(10));

        // Giới tính
        JPanel pnlGioiTinh = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlGioiTinh.add(new JLabel("Giới tính:"));
        JComboBox<String> cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        cbGioiTinh.setPreferredSize(new Dimension(100, 25));
        pnlGioiTinh.add(cbGioiTinh);
        content.add(pnlGioiTinh);

        dialog.add(content, BorderLayout.CENTER);

        // Nút
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnHuy = new JButton("Hủy");
        btnHuy.setOpaque(true);
        btnHuy.setBorderPainted(false);
        btnHuy.addActionListener(e -> dialog.dispose());
        
        btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBackground(COLOR_BLUE_LIGHT);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setOpaque(true);
        btnXacNhan.setBorderPainted(false);
        btnXacNhan.addActionListener(e -> {
            // Lấy thông tin và tạo PassengerDTO
            LoaiVe loaiVe = (LoaiVe) cbLoaiVe.getSelectedItem();
            String hoTen = txtHoTen.getText().trim();
            String cccd = txtCCCD.getText().trim();
            String sdt = txtSDT.getText().trim();
            
            Date selectedDate = (Date) spnNgaySinh.getValue();
            LocalDate ngaySinh = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            
            String gioiTinh = (String) cbGioiTinh.getSelectedItem();

            // Tạo PassengerDTO
            PassengerDTO passenger = new PassengerDTO(
                    loaiVe != null ? loaiVe.getMaLoaiVe() : "VT01",
                    hoTen,
                    cccd,
                    sdt,
                    ngaySinh,
                    gioiTinh
            );

            // Tạo TicketDTO
            BigDecimal giaVe = tinhGiaChoGhe(maCho, loaiVe != null ? loaiVe.getMaLoaiVe() : "VT01");
            TicketDTO ticket = new TicketDTO(maCho, passenger, loaiVe != null ? loaiVe.getMaLoaiVe() : "VT01", giaVe, true);

            // Lưu vào map
            ticketMap.put(maCho, ticket);
            khachHangMap.put(maCho, passenger);

            dialog.dispose();
        });

        pnlNut.add(btnHuy);
        pnlNut.add(btnXacNhan);
        dialog.add(pnlNut, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Tính giá cho một ghế.
     */
    private BigDecimal tinhGiaChoGhe(String maCho, String maLoaiVe) {
        if (chuyenTauHienTai == null) return BigDecimal.ZERO;
        
        String maLoaiToa = toaHienTai != null ? toaHienTai.loaiToa() : "G_GHE";
        return banVeService.tinhGiaVe(chuyenTauHienTai.maChuyenTau(), maLoaiToa, maLoaiVe, null);
    }

    /**
     * Cập nhật danh sách khách hàng trong panel.
     */
    private void capNhatDanhSachKhachHang() {
        pnlDanhSachKhachHang.removeAll();

        if (ticketMap.isEmpty()) {
            JLabel placeholder = new JLabel("Chọn ghế để nhập thông tin hành kháng");
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            placeholder.setForeground(Color.GRAY);
            pnlDanhSachKhachHang.add(placeholder);
        } else {
            for (Map.Entry<String, TicketDTO> entry : ticketMap.entrySet()) {
                JPanel card = taoCardKhachHang(entry.getKey(), entry.getValue());
                pnlDanhSachKhachHang.add(card);
                pnlDanhSachKhachHang.add(Box.createVerticalStrut(5));
            }
        }

        pnlDanhSachKhachHang.revalidate();
        pnlDanhSachKhachHang.repaint();
    }

    /**
     * Tạo card hiển thị thông tin khách hàng.
     */
    private JPanel taoCardKhachHang(String maCho, TicketDTO ticket) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(240, 248, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BLUE_LIGHT, 1),
                new EmptyBorder(8, 10, 8, 10)));

        PassengerDTO kh = ticket.khachHang();

        JLabel lblMaGhe = new JLabel("Ghế: " + maCho);
        lblMaGhe.setFont(new Font("Arial", Font.BOLD, 12));
        lblMaGhe.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblMaGhe);

        String thongTin = String.format("%s | %s | %s",
                kh.hoTen() != null && !kh.hoTen().isEmpty() ? kh.hoTen() : "Chưa nhập",
                kh.cccd() != null && !kh.cccd().isEmpty() ? kh.cccd() : "Chưa nhập",
                kh.sdt() != null && !kh.sdt().isEmpty() ? kh.sdt() : "Chưa nhập");
        JLabel lblThongTin = new JLabel(thongTin);
        lblThongTin.setFont(new Font("Arial", Font.PLAIN, 11));
        lblThongTin.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblThongTin);

        String gia = ticket.giaVe() != null ? formatCurrency(ticket.giaVe()) : "0 VND";
        JLabel lblGia = new JLabel("Giá: " + gia);
        lblGia.setFont(new Font("Arial", Font.BOLD, 11));
        lblGia.setForeground(COLOR_ORANGE);
        lblGia.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblGia);

        // Nút xóa
        JButton btnXoa = new JButton("Xóa");
        btnXoa.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnXoa.addActionListener(e -> {
            ticketMap.remove(maCho);
            khachHangMap.remove(maCho);
            
            // Cập nhật lại màu ghế
            JButton btnGhe = seatButtonsMap.get(maCho);
            if (btnGhe != null) {
                boolean laToaGiuongNam = toaHienTai != null && 
                        toaHienTai.loaiToa() != null && 
                        toaHienTai.loaiToa().contains("NGAM");
                btnGhe.setBackground(laToaGiuongNam ? COLOR_GHE_GIUONG : COLOR_GHE_TRONG);
                btnGhe.setForeground(Color.BLACK);
                btnGhe.setEnabled(true);
            }
            
            capNhatDanhSachKhachHang();
            tinhVaHienThiTongTien();
        });
        card.add(btnXoa);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        return card;
    }

    // ================================================================================
    // TÍNH VÀ HIỂN THỊ TỔNG TIỀN
    // ================================================================================

    /**
     * Tính và hiển thị tổng tiền.
     */
    private void tinhVaHienThiTongTien() {
        int soGhe = ticketMap.size();
        BigDecimal tongTien = BigDecimal.ZERO;

        for (TicketDTO ticket : ticketMap.values()) {
            if (ticket.giaVe() != null) {
                tongTien = tongTien.add(ticket.giaVe());
            }
        }

        lblSoGheDaChon.setText("Đã chọn: " + soGhe + " ghế");
        lblTongTien.setText("Tổng tiền: " + formatCurrency(tongTien));

        // Bật/tắt nút Tiếp theo
        btnTiepTheo.setEnabled(soGhe > 0);
    }

    /**
     * Format số tiền thành chuỗi VND.
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VND";
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " VND";
    }

    // ================================================================================
    // ACTION LISTENER
    // ================================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTimChuyen) {
            timKiemChuyenTau();
        } else if (e.getSource() == btnTiepTheo) {
            chuyenSangManHinhXacNhan();
        } else if (e.getSource() == btnHuy) {
            huyBo();
        }
    }

    // ================================================================================
    // CHUYỂN MÀN HÌNH & HỦY
    // ================================================================================

    /**
     * Thiết lập callback khi nhấn nút Tiếp theo.
     */
    public void setOnTiepTheo(Consumer<List<TicketDTO>> callback) {
        this.onTiepTheoCallback = callback;
    }

    /**
     * Chuyển sang màn hình xác nhận bán vé.
     */
    private void chuyenSangManHinhXacNhan() {
        if (ticketMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một ghế!");
            return;
        }

        // Kiểm tra thông tin khách hàng
        for (Map.Entry<String, TicketDTO> entry : ticketMap.entrySet()) {
            PassengerDTO kh = entry.getValue().khachHang();
            if (kh.hoTen() == null || kh.hoTen().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "Vui lòng nhập họ tên cho ghế " + entry.getKey() + "!");
                return;
            }
        }

        // Tạo danh sách vé để chuyển
        List<TicketDTO> danhSachVe = new ArrayList<>(ticketMap.values());
        
        if (onTiepTheoCallback != null) {
            onTiepTheoCallback.accept(danhSachVe);
        }
    }

    /**
     * Hủy bỏ và reset form.
     */
    private void huyBo() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn hủy bỏ và xóa tất cả thông tin đã nhập?",
                "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            resetAllData();
        }
    }

    /**
     * Reset tất cả dữ liệu trên form.
     */
    public void resetAllData() {
        // Reset chuyến tàu
        chuyenTauHienTai = null;
        toaHienTai = null;
        maToaDaChon = null;

        // Clear maps
        ticketMap.clear();
        khachHangMap.clear();
        seatButtonsMap.clear();
        gheDTOMap.clear();

        // Reset giao diện
        pnlChuyenTau.removeAll();
        pnlChuyenTau.add(new JLabel("Chọn Ga đi, Ga đến và Ngày đi để tìm kiếm"));
        pnlChuyenTau.revalidate();

        pnlToa.removeAll();
        pnlToa.revalidate();

        pnlSoDoGhe.removeAll();
        pnlSoDoGhe.add(new JLabel("Chọn một toa để xem sơ đồ ghế"));
        pnlSoDoGhe.revalidate();

        pnlDanhSachKhachHang.removeAll();
        JLabel placeholder = new JLabel("Chọn ghế để nhập thông tin hành kháng");
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        placeholder.setForeground(Color.GRAY);
        pnlDanhSachKhachHang.add(placeholder);
        pnlDanhSachKhachHang.revalidate();

        lblSoGheDaChon.setText("Đã chọn: 0 ghế");
        lblTongTien.setText("Tổng tiền: 0 VND");

        btnTiepTheo.setEnabled(false);
    }

    /**
     * Lấy danh sách vé đã chọn (getter cho màn hình khác sử dụng).
     */
    public List<TicketDTO> getDanhSachVeDaChon() {
        return new ArrayList<>(ticketMap.values());
    }

    /**
     * Lấy chuyến tàu hiện tại.
     */
    public ChuyenTauDTO getChuyenTauHienTai() {
        return chuyenTauHienTai;
    }

    /**
     * Lấy thông tin khách hàng.
     */
    public Map<String, PassengerDTO> getThongTinKhachHang() {
        return new HashMap<>(khachHangMap);
    }

    public static void main(String[] args) {
        // Thiết lập giao diện giống hệ điều hành (Windows/MacOS) cho đẹp
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Giao Diện Bán Vé JPA");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800); // Kích thước màn hình

            // Khởi tạo nhân viên giả lập nếu cần
            NhanVien nv = new NhanVien();
            // nv.setTenNhanVien("NV Test");

            // Tạo màn hình bán vé
            ManHinhBanVeJPA_BackUp banVePanel = new ManHinhBanVeJPA_BackUp(nv);

            // Thiết lập callback khi nhấn "Tiếp theo" để xem kết quả
            banVePanel.setOnTiepTheo(tickets -> {
                JOptionPane.showMessageDialog(frame,
                        "Chuyển sang xác nhận với " + tickets.size() + " vé.");
                tickets.forEach(t -> System.out.println("Ghế: " + t.maChoDat() + " - Khách: " + t.khachHang().hoTen()));
            });

            frame.add(banVePanel);
            frame.setLocationRelativeTo(null); // Hiển thị giữa màn hình
            frame.setVisible(true);
        });
    }
}
