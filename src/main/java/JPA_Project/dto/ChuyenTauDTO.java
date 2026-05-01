package JPA_Project.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO chứa thông tin chuyến tàu khi tìm kiếm.
 */
public record ChuyenTauDTO(
    String maChuyenTau,       // Mã chuyến tàu
    String maTuyen,          // Mã tuyến
    String tenTuyen,         // Tên tuyến
    String maTau,            // Mã tàu (số hiệu)
    String tenGaDi,          // Tên ga đi
    String tenGaDen,         // Tên ga đến
    String maGaDi,           // Mã ga đi
    String maGaDen,          // Mã ga đến
    LocalDate ngayKhoiHanh,  // Ngày khởi hành
    LocalTime gioKhoiHanh,    // Giờ khởi hành
    LocalDate ngayDenDuKien,  // Ngày đến dự kiến
    LocalTime gioDenDuKien,   // Giờ đến dự kiến
    int soToa,               // Số toa của tàu
    String trangThai         // Trạng thái chuyến tàu
) {
    /**
     * Lấy thời gian khởi hành dạng chuỗi.
     */
    public String getThoiGianKhoiHanh() {
        return String.format("%s ngày %s",
            gioKhoiHanh != null ? gioKhoiHanh.toString() : "--:--",
            ngayKhoiHanh != null ? ngayKhoiHanh.toString() : "---");
    }

    /**
     * Lấy tuyến đường dạng chuỗi.
     */
    public String getTuyenDuong() {
        return tenGaDi + " → " + tenGaDen;
    }
}
