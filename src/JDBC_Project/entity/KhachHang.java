package JDBC_Project.entity;

import java.time.LocalDate;

public class KhachHang {
    public String maKH;
    public String hoTen;
    public String soCCCD;
    public String sdt;
    public String gioiTinh;
    public LocalDate ngaySinh;

    public KhachHang() {
    }

    public KhachHang(String maKH, String hoTen, String soCCCD, LocalDate ngaySinh, String sdt) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
    }
    public KhachHang(String maKH, String hoTen, String soCCCD, LocalDate ngaySinh, String sdt, String gioiTinh) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.gioiTinh = gioiTinh;
    }
//không có giới tính
    public KhachHang(String maKH, String hoTen, String soCCCD, String sdt) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.sdt = sdt;
    }

    //Viết ràng buộc vào đây
    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
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
    // Getters/Setters cho thuộc tính mới: NgaySinh
    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    /**
     * Phương thức tính toán tuổi dựa trên ngày sinh và ngày hiện tại.
     * @return Tuổi (số nguyên).
     */
    public int getTuoi() {
        if (this.ngaySinh == null) return 0; // Trả về 0 nếu chưa có ngày sinh
        return java.time.Period.between(this.ngaySinh, LocalDate.now()).getYears();
    }

    /**
     * Setter cho tuổi, không nên sử dụng khi có NgaySinh.
     * Tuy nhiên, giữ lại để tương thích với các đoạn code cũ nếu cần.
     * @deprecated Thay vào đó, hãy sử dụng setNgaySinh.
     */
    @Deprecated
    public void setTuoi(int tuoi) {
        // this.tuoi = tuoi; // Không nên gán trực tiếp
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

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKH='" + maKH + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", soCCCD='" + soCCCD + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", sdt='" + sdt + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                '}';
    }
}
