package JPA_Project.dto;

import java.math.BigDecimal;

/**
 * DTO chứa thông tin vé cần tạo khi bán vé.
 * Kết hợp thông tin ghế và hành khách.
 */
public record TicketDTO(
    String maChoDat,      // Mã chỗ đặt (ví dụ: "SPT2-1")
    PassengerDTO khachHang, // Thông tin hành khách
    String maLoaiVe,      // Mã loại vé
    BigDecimal giaVe,     // Giá vé đã tính (có thể null nếu chưa tính)
    boolean daChon        // Đã chọn hay chưa
) {
    /**
     * Tạo TicketDTO mới với hành khách được cập nhật.
     */
    public TicketDTO withKhachHang(PassengerDTO newKhachHang) {
        return new TicketDTO(this.maChoDat, newKhachHang, this.maLoaiVe, this.giaVe, this.daChon);
    }

    /**
     * Tạo TicketDTO mới với giá vé được cập nhật.
     */
    public TicketDTO withGiaVe(BigDecimal newGiaVe) {
        return new TicketDTO(this.maChoDat, this.khachHang, this.maLoaiVe, newGiaVe, this.daChon);
    }

    /**
     * Tạo TicketDTO mới với trạng thái chọn được cập nhật.
     */
    public TicketDTO withDaChon(boolean newDaChon) {
        return new TicketDTO(this.maChoDat, this.khachHang, this.maLoaiVe, this.giaVe, newDaChon);
    }
}
