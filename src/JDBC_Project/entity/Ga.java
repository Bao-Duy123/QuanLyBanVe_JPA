package JDBC_Project.entity;

public class Ga {
	public String maGa;
	public String tenGa;
	public String diaChi;
	
	public Ga(String maGa, String tenGa, String diaChi) {
		super();
		this.maGa = maGa;
		this.tenGa = tenGa;
		this.diaChi = diaChi;
	}
    public Ga(String maGa) {
        super();
        this.maGa = maGa;
    }
	public Ga(){};
	public String getMaGa() {
		return maGa;
	}
	public void setMaGa(String maGa) {
		this.maGa = maGa;
	}
	public String getTenGa() {
		return tenGa;
	}
	public void setTenGa(String tenGa) {
		this.tenGa = tenGa;
	}
	public String getDiaChi() {
		return diaChi;
	}
	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

    @Override
    public String toString(){
        return tenGa;
    }
}
