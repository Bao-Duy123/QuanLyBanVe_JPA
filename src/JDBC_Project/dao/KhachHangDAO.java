package JDBC_Project.dao;

import database.ConnectDB;
import entity.KhachHang;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.sql.*;
import java.time.LocalDate;

public class KhachHangDAO {

    /**
     * Tra cứu chi tiết Khách hàng bằng Mã Khách hàng (MaKhachHang).
     * ĐÃ SỬA LỖI: KHÔNG dùng try-with-resources cho Connection.
     */
    public static KhachHang getKhachHangById(String maKhachHang) {
        KhachHang kh = null;
        // THAY ĐỔI 1: Thay 'Tuoi' bằng 'NgaySinh' trong câu lệnh SELECT
        String sql = "SELECT MaKhachHang, HoTen, CCCD, NgaySinh, SoDienThoai, GioiTinh FROM KhachHang WHERE MaKhachHang = ?";

        Connection con = null;
        try {
            con = ConnectDB.getConnection(); // Lấy kết nối

            // CHỈ DÙNG TRY-WITH-RESOURCES CHO PreparedStatement và ResultSet
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setString(1, maKhachHang);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Xử lý Ngày sinh từ SQL Date sang Java LocalDate
                        Date sqlDate = rs.getDate("NgaySinh");
                        LocalDate ngaySinh = null;
                        if (sqlDate != null) {
                            ngaySinh = sqlDate.toLocalDate();
                        }

                        // THAY ĐỔI 2: Cập nhật constructor KhachHang: Dùng 'ngaySinh' thay cho 'Tuoi'
                        kh = new KhachHang(
                                rs.getString("MaKhachHang"),
                                rs.getString("HoTen"),
                                rs.getString("CCCD"),
                                ngaySinh, // TRUYỀN LocalDate
                                rs.getString("SoDienThoai"),
                                rs.getString("GioiTinh")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Khách hàng theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return kh;
    }

    // LƯU Ý: TẤT CẢ CÁC PHƯƠNG THỨC TRUY VẤN KHÁC CŨNG PHẢI SỬA TƯƠNG TỰ


// Phương thức helper để ánh xạ ResultSet sang KhachHang
//    private static KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
//        // 1. Đọc java.sql.Date từ cột "NgaySinh"
//        java.sql.Date sqlDate = rs.getDate("NgaySinh");
//        // 2. Chuyển đổi sang java.time.LocalDate
//        LocalDate ngaySinh = null;
//        if (sqlDate != null) {
//            ngaySinh = sqlDate.toLocalDate();
//        }
//        // 3. Sử dụng ngaySinh trong constructor thay cho Tuoi
//        return new KhachHang(
//                rs.getString("MaKhachHang"),
//                rs.getString("HoTen"),
//                rs.getString("CCCD"),
//                ngaySinh, // Dùng LocalDate ngaySinh
//                rs.getString("SoDienThoai"),
//                rs.getString("GioiTinh")
//        );
//    }
    /**
     * Tìm kiếm Khách hàng theo số CCCD.
     * @param cccd Số căn cước công dân.
     * @return Đối tượng KhachHang nếu tìm thấy, ngược lại trả về null.
     */
    public KhachHang getKhachHangByCccd(String cccd) {
        KhachHang kh = null;
        String sql = "SELECT MaKhachHang, HoTen, CCCD, SoDienThoai, NgaySinh " +
                "FROM KhachHang WHERE CCCD = ?";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, cccd);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        kh = new KhachHang();
                        kh.setMaKH(rs.getString("MaKhachHang"));
                        kh.setHoTen(rs.getString("HoTen"));
                        kh.setSoCCCD(rs.getString("CCCD"));
                        kh.setSdt(rs.getString("SoDienThoai"));

                        //1. Đọc java.sql.Date từ cột "NgaySinh"
                        Date sqlDate = rs.getDate("NgaySinh");
                        // 2. Chuyển đổi sang java.time.LocalDate
                        LocalDate ngaySinh = null;
                        if (sqlDate != null) {
                            ngaySinh = sqlDate.toLocalDate();
                        }
                        kh.setNgaySinh(ngaySinh); // Gán LocalDate cho KhachHang
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Khách hàng theo CCCD: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return kh;
    }
    public static KhachHang findKhachHangByCCCD(String cccd) {
        KhachHang kh = null;
        String sql = "SELECT MaKhachHang, HoTen, CCCD, Tuoi, SoDienThoai, GioiTinh FROM KhachHang WHERE CCCD = ?";

        Connection conn = null; // KHAI BÁO BÊN NGOÀI
        try {
            conn = ConnectDB.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, cccd);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
//                        kh = mapResultSetToKhachHang(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi tìm khách hàng bằng CCCD: " + e.getMessage());
        }
        return kh;
    }




