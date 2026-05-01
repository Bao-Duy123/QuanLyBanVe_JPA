package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "ChiTietHoaDon")
@IdClass(ChiTietHoaDonId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDon {
    @Id
    @Column(name = "MaHD", columnDefinition = "NVARCHAR(20)")
    private String maHD;

    @Id
    @Column(name = "MaVe", columnDefinition = "NVARCHAR(20)")
    private String maVe;

    @Column(name = "DonGia")
    private double donGia;

    @Column(name = "SoLuong")
    private int soLuong;

    @ManyToOne
    @MapsId("maHD")
    @JoinColumn(name = "MaHD", referencedColumnName = "MaHD", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne
    @MapsId("maVe")
    @JoinColumn(name = "MaVe", referencedColumnName = "MaVe", insertable = false, updatable = false)
    private Ve ve;

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
