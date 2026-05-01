package JDBC_Project.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import entity.lopEnum.TrangThaiChuyenTau;

public class ChuyenTau {
    public String maChuyenTau;
    public Tuyen tuyen;
    public String maTau;
    public LocalDate ngayKhoiHanh;
    public LocalTime gioKhoiHanh;
    public Ga gaDi;
    public Ga gaDen;
    public Tau tau;
    public LocalDate ngayDenDuKien;
    public LocalTime gioDenDuKien;
    public NhanVien nhanVien;
    public TrangThaiChuyenTau thct;

    public ChuyenTau() {

    }
    // Constructor đầy đủ với đối tượng Tuyen
    public ChuyenTau(String maChuyenTau, Tuyen tuyen, String maTau,
                     LocalDate ngayKhoiHanh, LocalTime gioKhoiHanh,
                     Ga gaDi, Ga gaDen, Tau tau,
                     LocalDate ngayDenDuKien, LocalTime gioDenDuKien,
                     NhanVien nhanVien, TrangThaiChuyenTau thct) {
        this.maChuyenTau = maChuyenTau;
        this.tuyen = tuyen; // <--- Gán giá trị
        this.maTau = maTau;
        this.ngayKhoiHanh = ngayKhoiHanh;
        this.gioKhoiHanh = gioKhoiHanh;
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.tau = tau;
        this.ngayDenDuKien = ngayDenDuKien;
        this.gioDenDuKien = gioDenDuKien;
        this.nhanVien = nhanVien;
        this.thct = thct;
    }
    //11 tham số
    // Constructor đầy đủ với đối tượng Tuyen
    public ChuyenTau(String maChuyenTau, String maTau,
                     LocalDate ngayKhoiHanh, LocalTime gioKhoiHanh,
                     Ga gaDi, Ga gaDen, Tau tau,
                     LocalDate ngayDenDuKien, LocalTime gioDenDuKien,
                     NhanVien nhanVien, TrangThaiChuyenTau thct) {
        this.maChuyenTau = maChuyenTau;
        this.maTau = maTau;
        this.ngayKhoiHanh = ngayKhoiHanh;
        this.gioKhoiHanh = gioKhoiHanh;
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.tau = tau;
        this.ngayDenDuKien = ngayDenDuKien;
        this.gioDenDuKien = gioDenDuKien;
        this.nhanVien = nhanVien;
        this.thct = thct;
    }
    // Dựa trên lỗi, 6 tham số đầu là String, 3 tham số cuối là Object (null)
    public ChuyenTau(
            String maChuyenTau,
            String maTau,
            String ngayKHString,
            String gioKHString,
            String maGaDi,
            String maGaDen,
            Object thamSo7, // Giả định là Tau (hoặc null)
            Object thamSo8, // Giả định là NhanVien (hoặc null)
            Object thamSo9  // Giả định là TrangThaiChuyenTau (hoặc null)
    ) {
        this.maChuyenTau = maChuyenTau;
        this.maTau = maTau;
        try {
            this.ngayKhoiHanh = LocalDate.parse(ngayKHString);
            this.gioKhoiHanh = LocalTime.parse(gioKHString);
        } catch (Exception e) {
            System.err.println("Lỗi parse ngày/giờ trong constructor ChuyenTau: " + e.getMessage());
            this.ngayKhoiHanh = null;
            this.gioKhoiHanh = null;
        }
        this.gaDi = new Ga(maGaDi, null, null);
        this.gaDen = new Ga(maGaDen, null, null);

        this.tau = (Tau) thamSo7;
        this.nhanVien = (NhanVien) thamSo8;
        this.thct = (TrangThaiChuyenTau) thamSo9;

        this.ngayDenDuKien = null;
        this.gioDenDuKien = null;
    } {
    }

    public Tuyen getTuyen() {
        return tuyen;
    }

    public void setTuyen(Tuyen tuyen) {
        this.tuyen = tuyen;
    }

    public String getMaChuyenTau() {
        return maChuyenTau;
    }

    public void setMaChuyenTau(String maChuyenTau) {
        this.maChuyenTau = maChuyenTau;
    }

    public String getMaTau() {
        return maTau;
    }

    public void setMaTau(String tenChuyenTau) {
        this.maTau = tenChuyenTau;
    }

    public LocalDate getNgayKhoiHanh() {
        return ngayKhoiHanh;
    }

    public void setNgayKhoiHanh(LocalDate ngayKhoiHanh) {
        this.ngayKhoiHanh = ngayKhoiHanh;
    }

    public LocalTime getGioKhoiHanh() {
        return gioKhoiHanh;
    }

    public void setGioKhoiHanh(LocalTime gioKhoiHanh) {
        this.gioKhoiHanh = gioKhoiHanh;
    }

    public Ga getGaDi() {
        return gaDi;
    }

    public void setGaDi(Ga gaDi) {
        this.gaDi = gaDi;
    }

    public Ga getGaDen() {
        return gaDen;
    }

    public void setGaDen(Ga gaDen) {
        this.gaDen = gaDen;
    }

    public Tau getTau() {
        return tau;
    }

    public void setTau(Tau tau) {
        this.tau = tau;
    }

    public LocalDate getNgayDenDuKien() {
        return ngayDenDuKien;
    }

    public void setNgayDenDuKien(LocalDate ngayDenDuKien) {
        this.ngayDenDuKien = ngayDenDuKien;
    }

    public LocalTime getGioDenDuKien() {
        return gioDenDuKien;
    }

    public void setGioDenDuKien(LocalTime gioDenDuKien) {
        this.gioDenDuKien = gioDenDuKien;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public TrangThaiChuyenTau getThct() {
        return thct;
    }

    public void setThct(TrangThaiChuyenTau thct) {
        this.thct = thct;
    }


    @Override
    public String toString() {
        return "ChuyenTau{" +
                "maChuyenTau='" + maChuyenTau + '\'' +
                ", maTau='" + maTau + '\'' +
                ", ngayKhoiHanh=" + ngayKhoiHanh +
                ", gioKhoiHanh=" + gioKhoiHanh +
                ", gaDi=" + gaDi +
                ", gaDen=" + gaDen +
                ", tau=" + tau +
                ", ngayDenDuKien=" + ngayDenDuKien +
                ", gioDenDuKien=" + gioDenDuKien +
                ", nhanVien=" + nhanVien +
                ", thct=" + thct +
                '}';
    }
}
