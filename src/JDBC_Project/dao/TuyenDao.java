package JDBC_Project.dao;

import database.ConnectDB;
import entity.Tuyen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho Entity Tuyen.
 */
public class TuyenDao {
    private Connection con;

    public TuyenDao() {
        // Khởi tạo kết nối trong constructor (Giả sử ConnectDB đã được thiết lập)
        try {
            con = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy tất cả các tuyến tàu từ CSDL.
     * @return Danh sách các đối tượng Tuyen.
     */
    // Trong TuyenDao.java

    public List<Tuyen> layTatCaTuyen() throws SQLException {
        List<Tuyen> danhSach = new ArrayList<>();
        String sql = "SELECT MaTuyen, TenTuyen, GaDau, GaCuoi, DonGiaKM FROM Tuyen";

        // Mở kết nối NGAY TRONG try-with-resources
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Tuyen t = new Tuyen(
                        rs.getString("MaTuyen"),
                        rs.getString("TenTuyen"),
                        rs.getString("GaDau"),
                        rs.getString("GaCuoi"),
                        rs.getInt("DonGiaKM")
                );
                danhSach.add(t);
            }
        }
        return danhSach;
    }
    // Lấy tuyến theo Mã Tuyến

    public Tuyen layTuyenTheoMa(String maTuyen) throws SQLException {
        if (maTuyen == null || maTuyen.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT MaTuyen, TenTuyen, GaDau, GaCuoi, DonGiaKM FROM Tuyen WHERE MaTuyen = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, maTuyen);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Sử dụng biến tạm để dễ debug nếu cần
                    String ma = rs.getString("MaTuyen");
                    String ten = rs.getString("TenTuyen");
                    String gaD = rs.getString("GaDau");
                    String gaC = rs.getString("GaCuoi");
                    int don = rs.getInt("DonGiaKM");

                    return new Tuyen(ma, ten, gaD, gaC, don);
                }
            }
        }
        return null; // Không tìm thấy
    }

    /**
     * Thêm một Tuyến mới vào CSDL.
     */
    public boolean themTuyen(Tuyen tuyen) throws SQLException {
        String sql = "INSERT INTO Tuyen (MaTuyen, TenTuyen, GaDau, GaCuoi, DonGiaKM) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConnectDB.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, tuyen.getMaTuyen());
            pst.setString(2, tuyen.getTenTuyen());
            pst.setString(3, tuyen.getGaDau());
            pst.setString(4, tuyen.getGaCuoi());
            pst.setInt(5, tuyen.getDonGiaKM());
            return pst.executeUpdate() > 0;
        }
    }

    // TODO: Bổ sung các phương thức Sửa và Xóa Tuyến nếu cần


    //todo: lấy giá và sửa giá theo mã tuyến
    public int layGiaDonGia(String maTuyen) throws SQLException {
        String sql = "SELECT DonGiaKM FROM Tuyen WHERE MaTuyen = ?";
        try (Connection con = ConnectDB.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, maTuyen);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("DonGiaKM");
                }
            }
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }

    public boolean suaGiaTheoMaTuyen(String maTuyen, double giaMoi) throws SQLException {
        String sql = "UPDATE Tuyen SET DonGiaKM = ? WHERE MaTuyen = ?";
        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, giaMoi);
            pst.setString(2, maTuyen);
            return pst.executeUpdate() > 0;
        }
    }
}