package JDBC_Project.entity;

import java.util.Objects;

public class ChiTietHoaDon {
    private String maHD;
    private String maVe;
    private double donGia;
    private int soLuong;

    public ChiTietHoaDon(String maHD, String maVe, int soLuong) {
        this.maHD = maHD;
        this.maVe = maVe;
        this.soLuong = soLuong;
    }

    public ChiTietHoaDon(String maHD, String maVe, int soLuong, double donGia) {
        this.maHD = maHD;
        this.maVe = maVe;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }


    public ChiTietHoaDon() {
    }

    // Getters và Setters
    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {return this.donGia;}

    public void setDonGia(double donGia) {this.donGia = donGia;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return soLuong == that.soLuong && Objects.equals(maHD, that.maHD) && Objects.equals(maVe, that.maVe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHD, maVe, soLuong);
    }
}