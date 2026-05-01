package JDBC_Project.entity;


public class ChoDat {
        private String maCho;
        private String maToa;
        private String soCho;
        private int khoang;
        private int tang;
        private boolean daDat; // Trạng thái đặt vé tạm thời trên chuyến tàu cụ thể

        public ChoDat() {}

    public ChoDat(String maCho, String maToa, String soCho, int khoang, int tang) {
        this.maCho = maCho;
        this.maToa = maToa;
        this.soCho = soCho;
        this.khoang = khoang;
        this.tang = tang;
    }

    public String getMaCho() {
        return maCho;
    }

    public void setMaCho(String maCho) {
        this.maCho = maCho;
    }

    public String getMaToa() {
        return maToa;
    }

    public void setMaToa(String maToa) {
        this.maToa = maToa;
    }

    public String getSoCho() {
        return soCho;
    }

    public void setSoCho(String soCho) {
        this.soCho = soCho;
    }

    public int getKhoang() {
        return khoang;
    }

    public void setKhoang(int khoang) {
        this.khoang = khoang;
    }

    public int getTang() {
        return tang;
    }

    public void setTang(int tang) {
        this.tang = tang;
    }

    public boolean isDaDat() {
        return daDat;
    }

    public void setDaDat(boolean daDat) {
        this.daDat = daDat;
    }
    @Override
    public String toString() {
        return "ChoDat{" +
                "maCho='" + maCho + '\'' +
                ", maToa='" + maToa + '\'' +
                ", soCho='" + soCho + '\'' +
                ", daDat=" + daDat +
                '}';
    }
}

