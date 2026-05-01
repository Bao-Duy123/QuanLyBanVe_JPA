// java
package JDBC_Project.gui.Panel;

import control.CaLamViec;
import dao.*;
import entity.*;
import gui.MainFrame.AdminFullDashboard;
import gui.MainFrame.BanVeDashboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;


/*
logic chính:
- Tải khuyến mãi từ DB vào combobox
- Khi chọn KM và ấn "Áp dụng", kiểm tra điều kiện áp dụng (MIN_GIA, MIN_SL)
- Nếu thỏa mãn, áp dụng KM và cập nhật tổng tiền
- Nếu không th
- Luồng xác nhận bán vé (sau khi chọn được ghế mong muốn)
 */

/**
 * ManHinhXacNhanBanVe - phiên bản cập nhật: khuyến mãi đọc từ DB và hiển thị combobox.
 * Tên biến và phương thức bằng tiếng Việt không dấu.
 */
public class ManHinhXacNhanBanVe extends JPanel {
    private static final Logger log = LogManager.getLogger(ManHinhXacNhanBanVe.class);
    private Map<String, ChoDat> danhSachGhe;
    private Map<String, Object> danhSachKhach;
    private final Map<String, Long> danhSachGiaVe;
    private String maChuyen;
    private Date ngayDi;

    private JTable tbKhach;
    private JTable tbHoaDon;
    private JLabel lblTongTien;
    private JComboBox<String> cbHinhThuc;
    private JTextField txtTienKhachDua;
    private JTextField txtTienThoiLai;

    // UI Khuyen mai
    private JComboBox<KhuyenMai> cbKhuyenMai;
    private JLabel lblThongTinKhuyenMai;

    //UI Hoa don
    private JLabel lblMaHoaDon;
    private JLabel lblNguoiLapHD;
    private JLabel lblDienThoaiNV;
    private JLabel lblTenNguoiDat;
    private JLabel lblSdtNguoiDat;
    private JLabel lblPhuongThucTT;

    private JLabel lblTongCongLon;

    // Khuyen mai dang ap dung
    private KhuyenMai khuyenMaiApDung = null;

    // DAO
    private KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final VeDAO veBVDao = new VeDAO();

    private static final Color MAU_XANH_BTN = new Color(0, 180, 110);
    private static final Color MAU_CAM_BTN = new Color(255, 140, 0);

    // Thêm dòng này để khai báo biến mock
    private static  String MA_NV_LAP_HD_MOCK = "--";
    private DefaultTableModel model;
    private JButton btnXacNhan;
    private JLabel lblTienBangChu;
    private JButton btnHuy;
    private JButton btnKetThuc;
    private JButton btnQuayLai;
    private String maNV;
    private String tenNV;
    private String sdtNV;
    private KhuyenMai bestKm;
    private JLabel lblGiamGia;
    private JLabel lblTenKhuyenMai;

    public ManHinhXacNhanBanVe(Map<String, ChoDat> danhSachGheDaChon,
                               Map<String, ?> danhSachKhachHang, // Nhận Map với giá trị generic
                               String maChuyenTau,
                               Date ngayDi,
                               Map<String, Long> danhSachGiaVe) {

        this.danhSachGhe = new LinkedHashMap<>(danhSachGheDaChon);
        this.danhSachKhach = new LinkedHashMap<>((Map<String, Object>) danhSachKhachHang); // Ép kiểu an toàn (cần kiểm tra)
        this.maChuyen = maChuyenTau;
        this.ngayDi = ngayDi;
        this.danhSachGiaVe = danhSachGiaVe != null ? new HashMap<>(danhSachGiaVe) : new HashMap<>();

        layThongTinNhanVien();

        khoiTaoGiaoDien();

        // Tai khuyen mai tu DB va cap nhat giao dien
        taiKhuyenMaiTuDB();
        capNhatTongVaGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel tieuDe = new JLabel("Bán vé - xác nhận thanh toán");
        tieuDe.setFont(tieuDe.getFont().deriveFont(Font.BOLD, 20f));
        tieuDe.setBorder(new EmptyBorder(6, 6, 12, 6));
        add(tieuDe, BorderLayout.NORTH);

        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        chia.setResizeWeight(0.5);
        chia.setDividerSize(8);
        chia.setBorder(null);
        chia.setBackground(getBackground());

        chia.setLeftComponent(taoPanelTrai());
        chia.setRightComponent(taoPanelHoaDon());

        SwingUtilities.invokeLater(() -> {
            chia.setDividerLocation(0.5);
        });

        add(chia, BorderLayout.CENTER);
    }

