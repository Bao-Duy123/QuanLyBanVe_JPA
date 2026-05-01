package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Toa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Toa {
    @Id
    @Column(name = "MaToa", columnDefinition = "NVARCHAR(20)")
    private String maToa;

    @ManyToOne
    @JoinColumn(name = "SoHieuTau", referencedColumnName = "SoHieu")
    private Tau tau;

    @Column(name = "MaLoaiToa", columnDefinition = "NVARCHAR(20)")
    private String loaiToa;

    @ManyToOne
    @JoinColumn(name = "MaLoaiToa", referencedColumnName = "MaLoaiToa", insertable = false, updatable = false)
    private LoaiToa loaiToaRef;

    @OneToMany(mappedBy = "toa")
    @Builder.Default
    private List<ChoDat> danhSachChoDat = new ArrayList<>();

    public Toa(String maToa) {
        this.maToa = maToa;
    }

    public Toa(String maToa, Tau tau, String loaiToa) {
        this.maToa = maToa;
        this.tau = tau;
        this.loaiToa = loaiToa;
    }
}