    // Trong KhachHangDAO.java
    /**
     * Tạo Mã Khách hàng mới theo quy tắc: KH[DDMMYY][NNNN].
     * @return Mã Khách hàng mới, ví dụ: KH2810250001
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public String taoMaKhachHangMoi() throws SQLException {
        LocalDate homNay = LocalDate.now();
        // Định dạng ngày: DDMMYY
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));

        // Lấy số thứ tự lớn nhất hiện tại
        String lastSTTStr = getLastSoThuTuKhachHang(homNay);

        int nextNumber = 1;
        if (lastSTTStr != null) {
            try {
                // Chuyển "0015" thành 15, rồi cộng 1
                nextNumber = Integer.parseInt(lastSTTStr) + 1;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.err.println("Lỗi phân tích số thứ tự cuối cùng: " + e.getMessage());
            }
        }

        // Định dạng số thứ tự thành chuỗi 4 chữ số (0001)
        String soThuTuStr = String.format("%04d", nextNumber);

        return "KH" + ngayStr + soThuTuStr; // Ví dụ: KH2810250001
    }

    /**
     * Phương thức Helper: Truy vấn số thứ tự lớn nhất của khách hàng được tạo trong ngày.
     */
    private String getLastSoThuTuKhachHang(LocalDate ngayTaoKH) throws SQLException {
        String lastSTT = null;
        // Định dạng ngày: DDMMYY (Ví dụ: 281025)
        String ngayStr = ngayTaoKH.format(DateTimeFormatter.ofPattern("ddMMyy"));

        // Pattern truy vấn: KH[DDMMYY]%
        String maKhachHangPattern = "KH" + ngayStr + "%";

        // Truy vấn MaKhachHang lớn nhất trong ngày hiện tại
        String sql = "SELECT TOP 1 MaKhachHang FROM KhachHang WHERE MaKhachHang LIKE ? ORDER BY MaKhachHang DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKhachHangPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastMaKH = rs.getString("MaKhachHang");
                    // Cắt lấy 4 ký tự cuối (số thứ tự)
                    lastSTT = lastMaKH.substring(lastMaKH.length() - 4);
                }
            }
        }
        return lastSTT; // Ví dụ trả về "0015" hoặc null
    }

    /**
     * Lấy giá trị số nguyên của số thứ tự Khách hàng lớn nhất trong ngày.
     * @return Giá trị số nguyên của STT lớn nhất, hoặc 0 nếu không tìm thấy.
     */
    public int getLastKhachHangSTTValue(LocalDate ngayTaoKH) throws SQLException {
        String ngayStr = ngayTaoKH.format(DateTimeFormatter.ofPattern("ddMMyy"));
        String maKhachHangPattern = "KH" + ngayStr + "%";
        String sql = "SELECT TOP 1 MaKhachHang FROM KhachHang WHERE MaKhachHang LIKE ? ORDER BY MaKhachHang DESC";
        String lastMaKH = null;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKhachHangPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lastMaKH = rs.getString("MaKhachHang");
                }
            }
        }

        // Trích xuất và chuyển đổi
        if (lastMaKH != null && lastMaKH.length() == 12) {
            try {
                String soThuTuStr = lastMaKH.substring(8);
                return Integer.parseInt(soThuTuStr);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.err.println("Lỗi phân tích số thứ tự từ mã: " + lastMaKH);
                // Vẫn trả về 0 nếu mã hợp lệ về độ dài nhưng không phải số
            }
            System.out.println("Số thức tự của khách hàng là: " + lastMaKH);
        }
        return 0; // Trả về 0 nếu không tìm thấy hoặc lỗi
    }




    /**
     * Thêm mới hoặc Cập nhật thông tin Khách hàng (UPSERT logic).
     * Phương thức này chạy trong Transaction và KHÔNG đóng kết nối.
     *
     * @param conn Kết nối CSDL (đang mở transaction, autoCommit=false).
     * @param kh Đối tượng KhachHang cần xử lý.
     * @return true nếu thao tác INSERT hoặc UPDATE thành công.
     * @throws SQLException Nếu có lỗi CSDL nghiêm trọng.
     */
    public boolean themHoacCapNhatKhachHang(Connection conn, KhachHang kh) throws SQLException {

        // 1. Kiểm tra Khách hàng vãng lai (KHVL001)
        if ("KHVL001".equals(kh.getMaKH())) {
            return true;
        }

        // 2. Kiểm tra sự tồn tại bằng MaKhachHang đã được gán
        boolean isUpdate = false;
        String checkSql = "SELECT MaKhachHang FROM KhachHang WHERE MaKhachHang = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, kh.getMaKH());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    isUpdate = true; // Khách hàng ĐÃ tồn tại (UPDATE)
                }
            }
        }

        String sql;
        if (isUpdate) {
            // Cập nhật thông tin khách hàng hiện tại
            // SỬA SQL: Loại bỏ Tuoi, thêm NgaySinh, MaKhachHang là tham số cuối (WHERE)
            sql = "UPDATE KhachHang SET HoTen = ?, CCCD = ?, SoDienThoai = ?, GioiTinh = ?, NgaySinh = ? " +
                    "WHERE MaKhachHang = ?";
        } else {
            // Thêm mới khách hàng
            // SỬA SQL: Loại bỏ Tuoi, thêm NgaySinh
            sql = "INSERT INTO KhachHang (MaKhachHang, HoTen, CCCD, SoDienThoai, GioiTinh, NgaySinh) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Chuyển đổi LocalDate sang java.sql.Date (cho PreparedStatement)
            Date sqlNgaySinh = null;
            if (kh.getNgaySinh() != null) {
                sqlNgaySinh = Date.valueOf(kh.getNgaySinh());
            }

            if (isUpdate) {
                // Tham số cho UPDATE: HoTen, CCCD, SDT, GioiTinh, NgaySinh, MaKhachHang (vị trí 1-6)

                // 1. HoTen
                pstmt.setString(1, kh.getHoTen());
                // 2. CCCD
                pstmt.setString(2, kh.getSoCCCD());
                // 3. SoDienThoai
                pstmt.setString(3, kh.getSdt());

                // 4. GioiTinh
                if (kh.getGioiTinh() != null && !kh.getGioiTinh().isEmpty()) {
                    pstmt.setString(4, kh.getGioiTinh());
                } else {
                    pstmt.setNull(4, Types.NVARCHAR);
                }

                // 5. NgaySinh
                if (sqlNgaySinh != null) {
                    pstmt.setDate(5, sqlNgaySinh);
                } else {
                    pstmt.setNull(5, Types.DATE);
                }

                // 6. MaKhachHang (trong WHERE)
                pstmt.setString(6, kh.getMaKH());

            } else {
                // Tham số cho INSERT: MaKhachHang, HoTen, CCCD, SDT, GioiTinh, NgaySinh (vị trí 1-6)

                // 1. MaKhachHang
                pstmt.setString(1, kh.getMaKH());
                // 2. HoTen
                pstmt.setString(2, kh.getHoTen());
                // 3. CCCD
                pstmt.setString(3, kh.getSoCCCD());
                // 4. SoDienThoai
                pstmt.setString(4, kh.getSdt());

                // 5. GioiTinh
                if (kh.getGioiTinh() != null && !kh.getGioiTinh().isEmpty()) {
                    pstmt.setString(5, kh.getGioiTinh());
                } else {
                    pstmt.setNull(5, Types.NVARCHAR);
                }

                // 6. NgaySinh
                if (sqlNgaySinh != null) {
                    pstmt.setDate(6, sqlNgaySinh);
                } else {
                    pstmt.setNull(6, Types.DATE);
                }
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm/cập nhật Khách hàng (MaKH: " + kh.getMaKH() + "): " + e.getMessage());
            throw e;
        }
    }

    public static KhachHang timKhachHangTheoMaHoaDon(String maHoaDon) {
        String sql = """
        SELECT DISTINCT KH.HoTen, KH.SoDienThoai, KH.CCCD
        FROM HoaDon HD 
        JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
        JOIN Ve V ON V.MaVe = CT.MaVe
        JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
        JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
        WHERE HD.MaHD = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHoaDon);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.hoTen = rs.getString("HoTen");
                kh.sdt = rs.getString("SoDienThoai");
                kh.soCCCD = rs.getString("CCCD");
                // Các trường khác để mặc định
                return kh;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<KhachHang> timKhachHangTheoSoDienThoai(String giaTriTimKiem) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        String sql = """
        SELECT KH.HoTen, KH.SoDienThoai, KH.CCCD, HD.MaHD
        FROM HoaDon HD 
        JOIN KhachHang KH ON HD.MaKhachHang = KH.MaKhachHang
        WHERE KH.SoDienThoai = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.hoTen = rs.getString("HoTen");
                kh.sdt = rs.getString("SoDienThoai");
                kh.soCCCD = rs.getString("CCCD");
                // Có thể thêm MaHD vào nếu cần (tạo thêm thuộc tính hoặc dùng Map)
                danhSach.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public static ArrayList<KhachHang> timKhachHangTheoCCCD(String giaTriTimKiem) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        String sql = """
        SELECT KH.HoTen, KH.SoDienThoai, KH.CCCD, HD.MaHD
        FROM HoaDon HD 
        JOIN KhachHang KH ON HD.MaKhachHang = KH.MaKhachHang
        WHERE KH.CCCD = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.hoTen = rs.getString("HoTen");
                kh.sdt = rs.getString("SoDienThoai");
                kh.soCCCD = rs.getString("CCCD");
                // Nếu cần MaHD, thêm thuộc tính hoặc dùng class khác
                danhSach.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // Lọc Khách Hàng theo SĐT + Tháng/Năm hóa đơn
    public static ArrayList<KhachHang> timKhachHangTheoSDTLocThangNam(String sdt, int thang, int nam) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        String sql = """
            SELECT DISTINCT HD.MaHD, KH.MaKhachHang, KH.HoTen, KH.SoDienThoai, KH.CCCD
            FROM KhachHang KH
            JOIN HoaDon HD ON KH.MaKhachHang = HD.MaKhachHang
            WHERE KH.SoDienThoai = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
            """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sdt);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang(); // Giả sử có setters hoặc constructor phù hợp
                kh.setHoTen(rs.getString("HoTen"));
                kh.setSdt(rs.getString("SoDienThoai"));
                kh.setSoCCCD(rs.getString("CCCD"));
                danhSach.add(kh);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }

    // Lọc Khách Hàng theo CCCD + Tháng/Năm hóa đơn
    public static ArrayList<KhachHang> timKhachHangTheoCCCDLocThangNam(String cccd, int thang, int nam) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        String sql = """
            SELECT DISTINCT HD.MaHD, KH.MaKhachHang, KH.HoTen, KH.SoDienThoai, KH.CCCD
            FROM KhachHang KH
            JOIN HoaDon HD ON KH.MaKhachHang = HD.MaKhachHang
            WHERE KH.CCCD = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
                
            """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setHoTen(rs.getString("HoTen"));
                kh.setSdt(rs.getString("SoDienThoai"));
                kh.setSoCCCD(rs.getString("CCCD"));
                danhSach.add(kh);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }


}