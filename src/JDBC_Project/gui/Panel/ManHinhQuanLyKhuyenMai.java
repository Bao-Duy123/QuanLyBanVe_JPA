package JDBC_Project.gui.Panel;

// Import cần thiết
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.time.ZoneId;

// Import các lớp DAO và Entity
import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import gui.Popup.PopupTaoKhuyenMai;

/**
 * Lớp này tạo giao diện Quản lý Khuyến Mãi (Màn hình chính) với chức năng Lọc và Tìm kiếm.
 */
public class ManHinhQuanLyKhuyenMai extends JPanel implements ActionListener {

    // =================================================================================
    // CÁC MÀU SẮC VÀ FONT
    // =================================================================================
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("###,###,##0");


    // Khai báo các component chính trên màn hình quản lý
    private JTable table;
    private DefaultTableModel tableModel;

    // Các Component MỚI cho LỌC/TÌM KIẾM
    private JTextField txtTimKiem;
    private JComboBox<String> cbLocTrangThai;
    private JButton btnTimKiem;

    // Các nút chức năng
    private JButton btnThem, btnSua, btnKetThuc, btnGiaHan, btnLamMoi;

    private JTextField txtMaKM;
    private KhuyenMaiDAO khuyenMaiDAO;
    private JFrame parentFrame;

