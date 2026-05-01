package JDBC_Project.dao;

import database.ConnectDB;
import entity.ChoDat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChoDatDAO {

    /**
     * Lấy danh sách chỗ đặt theo Mã toa và Mã chuyến tàu.
     * ĐÃ SỬA LỖI: KHÔNG dùng try-with-resources cho Connection.
     */
    public List<ChoDat> getDanhSachChoDatByMaToaVaTrangThai(String maToa, String maChuyenTau) {
        List<ChoDat> danhSachChoDat = new ArrayList<>();

        String sql = "SELECT cd.MaCho, cd.MaToa, cd.SoCho, cd.Khoang, cd.Tang, "
                + "CASE WHEN v.MaVe IS NOT NULL THEN 1 ELSE 0 END AS DaDat "
                + "FROM ChoDat cd "
                + "LEFT JOIN Ve v ON cd.MaCho = v.MaChoDat "
                + "    AND v.MaChuyenTau = ? "
                + "    AND v.TrangThai = N'DA_BAN' "
                + "WHERE cd.MaToa = ? "
                + "ORDER BY cd.SoCho";

        Connection con = null; // KHAI BÁO BÊN NGOÀI
        try {
            con = ConnectDB.getConnection();

            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setString(1, maChuyenTau);
                pstmt.setString(2, maToa);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String maCho = rs.getString("MaCho");
                        String soCho = rs.getString("SoCho");
                        int khoang = rs.getInt("Khoang");

                        int tang = rs.getInt("Tang");
                        int daDatInt = rs.getInt("DaDat");

                        ChoDat choDat = new ChoDat(maCho, maToa, soCho, khoang, tang);
                        choDat.setDaDat(daDatInt == 1);

                        danhSachChoDat.add(choDat);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách chỗ đặt: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachChoDat;
    }

    /**
     * Lấy danh sách chỗ đặt theo phân chặng.
     * @param maToa
     * @param maChuyenTau
     * @param maGaDiSearch
     * @param maGaDenSearch
     * @return
     */
    public List<ChoDat> getDanhSachChoDatTheoPhanChanh(String maToa, String maChuyenTau, String maGaDiSearch, String maGaDenSearch) {
        List<ChoDat> danhSachChoDat = new ArrayList<>();

        // Tách mã tuyến (Ví dụ SE1)
        String maTuyen = maChuyenTau.split("_")[0];
        // Tách ngày gốc (Ví dụ 251220)
        String ngayGoc = maChuyenTau.split("_")[1];

        // Dùng StringBuilder để nối chuỗi an toàn hơn, tránh dính chữ
        String sql = "SELECT cd.MaCho, cd.MaToa, cd.SoCho, cd.Khoang, cd.Tang, " +
                "CASE WHEN EXISTS ( " +
                "    SELECT 1 FROM Ve v " +
                "    INNER JOIN ChuyenTau ct_booked ON v.MaChuyenTau = ct_booked.MaChuyenTau " +
                "    INNER JOIN GA_TRONG_TUYEN gtt_t_di ON ct_booked.GaDi = gtt_t_di.MaGa AND gtt_t_di.MaTuyen = ? " +
                "    INNER JOIN GA_TRONG_TUYEN gtt_t_den ON ct_booked.GaDen = gtt_t_den.MaGa AND gtt_t_den.MaTuyen = ? " +
                "    INNER JOIN GA_TRONG_TUYEN gtt_s_di ON ? = gtt_s_di.MaGa AND gtt_s_di.MaTuyen = ? " +
                "    INNER JOIN GA_TRONG_TUYEN gtt_s_den ON ? = gtt_s_den.MaGa AND gtt_s_den.MaTuyen = ? " +
                "    WHERE v.MaChoDat = cd.MaCho " +
                "    AND ct_booked.MaTuyen = ? " +
                "    AND v.MaChuyenTau LIKE ? " +
                "    AND v.TrangThai = N'DA_BAN' " +
                "    AND gtt_t_di.ThuTuGa < gtt_s_den.ThuTuGa " +
                "    AND gtt_t_den.ThuTuGa > gtt_s_di.ThuTuGa " +
                ") THEN 1 ELSE 0 END AS DaDat " +
                "FROM ChoDat cd " +
                "WHERE cd.MaToa = ? " +
                "ORDER BY cd.SoCho";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            // Phải truyền đúng 9 tham số tương ứng với 9 dấu '?' ở trên
            pstmt.setString(1, maTuyen);           // gtt_t_di.MaTuyen
            pstmt.setString(2, maTuyen);           // gtt_t_den.MaTuyen
            pstmt.setString(3, maGaDiSearch);      // gtt_s_di.MaGa
            pstmt.setString(4, maTuyen);           // gtt_s_di.MaTuyen
            pstmt.setString(5, maGaDenSearch);     // gtt_s_den.MaGa
            pstmt.setString(6, maTuyen);           // gtt_s_den.MaTuyen
            pstmt.setString(7, maTuyen);           // ct_booked.MaTuyen
            pstmt.setString(8, maTuyen + "_" + ngayGoc + "_%"); // v.MaChuyenTau LIKE
            pstmt.setString(9, maToa);             // cd.MaToa

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChoDat choDat = new ChoDat(
                            rs.getString("MaCho"),
                            rs.getString("MaToa"),
                            rs.getString("SoCho"),
                            rs.getInt("Khoang"),
                            rs.getInt("Tang")
                    );
                    choDat.setDaDat(rs.getInt("DaDat") == 1);
                    danhSachChoDat.add(choDat);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL tại line 104: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachChoDat;
    }

    /**
     * Tra cứu chi tiết Chỗ đặt bằng Mã Chỗ (MaCho).
     * ĐÃ SỬA LỖI: KHÔNG dùng try-with-resources cho Connection.
     */
    public static ChoDat getChoDatById(String maCho) {
        ChoDat cd = null;
        String sql = "SELECT MaCho, MaToa, SoCho, Khoang, Tang FROM ChoDat WHERE MaCho = ?";

        Connection con = null; // KHAI BÁO BÊN NGOÀI
        try {
            con = ConnectDB.getConnection();

            try (PreparedStatement pstmt = con.prepareStatement(sql)) { // Dùng try-with-resources cho pstmt

                pstmt.setString(1, maCho);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        cd = new ChoDat(
                                rs.getString("MaCho"),
                                rs.getString("MaToa"),
                                rs.getString("SoCho"),
                                rs.getInt("Khoang"),
                                rs.getInt("Tang")
                        );
                        cd.setDaDat(false);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Chỗ đặt theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return cd;
    }
}