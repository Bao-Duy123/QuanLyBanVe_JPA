package JDBC_Project.gui.Panel;

import javax.swing.*;


import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dao.VeDAO;
import entity.Ve;
// Import các Entity chi tiết cần thiết để hiển thị
import entity.KhachHang;
import entity.ChuyenTau;
import entity.ChoDat;
import gui.Popup.PopUpBillTraVe;
// Import lớp triển khai DAO (Giả định VeDAO là lớp triển khai)
// import dao.VeDAO; // Vì VeDAO là class triển khai, không cần import VeDAOImpl

/**
 * ManHinhTraVe: Tái hiện giao diện Trả vé theo mẫu, tích hợp logic tìm kiếm/hủy vé.
 * Đã sửa lỗi hiển thị dữ liệu giả lập.
 */
public class ManHinhTraVe extends JPanel {

    // --- CÁC MÀU SẮC VÀ FONT ---
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color COLOR_RED = new Color(231, 76, 60);
    private static final Color COLOR_BLUE_LIGHT = new Color(74, 184, 237);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private JTable tblKetQua;
    private javax.swing.table.DefaultTableModel modelKetQua;
    private JScrollPane scrollPaneKetQua;
    private java.util.List<Ve> dsVeVuaTim; // Lưu danh sách tạm thời
    // --- Components ---
    private JButton btnTimKiem, btnHuyBo, btnXacNhan;
    private JComboBox<String> cbTimKiemTheo;
    private JTextField txtMaVeHoacSDT;

    private JLabel lblTenKHValue, lblSDTValue, lblTuyenDuongValue, lblToaValue, lblThoiGianValue, lblSoGheValue, lblGiaGocValue, lblTienHoanTraValue;
    private JComboBox<String> cbLyDoTraVe;

    // --- DAO & STATE ---
    private VeDAO veDAO;
    private Ve veHienTai;

