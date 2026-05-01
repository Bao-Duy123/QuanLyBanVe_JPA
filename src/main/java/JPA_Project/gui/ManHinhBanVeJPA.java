package JPA_Project.gui;

import JPA_Project.dto.*;
import JPA_Project.entity.*;
import JPA_Project.service.BanVeService;
import JPA_Project.repository.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Consumer;

/**
 * ManHinhBanVeJPA - Màn hình bán vé tàu với JPA.
 * Giao diện đẹp, hiện đại theo mẫu thiết kế.
 */
public class ManHinhBanVeJPA extends JPanel implements ActionListener {

    // ================================================================================
    // SERVICES & REPOSITORIES
    // ================================================================================
    private final BanVeService banVeService;
    private final GaRepository gaRepository;
    private final KhachHangRepository khachHangRepository;
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
    private Map<String, JPanel> khachHangPanelMap = new LinkedHashMap<>();

    // ================================================================================
    // UI COMPONENTS - TỔNG KẾT
    // ================================================================================
    private JLabel lblTongTien;
    private JLabel lblSoGheDaChon;
    private JButton btnHuy;
    private JButton btnTiepTheo;

    // ================================================================================
    // MÀU SẮC - TOA TÀU
    // ================================================================================
    private static final Color COLOR_GHE_TRONG = new Color(200, 230, 201);
    private static final Color COLOR_GHE_DA_DAT = Color.DARK_GRAY;
    private static final Color COLOR_GHE_DANG_CHON = new Color(0, 123, 255);
    private static final Color COLOR_GHE_GIUONG = new Color(254, 215, 170);
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);
    private static final Color COLOR_ORANGE = new Color(255, 165, 0);
    private static final Color COLOR_BORDER = Color.GRAY;

    // Màu theo loại toa
    private static final Color COLOR_TOA_NGOI = new Color(200, 230, 201);      // Xanh lá nhạt - Ghế ngồi
    private static final Color COLOR_TOA_NGAM_4 = new Color(255, 243, 176);   // Vàng nhạt - Giường nằm khoang 4
    private static final Color COLOR_TOA_NGAM_6 = new Color(255, 224, 178);   // Cam nhạt - Giường nằm khoang 6

    // ================================================================================
    // DANH SÁCH LOẠI VÉ
    // ================================================================================
    private List<LoaiVe> danhSachLoaiVe;
    private DefaultComboBoxModel<String> loaiVeComboModel;

    // ================================================================================
    // CALLBACK
    // ================================================================================
    private Consumer<List<TicketDTO>> onTiepTheoCallback;

    // ================================================================================
    // CONSTRUCTOR
    // ================================================================================
    public ManHinhBanVeJPA() {
        this(null);
    }

    public ManHinhBanVeJPA(NhanVien nhanVien) {
        this.nhanVienHienTai = nhanVien;
        this.banVeService = new BanVeService();
        this.gaRepository = new GaRepository();
        this.khachHangRepository = new KhachHangRepository();
        this.danhSachLoaiVe = banVeService.getDanhSachLoaiVe();
        this.loaiVeComboModel = new DefaultComboBoxModel<>();

        khoiTaoGiaoDien();
        taiDanhSachGa();
    }

    /**
     * Helper: Lấy màu theo loại toa.
     */
    private Color getColorByLoaiToa(String loaiToa) {
        if (loaiToa == null) return COLOR_GHE_TRONG;
        if (loaiToa.contains("NGAM_6")) return COLOR_TOA_NGAM_6;
        if (loaiToa.contains("NGAM")) return COLOR_TOA_NGAM_4;
        return COLOR_TOA_NGOI;
    }

    /**
     * Helper: Lấy tên loại toa để hiển thị.
     */
    private String getTenLoaiToa(String loaiToa) {
        if (loaiToa == null) return "Ghế ngồi";
        if (loaiToa.contains("NGAM_6")) return "Giường nằm khoang 6";
        if (loaiToa.contains("NGAM")) return "Giường nằm khoang 4";
        return "Ghế ngồi";
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
        btnTiepTheo.setBackground(new Color(0, 123, 255));
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
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Tìm kiếm chuyến tàu"));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        row.setBackground(Color.WHITE);

        // Ga đi
        JLabel lblGaDi = new JLabel("Ga đi:");
        lblGaDi.setFont(new Font("Arial", Font.PLAIN, 13));
        row.add(lblGaDi);

        cbGaDi = new JComboBox<>();
        cbGaDi.setPreferredSize(new Dimension(150, 30));
        cbGaDi.setFont(new Font("Arial", Font.PLAIN, 13));
        row.add(cbGaDi);

        // Ga đến
        JLabel lblGaDen = new JLabel("Ga đến:");
        lblGaDen.setFont(new Font("Arial", Font.PLAIN, 13));
        row.add(lblGaDen);

        cbGaDen = new JComboBox<>();
        cbGaDen.setPreferredSize(new Dimension(150, 30));
        cbGaDen.setFont(new Font("Arial", Font.PLAIN, 13));
        row.add(cbGaDen);

        // Ngày đi
        JLabel lblNgayDi = new JLabel("Ngày đi:");
        lblNgayDi.setFont(new Font("Arial", Font.PLAIN, 13));
        row.add(lblNgayDi);

        spnNgayDi = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgayDi, "dd/MM/yyyy");
        spnNgayDi.setEditor(dateEditor);
        spnNgayDi.setPreferredSize(new Dimension(110, 30));
        row.add(spnNgayDi);

        // Nút tìm kiếm
        btnTimChuyen = new JButton("Tìm chuyến");
        btnTimChuyen.setPreferredSize(new Dimension(110, 35));
        btnTimChuyen.setBackground(COLOR_BLUE_LIGHT);
        btnTimChuyen.setForeground(Color.WHITE);
        btnTimChuyen.setFont(new Font("Arial", Font.BOLD, 13));
        btnTimChuyen.setFocusPainted(false);
        btnTimChuyen.addActionListener(this);
        row.add(btnTimChuyen);

        panel.add(row);
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
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Chọn toa"));

        // Panel chứa các nút toa
        JPanel pnlToaContainer = new JPanel(new BorderLayout());
        pnlToaContainer.setBackground(Color.WHITE);

        pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlToa.setBackground(Color.WHITE);

        JScrollPane scrollToa = new JScrollPane(pnlToa);
        scrollToa.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollToa.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollToa.setPreferredSize(new Dimension(0, 55));
        scrollToa.setBorder(null);
        scrollToa.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        pnlToaContainer.add(scrollToa, BorderLayout.CENTER);
        panel.add(pnlToaContainer);

        // Chú thích loại toa
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legend.setBackground(Color.WHITE);
        legend.add(taoChuGiai(COLOR_TOA_NGOI, "Ghế ngồi"));
        legend.add(taoChuGiai(COLOR_TOA_NGAM_4, "Giường nằm khoang 4"));
        legend.add(taoChuGiai(COLOR_TOA_NGAM_6, "Giường nằm khoang 6"));
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
        scrSoDoGhe.setPreferredSize(new Dimension(600, 250));

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

    private void taiDanhSachGa() {
        try {
            List<Ga> allGa = gaRepository.findAll();

            cbGaDi.removeAllItems();
            cbGaDen.removeAllItems();

            for (Ga ga : allGa) {
                cbGaDi.addItem(ga);
                cbGaDen.addItem(ga);
            }

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
    // XỬ LÝ TÌM KIẾM CHUYẾN TÀU
    // ================================================================================

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

        pnlChuyenTau.removeAll();
        pnlChuyenTau.add(new JLabel("Đang tìm kiếm..."));
        pnlChuyenTau.revalidate();
        pnlChuyenTau.repaint();

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

    private JPanel taoCardChuyenTau(ChuyenTauDTO ct) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(8, 12, 8, 12)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblTieuDe = new JLabel(ct.maChuyenTau());
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 12));
        lblTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTieuDe);

        String ngayDi = ct.ngayKhoiHanh() != null ? ct.ngayKhoiHanh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        String thongTin = String.format("%s → %s | %s | %s",
                ct.tenGaDi(), ct.tenGaDen(),
                ct.gioKhoiHanh() != null ? ct.gioKhoiHanh().toString() : "--:--",
                ngayDi);
        JLabel lblThongTin = new JLabel(thongTin);
        lblThongTin.setFont(new Font("Arial", Font.PLAIN, 11));
        lblThongTin.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblThongTin);

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

    private void chonChuyenTau(ChuyenTauDTO ct) {
        this.chuyenTauHienTai = ct;
        System.out.println("Đã chọn chuyến tàu: " + ct.maChuyenTau());

        taiDanhSachToa(ct.maChuyenTau());
    }

    private void taiDanhSachToa(String maChuyenTau) {
        pnlToa.removeAll();
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
                    e.printStackTrace();
                    pnlToa.removeAll();
                    pnlToa.add(new JLabel("Lỗi khi tải toa!"));
                    pnlToa.revalidate();
                }
            }
        };
        worker.execute();
    }

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

    private JButton taoNutToa(ToaDTO toa) {
        String displayText = toa.maToa() + " (" + getTenLoaiToa(toa.loaiToa()) + ")";
        JButton btn = new JButton(displayText);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Màu theo loại toa
        btn.setBackground(getColorByLoaiToa(toa.loaiToa()));
        btn.setForeground(Color.BLACK);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.addActionListener(e -> chonToa(toa));

        return btn;
    }

    // ================================================================================
    // XỬ LÝ CHỌN TOA VÀ VẼ SƠ ĐỒ GHẾ
    // ================================================================================

    private void chonToa(ToaDTO toa) {
        this.toaHienTai = toa;
        this.maToaDaChon = toa.maToa();

        // Đổi màu nút toa đã chọn
        for (Component comp : pnlToa.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().contains(toa.maToa())) {
                    btn.setBackground(COLOR_GHE_DANG_CHON);
                    btn.setForeground(Color.WHITE);
                } else {
                    // Reset về màu gốc - cần tìm lại loại toa
                    // Đơn giản: giữ nguyên màu
                }
            }
        }

        taiSoDoGhe(toa.maToa());
    }

    private void taiSoDoGhe(String maToa) {
        pnlSoDoGhe.removeAll();
        JLabel loading = new JLabel("Đang tải sơ đồ ghế...");
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSoDoGhe.add(Box.createVerticalGlue());
        pnlSoDoGhe.add(loading);
        pnlSoDoGhe.add(Box.createVerticalGlue());
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
                    e.printStackTrace();
                    pnlSoDoGhe.removeAll();
                    pnlSoDoGhe.add(new JLabel("Lỗi khi tải sơ đồ ghế!"));
                    pnlSoDoGhe.revalidate();
                }
            }
        };
        worker.execute();
    }

    /**
     * Vẽ sơ đồ ghế - phân biệt ghế ngồi và giường nằm.
     */
    private void veSoDoGhe(List<GheDTO> danhSachGhe) {
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        gheDTOMap.clear();

        if (danhSachGhe == null || danhSachGhe.isEmpty()) {
            pnlSoDoGhe.add(Box.createVerticalGlue());
            pnlSoDoGhe.add(new JLabel("Toa này không có ghế nào!"));
            pnlSoDoGhe.add(Box.createVerticalGlue());
            pnlSoDoGhe.revalidate();
            return;
        }

        boolean laToaGiuongNam = toaHienTai != null &&
                toaHienTai.loaiToa() != null &&
                toaHienTai.loaiToa().contains("NGAM");

        if (laToaGiuongNam) {
            veSoDoGiuongNam(danhSachGhe);
        } else {
            veSoDoGheNgoi(danhSachGhe);
        }

        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }

    /**
     * Vẽ sơ đồ ghế ngồi - chia theo cột, mỗi cột 4 dòng.
     */
    private void veSoDoGheNgoi(List<GheDTO> danhSachGhe) {
        // Sắp xếp theo số ghế
        List<GheDTO> sortedGhe = danhSachGhe.stream()
                .sorted(Comparator.comparingInt(g -> {
                    String soCho = g.soCho();
                    try {
                        return Integer.parseInt(soCho);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }))
                .collect(Collectors.toList());

        int soCot = 4;
        int soGheMoiCot = 4;

        // Tính số hàng cần thiết
        int soGhe = sortedGhe.size();
        int soHang = (int) Math.ceil((double) soGhe / soCot);

        // Tạo panel chính với GridLayout
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(Color.WHITE);

        // Panel hiển thị số cột (A, B, C, D...)
        JPanel columnHeader = new JPanel(new GridLayout(1, soCot, 5, 5));
        columnHeader.setBackground(Color.WHITE);
        columnHeader.setBorder(new EmptyBorder(5, 55, 5, 5));

        for (int c = 0; c < soCot; c++) {
            JLabel lblCol = new JLabel(Character.toString((char) ('A' + c)), SwingConstants.CENTER);
            lblCol.setFont(new Font("Arial", Font.BOLD, 12));
            lblCol.setOpaque(true);
            lblCol.setBackground(new Color(240, 240, 240));
            columnHeader.add(lblCol);
        }
        containerPanel.add(columnHeader, BorderLayout.NORTH);

        // Panel grid ghế
        JPanel gridPanel = new JPanel(new GridLayout(0, soCot, 5, 5));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        int index = 0;
        for (int hang = 0; hang < soHang; hang++) {
            for (int cot = 0; cot < soCot; cot++) {
                if (index < soGhe) {
                    GheDTO ghe = sortedGhe.get(index);
                    JButton btnGhe = taoNutGhe(ghe, false);
                    gridPanel.add(btnGhe);
                    seatButtonsMap.put(ghe.maChoDat(), btnGhe);
                    gheDTOMap.put(ghe.maChoDat(), ghe);
                    index++;
                } else {
                    // Ô trống
                    gridPanel.add(Box.createVerticalStrut(50));
                }
            }
        }

        containerPanel.add(gridPanel, BorderLayout.CENTER);

        // Thêm vào panel chính
        pnlSoDoGhe.add(containerPanel);
    }

    /**
     * Vẽ sơ đồ giường nằm - chia theo khoang.
     * Mỗi khoang có 2 cột đối diện: Giường 1,3,5 (trái) và 2,4,6 (phải)
     */
    private void veSoDoGiuongNam(List<GheDTO> danhSachGhe) {
        // Nhóm ghế theo khoang (chuyển Integer sang String để groupBy)
        Map<String, List<GheDTO>> gheTheoKhoang = danhSachGhe.stream()
                .collect(Collectors.groupingBy(g -> g.khoang() != null ? String.valueOf(g.khoang()) : "0"));

        // Sắp xếp theo khoang
        List<String> sortedKhoang = gheTheoKhoang.keySet().stream()
                .sorted(Comparator.comparingInt(k -> {
                    try {
                        return Integer.parseInt(k);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }))
                .collect(Collectors.toList());

        // Panel chính chứa các khoang
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBackground(Color.WHITE);

        for (String khoang : sortedKhoang) {
            List<GheDTO> gheTrongKhoang = gheTheoKhoang.get(khoang);
            JPanel khoangPanel = taoKhoangPanel(khoang, gheTrongKhoang);
            containerPanel.add(khoangPanel);
            containerPanel.add(Box.createVerticalStrut(10));
        }

        pnlSoDoGhe.add(containerPanel);
    }

    /**
     * Tạo panel cho một khoang giường nằm.
     */
    private JPanel taoKhoangPanel(String khoang, List<GheDTO> danhSachGhe) {
        JPanel khoangPanel = new JPanel(new BorderLayout(10, 0));
        khoangPanel.setBackground(Color.WHITE);
        khoangPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "Khoang " + khoang),
                new EmptyBorder(10, 10, 10, 10)));

        // Sắp xếp ghế: bên trái (1,3,5), bên phải (2,4,6)
        List<GheDTO> gheTrai = new ArrayList<>();
        List<GheDTO> ghePhai = new ArrayList<>();

        for (GheDTO ghe : danhSachGhe) {
            int soCho;
            try {
                soCho = Integer.parseInt(ghe.soCho());
            } catch (NumberFormatException e) {
                soCho = 0;
            }

            if (soCho % 2 == 1) { // Lẻ -> bên trái
                gheTrai.add(ghe);
            } else { // Chẵn -> bên phải
                ghePhai.add(ghe);
            }
        }

        // Sắp xếp theo tầng
        Comparator<GheDTO> byTang = Comparator.comparingInt(g -> g.tang() != null ? g.tang() : 0);
        gheTrai.sort(byTang);
        ghePhai.sort(byTang);

        // Panel bên trái
        JPanel panelTrai = new JPanel();
        panelTrai.setLayout(new BoxLayout(panelTrai, BoxLayout.Y_AXIS));
        panelTrai.setBackground(Color.WHITE);

        for (GheDTO ghe : gheTrai) {
            JButton btnGhe = taoNutGiuong(ghe);
            panelTrai.add(btnGhe);
            seatButtonsMap.put(ghe.maChoDat(), btnGhe);
            gheDTOMap.put(ghe.maChoDat(), ghe);
            panelTrai.add(Box.createVerticalStrut(5));
        }

        // Panel bên phải
        JPanel panelPhai = new JPanel();
        panelPhai.setLayout(new BoxLayout(panelPhai, BoxLayout.Y_AXIS));
        panelPhai.setBackground(Color.WHITE);

        for (GheDTO ghe : ghePhai) {
            JButton btnGhe = taoNutGiuong(ghe);
            panelPhai.add(btnGhe);
            seatButtonsMap.put(ghe.maChoDat(), btnGhe);
            gheDTOMap.put(ghe.maChoDat(), ghe);
            panelPhai.add(Box.createVerticalStrut(5));
        }

        // Panel trung tâm để 2 bên căn đều
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(panelTrai);
        centerPanel.add(panelPhai);

        // Wrapper panel để canh giữa
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(centerPanel);

        khoangPanel.add(wrapperPanel, BorderLayout.CENTER);

        return khoangPanel;
    }

    /**
     * Tạo nút giường nằm.
     */
    private JButton taoNutGiuong(GheDTO ghe) {
        JButton btn = new JButton(ghe.soCho());
        btn.setPreferredSize(new Dimension(60, 45));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (ghe.daDat()) {
            btn.setBackground(COLOR_GHE_DA_DAT);
            btn.setForeground(Color.WHITE);
            btn.setEnabled(false);
        } else {
            if (ticketMap.containsKey(ghe.maChoDat())) {
                btn.setBackground(COLOR_GHE_DANG_CHON);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(COLOR_GHE_GIUONG);
                btn.setForeground(Color.BLACK);
            }
        }

        btn.setBorderPainted(true);
        btn.setOpaque(true);
        btn.setToolTipText(ghe.maChoDat() + " - " + (ghe.daDat() ? "Đã đặt" : "Trống"));
        btn.addActionListener(e -> xuLyClickGhe(ghe));

        return btn;
    }

    private JButton taoNutGhe(GheDTO ghe, boolean laToaGiuongNam) {
        JButton btn = new JButton(ghe.soCho());
        btn.setPreferredSize(new Dimension(55, 45));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (ghe.daDat()) {
            btn.setBackground(COLOR_GHE_DA_DAT);
            btn.setForeground(Color.WHITE);
            btn.setEnabled(false);
        } else {
            if (ticketMap.containsKey(ghe.maChoDat())) {
                btn.setBackground(COLOR_GHE_DANG_CHON);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(laToaGiuongNam ? COLOR_GHE_GIUONG : COLOR_GHE_TRONG);
                btn.setForeground(Color.BLACK);
            }
        }

        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setToolTipText(ghe.maChoDat() + " - " + (ghe.daDat() ? "Đã đặt" : "Trống"));
        btn.addActionListener(e -> xuLyClickGhe(ghe));

        return btn;
    }

    private void xuLyClickGhe(GheDTO ghe) {
        if (ghe.daDat()) {
            JOptionPane.showMessageDialog(this, "Ghế này đã được đặt!");
            return;
        }

        if (ticketMap.containsKey(ghe.maChoDat())) {
            // Bỏ chọn ghế - xóa panel thông tin
            huyChonGhe(ghe.maChoDat());
            xoaPanelKhachHang(ghe.maChoDat());
        } else {
            // Chọn ghế - thêm panel thông tin vào danh sách
            themPanelKhachHang(ghe);
        }

        capNhatTrangThaiGhe();
        tinhVaHienThiTongTien();
    }

    private void huyChonGhe(String maCho) {
        ticketMap.remove(maCho);
        khachHangMap.remove(maCho);
    }

    private void capNhatTrangThaiGhe() {
        boolean laToaGiuongNam = toaHienTai != null &&
                toaHienTai.loaiToa() != null &&
                toaHienTai.loaiToa().contains("NGAM");

        for (Map.Entry<String, JButton> entry : seatButtonsMap.entrySet()) {
            String maCho = entry.getKey();
            JButton btn = entry.getValue();
            GheDTO ghe = gheDTOMap.get(maCho);

            if (ghe != null && ghe.daDat()) continue;

            if (ticketMap.containsKey(maCho)) {
                btn.setBackground(COLOR_GHE_DANG_CHON);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(laToaGiuongNam ? COLOR_GHE_GIUONG : COLOR_GHE_TRONG);
                btn.setForeground(Color.BLACK);
            }
        }
    }

    // ================================================================================
    // XỬ LÝ THÔNG TIN KHÁCH HÀNG - DẠNG INLINE
    // ================================================================================

    /**
     * Thêm panel nhập thông tin khách hàng vào danh sách.
     */
    private void themPanelKhachHang(GheDTO ghe) {
        String maCho = ghe.maChoDat();

        JPanel khachPanel = new JPanel();
        khachPanel.setLayout(new BoxLayout(khachPanel, BoxLayout.Y_AXIS));
        khachPanel.setBackground(new Color(255, 248, 240));
        khachPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ORANGE, 1),
                new EmptyBorder(10, 10, 10, 10)));
        khachPanel.setName(maCho);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(255, 228, 204));
        header.setOpaque(true);

        JLabel lblMaGhe = new JLabel("Ghế: " + maCho + " (" + ghe.soCho() + ")");
        lblMaGhe.setFont(new Font("Arial", Font.BOLD, 13));
        lblMaGhe.setForeground(COLOR_BLUE_LIGHT);
        header.add(lblMaGhe, BorderLayout.WEST);

        JButton btnXoa = new JButton("✕");
        btnXoa.setPreferredSize(new Dimension(25, 25));
        btnXoa.setFocusPainted(false);
        btnXoa.setBackground(COLOR_ORANGE);
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setBorderPainted(false);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.addActionListener(e -> {
            huyChonGhe(maCho);
            xoaPanelKhachHang(maCho);
            capNhatTrangThaiGhe();
            tinhVaHienThiTongTien();
        });
        header.add(btnXoa, BorderLayout.EAST);
        khachPanel.add(header);

        // Form inputs
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        Font fontLabel = new Font("Arial", Font.BOLD, 11);
        Font fontInput = new Font("Arial", Font.PLAIN, 11);

        // Họ tên
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField txtHoTen = new JTextField(15);
        txtHoTen.setFont(fontInput);
        txtHoTen.setName(maCho + "_hoTen");
        formPanel.add(txtHoTen, gbc);

        // CCCD
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("CCCD:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField txtCCCD = new JTextField(15);
        txtCCCD.setFont(fontInput);
        txtCCCD.setName(maCho + "_cccd");
        formPanel.add(txtCCCD, gbc);

        // Đối tượng
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Đối tượng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<String> cbDoiTuong = new JComboBox<>(new String[]{"Người lớn", "Sinh viên", "Trẻ em", "Người cao tuổi"});
        cbDoiTuong.setFont(fontInput);
        cbDoiTuong.setName(maCho + "_doiTuong");
        formPanel.add(cbDoiTuong, gbc);

        // Giá vé
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Giá vé:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel lblGia = new JLabel("...");
        lblGia.setFont(fontLabel);
        lblGia.setForeground(COLOR_ORANGE);
        formPanel.add(lblGia, gbc);

        khachPanel.add(formPanel);

        // Lưu panel
        khachHangPanelMap.put(maCho, khachPanel);

        // Cập nhật danh sách hiển thị
        pnlDanhSachKhachHang.removeAll();
        for (Map.Entry<String, JPanel> entry : khachHangPanelMap.entrySet()) {
            pnlDanhSachKhachHang.add(entry.getValue());
            pnlDanhSachKhachHang.add(Box.createVerticalStrut(8));
        }
        pnlDanhSachKhachHang.revalidate();
        pnlDanhSachKhachHang.repaint();

        // Tính giá ban đầu (Người lớn - VT01)
        tinhLaiGiaVaLuu(maCho, "VT01", txtHoTen, txtCCCD, cbDoiTuong, lblGia);

        // Sự kiện thay đổi đối tượng
        cbDoiTuong.addActionListener(e -> {
            String doiTuong = (String) cbDoiTuong.getSelectedItem();
            String maLoaiVe = chuyenDoiTuongSangMaLoaiVe(doiTuong);
            tinhLaiGiaVaLuu(maCho, maLoaiVe, txtHoTen, txtCCCD, cbDoiTuong, lblGia);
        });

        // Sự kiện thay đổi CCCD - kiểm tra khách hàng cũ
        txtCCCD.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String cccd = txtCCCD.getText().trim();
                if (cccd.length() >= 9) {
                    kiemTraKhachHang(maCho, cccd, txtHoTen, cbDoiTuong);
                }
            }
        });
    }

    /**
     * Chuyển đổi đối tượng sang mã loại vé.
     */
    private String chuyenDoiTuongSangMaLoaiVe(String doiTuong) {
        if (doiTuong == null) return "VT01";
        switch (doiTuong) {
            case "Sinh viên": return "VT03";
            case "Trẻ em": return "VT02";
            case "Người cao tuổi": return "VT04";
            default: return "VT01";
        }
    }

    /**
     * Tính lại giá và lưu thông tin.
     */
    private void tinhLaiGiaVaLuu(String maCho, String maLoaiVe, JTextField txtHoTen,
                                  JTextField txtCCCD, JComboBox<String> cbDoiTuong, JLabel lblGia) {
        BigDecimal gia = tinhGiaChoGhe(maCho, maLoaiVe);
        lblGia.setText(formatCurrency(gia));

        // Lưu vào ticketMap và khachHangMap
        LocalDate ngaySinh = null;
        PassengerDTO passenger = new PassengerDTO(maLoaiVe,
                txtHoTen.getText().trim(),
                txtCCCD.getText().trim(),
                null, ngaySinh, null);

        TicketDTO ticket = new TicketDTO(maCho, passenger, maLoaiVe, gia, true);

        khachHangMap.put(maCho, passenger);
        ticketMap.put(maCho, ticket);
    }

    /**
     * Kiểm tra khách hàng theo CCCD và điền thông tin.
     */
    private void kiemTraKhachHang(String maCho, String cccd, JTextField txtHoTen, JComboBox<String> cbDoiTuong) {
        try {
            Optional<KhachHang> khOpt = banVeService.getKhachHangByCCCD(cccd);
            if (khOpt.isPresent()) {
                KhachHang kh = khOpt.get();
                SwingUtilities.invokeLater(() -> {
                    txtHoTen.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
                });
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra CCCD: " + e.getMessage());
        }
    }

    /**
     * Xóa panel khách hàng.
     */
    private void xoaPanelKhachHang(String maCho) {
        khachHangPanelMap.remove(maCho);

        pnlDanhSachKhachHang.removeAll();
        if (khachHangPanelMap.isEmpty()) {
            JLabel placeholder = new JLabel("Chọn ghế để nhập thông tin hành khách");
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            placeholder.setForeground(Color.GRAY);
            pnlDanhSachKhachHang.add(placeholder);
        } else {
            for (Map.Entry<String, JPanel> entry : khachHangPanelMap.entrySet()) {
                pnlDanhSachKhachHang.add(entry.getValue());
                pnlDanhSachKhachHang.add(Box.createVerticalStrut(8));
            }
        }
        pnlDanhSachKhachHang.revalidate();
        pnlDanhSachKhachHang.repaint();
    }

    // ================================================================================
    // CẬP NHẬT GIAO DIỆN
    // ================================================================================

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

        btnTiepTheo.setEnabled(soGhe > 0);
    }

    // ================================================================================
    // TIỆN ÍCH
    // ================================================================================

    private BigDecimal tinhGiaChoGhe(String maCho, String maLoaiVe) {
        try {
            if (chuyenTauHienTai == null || toaHienTai == null) {
                return BigDecimal.ZERO;
            }
            String maLoaiToa = toaHienTai.loaiToa() != null ? toaHienTai.loaiToa() : "G_GHE";
            return banVeService.tinhGiaVe(chuyenTauHienTai.maChuyenTau(), maLoaiToa, maLoaiVe, null);
        } catch (Exception e) {
            System.err.println("Lỗi khi tính giá: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    // ================================================================================
    // PUBLIC METHODS (for BanVePanelJPA)
    // ================================================================================

    public ChuyenTauDTO getChuyenTauHienTai() {
        return chuyenTauHienTai;
    }

    public Map<String, PassengerDTO> getThongTinKhachHang() {
        return khachHangMap;
    }

    public void resetAllData() {
        resetForm();
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VND";
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " VND";
    }

    public void setOnTiepTheoCallback(Consumer<List<TicketDTO>> callback) {
        this.onTiepTheoCallback = callback;
    }

    public void resetForm() {
        ticketMap.clear();
        khachHangMap.clear();
        khachHangPanelMap.clear();
        seatButtonsMap.clear();
        gheDTOMap.clear();
        chuyenTauHienTai = null;
        toaHienTai = null;
        maToaDaChon = null;

        pnlChuyenTau.removeAll();
        pnlChuyenTau.add(new JLabel("Chọn Ga đi, Ga đến và Ngày đi để tìm kiếm"));
        pnlChuyenTau.revalidate();

        pnlToa.removeAll();
        pnlToa.add(new JLabel("Chọn chuyến tàu để xem toa"));
        pnlToa.revalidate();

        pnlSoDoGhe.removeAll();
        JLabel placeholder = new JLabel("Chọn một toa để xem sơ đồ ghế");
        placeholder.setForeground(Color.GRAY);
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSoDoGhe.add(placeholder);
        pnlSoDoGhe.revalidate();

        pnlDanhSachKhachHang.removeAll();
        JLabel placeholderKH = new JLabel("Chọn ghế để nhập thông tin hành khách");
        placeholderKH.setAlignmentX(Component.CENTER_ALIGNMENT);
        placeholderKH.setForeground(Color.GRAY);
        pnlDanhSachKhachHang.add(placeholderKH);
        pnlDanhSachKhachHang.revalidate();

        tinhVaHienThiTongTien();
    }

    // ================================================================================
    // XỬ LÝ SỰ KIỆN
    // ================================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTimChuyen) {
            timKiemChuyenTau();
        } else if (e.getSource() == btnHuy) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn hủy bỏ tất cả vé đã chọn?",
                    "Xác nhận hủy",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                resetForm();
            }
        } else if (e.getSource() == btnTiepTheo) {
            if (ticketMap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một ghế!");
                return;
            }

            List<TicketDTO> danhSachVe = new ArrayList<>(ticketMap.values());

            if (onTiepTheoCallback != null) {
                onTiepTheoCallback.accept(danhSachVe);
            }
        }
    }
}
