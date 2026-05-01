package JPA_Project.entity;

public enum TrangThaiChuyenTau {
    CHO_KHOI_HANH("Chờ khởi hành"),
    DA_HUY("Đã hủy"),
    CHUA_MO_BAN_VE("Chưa mở bán vé"),
    DA_DEN("Đã đến");

    private final String tenHienThi;

    TrangThaiChuyenTau(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiChuyenTau fromString(String text) {
        if (text == null) return CHO_KHOI_HANH;
        for (TrangThaiChuyenTau b : TrangThaiChuyenTau.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return CHO_KHOI_HANH;
    }
}
