package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tuyen")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tuyen {
    @Id
    @Column(name = "MaTuyen", columnDefinition = "NVARCHAR(20)")
    private String maTuyen;

    @Column(name = "TenTuyen", columnDefinition = "NVARCHAR(100)")
    private String tenTuyen;

    @Column(name = "GaDau", columnDefinition = "NVARCHAR(20)")
    private String gaDau;

    @Column(name = "GaCuoi", columnDefinition = "NVARCHAR(20)")
    private String gaCuoi;

    @Column(name = "DonGiaKM")
    private int donGiaKM;

    @OneToMany(mappedBy = "tuyen")
    @Builder.Default
    private List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();

    @OneToMany(mappedBy = "tuyen")
    @Builder.Default
    private List<GaTrongTuyen> danhSachGaTrongTuyen = new ArrayList<>();

    public Tuyen(String maTuyen) {
        this.maTuyen = maTuyen;
    }

    @Override
    public String toString() {
        return maTuyen + " - " + tenTuyen;
    }
}
