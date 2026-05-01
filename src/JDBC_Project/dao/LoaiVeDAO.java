package JDBC_Project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.ConnectDB;
import entity.LoaiVe;

public class LoaiVeDAO {


    /**
     * Lấy hệ số giảm giá (MucGiamGia) theo Mã Loại Vé từ CSDL.
     * CÓ THỂ BỊ LOẠI BỎ nếu dùng getLoaiVeByMa. Giữ lại để tương thích.
     * @param maLoaiVe Mã loại vé cần truy vấn
     * @return Hệ số giảm giá (mặc định 1.0 nếu không tìm thấy hoặc lỗi)
     */
    public double getHeSoByMaLoaiVe(String maLoaiVe) {
        if (maLoaiVe == null || maLoaiVe.trim().isEmpty()) {
            return 1.0;
        }

        double heSo = 1.0;
        // Lấy toàn bộ trường để đảm bảo tương lai dễ nâng cấp
        String sql = "SELECT MucGiamGia FROM LoaiVe WHERE maLoaiVe = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maLoaiVe);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    heSo = rs.getDouble("MucGiamGia");
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn CSDL để lấy hệ số loại vé: " + e.getMessage());
        }
        return heSo;
    }

    /**
     * Lấy tất cả các loại vé CÙNG VỚI điều kiện tuổi (TuoiMin, TuoiMax) có trong CSDL.
     * @return Danh sách các đối tượng LoaiVe
     */
    public List<LoaiVe> getAllLoaiVe() {
        List<LoaiVe> danhSachLoaiVe = new ArrayList<>();
        // Đã thêm TuoiMin, TuoiMax vào câu truy vấn
        String sql = "SELECT maLoaiVe, TenLoaiVe, MucGiamGia, TuoiMin, TuoiMax FROM LoaiVe";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LoaiVe loaiVe = new LoaiVe();
                loaiVe.setMaLoaiVe(rs.getString("maLoaiVe"));
                loaiVe.setTenLoai(rs.getString("TenLoaiVe"));
                loaiVe.setMucGiamGia(rs.getDouble("MucGiamGia"));
                // --- THÊM TRƯỜNG MỚI ---
                loaiVe.setTuoiMin(rs.getInt("TuoiMin"));
                loaiVe.setTuoiMax(rs.getInt("TuoiMax"));

                danhSachLoaiVe.add(loaiVe);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn CSDL để lấy tất cả LoaiVe: " + e.getMessage());
        }
        return danhSachLoaiVe;
    }


    /**
     * Cập nhật thông tin của một loại vé đã tồn tại (dựa vào MaLoaiVe).
     */
    public boolean updateLoaiVe(LoaiVe loaiVe) {
        if (loaiVe == null || loaiVe.getMaLoaiVe() == null) {
            return false;
        }

        String sql = "UPDATE LoaiVe SET TenLoaiVe = ?, MucGiamGia = ?, TuoiMin = ?, TuoiMax = ? WHERE maLoaiVe = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, loaiVe.getTenLoai());
            stmt.setDouble(2, loaiVe.getMucGiamGia());
            stmt.setInt(3, loaiVe.getTuoiMin());
            stmt.setInt(4, loaiVe.getTuoiMax());
            stmt.setString(5, loaiVe.getMaLoaiVe()); // Mã vé dùng cho điều kiện WHERE

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật Loại Vé: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một loại vé theo Mã.
     */
}