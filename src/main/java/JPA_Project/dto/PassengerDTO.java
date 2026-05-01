package JPA_Project.dto;

import java.time.LocalDate;

/**
 * DTO chứa thông tin hành khách (khách hàng) mua vé.
 * Sử dụng immutable record để đảm bảo tính bất biến.
 */
public record PassengerDTO(
    String maLoaiVe,      // Mã loại vé (VT01, VT02, VT03, VT04)
    String hoTen,         // Họ tên hành khách
    String cccd,          // Căn cước công dân
    String sdt,          // Số điện thoại
    LocalDate ngaySinh,  // Ngày sinh
    String gioiTinh      // Giới tính
) {}
