package JDBC_Project.control;

import dao.*;
import database.ConnectDB;
import entity.*;
import entity.DTO.ThongTinVeDTO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XuLyVeDoi {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO cthdDAO = new ChiTietHoaDonDAO();
    private final VeDAO veDAO = new VeDAO();

    // Class con để trả về kết quả giao dịch cho UI cập nhật
    public static class KetQuaGiaoDich {
        public boolean thanhCong;
        public String maHD;
        public List<Ve> danhSachVeMoi;
        public String loiNhan;
    }

    public KetQuaGiaoDich thucHienDoiVe(List<ThongTinVeDTO> listVeCu,
                                        Map<String, ChoDat> mapGheMoi,
                                        Map<String, Long> mapGiaMoi,
                                        String maNV,
                                        long tongChenhLech,
                                        String phuongThucThanhToan) throws SQLException { // Thêm tham số phương thức TT

        KetQuaGiaoDich kq = new KetQuaGiaoDich();
        kq.danhSachVeMoi = new ArrayList<>();

        Connection conn = null;

        LocalDate homNay = LocalDate.now();
        String soHieuCa = "01";
        String lastMaVeSTT = veDAO.getLastSoThuTuTrongCa(soHieuCa, homNay);
        int currentMaxVe = parseSTT(lastMaVeSTT);

        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false);

            // 1. TẠO HÓA ĐƠN
            String maHD = taoMaHoaDon(conn, maNV);
            String maKhachHang = listVeCu.get(0).getMaKhachHang();

            HoaDon hoaDon = new HoaDon(
                    maHD,
                    maKhachHang,
                    maNV,
                    null,
                    (double) tongChenhLech,
                    LocalDateTime.now(),
                    phuongThucThanhToan, // <--- Lấy đúng từ ComboBox UI truyền xuống
                    "Đổi vé"
            );

            if (!hoaDonDAO.themHoaDon(conn, hoaDon)) {
                throw new SQLException("Lỗi tạo hóa đơn: " + maHD);
            }

            // 2. XỬ LÝ VÉ CŨ
            String sqlUpdateVeCu = "UPDATE Ve SET TrangThai = ? WHERE MaVe = ?";
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateVeCu)) {
                for (ThongTinVeDTO veCu : listVeCu) {
                    pstmtUpdate.setString(1, "DA_HUY");
                    pstmtUpdate.setString(2, veCu.getMaVe());
                    pstmtUpdate.executeUpdate();

                    ChiTietHoaDon cthdAm = new ChiTietHoaDon(maHD, veCu.getMaVe(), -1, veCu.getGiaVe());
                    cthdDAO.themChiTietHoaDon(conn, cthdAm);
                }
            }

            // 3. XỬ LÝ VÉ MỚI
            int count = 1;
            for (ThongTinVeDTO veCu : listVeCu) {
                ChoDat gheMoi = mapGheMoi.get(veCu.getMaVe());
                Long giaMoi = mapGiaMoi.get(veCu.getMaVe());

                if (gheMoi != null && giaMoi != null) {
                    String maVeMoi = veDAO.taoMaVeMoi(currentMaxVe + count, soHieuCa);
                    count++;

                    Ve veMoi = new Ve(
                            maVeMoi,
                            veCu.getMaChuyenTau(),
                            gheMoi.getMaCho(),
                            maNV,
                            veCu.getMaKhachHang(),
                            veCu.getMaLoaiVe(),
                            (double) giaMoi,
                            "DA_BAN"
                    );

                    if (!insertVeMoi(conn, veMoi)) throw new SQLException("Lỗi thêm vé mới");

                    ChiTietHoaDon cthdDuong = new ChiTietHoaDon(maHD, maVeMoi, 1, (double) giaMoi);
                    cthdDAO.themChiTietHoaDon(conn, cthdDuong);

                    // Lưu vé mới vào kết quả để trả về UI
                    kq.danhSachVeMoi.add(veMoi);
                }
            }

            conn.commit();
            kq.thanhCong = true;
            kq.maHD = maHD; // Trả về Mã HĐ thật

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            kq.thanhCong = false;
            kq.loiNhan = e.getMessage();
            throw e;
        } finally {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }

        return kq;
    }

    // ... (Giữ nguyên các hàm private insertVeMoi, taoMaHoaDon, parseSTT như cũ) ...
    private boolean insertVeMoi(Connection conn, Ve ve) throws SQLException {
        String sql = "INSERT INTO Ve (MaVe, MaChuyenTau, MaChoDat, MaNV, MaKhachHang, MaLoaiVe, GiaVe, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ve.getMaVe());
            pstmt.setString(2, ve.getMaChuyenTau());
            pstmt.setString(3, ve.getMaChoDat());
            pstmt.setString(4, ve.getMaNV());
            pstmt.setString(5, ve.getMaKhachHang());
            pstmt.setString(6, ve.getMaLoaiVe());
            pstmt.setDouble(7, ve.getGiaVe());
            pstmt.setString(8, ve.getTrangThai());
            return pstmt.executeUpdate() > 0;
        }
    }

    private String taoMaHoaDon(Connection conn, String maNV) throws SQLException {
        String soHieuCa = "01";
        String ngayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        String maNVRutGon = (maNV != null && maNV.length() >= 4) ? maNV.substring(maNV.length() - 4) : "0000";
        String prefix = "HD" + soHieuCa + ngayStr + maNVRutGon;
        String sql = "SELECT TOP 1 MaHD FROM HoaDon WHERE MaHD LIKE ? ORDER BY MaHD DESC";
        String lastMaHD = null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prefix + "%");
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) lastMaHD = rs.getString("MaHD"); }
        }
        int nextSTT = 1;
        if (lastMaHD != null) {
            try { nextSTT = Integer.parseInt(lastMaHD.substring(lastMaHD.length() - 4)) + 1; } catch (Exception e) {}
        }
        return prefix + String.format("%04d", nextSTT);
    }

    private int parseSTT(String sttStr) {
        try { return Integer.parseInt(sttStr); } catch (Exception e) { return 0; }
    }
}