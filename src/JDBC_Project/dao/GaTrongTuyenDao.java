package JDBC_Project.dao;

import database.ConnectDB;
import entity.ChuyenTau;
import entity.GaTrongTuyen;
import entity.Tuyen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho Entity GaTrongTuyen.
 * Đã sửa đổi để sử dụng kiểu INT (số phút) cho các trường thời gian.
 */
public class GaTrongTuyenDao {
    private TuyenDao tuyenDao;

    public GaTrongTuyenDao() {
        tuyenDao = new TuyenDao();
    }


    /**
     * Lấy danh sách GaTrongTuyen theo Mã Tuyến. (SỬA: Đọc INT)
     * @param maTuyen Mã tuyến cần truy vấn.
     * @return Danh sách GaTrongTuyen.
     */
    public List<GaTrongTuyen> layGaTrongTuyenTheoMa(String maTuyen) throws SQLException {
        List<GaTrongTuyen> danhSach = new ArrayList<>();

        // Lấy thông tin Tuyen TRƯỚC khi mở kết nối cho GaTrongTuyen
        Tuyen tuyen = tuyenDao.layTuyenTheoMa(maTuyen);
        if (tuyen == null) return danhSach;

        String sql = "SELECT * FROM GA_TRONG_TUYEN WHERE MaTuyen = ? ORDER BY ThuTuGa ASC";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maTuyen);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    GaTrongTuyen gtt = new GaTrongTuyen(
                            tuyen,
                            rs.getString("MaGa"),
                            rs.getInt("ThuTuGa"),
                            rs.getInt("KhoangCachTichLuy"),
                            rs.getInt("ThoiGianDiChuyenToiGaTiepTheo"),
                            rs.getInt("ThoiGianDung")
                    );
                    danhSach.add(gtt);
                }
            }
        }
        return danhSach;
    }
    public void checkDatabaseLogic() {
        try {
            Tuyen t = tuyenDao.layTuyenTheoMa("SE1"); // Giả sử mã T01 có trong DB
            if (t != null) {
                System.out.println("✅ CSDL Hoạt động tốt: Lấy được tuyến " + t.getTenTuyen());
            } else {
                System.out.println("❓ CSDL Hoạt động: Nhưng không tìm thấy mã tuyến.");
            }
        } catch (SQLException e) {
            System.err.println("❌ CSDL Lỗi: Kết nối thất bại hoặc SQL sai.");
            e.printStackTrace();
        }
    }



    public static int tinhKhoangCachGiuaHaiGa(String maTuyen, String maGaDi, String maGaDen) throws SQLException {
        String sql = "SELECT MaGa, KhoangCachTichLuy FROM GA_TRONG_TUYEN " +
                "WHERE MaTuyen = ? AND (MaGa = ? OR MaGa = ?)";

        int dist1 = -1;
        int dist2 = -1;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maTuyen);
            pstmt.setString(2, maGaDi);
            pstmt.setString(3, maGaDen);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Sử dụng trim() để tránh lỗi nếu DB định dạng kiểu CHAR có khoảng trắng dư
                    String currentMaGa = rs.getString("MaGa").trim();
                    int dist = rs.getInt("KhoangCachTichLuy");

                    if (currentMaGa.equalsIgnoreCase(maGaDi.trim())) {
                        dist1 = dist;
                    }
                    if (currentMaGa.equalsIgnoreCase(maGaDen.trim())) {
                        dist2 = dist;
                    }
                }
            }
        }

        // 2. Kiểm tra kết quả
        if (dist1 != -1 && dist2 != -1) {
            return Math.abs(dist1 - dist2);
        } else {
            // In log để debug khi không tìm thấy
            System.out.println("Lỗi: Không tìm thấy đủ ga. Dist1: " + dist1 + ", Dist2: " + dist2);
            throw new IllegalArgumentException("Không tìm thấy dữ liệu cho Tuyến: " + maTuyen + " từ " + maGaDi + " đến " + maGaDen);
        }
    }

    /**
     * Thêm một GaTrongTuyen mới. (SỬA: Gán INT)
     */
    public boolean themGaTrongTuyen(GaTrongTuyen gtt) throws SQLException {
        String sql = "INSERT INTO GA_TRONG_TUYEN (MaTuyen, MaGa, ThuTuGa, KhoangCachTichLuy, ThoiGianDiChuyenToiGaTiepTheo, ThoiGianDung) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectDB.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, gtt.getTuyen().getMaTuyen());
            pst.setString(2, gtt.getMaGa());
            pst.setInt(3, gtt.getThuTuGa());
            pst.setInt(4, gtt.getKhoangCachTichLuy());
            // SỬA: Gán giá trị INT cho PreparedStatement
            pst.setInt(5, gtt.getThoiGianDiDenGaTiepTheo());
            pst.setInt(6, gtt.getThoiGianDung());
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật thông tin GaTrongTuyen. (SỬA: Gán INT)
     */
    public boolean capNhatGaTrongTuyen(GaTrongTuyen gtt) throws SQLException {
        String sql = "UPDATE GA_TRONG_TUYEN SET ThuTuGa = ?, KhoangCachTichLuy = ?, ThoiGianDiChuyenToiGaTiepTheo = ?, ThoiGianDung = ? " +
                "WHERE MaTuyen = ? AND MaGa = ?";

        try (Connection con = ConnectDB.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, gtt.getThuTuGa());
            pst.setInt(2, gtt.getKhoangCachTichLuy());
            // SỬA: Gán giá trị INT cho PreparedStatement
            pst.setInt(3, gtt.getThoiGianDiDenGaTiepTheo());
            pst.setInt(4, gtt.getThoiGianDung());
            pst.setString(5, gtt.getTuyen().getMaTuyen());
            pst.setString(6, gtt.getMaGa());
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Xóa một Ga khỏi Tuyến. (Không cần sửa đổi)
     */
    public boolean xoaGaTrongTuyen(String maTuyen, String maGa) throws SQLException {
        String sql = "DELETE FROM GA_TRONG_TUYEN WHERE MaTuyen = ? AND MaGa = ?";

        try (Connection con = ConnectDB.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, maTuyen);
            pst.setString(2, maGa);
            return pst.executeUpdate() > 0;
        }
    }
}