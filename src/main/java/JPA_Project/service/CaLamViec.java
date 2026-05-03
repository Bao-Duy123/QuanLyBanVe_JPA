package JPA_Project.service;

import JPA_Project.entity.NhanVien;

/**
 * CaLamViec - Quản lý phiên làm việc của nhân viên.
 * Sử dụng mẫu Singleton để đảm bảo chỉ có một phiên làm việc duy nhất.
 */
public class CaLamViec {
    private NhanVien nhanVienDangNhap;
    private static CaLamViec instance;

    private CaLamViec() {}

    public static CaLamViec getInstance() {
        if (instance == null) {
            instance = new CaLamViec();
        }
        return instance;
    }

    public void batDauCa(NhanVien nv) {
        this.nhanVienDangNhap = nv;
        System.out.println("Ca làm việc bắt đầu cho NV: " + nv.getHoTen());
    }

    public void ketThucCa() {
        this.nhanVienDangNhap = null;
        System.out.println("Ca làm việc đã kết thúc.");
    }

    public NhanVien getNhanVienDangNhap() {
        return nhanVienDangNhap;
    }

    public String getMaNVDangNhap() {
        return nhanVienDangNhap != null ? nhanVienDangNhap.getMaNV() : null;
    }

    public void reset() {
        instance = null;
    }
}
