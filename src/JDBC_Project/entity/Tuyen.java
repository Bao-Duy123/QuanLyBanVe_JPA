package JDBC_Project.entity;

import java.util.Objects;

/**
 * Entity Tuyen: Quản lý thông tin cơ bản về một tuyến đường sắt.
 */
public class Tuyen {
    private String maTuyen;
    private String tenTuyen;
    private String gaDau;   // Ga xuất phát của tuyến (ví dụ: 'HN')
    private String gaCuoi;// Ga cuối của tuyến (ví dụ: 'SG')
    private int donGiaKM;

    // Constructors
    public Tuyen(String maTuyen) {
        this.maTuyen = maTuyen;
    }

//    public Tuyen(String maTuyen, String tenTuyen, String gaDau, String gaCuoi) {
//        this.maTuyen = maTuyen;
//        this.tenTuyen = tenTuyen;
//        this.gaDau = gaDau;
//        this.gaCuoi = gaCuoi;
//    }
    public Tuyen(String maTuyen, String tenTuyen, String gaDau, String gaCuoi, int donGiaKM) {
        this.maTuyen = maTuyen;
        this.tenTuyen = tenTuyen;
        this.gaDau = gaDau;
        this.gaCuoi = gaCuoi;
        this.donGiaKM = donGiaKM;
    }

    // Getters and Setters
    public String getMaTuyen() {
        return maTuyen;
    }

    public void setMaTuyen(String maTuyen) {
        this.maTuyen = maTuyen;
    }

    public String getTenTuyen() {
        return tenTuyen;
    }

    public void setTenTuyen(String tenTuyen) {
        this.tenTuyen = tenTuyen;
    }

    public String getGaDau() {
        return gaDau;
    }

    public void setGaDau(String gaDau) {
        this.gaDau = gaDau;
    }

    public String getGaCuoi() {
        return gaCuoi;
    }

    public void setGaCuoi(String gaCuoi) {
        this.gaCuoi = gaCuoi;
    }

    public int getDonGiaKM() {
        return donGiaKM;
    }
    public void setDonGiaKM(int donGiaKM) {
        this.donGiaKM = donGiaKM;
    }

    // Override toString() để hiển thị trong JComboBox/JTable nếu cần
    @Override
    public String toString() {
        return maTuyen + " - " + tenTuyen;
    }

    // Override equals() và hashCode() cho việc so sánh và sử dụng trong Map/Set
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuyen tuyen = (Tuyen) o;
        return Objects.equals(maTuyen, tuyen.maTuyen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTuyen);
    }
}