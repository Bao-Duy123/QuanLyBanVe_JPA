package JDBC_Project.gui.Panel;

import java.awt.Toolkit;
import java.awt.event.InputEvent; // Hoặc java.awt.event.*
import java.awt.event.KeyEvent; // Hoặc java.awt.event.*
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.JComponent; // Nếu chưa có

import dao.ChiTietHoaDonDAO;
import dao.ChuyenTauDao;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import entity.ChuyenTau;
import entity.HoaDon;
import entity.KhachHang;
import gui.Popup.PopUpChiTietHoaDon;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

// Thêm các import này lên đầu file cùng các import khác
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
// --- Kết thúc thêm import ---



/**
 *
 * @author TranSon-Code
 */
public class ManHinhTraCuuHoaDon extends JPanel {

    /**
     * Creates new form ManHinhTraCuuHoaDon
     */
    public ManHinhTraCuuHoaDon() {
        initComponents();

        // Định nghĩa các placeholder texts
        placeholders.put("Số điện thoại", "VD : 0123456789");
        placeholders.put("Số CCCD", "VD : 079299000001");
        placeholders.put("Mã hóa đơn", "VD : HD0225102500010002");

        JTableHeader header = Table_DanhSachHoaDon.getTableHeader();


        header.setBorder(BorderFactory.createEmptyBorder());
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setBorder(BorderFactory.createEmptyBorder());
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        Dimension newSize = new Dimension(163, 50);

        comboBox_Year1.setPreferredSize(newSize);
        comboBox_Year1.setMinimumSize(newSize);
        comboBox_Year1.setMaximumSize(newSize);
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) Table_DanhSachHoaDon.getDefaultRenderer(Object.class);
        renderer.setVerticalAlignment(SwingConstants.TOP);


        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) Table_DanhSachHoaDon.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        TableColumnModel columnModel = Table_DanhSachHoaDon.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150); // Cột 0 - Mã hóa đơn
        columnModel.getColumn(1).setPreferredWidth(180); // Cột 1 - Khách hàng
        columnModel.getColumn(2).setPreferredWidth(180); // cột tuyến

        DefaultTableCellRenderer centerDataRenderer = new DefaultTableCellRenderer();
        centerDataRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerDataRenderer.setVerticalAlignment(SwingConstants.TOP); // Giữ lại căn lề trên

        columnModel.getColumn(2).setCellRenderer(centerDataRenderer); // Cột Tuyến
        columnModel.getColumn(3).setCellRenderer(centerDataRenderer); // Cột Số lượng vé
        columnModel.getColumn(4).setCellRenderer(centerDataRenderer); // Cột Ngày khởi hành
        columnModel.getColumn(5).setCellRenderer(centerDataRenderer); // Cột Tổng tiền

        // Custom cho cột tùy chọn
        DefaultTableCellRenderer linkRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                // Lấy component (là một JLabel) từ lớp cha
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 1. Đặt văn bản thành HTML để có gạch chân (underline)
                if (value != null) {
                    setText("<html><u>" + value.toString() + "</u></html>");
                }

                // 2. Đặt màu chữ thành xanh dương
                setForeground(Color.BLUE);

                // 3. Giữ nguyên các cài đặt căn lề từ yêu cầu trước
                setVerticalAlignment(SwingConstants.TOP);
                setHorizontalAlignment(SwingConstants.CENTER);

                return c;
            }
        };
        Table_DanhSachHoaDon.getColumnModel().getColumn(7).setCellRenderer(linkRenderer);

        // THÊM SỰ KIỆN CHO CỘT TÙY CHỌN ĐỂ HIỂN THỊ CHI TIẾT HÓA ĐƠN
        Table_DanhSachHoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Lấy vị trí cột và hàng mà người dùng click
                int row = Table_DanhSachHoaDon.rowAtPoint(evt.getPoint());
                int col = Table_DanhSachHoaDon.columnAtPoint(evt.getPoint());

                // Kiểm tra xem họ có click vào cột 6 ("Tùy chọn") không
                if (col == 7 && row >= 0) { // row >= 0 để đảm bảo họ click vào hàng có dữ liệu

                    // Đã click đúng cột "Xem"
                    // Lấy mã hóa đơn từ cột 0 của hàng đó
                    Object maHoaDonObj = Table_DanhSachHoaDon.getValueAt(row, 0);
                    String maHoaDon = (maHoaDonObj != null) ? maHoaDonObj.toString() : "N/A";

                    // Gọi hàm để hiển thị popup
                    showChiTietPopup(maHoaDon);
                }
            }
        });
        // --- KẾT THÚC CODE THÊM SỰ KIỆN ---




        // --- BẮT ĐẦU CODE SỬA LỖI KHÔNG DÁN ĐƯỢC ---

// Lấy InputMap và ActionMap của ô text khi nó được focus
        InputMap inputMap = txt_NhapThongTin.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = txt_NhapThongTin.getActionMap();

// Lấy phím tắt chuẩn cho Paste trên macOS (Cmd+V)
        KeyStroke pasteKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());

// Nếu InputMap chưa có key binding cho Cmd+V hoặc nó đang trỏ sai action:
        if (inputMap.get(pasteKeyStroke) == null || !inputMap.get(pasteKeyStroke).equals(DefaultEditorKit.pasteAction)) {
            System.out.println("Re-binding Cmd+V for Paste action..."); // In ra để biết code này chạy
            inputMap.put(pasteKeyStroke, DefaultEditorKit.pasteAction);
        }


        if (actionMap.get(DefaultEditorKit.pasteAction) == null) {
            actionMap.put(DefaultEditorKit.pasteAction, new DefaultEditorKit.PasteAction());
        }

