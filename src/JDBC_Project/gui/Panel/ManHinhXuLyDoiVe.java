package JDBC_Project.gui.Panel;

import com.toedter.calendar.JDateChooser;
import dao.*;
import control.VeSoDoTau;
import entity.*;
import entity.DTO.ThongTinVeDTO;
import service.NghiepVuTinhGiaVe;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ManHinhXuLyDoiVe extends JPanel implements ActionListener {

    // --- CẤU HÌNH UI ---
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);
    private static final Color COLOR_BG_HEADER = new Color(255, 228, 204);
    private static final Color COLOR_BG_INFO = new Color(245, 245, 245);

    // --- ĐỊNH NGHĨA MÀU SẮC ---
    private static final Color COLOR_GHE_TRONG = Color.LIGHT_GRAY;
    private static final Color COLOR_DA_BAN_NGUOI_LA = Color.BLACK;
    private static final Color COLOR_DANG_CHON = Color.BLUE;
    private static final Color COLOR_KHACH_KHAC_GIU = new Color(255, 193, 7);

    // --- PHÍ ĐỔI VÉ ---
    private static final long PHI_DOI_VE = 20000;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final Dimension KICH_THUOC_GHE_VUONG = new Dimension(50, 35);
    private static final Dimension KICH_THUOC_GIUONG_NAM = new Dimension(50, 35);

    // --- DỮ LIỆU ---
    private List<ThongTinVeDTO> listVeCanDoi;
    private String maVeDangActive = null;
    private Map<String, ChoDat> mapGheMoi = new HashMap<>();

    // Map lưu Mã chuyến tàu của từng vé đã chọn ghế (để validate cùng chuyến)
    private Map<String, String> mapVeToMaChuyen = new HashMap<>();

    // Map lưu RadioButton để thực hiện tự động chuyển hoặc reset
    private Map<String, JRadioButton> mapRadioButtons = new HashMap<>();

    private ButtonGroup bgKhachHang = new ButtonGroup();

    // --- UI ---
    private JComboBox<Ga> cbGaDi, cbGaDen;
    private JDateChooser dateChooserNgayDi;
    private JPanel pnlChuyenTau, pnlToa, pnlSoDoGhe, pnlDanhSachKhachHang;
    private JButton btnTimChuyen, btnHuy, btnReset, btnXacNhanDoi;

    // --- STATE ---
    private List<ChuyenTau> ketQuaTimKiem = new ArrayList<>();
    private String maChuyenTauHienTai = null;
    private String maToaHienTai = null;
    private JButton lastSelectedToaButton = null;
    private JPanel lastSelectedChuyenTauPanel = null;

    // --- MAPS & DAOs ---
    private Map<String, LoaiVe> mapAllLoaiVe = new HashMap<>();
    private Map<String, String> mapReverseLoaiVe;

    // [QUAN TRỌNG] Map lưu trữ nút ghế để update nhanh không cần vẽ lại
    private Map<String, JButton> seatButtonsMap = new HashMap<>();
    private Map<String, ChoDat> tatCaChoDatToaHienTai = new HashMap<>();

    private final ChoDatDAO choDatDao = new ChoDatDAO();
    private final LoaiVeDAO loaiVeDAO = new LoaiVeDAO();
    private final ToaDAO toaDao = new ToaDAO();
    // private final GaTrongTuyenDao gaTrongTuyenDao = new GaTrongTuyenDao(); // Chưa dùng tới

    public ManHinhXuLyDoiVe(List<ThongTinVeDTO> listVeInput) {
        this.listVeCanDoi = listVeInput;

        List<LoaiVe> allLoaiVe = loaiVeDAO.getAllLoaiVe();
        mapAllLoaiVe = allLoaiVe.stream().collect(Collectors.toMap(LoaiVe::getMaLoaiVe, lv -> lv));
        mapReverseLoaiVe = allLoaiVe.stream().collect(Collectors.toMap(this::formatLoaiVeHienThi, LoaiVe::getMaLoaiVe));

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel tieuDe = new JLabel("ĐỔI VÉ - CHỌN CHỖ MỚI");
        tieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        tieuDe.setForeground(new Color(230, 126, 34));
        tieuDe.setBorder(new EmptyBorder(10, 15, 10, 10));
        add(tieuDe, BorderLayout.NORTH);

        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        chia.setResizeWeight(0.65);
        chia.setDividerSize(5);
        chia.setBorder(null);

        chia.setLeftComponent(taoPanelTrai());
        chia.setRightComponent(taoPanelPhai());
        add(chia, BorderLayout.CENTER);

        if (!listVeCanDoi.isEmpty()) {
            autoFillAndSearch(listVeCanDoi.get(0));
        }
    }

    private void autoFillAndSearch(ThongTinVeDTO mau) {
        setCboGa(cbGaDi, mau.getMaGaDi());
        setCboGa(cbGaDen, mau.getMaGaDen());
        if (mau.getNgayKhoiHanh() != null) {
            dateChooserNgayDi.setDate(Date.valueOf(mau.getNgayKhoiHanh()));
        }
        timKiemChuyenTau();
        hienThiDanhSachKhachHang();
    }

    private void setCboGa(JComboBox<Ga> cbo, String maGa) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getMaGa().equals(maGa)) {
                cbo.setSelectedIndex(i); break;
            }
        }
    }

    // =========================================================================
    // UI TRÁI
    // =========================================================================
    private JPanel taoPanelTrai() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 3, false), new EmptyBorder(5, 5, 0, 0)));
        p.setBackground(Color.WHITE);

        p.add(createKhuVucTimKiem());
        p.add(Box.createVerticalStrut(10));
        p.add(createKhuVucDanhSachChuyenTau());
        p.add(Box.createVerticalStrut(10));
        p.add(createKhuVucChonToa());
        p.add(Box.createVerticalStrut(10));
        p.add(createKhuVucChonViTriGhe());
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel createKhuVucTimKiem() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setBorder(BorderFactory.createTitledBorder("Tìm chuyến tàu mới"));
        p.setBackground(Color.WHITE);

        Vector<Ga> listGa = new GaDao().layDanhSachGa();
        cbGaDi = new JComboBox<>(listGa);
        cbGaDen = new JComboBox<>(listGa);

        dateChooserNgayDi = new JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setPreferredSize(new Dimension(120, 25));
        dateChooserNgayDi.setMinSelectableDate(new java.util.Date());

        btnTimChuyen = new JButton("Tìm chuyến");
        styleNutChinh(btnTimChuyen);
        btnTimChuyen.addActionListener(this);

        p.add(new JLabel("Ga đi:")); p.add(cbGaDi);
        p.add(new JLabel("Ga đến:")); p.add(cbGaDen);
        p.add(new JLabel("Ngày đi:")); p.add(dateChooserNgayDi);
        p.add(btnTimChuyen);
        return p;
    }

    private JScrollPane createKhuVucDanhSachChuyenTau() {
        pnlChuyenTau = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlChuyenTau.setOpaque(false);
        JScrollPane sc = new JScrollPane(pnlChuyenTau);
        sc.setBorder(BorderFactory.createTitledBorder("Danh sách chuyến tàu"));
        sc.setPreferredSize(new Dimension(600, 190));
        sc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        return sc;
    }

    private JPanel createKhuVucChonToa() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("Chọn toa"));
        p.setBackground(Color.WHITE);

        pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlToa.setOpaque(false);
        JScrollPane sc = new JScrollPane(pnlToa);
        sc.setBorder(null);
        sc.setPreferredSize(new Dimension(500, 80));
        sc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        row.add(new JLabel("Chọn toa:"));
        row.add(sc);
        p.add(row);

        p.setPreferredSize(new Dimension(600, 120));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        return p;
    }

    private JPanel createKhuVucChonViTriGhe() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createTitledBorder("Sơ đồ ghế"));
        wrapper.setBackground(Color.WHITE);

        pnlSoDoGhe = new JPanel();
        pnlSoDoGhe.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlSoDoGhe.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSoDoGhe.setBackground(Color.WHITE);

        JScrollPane sc = new JScrollPane(pnlSoDoGhe);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.setPreferredSize(new Dimension(600, 250));
        sc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        wrapper.add(sc);

        wrapper.add(Box.createVerticalStrut(10));

        JPanel pnlLegend = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlLegend.setOpaque(false);
        pnlLegend.add(taoMucChuGiai(COLOR_GHE_TRONG, "Ghế trống"));
        pnlLegend.add(taoMucChuGiai(COLOR_DA_BAN_NGUOI_LA, "Đã bán"));
        pnlLegend.add(taoMucChuGiai(COLOR_DANG_CHON, "Chỗ mới (Đang chọn)"));
        pnlLegend.add(taoMucChuGiai(COLOR_KHACH_KHAC_GIU, "Đang giữ (Khách khác)"));

        wrapper.add(pnlLegend);
        return wrapper;
    }

    private JPanel taoMucChuGiai(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JLabel lblColor = new JLabel();
        lblColor.setOpaque(true);
        lblColor.setBackground(c);
        lblColor.setPreferredSize(new Dimension(15, 15));
        lblColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        p.add(lblColor);
        p.add(new JLabel(text));
        return p;
    }

    // =========================================================================
    // UI PHẢI: PANEL KHÁCH HÀNG
    // =========================================================================
    private JPanel taoPanelPhai() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false), new EmptyBorder(0, 0, 0, 0)));
        p.setBackground(Color.WHITE);

        pnlDanhSachKhachHang = new JPanel();
        pnlDanhSachKhachHang.setLayout(new BoxLayout(pnlDanhSachKhachHang, BoxLayout.Y_AXIS));
        pnlDanhSachKhachHang.setOpaque(false);
        pnlDanhSachKhachHang.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scroll = new JScrollPane(pnlDanhSachKhachHang);
        scroll.setBorder(BorderFactory.createTitledBorder("Danh sách khách cần đổi"));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        p.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(Color.WHITE);

        btnHuy = new JButton("Hủy bỏ");
        btnHuy.setPreferredSize(new Dimension(100, 40));
        btnHuy.setBackground(new Color(231, 76, 60));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFont(new Font("Arial", Font.BOLD, 14));
        btnHuy.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if(w instanceof gui.MainFrame.BanVeDashboard) ((gui.MainFrame.BanVeDashboard)w).chuyenManHinh("doiVe");
            else if (w instanceof Window) w.dispose();
        });

        btnReset = new JButton("Làm mới");
        btnReset.setPreferredSize(new Dimension(100, 40));
        btnReset.setBackground(Color.GRAY);
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Arial", Font.BOLD, 14));
        btnReset.addActionListener(e -> xuLyReset());

        btnXacNhanDoi = new JButton("Xác nhận đổi");
        btnXacNhanDoi.setPreferredSize(new Dimension(150, 40));
        btnXacNhanDoi.setBackground(new Color(46, 204, 113));
        btnXacNhanDoi.setForeground(Color.WHITE);
        btnXacNhanDoi.setFont(new Font("Arial", Font.BOLD, 14));
        btnXacNhanDoi.addActionListener(this);

        footer.add(btnHuy);
        footer.add(btnReset);
        footer.add(btnXacNhanDoi);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    private JPanel taoPanelThongTinKhach(ThongTinVeDTO ve) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // HEADER
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlHeader.setBackground(COLOR_BG_HEADER);
        JRadioButton rdo = new JRadioButton(ve.getHoTen());
        rdo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rdo.setOpaque(false);
        rdo.addActionListener(e -> xuLyChuyenKhachHang(ve.getMaVe()));
        bgKhachHang.add(rdo);
        mapRadioButtons.put(ve.getMaVe(), rdo);

        pnlHeader.add(rdo);
        pnlHeader.add(new JLabel(" (Mã vé: " + ve.getMaVe() + ")"));
        panel.add(pnlHeader, BorderLayout.NORTH);

        // BODY
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(5, 10, 5, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontVal = new Font("Segoe UI", Font.PLAIN, 12);

        JTextField txtHoTen = new JTextField(ve.getHoTen()); txtHoTen.setFont(fontVal); txtHoTen.setEditable(false);
        JTextField txtNgaySinh = new JTextField(ve.getNgaySinhStr()); txtNgaySinh.setFont(fontVal); txtNgaySinh.setEditable(false);
        JTextField txtSDT = new JTextField(ve.getSoDienThoai()); txtSDT.setFont(fontVal); txtSDT.setEditable(false);
        JTextField txtCCCD = new JTextField(ve.getCccd()); txtCCCD.setFont(fontVal); txtCCCD.setEditable(false);

        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; pnlContent.add(createLabel("Họ tên:", fontLabel), gbc);
        gbc.gridx = 1; gbc.weightx = 1; pnlContent.add(txtHoTen, gbc);
        gbc.gridx = 2; gbc.weightx = 0; pnlContent.add(createLabel("Ngày sinh:", fontLabel), gbc);
        gbc.gridx = 3; gbc.weightx = 1; pnlContent.add(txtNgaySinh, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0; pnlContent.add(createLabel("SĐT:", fontLabel), gbc);
        gbc.gridx = 1; gbc.weightx = 1; pnlContent.add(txtSDT, gbc);
        gbc.gridx = 2; gbc.weightx = 0; pnlContent.add(createLabel("CCCD:", fontLabel), gbc);
        gbc.gridx = 3; gbc.weightx = 1; pnlContent.add(txtCCCD, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 4;
        pnlContent.add(new JSeparator(), gbc);

        // THÔNG TIN VÉ CŨ
        gbc.gridy = 3;
        JPanel pnlVeCu = new JPanel(new GridLayout(0, 1, 0, 2));
        pnlVeCu.setOpaque(false);
        pnlVeCu.setBackground(COLOR_BG_INFO);
        pnlVeCu.setBorder(new EmptyBorder(5, 5, 5, 5));

        String chuyenCu = "Chuyến đã chọn: " + ve.getGaDi() + " -> " + ve.getGaDen();
        String tgCu = "Thời gian đi: " + ve.getNgayDiStr() + " - " + ve.getGioDiStr();
        String choCu = "Chỗ cũ: Toa " + formatToa(ve.getMaToa()) + " - Chỗ số " + ve.getSoCho();

        pnlVeCu.add(createLabel(chuyenCu, new Font("Segoe UI", Font.BOLD, 12)));
        pnlVeCu.add(createLabel(tgCu, fontVal));
        pnlVeCu.add(createLabel(choCu, fontVal));
        pnlContent.add(pnlVeCu, gbc);

        // THÔNG TIN VÉ MỚI
        gbc.gridy = 4;
        JPanel pnlVeMoi = new JPanel();
        pnlVeMoi.setLayout(new BoxLayout(pnlVeMoi, BoxLayout.Y_AXIS));
        pnlVeMoi.setOpaque(false);
        pnlVeMoi.setBorder(new EmptyBorder(5, 5, 5, 5));
        pnlVeMoi.setName("PNL_NEW_INFO_" + ve.getMaVe());

        JLabel lblChuyenMoi = new JLabel("Chuyến đang chọn: [Chưa chọn]");
        lblChuyenMoi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblChuyenMoi.setForeground(new Color(0, 100, 0));
        lblChuyenMoi.setName("LBL_NEW_ROUTE");

        JLabel lblTgMoi = new JLabel("Thời gian đi: --");
        lblTgMoi.setName("LBL_NEW_TIME");

        JLabel lblChoMoi = new JLabel("Chỗ mới: --");
        lblChoMoi.setName("LBL_NEW_SEAT");

        pnlVeMoi.add(lblChuyenMoi);
        pnlVeMoi.add(Box.createVerticalStrut(2));
        pnlVeMoi.add(lblTgMoi);
        pnlVeMoi.add(Box.createVerticalStrut(2));
        pnlVeMoi.add(lblChoMoi);
        pnlContent.add(pnlVeMoi, gbc);

        panel.add(pnlContent, BorderLayout.CENTER);

        // FOOTER
        JPanel pnlFooter = new JPanel(new GridLayout(1, 2, 5, 2));
        pnlFooter.setOpaque(false);
        pnlFooter.setBorder(new EmptyBorder(5, 15, 5, 15));
        JLabel lblGiaCu = new JLabel("Giá cũ: " + String.format("%,.0f", ve.getGiaVe()) + "VNĐ");
        lblGiaCu.setForeground(Color.RED);
        JLabel lblGiaMoi = new JLabel("Giá mới: 0");
        lblGiaMoi.setForeground(new Color(0, 123, 255));
        lblGiaMoi.setName("LBL_GIA_" + ve.getMaVe());
        lblGiaMoi.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlFooter.add(lblGiaCu);
        pnlFooter.add(lblGiaMoi);
        panel.add(pnlFooter, BorderLayout.SOUTH);

        return panel;
    }

    private String formatToa(String maToa) {
        if (maToa == null) return "";
        if (maToa.contains("-")) {
            return maToa.substring(maToa.lastIndexOf("-") + 1);
        }
        return maToa;
    }

    private JLabel createLabel(String text, Font f) {
        JLabel l = new JLabel(text); l.setFont(f); return l;
    }

    private void hienThiDanhSachKhachHang() {
        pnlDanhSachKhachHang.removeAll();
        bgKhachHang = new ButtonGroup();
        mapRadioButtons.clear();

        for (ThongTinVeDTO ve : listVeCanDoi) {
            pnlDanhSachKhachHang.add(taoPanelThongTinKhach(ve));
        }
        if (maVeDangActive == null && !listVeCanDoi.isEmpty()) {
            maVeDangActive = listVeCanDoi.get(0).getMaVe();
            SwingUtilities.invokeLater(() -> {
                if(mapRadioButtons.containsKey(maVeDangActive)) {
                    mapRadioButtons.get(maVeDangActive).setSelected(true);
                }
            });
        }
        pnlDanhSachKhachHang.revalidate();
        pnlDanhSachKhachHang.repaint();
    }

    private void xuLyChuyenKhachHang(String maVeMoi) {
        this.maVeDangActive = maVeMoi;

        // [FIX TỐI ƯU] Khi chuyển khách hàng, chỉ cần update lại màu sắc trên UI có sẵn
        // Không load lại DB trừ khi chưa có dữ liệu
        if (maToaHienTai != null && maChuyenTauHienTai != null) {
            if (seatButtonsMap.isEmpty()) {
                // Nếu chưa có sơ đồ thì mới load DB
                String loaiToa = layLoaiToa(maToaHienTai);
                String gaDi = ((Ga) cbGaDi.getSelectedItem()).getMaGa();
                String gaDen = ((Ga) cbGaDen.getSelectedItem()).getMaGa();
                List<ChoDat> listGhe = choDatDao.getDanhSachChoDatTheoPhanChanh(maToaHienTai, maChuyenTauHienTai, gaDi, gaDen);
                if (!listGhe.isEmpty()) {
                    if (loaiToa.toLowerCase().contains("giường")) veSoDoGiuongNam(listGhe);
                    else veSoDoGheNgoi(listGhe);
                }
            } else {
                // Nếu đã có sơ đồ, chỉ update màu
                capNhatMauSacGheNhanh();
            }
        }
    }

    // =========================================================================
    // VẼ SƠ ĐỒ GHẾ
    // =========================================================================
    private void veSoDoGheNgoi(List<ChoDat> listGhe) {
        pnlSoDoGhe.removeAll();
        tatCaChoDatToaHienTai.clear();
        seatButtonsMap.clear();

        if (listGhe == null || listGhe.isEmpty()) {
            JLabel lblTrong = new JLabel("Toa này chưa có dữ liệu ghế.");
            lblTrong.setBorder(new EmptyBorder(20, 20, 20, 20));
            pnlSoDoGhe.add(lblTrong);
            pnlSoDoGhe.revalidate(); pnlSoDoGhe.repaint();
            return;
        }

        pnlSoDoGhe.setLayout(new BoxLayout(pnlSoDoGhe, BoxLayout.Y_AXIS));
        pnlSoDoGhe.setOpaque(true);
        pnlSoDoGhe.setBackground(Color.WHITE);
        pnlSoDoGhe.setBorder(new EmptyBorder(5, 5, 0, 5));

        int soHang = 4;
        List<ChoDat> day1 = new ArrayList<>(), day2 = new ArrayList<>(), day3 = new ArrayList<>(), day4 = new ArrayList<>();
        for(ChoDat c : listGhe) {
            tatCaChoDatToaHienTai.put(c.getMaCho(), c);
            try {
                int stt = Integer.parseInt(c.getSoCho());
                int hang = (stt - 1) % soHang + 1;
                if(hang==1) day1.add(c); else if(hang==2) day2.add(c); else if(hang==3) day3.add(c); else day4.add(c);
            } catch (Exception e) {
                System.out.println("Lỗi parse số ghế: " + c.getSoCho());
            }
        }
        int soCot = (int) Math.ceil((double)listGhe.size()/4);

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        container.setOpaque(false);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Hàng 1-2
        content.add(taoContainerHaiHang(day1, day2, soCot, KICH_THUOC_GHE_VUONG));
        content.add(Box.createVerticalStrut(40));
        // Hàng 3-4
        content.add(taoContainerHaiHang(day3, day4, soCot, KICH_THUOC_GHE_VUONG));

        container.add(content);
        pnlSoDoGhe.add(container);

        pnlSoDoGhe.revalidate(); pnlSoDoGhe.repaint();
    }

    private JPanel taoContainerHaiHang(List<ChoDat> d1, List<ChoDat> d2, int cols, Dimension dim) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel p1 = new JPanel(new GridLayout(1, cols, 2, 2));
        for(ChoDat c : d1) p1.add(taoNutChoDat(c, dim));

        JPanel p2 = new JPanel(new GridLayout(1, cols, 2, 2));
        for(ChoDat c : d2) p2.add(taoNutChoDat(c, dim));

        p1.setOpaque(false); p2.setOpaque(false);
        p.add(p1); p.add(Box.createVerticalStrut(2)); p.add(p2);
        return p;
    }

    private void veSoDoGiuongNam(List<ChoDat> listGhe) {
        pnlSoDoGhe.removeAll();
        tatCaChoDatToaHienTai.clear();
        seatButtonsMap.clear();
        if (listGhe == null || listGhe.isEmpty()) return;

        pnlSoDoGhe.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnlSoDoGhe.setBorder(new EmptyBorder(10,5,10,5));

        listGhe.sort(Comparator.comparing(ChoDat::getKhoang).thenComparing(ChoDat::getTang).thenComparing(ChoDat::getSoCho));
        Map<Integer, List<ChoDat>> khoangs = listGhe.stream().collect(Collectors.groupingBy(ChoDat::getKhoang, LinkedHashMap::new, Collectors.toList()));

        for(Map.Entry<Integer, List<ChoDat>> e : khoangs.entrySet()) {
            JPanel pK = new JPanel(new GridLayout(3, 2, 5, 5));
            pK.setBorder(BorderFactory.createTitledBorder("Khoang " + e.getKey()));
            for(ChoDat c : e.getValue()) {
                tatCaChoDatToaHienTai.put(c.getMaCho(), c);
                pK.add(taoNutChoDat(c, KICH_THUOC_GIUONG_NAM));
            }
            pnlSoDoGhe.add(pK);
            pnlSoDoGhe.add(Box.createHorizontalStrut(10));
        }
        pnlSoDoGhe.revalidate(); pnlSoDoGhe.repaint();
    }

    private JButton taoNutChoDat(ChoDat cho, Dimension d) {
        JButton btn = new JButton(cho.getSoCho());
        btn.setPreferredSize(d);
        btn.setMinimumSize(d);
        btn.setMaximumSize(d);
        btn.setFont(new Font("Arial", Font.BOLD, 12));

        applyColorLogic(btn, cho); // Tô màu lần đầu

        btn.addActionListener(e -> xuLyChonGhe(btn, cho)); // Gắn sự kiện tối ưu

        seatButtonsMap.put(cho.getMaCho(), btn); // Lưu vào map để dùng lại
        return btn;
    }

    private void applyColorLogic(JButton btn, ChoDat choHienTai) {
        String maChoHienTai = choHienTai.getMaCho();
        boolean isGheMoi = false;
        String owner = null;

        for(Map.Entry<String, ChoDat> e : mapGheMoi.entrySet()) {
            String maVeKhachHang = e.getKey();
            ChoDat gheDaChon = e.getValue();

            if(gheDaChon.getMaCho().equals(maChoHienTai)) {
                String maChuyenCuaKhach = mapVeToMaChuyen.get(maVeKhachHang);
                if (maChuyenCuaKhach != null && maChuyenCuaKhach.equals(maChuyenTauHienTai)) {
                    isGheMoi = true;
                    owner = maVeKhachHang;
                    break;
                }
            }
        }

        if (isGheMoi) {
            if (owner.equals(maVeDangActive)) {
                btn.setBackground(COLOR_DANG_CHON);
                btn.setForeground(Color.WHITE);
                btn.setEnabled(true);
            } else {
                btn.setBackground(COLOR_KHACH_KHAC_GIU);
                btn.setForeground(Color.BLACK);
                btn.setEnabled(false);
            }
        } else if (choHienTai.isDaDat()) {
            btn.setBackground(COLOR_DA_BAN_NGUOI_LA);
            btn.setForeground(Color.WHITE);
            btn.setEnabled(false);
        } else {
            btn.setBackground(COLOR_GHE_TRONG);
            btn.setForeground(Color.BLACK);
            btn.setEnabled(maVeDangActive != null);
        }
    }

    private boolean checkCungChuyenTau() {
        if(mapVeToMaChuyen.isEmpty()) return true;
        for(Map.Entry<String, String> entry : mapVeToMaChuyen.entrySet()) {
            String maVeKhachKhac = entry.getKey();
            String maChuyenKhachKhac = entry.getValue();
            if(maVeKhachKhac.equals(maVeDangActive)) continue;
            if(!maChuyenKhachKhac.equals(maChuyenTauHienTai)) {
                return false;
            }
        }
        return true;
    }

    private void autoChuyenNguoiTiepTheo() {
        int indexCurrent = -1;
        for(int i=0; i<listVeCanDoi.size(); i++) {
            if(listVeCanDoi.get(i).getMaVe().equals(maVeDangActive)) {
                indexCurrent = i; break;
            }
        }
        if(indexCurrent != -1 && indexCurrent < listVeCanDoi.size() - 1) {
            String nextMaVe = listVeCanDoi.get(indexCurrent + 1).getMaVe();
            JRadioButton rdoNext = mapRadioButtons.get(nextMaVe);
            if(rdoNext != null) rdoNext.doClick();
        }
    }

    // =========================================================================
    // XỬ LÝ SỰ KIỆN CHỌN GHẾ (ĐÃ TỐI ƯU HÓA HOÀN TOÀN)
    // =========================================================================
    private void xuLyChonGhe(JButton btn, ChoDat cho) {
        if (maVeDangActive == null) return;

        boolean isBoChon = false;

        // 1. Logic bỏ chọn
        if (mapGheMoi.containsKey(maVeDangActive) && mapGheMoi.get(maVeDangActive).getMaCho().equals(cho.getMaCho())) {
            mapGheMoi.remove(maVeDangActive);
            mapVeToMaChuyen.remove(maVeDangActive);
            capNhatThongTinMoiChoKhach(maVeDangActive, 0, null);
            isBoChon = true;
        }
        else {
            // 2. Logic chọn mới
            if(!checkCungChuyenTau()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn cùng chuyến tàu với các hành khách khác!", "Sai chuyến tàu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Nếu khách đang chọn ghế khác thì xóa ghế cũ trước
            if (mapGheMoi.containsKey(maVeDangActive)) {
                mapGheMoi.remove(maVeDangActive);
            }

            mapGheMoi.put(maVeDangActive, cho);
            mapVeToMaChuyen.put(maVeDangActive, maChuyenTauHienTai);

            // TÍNH GIÁ BẤT ĐỒNG BỘ (ASYNC) ĐỂ KHÔNG ĐƠ UI
            tinhGiaVaCapNhatUIAsync(cho);
        }

        // [TỐI ƯU QUAN TRỌNG] Chỉ đổi màu các nút, không load lại DB, không vẽ lại UI
        capNhatMauSacGheNhanh();

        // Tự động chuyển người (nếu không phải là bỏ chọn)
        if (!isBoChon) {
            autoChuyenNguoiTiepTheo();
        }
    }

    // [METHOD MỚI] Cập nhật màu sắc siêu nhanh
    private void capNhatMauSacGheNhanh() {
        for (Map.Entry<String, JButton> entry : seatButtonsMap.entrySet()) {
            String maCho = entry.getKey();
            JButton btnGhe = entry.getValue();
            ChoDat choInfo = tatCaChoDatToaHienTai.get(maCho);

            if (choInfo != null) {
                applyColorLogic(btnGhe, choInfo);
            }
        }
        pnlSoDoGhe.repaint();
    }

    // [METHOD MỚI] Tính giá vé chạy ngầm (SwingWorker)
    private void tinhGiaVaCapNhatUIAsync(ChoDat cho) {
        String currentMaVe = maVeDangActive;
        String maLoaiVe = getMaLoaiVeOfActiveCustomer();

        // Set giá trị -1 để UI hiện chữ "Đang tính..."
        capNhatThongTinMoiChoKhach(currentMaVe, -1, cho);

        new SwingWorker<Long, Void>() {
            @Override
            protected Long doInBackground() throws Exception {
                // Tác vụ nặng chạy ở đây
                return tinhGiaVeTau(cho, maLoaiVe);
            }

            @Override
            protected void done() {
                try {
                    long gia = get();
                    // Cập nhật lại UI khi đã có kết quả
                    capNhatThongTinMoiChoKhach(currentMaVe, gia, cho);
                } catch (Exception e) {
                    e.printStackTrace();
                    capNhatThongTinMoiChoKhach(currentMaVe, 0, cho); // Lỗi thì về 0
                }
            }
        }.execute();
    }

    // --- RESET ---
    private void xuLyReset() {
        if(mapGheMoi.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tất cả các ghế đã chọn không?",
                "Xác nhận làm mới", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        mapGheMoi.clear();
        mapVeToMaChuyen.clear();

        for (ThongTinVeDTO ve : listVeCanDoi) {
            capNhatThongTinMoiChoKhach(ve.getMaVe(), 0, null);
        }

        if (!listVeCanDoi.isEmpty()) {
            maVeDangActive = listVeCanDoi.get(0).getMaVe();
            if (mapRadioButtons.containsKey(maVeDangActive)) {
                mapRadioButtons.get(maVeDangActive).setSelected(true);
            }
        }

        // Reset thì update lại màu sắc nhanh
        capNhatMauSacGheNhanh();
    }

    private void capNhatThongTinMoiChoKhach(String maVe, long gia, ChoDat choMoi) {
        for (Component c : pnlDanhSachKhachHang.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                JPanel body = (JPanel) p.getComponent(1);

                List<Component> comps = getComponentsInContainer(body);
                for(Component bc : comps) {
                    if(bc instanceof JPanel && ("PNL_NEW_INFO_" + maVe).equals(bc.getName())) {
                        JPanel pInfo = (JPanel) bc;

                        JLabel lblChuyen = null, lblTime = null, lblCho = null;

                        for(Component ic : pInfo.getComponents()) {
                            if("LBL_NEW_ROUTE".equals(ic.getName())) lblChuyen = (JLabel) ic;
                            else if("LBL_NEW_TIME".equals(ic.getName())) lblTime = (JLabel) ic;
                            else if("LBL_NEW_SEAT".equals(ic.getName())) lblCho = (JLabel) ic;
                        }

                        if(choMoi != null && lblChuyen != null) {
                            Ga gDi = (Ga) cbGaDi.getSelectedItem();
                            Ga gDen = (Ga) cbGaDen.getSelectedItem();
                            ChuyenTau ctSelected = getChuyenTauHienTai();
                            String ngayGio = "--";
                            if(ctSelected != null) {
                                ngayGio = ctSelected.getNgayKhoiHanh().format(DATE_FORMATTER) + " - " + ctSelected.getGioKhoiHanh().format(TIME_FORMATTER);
                            }
                            lblChuyen.setText("Chuyến đang chọn: " + gDi.getTenGa() + " -> " + gDen.getTenGa());
                            lblTime.setText("Thời gian đi: " + ngayGio);
                            lblCho.setText("Chỗ mới: Toa " + formatToa(choMoi.getMaToa()) + " - Chỗ số " + choMoi.getSoCho());
                        } else if(lblChuyen != null){
                            lblChuyen.setText("Chuyến đang chọn: [Chưa chọn]");
                            lblTime.setText("Thời gian đi: --");
                            lblCho.setText("Chỗ mới: --");
                        }
                    }
                }

                JPanel f = (JPanel) p.getComponent(2);
                for (Component fc : f.getComponents()) {
                    if (fc instanceof JLabel && ("LBL_GIA_" + maVe).equals(fc.getName())) {
                        // [FIX HIỂN THỊ] Hỗ trợ trạng thái đang tính toán
                        if (gia == -1) {
                            ((JLabel) fc).setText("Giá mới: Đang tính...");
                            ((JLabel) fc).setForeground(Color.ORANGE);
                        } else {
                            ((JLabel) fc).setText("Giá mới: " + (gia > 0 ? String.format("%,d", gia) + " VNĐ" : "0 VNĐ"));
                            ((JLabel) fc).setForeground(new Color(0, 123, 255));
                        }
                    }
                }
            }
        }
    }

    // --- HELPER METHODS ---
    private List<Component> getComponentsInContainer(Container container) {
        List<Component> list = new ArrayList<>();
        for (Component c : container.getComponents()) {
            list.add(c);
            if (c instanceof Container) list.addAll(getComponentsInContainer((Container) c));
        }
        return list;
    }

    private ChuyenTau getChuyenTauHienTai() {
        if(maChuyenTauHienTai == null) return null;
        for(ChuyenTau ct : ketQuaTimKiem) {
            if(ct.getMaChuyenTau().equals(maChuyenTauHienTai)) return ct;
        }
        return null;
    }

    private String layLoaiToa(String maToa) {
        try {
            if (maChuyenTauHienTai == null) return "Ghế mềm điều hòa";
            String maTau = null;
            for (ChuyenTau ct : ketQuaTimKiem) {
                if (maChuyenTauHienTai.equals(ct.getMaChuyenTau())) { maTau = ct.getMaTau(); break; }
            }
            if (maTau == null) return "Ghế mềm điều hòa";
            List<Toa> toas = toaDao.layToaTheoMaTau(maTau);
            if (toas != null) {
                for (Toa t : toas) { if (t.getMaToa().equals(maToa)) return t.getLoaiToa(); }
            }
        } catch (Exception e) {}
        return "Ghế mềm điều hòa";
    }

    private long tinhGiaVeTau(ChoDat cho, String maLoaiVe) throws Exception {
        Ga gaDi = (Ga) cbGaDi.getSelectedItem();
        Ga gaDen = (Ga) cbGaDen.getSelectedItem();

        if (gaDi == null || gaDen == null || maChuyenTauHienTai == null) {
            throw new Exception("Thông tin chuyến tàu hoặc ga chưa đầy đủ.");
        }

        long giaGoc = NghiepVuTinhGiaVe.tinhGiaVe(
                gaDi.getMaGa(),
                gaDen.getMaGa(),
                maChuyenTauHienTai,
                cho,
                maLoaiVe
        );

        return giaGoc + PHI_DOI_VE;
    }

    private String formatLoaiVeHienThi(LoaiVe lv) { return lv.getTenLoai(); }

    private String getMaLoaiVeOfActiveCustomer() {
        for(ThongTinVeDTO v : listVeCanDoi) {
            if(v.getMaVe().equals(maVeDangActive)) return mapReverseLoaiVe.getOrDefault(v.getTenLoaiVe(), "VT01");
        }
        return "VT01";
    }

    private void styleNutChinh(JButton btn) {
        btn.setBackground(COLOR_BLUE_LIGHT); btn.setForeground(Color.WHITE); btn.setPreferredSize(new Dimension(110, 25));
    }

    // --- TÌM KIẾM ---
    private void timKiemChuyenTau() {
        Ga gDi = (Ga) cbGaDi.getSelectedItem();
        Ga gDen = (Ga) cbGaDen.getSelectedItem();
        java.util.Date d = dateChooserNgayDi.getDate();

        if (gDi.getMaGa().equals(gDen.getMaGa())) { JOptionPane.showMessageDialog(this, "Ga trùng!"); return; }
        if (d == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày đi!"); return; }

        pnlToa.removeAll(); pnlToa.revalidate(); pnlToa.repaint();
        pnlSoDoGhe.removeAll(); pnlSoDoGhe.revalidate(); pnlSoDoGhe.repaint();
        maChuyenTauHienTai = null; maToaHienTai = null;
        lastSelectedChuyenTauPanel = null; lastSelectedToaButton = null;
        // Reset map cache khi tìm kiếm mới
        seatButtonsMap.clear();
        tatCaChoDatToaHienTai.clear();

        ChuyenTauDao dao = new ChuyenTauDao();
        ketQuaTimKiem = dao.timChuyenTauTheoGaVaNgayDi(gDi.getMaGa(), gDen.getMaGa(), SQL_DATE_FORMAT.format(d));

        pnlChuyenTau.removeAll();
        if (ketQuaTimKiem.isEmpty()) {
            pnlChuyenTau.add(new JLabel("Không tìm thấy tàu."));
        } else {
            for(ChuyenTau ct : ketQuaTimKiem) {
                String t1 = ct.getNgayKhoiHanh().format(DateTimeFormatter.ofPattern("dd/MM")) + " " + ct.getGioKhoiHanh().format(TIME_FORMATTER);
                String t2 = ct.getNgayDenDuKien().format(DateTimeFormatter.ofPattern("dd/MM")) + " " + ct.getGioDenDuKien().format(TIME_FORMATTER);
                VeSoDoTau v = new VeSoDoTau(ct.getMaChuyenTau().split("_")[0], t1, t2);
                JPanel p = new JPanel(new BorderLayout()); p.add(v); p.setBackground(Color.WHITE);
                p.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { chonChuyenTau(ct, p); }});
                pnlChuyenTau.add(p);
            }
        }
        pnlChuyenTau.revalidate(); pnlChuyenTau.repaint();
    }

    private void chonChuyenTau(ChuyenTau ct, JPanel p) {
        if(lastSelectedChuyenTauPanel!=null) lastSelectedChuyenTauPanel.setBackground(Color.WHITE);
        p.setBackground(Color.ORANGE); lastSelectedChuyenTauPanel=p;
        maChuyenTauHienTai=ct.getMaChuyenTau();
        List<Toa> listToa = toaDao.layToaTheoMaTau(ct.getMaTau());
        pnlToa.removeAll();
        for(Toa t : listToa) {
            String so = t.getMaToa().contains("-") ? t.getMaToa().substring(t.getMaToa().lastIndexOf("-")+1) : t.getMaToa();
            JButton b = new JButton("<html><center>Toa "+so+"<br>"+t.getLoaiToa()+"</center></html>");
            b.setPreferredSize(new Dimension(80,50)); b.setBackground(Color.LIGHT_GRAY);
            b.addActionListener(e->chonToa(t,b)); pnlToa.add(b);
        }
        pnlToa.revalidate(); pnlToa.repaint();
        pnlSoDoGhe.removeAll(); pnlSoDoGhe.repaint();
        maToaHienTai=null;
        seatButtonsMap.clear(); // Reset map khi đổi tàu
    }

    private void chonToa(Toa t, JButton b) {
        maToaHienTai=t.getMaToa();
        if(lastSelectedToaButton!=null) { lastSelectedToaButton.setBackground(Color.LIGHT_GRAY); lastSelectedToaButton.setForeground(Color.BLACK); }
        b.setBackground(COLOR_BLUE_LIGHT); b.setForeground(Color.WHITE); lastSelectedToaButton=b;

        String ga = ((Ga)cbGaDi.getSelectedItem()).getMaGa();
        String den = ((Ga)cbGaDen.getSelectedItem()).getMaGa();

        List<ChoDat> l = choDatDao.getDanhSachChoDatTheoPhanChanh(maToaHienTai, maChuyenTauHienTai, ga, den);
        if(t.getLoaiToa().toLowerCase().contains("giường")) veSoDoGiuongNam(l); else veSoDoGheNgoi(l);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTimChuyen) {
            timKiemChuyenTau();
        }
        else if (e.getSource() == btnXacNhanDoi) {
            if (mapGheMoi.size() < listVeCanDoi.size()) {
                int thieu = listVeCanDoi.size() - mapGheMoi.size();
                JOptionPane.showMessageDialog(this,
                        "Bạn chưa chọn đủ ghế mới cho tất cả hành khách!\nVui lòng chọn thêm " + thieu + " ghế nữa.",
                        "Chưa hoàn tất", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Map<String, Long> mapGiaMoi = new HashMap<>();

            try {
                for (ThongTinVeDTO veCu : listVeCanDoi) {
                    String maVe = veCu.getMaVe();
                    ChoDat gheMoi = mapGheMoi.get(maVe);

                    if (gheMoi != null) {
                        String maLoaiVe = veCu.getMaLoaiVe();
                        if (maLoaiVe == null || maLoaiVe.isEmpty()) {
                            maLoaiVe = "VT01";
                        }
                        long giaMoi = tinhGiaVeTau(gheMoi, maLoaiVe);
                        mapGiaMoi.put(maVe, giaMoi);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi tính giá vé mới: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof gui.MainFrame.BanVeDashboard) {
                gui.MainFrame.BanVeDashboard dashboard = (gui.MainFrame.BanVeDashboard) w;
                ManHinhXacNhanDoiVe panelXacNhan = new ManHinhXacNhanDoiVe(listVeCanDoi, mapGheMoi, mapGiaMoi);
                dashboard.themHoacCapNhatCard(panelXacNhan, "xacNhanDoiVe");
                dashboard.chuyenManHinh("xacNhanDoiVe");
            } else {
                JOptionPane.showMessageDialog(this, "Đã tính toán xong. Đang chuyển màn hình xác nhận...");
            }
        }
    }
}