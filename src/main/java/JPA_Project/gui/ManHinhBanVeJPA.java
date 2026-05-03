package JPA_Project.gui;

import JPA_Project.dto.*;
import JPA_Project.entity.*;
import JPA_Project.service.BanVeService;
import JPA_Project.network.RemoteRepository;
import JPA_Project.repository.KhachHangRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

/**
 * ManHinhBanVeJPA - Màn hình bán vé tàu với JPA.
 * Tách biệt UI và Business Logic theo mẫu thiết kế.
 */
public class ManHinhBanVeJPA extends JPanel implements ActionListener {

    // ================================================================================
    // SERVICES & REPOSITORIES
    // ================================================================================
    private final BanVeService banVeService;
    private final RemoteRepository remoteRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVien nhanVienHienTai;

    // ================================================================================
    // UI COMPONENTS - TÌM KIẾM
    // ================================================================================
    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JSpinner spnNgayDi;
    private JButton btnTimChuyen;
    private JPanel pnlChuyenTau;
    private JPanel pnlToa;

    // ================================================================================
    // UI COMPONENTS - CUSTOM COMPONENTS
    // ================================================================================
    private SoDoGhePanel soDoGhePanel;
    private KhachHangInfoPanel khachHangInfoPanel;

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
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);
    private static final Color COLOR_ORANGE = new Color(255, 165, 0);

    // ================================================================================
    // STATE
    // ================================================================================
    private ChuyenTauDTO chuyenTauHienTai;
    private ToaDTO toaHienTai;
    private String maToaDaChon = null;
    private Set<String> selectedSeats = new LinkedHashSet<>();
    private Map<String, GheDTO> seatDTOMap = new HashMap<>();

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
        this.remoteRepository = RemoteRepository.getInstance();
        this.khachHangRepository = new KhachHangRepository();

        // Initialize custom components
        this.soDoGhePanel = new SoDoGhePanel();
        this.khachHangInfoPanel = new KhachHangInfoPanel();

        khoiTaoGiaoDien();
        taiDanhSachGa();
        khoiTaoSuKien();
    }

    // ================================================================================
    // INITIALIZATION
    // ================================================================================

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

        // Đặt vị trí divider
        SwingUtilities.invokeLater(() -> splitMain.setDividerLocation(0.55));
    }

    private void khoiTaoSuKien() {
        // Setup seat selection callback
        soDoGhePanel.setOnSeatSelected(this::xuLyChonGhe);
        
        // Setup customer info callbacks
        khachHangInfoPanel.setOnRemoveSeat(this::xuLyHuyChonGhe);
        khachHangInfoPanel.setOnPassengerChanged(this::xuLyDoiLoaiVe);
    }

    // ================================================================================
    // PANEL CREATION
    // ================================================================================

    private JPanel taoPanelTrai() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        panel.add(taoPanelTimKiem());
        panel.add(Box.createVerticalStrut(10));

        panel.add(taoPanelDanhSachChuyenTau());
        panel.add(Box.createVerticalStrut(10));

        panel.add(taoPanelChonToa());
        panel.add(Box.createVerticalStrut(10));

        // Thêm SoDoGhePanel trực tiếp (đã có TitledBorder bên trong)
        JPanel soDoGheWrapper = new JPanel(new BorderLayout());
        soDoGheWrapper.setBorder(null);
        soDoGheWrapper.add(soDoGhePanel, BorderLayout.CENTER);
        panel.add(soDoGheWrapper);

        return panel;
    }

    private JPanel taoPanelPhai() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(0, 0, 0, 0)));
        panel.setPreferredSize(new Dimension(450, 0));

        // Thêm KhachHangInfoPanel vào đây
        JPanel khWrapper = new JPanel(new BorderLayout());
        khWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Thông tin hành khách"));
        khWrapper.add(khachHangInfoPanel, BorderLayout.CENTER);
        panel.add(khWrapper, BorderLayout.CENTER);

        // Footer với tổng kết và nút
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);

        // Panel tổng kết
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        lblSoGheDaChon = new JLabel("Đã chọn: 0 ghế");
        lblSoGheDaChon.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(lblSoGheDaChon);

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pricePanel.setOpaque(false);
        lblTongTien = new JLabel("Tổng tiền vé: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTien.setForeground(COLOR_ORANGE);
        pricePanel.add(lblTongTien);

        summaryPanel.add(infoPanel, BorderLayout.WEST);
        summaryPanel.add(pricePanel, BorderLayout.EAST);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        btnHuy = new JButton("< Hủy");
        btnHuy.setPreferredSize(new Dimension(90, 40));
        btnHuy.setFont(btnHuy.getFont().deriveFont(Font.BOLD, 14f));
        btnHuy.setBackground(new Color(220, 53, 69));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.setOpaque(true);
        btnHuy.setBorderPainted(false);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(this);

        btnTiepTheo = new JButton("Tiếp theo >");
        btnTiepTheo.setPreferredSize(new Dimension(120, 40));
        btnTiepTheo.setFont(btnTiepTheo.getFont().deriveFont(Font.BOLD, 14f));
        btnTiepTheo.setBackground(new Color(0, 123, 255));
        btnTiepTheo.setForeground(Color.WHITE);
        btnTiepTheo.setFocusPainted(false);
        btnTiepTheo.setOpaque(true);
        btnTiepTheo.setBorderPainted(false);
        btnTiepTheo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTiepTheo.addActionListener(this);
        btnTiepTheo.setEnabled(false);

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnTiepTheo);

        footerPanel.add(summaryPanel);
        footerPanel.add(buttonPanel);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoPanelTimKiem() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 249, 250));
        
        javax.swing.border.TitledBorder title = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Tìm kiếm chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 13f));
        panel.setBorder(title);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        row.setBackground(new Color(248, 249, 250));

        // Ga đi
        JLabel lblGaDi = new JLabel("Ga đi:");
        lblGaDi.setFont(new Font("Arial", Font.BOLD, 12));
        row.add(lblGaDi);

        cbGaDi = new JComboBox<>();
        cbGaDi.setBackground(Color.WHITE);
        row.add(cbGaDi);

        // Ga đến
        JLabel lblGaDen = new JLabel("Ga đến:");
        lblGaDen.setFont(new Font("Arial", Font.BOLD, 12));
        row.add(lblGaDen);

        cbGaDen = new JComboBox<>();
        cbGaDen.setBackground(Color.WHITE);
        row.add(cbGaDen);

        // Ngày đi
        JLabel lblNgayDi = new JLabel("Ngày đi:");
        lblNgayDi.setFont(new Font("Arial", Font.BOLD, 12));
        row.add(lblNgayDi);

        spnNgayDi = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgayDi, "dd/MM/yyyy");
        spnNgayDi.setEditor(dateEditor);
        spnNgayDi.setFont(new Font("Arial", Font.PLAIN, 12));
        row.add(spnNgayDi);

        // Nút tìm kiếm
        btnTimChuyen = new JButton("Tìm chuyến");
        btnTimChuyen.setBackground(COLOR_BLUE_LIGHT);
        btnTimChuyen.setForeground(Color.WHITE);
        btnTimChuyen.setFont(new Font("Arial", Font.BOLD, 12));
        btnTimChuyen.setFocusPainted(false);
        btnTimChuyen.setOpaque(true);
        btnTimChuyen.setBorderPainted(false);
        btnTimChuyen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTimChuyen.addActionListener(this);
        row.add(btnTimChuyen);

        panel.add(row);
        return panel;
    }

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

        return panel;
    }

    private JPanel taoPanelChonToa() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Chọn toa"));

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


        return panel;
    }

    private JLabel taoChuGiai(Color mau, String text) {
        JLabel label = new JLabel("  " + text + "  ");
        label.setOpaque(true);
        label.setBackground(mau);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        return label;
    }

    // ================================================================================
    // DATA LOADING
    // ================================================================================

    private void taiDanhSachGa() {
        try {
            List<Ga> allGa = remoteRepository.findAllGa();

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
    // SEARCH & SELECTION HANDLERS
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

    private void chonChuyenTau(ChuyenTauDTO ct) {
        this.chuyenTauHienTai = ct;
        System.out.println("Đã chọn chuyến tàu: " + ct.maChuyenTau());

        // Clear previous selections
        selectedSeats.clear();
        seatDTOMap.clear();
        khachHangInfoPanel.clear();
        soDoGhePanel.clear();
        capNhatTrangThai();

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

    private static final Color MAU_NUT_TOA_MAC_DINH = new Color(76, 175, 80); // Xanh lá
    private static final Color MAU_NUT_TOA_CHON = new Color(33, 150, 243);    // Xanh dương

    private JButton taoNutToa(ToaDTO toa) {
        String displayText = toa.maToa();
        JButton btn = new JButton(displayText);
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Mặc định xanh lá
        btn.setBackground(MAU_NUT_TOA_MAC_DINH);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.addActionListener(e -> chonToa(toa));

        return btn;
    }

    private void chonToa(ToaDTO toa) {
        this.toaHienTai = toa;
        this.maToaDaChon = toa.maToa();

        // Update button colors
        for (Component comp : pnlToa.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().contains(toa.maToa())) {
                    btn.setBackground(MAU_NUT_TOA_CHON);
                    btn.setForeground(Color.WHITE);
                } else {
                    btn.setBackground(MAU_NUT_TOA_MAC_DINH);
                    btn.setForeground(Color.WHITE);
                }
            }
        }

        taiSoDoGhe(toa.maToa());
    }

    private void taiSoDoGhe(String maToa) {
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
                    
                    // Store seat DTOs
                    seatDTOMap.clear();
                    for (GheDTO ghe : danhSachGhe) {
                        seatDTOMap.put(ghe.maChoDat(), ghe);
                    }
                    
                    // Render seats
                    String loaiToa = toaHienTai != null ? toaHienTai.loaiToa() : null;
                    String tenLoaiToa = toaHienTai != null ? toaHienTai.tenLoaiToa() : null;
                    soDoGhePanel.renderSeats(danhSachGhe, loaiToa, tenLoaiToa, maToa);
                    soDoGhePanel.updateSelectedSeats(selectedSeats);
                } catch (Exception e) {
                    System.err.println("Lỗi khi tải sơ đồ ghế: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // ================================================================================
    // SEAT SELECTION HANDLERS
    // ================================================================================

    private void xuLyChonGhe(GheDTO ghe) {
        String maCho = ghe.maChoDat();
        
        if (selectedSeats.contains(maCho)) {
            // Deselect
            xuLyHuyChonGhe(maCho);
        } else {
            // Select - add customer info panel
            selectedSeats.add(maCho);
            
            // Calculate initial price
            BigDecimal gia = tinhGiaChoGhe(maCho, "VT01");
            
            // Add to customer info panel
            khachHangInfoPanel.addSeat(ghe, "VT01", gia);
            
            // Update seat selection display
            soDoGhePanel.updateSelectedSeats(selectedSeats);
            capNhatTrangThai();
        }
    }

    private void xuLyHuyChonGhe(String maCho) {
        selectedSeats.remove(maCho);
        seatDTOMap.remove(maCho);
        khachHangInfoPanel.removeSeat(maCho);
        soDoGhePanel.updateSelectedSeats(selectedSeats);
        capNhatTrangThai();
    }

    private void xuLyDoiLoaiVe(String maCho, String maLoaiVe) {
        BigDecimal gia = tinhGiaChoGhe(maCho, maLoaiVe);
        khachHangInfoPanel.updatePrice(maCho, gia);
        capNhatTrangThai();
    }

    // ================================================================================
    // PRICE CALCULATION
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

    private void capNhatTrangThai() {
        int soGhe = selectedSeats.size();
        BigDecimal tongTien = khachHangInfoPanel.getTotalPrice();

        lblSoGheDaChon.setText("Đã chọn: " + soGhe + " ghế");
        lblTongTien.setText("Tổng tiền vé: " + formatCurrency(tongTien));
        btnTiepTheo.setEnabled(soGhe > 0);
    }

    // ================================================================================
    // UTILITY
    // ================================================================================

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VND";
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " VND";
    }

    // ================================================================================
    // PUBLIC METHODS
    // ================================================================================

    public ChuyenTauDTO getChuyenTauHienTai() {
        return chuyenTauHienTai;
    }

    /**
     * Get all customer information.
     */
    public Map<String, PassengerDTO> getThongTinKhachHang() {
        Map<String, PassengerDTO> result = new LinkedHashMap<>();
        Map<String, KhachHangInfoPanel.KhachHangInfo> allInfo = khachHangInfoPanel.getAllCustomerInfo();
        for (Map.Entry<String, KhachHangInfoPanel.KhachHangInfo> entry : allInfo.entrySet()) {
            KhachHangInfoPanel.KhachHangInfo info = entry.getValue();
            PassengerDTO passenger = new PassengerDTO(
                    info.getMaLoaiVe(),
                    info.getHoTen(),
                    info.getCccd(),
                    info.getSdt(),
                    null,
                    null
            );
            result.put(entry.getKey(), passenger);
        }
        return result;
    }

    public void resetAllData() {
        resetForm();
    }

    public void resetForm() {
        selectedSeats.clear();
        seatDTOMap.clear();
        khachHangInfoPanel.clear();
        soDoGhePanel.clear();
        chuyenTauHienTai = null;
        toaHienTai = null;
        maToaDaChon = null;

        pnlChuyenTau.removeAll();
        pnlChuyenTau.add(new JLabel("Chọn Ga đi, Ga đến và Ngày đi để tìm kiếm"));
        pnlChuyenTau.revalidate();

        pnlToa.removeAll();
        pnlToa.revalidate();

        capNhatTrangThai();
    }

    public void setOnTiepTheoCallback(Consumer<List<TicketDTO>> callback) {
        this.onTiepTheoCallback = callback;
    }

    // ================================================================================
    // ACTION LISTENER
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
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một ghế!");
                return;
            }

            List<TicketDTO> danhSachVe = new ArrayList<>();
            for (String maCho : selectedSeats) {
                GheDTO ghe = seatDTOMap.get(maCho);
                KhachHangInfoPanel.KhachHangInfo info = khachHangInfoPanel.getAllCustomerInfo().get(maCho);
                
                if (ghe != null && info != null) {
                    PassengerDTO passenger = new PassengerDTO(
                            info.getMaLoaiVe(),
                            info.getHoTen(),
                            info.getCccd(),
                            info.getSdt(),
                            null, // ngaySinh
                            null  // gioiTinh
                    );
                    TicketDTO ticket = new TicketDTO(
                            maCho,
                            passenger,
                            info.getMaLoaiVe(),
                            info.getGiaVe(),
                            true
                    );
                    danhSachVe.add(ticket);
                }
            }

            if (onTiepTheoCallback != null) {
                onTiepTheoCallback.accept(danhSachVe);
            }
        }
    }

    // ================================================================================
    // STATIC NESTED CLASS FOR COMPONENT INITIALIZATION
    // ================================================================================

    static {
        // Initialize components that require non-static context
    }
}
