package JDBC_Project.dao;

import database.ConnectDB;
import entity.HoaDon;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HoaDonDAO {

    /**
     * Thêm thực thể HoaDon mới vào CSDL.
     * Phương thức này được thiết kế để chạy trong một giao dịch (transaction)
     * và phải nhận Connection đã có sẵn.
     * * @param conn Kết nối CSDL (đang mở transaction).
     * @param hoaDon Đối tượng HoaDon cần thêm.
     * @return true nếu thêm thành công.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public boolean themHoaDon(Connection conn, HoaDon hoaDon) throws SQLException {
        // Cần khớp với 8 cột trong bảng HoaDon: MaHD, MaKhachHang, MaNVLap, MaKM, TongTien, NgayLap, PhuongThuc, LoaiHoaDon
        String sql = "INSERT INTO HoaDon (MaHD, MaKhachHang, MaNVLap, MaKM, TongTien, NgayLap, PhuongThuc, LoaiHoaDon) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Sử dụng PreparedStatement của kết nối giao dịch (conn)
        PreparedStatement pstmt = null;
        try {
                pstmt = conn.prepareStatement(sql);
            // Chuyển đổi java.time.LocalDateTime sang java.sql.Timestamp
            Timestamp ngayLapTimestamp = (hoaDon.getNgayLap() != null)
                    ? Timestamp.valueOf(hoaDon.getNgayLap())
                    : null;

            pstmt.setString(1, hoaDon.getMaHD());
            pstmt.setString(2, hoaDon.getMaKhachHang());
            pstmt.setString(3, hoaDon.getMaNVLap());

            // Xử lý MaKM có thể là NULL
            if (hoaDon.getMaKM() != null && !hoaDon.getMaKM().isEmpty()) {
                pstmt.setString(4, hoaDon.getMaKM());
            } else {
                pstmt.setNull(4, Types.NVARCHAR); // Đặt giá trị NULL SQL
            }

            pstmt.setDouble(5, hoaDon.getTongTien());
            pstmt.setTimestamp(6, ngayLapTimestamp);
            pstmt.setString(7, hoaDon.getPhuongThuc());
            pstmt.setString(8, hoaDon.getLoaiHoaDon());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm Hóa đơn (MaHD: " + hoaDon.getMaHD() + "): " + e.getMessage());
            throw e;
        } finally {
            // Đóng PreparedStatement
            if (pstmt != null) pstmt.close();
        }
    }


    /**
     * Truy vấn MaHD lớn nhất bắt đầu bằng một tiền tố dài (HD[CC][YYMMDD][MaNV]...).
     * @param maHdPatternPrefixDayDu Tiền tố đầy đủ của MaHD
     * @return MaHD lớn nhất tìm được, hoặc null nếu không tìm thấy.
     * @throws SQLException
     */
    // Trong HoaDonDAO.java
    public static String getLastMaHoaDonByPrefix(String maHdPatternPrefixDayDu) throws SQLException {
        String lastMaHD = null;

        // Sử dụng tiền tố đầy đủ (VD: HD012810250001) và thêm ký tự đại diện (%)
        String fixedPrefix = maHdPatternPrefixDayDu + "%";

        String sql = "SELECT TOP 1 MaHD FROM HoaDon WHERE MaHD LIKE ? ORDER BY MaHD DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fixedPrefix);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lastMaHD = rs.getString("MaHD"); // Lấy mã đầy đủ (VD: HD0128102500010004)
                }
            }
        }
        return lastMaHD;
    }

    // Tìm hóa đơn theo mã hóa đơn
    public static HoaDon timHoaDonTheoMa(String giaTriTimKiem) {
        HoaDon hd;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // Chọn tất cả các cột cần thiết từ bảng HoaDon (HD)
        String selectColumns = "SELECT HD.MaHD, HD.NgayLap, HD.MaKhachHang, HD.MaNVLap, HD.MaKM, HD.TongCong, HD.TongTien, HD.PhuongThuc, HD.LoaiHoaDon, HD.MaHD_Goc ";
        String sql = selectColumns + "FROM HoaDon HD WHERE HD.MaHD = ?";

        try {
            con = ConnectDB.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, giaTriTimKiem); // Tìm chính xác MaHD

            // -- Thực thi và xử lý kết quả --
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String maHoaDon = rs.getString("MaHD");
                Timestamp ngayLapTimestamp = rs.getTimestamp("NgayLap");
                String maKhachHang = rs.getString("MaKhachHang");
                String nhanVienLap = rs.getString("MaNVLap");
                String maKM = rs.getString("MaKM");
                double tongCong = rs.getDouble("TongCong"); // SQL TongCong -> Java tongCong
                double tongTien = rs.getDouble("TongTien");   // SQL TongTien -> Java tongTien
                String phuongThucThanhToan = rs.getString("PhuongThuc");
                String loaiHoaDon = rs.getString("LoaiHoaDon");
                String maHD_Goc = rs.getString("MaHD_Goc");

                LocalDateTime ngayLap = (ngayLapTimestamp != null) ? ngayLapTimestamp.toLocalDateTime() : null;

                // Sử dụng constructor đầy đủ (nếu có) hoặc constructor mặc định + setters
                // Giả sử bạn đã có constructor đầy đủ như đã tạo ở lần trước
                hd = new HoaDon(
                        maHoaDon,
                        ngayLap,
                        maKhachHang,
                        nhanVienLap,
                        maKM,
                        tongCong,
                        tongTien,
                        phuongThucThanhToan,
                        maHD_Goc,
                        loaiHoaDon
                );
                return hd;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm hóa đơn (" + giaTriTimKiem + "): " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng tài nguyên
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                // Không đóng Connection ở đây, để ConnectDB quản lý
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<HoaDon> timHoaDonTheoSDT(String giaTriTimKiem) {
        ArrayList<HoaDon> danhSach = new ArrayList<>();
        String sql = """
        SELECT HD.MaHD, HD.MaKhachHang, HD.NgayLap, HD.TongTien, HD.LoaiHoaDon
        FROM HoaDon HD
        JOIN KhachHang KH ON HD.MaKhachHang = KH.MaKhachHang
        WHERE KH.SoDienThoai = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("MaHD"));
                hd.setMaKhachHang(rs.getString("MaKhachHang"));
                hd.setNgayLap(rs.getTimestamp("NgayLap").toLocalDateTime());
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setLoaiHoaDon(rs.getString("LoaiHoaDon"));
                danhSach.add(hd);
            }
            return danhSach;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<HoaDon> timHoaDonTheoCCCD(String giaTriTimKiem) {
        ArrayList<HoaDon> danhSach = new ArrayList<>();
        String sql = """
        SELECT HD.MaHD, HD.MaKhachHang, HD.NgayLap, HD.TongTien, HD.LoaiHoaDon
        FROM HoaDon HD
        JOIN KhachHang KH ON HD.MaKhachHang = KH.MaKhachHang
        WHERE KH.CCCD = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("MaHD"));
                hd.setMaKhachHang(rs.getString("MaKhachHang"));
                hd.setNgayLap(rs.getTimestamp("NgayLap").toLocalDateTime());
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setLoaiHoaDon(rs.getString("LoaiHoaDon"));
                danhSach.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }


    // Lọc theo Số Điện Thoại + Tháng + Năm
    public static ArrayList<HoaDon> timHoaDonTheoSDTLocThangNam(String sdt, int thang, int nam) {
        ArrayList<HoaDon> danhSach = new ArrayList<>();
        String sql = """
            SELECT HD.MaHD, HD.NgayLap, HD.MaKhachHang, HD.MaNVLap, HD.MaKM, HD.TongCong, HD.TongTien, HD.PhuongThuc, HD.LoaiHoaDon, HD.MaHD_Goc
            FROM HoaDon HD
            JOIN KhachHang KH ON HD.MaKhachHang = KH.MaKhachHang
            WHERE KH.SoDienThoai = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
            """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sdt);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSach.add(taoHoaDonTuResultSet(rs)); // Gọi hàm helper
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }

    // Lọc theo CCCD + Tháng + Năm
    public static ArrayList<HoaDon> timHoaDonTheoCCCDLocThangNam(String cccd, int thang, int nam) {
        ArrayList<HoaDon> danhSach = new ArrayList<>();
        String sql = """
            SELECT HD.MaHD, HD.NgayLap, HD.MaKhachHang, HD.MaNVLap, HD.MaKM, HD.TongCong, HD.TongTien, HD.PhuongThuc, HD.LoaiHoaDon, HD.MaHD_Goc
            FROM HoaDon HD
            JOIN KhachHang KH ON HD.MaKhachHang = KH.MaKhachHang
            WHERE KH.CCCD = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
            """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSach.add(taoHoaDonTuResultSet(rs)); // Gọi hàm helper
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }

    // Hàm helper để tạo đối tượng HoaDon từ ResultSet (tránh lặp code)
    private static HoaDon taoHoaDonTuResultSet(ResultSet rs) throws SQLException {
        String maHoaDon = rs.getString("MaHD");
        Timestamp ngayLapTimestamp = rs.getTimestamp("NgayLap");
        String maKhachHang = rs.getString("MaKhachHang");
        String nhanVienLap = rs.getString("MaNVLap");
        String maKM = rs.getString("MaKM");
        double tongCong = rs.getDouble("TongCong");
        double tongTien = rs.getDouble("TongTien");
        String phuongThucThanhToan = rs.getString("PhuongThuc");
        String loaiHoaDon = rs.getString("LoaiHoaDon");
        String maHD_Goc = rs.getString("MaHD_Goc");
        LocalDateTime ngayLap = (ngayLapTimestamp != null) ? ngayLapTimestamp.toLocalDateTime() : null;

        // Giả sử có constructor đầy đủ
        return new HoaDon(maHoaDon, ngayLap, maKhachHang, nhanVienLap, maKM, tongCong, tongTien, phuongThucThanhToan, maHD_Goc, loaiHoaDon);
    }


}
