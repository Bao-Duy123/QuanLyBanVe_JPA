package JPA_Project.gui;

import JPA_Project.dto.*;
import JPA_Project.entity.*;
import JPA_Project.service.BanVeService;
import JPA_Project.repository.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * ManHinhXacNhanBanVeJPA - Phiên bản JPA của màn hình xác nhận bán vé.
 * Hoạt động độc lập trong project JPA, sử dụng BanVeService.
 * 
 * Luồng hoạt động:
 * 1. Hiển thị thông tin chuyến tàu và danh sách vé đã chọn
 * 2. Nhập thông tin thanh toán (hình thức, tiền khách đưa)
 * 3. Áp dụng khuyến mãi (nếu có)
 * 4. Xác nhận thanh toán -> Gọi BanVeService.thucHienBanVe()
 */
public class ManHinhXacNhanBanVeJPA extends JPanel {

    // ================================================================================
    // SERVICES
    // ================================================================================
    private final BanVeService banVeService;
    private final NhanVien nhanVienHienTai;

    // ================================================================================
    // DATA
    // ================================================================================
    private List<TicketDTO> danhSachVe;
    private PassengerDTO khachHangDaiDien;
    private ChuyenTauDTO chuyenTau;
    private Map<String, PassengerDTO> thongTinKhachHangMap;
    
    private KhuyenMai khuyenMaiApDung;
    private String maNV;
    private String tenNV;

    // ================================================================================
    // UI COMPONENTS - THÔNG TIN HÀNH TRÌNH
    // ================================================================================
    private JLabel lblMaChuyen;
    private JLabel lblTuyenDuong;
    private JLabel lblThoiGian;
    private JLabel lblSoHieuTau;

    // ================================================================================
    // UI COMPONENTS - DANH SÁCH VÉ
    // ================================================================================
    private JTable tblDanhSachVe;
    private DefaultTableModel tableModel;

    // ================================================================================
    // UI COMPONENTS - THANH TOÁN
    // ================================================================================
    private JLabel lblGiaVe;
    private JLabel lblGiamGia;
    private JLabel lblTongTien;
    private JComboBox<KhuyenMai> cbKhuyenMai;
    private JComboBox<String> cbHinhThucTT;
    private JTextField txtTienKhachDua;
    private JTextField txtTienThoiLai;
    private JLabel lblTienBangChu;

    // ================================================================================
    // UI COMPONENTS - HÓA ĐƠN
    // ================================================================================
    private JLabel lblMaHoaDon;
    private JLabel lblNguoiLapHD;
    private JLabel lblNgayLap;

    // ================================================================================
    // UI COMPONENTS - BUTTONS
    // ================================================================================
    private JButton btnXacNhan;
    private JButton btnQuayLai;
    private JButton btnHuy;
    private JButton btnKetThuc;

    // ================================================================================
    // MÀU SẮC
    // ================================================================================
    private static final Color COLOR_XANH_BTN = new Color(0, 180, 110);
    private static final Color COLOR_CAM_BTN = new Color(255, 140, 0);
    private static final Color COLOR_DO_NUT = new Color(220, 53, 69);

    // ================================================================================
    // FORMATTER
    // ================================================================================
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    // ================================================================================
    // CALLBACKS
    // ================================================================================
    private Runnable onQuayLaiCallback;
    private Runnable onKetThucCallback;

    // ================================================================================
    // CONSTRUCTOR
    // ================================================================================
    public ManHinhXacNhanBanVeJPA() {
        this(null, null, null, null);
    }

    public ManHinhXacNhanBanVeJPA(
            List<TicketDTO> danhSachVe,
            PassengerDTO khachHangDaiDien,
            ChuyenTauDTO chuyenTau,
            Map<String, PassengerDTO> thongTinKhachHangMap) {
        this(danhSachVe, khachHangDaiDien, chuyenTau, thongTinKhachHangMap, null);
    }

