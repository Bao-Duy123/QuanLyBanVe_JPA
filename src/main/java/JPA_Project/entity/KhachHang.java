package JPA_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KhachHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHang {
    @Id
    @Column(name = "MaKhachHang", columnDefinition = "NVARCHAR(20)")
    private String maKH;

    @Column(name = "HoTen", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String hoTen;

    @Column(name = "CCCD", unique = true, nullable = false, length = 20)
    private String soCCCD;

    @Column(name = "SoDienThoai", length = 20)
    private String sdt;

    @Column(name = "GioiTinh", length = 10, columnDefinition = "NVARCHAR(10)")
    private String gioiTinh;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @OneToMany(mappedBy = "khachHang")
    @Builder.Default
    private List<Ve> danhSachVe = new ArrayList<>();

    @OneToMany(mappedBy = "khachHang")
    @Builder.Default
    private List<HoaDon> danhSachHoaDon = new ArrayList<>();
}
