package JPA_Project.dto;

import java.math.BigDecimal;

/**
 * DTO chứa thông tin vé đã tạo sau khi bán.
 */
public record TicketResultDTO(
    String maVe,              // Mã vé đã tạo
    String maChoDat,          // Mã chỗ đặt
    String hoTenKhachHang,    // Họ tên khách hàng
    String tenLoaiVe,         // Tên loại vé
    BigDecimal giaGoc,        // Giá gốc trước giảm
    BigDecimal giaDaGiam,     // Giá sau khi giảm
    BigDecimal tienGiam       // Số tiền được giảm
) {}
