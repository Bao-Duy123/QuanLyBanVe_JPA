package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HoaDon")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    @Id
    @Column(name = "MaHD")
    private String maHD;

    @Column(name = "MaKhachHang")
    private String maKhachHang;

    @Column(name = "MaNVLap")
    private String maNVLap;

    @Column(name = "MaKM")
    private String maKM;

    @Column(name = "TongTien")
    private double tongTien;

    @Column(name = "NgayLap")
    private LocalDateTime ngayLap;

    @Column(name = "PhuongThuc", columnDefinition = "NVARCHAR(50)")
    private String phuongThuc;

    @Column(name = "LoaiHoaDon", columnDefinition = "NVARCHAR(50)")
    private String loaiHoaDon;

    @Column(name = "TongCong")
    private double tongCong;

    @Column(name = "MaHoaDon_Goc")
    private String maHoaDon_Goc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", referencedColumnName = "MaKhachHang", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNVLap", referencedColumnName = "MaNV", insertable = false, updatable = false)
    private NhanVien nhanVienLap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKM", referencedColumnName = "MaKM", insertable = false, updatable = false)
    private KhuyenMai khuyenMai;

    @OneToMany(mappedBy = "hoaDon")
    @Builder.Default
    private List<ChiTietHoaDon> chiTietHoaDons = new ArrayList<>();
}
