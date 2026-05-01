package JDBC_Project.entity;

public class Ve {
    // 1. Thuộc tính chính
    private String maVe; // Thay 'id' thành 'maVe' để nhất quán với CSDL
    private double giaVe; // Thay 'gia' thành 'giaVe'
    private String trangThai;
    private String maLoaiVe;

    // 2. Thuộc tính Khóa ngoại (Sử dụng cho DAO INSERT/UPDATE)
    private String maKhachHang; // Thêm
    private String maChuyenTau; // Thêm
    private String maChoDat;    // Thêm
    private String maNV;        // Thêm (MaNV/NhanVien tạo vé)

    // 3. Thuộc tính tiện ích (Sử dụng cho UI/Hiển thị)
    private String tenKhachHang; // Thay 'khachHang' thành 'tenKhachHang'
    private int soGhe;

    // 4. Thuộc tính Thực thể Chi tiết (Sử dụng cho DAO GET/Hiển thị đầy đủ)
    private KhachHang khachHangChiTiet;
    private ChuyenTau chuyenTauChiTiet;
    private ChoDat choDatChiTiet; // Thêm thuộc tính trạng thái vé

    // Default constructor
    public Ve() {
    }

    // Constructor with essential fields
    public Ve(String maVe, double giaVe, String trangThai, String maLoaiVe) {
        this.maVe = maVe;
        this.giaVe = giaVe;
        this.trangThai = trangThai;
        this.maLoaiVe = maLoaiVe;
    }

    // Full constructor
    public Ve(String maVe, double giaVe, String trangThai, String maLoaiVe,
              String maKhachHang, String maChuyenTau, String maChoDat, String maNV,
              String tenKhachHang, int soGhe,
              KhachHang khachHangChiTiet, ChuyenTau chuyenTauChiTiet, ChoDat choDatChiTiet) {
        this.maVe = maVe;
        this.giaVe = giaVe;
        this.trangThai = trangThai;
        this.maLoaiVe = maLoaiVe;
        this.maKhachHang = maKhachHang;
        this.maChuyenTau = maChuyenTau;
        this.maChoDat = maChoDat;
        this.maNV = maNV;
        this.tenKhachHang = tenKhachHang;
        this.soGhe = soGhe;
        this.khachHangChiTiet = khachHangChiTiet;
        this.chuyenTauChiTiet = chuyenTauChiTiet;
        this.choDatChiTiet = choDatChiTiet;
    }
    // Constructor đầy đủ không bao gồm thuộc tính tiện ích và chi tiết
        public Ve(String maVe, String maChuyenTau, String maChoDat, String maNV, String maKhachHang,
              String maLoaiVe, double giaVe, String trangThai) {
        this.maVe = maVe;
        this.maChuyenTau = maChuyenTau;
        this.maChoDat = maChoDat;
        this.maNV = maNV;
        this.maKhachHang = maKhachHang;
        this.maLoaiVe = maLoaiVe;
        this.giaVe = giaVe;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public double getGiaVe() {
        return giaVe;
    }

    public void setGiaVe(double giaVe) {
        this.giaVe = giaVe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaLoaiVe() {
        return maLoaiVe;
    }

    public void setMaLoaiVe(String maLoaiVe) {
        this.maLoaiVe = maLoaiVe;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getMaChuyenTau() {
        return maChuyenTau;
    }

    public void setMaChuyenTau(String maChuyenTau) {
        this.maChuyenTau = maChuyenTau;
    }

    public String getMaChoDat() {
        return maChoDat;
    }

    public void setMaChoDat(String maChoDat) {
        this.maChoDat = maChoDat;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public int getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(int soGhe) {
        this.soGhe = soGhe;
    }

    public KhachHang getKhachHangChiTiet() {
        return khachHangChiTiet;
    }

    public void setKhachHangChiTiet(KhachHang khachHangChiTiet) {
        this.khachHangChiTiet = khachHangChiTiet;
    }

    public ChuyenTau getChuyenTauChiTiet() {
        return chuyenTauChiTiet;
    }

    public void setChuyenTauChiTiet(ChuyenTau chuyenTauChiTiet) {
        this.chuyenTauChiTiet = chuyenTauChiTiet;
    }

    public ChoDat getChoDatChiTiet() {
        return choDatChiTiet;
    }

    public void setChoDatChiTiet(ChoDat choDatChiTiet) {
        this.choDatChiTiet = choDatChiTiet;
    }
    //toString
    @Override
    public String toString() {
        return "Ve{" +
                "maVe='" + maVe + '\'' +
                ", giaVe=" + giaVe +
                ", trangThai='" + trangThai + '\'' +
                ", maLoaiVe='" + maLoaiVe + '\'' +
                ", maKhachHang='" + maKhachHang + '\'' +
                ", maChuyenTau='" + maChuyenTau + '\'' +
                ", maChoDat='" + maChoDat + '\'' +
                ", maNV='" + maNV + '\'' +
                ", tenKhachHang='" + tenKhachHang + '\'' +
                ", soGhe=" + soGhe +
                ", khachHangChiTiet=" + khachHangChiTiet +
                ", chuyenTauChiTiet=" + chuyenTauChiTiet +
                ", choDatChiTiet=" + choDatChiTiet +
                '}';
    }
}
