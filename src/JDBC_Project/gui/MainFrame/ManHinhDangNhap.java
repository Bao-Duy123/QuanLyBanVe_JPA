package JDBC_Project.gui.MainFrame;

import control.XuLyNhanVien;
import control.XuLyTaiKhoan;
import entity.NhanVien;
import entity.TaiKhoan;
import control.CaLamViec;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

/**
 * Lớp ManHinhDangNhap: Tạo giao diện màn hình Đăng nhập
 */
public class ManHinhDangNhap extends JFrame implements ActionListener {

    // --- Thuộc tính (Properties) đã Việt hóa ---
    private static final String DUONG_DAN_LOGO = "src/images/logo-train.png";
    private static final String DUONG_DAN_ANH_TAU = "src/images/anh-tau.jpg";
    private static final int KICH_THUOC_LOGO = 80;
    private static final int CHIEU_RONG_ANH_TAU = 600;

    private JButton btnDangNhap;
    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;

    // --- Constructor đã Việt hóa ---
    public ManHinhDangNhap() {
        setTitle("Đăng nhập Hệ thống Quản lý Bán vé Tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // 1. Thêm Panel Ảnh Đoàn Tàu (Bên trái - WEST)
        JPanel bangAnhTau = taoBangAnhTau();
        add(bangAnhTau, BorderLayout.WEST);

        // 2. Thêm Panel Form Đăng nhập (Trung tâm - CENTER)
        JPanel bangTrungTamDangNhap = taoBangTrungTamDangNhap();
        add(bangTrungTamDangNhap, BorderLayout.CENTER);

        bangTrungTamDangNhap.setBackground(Color.WHITE);


        setVisible(true);
    }

    // --- Các Phương thức tạo Panel (Panel Creation Methods) đã Việt hóa ---

    /**
     * Phương thức tạo Panel chứa ảnh tàu (Bên trái)
     */
    private JPanel taoBangAnhTau() {
        JPanel bang = new JPanel(new BorderLayout());
        bang.setPreferredSize(new Dimension(CHIEU_RONG_ANH_TAU, this.getHeight()));

        bang.setBackground(new Color(41, 128, 185));

        JLabel nhanAnhTau = taoNhanAnhDaChinhKichThuoc(DUONG_DAN_ANH_TAU, CHIEU_RONG_ANH_TAU, 550, "TRAIN IMAGE HERE"); // createScaledImageLabel -> taoNhanAnhDaChinhKichThuoc

        JPanel containerTrungTam = new JPanel();
        containerTrungTam.setLayout(new BoxLayout(containerTrungTam, BoxLayout.Y_AXIS));
        containerTrungTam.setOpaque(false);

        containerTrungTam.add(Box.createVerticalGlue());
        nhanAnhTau.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerTrungTam.add(nhanAnhTau);
        containerTrungTam.add(Box.createVerticalGlue());

        bang.add(containerTrungTam, BorderLayout.CENTER);

        return bang;
    }

    /**
     * Phương thức tạo Panel trung tâm chứa form đăng nhập (Bên phải)
     */
    private JPanel taoBangTrungTamDangNhap() {
        JPanel bangTrungTam = new JPanel();
        bangTrungTam.setLayout(new BoxLayout(bangTrungTam, BoxLayout.Y_AXIS));
        bangTrungTam.setOpaque(false);

        JPanel bangFormDangNhap = taoBangFormDangNhap();

        bangFormDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);

        bangTrungTam.add(Box.createVerticalGlue());
        bangTrungTam.add(bangFormDangNhap);
        bangTrungTam.add(Box.createVerticalGlue());

        return bangTrungTam;
    }

