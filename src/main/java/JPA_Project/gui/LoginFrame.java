package JPA_Project.gui;

import JPA_Project.entity.NhanVien;
import JPA_Project.network.NetworkManager;
import JPA_Project.network.TrainClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class LoginFrame extends JFrame implements ActionListener {

    private static final String DUONG_DAN_LOGO = "src/images/logo-train.png";
    private static final String DUONG_DAN_ANH_TAU = "src/images/anh-tau.jpg";
    private static final int KICH_THUOC_LOGO = 80;
    private static final int CHIEU_RONG_ANH_TAU = 500;

    private JButton btnDangNhap;
    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private SwingWorker<LoginResult, Void> dangNhapWorker;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng nhập Hệ thống Quản lý Bán vé Tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel bangAnhTau = taoBangAnhTau();
        add(bangAnhTau, BorderLayout.WEST);

        JPanel bangTrungTamDangNhap = taoBangTrungTamDangNhap();
        add(bangTrungTamDangNhap, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel taoBangAnhTau() {
        JPanel bang = new JPanel(new BorderLayout());
        bang.setPreferredSize(new Dimension(CHIEU_RONG_ANH_TAU, 600));
        bang.setBackground(new Color(41, 128, 185));

        JLabel nhanAnhTau = taoNhanAnhDaChinhKichThuoc(DUONG_DAN_ANH_TAU, CHIEU_RONG_ANH_TAU, 500, "TRAIN IMAGE");
        if (nhanAnhTau.getIcon() == null) {
            nhanAnhTau.setText("HÌNH ẢNH TÀU");
            nhanAnhTau.setFont(new Font("Arial", Font.BOLD, 20));
            nhanAnhTau.setForeground(Color.WHITE);
            nhanAnhTau.setHorizontalAlignment(SwingConstants.CENTER);
        }

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

    private JPanel taoBangTrungTamDangNhap() {
        JPanel bangTrungTam = new JPanel();
        bangTrungTam.setLayout(new BoxLayout(bangTrungTam, BoxLayout.Y_AXIS));
        bangTrungTam.setBackground(Color.WHITE);
        bangTrungTam.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel bangForm = taoBangFormDangNhap();
        bangForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        bangForm.setMaximumSize(new Dimension(400, 450));

        bangTrungTam.add(Box.createVerticalGlue());
        bangTrungTam.add(bangForm);
        bangTrungTam.add(Box.createVerticalGlue());

        return bangTrungTam;
    }

    private JPanel taoBangFormDangNhap() {
        JPanel bang = new JPanel();
        bang.setLayout(new BoxLayout(bang, BoxLayout.Y_AXIS));
        bang.setBackground(Color.WHITE);
        bang.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel bangTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        bangTieuDe.setBackground(Color.WHITE);

        JLabel nhanLogo = taoNhanAnhDaChinhKichThuoc(DUONG_DAN_LOGO, KICH_THUOC_LOGO, KICH_THUOC_LOGO, "LOGO");
        if (nhanLogo.getIcon() == null) {
            nhanLogo.setText("VietLai");
            nhanLogo.setFont(new Font("Arial", Font.BOLD, 24));
            nhanLogo.setForeground(new Color(41, 128, 185));
        }

        JLabel nhanTieuDe = new JLabel("ĐĂNG NHẬP");
        nhanTieuDe.setFont(new Font("Arial", Font.BOLD, 28));
        nhanTieuDe.setForeground(new Color(52, 73, 94));

        bangTieuDe.add(nhanLogo);
        bangTieuDe.add(nhanTieuDe);
        bang.add(bangTieuDe);

        bang.add(Box.createVerticalStrut(30));

        JLabel lblTaiKhoan = new JLabel("Tên đăng nhập:");
        lblTaiKhoan.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 14));
        bang.add(lblTaiKhoan);
        bang.add(Box.createVerticalStrut(5));

        txtTaiKhoan = new JTextField();
        tuyChinhTruongVanBan(txtTaiKhoan);
        txtTaiKhoan.addActionListener(this);
        txtTaiKhoan.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    String taiKhoan = txtTaiKhoan.getText().trim();
                    String matKhau = new String(txtMatKhau.getPassword()).trim();
                    
                    System.out.println("[DEBUG-LoginFrame] Enter pressed in txtTaiKhoan - TaiKhoan: '" + taiKhoan + "'");
                    
                    if (taiKhoan.isEmpty()) {
                        txtTaiKhoan.requestFocus();
                    } else if (matKhau.isEmpty()) {
                        txtMatKhau.requestFocus();
                    } else {
                        xuLyDangNhapAsync();
                    }
                }
            }
        });
        bang.add(txtTaiKhoan);

        bang.add(Box.createVerticalStrut(15));

        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMatKhau.setFont(new Font("Arial", Font.PLAIN, 14));
        bang.add(lblMatKhau);
        bang.add(Box.createVerticalStrut(5));

        txtMatKhau = new JPasswordField();
        tuyChinhTruongVanBan(txtMatKhau);
        txtMatKhau.addActionListener(this);
        txtMatKhau.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    String taiKhoan = txtTaiKhoan.getText().trim();
                    String matKhau = new String(txtMatKhau.getPassword()).trim();
                    
                    System.out.println("[DEBUG-LoginFrame] Enter pressed in txtMatKhau - MatKhau length: " + matKhau.length());
                    
                    if (!taiKhoan.isEmpty() && !matKhau.isEmpty()) {
                        xuLyDangNhapAsync();
                    } else if (taiKhoan.isEmpty()) {
                        txtTaiKhoan.requestFocus();
                    }
                }
            }
        });
        bang.add(txtMatKhau);

        bang.add(Box.createVerticalStrut(25));

        btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        btnDangNhap.setBackground(new Color(0, 123, 255));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 16));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDangNhap.setOpaque(true);
        btnDangNhap.setBorderPainted(false);

        btnDangNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDangNhap.addActionListener(this);
        bang.add(btnDangNhap);

        bang.add(Box.createVerticalStrut(10));

        JButton nutQuenMatKhau = new JButton("Quên mật khẩu?");
        nutQuenMatKhau.setContentAreaFilled(false);
        nutQuenMatKhau.setBorderPainted(false);
        nutQuenMatKhau.setForeground(new Color(41, 128, 185));
        nutQuenMatKhau.setFont(new Font("Arial", Font.PLAIN, 12));
        nutQuenMatKhau.setAlignmentX(Component.LEFT_ALIGNMENT);
        nutQuenMatKhau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bang.add(nutQuenMatKhau);

        return bang;
    }

    private void tuyChinhTruongVanBan(JComponent truong) {
        truong.setFont(new Font("Arial", Font.PLAIN, 15));
        truong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        truong.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (truong instanceof JTextField) {
            ((JTextField) truong).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        } else if (truong instanceof JPasswordField) {
            ((JPasswordField) truong).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }
    }

    private JLabel taoNhanAnhDaChinhKichThuoc(String duongDan, int chieuRong, int chieuCao, String chuThich) {
        JLabel nhanAnh = new JLabel();
        try {
            File file = new File(duongDan);
            if (file.exists()) {
                ImageIcon iconGoc = new ImageIcon(duongDan);
                Image anh = iconGoc.getImage();
                Image anhDaChinhKichThuoc = anh.getScaledInstance(chieuRong, chieuCao, Image.SCALE_SMOOTH);
                nhanAnh.setIcon(new ImageIcon(anhDaChinhKichThuoc));
            } else {
                nhanAnh.setText(chuThich);
                nhanAnh.setFont(new Font("Arial", Font.BOLD, 16));
                nhanAnh.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            nhanAnh.setText(chuThich);
            nhanAnh.setFont(new Font("Arial", Font.BOLD, 16));
        }
        return nhanAnh;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == txtMatKhau || e.getSource() == btnDangNhap || e.getSource() == txtTaiKhoan) {
            // Hỏi kết nối Server trước
            hoiKetNoiServerVaDangNhap();
        }
    }
    
    /**
     * Hỏi kết nối Server trước, sau đó mới đăng nhập
     */
    private void hoiKetNoiServerVaDangNhap() {
        // Hiển thị dialog kết nối server TRƯỚC
        ServerConnectionDialog connectionDialog = new ServerConnectionDialog(null);
        connectionDialog.setVisible(true);
        
        if (connectionDialog.isConnected()) {
            System.out.println("[DEBUG-LoginFrame] Da ket noi server: " + 
                JPA_Project.network.NetworkManager.getInstance().getConnectionInfo());
        } else {
            System.out.println("[DEBUG-LoginFrame] Chua ket noi server - se kiem tra JPA khi dang nhap");
        }
        
        // Sau đó mới đăng nhập (kiểm tra JPA trong xuLyDangNhapAsync)
        xuLyDangNhapAsync();
    }

    private void xuLyDangNhapAsync() {
        if (dangNhapWorker != null && !dangNhapWorker.isDone()) {
            return;
        }

        final String tenDangNhap = txtTaiKhoan.getText().trim();
        final char[] matKhauChars = txtMatKhau.getPassword();
        final String matKhau = new String(matKhauChars);
        Arrays.fill(matKhauChars, '\0');

        System.out.println("[DEBUG-LoginFrame] xuLyDangNhapAsync called - tenDangNhap: '" + tenDangNhap + "'");

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên đăng nhập và Mật khẩu.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnDangNhap.setEnabled(false);
        txtTaiKhoan.setEnabled(false);
        txtMatKhau.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        dangNhapWorker = new SwingWorker<>() {
            @Override
            protected LoginResult doInBackground() {
                try {
                    // Kiểm tra xem đã kết nối Server chưa
                    if (NetworkManager.getInstance().isConnected()) {
                        // Đăng nhập qua Server
                        System.out.println("[DEBUG-LoginFrame] Dang nhap qua server...");
                        TrainClient.LoginResponse resp = NetworkManager.getInstance().getTrainClient().login(tenDangNhap, matKhau);
                        
                        if (resp.success) {
                            NhanVien nv = new NhanVien();
                            nv.setMaNV(resp.maNV);
                            nv.setHoTen(resp.hoTen);
                            nv.setChucVu(resp.chucVu);
                            
                            // Xác định role
                            String chucVu = resp.chucVu;
                            if (resp.maNV != null && (resp.maNV.startsWith("NVQL") || resp.maNV.startsWith("NVTP"))) {
                                chucVu = "QUAN_LY";
                            } else if (resp.maNV != null && resp.maNV.startsWith("NVBV")) {
                                chucVu = "NHAN_VIEN_BAN_VE";
                            }
                            
                            System.out.println("[DEBUG-LoginFrame] Server login success: " + resp.maNV);
                            return LoginResult.dangNhapThanhCong(nv, chucVu);
                        } else {
                            System.out.println("[DEBUG-LoginFrame] Server login failed: " + resp.message);
                            return LoginResult.dangNhapThatBai(resp.message);
                        }
                    }
                    
                    // Chưa kết nối Server - BẮT BUỘC phải kết nối
                    return LoginResult.dangNhapThatBai("Bạn cần kết nối đến Server trước khi đăng nhập.\nVui lòng chọn 'Kết nối Server' trong dialog kết nối.");
                } catch (Exception ex) {
                    System.err.println("[DEBUG-LoginFrame] Login exception: " + ex.getMessage());
                    ex.printStackTrace();
                    return LoginResult.dangNhapThatBai(ex.getMessage() != null ? ex.getMessage() : "Lỗi hệ thống.");
                }
            }

            @Override
            protected void done() {
                btnDangNhap.setEnabled(true);
                txtTaiKhoan.setEnabled(true);
                txtMatKhau.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());

                if (!isDisplayable()) return;

                try {
                    LoginResult ketQua = get();
                    if (ketQua.coLoi()) {
                        JOptionPane.showMessageDialog(LoginFrame.this, ketQua.getThongBaoLoi(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Mở MainFrame (Server đã được kết nối ở bước trước)
                    dispose();
                    MainFrame mainFrame = new MainFrame(ketQua.getNhanVien(), ketQua.getChucVu());
                    mainFrame.setVisible(true);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        dangNhapWorker.execute();
    }

    private static class LoginResult {
        private final NhanVien nhanVien;
        private final String thongBaoLoi;
        private final String chucVu;

        private LoginResult(NhanVien nhanVien, String thongBaoLoi, String chucVu) {
            this.nhanVien = nhanVien;
            this.thongBaoLoi = thongBaoLoi;
            this.chucVu = chucVu;
        }

        static LoginResult dangNhapThanhCong(NhanVien nhanVien, String chucVu) {
            return new LoginResult(nhanVien, null, chucVu);
        }

        static LoginResult dangNhapThatBai(String thongBaoLoi) {
            return new LoginResult(null, thongBaoLoi, null);
        }

        boolean coLoi() {
            return thongBaoLoi != null;
        }

        NhanVien getNhanVien() {
            return nhanVien;
        }

        String getThongBaoLoi() {
            return thongBaoLoi;
        }

        String getChucVu() {
            return chucVu;
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("sun.stdout.encoding", "UTF-8");
            System.setProperty("sun.stderr.encoding", "UTF-8");
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