    private void layThongTinNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv != null) {
            this.maNV= nv.getMaNV();
            this.tenNV = nv.getHoTen();
            this.sdtNV = nv.getSdt();
        } else {
            this.maNV = "";
            this.tenNV = "";
            this.sdtNV = "";
        }
    }

    private <T> T getKhachField(Object khach, String fieldName, Class<T> type) {
        if (khach == null) return null;
        try {
            // Thử truy cập accessor method (cho Record)
            Method method = khach.getClass().getMethod(fieldName);
            Object result = method.invoke(khach);
            return type.cast(result);
        } catch (Exception e) {
            // Thất bại, trả về null hoặc giá trị mặc định
            return null;
        }
    }


    private JPanel taoPanelTrai() {
        JPanel panelTrai = new JPanel();
        panelTrai.setLayout(new BoxLayout(panelTrai, BoxLayout.Y_AXIS));
        panelTrai.setBackground(new Color(255, 255, 255));
        panelTrai.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false),
                new EmptyBorder(12, 12, 12, 12)));

        panelTrai.add(taoPanelThongTinDatVe());
        panelTrai.add(Box.createRigidArea(new Dimension(0, 12)));
        panelTrai.add(taoPanelDanhSachKhach());
        panelTrai.add(Box.createRigidArea(new Dimension(0, 12)));
        panelTrai.add(taoPanelTongHoaDon());
        panelTrai.add(Box.createRigidArea(new Dimension(0, 12)));
        panelTrai.add(taoPanelThanhToan());
        panelTrai.add(Box.createVerticalGlue());

        JScrollPane scrLeftContent = new JScrollPane(panelTrai);
        scrLeftContent.setBorder(null);
        scrLeftContent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrLeftContent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrLeftContent.getVerticalScrollBar().setUnitIncrement(16);

        return panelTrai;
    }

    private JPanel taoPanelThongTinDatVe() {
        JPanel panelThongTin = new JPanel(new BorderLayout());
        panelThongTin.setOpaque(false);
        panelThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin hành trình"));

        JPanel noiDung = new JPanel(new GridLayout(0, 1));
        noiDung.setOpaque(false);

        //mã chuyến tàu là SE1_251218_HAN_SAG

        ChuyenTau chuyenTau = ChuyenTauDao.layChuyenTauBangMa(maChuyen);

        String tuyenStr = "-";
        String tenGaDi = "Chưa xác định";
        String tenGaDen = "Chưa xác định";
        String maTau = "-";
        String thoiGianHienThi = "-";
        if (maChuyen != null && maChuyen.contains("_")) {
            tuyenStr = chuyenTau.maTau;


            String[] parts = maChuyen.split("_");
            if (parts.length >= 4) {
                maTau = parts[0]; // SE1
                String maGaDi = parts[2]; // HAN
                String maGaDen = parts[3]; // SAG

                // Gọi DAO để lấy tên ga thực tế từ Database
                GaDao gaDao = new GaDao();
                Ga gaDi = gaDao.layGaBangMa(maGaDi);
                Ga gaDen = gaDao.layGaBangMa(maGaDen);

                if (gaDi != null) tenGaDi = gaDi.getTenGa();
                if (gaDen != null) tenGaDen = gaDen.getTenGa();


            }
            DateTimeFormatter ngayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            DateTimeFormatter gioFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String ngay = chuyenTau.getNgayKhoiHanh().format(ngayFormatter);
            String gio = chuyenTau.getGioKhoiHanh().format(gioFormatter);

            thoiGianHienThi = gio + " ngày " + ngay;

        }
        //ngày đi chỉ thể hiện được này đi thôi, không thể hiện được giờ đi
        int count = (danhSachGhe != null) ? danhSachGhe.size() : 0;

        noiDung.add(new JLabel("Tuyến tàu: " + (tuyenStr != null ? tuyenStr : "-")));



        // Tuyến đường: Ga Đi - Ga Đến
        noiDung.add(new JLabel("Tuyến đường: " + tenGaDi + " ➔ " + tenGaDen));

        // Thời gian khởi hành
        noiDung.add(new JLabel("Khởi hành: "+ thoiGianHienThi));


        // Mã tàu (đã tách từ mã chuyến)
        noiDung.add(new JLabel("Số hiệu tàu: "+ maTau));


        // Số lượng khách
        noiDung.add(new JLabel("Số lượng: "+ count + " hành khách"));


        panelThongTin.add(noiDung, BorderLayout.CENTER);
        return panelThongTin;

    }

    private JPanel taoPanelDanhSachKhach() {
        JPanel panelDanhSach = new JPanel(new BorderLayout());
        panelDanhSach.setOpaque(false);
        panelDanhSach.setBorder(BorderFactory.createTitledBorder("Danh sách khách hàng"));

        // THAY ĐỔI: Đổi "Ngày sinh" thành "Ngày sinh" (và đảm bảo dữ liệu là ngày sinh đầy đủ)
        String[] cot = {"Ghế", "Họ và tên", "Ngày sinh", "Số điện thoại", "CCCD"};
        model = new DefaultTableModel(cot, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        if (danhSachGhe != null && !danhSachGhe.isEmpty()) {
            List<ChoDat> choList = new ArrayList<>(danhSachGhe.values());
            choList.sort(Comparator.comparing(ChoDat::getMaToa).thenComparing(ChoDat::getSoCho));
            for (ChoDat c : choList) {
                Object khach = (danhSachKhach != null) ? danhSachKhach.get(c.getMaCho()) : null;

                // Lấy các trường dữ liệu như trước
                String ten = getKhachField(khach, "hoTen", String.class);
                // SỬA: Lấy trường Ngày sinh đầy đủ (ví dụ: dd/MM/yyyy) dưới dạng String
                String ngaySinhStr = getKhachField(khach, "ngaySinh", String.class); // Giả sử tên trường là "ngaySinh" và trả về String
                String sdt = getKhachField(khach, "sdt", String.class);
                String cccd = getKhachField(khach, "cccd", String.class);

                String tenStr = (ten != null && !ten.isEmpty()) ? ten : "-";
                // SỬA: Sử dụng trực tiếp chuỗi ngày sinh lấy được
                String ngaySinhHienThi = (ngaySinhStr != null && !ngaySinhStr.isEmpty()) ? ngaySinhStr : "-";
                String sdtStr = (sdt != null && !sdt.isEmpty()) ? sdt : "-";
                String cccdStr = (cccd != null && !cccd.isEmpty()) ? cccd : "-";

                // THAY ĐỔI: Thay thế tuổi/năm sinh bằng chuỗi ngày sinh
                model.addRow(new Object[]{c.getMaToa() + "-" + c.getSoCho(), tenStr, ngaySinhHienThi, sdtStr, cccdStr});
            }
        } else {
            model.addRow(new Object[]{"-", "Chưa có khách", "-", "-", "-"});
        }

        tbKhach = new JTable(model);
        tbKhach.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(tbKhach);
        sp.setPreferredSize(new Dimension(520, 160));
        panelDanhSach.add(sp, BorderLayout.CENTER);
        return panelDanhSach;
    }

    private JPanel taoPanelTongHoaDon() {
        JPanel panelTong = new JPanel(new BorderLayout());
        panelTong.setOpaque(false);
        panelTong.setBorder(BorderFactory.createTitledBorder("Tổng hóa đơn"));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        double giaVe = tinhGiaVe();
        Tong tong = tinhTongHoaDon(giaVe, khuyenMaiApDung);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        // 1. Dòng Giá vé (Subtotal)
        box.add(new JLabel("Giá vé: " + nf.format(Math.round(tong.subtotal)) + " VND"));


        lblTenKhuyenMai = new JLabel("Khuyến mãi áp dụng: " );
        // không có tác dụng sẽ được cập nhật sau
        box.add(lblTenKhuyenMai);

        // 3. Dòng Tổng tiền giảm (Discount)
        long giamGia = Math.round(tong.discount);
        // Khởi tạo lblGiamGia (Biến instance)
        lblGiamGia = new JLabel("Tổng tiền giảm: " + nf.format(giamGia) + " VND"); // Dòng này hiển thị GIÁ TRỊ GIẢM
        lblGiamGia.setForeground(Color.RED);

        // Thêm khoảng trắng và hiển thị giá trị giảm
        box.add(Box.createRigidArea(new Dimension(0, 4)));
        box.add(lblGiamGia);
        box.add(Box.createRigidArea(new Dimension(0, 4)));


        // 4. Dòng Tổng tiền phải thanh toán (Total)
        lblTongTien = new JLabel("Tổng số tiền phải thanh toán: " + nf.format(Math.round(tong.total)) + " VND");

        System.out.println( "gia ve" + nf.format(Math.round(tong.subtotal)) );

        System.out.println( "tong" + nf.format(Math.round(tong.total)) );

        lblTongTien.setFont(lblTongTien.getFont().deriveFont(Font.BOLD));
        lblTongTien.setForeground(new Color(200, 60, 10));
        box.add(Box.createRigidArea(new Dimension(0, 6)));
        box.add(lblTongTien);

        panelTong.add(box, BorderLayout.CENTER);
        return panelTong;
    }

    private JPanel taoPanelThanhToan() {
        JPanel panelThanhToan = new JPanel();
        panelThanhToan.setLayout(new BoxLayout(panelThanhToan, BoxLayout.Y_AXIS));
        panelThanhToan.setOpaque(false);
        panelThanhToan.setBorder(BorderFactory.createTitledBorder("Thanh toán"));

        // Khuyen mai: combobox lay tu DB
        JPanel dongKm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dongKm.setOpaque(false);
        dongKm.add(new JLabel("Khuyến mãi: "));

        cbKhuyenMai = new JComboBox<>();
        cbKhuyenMai.setPreferredSize(new Dimension(250, 26));
        cbKhuyenMai.setEnabled(false);
        dongKm.add(cbKhuyenMai);


        lblThongTinKhuyenMai = new JLabel(" ");
        dongKm.add(lblThongTinKhuyenMai);

        panelThanhToan.add(dongKm);

        // Khai báo Tong và NumberFormat ngay từ đầu (giá trị final hiệu quả)
        Tong t = tinhTongHoaDon(tinhGiaVe(), khuyenMaiApDung);
        long tongFinal = Math.round(t.total);
        final NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));


        JPanel dong1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dong1.setOpaque(false);
        dong1.add(new JLabel("Hình thức thanh toán: "));
        cbHinhThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});

        dong1.add(cbHinhThuc);
        panelThanhToan.add(dong1);


        cbHinhThuc.addActionListener(e -> {
            capNhatThongTinNguoiDat(); // Cập nhật hiển thị Phương thức TT
            xuLyLamTronTienMat(); // Logic làm tròn và gợi ý
        });

        JPanel dong2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dong2.setOpaque(false);

        // 1. KHỞI TẠO txtTienKhachDua VỚI TỔNG TIỀN HÓA ĐƠN BAN ĐẦU (T_HD)
        txtTienKhachDua = new JTextField(nf.format(tongFinal), 12);

        txtTienThoiLai = new JTextField(nf.format(0), 10);
        txtTienThoiLai.setEditable(false);
        dong2.add(new JLabel("Tiền khách đưa: "));
        dong2.add(txtTienKhachDua);
        dong2.add(new JLabel("Tiền thối lại: "));
        dong2.add(txtTienThoiLai);
        panelThanhToan.add(dong2);


        // =================================================================
        // START: LOGIC GỢI Ý MỚI - Đã khắc phục lỗi Lambda
        // =================================================================

        JPanel goiY = new JPanel(new FlowLayout(FlowLayout.LEFT));
        goiY.setOpaque(false);
        goiY.add(new JLabel("Gợi ý: "));
        // Tính toán giá trị làm tròn lên (Giá trị này sẽ được gán tự động khi chọn Tiền mặt)
        long tongLamTron = lamTronLen(tongFinal);

        List<Long> goiYListNew = new ArrayList<>();

        if (tongFinal > 0) {
            // 1. Gợi ý 1: Số tiền làm tròn lên theo quy tắc 50k (ví dụ: 120k -> 150k)
            if (tongLamTron > tongFinal && !goiYListNew.contains(tongLamTron)) {
                goiYListNew.add(tongLamTron);
            } else if (tongLamTron == tongFinal) {
                // Nếu đã tròn sẵn, gợi ý mệnh giá tròn lớn hơn 100k
                long lamTron100K = (long) (Math.ceil(tongFinal / 100000.0) * 100000);
                if (lamTron100K == tongFinal) {
                    // Nếu đã tròn 100k, gợi ý mệnh giá 100k tiếp theo
                    lamTron100K += 100000;
                }
                if (!goiYListNew.contains(lamTron100K)) {
                    goiYListNew.add(lamTron100K);
                }
            }

            // 2. Gợi ý 2 & 3: Mệnh giá lớn tròn chục/tròn trăm (200k, 500k, 1M,...)
            if (tongFinal < 200000 && !goiYListNew.contains(200000L)) goiYListNew.add(200000L);
            if (tongFinal < 500000 && !goiYListNew.contains(500000L)) goiYListNew.add(500000L);
            if (tongFinal < 1000000 && !goiYListNew.contains(1000000L)) goiYListNew.add(1000000L);


            // Xóa giá trị trùng và chỉ lấy 4 gợi ý
            Set<Long> set = new LinkedHashSet<>(goiYListNew);
            goiYListNew.clear();
            goiYListNew.addAll(set);
            goiYListNew.sort(Long::compare);

            goiYListNew = goiYListNew.subList(0, Math.min(goiYListNew.size(), 4));
        }

        for (Long h : goiYListNew) { // Sử dụng danh sách gợi ý đã cập nhật
            JButton b = new JButton(nf.format(h));
            b.setMargin(new Insets(4, 8, 4, 8));

            // SỬ DỤNG BIẾN NF ĐÃ KHAI BÁO FINAL BÊN NGOÀI
            b.addActionListener(e -> {
                txtTienKhachDua.setText(nf.format(h));
                capNhatTienThoi();
            });
            goiY.add(b);
        }
        panelThanhToan.add(goiY);

        // =================================================================
        // END: LOGIC GỢI Ý MỚI
        // =================================================================
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelNut.setOpaque(false);

        // NÚT HỦY (MỚI)
        btnHuy = new JButton("Hủy");
        btnHuy.setForeground(Color.BLACK); // Màu chữ đen
        btnHuy.setBackground(new Color(220, 220, 220)); // Màu nền xám nhạt
        btnHuy.setFocusPainted(false);
        btnHuy.setPreferredSize(new Dimension(140, 36));
        btnHuy.addActionListener(e -> huyBoThanhToan());
        panelNut.add(btnHuy);

        btnQuayLai = new JButton("Quay lại");
        btnQuayLai.setBackground(new Color(150, 150, 150));
        btnQuayLai.setForeground(Color.WHITE);
        btnQuayLai.setPreferredSize(new Dimension(140, 36));
        btnQuayLai.addActionListener(e -> quayLai());
        panelNut.add(btnQuayLai);

        btnXacNhan = new JButton("Xác nhận thanh toán");
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setBackground(MAU_XANH_BTN);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setPreferredSize(new Dimension(180, 36));
        btnXacNhan.addActionListener(e -> xacNhanThanhToan());
        panelNut.add(btnXacNhan);

        panelThanhToan.add(panelNut);

        txtTienKhachDua.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { capNhatTienThoi(); }
        });
        // 2. GỌI LÀM TRÒN LẦN ĐẦU (sau khi UI được tạo) ĐỂ KHỞI TẠO ĐÚNG GIÁ TRỊ THEO HÌNH THỨC TT MẶC ĐỊNH

        SwingUtilities.invokeLater(() -> xuLyLamTronTienMat());

        return panelThanhToan;
    }

    private JPanel taoPanelHoaDon() {
        JPanel panelHoaDon = new JPanel(new BorderLayout());
        panelHoaDon.setBackground(new Color(255, 255, 255));
        panelHoaDon.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false),
                new EmptyBorder(12, 12, 12, 12)));

        JLabel tieuDeHD = new JLabel("Hóa đơn mua vé");
        tieuDeHD.setFont(tieuDeHD.getFont().deriveFont(Font.BOLD, 16f));
        JPanel dau = new JPanel(new BorderLayout());
        dau.setOpaque(false);
        dau.add(tieuDeHD, BorderLayout.NORTH);

        JPanel thongTinHD = new JPanel();
        thongTinHD.setOpaque(false);
        thongTinHD.setLayout(new GridLayout(0, 2));

        // Khởi tạo các JLabel hiển thị thông tin Hóa đơn (trước khi tạo)
        lblMaHoaDon = new JLabel("");
        lblNguoiLapHD = new JLabel("");
        lblDienThoaiNV = new JLabel("");

        // Gán giá trị ban đầu cho các component
        thongTinHD.add(new JLabel("Mã hóa đơn: "));
        thongTinHD.add(lblMaHoaDon);
        thongTinHD.add(new JLabel("Người lập hóa đơn: "));
        thongTinHD.add(lblNguoiLapHD);
        thongTinHD.add(new JLabel("Điện thoại: "));
        thongTinHD.add(lblDienThoaiNV);
        dau.add(thongTinHD, BorderLayout.CENTER);

        JPanel panelNguoiDat = new JPanel();
        panelNguoiDat.setLayout(new BoxLayout(panelNguoiDat, BoxLayout.Y_AXIS));
        panelNguoiDat.setBorder(BorderFactory.createTitledBorder("Người đặt vé / Thanh toán"));
        panelNguoiDat.setOpaque(false);

        lblTenNguoiDat = new JLabel("Họ tên: [Tên khách đầu tiên]");
        lblSdtNguoiDat = new JLabel("Số điện thoại: [SĐT khách đầu tiên]");
        lblPhuongThucTT = new JLabel("Phương thức: [Hình thức TT]");

        panelNguoiDat.add(lblTenNguoiDat);
        panelNguoiDat.add(lblSdtNguoiDat);
        panelNguoiDat.add(lblPhuongThucTT);

        // Gộp tất cả vào phần NORTH
        JPanel headerWrapper = new JPanel(new BorderLayout(0, 5));
        headerWrapper.setOpaque(false);
        headerWrapper.add(dau, BorderLayout.NORTH);
        headerWrapper.add(panelNguoiDat, BorderLayout.CENTER);

        panelHoaDon.add(headerWrapper, BorderLayout.NORTH); // Thay thế dau bằng headerWrapper

        String[] cotHd = {"STT", "Mã vé", "Số lượng", "Giá vé", "Giảm giá", "Thành tiền"};
        DefaultTableModel modelHd = new DefaultTableModel(cotHd, 0);

        modelHd.addRow(new Object[]{"", "Chưa có ghế", "", "", "", ""});

