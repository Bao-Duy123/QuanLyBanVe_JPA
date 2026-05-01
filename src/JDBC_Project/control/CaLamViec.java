package JDBC_Project.control;


import entity.NhanVien;

public class CaLamViec {
    // 1. Dữ liệu: Thông tin cá nhân của nhân viên
    private NhanVien nhanVienDangNhap;

    // 2. Mẫu Singleton: Đảm bảo chỉ có một phiên làm việc duy nhất
    private static CaLamViec instance;

    // Ngăn không cho tạo instance mới từ bên ngoài
    private CaLamViec() {}

    // Phương thức tĩnh để lấy instance duy nhất
    public static CaLamViec getInstance() {
        if (instance == null) {
            instance = new CaLamViec();
        }
        return instance;
    }

    // --- Các Phương thức Quản lý Phiên ---

    /**
     * Thiết lập nhân viên cho ca làm việc khi đăng nhập.
     */
    public void batDauCa(NhanVien nv) {
        this.nhanVienDangNhap = nv;
        System.out.println("Ca làm việc bắt đầu cho NV: " + nv.getHoTen());
        // Có thể thêm: this.thoiGianBatDau = LocalDateTime.now();
    }

    /**
     * Kết thúc ca làm việc (đăng xuất).
     */
    public void ketThucCa() {
        this.nhanVienDangNhap = null;
        System.out.println("Ca làm việc đã kết thúc.");
    }

    /**
     * Lấy đối tượng nhân viên đang hoạt động.
     */
    public NhanVien getNhanVienDangNhap() {
        return nhanVienDangNhap;
    }

    // ... Thêm các hàm nghiệp vụ khác (Ví dụ: capNhatDoanhThuCa())
}