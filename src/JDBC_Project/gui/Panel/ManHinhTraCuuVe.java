package JDBC_Project.gui.Panel;

import dao.VeDAO;
import entity.*;
import gui.Popup.PopUpVeTau;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;


import java.awt.Dialog;
/**
 * ManHinhTraCuuVe: Màn hình Tra cứu vé theo mẫu.
 * ĐÃ SỬA LỖI: Logic gán biến instance cho JTextField.
 */
public class ManHinhTraCuuVe extends JPanel implements ActionListener {

    // ====== MÀU & FONT ======
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);

    // ====== COMPONENTS ======
    private ButtonGroup searchGroup;
    private JRadioButton rbMaVe, rbHoTenSDT, rbHoTenCCCD;
    private JButton btnTimKiem, btnXoaBoLoc;

    // Input panels
    private JPanel pnlInputContainer;
    private CardLayout clInput;

    // SỬA LỖI: Tách biệt các trường input cho từng panel
    private JTextField txtMaVe;
    private JTextField txtHoTen_SDT, txtSDT; // Panel SĐT
    private JTextField txtHoTen_CCCD, txtCCCD; // Panel CCCD

    // Table
    private JTable tableKetQua;
    private DefaultTableModel tableModel;

    // DAO
    private VeDAO veDAO;

    // Chế độ tìm kiếm
    private static final String MODE_MA_VE = "MaVe";
    private static final String MODE_SDT = "HoTenSDT";
    private static final String MODE_CCCD = "HoTenCCCD";

    public ManHinhTraCuuVe() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(BG_COLOR);

        try {
            veDAO = new dao.VeDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        // SỬA LỖI: Khởi tạo tất cả các trường input một lần duy nhất
        txtMaVe = new JTextField(20);
        txtHoTen_SDT = new JTextField(15);
        txtSDT = new JTextField(15);
        txtHoTen_CCCD = new JTextField(15);
        txtCCCD = new JTextField(15);

        // Header + nội dung chính
        add(createHeaderPanel(), BorderLayout.NORTH);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(createKhuVucTimKiem());
        content.add(Box.createRigidArea(new Dimension(0, 15)));
        content.add(createKhuVucKetQua());
        add(content, BorderLayout.CENTER);

        btnTimKiem.addActionListener(this);
        btnXoaBoLoc.addActionListener(this);


        // BỔ SUNG: Thêm Mouse Listener cho bảng
        tableKetQua.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });


        xoaBoLoc();
    }




    // Trong lớp ManHinhTraCuuVe
    private void tableMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tableKetQua.rowAtPoint(evt.getPoint());
        int col = tableKetQua.columnAtPoint(evt.getPoint());

        // Kiểm tra xem có click vào cột "Chi tiết" (cột cuối cùng) hay không
        if (col == tableKetQua.getColumnCount() - 1) { // Lấy index của cột cuối cùng

            // Lấy Mã vé từ cột thứ 2 (index 1)
            String maVe = tableModel.getValueAt(row, 1).toString();
            System.out.println("Đã chọn mã vé: " + maVe);

            // Tra cứu lại vé dựa trên Mã vé
            Ve ve = veDAO.getVeById(maVe);
//            Ve ve = veDAO.createMockVe();
            // Hiển thị chi tiết vé trong dialog

            if (ve != null) {
                hienThiChiTietVe(ve);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lấy chi tiết vé.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hienThiChiTietVe(Ve ve) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Chi tiết vé: " + ve.getMaVe(),
                Dialog.ModalityType.APPLICATION_MODAL // Sử dụng Constructor JDialog(Window, String, ModalityType)
        );        PopUpVeTau ticketPanel = new PopUpVeTau(ve);

        dialog.getContentPane().add(ticketPanel, BorderLayout.CENTER);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ==============================================================
    // I. HEADER
    // ==============================================================
    private JPanel createHeaderPanel() {
        // ... (Giữ nguyên) ...
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel lblLeft = new JLabel("Xin chào nhân viên, Trần Nam Sơn");
        lblLeft.setFont(FONT_BOLD_14);
        JLabel lblRight = new JLabel("Tra cứu vé");
        lblRight.setFont(FONT_TITLE);
        panel.add(lblLeft, BorderLayout.WEST);
        panel.add(lblRight, BorderLayout.EAST);
        return panel;
    }

    // ==============================================================
    // II. KHU VỰC TÌM KIẾM
    // ==============================================================
    private JPanel createKhuVucTimKiem() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // ---- 1. Radio chọn loại tìm kiếm ----
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        tabsPanel.setOpaque(false);
        tabsPanel.setBorder(new EmptyBorder(10, 15, 5, 15));

        searchGroup = new ButtonGroup();
        rbMaVe = createRadioButton("Mã vé", MODE_MA_VE, true);
        rbHoTenSDT = createRadioButton("Họ tên + Số điện thoại", MODE_SDT, false);
        rbHoTenCCCD = createRadioButton("Họ tên + CCCD", MODE_CCCD, false);

        tabsPanel.add(rbMaVe);
        tabsPanel.add(rbHoTenSDT);
        tabsPanel.add(rbHoTenCCCD);

        // ---- 2. Panel nhập liệu (CardLayout) ----
        clInput = new CardLayout();
        pnlInputContainer = new JPanel(clInput);
        pnlInputContainer.setOpaque(false);
        pnlInputContainer.setBorder(new EmptyBorder(0, 15, 10, 15));

        // SỬA LỖI: Truyền các biến instance đã khởi tạo vào
        pnlInputContainer.add(createInputPanelMaVe(txtMaVe), MODE_MA_VE);
        pnlInputContainer.add(createInputPanelHoTenGiaTriPhu("Số điện thoại:", txtHoTen_SDT, txtSDT), MODE_SDT);
        pnlInputContainer.add(createInputPanelHoTenGiaTriPhu("Số CCCD:", txtHoTen_CCCD, txtCCCD), MODE_CCCD);

        // ---- 3. Nút chức năng ----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setOpaque(false);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(PRIMARY_COLOR);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(FONT_BOLD_14);
        buttonPanel.add(btnTimKiem);

        btnXoaBoLoc = new JButton("Xóa bộ lọc");
        btnXoaBoLoc.setFont(FONT_BOLD_14);
        buttonPanel.add(btnXoaBoLoc);

        // ---- 4. Ghép lại ----
        panel.add(tabsPanel);
        panel.add(pnlInputContainer);
        panel.add(buttonPanel);
        return panel;
    }

    private JRadioButton createRadioButton(String text, String command, boolean selected) {
        JRadioButton rb = new JRadioButton(text);
        rb.setActionCommand(command);
        rb.setSelected(selected);
        rb.setOpaque(false);
        rb.setFont(FONT_PLAIN_14);
        rb.addActionListener(e -> clInput.show(pnlInputContainer, command));
        searchGroup.add(rb);
        return rb;
    }

    // SỬA LỖI: Nhận JTextField làm tham số
    private JPanel createInputPanelMaVe(JTextField maVeField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setOpaque(false);
        JLabel lbl = new JLabel("Mã vé:");
        lbl.setFont(FONT_PLAIN_14);
        maVeField.setFont(FONT_PLAIN_14);
        maVeField.setPreferredSize(new Dimension(250, 35));
        panel.add(lbl);
        panel.add(maVeField);
        return panel;
    }

    // SỬA LỖI: Nhận JTextField làm tham số
    private JPanel createInputPanelHoTenGiaTriPhu(String labelPhu, JTextField hoTenField, JTextField giaTriPhuField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setOpaque(false);

        JLabel lblHoTen = new JLabel("Họ tên:");
        lblHoTen.setFont(FONT_PLAIN_14);
        hoTenField.setFont(FONT_PLAIN_14);
        hoTenField.setPreferredSize(new Dimension(200, 35));

        JLabel lblPhu = new JLabel(labelPhu);
        lblPhu.setFont(FONT_PLAIN_14);
        giaTriPhuField.setFont(FONT_PLAIN_14);
        giaTriPhuField.setPreferredSize(new Dimension(200, 35));

        panel.add(lblHoTen);
        panel.add(hoTenField);
        panel.add(lblPhu);
        panel.add(giaTriPhuField);
        return panel;
    }


    // ==============================================================
    // III. KHU VỰC KẾT QUẢ
    // ==============================================================
    private JScrollPane createKhuVucKetQua() {
        // ... (Giữ nguyên) ...
        String[] columnNames = {"STT", "Mã vé", "Tên hành khách", "Trạng thái", "Hành trình", "Loại toa", "Ghế/Giường", "Ngày khởi hành", "Giá vé", "Chi tiết"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableKetQua = new JTable(tableModel);
        tableKetQua.setRowHeight(28);
        tableKetQua.setFont(FONT_PLAIN_14);
        tableKetQua.getTableHeader().setFont(FONT_BOLD_14);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableKetQua.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableKetQua.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scrollPane = new JScrollPane(tableKetQua);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách vé"));
        return scrollPane;
    }

    // ==============================================================
    // IV. LOGIC TÌM KIẾM
    // ==============================================================
    private void xoaBoLoc() {
        if (txtMaVe != null) txtMaVe.setText("");
        if (txtHoTen_SDT != null) txtHoTen_SDT.setText("");
        if (txtSDT != null) txtSDT.setText("");
        if (txtHoTen_CCCD != null) txtHoTen_CCCD.setText("");
        if (txtCCCD != null) txtCCCD.setText("");

        tableModel.setRowCount(0);
        rbMaVe.setSelected(true);
        clInput.show(pnlInputContainer, MODE_MA_VE);
    }

    private void timKiemVe() {
        if (veDAO == null) return;

        String mode = searchGroup.getSelection().getActionCommand();
        List<Ve> ketQua = new Vector<>();
        String hoTen = null;
        String sdt = null;
        String cccd = null;
        String maVe = null;

        // 1. Thu thập dữ liệu từ các trường nhập liệu CHÍNH XÁC
        if (mode.equals(MODE_MA_VE)) {
            maVe = txtMaVe.getText().trim();
            if (maVe.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã vé!", "Cảnh báo", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (mode.equals(MODE_SDT)) {
            hoTen = txtHoTen_SDT.getText().trim();
            sdt = txtSDT.getText().trim();
            if (hoTen.isEmpty() && sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ít nhất Họ tên hoặc SĐT!", "Cảnh báo", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (mode.equals(MODE_CCCD)) {
            hoTen = txtHoTen_CCCD.getText().trim();
            cccd = txtCCCD.getText().trim();
            if (hoTen.isEmpty() && cccd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ít nhất Họ tên hoặc CCCD!", "Cảnh báo", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 2. Gọi DAO
        // SỬA LỖI: Đảm bảo VeDAO có phương thức 4 tham số (đã cung cấp)
        ketQua = veDAO.timVeTheoKhachHang(hoTen, sdt, cccd, maVe);


        // 3. Hiển thị kết quả...
        if (ketQua == null || ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé nào phù hợp.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            tableModel.setRowCount(0);
            return;
        }

        napDuLieuLenBang(ketQua);
    }

    private void napDuLieuLenBang(List<Ve> danhSach) {
        tableModel.setRowCount(0);
        int stt = 1;

        for (Ve ve : danhSach) {
            KhachHang kh = ve.getKhachHangChiTiet();
            ChuyenTau ct = ve.getChuyenTauChiTiet();
            ChoDat cd = ve.getChoDatChiTiet();

            String tenKhach = (kh != null) ? kh.getHoTen() : "N/A";
            String hanhTrinh = (ct != null && ct.gaDi.tenGa != null && ct.gaDi.tenGa  != null)
                    ? ct.getGaDi().getTenGa() + " - " + ct.getGaDen().getTenGa() : "N/A";
            String loaiToa = (cd != null) ? cd.getMaToa() : "N/A";
            String ghe = (cd != null) ? cd.getSoCho() : "N/A";
            String ngayKH = (ct != null && ct.getNgayKhoiHanh() != null) ? ct.getNgayKhoiHanh().toString() : "N/A";

            // SỬA LỖI: HIỂN THỊ TRẠNG THÁI THỰC TẾ
            String trangThaiVe = "Đã bán";
            if (ve.getTrangThai() != null) {
                if (ve.getTrangThai().equalsIgnoreCase("DA-HUY")) {
                    trangThaiVe = "Đã hủy";
                } else if (ve.getTrangThai().equalsIgnoreCase("DA-BAN")) {
                    trangThaiVe = "Đã bán";
                } else {
                    trangThaiVe = ve.getTrangThai();
                }
            }

            tableModel.addRow(new Object[]{
                    stt++,
                    ve.getMaVe(),
                    tenKhach,
                    trangThaiVe, // Hiển thị trạng thái CSDL
                    hanhTrinh,
                    loaiToa,
                    ghe,
                    ngayKH,
                    String.format("%,.0f", ve.getGiaVe()),
                    "Xem chi tiết"
            });
        }
    }

    // ==============================================================
    // V. EVENTS
    // ==============================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTimKiem) timKiemVe();
        if (e.getSource() == btnXoaBoLoc) xoaBoLoc();
    }

    // ==============================================================
    // VI. MAIN TEST
    // ==============================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tra cứu vé");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ManHinhTraCuuVe());
            frame.setVisible(true);
        });
    }
}