// --- KẾT THÚC LOGIC ĐIỀN DỮ LIỆU ---

        tbHoaDon = new JTable(modelHd);
        tbHoaDon.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(tbHoaDon);
        sp.setPreferredSize(new Dimension(480, 300));
        panelHoaDon.add(sp, BorderLayout.CENTER);

        // =================================================================
        // FIX: TẠO FOOTER CHI TIẾT VÀ GÁN LABEL TỔNG CỘNG
        // =================================================================

        JPanel chan = new JPanel(new BorderLayout());
        chan.setOpaque(false);

        JPanel panelTongCong = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTongCong.setOpaque(false);

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        double tongVoiKM = tinhTongHoaDon(tinhGiaVe(), khuyenMaiApDung).total;

        // LABEL HIỂN THỊ TỔNG CỘNG LỚN (Đã có sẵn)
        // KHỞI TẠO BIẾN INSTANCE
        lblTongCongLon = new JLabel("Tổng cộng: " + nf.format(Math.round(tongVoiKM)));
        lblTongCongLon.setFont(lblTongCongLon.getFont().deriveFont(Font.BOLD, 14f));

        panelTongCong.add(lblTongCongLon);
        chan.add(panelTongCong, BorderLayout.NORTH); // Thêm tổng cộng (phần trên của footer)


        // --- PHẦN TIỀN VIẾT BẰNG CHỮ (Giữ chỗ) ---
        JPanel panelTienChu = new JPanel(new GridLayout(1, 1));
        panelTienChu.setOpaque(false);

        // FIX: Khởi tạo và thêm JLabel cho Tiền bằng chữ
        lblTienBangChu = new JLabel("Số tiền viết bằng chữ: ");
        lblTienBangChu.setFont(lblTienBangChu.getFont().deriveFont(Font.ITALIC));
        panelTienChu.add(lblTienBangChu);

        chan.add(panelTienChu, BorderLayout.CENTER); // Thêm dòng tiền chữ (phần giữa của footer)

        JPanel panelKetThuc = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelKetThuc.setOpaque(false);

        btnKetThuc = new JButton("Kết thúc >");
        btnKetThuc.setBackground(MAU_CAM_BTN);
        btnKetThuc.setForeground(Color.WHITE);
        btnKetThuc.setPreferredSize(new Dimension(140, 36));
        btnKetThuc.setEnabled(false);

        btnKetThuc.addActionListener(e -> ketThuc());
        panelKetThuc.add(btnKetThuc);

        chan.add(panelKetThuc, BorderLayout.SOUTH);

        panelHoaDon.add(chan, BorderLayout.SOUTH);

        // FIX: Cập nhật thông tin người lập HD ban đầu (Mock)
        if(maNV == null) {
            maNV = MA_NV_LAP_HD_MOCK;
        }
        lblNguoiLapHD.setText(tenNV);
        lblDienThoaiNV.setText(sdtNV);

        capNhatThongTinNguoiDat();
        return panelHoaDon;
    }

    /**
     * Cập nhật thông tin Người đặt vé (khách hàng đại diện) và Phương thức TT.
     */
    private void capNhatThongTinNguoiDat() {
        // Chỉ lấy Khách hàng đầu tiên làm đại diện (Khách vãng lai nếu danh sách rỗng)
        String tenKhach = "Khách Vãng Lai";
        String sdtKhach = "";
        String hinhThuc = (String) cbHinhThuc.getSelectedItem();

        if (danhSachKhach != null && !danhSachKhach.isEmpty()) {
            Object khachDauTien = danhSachKhach.values().iterator().next();
            String hoTen = getKhachField(khachDauTien, "hoTen", String.class);
            String sdt = getKhachField(khachDauTien, "sdt", String.class);

            if (hoTen != null && !hoTen.isEmpty()) tenKhach = hoTen;
            if (sdt != null && !sdt.isEmpty()) sdtKhach = sdt;
        }

        lblTenNguoiDat.setText("Họ tên: " + tenKhach);
        lblSdtNguoiDat.setText("Số điện thoại: " + sdtKhach);
        lblPhuongThucTT.setText("Phương thức: " + hinhThuc);
    }


    /**
     * Tính toán số tiền giảm giá tuyệt đối (VND) mà một KM mang lại.
     * @param km KhuyenMai cần tính.
     * @param subtotal Tổng tiền trước thuế/KM (chưa bao gồm thuế VAT).
     * @return Số tiền (double) giảm được.
     */
    private double tinhToanGiamGiaTuyetDoi(KhuyenMai km, double subtotal) {
        if (km == null || km.getGiaTriGiam() == null || km.getGiaTriGiam().doubleValue() <= 0) {
            return 0.0;
        }

        double discountValue = km.getGiaTriGiam().doubleValue();
        String loaiKM = km.getLoaiKM();

        if ("PHAN_TRAM_GIA".equals(loaiKM)) {
            // Giảm X% theo giá: subtotal * (X / 100)
            double discount = subtotal * (discountValue / 100.0);
            return Math.min(discount, subtotal); // Đảm bảo không giảm quá tổng tiền
        } else if ("CO_DINH".equals(loaiKM)) {
            // Giảm cố định Y VNĐ: Lấy giá trị cố định
            return Math.min(discountValue, subtotal); // Đảm bảo không giảm quá tổng tiền
        }
        return 0.0;
    }
    /**
     * Kiểm tra KM có thỏa mãn điều kiện áp dụng (MIN_GIA, MIN_SL) hay không.
     * @param km KhuyenMai cần kiểm tra.
     * @param subtotal Tổng tiền trước thuế.
     * @param soLuongVe Tổng số lượng vé.
     * @return true nếu thỏa mãn hoặc không có điều kiện, false nếu không thỏa mãn.
     */
    private boolean kiemTraDieuKienHopLe(KhuyenMai km, double subtotal, int soLuongVe) {
        if (km == null) return false;

        String dkApDung = km.getDkApDung();
        java.math.BigDecimal giaTriDK = km.getGiaTriDK();

        if ("NONE".equals(dkApDung) || giaTriDK == null) {
            return true; // Không có điều kiện
        }

        double dkValue = giaTriDK.doubleValue();

        if ("MIN_GIA".equals(dkApDung)) {
            // Kiểm tra tổng tiền đơn hàng có lớn hơn hoặc bằng điều kiện giá tối thiểu không
            return subtotal >= dkValue;
        }

        if ("MIN_SL".equals(dkApDung)) {
            // Kiểm tra tổng số lượng vé có lớn hơn hoặc bằng điều kiện số lượng tối thiểu không
            // Ép kiểu dkValue (DECIMAL) sang int/double
            return soLuongVe >= (int) Math.round(dkValue);
        }

        return false;
    }
    /**
     * Lọc danh sách KM hoạt động theo điều kiện Đơn hàng, và chọn KM giảm giá lớn nhất.
     * @param activePromos Danh sách KM đang hoạt động (đã được lọc theo ngày).
     * @return KhuyenMai tốt nhất, hoặc null nếu không có KM nào hợp lệ.
     */
    private KhuyenMai timKhuyenMaiTotNhat(List<KhuyenMai> activePromos) {
        if (activePromos == null || activePromos.isEmpty()) return null;

        double subtotal = tinhGiaVe();
        int soLuongVe = (danhSachGhe == null) ? 0 : danhSachGhe.size();

        bestKm = null;
        double maxDiscount = 0.0;

        for (KhuyenMai km : activePromos) {
            if (!kiemTraDieuKienHopLe(km, subtotal, soLuongVe)) {
                continue; // Bỏ qua KM không thỏa mãn
            }

            double currentDiscount = tinhToanGiamGiaTuyetDoi(km, subtotal);

            if (currentDiscount > maxDiscount) {
                maxDiscount = currentDiscount;
                bestKm = km;
            }
        }

        // Nếu KM tốt nhất chỉ giảm 0 VND (ví dụ: subtotal quá nhỏ), thì không áp dụng
        if (maxDiscount > 0.0) {
            return bestKm;
        }

        return null;
    }

    // Tai khuyen mai tu DB vao combobox VÀ TỰ ĐỘNG ÁP DỤNG KM TỐT NHẤT
    private void taiKhuyenMaiTuDB() {
        SwingUtilities.invokeLater(() -> {
            cbKhuyenMai.removeAllItems();

            // 1. Tải danh sách KM đang hoạt động (đã lọc theo ngày)
            List<KhuyenMai> activePromos = khuyenMaiDAO.layTatCaKMHoatDong();

            // 2. TÌM KM TỐT NHẤT VÀ GÁN TỰ ĐỘNG
            bestKm = timKhuyenMaiTotNhat(activePromos);

            // 3. Tải tất cả KM hoạt động (và "Không áp dụng") vào ComboBox
            KhuyenMai none = new KhuyenMai();
            none.setMaKM("");
            none.setTenKM("-- Khong ap dung --");
            cbKhuyenMai.addItem(none);

            double subtotal = tinhGiaVe();
            int soLuongVe = (danhSachGhe == null) ? 0 : danhSachGhe.size();

            if (activePromos != null) {
                for (KhuyenMai km : activePromos) {
                    kiemTraDieuKienHopLe(km, subtotal, soLuongVe);
                    cbKhuyenMai.addItem(km);
                }
            }

            // 4. ÁP DỤNG VÀ HIỂN THỊ
            if (bestKm != null) {
                khuyenMaiApDung = bestKm;
                cbKhuyenMai.setSelectedItem(bestKm); // Chọn KM tốt nhất trên ComboBox
                lblThongTinKhuyenMai.setText("Tự động áp dụng: " + bestKm.getMaKM());

                // --- LOGIC MỚI: THÔNG BÁO SỐ TIỀN GIẢM ---
                Tong tong = tinhTongHoaDon(subtotal, bestKm);
                NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

                System.out.println("---------------------------------------------------------");
                System.out.println("Tự động áp dụng KM tốt nhất: " + bestKm.getMaKM());
                System.out.println("Đơn hàng Subtotal: " + nf.format(Math.round(subtotal)) + " VND");
                System.out.println("Số tiền giảm được: " + nf.format(Math.round(tong.discount)) + " VND");
                //số tiền phải thanh toán sau KM và thuế
                System.out.println("Tổng tiền sau KM và thuế: " + nf.format(Math.round(tong.total)) + " VND");
                System.out.println("---------------------------------------------------------");

            } else {
                khuyenMaiApDung = null;
                cbKhuyenMai.setSelectedIndex(0);
                lblThongTinKhuyenMai.setText("Không có KM phù hợp để áp dụng.");
            }

            // 5. Cập nhật tổng tiền ngay sau khi áp dụng KM tự động
            capNhatTongVaGiaoDien();
        });
    }




    // Tinh tong (subtotal, discount, total)
    private Tong tinhTongHoaDon(double subtotal, KhuyenMai km) {
        Tong t = new Tong();
        t.subtotal = subtotal;
        double discount = 0; // Giá trị giảm tuyệt đối (VND)

        // 1. TÍNH GIÁ TRỊ GIẢM (DISCOUNT)
        if (km != null && km.getMaKM() != null && !km.getMaKM().isEmpty()) {
            // Phương thức này kiểm tra LoaiKM và tính ra số tiền giảm tuyệt đối
            discount = tinhToanGiamGiaTuyetDoi(km, subtotal);
            // Đảm bảo chiết khấu không vượt quá tổng tiền
            discount = Math.min(discount, subtotal);
        }

        t.discount = discount;
        t.total = Math.max(0, subtotal - discount);
        return t;
    }

    /**
     * Tính toán số tiền cần thu đã được làm tròn lên theo bội số quy định (ví dụ: 50.000).
     * @param tong Số tiền chính xác phải thanh toán.
     * @return Số tiền làm tròn (lớn hơn hoặc bằng tong).
     */
    private long lamTronLen(long tong) {
        if (tong <= 0) return 0;

        long boiSo = 50000L; // Quy tắc làm tròn: 50.000

        // Làm tròn lên theo bội số
        long tongLamTron = (long) (Math.ceil(tong / (double) boiSo) * boiSo);

        return tongLamTron;
    }

    /**
     * Xử lý làm tròn số tiền khách đưa khi chọn hình thức "Tiền mặt".
     * Tuân thủ quy tắc làm tròn lên (Ví dụ: 120.000 -> 150.000).
     */
    private void xuLyLamTronTienMat() {
        double tongPhaiThanhToan = tinhTongHoaDon(tinhGiaVe(), khuyenMaiApDung).total;
        long tongFinal = Math.round(tongPhaiThanhToan);

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        if ("Tiền mặt".equals(cbHinhThuc.getSelectedItem().toString())) {

            txtTienKhachDua.setText(nf.format(tongFinal));

            txtTienKhachDua.setEditable(true);

            capNhatTienThoi(); // Cập nhật tiền thối lại ngay lập tức
        } else {
            // Chuyển khoản/Thẻ: Mặc định là tổng tiền hóa đơn
            txtTienKhachDua.setText(nf.format(Math.round(tongPhaiThanhToan)));
            txtTienKhachDua.setEditable(false); // Không cần nhập tiền khách đưa
            txtTienThoiLai.setText(nf.format(0)); // Tiền thối lại là 0
        }
    }

    /*
     * LƯU Ý: Phương thức tinhToanGiamGiaTuyetDoi(KhuyenMai km, double subtotal)
     * phải được định nghĩa trong lớp ManHinhXacNhanBanVe để code trên hoạt động.
     * (Đã được tạo ở phản hồi trước đó)
     */

    private void capNhatTongVaGiaoDien() {
        double subtotal = tinhGiaVe();
        Tong t = tinhTongHoaDon(subtotal, khuyenMaiApDung);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        long tongTienFinal = Math.round(t.total);
        long giamGia = Math.round(t.discount);

        if (lblGiamGia != null){
            lblGiamGia.setText("Tổng tiền giảm: " + nf.format(giamGia) + " VND");
        }
        if (lblTenKhuyenMai != null) {
            String tenKm = (khuyenMaiApDung != null) ? khuyenMaiApDung.getTenKM() : "Không áp dụng";
            lblTenKhuyenMai.setText("Khuyến mãi áp dụng: " + tenKm);
        }
        if (lblTongTien != null) {
            lblTongTien.setText("Tổng số tiền phải thanh toán: " + nf.format(Math.round(t.total)) + " VND");
        }

        if (lblTongCongLon != null) {
            lblTongCongLon.setText("Tổng cộng: " + nf.format(tongTienFinal));
        }
        // GỌI HÀM LÀM TRÒN MỚI
        if (cbHinhThuc != null && txtTienKhachDua != null) {
            xuLyLamTronTienMat();
        } else {
            capNhatTienThoi(); // Nếu chưa tạo UI, gọi hàm cũ để tính toán tiền thối (nếu có)
        }
        // FIX MỚI: Cập nhật Tiền bằng chữ ngay khi tổng tiền thay đổi (trước khi thanh toán)
        if (lblTienBangChu != null) {
            lblTienBangChu.setText("Số tiền viết bằng chữ: " + docSo(tongTienFinal));
        }

        capNhatTienThoi();
    }

    private double tinhGiaVe() {
        if (danhSachGiaVe == null || danhSachGiaVe.isEmpty()) {
            return 0;
        }
        double subtotal = 0;
        for (Long gia : danhSachGiaVe.values()) {
            if (gia != null) {
                subtotal += gia.doubleValue();
            }
        }
        return subtotal;
    }

    private void capNhatTienThoi() {
        try {
            String raw = txtTienKhachDua.getText().replaceAll("[^0-9]", "");
            if (raw.isEmpty()) {
                txtTienThoiLai.setText("0");
                return;
            }
            long given = Long.parseLong(raw);
            double subtotal = tinhGiaVe();
            Tong t = tinhTongHoaDon(subtotal, khuyenMaiApDung);
            long tong = Math.round(t.total);
            long change = given - tong;
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            txtTienThoiLai.setText(nf.format(Math.max(0, change)));
        } catch (Exception ex) {
            txtTienThoiLai.setText("0");
        }
    }

    // Trong capNhatChiTietHoaDonUI

    /**
     * Cập nhật Bảng Chi tiết Hóa đơn và Footer sau khi giao dịch thành công.
     * @param hoaDon Đối tượng HoaDon đã được lưu.
     * @param danhSachVe Danh sách VeCuaBanVe đã có mã vé và giá cuối cùng.
     */
    private void capNhatChiTietHoaDonUI(HoaDon hoaDon, List<Ve> danhSachVe) {
        if (tbHoaDon == null || !(tbHoaDon.getModel() instanceof DefaultTableModel)) return;

        DefaultTableModel modelHd = (DefaultTableModel) tbHoaDon.getModel();
        modelHd.setRowCount(0); // Xóa dữ liệu cũ

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        long tongTienThanhToan = 0L;
        int stt = 1;

        // TÍNH TOÁN CÁC THÔNG SỐ CẦN THIẾT CHO VIỆC HIỂN THỊ
        Tong tongKetQua = tinhTongHoaDon(tinhGiaVe(), khuyenMaiApDung);
        double tongGiamTuyetDoi = tongKetQua.discount;
        double tongSubtotal = tongKetQua.subtotal;

        // Tạo Map để tra cứu giá gốc (subtotal) của từng vé trước khi áp dụng KM tổng
        // (Đây là giá trị trong danhSachGiaVe)
        Map<String, Long> giaGocCuaVe = danhSachGiaVe;

        // 1. Lặp qua danh sách vé và điền vào bảng
        for (Ve ve : danhSachVe) {

            // Lấy giá trị thanh toán cuối cùng của vé (đã có trong Entity Ve sau giao dịch)
            long giaVeThanhToan = (long) ve.getGiaVe();

            // Tìm giá gốc của vé (từ map danhSachGiaVe)
            long giaGoc = giaGocCuaVe.getOrDefault(ve.getMaChoDat(), Long.valueOf(giaVeThanhToan));

            // Tính toán khoản giảm giá đã phân bổ: Giá Gốc - Giá Thanh Toán
            long giamGiaPhanBo = giaGoc - giaVeThanhToan;

            // Tính lại tổng tiền thanh toán (cần thiết nếu có sai số làm tròn)
            tongTienThanhToan += giaVeThanhToan;

            modelHd.addRow(new Object[]{
                    stt++,
                    ve.getMaVe(),
                    1,
                    nf.format(giaGoc), // Giá vé (trước KM tổng)
                    nf.format(giamGiaPhanBo), // Giảm giá KM đã phân bổ
                    nf.format(giaVeThanhToan) // Thành tiền (cuối cùng)
            });
        }

        // --- 2. Cập nhật Footer ---

        // Tổng tiền cuối cùng (có thể sử dụng tongKetQua.total hoặc tongTienThanhToan)
        long tongTienFinal = Math.round(tongKetQua.total);

        // GỌI HÀM CHUYỂN SỐ THÀNH CHỮ
        String tienBangChu = docSo(tongTienFinal);

        // Cập nhật nhãn Tổng cộng lớn (Panel phải, footer)
        // Cần tìm lại JLabel lblTongCongLon (Giả định nó là một thành phần của UI)
        // Do lblTongCongLon không phải là biến instance, ta cần sửa lại phần này
        // Tạm thời bỏ qua việc cập nhật lblTongCongLon, chỉ tập trung vào lblTienBangChu
        // Ghi chú: Cần kiểm tra lại cấu trúc taoPanelHoaDon để biết cách cập nhật lblTongCongLon

        // FIX: Cập nhật Tiền viết bằng chữ
        if (lblTienBangChu != null) {
            lblTienBangChu.setText("Số tiền viết bằng chữ: " + tienBangChu);
        }

        // FIX: Cập nhật hiển thị Tổng tiền lớn trong footer Hóa đơn
        // Do không thể truy cập lblTongCongLon, ta phải tìm cách khác.
        // TẠM THỜI: Sửa lại tên JLabel trong taoPanelHoaDon() thành biến instance.

        // 3. Vô hiệu hóa nút Thanh toán và Khuyến mãi
        if (btnHuy != null) btnHuy.setEnabled(false);
        if (btnXacNhan != null) btnXacNhan.setEnabled(false);
        if (btnQuayLai != null) btnQuayLai.setEnabled(false);
        if (cbHinhThuc != null) cbHinhThuc.setEnabled(false);
        if (btnKetThuc != null) btnKetThuc.setEnabled(true);
        if (lblTongCongLon != null) {
            lblTongCongLon.setText("Tổng cộng: " + nf.format(tongTienFinal));
        }
    }

    // Mảng chữ số từ 0 đến 9
    private static final String[] chuSo = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};

    // Mảng hàng đơn vị lớn
    private static final String[] donVi = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ"};

    /**
     * Phương thức Helper: Đọc một nhóm 3 chữ số (000-999) thành chữ.
     * @param soBaChuSo Số (0-999) cần đọc.
     * @return Chuỗi chữ tiếng Việt.
     */
    private  String docBaSo(int soBaChuSo) {
        if (soBaChuSo == 0) return chuSo[0];

        int tram = soBaChuSo / 100;
        int chuc = (soBaChuSo % 100) / 10;
        int donViLe = soBaChuSo % 10;

        String ketQua = "";

        // Đọc hàng trăm
        if (tram > 0) {
            ketQua += chuSo[tram] + " trăm";
        }

        // Đọc hàng chục và đơn vị
        if (chuc > 1) {
            ketQua += (tram > 0 || ketQua.isEmpty() ? " " : "") + chuSo[chuc] + " mươi";
            if (donViLe > 0) {
                if (donViLe == 5) {
                    ketQua += " lăm"; // ví dụ: hai mươi lăm
                } else if (donViLe == 1) {
                    ketQua += " mốt"; // ví dụ: hai mươi mốt
                } else {
                    ketQua += " " + chuSo[donViLe];
                }
            }
        } else if (chuc == 1) {
            ketQua += (tram > 0 || ketQua.isEmpty() ? " " : "") + "mười";
            if (donViLe > 0) {
                if (donViLe == 5) {
                    ketQua += " lăm"; // ví dụ: mười lăm
                } else {
                    ketQua += " " + chuSo[donViLe];
                }
            }
        } else { // chuc == 0
            if (tram > 0 && (donViLe > 0)) {
                ketQua += " lẻ"; // ví dụ: một trăm lẻ năm
            }
            if (donViLe > 0) {
                ketQua += (ketQua.isEmpty() ? "" : " ") + chuSo[donViLe];
            }
        }

        return ketQua.trim();
    }


    /**
     * Phương thức chính: Đọc một số nguyên lớn (long) thành chuỗi tiếng Việt.
     * @param number Số tiền (long) cần đọc (ví dụ: 2420000).
     * @return Chuỗi tiền tệ tiếng Việt (ví dụ: "hai triệu bốn trăm hai mươi nghìn đồng").
     */
    public  String docSo(long number) {
        if (number == 0) return "Không đồng";
        if (number < 0) return "Âm " + docSo(-number); // Xử lý số âm

        String s = String.valueOf(number);
        int length = s.length();
        int soNhom = (length + 2) / 3;

        // Chia số thành các nhóm 3 chữ số (từ phải sang trái)
        List<String> nhomSo = new ArrayList<>();
        for (int i = 0; i < soNhom; i++) {
            int start = Math.max(0, length - (i + 1) * 3);
            int end = length - i * 3;
            nhomSo.add(s.substring(start, end));
        }

        String ketQua = "";
        boolean isPreviousGroupZero = false;

        // Đọc từng nhóm 3 số
        for (int i = 0; i < soNhom; i++) {
            int val = Integer.parseInt(nhomSo.get(i));

            // Xử lý nhóm 3 số
            String chu = docBaSo(val);

            // Nếu không phải nhóm cuối cùng (đơn vị lớn hơn 0)
            if (!chu.isEmpty() && i < donVi.length) {
                ketQua = chu + " " + donVi[i] + (ketQua.isEmpty() ? "" : " ") + ketQua;
                isPreviousGroupZero = false;
            } else if (val == 0) {
                isPreviousGroupZero = true; // Nhóm 000
            }
        }

        // Loại bỏ các khoảng trắng thừa và thêm "đồng"
        return (ketQua.trim().substring(0, 1).toUpperCase() + ketQua.trim().substring(1) + " đồng").replaceAll("\\s+", " ");
    }

    // =====================================================================================
