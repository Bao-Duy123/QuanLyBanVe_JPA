package JDBC_Project.gui.Panel;

import gui.MainFrame.BanVeDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.toedter.calendar.JDateChooser;
import dao.GaDao;
import dao.DTO.ThongTinVeDTODAO;
import database.ConnectDB;
import entity.DTO.ThongTinVeDTO;
import entity.Ga;

public class ManHinhDoiVe extends JPanel {

    private static final Font GLOBAL_FONT = new Font("Arial", Font.PLAIN, 13);

    // --- UI ---
    private JPanel pnlListTickets;
    private JTextField txtNhapThongTin;
    private JComboBox<String> cboTimKiemTheo;
    private JComboBox<String> cboGaDi;
    private JComboBox<String> cboGaDen;
    private JDateChooser dateChooserNgayDi;
    private JButton btnDoiToanBo;

    private ThongTinVeDTODAO ticketDao;

    public ManHinhDoiVe() {
        ticketDao = new ThongTinVeDTODAO();
        init();
    }

    private void init() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // =========================================================================
        // PHẦN TOP (GIỮ NGUYÊN)
        // =========================================================================
        JPanel pnlTop = new JPanel(new GridBagLayout());
        pnlTop.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlTop.add(createLabel("Tìm kiếm theo :"), gbc);

        gbc.gridx = 1;
        String[] searchOptions = {"Mã vé", "Số điện thoại", "Số CCCD/Định danh", "Mã hóa đơn"};
        cboTimKiemTheo = new JComboBox<>(searchOptions);
        cboTimKiemTheo.setFont(GLOBAL_FONT);
        cboTimKiemTheo.setPreferredSize(new Dimension(150, 25));
        pnlTop.add(cboTimKiemTheo, gbc);

