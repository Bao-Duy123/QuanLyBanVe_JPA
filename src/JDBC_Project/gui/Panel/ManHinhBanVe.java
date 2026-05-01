package JDBC_Project.gui.Panel;

// ====================================================================================
// MODULE: 0. IMPORTS VÀ KHAI BÁO GÓI
// ====================================================================================

import com.toedter.calendar.JDateChooser;
import dao.*;
import entity.*;
import gui.MainFrame.AdminFullDashboard;
import gui.MainFrame.BanVeDashboard;
import control.VeSoDoTau;
import org.apache.poi.ss.usermodel.*;
import service.NghiepVuTinhGiaVe;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class ManHinhBanVe extends JPanel implements MouseListener, ActionListener {

    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final String MA_VE_NL = "VT01";


    private  Map<String, LoaiVe> mapAllLoaiVe = new HashMap<>();
    private final Map<String, String> mapReverseLoaiVe;


    // ====================================================================================
    // MODULE: 1. KHAI BÁO BIẾN TRẠNG THÁI VÀ DAO (STATE & DATA ACCESS)
    // ====================================================================================

    // --- 1.1. BIẾN UI / COMPONENT ---
    private JComboBox<Ga> cbGaDi;
    private JComboBox<Ga> cbGaDen;
    private JDateChooser dateChooserNgayDi;
    private DefaultTableModel tableModel;
    private JTextField txtTongSoKhach;
    private JButton btnTimChuyen;
    private JButton btnHuy, btnTiepTheo;
    private JLabel lblTotalPrice;
    private JPanel pnlToa;
    private JPanel pnlSoDoGhe;
    private JLabel lblGheDaChonTong;
    private JScrollPane thongTinKhachScrollPane;

    private JButton btnVeDoan;

    // --- 1.2. BIẾN TRẠNG THÁI DỮ LIỆU ---
    private List<ChuyenTau> ketQua = new ArrayList<>();
    private String maChuyenTauHienTai = null;
    private String maToaHienTai = null;
    private JButton lastSelectedToaButton = null;

    // === THÊM BIẾN MỚI CHO DANH SÁCH CHUYẾN TÀU ===
    private JPanel pnlChuyenTau;
    private JScrollPane scrChuyenTau;

    private Map<String, ChiTietKhach> danhSachKhachHang = new LinkedHashMap<>();
    private Map<String, ChoDat> danhSachGheDaChon = new LinkedHashMap<>();
    private Map<String, ChoDat> tatCaChoDatToaHienTai = new HashMap<>();

    // Map lưu giá mỗi ghế
    private Map<String, Long> danhSachGiaVe = new HashMap<>();

    // Map nút ghế:
    private Map<String, JButton> seatButtonsMap = new HashMap<>();
    // Map lưu trữ Label Giá cho mỗi MaCho (để cập nhật giá khi đổi loại vé/tuổi)
    private Map<String, JLabel> giaLabelMap = new HashMap<>();
    // Map lưu trữ các JTextField để dễ dàng truy cập và kiểm tra
    private Map<String, JTextField> inputFieldsMap = new HashMap<>();
    // Map lưu trữ các JLabel lỗi tương ứng
    private Map<String, JLabel> errorLabelsMap = new HashMap<>();

    // Biến điều khiển để dễ dàng bật/tắt Verifiers
    private boolean enableValidation = true;

    // Biến lưu panel chuyến tàu đã chọn trước đó (để đổi màu khi chọn)
    private JPanel lastSelectedChuyenTauPanel = null;

    // --- 1.3. DAO (Data Access Objects) ---
    private final ChoDatDAO choDatDao = new ChoDatDAO();
    private final LoaiToaDAO loaiChoDatDAO = new LoaiToaDAO();
    private final LoaiVeDAO loaiVeDAO = new LoaiVeDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private JPanel pnlDanhSachKhachHang;




    // ====================================================================================
    // MODULE: 2. RECORD CHI TIẾT KHÁCH (DỮ LIỆU TẠM THỜI)
    // ====================================================================================

    private static record ChiTietKhach(
            ChoDat choDat,
            String maLoaiVe,
            String hoTen,
            String cccd,
            String sdt,
            String ngaySinh
    ) {
        // PHƯƠNG THỨC HỖ TRỢ BẤT BIẾN (WITH)
        public ChiTietKhach withMaLoaiVe(String newMaLoaiVe) {
            return new ChiTietKhach(this.choDat, newMaLoaiVe, this.hoTen, this.cccd, this.sdt, this.ngaySinh);
        }

        public ChiTietKhach withHoTen(String newHoTen) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, newHoTen, this.cccd, this.sdt, this.ngaySinh);
        }

        public ChiTietKhach withCccd(String newCccd) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, this.hoTen, newCccd, this.sdt, this.ngaySinh);
        }

        public ChiTietKhach withSdt(String newSdt) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, this.hoTen, this.cccd, newSdt, this.ngaySinh);
        }

        public ChiTietKhach withNgaySinh(String newNgaySinh) {
            return new ChiTietKhach(this.choDat, this.maLoaiVe, this.hoTen, this.cccd, this.sdt, newNgaySinh);
        }

    }


    // ====================================================================================
    // MODULE: 3. CONSTRUCTOR VÀ LAYOUT CHÍNH (MAIN LAYOUT)
    // ====================================================================================

    public ManHinhBanVe() {
        setLayout(new BorderLayout());
        setBackground(Color.white);

        JLabel tieuDe = new JLabel("Bán vé");
        tieuDe.setFont(tieuDe.getFont().deriveFont(Font.BOLD, 25f));
        tieuDe.setBorder(new EmptyBorder(6, 15, 12, 6));
        add(tieuDe, BorderLayout.NORTH);

        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        chia.setResizeWeight(0.5);
        chia.setDividerSize(5);
        chia.setBorder(null);
        chia.setBackground(getBackground());


        // KHỞI TẠO MAP LOẠI VÉ VÀ MAP NGƯỢC
        List<LoaiVe> allLoaiVe = loaiVeDAO.getAllLoaiVe();
        mapAllLoaiVe = allLoaiVe.stream()
                .collect(Collectors.toMap(LoaiVe::getMaLoaiVe, lv -> lv));

        // Khởi tạo Map Ngược (Sử dụng hàm format mới)
        mapReverseLoaiVe = allLoaiVe.stream()
                .collect(Collectors.toMap(this::formatLoaiVeHienThi, LoaiVe::getMaLoaiVe));


        chia.setLeftComponent(taoPanelTrai());
        chia.setRightComponent(taoPanelPhai());

        SwingUtilities.invokeLater(() -> {
            chia.setDividerLocation(0.65);
        });

        add(chia, BorderLayout.CENTER);
    }
    private JPanel taoPanelTrai() {
        JPanel panelTrai = new JPanel();
        panelTrai.setLayout(new BoxLayout(panelTrai, BoxLayout.Y_AXIS));
        panelTrai.setBackground(Color.white);

        panelTrai.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 3, false),
                new EmptyBorder(5, 5, 0, 0)));
        // emptyborder theo thứ tự trên, trái, dưới, phải

        panelTrai.add(createKhuVucTimKiem());
        panelTrai.add(Box.createVerticalStrut(10));
        panelTrai.add(createKhuVucDanhSachChuyenTau());
        panelTrai.add(Box.createVerticalStrut(10));
        panelTrai.add(createKhuVucChonToa());
        panelTrai.add(Box.createVerticalStrut(10));
        panelTrai.add(createKhuVucChonViTriGhe());
        panelTrai.add(Box.createVerticalStrut(10));

        panelTrai.add(Box.createVerticalGlue());
        return panelTrai;
    }

    private JPanel taoPanelPhai(){
        JPanel panelPhai = new JPanel(new BorderLayout());
        panelPhai.setBackground(Color.white);
        panelPhai.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false),
                new EmptyBorder(0, 0, 0, 0)));

        JPanel khuVucThongTinKhach = createKhuVucThongTinKhach();
        panelPhai.add(khuVucThongTinKhach, BorderLayout.CENTER);



        return panelPhai;
    }

    // ====================================================================================
    // MODULE: 4. UI BUILDERS (TẠO CÁC KHU VỰC GIAO DIỆN CON)
    // ====================================================================================

    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
