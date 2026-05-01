package JDBC_Project.entity.lopEnum;

public enum TrangThaiTaiKhoan {
    DANG_HOAT_DONG("Đang hoạt động"),
    DUNG_HOAT_DONG("Dừng hoạt động");

    private final String tenHienThi;

    TrangThaiTaiKhoan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }


    public String getTenHienThi() {
        return tenHienThi;
    }

    // Phương thức chuyển đổi tùy chỉnh
    public static TrangThaiTaiKhoan fromString(String text) {
        for (TrangThaiTaiKhoan b : TrangThaiTaiKhoan.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái: " + text);
    }
}
