package JDBC_Project.dao;

import database.ConnectDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    /**
     * Lấy thống kê doanh thu và số vé của 1 nhân viên trong ngày hôm nay
     * @param maNV Mã nhân viên đang đăng nhập
     * @return Một Map chứa "soVe" và "doanhThu"
     */
    public Map<String, Object> getThongKeTrongNgay(String maNV) {
        Map<String, Object> ketQua = new HashMap<>();
        // Mặc định nếu không có dữ liệu
        ketQua.put("soVe", 0);
        ketQua.put("doanhThu", 0.0);

        String sql = "SELECT COUNT(cthd.MaVe) AS SoVe, SUM(hd.TongTien) AS DoanhThu " +
                "FROM HoaDon hd " +
                "JOIN ChiTietHoaDon cthd ON hd.MaHD = cthd.MaHD " +
                "WHERE hd.MaNVLap = ? AND CAST(hd.NgayLap AS DATE) = CAST(GETDATE() AS DATE)";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ketQua.put("soVe", rs.getInt("SoVe"));
                ketQua.put("doanhThu", rs.getDouble("DoanhThu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    /**
     * Lấy danh sách 5 chuyến tàu sắp khởi hành trong ngày
     */
    public List<String[]> getChuyenTauSapChay() {
        List<String[]> ds = new ArrayList<>();
        String sql = "SELECT TOP 5 ct.MaChuyenTau, gDi.TenGa, gDen.TenGa, ct.GioKhoiHanh " +
                "FROM ChuyenTau ct " +
                "JOIN Ga gDi ON ct.GaDi = gDi.MaGa " +
                "JOIN Ga gDen ON ct.GaDen = gDen.MaGa " +
                "WHERE ct.NgayKhoiHanh = CAST(GETDATE() AS DATE) " +
                "AND ct.GioKhoiHanh >= CAST(GETDATE() AS TIME) " +
                "ORDER BY ct.GioKhoiHanh ASC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(new String[]{
                        rs.getString(1), // Mã chuyến
                        rs.getString(2), // Ga đi
                        rs.getString(3), // Ga đến
                        rs.getString(4)  // Giờ chạy
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Lấy các chương trình khuyến mãi đang còn hiệu lực
     */
    public List<Map<String, String>> getKhuyenMaiHienNay() {
        List<Map<String, String>> ds = new ArrayList<>();
        String sql = "SELECT TenKM, DKApDung, GiaTriGiam FROM KhuyenMai " +
                "WHERE TrangThai = 'HOAT_DONG' " +
                "AND GETDATE() BETWEEN NgayBD AND NgayKT";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, String> km = new HashMap<>();
                km.put("ten", rs.getString("TenKM"));
                km.put("dieukien", rs.getString("DKApDung"));
                km.put("giamgia", rs.getString("GiaTriGiam"));
                ds.add(km);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public List<Map<String, Object>> getHoaDonGanDay(String maNV) {
        List<Map<String, Object>> ds = new ArrayList<>();
        // Join với bảng Khách hàng để lấy tên khách cho đẹp
        String sql = "SELECT TOP 5 h.MaHD, k.HoTen, h.TongTien, h.NgayLap " +
                "FROM HoaDon h " +
                "JOIN KhachHang k ON h.MaKhachHang = k.MaKhachHang " +
                "WHERE h.MaNVLap = ? " +
                "ORDER BY h.NgayLap DESC"; // Mới nhất lên đầu

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> hd = new HashMap<>();
                hd.put("maHD", rs.getString("MaHD"));
                hd.put("tenKH", rs.getString("HoTen"));
                hd.put("tongTien", rs.getDouble("TongTien"));
                hd.put("ngayLap", rs.getTimestamp("NgayLap"));
                ds.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}