// PHƯƠNG THỨC XỬ LÝ KHÁCH HÀNG (B1)
// =====================================================================================
    /**
     * Xử lý Khách hàng: Tìm kiếm, tạo mới (nếu chưa có), và trả về Map các Entity KhachHang đã hoàn chỉnh MaKH.
     *
     * @param maKhachHangDaiDienOut (Output) Biến array 1 phần tử để chứa MaKhachHang đại diện cho Hóa đơn.
     * @return Map<MaCho, KhachHang entity đã xử lý>.
     * @throws SQLException Nếu có lỗi CSDL khi tạo mã KH.
     */
    private Map<String, KhachHang> xuLyVaTaoKhachHangEntities(String[] maKhachHangDaiDienOut) throws SQLException {
        Map<String, KhachHang> danhSachKhachHangEntity = new HashMap<>();

        // Khởi tạo danh sách khách hàng MỚI cần được INSERT vào DB trong transaction
        List<KhachHang> danhSachKhachHangMoiCanLuu = new ArrayList<>();

        // Nếu không có khách nào, trả về Map rỗng
        if (danhSachKhach == null || danhSachKhach.isEmpty()) {
            maKhachHangDaiDienOut[0] = "KHVL001"; // Gán mặc định
            return danhSachKhachHangEntity;
        }
        // [FIX START] TÌM STT LỚN NHẤT MỘT LẦN VÀ DÙNG BIẾN ĐẾM TẠM
        LocalDate homNay = LocalDate.now();
        // Lấy giá trị số nguyên lớn nhất (VD: 10)
        int currentSTT = khachHangDAO.getLastKhachHangSTTValue(homNay);
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));
        // [FIX END]

        // Lặp qua danh sách khách hàng từ UI để tạo/tìm Entity KhachHang
        for (Map.Entry<String, Object> entry : danhSachKhach.entrySet()) {
            Object khach = entry.getValue();

            // Trích xuất các trường dữ liệu thô
            String cccd = getKhachField(khach, "cccd", String.class);
            String sdt = getKhachField(khach, "sdt", String.class);
            String hoTen = getKhachField(khach, "hoTen", String.class);
            LocalDate ngaySinh = getKhachField(khach, "ngaySinh", LocalDate.class);
            String gioiTinh = getKhachField(khach, "gioiTinh", String.class); // Thêm nếu cần

            if (ngaySinh == null) {
                String ngaySinhStr = getKhachField(khach, "ngaySinh", String.class);
                if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
                    try {
                        // Chuyển đổi từ String (dd/MM/yyyy) sang LocalDate
                        ngaySinh = LocalDate.parse(ngaySinhStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (Exception e) {
                        System.err.println("Cảnh báo: Không thể parse Ngày sinh từ chuỗi '" + ngaySinhStr + "'. Dùng null.");
                        ngaySinh = null;
                    }
                }
            }

            KhachHang khachHangDaTonTai = null;
            // Chỉ tìm khi có CCCD
            if (cccd != null && !cccd.isEmpty()) {
                khachHangDaTonTai = khachHangDAO.getKhachHangByCccd(cccd);
            }

            KhachHang khachHangCanLuu;

            if (khachHangDaTonTai == null) {
                currentSTT++;
                String soThuTuStr = String.format("%04d", currentSTT);
                String maKHNew = "KH" + ngayStr + soThuTuStr;

                System.out.println("Tạo mã mới: " + maKHNew);

                khachHangCanLuu = new KhachHang(
                        maKHNew,
                        hoTen,
                        cccd,
                        ngaySinh,
                        sdt
                );

                // THÊM VÀO DANH SÁCH CẦN LƯU
                danhSachKhachHangMoiCanLuu.add(khachHangCanLuu);
            } else {
                khachHangCanLuu = khachHangDaTonTai;

                // BỔ SUNG: Cập nhật các thuộc tính mới nhận từ form vào Entity cũ
                khachHangCanLuu.setHoTen(hoTen);
                khachHangCanLuu.setNgaySinh(ngaySinh);
                khachHangCanLuu.setSdt(sdt);
            }

            danhSachKhachHangEntity.put(entry.getKey(), khachHangCanLuu);

            // Gán Mã KH đại diện: Mã KH của người đầu tiên trong Map
            if (maKhachHangDaiDienOut[0] == null) {
                maKhachHangDaiDienOut[0] = khachHangCanLuu.getMaKH();
            }
        }

        // Nếu danh sách không rỗng nhưng chưa gán được MaKH đại diện (lỗi logic), dùng KHVL001
        if (maKhachHangDaiDienOut[0] == null) {
            maKhachHangDaiDienOut[0] = "KHVL001";
        }
        // In danh sách KhachHang Entity để kiểm tra
        inDanhSachKhachHangEntity(danhSachKhachHangEntity);

        return danhSachKhachHangEntity;
    }

    private void inDanhSachKhachHangEntity(Map<String, KhachHang> danhSachKhachHangEntity) {

        System.out.println("--- Bắt đầu in Danh sách Khách hàng Entity ---");

        // Lặp qua toàn bộ Map
        danhSachKhachHangEntity.forEach((maGhe, khachHangEntity) -> {

            // In toàn bộ Map: key (Mã ghế) và value (Đối tượng Khách hàng)
            System.out.println("Danh sách khách hàng thực thể Ghế [" + maGhe + "]: " + khachHangEntity.toString());

            // Kiểm tra cụ thể trường 'maKhachHang' (Sử dụng getMaKH() thay vì getKhachField)
            String maKhach = khachHangEntity.getMaKH(); // Giả định phương thức getter là getMaKH()

            System.out.println("  -> MaKhachHang trích xuất: " + maKhach);
        });

        System.out.println("--- Kết thúc in Danh sách Khách hàng Entity ---");
    }

    // =====================================================================================
