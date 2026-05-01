package JDBC_Project.dao;

import database.ConnectDB;
import entity.Ga;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GaDao {

	public static Ga layGaBangMa(String maGaDi) {
		Ga ga = null;
        String sql = "SELECT MaGa, TenGa, DiaChi FROM Ga WHERE MaGa = ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maGaDi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ga = new Ga(rs.getString("MaGa"), rs.getString("TenGa"), rs.getString("DiaChi"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi tải Ga theo MaGa: " + e.getMessage());
            e.printStackTrace();
        }
        return ga;
    }

    public static Ga layGaBangTen(String tenGa) {
        Ga ga = null;
        String sql = "SELECT MaGa, TenGa, DiaChi FROM Ga WHERE TenGa = ?";

        Connection con = null; // KHAI BÁO BÊN NGOÀI
        try {
            con = ConnectDB.getInstance().getConnection();

            // CHỈ DÙNG TRY-WITH-RESOURCES CHO PreparedStatement
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setString(1, tenGa);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ga = new Ga(
                                rs.getString("MaGa"),
                                rs.getString("TenGa"),
                                rs.getString("DiaChi")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải Ga theo TenGa: " + tenGa + " - Chi tiết: " + e.getMessage());
            e.printStackTrace();
        }
        return ga;
    }


    private static GaDao instance;
	public static GaDao getInstance() {
        if (instance == null) {
            // Đồng bộ hóa (synchronized) để đảm bảo an toàn đa luồng khi khởi tạo
            synchronized (GaDao.class) {
                if (instance == null) {
                    // Tạo instance chỉ một lần
                    instance = new GaDao();
                }
            }
        }
        return instance;
    }


    // Trong GaDao.java
    public Vector<Ga> layDanhSachGa() {
        Vector<Ga> danhSachGa = new Vector<>();
        String sql = "SELECT MaGa, TenGa, DiaChi FROM Ga";

        Connection con = null; // KHAI BÁO BÊN NGOÀI
        try {
            con = ConnectDB.getInstance().getConnection();

            try (PreparedStatement pstmt = con.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Ga ga = new Ga(rs.getString("MaGa"), rs.getString("TenGa"), rs.getString("DiaChi"));
                    System.out.println(rs.getString("MaGa"));
                    danhSachGa.add(ga);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách Ga: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(danhSachGa);
        return danhSachGa;
    }
    //---

    /**
     * Lấy danh sách Tên Ga từ CSDL.
     * @return Danh sách các chuỗi TenGa.
     * @throws SQLException Nếu có lỗi xảy ra khi truy vấn CSDL.
     */
    public static List<String> layDanhSachTenGa() throws SQLException {
        List<String> danhSachTenGa = new ArrayList<>();
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT TenGa FROM Ga");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                danhSachTenGa.add(rs.getString("TenGa"));
            }
        }
        return danhSachTenGa;
    }

}
