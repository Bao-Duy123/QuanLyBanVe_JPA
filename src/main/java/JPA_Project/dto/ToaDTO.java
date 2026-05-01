package JPA_Project.dto;

import java.time.LocalDate;

/**
 * DTO chứa thông tin toa tàu trong một chuyến tàu.
 */
public record ToaDTO(
    String maToa,         // Mã toa (ví dụ: "SPT2")
    String tenLoaiToa,    // Tên loại toa (ví dụ: "Ghế ngồi mềm", "Giường nằm")
    String loaiToa,       // Mã loại toa (ví dụ: "G_NGAM", "G_GHE")
    double heSo,          // Hệ số giá của loại toa
    int soLuongGhe,      // Số lượng ghế/giường trong toa
    String viTriToa      // Vị trí toa (đầu, giữa, cuối)
) {}
