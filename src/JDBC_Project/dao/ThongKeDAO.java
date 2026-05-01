package JDBC_Project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import database.ConnectDB; // Đảm bảo project đã có class kết nối này
import database.ConnectDB;

public class ThongKeDAO {

    /**
     * Lấy thống kê số lượng vé bán/trả tuần này và % tăng trưởng so với tuần trước
     */
    public Map<String, Double> getThongKeTuan() {
        Map<String, Double> result = new HashMap<>();
        // SQL: So sánh dữ liệu tuần này (WeeksAgo=0) và tuần trước (WeeksAgo=1)
        String sql = "WITH WeeklyStats AS ( " +
                "    SELECT " +
                "        DATEDIFF(WEEK, h.NgayLap, GETDATE()) AS WeeksAgo, " +
                "        COUNT(CASE WHEN v.TrangThai = 'DA_BAN' THEN 1 END) AS VeDaBan, " +
                "        COUNT(CASE WHEN v.TrangThai = 'DA-HUY' OR h.LoaiHoaDon = N'Trả vé' THEN 1 END) AS VeDaTra " +
                "    FROM HoaDon h " +
                "    JOIN ChiTietHoaDon cthd ON h.MaHD = cthd.MaHD " +
                "    JOIN Ve v ON cthd.MaVe = v.MaVe " +
                "    WHERE h.NgayLap >= DATEADD(WEEK, -2, GETDATE()) " +
                "    GROUP BY DATEDIFF(WEEK, h.NgayLap, GETDATE()) " +
                ") " +
                "SELECT " +
                "    ISNULL(Curr.VeDaBan, 0) AS BanTuanNay, " +
                "    ISNULL(Curr.VeDaTra, 0) AS TraTuanNay, " +
                "    ISNULL(Prev.VeDaBan, 0) AS BanTuanTruoc, " +
                "    ISNULL(Prev.VeDaTra, 0) AS TraTuanTruoc " +
                "FROM (SELECT * FROM WeeklyStats WHERE WeeksAgo = 0) Curr " +
                "FULL OUTER JOIN (SELECT * FROM WeeklyStats WHERE WeeksAgo = 1) Prev ON 1=1";

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double banNay = rs.getDouble("BanTuanNay");
                double traNay = rs.getDouble("TraTuanNay");
                double banTruoc = rs.getDouble("BanTuanTruoc");

                result.put("BanTuanNay", banNay);
                result.put("TraTuanNay", traNay);

                // Tính % tăng trưởng (tránh chia cho 0)
                double tangTruongBan = (banTruoc == 0) ? (banNay > 0 ? 100.0 : 0.0) : ((banNay - banTruoc) / banTruoc) * 100.0;
                result.put("TangTruongBan", tangTruongBan);
            }
            rs.close(); stmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    /**
     * Lấy số lượng vé theo loại trong 7 ngày qua (Dùng LEFT JOIN để hiện cả loại vé số lượng 0)
     */
    public Map<String, Integer> getSoLuongTheoLoaiVe() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT lv.TenLoaiVe, COUNT(v.MaVe) AS SoLuong " +
                "FROM LoaiVe lv " +
                "LEFT JOIN Ve v ON lv.MaLoaiVe = v.MaLoaiVe " +
                "     AND v.TrangThai = 'DA_BAN' " +
                "     AND v.MaVe IN ( " +
                "         SELECT cthd.MaVe FROM ChiTietHoaDon cthd " +
                "         JOIN HoaDon h ON cthd.MaHD = h.MaHD " +
                "         WHERE h.NgayLap >= DATEADD(DAY, -7, GETDATE()) " +
                "     ) " +
                "GROUP BY lv.TenLoaiVe";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("TenLoaiVe"), rs.getInt("SoLuong"));
            }
            rs.close(); stmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    /**
     * Tính tỷ lệ lấp đầy CHỈ TRONG NGÀY HÔM NAY
     * Công thức: (Tổng vé bán các chuyến chạy hôm nay / Tổng ghế các chuyến chạy hôm nay) * 100
     */
    public double getTyLeLapDayHomNay() {
        double tyLe = 0.0;
        String sql =
                "DECLARE @Today DATE = CAST(GETDATE() AS DATE); " +
                        "SELECT " +
                        "    (SELECT COUNT(*) FROM Ve v " +
                        "     JOIN ChuyenTau ct ON v.MaChuyenTau = ct.MaChuyenTau " +
                        "     WHERE v.TrangThai = 'DA_BAN' AND ct.NgayKhoiHanh = @Today) AS SoVeDaBan, " +
                        "    (SELECT COUNT(cd.MaCho) FROM ChuyenTau ct " +
                        "     JOIN Toa t ON ct.MaTau = t.SoHieuTau " +
                        "     JOIN ChoDat cd ON t.MaToa = cd.MaToa " +
                        "     WHERE ct.NgayKhoiHanh = @Today) AS TongSoGheCungUng";

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int veBan = rs.getInt("SoVeDaBan");
                int tongGhe = rs.getInt("TongSoGheCungUng");

                // Debug kiểm tra
                // System.out.println("Hôm nay: Bán " + veBan + "/" + tongGhe + " ghế");

                if (tongGhe > 0) {
                    tyLe = ((double) veBan / tongGhe) * 100.0;
                }
            }
            rs.close(); stmt.close();
        } catch (Exception e) { e.printStackTrace(); }
        return tyLe;
    }
}