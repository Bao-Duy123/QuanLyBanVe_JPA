package JDBC_Project.gui.Panel;

import control.CaLamViec;
import dao.*;
import entity.*;
import entity.lopEnum.TrangThaiChuyenTau;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ManHinhQuanLyChuyenTau extends JPanel {

    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_12 = new Font("Segoe UI", Font.BOLD, 12);

    // --- Biến logic ---
    private List<GaTrongTuyen> danhSachGaTam = new ArrayList<>();

    private JTabbedPane tabbedPane;

    // --- Components Tab 1 ---
    private JTable tbTuyen, tbGaTrongTuyen;
    private DefaultTableModel modelTuyen, modelGa;
    private JTextField txtMaTuyen, txtTenTuyen;
    private JComboBox<Ga> cbGaDau, cbGaCuoi, cbMaGa;
    private JTextField txtThuTuGa, txtKhoangCach, txtThoiGianDi, txtThoiGianDung;
    private JButton btnThemTuyen, btnXoaTrangTuyen, btnThemGa, btnXoaGa;
    private JButton btnTimKiemChuyen, btnTaoLichTrinh;

    // --- Components Tab 2 ---
    private JDateChooser dateTimKiem, dateChooserBatDau, dateChooserKetThuc;
    private JComboBox<Tuyen> cbTimTuyen, cbChonTuyenLichTrinh;
    private JComboBox<Tau> cbChonTau;
    private JTable tbChuyenTau;
    private DefaultTableModel modelChuyenTau;
    private JTextField txtGioKhoiHanhChinh;
    private JTextField txtDonGiaKM;
    // DAOs
    private final TuyenDao tuyenDao;
    private final GaTrongTuyenDao gaTrongTuyenDao;
    private final GaDao gaDao;
    private final ChuyenTauDao chuyenTauDao;
    private final TauDAO tauDao;

    public ManHinhQuanLyChuyenTau() {
        try {
            tuyenDao = new TuyenDao();
            gaTrongTuyenDao = new GaTrongTuyenDao();
            gaDao = new GaDao();
            chuyenTauDao = new ChuyenTauDao();
            tauDao = new TauDAO();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khởi tạo DAO", e);
        }

        khoiTaoGiaoDien();

        try {
            taiDuLieuTuyen();
            loadDuLieuGaDocLap();
            loadDuLieuTau();
        } catch (SQLException e) {
            hienThiThongBaoLoi("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        JLabel tieuDe = new JLabel("HỆ THỐNG QUẢN LÝ LỊCH TRÌNH TÀU HỎA");
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tieuDe.setForeground(new Color(0, 51, 153));
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        tieuDe.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(tieuDe, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_BOLD_12);

        // TAB 1
        JPanel pnlTab1 = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlTab1.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlTab1.add(taoPanelTuyen());
        pnlTab1.add(taoPanelGaTrongTuyen());
        tabbedPane.addTab("1. Thiết lập Tuyến & Ga dừng", pnlTab1);

        // TAB 2
        JPanel pnlTab2 = new JPanel(new BorderLayout(0, 10));
        pnlTab2.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlTab2.add(taoPanelTimKiemChuyenTau(), BorderLayout.CENTER);
        pnlTab2.add(taoPanelTaoLichTrinh(), BorderLayout.SOUTH);
        tabbedPane.addTab("2. Quản lý Chuyến tàu & Lịch trình", pnlTab2);

        add(tabbedPane, BorderLayout.CENTER);
    }
    // --- Các hàm tạo Panel (Giữ nguyên logic cũ của bạn và điều chỉnh Tab 2) ---

    private JPanel taoPanelTuyen() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Thông tin Tuyến mới"));

        // 1. Form nhập liệu: Tăng lên GridLayout(5, 2) để thêm dòng Đơn giá
        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 5, 5));

        pnlForm.add(new JLabel("Mã Tuyến:"));
        txtMaTuyen = new JTextField();
        pnlForm.add(txtMaTuyen);

        pnlForm.add(new JLabel("Tên Tuyến:"));
        txtTenTuyen = new JTextField();
        pnlForm.add(txtTenTuyen);

        pnlForm.add(new JLabel("Ga Đầu:"));
        cbGaDau = new JComboBox<>();
        pnlForm.add(cbGaDau);

        pnlForm.add(new JLabel("Ga Cuối:"));
        cbGaCuoi = new JComboBox<>();
        pnlForm.add(cbGaCuoi);

        // THÊM MỚI: Ô nhập Đơn giá/KM
        pnlForm.add(new JLabel("Đơn giá/KM (VNĐ):"));
        txtDonGiaKM = new JTextField();
        pnlForm.add(txtDonGiaKM);

        // 2. Bảng danh sách tuyến: Thêm cột "Đơn giá/KM"
        modelTuyen = new DefaultTableModel(
                new String[]{"Mã Tuyến", "Tên Tuyến", "Ga Đầu", "Ga Cuối", "Đơn giá/KM"}, 0
        );
        tbTuyen = new JTable(modelTuyen);

        // Nút bấm
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnThemTuyen = new JButton("XÁC NHẬN LƯU TUYẾN");
        btnThemTuyen.setBackground(new Color(0, 102, 204));
        btnThemTuyen.setForeground(Color.WHITE);
        btnXoaTrangTuyen = new JButton("Làm mới/Mới");

        pnlNut.add(btnXoaTrangTuyen);
        pnlNut.add(btnThemTuyen);

        btnThemTuyen.addActionListener(e -> logicThemTuyen());
        btnXoaTrangTuyen.addActionListener(e -> xoaTrangFormTuyen());

        panel.add(pnlForm, BorderLayout.NORTH);
        panel.add(new JScrollPane(tbTuyen), BorderLayout.CENTER);
        panel.add(pnlNut, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel taoPanelGaTrongTuyen() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Cấu hình Ga dừng (Thêm vào danh sách chờ)"));

        // Form nhập ga
        JPanel pnlNhapGa = new JPanel(new GridLayout(3, 4, 5, 5));
        pnlNhapGa.add(new JLabel("Mã Ga:")); cbMaGa = new JComboBox<>(); pnlNhapGa.add(cbMaGa);
        pnlNhapGa.add(new JLabel("Thứ tự:")); txtThuTuGa = new JTextField(); pnlNhapGa.add(txtThuTuGa);
        pnlNhapGa.add(new JLabel("KC (km):")); txtKhoangCach = new JTextField(); pnlNhapGa.add(txtKhoangCach);
        pnlNhapGa.add(new JLabel("Dừng (p):")); txtThoiGianDung = new JTextField(); pnlNhapGa.add(txtThoiGianDung);
        pnlNhapGa.add(new JLabel("TG Đi (p):")); txtThoiGianDi = new JTextField(); pnlNhapGa.add(txtThoiGianDi);

        // Bảng danh sách ga tạm
        modelGa = new DefaultTableModel(new String[]{"Mã Ga", "Thứ tự", "KC", "TG Đi", "Dừng"}, 0);
        tbGaTrongTuyen = new JTable(modelGa);

        // Nút bấm
        JPanel pnlNutGa = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnThemGa = new JButton("Thêm vào danh sách chờ");
        btnXoaGa = new JButton("Xóa khỏi danh sách chờ");
        btnXoaGa.setForeground(Color.RED);

        pnlNutGa.add(btnThemGa);
        pnlNutGa.add(btnXoaGa);

        btnThemGa.addActionListener(e -> logicThemGa());
        btnXoaGa.addActionListener(e -> logicXoaGa());

        panel.add(pnlNhapGa, BorderLayout.NORTH);
        panel.add(new JScrollPane(tbGaTrongTuyen), BorderLayout.CENTER);
        panel.add(pnlNutGa, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel taoPanelNhapLieuGa() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 5, 5));
        panel.add(new JLabel("Mã Ga:")); cbMaGa = new JComboBox<>(); panel.add(cbMaGa);
        panel.add(new JLabel("Thứ tự:")); txtThuTuGa = new JTextField(); panel.add(txtThuTuGa);
        panel.add(new JLabel("Khoảng cách:")); txtKhoangCach = new JTextField(); panel.add(txtKhoangCach);
        panel.add(new JLabel("Dừng (phút):")); txtThoiGianDung = new JTextField(); panel.add(txtThoiGianDung);
        panel.add(new JLabel("TG đi kế:")); txtThoiGianDi = new JTextField(); panel.add(txtThoiGianDi);
        return panel;
    }

    private JPanel taoPanelTimKiemChuyenTau() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Tra cứu Chuyến tàu"));

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlFilter.add(new JLabel("Tuyến:"));
        cbTimTuyen = new JComboBox<>();
        cbTimTuyen.setPreferredSize(new Dimension(200, 25));
        pnlFilter.add(cbTimTuyen);

        pnlFilter.add(new JLabel("Ngày:"));
        dateTimKiem = new JDateChooser(new Date());
        dateTimKiem.setPreferredSize(new Dimension(150, 25));
        pnlFilter.add(dateTimKiem);

        btnTimKiemChuyen = new JButton("Tìm kiếm");
        pnlFilter.add(btnTimKiemChuyen);
        panel.add(pnlFilter, BorderLayout.NORTH);

        modelChuyenTau = new DefaultTableModel(new String[]{"Mã Chuyến", "Tàu", "Ngày KH", "Giờ KH", "Ga Đi", "Ga Đến", "Trạng Thái"}, 0);
        tbChuyenTau = new JTable(modelChuyenTau);
        panel.add(new JScrollPane(tbChuyenTau), BorderLayout.CENTER);

        btnTimKiemChuyen.addActionListener(e -> logicTimKiemChuyenTau());
        return panel;
    }

    private JPanel taoPanelTaoLichTrinh() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(new Color(230, 240, 250));
        panel.setBorder(new TitledBorder("4. Tạo Lịch Trình Chuyến Tàu Hàng Loạt"));
        panel.setPreferredSize(new Dimension(0, 120));

        // ComboBox chọn Tuyến (Mới thêm vào theo yêu cầu)
        panel.add(new JLabel("Chọn Tuyến:"));
        cbChonTuyenLichTrinh = new JComboBox<>();
        cbChonTuyenLichTrinh.setPreferredSize(new Dimension(200, 25));
        panel.add(cbChonTuyenLichTrinh);

        panel.add(new JLabel("Tàu:"));
        cbChonTau = new JComboBox<>();
        cbChonTau.setPreferredSize(new Dimension(100, 25));
        panel.add(cbChonTau);

        panel.add(new JLabel("Giờ:"));

        txtGioKhoiHanhChinh = new JTextField("06:00", 5);
        txtGioKhoiHanhChinh.setPreferredSize(new Dimension(60, 30));
        panel.add(txtGioKhoiHanhChinh);

        panel.add(new JLabel("Từ:"));

        dateChooserBatDau = new JDateChooser(new Date());
        dateChooserBatDau.setPreferredSize(new Dimension(180, 30));
        panel.add(dateChooserBatDau);

        panel.add(new JLabel("Đến:"));

        dateChooserKetThuc = new JDateChooser(new Date());
        dateChooserKetThuc.setPreferredSize(new Dimension(180, 30));
        panel.add(dateChooserKetThuc);

        btnTaoLichTrinh = new JButton("Tạo Lịch Trình");
        btnTaoLichTrinh.setPreferredSize(new Dimension(150, 35));
        btnTaoLichTrinh.setBackground(new Color(0, 153, 76));
        btnTaoLichTrinh.setForeground(Color.WHITE);
        btnTaoLichTrinh.addActionListener(e -> taoLichTrinhHangLoat());
        panel.add(btnTaoLichTrinh);

        return panel;
    }

    // --- LOGIC XỬ LÝ ---

    private void taoLichTrinhHangLoat() {
        // Lấy dữ liệu từ ComboBox thay vì JTextField ở panel Tuyến
        Tuyen tuyenObj = (Tuyen) cbChonTuyenLichTrinh.getSelectedItem();
        if (tuyenObj == null) {
            hienThiThongBaoLoi("Vui lòng chọn một Tuyến để tạo lịch trình.");
            return;
        }

        Date utilStartDate = dateChooserBatDau.getDate();
        Date utilEndDate = dateChooserKetThuc.getDate();
        String gioBatDauStr = txtGioKhoiHanhChinh.getText().trim();

        if (utilStartDate == null || utilEndDate == null) {
            hienThiThongBaoLoi("Vui lòng chọn ngày bắt đầu và kết thúc.");
            return;
        }

        LocalDate startDay = utilStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDay = utilEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (startDay.isAfter(endDay)) {
            hienThiThongBaoLoi("Ngày bắt đầu không được sau ngày kết thúc.");
            return;
        }

        LocalTime gioBatDauCoDinh;
        try {
            if (gioBatDauStr.length() == 5) gioBatDauStr += ":00";
            gioBatDauCoDinh = LocalTime.parse(gioBatDauStr);
        } catch (Exception e) {
            hienThiThongBaoLoi("Giờ không hợp lệ (HH:mm).");
            return;
        }

        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<GaTrongTuyen> danhSachGa = gaTrongTuyenDao.layGaTrongTuyenTheoMa(tuyenObj.getMaTuyen());
            Tau tauObj = (Tau) cbChonTau.getSelectedItem();

            if (tauObj == null) {
                hienThiThongBaoLoi("Vui lòng chọn tàu.");
                return;
            }

            int soLuongChuyenTaoMoi = 0;
            LocalDate currentDay = startDay;
            while (!currentDay.isAfter(endDay)) {
                String ngayDinhDangMa = currentDay.format(DateTimeFormatter.ofPattern("ddMMyy"));
                if (!chuyenTauDao.kiemTraDaCoLichTrinhNgay(tuyenObj.getMaTuyen(), ngayDinhDangMa)) {
                    soLuongChuyenTaoMoi += taoLichTrinhNgay(tuyenObj, tauObj, currentDay, gioBatDauCoDinh, danhSachGa);
                }
                currentDay = currentDay.plusDays(1);
            }

            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Đã tạo thành công " + soLuongChuyenTaoMoi + " bản ghi.");
        } catch (SQLException e) {
            this.setCursor(Cursor.getDefaultCursor());
            hienThiThongBaoLoi("Lỗi CSDL: " + e.getMessage());
        }
    }

    // Tận dụng hàm taoLichTrinhNgay cũ của bạn...
    private int taoLichTrinhNgay(Tuyen tuyenObj, entity.Tau tauObj, LocalDate ngayKhoiHanh, LocalTime gioBatDau, List<GaTrongTuyen> danhSachGa) throws SQLException {
        int soLuongTauDuocTao = 0;
        Map<String, LocalDateTime> masterSchedule = new HashMap<>();
        LocalDateTime thoiDiemHienTai = LocalDateTime.of(ngayKhoiHanh, gioBatDau);

        masterSchedule.put(danhSachGa.get(0).getMaGa(), thoiDiemHienTai);

        for (int i = 0; i < danhSachGa.size() - 1; i++) {
            GaTrongTuyen gaHienTai = danhSachGa.get(i);
            GaTrongTuyen gaKeTiep = danhSachGa.get(i + 1);

            int tgChayPhut = gaHienTai.getThoiGianDiDenGaTiepTheo();
            thoiDiemHienTai = thoiDiemHienTai.plusMinutes(tgChayPhut);
            masterSchedule.put(gaKeTiep.getMaGa() + "_DEN", thoiDiemHienTai);

            if (i + 1 < danhSachGa.size() - 1) {
                int tgDungPhut = gaKeTiep.getThoiGianDung();
                thoiDiemHienTai = thoiDiemHienTai.plusMinutes(tgDungPhut);
                masterSchedule.put(gaKeTiep.getMaGa(), thoiDiemHienTai);
            } else {
                masterSchedule.put(gaKeTiep.getMaGa(), thoiDiemHienTai);
            }
        }

        String ngayDauTienStr = ngayKhoiHanh.format(DateTimeFormatter.ofPattern("ddMMyy"));
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();

        for (int i = 0; i < danhSachGa.size(); i++) {
            for (int j = i + 1; j < danhSachGa.size(); j++) {
                GaTrongTuyen gttDi = danhSachGa.get(i);
                GaTrongTuyen gttDen = danhSachGa.get(j);

                LocalDateTime thoiDiemDi = masterSchedule.get(gttDi.getMaGa());
                LocalDateTime thoiDiemDen = (j == danhSachGa.size() - 1) ? masterSchedule.get(gttDen.getMaGa()) : masterSchedule.get(gttDen.getMaGa() + "_DEN");

                String maChuyenTau = String.format("%s_%s_%s_%s", tuyenObj.getMaTuyen().trim(), ngayDauTienStr, gttDi.getMaGa(), gttDen.getMaGa());

                ChuyenTau ct = new ChuyenTau(
                        maChuyenTau, tuyenObj, tauObj.getSoHieu(), thoiDiemDi.toLocalDate(), thoiDiemDi.toLocalTime(),
                        new Ga(gttDi.getMaGa()), new Ga(gttDen.getMaGa()), tauObj, thoiDiemDen.toLocalDate(),
                        thoiDiemDen.toLocalTime(), nv, TrangThaiChuyenTau.DANG_MO_BAN_VE
                );
                chuyenTauDao.themChuyenTauNangCao(ct);
                soLuongTauDuocTao++;
            }
        }
        return soLuongTauDuocTao;
    }
    private void logicThemTuyen() {
        String ma = txtMaTuyen.getText().trim();
        String ten = txtTenTuyen.getText().trim();
        Ga gaDau = (Ga) cbGaDau.getSelectedItem();
        Ga gaCuoi = (Ga) cbGaCuoi.getSelectedItem();
        int donGiaKM;
        try {
            donGiaKM = Integer.parseInt(txtDonGiaKM.getText().trim());
        } catch (NumberFormatException e) {
            hienThiThongBaoLoi("Đơn giá/KM không hợp lệ!");
            return;
        }

        if (ma.isEmpty() || ten.isEmpty()) {
            hienThiThongBaoLoi("Vui lòng nhập Mã và Tên tuyến!");
            return;
        }

        if (danhSachGaTam.size() < 2) {
            hienThiThongBaoLoi("Bắt buộc phải có ít nhất 2 ga (Ga Đầu & Ga Cuối) trong danh sách chờ!");
            return;
        }

        try {
            Tuyen t = new Tuyen(ma, ten, gaDau.getMaGa(), gaCuoi.getMaGa(), donGiaKM );
            if (tuyenDao.themTuyen(t)) {
                for (GaTrongTuyen gtt : danhSachGaTam) {
                    gtt.setTuyen(t);
                    gaTrongTuyenDao.themGaTrongTuyen(gtt);
                }
                JOptionPane.showMessageDialog(this, "Tạo Tuyến và cấu hình Ga thành công!");
                taiDuLieuTuyen();
                xoaTrangFormTuyen();
            }
        } catch (SQLException e) {
            hienThiThongBaoLoi("Lỗi lưu tuyến: " + e.getMessage());
        }
    }
    private void logicThemGa() {
        try {
            Ga gaChon = (Ga) cbMaGa.getSelectedItem();
            for (GaTrongTuyen g : danhSachGaTam) {
                if (g.getMaGa().equals(gaChon.getMaGa())) {
                    hienThiThongBaoLoi("Ga này đã có trong danh sách chờ!");
                    return;
                }
            }

            GaTrongTuyen gtt = new GaTrongTuyen();
            gtt.setMaGa(gaChon.getMaGa());
            gtt.setThuTuGa(Integer.parseInt(txtThuTuGa.getText().trim()));
            gtt.setKhoangCachTichLuy(Integer.parseInt(txtKhoangCach.getText().trim()));
            gtt.setThoiGianDiDenGaTiepTheo(Integer.parseInt(txtThoiGianDi.getText().trim()));
            gtt.setThoiGianDung(Integer.parseInt(txtThoiGianDung.getText().trim()));

            danhSachGaTam.add(gtt);
            modelGa.addRow(new Object[]{gtt.getMaGa(), gtt.getThuTuGa(), gtt.getKhoangCachTichLuy(), gtt.getThoiGianDiDenGaTiepTheo(), gtt.getThoiGianDung()});
            xoaTrangFormGa();
        } catch (Exception e) {
            hienThiThongBaoLoi("Dữ liệu thông Ga không hợp lệ! Vui lòng kiểm tra lại");
        }
    }
    private void logicXoaGa() {
        int selectedRow = tbGaTrongTuyen.getSelectedRow();
        if (selectedRow != -1) {
            danhSachGaTam.remove(selectedRow);
            modelGa.removeRow(selectedRow);
            xoaTrangFormGa();
        } else {
            hienThiThongBaoLoi("Vui lòng chọn Ga trong danh sách chờ để xóa!");
        }
    }

    private void xoaTrangFormTuyen() {
        txtMaTuyen.setText("");
        txtTenTuyen.setText("");
        txtMaTuyen.setEditable(true);
        if (cbGaDau.getItemCount() > 0) cbGaDau.setSelectedIndex(0);
        if (cbGaCuoi.getItemCount() > 0) cbGaCuoi.setSelectedIndex(0);
        tbTuyen.clearSelection();
    }

    private void xoaTrangFormGa() {
        cbMaGa.setEnabled(true);
        txtThuTuGa.setText("");
        txtKhoangCach.setText("");
        txtThoiGianDi.setText("");
        txtThoiGianDung.setText("");
    }

    private void taiDuLieuTuyen() throws SQLException {
        // 1. Xóa dữ liệu cũ
        modelTuyen.setRowCount(0);
        cbTimTuyen.removeAllItems();
        cbChonTuyenLichTrinh.removeAllItems();

        // 2. Lấy danh sách từ DAO
        List<Tuyen> ds = tuyenDao.layTatCaTuyen();

        for (Tuyen t : ds) {
            // Đổ vào Bảng (JTable) - thêm đơn giá vào cột thứ 5
            modelTuyen.addRow(new Object[]{
                    t.getMaTuyen(),
                    t.getTenTuyen(),
                    t.getGaDau(),
                    t.getGaCuoi(),
                    t.getDonGiaKM()
            });

            // Đổ vào ComboBox (Quan trọng)
            cbTimTuyen.addItem(t);
            cbChonTuyenLichTrinh.addItem(t);
        }
    }

    // --- Các hàm hỗ trợ khác (xóa trắng, load ga, load tàu...) giữ nguyên như code cũ của bạn ---

    private void chonGaTrongComboBox(JComboBox<Ga> cb, String maGa) {
        if (maGa == null || cb == null) return;

        String target = maGa.trim(); // Loại bỏ khoảng trắng thừa
        for (int i = 0; i < cb.getItemCount(); i++) {
            Ga gaTrongCombo = cb.getItemAt(i);
            if (gaTrongCombo != null && gaTrongCombo.getMaGa().trim().equalsIgnoreCase(target)) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }
    private void dienThongTinGaVaoForm() {
        int selectedRow = tbGaTrongTuyen.getSelectedRow();
        if (selectedRow != -1) {
            String maGa = (String) modelGa.getValueAt(selectedRow, 0);
            chonGaTrongComboBox(cbMaGa, maGa);
            txtThuTuGa.setText(modelGa.getValueAt(selectedRow, 1).toString());
            txtKhoangCach.setText(modelGa.getValueAt(selectedRow, 2).toString());
            txtThoiGianDi.setText(modelGa.getValueAt(selectedRow, 3).toString());
            txtThoiGianDung.setText(modelGa.getValueAt(selectedRow, 4).toString());
            cbMaGa.setEnabled(false);
        }
    }

    private void loadDuLieuGaDocLap() throws SQLException {
        Vector<Ga> dsGa = gaDao.layDanhSachGa();
        cbGaDau.removeAllItems(); cbGaCuoi.removeAllItems(); cbMaGa.removeAllItems();
        for (Ga g : dsGa) {
            cbGaDau.addItem(g); cbGaCuoi.addItem(g); cbMaGa.addItem(g);
        }
    }
    private class GaRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Ga) {
                setText(((Ga) value).getTenGa() + " (" + ((Ga) value).getMaGa() + ")");
            }
            return this;
        }
    }
    private void loadDuLieuTau() {
        cbChonTau.removeAllItems();
        try {
            // Giả sử TauDao có hàm layTatCaTau() trả về List<Tau>
            // Hoặc bạn có thể dùng select * from Tau trong TauDao
            List<Tau> danhSachTau = tauDao.layTatCa();

            for (Tau t : danhSachTau) {
                // Chỉ thêm tàu đang hoạt động (tuỳ logic của bạn)
                if (!"Bảo trì".equals(t.getTrangThai())) {
                    cbChonTau.addItem(t);
                }
            }

            cbChonTau.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Tau) {
                        Tau t = (Tau) value;
                        setText(t.getSoHieu() + " (" + t.getTrangThai() + ")");
                    }
                    return this;
                }
            });

        } catch (Exception e) {
            hienThiThongBaoLoi("Không tải được danh sách tàu: " + e.getMessage());
        }
    }
    private void logicTimKiemChuyenTau() {
        Tuyen tuyen = (Tuyen) cbTimTuyen.getSelectedItem();
        Date utilDate = dateTimKiem.getDate();

        if (tuyen == null) {
            hienThiThongBaoLoi("Vui lòng chọn Tuyến để tìm kiếm!");
            return;
        }
        if (utilDate == null) {
            hienThiThongBaoLoi("Vui lòng chọn Ngày để tìm kiếm!");
            return;
        }

        LocalDate ngayTim = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Xóa dữ liệu cũ trên bảng trước khi tìm
        modelChuyenTau.setRowCount(0);

        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // Hiển thị con trỏ chờ

            List<ChuyenTau> ds = chuyenTauDao.layChuyenTauTheoDieuKien(tuyen.getMaTuyen(), ngayTim);

            this.setCursor(Cursor.getDefaultCursor());

            if (ds == null || ds.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy chuyến tàu nào cho tuyến " + tuyen.getTenTuyen() + " vào ngày " + ngayTim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (ChuyenTau ct : ds) {
                    modelChuyenTau.addRow(new Object[]{
                            ct.getMaChuyenTau(),
                            (ct.getTau() != null) ? ct.getTau().getSoHieu() : "N/A",
                            ct.getNgayKhoiHanh(),
                            ct.getGioKhoiHanh(),
                            (ct.getGaDi() != null) ? ct.getGaDi().getTenGa() : "N/A",
                            (ct.getGaDen() != null) ? ct.getGaDen().getTenGa() : "N/A",
                            (ct.getThct() != null) ? ct.getThct().getTenHienThi() : "Đang chờ"
                    });
                }
            }
        } catch (Exception e) {
            this.setCursor(Cursor.getDefaultCursor());
            e.printStackTrace(); // In ra console để lập trình viên kiểm tra
            hienThiThongBaoLoi("Lỗi hệ thống khi tìm kiếm: " + e.getMessage());
        }
    }
    private void hienThiThongBaoLoi(String msg) { JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE); }

    private void taiDuLieuGaTrongTuyen(String maTuyen) {
        modelGa.setRowCount(0);
        try {
            List<GaTrongTuyen> danhSachGa = gaTrongTuyenDao.layGaTrongTuyenTheoMa(maTuyen);
            for (GaTrongTuyen g : danhSachGa) {
                modelGa.addRow(new Object[]{
                        g.getMaGa(),
                        g.getThuTuGa(),
                        g.getKhoangCachTichLuy(),
                        g.getThoiGianDiDenGaTiepTheo(),
                        g.getThoiGianDung()
                });
            }
        } catch (SQLException e) {
            hienThiThongBaoLoi("Lỗi tải dữ liệu Ga trong Tuyến: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Mock data nhân viên để test
        NhanVien nv = new NhanVien(); nv.setMaNV("NVQL0001");
        CaLamViec.getInstance().batDauCa(nv);

        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Quản lý Chuyến Tàu");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1100, 750);
            f.add(new ManHinhQuanLyChuyenTau());
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}