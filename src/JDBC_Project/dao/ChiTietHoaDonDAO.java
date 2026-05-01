package JDBC_Project.dao;

import database.ConnectDB;
import entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    /**
     * Thêm Chi tiết Hóa đơn vào CSDL.
     * Phương thức này được thiết kế để chạy trong một giao dịch (transaction),
     * do đó nó nhận Connection đã có sẵn.
     * * @param conn Kết nối CSDL (đang mở transaction).
     * @param cthd Đối tượng ChiTietHoaDon cần thêm.
     * @return true nếu thêm thành công.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public boolean themChiTietHoaDon(Connection conn, ChiTietHoaDon cthd) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon (MaHD, MaVe, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = null; // Khai báo PreparedStatement bên ngoài try

        try { // Chỉ sử dụng khối try/catch truyền thống
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, cthd.getMaHD());
            pstmt.setString(2, cthd.getMaVe());
            pstmt.setInt(3, cthd.getSoLuong());
            pstmt.setDouble(4, cthd.getDonGia());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm Chi tiết Hóa đơn (MaHD: " + cthd.getMaHD() + ", MaVe: " + cthd.getMaVe() + "):");
            e.printStackTrace();
            throw e;
        } finally {
            // Đóng PreparedStatement, KHÔNG đóng Connection
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // (Có thể thêm các phương thức khác như: getChiTietByMaHD, deleteChiTiet, v.v.)

    // Phương thức đếm số lượng vé theo mã hóa đơn
    public static int demSoLuongVeTheoMaHoaDon(String maHD) {
        int soLuongVe = 0;
        String sql = "SELECT COUNT(*) AS SoLuongVe FROM ChiTietHoaDon WHERE MaHD = ?";

        try (Connection conn = ConnectDB.getConnection(); // Giả sử có lớp kết nối DB
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                soLuongVe = rs.getInt("SoLuongVe");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý ngoại lệ phù hợp (log, throw custom exception, v.v.)
        }

        return soLuongVe;
    }

    // Phương thức lấy thông tin chuyến tàu theo mã hóa đơn
    public static ChuyenTau chuyenTauTheoCTHD(String maHD) {
        ChuyenTau chuyenTau = null;
        String sql = "SELECT DISTINCT HD.MaHD, J.MaChuyenTau, J.MaTau, J.GaDi, J.GaDen, " +
                "J.NgayKhoiHanh, J.GioKhoiHanh, J.NgayDenDuKien, J.GioDenDuKien " +
                "FROM HoaDon HD JOIN Ve V ON HD.MaKhachHang = V.MaKhachHang " +
                "JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau " +
                "WHERE HD.MaHD = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                chuyenTau = new ChuyenTau();

                // Kiểm tra MaHD (giữ nguyên logic bạn có)
                String maHDKiemTra = rs.getString("MaHD");
                if (!maHDKiemTra.equals(maHD)) {
                    return null; // Không khớp → bỏ qua
                }

                // Set dữ liệu
                chuyenTau.setMaChuyenTau(rs.getString("MaChuyenTau"));
                chuyenTau.setMaTau(rs.getString("MaTau"));
                chuyenTau.setNgayKhoiHanh(rs.getObject("NgayKhoiHanh", LocalDate.class));
                chuyenTau.setGioKhoiHanh(rs.getObject("GioKhoiHanh", LocalTime.class));

                Ga gaDi = new Ga();
                gaDi.setTenGa(rs.getString("GaDi"));
                chuyenTau.setGaDi(gaDi);

                Ga gaDen = new Ga();
                gaDen.setTenGa(rs.getString("GaDen"));
                chuyenTau.setGaDen(gaDen);

                chuyenTau.setNgayDenDuKien(rs.getObject("NgayDenDuKien", LocalDate.class));
                chuyenTau.setGioDenDuKien(rs.getObject("GioDenDuKien", LocalTime.class));

                // Các thuộc tính không có trong truy vấn
                chuyenTau.setTau(null);
                chuyenTau.setNhanVien(null);
                chuyenTau.setThct(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chuyenTau; // Trả về 1 object (hoặc null nếu không có)
    }




    public static List<ChiTietHoaDon> timChiTietHoaDonTheoMaHD(String maHD) {
        List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();
        String sql = "SELECT MaHD, MaVe, SoLuong, DonGia FROM ChiTietHoaDon WHERE MaHD = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChiTietHoaDon cthd = new ChiTietHoaDon();

                cthd.setMaHD(rs.getString("MaHD"));
                cthd.setMaVe(rs.getString("MaVe"));
                cthd.setSoLuong(rs.getInt("SoLuong"));
                cthd.setDonGia(rs.getDouble("DonGia"));

                danhSachChiTiet.add(cthd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Có thể throw custom exception hoặc log
        }

        return danhSachChiTiet;
    }

    public static KhuyenMai layThongTinKhuyenMaiTheoMaHD(String maHD) {
        // Luôn tạo object, không trả về null, đảm bảo an toàn.
        KhuyenMai km = new KhuyenMai();

        // Khởi tạo các giá trị mặc định an toàn
        km.setGiaTriGiam(java.math.BigDecimal.ZERO);
        km.setTenKM(" ");
        km.setLoaiKM("NONE"); // Thiết lập loại mặc định để tránh NullPointerException khi hiển thị

        // Lấy thông tin KM: LoaiKM, GiaTriGiam, TenKM
        // Lưu ý: Đảm bảo bảng HoaDon có cột MaKM
        String sql = "SELECT KM.LoaiKM, KM.GiaTriGiam, KM.TenKM " +
                "FROM HoaDon HD LEFT JOIN KhuyenMai KM ON HD.MaKM = KM.MaKM " +
                "WHERE HD.MaHD = ?"; // Dùng HD.MaHD để rõ ràng

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                // Lấy các trường theo cấu trúc mới
                String loaiKMDB = rs.getString("LoaiKM"); // Có thể null nếu MaKM trong Hóa Đơn là NULL
                java.math.BigDecimal giaTriGiamDB = rs.getBigDecimal("GiaTriGiam");
                String tenKM = rs.getString("TenKM");

                if (loaiKMDB != null && giaTriGiamDB != null) {
                    // Trường hợp có Khuyến mãi được áp dụng

                    km.setLoaiKM(loaiKMDB);
                    km.setTenKM(tenKM != null ? tenKM.trim() : "Ưu đãi");

                    // GIẢI QUYẾT VẤN ĐỀ LOGIC TÍNH TOÁN (0.10 -> 10.0):
                    // Nếu KM là PHAN_TRAM_GIA, giá trị trong CSDL đã là 10.0 (10%)
                    // Nếu KM là CO_DINH, giá trị trong CSDL đã là 50000.00 (50K)

                    // Do đó, chúng ta KHÔNG cần nhân với 100 nữa.
                    km.setGiaTriGiam(giaTriGiamDB);

                } else {
                    // Hóa đơn được tìm thấy nhưng MaKM là NULL (Không áp dụng KM)
                    // Đã được xử lý bởi giá trị mặc định ở đầu phương thức
                }
            } else {
                // Không tìm thấy hóa đơn (Đã được xử lý bởi giá trị mặc định ở đầu phương thức)
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Lỗi CSDL -> Trả về giá trị mặc định (đã thiết lập ở đầu phương thức)
        }

        return km; // Luôn trả về KhuyenMai hợp lệ
    }

    public static KhachHang layKhachHangTheoHoaDon(String maHD) {
        KhachHang khachHang = new KhachHang();
        String sql = "SELECT KH.HoTen, KH.SoDienThoai, KH.GioiTinh " +
                "FROM HoaDon HD JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang " +
                "WHERE MaHD = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                khachHang.setHoTen(rs.getString("HoTen"));
                khachHang.setSdt(rs.getString("SoDienThoai"));
                khachHang.setGioiTinh(rs.getString("GioiTinh"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return khachHang; // Luôn trả về 1 object KhachHang (có dữ liệu hoặc rỗng nếu lỗi)
    }



    public static List<KhachHang> layKhachHangTheoCTHD(String maHD) {
        List<KhachHang> danhSachKhachHang = new ArrayList<>();
        String sql = "SELECT KH.HoTen, KH.CCCD, KH.GioiTinh " +
                "FROM ChiTietHoaDon [CT] JOIN Ve V ON [CT].MaVe = V.MaVe " +
                "JOIN KhachHang KH ON KH.MaKhachHang = V.MaKhachHang " +
                "WHERE [CT].MaHD = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String hoTen = rs.getString("HoTen");
                String cccd = rs.getString("CCCD");
                String gt = rs.getString("GioiTinh");

                KhachHang kh = new KhachHang(); // Giả sử có setters
                kh.setHoTen(hoTen);
                kh.setSoCCCD(cccd);
                kh.setGioiTinh(gt);
                danhSachKhachHang.add(kh);
            }

            // Bỏ qua phần in debug nếu không cần thiết nữa
            // if (danhSachKhachHang.isEmpty()) { ... }
            // for (KhachHang kh : danhSachKhachHang) { ... }

        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy thông tin khách hàng cho hóa đơn " + maHD + ": " + e.getMessage());
            e.printStackTrace();
        }

        return danhSachKhachHang;
    }




    public static HoaDon layHoaDonTheoMaHD(String maHD) {
        HoaDon hd = null;
        String sql = "SELECT NgayLap, TongCong, TongTien, PhuongThuc FROM HoaDon WHERE MaHD = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Lấy LocalDateTime từ cột NgayLap
                LocalDateTime ngayLapDB = rs.getObject("NgayLap", LocalDateTime.class);
                // Chuyển sang dd-MM-yyyy
                String ngayLapFormatted = ngayLapDB.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                double tongCong = rs.getDouble("TongCong");
                double tongTien = rs.getDouble("TongTien");
                String phuongThuc = rs.getString("PhuongThuc");

                hd = new HoaDon();
                hd.setNgayLap(ngayLapDB);
                hd.setTongCong(tongCong);
                hd.setTongTien(tongTien);
                hd.setPhuongThuc(phuongThuc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hd; // Trả về 1 object (null nếu không tìm thấy – nhưng theo bạn là luôn có)
    }

    public static NhanVien layTenNhanVienTheoMaHD(String maHD) {
        NhanVien nhanVien = new NhanVien(); // Luôn tạo object
        String sql = "SELECT NV.HoTen " +
                "FROM HoaDon HD JOIN NhanVien NV ON HD.MaNVLap = NV.MaNV " +
                "WHERE HD.MaHD = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nhanVien.setHoTen(rs.getString("HoTen"));
                // Các thuộc tính khác để null hoặc rỗng
            } else {
                nhanVien.setHoTen("Không xác định"); // Nếu không tìm thấy
            }

        } catch (SQLException e) {
            e.printStackTrace();
            nhanVien.setHoTen("Lỗi hệ thống");
        }

        return nhanVien; // Luôn trả về object NhanVien hợp lệ
    }

    public static List<LoaiVe> layLoaiVeTheoMaHD(String maHD) {
        List<LoaiVe> danhSachLoaiVe = new ArrayList<>();
        String sql = "SELECT LV.TenLoaiVe " +
                "FROM Ve V " +
                "JOIN ChiTietHoaDon CT ON V.MaVe = CT.MaVe " +
                "JOIN LoaiVe LV ON LV.MaLoaiVe = V.MaLoaiVe " +
                "WHERE CT.MaHD = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LoaiVe loaiVe = new LoaiVe();
                loaiVe.setTenLoai(rs.getString("TenLoaiVe"));
                // Các thuộc tính khác để null hoặc mặc định
                loaiVe.setMaLoaiVe(null);      // Không có trong truy vấn
                loaiVe.setMucGiamGia(0.0);     // Không có trong truy vấn
                danhSachLoaiVe.add(loaiVe);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSachLoaiVe;
    }
}