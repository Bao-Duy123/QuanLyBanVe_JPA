package JPA_Project.gui;

import JPA_Project.entity.KhuyenMai;
import JPA_Project.repository.KhuyenMaiRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.toedter.calendar.JDateChooser;

/**
 * ManHinhQuanLyKhuyenMaiJPA - Quản lý khuyến mãi
 */
public class ManHinhQuanLyKhuyenMaiJPA extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("###,###,##0");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private static final String LOAI_DK_MIN_GIA = "MIN_GIA";
    private static final String LOAI_DK_MIN_SL = "MIN_SL";
    private static final String LOAI_DK_NONE = "NONE";
    private static final String LOAI_GIAM_PHAN_TRAM = "PHAN_TRAM_GIA";
    private static final String LOAI_GIAM_CO_DINH = "CO_DINH";

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem, txtMaKM;
    private JComboBox<String> cbLocTrangThai;
    private JButton btnTimKiem, btnThem, btnSua, btnKetThuc, btnGiaHan, btnLamMoi;
    
    private KhuyenMaiRepository khuyenMaiRepository;
    private JFrame parentFrame;

    public ManHinhQuanLyKhuyenMaiJPA() {
        khuyenMaiRepository = new KhuyenMaiRepository();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Tiêu đề
        JLabel title = new JLabel("Quản lý Khuyến Mãi");
        title.setFont(FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        add(title, BorderLayout.NORTH);
        
        // Panel chính
        JPanel mainArea = new JPanel();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.Y_AXIS));
        mainArea.setOpaque(false);
        
        txtMaKM = new JTextField();
        txtMaKM.setVisible(false);
        add(txtMaKM);
        
        JPanel controlPanel = createControlPanel();
        mainArea.add(controlPanel);
        mainArea.add(Box.createRigidArea(new Dimension(0, 20)));
        mainArea.add(createTablePanel());
        
        add(mainArea, BorderLayout.CENTER);
        
        loadDataToTable();
        lamMoiTrangThaiChon();
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setOpaque(false);
        
        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setOpaque(false);
        
        btnThem = new JButton("Tạo Khuyến Mãi");
        btnSua = new JButton("Cập Nhật");
        btnKetThuc = new JButton("Kết Thúc KM");
        btnGiaHan = new JButton("Gia Hạn KM");
        btnLamMoi = new JButton("Làm Mới");
        
        btnThem.setBackground(PRIMARY_COLOR);
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);
        
        btnThem.setFont(FONT_PLAIN_14);
        btnSua.setFont(FONT_PLAIN_14);
        btnKetThuc.setFont(FONT_PLAIN_14);
        btnGiaHan.setFont(FONT_PLAIN_14);
        btnLamMoi.setFont(FONT_PLAIN_14);
        
        btnThem.addActionListener(e -> moPopupTaoKhuyenMai(null));
        btnSua.addActionListener(e -> handleSuaKhuyenMai());
        btnKetThuc.addActionListener(e -> handleKetThucKhuyenMai());
        btnGiaHan.addActionListener(e -> handleGiaHanKhuyenMai());
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            cbLocTrangThai.setSelectedIndex(0);
            lamMoiTrangThaiChon();
            loadDataToTable();
        });
        
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnKetThuc);
        buttonPanel.add(btnGiaHan);
        
        controlPanel.add(buttonPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Panel tìm kiếm & lọc
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchFilterPanel.setOpaque(false);
        searchFilterPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));
        
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(FONT_PLAIN_14);
        btnTimKiem = new JButton("Tìm Kiếm");
        btnTimKiem.setFont(FONT_PLAIN_14);
        btnTimKiem.addActionListener(e -> loadDataToTable());
        
        cbLocTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang Hoạt Động", "Chưa Hoạt Động", "Đã Kết Thúc"});
        cbLocTrangThai.setFont(FONT_PLAIN_14);
        
        searchFilterPanel.add(new JLabel("Mã/Tên KM:"));
        searchFilterPanel.add(txtTimKiem);
        searchFilterPanel.add(btnTimKiem);
        searchFilterPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        searchFilterPanel.add(new JLabel("Lọc theo Trạng thái:"));
        searchFilterPanel.add(cbLocTrangThai);
        searchFilterPanel.add(btnLamMoi);
        
        controlPanel.add(searchFilterPanel);
        
        return controlPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));
        
        String[] columnNames = {"Mã KM", "Tên KM", "Loại Giảm", "Giảm (%)", "Giảm (VND)", "DK Áp Dụng", "Bắt đầu", "Kết thúc", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setFont(FONT_PLAIN_14);
        table.getTableHeader().setFont(FONT_BOLD_14);
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    fillFormFromTable(row);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void loadDataToTable() {
        tableModel.setRowCount(0);
        
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String selectedStatus = (String) cbLocTrangThai.getSelectedItem();
        
        List<KhuyenMai> dsKM = khuyenMaiRepository.findAll();
        if (dsKM == null) return;
        
        for (KhuyenMai km : dsKM) {
            String trangThaiHienThi = getStatusHienThi(km.getTrangThai());
            
            // Lọc theo trạng thái
            if (!"Tất cả".equals(selectedStatus) && !selectedStatus.equals(trangThaiHienThi)) {
                continue;
            }
            
            // Tìm kiếm
            if (!keyword.isEmpty()) {
                boolean matchMa = km.getMaKM().toLowerCase().contains(keyword);
                boolean matchTen = km.getTenKM() != null && km.getTenKM().toLowerCase().contains(keyword);
                if (!matchMa && !matchTen) {
                    continue;
                }
            }
            
            double giamPhanTram = 0.0;
            double giamCoDinh = 0.0;
            String loaiHienThi = "";
            
            if (LOAI_GIAM_PHAN_TRAM.equals(km.getLoaiKM())) {
                giamPhanTram = km.getGiaTriGiam() != null ? km.getGiaTriGiam().doubleValue() : 0;
                loaiHienThi = "Phần Trăm";
            } else if (LOAI_GIAM_CO_DINH.equals(km.getLoaiKM())) {
                giamCoDinh = km.getGiaTriGiam() != null ? km.getGiaTriGiam().doubleValue() : 0;
                loaiHienThi = "Cố Định";
            }
            
            String dkApDungHienThi;
            if (LOAI_DK_MIN_GIA.equals(km.getDkApDung()) && km.getGiaTriDK() != null) {
                dkApDungHienThi = "HĐ >= " + VND_FORMAT.format(km.getGiaTriDK()) + " VND";
            } else if (LOAI_DK_MIN_SL.equals(km.getDkApDung()) && km.getGiaTriDK() != null) {
                dkApDungHienThi = "SL Vé >= " + km.getGiaTriDK().intValue();
            } else {
                dkApDungHienThi = "Không";
            }
            
            String ngayBatDau = km.getNgayBatDau() != null ? km.getNgayBatDau().toLocalDate().toString() : "";
            String ngayKetThuc = km.getNgayKetThuc() != null ? km.getNgayKetThuc().toLocalDate().toString() : "";
            
            Object[] row = new Object[]{
                    km.getMaKM(),
                    km.getTenKM(),
                    loaiHienThi,
                    giamPhanTram > 0 ? giamPhanTram + "%" : "",
                    giamCoDinh > 0 ? VND_FORMAT.format(giamCoDinh) : "",
                    dkApDungHienThi,
                    ngayBatDau,
                    ngayKetThuc,
                    trangThaiHienThi
            };
            tableModel.addRow(row);
        }
        lamMoiTrangThaiChon();
    }
    
    private String getStatusHienThi(String status) {
        if ("HOAT_DONG".equals(status)) return "Đang Hoạt Động";
        else if ("HET_HAN".equals(status)) return "Đã Kết Thúc";
        else return "Chưa Hoạt Động";
    }
    
    private void fillFormFromTable(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        
        Object objMa = tableModel.getValueAt(row, 0);
        String maKM = (objMa != null) ? objMa.toString() : "";
        
        Object objTrangThai = tableModel.getValueAt(row, tableModel.getColumnCount() - 1);
        String trangThaiHienThi = (objTrangThai != null) ? objTrangThai.toString() : "";
        
        txtMaKM.setText(maKM);
        btnSua.setEnabled(!maKM.isEmpty());
        btnGiaHan.setEnabled(!maKM.isEmpty());
        btnKetThuc.setEnabled("Đang Hoạt Động".equals(trangThaiHienThi));
    }
    
    private void lamMoiTrangThaiChon() {
        txtMaKM.setText("");
        btnSua.setEnabled(false);
        btnKetThuc.setEnabled(false);
        btnGiaHan.setEnabled(false);
        table.clearSelection();
    }
    
    private void moPopupTaoKhuyenMai(String maKM) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) topFrame = new JFrame();
        
        PopupTaoKhuyenMaiJPA popup = new PopupTaoKhuyenMaiJPA(topFrame, this, maKM);
        popup.setVisible(true);
    }
    
    private void handleSuaKhuyenMai() {
        String maKM = txtMaKM.getText();
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Cập Nhật.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        moPopupTaoKhuyenMai(maKM);
    }
    
    private void handleKetThucKhuyenMai() {
        String maKM = txtMaKM.getText();
        if (maKM.isEmpty()) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn KẾT THÚC Khuyến Mãi [" + maKM + "] ngay lập tức?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                KhuyenMai km = khuyenMaiRepository.findById(maKM);
                if (km != null) {
                    km.setTrangThai("HET_HAN");
                    km.setNgayKetThuc(LocalDateTime.now());
                    khuyenMaiRepository.save(km);
                    JOptionPane.showMessageDialog(this, "Đã Kết Thúc Khuyến Mãi [" + maKM + "].", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadDataToTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleGiaHanKhuyenMai() {
        String maKM = txtMaKM.getText();
        if (maKM.isEmpty()) return;
        
        try {
            KhuyenMai km = khuyenMaiRepository.findById(maKM);
            if (km == null) return;
            
            JPanel datePanel = new JPanel(new FlowLayout());
            JDateChooser newDateChooser = new JDateChooser();
            newDateChooser.setDateFormatString("dd/MM/yyyy");
            datePanel.add(new JLabel("Chọn Ngày Kết Thúc mới:"));
            datePanel.add(newDateChooser);
            
            int result = JOptionPane.showConfirmDialog(this, datePanel, "Gia Hạn KM [" + maKM + "]", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION && newDateChooser.getDate() != null) {
                java.util.Date date = newDateChooser.getDate();
                LocalDateTime ngayKetThucMoi = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                
                km.setNgayKetThuc(ngayKetThucMoi);
                String trangThaiMoi = km.getNgayBatDau().isBefore(LocalDateTime.now()) ? "HOAT_DONG" : "KHONG_HOAT_DONG";
                km.setTrangThai(trangThaiMoi);
                
                khuyenMaiRepository.save(km);
                JOptionPane.showMessageDialog(this, "Gia Hạn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Khuyến Mãi - JPA");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.add(new ManHinhQuanLyKhuyenMaiJPA());
            frame.setVisible(true);
        });
    }
}
