package JPA_Project.entity;

import JPA_Project.converter.TrangThaiChuyenTauConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ChuyenTau")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChuyenTau {
    @Id
    @Column(name = "MaChuyenTau", columnDefinition = "NVARCHAR(20)")
    public String maChuyenTau;

    @Column(name = "MaTuyen", columnDefinition = "NVARCHAR(20)")
    public String maTuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTuyen", referencedColumnName = "MaTuyen", insertable = false, updatable = false)
    @ColumnTransformer(read = "TRIM(MaTuyen)")
    public Tuyen tuyen;

    @Column(name = "MaTau")
    public String maTau;

    @Column(name = "NgayKhoiHanh")
    public LocalDate ngayKhoiHanh;

    @Column(name = "GioKhoiHanh")
    public LocalTime gioKhoiHanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GaDi", referencedColumnName = "MaGa")
    public Ga gaDi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GaDen", referencedColumnName = "MaGa")
    public Ga gaDen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTau", referencedColumnName = "SoHieu", insertable = false, updatable = false)
    public Tau tau;

    @Column(name = "NgayDenDuKien")
    public LocalDate ngayDenDuKien;

    @Column(name = "GioDenDuKien")
    public LocalTime gioDenDuKien;

    @Column(name = "MaNV")
    public String maNV;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNV", referencedColumnName = "MaNV", insertable = false, updatable = false)
    public NhanVien nhanVien;

    @Column(name = "TrangThai")
    @Convert(converter = TrangThaiChuyenTauConverter.class)
    public TrangThaiChuyenTau thct;

    @OneToMany(mappedBy = "chuyenTau")
    @Builder.Default
    private List<Ve> danhSachVe = new ArrayList<>();
}
