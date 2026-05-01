package JDBC_Project.entity;

public class LoaiVe {
    private String maLoaiVe;
    private String tenLoai;
    private double mucGiamGia; // Hệ số giảm giá (ví dụ: 0.75)
    private int tuoiMin;       // Tuổi tối thiểu (>= 0)
    private int tuoiMax;       // Tuổi tối đa (<= 999)

    // Constructor mặc định
    public LoaiVe() {
    }

    // Constructor đầy đủ
    public LoaiVe(String maLoaiVe, String tenLoai, double mucGiaGiam, int tuoiMin, int tuoiMax) {
        this.maLoaiVe = maLoaiVe;
        this.tenLoai = tenLoai;
        this.mucGiamGia = mucGiaGiam;
        this.tuoiMin = tuoiMin;
        this.tuoiMax = tuoiMax;
    }

    // Getters
    public String getMaLoaiVe() { return maLoaiVe; }
    public String getTenLoai() { return tenLoai; }
    public double getMucGiamGia() { return mucGiamGia; }
    public int getTuoiMin() { return tuoiMin; }
    public int getTuoiMax() { return tuoiMax; }

    // Setters
    public void setMaLoaiVe(String maLoaiVe) { this.maLoaiVe = maLoaiVe; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }
    public void setMucGiamGia(double mucGiamGia) { this.mucGiamGia = mucGiamGia; }
    public void setTuoiMin(int tuoiMin) { this.tuoiMin = tuoiMin; }
    public void setTuoiMax(int tuoiMax) { this.tuoiMax = tuoiMax; }

    // Phương thức tiện ích
    public boolean isTuoiHopLe(int tuoi) {
        return tuoi >= tuoiMin && tuoi <= tuoiMax;
    }
}