// (Tùy chọn) Thêm cả Ctrl+V cho chắc, dù trên Mac dùng Cmd+V
        KeyStroke ctrlVPasteKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
        if (inputMap.get(ctrlVPasteKeyStroke) == null || !inputMap.get(ctrlVPasteKeyStroke).equals(DefaultEditorKit.pasteAction)) {
            inputMap.put(ctrlVPasteKeyStroke, DefaultEditorKit.pasteAction);
        }
// --- KẾT THÚC CODE SỬA LỖI KHÔNG DÁN ĐƯỢC ---

//    // Làm rỗng bảng danh sách hoóa đơn
        lamRongBangHoaDon();

    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        lblTieuDeLocTG = new JLabel();
        lblNhapThang = new JLabel();
        ComboBox_DanhMucTimKiem = new JComboBox<>();
        lblThongTinTraCuu = new JLabel();
        txt_NhapThongTin = new JTextField();
        btn_TraCuu = new JButton();
        lblNhapThongTin = new JLabel();
        lblDanhMucTimKiem = new JLabel();
        comboBox_Month = new JComboBox<>();
        lblNhapNam = new JLabel();
        btnKetQuaTimKiem = new JLabel();
        lblThongBaoKetQua = new JLabel() {
            {
                // Tắt chế độ vẽ nền chữ nhật mặc định
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. VẼ NỀN BO TRÒN:
                // Nó sẽ tự động lấy màu background (xanh) bạn đã set
                g2.setColor(getBackground());

                // Bạn có thể thay đổi số 15, 15 để bo tròn nhiều hay ít
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();

                // 2. VẼ CHỮ LÊN TRÊN:
                // Gọi hàm gốc để vẽ chữ (text) lên trên
                super.paintComponent(g);
            }
        };
        jScrollPane1 = new JScrollPane();
        Table_DanhSachHoaDon = new JTable();
        btn_Xoa = new JButton();
        btn_LamMoi = new JButton();
        btnFilter1 = new JButton();
        comboBox_Year1 = new JComboBox<>();

        lblTieuDeLocTG.setFont(new Font("Helvetica Neue", 0, 28)); // NOI18N
        lblTieuDeLocTG.setText("Tìm kiếm theo thời gian");

        lblNhapThang.setFont(new Font("Helvetica Neue", 1, 16)); // NOI18N
        lblNhapThang.setText("Tháng ");

        ComboBox_DanhMucTimKiem.setBackground(new Color(220, 222, 221));
        ComboBox_DanhMucTimKiem.setFont(new Font("Helvetica Neue", 0, 18)); // NOI18N
        ComboBox_DanhMucTimKiem.setForeground(new Color(102, 102, 102));
        ComboBox_DanhMucTimKiem.setModel(new DefaultComboBoxModel<>(new String[] { "Số điện thoại", "Số CCCD", "Mã hóa đơn" }));
        ComboBox_DanhMucTimKiem.setMinimumSize(new Dimension(124, 100));
        ComboBox_DanhMucTimKiem.setPreferredSize(new Dimension(133, 50));
        ComboBox_DanhMucTimKiem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePlaceholderText(); // Gọi hàm cập nhật khi ComboBox thay đổi
            }
        });

        lblThongTinTraCuu.setFont(new Font("Helvetica Neue", 1, 18)); // NOI18N
        lblThongTinTraCuu.setText("Thông tin tra cứu ");

        txt_NhapThongTin.setBackground(new Color(220, 222, 221));
        txt_NhapThongTin.setFont(new Font("Helvetica Neue", 0, 18)); // NOI18N
        txt_NhapThongTin.setForeground(new Color(102, 102, 102));
        txt_NhapThongTin.setMinimumSize(new Dimension(124, 40));
        txt_NhapThongTin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                txt_NhapThongTinActionPerformed(evt);
            }
        });

        // 1. Lưu màu chữ mặc định của JTextField
        defaultTextColor = txt_NhapThongTin.getForeground();


        // 4. Gắn FocusListener cho JTextField để xử lý hiển thị/ẩn placeholder
        txt_NhapThongTin.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // Khi click vào JTextField
                if (txt_NhapThongTin.getText().equals(currentPlaceholder)) {
                    txt_NhapThongTin.setText(""); // Xóa placeholder
                    txt_NhapThongTin.setForeground(defaultTextColor); // Đổi màu chữ về mặc định
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Khi click ra ngoài JTextField
                if (txt_NhapThongTin.getText().trim().isEmpty()) {
                    setPlaceholder(); // Đặt lại placeholder nếu ô trống
                }
            }
        });
        // Cập nhật và hiển thị placeholder ban đầu
        updatePlaceholderText();



        btn_TraCuu.setBackground(new Color(0, 102, 255));
        btn_TraCuu.setFont(new Font("Helvetica Neue", 0, 16)); // NOI18N
        btn_TraCuu.setForeground(new Color(255, 255, 255));
        btn_TraCuu.setText("Tra cứu");
        btn_TraCuu.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        btn_TraCuu.setBorderPainted(false);
        btn_TraCuu.setOpaque(true);
        btn_TraCuu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_TraCuuActionPerformed(evt);
            }
        });

        lblNhapThongTin.setFont(new Font("Helvetica Neue", 0, 28)); // NOI18N
        lblNhapThongTin.setText("Nhập thông tin tra cứu hóa đơn");

        lblDanhMucTimKiem.setFont(new Font("Helvetica Neue", 1, 18)); // NOI18N
        lblDanhMucTimKiem.setText("Tìm kiếm theo ");

        comboBox_Month.setBackground(new Color(220, 222, 221));
        comboBox_Month.setFont(new Font("Helvetica Neue", 0, 18)); // NOI18N
        comboBox_Month.setModel(new DefaultComboBoxModel<>(new String[] { "<html><br><html>", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", " " }));
        comboBox_Month.setMinimumSize(new Dimension(72, 50));
        comboBox_Month.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                comboBox_MonthActionPerformed(evt);
            }
        });

        lblNhapNam.setFont(new Font("Helvetica Neue", 1, 16)); // NOI18N
        lblNhapNam.setText("Năm");

        btnKetQuaTimKiem.setFont(new Font("Apple Braille", 0, 26)); // NOI18N
        btnKetQuaTimKiem.setText("Kết quả tìm kiếm");

        lblThongBaoKetQua.setBackground(new Color(0, 204, 102));
        lblThongBaoKetQua.setFont(new Font("Helvetica Neue", 1, 14)); // NOI18N
        lblThongBaoKetQua.setForeground(new Color(255, 255, 255));
        lblThongBaoKetQua.setHorizontalAlignment(SwingConstants.CENTER);
        lblThongBaoKetQua.setText("0 hóa đơn");

        Table_DanhSachHoaDon.setFont(new Font("Helvetica Neue", 0, 16)); // NOI18N
        Table_DanhSachHoaDon.setModel(new DefaultTableModel(
                new Object [][] {
                        {"HD2109202400010005", "<html>Nguyễn Văn Nam<br>0123456789</html>", "Sài Gòn - Hà Nội", "3", "<html>21-09-2024<br>08:30<html>", "2.420.000đ", "Hóa đơn mua vé", "Xem"},
                        {"HD1901202400080436", "<html>Nguyễn Văn Nam<br>0123456789</html>", "Sài Gòn - Đà Nẵng", "2", "<html>10-01-2024<br>17:25</html>", "1.850.000đ", "Hóa đơn mua vé", "Xem"},
                        {null, null, null, null, "", null, null, null}
                },
                new String [] {
                        "Mã hóa đơn", "Khách hàng", "Tuyến", "Số lượng vé", "Ngày khởi hành", "Tổng tiền", "Loại hóa đơn", "Tùy chọn"
                }
        ) {
            Class[] types = new Class [] {
                    String.class, String.class, String.class, String.class, Object.class, String.class, String.class, Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        Table_DanhSachHoaDon.setRowHeight(60);
        jScrollPane1.setViewportView(Table_DanhSachHoaDon);

        btn_Xoa.setBackground(new Color(255, 0, 51));
        btn_Xoa.setFont(new Font("Helvetica Neue", 0, 16)); // NOI18N
        btn_Xoa.setForeground(new Color(255, 255, 255));
        btn_Xoa.setText("Xóa");
        btn_Xoa.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        btn_Xoa.setBorderPainted(false);
        btn_Xoa.setOpaque(true);
        btn_Xoa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_XoaActionPerformed(evt);
            }
        });

        btn_LamMoi.setBackground(new Color(255, 153, 0));
        btn_LamMoi.setForeground(new Color(255, 255, 255));
        btn_LamMoi.setText("Làm mới");
        btn_LamMoi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_LamMoiActionPerformed(evt);
            }
        });


        btnFilter1.setBackground(new Color(0, 102, 255));
        btnFilter1.setFont(new Font("Helvetica Neue", 0, 16)); // NOI18N
        btnFilter1.setForeground(new Color(255, 255, 255));
        btnFilter1.setText("Lọc");
        btnFilter1.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        btnFilter1.setBorderPainted(false);
        btnFilter1.setOpaque(true);
        btnFilter1.setPreferredSize(new Dimension(33, 50));
        btnFilter1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });

        comboBox_Year1.setBackground(new Color(220, 222, 221));
        comboBox_Year1.setFont(new Font("Helvetica Neue", 0, 18)); // NOI18N
        comboBox_Year1.setModel(new DefaultComboBoxModel<>(new String[] { "<html><br></html>", "2020", "2021", "2022", "2023", "2024", "2025" }));
        comboBox_Year1.setMinimumSize(new Dimension(72, 50));
        comboBox_Year1.setPreferredSize(new Dimension(74, 50));
        comboBox_Year1.setSize(new Dimension(20, 20));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addComponent(ComboBox_DanhMucTimKiem, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(comboBox_Month, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(10, 10, 10)
                                                                                .addComponent(lblDanhMucTimKiem)))
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(71, 71, 71)
                                                                                .addComponent(lblThongTinTraCuu))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addGap(59, 59, 59)
                                                                                                .addComponent(txt_NhapThongTin, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE))
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addGap(57, 57, 57)
                                                                                                .addComponent(comboBox_Year1, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
                                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addComponent(btnFilter1, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)))
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(btn_TraCuu, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(btn_Xoa, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE))))
                                                        .addComponent(lblNhapThongTin)
                                                        .addComponent(lblTieuDeLocTG))
                                                .addContainerGap(15, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnKetQuaTimKiem)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lblThongBaoKetQua, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btn_LamMoi))))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addComponent(lblNhapThang)
                                .addGap(221, 221, 221)
                                .addComponent(lblNhapNam)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(lblNhapThongTin)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblDanhMucTimKiem)
                                        .addComponent(lblThongTinTraCuu))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(txt_NhapThongTin, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btn_TraCuu, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btn_Xoa, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(ComboBox_DanhMucTimKiem, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addGap(22, 22, 22)
                                .addComponent(lblTieuDeLocTG)
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblNhapNam)
                                        .addComponent(lblNhapThang))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(comboBox_Year1, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboBox_Month, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnFilter1, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnKetQuaTimKiem)
                                        .addComponent(lblThongBaoKetQua, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btn_LamMoi, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE)
                                .addGap(108, 108, 108))
        );
    }// </editor-fold>

    private void ComboBox_DanhMucTimKiemActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btn_TraCuuActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
        lamRongBangHoaDon();



        // 1. Lấy tiêu chí tìm kiếm từ ComboBox
        String loaiTimKiem = (String) ComboBox_DanhMucTimKiem.getSelectedItem();

        // 2. Lấy giá trị tìm kiếm từ JTextField
        String giaTriTimKiem = txt_NhapThongTin.getText().trim();

        // 3. Kiểm tra xem người dùng đã nhập giá trị chưa
        // Đồng thời kiểm tra xem có phải đang hiển thị placeholder không
        if (giaTriTimKiem.isEmpty() || giaTriTimKiem.startsWith("VD :")) { // Kiểm tra cả placeholder
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin cần tra cứu!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txt_NhapThongTin.requestFocus(); // Focus lại vào ô nhập liệu
            return; // Dừng lại
        }

        DefaultTableModel model = (DefaultTableModel) Table_DanhSachHoaDon.getModel();

        // --- Gọi DAO để tìm kiếm ---
        // Tạo đối tượng DAO

        //Phân ra tìm kiếm theo loại
        if (ComboBox_DanhMucTimKiem.getSelectedItem().equals("Mã hóa đơn")) {
            HoaDon hoaDonNew = HoaDonDAO.timHoaDonTheoMa(giaTriTimKiem);
            if (hoaDonNew == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn với mã: " + giaTriTimKiem, "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
                return; // Dừng lại nếu không tìm thấy
            }
            KhachHang khachhangNew = KhachHangDAO.timKhachHangTheoMaHoaDon(giaTriTimKiem);
            ChuyenTau chuyenTauNew = ChuyenTauDao.timKiemChuyenTauTheoMaHoaDon(giaTriTimKiem);
            int soLuongVe = ChiTietHoaDonDAO.demSoLuongVeTheoMaHoaDon(giaTriTimKiem);

//            hoaDonNew.getMaHD();
//            khachhangNew.getSoCCCD();
//            khachhangNew.getSdt();
//            khachhangNew.getSoCCCD();
//            chuyenTauNew.gaDi.getTenGa();
//            chuyenTauNew.gaDen.getTenGa();
//            soLuongVe;
//            chuyenTauNew.getNgayKhoiHanh();
//            chuyenTauNew.getNgayKhoiHanh();
//            hoaDonNew.getTongTien();
//            hoaDonNew.getLoaiHoaDon();

            //Chèn vào bảng
            String ngayHienThi = chuyenTauNew.ngayKhoiHanh.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            Object[] row = {
                    hoaDonNew.getMaHD(),
                    "<html>" + khachhangNew.getHoTen() + "<br>" + khachhangNew.getSdt() + "</html>",
                    chuyenTauNew.gaDi.getTenGa() + " - " + chuyenTauNew.gaDen.getTenGa(),
                    soLuongVe + "",
                    "<html>" + ngayHienThi + "<br>" + chuyenTauNew.gioKhoiHanh + "</html>",
                    String.format("%,.0fđ", hoaDonNew.getTongTien()).replace(",", "."),
                    hoaDonNew.getLoaiHoaDon(),
                    "Xem"
            };
            model.addRow(row);

            System.out.println(hoaDonNew.toString());
            System.out.println(khachhangNew.toString());
            System.out.println(chuyenTauNew.gaDi.tenGa + " - " + chuyenTauNew.gaDen.tenGa + " " + chuyenTauNew.ngayKhoiHanh.toString() + " " + chuyenTauNew.gioKhoiHanh.toString() );

            // Cập nhật label thông báo (ví dụ)
            lblThongBaoKetQua.setText("1 hóa đơn");
        }


        // TÌM HÓA ĐƠN THEO SỐ ĐIỆN THOẠI
        else if (ComboBox_DanhMucTimKiem.getSelectedItem().equals("Số điện thoại")){
            // Gọi các DAO để lấy danh sách tương ứng
            // Giả sử các phương thức DAO là static hoặc bạn đã có instance
            ArrayList<HoaDon> hoaDonKetQua = HoaDonDAO.timHoaDonTheoSDT(giaTriTimKiem);
            ArrayList<ChuyenTau> chuyenTauKetQua = ChuyenTauDao.timKiemChuyenTauTheoSoDienThoai(giaTriTimKiem);
            ArrayList<KhachHang> khachHangKetQua = KhachHangDAO.timKhachHangTheoSoDienThoai(giaTriTimKiem);

            // Kiểm tra xem có kết quả không
            if (hoaDonKetQua == null || hoaDonKetQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào với số điện thoại: " + giaTriTimKiem, "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
                model.setRowCount(0); // Xóa bảng
                lblThongBaoKetQua.setText("0 hóa đơn");
                return; // Dừng lại
            }

//            for (HoaDon hd : hoaDonKetQua) {
//                System.out.println("Mã HĐ: " + hd.getMaHD());
//                System.out.println("Mã KH: " + hd.getMaKhachHang());
//                System.out.println("Ngày lập: " +
//                        hd.getNgayLap().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
//                System.out.println("Tổng tiền: " + String.format("%,.0fđ", hd.getTongTien()).replace(",", "."));
//                System.out.println("Loại HĐ: " + hd.getLoaiHoaDon());
//                System.out.println("-------------------");
//            }
//
//            for (KhachHang kh : khachHangKetQua) {
//                System.out.println("Họ tên: " + kh.hoTen);
//                System.out.println("SĐT: " + kh.sdt);
//                System.out.println("CCCD: " + kh.soCCCD);
//                System.out.println("-------------------");
//            }
//
//            for (ChuyenTau ct : chuyenTauKetQua) {
//                System.out.println("Ga đi: " + ct.gaDi.getTenGa());
//                System.out.println("Ga đến: " + ct.gaDen.getTenGa());
//                System.out.println("Ngày khởi hành: " +
//                        ct.ngayKhoiHanh.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//                System.out.println("Giờ khởi hành: " + ct.gioKhoiHanh);
//                System.out.println("-------------------");
//            }

            // ---- BẮT ĐẦU CHÈN DỮ LIỆU VÀO BẢNG ----
            model.setRowCount(0); // Xóa dữ liệu cũ trước khi thêm mới

            // Lặp qua danh sách kết quả (vì bạn đảm bảo size bằng nhau)
            for (int i = 0; i < hoaDonKetQua.size(); i++) {
                HoaDon hd = hoaDonKetQua.get(i);
                ChuyenTau ct = chuyenTauKetQua.get(i);
                KhachHang kh = khachHangKetQua.get(i);

                // Lấy số lượng vé (cần DAO ChiTietHoaDon)
                // **** GIẢ ĐỊNH: Có ChiTietHoaDonDAO.demSoLuongVeTheoMaHoaDon(maHD) ****
                int soLuongVe = ChiTietHoaDonDAO.demSoLuongVeTheoMaHoaDon(hd.getMaHD());

                // Định dạng ngày, giờ, tiền tệ
                String ngayHienThi = ct.ngayKhoiHanh.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String tongTienFormatted = String.format("%,.0fđ", hd.getTongTien()).replace(",", ".");

                // Tạo dữ liệu cho hàng
                Object[] rowData = {
                        hd.getMaHD(),
                        "<html>" + kh.getHoTen() + "<br>" + kh.getSdt() + "</html>",
                        ct.gaDi.getTenGa() + " - " + ct.gaDen.getTenGa(),
                        String.valueOf(soLuongVe), // Chuyển int sang String
                        "<html>" + ngayHienThi + "<br>" + ct.gioKhoiHanh + "</html>",
                        tongTienFormatted,
                        hd.getLoaiHoaDon(),
                        "Xem"
                };

                // Thêm hàng vào model
                model.addRow(rowData);
            }
            // ---- KẾT THÚC CHÈN DỮ LIỆU ----

            // Cập nhật label thông báo
            lblThongBaoKetQua.setText(hoaDonKetQua.size() + " hóa đơn");

        }


        // TÌM HÓA ĐƠN THEO SỐ CCCD
        else if (ComboBox_DanhMucTimKiem.getSelectedItem().equals("Số CCCD")){
            // Gọi các DAO tìm theo CCCD
            ArrayList<HoaDon> hoaDonKetQua = HoaDonDAO.timHoaDonTheoCCCD(giaTriTimKiem);
            ArrayList<KhachHang> khachHangKetQua = KhachHangDAO.timKhachHangTheoCCCD(giaTriTimKiem);
            ArrayList<ChuyenTau> chuyenTauKetQua = ChuyenTauDao.timChuyenTauTheoCCCD(giaTriTimKiem); // Giả sử tên phương thức này đúng

            for (HoaDon hd : hoaDonKetQua) {
                System.out.println("Mã HĐ: " + hd.getMaHD());
                System.out.println("Mã KH: " + hd.getMaKhachHang());
                System.out.println("Ngày lập: " +
                        hd.getNgayLap().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
                System.out.println("Tổng tiền: " + String.format("%,.0fđ", hd.getTongTien()).replace(",", "."));
                System.out.println("Loại HĐ: " + hd.getLoaiHoaDon());
                System.out.println("-------------------");
            }

            for (KhachHang kh : khachHangKetQua) {
                System.out.println("Họ tên: " + kh.hoTen);
                System.out.println("SĐT: " + kh.sdt);
                System.out.println("CCCD: " + kh.soCCCD);
                System.out.println("-------------------");
            }

            for (ChuyenTau ct : chuyenTauKetQua) {
                System.out.println("Ga đi: " + ct.gaDi.getTenGa());
                System.out.println("Ga đến: " + ct.gaDen.getTenGa());
                System.out.println("Ngày khởi hành: " +
                        ct.ngayKhoiHanh.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                System.out.println("Giờ khởi hành: " + ct.gioKhoiHanh);
                System.out.println("-------------------");
            }

            // Kiểm tra xem có kết quả không
            if (hoaDonKetQua == null || hoaDonKetQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào với CCCD: " + giaTriTimKiem, "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
                model.setRowCount(0); // Xóa bảng
                lblThongBaoKetQua.setText("0 hóa đơn");
                return; // Dừng lại
            }

            // ---- BẮT ĐẦU CHÈN DỮ LIỆU VÀO BẢNG ----
            model.setRowCount(0); // Xóa dữ liệu cũ trước khi thêm mới

            // Lặp qua danh sách kết quả (vì bạn đảm bảo size bằng nhau)
            for (int i = 0; i < hoaDonKetQua.size(); i++) {
                HoaDon hd = hoaDonKetQua.get(i);
                ChuyenTau ct = chuyenTauKetQua.get(i);
                KhachHang kh = khachHangKetQua.get(i);

                // Lấy số lượng vé
                // **** GIẢ ĐỊNH: Có ChiTietHoaDonDAO.demSoLuongVeTheoMaHoaDon(maHD) ****
                int soLuongVe = ChiTietHoaDonDAO.demSoLuongVeTheoMaHoaDon(hd.getMaHD());

                // Định dạng ngày, giờ, tiền tệ
                String ngayHienThi = ct.ngayKhoiHanh.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String tongTienFormatted = String.format("%,.0fđ", hd.getTongTien()).replace(",", ".");

                // Tạo dữ liệu cho hàng
                Object[] rowData = {
                        hd.getMaHD(),
                        "<html>" + kh.getHoTen() + "<br>" + kh.getSdt() + "</html>", // Vẫn hiển thị SĐT
                        ct.gaDi.getTenGa() + " - " + ct.gaDen.getTenGa(),
                        String.valueOf(soLuongVe),
                        "<html>" + ngayHienThi + "<br>" + ct.gioKhoiHanh + "</html>",
                        tongTienFormatted,
                        hd.getLoaiHoaDon(),
                        "Xem"
                };

                // Thêm hàng vào model
                model.addRow(rowData);
            }
            // ---- KẾT THÚC CHÈN DỮ LIỆU ----

            // Cập nhật label thông báo
            lblThongBaoKetQua.setText(hoaDonKetQua.size() + " hóa đơn");
        }

//        // --- Xử lý kết quả (Hiện tại chỉ in ra console) ---
//        if (danhSachKetQua.isEmpty()) {
//            System.out.println("Không tìm thấy hóa đơn nào phù hợp.");
//        } else {
//            System.out.println("Tìm thấy " + danhSachKetQua.size() + " hóa đơn:");
//            for (HoaDon hd : danhSachKetQua) {
//                System.out.println(hd.toString()); // In thông tin chi tiết từng hóa đơn
//            }
//            // TODO: Cập nhật JTable (Table_DanhSachHoaDon) với dữ liệu từ danhSachKetQua
//            // Ví dụ: updateTableData(danhSachKetQua);
//            // lblThongBaoKetQua.setText(danhSachKetQua.size() + " hóa đơn");
//        }
    }

    private void comboBox_MonthActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void txt_NhapThongTinActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btn_XoaActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
        txt_NhapThongTin.setText("");
    }

    // ... (Imports remain the same, ensure DateTimeFormatter is imported) ...

    private void btnFilter1ActionPerformed(ActionEvent evt) {
        //TODO add your handling code here:

        // 1. Get main search criteria
        lamRongBangHoaDon();
        String loaiTimKiem = (String) ComboBox_DanhMucTimKiem.getSelectedItem();

        // --- EXIT IF SEARCHING BY MaHD ---
        if ("Mã hóa đơn".equals(loaiTimKiem)) {
            JOptionPane.showMessageDialog(this, // Tham chiếu đến JPanel hiện tại
                    "Không áp dụng khi tìm theo Mã hóa đơn.", // Nội dung thông báo
                    "Thông báo", // Tiêu đề cửa sổ
                    JOptionPane.INFORMATION_MESSAGE);
            // Optionally show a message to the user
            // JOptionPane.showMessageDialog(this, "Chức năng lọc không áp dụng khi tìm theo Mã hóa đơn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // --- END CHECK ---

        // 2. Get month/year selections
        int monthIndex = comboBox_Month.getSelectedIndex();
        int yearIndex = comboBox_Year1.getSelectedIndex();
        String selectedMonthStr = (String) comboBox_Month.getSelectedItem();
        String selectedYearStr = (String) comboBox_Year1.getSelectedItem();

        // 3. Get main search value
        String giaTriTimKiem = txt_NhapThongTin.getText().trim();

        // 4. --- VALIDATION ---
        if (monthIndex == 0 || yearIndex == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn cả Tháng và Năm để lọc!", "Thiếu thông tin lọc", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (giaTriTimKiem.isEmpty() || giaTriTimKiem.startsWith("VD :")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập " + loaiTimKiem + " cần lọc!", "Thiếu thông tin tra cứu", JOptionPane.WARNING_MESSAGE);
            txt_NhapThongTin.requestFocus();
            return;
        }

        // 5. Convert Month/Year to int
        int selectedMonthInt = 0;
        int selectedYearInt = 0;
        try {
            selectedMonthInt = Integer.parseInt(selectedMonthStr);
            selectedYearInt = Integer.parseInt(selectedYearStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá trị Tháng hoặc Năm không hợp lệ.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 6. Get TableModel and clear table
        DefaultTableModel model = (DefaultTableModel) Table_DanhSachHoaDon.getModel();
        model.setRowCount(0);

        // 7. --- CALL FILTERING DAO METHODS ---
        ArrayList<HoaDon> hoaDonKetQua = null;
        ArrayList<KhachHang> khachHangKetQua = null;
        ArrayList<ChuyenTau> chuyenTauKetQua = null;

        System.out.println("Bắt đầu lọc theo: " + loaiTimKiem + " = " + giaTriTimKiem + ", Tháng=" + selectedMonthInt + ", Năm=" + selectedYearInt);

        try {
            // Only call filtering DAOs for SDT or CCCD
            if ("Số điện thoại".equals(loaiTimKiem)) {
                hoaDonKetQua = HoaDonDAO.timHoaDonTheoSDTLocThangNam(giaTriTimKiem, selectedMonthInt, selectedYearInt);
                khachHangKetQua = KhachHangDAO.timKhachHangTheoSDTLocThangNam(giaTriTimKiem, selectedMonthInt, selectedYearInt);
                chuyenTauKetQua = ChuyenTauDao.timChuyenTauTheoSDTLocThangNam(giaTriTimKiem, selectedMonthInt, selectedYearInt);
            } else if ("Số CCCD".equals(loaiTimKiem)) {
                hoaDonKetQua = HoaDonDAO.timHoaDonTheoCCCDLocThangNam(giaTriTimKiem, selectedMonthInt, selectedYearInt);
                khachHangKetQua = KhachHangDAO.timKhachHangTheoCCCDLocThangNam(giaTriTimKiem, selectedMonthInt, selectedYearInt);
                chuyenTauKetQua = ChuyenTauDao.timChuyenTauTheoCCCDLocThangNam(giaTriTimKiem, selectedMonthInt, selectedYearInt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi truy vấn dữ liệu:\n" + e.getMessage(), "Lỗi Cơ Sở Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            lblThongBaoKetQua.setText("Lỗi");
            return;
        }

        for (HoaDon hd : hoaDonKetQua) {
            System.out.println("Mã HĐ: " + hd.getMaHD());
            System.out.println("Mã KH: " + hd.getMaKhachHang());
            System.out.println("Ngày lập: " +
                    hd.getNgayLap().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            System.out.println("Tổng tiền: " + String.format("%,.0fđ", hd.getTongTien()).replace(",", "."));
            System.out.println("Loại HĐ: " + hd.getLoaiHoaDon());
            System.out.println("-------------------");
        }

        for (KhachHang kh : khachHangKetQua) {
            System.out.println("Họ tên: " + kh.hoTen);
            System.out.println("SĐT: " + kh.sdt);
            System.out.println("CCCD: " + kh.soCCCD);
            System.out.println("-------------------");
        }

        for (ChuyenTau ct : chuyenTauKetQua) {
            System.out.println("Ga đi: " + ct.gaDi.getTenGa());
            System.out.println("Ga đến: " + ct.gaDen.getTenGa());
            System.out.println("Ngày khởi hành: " +
                    ct.ngayKhoiHanh.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            System.out.println("Giờ khởi hành: " + ct.gioKhoiHanh);
            System.out.println("-------------------");
        }


        // 8. --- CHECK IF HoaDon RESULTS EXIST (PRIMARY CHECK) ---
        if (hoaDonKetQua == null || hoaDonKetQua.isEmpty()) {
            lblThongBaoKetQua.setText("0 hóa đơn");
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào phù hợp với điều kiện lọc.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return; // Stop if no invoices found
        }

        for (HoaDon hd : hoaDonKetQua) {
            System.out.println("Mã HĐ: " + hd.getMaHD());
            System.out.println("Mã KH: " + hd.getMaKhachHang());
            System.out.println("Ngày lập: " +
                    hd.getNgayLap().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            System.out.println("Tổng tiền: " + String.format("%,.0fđ", hd.getTongTien()).replace(",", "."));
            System.out.println("Loại HĐ: " + hd.getLoaiHoaDon());
            System.out.println("-------------------");
        }

        for (KhachHang kh : khachHangKetQua) {
            System.out.println("Họ tên: " + kh.hoTen);
            System.out.println("SĐT: " + kh.sdt);
            System.out.println("CCCD: " + kh.soCCCD);
            System.out.println("-------------------");
        }

        for (ChuyenTau ct : chuyenTauKetQua) {
            System.out.println("Ga đi: " + ct.gaDi.getTenGa());
            System.out.println("Ga đến: " + ct.gaDen.getTenGa());
            System.out.println("Ngày khởi hành: " +
                    ct.ngayKhoiHanh.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            System.out.println("Giờ khởi hành: " + ct.gioKhoiHanh);
            System.out.println("-------------------");
        }

        // --- ASSUME OTHER LISTS (khachHangKetQua, chuyenTauKetQua) ARE CONSISTENT ---

        // 9. --- POPULATE JTABLE ---
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (int i = 0; i < hoaDonKetQua.size(); i++) { // Loop based on hoaDonKetQua size
            HoaDon hd = hoaDonKetQua.get(i);
            // Directly get corresponding KhachHang and ChuyenTau assuming lists are synced
            KhachHang kh = khachHangKetQua.get(i);
            ChuyenTau ct = chuyenTauKetQua.get(i);

            int soLuongVe = 0;
            try {
                soLuongVe = ChiTietHoaDonDAO.demSoLuongVeTheoMaHoaDon(hd.getMaHD()); // Assuming this DAO method exists
            } catch (Exception e){
                System.err.println("Lỗi khi lấy số lượng vé cho HĐ " + hd.getMaHD() + ": " + e.getMessage());
            }

            // Formatting (assuming ct, kh, gaDi, gaDen, etc. are not null based on your confidence)
            String ngayHienThi = ct.getNgayKhoiHanh().format(dateFormatter);
            String gioHienThi = ct.getGioKhoiHanh().toString();
            String tuyen = ct.getGaDi().getTenGa() + " - " + ct.getGaDen().getTenGa();
            String tenKH = kh.getHoTen();
            String sdtKH = kh.getSdt();
            String tongTienFormatted = String.format("%,.0fđ", hd.getTongTien()).replace(",", ".");
            String loaiHD = hd.getLoaiHoaDon(); // Directly get value

            Object[] rowData = {
                    hd.getMaHD(),
                    "<html>" + tenKH + "<br>" + sdtKH + "</html>",
                    tuyen,
                    String.valueOf(soLuongVe),
                    "<html>" + ngayHienThi + "<br>" + gioHienThi + "</html>",
                    tongTienFormatted,
                    loaiHD, // Use the direct value
                    "Xem"
            };
            model.addRow(rowData);
        }
        lblThongBaoKetQua.setText(hoaDonKetQua.size() + " hóa đơn");

    } // End of btnFilter1ActionPerformed


    private void btn_LamMoiActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
        lamRongBangHoaDon();
        comboBox_Month.setSelectedIndex(0);
        comboBox_Year1.setSelectedIndex(0);
    }

    // Variables declaration - do not modify
    private JComboBox<String> ComboBox_DanhMucTimKiem, comboBox_Month, comboBox_Year1;
    private JTable Table_DanhSachHoaDon;
    private JButton btnFilter1, btn_LamMoi, btn_TraCuu, btn_Xoa;
    private JLabel btnKetQuaTimKiem, lblDanhMucTimKiem, lblNhapNam, lblNhapThang,
            lblNhapThongTin, lblThongBaoKetQua, lblThongTinTraCuu, lblTieuDeLocTG;
    private JScrollPane jScrollPane1;
    private JTextField txt_NhapThongTin;
    private final Map<String, String> placeholders = new HashMap<>();
    private String currentPlaceholder = ""; // Placeholder hiện tại dựa trên ComboBox
    private Color placeholderColor = Color.GRAY; // Màu cho placeholder text
    private Color defaultTextColor; // Màu chữ mặc định khi nhập liệu
// End of variables declaration


    // Phương thức để làm rỗng bảng Table_DanhSachHoaDon
    private void lamRongBangHoaDon() {
        // 1. Lấy TableModel của bảng
        Object modelObj = Table_DanhSachHoaDon.getModel();

        // 2. Kiểm tra xem nó có phải là DefaultTableModel không (thường là vậy)
        if (modelObj instanceof DefaultTableModel) {
            DefaultTableModel model = (DefaultTableModel) modelObj;
            model.setRowCount(0);
        } else {
            // Xử lý nếu model không phải loại DefaultTableModel (ít gặp)
            System.err.println("Không thể làm rỗng bảng: Model không phải DefaultTableModel.");
        }
    }
    /**
     * Cập nhật biến currentPlaceholder và gọi setPlaceholder nếu ô trống.
     */
    private void updatePlaceholderText() {
        String selectedItem = (String) ComboBox_DanhMucTimKiem.getSelectedItem();
        currentPlaceholder = placeholders.getOrDefault(selectedItem, ""); // Lấy placeholder tương ứng

        // Chỉ cập nhật text field nếu nó đang trống hoặc đang hiển thị placeholder cũ
        if (txt_NhapThongTin.getText().trim().isEmpty() || placeholders.containsValue(txt_NhapThongTin.getText())) {
            setPlaceholder();
        }
    }

    /**
     * Hiển thị placeholder hiện tại (currentPlaceholder) trong JTextField.
     */
    private void setPlaceholder() {
        txt_NhapThongTin.setText(currentPlaceholder);
        txt_NhapThongTin.setForeground(placeholderColor); // Đặt màu xám cho placeholder
    }


    /**
     * Hiển thị popup chi tiết hóa đơn với hiệu ứng nền mờ (overlay).
     * @param maHoaDon Mã hóa đơn để hiển thị
     */
    /**
     * Hiển thị popup chi tiết hóa đơn với hiệu ứng nền mờ (overlay).
     * @param maHoaDon Mã hóa đơn để hiển thị
     */
    private void showChiTietPopup(String maHoaDon) {
        // 1. Lấy cửa sổ JFrame cha (cửa sổ chính của ứng dụng)
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) {
            return; // Không tìm thấy frame cha
        }

        // 2. Tạo panel chi tiết (cái popup)
        // (Trong tương lai, bạn sẽ muốn truyền maHoaDon vào đây để nó tải đúng data)
        PopUpChiTietHoaDon chiTietPanel = new PopUpChiTietHoaDon(maHoaDon);

        // (Tùy chọn: bạn có thể tạo một hàm public trong PopUpChiTietHoaDon
        // ví dụ: chiTietPanel.loadDataByMaHoaDon(maHoaDon);
        // và gọi nó ở đây)

        // 3. Tạo lớp nền mờ (Overlay)
        JPanel overlayPanel = new JPanel(new java.awt.GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Vẽ nền đen mờ 50% (alpha = 150)
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false); // Quan trọng để cho phép vẽ trong suốt
        overlayPanel.add(chiTietPanel); // Thêm panel popup vào giữa

        // 4. Lấy Glass Pane của JFrame và đặt overlay vào
        topFrame.setGlassPane(overlayPanel);

        // 5. Hiển thị nó
        overlayPanel.setVisible(true);
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            ManHinhTraCuuHoaDon panel = new ManHinhTraCuuHoaDon();
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}


