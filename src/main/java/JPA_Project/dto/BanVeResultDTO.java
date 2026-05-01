package JPA_Project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO chứa thông tin kết quả bán vé.
 */
public record BanVeResultDTO(
    boolean thanhCong,            // Trạng thái giao dịch
    String maHoaDon,              // Mã hóa đơn đã tạo
    String maKhachHangDaiDien,    // Mã khách hàng đại diện
    BigDecimal tongTien,          // Tổng tiền hóa đơn
    BigDecimal tienGiamGia,       // Tiền được giảm
    java.util.List<TicketResultDTO> danhSachVe, // Danh sách vé đã tạo
    String loiLoi,                // Lỗi nếu có
    LocalDateTime thoiGianLap     // Thời gian lập hóa đơn
) {
    /**
     * Tạo kết quả thành công.
     */
    public static BanVeResultDTO success(
            String maHoaDon,
            String maKhachHangDaiDien,
            BigDecimal tongTien,
            BigDecimal tienGiamGia,
            java.util.List<TicketResultDTO> danhSachVe) {
        return new BanVeResultDTO(
            true, maHoaDon, maKhachHangDaiDien, tongTien, tienGiamGia,
            danhSachVe, null, LocalDateTime.now()
        );
    }

    /**
     * Tạo kết quả thất bại.
     */
    public static BanVeResultDTO failure(String loiLoi) {
        return new BanVeResultDTO(
            false, null, null, BigDecimal.ZERO, BigDecimal.ZERO,
            java.util.List.of(), loiLoi, null
        );
    }
}
