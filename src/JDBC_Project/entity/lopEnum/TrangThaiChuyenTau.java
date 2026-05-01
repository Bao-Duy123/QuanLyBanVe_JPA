package JDBC_Project.entity.lopEnum;

public enum TrangThaiChuyenTau {

    DANG_MO_BAN_VE("Chờ khởi hành"),
    DA_HUY("Đã hủy"),
    CHUA_MO_BAN_VE("Chưa mở bán vé"),
    DA_DEN("Đã đến");

    private final String tenHienThi;

    TrangThaiChuyenTau(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }
    //Không dùng tên hiển thị nữa dùng trực tiếp enum như thế nào




    public String getTenHienThi() {
        return tenHienThi;
    }

    // Phương thức chuyển đổi tùy chỉnh
    public static TrangThaiChuyenTau fromString(String text) {
        for (TrangThaiChuyenTau b : TrangThaiChuyenTau.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái: " + text);
    }
}