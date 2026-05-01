package JDBC_Project.dao.DTO;

import entity.DTO.ThongTinVeDTO;
import database.ConnectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThongTinVeDTODAO {

    // --- SỬA QUERY: THÊM KH.MaKhachHang VÀ V.MaLoaiVe ---
    private static final String BASE_QUERY =
            "SELECT V.MaVe, L.TenLoaiVe, V.MaLoaiVe, " + // Thêm V.MaLoaiVe
                    "       KH.HoTen, KH.CCCD, KH.SoDienThoai, KH.NgaySinh, KH.MaKhachHang, " + // Thêm KH.MaKhachHang
                    "       V.MaChuyenTau, " +
                    "       G1.TenGa AS TenGaDi, CT.GaDi AS MaGaDi, " +
                    "       G2.TenGa AS TenGaDen, CT.GaDen AS MaGaDen, " +
                    "       NgayKhoiHanh, GioKhoiHanh, NgayDenDuKien, GioDenDuKien, " +
                    "       SoHieuTau, T.MaToa, LT.TenLoaiToa, SoCho, Khoang, Tang, GiaVe, V.TrangThai " +
                    "FROM KhachHang KH " +
                    "JOIN Ve V ON KH.MaKhachHang = V.MaKhachHang " +
                    "JOIN ChoDat CD ON V.MaChoDat = CD.MaCho " +
                    "JOIN ChuyenTau CT ON CT.MaChuyenTau = V.MaChuyenTau " +
                    "JOIN Toa T ON CD.MaToa = T.MaToa " +
                    "JOIN Tau TR ON TR.SoHieu = T.SoHieuTau " +
                    "JOIN LoaiVe L ON V.MaLoaiVe = L.MaLoaiVe " +
                    "JOIN Ga G1 ON CT.GaDi = G1.MaGa " +
                    "JOIN Ga G2 ON CT.GaDen = G2.MaGa " +
                    "JOIN LoaiToa LT ON T.MaLoaiToa = LT.MaLoaiToa ";

    public ThongTinVeDTO getVeByMaVe(String maVe) {
        ThongTinVeDTO ve = null;
        String sql = BASE_QUERY + " WHERE V.MaVe = ?";
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maVe);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) ve = mapToDTO(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return ve;
    }

    public List<ThongTinVeDTO> getVeBySDT(String sdt) {
        List<ThongTinVeDTO> list = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE KH.SoDienThoai = ?";
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sdt);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapToDTO(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<ThongTinVeDTO> getVeByCCCD(String cccd) {
        List<ThongTinVeDTO> list = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE KH.CCCD = ?";
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapToDTO(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Query tìm theo hóa đơn (Cũng phải sửa thêm MaKhachHang, MaLoaiVe)
    public List<ThongTinVeDTO> getVeByMaHoaDon(String maHoaDon) {
        List<ThongTinVeDTO> list = new ArrayList<>();
        String sql =
                "SELECT " +
                        "    HD.MaHD, V.MaVe, L.TenLoaiVe, V.MaLoaiVe, " + // Thêm V.MaLoaiVe
                        "    KHV.HoTen, KHV.CCCD, KHV.SoDienThoai, KHV.NgaySinh, KHV.MaKhachHang, " + // Thêm KHV.MaKhachHang
                        "    V.MaChuyenTau, " +
                        "    G1.TenGa AS TenGaDi, CT.GaDi AS MaGaDi, " +
                        "    G2.TenGa AS TenGaDen, CT.GaDen AS MaGaDen, " +
                        "    CT.NgayKhoiHanh, CT.GioKhoiHanh, " +
                        "    CT.NgayDenDuKien, CT.GioDenDuKien, " +
                        "    TR.SoHieu AS SoHieuTau, " +
                        "    T.MaToa, LT.TenLoaiToa, " +
                        "    CD.SoCho, CD.Khoang, CD.Tang, " +
                        "    V.GiaVe, V.TrangThai " +
                        "FROM HoaDon HD " +
                        "JOIN KhachHang KHHD      ON KHHD.MaKhachHang = HD.MaKhachHang " +
                        "JOIN ChiTietHoaDon CTHD  ON HD.MaHD = CTHD.MaHD " +
                        "JOIN Ve V                ON V.MaVe = CTHD.MaVe " +
                        "JOIN KhachHang KHV       ON KHV.MaKhachHang = V.MaKhachHang " +
                        "JOIN LoaiVe L            ON L.MaLoaiVe = V.MaLoaiVe " +
                        "JOIN ChuyenTau CT        ON CT.MaChuyenTau = V.MaChuyenTau " +
                        "JOIN ChoDat CD           ON CD.MaCho = V.MaChoDat " +
                        "JOIN Toa T               ON T.MaToa = CD.MaToa " +
                        "JOIN Tau TR              ON TR.SoHieu = T.SoHieuTau " +
                        "JOIN LoaiToa LT ON T.MaLoaiToa = LT.MaLoaiToa " +
                        "JOIN Ga G1 ON CT.GaDi = G1.MaGa " +
                        "JOIN Ga G2 ON CT.GaDen = G2.MaGa " +
                        "WHERE HD.MaHD = ?";
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapToDTO(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<ThongTinVeDTO> getVeTheoSDTVaLoTrinh(String sdt, String tenGaDi, String tenGaDen, java.util.Date ngayKhoiHanh) {
        List<ThongTinVeDTO> list = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE KH.SoDienThoai = ? AND G1.TenGa = ? AND G2.TenGa = ? AND CT.NgayKhoiHanh = ?";
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sdt);
            pstmt.setString(2, tenGaDi);
            pstmt.setString(3, tenGaDen);
            pstmt.setDate(4, new Date(ngayKhoiHanh.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapToDTO(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<ThongTinVeDTO> getVeTheoCCCDVaLoTrinh(String cccd, String tenGaDi, String tenGaDen, java.util.Date ngayKhoiHanh) {
        List<ThongTinVeDTO> list = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE KH.CCCD = ? AND G1.TenGa = ? AND G2.TenGa = ? AND CT.NgayKhoiHanh = ?";
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            pstmt.setString(2, tenGaDi);
            pstmt.setString(3, tenGaDen);
            pstmt.setDate(4, new Date(ngayKhoiHanh.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapToDTO(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- CẬP NHẬT MAPPER ---
    private ThongTinVeDTO mapToDTO(ResultSet rs) throws SQLException {
        String trangThaiRaw = rs.getString("TrangThai");
        String trangThaiHienThi = trangThaiRaw;
        if ("DA_BAN".equalsIgnoreCase(trangThaiRaw)) trangThaiHienThi = "Đã bán";
        else if ("DA_HUY".equalsIgnoreCase(trangThaiRaw)) trangThaiHienThi = "Đã hủy";

        String rawMaToa = rs.getString("MaToa");
        String cleanMaToa = (rawMaToa != null && rawMaToa.contains("-")) ? rawMaToa.substring(rawMaToa.lastIndexOf("-") + 1) : rawMaToa;

        Date nsDate = rs.getDate("NgaySinh");
        java.time.LocalDate ngaySinh = (nsDate != null) ? nsDate.toLocalDate() : null;

        return new ThongTinVeDTO(
                rs.getString("MaVe"),
                rs.getString("TenLoaiVe"),
                rs.getString("HoTen"),
                rs.getString("CCCD"),
                rs.getString("SoDienThoai"),
                ngaySinh,
                rs.getString("MaChuyenTau"),
                rs.getString("TenGaDi"),
                rs.getString("TenGaDen"),
                rs.getDate("NgayKhoiHanh").toLocalDate(),
                rs.getTime("GioKhoiHanh").toLocalTime(),
                rs.getDate("NgayDenDuKien").toLocalDate(),
                rs.getTime("GioDenDuKien").toLocalTime(),
                rs.getString("SoHieuTau"),
                cleanMaToa,
                rs.getString("TenLoaiToa"),
                rs.getString("SoCho"),
                rs.getString("Khoang"),
                rs.getString("Tang"),
                rs.getDouble("GiaVe"),
                trangThaiHienThi,
                rs.getString("MaGaDi"),
                rs.getString("MaGaDen"),
                // Thêm 2 trường mới ở cuối
                rs.getString("MaKhachHang"),
                rs.getString("MaLoaiVe")
        );
    }
}
