package JPA_Project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Ga")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ga {
	@Id
	@Column(name = "MaGa", columnDefinition = "NVARCHAR(20)")
	public String maGa;

	@Column(name = "TenGa", columnDefinition = "NVARCHAR(100)")
	public String tenGa;

	@Column(name = "DiaChi", columnDefinition = "NVARCHAR(255)")
	public String diaChi;

    @OneToMany(mappedBy = "gaDi")
    @Builder.Default
    private List<ChuyenTau> cacChuyenTauDi = new ArrayList<>();

    @OneToMany(mappedBy = "gaDen")
    @Builder.Default
    private List<ChuyenTau> cacChuyenTauDen = new ArrayList<>();

    @OneToMany(mappedBy = "ga")
    @Builder.Default
    private List<GaTrongTuyen> danhSachGaTrongTuyen = new ArrayList<>();
	
	public Ga(String maGa, String tenGa, String diaChi) {
		this.maGa = maGa;
		this.tenGa = tenGa;
		this.diaChi = diaChi;
	}
    public Ga(String maGa) {
        this.maGa = maGa;
    }

	public String getMaGa() { return maGa; }
	public void setMaGa(String maGa) { this.maGa = maGa; }
	public String getTenGa() { return tenGa; }
	public void setTenGa(String tenGa) { this.tenGa = tenGa; }
	public String getDiaChi() { return diaChi; }
	public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    @Override
    public String toString(){ return tenGa; }
}
