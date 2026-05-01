package JPA_Project.gui;

import JPA_Project.service.KhuyenMaiService;
import JPA_Project.entity.KhuyenMai;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuanLyPanel extends JPanel {
    private final KhuyenMaiService khuyenMaiService;

    private JTable tblKhuyenMai;
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnTaiLai;
    private JLabel lblLoading;

    public QuanLyPanel() {
        this.khuyenMaiService = new KhuyenMaiService();

        initComponents();
        taiLaiDanhSach();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("QUẢN LÝ KHUYẾN MÃI");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(41, 128, 185));
        panel.add(lblTitle);

        lblLoading = new JLabel("");
        lblLoading.setFont(new Font("Arial", Font.ITALIC, 12));
        lblLoading.setForeground(Color.GRAY);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblLoading);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách khuyến mãi"));

        String[] columns = {"Mã KM", "Tên KM", "Loại", "Giá trị giảm", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhuyenMai = new JTable(model);
        tblKhuyenMai.setRowHeight(25);
        tblKhuyenMai.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblKhuyenMai.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblKhuyenMai.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblKhuyenMai.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblKhuyenMai.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblKhuyenMai.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblKhuyenMai.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblKhuyenMai.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(tblKhuyenMai);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnTaiLai = new JButton("Tải lại");
        btnTaiLai.setIcon(UIManager.getIcon("FileView.refreshIcon"));
        btnTaiLai.addActionListener(e -> taiLaiDanhSach());
        panel.add(btnTaiLai);

        btnThem = new JButton("Thêm mới");
        btnThem.setIcon(UIManager.getIcon("FileView.fileIcon"));
        btnThem.addActionListener(e -> themKhuyenMai());
        panel.add(btnThem);

        btnSua = new JButton("Sửa");
        btnSua.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        btnSua.addActionListener(e -> suaKhuyenMai());
        panel.add(btnSua);

        btnXoa = new JButton("Xóa");
        btnXoa.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        btnXoa.setBackground(new Color(204, 0, 0));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.addActionListener(e -> xoaKhuyenMai());
        panel.add(btnXoa);

        return panel;
    }

    private void taiLaiDanhSach() {
        lblLoading.setText("Đang tải dữ liệu...");

        SwingWorker<List<KhuyenMai>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<KhuyenMai> doInBackground() {
                return khuyenMaiService.getAllKhuyenMai();
            }

            @Override
            protected void done() {
                lblLoading.setText("");
                try {
                    hienThiDanhSach(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(QuanLyPanel.this,
                            "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void hienThiDanhSach(List<KhuyenMai> dsKM) {
        DefaultTableModel model = (DefaultTableModel) tblKhuyenMai.getModel();
        model.setRowCount(0);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (KhuyenMai km : dsKM) {
            String loaiKM = "PHAN_TRAM_GIA".equals(km.getLoaiKM()) ? "% Giảm giá" : "Cố định";
            String giaTriGiam = "";
            if (km.getGiaTriGiam() != null) {
                if ("PHAN_TRAM_GIA".equals(km.getLoaiKM())) {
                    giaTriGiam = km.getGiaTriGiam().toString() + "%";
                } else {
                    giaTriGiam = String.format("%.0f VNĐ", km.getGiaTriGiam().doubleValue());
                }
            }

            model.addRow(new Object[]{
                km.getMaKM(),
                km.getTenKM(),
                loaiKM,
                giaTriGiam,
                km.getNgayBatDau() != null ? km.getNgayBatDau().format(dtf) : "",
                km.getNgayKetThuc() != null ? km.getNgayKetThuc().format(dtf) : "",
                km.getTrangThai() != null ? km.getTrangThai() : ""
            });
        }
    }

    private void themKhuyenMai() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm khuyến mãi", true);
        dialog.setSize(450, 420);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtMaKM = new JTextField(20);
        JTextField txtTenKM = new JTextField(20);
        JComboBox<String> cbbLoaiKM = new JComboBox<>(new String[]{"PHAN_TRAM_GIA", "CO_DINH"});
        JTextField txtGiaTriGiam = new JTextField(20);
        JSpinner spnNgayBD = new JSpinner(new SpinnerDateModel());
        JSpinner spnNgayKT = new JSpinner(new SpinnerDateModel());

        panel.add(createFormRow("Mã khuyến mãi:", txtMaKM));
        panel.add(createFormRow("Tên khuyến mãi:", txtTenKM));
        panel.add(createFormRow("Loại khuyến mãi:", cbbLoaiKM));
        panel.add(createFormRow("Giá trị giảm:", txtGiaTriGiam));
        panel.add(createFormRow("Ngày bắt đầu:", spnNgayBD));
        panel.add(createFormRow("Ngày kết thúc:", spnNgayKT));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = new JButton("Lưu");
        btnLuu.setBackground(new Color(0, 153, 76));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.addActionListener(e -> {
            try {
                String maKM = txtMaKM.getText().trim();
                String tenKM = txtTenKM.getText().trim();
                String loaiKM = (String) cbbLoaiKM.getSelectedItem();
                BigDecimal giaTriGiam = new BigDecimal(txtGiaTriGiam.getText().trim());

                java.util.Date ngayBDDate = (java.util.Date) spnNgayBD.getValue();
                java.util.Date ngayKTDate = (java.util.Date) spnNgayKT.getValue();

                LocalDateTime ngayBD = ngayBDDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime ngayKT = ngayKTDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

                khuyenMaiService.taoKhuyenMai(maKM, tenKM, loaiKM, giaTriGiam, ngayBD, ngayKT);
                JOptionPane.showMessageDialog(dialog, "Thêm khuyến mãi thành công!");
                dialog.dispose();
                taiLaiDanhSach();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(ev -> dialog.dispose());

        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void suaKhuyenMai() {
        int selectedRow = tblKhuyenMai.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần sửa!");
            return;
        }

        String maKM = (String) tblKhuyenMai.getValueAt(selectedRow, 0);
        KhuyenMai km = khuyenMaiService.getKhuyenMaiById(maKM).orElse(null);

        if (km == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi!");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa khuyến mãi", true);
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtMaKM = new JTextField(km.getMaKM(), 20);
        txtMaKM.setEditable(false);
        JTextField txtTenKM = new JTextField(km.getTenKM(), 20);
        JComboBox<String> cbbLoaiKM = new JComboBox<>(new String[]{"PHAN_TRAM_GIA", "CO_DINH"});
        cbbLoaiKM.setSelectedItem(km.getLoaiKM());
        JTextField txtGiaTriGiam = new JTextField(km.getGiaTriGiam() != null ? km.getGiaTriGiam().toString() : "", 20);
        JComboBox<String> cbbTrangThai = new JComboBox<>(new String[]{"HOAT_DONG", "HET_HAN", "KHONG_HOAT_DONG"});
        cbbTrangThai.setSelectedItem(km.getTrangThai());

        panel.add(createFormRow("Mã khuyến mãi:", txtMaKM));
        panel.add(createFormRow("Tên khuyến mãi:", txtTenKM));
        panel.add(createFormRow("Loại khuyến mãi:", cbbLoaiKM));
        panel.add(createFormRow("Giá trị giảm:", txtGiaTriGiam));
        panel.add(createFormRow("Trạng thái:", cbbTrangThai));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = new JButton("Lưu");
        btnLuu.setBackground(new Color(0, 153, 76));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.addActionListener(e -> {
            try {
                km.setTenKM(txtTenKM.getText().trim());
                km.setLoaiKM((String) cbbLoaiKM.getSelectedItem());
                km.setGiaTriGiam(new BigDecimal(txtGiaTriGiam.getText().trim()));
                km.setTrangThai((String) cbbTrangThai.getSelectedItem());

                khuyenMaiService.save(km);
                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                dialog.dispose();
                taiLaiDanhSach();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(ev -> dialog.dispose());

        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void xoaKhuyenMai() {
        int selectedRow = tblKhuyenMai.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa khuyến mãi này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String maKM = (String) tblKhuyenMai.getValueAt(selectedRow, 0);
            KhuyenMai km = khuyenMaiService.getKhuyenMaiById(maKM).orElse(null);
            if (km != null) {
                khuyenMaiService.delete(km);
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                taiLaiDanhSach();
            }
        }
    }

    private JPanel createFormRow(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(140, 25));
        panel.add(lbl);
        component.setPreferredSize(new Dimension(220, 25));
        panel.add(component);
        return panel;
    }
}
