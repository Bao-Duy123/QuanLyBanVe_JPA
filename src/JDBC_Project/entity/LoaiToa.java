package JDBC_Project.entity;


public class LoaiToa {
    private String maLoaiCho;
    private String tenLoaiCho;
    private double heSo;

    public LoaiToa() {}

    public LoaiToa(String maLoaiCho, String tenLoaiCho, double heSo) {
        this.maLoaiCho = maLoaiCho;
        this.tenLoaiCho = tenLoaiCho;
        this.heSo = heSo;
    }

    // Getters and Setters
    public String getMaLoaiCho() { return maLoaiCho; }
    public void setMaLoaiCho(String maLoaiCho) { this.maLoaiCho = maLoaiCho; }
    public String getTenLoaiCho() { return tenLoaiCho; }
    public void setTenLoaiCho(String tenLoaiCho) { this.tenLoaiCho = tenLoaiCho; }
    public double getHeSo() { return heSo; }
    public void setHeSo(double heSo) { this.heSo = heSo; }
}
