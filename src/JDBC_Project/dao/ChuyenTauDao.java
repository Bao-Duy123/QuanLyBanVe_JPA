
package JDBC_Project.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import entity.*;
import database.ConnectDB;
import entity.lopEnum.TrangThaiChuyenTau;


public class ChuyenTauDao {
    private ArrayList<ChuyenTau> danhSachChuyenTau;

    public ChuyenTauDao() {
        danhSachChuyenTau = new ArrayList<ChuyenTau>();
    }


public List<ChuyenTau> getAllChuyenTau() throws SQLException {
    List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // Sắp xếp theo ngày, giờ khởi hành (mới nhất lên đầu)
    String sql = "SELECT * FROM ChuyenTau ORDER BY NgayKhoiHanh DESC, GioKhoiHanh DESC";

    try {
        conn = ConnectDB.getConnection(); // Lấy kết nối
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            String maChuyenTau = rs.getString("MaChuyenTau");
            String maTau = rs.getString("MaTau"); // Đây là SoHieu, ví dụ "SE1"
            String maNV = rs.getString("MaNV"); // Có thể NULL
            String maGaDi = rs.getString("GaDi"); // CSDL lưu Tên Ga
            String maGaDen = rs.getString("GaDen"); // CSDL lưu Tên Ga

            // Lấy ngày/giờ và chuyển đổi sang java.time
            Date dbNgayKH = rs.getDate("NgayKhoiHanh");
            Time dbGioKH = rs.getTime("GioKhoiHanh");
            Date dbNgayDen = rs.getDate("NgayDenDuKien");
            Time dbGioDen = rs.getTime("GioDenDuKien");

            LocalDate ngayKhoiHanh = (dbNgayKH != null) ? dbNgayKH.toLocalDate() : null;
            LocalTime gioKhoiHanh = (dbGioKH != null) ? dbGioKH.toLocalTime() : null;
            LocalDate ngayDenDuKien = (dbNgayDen != null) ? dbNgayDen.toLocalDate() : null;
            LocalTime gioDenDuKien = (dbGioDen != null) ? dbGioDen.toLocalTime() : null;

            String trangThaiStr = rs.getString("TrangThai");

            // Lấy các đối tượng liên quan từ DAO khác
            // Giả sử các DAO đã được khởi tạo trong constructor của ChuyenTauDao
            Tau tau = TauDAO.getTauById(maTau); // Dùng TauDAO
            Ga gaDi = GaDao.layGaBangMa(maGaDi); // Dùng GaDao (tìm theo Tên Ga)
            Ga gaDen = GaDao.layGaBangMa(maGaDen); // Dùng GaDao (tìm theo Tên Ga)

            NhanVien nhanVien = null;
            if (maNV != null && !maNV.isEmpty()) {
                nhanVien = NhanVienDao.getNhanVienById(maNV); // Dùng NhanVienDao
            }

            // Chuyển đổi String trạng thái từ CSDL sang Enum
            // (Giả sử bạn có hàm fromString trong Enum)
            TrangThaiChuyenTau trangThaiEnum = TrangThaiChuyenTau.fromString(trangThaiStr);

            // Kiểm tra null trước khi tạo ChuyenTau
            if (tau != null && gaDi != null && gaDen != null && ngayKhoiHanh != null) {
                // Tạo đối tượng ChuyenTau
                // Dùng constructor khớp với entity ChuyenTau của bạn
                ChuyenTau ct = new ChuyenTau(
                        maChuyenTau,
                        maTau, // maTau (String)
                        ngayKhoiHanh,
                        gioKhoiHanh,
                        gaDi, // Ga object
                        gaDen, // Ga object
                        tau, // Tau object
                        ngayDenDuKien,
                        gioDenDuKien,
                        nhanVien, // NhanVien object
                        trangThaiEnum // Enum
                );
                danhSachChuyenTau.add(ct);
            } else {
                System.err.println("WARN: Bỏ qua chuyến tàu " + maChuyenTau + " do thiếu thông tin Tàu/Ga.");
            }
        }
    } catch (SQLException e) {
        System.err.println("Lỗi khi lấy danh sách chuyến tàu: " + e.getMessage());
        e.printStackTrace();
        throw e; // Ném lỗi ra ngoài để lớp gọi xử lý
    } finally {
        // Đóng ResultSet và PreparedStatement
        if (rs != null) try { rs.close(); } catch (SQLException e) { /* Bỏ qua */ }
        if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { /* Bỏ qua */ }
        // Không đóng Connection ở đây
    }
    return danhSachChuyenTau;
}
    public boolean updateChuyenTau(ChuyenTau ct) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int rowsAffected = 0;

        // Câu SQL dựa trên CSDL QuanLyVeTauTest2
        String sql = "UPDATE ChuyenTau SET MaTau = ?, MaNV = ?, GaDi = ?, GaDen = ?, " +
                "NgayKhoiHanh = ?, GioKhoiHanh = ?, NgayDenDuKien = ?, GioDenDuKien = ?, TrangThai = ? " +
                "WHERE MaChuyenTau = ?";

        // --- Kiểm tra dữ liệu đầu vào cơ bản ---
        if (ct == null || ct.getMaChuyenTau() == null) {
            throw new IllegalArgumentException("Thông tin chuyến tàu không hợp lệ để cập nhật (thiếu mã)");
        }
        if (ct.getTau() == null || ct.getGaDi() == null || ct.getGaDen() == null ||
                ct.getNgayKhoiHanh() == null || ct.getNgayDenDuKien() == null) {
            throw new IllegalArgumentException("Thông tin Tàu, Ga, hoặc Ngày không được để trống.");
        }
        if (ct.getGaDi().getTenGa().equals(ct.getGaDen().getTenGa())) {
            throw new IllegalArgumentException("Ga đi và Ga đến không được trùng nhau.");
        }


        try {
            conn = ConnectDB.getConnection(); // Lấy kết nối mới
            pstmt = conn.prepareStatement(sql);

            // --- Set các tham số ---

            // 1. MaTau (Tham chiếu đến Tau.SoHieu)
            // Giả sử entity Tau của bạn có getSoHieu()
            pstmt.setString(1, ct.getTau().getSoHieu());

            // 2. MaNV (Có thể null)
            if (ct.getNhanVien() != null && ct.getNhanVien().getMaNV() != null) {
                pstmt.setString(2, ct.getNhanVien().getMaNV());
            } else {
                pstmt.setNull(2, Types.NVARCHAR);
            }

            // 3. GaDi (Lưu TenGa)
            pstmt.setString(3, ct.getGaDi().getTenGa());
            // 4. GaDen (Lưu TenGa)
            pstmt.setString(4, ct.getGaDen().getTenGa());

            // 5. NgayKhoiHanh (LocalDate -> sql.Date)
            pstmt.setDate(5, Date.valueOf(ct.getNgayKhoiHanh()));
            // 6. GioKhoiHanh (LocalTime -> sql.Time)
            pstmt.setTime(6, (ct.getGioKhoiHanh() != null) ? Time.valueOf(ct.getGioKhoiHanh()) : null);

            // 7. NgayDenDuKien
            pstmt.setDate(7, Date.valueOf(ct.getNgayDenDuKien()));
            // 8. GioDenDuKien
            pstmt.setTime(8, (ct.getGioDenDuKien() != null) ? Time.valueOf(ct.getGioDenDuKien()) : null);

            // 9. TrangThai (Chuyển Enum sang String nếu cần)
            // Giả sử getTrangThai() trả về String hoặc Enum.toString()
            pstmt.setString(9, ct.getThct().toString());

            // 10. WHERE MaChuyenTau
            pstmt.setString(10, ct.getMaChuyenTau());

            rowsAffected = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật chuyến tàu: " + e.getMessage());
            throw e; // Ném lỗi ra ngoài
        } finally {
            // Đóng PreparedStatement
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // Không đóng Connection ở đây, ConnectDB sẽ quản lý
            // (Nếu ConnectDB không quản lý, bạn cần đóng conn ở đây)
        }
        return rowsAffected > 0;
    }

    public boolean themChuyenTauNangCao(ChuyenTau ct) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int rowsAffected = 0;

        // 1. CÂU SQL CHUẨN HÓA (Khớp với DB QuanLyVeTauProMax)
        String sql = "INSERT INTO ChuyenTau (MaChuyenTau, MaTuyen, MaTau, MaNV, GaDi, GaDen, " +
                "NgayKhoiHanh, GioKhoiHanh, NgayDenDuKien, GioDenDuKien, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // 2. KIỂM TRA DỮ LIỆU ĐẦU VÀO
        if (ct == null || ct.getMaChuyenTau() == null || ct.getMaChuyenTau().isEmpty()) {
            throw new IllegalArgumentException("Mã chuyến tàu không được để trống.");
        }
        // Kiểm tra các thành phần bắt buộc khác (Tuyến, Tàu, Ngày)
        if (ct.getTuyen() == null || ct.getTau() == null || ct.getNgayKhoiHanh() == null) {
            throw new IllegalArgumentException("Thông tin chuyến tàu thiếu (Tuyến, Tàu hoặc Ngày đi).");
        }

        try {
            // Lấy kết nối
            conn = ConnectDB.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);

            // --- THIẾT LẬP THAM SỐ (SET PARAMETERS) ---

            // 1. MaChuyenTau
            pstmt.setString(1, ct.getMaChuyenTau());

            // 2. MaTuyen
            pstmt.setString(2, ct.getTuyen().getMaTuyen());

            // 3. MaTau
            pstmt.setString(3, ct.getTau().getSoHieu());

            // 4. MaNV
            if (ct.getNhanVien() != null) {
                pstmt.setString(4, ct.getNhanVien().getMaNV());
            } else {
                pstmt.setNull(4, Types.NVARCHAR);
            }

            // 5. GaDi
            pstmt.setString(5, ct.getGaDi().getMaGa());

            // 6. GaDen
            pstmt.setString(6, ct.getGaDen().getMaGa());

            // 7. NgayKhoiHanh
            pstmt.setDate(7, Date.valueOf(ct.getNgayKhoiHanh()));

            // 8. GioKhoiHanh
            pstmt.setTime(8, Time.valueOf(ct.getGioKhoiHanh()));

            // 9. NgayDenDuKien
            if (ct.getNgayDenDuKien() != null) {
                pstmt.setDate(9, Date.valueOf(ct.getNgayDenDuKien()));
            } else {
                pstmt.setNull(9, Types.DATE);
            }

            // 10. GioDenDuKien
            if (ct.getGioDenDuKien() != null) {
                pstmt.setTime(10, Time.valueOf(ct.getGioDenDuKien()));
            } else {
                pstmt.setNull(10, Types.TIME);
            }

            // 11. TrangThai
            String tenHienThi = ct.getThct() != null ? ct.getThct().getTenHienThi() : "Chờ khởi hành";
            pstmt.setString(11, tenHienThi);

            // --- THỰC THI ---
            rowsAffected = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi thêm chuyến tàu: " + e.getMessage());

            // Bắt lỗi trùng khóa chính (Error Code 2627 hoặc 2601 trong SQL Server)
            if (e.getErrorCode() == 2627 || e.getMessage().contains("PRIMARY KEY")) {
                throw new SQLException("Mã chuyến tàu '" + ct.getMaChuyenTau() + "' đã tồn tại trong hệ thống.", e.getSQLState(), e);
            }
            // Bắt lỗi khóa ngoại (Ví dụ: Mã Tàu hoặc Mã Tuyến không tồn tại)
            if (e.getMessage().contains("FOREIGN KEY")) {
                throw new SQLException("Dữ liệu tham chiếu không hợp lệ (Tuyến hoặc Tàu không tồn tại).", e.getSQLState(), e);
            }

            throw e; // Ném lại các lỗi khác để Controller xử lý
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return rowsAffected > 0;
    }


    public List<ChuyenTau> timChuyenTauTheoGaVaNgayDi(String gaXP, String gaKT, String ngayDi) {
        List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();
        System.out.println("Input: " + gaXP + " | " + gaKT + " | " + ngayDi);

        // 1. Sửa lỗi thiếu dấu cách trước " AND ("
        String sql = "SELECT CT.*, " +
                "GA_DI.TenGa AS TenGaDi, GA_DI.DiaChi AS DiaChiGaDi, " +
                "GA_DEN.TenGa AS TenGaDen, GA_DEN.DiaChi AS DiaChiGaDen, " +
                "T.TrangThai AS TrangThaiTau, " +
                "NV.HoTen AS TenNV, NV.SDT AS SDTNV, NV.Email AS EmailNV " +
                "FROM ChuyenTau CT " +
                "LEFT JOIN Ga GA_DI ON CT.GaDi = GA_DI.MaGa " +
                "LEFT JOIN Ga GA_DEN ON CT.GaDen = GA_DEN.MaGa " +
                "LEFT JOIN Tau T ON CT.MaTau = T.SoHieu " +
                "LEFT JOIN NhanVien NV ON CT.MaNV = NV.MaNV " +
                "WHERE CT.GaDi = ? AND CT.GaDen = ? AND CT.NgayKhoiHanh = ? " + // <-- Dấu cách quan trọng
                "AND (" +
                "      CT.NgayKhoiHanh > CAST(GETDATE() AS DATE) " +
                "      OR (CT.NgayKhoiHanh = CAST(GETDATE() AS DATE) AND CT.GioKhoiHanh > CAST(GETDATE() AS TIME))" +
                ")";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, gaXP);
            pstmt.setString(2, gaKT);
            pstmt.setString(3, ngayDi); // Fallback về setString nếu lỗi


            System.out.println("Bắt đầu query...");

            try (ResultSet rs = pstmt.executeQuery()) {
                // Biến kiểm tra xem có dòng nào không
                boolean coDuLieu = false;

                while (rs.next()) {
                    coDuLieu = true;
                    String maChuyenTau = rs.getString("MaChuyenTau");
                    System.out.println("Tìm thấy chuyến: " + maChuyenTau); // Debug

                    String maTau = rs.getString("MaTau");
                    String maNV = rs.getString("MaNV");

                    LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                    LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();
                    LocalDate ngayDen = rs.getDate("NgayDenDuKien").toLocalDate();
                    LocalTime gioDen = rs.getTime("GioDenDuKien").toLocalTime();

                    String trangThai = rs.getString("TrangThai");
                    TrangThaiChuyenTau tt = TrangThaiChuyenTau.fromString(trangThai);

                    Ga gaDi = new Ga(rs.getString("GaDi"), rs.getString("TenGaDi"), rs.getString("DiaChiGaDi"));
                    Ga gaDen = new Ga(rs.getString("GaDen"), rs.getString("TenGaDen"), rs.getString("DiaChiGaDen"));
                    Tau tau = new Tau(rs.getString("MaTau"), rs.getString("TrangThaiTau"));

                    NhanVien nv = null;
                    if (maNV != null && !maNV.isEmpty()) {
                        nv = NhanVienDao.getNhanVienById(maNV);
                    }

                    ChuyenTau ct = new ChuyenTau(maChuyenTau, maTau, ngayKH, gioKH, gaDi, gaDen, tau, ngayDen, gioDen, nv, tt);
                    danhSachChuyenTau.add(ct);
                }

                if (!coDuLieu) {
                    System.out.println("Query chạy thành công nhưng KHÔNG tìm thấy bản ghi nào khớp điều kiện.");
                    System.out.println("Lưu ý: Code đang lọc bỏ các chuyến tàu đã khởi hành trong quá khứ so với giờ Server.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL Exception: ");
            e.printStackTrace();
        }
        return danhSachChuyenTau;
    }
    //chuyển trạng thái chuyến tàu theo mã chuyến tàu và trạng thái mới
    //theo đúng lý thuyết, trạng thái chuyến tàu sẽ đổi từ "Đang Đặt" -> "Đã Khởi Hành" -> "Đã Kết Thúc"
    //theo thời gian thực tế, tuy nhiên để đơn giản hóa, ta sẽ cho phép đổi trạng thái trực tiếp
    // ınhư mong muốn
    public boolean chuyenTrangThaiChuyenTau(String maChuyenTau, String trangThai) {
        String sql = "UPDATE ChuyenTau SET TrangThai = ? WHERE MaChuyenTau = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
            stmt.setString(2, maChuyenTau);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                if (this.danhSachChuyenTau != null) {
                    for (ChuyenTau ct : this.danhSachChuyenTau) {
                        if (ct.getMaChuyenTau().equals(maChuyenTau)) {
                            // assuming setter exists
                            ct.setThct(TrangThaiChuyenTau.valueOf(trangThai));
                            break;
                        }
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

//    layChuyenTauBangMa
    public static ChuyenTau layChuyenTauBangMa(String maChuyenTau) {
        ChuyenTau ct = null;
        String sql = "SELECT * FROM ChuyenTau WHERE MaChuyenTau = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maChuyenTau);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String maTau = rs.getString("MaTau");
                    String maNV = rs.getString("MaNV");
                    String maGaDiDb = rs.getString("GaDi");
                    String maGaDenDb = rs.getString("GaDen");
                    LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                    LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();
                    LocalDate ngayDen = rs.getDate("NgayDenDuKien").toLocalDate();
                    LocalTime gioDen = rs.getTime("GioDenDuKien").toLocalTime();
                    String trangThai = rs.getString("TrangThai");

                    TrangThaiChuyenTau tt = TrangThaiChuyenTau.fromString(trangThai);

                    Ga gaDi = GaDao.layGaBangMa(maGaDiDb);
                    Ga gaDen = GaDao.layGaBangMa(maGaDenDb);
                    Tau tau = TauDAO.getTauById(maTau);
                    NhanVien nv = NhanVienDao.getNhanVienById(maNV);

                    ct = new ChuyenTau(maChuyenTau, maTau, ngayKH, gioKH, gaDi, gaDen, tau, ngayDen, gioDen, nv, tt);
                    System.out.println("Đã tìm thấy Chuyến Tàu với mã: " + maChuyenTau);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Chuyến Tàu theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return ct;
    }

    public static ChuyenTau timKiemChuyenTauTheoMaHoaDon(String maHoaDon) {
        String sql = """
                SELECT DISTINCT J.GaDi, J.GaDen, J.NgayKhoiHanh, J.GioKhoiHanh
                FROM HoaDon HD 
                JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                JOIN Ve V ON V.MaVe = CT.MaVe
                JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                WHERE HD.MaHD = ?
                """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHoaDon);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ChuyenTau ct = new ChuyenTau();
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                return ct;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TIMKIEM THEO SODIENTHOAI
    public static ArrayList<ChuyenTau> timKiemChuyenTauTheoSoDienThoai(String giaTriTimKiem) {
        ArrayList<ChuyenTau> danhSach = new ArrayList<>();
        String sql = """
                
                                        
                                        SELECT DISTINCT HD.MaHD, KH.SoDienThoai, KH.HoTen, J.GaDi, J.GaDen , J.NgayKhoiHanh, J.GioKhoiHanh
                                        FROM HoaDon HD JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                                        JOIN Ve V ON V.MaVe = CT.MaVe
                                        JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
                                        JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                                        WHERE SoDienThoai = ?
                """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChuyenTau ct = new ChuyenTau();
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                danhSach.add(ct);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public static ArrayList<ChuyenTau> timChuyenTauTheoCCCD(String giaTriTimKiem) {
        ArrayList<ChuyenTau> danhSach = new ArrayList<>();
        String sql = """
                                SELECT DISTINCT HD.MaHD, KH.SoDienThoai, KH.HoTen,KH.CCCD, J.GaDi, J.GaDen , J.NgayKhoiHanh, J.GioKhoiHanh
                                 FROM HoaDon HD JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                                 JOIN Ve V ON V.MaVe = CT.MaVe
                                 JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
                                 JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                                 WHERE KH.CCCD = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChuyenTau ct = new ChuyenTau();
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                danhSach.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // Lọc Chuyến Tàu theo SĐT Khách Hàng + Tháng/Năm hóa đơn
    public static ArrayList<ChuyenTau> timChuyenTauTheoSDTLocThangNam(String sdt, int thang, int nam) {
        ArrayList<ChuyenTau> danhSach = new ArrayList<>();
        String sql = """
            SELECT DISTINCT HD.MaHD, KH.SoDienThoai, KH.HoTen, J.GaDi, J.GaDen , J.NgayKhoiHanh, J.GioKhoiHanh
                                        FROM HoaDon HD JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                                        JOIN Ve V ON V.MaVe = CT.MaVe
                                        JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
                                        JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                                        WHERE KH.SoDienThoai = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
            """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sdt);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ChuyenTau ct = new ChuyenTau(); // Giả sử có setters hoặc constructor phù hợp
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                // Set các thuộc tính khác nếu cần
                danhSach.add(ct);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }

    // Lọc Chuyến Tàu theo CCCD Khách Hàng + Tháng/Năm hóa đơn
    public static ArrayList<ChuyenTau> timChuyenTauTheoCCCDLocThangNam(String cccd, int thang, int nam) {
        ArrayList<ChuyenTau> danhSach = new ArrayList<>();
        String sql = """
             SELECT DISTINCT HD.MaHD, KH.SoDienThoai, KH.HoTen, J.GaDi, J.GaDen , J.NgayKhoiHanh, J.GioKhoiHanh
                                        FROM HoaDon HD JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                                        JOIN Ve V ON V.MaVe = CT.MaVe
                                        JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
                                        JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                                        WHERE KH.CCCD = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
             """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ChuyenTau ct = new ChuyenTau();
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                danhSach.add(ct);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }

    public List<ChuyenTau> layChuyenTauTheoDieuKien(String maTuyen, LocalDate ngay) throws SQLException {
        List<ChuyenTau> ds = new ArrayList<>();
        // JOIN với các bảng liên quan để lấy đầy đủ thông tin hiển thị
        String sql = "SELECT CT.*, T.TrangThai AS TrangThaiTau, " +
                "G1.TenGa AS TenGaDi, G2.TenGa AS TenGaDen " +
                "FROM ChuyenTau CT " +
                "LEFT JOIN Tau T ON CT.MaTau = T.SoHieu " +
                "LEFT JOIN Ga G1 ON CT.GaDi = G1.MaGa " +
                "LEFT JOIN Ga G2 ON CT.GaDen = G2.MaGa " +
                "WHERE CT.MaTuyen = ? AND CT.NgayKhoiHanh = ? " +
                "ORDER BY CT.GioKhoiHanh ASC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maTuyen);
            pst.setDate(2, Date.valueOf(ngay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // 1. Lấy dữ liệu cơ bản
                    String maChuyenTau = rs.getString("MaChuyenTau");
                    String maTau = rs.getString("MaTau");
                    LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                    LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();

                    // Xử lý Ngày/Giờ đến dự kiến (có thể null)
                    Date sqlNgayDen = rs.getDate("NgayDenDuKien");
                    LocalDate ngayDen = (sqlNgayDen != null) ? sqlNgayDen.toLocalDate() : null;
                    Time sqlGioDen = rs.getTime("GioDenDuKien");
                    LocalTime gioDen = (sqlGioDen != null) ? sqlGioDen.toLocalTime() : null;

                    // 2. Tạo các Object liên quan (Mapping từ kết quả JOIN)
                    Ga gaDi = new Ga(rs.getString("GaDi"), rs.getString("TenGaDi"), null);
                    Ga gaDen = new Ga(rs.getString("GaDen"), rs.getString("TenGaDen"), null);
                    Tau tau = new Tau(maTau, rs.getString("TrangThaiTau"));

                    // 3. Xử lý Trạng thái (Enum)
                    String trangThaiStr = rs.getString("TrangThai");
                    TrangThaiChuyenTau tt = TrangThaiChuyenTau.fromString(trangThaiStr);

                    // 4. Khởi tạo đối tượng ChuyenTau (Dùng constructor khớp với entity của bạn)
                    // Lưu ý: Nếu constructor yêu cầu Tuyen, bạn có thể tạo 1 object Tuyen rỗng chỉ có mã
                    ChuyenTau ct = new ChuyenTau(
                            maChuyenTau,
                            maTau,
                            ngayKH,
                            gioKH,
                            gaDi,
                            gaDen,
                            tau,
                            ngayDen,
                            gioDen,
                            null, // NhanVien set null nếu không cần hiển thị ngay
                            tt
                    );

                    ds.add(ct);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc chuyến tàu theo điều kiện: " + e.getMessage());
            throw e;
        }
        return ds;
    }

    // Trong lớp ChuyenTauDao.java
    public boolean kiemTraTonTaiChuyenTau(String maTuyen, LocalDate ngay) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChuyenTau WHERE maTuyen = ? AND ngayKhoiHanh = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maTuyen);
            ps.setDate(2, Date.valueOf(ngay));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    // Trong ChuyenTauDao.java
    public boolean kiemTraDaCoLichTrinhNgay(String maTuyen, String ngayGocStr) throws SQLException {
        // Logic: Tìm bất kỳ chuyến tàu nào có mã bắt đầu bằng "SE1_201225_"
        // Nếu tồn tại dù chỉ 1 chặng, nghĩa là ngày đó đã được khởi tạo rồi.
        String prefix = maTuyen.trim() + "_" + ngayGocStr + "_%";
        String sql = "SELECT TOP 1 1 FROM ChuyenTau WHERE MaChuyenTau LIKE ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, prefix);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Trả về true nếu đã có dữ liệu
            }
        }
    }
}