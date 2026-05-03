package JPA_Project.gui;

import JPA_Project.entity.KhuyenMai;
import JPA_Project.repository.KhuyenMaiRepository;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * PopupTaoKhuyenMaiJPA - Popup tạo/sửa khuyến mãi
 */
public class PopupTaoKhuyenMaiJPA extends JDialog implements ActionListener {

    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("###,###,##0");
    
    private static final String LOAI_DK_MIN_GIA = "MIN_GIA";
    private static final String LOAI_DK_MIN_SL = "MIN_SL";
    private static final String LOAI_DK_NONE = "NONE";
    private static final String LOAI_GIAM_PHAN_TRAM = "PHAN_TRAM_GIA";
    private static final String LOAI_GIAM_CO_DINH = "CO_DINH";

    private JTextField txtMaKM;
    private JTextField txtTenKM;
    private JDateChooser dateChooserBatDau;
    private JDateChooser dateChooserKetThuc;
    private JSpinner spinnerPhanTram;
    private JSpinner spinnerTienGiam;
    private JComboBox<String> cbDieuKien;
    private JTextField txtGiaTriDK;
    private JButton btnLuu, btnHuy;
    
    private KhuyenMaiRepository khuyenMaiRepository;
    private ManHinhQuanLyKhuyenMaiJPA parentPanel;
    private String currentMaKM;

