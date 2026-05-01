package JDBC_Project.gui.Popup;


import dao.ChiTietHoaDonDAO;
import entity.*;

import java.time.format.DateTimeFormatter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image; // Cho resizeIcon
import java.awt.RenderingHints;
import java.awt.event.ActionEvent; // Cho ActionListener
import java.awt.event.ActionListener; // Cho ActionListener
import java.util.List;

import javax.swing.*;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;


/**
 *
 * @author laptoptt
 */
// Đã sửa javax.swing.JPanel -> JPanel
public class PopUpChiTietHoaDon extends JPanel {
    /**
     * Creates new form PopUpChiTietHoaDon
     */
    // Variables declaration - do not modify
    private JTable BangChiTietHanhKhach;
    private JButton btnDongChiTietHoaDon, btnInHoaDon, btnSoLuongVe;
    private JLabel jLabel10, jLabel12, jLabel15, jLabel18, jLabel19, jLabel20, jLabel21, jLabel22, jLabel25, jLabel26, jLabel27, jLabel29, jLabel3, jLabel30, jLabel31, jLabel32, jLabel33, jLabel34, jLabel35, jLabel4, jLabel5, lblEmail, lblGaDen, lblGaDi, lblGioDuKien, lblGioKhoiHanh, lblKMchoHoaDon, lblMaChuyen, lblNgayDuKien, lblNgayKhoiHanh, lblNgayLapHD, lblNguoiThanhToan, lblNoiDungKhuyenMai, lblPhuongThucThanhToan, lblSoDienThoai, lblSoHieuTau, lblSoTienPhaiThanhToan, lblTenHanhKhach, lblTenNguoiLap, lblTieuDeHanhKhach, lblTieuDeMaHoaDon, lblTongTien;
    private JPanel jPanel1, jPanel2, jPanel3, jPanel4;
    private JScrollPane jScrollPane1;
    // End of variables declaration

    private String imagePath_Male;
    private String imagePath_Female;
    private ImageIcon resizedMaleIcon;
    private ImageIcon resizedFemaleIcon;

