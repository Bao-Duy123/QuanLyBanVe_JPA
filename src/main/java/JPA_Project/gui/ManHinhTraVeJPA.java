package JPA_Project.gui;

import JPA_Project.entity.ChuyenTau;
import JPA_Project.entity.Ga;
import JPA_Project.entity.Ve;
import JPA_Project.repository.VeRepository;
import JPA_Project.repository.GaRepository;
import JPA_Project.repository.ChuyenTauRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ManHinhTraVeJPA - Màn hình trả vé
 */
public class ManHinhTraVeJPA extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color COLOR_RED = new Color(231, 76, 60);
    private static final Color COLOR_BLUE_LIGHT = new Color(74, 184, 237);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private JTable tblKetQua;
    private javax.swing.table.DefaultTableModel modelKetQua;
    private JScrollPane scrollPaneKetQua;
    private List<Ve> dsVeVuaTim;

    private JButton btnTimKiem, btnHuyBo, btnXacNhan;
    private JComboBox<String> cbTimKiemTheo;
    private JTextField txtMaVeHoacSDT;

    private JLabel lblTenKHValue, lblSDTValue, lblTuyenDuongValue, lblToaValue, lblThoiGianValue, lblSoGheValue, lblGiaGocValue, lblTienHoanTraValue;
    private JComboBox<String> cbLyDoTraVe;

    private VeRepository veRepository;
    private Ve veHienTai;

    public ManHinhTraVeJPA() {
        veRepository = new VeRepository();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(BG_COLOR);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);

        initEventHandlers();
        hienThiDuLieuMau();
        xoaTrangThongTin();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Trả vé");
        title.setFont(FONT_TITLE);
        panel.add(title, BorderLayout.WEST);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createSearchPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createTablePanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createTicketInfoPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createReasonAndButtonPanel());
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Danh sách vé tìm thấy", PRIMARY_COLOR));
        panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));

        String[] columns = {"Mã vé", "Tên khách hàng", "Tuyến đường", "Số ghế", "Giá vé"};
        modelKetQua = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblKetQua = new JTable(modelKetQua);
        scrollPaneKetQua = new JScrollPane(tblKetQua);
        panel.add(scrollPaneKetQua, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Tìm kiếm thông tin vé", PRIMARY_COLOR));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchRow.setOpaque(false);

        searchRow.add(new JLabel("Tìm kiếm theo:"));
        cbTimKiemTheo = new JComboBox<>(new String[]{"Mã vé", "Số điện thoại"});
        cbTimKiemTheo.setFont(FONT_PLAIN_14);
        searchRow.add(cbTimKiemTheo);

        txtMaVeHoacSDT = new JTextField(15);
        txtMaVeHoacSDT.setFont(FONT_PLAIN_14);
        searchRow.add(txtMaVeHoacSDT);

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(COLOR_BLUE_LIGHT);
        btnTimKiem.setForeground(Color.WHITE);

        btnTimKiem.setOpaque(true);
        btnTimKiem.setBorderPainted(false);

        btnTimKiem.setFont(FONT_BOLD_14);
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        searchRow.add(btnTimKiem);

        panel.removeAll();
        panel.add(searchRow);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JPanel createTicketInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Thông tin vé", PRIMARY_COLOR));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoGrid = new JPanel(new GridLayout(4, 4, 20, 10));
        infoGrid.setBorder(new EmptyBorder(10, 15, 15, 15));
        infoGrid.setOpaque(false);

        lblTenKHValue = new JLabel();
        addLabelAndValue(infoGrid, "Họ tên khách hàng", lblTenKHValue, "---");
        lblSDTValue = new JLabel();
        addLabelAndValue(infoGrid, "Số điện thoại", lblSDTValue, "---");
        lblTuyenDuongValue = new JLabel();
        addLabelAndValue(infoGrid, "Tuyến đường", lblTuyenDuongValue, "---");
        lblToaValue = new JLabel();
        addLabelAndValue(infoGrid, "Toa", lblToaValue, "---");
        lblThoiGianValue = new JLabel();
        addLabelAndValue(infoGrid, "Thời gian khởi hành", lblThoiGianValue, "---");
        lblSoGheValue = new JLabel();
        addLabelAndValue(infoGrid, "Số ghế", lblSoGheValue, "---");

        lblGiaGocValue = new JLabel();
        addLabelAndValue(infoGrid, "Giá vé gốc", lblGiaGocValue, "---", Color.BLACK, FONT_BOLD_14);
        lblTienHoanTraValue = new JLabel();
        addLabelAndValue(infoGrid, "Số tiền hoàn trả", lblTienHoanTraValue, "---", COLOR_RED, FONT_BOLD_14);

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

        btnHuyBo.setOpaque(true);
        btnHuyBo.setBorderPainted(false);

        btnHuyBo.setForeground(Color.WHITE);
        btnHuyBo.setFont(FONT_BOLD_14);

        btnXacNhan = new JButton("Xác nhận trả vé");
        btnXacNhan.setBackground(PRIMARY_COLOR);

        btnXacNhan.setOpaque(true);
        btnXacNhan.setBorderPainted(false);

        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(FONT_BOLD_14);

        buttonPanel.add(btnHuyBo);
        buttonPanel.add(btnXacNhan);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void initEventHandlers() {
        btnTimKiem.addActionListener(e -> xuLyTimKiemVe());
        btnXacNhan.addActionListener(e -> xuLyHuyVe());
        btnHuyBo.addActionListener(e -> {
            xoaTrangThongTin();
            hienThiDuLieuMau();
        });

        tblKetQua.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblKetQua.getSelectedRow();
                if (row != -1 && dsVeVuaTim != null && row < dsVeVuaTim.size()) {
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

        dsVeVuaTim = veRepository.timVeTheoKhachHang(null, sdt, null, maVe);

        modelKetQua.setRowCount(0);
        xoaTrangThongTin();

        if (dsVeVuaTim != null && !dsVeVuaTim.isEmpty()) {
            for (Ve v : dsVeVuaTim) {
                String tuyen = "---";
                if (v.getChuyenTau() != null && v.getChuyenTau().getGaDi() != null && v.getChuyenTau().getGaDen() != null) {
                    tuyen = v.getChuyenTau().getGaDi().getTenGa() + " - " + v.getChuyenTau().getGaDen().getTenGa();
                }
                String tenKH = v.getTenKhachHang() != null ? v.getTenKhachHang() : "Khách lẻ";
                String ghe = v.getChoDat() != null ? v.getChoDat().getSoCho() : "---";

                modelKetQua.addRow(new Object[]{
                        v.getMaVe(),
                        tenKH,
                        tuyen,
                        ghe,
                        String.format("%,.0f", v.getGiaVe() != null ? v.getGiaVe() : 0)
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé!");
        }
    }

    private void hienThiThongTinVe(Ve ve) {
        String tenKH = ve.getTenKhachHang() != null ? ve.getTenKhachHang() : "N/A";
        String sdt = ve.getKhachHang() != null ? ve.getKhachHang().getSdt() : "---";

        lblTenKHValue.setText(tenKH);
        lblSDTValue.setText(sdt);

        ChuyenTau ct = ve.getChuyenTau();
        if (ct != null && ct.getGaDi() != null && ct.getGaDen() != null) {
            String tuyenDuong = ct.getGaDi().getTenGa() + " - " + ct.getGaDen().getTenGa();
            String thoiGianKH = ct.getNgayKhoiHanh().format(DATE_FORMATTER) + " " + ct.getGioKhoiHanh().format(TIME_FORMATTER);
            lblTuyenDuongValue.setText(tuyenDuong);
            lblThoiGianValue.setText(thoiGianKH);
        } else {
            lblTuyenDuongValue.setText("---");
            lblThoiGianValue.setText("---");
        }

        if (ve.getChoDat() != null) {
            lblToaValue.setText(ve.getChoDat().getMaToa());
            lblSoGheValue.setText(ve.getChoDat().getSoCho());
        } else {
            lblToaValue.setText("---");
            lblSoGheValue.setText("---");
        }

        double giaGoc = ve.getGiaVe() != null ? ve.getGiaVe() : 0;
        double tienHoanTra = tinhTienHoanTraMoi(ve);

        lblGiaGocValue.setText(String.format("%,.0f VNĐ", giaGoc));
        lblTienHoanTraValue.setText(String.format("%,.0f VNĐ", tienHoanTra));
        lblTienHoanTraValue.setForeground(new Color(39, 174, 96));

        btnXacNhan.setEnabled(true);
        cbLyDoTraVe.setEnabled(true);
    }

    private double tinhTienHoanTraMoi(Ve ve) {
        if (ve == null || ve.getChuyenTau() == null) return 0;

        ChuyenTau ct = ve.getChuyenTau();
        LocalDateTime thoiDiemKhoiHanh = LocalDateTime.of(ct.getNgayKhoiHanh(), ct.getGioKhoiHanh());
        LocalDateTime bayGio = LocalDateTime.now();

        long soGioConLai = java.time.Duration.between(bayGio, thoiDiemKhoiHanh).toHours();
        double giaVe = ve.getGiaVe() != null ? ve.getGiaVe() : 0;

        if (soGioConLai >= 24) {
            return giaVe * 0.8;
        } else if (soGioConLai < 0) {
            return 0;
        } else if (soGioConLai >= 4) {
            return giaVe * 0.9;
        } else {
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

        String tienHoanStr = lblTienHoanTraValue.getText();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận hủy vé " + veHienTai.getMaVe() + " và hoàn trả " + tienHoanStr + "?",
                "Xác nhận Trả vé", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = veRepository.capNhatTrangThai(veHienTai.getMaVe(), "DA_HUY");

            if (success) {
                JOptionPane.showMessageDialog(this, "Trả vé thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                hienThiDuLieuMau();
                xoaTrangThongTin();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hienThiDuLieuMau() {
        dsVeVuaTim = veRepository.findTop5DaBanMoiNhatDetailed();

        modelKetQua.setRowCount(0);

        if (dsVeVuaTim != null && !dsVeVuaTim.isEmpty()) {
            for (Ve v : dsVeVuaTim) {
                String tuyen = "---";
                if (v.getChuyenTau() != null && v.getChuyenTau().getGaDi() != null) {
                    tuyen = v.getChuyenTau().getGaDi().getTenGa() + " - " + v.getChuyenTau().getGaDen().getTenGa();
                }

                String tenKH = v.getTenKhachHang() != null ? v.getTenKhachHang() : "Khách lẻ";
                String ghe = v.getChoDat() != null ? v.getChoDat().getSoCho() : "---";

                modelKetQua.addRow(new Object[]{
                        v.getMaVe(),
                        tenKH,
                        tuyen,
                        ghe,
                        String.format("%,.0f", v.getGiaVe() != null ? v.getGiaVe() : 0)
                });
            }
        }
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

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Demo Màn hình Trả vé - JPA");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(new ManHinhTraVeJPA(), BorderLayout.CENTER);

            frame.setContentPane(mainPanel);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
