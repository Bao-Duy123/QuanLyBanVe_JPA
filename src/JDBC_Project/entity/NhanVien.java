package JDBC_Project.entity;

import java.time.LocalDate; // <-- Thêm import cho LocalDate
import java.sql.Date; // Giả định bạn cần dùng sql.Date để tương thích với CSDL

public class NhanVien {
    public String maNV;
    public String hoTen;
    public String soCCCD;
    public LocalDate ngaySinh;
    public String email;
    public String sdt;
    public String gioiTinh;
    public String diaChi;
    public LocalDate ngayVaoLam; // <--- Đã sửa từ String sang LocalDate
    public String chucVu;

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, String sdt) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.sdt = sdt;
    }

    public NhanVien(String maNV, String hoTen, String soCCCD, LocalDate ngaySinh, String email, String sdt,
                    String gioiTinh, String diaChi, LocalDate ngayVaoLam, String chucVu) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.sdt = sdt;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.ngayVaoLam = ngayVaoLam;
        this.chucVu = chucVu;
    }

    public NhanVien(String maNV, String hoTen, String cccd, String diaChi, String sdt) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soCCCD = cccd;
        this.diaChi = diaChi;
        this.sdt = sdt;
    }

    public NhanVien(String maNV, String hoTen, String soCCCD, String rolePlaceholder, String email, String sdt,
                    String gioiTinh, String diaChi, Date ngayVaoLamSql, String chucVu) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.email = email;
        this.sdt = sdt;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        // Chuyển đổi java.sql.Date sang LocalDate (nếu cần)
        this.ngayVaoLam = ngayVaoLamSql != null ? ngayVaoLamSql.toLocalDate() : null;
        this.chucVu = chucVu;
        // Không có ngaySinh trong constructor này, giữ nguyên null
    }



    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

}