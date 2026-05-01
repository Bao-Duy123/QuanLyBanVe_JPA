package JDBC_Project.entity;

public class Tau {

    private String soHieu;
    private String TrangThai;

    public Tau() {
    }

    public Tau(String soHieu, String trangThai) {
        this.soHieu = soHieu;
        TrangThai = trangThai;
    }

    public String getSoHieu() {
        return soHieu;
    }

    public void setSoHieu(String soHieu) {
        this.soHieu = soHieu;
    }

    public String getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(String trangThai) {
        TrangThai = trangThai;
    }
}
