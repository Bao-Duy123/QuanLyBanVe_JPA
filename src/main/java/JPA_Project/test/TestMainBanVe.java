package JPA_Project.test;

import JPA_Project.dto.*;
import JPA_Project.entity.*;
import JPA_Project.repository.*;
import JPA_Project.service.BanVeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * TestMainBanVe - Test class để kiểm tra nghiệp vụ bán vé đầy đủ mà không cần giao diện.
 * 
 * Luồng test:
 * 1. Tìm chuyến tàu DNA -> NTR ngày 02/05/2026
 * 2. Lấy danh sách toa của chuyến tàu
 * 3. Lấy sơ đồ ghế toa 1
 * 4. Tạo thông tin khách hàng (hoặc tìm nếu đã tồn tại)
 * 5. Tạo TicketDTO cho ghế 1
 * 6. Thực hiện bán vé
 * 7. Kiểm tra kết quả Ve và HoaDon được lưu
 */
public class TestMainBanVe {

    // ================================================================================
    // THÔNG TIN TEST
    // ================================================================================
    private static final String MA_NV = "NVBV0001";
    private static final String GA_DI = "DNA";      // Đà Nẵng
    private static final String GA_DEN = "NTR";     // Nha Trang
    private static final LocalDate NGAY_DI = LocalDate.of(2026, 5, 2);
    
    // Thông tin khách hàng
    private static final String TEN_KH = "Duy";
    private static final String CCCD = "111111110000";
    private static final String SDT = "1111111111";
    private static final LocalDate NGAY_SINH = LocalDate.of(2005, 9, 9);
    private static final String GIOI_TINH = "Nam";

    // ================================================================================
    // SERVICES & REPOSITORIES
    // ================================================================================
    private final BanVeService banVeService;
    private final ChuyenTauRepository chuyenTauRepo;
    private final ToaRepository toaRepo;
    private final ChoDatRepository choDatRepo;
    private final KhachHangRepository khachHangRepo;
    private final VeRepository veRepo;
    private final HoaDonRepository hoaDonRepo;

