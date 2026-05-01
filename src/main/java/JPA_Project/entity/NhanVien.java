package JPA_Project.entity;

import java.time.LocalDate;
import java.sql.Date;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVien {

    @Id
    @Column(name = "MaNV", columnDefinition = "NVARCHAR(20)")
    private String maNV;

    @Column(name = "HoTen", columnDefinition = "NVARCHAR(100)")
    private String hoTen;

    @Column(name = "SoCCCD")
    private String soCCCD;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "Email", columnDefinition = "NVARCHAR(100)")
    private String email;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "GioiTinh", columnDefinition = "NVARCHAR(10)")
    private String gioiTinh;

    @Column(name = "DiaChi", columnDefinition = "NVARCHAR(255)")
    private String diaChi;

    @Column(name = "NgayVaoLam")
    private LocalDate ngayVaoLam;

    @Column(name = "ChucVu", columnDefinition = "NVARCHAR(50)")
    private String chucVu;

    @OneToOne(mappedBy = "nhanVien")
    private TaiKhoan taiKhoan;

    @OneToMany(mappedBy = "nhanVienLap")
    @Builder.Default
    private List<HoaDon> danhSachHoaDonLap = new ArrayList<>();

    @OneToMany(mappedBy = "nhanVien")
    @Builder.Default
    private List<Ve> danhSachVe = new ArrayList<>();

    @OneToMany(mappedBy = "nhanVien")
    @Builder.Default
    private List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();

    public NhanVien(String maNV, String hoTen, String sdt) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.sdt = sdt;
    }

    public NhanVien(String maNV, String hoTen, String cccd, String diaChi, String sdt) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soCCCD = cccd;
        this.diaChi = diaChi;
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

    public NhanVien(String maNV, String hoTen, String soCCCD, String rolePlaceholder, String email, String sdt,
                    String gioiTinh, String diaChi, Date ngayVaoLamSql, String chucVu) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.email = email;
        this.sdt = sdt;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.ngayVaoLam = ngayVaoLamSql != null ? ngayVaoLamSql.toLocalDate() : null;
        this.chucVu = chucVu;
    }
}