//        panel.setBackground(Color.LIGHT_GRAY);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Tìm kiếm chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        Vector<Ga> danhSachGa = new GaDao().layDanhSachGa();

        panel.add(new JLabel("Ga đi"));
        cbGaDi = new JComboBox<>(danhSachGa);
        panel.add(cbGaDi);

        panel.add(new JLabel("Ga đến"));
        cbGaDen = new JComboBox<>(danhSachGa);
        panel.add(cbGaDen);

        //test
        cbGaDi.setSelectedIndex(1);
        if (danhSachGa.size() > 1) {
            cbGaDen.setSelectedIndex(2);
        }

        JLabel lblNgayDi = new JLabel("Ngày đi");
        lblNgayDi.setPreferredSize(new Dimension(50, 25));
        panel.add(lblNgayDi);
        dateChooserNgayDi = new JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setDate(new Date());

        dateChooserNgayDi.setMinSelectableDate(new Date());

        dateChooserNgayDi.setPreferredSize(new Dimension(100, 25));
        panel.add(dateChooserNgayDi);

        btnTimChuyen = new JButton("Tìm chuyến");
        styleNutChinh(btnTimChuyen);
        btnTimChuyen.addActionListener(this);
        panel.add(btnTimChuyen);

        return panel;
    }

    private JScrollPane createKhuVucDanhSachChuyenTau() {
        // Tên chuyến tàu và thông tin sẽ được hiển thị trên các Panel tùy chỉnh
        pnlChuyenTau = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        //border cho pnlChuyenTau
        pnlChuyenTau.setOpaque(false);

        // Bọc pnlChuyenTau vào JScrollPane
        scrChuyenTau = new JScrollPane(pnlChuyenTau);
        scrChuyenTau.setBorder(null);

        // Thiết lập chỉ cuộn ngang, thanh cuộn dọc bị tắt (hoặc AS_NEEDED nếu muốn)
        scrChuyenTau.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrChuyenTau.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Giới hạn chiều cao của JScrollPane để nó không chiếm quá nhiều không gian
        //preferredSize là kích thước ưu tiên, maximumSize là kích thước tối đa
        scrChuyenTau.setPreferredSize(new Dimension(600, 190));
        scrChuyenTau.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Khởi tạo trạng thái ban đầu
        pnlChuyenTau.add(new JLabel("Vui lòng chọn Ga đi, Ga đến và Ngày đi để tìm kiếm."));

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Danh sách chuyến tàu");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        scrChuyenTau.setBorder(title);

        return scrChuyenTau;
    }

    private JPanel createKhuVucChonToa() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Chọn toa và số lượng khách");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.setBackground(Color.white);

        // --- KHU VỰC NHẬP TỔNG SỐ KHÁCH (DÙNG ToaPanelTangGiam) ---
        txtTongSoKhach = new JTextField(2);
        txtTongSoKhach.setPreferredSize(new Dimension(60, 25));
        txtTongSoKhach.setMaximumSize(new Dimension(60, 25));
        JPanel pnlTongSoKhachControl = ToaPanelTangGiam("Tổng số khách", "1", txtTongSoKhach);
        pnlTongSoKhachControl.setMaximumSize(new Dimension(300, 25));

        topRow.add(pnlTongSoKhachControl);

        // --- THÊM NÚT VÉ ĐOÀN ---
        btnVeDoan = new JButton("Vé Đoàn");
        styleNutChinh(btnVeDoan);
        btnVeDoan.setBackground(new Color(255, 165, 0)); // Màu cam nổi bật
        btnVeDoan.addActionListener(this);
        topRow.add(btnVeDoan);
        panel.add(topRow);

        // --- KHU VỰC CHỌN TOA ---
        pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlToa.setBackground(Color.red);
        pnlToa.setOpaque(false);

        // Bọc pnlToa (nơi các nút được thêm vào) vào JScrollPane
        JScrollPane scrToa = new JScrollPane(pnlToa);
        scrToa.setBorder(null);
        scrToa.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrToa.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Giới hạn chiều cao của JScrollPane để nó không chiếm quá nhiều không gian
        scrToa.setPreferredSize(new Dimension(500, 80));
        scrToa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Thêm Label "Chọn toa:" ra ngoài ScrollPane để Label luôn hiển thị
        JPanel rowToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rowToa.add(new JLabel("Chọn toa:"));
        rowToa.add(scrToa);
        rowToa.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(rowToa);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.setPreferredSize(new Dimension(600, 170));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));


        return panel;
    }

    private JPanel createKhuVucChonViTriGhe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.setBackground(Color.red);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Chọn vị trí của ghế");
        title.setTitleFont(title.getTitleFont().deriveFont(Font.BOLD, 14f));
        panel.setBorder(title);

        pnlSoDoGhe = new JPanel();
        pnlSoDoGhe.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane soDoScrollPane = new JScrollPane(pnlSoDoGhe);
        soDoScrollPane.setBorder(BorderFactory.createEmptyBorder());
        soDoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        soDoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        soDoScrollPane.setPreferredSize(new Dimension(100, 180));
        soDoScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        panel.add(soDoScrollPane);
        panel.add(Box.createVerticalStrut(10));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(taoMucChuGiai(Color.LIGHT_GRAY, "Chỗ trống"));
        legendPanel.add(taoMucChuGiai(Color.BLACK, "Đã đặt"));
        legendPanel.add(taoMucChuGiai(new Color(0, 123, 255), "Đang chọn"));
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(legendPanel);
        panel.add(Box.createVerticalStrut(5));

        // --- KHU VỰC ĐÃ THAY ĐỔI: Chỉ hiện tóm tắt số lượng ---
        JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlSummary.setOpaque(false);

        lblGheDaChonTong = new JLabel("Ghế đã chọn: 0/1"); // Khởi tạo với giá trị mặc định
        lblGheDaChonTong.setFont(lblGheDaChonTong.getFont().deriveFont(Font.BOLD, 14f));
        lblGheDaChonTong.setForeground(COLOR_BLUE_LIGHT);

        pnlSummary.add(lblGheDaChonTong);
        pnlSummary.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(pnlSummary);
        panel.add(Box.createVerticalStrut(5));

        capNhatDanhSachGheDaChonUI(); // Lệnh này vẫn giữ lại để cập nhật summary khi khởi tạo

        return panel;
    }

    private JPanel createKhuVucThongTinKhach() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Thông tin khách hàng"));

        // --- FIX: Thiết lập kích thước ưu tiên cho panel cha ---
        // Chiều rộng cố định khoảng 400-500px là đủ cho thông tin khách
        panel.setPreferredSize(new Dimension(450, 0));
        // Chiều cao để 0 hoặc bất kỳ vì BorderLayout.CENTER của cha sẽ lo chiều cao

        // --- KHỞI TẠO PANEL CHỨA DANH SÁCH KHÁCH HÀNG ---
        pnlDanhSachKhachHang = new JPanel();
        pnlDanhSachKhachHang.setLayout(new BoxLayout(pnlDanhSachKhachHang, BoxLayout.Y_AXIS));
        pnlDanhSachKhachHang.setOpaque(false);
        pnlDanhSachKhachHang.setBorder(new EmptyBorder(5, 5, 5, 5));

        pnlDanhSachKhachHang.add(new JLabel("Chọn ghế để thêm thông tin."));

        thongTinKhachScrollPane = new JScrollPane(pnlDanhSachKhachHang);
        thongTinKhachScrollPane.setBorder(null);
        thongTinKhachScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Tắt thanh cuộn ngang để tránh giao diện xấu, nội dung sẽ tự wrap hoặc bị cắt nếu quá dài
        thongTinKhachScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // --- THAY ĐỔI: Add ScrollPane vào CENTER ---
        panel.add(thongTinKhachScrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::capNhatThongTinKhachUI);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        btnHuy = new JButton("< Hủy");
        btnHuy.setPreferredSize(new Dimension(80, 40));
        btnHuy.setFont(btnHuy.getFont().deriveFont(Font.BOLD, 14f));
        btnHuy.setBackground(new Color(220, 53, 69));
        btnHuy.setForeground(Color.WHITE);

        btnTiepTheo = new JButton("Tiếp theo >");
        btnTiepTheo.setPreferredSize(new Dimension(120, 40));
        btnTiepTheo.setFont(btnTiepTheo.getFont().deriveFont(Font.BOLD, 14f));
        btnTiepTheo.setBackground(new Color(0, 123, 255));
        btnTiepTheo.setForeground(Color.WHITE);

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnTiepTheo);
        //Đăng ký sự kiện
        btnHuy.addActionListener(this);
        btnTiepTheo.addActionListener(this);

        JPanel fullSummary = new JPanel(new BorderLayout());
        fullSummary.setBackground(Color.white);
        fullSummary.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(new JLabel("Đã chọn: X/Y")); // Label này cần được cập nhật thực tế

        lblTotalPrice = new JLabel("Tổng tiền vé: 0 VNĐ");
        lblTotalPrice.setFont(lblTotalPrice.getFont().deriveFont(Font.BOLD, 14f));
        lblTotalPrice.setForeground(new Color(255, 165, 0));
        summaryPanel.add(lblTotalPrice);

        fullSummary.add(summaryPanel, BorderLayout.EAST);

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);

        footerPanel.add(fullSummary);
        footerPanel.add(buttonPanel);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }


    // ====================================================================================
    // MODULE: 5. LOGIC XỬ LÝ DỮ LIỆU CHÍNH (DATA PROCESSING LOGIC)
    // ====================================================================================

    // --- 5.1. LOGIC TÌM KIẾM CHUYẾN TÀU VÀ NẠP DỮ LIỆU ---

    private void timKiemChuyenTau() {
        Ga gaDiSelected = (Ga) cbGaDi.getSelectedItem();
        Ga gaDenSelected = (Ga) cbGaDen.getSelectedItem();

        System.out.println(gaDiSelected.getMaGa());
        System.out.println(gaDenSelected.getMaGa());

        String maGaDi =  gaDiSelected.getMaGa();
        String maGaDen = gaDenSelected.getMaGa();

        // 2. [QUAN TRỌNG] Kiểm tra trùng Ga Đi và Ga Đến
        if (maGaDi.equals(maGaDen)) {
            JOptionPane.showMessageDialog(this, "Ga đi và Ga đến không được trùng nhau. Vui lòng chọn lại!", "Lỗi chọn ga", JOptionPane.WARNING_MESSAGE);
            return; // Dừng hàm tại đây, không thực hiện tìm kiếm
        }
        // 3. Lấy ngày đi và chuyển định dạng sang SQL
        Date date = dateChooserNgayDi.getDate();
        String ngayDiSQL = SQL_DATE_FORMAT.format(date);

        //4. Kiểm tra ngày đi không được trước ngày hiện tại
        // 1. Chuyển đổi Date sang LocalDate (Chỉ lấy ngày, bỏ giờ)
        LocalDate ngayChon = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate homNay = LocalDate.now();
        // 2. So sánh: Nếu ngày chọn TRƯỚC ngày hôm nay
        if (ngayChon.isBefore(homNay)) {
            JOptionPane.showMessageDialog(this,
                    "Ngày đi không được chọn trong quá khứ. Vui lòng chọn lại!",
                    "Lỗi ngày đi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ChuyenTauDao dao = new ChuyenTauDao();
        System.out.println("Tìm chuyến tàu từ " + maGaDi + " đến " + maGaDen + " vào ngày " + ngayDiSQL);
        ketQua = dao.timChuyenTauTheoGaVaNgayDi(maGaDi, maGaDen, ngayDiSQL);
        System.out.println(ketQua);

        if (ketQua == null || ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy chuyến tàu nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }

        taoDanhSachChuyenTauPanel(ketQua);
        // =============================================
    }

    private void taoDanhSachChuyenTauPanel(List<ChuyenTau> danhSach) {
        pnlChuyenTau.removeAll();
        // Thiết lập lại dữ liệu cũ
        maChuyenTauHienTai = null;
        pnlSoDoGhe.removeAll();
        pnlSoDoGhe.add(new JLabel("Vui lòng chọn chuyến tàu để chọn toa."));
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
        pnlToa.removeAll();
        pnlToa.revalidate();
        pnlToa.repaint();


        if (danhSach == null || danhSach.isEmpty()) {
            pnlChuyenTau.add(new JLabel("Không tìm thấy chuyến tàu nào phù hợp."));
        } else {
            for (int i = 0; i < danhSach.size(); i++) {
                ChuyenTau ct = danhSach.get(i);

                String ngayDiHienThi = ct.getNgayKhoiHanh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                String gioDiHienThi = ct.getGioKhoiHanh().format(TIME_FORMATTER);

                // Lấy ngày đến dự kiến
                String ngayDenHienThi = ct.getNgayDenDuKien().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String gioDenHienThi = ct.getGioDenDuKien().format(TIME_FORMATTER);

                JPanel pnlChuyenTauNut = taoNutChuyenTauVeSoDo(
                        ct.getMaChuyenTau(),
                        ngayDiHienThi,
                        gioDiHienThi,
                        ngayDenHienThi,
                        gioDenHienThi
                );


                final int index = i;
                pnlChuyenTauNut.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        xuLyChonChuyenTauPanel(pnlChuyenTauNut, ct, index);
                    }
                });

                pnlChuyenTau.add(pnlChuyenTauNut);
            }
        }

        pnlChuyenTau.revalidate();
        pnlChuyenTau.repaint();
    }

    private JPanel taoNutChuyenTauVeSoDo(String maChuyen, String ngayDi, String gioDi, String ngayDen, String gioDen) {
        // 1. Tạo đối tượng VeSoDoTau (Cơ sở đồ họa)
        String thoiGianDiHienThi = ngayDi + " " + gioDi;
        String thoiGianDenHienThi = ngayDen + " "+ gioDen;

        //mã chuyến tàu dạng SE1_251218_DNANTR => lấy mã tuyến SE1
        String maTuyen = maChuyen.split("_")[0];

        VeSoDoTau soDoTauPanel = new VeSoDoTau(maTuyen, thoiGianDiHienThi, thoiGianDenHienThi);

        // 2. Tạo Container đóng vai trò là NÚT (Sử dụng JPanel đơn giản)
        JPanel nutChuyenTauContainer = new JPanel(new BorderLayout());
        nutChuyenTauContainer.add(soDoTauPanel, BorderLayout.CENTER);
        // 3. Định dạng Panel để mô phỏng trạng thái nút (Default State)
        nutChuyenTauContainer.setBackground(Color.WHITE);

        // Đặt kích thước để Panel phù hợp
        nutChuyenTauContainer.setPreferredSize(soDoTauPanel.getPreferredSize());

        // --- Lắng nghe sự kiện (Đóng vai trò như ActionListener) ---
        nutChuyenTauContainer.addMouseListener(new MouseAdapter() {
            private Color originalBackground;
            private Border originalBorder;

            @Override
            public void mouseEntered(MouseEvent e) {
                originalBackground = nutChuyenTauContainer.getBackground();
                originalBorder = nutChuyenTauContainer.getBorder();
                nutChuyenTauContainer.setBackground(new Color(220, 220, 220)); // Hiệu ứng hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (nutChuyenTauContainer != lastSelectedChuyenTauPanel) {
                    nutChuyenTauContainer.setBackground(originalBackground);
                    nutChuyenTauContainer.setBorder(originalBorder);
                }
            }
        });

        return nutChuyenTauContainer;
    }

    // --- Hàm xử lý sự kiện chọn chuyến tàu (Tương tự xuLyChonToa) ---
    private void xuLyChonChuyenTauPanel(JPanel currentPanel, ChuyenTau chuyenTau, int index) {

        // 1. Reset trạng thái chọn của Panel cũ
        if (lastSelectedChuyenTauPanel != null) {
            lastSelectedChuyenTauPanel.setBackground(Color.WHITE);
        }

        // 2. Đặt trạng thái chọn cho Panel hiện tại
        // Đổi màu thành màu cam đậm khi chọn
        currentPanel.setBackground(Color.orange);

        lastSelectedChuyenTauPanel = currentPanel; // Lưu Panel đã chọn

        // 3. Xử lý logic nghiệp vụ
        String maChuyenTauMoi = chuyenTau.getMaChuyenTau();

        if (!maChuyenTauMoi.equals(maChuyenTauHienTai)) {
            danhSachGheDaChon.clear();
            danhSachKhachHang.clear();
            danhSachGiaVe.clear();
            lastSelectedToaButton = null;
            maToaHienTai = null;
            seatButtonsMap.clear();
            tatCaChoDatToaHienTai.clear();

            capNhatDanhSachGheDaChonUI();
            capNhatThongTinKhachUI();

            pnlToa.removeAll();
            pnlToa.revalidate();
            pnlToa.repaint();

            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Vui lòng chọn Toa."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
        }

        maChuyenTauHienTai = maChuyenTauMoi;
        hienThiDanhSachToaTau(chuyenTau.getMaTau());
    }


    private void hienThiDanhSachToaTau(String maTau) {
        List<Toa> danhSachToa = new ArrayList<>();
        try {
            ToaDAO toaTauDAO = new ToaDAO();
            danhSachToa = toaTauDAO.layToaTheoMaTau(maTau);
            System.out.println(danhSachToa);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách toa tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        napNutToa(danhSachToa);
    }

    public void napNutToa(List<Toa> danhSachToa) {
        pnlToa.removeAll();

        for (Toa toa : danhSachToa) {
            String soThuTuToa = laySoThuTuToa(toa.getMaToa());
            String text = "Toa " + soThuTuToa + "\n" + toa.getLoaiToa();
            JButton btnToa = taoNutToa(text, null, null);
            btnToa.addActionListener(e -> xuLyChonToa(btnToa, toa.getMaToa()));
            pnlToa.add(btnToa);// Dòng này thêm nút vào panel
        }
        pnlToa.revalidate();
        pnlToa.repaint();
    }


    // --- 5.2. LOGIC TÍNH TOÁN & CẬP NHẬT TRẠNG THÁI ---

    private void capNhatSoLuongYeuCau() {
        // Chỉ lấy giá trị từ trường TỔNG SỐ KHÁCH
        int tongSoKhachMoi = parseTextFieldToInt(txtTongSoKhach);

        // Cảnh báo nếu số ghế đã chọn vượt quá tổng số khách mới
        if (danhSachGheDaChon != null && danhSachGheDaChon.size() > tongSoKhachMoi) {
            JOptionPane.showMessageDialog(null,
                    "Số lượng ghế đã chọn (" + danhSachGheDaChon.size() + ") vượt quá Tổng số khách mới (" + tongSoKhachMoi + "). Vui lòng hủy chọn bớt.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateKhachRecord(String maCho, String newValue, String fieldName) {
        ChiTietKhach original = danhSachKhachHang.get(maCho);
        if (original == null) return;

        ChiTietKhach updated = switch (fieldName) {
            case "hoTen" -> original.withHoTen(newValue);
            case "cccd" -> original.withCccd(newValue);
            case "sdt" -> original.withSdt(newValue);
            case "ngaySinh" -> original.withNgaySinh(newValue);
            default -> original;
        };
        danhSachKhachHang.put(maCho, updated);
    }

    private void xuLyNgaySinhThayDoi(String maCho, String ngaySinhText, JLabel giaLabel) {
        try {
            int age = tinhTuoi(ngaySinhText);

            // 1. Gợi ý loại vé dựa trên tuổi mới
            String suggestedMaLoaiVe = goiYLoaiVeByAge(age);

            ChiTietKhach khach = danhSachKhachHang.get(maCho);
            if (khach == null) return;

            if (!khach.maLoaiVe().equals(suggestedMaLoaiVe)) {
                // 2. Cập nhật loại vé nếu tuổi mới gợi ý loại khác
                ChiTietKhach updatedKhach = khach.withMaLoaiVe(suggestedMaLoaiVe);
                danhSachKhachHang.put(maCho, updatedKhach);

                // *** SỬA LỖI XÓA DỮ LIỆU: Thay vì tạo lại toàn bộ UI, chỉ cập nhật ComboBox ***
                updateLoaiVeComboBoxUI(maCho, suggestedMaLoaiVe);
                // *** KẾT THÚC SỬA LỖI ***

                // Sau đó tính lại giá
                long gia = tinhGiaVeTau(updatedKhach.choDat(), updatedKhach.maLoaiVe());
                danhSachGiaVe.put(maCho, gia);
            } else {
                // 3. Nếu loại vé không đổi, chỉ tính lại giá (để cập nhật giaLabel nếu có)
                long gia = tinhGiaVeTau(khach.choDat(), khach.maLoaiVe());
                danhSachGiaVe.put(maCho, gia);
                giaLabel.setText(formatVnd(gia));
            }
            capNhatTongTienUI();
        } catch (Exception ex) {
            System.err.println("Lỗi tính lại giá vé sau khi nhập ngày sinh: " + ex.getMessage());
        }
    }
    private void xuLyNhapNhanhKhachHang(JTextField cccdField, JTextField hoTenField,
                                        JTextField sdtField, JTextField ngaySinhField, String maCho) {
        String cccd = cccdField.getText().trim();
        if (cccd.isEmpty()) return;

        try {
            KhachHang kh = khachHangDAO.getKhachHangByCccd(cccd);

            if (kh != null) {
                hoTenField.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
                sdtField.setText(kh.getSdt() != null ? kh.getSdt() : "");

                String ngaySinhStr = "";
                if (kh.getNgaySinh() != null) {
                    ngaySinhStr = kh.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                ngaySinhField.setText(ngaySinhStr);

                ChiTietKhach original = danhSachKhachHang.get(maCho);
                if (original != null) {
                    ChiTietKhach updated = original
                            .withHoTen(hoTenField.getText())
                            .withCccd(cccd)
                            .withSdt(sdtField.getText())
                            .withNgaySinh(ngaySinhStr);

                    danhSachKhachHang.put(maCho, updated);

                    JLabel finalGiaLabel = giaLabelMap.get(maCho);

                    if (finalGiaLabel != null) {
                        xuLyNgaySinhThayDoi(maCho, ngaySinhStr, finalGiaLabel);
                    }
                    capNhatThongTinKhachUI();
                }
                System.out.println("Nhập nhanh thành công cho CCCD: " + cccd);

            } else {
                System.out.println("Không tìm thấy khách hàng với CCCD: " + cccd);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(cccdField, "Lỗi khi truy vấn CSDL để tìm khách hàng: " + e.getMessage(), "Lỗi Nhập nhanh", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }



    // --- 5.3. LOGIC XỬ LÝ SƠ ĐỒ GHẾ ---

    private void xuLyChonToa(JButton currentButton, String maToa) {
        maToaHienTai = maToa;

        if (lastSelectedToaButton != null) {
            lastSelectedToaButton.setBackground(Color.LIGHT_GRAY);
            lastSelectedToaButton.setForeground(Color.BLACK);
        }
        currentButton.setBackground(new Color(0, 123, 255));
        currentButton.setForeground(Color.WHITE);
        lastSelectedToaButton = currentButton;

        if (this.maChuyenTauHienTai == null || this.maChuyenTauHienTai.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn chuyến tàu trước.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Chưa chọn chuyến tàu."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
            return;
        }

        System.out.println("Đã chọn Toa: " + maToa + ". Tiến hành tải sơ đồ ghế." );

//        List<ChoDat> danhSachChoDat = choDatDao.getDanhSachChoDatByMaToaVaTrangThai(
//                maToa,
//                maChuyenTauHienTai
//        );
        String maGaDiStr = ((Ga) cbGaDi.getSelectedItem()).getMaGa();
        String maGaDenStr = ((Ga) cbGaDen.getSelectedItem()).getMaGa();

        List<ChoDat> danhSachChoDat = choDatDao.getDanhSachChoDatTheoPhanChanh(
                maToa,
                maChuyenTauHienTai,
                maGaDiStr,
                maGaDenStr
        );

        String loaiToa = "";
        try {
            loaiToa = layLoaiToa(maToa);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tra cứu loại toa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Không xác định được loại toa."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
            return;
        }

        if (danhSachChoDat.isEmpty()) {
            pnlSoDoGhe.removeAll();
            pnlSoDoGhe.add(new JLabel("Toa này chưa có dữ liệu chỗ đặt."));
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
        } else {
            if (loaiToa.toLowerCase().contains("giường")) {
                veSoDoGiuongNam(danhSachChoDat);
            } else {
                veSoDoGheNgoi(danhSachChoDat);
            }
        }
    }

    private void xuLyChonGhe(JButton btnCho, ChoDat cho) {
        String maCho = cho.getMaCho();

        int tongSoKhachYeuCau = parseTextFieldToInt(txtTongSoKhach);

        if (danhSachGheDaChon.containsKey(maCho)) {
            // --- Logic Hủy chọn ghế ---
            danhSachGheDaChon.remove(maCho);
            danhSachGiaVe.remove(maCho);
            danhSachKhachHang.remove(maCho);
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
        } else {
            // --- Logic Chọn ghế ---
            if (danhSachGheDaChon.size() >= tongSoKhachYeuCau) {
                JOptionPane.showMessageDialog(this,
                        "Đã chọn đủ " + tongSoKhachYeuCau + " ghế. Vui lòng thay đổi số lượng khách hoặc hủy chọn ghế cũ trước.",
                        "Giới hạn chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ChiTietKhach chiTietKhach = new ChiTietKhach(
                    cho,
                    MA_VE_NL,
                    "",
                    "",
                    "",
                    ""
            );

            try {
                long gia = tinhGiaVeTau(cho, chiTietKhach.maLoaiVe());
                danhSachGiaVe.put(maCho, gia);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Không thể tính giá cho ghế " + cho.getSoCho() + ": " + ex.getMessage(),
                        "Lỗi tính giá", JOptionPane.ERROR_MESSAGE);
                return;
            }

            danhSachGheDaChon.put(maCho, cho);
            btnCho.setBackground(new Color(0, 123, 255));
            btnCho.setForeground(Color.WHITE);

            danhSachKhachHang.put(maCho, chiTietKhach);
        }

        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
        capNhatTongTienUI();
    }

    // ====================================================================================
    // MODULE: 6. LOGIC VALIDATION
    // ====================================================================================
    private boolean validateAllInputs() {
        if (!enableValidation) return true;

        boolean allValid = true;

        for (String maCho : danhSachKhachHang.keySet()) {
            // Lấy components từ Map
            JTextField hoTenField = inputFieldsMap.get(maCho + "_hoTen");
            JLabel hoTenErrorLabel = errorLabelsMap.get(maCho + "_hoTen");

            JTextField cccdField = inputFieldsMap.get(maCho + "_cccd");
            JLabel cccdErrorLabel = errorLabelsMap.get(maCho + "_cccd");

            JTextField ngaySinhField = inputFieldsMap.get(maCho + "_ngaySinh");
            JLabel ngaySinhErrorLabel = errorLabelsMap.get(maCho + "_ngaySinh");

            JTextField sdtField = inputFieldsMap.get(maCho + "_sdt");
            JLabel sdtErrorLabel = errorLabelsMap.get(maCho + "_sdt");

            boolean v1 = true, v2 = true, v3 = true, v4 = true;

            if (hoTenField != null) {
                v1 = validateField(hoTenField, hoTenErrorLabel, "hoTen");
            }
            if (cccdField != null) {
                v2 = validateField(cccdField, cccdErrorLabel, "cccd");
            }
            if (ngaySinhField != null) {
                v3 = validateField(ngaySinhField, ngaySinhErrorLabel, "ngaySinh");
            }
            if (sdtField != null) {
                v4 = validateField(sdtField, sdtErrorLabel, "sdt");
            }

            if (!(v1 && v2 && v3 && v4)) {
                allValid = false;
            }
        }
        return allValid;
    }

    // ====================================================================================
    // MODULE: 7. CẬP NHẬT GIAO DIỆN KHÁCH HÀNG (UI UPDATERS)
    // ====================================================================================

    private JPanel createKhachPanel(ChiTietKhach khach) {
        // --- KHỞI TẠO PANEL CHÍNH ---
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));

        // Tăng chiều cao lên một chút để chứa đủ 4 dòng (Input + Error + Input + Error)
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        panel.setPreferredSize(new Dimension(300, 140));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setName(khach.choDat().getMaCho());

        // --- 1. HEADER (Giữ nguyên) ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        pnlHeader.setOpaque(true);
        pnlHeader.setBackground(new Color(255, 228, 204));

        String thongTinCho = "Chỗ: " + khach.choDat().getSoCho() + " / Toa: " + laySoThuTuToa(khach.choDat().getMaToa());
        JLabel lblCho = new JLabel(thongTinCho);
        lblCho.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlHeader.add(lblCho);

        JComboBox<String> cbLoaiKhach = new JComboBox<>(getLoaiVeOptions());
        cbLoaiKhach.setSelectedItem(getTenLoaiVeHienThi(khach.maLoaiVe()));
        cbLoaiKhach.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbLoaiKhach.setPreferredSize(new Dimension(140, 22));
        pnlHeader.add(cbLoaiKhach);

        JLabel lblGia = new JLabel("...");
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGia.setForeground(new Color(52, 152, 219));
        try {
            long gia = tinhGiaVeTau(khach.choDat(), khach.maLoaiVe());
            danhSachGiaVe.put(khach.choDat().getMaCho(), gia);
            lblGia.setText(formatVnd(gia));
        } catch (Exception e) { lblGia.setText("0 VNĐ"); }
        giaLabelMap.put(khach.choDat().getMaCho(), lblGia);
        pnlHeader.add(lblGia);

        panel.add(pnlHeader, BorderLayout.NORTH);

        // --- 2. FORM INPUT (CẤU TRÚC 4 DÒNG) ---
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Font chữ
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 12);
        Font fontError = new Font("Segoe UI", Font.ITALIC, 10); // Font nhỏ cho lỗi
        Color colorError = Color.RED;

        // --- Init Components ---
        JTextField txtHoTen = new JTextField(khach.hoTen()); txtHoTen.setFont(fontInput);
        JTextField txtNgaySinh = new JTextField(khach.ngaySinh()); txtNgaySinh.setFont(fontInput);
        JTextField txtSDT = new JTextField(khach.sdt()); txtSDT.setFont(fontInput);
        JTextField txtCCCD = new JTextField(khach.cccd()); txtCCCD.setFont(fontInput);

        // Labels báo lỗi riêng biệt
        JLabel errHoTen = new JLabel(" "); errHoTen.setFont(fontError); errHoTen.setForeground(colorError);
        JLabel errNgaySinh = new JLabel(" "); errNgaySinh.setFont(fontError); errNgaySinh.setForeground(colorError);
        JLabel errSDT = new JLabel(" "); errSDT.setFont(fontError); errSDT.setForeground(colorError);
        JLabel errCCCD = new JLabel(" "); errCCCD.setFont(fontError); errCCCD.setForeground(colorError);

        String maCho = khach.choDat().getMaCho();
        inputFieldsMap.put(maCho + "_hoTen", txtHoTen);
        inputFieldsMap.put(maCho + "_ngaySinh", txtNgaySinh);
        inputFieldsMap.put(maCho + "_sdt", txtSDT);
        inputFieldsMap.put(maCho + "_cccd", txtCCCD);

        errorLabelsMap.put(maCho + "_hoTen", errHoTen);
        errorLabelsMap.put(maCho + "_ngaySinh", errNgaySinh);
        errorLabelsMap.put(maCho + "_sdt", errSDT);
        errorLabelsMap.put(maCho + "_cccd", errCCCD);

        // --- DÒNG 1: INPUT [Họ tên] và [Ngày sinh] ---
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 0, 0, 5); // padding

        // Label Họ tên
        gbc.gridx = 0; gbc.weightx = 0;
        JLabel lblHoTen = new JLabel("Họ tên*:"); lblHoTen.setFont(fontLabel);
        pnlContent.add(lblHoTen, gbc);

        // Field Họ tên
        gbc.gridx = 1; gbc.weightx = 0.5;
        gbc.insets = new Insets(2, 0, 0, 15); // Cách phải 15px để tách nhóm
        pnlContent.add(txtHoTen, gbc);

        // Label Ngày sinh
        gbc.gridx = 2; gbc.weightx = 0;
        gbc.insets = new Insets(2, 0, 0, 5);
        JLabel lblNgaySinh = new JLabel("Ngày sinh*:"); lblNgaySinh.setFont(fontLabel);
        pnlContent.add(lblNgaySinh, gbc);

        // Field Ngày sinh
        gbc.gridx = 3; gbc.weightx = 0.5;
        gbc.insets = new Insets(2, 0, 0, 0);
        pnlContent.add(txtNgaySinh, gbc);

        // --- DÒNG 2: THÔNG BÁO LỖI CHO DÒNG 1 ---
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0); // Padding dưới để cách dòng 3

        // Lỗi Họ tên (Nằm dưới Field Họ tên - cột 1)
        gbc.gridx = 1; gbc.weightx = 0.5;
        pnlContent.add(errHoTen, gbc);

        // Lỗi Ngày sinh (Nằm dưới Field Ngày sinh - cột 3)
        gbc.gridx = 3; gbc.weightx = 0.5;
        pnlContent.add(errNgaySinh, gbc);

        // --- DÒNG 3: INPUT [SĐT] và [CCCD] ---
        gbc.gridy = 2;
        gbc.insets = new Insets(2, 0, 0, 5);

        // Label SĐT
        gbc.gridx = 0; gbc.weightx = 0;
        JLabel lblSDT = new JLabel("SĐT:"); lblSDT.setFont(fontLabel);
        pnlContent.add(lblSDT, gbc);

        // Field SĐT
        gbc.gridx = 1; gbc.weightx = 0.5;
        gbc.insets = new Insets(2, 0, 0, 15);
        pnlContent.add(txtSDT, gbc);

        // Label CCCD
        gbc.gridx = 2; gbc.weightx = 0;
        gbc.insets = new Insets(2, 0, 0, 5);
        JLabel lblCCCD = new JLabel("CCCD*:"); lblCCCD.setFont(fontLabel);
        pnlContent.add(lblCCCD, gbc);

        // Field CCCD
        gbc.gridx = 3; gbc.weightx = 0.5;
        gbc.insets = new Insets(2, 0, 0, 0);
        pnlContent.add(txtCCCD, gbc);

        // --- DÒNG 4: THÔNG BÁO LỖI CHO DÒNG 3 ---
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Lỗi SĐT
        gbc.gridx = 1; gbc.weightx = 0.5;
        pnlContent.add(errSDT, gbc);

        // Lỗi CCCD
        gbc.gridx = 3; gbc.weightx = 0.5;
        pnlContent.add(errCCCD, gbc);

        panel.add(pnlContent, BorderLayout.CENTER);

        // --- SỰ KIỆN LOGIC (Focus Listener) ---
        FocusAdapter validationListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField source = (JTextField) e.getSource();
                String fieldName = "";

                // Xác định field và validate ngay lập tức
                if (source == txtHoTen) {
                    fieldName = "hoTen";
                    validateField(source, errHoTen, "hoTen");
                } else if (source == txtNgaySinh) {
                    fieldName = "ngaySinh";
                    validateField(source, errNgaySinh, "ngaySinh");
                    // Logic phụ: tính giá
                    if (errNgaySinh.getText().trim().isEmpty() && giaLabelMap.get(maCho) != null) {
                        xuLyNgaySinhThayDoi(maCho, source.getText(), giaLabelMap.get(maCho));
                    }
                } else if (source == txtSDT) {
                    fieldName = "sdt";
                    validateField(source, errSDT, "sdt");
                } else if (source == txtCCCD) {
                    fieldName = "cccd";
                    validateField(source, errCCCD, "cccd");
                    // Logic phụ: nhập nhanh
                    if (errCCCD.getText().trim().isEmpty()) {
                        xuLyNhapNhanhKhachHang(txtCCCD, txtHoTen, txtSDT, txtNgaySinh, maCho);
                    }
                }
                updateKhachRecord(maCho, source.getText(), fieldName);
            }
        };

        txtHoTen.addFocusListener(validationListener);
        txtNgaySinh.addFocusListener(validationListener);
        txtSDT.addFocusListener(validationListener);
        txtCCCD.addFocusListener(validationListener);

        // ComboBox Event
        cbLoaiKhach.addActionListener(e -> {
            String maMoi = getMaLoaiVeFromHienThi((String) cbLoaiKhach.getSelectedItem());
            ChiTietKhach originalKhach = danhSachKhachHang.get(maCho);

            if (originalKhach == null) return;

            // Lấy components Ngày sinh (đã được lưu trong Map)
            JTextField txtNgaySinhToValidate = inputFieldsMap.get(maCho + "_ngaySinh");
            JLabel errNgaySinhToValidate = errorLabelsMap.get(maCho + "_ngaySinh");

            // 1. Cập nhật Loại vé trong Record
            ChiTietKhach updatedKhach = originalKhach.withMaLoaiVe(maMoi);
            danhSachKhachHang.put(maCho, updatedKhach);

            // 2. Thực hiện Validation kép: Ngày sinh hợp lệ về format VÀ Ngày sinh phù hợp với Loại vé
            boolean isDobFormatValid = true;
            boolean isAgeMatchValid = true;
            String ngaySinhValue = txtNgaySinhToValidate.getText().trim();

            // Kiểm tra format trước
            if (!validateField(txtNgaySinhToValidate, errNgaySinhToValidate, "ngaySinh")) {
                isDobFormatValid = false;
            }

            // Nếu format hợp lệ, kiểm tra logic tuổi
            if (isDobFormatValid && !ngaySinhValue.isEmpty()) {
                if (!kiemTraHopLeLoaiVeTheoNgaySinh(ngaySinhValue, maMoi)) {
                    isAgeMatchValid = false;

                    txtNgaySinhToValidate.setBorder(BorderFactory.createLineBorder(Color.RED));
                    String tenLoaiMoi = getTenLoaiVeHienThi(maMoi);
                    errNgaySinhToValidate.setText("Tuổi không đúng loại vé");
                } else {
                    txtNgaySinhToValidate.setBorder(UIManager.getBorder("TextField.border"));
                    errNgaySinhToValidate.setText("");
                }
            }

            // 3. Nếu tuổi hợp lệ, tính lại giá
            if (isAgeMatchValid) {
                try {
                    long gia = tinhGiaVeTau(updatedKhach.choDat(), updatedKhach.maLoaiVe());
                    danhSachGiaVe.put(maCho, gia);
                    lblGia.setText(formatVnd(gia));
                } catch (Exception ex) {
                    lblGia.setText("Lỗi Giá!");
                    System.err.println("Lỗi tính giá khi đổi loại vé: " + ex.getMessage());
                }
            } else {
                danhSachGiaVe.put(maCho, 0L);
                lblGia.setText("0 VNĐ (Lỗi Tuổi)");
            }

            // 4. Cập nhật tổng tiền
            capNhatTongTienUI();
        });

        return panel;
    }

    private boolean validateField(JTextField field, JLabel errorLabel, String type) {
        String value = field.getText().trim();
        String errorMsg = "";
        boolean isValid = true;
        Border errorBorder = BorderFactory.createLineBorder(Color.RED);
        Border normalBorder = UIManager.getBorder("TextField.border");

        switch (type) {
            case "hoTen":
                if (value.isEmpty()) {
                    isValid = false;
                    errorMsg = "Họ tên không để trống.";
                }
                // Regex:
                else if (!value.matches("^[\\p{L}\\s]+$")) {
                    isValid = false;
                    errorMsg = "Không chứa số hay ký tự đặc biệt.";
                }
                break;
            case "ngaySinh":
                if (value.isEmpty()) {
                    isValid = false;
                    errorMsg = "Ngày sinh không để trống.";
                } else {
                    try {
                        Date dob = INPUT_DATE_FORMAT.parse(value);
                        if (dob.after(new Date())) {
                            isValid = false;
                            errorMsg = "Ngày tương lai.";
                        }
                    } catch (ParseException e) {
                        isValid = false;
                        errorMsg = "Sai định dạng dd/mm/yyyy.";
                    }
                }
                break;
            case "cccd":
                if (value.isEmpty()) {
                    isValid = false;
                    errorMsg = "CCCD không để trống.";
                } else if (!value.matches("\\d{12}")) {
                    isValid = false;
                    errorMsg = "CCCD đủ 12 số.";
                }
                break;
            case "sdt":
                if (value.isEmpty()) {
                    isValid = false;
                    errorMsg = "SDT không để trống.";
                } else if (!value.matches("0\\d{9}")) {
                    isValid = false;
                    errorMsg = "SDT đủ 10 số bắt đầu bằng 0.";
                }
                break;
        }

        if (!isValid) {
            field.setBorder(errorBorder);
            if (errorLabel != null) errorLabel.setText(errorMsg);
        } else {
            field.setBorder(normalBorder);
            if (errorLabel != null) errorLabel.setText("");
        }

        return isValid;
    }

    private void capNhatThongTinKhachUI() {
        if (thongTinKhachScrollPane == null) {
            System.out.println("Lỗi: thongTinKhachScrollPane chưa được khởi tạo.");
            return;
        }
        JPanel infoScrollPanel = (JPanel) thongTinKhachScrollPane.getViewport().getView();
        infoScrollPanel.removeAll();

        List<ChiTietKhach> danhSachChiTiet = new ArrayList<>(danhSachKhachHang.values());

        if (danhSachChiTiet.isEmpty()) {
            infoScrollPanel.add(new JLabel("Chưa có chỗ nào được chọn."));
            infoScrollPanel.add(Box.createVerticalGlue());
            infoScrollPanel.revalidate();
            infoScrollPanel.repaint();
            return;
        }

        for (ChiTietKhach khach : danhSachChiTiet) {
            JPanel khachPanel = createKhachPanel(khach);
            infoScrollPanel.add(khachPanel);
        }

        infoScrollPanel.add(Box.createVerticalGlue());
        infoScrollPanel.revalidate();
        infoScrollPanel.repaint();
    }

    private void capNhatDanhSachGheDaChonUI() {
        // Lấy số ghế đã chọn
        int soGheDaChon = danhSachGheDaChon.size();
        // Lấy số khách yêu cầu
        int soKhachYeuCau = parseTextFieldToInt(txtTongSoKhach);
        // Kiểm tra để tránh lỗi nếu lblGheDaChonSummary chưa được khởi tạo (chỉ xảy ra khi gọi quá sớm)
        if (lblGheDaChonTong == null) return;
        // Cập nhật JLabel tóm tắt
        String summaryText = String.format("Chỗ đã chọn: %d/%d", soGheDaChon, soKhachYeuCau);
        lblGheDaChonTong.setText(summaryText);

        // Tùy chỉnh màu sắc để làm nổi bật (tùy chọn)
        if (soGheDaChon == soKhachYeuCau && soKhachYeuCau > 0) {
            lblGheDaChonTong.setForeground(new Color(39, 174, 96)); // Màu xanh lá khi đủ
        } else if (soGheDaChon > soKhachYeuCau) {
            lblGheDaChonTong.setForeground(Color.RED); // Màu đỏ nếu vượt quá yêu cầu
        } else {
            lblGheDaChonTong.setForeground(COLOR_BLUE_LIGHT);
        }


    }

    private void capNhatTongTienUI() {
        long total = 0L;
        for (Long v : danhSachGiaVe.values()) total += v == null ? 0L : v;
        lblTotalPrice.setText("Tổng tiền vé: " + formatVnd(total));
    }


    // ====================================================================================
    // MODULE: 8. HÀM TÍNH TOÁN GIÁ VÀ TUỔI (UTILITY CALCULATORS)
    // ====================================================================================

    private static int tinhTuoi(String dobString) {
        if (dobString == null || dobString.trim().isEmpty()) return 0;
        try {
            Date birthDate = INPUT_DATE_FORMAT.parse(dobString);
            Calendar dob = Calendar.getInstance();
            dob.setTime(birthDate);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            return -1;
        }
    }

    private String goiYLoaiVeByAge(int age) {
        final String MA_VE_DEFAULT = "VT01";

        if (age == -1 || age == 0) return MA_VE_DEFAULT; // Ngày sinh không hợp lệ/tuổi 0


        List<String> priorityOrder = List.of("VT02", "VT03", "VT01", "VT04");

        for (String maLoaiVe : priorityOrder) {
            LoaiVe loaiVe = mapAllLoaiVe.get(maLoaiVe);

            if (loaiVe != null) {
                if (loaiVe.isTuoiHopLe(age)) {
                    return maLoaiVe;
                }
            }
        }
        return MA_VE_DEFAULT;
    }

    /**
     * Kiểm tra tính hợp lệ của loại vé được chọn dựa trên ngày sinh.
     * @param ngaySinhStr Ngày sinh của khách hàng (dạng dd/MM/yyyy).
     * @param maLoaiVeDaChon Mã loại vé mà người dùng đã chọn (VD: VT02).
     * @return true nếu loại vé phù hợp với độ tuổi tính từ ngày sinh, ngược lại là false.
     */
    private boolean kiemTraHopLeLoaiVeTheoNgaySinh(String ngaySinhStr, String maLoaiVeDaChon) {
        int tuoi = tinhTuoi(ngaySinhStr);

        if (tuoi < 0) {
            return false;
        }

        LoaiVe loaiVe = mapAllLoaiVe.get(maLoaiVeDaChon);

        if (loaiVe == null) {
            return true;
        }

        return loaiVe.isTuoiHopLe(tuoi);
    }


    private long tinhGiaVeTau(ChoDat cho, String maLoaiVe) throws Exception {
        Ga gaDi = (Ga) cbGaDi.getSelectedItem();
        Ga gaDen = (Ga) cbGaDen.getSelectedItem();

        if (gaDi == null || gaDen == null || maChuyenTauHienTai == null) {
            throw new Exception("Thông tin chuyến tàu hoặc ga chưa đầy đủ.");
        }

        // Gọi Service xử lý logic
        return NghiepVuTinhGiaVe.tinhGiaVe(
                gaDi.getMaGa(),
                gaDen.getMaGa(),
                maChuyenTauHienTai,
                cho,
                maLoaiVe
        );
    }

    // ====================================================================================
    // MODULE: 9. XỬ LÝ SỰ KIỆN (EVENT HANDLERS)
    // ====================================================================================

    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == btnTimChuyen){
            timKiemChuyenTau();
        } else if (src == btnHuy) {
            huyBoDatVe();
        } else if (src == btnTiepTheo) {
            xuLyNutTiepTheo();
        }
        else if (src == btnVeDoan) { // <<< THÊM: Xử lý Vé Đoàn
            hienThiPopupVeDoan();
        }
    }

    private void huyBoDatVe() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn hủy toàn bộ dữ liệu và quay về Trang chủ?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        resetAllData();

        Window w = SwingUtilities.getWindowAncestor(this);

        if (w instanceof BanVeDashboard || w instanceof AdminFullDashboard) {

            // Xác định tên card Trang chủ tương ứng với Dashboard
            String tenCardTrangChu = (w instanceof AdminFullDashboard) ? "trangChuQL" : "trangChuNV";

            ManHinhTrangChuNVBanVe trangChuPanel = new ManHinhTrangChuNVBanVe(null);

            if (w instanceof BanVeDashboard) {
                BanVeDashboard dashboard = (BanVeDashboard) w;
                dashboard.themHoacCapNhatCard(trangChuPanel, tenCardTrangChu);
                dashboard.chuyenManHinh(tenCardTrangChu);

            } else if (w instanceof AdminFullDashboard) {
                AdminFullDashboard dashboard = (AdminFullDashboard) w;
                dashboard.themHoacCapNhatCard(trangChuPanel, tenCardTrangChu);
                dashboard.chuyenManHinh(tenCardTrangChu);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tìm thấy cửa sổ Dashboard. Vui lòng chạy ứng dụng từ Dashboard hợp lệ.",
                    "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyNutTiepTheo() {
        int required = parseTextFieldToInt(txtTongSoKhach);

        if (required <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn số lượng khách hợp lệ.",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (danhSachGheDaChon.size() != required) {
            JOptionPane.showMessageDialog(this,
                    "Số chỗ đã chọn (" + danhSachGheDaChon.size() + ") không khớp với Tổng số khách (" + required + ").",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateAllInputs()) {
            JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra và điền đầy đủ, chính xác thông tin khách hàng.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (ChiTietKhach khach : danhSachKhachHang.values()) {
            if (khach.hoTen() == null || khach.hoTen().trim().isEmpty() ||
                    khach.cccd() == null || khach.cccd().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ Họ tên và CCCD cho tất cả " + required + " khách hàng.",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Window w = SwingUtilities.getWindowAncestor(this);

        if (w instanceof BanVeDashboard || w instanceof AdminFullDashboard) {

            // Khởi tạo Panel Xác nhận
            ManHinhXacNhanBanVe confirmPanel = new ManHinhXacNhanBanVe(
                    danhSachGheDaChon,
                    danhSachKhachHang,
                    maChuyenTauHienTai,
                    dateChooserNgayDi.getDate(),
                    new HashMap<>(danhSachGiaVe)
            );

            if (w instanceof BanVeDashboard) {
                BanVeDashboard dashboard = (BanVeDashboard) w;
                dashboard.themHoacCapNhatCard(confirmPanel, "xacNhanBanVe");
                dashboard.chuyenManHinh("xacNhanBanVe");

            } else if (w instanceof AdminFullDashboard) {
                // Ép kiểu sang AdminFullDashboard để gọi các phương thức chung
                AdminFullDashboard dashboard = (AdminFullDashboard) w;
                dashboard.themHoacCapNhatCard(confirmPanel, "xacNhanBanVe");
                dashboard.chuyenManHinh("xacNhanBanVe");
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tìm thấy cửa sổ Dashboard. Vui lòng chạy ứng dụng từ BanVeDashboard.",
                    "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ====================================================================================
    // MODULE: 9. XỬ LÝ SỰ KIỆN (EVENT HANDLERS) - Thêm phương thức mới
    // ====================================================================================

    /**
     * Hiển thị Pop-up nhập thông tin vé đoàn và xử lý đặt chỗ tự động.
     */
    // --- Đặt phương thức này trong lớp ManHinhBanVe ---

    private void hienThiPopupVeDoan() {
        if (maChuyenTauHienTai == null || maChuyenTauHienTai.isEmpty() || maToaHienTai == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn chuyến tàu và toa tàu trước khi đặt vé đoàn.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window ownerWindow = SwingUtilities.getWindowAncestor(this);
        Frame ownerFrame = (ownerWindow instanceof Frame) ? (Frame) ownerWindow : null;

        JDialog dialog = new JDialog(ownerFrame, "Đặt vé Đoàn / Từ File", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(450, 280); // Giảm chiều cao vì form đã ngắn hơn
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Panel chính
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Khu vực 1: Nhập số lượng người (Thủ công) ---
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Chỉ còn 1 hàng
        inputPanel.setBorder(BorderFactory.createTitledBorder("Nhập số lượng thủ công"));

        JTextField txtSoNguoi = new JTextField("10");
        inputPanel.add(new JLabel("Số lượng người (*):"));
        inputPanel.add(txtSoNguoi);

        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- Khu vực 2: Tải File ---
        JButton btnTaiFile = new JButton("Tải lên Danh sách Khách hàng (.xlsx, .xls, .csv)");
        btnTaiFile.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTaiFile.setFocusPainted(false);

        JLabel lblTaiFileLuuY = new JLabel("Hoặc chọn file danh sách để nhập nhanh hơn");
        lblTaiFileLuuY.setFont(new Font("Arial", Font.ITALIC, 11));
        lblTaiFileLuuY.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnTaiFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "File Khách hàng (*.xlsx, *.xls, *.csv)", "xlsx", "xls", "csv");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                List<String[]> duLieuKhachHang = docDuLieuKhachHangTuFile(selectedFile);

                if (duLieuKhachHang != null && !duLieuKhachHang.isEmpty()) {
                    xuLyDatChoTuFile(duLieuKhachHang);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "File không chứa dữ liệu hợp lệ.", "Lỗi File", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainPanel.add(btnTaiFile);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(lblTaiFileLuuY);

        dialog.add(mainPanel, BorderLayout.NORTH);

        // Panel nút bấm phía dưới
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDatVe = new JButton("Xác nhận");
        JButton btnHuy = new JButton("Hủy bỏ");

        btnHuy.addActionListener(e -> dialog.dispose());

        btnDatVe.addActionListener(e -> {
            try {
                String textSoNguoi = txtSoNguoi.getText().trim();
                if (textSoNguoi.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số lượng người.", "Thiếu thông tin", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int soNguoi = Integer.parseInt(textSoNguoi);
                if (soNguoi <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Số lượng người phải lớn hơn 0.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ChonChoNhanh(dialog, soNguoi);

                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Số lượng người phải là chữ số.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnDatVe);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Logic chính xử lý việc chọn chỗ tự động cho vé đoàn và cập nhật UI.
     */
    private List<String[]> docDuLieuKhachHangTuFile(File file) {
        List<String[]> danhSachKhachHang = new ArrayList<>();
        // Yêu cầu: Đảm bảo có 5 cột dữ liệu chính
        // [0] HoTen, [1] CCCD, [2] SĐT, [3] NgaySinh, [4] MaLoaiVe
        final int SO_COT_CAN_THIET = 5;

        String fileName = file.getName().toLowerCase();

        // === Logic đọc CSV/TXT (Giả lập) ===
        if (fileName.endsWith(".csv") || fileName.endsWith(".txt")) {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) scanner.nextLine(); // Bỏ qua tiêu đề
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] data = line.split(",");

                    if (data.length >= SO_COT_CAN_THIET) {
                        String[] khachData = new String[SO_COT_CAN_THIET];
                        for (int i = 0; i < SO_COT_CAN_THIET; i++) {
                            khachData[i] = data[i].trim();
                        }
                        danhSachKhachHang.add(khachData);
                    }
                }
                return danhSachKhachHang;
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy file CSV: " + e.getMessage(), "Lỗi File", JOptionPane.ERROR_MESSAGE);
                return null;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi đọc file CSV: " + e.getMessage(), "Lỗi File", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        // === Logic đọc EXCEL (Apache POI) ===

        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            try (FileInputStream fileInputStream = new FileInputStream(file);


                 Workbook workbook = WorkbookFactory.create(fileInputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();

                if (rowIterator.hasNext()) rowIterator.next(); // Bỏ qua dòng tiêu đề

                DataFormatter formatter = new DataFormatter();

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // Kiểm tra ô đầu tiên để bỏ qua dòng trống
                    if (row.getCell(0) == null || formatter.formatCellValue(row.getCell(0)).trim().isEmpty()) {
                        continue;
                    }

                    String[] data = new String[SO_COT_CAN_THIET];
                    boolean duLieuHopLe = true;

                    for (int i = 0; i < SO_COT_CAN_THIET; i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String cellValue = formatter.formatCellValue(cell).trim();

                        if (i <= 2 && cellValue.isEmpty()) { // HoTen, CCCD, SĐT không được trống
                            duLieuHopLe = false;
                            break;
                        }
                        data[i] = cellValue;
                    }

                    if (duLieuHopLe) {
                        danhSachKhachHang.add(data);
                    }
                }
                return danhSachKhachHang;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi đọc file Excel: Vui lòng đảm bảo file không mở và định dạng đúng.",
                        "Lỗi Apache POI", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return null;
            }
        }

        // Nếu không phải định dạng được hỗ trợ
        JOptionPane.showMessageDialog(this, "Định dạng file không được hỗ trợ (.xlsx, .xls, .csv, .txt).", "Lỗi File", JOptionPane.ERROR_MESSAGE);
        return null;
    }

// --- Đặt phương thức này trong lớp ManHinhBanVe ---

    private void xuLyDatChoTuFile(List<String[]> duLieuKhachHangTuFile) {
        int soNguoi = duLieuKhachHangTuFile.size();
        if (soNguoi == 0) {
            JOptionPane.showMessageDialog(this, "File không chứa khách hàng nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 1. Lấy tất cả ghế đang trống và sắp xếp theo số thứ tự ghế
        List<ChoDat> tatCaGheTrong = tatCaChoDatToaHienTai.values().stream()
                .filter(cho -> !cho.isDaDat() && !danhSachGheDaChon.containsKey(cho.getMaCho()))
                .sorted(Comparator.comparingInt(cho -> Integer.parseInt(cho.getSoCho()))) // Quan trọng: Sắp xếp theo số ghế 1, 2, 3...
                .collect(Collectors.toList());

        if (tatCaGheTrong.size() < soNguoi) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ tìm thấy " + tatCaGheTrong.size() + " chỗ trống trong toa, không đủ cho " + soNguoi + " người.",
                    "Không đủ chỗ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Thuật toán tìm dãy ghế gần nhau nhất
        List<ChoDat> choChonToiUu = timDayGheGanNhau(tatCaGheTrong, soNguoi);

        // 3. Reset và thực hiện đặt vé (giữ nguyên logic cũ của bạn nhưng dùng danh sách tối ưu)
        this.danhSachGheDaChon.clear();
        this.danhSachKhachHang.clear();
        this.danhSachGiaVe.clear();

        int count = 0;
        for (int i = 0; i < choChonToiUu.size(); i++) {
            ChoDat cho = choChonToiUu.get(i);
            String[] data = duLieuKhachHangTuFile.get(i);
            String maCho = cho.getMaCho();

            String maLoaiVe = (data[4] != null && data[4].startsWith("VT")) ? data[4] : MA_VE_NL;

            ChiTietKhach chiTietKhach = new ChiTietKhach(
                    cho, maLoaiVe, data[0], data[1], data[2], data[3]
            );

            try {
                long gia = tinhGiaVeTau(cho, chiTietKhach.maLoaiVe());
                danhSachGiaVe.put(maCho, gia);
            } catch (Exception ex) {
                continue;
            }

            danhSachGheDaChon.put(maCho, cho);
            danhSachKhachHang.put(maCho, chiTietKhach);

            JButton btnCho = seatButtonsMap.get(maCho);
            if (btnCho != null) {
                btnCho.setBackground(new Color(0, 123, 255));
                btnCho.setForeground(Color.WHITE);
            }
            count++;
        }

        // Cập nhật UI
        txtTongSoKhach.setText(String.valueOf(count));
        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
        capNhatTongTienUI();

        JOptionPane.showMessageDialog(this, "Đã tự động chọn " + count + " chỗ gần nhau nhất cho đoàn.");
    }

    private void ChonChoNhanh(Dialog dialog, int soNguoi){
        List<ChoDat> tatCaGheTrong = tatCaChoDatToaHienTai.values().stream()
                .filter(cho -> !cho.isDaDat() && !danhSachGheDaChon.containsKey(cho.getMaCho()))
                .sorted(Comparator.comparingInt(cho -> Integer.parseInt(cho.getSoCho())))
                .collect(Collectors.toList());

        // 2. Kiểm tra số lượng
        if (tatCaGheTrong.size() < soNguoi) {

            JOptionPane.showMessageDialog(dialog,
                    "Không đủ chỗ! Toa hiện tại chỉ còn " + tatCaGheTrong.size() + " chỗ trống.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Tìm các chỗ gần nhau nhất bằng hàm bổ trợ bạn đã viết
        List<ChoDat> choChonToiUu = timDayGheGanNhau(tatCaGheTrong, soNguoi);

        // 4. Reset các lựa chọn cũ (nếu muốn đặt mới hoàn toàn)
        // Hoặc bỏ qua nếu muốn đặt cộng dồn
        this.danhSachGheDaChon.clear();
        this.danhSachKhachHang.clear();
        this.danhSachGiaVe.clear();

        // 5. Đổ dữ liệu vào Map và cập nhật trạng thái Button
        for (ChoDat cho : choChonToiUu) {
            String maCho = cho.getMaCho();

            // Tạo khách hàng trống để người dùng điền sau
            ChiTietKhach chiTietKhach = new ChiTietKhach(
                    cho,
                    MA_VE_NL, // Mặc định loại vé người lớn
                    "", "", "", "" // Các thông tin khác để trống
            );

            try {
                long gia = tinhGiaVeTau(cho, MA_VE_NL);
                danhSachGiaVe.put(maCho, gia);
            } catch (Exception ex) {
                continue;
            }

            danhSachGheDaChon.put(maCho, cho);
            danhSachKhachHang.put(maCho, chiTietKhach);

            // Đổi màu nút trên sơ đồ (Màu xanh dương - Đang chọn)
            JButton btnCho = seatButtonsMap.get(maCho);
            if (btnCho != null) {
                btnCho.setBackground(new Color(0, 123, 255));
                btnCho.setForeground(Color.WHITE);
            }
        }

        // 6. Cập nhật toàn bộ UI màn hình chính
        txtTongSoKhach.setText(String.valueOf(danhSachGheDaChon.size()));
        capNhatDanhSachGheDaChonUI();
        capNhatThongTinKhachUI();
        capNhatTongTienUI();

        JOptionPane.showMessageDialog(this, "Đã tự động chọn " + soNguoi + " ghế gần nhau nhất.");
    }

    /**
     * Hàm bổ trợ: Tìm danh sách ghế có độ tập trung cao nhất (gần nhau nhất)
     */
    private List<ChoDat> timDayGheGanNhau(List<ChoDat> danhSachGheTrong, int soLuong) {
        if (danhSachGheTrong.isEmpty()) return new ArrayList<>();

        List<List<ChoDat>> cacCumGhe = new ArrayList<>();
        List<ChoDat> cumHienTai = new ArrayList<>();
        cumHienTai.add(danhSachGheTrong.get(0));

        for (int i = 1; i < danhSachGheTrong.size(); i++) {
            // Parse String sang Int để thực hiện phép cộng
            int soTruoc = Integer.parseInt(danhSachGheTrong.get(i - 1).getSoCho());
            int soSau = Integer.parseInt(danhSachGheTrong.get(i).getSoCho());

            if (soSau == soTruoc + 1) {
                cumHienTai.add(danhSachGheTrong.get(i));
            } else {
                cacCumGhe.add(new ArrayList<>(cumHienTai));
                cumHienTai.clear();
                cumHienTai.add(danhSachGheTrong.get(i));
            }
        }
        cacCumGhe.add(cumHienTai);

        for (List<ChoDat> cum : cacCumGhe) {
            if (cum.size() >= soLuong) return cum.subList(0, soLuong);
        }
        return danhSachGheTrong.subList(0, soLuong);
    }




    // ====================================================================================
    // MODULE: 10. HÀM HỖ TRỢ CHUNG (GENERAL UTILITIES)
    // ====================================================================================

    private JPanel ToaPanelTangGiam(String labelText, String initialValue, JTextField targetField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel labelDiscountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelDiscountPanel.setOpaque(false);

        JLabel mainLabel = new JLabel(labelText);
        mainLabel.setFont(mainLabel.getFont().deriveFont(Font.BOLD, 14f));
        labelDiscountPanel.add(mainLabel);

        panel.add(labelDiscountPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        controlPanel.setOpaque(false);

        JButton btnMinus = new JButton("−");
        btnMinus.setPreferredSize(new Dimension(30, 30));
        btnMinus.setMargin(new Insets(0, 0, 0, 0));
        styleSpinButton(btnMinus);

        targetField.setText(initialValue);
        targetField.setHorizontalAlignment(JTextField.CENTER);
        targetField.setPreferredSize(new Dimension(60, 30));
        targetField.setMaximumSize(new Dimension(60, 30));

        // Cho phép sửa
        targetField.setEditable(true);
        // Thêm sự kiện khi mất focus để kiểm tra giá trị hợp lệ
        targetField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                kiemTraHopLeSoKhach();
            }
        });
        targetField.addActionListener(e -> kiemTraHopLeSoKhach());

        JButton btnPlus = new JButton("+");
        btnPlus.setPreferredSize(new Dimension(30, 30));
        btnPlus.setMargin(new Insets(0, 0, 0, 0));
        styleSpinButton(btnPlus);

        btnPlus.addActionListener(e -> changeQuantity(targetField, 1));
        btnMinus.addActionListener(e -> changeQuantity(targetField, -1));

        controlPanel.add(btnMinus);
        controlPanel.add(targetField);
        controlPanel.add(btnPlus);

        panel.add(controlPanel, BorderLayout.EAST);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return panel;
    }

    private void changeQuantity(JTextField field, int delta) {
        int currentValue = parseTextFieldToInt(field);
        int newValue = currentValue + delta;
        if (newValue <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng khách phải lớn hơn 0", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return; // Không cho phép giảm xuống 0 hoặc âm
        }

        field.setText(String.valueOf(newValue));
        capNhatSoLuongYeuCau();
        capNhatDanhSachGheDaChonUI();

    }

    private void kiemTraHopLeSoKhach() {
        String text = txtTongSoKhach.getText().trim();
        try {
            int value = Integer.parseInt(text);
            if (value <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng khách phải lớn hơn 0", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtTongSoKhach.setText("1"); // Reset về giá trị mặc định an toàn
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTongSoKhach.setText("1");
        }

        // Sau khi kiểm tra xong, cập nhật lại trạng thái giao diện liên quan
        capNhatSoLuongYeuCau();
        capNhatDanhSachGheDaChonUI();
    }

    private int parseTextFieldToInt(JTextField field) {
        try {
            if (field.getText().trim().isEmpty()) return 0;
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void styleSpinButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    private void styleNutChinh(JButton btn) {
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(110, 25));
    }



    private JButton taoNutToa(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(String.format("<html><center>%s</center></html>", text.replace("\n", "<br>")));
        btn.setPreferredSize(new Dimension(110, 60));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
        btn.setBackground(bgColor != null ? bgColor : Color.LIGHT_GRAY);
        btn.setForeground(fgColor != null ? fgColor : Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel taoMucChuGiai(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        JLabel square = new JLabel();
        square.setPreferredSize(new Dimension(15, 15));
        square.setOpaque(true);
        square.setBackground(color);
        square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(square);
        panel.add(new JLabel(text));
        return panel;
    }


    private String layLoaiToa(String maToa) throws Exception {
        if (maToa == null || maChuyenTauHienTai == null) {
            throw new Exception("Thiếu Mã toa hoặc Mã chuyến tàu.");
        }

        String maTau = null;
        for (ChuyenTau ct : ketQua) {
            if (maChuyenTauHienTai.equals(ct.getMaChuyenTau())) {
                maTau = ct.getMaTau();
                break;
            }
        }
        if (maTau == null) throw new Exception("Không tìm thấy mã tàu cho chuyến tàu hiện tại.");

        ToaDAO tdao = new ToaDAO();
        List<Toa> toas = tdao.layToaTheoMaTau(maTau);
        if (toas != null) {
            for (Toa t : toas) {
                if (t.getMaToa().equals(maToa)) {
                    return t.getLoaiToa();
                }
            }
        }
        throw new Exception("Không tìm thấy Loại toa cho mã toa: " + maToa);
    }

    private String laySoThuTuToa(String maToa) {
        if (maToa != null && maToa.contains("-")) {
            return maToa.substring(maToa.lastIndexOf('-') + 1);
        }
        return maToa;
    }

    private String[] getLoaiVeOptions() {
        return mapAllLoaiVe.values().stream()
                .map(this::formatLoaiVeHienThi)
                .toArray(String[]::new);
    }
    private String formatLoaiVeHienThi(LoaiVe lv) {
        String tenLoai = lv.getTenLoai();
        String ma = lv.getMaLoaiVe();

        // Tính phần trăm giảm giá
        int phanTramGiam = (int) Math.round((1.0 - lv.getMucGiamGia()) * 100);
        String giamStr = (phanTramGiam > 0) ? " - " + phanTramGiam + "%" : "";

        // Kiểm tra nếu là Trẻ em (VT02) hoặc Người cao tuổi (VT03)
        if (ma.equals("VT02") || ma.equals("VT03")) {
            String dieuKienTuoi = "";
            if (lv.getTuoiMax() >= 999) {
                dieuKienTuoi = " (>= " + lv.getTuoiMin() + ")";
            } else {
                dieuKienTuoi = " (" + lv.getTuoiMin() + "-" + lv.getTuoiMax() + ")";
            }
            return tenLoai + dieuKienTuoi + giamStr;
        }

        // Các loại khác (Người lớn, Sinh viên...) chỉ hiện tên và % giảm (nếu có)
        return tenLoai + giamStr;
    }
    private String getTenLoaiVeHienThi(String maLoaiVe) {
        LoaiVe lv = mapAllLoaiVe.get(maLoaiVe);
        if (lv != null) {
            return formatLoaiVeHienThi(lv);
        }
        LoaiVe defaultLv = mapAllLoaiVe.get("VT01");
        return defaultLv != null ? formatLoaiVeHienThi(defaultLv) : "Người lớn";
    }

    private String getMaLoaiVeFromHienThi(String tenHienThi) {
        return mapReverseLoaiVe.getOrDefault(tenHienThi, "VT01");
    }

    private String formatVnd(long amount) {
        try {
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
            return nf.format(amount) + " VNĐ";
        } catch (Exception e) {
            return amount + " VNĐ";
        }
    }
    /**
     * Tìm và cập nhật JComboBox loại vé cho một khách hàng cụ thể.
     * @param maCho Mã chỗ ngồi.
     * @param maLoaiVeMoi Mã loại vé mới cần hiển thị.
     */
    private void updateLoaiVeComboBoxUI(String maCho, String maLoaiVeMoi) {
        JPanel infoScrollPanel = (JPanel) thongTinKhachScrollPane.getViewport().getView();

        // Duyệt qua các Panel khách hàng con
        for (Component comp : infoScrollPanel.getComponents()) {
            if (comp instanceof JPanel panelKhach && panelKhach.getName() != null && panelKhach.getName().equals(maCho)) {
                // Panel khách hàng được tìm thấy. Giờ tìm ComboBox trong header.
                JPanel pnlHeader = (JPanel) panelKhach.getComponent(0); // Header là BorderLayout.NORTH

                for (Component headerComp : pnlHeader.getComponents()) {
                    if (headerComp instanceof JComboBox<?> cb) {
                        // Cập nhật giá trị hiển thị (dùng hàm tiện ích có sẵn)
                        cb.setSelectedItem(getTenLoaiVeHienThi(maLoaiVeMoi));
                        return;
                    }
                }
            }
        }
    }



    // ====================================================================================
    // MODULE: 11. XỬ LÝ SƠ ĐỒ GHẾ (SEAT MAP DRAWING)
    // ====================================================================================
    private static final Dimension KICH_THUOC_GHE_VUONG = new Dimension(47, 18);
    private static final Dimension KICH_THUOC_GIUONG_NAM = new Dimension(47, 25);
    private void veSoDoGheNgoi(List<ChoDat> danhSachChoDat) {
        // 1. Dọn dẹp
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        tatCaChoDatToaHienTai.clear();

        // 2. Thiết lập Layout
        pnlSoDoGhe.setLayout(new BoxLayout(pnlSoDoGhe, BoxLayout.Y_AXIS));
        pnlSoDoGhe.setOpaque(true);
        pnlSoDoGhe.setBackground(Color.WHITE);
        pnlSoDoGhe.setBorder(new EmptyBorder(5, 5, 0, 5));

        int soHang = 4;
        int tongSoCho = danhSachChoDat.size();
        // Giả định tổng số chỗ là 64 (tức 32 chỗ mỗi nửa)
        int kichThuocNuaToa = tongSoCho / 2;

        // --- Nửa Trái (Ghế 1-32) ---
        int soCot = (int) Math.ceil((double) kichThuocNuaToa / soHang); // 32 chỗ / 4 hàng = 8 cột
        List<ChoDat> choToa = danhSachChoDat;

        // --- Container Trái (Ghế 1-16 hoặc 1-32 nếu toa chỉ có 2 dãy) ---
        // Giả sử 4 hàng là 4 dãy ghế chạy dọc toa (như sơ đồ bạn vẽ)
        // Sẽ chia toa thành 4 dãy, 2 dãy bên trái và 2 dãy bên phải lối đi.
        // Lấy 2 dãy (2/4 hàng) đầu tiên
        int soGheMoiDay = tongSoCho / 4;

        // Nửa bên cửa sổ (2 hàng đầu)
        List<ChoDat> choCuaSo = new ArrayList<>();
        // Nửa bên lối đi (2 hàng cuối)
        List<ChoDat> choLoiDi = new ArrayList<>();

        // Logic chia dữ liệu theo hàng vật lý (1-2 và 3-4)
        // Giả sử chỗ ngồi 1, 2, 3, 4 là hàng 1, 2, 3, 4 theo chiều ngang toa
        // Hàng 1 (A), Hàng 2 (B) ở một bên. Hàng 3 (C), Hàng 4 (D) ở bên kia.

        // Tách 64 ghế thành 4 dãy 16 ghế (16 cột)
        int soGheMoiCot = tongSoCho / soCot; // 64 / 8 = 8 ??? Không, 4 hàng, 16 cột
        soCot = tongSoCho / soHang; // 64 / 4 = 16 cột

        // --- TÁCH THÀNH 4 DÃY GHẾ THEO LỐI ĐI NGANG (2 Dãy + Lối đi + 2 Dãy) ---
        List<ChoDat> day1 = new ArrayList<>();
        List<ChoDat> day2 = new ArrayList<>();
        List<ChoDat> day3 = new ArrayList<>();
        List<ChoDat> day4 = new ArrayList<>();

        // Sắp xếp lại dựa trên số ghế (STT) để đảm bảo 1, 2, 3, 4 là hàng dọc.
        // Nếu STT%4 == 1 => Hàng 1
        // Nếu STT%4 == 2 => Hàng 2
        // Nếu STT%4 == 3 => Hàng 3
        // Nếu STT%4 == 0 => Hàng 4 (hoặc 4)

        for (ChoDat cho : danhSachChoDat) {
            int stt = Integer.parseInt(cho.getSoCho());
            int hangDoc = (stt - 1) % soHang + 1; // 1, 2, 3, 4, 1, 2, 3, 4, ...

            // Chia thành 4 dãy dọc:
            if (hangDoc == 1) day1.add(cho);
            else if (hangDoc == 2) day2.add(cho);
            else if (hangDoc == 3) day3.add(cho);
            else if (hangDoc == 4) day4.add(cho);
        }

        // Nửa Trái (Hàng 1 và Hàng 2)
        JPanel containerTrai = taoContainerHaiHang(day1, day2, soCot, KICH_THUOC_GHE_VUONG);
        pnlSoDoGhe.add(containerTrai);

        // Lối đi dọc
        pnlSoDoGhe.add(Box.createHorizontalStrut(40)); // Lối đi rộng hơn

        // Nửa Phải (Hàng 3 và Hàng 4)
        JPanel containerPhai = taoContainerHaiHang(day3, day4, soCot, KICH_THUOC_GHE_VUONG);
        pnlSoDoGhe.add(containerPhai);

        // 3. Cập nhật UI
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }


    // --- HÀM HỖ TRỢ MỚI: TẠO 2 HÀNG GHẾ VÀ GHÉP CHÚNG THEO CỘT ---
    private JPanel taoContainerHaiHang(List<ChoDat> dayTren, List<ChoDat> dayDuoi, int soCot, Dimension kichThuoc) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlDayTren = new JPanel(new GridLayout(1, soCot, 2, 2));
        for (ChoDat cho : dayTren) {
            pnlDayTren.add(taoNutChoDat(cho, kichThuoc));
        }
        container.add(pnlDayTren);

        container.add(Box.createVerticalStrut(2));

        JPanel pnlDayDuoi = new JPanel(new GridLayout(1, soCot, 2, 2));
        for (ChoDat cho : dayDuoi) {
            pnlDayDuoi.add(taoNutChoDat(cho, kichThuoc));
        }
        container.add(pnlDayDuoi);

        return container;
    }

    // Phương thức vẽ sơ đồ giường nằm (theo Khoang, 3 tầng)
    private void veSoDoGiuongNam(List<ChoDat> danhSachChoDat) {
        // 1. Dọn dẹp
        pnlSoDoGhe.removeAll();
        seatButtonsMap.clear();
        tatCaChoDatToaHienTai.clear();

        // 2. Sắp xếp và Nhóm chỗ theo Khoang
        danhSachChoDat.sort(Comparator
                .comparing(ChoDat::getKhoang)
                .thenComparing(ChoDat::getTang)
                .thenComparing(ChoDat::getSoCho));

        Map<Integer, List<ChoDat>> gheTheoKhoang = danhSachChoDat.stream()
                .collect(Collectors.groupingBy(ChoDat::getKhoang, LinkedHashMap::new, Collectors.toList()));

        // 3. Thiết lập Layout
        pnlSoDoGhe.setLayout(new BoxLayout(pnlSoDoGhe, BoxLayout.X_AXIS));
        pnlSoDoGhe.setBorder(new EmptyBorder(10, 5, 10, 5));

        boolean laKhoangDauTien = true;
        for (Map.Entry<Integer, List<ChoDat>> entry : gheTheoKhoang.entrySet()) {
            if (!laKhoangDauTien) {
                // Thêm hành lang/vách ngăn giữa các Khoang
                pnlSoDoGhe.add(Box.createHorizontalStrut(10));
            }
            // Dùng KICH_THUOC_GIUONG_NAM
            JPanel pnlKhoang = taoContainerKhoangGiuongNam(entry.getKey(), entry.getValue());
            pnlSoDoGhe.add(pnlKhoang);
            laKhoangDauTien = false;
        }

        // 4. Cập nhật UI
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }


    // Hàm hỗ trợ: Tạo Container cho giường nằm (GridLayout 3x2)
    private JPanel taoContainerKhoangGiuongNam(int soKhoang, List<ChoDat> choTrongKhoang) {
        // 3 Hàng (Tầng 1, 2, 3), 2 Cột (2 chỗ nằm ngang)
        JPanel khoangPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        khoangPanel.setBorder(BorderFactory.createTitledBorder("Khoang " + soKhoang));

        for (ChoDat cho : choTrongKhoang) {
            // Dùng KICH_THUOC_GIUONG_NAM
            JButton nutGhe = taoNutChoDat(cho, KICH_THUOC_GIUONG_NAM);
            khoangPanel.add(nutGhe);
        }
        return khoangPanel;
    }


    // Hàm hỗ trợ: Tạo và thiết lập trạng thái cho JButton
    private JButton taoNutChoDat(ChoDat cho, Dimension kichThuoc) {
        String tenHienThi = cho.getSoCho();

        JButton btnCho = new JButton(tenHienThi);

        // Thiết lập kích thước
        btnCho.setPreferredSize(kichThuoc);
        btnCho.setMinimumSize(kichThuoc);
        btnCho.setMaximumSize(kichThuoc);
        btnCho.setFont(btnCho.getFont().deriveFont(Font.BOLD, 12f));

        boolean isSelected = danhSachGheDaChon.containsKey(cho.getMaCho());
        boolean isBooked = cho.isDaDat();
        tatCaChoDatToaHienTai.put(cho.getMaCho(), cho);

        // Thiết lập màu sắc và trạng thái
        if (isSelected) {
            btnCho.setBackground(new Color(0, 123, 255));
            btnCho.setForeground(Color.WHITE);
            btnCho.setEnabled(true);
            btnCho.setToolTipText("Ghế đang được chọn");
        } else if (isBooked) {
            btnCho.setBackground(Color.BLACK);
            btnCho.setForeground(Color.WHITE);
            btnCho.setEnabled(false);
            btnCho.setToolTipText("Ghế đã được bán");
        } else {
            btnCho.setBackground(Color.LIGHT_GRAY);
            btnCho.setForeground(Color.BLACK);
            btnCho.setEnabled(true);
        }

        if (btnCho.isEnabled()) {
            btnCho.addActionListener(e -> xuLyChonGhe(btnCho, cho));
        }

        // Cập nhật map nút
        seatButtonsMap.put(cho.getMaCho(), btnCho);
        return btnCho;
    }
    // ====================================================================================
    // MODULE: 12. RESET DỮ LIỆU
    // ====================================================================================

    public void resetAllData() {
        danhSachGheDaChon.clear();
        danhSachKhachHang.clear();
        tatCaChoDatToaHienTai.clear();
        seatButtonsMap.clear();
        danhSachGiaVe.clear();
        inputFieldsMap.clear();
        errorLabelsMap.clear();

        ketQua.clear();

        maChuyenTauHienTai = null;
        maToaHienTai = null;
        lastSelectedToaButton = null;
        lastSelectedChuyenTauPanel = null;

        if (tableModel != null) {
            tableModel.setRowCount(0);
        }

        if (txtTongSoKhach != null) {
            txtTongSoKhach.setText("1"); // Reset số khách yêu cầu
        }

        SwingUtilities.invokeLater(() -> {
            capNhatDanhSachGheDaChonUI();

            if (pnlChuyenTau != null) {
                pnlChuyenTau.removeAll();
                pnlChuyenTau.add(new JLabel("Vui lòng chọn Ga đi, Ga đến và Ngày đi để tìm kiếm."));
                pnlChuyenTau.revalidate();
                pnlChuyenTau.repaint();
            }

            if (pnlToa != null) {
                pnlToa.removeAll();
                pnlToa.revalidate();
                pnlToa.repaint();
            }

            if (pnlSoDoGhe != null) {
                pnlSoDoGhe.removeAll();
                pnlSoDoGhe.add(new JLabel("Chưa có sơ đồ ghế."));
                pnlSoDoGhe.revalidate();
                pnlSoDoGhe.repaint();
            }

            capNhatThongTinKhachUI();
            capNhatTongTienUI();
        });
    }


    // ====================================================================================
    // MODULE: 13. HÀM MAIN (CHẠY ĐỘC LẬP)
    // ====================================================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Panel Bán vé (Kiểm tra)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ManHinhBanVe(), BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}