package JPA_Project.gui;

import JPA_Project.entity.NhanVien;
import JPA_Project.entity.TaiKhoan;
import JPA_Project.service.NhanVienService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.toedter.calendar.JDateChooser;

/**
 * ManHinhQuanLyNhanVienJPA - Panel quản lý nhân viên sử dụng JPA.
 * Chuyển đổi từ JDBC sang JPA, tách biệt Service layer.
 * Hỗ trợ đa luồng với SwingWorker.
 */
public class ManHinhQuanLyNhanVienJPA extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color COLOR_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_YELLOW = new Color(241, 196, 15);
    private static final Color COLOR_ORANGE = new Color(230, 126, 34);
    private static final Color COLOR_RED = new Color(231, 76, 60);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);

    // Service
    private final NhanVienService nhanVienService;

    // Components form
    private JTextField txtHoTen, txtEmail, txtSoCCCD, txtDiaChi, txtTenDangNhap, txtSoDienThoai;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cbChucVu, cbTrangThai;
    private JDateChooser dateNgayVaoLam, dateNgayTao;
    private JRadioButton radNam, radNu;
    private ButtonGroup bgGender;
    private TitledBorder formBorder;
    private JTable table;
    private DefaultTableModel model;
    private JTextArea txtGhiChu;

    // Components summary
    private JLabel lblTongSo, lblNhanVien, lblQuanLy;
    private JLabel lblLoading;

    // Components tìm kiếm
    private JComboBox<String> cbSearchType;
    private JTextField txtSearchInput;
    private JComboBox<String> cbSearchStatus;

    // Trạng thái form
    private String maNV_dangSua = null;

    public ManHinhQuanLyNhanVienJPA() {
        this.nhanVienService = new NhanVienService();
        setLayout(new BorderLayout());

        JPanel centerContentPanel = createCenterContentPanel();
        JPanel eastFormPanel = createEastFormPanel();

        JPanel mainContentWrapper = new JPanel(new BorderLayout(15, 15));
        mainContentWrapper.setBackground(BG_COLOR);
        mainContentWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainContentWrapper.add(centerContentPanel, BorderLayout.CENTER);
        mainContentWrapper.add(eastFormPanel, BorderLayout.EAST);

        add(mainContentWrapper, BorderLayout.CENTER);

        // Tải dữ liệu ban đầu
        SwingUtilities.invokeLater(() -> {
            loadTableData();
            updateSummaryBoxes();
            clearEmployeeForm();
        });
    }

    // ================================================================================
    // KHU VỰC TẠO GIAO DIỆN (UI Creation)
    // ================================================================================

    private JPanel createCenterContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(createSummaryPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createSearchPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createMainTablePanel());
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 15));
        panel.setOpaque(false);
        lblTongSo = new JLabel("0");
        lblNhanVien = new JLabel("0");
        lblQuanLy = new JLabel("0");
        lblLoading = new JLabel("");
        lblLoading.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblLoading.setForeground(Color.GRAY);
        panel.add(createSummaryBox("Tổng số tài khoản:", lblTongSo, COLOR_GREEN));
        panel.add(createSummaryBox("Nhân viên bán vé:", lblNhanVien, COLOR_YELLOW));
        panel.add(createSummaryBox("Quản lý:", lblQuanLy, COLOR_ORANGE));
        panel.add(lblLoading);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setPreferredSize(new Dimension(0, 100));
        return panel;
    }

    private JPanel createSummaryBox(String title, JLabel lblValue, Color bgColor) {
        JPanel box = new JPanel(new BorderLayout(0, 5));
        box.setBackground(bgColor);
        box.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_BOLD_14);
        lblTitle.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);
        box.add(lblTitle, BorderLayout.NORTH);
        box.add(lblValue, BorderLayout.CENTER);
        return box;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tìm kiếm theo:"), gbc);
        gbc.gridx = 1;
        String[] searchOptions = {"Mã nhân viên", "Số điện thoại", "Số CCCD", "Họ tên nhân viên"};
        cbSearchType = new JComboBox<>(searchOptions);
        cbSearchType.setFont(FONT_PLAIN_14);
        panel.add(cbSearchType, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Nhập thông tin:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSearchInput = new JTextField(20);
        txtSearchInput.setFont(FONT_PLAIN_14);
        panel.add(txtSearchInput, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        String[] statusOptions = {"Đang hoạt động", "Ngừng hoạt động"};
        cbSearchStatus = new JComboBox<>(statusOptions);
        cbSearchStatus.setFont(FONT_PLAIN_14);
        panel.add(cbSearchStatus, gbc);
        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 5, 5, 5);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(FONT_BOLD_14);
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchEmployees());
        panel.add(btnSearch, gbc);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.setPreferredSize(new Dimension(0, 150));
        return panel;
    }

    private JPanel createMainTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("DANH SÁCH TÀI KHOẢN");
        lblTitle.setFont(FONT_TITLE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        JButton btnAdd = new JButton("+ Thêm nhân viên");
        btnAdd.setFont(FONT_BOLD_14);
        btnAdd.setBackground(COLOR_GREEN);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> {
            clearEmployeeForm();
            formBorder.setTitle("Thêm nhân viên mới");
            Component formPanelComponent = getFormComponentIfPresent(this, "Thông tin nhân viên");
            if(formPanelComponent != null) formPanelComponent.repaint();
            maNV_dangSua = null;
        });
        headerPanel.add(btnAdd, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"STT", "Mã NV", "Họ Tên", "Giới Tính", "Chức Vụ", "Trạng Thái", "Tùy Chọn"};
        int optionColumnIndex = 6;

        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == optionColumnIndex; }
        };
        table = new JTable(model);
        table.setFont(FONT_PLAIN_14);
        table.setRowHeight(30);
        table.getTableHeader().setFont(FONT_BOLD_14);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        table.getColumnModel().getColumn(optionColumnIndex).setCellRenderer(new ButtonColumnRenderer());
        table.getColumnModel().getColumn(optionColumnIndex).setCellEditor(new ButtonColumnEditor(new JCheckBox()));
        table.getColumnModel().getColumn(optionColumnIndex).setMaxWidth(120);
        table.getColumnModel().getColumn(optionColumnIndex).setMinWidth(120);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow >= 0 && table.getSelectedColumn() != optionColumnIndex) {
                    String maNV = table.getValueAt(selectedRow, 1).toString();
                    fillFormFromTable(maNV);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEastFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(550, 0));
        panel.add(createNotesPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createEmployeeFormPanel());
        return panel;
    }

    private JPanel createNotesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Ghi chú",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, Color.BLACK
        );
        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));
        txtGhiChu = new JTextArea();
        txtGhiChu.setBackground(new Color(255, 255, 224));
        txtGhiChu.setFont(FONT_PLAIN_14);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        JScrollPane scrollNotes = new JScrollPane(txtGhiChu);
        scrollNotes.setPreferredSize(new Dimension(0, 150));
        JButton btnSaveNotes = new JButton("Lưu");
        btnSaveNotes.setFont(FONT_BOLD_14);
        btnSaveNotes.setBackground(PRIMARY_COLOR);
        btnSaveNotes.setForeground(Color.WHITE);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSaveNotes);
        panel.add(scrollNotes, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        return panel;
    }

    private JPanel createEmployeeFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        formBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin nhân viên",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD_14, PRIMARY_COLOR
        );
        panel.setBorder(BorderFactory.createCompoundBorder(formBorder, new EmptyBorder(15, 15, 15, 15)));
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // --- CỘT 1 Labels---
        gbc.gridx = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.gridy = 0; fieldsPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridy = 1; fieldsPanel.add(new JLabel("Email:"), gbc);
        gbc.gridy = 2; fieldsPanel.add(new JLabel("Số CCCD:"), gbc);
        gbc.gridy = 3; fieldsPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridy = 4; fieldsPanel.add(new JLabel("Chức vụ:"), gbc);
        gbc.gridy = 6; fieldsPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridy = 7; fieldsPanel.add(new JLabel("Ngày tạo:"), gbc);

        // --- CỘT 2 Components---
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.gridy = 0; txtHoTen = new JTextField(); fieldsPanel.add(txtHoTen, gbc);
        gbc.gridy = 1; txtEmail = new JTextField(); fieldsPanel.add(txtEmail, gbc);
        gbc.gridy = 2; txtSoCCCD = new JTextField(); fieldsPanel.add(txtSoCCCD, gbc);
        gbc.gridy = 3; txtDiaChi = new JTextField(); fieldsPanel.add(txtDiaChi, gbc);
        gbc.gridy = 4; cbChucVu = new JComboBox<>(new String[]{"Nhân viên bán vé", "Quản lý", "Trưởng phòng"}); fieldsPanel.add(cbChucVu, gbc);
        gbc.gridy = 6; txtTenDangNhap = new JTextField(); txtTenDangNhap.setEditable(false); fieldsPanel.add(txtTenDangNhap, gbc);
        gbc.gridy = 7;
        dateNgayTao = new JDateChooser();
        dateNgayTao.setDateFormatString("dd/MM/yyyy HH:mm:ss");
        dateNgayTao.setEnabled(false);
        fieldsPanel.add(dateNgayTao, gbc);

        // --- CỘT 3 Labels---
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.insets = new Insets(5, 15, 5, 5);
        gbc.gridy = 0; fieldsPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridy = 1; fieldsPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridy = 4; fieldsPanel.add(new JLabel("Ngày vào làm:"), gbc);
        gbc.gridy = 6; fieldsPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridy = 7; fieldsPanel.add(new JLabel("Trạng thái:"), gbc);

        // --- CỘT 4 Components---
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); genderPanel.setOpaque(false);
        radNam = new JRadioButton("Nam"); radNam.setOpaque(false);
        radNu = new JRadioButton("Nữ"); radNu.setOpaque(false);
        bgGender = new ButtonGroup(); bgGender.add(radNam); bgGender.add(radNu);
        genderPanel.add(radNam); genderPanel.add(radNu); fieldsPanel.add(genderPanel, gbc);
        gbc.gridy = 1; txtSoDienThoai = new JTextField(); fieldsPanel.add(txtSoDienThoai, gbc);
        gbc.gridy = 4;
        dateNgayVaoLam = new JDateChooser(); dateNgayVaoLam.setDateFormatString("dd/MM/yyyy");
        fieldsPanel.add(dateNgayVaoLam, gbc);
        gbc.gridy = 6; txtMatKhau = new JPasswordField(); fieldsPanel.add(txtMatKhau, gbc);
        gbc.gridy = 7; cbTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Ngừng hoạt động"}); fieldsPanel.add(cbTrangThai, gbc);

        cbChucVu.addActionListener(e -> autoGenerateUsername());

        // --- Panel Nút Bấm ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); buttonPanel.setOpaque(false);
        JButton btnHuy = new JButton("Hủy"); btnHuy.setFont(FONT_BOLD_14); btnHuy.setBackground(COLOR_RED); btnHuy.setForeground(Color.WHITE);
        JButton btnXoaTrang = new JButton("Xóa trắng"); btnXoaTrang.setFont(FONT_BOLD_14);
        JButton btnLuu = new JButton("Lưu"); btnLuu.setFont(FONT_BOLD_14); btnLuu.setBackground(COLOR_GREEN); btnLuu.setForeground(Color.WHITE);
        buttonPanel.add(btnHuy); buttonPanel.add(btnXoaTrang); buttonPanel.add(btnLuu);

        // Thêm sự kiện cho nút
        btnLuu.addActionListener(e -> saveEmployee());
        btnXoaTrang.addActionListener(e -> clearEmployeeForm());
        btnHuy.addActionListener(e -> {
            clearEmployeeForm();
            formBorder.setTitle("Thêm nhân viên mới");
            Component formPanelComponent = getFormComponentIfPresent(this, "Thông tin nhân viên");
            if(formPanelComponent != null) formPanelComponent.repaint();
        });

        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================================
    // LỚP NỘI TẠI CHO NÚT BẢNG
    // ================================================================================
    class ButtonColumnRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton btnEdit, btnDelete;
        public ButtonColumnRenderer(){
            setOpaque(true); setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0)); setBackground(Color.WHITE);
            btnEdit = new JButton("\u270E"); btnEdit.setToolTipText("Sửa"); btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); btnEdit.setMargin(new Insets(2, 5, 2, 5)); btnEdit.setBackground(new Color(255, 193, 7)); btnEdit.setForeground(Color.BLACK);
            btnDelete = new JButton("\uD83D\uDDD1"); btnDelete.setToolTipText("Xóa"); btnDelete.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); btnDelete.setMargin(new Insets(2, 5, 2, 5)); btnDelete.setBackground(new Color(220, 53, 69)); btnDelete.setForeground(Color.WHITE);
            add(btnEdit); add(btnDelete);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int row, int col) { 
            if (isSelected) setBackground(t.getSelectionBackground()); else setBackground(Color.WHITE); 
            return this;
        }
    }

    class ButtonColumnEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor, javax.swing.table.TableCellRenderer {
        private final JPanel panel; private final JButton btnEdit, btnDelete; private JTable table; private int row;
        public ButtonColumnEditor(JCheckBox checkBox) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); panel.setBackground(Color.WHITE);
            btnEdit = new JButton("\u270E"); btnEdit.setToolTipText("Sửa"); btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); btnEdit.setMargin(new Insets(2, 5, 2, 5)); btnEdit.setBackground(new Color(255, 193, 7)); btnEdit.setForeground(Color.BLACK);
            btnDelete = new JButton("\uD83D\uDDD1"); btnDelete.setToolTipText("Xóa"); btnDelete.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); btnDelete.setMargin(new Insets(2, 5, 2, 5)); btnDelete.setBackground(new Color(220, 53, 69)); btnDelete.setForeground(Color.WHITE);
            panel.add(btnEdit); panel.add(btnDelete);

            btnEdit.addActionListener(new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    String maNV = table.getValueAt(row, 1).toString();
                    fillFormFromTable(maNV);
                }
            });
            btnDelete.addActionListener(new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    String maNV = table.getValueAt(row, 1).toString();
                    String hoTen = table.getValueAt(row, 2).toString();
                    int confirm = JOptionPane.showConfirmDialog(table,
                            "Bạn có chắc muốn cập nhật trạng thái 'Ngừng hoạt động' cho nhân viên: \n" + hoTen + " (" + maNV + ")?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        softDeleteEmployee(maNV);
                    }
                }
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isSelected, int r, int c) { this.table = t; this.row = r; panel.setBackground(t.getSelectionBackground()); return panel; }
        @Override public Object getCellEditorValue() { return ""; }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int r, int c) { panel.setBackground(isSelected ? t.getSelectionBackground() : Color.WHITE); return panel; }
    }

    // ================================================================================
    // CÁC HÀM XỬ LÝ NGHIỆP VỤ VÀ CHUYỂN ĐỔI KIỂU DỮ LIỆU
    // ================================================================================

    private Date convertLocalDateToUtilDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate convertUtilDateToLocalDate(Date utilDate) {
        if (utilDate == null) return null;
        return utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Tải toàn bộ dữ liệu từ Service lên JTable (sử dụng SwingWorker).
     */
    private void loadTableData() {
        model.setRowCount(0);
        lblLoading.setText("Đang tải dữ liệu...");

        SwingWorker<List<TaiKhoan>, Void> worker = new SwingWorker<List<TaiKhoan>, Void>() {
            @Override
            protected List<TaiKhoan> doInBackground() {
                return nhanVienService.getAllTaiKhoanWithNhanVien();
            }

            @Override
            protected void done() {
                lblLoading.setText("");
                try {
                    List<TaiKhoan> danhSachTK = get();
                    int stt = 1;
                    for (TaiKhoan tk : danhSachTK) {
                        NhanVien nv = tk.getNhanVien();
                        String maNV = (nv != null) ? nv.getMaNV() : tk.getMaNV();
                        String hoTen = (nv != null) ? nv.getHoTen() : "Không tìm thấy NV";
                        String gioiTinh = (nv != null) ? nv.getGioiTinh() : "";
                        String chucVu = (nv != null) ? nv.getChucVu() : "";

                        Vector<Object> row = new Vector<>();
                        row.add(stt++);
                        row.add(maNV);
                        row.add(hoTen);
                        row.add(gioiTinh);
                        row.add(chucVu);
                        row.add(tk.getTrangThai());
                        row.add("");
                        model.addRow(row);
                    }
                } catch (Exception e) {
                    handleException("Lỗi khi tải dữ liệu nhân viên", e);
                }
            }
        };
        worker.execute();
    }

    /**
     * Cập nhật các ô thống kê (Tổng số, Nhân viên, Quản lý) - sử dụng SwingWorker.
     */
    private void updateSummaryBoxes() {
        SwingWorker<Map<String, Integer>, Void> worker = new SwingWorker<Map<String, Integer>, Void>() {
            @Override
            protected Map<String, Integer> doInBackground() {
                return nhanVienService.getStatistics();
            }

            @Override
            protected void done() {
                try {
                    Map<String, Integer> stats = get();
                    lblTongSo.setText(stats.getOrDefault("total", 0).toString());
                    lblNhanVien.setText(stats.getOrDefault("nhanVien", 0).toString());
                    lblQuanLy.setText(stats.getOrDefault("quanLy", 0).toString());
                } catch (Exception e) {
                    System.err.println("Lỗi khi cập nhật thống kê: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Xóa trắng form, đưa về trạng thái "Thêm mới".
     */
    private void clearEmployeeForm() {
        txtHoTen.setText("");
        txtEmail.setText("");
        txtSoCCCD.setText("");
        txtDiaChi.setText("");
        txtSoDienThoai.setText("");
        txtMatKhau.setText("");
        cbChucVu.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        dateNgayVaoLam.setDate(null);
        dateNgayTao.setDate(new Date());
        bgGender.clearSelection();
        txtGhiChu.setText("");
        maNV_dangSua = null;

        txtTenDangNhap.setEditable(false);
        autoGenerateUsername();

        formBorder.setTitle("Thêm nhân viên mới");
        Component formPanelComponent = getFormComponentIfPresent(this, "Thông tin nhân viên");
        if(formPanelComponent == null) formPanelComponent = getFormComponentIfPresent(this, "Thêm nhân viên mới");
        if(formPanelComponent != null) formPanelComponent.repaint();
    }

    private Component getFormComponentIfPresent(Container container, String title) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JComponent) {
                JComponent jcomp = (JComponent) comp;
                if (jcomp.getBorder() instanceof TitledBorder) {
                    TitledBorder border = (TitledBorder) jcomp.getBorder();
                    if (border.getTitle().startsWith("Thông tin") ||
                            border.getTitle().startsWith("Thêm") ||
                            border.getTitle().startsWith("Sửa")) {
                        return comp;
                    }
                }
            }
            if (comp instanceof Container) {
                Component found = getFormComponentIfPresent((Container) comp, title);
                if (found != null) return found;
            }
        }
        return null;
    }

    /**
     * Điền thông tin chi tiết của nhân viên lên form khi nhấp vào bảng.
     */
    private void fillFormFromTable(String maNV) {
        SwingWorker<TaiKhoan, Void> worker = new SwingWorker<TaiKhoan, Void>() {
            @Override
            protected TaiKhoan doInBackground() {
                return nhanVienService.findTaiKhoanByMaNV(maNV).orElse(null);
            }

            @Override
            protected void done() {
                try {
                    TaiKhoan tk = get();
                    if (tk != null) {
                        maNV_dangSua = maNV;
                        NhanVien nv = tk.getNhanVien();

                        txtHoTen.setText(nv.getHoTen());
                        txtEmail.setText(nv.getEmail());
                        txtSoCCCD.setText(nv.getSoCCCD());
                        txtDiaChi.setText(nv.getDiaChi());
                        cbChucVu.setSelectedItem(nv.getChucVu());
                        txtSoDienThoai.setText(nv.getSdt());

                        dateNgayVaoLam.setDate(convertLocalDateToUtilDate(nv.getNgayVaoLam()));

                        if ("Nam".equalsIgnoreCase(nv.getGioiTinh())) radNam.setSelected(true);
                        else if ("Nữ".equalsIgnoreCase(nv.getGioiTinh())) radNu.setSelected(true);
                        else bgGender.clearSelection();

                        txtTenDangNhap.setText(tk.getTenDangNhap());
                        txtTenDangNhap.setEditable(false);

                        txtMatKhau.setText(tk.getMatKhau());
                        cbTrangThai.setSelectedItem(tk.getTrangThai());

                        dateNgayTao.setDate(convertLocalDateToUtilDate(tk.getNgayTao()));

                        formBorder.setTitle("Sửa thông tin nhân viên: " + maNV);
                        Component formPanelComponent = getFormComponentIfPresent(ManHinhQuanLyNhanVienJPA.this, "Thông tin nhân viên");
                        if(formPanelComponent != null) formPanelComponent.repaint();
                    } else {
                        JOptionPane.showMessageDialog(ManHinhQuanLyNhanVienJPA.this, "Không tìm thấy thông tin chi tiết cho nhân viên " + maNV, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    handleException("Lỗi khi tải thông tin nhân viên", e);
                }
            }
        };
        worker.execute();
    }

    /**
     * Cập nhật trạng thái nhân viên thành 'Ngừng hoạt động'.
     */
    private void softDeleteEmployee(String maNV) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return nhanVienService.softDeleteNhanVien(maNV);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(ManHinhQuanLyNhanVienJPA.this, 
                                "Đã cập nhật trạng thái nhân viên " + maNV + " thành 'Ngừng hoạt động'.", 
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadTableData();
                        updateSummaryBoxes();
                        clearEmployeeForm();
                    } else {
                        JOptionPane.showMessageDialog(ManHinhQuanLyNhanVienJPA.this, 
                                "Không tìm thấy nhân viên để cập nhật trạng thái!", 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    handleException("Lỗi khi cập nhật trạng thái nhân viên", e);
                }
            }
        };
        worker.execute();
    }

    /**
     * Lưu (Thêm mới hoặc Cập nhật) thông tin nhân viên.
     */
    private void saveEmployee() {
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String cccd = txtSoCCCD.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String chucVu = cbChucVu.getSelectedItem().toString();
        String sdt = txtSoDienThoai.getText().trim();

        LocalDate ngayVaoLamLocal = convertUtilDateToLocalDate(dateNgayVaoLam.getDate());
        LocalDate ngayTaoLocal = convertUtilDateToLocalDate(dateNgayTao.getDate());
        LocalDate ngaySinhLocal = null;

        String gioiTinh = radNam.isSelected() ? "Nam" : (radNu.isSelected() ? "Nữ" : null);

        String maNV = (maNV_dangSua == null) ? txtTenDangNhap.getText() : maNV_dangSua;
        String tenDangNhap = txtTenDangNhap.getText();

        String matKhau = new String(txtMatKhau.getPassword());
        String trangThai = cbTrangThai.getSelectedItem().toString();

        // Validate dữ liệu
        if (hoTen.isEmpty() || tenDangNhap.isEmpty() || matKhau.isEmpty() || gioiTinh == null || ngayVaoLamLocal == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường bắt buộc:\n* Họ tên\n* Giới tính\n* Ngày vào làm\n* Mật khẩu", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo đối tượng Entity
        NhanVien nv = NhanVien.builder()
                .maNV(maNV)
                .hoTen(hoTen)
                .soCCCD(cccd)
                .ngaySinh(ngaySinhLocal)
                .email(email)
                .sdt(sdt)
                .gioiTinh(gioiTinh)
                .diaChi(diaChi)
                .ngayVaoLam(ngayVaoLamLocal)
                .chucVu(chucVu)
                .build();

        TaiKhoan tk = TaiKhoan.builder()
                .tenDangNhap(tenDangNhap)
                .matKhau(matKhau)
                .ngayTao(ngayTaoLocal != null ? ngayTaoLocal : LocalDate.now())
                .trangThai(trangThai)
                .nhanVien(nv)
                .build();

        // Gọi Service (sử dụng SwingWorker)
        lblLoading.setText("Đang xử lý...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    if (maNV_dangSua == null) {
                        return nhanVienService.addNhanVien(nv, tk);
                    } else {
                        return nhanVienService.updateNhanVien(nv, tk);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

            @Override
            protected void done() {
                lblLoading.setText("");
                try {
                    boolean success = get();
                    if (success) {
                        String message = (maNV_dangSua == null) 
                                ? "Thêm nhân viên mới thành công!\nMã nhân viên: " + nv.getMaNV()
                                : "Cập nhật thông tin nhân viên thành công!";
                        JOptionPane.showMessageDialog(ManHinhQuanLyNhanVienJPA.this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadTableData();
                        updateSummaryBoxes();
                        clearEmployeeForm();
                    }
                } catch (Exception e) {
                    handleException("Lỗi khi lưu nhân viên", e);
                }
            }
        };
        worker.execute();
    }

    /**
     * Tự động tạo Tên đăng nhập (Mã NV) khi thay đổi Chức vụ.
     */
    private void autoGenerateUsername() {
        if (maNV_dangSua == null) {
            String selectedRole = cbChucVu.getSelectedItem().toString();

            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return nhanVienService.getNextMaNV(selectedRole);
                }

                @Override
                protected void done() {
                    try {
                        String newMaNV = get();
                        txtTenDangNhap.setText(newMaNV);
                        txtTenDangNhap.setEditable(false);
                    } catch (Exception e) {
                        System.err.println("Lỗi khi tạo mã NV tự động: " + e.getMessage());
                        txtTenDangNhap.setText("");
                    }
                }
            };
            worker.execute();
        } else {
            txtTenDangNhap.setEditable(false);
        }
    }

    /**
     * Thực hiện tìm kiếm theo các tiêu chí.
     */
    private void searchEmployees() {
        String searchBy = cbSearchType.getSelectedItem().toString();
        String searchTerm = txtSearchInput.getText().trim();
        String status = cbSearchStatus.getSelectedItem().toString();
        model.setRowCount(0);
        lblLoading.setText("Đang tìm kiếm...");

        SwingWorker<List<TaiKhoan>, Void> worker = new SwingWorker<List<TaiKhoan>, Void>() {
            @Override
            protected List<TaiKhoan> doInBackground() {
                return nhanVienService.searchNhanVien(searchBy, searchTerm, status);
            }

            @Override
            protected void done() {
                lblLoading.setText("");
                try {
                    List<TaiKhoan> results = get();
                    int stt = 1;
                    if (results.isEmpty()) {
                        JOptionPane.showMessageDialog(ManHinhQuanLyNhanVienJPA.this, 
                                "Không tìm thấy kết quả nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        for(TaiKhoan tk : results) {
                            NhanVien nv = tk.getNhanVien();
                            Vector<Object> row = new Vector<>();
                            row.add(stt++);
                            row.add(nv.getMaNV());
                            row.add(nv.getHoTen());
                            row.add(nv.getGioiTinh());
                            row.add(nv.getChucVu());
                            row.add(tk.getTrangThai());
                            row.add("");
                            model.addRow(row);
                        }
                    }
                } catch (Exception e) {
                    handleException("Lỗi khi tìm kiếm", e);
                }
            }
        };
        worker.execute();
    }

    private void handleException(String messagePrefix, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, messagePrefix + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
