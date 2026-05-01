package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KhuyenMai")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {

    @Id
    @Column(name = "MaKM", columnDefinition = "NVARCHAR(20)")
    private String maKM;

    @Column(name = "TenKM", columnDefinition = "NVARCHAR(100)")
    private String tenKM;

    @Column(name = "LoaiKM", columnDefinition = "NVARCHAR(50)")
    private String loaiKM;

    @Column(name = "GiaTriGiam")
    private BigDecimal giaTriGiam;

    @Column(name = "DKApDung", columnDefinition = "NVARCHAR(50)")
    private String dkApDung;

    @Column(name = "GiaTriDK")
    private BigDecimal giaTriDK;

    @Column(name = "NgayBD")
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKT")
    private LocalDateTime ngayKetThuc;

    @Column(name = "TrangThai", columnDefinition = "NVARCHAR(50)")
    private String trangThai;

    @OneToMany(mappedBy = "khuyenMai")
    @Builder.Default
    private List<HoaDon> danhSachHoaDon = new ArrayList<>();

    public KhuyenMai(String maKM, String tenKM, String loaiKM, BigDecimal giaTriGiam, String dkApDung,
                              BigDecimal giaTriDK, LocalDateTime ngayBD, LocalDateTime ngayKT, String trangThai) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.loaiKM = loaiKM;
        this.giaTriGiam = giaTriGiam;
        this.dkApDung = dkApDung;
        this.giaTriDK = giaTriDK;
        this.ngayBatDau = ngayBD;
        this.ngayKetThuc = ngayKT;
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return this.tenKM;
    }
}
