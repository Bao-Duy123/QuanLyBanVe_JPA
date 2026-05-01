package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Ve")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ve {
    @Id
    @Column(name = "MaVe", columnDefinition = "NCHAR(10)")
    private String maVe;

    @Column(name = "GiaVe", columnDefinition = "DECIMAL(10,2)")
    private Double giaVe;

    @Column(name = "TrangThai", columnDefinition = "NVARCHAR(50)")
    private String trangThai;

    @Column(name = "MaLoaiVe")
    private String maLoaiVe;

    @Column(name = "MaKhachHang")
    private String maKhachHang;

    @Column(name = "MaChuyenTau")
    private String maChuyenTau;

    @Column(name = "MaChoDat")
    private String maChoDat;

    @Column(name = "MaNV")
    private String maNV;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLoaiVe", referencedColumnName = "MaLoaiVe", insertable = false, updatable = false)
    private LoaiVe loaiVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", referencedColumnName = "MaKhachHang", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaChuyenTau", referencedColumnName = "MaChuyenTau", insertable = false, updatable = false)
    private ChuyenTau chuyenTau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaChoDat", referencedColumnName = "MaCho", insertable = false, updatable = false)
    private ChoDat choDat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNV", referencedColumnName = "MaNV", insertable = false, updatable = false)
    private NhanVien nhanVien;

    @Transient
    private String tenKhachHang;

    @Transient
    private int soGhe;

    @OneToMany(mappedBy = "ve")
    @Builder.Default
    private List<ChiTietHoaDon> chiTietHoaDons = new ArrayList<>();
}