    public PopupTaoKhuyenMaiJPA(JFrame parent, ManHinhQuanLyKhuyenMaiJPA parentPanel, String maKM) {
        super(parent, true);
        this.parentPanel = parentPanel;
        this.currentMaKM = maKM;
        this.khuyenMaiRepository = new KhuyenMaiRepository();

        setTitle(maKM == null ? "Tạo Khuyến Mãi Mới" : "Cập Nhật Khuyến Mãi: " + maKM);
        setSize(750, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBackground(BG_COLOR);
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel(maKM == null ? "Tạo Khuyến Mãi" : "Cập Nhật Khuyến Mãi");
        title.setFont(FONT_TITLE);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        contentPane.add(title, BorderLayout.NORTH);

        JPanel formArea = createFormPanel();
        contentPane.add(formArea, BorderLayout.CENTER);

        JPanel actionPanel = createActionPanel();
        contentPane.add(actionPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);

        if (maKM == null) {
            lamMoiForm();
        } else {
            loadDataForEdit(maKM);
        }

        addListenerToSpinners();
    }

    private void addListenerToSpinners() {
        spinnerPhanTram.addChangeListener(e -> {
            if ((Integer) spinnerPhanTram.getValue() > 0 && (Integer) spinnerTienGiam.getValue() > 0) {
                spinnerTienGiam.setValue(0);
            }
        });

        spinnerTienGiam.addChangeListener(e -> {
            if ((Integer) spinnerTienGiam.getValue() > 0 && (Integer) spinnerPhanTram.getValue() > 0) {
                spinnerPhanTram.setValue(0);
            }
        });
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã KM
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Mã KM:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtMaKM = new JTextField(15);
        txtMaKM.setEditable(false);
        fieldsPanel.add(txtMaKM, gbc);

        // Tên KM
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Tên KM:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtTenKM = new JTextField(15);
        fieldsPanel.add(txtTenKM, gbc);

        // Ngày Bắt Đầu
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        dateChooserBatDau = new JDateChooser();
        dateChooserBatDau.setDateFormatString("dd/MM/yyyy");
        fieldsPanel.add(dateChooserBatDau, gbc);

        // Ngày Kết Thúc
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        dateChooserKetThuc = new JDateChooser();
        dateChooserKetThuc.setDateFormatString("dd/MM/yyyy");
        fieldsPanel.add(dateChooserKetThuc, gbc);

        // Giảm theo phần trăm
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 20, 5, 5);

        gbc.gridx = 2; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Giảm (%):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        spinnerPhanTram = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        fieldsPanel.add(spinnerPhanTram, gbc);

        // Giảm theo tiền
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0;
        gbc.insets = new Insets(5, 20, 5, 5);
        fieldsPanel.add(new JLabel("Giảm (VND):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        spinnerTienGiam = new JSpinner(new SpinnerNumberModel(0, 0, 10000000, 10000));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerTienGiam, "###,###,##0");
        spinnerTienGiam.setEditor(editor);
        fieldsPanel.add(spinnerTienGiam, gbc);

        // Điều kiện áp dụng
        JPanel dkPanel = createDieuKienPanel();
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 5, 5);
        fieldsPanel.add(dkPanel, gbc);

        panel.add(fieldsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDieuKienPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Điều kiện áp dụng"));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("Loại điều kiện:"));
        cbDieuKien = new JComboBox<>(new String[]{
                "Không có điều kiện",
                "Hóa đơn tối thiểu (VND)",
                "Số lượng vé tối thiểu"
        });
        cbDieuKien.addActionListener(this);
        panel.add(cbDieuKien);

        panel.add(new JLabel("Giá trị ĐK:"));
        txtGiaTriDK = new JTextField(15);
        txtGiaTriDK.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(txtGiaTriDK);

        txtGiaTriDK.setEnabled(false);
        txtGiaTriDK.setText("");

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.setOpaque(false);

        btnLuu = new JButton(currentMaKM == null ? "Lưu Khuyến Mãi" : "Cập Nhật");
        btnLuu.setFont(FONT_BOLD_14);
        btnLuu.addActionListener(this);

        btnHuy = new JButton("Hủy");
        btnHuy.setFont(FONT_BOLD_14);
        btnHuy.addActionListener(this);

        panel.add(btnLuu);
        panel.add(btnHuy);

        return panel;
    }

    private void loadDataForEdit(String maKM) {
        KhuyenMai km = khuyenMaiRepository.findById(maKM);
        if (km == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy Khuyến Mãi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            lamMoiForm();
            return;
        }

        txtMaKM.setText(km.getMaKM());
        txtTenKM.setText(km.getTenKM());

        if (km.getNgayBatDau() != null) {
            dateChooserBatDau.setDate(Date.from(km.getNgayBatDau().atZone(ZoneId.systemDefault()).toInstant()));
        }
        if (km.getNgayKetThuc() != null) {
            dateChooserKetThuc.setDate(Date.from(km.getNgayKetThuc().atZone(ZoneId.systemDefault()).toInstant()));
        }

        if (LOAI_GIAM_PHAN_TRAM.equals(km.getLoaiKM())) {
            spinnerPhanTram.setValue(km.getGiaTriGiam() != null ? km.getGiaTriGiam().intValue() : 0);
            spinnerTienGiam.setValue(0);
        } else if (LOAI_GIAM_CO_DINH.equals(km.getLoaiKM())) {
            spinnerPhanTram.setValue(0);
            spinnerTienGiam.setValue(km.getGiaTriGiam() != null ? km.getGiaTriGiam().intValue() : 0);
        }

        if (LOAI_DK_MIN_GIA.equals(km.getDkApDung())) {
            cbDieuKien.setSelectedIndex(1);
            txtGiaTriDK.setText(VND_FORMAT.format(km.getGiaTriDK()));
            txtGiaTriDK.setEnabled(true);
        } else if (LOAI_DK_MIN_SL.equals(km.getDkApDung())) {
            cbDieuKien.setSelectedIndex(2);
            txtGiaTriDK.setText(km.getGiaTriDK() != null ? km.getGiaTriDK().intValue() + "" : "0");
            txtGiaTriDK.setEnabled(true);
        } else {
            cbDieuKien.setSelectedIndex(0);
            txtGiaTriDK.setText("");
            txtGiaTriDK.setEnabled(false);
        }
    }

    private void lamMoiForm() {
        txtMaKM.setText("(Mã sẽ tự sinh khi lưu)");
        txtMaKM.setForeground(Color.GRAY);
        txtTenKM.setText("");
        dateChooserBatDau.setDate(null);
        dateChooserKetThuc.setDate(null);
        spinnerPhanTram.setValue(0);
        spinnerTienGiam.setValue(0);
        cbDieuKien.setSelectedIndex(0);
        txtGiaTriDK.setText("");
        txtGiaTriDK.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnHuy) {
            dispose();
        } else if (src == btnLuu) {
            if (currentMaKM == null) {
                handleThemKhuyenMai();
            } else {
                handleCapNhatKhuyenMai();
            }
        } else if (src == cbDieuKien) {
            int dkIndex = cbDieuKien.getSelectedIndex();
            boolean isEnabled = dkIndex != 0;
            txtGiaTriDK.setEnabled(isEnabled);
            if (!isEnabled) {
                txtGiaTriDK.setText("");
            } else if (dkIndex == 1) {
                txtGiaTriDK.setText(VND_FORMAT.format(0));
            } else if (dkIndex == 2) {
                txtGiaTriDK.setText("0");
            }
        }
    }

    private boolean validateAndGetFormData() {
        String tenKM = txtTenKM.getText().trim();
        Date ngayBD = dateChooserBatDau.getDate();
        Date ngayKT = dateChooserKetThuc.getDate();
        int phanTram = (Integer) spinnerPhanTram.getValue();
        int tienGiam = (Integer) spinnerTienGiam.getValue();

        if (tenKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên Khuyến Mãi không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (tenKM.length() < 5 || !tenKM.matches("^[a-zA-Z0-9\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Tên Khuyến Mãi phải có ít nhất 5 ký tự và không chứa ký tự đặc biệt.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (ngayBD == null || ngayKT == null) {
            JOptionPane.showMessageDialog(this, "Ngày không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (ngayKT.before(ngayBD)) {
            JOptionPane.showMessageDialog(this, "Ngày Kết Thúc phải sau Ngày Bắt Đầu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phanTram == 0 && tienGiam == 0) {
            JOptionPane.showMessageDialog(this, "Phải chọn mức giảm giá.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phanTram > 0 && tienGiam > 0) {
            JOptionPane.showMessageDialog(this, "Chỉ được chọn một loại giảm giá.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int dkIndex = cbDieuKien.getSelectedIndex();
        if (dkIndex != 0) {
            String dkValueStr = txtGiaTriDK.getText().trim().replace(",", "");
            if (dkValueStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Giá trị Điều kiện.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            try {
                double val = Double.parseDouble(dkValueStr);
                if (val <= 0) {
                    JOptionPane.showMessageDialog(this, "Giá trị Điều kiện phải > 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Giá trị Điều kiện phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private void handleThemKhuyenMai() {
        if (!validateAndGetFormData()) return;
        
        try {
            KhuyenMai km = createKhuyenMaiFromForm();
            
            // Sinh mã mới
            String maMoi = "KM" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMyy"))
                    + String.format("%03d", (int)(Math.random() * 999));
            km.setMaKM(maMoi);
            
            khuyenMaiRepository.save(km);
            
            JOptionPane.showMessageDialog(this,
                    "Tạo Khuyến Mãi thành công!\nMã: " + maMoi,
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            parentPanel.loadDataToTable();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCapNhatKhuyenMai() {
        if (!validateAndGetFormData()) return;
        
        try {
            KhuyenMai km = createKhuyenMaiFromForm();
            km.setMaKM(currentMaKM);
            
            khuyenMaiRepository.save(km);
            
            JOptionPane.showMessageDialog(this, "Cập Nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.loadDataToTable();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private KhuyenMai createKhuyenMaiFromForm() {
        String tenKM = txtTenKM.getText().trim();
        Date ngayBDDate = dateChooserBatDau.getDate();
        Date ngayKTDate = dateChooserKetThuc.getDate();

        int phanTram = (Integer) spinnerPhanTram.getValue();
        int tienGiam = (Integer) spinnerTienGiam.getValue();

        String loaiKM;
        BigDecimal giaTriGiam;

        if (phanTram > 0) {
            loaiKM = LOAI_GIAM_PHAN_TRAM;
            giaTriGiam = new BigDecimal(phanTram);
        } else {
            loaiKM = LOAI_GIAM_CO_DINH;
            giaTriGiam = new BigDecimal(tienGiam);
        }

        String dkApDung;
        BigDecimal giaTriDK = null;
        String dkValueStr = txtGiaTriDK.getText().trim().replace(",", "");

        int dkIndex = cbDieuKien.getSelectedIndex();
        if (dkIndex == 1) {
            dkApDung = LOAI_DK_MIN_GIA;
            try { giaTriDK = new BigDecimal(dkValueStr); } catch (Exception e) {}
        } else if (dkIndex == 2) {
            dkApDung = LOAI_DK_MIN_SL;
            try { giaTriDK = new BigDecimal(dkValueStr); } catch (Exception e) {}
        } else {
            dkApDung = LOAI_DK_NONE;
        }

        LocalDateTime ngayBD = ngayBDDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime ngayKT = ngayKTDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withHour(23).withMinute(59).withSecond(59);

        String trangThai = ngayBD.isAfter(LocalDateTime.now()) ? "KHONG_HOAT_DONG" : "HOAT_DONG";

        return KhuyenMai.builder()
                .tenKM(tenKM)
                .loaiKM(loaiKM)
                .giaTriGiam(giaTriGiam)
                .dkApDung(dkApDung)
                .giaTriDK(giaTriDK)
                .ngayBatDau(ngayBD)
                .ngayKetThuc(ngayKT)
                .trangThai(trangThai)
                .build();
    }
}
