package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "TaiKhoan")
public class TaiKhoan {
    @Id
    @Column(name = "MaNV")
    private String maNV;

    @Column(name = "TenDangNhap")
    private String tenDangNhap;

    @Column(name = "MatKhau")
    private String matKhau;

    @Column(name = "NgayTao")
    private LocalDate ngayTao;

    @Column(name = "TrangThai")
    private String trangThai;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "MaNV", referencedColumnName = "MaNV")
    private NhanVien nhanVien;

    public TaiKhoan(String tenDangNhap, String maNV, String matKhau, LocalDate ngayTao, String trangThai) {
        this.tenDangNhap = tenDangNhap;
        this.maNV = maNV;
        this.matKhau = matKhau;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
    }

    public TaiKhoan(String tenDangNhap, String matKhau, LocalDate ngayTao, String trangThai, NhanVien nhanVien) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.nhanVien = nhanVien;
        this.maNV = nhanVien != null ? nhanVien.getMaNV() : null;
    }
}
