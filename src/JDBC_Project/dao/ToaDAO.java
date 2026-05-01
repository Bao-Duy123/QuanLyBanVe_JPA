package JDBC_Project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.ConnectDB;
import entity.LoaiToa;
import entity.Toa; // Sử dụng tên Toa entity theo yêu cầu
import entity.Tau;

public class ToaDAO {

    // Phương thức LẤY DANH SÁCH TOA THEO MÃ TÀU (Cần thiết để biết các toa tàu có trong một chuyến tàu)
    // Lưu ý: Trong thiết kế DB của bạn, Toa liên kết với Tau (MaTau),
    // nên để tìm toa cho một chuyến tàu, ta cần tìm MaTau của chuyến tàu đó trước.
    public List<Toa> layToaTheoMaTau(String maTau) {
        List<Toa> danhSachToa = new ArrayList<>();

        // SQL Tối ưu: JOIN với bảng Tau để lấy thông tin (TrangThai) của tàu
        String sql = "SELECT T.MaToa, T.SoHieuTau, T.maLoaiToa, "
                + "TAU.TrangThai AS TrangThaiTau "
                + "FROM Toa T "
                + "LEFT JOIN Tau TAU ON T.SoHieuTau = TAU.SoHieu "
                + "WHERE T.SoHieuTau = ?";

        // SỬ DỤNG TRY-WITH-RESOURCES CHO CONNECTION
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maTau);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Lấy dữ liệu thô
                    String maToa = rs.getString("MaToa");
                    String maTauDB = rs.getString("SoHieuTau");
                    String loaiToaStr = rs.getString("maLoaiToa");

                    //lấy tên loại toa từ mã loại toa
                    LoaiToa loaiToa = LoaiToaDAO.getLoaiToaByMaLoaiToa(loaiToaStr);
                    String tenLoaiToa = loaiToa != null ? loaiToa.getTenLoaiCho() : "Không xác định";

                    // 2. TẠO đối tượng Tau (Tận dụng dữ liệu JOIN)
                    // KHÔNG gọi TauDAO.getTauById() nữa!
                    String trangThaiTau = rs.getString("TrangThaiTau");
                    Tau tau = new Tau(maTauDB, trangThaiTau);

                    // 3. Tạo đối tượng Toa
                    Toa toa = new Toa(maToa, tau, tenLoaiToa);
                    danhSachToa.add(toa);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách toa cho tàu " + maTau + ": ");
            e.printStackTrace();
        }

        return danhSachToa;
    }

    // Lấy thông tin một toa tàu theo Mã Toa
    public Toa getToaById(String maToa) {
        Toa toa = null;
        String sql = "SELECT MaToa, MaTau, maLoaiToa FROM Toa WHERE MaToa = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql);
        ) {
            pstmt.setString(1, maToa);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String maTauDB = rs.getString("SoHieuTau");
                    String loaiToaStr = rs.getString("maLoaiToa");

                    Tau tau = TauDAO.getTauById(maTauDB);

                    toa = new Toa(maToa, tau, loaiToaStr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toa;
    }



}