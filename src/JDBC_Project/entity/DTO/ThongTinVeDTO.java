package JDBC_Project.entity.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ThongTinVeDTO {
    // --- CÁC TRƯỜNG ID QUAN TRỌNG ---
    private String maKhachHang;
    private String maLoaiVe;

    private String maVe;
    private String tenLoaiVe;
    private String hoTen;
    private String cccd;
    private String soDienThoai;
    private LocalDate ngaySinh;

    private String maChuyenTau;
    private String maGaDi;
    private String maGaDen;
    private String gaDi;
    private String gaDen;
    private LocalDate ngayKhoiHanh;
    private LocalTime gioKhoiHanh;
    private LocalDate ngayDenDuKien;
    private LocalTime gioDenDuKien;
    private String soHieuTau;
    private String maToa;
    private String loaiToa;
    private String soCho;
    private String khoang;
    private String tang;
    private double giaVe;
    private String trangThai;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public ThongTinVeDTO() {
    }

    public ThongTinVeDTO(String maVe, String tenLoaiVe, String hoTen, String cccd, String soDienThoai,
                         LocalDate ngaySinh, String maChuyenTau, String gaDi, String gaDen,
                         LocalDate ngayKhoiHanh, LocalTime gioKhoiHanh,
                         LocalDate ngayDenDuKien, LocalTime gioDenDuKien,
                         String soHieuTau, String maToa, String loaiToa, String soCho,
                         String khoang, String tang, double giaVe, String trangThai,
                         String maGaDi, String maGaDen,
                         String maKhachHang, String maLoaiVe) {
        this.maVe = maVe;
        this.tenLoaiVe = tenLoaiVe;
        this.hoTen = hoTen;
        this.cccd = cccd;
        this.soDienThoai = (soDienThoai == null) ? "" : soDienThoai;
        this.ngaySinh = ngaySinh;
        this.maChuyenTau = maChuyenTau;
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.ngayKhoiHanh = ngayKhoiHanh;
        this.gioKhoiHanh = gioKhoiHanh;
        this.ngayDenDuKien = ngayDenDuKien;
        this.gioDenDuKien = gioDenDuKien;
        this.soHieuTau = soHieuTau;
        this.maToa = maToa;
        this.loaiToa = loaiToa;
        this.soCho = soCho;
        this.khoang = (khoang == null) ? "" : khoang;
        this.tang = (tang == null) ? "" : tang;
        this.giaVe = giaVe;
        this.trangThai = trangThai;
        this.maGaDi = maGaDi;
        this.maGaDen = maGaDen;
        this.maKhachHang = maKhachHang;
        this.maLoaiVe = maLoaiVe;
    }

    // --- GETTERS ---
    public String getMaKhachHang() { return maKhachHang; }
    public String getMaLoaiVe() { return maLoaiVe; }

    public String getMaVe() { return maVe; }
    public String getTenLoaiVe() { return tenLoaiVe; }
    public String getHoTen() { return hoTen; }
    public String getCccd() { return cccd; }
    public String getSoDienThoai() { return soDienThoai; }
    public LocalDate getNgaySinh() { return ngaySinh; }

    public String getNgaySinhStr() { return ngaySinh != null ? ngaySinh.format(DATE_FMT) : ""; }

    public String getMaChuyenTau() { return maChuyenTau; }
    public String getGaDi() { return gaDi; }
    public String getGaDen() { return gaDen; }
    public LocalDate getNgayKhoiHanh() { return ngayKhoiHanh; }
    public LocalTime getGioKhoiHanh() { return gioKhoiHanh; }
    public LocalDate getNgayDenDuKien() { return ngayDenDuKien; }
    public LocalTime getGioDenDuKien() { return gioDenDuKien; }
    public String getSoHieuTau() { return soHieuTau; }
    public String getMaToa() { return maToa; }
    public String getLoaiToa() { return loaiToa; }
    public String getSoCho() { return soCho; }
    public String getKhoang() { return khoang; }
    public String getTang() { return tang; }
    public double getGiaVe() { return giaVe; }
    public String getTrangThai() { return trangThai; }
    public String getMaGaDi() { return maGaDi; }
    public String getMaGaDen() { return maGaDen; }

    // --- CÁC HÀM FORMAT NGÀY GIỜ (QUAN TRỌNG CHO UI) ---
    public String getNgayDiStr() {
        return ngayKhoiHanh != null ? ngayKhoiHanh.format(DATE_FMT) : "";
    }
    public String getGioDiStr() {
        return gioKhoiHanh != null ? gioKhoiHanh.format(TIME_FMT) : "";
    }

    // --> ĐÂY LÀ 2 HÀM BỊ THIẾU GÂY LỖI ĐỎ <--
    public String getNgayDenStr() {
        return ngayDenDuKien != null ? ngayDenDuKien.format(DATE_FMT) : "";
    }
    public String getGioDenStr() {
        return gioDenDuKien != null ? gioDenDuKien.format(TIME_FMT) : "";
    }

    @Override
    public String toString() {
        return maVe + " - " + hoTen;
    }
}