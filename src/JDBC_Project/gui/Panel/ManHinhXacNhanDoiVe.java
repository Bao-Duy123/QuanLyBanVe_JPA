package JDBC_Project.gui.Panel;

import control.CaLamViec;
import control.XuLyVeDoi;
import entity.*;
import entity.DTO.ThongTinVeDTO;
import gui.MainFrame.AdminFullDashboard;
import gui.MainFrame.BanVeDashboard;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ManHinhXacNhanDoiVe extends JPanel {

    // --- DỮ LIỆU ---
    private List<ThongTinVeDTO> listVeCu;
    private Map<String, ChoDat> mapGheMoi;
    private Map<String, Long> mapGiaMoi;

    // --- UI COMPONENTS ---
    private JTable tbKhachHang; // Bảng trái
    private JTable tbHoaDon;    // Bảng phải
    private JLabel lblTongTienCu, lblTongTienMoi, lblChenhLech, lblTienBangChu;

    // UI Hóa đơn header
    private JLabel lblMaHoaDon, lblNguoiLapHD, lblDienThoaiNV;
    private JLabel lblTenNguoiDat, lblSdtNguoiDat, lblPhuongThucTT;
    private JLabel lblTongCongLon;

    // UI Thanh toán
    private JComboBox<String> cbHinhThuc;
    private JTextField txtTienKhachDua, txtTienThoiLai;
    private JButton btnXacNhan, btnHuy, btnKetThuc;

    // --- LOGIC ---
    private long tongTienCu = 0;
    private long tongTienMoi = 0;
    private long chenhLech = 0;
    private String maNV, tenNV, sdtNV;

    private final XuLyVeDoi xuLyVeDoi = new XuLyVeDoi();
    private static final NumberFormat VN_MONEY = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final Color MAU_XANH_BTN = new Color(0, 180, 110);
    private static final Color MAU_CAM_BTN = new Color(255, 140, 0);

    public ManHinhXacNhanDoiVe(List<ThongTinVeDTO> listVeCu,
                               Map<String, ChoDat> mapGheMoi,
                               Map<String, Long> mapGiaMoi) {
        this.listVeCu = listVeCu;
        this.mapGheMoi = mapGheMoi;
        this.mapGiaMoi = mapGiaMoi;

        layThongTinNhanVien();
        tinhToanTongTien();
        khoiTaoGiaoDien();
    }

    private void layThongTinNhanVien() {
        NhanVien nv = CaLamViec.getInstance().getNhanVienDangNhap();
        if (nv != null) {
            this.maNV = nv.getMaNV();
            this.tenNV = nv.getHoTen();
            this.sdtNV = nv.getSdt();
        } else {
            this.maNV = "NV--"; this.tenNV = "Admin"; this.sdtNV = "---";
        }
    }

    private void tinhToanTongTien() {
        tongTienCu = 0;
        tongTienMoi = 0;
        for (ThongTinVeDTO veCu : listVeCu) {
            tongTienCu += veCu.getGiaVe();
            if (mapGiaMoi.containsKey(veCu.getMaVe())) {
                tongTienMoi += mapGiaMoi.get(veCu.getMaVe());
            }
        }
        chenhLech = tongTienMoi - tongTienCu;
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel tieuDe = new JLabel("ĐỔI VÉ - XÁC NHẬN THANH TOÁN");
        tieuDe.setFont(tieuDe.getFont().deriveFont(Font.BOLD, 20f));
        tieuDe.setBorder(new EmptyBorder(6, 6, 12, 6));
        add(tieuDe, BorderLayout.NORTH);

        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        chia.setResizeWeight(0.5);
        chia.setDividerSize(8);
        chia.setBorder(null);
        chia.setBackground(getBackground());

        chia.setLeftComponent(taoPanelTrai());
        chia.setRightComponent(taoPanelHoaDon()); // Panel phải (Hóa đơn)

        add(chia, BorderLayout.CENTER);
    }

    // =========================================================================
    // PANEL TRÁI
    // =========================================================================
    private JScrollPane taoPanelTrai() {
        JPanel panelTrai = new JPanel();
        panelTrai.setLayout(new BoxLayout(panelTrai, BoxLayout.Y_AXIS));
        panelTrai.setBackground(Color.WHITE);
        panelTrai.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false), new EmptyBorder(12, 12, 12, 12)));

        panelTrai.add(taoPanelThongTinHanhTrinh());
        panelTrai.add(Box.createVerticalStrut(12));
        panelTrai.add(taoPanelDanhSachKhachHang());
        panelTrai.add(Box.createVerticalStrut(12));
        panelTrai.add(taoPanelTongTien());
        panelTrai.add(Box.createVerticalStrut(12));
        panelTrai.add(taoPanelThanhToan());
        panelTrai.add(Box.createVerticalGlue());

        return new JScrollPane(panelTrai);
    }

    private JPanel taoPanelThongTinHanhTrinh() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Thông tin vé mới"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        ThongTinVeDTO vCu = listVeCu.get(0);

        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 13);
        Font fontNoiDung = new Font("Segoe UI", Font.PLAIN, 13);

        // 1. Dòng tiêu đề "Hành trình:"
        // Top: 5 (Vừa đủ thoáng với đường viền trên)
        gbc.insets = new Insets(5, 10, 2, 5);
        JLabel lblTitle = new JLabel("Hành trình:");
        lblTitle.setFont(fontTieuDe);
        p.add(lblTitle, gbc);

        // 2. Dòng "Chuyến: ..."
        gbc.gridy++;
        gbc.insets = new Insets(2, 10, 2, 5);
        JLabel lblChuyen = new JLabel("Chuyến: " + vCu.getGaDi() + " - " + vCu.getGaDen());
        lblChuyen.setFont(fontNoiDung);
        p.add(lblChuyen, gbc);

        // 3. Dòng "Ngày khởi hành..."
        gbc.gridy++;
        gbc.insets = new Insets(2, 10, 2, 5);
        JLabel lblNgay = new JLabel("Ngày khởi hành: " + vCu.getNgayDiStr());
        lblNgay.setFont(fontNoiDung);
        p.add(lblNgay, gbc);

        // 4. Dòng "Giờ khởi hành..."
        gbc.gridy++;
        gbc.insets = new Insets(2, 10, 2, 5);
        JLabel lblGio = new JLabel("Giờ khởi hành: " + vCu.getGioDiStr());
        lblGio.setFont(fontNoiDung);
        p.add(lblGio, gbc);

        // 5. Dòng "Số lượng vé đổi..."
        // Top: 8 (Tách ra khỏi nhóm thông tin trên một chút cho dễ nhìn)
        // Bottom: 6 (Cách viền dưới một khoảng vừa vặn, không bị sát sàn)
        gbc.gridy++;
        gbc.insets = new Insets(8, 10, 6, 5);
        JLabel lblSoLuong = new JLabel("Số lượng vé đổi: " + listVeCu.size());
        lblSoLuong.setFont(fontTieuDe);
        p.add(lblSoLuong, gbc);

        // Panel rỗng đẩy nội dung lên trên
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        p.add(new JPanel() {{ setOpaque(false); }}, gbc);

        return p;
    }

    private JPanel taoPanelDanhSachKhachHang() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder("Chi tiết đổi chỗ"));

        // BỎ CỘT MŨI TÊN (->)
        String[] cols = {"Khách hàng", "Chỗ Cũ", "Giá Cũ", "Chỗ Mới", "Giá Mới"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        for (ThongTinVeDTO veCu : listVeCu) {
            String maVe = veCu.getMaVe();
            ChoDat gheMoi = mapGheMoi.get(maVe);
            Long giaMoi = mapGiaMoi.get(maVe);

            String choCu = "Toa " + formatToa(veCu.getMaToa()) + "-" + veCu.getSoCho();
            String choMoi = (gheMoi != null) ? "Toa " + formatToa(gheMoi.getMaToa()) + "-" + gheMoi.getSoCho() : "--";

            model.addRow(new Object[]{
                    veCu.getHoTen(),
                    choCu,
                    VN_MONEY.format(veCu.getGiaVe()),
                    choMoi,
                    VN_MONEY.format(giaMoi != null ? giaMoi : 0)
            });
        }

        tbKhachHang = new JTable(model);
        tbKhachHang.setRowHeight(25);
        JScrollPane sp = new JScrollPane(tbKhachHang);
        sp.setPreferredSize(new Dimension(500, 150));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel taoPanelTongTien() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder("Tổng hợp chi phí"));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        lblTongTienCu = new JLabel("Tổng giá vé cũ (Hoàn lại): " + VN_MONEY.format(tongTienCu) + " VNĐ");
        lblTongTienMoi = new JLabel("Tổng giá vé mới đã bao gồm phí đổi (Thu mới): " + VN_MONEY.format(tongTienMoi) + " VNĐ");

        String txtCL; Color colorCL;
        if (chenhLech > 0) {
            txtCL = "KHÁCH CẦN TRẢ THÊM: " + VN_MONEY.format(chenhLech) + " VNĐ";
            colorCL = Color.RED;
        } else if (chenhLech < 0) {
            txtCL = "HOÀN TIỀN : " + VN_MONEY.format(Math.abs(chenhLech)) + " VNĐ";
            colorCL = new Color(0, 100, 0);
        } else {
            txtCL = "KHÔNG CẦN THANH TOÁN";
            colorCL = Color.BLUE;
        }

        lblChenhLech = new JLabel(txtCL);
        lblChenhLech.setFont(new Font("Arial", Font.BOLD, 16));
        lblChenhLech.setForeground(colorCL);

        box.add(lblTongTienCu);
        box.add(Box.createVerticalStrut(5));
        box.add(lblTongTienMoi);
        box.add(Box.createVerticalStrut(10));
        box.add(lblChenhLech);

        p.add(box, BorderLayout.CENTER);
        return p;
    }

    private JPanel taoPanelThanhToan() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder("Thanh toán"));

        // Hình thức (QUAN TRỌNG: Đây là input cho cột PhuongThuc)
        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT)); r1.setOpaque(false);
        r1.add(new JLabel("Hình thức: "));
        cbHinhThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});
        r1.add(cbHinhThuc);
        p.add(r1);

        // Input tiền (Chỉ hiện nếu khách phải trả thêm > 0)
        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT)); r2.setOpaque(false);
        txtTienKhachDua = new JTextField(12);
        txtTienThoiLai = new JTextField("0", 10);
        txtTienThoiLai.setEditable(false);

        if (chenhLech > 0) {
            r2.add(new JLabel("Khách đưa: "));
            r2.add(txtTienKhachDua);
            r2.add(new JLabel("Tiền thối: "));
            r2.add(txtTienThoiLai);

            txtTienKhachDua.setText(String.valueOf(lamTronTien(chenhLech)));
            txtTienKhachDua.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) { tinhTienThoi(); }
            });
            cbHinhThuc.addActionListener(e -> {
                if("Tiền mặt".equals(cbHinhThuc.getSelectedItem())) {
                    txtTienKhachDua.setText(String.valueOf(lamTronTien(chenhLech)));
                    txtTienKhachDua.setEditable(true);
                } else {
                    txtTienKhachDua.setText(String.valueOf(chenhLech));
                    txtTienKhachDua.setEditable(false);
                }
                tinhTienThoi();
            });
            tinhTienThoi();
        } else if (chenhLech < 0) {
            JLabel msg = new JLabel("<html><center>Số tiền hoàn lại: <font size='5' color='green'>"
                    + VN_MONEY.format(Math.abs(chenhLech)) + " VNĐ</font></center></html>");
            r2.add(msg);
            txtTienKhachDua.setVisible(false);
            txtTienThoiLai.setVisible(false);
        } else {
            r2.add(new JLabel("Giao dịch đổi ngang giá."));
            txtTienKhachDua.setVisible(false);
            txtTienThoiLai.setVisible(false);
        }
        p.add(r2);

        // Buttons
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pBtn.setOpaque(false);

        btnHuy = new JButton("Hủy");
        btnHuy.setBackground(new Color(220, 220, 220));
        btnHuy.setPreferredSize(new Dimension(100, 36));
        btnHuy.addActionListener(e -> quayLai());

        btnXacNhan = new JButton("Xác nhận đổi vé");
        btnXacNhan.setBackground(MAU_XANH_BTN);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Arial", Font.BOLD, 14));
        btnXacNhan.setPreferredSize(new Dimension(180, 36));
        btnXacNhan.addActionListener(e -> xuLyLuuDatabase()); // Action xử lý lưu và cập nhật UI phải

        pBtn.add(btnHuy);
        pBtn.add(btnXacNhan);
        p.add(pBtn);

        return p;
    }

    // =========================================================================
    // PANEL PHẢI: PREVIEW HÓA ĐƠN (Ban đầu trống)
    // =========================================================================
    private JPanel taoPanelHoaDon() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, false), new EmptyBorder(12, 12, 12, 12)));

        // HEADER
        JPanel pHeader = new JPanel(new BorderLayout()); pHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Hóa đơn đổi vé");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        pHeader.add(lblTitle, BorderLayout.NORTH);

        JPanel pInfo = new JPanel(new GridLayout(0, 2)); pInfo.setOpaque(false);

        // TRẠNG THÁI BAN ĐẦU: ĐANG CHỜ XỬ LÝ
        pInfo.add(new JLabel("Mã hóa đơn:")); pInfo.add(lblMaHoaDon = new JLabel("[Đang chờ xử lý]"));
        lblMaHoaDon.setForeground(Color.GRAY); // Màu xám biểu thị chưa tạo

        pInfo.add(new JLabel("Người lập:")); pInfo.add(lblNguoiLapHD = new JLabel(tenNV));
        pInfo.add(new JLabel("SĐT NV:")); pInfo.add(lblDienThoaiNV = new JLabel(sdtNV));
        pHeader.add(pInfo, BorderLayout.CENTER);

        // INFO KHÁCH
        JPanel pKhach = new JPanel();
        pKhach.setLayout(new BoxLayout(pKhach, BoxLayout.Y_AXIS));
        pKhach.setOpaque(false);
        pKhach.setBorder(BorderFactory.createTitledBorder("Khách hàng"));

        ThongTinVeDTO k = listVeCu.get(0);
        lblTenNguoiDat = new JLabel("Họ tên: " + k.getHoTen());
        String contact = k.getSoDienThoai().isEmpty() ? k.getCccd() : k.getSoDienThoai();
        lblSdtNguoiDat = new JLabel("SĐT/CCCD: " + contact);
        lblPhuongThucTT = new JLabel("Phương thức: [Chờ thanh toán]");

        pKhach.add(lblTenNguoiDat);
        pKhach.add(lblSdtNguoiDat);
        pKhach.add(lblPhuongThucTT);

        JPanel pTopWrapper = new JPanel(new BorderLayout()); pTopWrapper.setOpaque(false);
        pTopWrapper.add(pHeader, BorderLayout.NORTH);
        pTopWrapper.add(pKhach, BorderLayout.SOUTH);
        p.add(pTopWrapper, BorderLayout.NORTH);

        // TABLE CHI TIẾT (Ban đầu TRỐNG)
        String[] cols = {"STT", "Mã vé", "Loại", "SL", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0); // RowCount = 0
        tbHoaDon = new JTable(model);
        tbHoaDon.setFillsViewportHeight(true);
        p.add(new JScrollPane(tbHoaDon), BorderLayout.CENTER);

        // FOOTER
        JPanel pFooter = new JPanel(new BorderLayout()); pFooter.setOpaque(false);

        JPanel pTong = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pTong.setOpaque(false);
        lblTongCongLon = new JLabel("Chênh lệch: " + VN_MONEY.format(chenhLech));
        lblTongCongLon.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongCongLon.setForeground(chenhLech > 0 ? Color.RED : (chenhLech < 0 ? new Color(0,100,0) : Color.BLUE));
        pTong.add(lblTongCongLon);
        pFooter.add(pTong, BorderLayout.NORTH);

        JPanel pChu = new JPanel(new FlowLayout(FlowLayout.LEFT)); pChu.setOpaque(false);
        lblTienBangChu = new JLabel("Bằng chữ: " + docSo(Math.abs(chenhLech)));
        lblTienBangChu.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pChu.add(lblTienBangChu);
        pFooter.add(pChu, BorderLayout.CENTER);

        btnKetThuc = new JButton("Kết thúc >");
        btnKetThuc.setBackground(MAU_CAM_BTN);
        btnKetThuc.setForeground(Color.WHITE);
        btnKetThuc.setPreferredSize(new Dimension(120, 36));
        btnKetThuc.setEnabled(false); // Chỉ hiện khi xong
        btnKetThuc.addActionListener(e -> ketThuc());

        JPanel pEnd = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pEnd.setOpaque(false);
        pEnd.add(btnKetThuc);
        pFooter.add(pEnd, BorderLayout.SOUTH);

        p.add(pFooter, BorderLayout.SOUTH);
        return p;
    }

    // =========================================================================
    // LOGIC & DATA BINDING
    // =========================================================================

    private void tinhTienThoi() {
        try {
            long dua = Long.parseLong(txtTienKhachDua.getText().trim());
            long thoi = dua - Math.abs(chenhLech);
            txtTienThoiLai.setText(VN_MONEY.format(Math.max(0, thoi)));
        } catch(Exception e) { txtTienThoiLai.setText("0"); }
    }

    private long lamTronTien(long tien) {
        return ((tien + 999) / 1000) * 1000;
    }

    // --- QUAN TRỌNG: HÀM XỬ LÝ LƯU VÀ CẬP NHẬT UI PHẢI ---
    private void xuLyLuuDatabase() {
        if (chenhLech > 0) {
            try {
                long dua = Long.parseLong(txtTienKhachDua.getText().trim());
                if (dua < chenhLech) {
                    JOptionPane.showMessageDialog(this, "Tiền khách đưa không đủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thực hiện giao dịch đổi vé?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Lấy phương thức thanh toán từ ComboBox để lưu vào DB
            String phuongThuc = cbHinhThuc.getSelectedItem().toString();

            // Gọi Controller trả về Kết quả chi tiết
            XuLyVeDoi.KetQuaGiaoDich kq = xuLyVeDoi.thucHienDoiVe(listVeCu, mapGheMoi, mapGiaMoi, maNV, chenhLech, phuongThuc);

            if (kq.thanhCong) {
                // --- CẬP NHẬT UI SAU KHI THÀNH CÔNG ---

                // 1. Cập nhật Mã Hóa Đơn thật
                lblMaHoaDon.setText(kq.maHD);
                lblMaHoaDon.setForeground(new Color(0, 100, 0)); // Xanh lá đậm cho đẹp

                // 2. Cập nhật Phương thức thanh toán hiển thị
                lblPhuongThucTT.setText("Phương thức: " + phuongThuc);

                // 3. Điền dữ liệu vào Bảng Hóa Đơn (Bên phải)
                fillRightTable(kq.danhSachVeMoi);

                JOptionPane.showMessageDialog(this, "Giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // 4. Khóa các nút
                btnXacNhan.setEnabled(false);
                btnHuy.setEnabled(false);
                btnKetThuc.setEnabled(true); // Mở nút Kết thúc
                cbHinhThuc.setEnabled(false);
                if (txtTienKhachDua != null) txtTienKhachDua.setEditable(false);

            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + kq.loiNhan, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi Database: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillRightTable(List<Ve> listVeMoi) {
        DefaultTableModel modelHD = (DefaultTableModel) tbHoaDon.getModel();
        modelHD.setRowCount(0);
        int stt = 1;

        // Dòng vé cũ (Âm)
        for (ThongTinVeDTO veCu : listVeCu) {
            modelHD.addRow(new Object[]{
                    stt++,
                    veCu.getMaVe(), // Mã thật của vé cũ
                    "Trả vé cũ",
                    "-1",
                    VN_MONEY.format(veCu.getGiaVe()),
                    VN_MONEY.format(-veCu.getGiaVe())
            });
        }

        // Dòng vé mới (Dương) - Lấy từ danh sách vé mới thật do Controller trả về
        for (Ve veMoi : listVeMoi) {
            modelHD.addRow(new Object[]{
                    stt++,
                    veMoi.getMaVe(), // <--- MÃ VÉ MỚI THẬT (VD: VE01...)
                    "Đổi vé mới",
                    "1",
                    VN_MONEY.format(veMoi.getGiaVe()),
                    VN_MONEY.format(veMoi.getGiaVe())
            });
        }
    }

    private void quayLai() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if(w instanceof BanVeDashboard) ((BanVeDashboard)w).chuyenManHinh("banVe_cheDoDoiVe");
    }

    private void ketThuc() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if(w instanceof BanVeDashboard) {
            ((BanVeDashboard)w).chuyenManHinh("trangChuNV");
        } else if (w instanceof AdminFullDashboard) {
            ((AdminFullDashboard)w).chuyenManHinh("trangChuQL");
        }
    }

    // --- UTILS ---
    private String formatToa(String ma) {
        return (ma != null && ma.contains("-")) ? ma.substring(ma.lastIndexOf("-") + 1) : ma;
    }

    private static final String[] chuSo = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
    private static final String[] donVi = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ"};
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
        for (int i = 0; i < soNhom; i++) {
            int val = Integer.parseInt(nhomSo.get(i));
            String chu = docBaSo(val);
            if (!chu.isEmpty() && i < donVi.length) {
                ketQua = chu + " " + donVi[i] + (ketQua.isEmpty() ? "" : " ") + ketQua;
            }
        }
        return (ketQua.trim().substring(0, 1).toUpperCase() + ketQua.trim().substring(1) + " đồng").replaceAll("\\s+", " ");
    }
    private String docBaSo(int soBaChuSo) {
        if (soBaChuSo == 0) return "";
        int tram = soBaChuSo / 100;
        int chuc = (soBaChuSo % 100) / 10;
        int donViLe = soBaChuSo % 10;
        String kq = "";
        if (tram > 0) kq += chuSo[tram] + " trăm";
        if (chuc > 1) {
            kq += (tram > 0 ? " " : "") + chuSo[chuc] + " mươi";
            if (donViLe == 1) kq += " mốt"; else if (donViLe == 5) kq += " lăm"; else if (donViLe > 0) kq += " " + chuSo[donViLe];
        } else if (chuc == 1) {
            kq += (tram > 0 ? " " : "") + "mười";
            if (donViLe == 5) kq += " lăm"; else if (donViLe > 0) kq += " " + chuSo[donViLe];
        } else {
            if (tram > 0 && donViLe > 0) kq += " lẻ " + chuSo[donViLe];
            else if (donViLe > 0) kq += (tram > 0 ? " " : "") + chuSo[donViLe];
        }
        return kq;
    }
}