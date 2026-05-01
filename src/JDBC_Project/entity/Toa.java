package JDBC_Project.entity;

import java.util.Objects;

public class Toa {
    private String maToa;
    private Tau tau;        // Thực thể Tàu (Quan hệ n-1)
    private String loaiToa;

    // --- CONSTRUCTORS ---

    public Toa() {
    }

    public Toa(String maToa) {
        this.maToa = maToa;
    }

    public Toa(String maToa, Tau tau, String loaiToa) {
        this.maToa = maToa;
        this.tau = tau;
        this.loaiToa = loaiToa;
    }

    // --- GETTERS VÀ SETTERS ---

    public String getMaToa() {
        return maToa;
    }

    public void setMaToa(String maToa) {
        this.maToa = maToa;
    }

    public Tau getTau() {
        return tau;
    }

    public void setTau(Tau tau) {
        this.tau = tau;
    }




    public String getLoaiToa() {
        return loaiToa;
    }

    public void setLoaiToa(String loaiToa) {
        this.loaiToa = loaiToa;
    }





    // --- PHƯƠNG THỨC BỔ TRỢ ---

    @Override
    public String toString() {
        return "Toa{" +
                "maToa='" + maToa + '\'' +
                ", tau=" + (tau != null ? tau.getSoHieu() : "null") +
                ", loaiToa='" + loaiToa + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Toa toa = (Toa) o;
        return Objects.equals(maToa, toa.maToa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maToa);
    }
}