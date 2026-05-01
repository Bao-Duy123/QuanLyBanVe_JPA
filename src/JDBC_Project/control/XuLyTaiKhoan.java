package JDBC_Project.control;

import dao.TaiKhoanDAO;
import entity.TaiKhoan;

/**
 * Lớp Control xử lý các nghiệp vụ liên quan đến Tài Khoản, chủ yếu là Đăng Nhập.
 */
public class XuLyTaiKhoan {

    private static final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Phương thức xác thực người dùng bằng cách kiểm tra TenDangNhap và MatKhau
     * trong CSDL thông qua TaiKhoanDAO.
     * * @param tenDangNhap Tên đăng nhập (TenDangNhap hoặc MaNV)
     * @param matKhau Mat khẩu thô do người dùng nhập.
     * @return TaiKhoan object nếu đăng nhập thành công, null nếu thất bại.
     */
    public static TaiKhoan authenticate(String tenDangNhap, String matKhau) {


        TaiKhoan taiKhoan = taiKhoanDAO.dangNhap(tenDangNhap, matKhau);

        if (taiKhoan == null) {
            System.out.println("Lỗi xác thực: Tên đăng nhập hoặc mật khẩu không đúng.");
            return null;
        }

        if (taiKhoan.getTrangThai() != null && taiKhoan.getTrangThai().equalsIgnoreCase("Đang hoạt động")) {
            System.out.println("Đăng nhập thành công cho nhân viên: " + taiKhoan.getMaNV());
            return taiKhoan;
        } else {
            System.out.println("Lỗi xác thực: Tài khoản chưa được kích hoạt hoặc đã bị khóa.");
            return null;
        }
    }
    public static boolean doiMatKhau(String maNV, String matKhauMoi) {
        // Gọi trực tiếp phương thức vừa thêm ở TaiKhoanDAO
        return TaiKhoanDAO.doiMatKhau(maNV, matKhauMoi);
    }
    // Thêm các phương thức xử lý nghiệp vụ khác như:
    // thayDoiMatKhau(TaiKhoan tk, String matKhauMoi), taoTaiKhoanMoi(NhanVien nv, String matKhau), ...
}