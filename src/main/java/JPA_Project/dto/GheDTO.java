package JPA_Project.dto;

import java.time.LocalDate;

/**
 * DTO chứa thông tin ghế đã chọn khi bán vé.
 * Sử dụng immutable record để đảm bảo tính bất biến.
 */
public record GheDTO(
    String maChoDat,      // Mã chỗ đặt (ví dụ: "SPT2-1")
    String maToa,         // Mã toa (ví dụ: "SPT2")
    String soCho,         // Số ghế/giường (ví dụ: "A1", "1")
    Integer khoang,       // Khoang (1, 2, 3) - có thể null
    Integer tang,         // Tầng (1, 2) - có thể null
    boolean daDat         // Đã đặt hay chưa
) {}
