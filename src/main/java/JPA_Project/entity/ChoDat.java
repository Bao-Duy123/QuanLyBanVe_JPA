package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ChoDat")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChoDat {
    @Id
    @Column(name = "MaCho", columnDefinition = "NVARCHAR(20)")
    private String maCho;

    @Column(name = "MaToa", columnDefinition = "NVARCHAR(20)")
    private String maToa;

    @ManyToOne
    @JoinColumn(name = "MaToa", referencedColumnName = "MaToa", insertable = false, updatable = false)
    private Toa toa;

    @Column(name = "SoCho", columnDefinition = "NVARCHAR(10)")
    private String soCho;

    @Column(name = "Khoang")
    private Integer khoang;

    @Column(name = "Tang")
    private Integer tang;

    @Column(name = "MaChuyenTau", columnDefinition = "NVARCHAR(20)")
    private String maChuyenTau;

    @Transient
    private boolean daDat;

    @OneToMany(mappedBy = "choDat")
    @Builder.Default
    private List<Ve> danhSachVe = new ArrayList<>();

    public ChoDat(String maCho, String maToa, String soCho, int khoang, int tang) {
        this.maCho = maCho;
        this.maToa = maToa;
        this.soCho = soCho;
        this.khoang = khoang;
        this.tang = tang;
    }
}