    public PopUpChiTietHoaDon(String maHoaDon) {
        initComponents();
        lblTieuDeMaHoaDon.setText(maHoaDon);

        truyenDuLieuChiTietHoaDon(maHoaDon);

        // --- BẮT ĐẦU CODE CUSTOM CỦA BẠN ---

        // 1. Đường dẫn ảnh (giữ nguyên)
        String imagePath_Train = "/images/train.png";
        String imagePath_Passenger = "/images/hanhkhach.png";
        String imagePath_Telephone = "/images/telephone.png";
        String imagePath_Passengers = "/images/nhieuhanhkhach.png";
        String imagePath_Transaction = "/images/payment-success.png";
        String imagePath_PaymentMethod = "/images/payment-method.png";
        String imagePath_Calendar = "/images/schedule.png";
        imagePath_Male = "/images/male-gender.png";
        imagePath_Female = "/images/female-gender.png";
        String imagePath_Gender = "/images/gender.png";

        // 3. Gọi hàm resize
        ImageIcon resizedTrainIcon = resizeIcon(imagePath_Train, 28, 28);
        ImageIcon resizedPassengerIcon = resizeIcon(imagePath_Passenger,20, 20 );
        ImageIcon resizedTelephoneIcon = resizeIcon(imagePath_Telephone,20, 20 );
        ImageIcon resizedPassengersIcon = resizeIcon(imagePath_Passengers,25, 25 );
        ImageIcon resizedTransactionIcon = resizeIcon(imagePath_Transaction,30, 30 );
        ImageIcon resizedPaymentMethodIcon = resizeIcon(imagePath_PaymentMethod,20, 20 );
        ImageIcon resizedCalendarIcon = resizeIcon(imagePath_Calendar, 20, 20);
        ImageIcon resizedGenderIcon = resizeIcon(imagePath_Gender,20, 20 );
        resizedMaleIcon = resizeIcon(imagePath_Male, 20, 20);
        resizedFemaleIcon = resizeIcon(imagePath_Female, 20, 20);


        // 4. Đặt lại icon
        lblTieuDeMaHoaDon.setIcon(resizedTrainIcon);
        jLabel25.setIcon(resizedPassengerIcon);
        lblSoDienThoai.setIcon(resizedTelephoneIcon);
        jLabel18.setIcon(resizedPassengersIcon);
        lblSoTienPhaiThanhToan.setIcon(resizedTransactionIcon); // Đã sửa từ lblTongTien
        jLabel27.setIcon(resizedPaymentMethodIcon);
        lblNgayLapHD.setIcon(resizedCalendarIcon); // Đã sửa từ lblNgayThanhToan
        lblEmail.setIcon(resizedGenderIcon);

        if (lblEmail.getText().equals("Nam"))
            lblEmail.setIcon(resizedMaleIcon);
        else lblEmail.setIcon(resizedFemaleIcon);

        // Custom nút
        btnInHoaDon.setOpaque(true);
        btnInHoaDon.setBorderPainted(false);
        btnDongChiTietHoaDon.setOpaque(true);
        btnDongChiTietHoaDon.setBorderPainted(false);

        // --- BẮT ĐẦU CODE CUSTOM BẢNG ---
        JTableHeader header = BangChiTietHanhKhach.getTableHeader();
        header.setBackground(new Color(100, 100, 100)); // Đã sửa java.awt.Color -> Color
        header.setForeground(Color.BLACK); // Đã sửa
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 15)); // Đã sửa java.awt.Font -> Font
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER); // Đã sửa javax.swing.SwingConstants -> SwingConstants

        DefaultTableCellRenderer centerDataRenderer = new DefaultTableCellRenderer();
        centerDataRenderer.setHorizontalAlignment(SwingConstants.CENTER); // Đã sửa
        TableColumnModel columnModel = BangChiTietHanhKhach.getColumnModel();

        columnModel.getColumn(0).setCellRenderer(centerDataRenderer); // STT
        columnModel.getColumn(2).setCellRenderer(centerDataRenderer); // Giới tính
        columnModel.getColumn(3).setCellRenderer(centerDataRenderer); // Định danh/CCCD (Index 2)
        columnModel.getColumn(4).setCellRenderer(centerDataRenderer); // Giá vé (Index 4)
        columnModel.getColumn(5).setCellRenderer(centerDataRenderer); // Số lượng
        columnModel.getColumn(6).setCellRenderer(centerDataRenderer); // Số lượng

        columnModel.getColumn(1).setPreferredWidth(155); // Họ và tên
        columnModel.getColumn(3).setPreferredWidth(140); // Mã định danh/CCCD
        columnModel.getColumn(0).setPreferredWidth(40);  // STT
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(4).setPreferredWidth(110); // Loại vé
        columnModel.getColumn(5).setPreferredWidth(60); // Số lượng
        columnModel.getColumn(6).setPreferredWidth(110); // Đơn giá
        // columnModel.getColumn(6).setPreferredWidth(70); // Thành tiền (đã có ở trên, dòng này dư)

        BangChiTietHanhKhach.setShowGrid(false);
        BangChiTietHanhKhach.setIntercellSpacing(new Dimension(8, 0)); // Đã sửa java.awt.Dimension -> Dimension
        // --- KẾT THÚC CODE CUSTOM BẢNG ---
    }

    // Đã sửa javax.swing.ImageIcon -> ImageIcon, java.awt.Image -> Image
    private ImageIcon resizeIcon(String path, int width, int height) {
        java.net.URL imgURL = getClass().getResource(path);

        if (imgURL == null) {
            System.err.println("Không tìm thấy ảnh tại đường dẫn: " + path);
            return null;
        }
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        // Đã sửa javax.swing.* -> Simple names
        jPanel1 = new JPanel();
        lblTieuDeMaHoaDon = new JLabel();
        jPanel2 = new JPanel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        lblMaChuyen = new JLabel();
        lblNgayKhoiHanh = new JLabel();
        lblGaDi = new JLabel();
        lblGioKhoiHanh = new JLabel();
        jLabel10 = new JLabel();
        lblGaDen = new JLabel();
        jLabel12 = new JLabel();
        lblNgayDuKien = new JLabel();
        lblGioDuKien = new JLabel();
        jLabel15 = new JLabel();
        lblSoHieuTau = new JLabel();
        jPanel3 = new JPanel();
        jLabel18 = new JLabel() {
            { setOpaque(false); }
            @Override
            protected void paintComponent(Graphics g) { // Đã sửa java.awt.* -> *
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        jLabel19 = new JLabel();
        jLabel20 = new JLabel();
        jLabel21 = new JLabel();
        lblTenHanhKhach = new JLabel();
        lblEmail = new JLabel();
        lblSoDienThoai = new JLabel();
        jLabel25 = new JLabel() {
            { setOpaque(false); }
            @Override
            protected void paintComponent(Graphics g) { // Đã sửa
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        jScrollPane1 = new JScrollPane();
        BangChiTietHanhKhach = new JTable();
        btnSoLuongVe = new JButton();
        lblTieuDeHanhKhach = new JLabel();
        jPanel4 = new JPanel();
        lblNgayLapHD = new JLabel();
        jLabel29 = new JLabel();
        jLabel31 = new JLabel();
        lblSoTienPhaiThanhToan = new JLabel();
        jLabel33 = new JLabel();
        lblPhuongThucThanhToan = new JLabel();
        jLabel30 = new JLabel();
        lblTenNguoiLap = new JLabel();
        jLabel27 = new JLabel();
        jLabel32 = new JLabel();
        lblKMchoHoaDon = new JLabel();
        jLabel34 = new JLabel();
        lblTongTien = new JLabel();
        jLabel22 = new JLabel();
        lblNguoiThanhToan = new JLabel();
        jLabel35 = new JLabel();
        lblNoiDungKhuyenMai = new JLabel();
        jLabel26 = new JLabel();
        btnDongChiTietHoaDon = new JButton();
        btnInHoaDon = new JButton();

        setBackground(new Color(218, 218, 218)); // Đã sửa
        setForeground(new Color(218, 218, 218)); // Đã sửa

        jPanel1.setBackground(new Color(255, 255, 255)); // Đã sửa

        lblTieuDeMaHoaDon.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 24)); // Đã sửa java.awt.Font -> Font
        lblTieuDeMaHoaDon.setHorizontalAlignment(SwingConstants.CENTER); // Đã sửa javax.swing.SwingConstants -> SwingConstants
        lblTieuDeMaHoaDon.setIcon(new ImageIcon("/images/train.png")); // Đã sửa javax.swing.ImageIcon -> ImageIcon

        jPanel2.setBackground(new Color(255, 255, 255)); // Đã sửa

        jLabel3.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel3.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel3.setText("Mã chuyến :");

        jLabel4.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel4.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel4.setText("Ga đi :");

        jLabel5.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel5.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel5.setText("Ngày đi :");

        lblMaChuyen.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        lblMaChuyen.setText("C78324");

        lblNgayKhoiHanh.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        lblNgayKhoiHanh.setText("21/08/2024");

        lblGaDi.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        lblGaDi.setText("Sài Gòn");

        lblGioKhoiHanh.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        lblGioKhoiHanh.setText("08:30");

        jLabel10.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel10.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel10.setText("Ga đến :");

        lblGaDen.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        lblGaDen.setText("Hà Nội");

        jLabel12.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel12.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel12.setText("Dự kiến :");

        lblNgayDuKien.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        lblNgayDuKien.setText("23/09/2024");

        lblGioDuKien.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        lblGioDuKien.setText("06 : 45");

        jLabel15.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel15.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel15.setText("Số hiệu tàu :");

        lblSoHieuTau.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        lblSoHieuTau.setText("SE1");

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2); // Đã sửa javax.swing.GroupLayout -> GroupLayout
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup( // Giữ nguyên cấu trúc layout
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(70, 70, 70)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lblMaChuyen)
                                        .addComponent(lblGaDi)
                                        .addComponent(lblNgayKhoiHanh)
                                        .addComponent(lblGioKhoiHanh))
                                .addGap(188, 188, 188)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel12)
                                                .addGap(94, 94, 94)
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblGioDuKien)
                                                        .addComponent(lblNgayDuKien)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jLabel10, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(84, 84, 84))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jLabel15)
                                                                .addGap(68, 68, 68)))
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblSoHieuTau)
                                                        .addComponent(lblGaDen))
                                                .addGap(28, 28, 28)))
                                .addContainerGap(80, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup( // Giữ nguyên cấu trúc layout
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(14, 14, 14)
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel3)
                                                        .addComponent(lblMaChuyen)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel15)
                                                        .addComponent(lblSoHieuTau))))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(lblGaDi)
                                                        .addComponent(jLabel4))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblNgayKhoiHanh)
                                                        .addComponent(jLabel5))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblGioKhoiHanh))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel10)
                                                        .addComponent(lblGaDen))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(lblNgayDuKien)
                                                        .addComponent(jLabel12))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblGioDuKien)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new Color(255, 255, 255)); // Đã sửa

        jLabel18.setBackground(new Color(100, 100, 100)); // Đã sửa
        jLabel18.setFont(new Font("Oriya Sangam MN", Font.PLAIN, 16)); // Đã sửa
        jLabel18.setForeground(new Color(255, 255, 255)); // Đã sửa
        jLabel18.setHorizontalAlignment(SwingConstants.CENTER); // Đã sửa
        jLabel18.setIcon(new ImageIcon("/images/nhieuhanhkhach.png")); // Đã sửa
        jLabel18.setText("Hành khách ");
        jLabel18.setSize(new Dimension(17, 60)); // Đã sửa

        jLabel19.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel19.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel19.setText("Họ tên :");

        jLabel20.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel20.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel20.setText("Giới tính :");

        jLabel21.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel21.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel21.setText("Số điện thoại :");

        lblTenHanhKhach.setFont(new Font("Kailasa", Font.PLAIN, 17)); // Đã sửa
        lblTenHanhKhach.setText("Nguyễn Văn Nam");

        lblEmail.setFont(new Font("Kailasa", Font.PLAIN, 17)); // Đã sửa
        lblEmail.setIcon(new ImageIcon("/images/gender.png")); // Đã sửa
        lblEmail.setText("Nam");

        lblSoDienThoai.setFont(new Font("Kailasa", Font.PLAIN, 17)); // Đã sửa
        lblSoDienThoai.setIcon(new ImageIcon("/images/telephone.png")); // Đã sửa
        lblSoDienThoai.setText("0123456789");

        jLabel25.setBackground(new Color(100, 100, 100)); // Đã sửa
        jLabel25.setFont(new Font("Oriya Sangam MN", Font.PLAIN, 16)); // Đã sửa
        jLabel25.setForeground(new Color(255, 255, 255)); // Đã sửa
        jLabel25.setHorizontalAlignment(SwingConstants.CENTER); // Đã sửa
        jLabel25.setIcon(new ImageIcon("/images/hanhkhach.png")); // Đã sửa
        jLabel25.setText(" Người đặt vé  ");

        BangChiTietHanhKhach.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1)); // Đã sửa javax.swing.BorderFactory -> BorderFactory
        BangChiTietHanhKhach.setFont(new Font("Helvetica Neue", Font.PLAIN, 15)); // Đã sửa
        BangChiTietHanhKhach.setModel(new DefaultTableModel( // Đã sửa javax.swing.table.DefaultTableModel -> DefaultTableModel
                new Object [][] {
                        { 1, "Nguyễn Văn Nam", "Nam" ,"044180000960", "Người lớn", "1", "900.000đ"}, // Integer tự động boxing/unboxing
                        { 2, "Nguyễn Trúc Thùy Tiên", "Nữ" , "077202000870", "Sinh Viên", "1", "810.000đ"},
                        { 3, "Trần Đức Nam", "Nam" , "044210000102", "Trẻ em", "1", "740.000đ"},
                        {null, null, null, null, null, null, null}
                },
                new String [] {
                        "STT", "Họ và tên", "Giới Tính", "Định danh/CCCD", "Loại vé", "Số lượng", "Đơn giá"
                }
        ) {
            // Đã sửa java.lang.* -> *
            Class<?>[] types = new Class [] {
                    Integer.class, String.class, String.class, String.class, Object.class, Object.class, String.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) { // Đã sửa Class -> Class<?>
                return types [columnIndex];
            }
        });
        BangChiTietHanhKhach.setRowHeight(30);
        jScrollPane1.setViewportView(BangChiTietHanhKhach);

        btnSoLuongVe.setFont(new Font("Helvetica Neue", Font.BOLD, 13)); // Đã sửa
        btnSoLuongVe.setForeground(new Color(0, 0, 0)); // Đã sửa
        btnSoLuongVe.setText("3 vé");
        btnSoLuongVe.addActionListener(new ActionListener() { // Đã sửa
            public void actionPerformed(ActionEvent evt) { // Đã sửa
                btnSoLuongVeActionPerformed(evt);
            }
        });

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3); // Đã sửa
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup( // Giữ nguyên cấu trúc layout
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel25)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel18)
                                                .addGap(39, 39, 39)
                                                .addComponent(btnSoLuongVe))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGap(41, 41, 41)
                                                                .addComponent(jLabel19, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(134, 134, 134)
                                                                .addComponent(jLabel21))
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGap(56, 56, 56)
                                                                .addComponent(lblTenHanhKhach)
                                                                .addGap(78, 78, 78)
                                                                .addComponent(lblSoDienThoai, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGap(67, 67, 67)
                                                                .addComponent(lblEmail, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGap(43, 43, 43)
                                                                .addComponent(jLabel20, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)))))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 823, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup( // Giữ nguyên cấu trúc layout
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel25, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel19, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel20)
                                        .addComponent(jLabel21, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTenHanhKhach)
                                        .addComponent(lblSoDienThoai, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblEmail, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel18, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnSoLuongVe))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
        );

        lblTieuDeHanhKhach.setFont(new Font("Noto Sans Tagalog", Font.PLAIN, 16)); // Đã sửa
        lblTieuDeHanhKhach.setText("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        jPanel4.setBackground(new Color(255, 255, 255)); // Đã sửa

        lblNgayLapHD.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        lblNgayLapHD.setIcon(new ImageIcon("/images/schedule.png")); // Đã sửa
        lblNgayLapHD.setText("19/08/2024");

        jLabel29.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        jLabel29.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel29.setText("Ngày lập :");

        jLabel31.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        jLabel31.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel31.setText("Số tiền phải thanh toán :");

        lblSoTienPhaiThanhToan.setFont(new Font("Helvetica Neue", Font.PLAIN, 20)); // Đã sửa
        lblSoTienPhaiThanhToan.setIcon(new ImageIcon("/images/payment-success.png")); // Đã sửa
        lblSoTienPhaiThanhToan.setText("2.240.000 ₫");

        jLabel33.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        jLabel33.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel33.setText("Khuyến mãi :");

        lblPhuongThucThanhToan.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        lblPhuongThucThanhToan.setText("Tiền mặt");

        jLabel30.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        jLabel30.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel30.setText("Người lập hóa đơn:");

        lblTenNguoiLap.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        lblTenNguoiLap.setText("Phạm Lan Thy");

        jLabel27.setFont(new Font("Helvetica Neue", Font.PLAIN, 19)); // Đã sửa
        jLabel27.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel27.setIcon(new ImageIcon("/images/payment-method.png")); // Đã sửa
        jLabel27.setText(":");

        jLabel32.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        jLabel32.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel32.setText("Tổng cộng :");

        lblKMchoHoaDon.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        lblKMchoHoaDon.setText("5%");

        jLabel34.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        jLabel34.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel34.setText("Nội dung :");

        lblTongTien.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        lblTongTien.setText("2.240.000 đ");

        jLabel22.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        jLabel22.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel22.setText("Người thanh toán :");

        lblNguoiThanhToan.setFont(new Font("Kailasa", Font.PLAIN, 16)); // Đã sửa
        lblNguoiThanhToan.setText("Nguyễn Văn Nam");

        jLabel35.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        jLabel35.setForeground(new Color(158, 158, 158)); // Đã sửa
        jLabel35.setText("Phương thức");

        lblNoiDungKhuyenMai.setFont(new Font("Helvetica Neue", Font.PLAIN, 16)); // Đã sửa
        lblNoiDungKhuyenMai.setText("Giảm trực tiếp cho hóa đơn");

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4); // Đã sửa
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup( // Giữ nguyên cấu trúc layout
                jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addComponent(jLabel30)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(lblTenNguoiLap))
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                                        .addComponent(jLabel33, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(jLabel32, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                                .addComponent(jLabel35, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jLabel27, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(jLabel22, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
                                                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                                .addGap(11, 11, 11)
                                                                                .addComponent(lblTongTien))
                                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(lblPhuongThucThanhToan)
                                                                                        .addComponent(lblKMchoHoaDon)
                                                                                        .addComponent(lblNguoiThanhToan))))))
                                                .addGap(59, 59, 59)
                                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addComponent(jLabel34, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lblNoiDungKhuyenMai))
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addComponent(jLabel29, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lblNgayLapHD, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE)))
                                                .addGap(110, 110, Short.MAX_VALUE))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblSoTienPhaiThanhToan, GroupLayout.PREFERRED_SIZE, 236, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel31, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup( // Giữ nguyên cấu trúc layout
                jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel30, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblTenNguoiLap, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel29, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblNgayLapHD, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel32, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblTongTien, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel33, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblKMchoHoaDon, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel34, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblNoiDungKhuyenMai, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel27, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel35, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblPhuongThucThanhToan))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel22, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblNguoiThanhToan))
                                .addGap(29, 29, 29)
                                .addComponent(jLabel31)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSoTienPhaiThanhToan, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(40, Short.MAX_VALUE))
        );

        jLabel26.setFont(new Font("Noto Sans Tagalog", Font.PLAIN, 16)); // Đã sửa
        jLabel26.setText("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        btnDongChiTietHoaDon.setBackground(new Color(255, 0, 0)); // Đã sửa
        btnDongChiTietHoaDon.setFont(new Font("Helvetica Neue", Font.PLAIN, 24)); // Đã sửa
        btnDongChiTietHoaDon.setForeground(new Color(255, 255, 255)); // Đã sửa
        btnDongChiTietHoaDon.setText("X");
        btnDongChiTietHoaDon.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 51))); // Đã sửa
        btnDongChiTietHoaDon.addActionListener(new ActionListener() { // Đã sửa
            public void actionPerformed(ActionEvent evt) { // Đã sửa
                btnDongChiTietHoaDonActionPerformed(evt);
            }
        });

        btnInHoaDon.setBackground(new Color(0, 222, 40)); // Đã sửa
        btnInHoaDon.setFont(new Font("Helvetica Neue", Font.BOLD, 16)); // Đã sửa
        btnInHoaDon.setForeground(new Color(255, 255, 255)); // Đã sửa
        btnInHoaDon.setText("In hóa đơn");
        btnInHoaDon.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1)); // Đã sửa

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1); // Đã sửa
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup( // Giữ nguyên cấu trúc layout
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(lblTieuDeMaHoaDon, GroupLayout.PREFERRED_SIZE, 814, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(btnDongChiTietHoaDon))
                                                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(lblTieuDeHanhKhach, GroupLayout.DEFAULT_SIZE, 1073, Short.MAX_VALUE)
                                                                .addComponent(jLabel26, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(217, 217, 217)
                                                .addComponent(btnInHoaDon, GroupLayout.PREFERRED_SIZE, 492, GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup( // Giữ nguyên cấu trúc layout
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(lblTieuDeMaHoaDon, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btnDongChiTietHoaDon))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTieuDeHanhKhach, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnInHoaDon, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(17, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(this); // Đã sửa
        this.setLayout(layout);
        layout.setHorizontalGroup( // Giữ nguyên cấu trúc layout
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 886, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        layout.setVerticalGroup( // Giữ nguyên cấu trúc layout
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>

    // Đã sửa java.awt.event.ActionEvent -> ActionEvent
    private void btnDongChiTietHoaDonActionPerformed(ActionEvent evt) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this); // Đã sửa javax.swing.* -> *
        if (topFrame != null) {
            Component glassPane = topFrame.getGlassPane(); // Đã sửa java.awt.Component -> Component
            glassPane.setVisible(false);
        }
    }

    // Đã sửa
    private void btnSoLuongVeActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }



    public void truyenDuLieuChiTietHoaDon(String maHD) {
        // 1. Chi tiết hóa đơn
        List<ChiTietHoaDon> chiTietList = ChiTietHoaDonDAO.timChiTietHoaDonTheoMaHD(maHD);

        // 2. Chuyến tàu (chỉ 1)
        ChuyenTau chuyenTau = ChiTietHoaDonDAO.chuyenTauTheoCTHD(maHD);

        // 3. Khuyến mãi
        KhuyenMai khuyenMai = ChiTietHoaDonDAO.layThongTinKhuyenMaiTheoMaHD(maHD);

        // 4. Khách hàng đặt hóa đơn (1 người)
        KhachHang khachHangDat = ChiTietHoaDonDAO.layKhachHangTheoHoaDon(maHD);

        // 5. Danh sách khách hàng đi vé
        List<KhachHang> khachHangDiVe = ChiTietHoaDonDAO.layKhachHangTheoCTHD(maHD);

        // 6. Thông tin hóa đơn
        HoaDon hoaDon = ChiTietHoaDonDAO.layHoaDonTheoMaHD(maHD);

        // 7. Tên nhân viên lập hóa đơn
        NhanVien nhanVien = ChiTietHoaDonDAO.layTenNhanVienTheoMaHD(maHD);

        // 8. Tên loại vé theo mã hóa đơn
        List<LoaiVe> loaiVe = ChiTietHoaDonDAO.layLoaiVeTheoMaHD(maHD);

        // ===================================================================
        // IN RA MÀN HÌNH ĐỂ XEM DỮ LIỆU
        // ===================================================================

        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("               CHI TIẾT HÓA ĐƠN: " + maHD);
        System.out.println("════════════════════════════════════════════════════════════\n");

        // 1. Thông tin hóa đơn
        System.out.println("THÔNG TIN HÓA ĐƠN:");
        if (hoaDon != null) {
            String ngayLapFormatted = hoaDon.getNgayLap().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            System.out.printf("  Ngày lập: %s%n", ngayLapFormatted);
            System.out.printf("  Tổng cộng: %,15.0f VNĐ%n", hoaDon.getTongCong());
            System.out.printf("  Phương thức: %s%n", hoaDon.getPhuongThuc());
        } else {
            System.out.println("  Không có thông tin hóa đơn.");
        }
        System.out.println();

        // 2. Khách hàng đặt hóa đơn
        System.out.println("KHÁCH HÀNG ĐẶT HÓA ĐƠN:");
        System.out.printf("  Họ tên: %s%n", khachHangDat.getHoTen());
        System.out.printf("  SĐT: %s%n", khachHangDat.getSdt());
        System.out.printf("  Giới tính: %s%n", khachHangDat.getGioiTinh());
        System.out.println();

        // 3. Danh sách vé (Chi tiết hóa đơn)
        System.out.println("DANH SÁCH VÉ:");
        System.out.println("  Mã vé        | SL | Đơn giá (VNĐ)");
        System.out.println("  -----------------------------------");
        for (ChiTietHoaDon ct : chiTietList) {
            System.out.printf("  %-12s | %2d | %,12.0f%n",
                    ct.getMaVe(), ct.getSoLuong(), ct.getDonGia());
        }
        System.out.println();

        // 4. Thông tin chuyến tàu
        System.out.println("THÔNG TIN CHUYẾN TÀU:");
        if (chuyenTau != null) {
            System.out.printf("  Mã chuyến: %s | Mã tàu: %s%n",
                    chuyenTau.getMaChuyenTau(), chuyenTau.getMaTau());
            System.out.printf("  Ga đi: %-20s → Ga đến: %-20s%n",
                    chuyenTau.getGaDi().getTenGa(), chuyenTau.getGaDen().getTenGa());
            System.out.printf("  Khởi hành: %s %s%n",
                    chuyenTau.getNgayKhoiHanh(), chuyenTau.getGioKhoiHanh());
            System.out.printf("  Đến dự kiến: %s %s%n",
                    chuyenTau.getNgayDenDuKien(), chuyenTau.getGioDenDuKien());
        } else {
            System.out.println("  Không có thông tin chuyến tàu.");
        }
        System.out.println();

        // 5. Khuyến mãi
        System.out.println("KHUYẾN MÃI:");

// Giả định bạn đã có một đối tượng KhuyenMai hợp lệ (không null)
        if (khuyenMai != null) {
            if (khuyenMai.getGiaTriGiam().compareTo(java.math.BigDecimal.ZERO) > 0) {
                String loaiKM = khuyenMai.getLoaiKM();
                String giaTriGiamFormatted;
                if ("PHAN_TRAM_GIA".equals(loaiKM)) {
                    giaTriGiamFormatted = String.format("%.0f%%", khuyenMai.getGiaTriGiam());
                } else if ("CO_DINH".equals(loaiKM)) {
                    java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                    giaTriGiamFormatted = nf.format(khuyenMai.getGiaTriGiam()) + " VNĐ";
                } else {
                    giaTriGiamFormatted = khuyenMai.getGiaTriGiam().toString();
                }
                System.out.printf("  Giảm: %s | Mô tả: %s%n",
                        giaTriGiamFormatted, khuyenMai.getTenKM().trim());

            } else {
                System.out.println("  Không có khuyến mãi áp dụng.");
            }
        } else {
            System.out.println("  Không có khuyến mãi áp dụng.");
        }

        System.out.println();

        // 6. Danh sách khách hàng đi vé
        System.out.println("DANH SÁCH KHÁCH HÀNG ĐI VÉ:");
        System.out.println("  Họ tên                  | CCCD           | Giới tính");
        System.out.println("  -----------------------------------------------------");
        for (KhachHang kh : khachHangDiVe) {
            System.out.printf("  %-23s | %-14s | %s%n",
                    kh.getHoTen(), kh.getSoCCCD(), kh.getGioiTinh());
        }

        System.out.println("\n════════════════════════════════════════════════════════════");



        // BẮT ĐẦU GÁN DỮ LIỆU VÀO CÁC LABEL TRONG PANEL
        // 1. Thông tin chuyến tàu
        lblMaChuyen.setText(chuyenTau.getMaChuyenTau());
        lblSoHieuTau.setText(chuyenTau.getMaTau());
        lblGaDi.setText(chuyenTau.getGaDi().getTenGa());
        lblGaDen.setText(chuyenTau.getGaDen().getTenGa());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblNgayKhoiHanh.setText(chuyenTau.getNgayKhoiHanh().format(formatter));
        lblGioKhoiHanh.setText(chuyenTau.getGioKhoiHanh().toString());
        lblNgayDuKien.setText(chuyenTau.getNgayDenDuKien().format(formatter));
        lblGioDuKien.setText(chuyenTau.getGioDenDuKien().toString());

        // 2. Thông tin khách hàng đặt hóa đơn
        lblTenHanhKhach.setText(khachHangDat.getHoTen());
        lblSoDienThoai.setText(khachHangDat.getSdt());
        lblEmail.setText(khachHangDat.getGioiTinh());


        // Kiểm tra null trước khi gọi bất kỳ phương thức nào
        if (khachHangDat.getGioiTinh() != null) {
            if (khachHangDat.getGioiTinh().equals("Nam")){
                lblEmail.setIcon(resizedMaleIcon);
            }
            else {
                lblEmail.setIcon(resizedFemaleIcon);
            }
        } else {
            // Xử lý khi Giới Tính là NULL (ví dụ: gán icon mặc định)
            // lblEmail.setIcon(resizedGenderIcon);
            lblEmail.setIcon(null); // Không gán icon
            lblEmail.setText("N/A");
        }
        lblEmail.repaint();

        //4 Thông tin giao dịch
        lblNgayLapHD.setText(hoaDon.getNgayLap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")));
        lblTongTien.setText(String.format("%,.0f đ", hoaDon.getTongCong()));
        lblPhuongThucThanhToan.setText(hoaDon.getPhuongThuc());
        lblTenNguoiLap.setText(nhanVien.getHoTen());
        lblSoTienPhaiThanhToan.setText(String.format("%,.0f ₫", hoaDon.getTongTien()));
        lblNguoiThanhToan.setText(khachHangDat.getHoTen());

        // 5. Khuyến mãi (Đã sửa để kiểm tra loại khuyến mãi)

// BƯỚC 1: Đảm bảo đối tượng KhuyenMai không null và có giá trị giảm > 0
        if (khuyenMai != null && khuyenMai.getGiaTriGiam().compareTo(java.math.BigDecimal.ZERO) > 0) {

            String loaiKM = khuyenMai.getLoaiKM();
            java.math.BigDecimal giaTriGiam = khuyenMai.getGiaTriGiam();
            String textHienThiKM;

            // BƯỚC 2: Kiểm tra loại KM để định dạng giá trị chính xác
            if ("PHAN_TRAM_GIA".equals(loaiKM)) {
                // Loại 1: Giảm theo Phần trăm giá
                // Hiển thị giá trị giảm + ký hiệu %
                textHienThiKM = String.format("%.0f%%", giaTriGiam);

            } else if ("CO_DINH".equals(loaiKM)) {
                // Loại 2: Giảm cố định (VNĐ)
                // Định dạng tiền tệ cho dễ đọc (ví dụ: 50.000)
                java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                textHienThiKM = nf.format(giaTriGiam) + " VNĐ";

            } else {
                // Trường hợp không xác định
                textHienThiKM = giaTriGiam.toString();
            }

            // BƯỚC 3: Cập nhật Labels trên giao diện

            // Cập nhật Label hiển thị giá trị giảm (ví dụ: "10%" hoặc "50.000 VNĐ")
            lblKMchoHoaDon.setText(textHienThiKM);

            // Cập nhật Label hiển thị mô tả/tên khuyến mãi
            lblNoiDungKhuyenMai.setText(khuyenMai.getTenKM().trim());

        } else {
            // Trường hợp không có khuyến mãi áp dụng hoặc giá trị giảm <= 0
            lblKMchoHoaDon.setText("0%"); // Hoặc "Không áp dụng" tùy theo thiết kế
            lblNoiDungKhuyenMai.setText("Không có Khuyến mãi phù hợp.");
        }

        // 6. Cập nhật bảng chi tiết hành khách
        DefaultTableModel model = (DefaultTableModel) BangChiTietHanhKhach.getModel();
        model.setRowCount(0);

        for (int i = 0; i < chiTietList.size(); i++) {
            model.addRow(new Object[]{
                    (i + 1),                                    // STT
                    khachHangDiVe.get(i).getHoTen(),            // Họ và tên
                    khachHangDiVe.get(i).getGioiTinh(),         // Giới tính
                    khachHangDiVe.get(i).getSoCCCD(),           // CCCD
                    loaiVe.get(i).getTenLoai(),                 // Loại vé
                    chiTietList.get(i).getSoLuong(),            // Số lượng
                    String.format("%,.0f", chiTietList.get(i).getDonGia()) // Đơn giá
            });
        }

        btnSoLuongVe.setText(chiTietList.size() + " Vé");

    }

    // --- HÀM MAIN GIỮ NGUYÊN HOÀN TOÀN ---
    public static void main(String[] args) {
        // 1. Tạo một cửa sổ mới
        // Đã sửa javax.swing.JFrame -> JFrame
        JFrame frame = new JFrame("Test Màn Hình Tra Cứu");

        // 2. Tạo một bản sao của JPanel (chính là class này)
        PopUpChiTietHoaDon panel = new PopUpChiTietHoaDon("HD0117102500020001");

        // 3. Thêm panel vào làm nội dung cho cửa sổ
        frame.setContentPane(panel);

        // 4. Thiết lập hành động mặc định khi bấm nút X (tắt chương trình)
        // Đã sửa javax.swing.JFrame -> JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 5. Tự động điều chỉnh kích thước cửa sổ vừa với nội dung panel
        frame.pack();

        // 6. Tùy chọn: Đặt cửa sổ ra giữa màn hình
        frame.setLocationRelativeTo(null);

        // 7. Hiển thị cửa sổ
        frame.setVisible(true);
    }
    // ---------------------------------------------
}