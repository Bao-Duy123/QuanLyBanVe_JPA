package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tau")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tau {

    @Id
    @Column(name = "SoHieu", columnDefinition = "NVARCHAR(20)")
    private String soHieu;

    @Column(name = "TrangThai", columnDefinition = "NVARCHAR(50)")
    private String TrangThai;

    @OneToMany(mappedBy = "tau")
    @Builder.Default
    private List<Toa> danhSachToa = new ArrayList<>();

    @OneToMany(mappedBy = "tau")
    @Builder.Default
    private List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();

    public Tau(String soHieu, String trangThai) {
        this.soHieu = soHieu;
        TrangThai = trangThai;
    }
}