    public ManHinhQuanLyKhuyenMai() {
        // Khởi tạo DAO
        khuyenMaiDAO = new KhuyenMaiDAO();

        // Tìm JFrame cha (nếu có)
        SwingUtilities.invokeLater(() -> {
            Container parent = getTopLevelAncestor();
            if (parent instanceof JFrame) {
                parentFrame = (JFrame) parent;
            } else if (parent instanceof JDialog) {
                // Xử lý trường hợp nằm trong JDialog
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof JDialog) {
                    parentFrame = (JFrame) ((JDialog) window).getOwner();
                } else if (window instanceof JFrame) {
                    parentFrame = (JFrame) window;
                }
            }
        });


        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Tiêu đề ---
        JLabel title = new JLabel("Quản lý Khuyến Mãi");
        title.setFont(FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        add(title, BorderLayout.NORTH);

        // --- Khu vực chính (Điều khiển và Bảng) ---
        JPanel mainArea = new JPanel();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.Y_AXIS));
        mainArea.setOpaque(false);

        // Cần một JTextField ẩn để lưu Mã KM được chọn từ bảng
        txtMaKM = new JTextField();
        txtMaKM.setVisible(false);
        this.add(txtMaKM);

        // 1. Panel Điều khiển (Nút chức năng, Lọc & Tìm kiếm)
        JPanel controlPanel = createControlPanel(); // Dùng panel mới
        mainArea.add(controlPanel);

        // Khoảng cách
        mainArea.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Bảng dữ liệu
        JPanel tablePanel = createTablePanel();
        mainArea.add(tablePanel);

        add(mainArea, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadDataToTable();
        lamMoiTrangThaiChon();
    }

    /**
     * Tạo panel điều khiển (chứa các nút chức năng và khu vực lọc/tìm kiếm)
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setOpaque(false);

        // --- 1. Panel Nút chức năng chính ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setOpaque(false);

        btnThem = new JButton("➕ Tạo Khuyến Mãi");
        btnSua = new JButton("📝 Cập Nhật");
        btnKetThuc = new JButton("⛔ Kết Thúc KM");
        btnGiaHan = new JButton("⏳ Gia Hạn KM");
        btnLamMoi = new JButton("🔄 Làm Mới");

        // Đặt màu cho nút chính (Tạo mới)
        btnThem.setBackground(PRIMARY_COLOR);
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);

        // Đặt font
        btnThem.setFont(FONT_PLAIN_14);
        btnSua.setFont(FONT_PLAIN_14);
        btnKetThuc.setFont(FONT_PLAIN_14);
        btnGiaHan.setFont(FONT_PLAIN_14);
        btnLamMoi.setFont(FONT_PLAIN_14);

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnKetThuc.addActionListener(this);
        btnGiaHan.addActionListener(this);
        btnLamMoi.addActionListener(this);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnKetThuc);
        buttonPanel.add(btnGiaHan);

        controlPanel.add(buttonPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Khoảng cách

        // --- 2. Panel Lọc và Tìm kiếm MỚI ---
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchFilterPanel.setOpaque(false);
        searchFilterPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));

        // Tìm kiếm theo Mã/Tên
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(FONT_PLAIN_14);
        btnTimKiem = new JButton("🔎 Tìm Kiếm");
        btnTimKiem.setFont(FONT_PLAIN_14);
        btnTimKiem.addActionListener(this);

        // Lọc theo Trạng thái
        cbLocTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang Hoạt Động", "Chưa Hoạt Động", "Đã Kết Thúc"});
        cbLocTrangThai.setFont(FONT_PLAIN_14);
        cbLocTrangThai.addActionListener(this); // Đăng ký sự kiện để tự động lọc khi đổi trạng thái

        searchFilterPanel.add(new JLabel("Mã/Tên KM:"));
        searchFilterPanel.add(txtTimKiem);
        searchFilterPanel.add(btnTimKiem);
        searchFilterPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        searchFilterPanel.add(new JLabel("Lọc theo Trạng thái:"));
        searchFilterPanel.add(cbLocTrangThai);
        searchFilterPanel.add(btnLamMoi); // Chuyển nút Làm Mới xuống đây cho tiện

        controlPanel.add(searchFilterPanel);

        return controlPanel;
    }


    /**
     * Tạo panel chứa bảng hiển thị danh sách khuyến mãi
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));

        // Tên cột: (Đã điều chỉnh thứ tự cho hợp lý)
        String[] columnNames = {"Mã KM", "Tên KM", "Loại Giảm", "Giảm (%)", "Giảm (VND)", "DK Áp Dụng", "Bắt đầu", "Kết thúc", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa bảng
            }
        };
        table = new JTable(tableModel);

        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setFont(FONT_PLAIN_14);
        table.getTableHeader().setFont(FONT_BOLD_14);
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        // Thiết lập chiều rộng cột (tùy chọn)
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);

        // Thêm sự kiện click chuột để lưu MaKM được chọn và bật nút
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

    /**
     * Đổ dữ liệu thực tế từ DAO lên bảng, áp dụng LỌC và TÌM KIẾM
     */
    public void loadDataToTable() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        // Lấy điều kiện lọc và tìm kiếm từ component
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String selectedStatus = (String) cbLocTrangThai.getSelectedItem();

        List<KhuyenMai> dsKM = khuyenMaiDAO.layTatCaKhuyenMai(); // Lấy tất cả KM

        // DEBUG: Kiểm tra xem có dữ liệu từ DAO không
        System.out.println("Số lượng KM lấy được từ DB: " + (dsKM != null ? dsKM.size() : "null"));

        if (dsKM == null) return;

        for (KhuyenMai km : dsKM) {
            System.out.println(km.getMaKM() + " - " + km.getTenKM()); // DEBUG: In mã và tên KM

            // --- BƯỚC 1: LỌC THEO TRẠNG THÁI & TÌM KIẾM ---
            String trangThaiHienThi = getStatusHienThi(km.getTrangThai());

            // Lọc theo Trạng thái
            if (!"Tất cả".equals(selectedStatus) && !selectedStatus.equals(trangThaiHienThi)) {
                continue; // Bỏ qua nếu không khớp trạng thái
            }

            // Tìm kiếm theo Mã hoặc Tên (case-insensitive)
            if (!keyword.isEmpty()) {
                boolean matchMa = km.getMaKM().toLowerCase().contains(keyword);
                boolean matchTen = km.getTenKM().toLowerCase().contains(keyword);
                if (!matchMa && !matchTen) {
                    continue; // Bỏ qua nếu không khớp tìm kiếm
                }
            }

            // --- BƯỚC 2: CHUẨN BỊ DỮ LIỆU HIỂN THỊ ---

            double giamPhanTram = 0.0;
            double giamCoDinh = 0.0;
            String loaiHienThi = "";

            if ("PHAN_TRAM_GIA".equals(km.getLoaiKM())) {
                giamPhanTram = km.getGiaTriGiam().doubleValue();
                loaiHienThi = "Phần Trăm";
            } else if ("CO_DINH".equals(km.getLoaiKM())) {
                giamCoDinh = km.getGiaTriGiam().doubleValue();
                loaiHienThi = "Cố Định";
            }

            String dkApDungHienThi;
            if ("MIN_GIA".equals(km.getDkApDung()) && km.getGiaTriDK() != null) {
                dkApDungHienThi = "HĐ >= " + VND_FORMAT.format(km.getGiaTriDK()) + " VND";
            } else if ("MIN_SL".equals(km.getDkApDung()) && km.getGiaTriDK() != null) {
                dkApDungHienThi = "SL Vé >= " + km.getGiaTriDK().intValue();
            } else {
                dkApDungHienThi = "Không";
            }

            // --- BƯỚC 3: THÊM DÒNG VÀO BẢNG ---
            Object[] row = new Object[]{
                    km.getMaKM(),
                    km.getTenKM(),
                    loaiHienThi,
                    giamPhanTram > 0 ? giamPhanTram + "%" : "",
                    giamCoDinh > 0 ? VND_FORMAT.format(giamCoDinh) : "",
                    dkApDungHienThi,
                    km.getNgayBatDau().toLocalDate().toString(),
                    km.getNgayKetThuc().toLocalDate().toString(),
                    trangThaiHienThi
            };
            tableModel.addRow(row);
        }
        lamMoiTrangThaiChon();
    }

    /**
     * Chuyển trạng thái lưu trong DB sang trạng thái hiển thị trên UI.
     */
    private String getStatusHienThi(String status) {
        if("HOAT_DONG".equals(status)) {
            return "Đang Hoạt Động";
        } else if("HET_HAN".equals(status)) {
            return "Đã Kết Thúc";
        } else { // KHONG_HOAT_DONG
            return "Chưa Hoạt Động";
        }
    }


   /**
            * Cập nhật trạng thái nút khi click vào một hàng trên bảng.
            * Đã sửa lỗi NullPointerException bằng cách kiểm tra dữ liệu an toàn.
            */
    private void fillFormFromTable(int row) {
        // 1. Kiểm tra hàng hợp lệ
        if (row < 0 || row >= tableModel.getRowCount()) {
            return;
        }

        // 2. Lấy dữ liệu an toàn (Dùng String.valueOf hoặc kiểm tra != null)
        // Nếu getValueAt là null, String.valueOf sẽ trả về chuỗi "null"
        // Hoặc dùng cách kiểm tra để trả về chuỗi rỗng ""
        Object objMa = tableModel.getValueAt(row, 0);
        String maKM = (objMa != null) ? objMa.toString() : "";

        Object objTrangThai = tableModel.getValueAt(row, tableModel.getColumnCount() - 1);
        String trangThaiHienThi = (objTrangThai != null) ? objTrangThai.toString() : "";

        // 3. Điền dữ liệu vào form
        txtMaKM.setText(maKM);

        // 4. Cập nhật trạng thái các nút bấm
        btnSua.setEnabled(!maKM.isEmpty()); // Chỉ bật nếu có mã
        btnGiaHan.setEnabled(!maKM.isEmpty());

        // 5. Kiểm tra trạng thái (Dùng equalsIgnoreCase để an toàn hơn)
        if (trangThaiHienThi != null && trangThaiHienThi.equalsIgnoreCase("Đang Hoạt Động")) {
            btnKetThuc.setEnabled(true);
        } else {
            btnKetThuc.setEnabled(false);
        }
    }

    /**
     * Thiết lập trạng thái ban đầu/sau khi làm mới
     */
    private void lamMoiTrangThaiChon() {
        txtMaKM.setText("");
        btnSua.setEnabled(false);
        btnKetThuc.setEnabled(false);
        btnGiaHan.setEnabled(false);
        table.clearSelection();
    }

    // =================================================================================
    // LOGIC XỬ LÝ SỰ KIỆN (ActionListener)
    // =================================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLamMoi) {
            txtTimKiem.setText(""); // Xóa ô tìm kiếm
            cbLocTrangThai.setSelectedIndex(0); // Đặt lại lọc là "Tất cả"
            lamMoiTrangThaiChon();
            loadDataToTable(); // Tải lại bảng
        }
        else if (src == btnTimKiem || src == cbLocTrangThai) {
            String keyword = txtTimKiem.getText().trim();
            // 1. Kiểm tra rỗng
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã khuyến mãi để tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtTimKiem.requestFocus();
                return;
            }
            // 2. Kiểm tra định dạng bằng Regex: KM + 2 số tháng + 2 số năm + 3 số thứ tự
            String regex = "^KM(0[1-9]|1[0-2])\\d{2}\\d{3}$";

            if (!keyword.matches(regex)) {
                JOptionPane.showMessageDialog(this,
                        "Mã tìm kiếm không đúng định dạng!\nQuy tắc: KM + Tháng(2 số) + Năm(2 số) + STT(3 số)\nVí dụ: KM1225001",
                        "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                txtTimKiem.selectAll();
                txtTimKiem.requestFocus();
                return;
            }

            loadDataToTable();
        }
        else if (src == btnThem) {
            // Mở Popup Tạo Khuyến Mãi (Tham số null báo hiệu là chế độ THÊM)
            PopupTaoKhuyenMai popup = new PopupTaoKhuyenMai(parentFrame, this, null);
            popup.setVisible(true);
        }
        else if (src == btnSua) {
            handleSuaKhuyenMai();
        }
        else if (src == btnKetThuc) {
            handleKetThucKhuyenMai();
        }
        else if (src == btnGiaHan) {
            handleGiaHanKhuyenMai();
        }
    }

    private void handleSuaKhuyenMai() {
        String maKM = txtMaKM.getText();
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Cập Nhật.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mở Popup Sửa Khuyến Mãi (Truyền MaKM để Popup load dữ liệu và chuyển sang chế độ SỬA)
        PopupTaoKhuyenMai popup = new PopupTaoKhuyenMai(parentFrame, this, maKM);
        popup.setVisible(true);
    }


    private void handleKetThucKhuyenMai() {
        String maKM = txtMaKM.getText();
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Kết Thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra lại trạng thái KM trong CSDL trước khi kết thúc
        KhuyenMai km = khuyenMaiDAO.layKhuyenMaiTheoMa(maKM);
        if (km == null || !"HOAT_DONG".equals(km.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Khuyến Mãi này không ở trạng thái HOAT_DONG để có thể kết thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn KẾT THÚC Khuyến Mãi [" + maKM + "] ngay lập tức?",
                "Xác nhận Kết thúc", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            LocalDateTime now = LocalDateTime.now();

            // Gọi DAO.capNhatTrangThai để chuyển trạng thái sang HET_HAN và đặt Ngày KT là hiện tại
            boolean success = khuyenMaiDAO.capNhatTrangThai(maKM, "HET_HAN", now);

            if (success) {
                JOptionPane.showMessageDialog(this, "Đã Kết Thúc Khuyến Mãi [" + maKM + "].", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable(); // Tải lại bảng
            } else {
                JOptionPane.showMessageDialog(this, "Kết thúc Khuyến Mãi [" + maKM + "] thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleGiaHanKhuyenMai() {
        String maKM = txtMaKM.getText();
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khuyến Mãi cần Gia Hạn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        KhuyenMai km = khuyenMaiDAO.layKhuyenMaiTheoMa(maKM);
        if (km == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy Khuyến Mãi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo JDateChooser để chọn Ngày Kết Thúc mới
        JDateChooser newDateChooser = new JDateChooser();
        newDateChooser.setDateFormatString("dd/MM/yyyy");
        // Đặt ngày mặc định là Ngày Kết Thúc cũ
        newDateChooser.setDate(Date.from(km.getNgayKetThuc().atZone(ZoneId.systemDefault()).toInstant()));

        JPanel datePanel = new JPanel(new FlowLayout());
        datePanel.add(new JLabel("Chọn Ngày Kết Thúc mới:"));
        datePanel.add(newDateChooser);

        int result = JOptionPane.showConfirmDialog(this, datePanel, "Gia Hạn Khuyến Mãi [" + maKM + "]", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && newDateChooser.getDate() != null) {
            Date ngayKetThucMoiDate = newDateChooser.getDate();
            LocalDateTime ngayKetThucMoi = ngayKetThucMoiDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Đặt giờ kết thúc là cuối ngày (23:59:59)
            ngayKetThucMoi = ngayKetThucMoi.withHour(23).withMinute(59).withSecond(59);

            // Xác định trạng thái mới (Nếu Ngày Bắt Đầu đã qua thì là HOAT_DONG, nếu chưa qua thì là KHONG_HOAT_DONG)
            String trangThaiMoi = km.getNgayBatDau().isBefore(LocalDateTime.now()) ? "HOAT_DONG" : "KHONG_HOAT_DONG";

            // Gọi DAO.capNhatTrangThai để cập nhật ngày và trạng thái
            boolean success = khuyenMaiDAO.capNhatTrangThai(maKM, trangThaiMoi, ngayKetThucMoi);

            if (success) {
                JOptionPane.showMessageDialog(this, "Gia Hạn Khuyến Mãi [" + maKM + "] đến " + DATE_FORMAT.format(ngayKetThucMoiDate) + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
            } else {
                JOptionPane.showMessageDialog(this, "Gia Hạn Khuyến Mãi [" + maKM + "] thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } else if (result == JOptionPane.OK_OPTION && newDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày Kết Thúc mới.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Phương thức main để chạy độc lập
     */
    public static void main(String[] args) {
        // Cần khởi tạo kết nối CSDL tại đây nếu bạn muốn chạy độc lập
        // ConnectDB.getInstance().connect();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kiểm tra Màn hình Quản lý Khuyến Mãi");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);

            // Tạo một MainFrame giả định để chứa Panel
            JPanel mainFrame = new JPanel(new BorderLayout());
            mainFrame.add(new ManHinhQuanLyKhuyenMai(), BorderLayout.CENTER);

            frame.setContentPane(mainFrame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}