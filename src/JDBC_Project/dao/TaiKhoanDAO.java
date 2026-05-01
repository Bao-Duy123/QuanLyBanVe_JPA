package JDBC_Project.dao;

import entity.TaiKhoan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TaiKhoanDAO {

    // Khai báo biến connection ở phạm vi lớp/DAO nếu nó được dùng lại
    // Nếu không, khai báo nó trong phương thức để đảm bảo quản lý tài nguyên tốt hơn

    /**
     * Thực hiện chức năng đăng nhập, xác thực MaNV và MatKhau.
     * @param maNVOrTenDangNhap Mã nhân viên hoặc Tên đăng nhập
     * @param matKhau Mật khẩu thô
     * @return TaiKhoan object nếu đăng nhập thành công, null nếu thất bại.
     */
    public TaiKhoan dangNhap(String maNVOrTenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = null;
        Connection connection = null; // Khai báo Connection ở đây để đóng trong khối finally

        //Truy vấn của bạn đang tìm kiếm theo MaNV và MatKhau.
        String sql = "SELECT MaNV, TenDangNhap, NgayTao, TrangThai FROM TaiKhoan WHERE MaNV = ? AND MatKhau = ?";
        try {
            connection = database.ConnectDB.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, maNVOrTenDangNhap);
                preparedStatement.setString(2, matKhau);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Đọc dữ liệu từ ResultSet
                        String maNV = resultSet.getString("MaNV");
                        String tenDangNhapDB = resultSet.getString("TenDangNhap");
                        String trangThai = resultSet.getString("TrangThai");

                        // Chuyển đổi từ java.sql.Date sang java.time.LocalDate
                        LocalDate ngayTao = resultSet.getDate("NgayTao").toLocalDate();

                        // Trả về đối tượng TaiKhoan
                        taiKhoan = new TaiKhoan(tenDangNhapDB, maNV, matKhau, ngayTao, trangThai);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi CSDL khi thực hiện đăng nhập: " + e.getMessage());
        }
        return taiKhoan;
    }
    public static boolean doiMatKhau(String maNV, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE MaNV = ?";
        try (Connection con = database.ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, matKhauMoi);
            pstmt.setString(2, maNV);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}