        gbc.gridx = 2; gbc.insets = new Insets(5, 30, 5, 5);
        pnlTop.add(createLabel("Nhập thông tin :"), gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        txtNhapThongTin = new JTextField();
        txtNhapThongTin.setFont(GLOBAL_FONT);
        txtNhapThongTin.setPreferredSize(new Dimension(200, 25));
        pnlTop.add(txtNhapThongTin, gbc);

        gbc.gridx = 6; gbc.weightx = 0; gbc.gridwidth = 1;
        JPanel pnlBtnRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton btnTim = createStyledButton("Tìm", new Color(66, 133, 244), Color.WHITE);
        JButton btnXoaInput = createStyledButton("Xóa", new Color(234, 67, 53), Color.WHITE);
        pnlBtnRow1.add(btnTim);
        pnlBtnRow1.add(btnXoaInput);
        pnlTop.add(pnlBtnRow1, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        gbc.insets = new Insets(15, 10, 5, 5);
        pnlTop.add(createLabel("Ga đi :"), gbc);

        gbc.gridx = 1; gbc.insets = new Insets(15, 5, 5, 5);
        cboGaDi = new JComboBox<>();
        cboGaDi.setFont(GLOBAL_FONT);
        cboGaDi.setPreferredSize(new Dimension(150, 25));
        pnlTop.add(cboGaDi, gbc);

        gbc.gridx = 2; gbc.insets = new Insets(15, 30, 5, 5);
        pnlTop.add(createLabel("Ga đến :"), gbc);

        gbc.gridx = 3; gbc.weightx = 0.5; gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 5, 5, 5);
        cboGaDen = new JComboBox<>();
        cboGaDen.setFont(GLOBAL_FONT);
        pnlTop.add(cboGaDen, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        pnlTop.add(createLabel("Ngày khởi hành :"), gbc);

        gbc.gridx = 5; gbc.weightx = 0.5;
        dateChooserNgayDi = new JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setDate(new Date());
        dateChooserNgayDi.setPreferredSize(new Dimension(130, 25));
        JPanel pnlDateWrapper = new JPanel(new BorderLayout());
        pnlDateWrapper.add(dateChooserNgayDi, BorderLayout.CENTER);
        pnlTop.add(pnlDateWrapper, gbc);

        gbc.gridx = 6; gbc.weightx = 0;
        JPanel pnlBtnRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton btnLoc = createStyledButton("Lọc", new Color(66, 133, 244), Color.WHITE);
        JButton btnXoaLoc = createStyledButton("Xóa", new Color(234, 67, 53), Color.WHITE);
        pnlBtnRow2.add(btnLoc);
        pnlBtnRow2.add(btnXoaLoc);
        pnlTop.add(pnlBtnRow2, gbc);

        // =========================================================================
        // 2. PHẦN KẾT QUẢ (RESULTS)
        // =========================================================================
        JPanel pnlResultArea = new JPanel(new BorderLayout());
        JPanel pnlResultHeader = new JPanel(new BorderLayout());
        pnlResultHeader.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel lblKetQuaHeader = new JLabel("Kết quả tìm kiếm");
        lblKetQuaHeader.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnLamMoi = new JButton("↻ Làm mới");
        btnLamMoi.setFont(GLOBAL_FONT);
        pnlResultHeader.add(lblKetQuaHeader, BorderLayout.WEST);
        pnlResultHeader.add(btnLamMoi, BorderLayout.EAST);

        pnlListTickets = new JPanel(new GridLayout(0, 2, 20, 20));
        pnlListTickets.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlListTicketsWrapper = new JPanel(new BorderLayout());
        pnlListTicketsWrapper.add(pnlListTickets, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(pnlListTicketsWrapper);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        pnlResultArea.add(pnlResultHeader, BorderLayout.NORTH);
        pnlResultArea.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnXoaTrang = createStyledButton("Xóa trắng", new Color(234, 67, 53), Color.WHITE);

        btnDoiToanBo = createStyledButton("Đổi toàn bộ", new Color(40, 70, 220), Color.WHITE);
        btnDoiToanBo.setEnabled(false);

        pnlFooter.add(btnXoaTrang);
        pnlFooter.add(btnDoiToanBo);
        pnlResultArea.add(pnlFooter, BorderLayout.SOUTH);

        // =========================================================================
        // 3. XỬ LÝ SỰ KIỆN (EVENTS)
        // =========================================================================

        btnXoaInput.addActionListener(e -> {
            txtNhapThongTin.setText("");
            txtNhapThongTin.requestFocus();
        });

        // --- NÚT TÌM ---
        btnTim.addActionListener(e -> {
            String input = txtNhapThongTin.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtNhapThongTin.requestFocus();
                return;
            }

            pnlListTickets.removeAll();
            int searchType = cboTimKiemTheo.getSelectedIndex();
            List<ThongTinVeDTO> results = new ArrayList<>();

            try {
                switch (searchType) {
                    case 0: results.add(ticketDao.getVeByMaVe(input)); break;
                    case 1: results = ticketDao.getVeBySDT(input); break;
                    case 2: results = ticketDao.getVeByCCCD(input); break;
                    case 3: results = ticketDao.getVeByMaHoaDon(input); break;
                }

                results.removeIf(java.util.Objects::isNull);
                btnDoiToanBo.setEnabled(!results.isEmpty());

                if (results.isEmpty()) {
                    showNotFoundMessage();
                } else {
                    pnlListTickets.setLayout(new GridLayout(0, 2, 20, 20));
                    for (ThongTinVeDTO dto : results) {
                        TicketPanel ticketPanel = new TicketPanel();
                        ticketPanel.setTicket(dto);
                        pnlListTickets.add(ticketPanel);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi truy vấn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            pnlListTickets.revalidate();
            pnlListTickets.repaint();
        });

        // --- NÚT LỌC ---
        btnLoc.addActionListener(e -> {
            String input = txtNhapThongTin.getText().trim();
            int searchType = cboTimKiemTheo.getSelectedIndex();

            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Số điện thoại hoặc CCCD cần lọc!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (searchType != 1 && searchType != 2) {
                JOptionPane.showMessageDialog(this, "Chức năng 'Lọc' chỉ áp dụng cho tìm kiếm theo SĐT hoặc CCCD/Định danh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Validation inputs
            boolean isGaDiEmpty = cboGaDi.getSelectedIndex() <= 0;
            boolean isGaDenEmpty = cboGaDen.getSelectedIndex() <= 0;
            Date ngayDiUtil = dateChooserNgayDi.getDate();
            String rawDateText = ((JTextField)dateChooserNgayDi.getDateEditor().getUiComponent()).getText();
            boolean isDateEmpty = rawDateText.trim().isEmpty();
            boolean isDateInvalidFormat = (!isDateEmpty && ngayDiUtil == null);

            if (isGaDiEmpty && isGaDenEmpty && isDateEmpty) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin để lọc!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (isGaDiEmpty || isGaDenEmpty || isDateEmpty || isDateInvalidFormat) {
                JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra lại điều kiện lọc (Ga và Ngày)!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            pnlListTickets.removeAll();
            List<ThongTinVeDTO> results = new ArrayList<>();

            try {
                String gaDi = (String) cboGaDi.getSelectedItem();
                String gaDen = (String) cboGaDen.getSelectedItem();
                java.sql.Date ngayKhoiHanhSQL = new java.sql.Date(ngayDiUtil.getTime());

                if (searchType == 1) {
                    results = ticketDao.getVeTheoSDTVaLoTrinh(input, gaDi, gaDen, ngayKhoiHanhSQL);
                } else {
                    results = ticketDao.getVeTheoCCCDVaLoTrinh(input, gaDi, gaDen, ngayKhoiHanhSQL);
                }

                btnDoiToanBo.setEnabled(!results.isEmpty());

                if (results.isEmpty()) {
                    showNotFoundMessage();
                } else {
                    pnlListTickets.setLayout(new GridLayout(0, 2, 20, 20));
                    for (ThongTinVeDTO dto : results) {
                        TicketPanel ticketPanel = new TicketPanel();
                        ticketPanel.setTicket(dto);
                        pnlListTickets.add(ticketPanel);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            pnlListTickets.revalidate();
            pnlListTickets.repaint();
        });

        // --- NÚT RESET ---
        btnXoaLoc.addActionListener(e -> {
            cboGaDi.setSelectedIndex(0);
            cboGaDen.setSelectedIndex(0);
            dateChooserNgayDi.setDate(new Date());
        });

        btnXoaTrang.addActionListener(e -> {
            txtNhapThongTin.setText("");
            cboGaDi.setSelectedIndex(0);
            cboGaDen.setSelectedIndex(0);
            dateChooserNgayDi.setDate(new Date());
            pnlListTickets.removeAll();
            pnlListTickets.repaint();
            txtNhapThongTin.requestFocus();
            btnDoiToanBo.setEnabled(false);
        });

        btnLamMoi.addActionListener(e -> {
            pnlListTickets.removeAll();
            pnlListTickets.revalidate();
            pnlListTickets.repaint();
            btnDoiToanBo.setEnabled(false);
        });

        // =========================================================================
        // LOGIC THU THẬP DỮ LIỆU ĐỔI VÉ
        // =========================================================================
        btnDoiToanBo.addActionListener(e -> {
            List<ThongTinVeDTO> danhSachVeCanDoi = new ArrayList<>();

            // [MỚI] Biến dùng để ghi nhớ mã chuyến của vé đầu tiên tìm thấy
            String maChuyenTauChung = null;

            // [CŨ - GIỮ NGUYÊN] Duyệt qua UI để lấy vé
            Component[] components = pnlListTickets.getComponents();
            for (Component comp : components) {
                if (comp instanceof TicketPanel) {
                    TicketPanel panel = (TicketPanel) comp;
                    ThongTinVeDTO ve = panel.getTicket();

                    // [CŨ - GIỮ NGUYÊN] Chỉ lấy vé đủ điều kiện (thời gian, trạng thái)
                    if (checkDieuKienDoiVe(ve)) {

                        // --- [MỚI - BẮT ĐẦU] LOGIC KIỂM TRA CÙNG CHUYẾN TÀU ---
                        if (maChuyenTauChung == null) {
                            // Nếu đây là vé hợp lệ đầu tiên, lấy mã chuyến của nó làm chuẩn
                            maChuyenTauChung = ve.getMaChuyenTau();
                        } else {
                            // Những vé sau phải so sánh với mã chuẩn này
                            if (!maChuyenTauChung.equals(ve.getMaChuyenTau())) {
                                JOptionPane.showMessageDialog(this,
                                        "Không thể đổi đồng thời các vé thuộc nhiều chuyến tàu khác nhau!\n" +
                                                "Vui lòng xóa bớt vé khác chuyến hoặc lọc lại danh sách.",
                                        "Lỗi chọn vé", JOptionPane.ERROR_MESSAGE);
                                return; // Dừng ngay lập tức, không cho đi tiếp
                            }
                        }
                        // --- [MỚI - KẾT THÚC] ---

                        // [CŨ - GIỮ NGUYÊN] Thêm vé vào danh sách
                        danhSachVeCanDoi.add(ve);
                    }
                }
            }

            // [CŨ - GIỮ NGUYÊN] Kiểm tra danh sách rỗng
            if (danhSachVeCanDoi.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy vé nào đủ điều kiện đổi trên màn hình!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            } else {
                // [CŨ - GIỮ NGUYÊN] Hiển thị xác nhận
                String msg = "Tìm thấy " + danhSachVeCanDoi.size() + " vé hợp lệ thuộc chuyến " + maChuyenTauChung + ".\n"
                        + "Bạn có chắc chắn muốn đổi TOÀN BỘ danh sách này không?";

                int choice = JOptionPane.showConfirmDialog(this, msg,
                        "Xác nhận đổi toàn bộ", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    // [CŨ - GIỮ NGUYÊN] In log kiểm tra
                    inDanhSachVeRaConsole(danhSachVeCanDoi, "DANH SÁCH ĐỔI TOÀN BỘ");

                    // [CŨ - GIỮ NGUYÊN] Khởi tạo màn hình xử lý & Điều hướng
                    ManHinhXuLyDoiVe panelDoiVeMoi = new ManHinhXuLyDoiVe(danhSachVeCanDoi); // Lớp này là ManHinhBanVe_DoiVe mà bạn đổi tên
                    Window win = SwingUtilities.getWindowAncestor(this);

                    if (win instanceof BanVeDashboard) {
                        BanVeDashboard dashboard = (BanVeDashboard) win;
                        dashboard.themHoacCapNhatCard(panelDoiVeMoi, "banVe_cheDoDoiVe");
                        dashboard.chuyenManHinh("banVe_cheDoDoiVe");
                    } else {
                        JDialog dialog = new JDialog((Frame) win, "Đổi Vé", true);
                        dialog.setContentPane(panelDoiVeMoi);
                        dialog.setSize(1200, 800);
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                    }
                }
            }
        });

        // Layout Panel tổng
        JPanel pnlNorthWrapper = new JPanel(new BorderLayout());
        pnlNorthWrapper.setBackground(Color.WHITE);
        pnlNorthWrapper.add(pnlTop, BorderLayout.CENTER);
        pnlNorthWrapper.add(createNotePanel(), BorderLayout.SOUTH);

        add(pnlNorthWrapper, BorderLayout.NORTH);
        add(pnlResultArea, BorderLayout.CENTER);

        loadDataToCombobox();
    }

    private boolean checkDieuKienDoiVe(ThongTinVeDTO t) {
        if (t == null) return false;
        if ("Đã hủy".equalsIgnoreCase(t.getTrangThai())) return false;
        if (t.getNgayKhoiHanh() != null && t.getGioKhoiHanh() != null) {
            LocalDateTime departureTime = LocalDateTime.of(t.getNgayKhoiHanh(), t.getGioKhoiHanh());
            LocalDateTime deadline = departureTime.minusHours(24);
            return !LocalDateTime.now().isAfter(deadline);
        }
        return true;
    }

    // --- HÀM IN KIỂM TRA DỮ LIỆU ĐƯỢC THU THẬP ---
    private void inDanhSachVeRaConsole(List<ThongTinVeDTO> list, String title) {
        System.out.println("\n====== " + title + " ======");
        for (ThongTinVeDTO ve : list) {
            System.out.println("Vé: " + ve.getMaVe());
            System.out.println(" - Khách: " + ve.getHoTen() + " (" + ve.getMaKhachHang() + ")");
            System.out.println(" - Ngày sinh: " + ve.getNgaySinhStr());
            System.out.println(" - Loại vé: " + ve.getTenLoaiVe() + " (" + ve.getMaLoaiVe() + ")");
            System.out.println("------------------------------------");
        }
        System.out.println("==================================================\n");
    }

    private void loadDataToCombobox() {
        try {
            Vector<Ga> danhSachGa = new GaDao().layDanhSachGa();
            DefaultComboBoxModel<String> modelGaDi = new DefaultComboBoxModel<>();
            modelGaDi.addElement("");
            DefaultComboBoxModel<String> modelGaDen = new DefaultComboBoxModel<>();
            modelGaDen.addElement("");

            if (danhSachGa != null) {
                for (Ga ga : danhSachGa) {
                    modelGaDi.addElement(ga.getTenGa());
                    modelGaDen.addElement(ga.getTenGa());
                }
            }
            cboGaDi.setModel(modelGaDi);
            cboGaDen.setModel(modelGaDen);
        } catch (Exception e) {}
    }

    private void showNotFoundMessage() {
        pnlListTickets.setLayout(new BorderLayout());
        JLabel lblNotFound = new JLabel("Không tìm thấy kết quả phù hợp", SwingConstants.CENTER);
        lblNotFound.setFont(new Font("Arial", Font.BOLD, 20));
        lblNotFound.setForeground(Color.RED);
        pnlListTickets.add(lblNotFound, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(GLOBAL_FONT);
        return lbl;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(GLOBAL_FONT);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 30));
        return btn;
    }

    private JPanel createNotePanel() {
        JPanel pnlNote = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlNote.setBackground(new Color(250, 250, 250));
        pnlNote.setBorder(new EmptyBorder(5, 25, 10, 25));
        JLabel lblTitle = new JLabel("<html><span style='color:red; font-style:italic;'>*Lưu ý :</span></html>");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        pnlNote.add(lblTitle);
        String[] lines = {
                "- Dưới 24 giờ trước khi tàu khởi hành không thể đổi vé",
                "- Từ 24 giờ trở lên sẽ tính 20.000đ/lượt đổi vé",
                "- Nếu khách hàng không mang theo hoặc quên mã vé, có thể tìm kiếm theo mã định danh/cccd và chuyến đi của khách hàng"
        };
        for (String line : lines) {
            JLabel lblLine = new JLabel(line);
            lblLine.setFont(new Font("Arial", Font.PLAIN, 13));
            lblLine.setForeground(Color.BLACK);
            pnlNote.add(lblLine);
        }
        return pnlNote;
    }

    // =================================================================================
    // CLASS TICKET PANEL
    // =================================================================================
    public class TicketPanel extends JPanel {
        public JLabel lblMaVe, lblHoTen, lblCCCD, lblMaChuyen;
        public JLabel lblGaDi, lblNgayDi, lblGioDi;
        public JLabel lblGaDen, lblNgayDen, lblGioDen;
        public JLabel lblSoHieuTau, lblKhoang, lblGiaVe, lblSoCho;
        public JLabel lblLoaiVe, lblNgaySinh, lblSDT;
        public JLabel lblToa, lblTang, lblTrangThai;
        public JButton btnDoiVe, btnClose;

        private ThongTinVeDTO currentTicketDTO;

        private final Font FONT_LABEL = new Font("Arial", Font.PLAIN, 16);
        private final Font FONT_VALUE = new Font("Arial", Font.PLAIN, 16);
        private final Font FONT_STATION = new Font("Arial", Font.BOLD, 20);
        private final NumberFormat currencyVN = NumberFormat.getInstance(new Locale("vi", "VN"));

        public TicketPanel() {
            initUI();

            // Xử lý sự kiện nút Đổi vé đơn lẻ (BÊN TRONG TicketPanel)
            btnDoiVe.addActionListener(e -> {
                if (currentTicketDTO != null) {
                    // 1. Kiểm tra điều kiện đổi vé
                    if (!ManHinhDoiVe.this.checkDieuKienDoiVe(currentTicketDTO)) {
                        JOptionPane.showMessageDialog(this,
                                "Vé này không đủ điều kiện đổi (Quá hạn đổi hoặc đã hủy)!",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 2. Hộp thoại xác nhận
                    int choice = JOptionPane.showConfirmDialog(this,
                            "Bạn có chắc chắn muốn đổi vé mã: " + currentTicketDTO.getMaVe() + " không?",
                            "Xác nhận đổi vé", JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        // 3. Tạo danh sách chứa 1 vé duy nhất
                        List<ThongTinVeDTO> singleList = new ArrayList<>();
                        singleList.add(currentTicketDTO);

                        // --- GỌI HÀM IN KIỂM TRA RA CONSOLE (Tùy chọn) ---
                        inDanhSachVeRaConsole(singleList, "DANH SÁCH ĐỔI VÉ LẺ");

                        // 4. Khởi tạo màn hình Bán Vé (Chế độ Đổi)
                        ManHinhXuLyDoiVe panelDoiVeMoi = new ManHinhXuLyDoiVe(singleList);

                        // 5. Lấy cửa sổ cha (Dashboard)
                        Window win = SwingUtilities.getWindowAncestor(TicketPanel.this);

                        if (win instanceof BanVeDashboard) {
                            BanVeDashboard dashboard = (BanVeDashboard) win;

                            // 6. Thêm Panel mới vào CardLayout và hiển thị
                            dashboard.themHoacCapNhatCard(panelDoiVeMoi, "banVe_cheDoDoiVe");
                            dashboard.chuyenManHinh("banVe_cheDoDoiVe");
                        } else {
                            // Dự phòng cho trường hợp chạy test độc lập
                            JDialog dialog = new JDialog((Frame) win, "Đổi Vé Lẻ", true);
                            dialog.setContentPane(panelDoiVeMoi);
                            dialog.setSize(1200, 800);
                            dialog.setLocationRelativeTo(null);
                            dialog.setVisible(true);
                        }
                    }
                }
            });
        }

        public ThongTinVeDTO getTicket() {
            return this.currentTicketDTO;
        }

        public void setTicket(ThongTinVeDTO t) {
            this.currentTicketDTO = t;

            lblMaVe.setText(t.getMaVe());
            lblHoTen.setText(t.getHoTen());
            lblCCCD.setText(t.getCccd());
            lblMaChuyen.setText(t.getMaChuyenTau());

            // --- SỬA HIỂN THỊ TÊN GA [MÃ GA] ---
            lblGaDi.setText(t.getGaDi() + " [" + t.getMaGaDi() + "]");
            lblGaDen.setText(t.getGaDen() + " [" + t.getMaGaDen() + "]");

            lblNgayDi.setText(t.getNgayDiStr());
            lblGioDi.setText(t.getGioDiStr());
            lblNgayDen.setText(t.getNgayDenStr());
            lblGioDen.setText(t.getGioDenStr());
            lblSoHieuTau.setText(t.getSoHieuTau());
            lblToa.setText(t.getMaToa() + " - " + t.getLoaiToa());
            lblSoCho.setText(t.getSoCho());
            lblKhoang.setText(t.getKhoang());
            lblTang.setText(t.getTang());
            lblLoaiVe.setText(t.getTenLoaiVe());

            // --- GỌI HÀM getNgaySinhStr() ĐÃ FORMAT SẴN TRONG DTO ---
            lblNgaySinh.setText(t.getNgaySinhStr());

            lblSDT.setText(t.getSoDienThoai());
            lblGiaVe.setText(currencyVN.format(t.getGiaVe()) + " VND");

            boolean allowChange = checkDieuKienDoiVe(t);
            if (!allowChange) {
                if ("Đã hủy".equalsIgnoreCase(t.getTrangThai())) {
                    lblTrangThai.setText("Đã hủy");
                    lblTrangThai.setForeground(Color.RED);
                    this.setBorder(new LineBorder(Color.RED, 3));
                } else {
                    lblTrangThai.setText("Đã bán (Hết hạn đổi)");
                    lblTrangThai.setForeground(Color.BLUE);
                    this.setBorder(new LineBorder(Color.GRAY, 3));
                }
                btnDoiVe.setText("Không khả dụng");
                btnDoiVe.setEnabled(false);
                btnDoiVe.setBackground(Color.LIGHT_GRAY);
            } else {
                lblTrangThai.setText("Đã bán");
                lblTrangThai.setForeground(Color.BLUE);
                btnDoiVe.setText("Đổi vé");
                btnDoiVe.setBackground(new Color(65, 85, 255));
                btnDoiVe.setEnabled(true);
                this.setBorder(new LineBorder(new Color(76, 175, 80), 3));
            }
        }

        private void initUI() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(new LineBorder(new Color(76, 175, 80), 3));
            setPreferredSize(new Dimension(0, 400));

            JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            pnlHeader.setOpaque(false);
            btnClose = new JButton("X");
            btnClose.setFont(new Font("Arial", Font.PLAIN, 14));
            btnClose.setBackground(new Color(234, 67, 53));
            btnClose.setForeground(Color.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.setBorderPainted(false);
            btnClose.setPreferredSize(new Dimension(50, 30));
            btnClose.addActionListener(e -> {
                Container parent = this.getParent();
                if(parent != null) {
                    parent.remove(this);
                    parent.revalidate();
                    parent.repaint();
                }
            });
            pnlHeader.add(btnClose);
            add(pnlHeader, BorderLayout.NORTH);

            JPanel pnlContent = new JPanel(new GridBagLayout());
            pnlContent.setOpaque(false);
            pnlContent.setBorder(new EmptyBorder(0, 20, 0, 20));

            lblMaVe = new JLabel(); lblHoTen = new JLabel(); lblCCCD = new JLabel();
            lblMaChuyen = new JLabel();
            lblGaDi = new JLabel(); lblGaDi.setFont(FONT_STATION);
            lblNgayDi = new JLabel(); lblGioDi = new JLabel();
            lblGaDen = new JLabel(); lblGaDen.setFont(FONT_STATION);
            lblNgayDen = new JLabel(); lblGioDen = new JLabel();
            lblSoHieuTau = new JLabel(); lblSoHieuTau.setFont(new Font("Arial", Font.BOLD, 19));
            lblSoCho = new JLabel(); lblSoCho.setFont(new Font("Arial", Font.BOLD, 16));
            lblKhoang = new JLabel(); lblGiaVe = new JLabel();
            lblLoaiVe = new JLabel(); lblNgaySinh = new JLabel(); lblSDT = new JLabel();
            lblToa = new JLabel(); lblTang = new JLabel();
            lblTrangThai = new JLabel(); lblTrangThai.setFont(new Font("Arial", Font.BOLD, 16));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(5, 0, 5, 10);

            addLabelPair(pnlContent, "Mã vé : ", lblMaVe, 0, 0);
            addLabelPair(pnlContent, "Họ tên : ", lblHoTen, 0, 1);

            // --- HIỂN THỊ NGÀY SINH ---
            addLabelPair(pnlContent, "Ngày Sinh : ", lblNgaySinh, 2, 1);

            addLabelPair(pnlContent, "Số CCCD/Định danh : ", lblCCCD, 0, 2);
            addLabelPair(pnlContent, "Loại vé : ", lblLoaiVe, 2, 0);
            addLabelPair(pnlContent, "Số điện thoại : ", lblSDT, 2, 2);

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.insets = new Insets(15, 0, 5, 0);
            JPanel pnlMaChuyen = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            pnlMaChuyen.setOpaque(false);
            JLabel lblTitleMC = new JLabel("Mã chuyến : "); lblTitleMC.setFont(FONT_LABEL);
            lblMaChuyen.setFont(FONT_VALUE);
            pnlMaChuyen.add(lblTitleMC); pnlMaChuyen.add(lblMaChuyen);
            pnlContent.add(pnlMaChuyen, gbc);

            gbc.gridy = 4; gbc.insets = new Insets(5, 50, 15, 0);
            JPanel pnlRoute = new JPanel(new GridBagLayout());
            pnlRoute.setOpaque(false);
            GridBagConstraints gbcRoute = new GridBagConstraints();
            gbcRoute.gridx = 0; gbcRoute.gridy = 0; gbcRoute.anchor = GridBagConstraints.WEST;
            pnlRoute.add(lblGaDi, gbcRoute);
            gbcRoute.gridy = 1; pnlRoute.add(lblNgayDi, gbcRoute);
            gbcRoute.gridy = 2; pnlRoute.add(lblGioDi, gbcRoute);

            gbcRoute.gridx = 1; gbcRoute.gridy = 0; gbcRoute.anchor = GridBagConstraints.CENTER;
            gbcRoute.insets = new Insets(0, 40, 0, 40);
            JLabel lblDash = new JLabel("-"); lblDash.setFont(new Font("Arial", Font.BOLD, 22));
            pnlRoute.add(lblDash, gbcRoute);

            gbcRoute.gridx = 2; gbcRoute.gridy = 0; gbcRoute.insets = new Insets(0,0,0,0); gbcRoute.anchor = GridBagConstraints.WEST;
            pnlRoute.add(lblGaDen, gbcRoute);
            gbcRoute.gridy = 1; pnlRoute.add(lblNgayDen, gbcRoute);
            gbcRoute.gridy = 2; pnlRoute.add(lblGioDen, gbcRoute);
            pnlContent.add(pnlRoute, gbc);

            gbc.gridwidth = 1; gbc.insets = new Insets(5, 0, 5, 10);
            addLabelPair(pnlContent, "Số hiệu tàu : ", lblSoHieuTau, 0, 5);
            addLabelPair(pnlContent, "Toa : ", lblToa, 2, 5);
            addLabelPair(pnlContent, "Số chỗ : ", lblSoCho, 0, 6);
            addLabelPair(pnlContent, "Khoang : ", lblKhoang, 0, 7);
            addLabelPair(pnlContent, "Tầng : ", lblTang, 2, 7);
            addLabelPair(pnlContent, "Giá vé : ", lblGiaVe, 0, 8);

            gbc.gridx = 2; gbc.gridy = 8;
            JLabel lblTitleTT = new JLabel("Trạng thái : "); lblTitleTT.setFont(FONT_LABEL);
            pnlContent.add(lblTitleTT, gbc);
            gbc.gridx = 3; pnlContent.add(lblTrangThai, gbc);

            add(pnlContent, BorderLayout.CENTER);

            JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            pnlFooter.setOpaque(false);
            btnDoiVe = new JButton("Đổi vé");
            btnDoiVe.setFont(new Font("Arial", Font.BOLD, 15));
            btnDoiVe.setBackground(new Color(65, 85, 255));
            btnDoiVe.setForeground(Color.WHITE);
            btnDoiVe.setFocusPainted(false);
            btnDoiVe.setBorderPainted(false);
            btnDoiVe.setPreferredSize(new Dimension(150, 35));
            pnlFooter.add(btnDoiVe);
            add(pnlFooter, BorderLayout.SOUTH);
        }

        private void addLabelPair(JPanel panel, String title, JLabel value, int col, int row) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 0, 3, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = col; gbc.gridy = row;
            JLabel lblTitle = new JLabel(title); lblTitle.setFont(FONT_LABEL);
            panel.add(lblTitle, gbc);
            gbc.gridx = col + 1; gbc.weightx = 1.0;
            value.setFont(FONT_VALUE);
            panel.add(value, gbc);
        }
    }

    public static void main(String[] args) {
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {}
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Màn Hình Đổi Vé");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ManHinhDoiVe mainPanel = new ManHinhDoiVe();
            frame.add(mainPanel);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}