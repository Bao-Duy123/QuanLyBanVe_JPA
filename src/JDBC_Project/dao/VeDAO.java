package JDBC_Project.dao;

import database.ConnectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VeDAO {

    /**
     * Tra cứu chi tiết vé theo Mã vé hoặc SĐT khách hàng (cho màn hình Trả vé).
     *
     * @param maVe Mã vé
     * @param sdt Số điện thoại khách hàng
     * @return Đối tượng Ve đã nạp đầy đủ thông tin chi tiết (ChiTiet)
     */
    /**
     * Lấy danh sách 5 vé mới nhất có trạng thái 'Đã bán' để làm dữ liệu mẫu.
     */
    public List<Ve> getTop5VeMoiNhat() {
        List<Ve> danhSachVe = new ArrayList<>();
        // Thêm điều kiện TrangThai để lọc vé đã hủy
        String sql = "SELECT TOP 5 V.MaVe, V.GiaVe, V.TrangThai, V.MaKhachHang, V.MaChuyenTau, V.MaChoDat, V.MaLoaiVe, V.MaNV " +
                "FROM Ve V " +
                "WHERE V.TrangThai = N'DA_BAN' " + // Hãy chắc chắn giá trị này giống trong DB
                "ORDER BY V.MaVe DESC";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Ve ve = new Ve();
                ve.setMaVe(rs.getString("MaVe"));
                ve.setGiaVe(rs.getDouble("GiaVe"));
                ve.setTrangThai(rs.getString("TrangThai"));
                ve.setMaKhachHang(rs.getString("MaKhachHang"));
                ve.setMaChuyenTau(rs.getString("MaChuyenTau"));
                ve.setMaChoDat(rs.getString("MaChoDat"));
                ve.setMaLoaiVe(rs.getString("MaLoaiVe"));
                ve.setMaNV(rs.getString("MaNV"));

                danhSachVe.add(ve);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy dữ liệu mẫu: " + e.getMessage());
        } finally {
            ConnectDB.disconnect();
        }

        // Nạp chi tiết thực thể sau khi đóng kết nối chính để tránh lỗi "Connection is closed"
        for (Ve ve : danhSachVe) {
            if (ve.getMaKhachHang() != null) {
                KhachHang kh = KhachHangDAO.getKhachHangById(ve.getMaKhachHang());
                ve.setKhachHangChiTiet(kh);
                if (kh != null) ve.setTenKhachHang(kh.getHoTen());
            }
            if (ve.getMaChuyenTau() != null) {
                ve.setChuyenTauChiTiet(ChuyenTauDao.layChuyenTauBangMa(ve.getMaChuyenTau()));
            }
            if (ve.getMaChoDat() != null) {
                ve.setChoDatChiTiet(ChoDatDAO.getChoDatById(ve.getMaChoDat()));
            }
        }
        return danhSachVe;
    }
    public Ve getChiTietVeChoTraVe(String maVe, String sdt) {
        Ve ve = null;

        // Truy vấn cần JOIN để lấy thông tin chi tiết cho Khách hàng, Chuyến tàu, Chỗ đặt
        String sql = "SELECT V.MaVe, V.GiaVe, V.TrangThai, V.MaKhachHang, V.MaChuyenTau, V.MaChoDat, V.MaLoaiVe, " +
                "KH.HoTen AS TenKhachHang, KH.SoDienThoai, " +
                "CD.SoCho, T.MaToa " + // T.MaToa có thể dùng để kiểm tra tính đầy đủ
                "FROM Ve V " +
                "LEFT JOIN KhachHang KH ON V.MaKhachHang = KH.MaKhachHang " +
                "LEFT JOIN ChoDat CD ON V.MaChoDat = CD.MaCho " +
                "LEFT JOIN Toa T ON CD.MaToa = T.MaToa " + // Chuyến tàu không cần JOIN ở đây vì ta sẽ gọi DAO sau
                "WHERE (V.MaVe = ? OR KH.SoDienThoai = ?) AND V.TrangThai <> N'DA-HUY'";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, maVe != null && !maVe.isEmpty() ? maVe : "NULL_MAVE");
                pstmt.setString(2, sdt != null && !sdt.isEmpty() ? sdt : "NULL_SDT");

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ve = new Ve();

                        // 1. Gán thuộc tính chính
                        ve.setMaVe(rs.getString("MaVe")); // Sử dụng setMaVe thay vì setId
                        ve.setGiaVe(rs.getDouble("GiaVe")); // Sử dụng setGiaVe thay vì setGia
                        ve.setTrangThai(rs.getString("TrangThai"));

                        // 2. Gán Khóa ngoại
                        String maKHDb = rs.getString("MaKhachHang");
                        String maCTDb = rs.getString("MaChuyenTau");
                        String maChoDatDb = rs.getString("MaChoDat");

                        ve.setMaKhachHang(maKHDb);
                        ve.setMaChuyenTau(maCTDb);
                        ve.setMaChoDat(maChoDatDb);
                        // MaLoaiVe đã có trong Ve
                        ve.setMaLoaiVe(rs.getString("MaLoaiVe"));

                        // 3. Gọi DAO để nạp đầy đủ thông tin Chi tiết
                        KhachHang kh = (maKHDb != null) ? KhachHangDAO.getKhachHangById(maKHDb) : null;
                        ChuyenTau ct = (maCTDb != null) ? ChuyenTauDao.layChuyenTauBangMa(maCTDb) : null;
                        ChoDat cd = (maChoDatDb != null) ? ChoDatDAO.getChoDatById(maChoDatDb) : null;

                        ve.setKhachHangChiTiet(kh);
                        ve.setChuyenTauChiTiet(ct);
                        ve.setChoDatChiTiet(cd);

                        // 4. Gán thuộc tính tiện ích (Hiển thị)
                        if (kh != null) {
                            ve.setTenKhachHang(kh.getHoTen()); // Sử dụng setTenKhachHang
                        }
                        if (cd != null && cd.getSoCho() != null) {
                            try {
                                // Logic chuyển đổi số ghế
                                ve.setSoGhe(Integer.parseInt(cd.getSoCho().replaceAll("[^\\d]", "")));
                            } catch (NumberFormatException e) {
                                ve.setSoGhe(0);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm chi tiết vé từ CSDL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return ve;
    }

    /**
     * Tìm vé theo thông tin Khách hàng và/hoặc Mã vé.
     * Chỉ lấy thông tin cơ bản và Khóa ngoại.
     */
    public List<Ve> timVeTheoKhachHang(String hoTen, String sdt, String cccd, String maVe) {
        List<Ve> danhSachVe = new ArrayList<>();

        // 1. Xây dựng câu lệnh SQL (giữ nguyên logic của bạn)
        StringBuilder sql = new StringBuilder(
                "SELECT V.MaVe, V.GiaVe, V.TrangThai, V.MaKhachHang, V.MaChuyenTau, V.MaChoDat, V.MaLoaiVe, V.MaNV " +
                        "FROM Ve V " +
                        "JOIN KhachHang KH ON V.MaKhachHang = KH.MaKhachHang " +
                        "WHERE 1=1"
        );

        if (maVe != null && !maVe.isEmpty()) sql.append(" AND V.MaVe = ?");
        if (hoTen != null && !hoTen.isEmpty()) sql.append(" AND KH.HoTen LIKE ?");
        if (sdt != null && !sdt.isEmpty()) sql.append(" AND KH.SoDienThoai LIKE ?");
        if (cccd != null && !cccd.isEmpty()) sql.append(" AND KH.CCCD LIKE ?");

        // 2. BƯỚC 1: LẤY DỮ LIỆU THÔ TỪ CSDL VÀO DANH SÁCH TẠM
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (maVe != null && !maVe.isEmpty()) pstmt.setString(paramIndex++, maVe);
            if (hoTen != null && !hoTen.isEmpty()) pstmt.setString(paramIndex++, "%" + hoTen + "%");
            if (sdt != null && !sdt.isEmpty()) pstmt.setString(paramIndex++, "%" + sdt + "%");
            if (cccd != null && !cccd.isEmpty()) pstmt.setString(paramIndex++, "%" + cccd + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Ve ve = new Ve();
                    ve.setMaVe(rs.getString("MaVe"));
                    ve.setGiaVe(rs.getDouble("GiaVe"));
                    ve.setTrangThai(rs.getString("TrangThai"));
                    ve.setMaLoaiVe(rs.getString("MaLoaiVe"));

                    // Chỉ gán mã (khóa ngoại) trước
                    ve.setMaKhachHang(rs.getString("MaKhachHang"));
                    ve.setMaChuyenTau(rs.getString("MaChuyenTau"));
                    ve.setMaChoDat(rs.getString("MaChoDat"));
                    ve.setMaNV(rs.getString("MaNV"));

                    danhSachVe.add(ve);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm vé: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect(); // Đóng kết nối chính sau khi đọc xong ResultSet
        }

        // 3. BƯỚC 2: NẠP CHI TIẾT ENTITY (Tránh gọi DAO lồng nhau khi đang mở ResultSet)
        for (Ve ve : danhSachVe) {
            if (ve.getMaKhachHang() != null) {
                KhachHang kh = KhachHangDAO.getKhachHangById(ve.getMaKhachHang());
                ve.setKhachHangChiTiet(kh);
                if (kh != null) ve.setTenKhachHang(kh.getHoTen());
            }
            if (ve.getMaChuyenTau() != null) {
                ve.setChuyenTauChiTiet(ChuyenTauDao.layChuyenTauBangMa(ve.getMaChuyenTau()));
            }
            if (ve.getMaChoDat() != null) {
                ve.setChoDatChiTiet(ChoDatDAO.getChoDatById(ve.getMaChoDat()));
            }
        }

        return danhSachVe;
    }

    /**
     * Triển khai: Cập nhật trạng thái vé thành "Đã hủy" (Trả vé).
     */
    public boolean huyVe(String maVe) {
        String sql = "UPDATE Ve SET TrangThai = N'DA-HUY' WHERE MaVe = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maVe);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy vé: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy chi tiết vé theo Mã vé, bao gồm các thực thể phụ thuộc.
     */
    public Ve getVeById(String maVe) {
        Ve ve = null;

        // Truy vấn SQL cơ bản chỉ lấy các khóa ngoại và thông tin trực tiếp của bảng Ve
        String sql = "SELECT MaVe, GiaVe, TrangThai, MaKhachHang, MaChuyenTau, MaChoDat, MaLoaiVe, MaNV " +
                "FROM Ve " +
                "WHERE MaVe = ?";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

                if (maVe == null || maVe.isEmpty()) {
                    return null;
                }

                pstmt.setString(1, maVe);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ve = new Ve();

                        // 1. Gán thuộc tính chính
                        ve.setMaVe(rs.getString("MaVe"));
                        ve.setGiaVe(rs.getDouble("GiaVe"));
                        ve.setTrangThai(rs.getString("TrangThai"));
                        ve.setMaLoaiVe(rs.getString("MaLoaiVe"));

                        // 2. Gán Khóa ngoại
                        String maKHDb = rs.getString("MaKhachHang");
                        String maCTDb = rs.getString("MaChuyenTau");
                        String maChoDatDb = rs.getString("MaChoDat");
                        String maNVDb = rs.getString("MaNV"); // Thêm MaNV

                        ve.setMaKhachHang(maKHDb);
                        ve.setMaChuyenTau(maCTDb);
                        ve.setMaChoDat(maChoDatDb);
                        ve.setMaNV(maNVDb);

                        // 3. GỌI DAO KHÁC ĐỂ NẠP ĐẦY ĐỦ CÁC THỰC THỂ PHỤ THUỘC
                        KhachHang kh = (maKHDb != null) ? KhachHangDAO.getKhachHangById(maKHDb) : null;
                        ChuyenTau ct = (maCTDb != null) ? ChuyenTauDao.layChuyenTauBangMa(maCTDb) : null;
                        ChoDat cd = (maChoDatDb != null) ? ChoDatDAO.getChoDatById(maChoDatDb) : null;

                        // Gán Entity chi tiết
                        ve.setKhachHangChiTiet(kh);
                        ve.setChuyenTauChiTiet(ct);
                        ve.setChoDatChiTiet(cd);

                        // 4. Gán các trường tiện ích (Tên khách, số ghế)
                        if (kh != null) {
                            ve.setTenKhachHang(kh.getHoTen());
                        }
                        if (cd != null && cd.getSoCho() != null) {
                            try {
                                ve.setSoGhe(Integer.parseInt(cd.getSoCho().replaceAll("[^\\d]", "")));
                            } catch (NumberFormatException e) {
                                ve.setSoGhe(0);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm chi tiết vé theo ID từ CSDL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                ConnectDB.disconnect();
            }
        }
        return ve;
    }


    /**
     * Dười đây là code của duy về phần tạo vé mới
     *
     */
// Khởi tạo các DAO phụ thuộc để dùng trong Transaction
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO cthdDAO = new ChiTietHoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    // =================================================================================
    // CÁC HÀM TIỆN ÍCH CHO VIỆC TẠO MÃ VÉ
    // =================================================================================

    private String getSoHieuCaHienTai() {
        // TODO: Thay thế bằng logic truy vấn thực tế.
        // Giả định trả về số hiệu ca (ví dụ: "01", "02")
        return "01";
    }

    /**
     * Truy vấn MaVe lớn nhất trong ngày/ca hiện tại.
     */
    public String getLastSoThuTuTrongCa(String soHieuCa, LocalDate ngayTaoVe) throws SQLException {
        String lastSTT = null;
        // Định dạng ngày: ddMMyy
        String ngayStr = ngayTaoVe.format(DateTimeFormatter.ofPattern("ddMMyy"));
        String maVePattern = "VE" + soHieuCa + ngayStr + "%";

        // Sử dụng TOP 1 và ORDER BY để tìm mã vé mới nhất
        String sql = "SELECT TOP 1 MaVe FROM Ve WHERE MaVe LIKE ? ORDER BY MaVe DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maVePattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastMaVe = rs.getString("MaVe");
                    // Cắt lấy 4 ký tự cuối (số thứ tự NNNN)
                    lastSTT = lastMaVe.substring(lastMaVe.length() - 4);
                }
            }
        }
        return lastSTT;
    }

    /**
     * Tạo mã vé mới theo quy tắc: VE[CC][DDmmYY][NNNN].
     */
    public String taoMaVeMoi(int nextNumber, String soHieuCa) {
        LocalDate homNay = LocalDate.now();
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));
        String soThuTuStr = String.format("%04d", nextNumber); // Đảm bảo đủ 4 chữ số

        return "VE" + soHieuCa + ngayStr + soThuTuStr;
    }

    // =================================================================================
    // CHỨC NĂNG BÁN VÉ (TRANSACTION - Thay thế VeCuaBanVeDAO)
    // =================================================================================

    /**
     * Hàm chính thực hiện Transaction Bán Vé (ACID).
     *
     * @param hoaDon Đối tượng hóa đơn đã chuẩn bị.
     * @param danhSachVe Danh sách vé (Entity Ve) đã được tạo (MaVe=null, nhưng các FK đã có).
     * @param danhSachKhachHangEntities Map chứa KhachHang (đã tồn tại hoặc mới)
     *
     * @return true nếu toàn bộ giao dịch thành công.
     * nếu không thành công thì sẽ không lưu khách hàng, hóa đơn, vé vào CSDL.
     * @throws SQLException Nếu có lỗi CSDL (Transaction đã bị ROLLBACK).
     */
    public boolean banVeTrongTransaction(HoaDon hoaDon, List<Ve> danhSachVe, Map<String, KhachHang> danhSachKhachHangEntities) throws SQLException {
        Connection conn = null;
        boolean success = false;

        // --- BƯỚC 1: TÍNH TOÁN MÃ VÉ CHO TẤT CẢ VÉ TRƯỚC TRANSACTION ---
        LocalDate homNay = LocalDate.now();
        String soHieuCa = getSoHieuCaHienTai();
        String lastSTTStr = getLastSoThuTuTrongCa(soHieuCa, homNay);

        int currentMaxNumber = 0;
        if (lastSTTStr != null) {
            try {
                currentMaxNumber = Integer.parseInt(lastSTTStr);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi phân tích số thứ tự cuối cùng: " + e.getMessage());
            }
        }

        // --- BƯỚC 2: KHỞI TẠO TRANSACTION VÀ THỰC HIỆN INSERT ---
        try {
            // Lấy kết nối từ ConnectDB
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false); // 1. Bắt đầu giao dịch

            // B1: Xử lý Khách hàng (Thêm mới hoặc Cập nhật)
            for (KhachHang kh : danhSachKhachHangEntities.values()) {
                if (!khachHangDAO.themHoacCapNhatKhachHang(conn, kh)) {
                    throw new SQLException("Lỗi khi thêm/cập nhật thông tin Khách hàng: " + kh.getMaKH());
                }
            }

            // B2: Thêm Hóa đơn
            if (!hoaDonDAO.themHoaDon(conn, hoaDon)) {
                throw new SQLException("Thêm Hóa đơn thất bại.");
            }

            // B3 & B4: Thêm từng Vé và Chi tiết Hóa đơn
            for (int i = 0; i < danhSachVe.size(); i++) {
                Ve ve = danhSachVe.get(i); // Sử dụng Entity Ve đã hợp nhất

                // a. TẠO MÃ VÉ MỚI DUY NHẤT TRONG GIAO DỊCH
                int nextNumber = currentMaxNumber + i + 1;
                ve.setMaVe(taoMaVeMoi(nextNumber, soHieuCa));

                // b. Thêm Vé (Sử dụng hàm themVe mới)
                if (!themVe(conn, ve)) {
                    throw new SQLException("Thêm Vé thất bại: " + ve.getMaVe());
                }
                // Lấy đơn giá thực tế từ Vé
                double donGiaThucTe = ve.getGiaVe();
                // c. Thêm Chi tiết Hóa đơn
                ChiTietHoaDon cthd = new ChiTietHoaDon(hoaDon.getMaHD(), ve.getMaVe(), 1, donGiaThucTe);
                if (!cthdDAO.themChiTietHoaDon(conn, cthd)) {
                    throw new SQLException("Thêm Chi tiết Hóa đơn thất bại.");
                }
            }

            conn.commit(); // 5. Hoàn tất giao dịch
            success = true;
            //

        } catch (SQLException e) {
            // Nếu có lỗi, hủy bỏ tất cả thay đổi
            if (conn != null) {
                try {
                    conn.rollback(); // 4. Hủy bỏ (Rollback)
                    System.err.println("Giao dịch bán vé thất bại, đã rollback.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            //
            e.printStackTrace();
            throw e; // Ném lỗi lên để UI xử lý thông báo thất bại
        } finally {
            if (conn != null) {
                // Đóng kết nối giao dịch cục bộ một cách an toàn
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * Thêm một thực thể Vé mới vào CSDL (trong bối cảnh giao dịch).
     * <p>
     * Phương thức này sử dụng kết nối đã có sẵn (tham số {@code conn}),
     * sử dụng các Khóa ngoại (String) của Entity Ve.
     * @param conn Kết nối CSDL đang mở (có {@code autoCommit=false}).
     * @param ve Đối tượng Vé cần thêm (đã có MaVe).
     * @return true nếu thêm thành công.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public boolean themVe(Connection conn, Ve ve) throws SQLException {
        // Cần khớp với 8 cột trong bảng Ve: MaVe, MaChuyenTau, MaChoDat, MaNV, MaKhachHang, MaLoaiVe, GiaVe, TrangThai
        String sql = "INSERT INTO Ve (MaVe, MaChuyenTau, MaChoDat, MaNV, MaKhachHang, MaLoaiVe, GiaVe, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

            // Dùng Getters của các thuộc tính Khóa ngoại/thuộc tính chính từ Entity Ve đã hợp nhất
            pstmt.setString(1, ve.getMaVe());
            pstmt.setString(2, ve.getMaChuyenTau());
            pstmt.setString(3, ve.getMaChoDat());

            // Xử lý MaNV (có thể NULL)
            if (ve.getMaNV() != null && !ve.getMaNV().isEmpty()) {
                pstmt.setString(4, ve.getMaNV());
            } else {
                pstmt.setNull(4, Types.NVARCHAR);
            }

            // Xử lý MaKhachHang (có thể NULL)
            if (ve.getMaKhachHang() != null && !ve.getMaKhachHang().isEmpty()) {
                pstmt.setString(5, ve.getMaKhachHang());
            } else {
                pstmt.setNull(5, Types.NVARCHAR);
            }

            pstmt.setString(6, ve.getMaLoaiVe());
            pstmt.setDouble(7, ve.getGiaVe());
            pstmt.setString(8, ve.getTrangThai());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm Vé (MaVe: " + ve.getMaVe() + "): " + e.getMessage());
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }


}