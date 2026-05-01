package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GA_TRONG_TUYEN")
@IdClass(GaTrongTuyenId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GaTrongTuyen {
    @Id
    @Column(name = "MaTuyen", columnDefinition = "NVARCHAR(20)")
    private String maTuyen;

    @Id
    @Column(name = "MaGa", columnDefinition = "NVARCHAR(20)")
    private String maGa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTuyen", referencedColumnName = "MaTuyen", insertable = false, updatable = false)
    private Tuyen tuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGa", referencedColumnName = "MaGa", insertable = false, updatable = false)
    private Ga ga;

    @Column(name = "ThuTuGa")
    private int thuTuGa;

    @Column(name = "KhoangCachTichLuy")
    private int khoangCachTichLuy;

    @Column(name = "ThoiGianDiChuyenToiGaTiepTheo")
    private int thoiGianDiDenGaTiepTheo;

    @Column(name = "ThoiGianDung")
    private int thoiGianDung;
}