    public ManHinhXacNhanBanVeJPA(
            List<TicketDTO> danhSachVe,
            PassengerDTO khachHangDaiDien,
            ChuyenTauDTO chuyenTau,
            Map<String, PassengerDTO> thongTinKhachHangMap,
            NhanVien nhanVien) {

        this.banVeService = new BanVeService();
        this.nhanVienHienTai = nhanVien;
        this.danhSachVe = danhSachVe != null ? new ArrayList<>(danhSachVe) : new ArrayList<>();
        this.khachHangDaiDien = khachHangDaiDien;
        this.chuyenTau = chuyenTau;
        this.thongTinKhachHangMap = thongTinKhachHangMap != null ? new HashMap<>(thongTinKhachHangMap) : new HashMap<>();

        // Lấy thông tin nhân viên
        layThongTinNhanVien();

        khoiTaoGiaoDien();
        taiKhuyenMai();
        capNhatGiaoDien();
    }

    /**
     * Lấy thông tin nhân viên đang đăng nhập.
     */
    private void layThongTinNhanVien() {
        if (nhanVienHienTai != null) {
            this.maNV = nhanVienHienTai.getMaNV();
            this.tenNV = nhanVienHienTai.getHoTen();
        } else {
            this.maNV = "ADMIN001";
            this.tenNV = "Admin";
        }
    }

    /**
     * Khởi tạo giao diện chính.
     */
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        // Tiêu đề
        JLabel tieuDe = new JLabel("Xác nhận bán vé", SwingConstants.CENTER);
        tieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        tieuDe.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(tieuDe, BorderLayout.NORTH);

        // Split pane chính
        JSplitPane splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitMain.setResizeWeight(0.5);
        splitMain.setDividerSize(5);
        splitMain.setBorder(null);

        // Panel bên trái: Thông tin + Thanh toán
        JPanel panelTrai = taoPanelTrai();
        splitMain.setLeftComponent(panelTrai);

        // Panel bên phải: Hóa đơn
        JPanel panelPhai = taoPanelPhai();
        splitMain.setRightComponent(panelPhai);

        add(splitMain, BorderLayout.CENTER);

        // Footer
        JPanel footer = taoFooter();
        add(footer, BorderLayout.SOUTH);

