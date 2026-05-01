package JDBC_Project.entity.lopEnum;

public enum PhuongThucThanhToan {
    TIEN_MAT("Tiền mặt"),
    CHUYEN_KHOANG("Chuyển khoản");

    private final String tenHienThi;

    PhuongThucThanhToan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }
    //Không dùng tên hiển thị nữa dùng trực tiếp enum như thế nào


    public String getTenHienThi() {
        return tenHienThi;
    }

    // Phương thức chuyển đổi tùy chỉnh
    public static PhuongThucThanhToan fromString(String text) {
        for (PhuongThucThanhToan b : PhuongThucThanhToan.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái: " + text);
    }

}