    public ManHinhTraVe() {
        // --- 1. SETUP ---
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(BG_COLOR);

        // --- 2. KHỞI TẠO DAO ---
        try {
            // SỬ DỤNG LỚP VeDAO TRỰC TIẾP (vì bạn xác nhận VeDAO là lớp triển khai)
            veDAO = new VeDAO();
        } catch (Exception e) {
            System.err.println("Không thể khởi tạo VeDAO: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Lỗi hệ thống: Không thể kết nối với CSDL hoặc khởi tạo DAO.",
                    "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        // 3. THÊM CÁC PANEL
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);

        // 4. THÊM SỰ KIỆN
        initEventHandlers();
        hienThiDuLieuMau();
        // Đặt trạng thái ban đầu
        xoaTrangThongTin();
    }

    // =========================================================================
    // I. UI BUILDERS
    // =========================================================================

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Trả vé");
        title.setFont(FONT_TITLE);
        panel.add(title, BorderLayout.WEST);

        JLabel nvLabel = new JLabel("Trần Nam Sơn");
        nvLabel.setFont(FONT_BOLD_14);
        panel.add(nvLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createSearchPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // THÊM BẢNG HIỂN THỊ DANH SÁCH VÉ Ở ĐÂY
        panel.add(createTablePanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createTicketInfoPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createReasonAndButtonPanel());
        return panel;
    }

    // Hàm tạo bảng mới
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Danh sách vé tìm thấy", PRIMARY_COLOR));
        panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));

        String[] columns = {"Mã vé", "Tên khách hàng", "Tuyến đường", "Số ghế", "Giá vé"};
        modelKetQua = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblKetQua = new JTable(modelKetQua);
        scrollPaneKetQua = new JScrollPane(tblKetQua);
        panel.add(scrollPaneKetQua, BorderLayout.CENTER);

        return panel;
    }

    // --- VÙNG 1: TÌM KIẾM ---
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Tìm kiếm thông tin vé", PRIMARY_COLOR));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchRow.setOpaque(false);

        // 1. Nút Lịch sử Trả vé (ĐÃ XÓA)

        // 2. Tìm kiếm theo
        searchRow.add(new JLabel("Tìm kiếm theo:"));
        cbTimKiemTheo = new JComboBox<>(new String[]{"Mã vé", "Số điện thoại"});
        cbTimKiemTheo.setFont(FONT_PLAIN_14);
        searchRow.add(cbTimKiemTheo);

        // 3. Input
        txtMaVeHoacSDT = new JTextField( 15);
        txtMaVeHoacSDT.setFont(FONT_PLAIN_14);
        searchRow.add(txtMaVeHoacSDT);

        // 4. Nút Tìm kiếm
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(COLOR_BLUE_LIGHT);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(FONT_BOLD_14);
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        searchRow.add(btnTimKiem);

        panel.removeAll();
        panel.add(searchRow);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    // --- VÙNG 2: THÔNG TIN VÉ ---
    private JPanel createTicketInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Thông tin vé", PRIMARY_COLOR));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoGrid = new JPanel(new GridLayout(4, 4, 20, 10));
        infoGrid.setBorder(new EmptyBorder(10, 15, 15, 15));
        infoGrid.setOpaque(false);

        lblTenKHValue = new JLabel(); addLabelAndValue(infoGrid, "Họ tên khách hàng", lblTenKHValue, "---");
        lblSDTValue = new JLabel(); addLabelAndValue(infoGrid, "Số điện thoại", lblSDTValue, "---");
        lblTuyenDuongValue = new JLabel(); addLabelAndValue(infoGrid, "Tuyến đường", lblTuyenDuongValue, "---");
        lblToaValue = new JLabel(); addLabelAndValue(infoGrid, "Toa", lblToaValue, "---");
        lblThoiGianValue = new JLabel(); addLabelAndValue(infoGrid, "Thời gian khởi hành", lblThoiGianValue, "---");
        lblSoGheValue = new JLabel(); addLabelAndValue(infoGrid, "Số ghế", lblSoGheValue, "---");

        lblGiaGocValue = new JLabel(); addLabelAndValue(infoGrid, "Giá vé gốc", lblGiaGocValue, "---", Color.BLACK, FONT_BOLD_14);
        lblTienHoanTraValue = new JLabel(); addLabelAndValue(infoGrid, "Số tiền hoàn trả", lblTienHoanTraValue, "---", COLOR_RED, FONT_BOLD_14);

        panel.add(infoGrid, BorderLayout.CENTER);
        return panel;
    }

    private void addLabelAndValue(JPanel grid, String labelText, JLabel valueLabel, String initialValue) {
        addLabelAndValue(grid, labelText, valueLabel, initialValue, Color.BLACK, FONT_PLAIN_14);
    }

    private void addLabelAndValue(JPanel grid, String labelText, JLabel valueLabel, String initialValue, Color valueColor, Font valueFont) {
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_PLAIN_14);
        grid.add(label);

        valueLabel.setText(initialValue);
        valueLabel.setFont(valueFont);
        valueLabel.setForeground(valueColor);
        grid.add(valueLabel);
    }

    private JPanel createReasonAndButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel reasonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        reasonPanel.setBackground(Color.WHITE);
        reasonPanel.setBorder(createStyledBorder("Lý do trả vé", PRIMARY_COLOR));

        reasonPanel.add(new JLabel("Chọn lý do trả vé"));
        cbLyDoTraVe = new JComboBox<>(new String[]{"Chọn lý do trả vé", "Khách hàng thay đổi kế hoạch", "Lỗi nhập liệu", "Chuyến tàu bị hủy"});
        cbLyDoTraVe.setFont(FONT_PLAIN_14);
        reasonPanel.add(cbLyDoTraVe);

        panel.add(reasonPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        btnHuyBo = new JButton("Hủy bỏ");
        btnHuyBo.setBackground(COLOR_RED);
        btnHuyBo.setForeground(Color.WHITE);
        btnHuyBo.setFont(FONT_BOLD_14);

        btnXacNhan = new JButton("Xác nhận trả vé");
        btnXacNhan.setBackground(PRIMARY_COLOR);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(FONT_BOLD_14);

        buttonPanel.add(btnHuyBo);
        buttonPanel.add(btnXacNhan);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private TitledBorder createStyledBorder(String title, Color color) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONT_BOLD_14,
                color
        );
    }

    // =========================================================================
    // II. LOGIC & EVENTS
    // =========================================================================

    private void initEventHandlers() {
        btnTimKiem.addActionListener(e -> xuLyTimKiemVe());
        btnXacNhan.addActionListener(e -> xuLyHuyVe());
        btnHuyBo.addActionListener(e -> xoaTrangThongTin());
        // Đã xóa btnLichSuTraVe.addActionListener(e -> xuLyChuyenManHinhLichSu());
        tblKetQua.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblKetQua.getSelectedRow();
                if (row != -1 && dsVeVuaTim != null) {
                    // Lấy đúng đối tượng Ve từ danh sách dựa trên dòng được chọn
                    veHienTai = dsVeVuaTim.get(row);
                    hienThiThongTinVe(veHienTai);
                }
            }
        });
    }


    private void xuLyTimKiemVe() {
        String searchBy = (String) cbTimKiemTheo.getSelectedItem();
        String searchText = txtMaVeHoacSDT.getText().trim();

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin!");
            return;
        }

        String maVe = "Mã vé".equals(searchBy) ? searchText : null;
        String sdt = "Số điện thoại".equals(searchBy) ? searchText : null;

        // Gọi hàm trả về danh sách từ DAO
        dsVeVuaTim = veDAO.timVeTheoKhachHang(null, sdt, null, maVe);

        modelKetQua.setRowCount(0); // Xóa bảng cũ
        xoaTrangThongTin();

        if (dsVeVuaTim != null && !dsVeVuaTim.isEmpty()) {
            for (Ve v : dsVeVuaTim) {
                v.toString();
                String tuyen = "---";
                if(v.getChuyenTauChiTiet() != null) {
                    tuyen = v.getChuyenTauChiTiet().gaDi.tenGa + " - " + v.getChuyenTauChiTiet().gaDen.tenGa;
                }
                modelKetQua.addRow(new Object[]{
                        v.getMaVe(),
                        v.getTenKhachHang(),
                        tuyen,
                        v.getChoDatChiTiet() != null ? v.getChoDatChiTiet().getSoCho() : "---",
                        String.format("%,.0f", v.getGiaVe())
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé!");
        }
    }

    // Trong lớp Form/UI của bạn

    private void hienThiThongTinVe(Ve ve) {
        KhachHang kh = ve.getKhachHangChiTiet();
        ChuyenTau ct = ve.getChuyenTauChiTiet();
        ChoDat cd = ve.getChoDatChiTiet();

        // 1. THÔNG TIN KHÁCH HÀNG
        lblTenKHValue.setText(ve.getTenKhachHang() != null ? ve.getTenKhachHang() : "N/A");
        lblSDTValue.setText(kh != null ? kh.getSdt() : "---");

        // 2. THÔNG TIN CHUYẾN TÀU & GA
        if (ct != null && ct.getGaDi() != null && ct.getGaDen() != null) {
            String tuyenDuong = ct.getGaDi().getTenGa() + " - " + ct.getGaDen().getTenGa();
            String thoiGianKH = ct.getNgayKhoiHanh().toString() + " " + ct.getGioKhoiHanh().toString();

            lblTuyenDuongValue.setText(tuyenDuong);
            lblThoiGianValue.setText(thoiGianKH);
        } else {
            lblTuyenDuongValue.setText("---");
            lblThoiGianValue.setText("---");
        }

        // 3. THÔNG TIN CHỖ ĐẶT
        if (cd != null) {
            lblToaValue.setText(cd.getMaToa());
            lblSoGheValue.setText(cd.getSoCho());
        } else {
            lblToaValue.setText("---");
            lblSoGheValue.setText("---");
        }

        // 4. GIÁ CẢ (Giữ nguyên)
        double giaGoc = ve.getGiaVe();
        double tienHoanTra = tinhTienHoanTraMoi(ve);

        lblGiaGocValue.setText(String.format("%,.0f VNĐ", giaGoc));
        lblTienHoanTraValue.setText(String.format("%,.0f VNĐ", tienHoanTra));

        // Đổi màu xanh để dễ phân biệt
        lblTienHoanTraValue.setForeground(new Color(39, 174, 96));

        btnXacNhan.setEnabled(true);
        cbLyDoTraVe.setEnabled(true);
    }



    private double tinhTienHoanTraMoi(Ve ve) {
        if (ve == null || ve.getChuyenTauChiTiet() == null) return 0;

        ChuyenTau ct = ve.getChuyenTauChiTiet();
        LocalDateTime thoiDiemKhoiHanh = LocalDateTime.of(ct.getNgayKhoiHanh(), ct.getGioKhoiHanh());
        LocalDateTime bayGio = LocalDateTime.now();

        // Tính số giờ còn lại
        long soGioConLai = java.time.Duration.between(bayGio, thoiDiemKhoiHanh).toHours();
        double giaVe = ve.getGiaVe();

        // SỬA LẠI LOGIC TẠI ĐÂY:
        if (soGioConLai >= 24) {
            // TRÊN 24 GIỜ: Nhận 80%
            return giaVe * 0.8;
        } else if (soGioConLai<0)
            return 0;

         else if (soGioConLai >= 4) {
            // TỪ 4 ĐẾN DƯỚI 24 GIỜ: Nhận 90%
            return giaVe * 0.9;
        } else {
            // DƯỚI 4 GIỜ (Bao gồm cả số âm - tàu đã chạy): Nhận 100%
            return giaVe;
        }
    }
    private void xuLyHuyVe() {
        if (veHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm kiếm vé trước khi xác nhận trả vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String lyDo = (String) cbLyDoTraVe.getSelectedItem();
        if (lyDo == null || lyDo.equals("Chọn lý do trả vé")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lý do trả vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy tiền hoàn trả từ label hoặc tính toán lại
        String tienHoanStr = lblTienHoanTraValue.getText();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận hủy vé " + veHienTai.getMaVe() + " và hoàn trả " + tienHoanStr + "?",
                "Xác nhận Trả vé", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (veDAO.huyVe(veHienTai.getMaVe())) {
                JOptionPane.showMessageDialog(this, "Trả vé thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // 1. Tính toán tiền hoàn trả
                double tienHoanTra = tinhTienHoanTraMoi(veHienTai);

                // 2. SỬ DỤNG LẠI BIẾN lyDo (KHÔNG khai báo String ở đầu dòng này nữa)
                // Vì biến lyDo đã được khai báo ở dòng 286 của file ManHinhTraVe.java
                hienThiPopupBill(veHienTai, tienHoanTra, lyDo);

                // 3. Cập nhật lại bảng danh sách vé
                String searchText = txtMaVeHoacSDT.getText().trim();
                if (!searchText.isEmpty()) {
                    xuLyTimKiemVe();
                } else {
                    hienThiDuLieuMau();
                }

                xoaTrangThongTin();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Hàm hiển thị Popup có nền mờ (Overlay) giống hệt màn hình Tra Cứu Hóa Đơn
     */

    /**
     * Hàm bổ trợ lấy mã HD (giữ nguyên để không phải sửa file Ve)
     */
    private void hienThiDuLieuMau() {
        // 1. Gọi hàm lấy 5 vé từ DAO
        dsVeVuaTim = veDAO.getTop5VeMoiNhat();

        // 2. Làm sạch bảng trước khi đổ dữ liệu
        modelKetQua.setRowCount(0);

        // 3. Duyệt danh sách và thêm vào bảng
        if (dsVeVuaTim != null && !dsVeVuaTim.isEmpty()) {
            for (Ve v : dsVeVuaTim) {
                String tuyen = "---";
                // Kiểm tra null cho Chuyến tàu và Ga để tránh lỗi hiển thị
                if(v.getChuyenTauChiTiet() != null && v.getChuyenTauChiTiet().getGaDi() != null) {
                    tuyen = v.getChuyenTauChiTiet().getGaDi().getTenGa() + " - " + v.getChuyenTauChiTiet().getGaDen().getTenGa();
                }

                modelKetQua.addRow(new Object[]{
                        v.getMaVe(),
                        v.getTenKhachHang() != null ? v.getTenKhachHang() : "Khách lẻ",
                        tuyen,
                        v.getChoDatChiTiet() != null ? v.getChoDatChiTiet().getSoCho() : "---",
                        String.format("%,.0f", v.getGiaVe())
                });
            }
        }
    }
    private String layMaHDTuMaVe(String maVe) {
        String maHD = "";
        String sql = "SELECT MaHD FROM ChiTietHoaDon WHERE MaVe = ?";
        try (java.sql.Connection con = database.ConnectDB.getConnection();
             java.sql.PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maVe);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) maHD = rs.getString("MaHD");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return maHD;
    }
    private void hienThiPopupBill(Ve ve, double tienHoanTra, String lyDo) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) return;

        // 1. Khởi tạo Panel hóa đơn trả vé
        PopUpBillTraVe billPanel = new PopUpBillTraVe(ve, tienHoanTra, lyDo);

        // 2. Tạo JDialog để hiển thị như một cửa sổ thực sự
        JDialog dialog = new JDialog(topFrame, "Hóa đơn hoàn trả", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(billPanel, BorderLayout.CENTER);

        // Nút đóng/in
        JButton btnDong = new JButton("Đóng & Hoàn tất");
        btnDong.addActionListener(e -> dialog.dispose());
        JPanel pBtn = new JPanel();
        pBtn.add(btnDong);
        dialog.add(pBtn, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(topFrame);
        dialog.setVisible(true);
    }
    private void xoaTrangThongTin() {
        lblTenKHValue.setText("---");
        lblSDTValue.setText("---");
        lblTuyenDuongValue.setText("---");
        lblToaValue.setText("---");
        lblThoiGianValue.setText("---");
        lblSoGheValue.setText("---");
        lblGiaGocValue.setText("---");
        lblTienHoanTraValue.setText("---");

        cbLyDoTraVe.setSelectedIndex(0);
        txtMaVeHoacSDT.setText("");

        veHienTai = null;
        btnXacNhan.setEnabled(false);
        cbLyDoTraVe.setEnabled(false);
    }

    // =========================================================================
    // III. MAIN TEST
    // =========================================================================

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Demo Màn hình Trả vé");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(new ManHinhTraVe(), BorderLayout.CENTER);

            JPanel mockMenu = new JPanel();
            mockMenu.setBackground(new Color(34, 137, 203));
            mockMenu.setPreferredSize(new Dimension(200, 0));

            JLabel lblTraVe = new JLabel("<html><b style='color: white;'>▶ Trả vé</b></html>");
            lblTraVe.setBorder(new EmptyBorder(10, 10, 10, 10));
            lblTraVe.setBackground(new Color(74, 184, 237));
            lblTraVe.setOpaque(true);
            lblTraVe.setMaximumSize(new Dimension(200, 40));
            lblTraVe.setAlignmentX(Component.LEFT_ALIGNMENT);

            mockMenu.setLayout(new BoxLayout(mockMenu, BoxLayout.Y_AXIS));
            mockMenu.add(Box.createVerticalStrut(200));
            mockMenu.add(lblTraVe);
            mockMenu.add(Box.createVerticalGlue());

            mainPanel.add(mockMenu, BorderLayout.WEST);

            frame.setContentPane(mainPanel);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}