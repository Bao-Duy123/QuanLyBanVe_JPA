package JDBC_Project.dao;

import database.ConnectDB;
import entity.LoaiToa;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO tạm thời cho hệ số theo loại chỗ đặt.
 * mucGiaGiam trong DB được hiểu là hệ số nhân (ví dụ giường nằm = 1.5)
 */
public class LoaiToaDAO {
    private final Map<String, Double> mock = new HashMap<>();

    public List<LoaiToa> getAllLoaiToa() {
        List<LoaiToa> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoaiToa";

        try { Connection con = ConnectDB.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String ma = rs.getString("MaLoaiToa");
                String ten = rs.getString("TenLoaiToa");
                double heSo = rs.getDouble("HeSo");

                LoaiToa loai = new LoaiToa(ma, ten, heSo);
                ds.add(loai);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Tìm một loại chỗ theo Mã
     */
    public static LoaiToa getLoaiToaByMa(String ten) {
        LoaiToa loai = null;

        String sql = "SELECT * FROM LoaiToa WHERE TenLoaiToa = ?";

        try {Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ten);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                loai = new LoaiToa(
                        rs.getString("MaLoaiToa"),
                        rs.getString("TenLoaiToa"),
                        rs.getDouble("HeSo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loai;
    }
    /**
     * Tìm một loại chỗ theo Mã
     */
    public static LoaiToa getLoaiToaByMaLoaiToa(String maLoai) {
        LoaiToa loai = null;

        String sql = "SELECT * FROM LoaiToa WHERE MaLoaiToa = ?";

        try {Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maLoai);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                loai = new LoaiToa(
                        rs.getString("MaLoaiToa"),
                        rs.getString("TenLoaiToa"),
                        rs.getDouble("HeSo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loai;
    }



    /**
     * Thêm mới một loại chỗ
     */
    public boolean addLoaiChoDat(LoaiToa loai) {

        PreparedStatement ps = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            ps = con.prepareStatement("INSERT INTO LoaiToa VALUES(?, ?, ?)");
            ps.setString(1, loai.getMaLoaiCho());
            ps.setString(2, loai.getTenLoaiCho());
            ps.setDouble(3, loai.getHeSo());
            n = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Cập nhật thông tin loại chỗ
     */
    public boolean updateLoaiToa(LoaiToa loai) {
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE LoaiToa SET TenLoaiToa = ?, HeSo = ? WHERE MaLoaiToa = ?");
            ps.setString(1, loai.getTenLoaiCho());
            ps.setDouble(2, loai.getHeSo());
            ps.setString(3, loai.getMaLoaiCho());
            n = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

}