        // Đặt vị trí divider
        SwingUtilities.invokeLater(() -> splitMain.setDividerLocation(0.5));
    }

    /**
     * Tạo panel bên trái.
     */
    private JPanel taoPanelTrai() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1),
                new EmptyBorder(15, 15, 15, 15)));

        // 1. Thông tin hành trình
        panel.add(taoPanelThongTinHanhTrinh());
        panel.add(Box.createVerticalStrut(15));

        // 2. Danh sách vé
        panel.add(taoPanelDanhSachVe());
        panel.add(Box.createVerticalStrut(15));

        // 3. Tổng kết tiền
        panel.add(taoPanelTongKet());
        panel.add(Box.createVerticalStrut(15));

        // 4. Thanh toán
        panel.add(taoPanelThanhToan());

        return panel;
    }

    /**
     * Tạo panel bên phải (hóa đơn).
     */
    private JPanel taoPanelPhai() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1),
                new EmptyBorder(15, 15, 15, 15)));

        // Tiêu đề hóa đơn
        JLabel tieuDeHD = new JLabel("HÓA ĐƠN", SwingConstants.CENTER);
        tieuDeHD.setFont(new Font("Arial", Font.BOLD, 18));
        tieuDeHD.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(tieuDeHD, BorderLayout.NORTH);

        // Thông tin hóa đơn
        JPanel thongTinHD = new JPanel();
        thongTinHD.setLayout(new BoxLayout(thongTinHD, BoxLayout.Y_AXIS));
        thongTinHD.setBackground(Color.WHITE);

        lblMaHoaDon = new JLabel("Mã HD: Đang chờ...");
        lblMaHoaDon.setFont(new Font("Arial", Font.BOLD, 14));

        lblNguoiLapHD = new JLabel("Người lập: " + (tenNV != null ? tenNV : "Admin"));
        lblNgayLap = new JLabel("Ngày lập: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        thongTinHD.add(lblMaHoaDon);
        thongTinHD.add(Box.createVerticalStrut(5));
        thongTinHD.add(lblNguoiLapHD);
        thongTinHD.add(Box.createVerticalStrut(5));
        thongTinHD.add(lblNgayLap);

        panel.add(thongTinHD, BorderLayout.CENTER);

        // Footer hóa đơn
        JPanel footerHD = new JPanel();
        footerHD.setLayout(new BoxLayout(footerHD, BoxLayout.Y_AXIS));
        footerHD.setBackground(Color.WHITE);

        JSeparator sep = new JSeparator();
        footerHD.add(sep);
        footerHD.add(Box.createVerticalStrut(10));

        lblTienBangChu = new JLabel("Số tiền bằng chữ: ");
        lblTienBangChu.setFont(new Font("Arial", Font.ITALIC, 12));
        footerHD.add(lblTienBangChu);

        panel.add(footerHD, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo panel footer với các nút.
     */
    private JPanel taoFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 40));
        btnHuy.setBackground(COLOR_DO_NUT);
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.addActionListener(e -> huyBo());

        btnQuayLai = new JButton("< Quay lại");
        btnQuayLai.setPreferredSize(new Dimension(120, 40));
        btnQuayLai.setBackground(Color.GRAY);
        btnQuayLai.setForeground(Color.WHITE);
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.addActionListener(e -> quayLai());

        btnXacNhan = new JButton("Xác nhận thanh toán");
        btnXacNhan.setPreferredSize(new Dimension(180, 40));
        btnXacNhan.setBackground(COLOR_XANH_BTN);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.addActionListener(e -> xacNhanThanhToan());

        btnKetThuc = new JButton("Kết thúc >");
        btnKetThuc.setPreferredSize(new Dimension(120, 40));
        btnKetThuc.setBackground(COLOR_CAM_BTN);
        btnKetThuc.setForeground(Color.WHITE);
        btnKetThuc.setFocusPainted(false);
        btnKetThuc.setEnabled(false);
        btnKetThuc.addActionListener(e -> ketThuc());

        panel.add(btnHuy);
        panel.add(btnQuayLai);
        panel.add(btnXacNhan);
        panel.add(btnKetThuc);

        return panel;
    }

    /**
     * Tạo panel thông tin hành trình.
     */
    private JPanel taoPanelThongTinHanhTrinh() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin hành trình"));

        if (chuyenTau != null) {
            lblMaChuyen = new JLabel("Mã chuyến: " + chuyenTau.maChuyenTau());
            lblTuyenDuong = new JLabel("Tuyến: " + chuyenTau.tenGaDi() + " → " + chuyenTau.tenGaDen());
            String thoiGian = chuyenTau.gioKhoiHanh() != null ? chuyenTau.gioKhoiHanh().toString() : "--:--";
            lblThoiGian = new JLabel("Khởi hành: " + thoiGian + " ngày " + chuyenTau.ngayKhoiHanh());
            lblSoHieuTau = new JLabel("Số hiệu tàu: " + chuyenTau.maTau());

            panel.add(lblMaChuyen);
            panel.add(lblTuyenDuong);
            panel.add(lblThoiGian);
            panel.add(lblSoHieuTau);
        } else {
            panel.add(new JLabel("Không có thông tin chuyến tàu"));
        }

        return panel;
    }

    /**
     * Tạo panel danh sách vé.
     */
    private JPanel taoPanelDanhSachVe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách vé"));

        String[] cot = {"STT", "Ghế", "Họ tên", "Loại vé", "Giá vé"};
        tableModel = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDanhSachVe = new JTable(tableModel);
        tblDanhSachVe.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tblDanhSachVe);
        scrollPane.setPreferredSize(new Dimension(400, 150));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel tổng kết tiền.
     */
    private JPanel taoPanelTongKet() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Tổng kết"));

        // Giá vé
        JPanel pnlGiaVe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlGiaVe.setBackground(Color.WHITE);
        pnlGiaVe.add(new JLabel("Giá vé: "));
        lblGiaVe = new JLabel("0 VND");
        lblGiaVe.setFont(new Font("Arial", Font.BOLD, 12));
        pnlGiaVe.add(lblGiaVe);
        panel.add(pnlGiaVe);

        // Giảm giá
        JPanel pnlGiamGia = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlGiamGia.setBackground(Color.WHITE);
        pnlGiamGia.add(new JLabel("Giảm giá: "));
        lblGiamGia = new JLabel("0 VND");
        lblGiamGia.setFont(new Font("Arial", Font.BOLD, 12));
        lblGiamGia.setForeground(Color.RED);
        pnlGiamGia.add(lblGiamGia);
        panel.add(pnlGiamGia);

        // Tổng tiền
        JPanel pnlTongTien = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTongTien.setBackground(Color.WHITE);
        pnlTongTien.add(new JLabel("Tổng tiền: "));
        lblTongTien = new JLabel("0 VND");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(COLOR_CAM_BTN);
        pnlTongTien.add(lblTongTien);
        panel.add(pnlTongTien);

        return panel;
    }

    /**
     * Tạo panel thanh toán.
     */
    private JPanel taoPanelThanhToan() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));

        // Khuyến mãi
        JPanel pnlKM = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlKM.setBackground(Color.WHITE);
        pnlKM.add(new JLabel("Khuyến mãi:"));
        cbKhuyenMai = new JComboBox<>();
        cbKhuyenMai.setPreferredSize(new Dimension(200, 25));
        cbKhuyenMai.addActionListener(e -> apDungKhuyenMai());
        pnlKM.add(cbKhuyenMai);
        panel.add(pnlKM);

        // Hình thức thanh toán
        JPanel pnlHTTT = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlHTTT.setBackground(Color.WHITE);
        pnlHTTT.add(new JLabel("Hình thức:"));
        cbHinhThucTT = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});
        cbHinhThucTT.setPreferredSize(new Dimension(150, 25));
        cbHinhThucTT.addActionListener(e -> xuLyThayDoiHinhThucTT());
        pnlHTTT.add(cbHinhThucTT);
        panel.add(pnlHTTT);

        // Tiền khách đưa
        JPanel pnlTienDua = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTienDua.setBackground(Color.WHITE);
        pnlTienDua.add(new JLabel("Tiền khách đưa:"));
        txtTienKhachDua = new JTextField(15);
        txtTienKhachDua.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                tinhTienThoiLai();
            }
        });
        pnlTienDua.add(txtTienKhachDua);
        panel.add(pnlTienDua);

        // Tiền thối lại
        JPanel pnlTienThoi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTienThoi.setBackground(Color.WHITE);
        pnlTienThoi.add(new JLabel("Tiền thối lại:"));
        txtTienThoiLai = new JTextField(15);
        txtTienThoiLai.setEditable(false);
        pnlTienThoi.add(txtTienThoiLai);
        panel.add(pnlTienThoi);

        return panel;
    }

    // ================================================================================
    // TẢI VÀ CẬP NHẬT DỮ LIỆU
    // ================================================================================

    /**
     * Tải danh sách khuyến mãi.
     */
    private void taiKhuyenMai() {
        try {
            List<KhuyenMai> dsKM = banVeService.getDanhSachKhuyenMaiConHieuLuc();
            
            cbKhuyenMai.removeAllItems();
            
            // Thêm option "Không áp dụng"
            cbKhuyenMai.addItem(null);
            
            // Thêm các khuyến mãi
            for (KhuyenMai km : dsKM) {
                cbKhuyenMai.addItem(km);
            }

            // Tự động chọn KM tốt nhất
            if (!dsKM.isEmpty()) {
                KhuyenMai kmTotNhat = timKhuyenMaiTotNhat(dsKM);
                if (kmTotNhat != null) {
                    cbKhuyenMai.setSelectedItem(kmTotNhat);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải khuyến mãi: " + e.getMessage());
        }
    }

    /**
     * Tìm khuyến mãi tốt nhất dựa trên giá trị giảm.
     */
    private KhuyenMai timKhuyenMaiTotNhat(List<KhuyenMai> dsKM) {
        if (dsKM.isEmpty()) return null;

        BigDecimal tongTien = tinhTongTienGoc();
        KhuyenMai best = null;
        BigDecimal maxGiam = BigDecimal.ZERO;

        for (KhuyenMai km : dsKM) {
            BigDecimal giam = tinhGiaTriGiam(km, tongTien);
            if (giam.compareTo(maxGiam) > 0) {
                maxGiam = giam;
                best = km;
            }
        }

        return best;
    }

    /**
     * Tính giá trị giảm của khuyến mãi.
     */
    private BigDecimal tinhGiaTriGiam(KhuyenMai km, BigDecimal giaGoc) {
        if (km == null || km.getGiaTriGiam() == null) {
            return BigDecimal.ZERO;
        }

        if ("PHAN_TRAM_GIA".equals(km.getLoaiKM())) {
            double phanTram = km.getGiaTriGiam().doubleValue() / 100.0;
            return giaGoc.multiply(BigDecimal.valueOf(phanTram));
        } else if ("CO_DINH".equals(km.getLoaiKM())) {
            return km.getGiaTriGiam();
        }

        return BigDecimal.ZERO;
    }

    /**
     * Áp dụng khuyến mãi.
     */
    private void apDungKhuyenMai() {
        KhuyenMai selected = (KhuyenMai) cbKhuyenMai.getSelectedItem();
        this.khuyenMaiApDung = selected;
        capNhatGiaoDien();
    }

    /**
     * Xử lý thay đổi hình thức thanh toán.
     */
    private void xuLyThayDoiHinhThucTT() {
        String hinhThuc = (String) cbHinhThucTT.getSelectedItem();
        
        if ("Tiền mặt".equals(hinhThuc)) {
            txtTienKhachDua.setEditable(true);
            // Đề xuất số tiền làm tròn
            long tongTien = tinhTongTienSauGiam().longValue();
            long lamTron = (long) Math.ceil(tongTien / 50000.0) * 50000;
            txtTienKhachDua.setText(currencyFormat.format(lamTron));
        } else {
            // Chuyển khoản - không cần tiền thối lại
            txtTienKhachDua.setEditable(false);
            txtTienKhachDua.setText(currencyFormat.format(tinhTongTienSauGiam()));
            txtTienThoiLai.setText("0 VND");
        }
        
        tinhTienThoiLai();
    }

    /**
     * Tính tiền thối lại.
     */
    private void tinhTienThoiLai() {
        try {
            String raw = txtTienKhachDua.getText().replaceAll("[^0-9]", "");
            if (raw.isEmpty()) {
                txtTienThoiLai.setText("0 VND");
                return;
            }

            long tienDua = Long.parseLong(raw);
            long tongTien = tinhTongTienSauGiam().longValue();
            long thoiLai = Math.max(0, tienDua - tongTien);

            txtTienThoiLai.setText(currencyFormat.format(thoiLai));
        } catch (Exception e) {
            txtTienThoiLai.setText("0 VND");
        }
    }

    // ================================================================================
    // CẬP NHẬT GIAO DIỆN
    // ================================================================================

    /**
     * Cập nhật toàn bộ giao diện.
     */
    private void capNhatGiaoDien() {
        // Cập nhật bảng danh sách vé
        capNhatBangDanhSachVe();

        // Cập nhật tổng tiền
        BigDecimal giaVe = tinhTongTienGoc();
        BigDecimal giamGia = BigDecimal.ZERO;
        if (khuyenMaiApDung != null) {
            giamGia = tinhGiaTriGiam(khuyenMaiApDung, giaVe);
        }
        BigDecimal tongTien = giaVe.subtract(giamGia);

        lblGiaVe.setText(currencyFormat.format(giaVe) + " VND");
        lblGiamGia.setText(currencyFormat.format(giamGia) + " VND");
        lblTongTien.setText(currencyFormat.format(tongTien) + " VND");

        // Cập nhật số tiền bằng chữ
        lblTienBangChu.setText("Số tiền bằng chữ: " + docSo(tongTien.longValue()));

        // Cập nhật text tiền khách đưa
        if ("Tiền mặt".equals(cbHinhThucTT.getSelectedItem())) {
            long lamTron = (long) Math.ceil(tongTien.longValue() / 50000.0) * 50000;
            txtTienKhachDua.setText(currencyFormat.format(lamTron));
        } else {
            txtTienKhachDua.setText(currencyFormat.format(tongTien));
        }

        tinhTienThoiLai();
    }

    /**
     * Cập nhật bảng danh sách vé.
     */
    private void capNhatBangDanhSachVe() {
        tableModel.setRowCount(0);

        if (danhSachVe == null || danhSachVe.isEmpty()) {
            return;
        }

        int stt = 1;
        for (TicketDTO ticket : danhSachVe) {
            String hoTen = "";
            String loaiVe = ticket.maLoaiVe();
            
            if (ticket.khachHang() != null) {
                hoTen = ticket.khachHang().hoTen() != null ? ticket.khachHang().hoTen() : "";
            }
            
            String giaVe = ticket.giaVe() != null ? currencyFormat.format(ticket.giaVe()) : "0";

            tableModel.addRow(new Object[]{stt++, ticket.maChoDat(), hoTen, loaiVe, giaVe});
        }
    }

    /**
     * Tính tổng tiền gốc (chưa trừ khuyến mãi).
     */
    private BigDecimal tinhTongTienGoc() {
        BigDecimal tong = BigDecimal.ZERO;
        if (danhSachVe != null) {
            for (TicketDTO ticket : danhSachVe) {
                if (ticket.giaVe() != null) {
                    tong = tong.add(ticket.giaVe());
                }
            }
        }
        return tong;
    }

    /**
     * Tính tổng tiền sau khi áp dụng khuyến mãi.
     */
    private BigDecimal tinhTongTienSauGiam() {
        BigDecimal giaVe = tinhTongTienGoc();
        BigDecimal giamGia = BigDecimal.ZERO;
        
        if (khuyenMaiApDung != null) {
            giamGia = tinhGiaTriGiam(khuyenMaiApDung, giaVe);
        }
        
        return giaVe.subtract(giamGia);
    }

    // ================================================================================
    // ĐỌC SỐ THÀNH CHỮ
    // ================================================================================

    private static final String[] CHU_SO = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
    private static final String[] DON_VI = {"", "nghìn", "triệu", "tỷ"};

    private String docBaSo(int so) {
        if (so == 0) return CHU_SO[0];
        
        int tram = so / 100;
        int chuc = (so % 100) / 10;
        int donVi = so % 10;
        
        String ketQua = "";
        
        if (tram > 0) ketQua += CHU_SO[tram] + " trăm";
        
        if (chuc > 1) {
            ketQua += (tram > 0 ? " " : "") + CHU_SO[chuc] + " mươi";
            if (donVi > 0) {
                if (donVi == 5) ketQua += " lăm";
                else if (donVi == 1) ketQua += " mốt";
                else ketQua += " " + CHU_SO[donVi];
            }
        } else if (chuc == 1) {
            ketQua += (tram > 0 ? " " : "") + "mười";
            if (donVi > 0) {
                if (donVi == 5) ketQua += " lăm";
                else ketQua += " " + CHU_SO[donVi];
            }
        } else if (tram > 0 && donVi > 0) {
            ketQua += " lẻ " + CHU_SO[donVi];
        } else if (donVi > 0) {
            ketQua += (tram > 0 ? " " : "") + CHU_SO[donVi];
        }
        
        return ketQua.trim();
    }

    public String docSo(long number) {
        if (number == 0) return "Không đồng";
        if (number < 0) return "Âm " + docSo(-number);

        String s = String.valueOf(number);
        int length = s.length();
        int soNhom = (length + 2) / 3;

        List<String> nhomSo = new ArrayList<>();
        for (int i = 0; i < soNhom; i++) {
            int start = Math.max(0, length - (i + 1) * 3);
            int end = length - i * 3;
            nhomSo.add(s.substring(start, end));
        }

        String ketQua = "";
        for (int i = soNhom - 1; i >= 0; i--) {
            int val = Integer.parseInt(nhomSo.get(i));
            String chu = docBaSo(val);
            if (!chu.isEmpty() && i < DON_VI.length) {
                ketQua = chu + " " + DON_VI[i] + (ketQua.isEmpty() ? "" : " " + ketQua);
            }
        }

        return (ketQua.substring(0, 1).toUpperCase() + ketQua.substring(1) + " đồng")
                .replaceAll("\\s+", " ");
    }

    // ================================================================================
    // XÁC NHẬN THANH TOÁN
    // ================================================================================

    /**
     * Xác nhận thanh toán.
     */
    private void xacNhanThanhToan() {
        // Kiểm tra tiền khách đưa
        try {
            String raw = txtTienKhachDua.getText().replaceAll("[^0-9]", "");
            long tienDua = Long.parseLong(raw);
            long tongTien = tinhTongTienSauGiam().longValue();

            if ("Tiền mặt".equals(cbHinhThucTT.getSelectedItem()) && tienDua < tongTien) {
                JOptionPane.showMessageDialog(this, 
                        "Tiền khách đưa không đủ để thanh toán!",
                        "Lỗi thanh toán",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Số tiền không hợp lệ!",
                    "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xác nhận từ người dùng
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận thanh toán?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Thực hiện giao dịch
        thucHienGiaoDich();
    }

    /**
     * Thực hiện giao dịch bán vé.
     */
    private void thucHienGiaoDich() {
        btnXacNhan.setEnabled(false);
        btnQuayLai.setEnabled(false);
        btnHuy.setEnabled(false);

        // Hiển thị loading
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<BanVeResultDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected BanVeResultDTO doInBackground() {
                String maKM = khuyenMaiApDung != null ? khuyenMaiApDung.getMaKM() : null;
                String hinhThucTT = (String) cbHinhThucTT.getSelectedItem();

                return banVeService.thucHienBanVe(
                        danhSachVe,
                        khachHangDaiDien,
                        chuyenTau != null ? chuyenTau.maChuyenTau() : "",
                        maNV,
                        maKM,
                        hinhThucTT
                );
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());

                try {
                    BanVeResultDTO ketQua = get();

                    if (ketQua.thanhCong()) {
                        // Thành công
                        hienThiKetQuaThanhCong(ketQua);
                    } else {
                        // Thất bại
                        JOptionPane.showMessageDialog(ManHinhXacNhanBanVeJPA.this,
                                "Giao dịch thất bại: " + ketQua.loiLoi(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        
                        btnXacNhan.setEnabled(true);
                        btnQuayLai.setEnabled(true);
                        btnHuy.setEnabled(true);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi thực hiện giao dịch: " + e.getMessage());
                    e.printStackTrace();
                    
                    JOptionPane.showMessageDialog(ManHinhXacNhanBanVeJPA.this,
                            "Lỗi hệ thống: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    
                    btnXacNhan.setEnabled(true);
                    btnQuayLai.setEnabled(true);
                    btnHuy.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    /**
     * Hiển thị kết quả thanh toán thành công.
     */
    private void hienThiKetQuaThanhCong(BanVeResultDTO ketQua) {
        // Cập nhật mã hóa đơn
        lblMaHoaDon.setText("Mã HD: " + ketQua.maHoaDon());
        lblMaHoaDon.setForeground(COLOR_XANH_BTN);

        // Vô hiệu hóa các nút
        btnXacNhan.setEnabled(false);
        btnQuayLai.setEnabled(false);
        btnHuy.setEnabled(false);
        btnKetThuc.setEnabled(true);

        // Hiển thị thông báo
        StringBuilder sb = new StringBuilder();
        sb.append("Thanh toán thành công!\n");
        sb.append("Mã hóa đơn: ").append(ketQua.maHoaDon()).append("\n");
        sb.append("Tổng tiền: ").append(currencyFormat.format(ketQua.tongTien())).append(" VND\n");
        sb.append("\nDanh sách vé:\n");
        
        if (ketQua.danhSachVe() != null) {
            for (TicketResultDTO ve : ketQua.danhSachVe()) {
                sb.append("- ").append(ve.maVe())
                  .append(" (Ghế: ").append(ve.maChoDat()).append(")\n");
            }
        }

        JOptionPane.showMessageDialog(this,
                sb.toString(),
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ================================================================================
    // ĐIỀU HƯỚNG
    // ================================================================================

    /**
     * Thiết lập callback khi nhấn nút Quay lại.
     */
    public void setOnQuayLai(Runnable callback) {
        this.onQuayLaiCallback = callback;
    }

    /**
     * Thiết lập callback khi nhấn nút Kết thúc.
     */
    public void setOnKetThuc(Runnable callback) {
        this.onKetThucCallback = callback;
    }

    /**
     * Quay lại màn hình bán vé.
     */
    private void quayLai() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Quay lại màn hình bán vé?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (onQuayLaiCallback != null) {
                onQuayLaiCallback.run();
            }
        }
    }

    /**
     * Hủy bỏ.
     */
    private void huyBo() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hủy bỏ giao dịch và quay về trang chủ?",
                "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (onKetThucCallback != null) {
                onKetThucCallback.run();
            }
        }
    }

    /**
     * Kết thúc - quay về trang chủ.
     */
    private void ketThuc() {
        if (onKetThucCallback != null) {
            onKetThucCallback.run();
        }
    }

    // ================================================================================
    // SETTERS
    // ================================================================================

    /**
     * Thiết lập dữ liệu bán vé.
     */
    public void setData(
            List<TicketDTO> danhSachVe,
            PassengerDTO khachHangDaiDien,
            ChuyenTauDTO chuyenTau,
            Map<String, PassengerDTO> thongTinKhachHangMap) {
        this.danhSachVe = danhSachVe != null ? new ArrayList<>(danhSachVe) : new ArrayList<>();
        this.khachHangDaiDien = khachHangDaiDien;
        this.chuyenTau = chuyenTau;
        this.thongTinKhachHangMap = thongTinKhachHangMap != null ? new HashMap<>(thongTinKhachHangMap) : new HashMap<>();

        taiKhuyenMai();
        capNhatGiaoDien();
    }

    /**
     * Reset form về trạng thái ban đầu.
     */
    public void reset() {
        danhSachVe.clear();
        khachHangDaiDien = null;
        chuyenTau = null;
        thongTinKhachHangMap.clear();
        khuyenMaiApDung = null;

        lblMaHoaDon.setText("Mã HD: Đang chờ...");
        lblMaHoaDon.setForeground(Color.BLACK);

        btnXacNhan.setEnabled(true);
        btnQuayLai.setEnabled(true);
        btnHuy.setEnabled(true);
        btnKetThuc.setEnabled(false);

        taiKhuyenMai();
        capNhatGiaoDien();
    }
}