    /**
     * Phương thức tạo Panel chứa các trường nhập liệu (Form Đăng nhập)
     */
    private JPanel taoBangFormDangNhap() {
        JPanel bang = new JPanel();
        bang.setLayout(new BoxLayout(bang, BoxLayout.Y_AXIS));
        bang.setOpaque(false);
        bang.setBorder(new EmptyBorder(50, 50, 50, 50));

        bang.setPreferredSize(new Dimension(400, 450));
        bang.setMaximumSize(new Dimension(400, 450));

        // --- 1. Logo và Tiêu đề ---
        JPanel bangTieuDe = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bangTieuDe.setOpaque(false);

        JLabel nhanLogo = taoNhanAnhDaChinhKichThuoc(DUONG_DAN_LOGO, KICH_THUOC_LOGO, KICH_THUOC_LOGO, "LOGO HERE"); // createScaledImageLabel -> taoNhanAnhDaChinhKichThuoc

        JLabel nhanTieuDe = new JLabel("ĐĂNG NHẬP");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 30));

        bangTieuDe.add(nhanLogo);
        bangTieuDe.add(nhanTieuDe);

        bangTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);
        bang.add(bangTieuDe);

        bang.add(Box.createVerticalStrut(30));

        // --- 2. Tên đăng nhập ---
        JLabel lblTaiKhoan = new JLabel("Tên đăng nhập:");
        lblTaiKhoan.setAlignmentX(Component.LEFT_ALIGNMENT);
        bang.add(lblTaiKhoan);

        bang.add(Box.createVerticalStrut(5));

        txtTaiKhoan = new JTextField();
        tuyChinhTruongVanBan(txtTaiKhoan);
        txtTaiKhoan.addActionListener(this);
        bang.add(txtTaiKhoan);

        bang.add(Box.createVerticalStrut(15));


        // --- 3. Mật khẩu ---
        JLabel nhanMatKhau = new JLabel("Mật khẩu:");
        nhanMatKhau.setAlignmentX(Component.LEFT_ALIGNMENT);
        bang.add(nhanMatKhau);

        bang.add(Box.createVerticalStrut(5));

        txtMatKhau = new JPasswordField();
        tuyChinhTruongVanBan(txtMatKhau);
        txtMatKhau.addActionListener(this);
        bang.add(txtMatKhau);

        bang.add(Box.createVerticalStrut(30));

        // --- 4. Nút Đăng nhập ---
        btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        btnDangNhap.setBackground(new Color(0, 123, 255));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 16));
        btnDangNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDangNhap.addActionListener(this);

        // --- 5. Nút Quên mật khẩu ---
        JButton nutQuenMatKhau = new JButton("Quên mật khẩu");
        nutQuenMatKhau.setContentAreaFilled(false);
        nutQuenMatKhau.setBorderPainted(false);
        nutQuenMatKhau.setForeground(new Color(0, 123, 255));
        nutQuenMatKhau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nutQuenMatKhau.setFont(new Font("Arial", Font.PLAIN, 12));
        nutQuenMatKhau.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Thêm các thành phần vào Panel
        bang.add(btnDangNhap);
        bang.add(Box.createVerticalStrut(10));
        bang.add(nutQuenMatKhau);
        nutQuenMatKhau.addActionListener(e -> xuLyQuenMatKhau());
        return bang;
    }

    /**
     * Tùy chỉnh JTextComponent
     */
    private void tuyChinhTruongVanBan(JTextComponent truong) {
        truong.setFont(new Font("Arial", Font.PLAIN, 16));
        truong.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        truong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        truong.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Tải và tạo JLabel chứa ảnh, điều chỉnh kích thước
     */
    private JLabel taoNhanAnhDaChinhKichThuoc(String duongDan, int chieuRong, int chieuCao, String chuThichDuPhong) { // createScaledImageLabel -> taoNhanAnhDaChinhKichThuoc
        JLabel nhanAnh = new JLabel();
        try {
            File file = new File(duongDan);
            if (!file.exists()) {
                System.err.println("Lỗi: Không tìm thấy file ảnh tại đường dẫn: " + duongDan);
                nhanAnh.setText(chuThichDuPhong);
                return nhanAnh;
            }

            ImageIcon iconGoc = new ImageIcon(duongDan);
            Image anh = iconGoc.getImage();
            Image anhDaChinhKichThuoc = anh.getScaledInstance(chieuRong, chieuCao, Image.SCALE_SMOOTH);
            nhanAnh.setIcon(new ImageIcon(anhDaChinhKichThuoc));

        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh: " + e.getMessage());
            e.printStackTrace();
            nhanAnh.setText(chuThichDuPhong);
        }
        return nhanAnh;
    }


    /**
     * Phương thức Main để chạy màn hình đăng nhập.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManHinhDangNhap::new);
    }

    // ==============================================================================
    // LOGIC XỬ LÝ SỰ KIỆN ĐĂNG NHẬP (Action Listener)
    // ==============================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // 1. Nếu nhấn Enter trên Tên đăng nhập
        if (source == txtTaiKhoan) {
            txtMatKhau.requestFocusInWindow(); // Chuyển focus sang Mật khẩu
            return;
        }




        if (source == txtMatKhau || source == btnDangNhap) {
            String tenDangNhap = txtTaiKhoan.getText().trim();
            String matKhau = new String(txtMatKhau.getPassword()).trim();

            if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên đăng nhập và Mật khẩu.", "Lỗi đăng nhập", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // --- BƯỚC 1: KIỂM TRA ĐĂNG NHẬP CỨNG (Admin Bypass) ---
            if (tenDangNhap.equals("admin") && matKhau.equals("admin")) {
                // Đăng nhập Admin thành công mà không cần kiểm tra CSDL


                NhanVien nhanVienAdmin = new NhanVien("ADMIN001", "ADMIN", "0000000000");

                // BẮT ĐẦU CA LÀM VIỆC VÀ LƯU SESSION
                CaLamViec.getInstance().batDauCa(nhanVienAdmin);

                // HIỂN THỊ MÀN HÌNH DASHBOARD Quản lý
                new AdminFullDashboard().setVisible(true);
                this.dispose();
                return;
            }

            // Bước 2: Gọi hàm xác thực từ lớp Control
            TaiKhoan taiKhoan = XuLyTaiKhoan.authenticate(tenDangNhap, matKhau);

            if (taiKhoan != null) {
                // Xác thực thành công:
                String maNV = taiKhoan.getMaNV();

                // --- BƯỚC 2: LẤY VÀ LƯU THÔNG TIN NHÂN VIÊN ---
                NhanVien nhanVien = null;
                try {
                    // Gọi lớp xử lý để lấy thông tin NhanVien dựa trên MaNV
                    nhanVien = XuLyNhanVien.layThongTinNhanVienChoCaLamViec(maNV);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin nhân viên: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                    return; // Ngừng nếu không lấy được thông tin NV
                }

                if (nhanVien == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin nhân viên cho tài khoản này.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // BƯỚC 3: BẮT ĐẦU CA LÀM VIỆC VÀ LƯU SESSION => sẽ lấy để dùng sau
                CaLamViec.getInstance().batDauCa(nhanVien);

                // --- BƯỚC 4: HIỂN THỊ MÀN HÌNH DASHBOARD PHÙ HỢP ---
                if (maNV.startsWith("NVQL")) {
//                    new JOptionPane().showMessageDialog(this, "Đăng nhập QUẢN LÝ thành công.");
                    new QuanLyDashboard().setVisible(true);
                } else if (maNV.startsWith("NVBV")) {
//                    new JOptionPane().showMessageDialog(this, "Đăng nhập BÁN VÉ thành công.");
                    new BanVeDashboard().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Tài khoản không có quyền truy cập.", "Lỗi phân quyền", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng.", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public class EmailService {
        // Thông tin tài khoản gửi (Bạn nên dùng mật khẩu ứng dụng của Gmail)
        private static final String username = "trannaml91920@gmail.com";
        private static final String password = "qniyzwpvutcfkqzn"; // Mã 16 ký tự App Password

        public static boolean guiOTP(String emailNguoiNhan, String maOTP) {
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); // Bảo mật TLS

            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailNguoiNhan));
                message.setSubject("Mã xác thực phục hồi mật khẩu");
                message.setText("Mã xác thực của bạn là: " + maOTP + "\nMã này có hiệu lực trong 5 phút.");

                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    private void xuLyQuenMatKhau() {
        // 1. Nhập mã nhân viên
        String maNV = JOptionPane.showInputDialog(this, "Nhập Mã nhân viên cần lấy lại mật khẩu:");
        if (maNV == null || maNV.isEmpty()) return;

        // 2. Lấy Email từ CSDL (Giả sử bạn đã có hàm này trong XuLyNhanVien)
        String emailNV = XuLyNhanVien.layEmailTheoMa(maNV);

        if (emailNV != null) {
            // 3. Tạo mã OTP 6 số
            String maOTP = String.valueOf((int)((Math.random() * 899999) + 100000));

            // Gửi mail (Chạy trên một luồng phụ để không bị treo giao diện)
            new Thread(() -> {
                boolean daGui = EmailService.guiOTP(emailNV, maOTP);
                if (daGui) {
                    // 4. Yêu cầu nhập OTP xác nhận
                    String nhapOTP = JOptionPane.showInputDialog(this, "Mã OTP đã gửi đến email: " + emailNV + "\nVui lòng nhập mã vào đây:");

                    if (maOTP.equals(nhapOTP)) {
                        // Cho phép đổi mật khẩu mới
                        String mkMoi = JOptionPane.showInputDialog(this, "Xác thực thành công! Nhập mật khẩu mới:");
                        if (mkMoi != null) {
                            XuLyTaiKhoan.doiMatKhau(maNV, mkMoi);
                            JOptionPane.showMessageDialog(this, "Đã cập nhật mật khẩu mới.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Mã OTP không chính xác!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi gửi Email. Vui lòng kiểm tra kết nối mạng.");
                }
            }).start();

        } else {
            JOptionPane.showMessageDialog(this, "Mã nhân viên không tồn tại hoặc chưa đăng ký Email!");
        }
    }
}