// PHƯƠNG THỨC TẠO ENTITY VÀ GỌI TRANSACTION (B2, B3, B4)
// =====================================================================================

    /**
     * Thực hiện toàn bộ giao dịch bán vé trong Transaction (Lưu KH, HD, Vé).
     * Đã sửa logic phân bổ chiết khấu dựa trên tổng giá trị giảm tuyệt đối (discount).
     * * @param danhSachKhachHangEntity Map<MaCho, KhachHang> đã tìm/tạo Entity.
     * @param maKhachHangDaiDienStr Mã KH đại diện cho Hóa đơn.
     * @param khachHangDaiDienObj Đối tượng KhachHang Entity đại diện.
     */
    private void thucHienGiaoDichBanVe(Map<String, KhachHang> danhSachKhachHangEntity, String maKhachHangDaiDienStr, KhachHang khachHangDaiDienObj) {

        // --- B1: Lấy và Chuẩn bị Dữ liệu ---
        String maHD = taoMaHoaDon();

        Tong tongKetQua = tinhTongHoaDon(tinhGiaVe(), khuyenMaiApDung);
        double tongTienPhaiThanhToan = tongKetQua.total;
        String phuongThuc = cbHinhThuc.getSelectedItem().toString();
        String loaiHoaDon = "Bán vé trực tiếp";
        String maNVLap = maNV;

        // Kiểm tra tiền khách đưa
        try {
            String raw = txtTienKhachDua.getText().replaceAll("[^0-9]", "");
            long tienKhachDua = Long.parseLong(raw);
            if (tienKhachDua < Math.round(tongTienPhaiThanhToan)) {
                JOptionPane.showMessageDialog(this, "Tiền khách đưa không đủ để thanh toán.", "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền khách đưa hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // --- B2: Tạo thực thể Hóa đơn (HoaDon) ---
        HoaDon hoaDon = new HoaDon(
                maHD,
                maKhachHangDaiDienStr, // Mã KH đã được xác định/tìm thấy
                maNVLap,
                khuyenMaiApDung != null ? khuyenMaiApDung.getMaKM() : null,
                Math.round(tongTienPhaiThanhToan),
                java.time.LocalDateTime.now(),
                phuongThuc,
                loaiHoaDon
        );

        // --- B3: Tạo danh sách thực thể Vé (Ve) và Phân bổ Chiết khấu ---
        List<Ve> danhSachVeMoi = new ArrayList<>();

        double tongGiamTuyetDoi = tongKetQua.discount;
        double tongSubtotal = tongKetQua.subtotal;
        // --- LOGIC MỚI: TÍNH TOÁN GIẢM GIÁ ĐỀU CHO TỪNG VÉ (nếu là KM cố định) ---
        double giamGiaCoDinhTrenMoiVe = 0.0;
        int soLuongVe = danhSachGhe.size();
        boolean laKhuyenMaiCoDinh = khuyenMaiApDung != null && "CO_DINH".equals(khuyenMaiApDung.getLoaiKM());

        if (laKhuyenMaiCoDinh && soLuongVe > 0) {
            // Trường hợp 1: Giảm giá cố định (ví dụ: 50K), chia đều cho N vé
            giamGiaCoDinhTrenMoiVe = tongGiamTuyetDoi / soLuongVe;
        }
        double tongGiamDaPhanBo = 0.0;
        int count = 0; // Đếm số vé đang xử lý

        for (Map.Entry<String, ChoDat> entry : danhSachGhe.entrySet()) {
            ChoDat cho = entry.getValue();
            count++;
            // Lấy KhachHang Entity đã xử lý
            KhachHang khachHangChoVe = danhSachKhachHangEntity.get(entry.getKey());

            // Lấy giá cơ bản đã có chiết khấu loại vé
            Long giaVeBaseLong = danhSachGiaVe.get(cho.getMaCho());
            long giaVeBase = 0L;
            if (giaVeBaseLong != null) {
                giaVeBase = giaVeBaseLong.longValue();
            } else {
                System.err.println("LỖI LOGIC: Không tìm thấy giá vé cho ghế: " + cho.getMaCho());
                continue; // Bỏ qua ghế này
            }

            // --- B3.1: PHÂN BỔ CHIẾT KHẤU KHUYẾN MÃI CHO TỪNG VÉ ---
            double giaSauKhuyenMai;
            double giaTriGiamChoVeNay = 0.0;

            if (tongGiamTuyetDoi > 0) {
                if (laKhuyenMaiCoDinh) {
                    // TRƯỜNG HỢP 1: KM Cố định (50K): Chia đều cho từng vé

                    if (count < soLuongVe) {
                        giaTriGiamChoVeNay = giamGiaCoDinhTrenMoiVe;
                        tongGiamDaPhanBo += giaTriGiamChoVeNay;
                    } else {
                        // Vé cuối cùng: Phân bổ phần còn lại để xử lý sai số làm tròn
                        giaTriGiamChoVeNay = tongGiamTuyetDoi - tongGiamDaPhanBo;
                    }

                } else {
                    // TRƯỜNG HỢP 2: KM Phần trăm (10%): Phân bổ theo tỷ lệ giá trị
                    if (tongSubtotal > 0) {
                        if (count < soLuongVe) {
                            // Tính chiết khấu theo tỷ lệ giá trị: (Giá vé/Tổng Subtotal) * Tổng Giảm Tuyệt Đối
                            double tyLeGiaVe = (double) giaVeBase / tongSubtotal;
                            giaTriGiamChoVeNay = tongGiamTuyetDoi * tyLeGiaVe;
                            tongGiamDaPhanBo += giaTriGiamChoVeNay;
                        } else {
                            // Vé cuối cùng: Phân bổ phần còn lại để xử lý sai số làm tròn
                            giaTriGiamChoVeNay = tongGiamTuyetDoi - tongGiamDaPhanBo;
                        }
                    } else {
                        // Tổng subtotal bằng 0, không giảm gì thêm
                        giaTriGiamChoVeNay = 0.0;
                    }
                }

                giaSauKhuyenMai = giaVeBase - giaTriGiamChoVeNay;

            } else {
                // Không áp dụng Khuyến mãi
                giaSauKhuyenMai = giaVeBase;
            }
            // Đảm bảo giá vé sau KM không âm
            giaSauKhuyenMai = Math.max(0, giaSauKhuyenMai);

            // --- B3.2: TẠO ENTITY VÉ ---
            // Lấy MaKhachHang đã được xác định từ Entity
            String maKhach = khachHangChoVe.getMaKH();

            // Lấy MaLoaiVe
            Object khachTho = danhSachKhach.get(cho.getMaCho());
            String maLoaiVe = (khachTho != null) ? getKhachField(khachTho, "maLoaiVe", String.class) : "VT01";


            // Tạo đối tượng Ve
            Ve ve = new Ve(
                    null, // MaVe sẽ được tạo tự động trong DAO
                    maChuyen,
                    cho.getMaCho(),
                    maNVLap,
                    maKhach,
                    maLoaiVe,
                    Math.round(giaSauKhuyenMai), // SỬ DỤNG GIÁ SAU K.MÃI
                    "DA_BAN"
            );
            System.out.println("Thông tin của vé trước giao dịch: "+ ve.toString());
            danhSachVeMoi.add(ve);
        }

        // --- B4: Gọi DAO để thực hiện Transaction ---
        try {
            boolean success = veBVDao.banVeTrongTransaction(hoaDon, danhSachVeMoi, danhSachKhachHangEntity);
            System.out.println("Kết quả của hóa đơn " + hoaDon.toString());

            if (success) {
                // 1. Cập nhật header (Mã HD, người lập)
                hienThiThongTinHoaDon(hoaDon);

                // 2. Cập nhật bảng chi tiết và footer
                capNhatChiTietHoaDonUI(hoaDon, danhSachVeMoi);

                // 3. Vô hiệu hóa form bên trái (nút xác nhận, tiền khách đưa, v.v.)
                btnXacNhan.setEnabled(false);
                txtTienKhachDua.setEditable(false);

                System.out.println("Số lượng vé được thêm " + danhSachVeMoi.size());
                for (Ve ve : danhSachVeMoi){
                    System.out.println("Mã vé được tạo: " + ve.getMaVe());
                }
                System.out.println("Đã thêm được hóa đơn: " + maHD);

                // Xây dựng thông báo chi tiết
                StringBuilder sb = new StringBuilder("Thanh toán thành công!\n");
                sb.append("Mã Hóa đơn: ").append(maHD).append("\n");
                sb.append("--- Danh sách Mã Vé đã tạo ---\n");

                // Lặp qua danh sách Entity đã được cập nhật MaVe từ DAO
                for (Ve ve : danhSachVeMoi) {
                    sb.append(ve.getMaVe()).append(" (Ghế: ").append(ve.getMaChoDat()).append(")\n");
                    System.out.println("Thông tin của vé sau giao dịch: "+ ve.toString());
                }

                JOptionPane.showMessageDialog(this,
                        sb.toString(),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(this, "Giao dịch không hoàn tất. Vui lòng kiểm tra lại.", "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: Không thể hoàn tất giao dịch. " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    /**
     * Cập nhật các JLabel hiển thị thông tin hóa đơn sau khi transaction thành công.
     * @param hd Đối tượng HoaDon đã được lưu vào CSDL.
     */
    public void hienThiThongTinHoaDon(HoaDon hd) {
        if (hd != null) {
            lblMaHoaDon.setText(hd.getMaHD());
            lblMaHoaDon.setForeground(MAU_XANH_BTN);
        }
    }


    // =====================================================================================
// PHƯƠNG THỨC GỐC ĐƯỢC CẬP NHẬT
// ====================================================================================

    private void xacNhanThanhToan() {
        String[] maKhachHangDaiDienArr = new String[1]; // Biến hứng Mã KH đại diện
        Map<String, KhachHang> danhSachKhachHangEntity;

        try {
            // BƯỚC 1: Xử lý và tạo Entity Khách hàng
            danhSachKhachHangEntity = xuLyVaTaoKhachHangEntities(maKhachHangDaiDienArr);

            // FIX: Trích xuất MaKH đại diện và tìm Entity tương ứng
            String maKhachHangDaiDienStr = maKhachHangDaiDienArr[0];

// FIX: Xác định đối tượng KhachHang Entity đại diện đầy đủ
            KhachHang khachHangDaiDienObj;

            if (maKhachHangDaiDienStr.equals("KHVL001")) {
                // Nếu là khách vãng lai, tạo đối tượng KHVL001 mặc định.
                // CẦN ĐẢM BẢO constructor này khớp với lớp KhachHang của bạn (6 tham số giả định).
                khachHangDaiDienObj = new KhachHang(
                        "KHVL001",
                        "Khách Vãng Lai",
                        "000000000000", // CCCD mặc định
                        LocalDate.of(1900, 1, 1), // Ngày sinh mặc định
                        "00000000",// SĐT mặc định
                        null
                );
            } else {
                // Nếu là khách đã có dữ liệu, lấy đối tượng Entity đã được tạo/tìm thấy trong vòng lặp xử lý khách hàng.
                // Vì đây là khách đầu tiên, ta có thể tìm nó trong Map danhSachKhachHangEntity.
                // Ta lấy khách hàng bất kỳ từ Map, vì chúng ta đang giả định tất cả đều là một giao dịch
                khachHangDaiDienObj = danhSachKhachHangEntity.values().iterator().next();
            }



            // BƯỚC 2, 3, 4: Tạo Entity, gọi Transaction
            thucHienGiaoDichBanVe(danhSachKhachHangEntity, maKhachHangDaiDienStr, khachHangDaiDienObj);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL nghiêm trọng: " + e.getMessage(), "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Hành động cho nút "Kết thúc >" (Chuyển về trang chủ sau khi thanh toán xong).
     */
    private void ketThuc() {
        resetAllData();

        resetManHinhBanVeChinh();

        // 2. Chuyển Panel
        chuyenManHinh("trangChu");
    }

    /**
     * Helper: Truy cập BanVeDashboard và gọi resetAllData() của ManHinhBanVe.
     * Phương thức này giả định ManHinhBanVe có phương thức public resetAllData().
     */
    private void resetManHinhBanVeChinh() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof BanVeDashboard || w instanceof AdminFullDashboard) {

            // 2. Khai báo một tham chiếu chung tới ManHinhBanVeInstance
            ManHinhBanVe manHinhBanVeDeReset = null;

            if (w instanceof BanVeDashboard) {
                BanVeDashboard dashboard = (BanVeDashboard) w;
                manHinhBanVeDeReset = dashboard.manHinhBanVeInstance;

            } else if (w instanceof AdminFullDashboard) {
                AdminFullDashboard dashboard = (AdminFullDashboard) w;
                manHinhBanVeDeReset = dashboard.manHinhBanVeInstance;
            }

            // 3. Thực hiện reset dữ liệu
            if (manHinhBanVeDeReset != null) {
                manHinhBanVeDeReset.resetAllData();
                System.out.println("Đã gọi resetAllData() thành công trên Dashboard " + w.getClass().getSimpleName());

                // --- Thêm logic chuyển màn hình (Nếu cần) ---
                // Ví dụ: Sau khi reset thì quay về màn hình Bán vé
                if (w instanceof BanVeDashboard) {
                    ((BanVeDashboard) w).chuyenManHinh("banVeMoi");
                } else if (w instanceof AdminFullDashboard) {
                    ((AdminFullDashboard) w).chuyenManHinh("banVeMoi");
                }

            } else {
                System.err.println("LỖI: manHinhBanVeInstance trong Dashboard là NULL. Không thể reset.");
            }

        } else {
            System.err.println("LỖI: Cửa sổ cha không phải là BanVeDashboard hay AdminFullDashboard.");
        }
    }

    /**
     * Lấy số hiệu ca làm việc hiện tại (Mặc định là "01").
     * Cần thay thế bằng logic truy vấn thực tế.
     * @return Chuỗi 2 ký tự đại diện cho số hiệu ca.
     */
    private String getSoHieuCaHienTai() {
        // TODO: Thay thế bằng logic truy vấn thực tế.
        return "01";
    }

    /**
     * Tạo mã Hóa đơn mới theo quy tắc: HD[CC][DDMMYY][MaNV][STT].
     * @return Mã Hóa đơn mới, ví dụ: HD0127102500010001
     */
    private String taoMaHoaDon() {
        try {
            String soHieuCa = getSoHieuCaHienTai();
            String maNVDayDu = maNV;

            // Trích xuất 4 ký tự cuối của Mã NV (V001)
            String maNVRutGon;
            if (maNVDayDu != null && maNVDayDu.length() >= 4) {
                maNVRutGon = maNVDayDu.substring(maNVDayDu.length() - 4);
            } else {
                maNVRutGon = "0000";
            }

            String ngayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));

            // Tiền tố đầy đủ (HD01251027V001)
            String maHdPatternPrefixDayDu = "HD" + soHieuCa + ngayStr + maNVRutGon;

            // 1. GỌI DAO ĐỂ TÌM MÃ ĐẦY ĐỦ LỚN NHẤT
            String lastMaHD = HoaDonDAO.getLastMaHoaDonByPrefix(maHdPatternPrefixDayDu);
            int nextNumber = 1;

            if (lastMaHD != null) {
                // 2. TRÍCH XUẤT STT TỪ MÃ ĐẦY ĐỦ (lấy 4 ký tự cuối)
                try {
                    String lastSTTStr = lastMaHD.substring(lastMaHD.length() - 4);
                    nextNumber = Integer.parseInt(lastSTTStr) + 1; // 0004 -> 5
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.err.println("Lỗi trích xuất STT từ Mã HD: " + lastMaHD + ". Bắt đầu lại từ 1.");
                    nextNumber = 1;
                }
            }

            // Định dạng số thứ tự (0005)
            String soThuTuStr = String.format("%04d", nextNumber);

            // Gộp và trả về mã mới (Ví dụ: HD0128102500010005)
            return maHdPatternPrefixDayDu + soThuTuStr;

        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi tạo Mã Hóa Đơn: " + e.getMessage());
            return "HDERR" + System.currentTimeMillis();
        }
    }
//tong là đại diện cho subtotal, discount, total của hóa đơn
    private static class Tong {
        double subtotal;
        double discount;
        double total;
    }

    private void huyBoThanhToan() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn hủy toàn bộ dữ liệu và quay về Trang chủ?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // 1. Reset dữ liệu của màn hình hiện tại
        resetAllData();
        // 2. Chuyển Panel
        chuyenManHinh("trangChu");
    }

    /**
     * Hành động cho nút "Quay lại" (Về màn hình bán vé chính).
     */
    private void quayLai() {
        // NOTE: Giả định Panel bán vé chính là ManHinhBanVe

        // KHÔNG reset dữ liệu ở đây, dữ liệu phải được giữ lại cho màn hình trước.
        resetAllData();

        chuyenManHinh("manHinhBanVe");
    }

    /**
     * Helper chung để chuyển đổi giữa các Panel trong Dashboard.
     * Phương thức này được tối ưu để hoạt động trên cả BanVeDashboard và AdminFullDashboard
     * mà không cần sử dụng Interface, bằng cách kiểm tra kiểu tường minh.
     */
    private void chuyenManHinh(String cardName) {
        Window w = SwingUtilities.getWindowAncestor(this);

        // Xác định tên card đích cuối cùng dựa trên Dashboard
        String finalCardName = cardName;
        String dashboardType = null;

        if (w instanceof BanVeDashboard) {
            dashboardType = "BanVe";
        } else if (w instanceof AdminFullDashboard) {
            dashboardType = "Admin";
        }

        if (dashboardType != null) {
            // 1. Chuẩn hóa tên card:
            if ("trangChu".equals(cardName)) {
                // Nếu muốn về trang chủ, phải dùng tên card chính xác của Dashboard
                finalCardName = ("Admin".equals(dashboardType)) ? "trangChuQL" : "trangChuNV";
            } else if ("manHinhBanVe".equals(cardName)) {
                // Đảm bảo tên card Bán vé đúng
                finalCardName = "banVeMoi";
            }

            // 2. Xử lý chuyển màn hình:
            if ("BanVe".equals(dashboardType)) {
                BanVeDashboard dashboard = (BanVeDashboard) w;

                // Chỉ thêm/cập nhật nếu là Trang chủ (vì Trang chủ thường cần refresh)
                if ("trangChuNV".equals(finalCardName)) {
                    dashboard.themHoacCapNhatCard(new ManHinhTrangChuNVBanVe(null), finalCardName);
                }

                dashboard.chuyenManHinh(finalCardName);

            } else if ("Admin".equals(dashboardType)) {
                AdminFullDashboard dashboard = (AdminFullDashboard) w;

                // Chỉ thêm/cập nhật nếu là Trang chủ
                if ("trangChuQL".equals(finalCardName)) {
                    // Giả định AdminFullDashboard dùng ManHinhDashboardQuanLy làm trang chủ
                    dashboard.themHoacCapNhatCard(new ManHinhDashboardQuanLy(), finalCardName);
                }

                // Chuyển màn hình bằng phương thức đã được chuẩn hóa
                dashboard.chuyenManHinh(finalCardName);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: Không tìm thấy Dashboard hợp lệ để chuyển hướng hệ thống.",
                    "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void resetAllData() {
        // 1. Dọn dẹp dữ liệu nội bộ
        if (danhSachGhe != null) danhSachGhe.clear();
        if (danhSachKhach != null) danhSachKhach.clear();
        if (danhSachGiaVe != null) danhSachGiaVe.clear();
        khuyenMaiApDung = null;

        // 2. Reset UI Components (Nếu cần)
        if (model != null) model.setRowCount(0); // Dọn dẹp bảng khách hàng
        if (tbHoaDon != null && tbHoaDon.getModel() instanceof DefaultTableModel) {
            ((DefaultTableModel) tbHoaDon.getModel()).setRowCount(0);
        }

        // 3. Cập nhật các JLabel về trạng thái ban đầu
        lblMaHoaDon.setText("");
        lblMaHoaDon.setForeground(Color.BLACK);
        lblNguoiLapHD.setText("");


        capNhatTongVaGiaoDien(); // Tính toán lại tổng tiền (sẽ là 0)
    }

    // BỔ SUNG TRONG ManHinhXacNhanBanVe.java (Ngoại trừ phương thức, ví dụ: ở dưới cùng)

    // Định nghĩa Class/Record giả lập (phải khớp với cấu trúc bạn đang dùng)
    private static final class ChiTietKhachMock { // Dùng Class/final Class nếu Record bị giới hạn
        private final ChoDat choDat;
        private final String maLoaiVe;
        private final String hoTen;
        private final String cccd;
        private final String sdt;
        private final LocalDate ngaySinh;

        // Constructor đầy đủ
        public ChiTietKhachMock(ChoDat choDat, String maLoaiVe, String hoTen, String cccd, String sdt, LocalDate ngaySinh) {
            this.choDat = choDat;
            this.maLoaiVe = maLoaiVe;
            this.hoTen = hoTen;
            this.cccd = cccd;
            this.sdt = sdt;
            this.ngaySinh = ngaySinh;
        }
        // Cần thêm getters nếu logic UI dùng chúng (choDat(), maLoaiVe(), ...)
        public ChoDat choDat() { return choDat; }
        public String maLoaiVe() { return maLoaiVe; }
        public String hoTen() { return hoTen; }
        public String cccd() { return cccd; }
        public String sdt() { return sdt; }
        public LocalDate ngaySinh() { return ngaySinh; }
    }

// ...
// BỔ SUNG TRONG ManHinhXacNhanBanVe.java
    private static Object createMockChiTietKhach(
            ChoDat choDat,
            String maLoaiVe,
            String hoTen,
            String cccd,
            String sdt,
            LocalDate ngaySinh) {
        // SỬ DỤNG CLASS GIẢ LẬP ĐÃ TẠO Ở TRÊN (ChiTietKhachMock)
        return new ChiTietKhachMock(choDat, maLoaiVe, hoTen, cccd, sdt, ngaySinh);
    }
    // main test neu can
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- 1. Tạo dữ liệu giả cho ChoDat (Ghế đã chọn) ---
            Map<String, ChoDat> mockGheDaChon = new LinkedHashMap<>();

            ChoDat cho1 = new ChoDat("C15-SPT2-1", "SPT2-1", "A1", 1, 1);
            ChoDat cho2 = new ChoDat("C16-SPT2-2", "SPT2-1", "A2", 1, 1);

            mockGheDaChon.put("C15-SPT2-1", cho1);
            mockGheDaChon.put("C16-SPT2-1", cho2);


            Map<String, Object> mockKhachHang = new LinkedHashMap<>();
            LocalDate ngaySinhA = LocalDate.of(1990, 3, 15);
            LocalDate ngaySinhB = LocalDate.of(1990, 6, 10);

            Object khach1 = createMockChiTietKhach(
                    cho1,
                    "VT01",
                    "Nguyen Van A",
                    "012345678901",
                    "0901234567",
                    ngaySinhA // TRUYỀN ĐỐI TƯỢNG LocalDate
            );
            Object khach2 = createMockChiTietKhach(
                    cho2,
                    "VT02",
                    "Tran Thi B",
                    "012345678902",
                    "0907654321",
                    ngaySinhB // TRUYỀN ĐỐI TƯỢNG LocalDate
            );

            mockKhachHang.put("C15-SPT2-1", khach1);
            mockKhachHang.put("C16-SPT2-1", khach2);

            // --- 3. Dữ liệu chuyến tàu và ngày đi ---
            String maChuyenTau = "CTSPT2SG_PT121025";
            Date ngayDi = new Date(); // Ngày hiện tại

            // --- 4. Dữ liệu giá vé ---
            Map<String, Long> mockGiaVe = new HashMap<>();
            mockGiaVe.put("C15-SPT2-1", 100000L); // 500,000 VNĐ
            mockGiaVe.put("C16-SPT2-1", 20000L); // 250,000 VNĐ

            // --- 5. Khởi tạo màn hình XacNhanBanVe ---
            JFrame frame = new JFrame("Màn hình Xác nhận Bán vé (Test)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                // CAST VÀO MAP CHUNG VÀ TRUYỀN DỮ LIỆU GIẢ
                ManHinhXacNhanBanVe confirmPanel = new ManHinhXacNhanBanVe(
                        mockGheDaChon,
                        (Map)mockKhachHang, // Ép kiểu giả định
                        maChuyenTau,
                        ngayDi,
                        mockGiaVe
                );

                frame.add(confirmPanel);
                frame.pack();
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi khởi tạo ManHinhXacNhanBanVe: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}