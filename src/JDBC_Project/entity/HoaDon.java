package JDBC_Project.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class HoaDon {
    private String maHD;
    private String maKhachHang;
    private String maNVLap;
    private String maKM;
    private double tongTien;
    private LocalDateTime ngayLap;
    private String phuongThuc;
    private String loaiHoaDon;
    private double tongCong;
    private String maHoaDon_Goc;

    public HoaDon(String maHD, String maKhachHang, String maNVLap, String maKM,
                  double tongTien, LocalDateTime ngayLap, String phuongThuc,
                  String loaiHoaDon) {
        this.maHD = maHD;
        this.maKhachHang = maKhachHang;
        this.maNVLap = maNVLap;
        this.maKM = maKM;
        this.tongTien = tongTien;
        this.ngayLap = ngayLap;
        this.phuongThuc = phuongThuc;
        this.loaiHoaDon = loaiHoaDon;
    }

    public HoaDon(String maHoaDon, LocalDateTime ngayLap, String maKhachHang, String nhanVienLap, String maKM, double tongCong, double tongTien, String phuongThucThanhToan, String maHoaDon_Goc, String loaiHoaDon) {
        this.setMaHD(maHoaDon);
        this.setNgayLap(ngayLap);
        this.setMaKhachHang(maKhachHang);
        this.setMaNVLap(nhanVienLap);
        this.setMaKM(maKM);
        this.setTongCong(tongCong);
        this.setTongTien(tongTien);
        this.setPhuongThuc(phuongThucThanhToan);
        this.setMaHoaDon_Goc(maHoaDon_Goc);
        this.setLoaiHoaDon(loaiHoaDon);
    }

    // Constructor mặc định
    public HoaDon() {
    }

    // Getters và Setters
    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getMaNVLap() { return maNVLap; }
    public void setMaNVLap(String maNVLap) { this.maNVLap = maNVLap; }
    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }
    public String getLoaiHoaDon() { return loaiHoaDon; }
    public void setLoaiHoaDon(String loaiHoaDon) { this.loaiHoaDon = loaiHoaDon; }
    public double getTongCong() { return tongCong; }
    public void setTongCong(double tongCong) { this.tongCong = tongCong; }
    public String getMaHoaDon_Goc() { return maHoaDon_Goc; }
    public void setMaHoaDon_Goc(String maHoaDon_Goc) { this.maHoaDon_Goc = maHoaDon_Goc; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHD, hoaDon.maHD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHD);
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHD='" + maHD + '\'' +
                ", maKhachHang='" + maKhachHang + '\'' +
                ", maNVLap='" + maNVLap + '\'' +
                ", maKM='" + maKM + '\'' +
                ", tongTien=" + tongTien +
                ", ngayLap=" + ngayLap +
                ", phuongThuc='" + phuongThuc + '\'' +
                ", loaiHoaDon='" + loaiHoaDon + '\'' +
                '}';
    }
}