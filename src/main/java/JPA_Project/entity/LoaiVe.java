package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LoaiVe")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoaiVe {
    @Id
    @Column(name = "MaLoaiVe", columnDefinition = "NVARCHAR(20)")
    private String maLoaiVe;

    @Column(name = "TenLoaiVe", columnDefinition = "NVARCHAR(50)")
    private String tenLoai;

    @Column(name = "MucGiamGia")
    private double mucGiamGia;

    @Column(name = "TuoiMin")
    private int tuoiMin;

    @Column(name = "TuoiMax")
    private int tuoiMax;

    @OneToMany(mappedBy = "loaiVe")
    @Builder.Default
    private List<Ve> danhSachVe = new ArrayList<>();

    public LoaiVe(String maLoaiVe, String tenLoai, double mucGiaGiam, int tuoiMin, int tuoiMax) {
        this.maLoaiVe = maLoaiVe;
        this.tenLoai = tenLoai;
        this.mucGiamGia = mucGiaGiam;
        this.tuoiMin = tuoiMin;
        this.tuoiMax = tuoiMax;
    }

    @Override
    public String toString() {
        return tenLoai != null ? tenLoai : maLoaiVe;
    }

    public String getTenLoaiVe() {
        return tenLoai;
    }
}