    public TestMainBanVe() {
        this.banVeService = new BanVeService();
        this.chuyenTauRepo = new ChuyenTauRepository();
        this.toaRepo = new ToaRepository();
        this.choDatRepo = new ChoDatRepository();
        this.khachHangRepo = new KhachHangRepository();
        this.veRepo = new VeRepository();
        this.hoaDonRepo = new HoaDonRepository();
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("TEST NGHIỆP VỤ BÁN VÉ - JPA VERSION");
        System.out.println("=".repeat(70));
        
        TestMainBanVe test = new TestMainBanVe();
        
        try {
            test.runFullTest();
        } catch (Exception e) {
            System.err.println("\n❌ TEST THẤT BẠI!");
            System.err.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Chạy test đầy đủ.
     */
    public void runFullTest() {
        try {
            // Bước 1: Tìm chuyến tàu
            System.out.println("\n📍 BƯỚC 1: Tìm chuyến tàu " + GA_DI + " -> " + GA_DEN);
            List<ChuyenTauDTO> danhSachChuyen = timChuyenTau(GA_DI, GA_DEN, NGAY_DI);
            if (danhSachChuyen.isEmpty()) {
                throw new RuntimeException("Không tìm thấy chuyến tàu!");
            }
            ChuyenTauDTO chuyenTau = danhSachChuyen.get(0);
            System.out.println("✅ Tìm thấy chuyến: " + chuyenTau.maChuyenTau());

            // Bước 2: Lấy danh sách toa
            System.out.println("\n📍 BƯỚC 2: Lấy danh sách toa");
            List<ToaDTO> danhSachToa = layDanhSachToa(chuyenTau.maChuyenTau());
            if (danhSachToa.isEmpty()) {
                throw new RuntimeException("Chuyến tàu không có toa nào!");
            }
            ToaDTO toa = danhSachToa.get(2);
            System.out.println("✅ Toa 1: " + toa.maToa() + " (" + toa.tenLoaiToa() + ")");

            // Bước 3: Lấy sơ đồ ghế
            System.out.println("\n📍 BƯỚC 3: Lấy sơ đồ ghế toa " + toa.maToa());
            List<GheDTO> danhSachGhe = laySoDoGhe(toa.maToa(), chuyenTau.maChuyenTau());
            System.out.println("✅ Tổng số ghế: " + danhSachGhe.size());
            
            // Đếm ghế trống
            long gheTrong = danhSachGhe.stream().filter(g -> !g.daDat()).count();
            System.out.println("   Ghế trống: " + gheTrong);
            System.out.println("   Ghế đã đặt: " + (danhSachGhe.size() - gheTrong));

            // Bước 4: Chọn ghế 1
            System.out.println("\n📍 BƯỚC 4: Chọn ghế");
            GheDTO gheChon = danhSachGhe.stream()
                    .filter(g -> !g.daDat())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Không có ghế trống!"));
            System.out.println("✅ Ghế đã chọn: " + gheChon.maChoDat() + " (Số: " + gheChon.soCho() + ")");

            // Bước 5: Tạo/Cập nhật thông tin khách hàng
            System.out.println("\n📍 BƯỚC 5: Tạo thông tin khách hàng");
            KhachHang khachHang = taoHoacTimKhachHang(TEN_KH, CCCD, SDT, NGAY_SINH, GIOI_TINH);
            System.out.println("✅ Khách hàng: " + khachHang.getMaKH() + " - " + khachHang.getHoTen());

            // Bước 6: Tạo TicketDTO
            System.out.println("\n📍 BƯỚC 6: Tạo TicketDTO");
            PassengerDTO passenger = new PassengerDTO(
                    "VT01",           // Người lớn
                    TEN_KH,
                    CCCD,
                    SDT,
                    NGAY_SINH,
                    GIOI_TINH
            );

            BigDecimal giaVe = tinhGiaVe(chuyenTau.maChuyenTau(), toa.loaiToa(), "VT01", null);
            System.out.println("   Giá vé: " + giaVe + " VND");

            TicketDTO ticket = new TicketDTO(
                    gheChon.maChoDat(),
                    passenger,
                    "VT01",
                    giaVe,
                    true
            );

            List<TicketDTO> danhSachVe = new ArrayList<>();
            danhSachVe.add(ticket);
            System.out.println("✅ Đã tạo TicketDTO cho ghế: " + ticket.maChoDat());

            // Bước 7: Thực hiện bán vé
            System.out.println("\n📍 BƯỚC 7: Thực hiện bán vé");
            BanVeResultDTO ketQua = thucHienBanVe(
                    danhSachVe,
                    passenger,
                    chuyenTau.maChuyenTau(),
                    MA_NV,
                    null,  // Không có khuyến mãi
                    "Tiền mặt"
            );

            // Bước 8: Kiểm tra kết quả
            System.out.println("\n📍 BƯỚC 8: Kiểm tra kết quả");
            if (ketQua.thanhCong()) {
                System.out.println("✅ GIAO DỊCH THÀNH CÔNG!");
                System.out.println("   Mã hóa đơn: " + ketQua.maHoaDon());
                System.out.println("   Mã khách hàng: " + ketQua.maKhachHangDaiDien());
                System.out.println("   Tổng tiền: " + formatCurrency(ketQua.tongTien()) + " VND");
                
                if (ketQua.danhSachVe() != null) {
                    System.out.println("   Số vé: " + ketQua.danhSachVe().size());
                    for (TicketResultDTO ve : ketQua.danhSachVe()) {
                        System.out.println("      - " + ve.maVe() + " | Ghế: " + ve.maChoDat() + " | Giá: " + formatCurrency(ve.giaDaGiam()) + " VND");
                    }
                }
            } else {
                System.out.println("❌ GIAO DỊCH THẤT BẠI!");
                System.out.println("   Lỗi: " + ketQua.loiLoi());
            }

            // Bước 9: Xác nhận lưu vào database
            System.out.println("\n📍 BƯỚC 9: Xác nhận dữ liệu trong database");
            xacNhanDuLieu(ketQua);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("🎉 TEST HOÀN TẤT!");
            System.out.println("=".repeat(70));

        } catch (Exception e) {
            System.err.println("\n❌ TEST THẤT BẠI!");
            System.err.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================================
    // CÁC BƯỚC XỬ LÝ
    // ================================================================================

    private List<ChuyenTauDTO> timChuyenTau(String gaDi, String gaDen, LocalDate ngayDi) {
        System.out.println("   Gọi banVeService.findChuyenTau(" + gaDi + ", " + gaDen + ", " + ngayDi + ")");
        List<ChuyenTauDTO> ketQua = banVeService.findChuyenTau(gaDi, gaDen, ngayDi);
        System.out.println("   Kết quả: " + ketQua.size() + " chuyến tàu");
        return ketQua;
    }

    private List<ToaDTO> layDanhSachToa(String maChuyenTau) {
        System.out.println("   Gọi banVeService.getDanhSachToaByChuyenTau(" + maChuyenTau + ")");
        List<ToaDTO> ketQua = banVeService.getDanhSachToaByChuyenTau(maChuyenTau);
        System.out.println("   Kết quả: " + ketQua.size() + " toa");
        return ketQua;
    }

    private List<GheDTO> laySoDoGhe(String maToa, String maChuyenTau) {
        System.out.println("   Gọi banVeService.getSoDoGheByToa(" + maToa + ", " + maChuyenTau + ")");
        List<GheDTO> ketQua = banVeService.getSoDoGheByToa(maToa, maChuyenTau);
        System.out.println("   Kết quả: " + ketQua.size() + " ghế");
        return ketQua;
    }

    private KhachHang taoHoacTimKhachHang(String hoTen, String cccd, String sdt, LocalDate ngaySinh, String gioiTinh) {
        System.out.println("   Tìm khách hàng theo CCCD: " + cccd);
        
        // Thử tìm khách hàng theo CCCD
        KhachHang kh = khachHangRepo.findByCccd(cccd);
        
        if (kh != null) {
            System.out.println("   Tìm thấy khách hàng cũ: " + kh.getMaKH());
            // Cập nhật thông tin mới
            kh.setHoTen(hoTen);
            kh.setSdt(sdt);
            kh.setNgaySinh(ngaySinh);
            kh.setGioiTinh(gioiTinh);
            return kh;
        }
        
        // Tạo mới khách hàng
        System.out.println("   Tạo khách hàng mới...");
        LocalDate homNay = LocalDate.now();
        String ngayStr = homNay.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy"));
        
        // Đếm số KH hôm nay để tạo mã
        int count = demSoKhachHangHomNay();
        String maKH = String.format("KH%s%04d", ngayStr, count + 1);
        
        KhachHang khMoi = KhachHang.builder()
                .maKH(maKH)
                .hoTen(hoTen)
                .soCCCD(cccd)
                .sdt(sdt)
                .ngaySinh(ngaySinh)
                .gioiTinh(gioiTinh)
                .build();
        
        // Lưu vào database
        khachHangRepo.save(khMoi);
        System.out.println("   Đã tạo khách hàng mới: " + maKH);
        
        return khMoi;
    }

    private int demSoKhachHangHomNay() {
        LocalDate homNay = LocalDate.now();
        String pattern = "KH" + homNay.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy")) + "%";
        
        List<KhachHang> dsKH = khachHangRepo.findAll();
        int count = 0;
        for (KhachHang kh : dsKH) {
            if (kh.getMaKH() != null && kh.getMaKH().startsWith("KH" + homNay.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy")))) {
                count++;
            }
        }
        return count;
    }

    private BigDecimal tinhGiaVe(String maChuyenTau, String maLoaiToa, String maLoaiVe, String maKM) {
        System.out.println("   Tính giá: Chuyến=" + maChuyenTau + ", Toa=" + maLoaiToa + ", Loại vé=" + maLoaiVe);
        BigDecimal gia = banVeService.tinhGiaVe(maChuyenTau, maLoaiToa, maLoaiVe, maKM);
        System.out.println("   Giá tính được: " + gia + " VND");
        return gia;
    }

    private BanVeResultDTO thucHienBanVe(
            List<TicketDTO> danhSachVe,
            PassengerDTO khachHangDaiDien,
            String maChuyenTau,
            String maNV,
            String maKM,
            String phuongThucTT) {
        
        System.out.println("   Gọi banVeService.thucHienBanVe()");
        System.out.println("   - Số vé: " + danhSachVe.size());
        System.out.println("   - Chuyến: " + maChuyenTau);
        System.out.println("   - NV: " + maNV);
        System.out.println("   - KM: " + (maKM != null ? maKM : "Không"));
        System.out.println("   - Thanh toán: " + phuongThucTT);
        
        BanVeResultDTO ketQua = banVeService.thucHienBanVe(
                danhSachVe,
                khachHangDaiDien,
                maChuyenTau,
                maNV,
                maKM,
                phuongThucTT
        );
        
        return ketQua;
    }

    private void xacNhanDuLieu(BanVeResultDTO ketQua) {
        if (!ketQua.thanhCong()) {
            System.out.println("   Giao dịch thất bại, không cần xác nhận.");
            return;
        }

        String maHD = ketQua.maHoaDon();
        String maKH = ketQua.maKhachHangDaiDien();

        // Kiểm tra hóa đơn
        System.out.println("\n   --- KIỂM TRA HÓA ĐƠN ---");
        Optional<HoaDon> hdOpt = hoaDonRepo.findByMaHD(maHD);
        if (hdOpt.isPresent()) {
            HoaDon hd = hdOpt.get();
            System.out.println("   ✅ Hóa đơn tồn tại:");
            System.out.println("      Mã HD: " + hd.getMaHD());
            System.out.println("      Mã KH: " + hd.getMaKhachHang());
            System.out.println("      Tổng tiền: " + formatCurrency(BigDecimal.valueOf(hd.getTongTien())) + " VND");
            System.out.println("      Ngày lập: " + hd.getNgayLap());
        } else {
            System.out.println("   ❌ Không tìm thấy hóa đơn: " + maHD);
        }

        // Kiểm tra vé
        if (ketQua.danhSachVe() != null) {
            System.out.println("\n   --- KIỂM TRA VÉ ---");
            for (TicketResultDTO veDTO : ketQua.danhSachVe()) {
                Optional<Ve> veOpt = veRepo.findByMaVe(veDTO.maVe());
                if (veOpt.isPresent()) {
                    Ve ve = veOpt.get();
                    System.out.println("   ✅ Vé tồn tại:");
                    System.out.println("      Mã vé: " + ve.getMaVe());
                    System.out.println("      Ghế: " + ve.getMaChoDat());
                    System.out.println("      Chuyến: " + ve.getMaChuyenTau());
                    System.out.println("      Giá: " + formatCurrency(BigDecimal.valueOf(ve.getGiaVe())) + " VND");
                    System.out.println("      Trạng thái: " + ve.getTrangThai());
                } else {
                    System.out.println("   ❌ Không tìm thấy vé: " + veDTO.maVe());
                }
            }
        }
    }

    // ================================================================================
    // UTILITY
    // ================================================================================

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0";
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}
