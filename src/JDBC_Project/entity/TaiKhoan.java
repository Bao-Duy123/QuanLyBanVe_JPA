package JDBC_Project.entity;

import java.time.LocalDate;

public class TaiKhoan {
    private String tenDangNhap;
    private String maNV;
    private String matKhau;
    private LocalDate ngayTao;
    private String trangThai;
    private NhanVien nhanVien; // <--- Thuộc tính đã khai báo

    // 1. CONSTRUCTOR GỐC (ĐÃ SỬA LỖI GÁN ngayTao)
    public TaiKhoan(String tenDangNhap, String maNV, String matKhau, LocalDate ngayTao, String trangThai) {
        this.tenDangNhap = tenDangNhap;
        this.maNV = maNV;
        this.matKhau = matKhau;
        this.ngayTao = ngayTao; // <--- ĐÃ SỬA: Bổ sung gán giá trị
        this.trangThai = trangThai;
        // nhanVien = null
    }

    // 2. CONSTRUCTOR CẦN THIẾT CHO PHƯƠNG THỨC saveEmployee()
    // Giả định thứ tự tham số là (Tên ĐN, Mật Khẩu, Ngày Tạo, Trạng Thái, NhanVien)
    // LƯU Ý: MaNV thường được lấy từ NhanVien.getMaNV()
    public TaiKhoan(String tenDangNhap, String matKhau, LocalDate ngayTao, String trangThai, NhanVien nhanVien) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.nhanVien = nhanVien;
        this.maNV = nhanVien != null ? nhanVien.getMaNV() : null; // Lấy MaNV từ NhanVien
    }

    public TaiKhoan(){}

    // PHƯƠNG THỨC ĐÃ SỬA: trả về biến 'nhanVien'
    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    // Bạn cũng nên thêm setter cho thuộc tính này để gán giá trị:
    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public String getTrangThai() {
        return trangThai;
    }
}