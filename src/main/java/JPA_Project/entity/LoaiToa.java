package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LoaiToa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoaiToa {
    @Id
    @Column(name = "MaLoaiToa", columnDefinition = "NVARCHAR(20)")
    private String maLoaiToa;

    @Column(name = "TenLoaiToa", columnDefinition = "NVARCHAR(50)")
    private String tenLoaiToa;

    @Column(name = "HeSo")
    private double heSo;

    @OneToMany(mappedBy = "loaiToaRef")
    @Builder.Default
    private List<Toa> danhSachToa = new ArrayList<>();

    public LoaiToa(String maLoaiToa, String tenLoaiToa, double heSo) {
        this.maLoaiToa = maLoaiToa;
        this.tenLoaiToa = tenLoaiToa;
        this.heSo = heSo;
    }
    
    @Override
    public String toString() {
        return tenLoaiToa != null ? tenLoaiToa : maLoaiToa;
    }
}
