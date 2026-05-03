package JPA_Project.test;

import JPA_Project.dto.*;
import JPA_Project.entity.*;
import JPA_Project.service.BanVeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BanVeServiceTest - Lớp test để kiểm tra các phương thức của BanVeService.
 * Chạy trực tiếp bằng main() mà không cần mở GUI.
 * 
 * Các test case:
 * 1. Tìm kiếm chuyến tàu theo ga đi, ga đến, ngày đi
 * 2. Lấy danh sách toa của chuyến tàu
 * 3. Lấy sơ đồ ghế theo toa
 * 4. Tính giá vé
 * 5. Tạo mã vé và mã hóa đơn
 */
public class BanVeServiceTest {

    private final BanVeService banVeService;

    public BanVeServiceTest() {
        this.banVeService = new BanVeService();
    }

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        BAN VE SERVICE TEST - JPA IMPLEMENTATION          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        BanVeServiceTest test = new BanVeServiceTest();
        
        try {
            // Test 1: Tìm kiếm chuyến tàu
            test.testTimKiemChuyenTau();
            
            // Test 2: Lấy danh sách toa
            test.testLayDanhSachToa();
            
            // Test 3: Lấy sơ đồ ghế
            test.testLaySoDoGhe();
            
            // Test 4: Tính giá vé
            test.testTinhGiaVe();
            
            // Test 5: Tạo mã vé và hóa đơn
            test.testTaoMaVeVaHoaDon();
            
            // Test 6: Lấy danh sách loại vé
            test.testLayDanhSachLoaiVe();
            
            // Test 7: Lấy khuyến mãi
            test.testLayDanhSachKhuyenMai();

            System.out.println();
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                   TẤT CẢ CÁC TEST ĐÃ HOÀN TẤT            ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            
        } catch (Exception e) {
            System.err.println("LỖI KHI CHẠY TEST: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test 1: Tìm kiếm chuyến tàu theo ga đi, ga đến, ngày đi.
     */
    private void testTimKiemChuyenTau() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("TEST 1: TÌM KIẾM CHUYẾN TÀU");
        System.out.println("═══════════════════════════════════════════════════════════");
        
//        // Ví dụ: Tìm chuyến từ ga DNA đến ga NTR ngày 2025-12-20
//        String maGaDi = "DNA";
//        String maGaDen = "NTR";
//        LocalDate ngayDi = LocalDate.of(2025, 12, 20);

        // Ví dụ: Tìm chuyến từ ga DNA đến ga NTR ngày 2025-12-20
        String maGaDi = "DNA";
        String maGaDen = "HAN";
        LocalDate ngayDi = LocalDate.of(2026, 05, 03);
        
        System.out.println("Tìm kiếm: " + maGaDi + " -> " + maGaDen + " ngày " + ngayDi);
        
        List<ChuyenTauDTO> danhSachChuyen = banVeService.findChuyenTau(maGaDi, maGaDen, ngayDi);
        
        if (danhSachChuyen.isEmpty()) {
            System.out.println("⚠ Không tìm thấy chuyến tàu nào!");
            // Thử với dữ liệu mẫu khác
            System.out.println("Thử với ngày khác...");
            ngayDi = LocalDate.now().plusDays(7);
            danhSachChuyen = banVeService.findChuyenTau(maGaDi, maGaDen, ngayDi);
        }
        
        System.out.println("Kết quả: Tìm thấy " + danhSachChuyen.size() + " chuyến tàu");
        
        for (ChuyenTauDTO ct : danhSachChuyen) {
            System.out.println("  • " + ct.maChuyenTau());
            System.out.println("    Tuyến: " + ct.tenGaDi() + " → " + ct.tenGaDen());
            System.out.println("    Giờ: " + ct.gioKhoiHanh() + " | " + ct.tenTuyen());
            System.out.println("    Tàu: " + ct.maTau() + " | Số toa: " + ct.soToa());
            System.out.println("    Trạng thái: " + ct.trangThai());
            System.out.println();
        }
        
        System.out.println("✓ Test 1 hoàn tất");
        System.out.println();
    }

    /**
     * Test 2: Lấy danh sách toa của chuyến tàu.
     */
    private void testLayDanhSachToa() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("TEST 2: LẤY DANH SÁCH TOA THEO CHUYẾN TÀU");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        // Lấy mã chuyến từ test 1 hoặc sử dụng mã mẫu
        String maChuyenTau = "SE1_201225_DNA_NTR"; // Thay đổi theo dữ liệu thực tế
        
        System.out.println("Lấy danh sách toa cho chuyến: " + maChuyenTau);
        
        List<ToaDTO> danhSachToa = banVeService.getDanhSachToaByChuyenTau(maChuyenTau);
        
        if (danhSachToa.isEmpty()) {
            System.out.println("⚠ Không có toa nào hoặc chuyến tàu không tồn tại!");
            
            // Thử tìm chuyến tàu gần nhất để lấy toa
            System.out.println("Thử lấy toa từ chuyến tàu gần nhất...");
            LocalDate today = LocalDate.now();
            List<ChuyenTauDTO> chuyenTaus = banVeService.findChuyenTau("DNA", "NTR", today);
            if (!chuyenTaus.isEmpty()) {
                maChuyenTau = chuyenTaus.get(0).maChuyenTau();
                System.out.println("Sử dụng chuyến: " + maChuyenTau);
                danhSachToa = banVeService.getDanhSachToaByChuyenTau(maChuyenTau);
            }
        }
        
        System.out.println("Kết quả: Tìm thấy " + danhSachToa.size() + " toa");
        
        for (ToaDTO toa : danhSachToa) {
            System.out.println("  • " + toa.maToa() + " - " + toa.tenLoaiToa());
            System.out.println("    Loại: " + toa.loaiToa() + " | Hệ số: " + toa.heSo());
            System.out.println("    Số ghế: " + toa.soLuongGhe() + " | Vị trí: " + toa.viTriToa());
            System.out.println();
        }
        
        System.out.println("✓ Test 2 hoàn tất");
        System.out.println();
    }

    /**
     * Test 3: Lấy sơ đồ ghế theo toa.
     */
    private void testLaySoDoGhe() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("TEST 3: LẤY SƠ ĐỒ GHẾ THEO TOA");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        // Lấy dữ liệu thực từ DB: tìm chuyến -> lấy toa đầu tiên
        String maChuyenTau = null;
        String maToa = null;
        
        LocalDate today = LocalDate.now();
        List<ChuyenTauDTO> chuyenTaus = banVeService.findChuyenTau("DNA", "NTR", today);
        if (!chuyenTaus.isEmpty()) {
            maChuyenTau = chuyenTaus.get(0).maChuyenTau();
            List<ToaDTO> toas = banVeService.getDanhSachToaByChuyenTau(maChuyenTau);
            if (!toas.isEmpty()) {
                maToa = toas.get(0).maToa();
            }
        }
        
        // Fallback: dùng mã mẫu nếu không tìm được
        if (maToa == null || maChuyenTau == null) {
            maChuyenTau = "SE1_201225_DNA_NTR";
            maToa = "SPT2-1"; // Toa mẫu có trong DB
            //Lỗi khi đọc loại toa khác giường nằm thì bị lỗi
        }
        
        System.out.println("Lấy sơ đồ ghế: Toa=" + maToa + ", Chuyến=" + maChuyenTau);
        
        List<GheDTO> soDoGhe = banVeService.getSoDoGheByToa(maToa, maChuyenTau);
        
        if (soDoGhe.isEmpty()) {
            System.out.println("⚠ Không có ghế nào hoặc toa không tồn tại!");
            System.out.println("✓ Test 3 hoàn tất (không có dữ liệu ghế)");
            System.out.println();
            return;
        }
        
        System.out.println("Kết quả: Tìm thấy " + soDoGhe.size() + " ghế");
        
        // Phân loại ghế
        long gheTrong = soDoGhe.stream().filter(g -> !g.daDat()).count();
        long gheDaDat = soDoGhe.stream().filter(GheDTO::daDat).count();
        
        System.out.println("  Ghế trống: " + gheTrong);
        System.out.println("  Ghế đã đặt: " + gheDaDat);
        System.out.println();
        
        // Hiển thị sơ đồ ghế (theo tầng)
        Map<Integer, List<GheDTO>> theoTang = soDoGhe.stream()
                .collect(Collectors.groupingBy(g -> g.tang() != null ? g.tang() : 0));

        for (Map.Entry<Integer, List<GheDTO>> entry : theoTang.entrySet()) {
            int tangVal = entry.getKey();
            String labelTang = tangVal == 0 ? "Ghế (không phân tầng)" : "Tầng " + tangVal;
            System.out.println("  " + labelTang + ":");
            String gheStr = entry.getValue().stream()
                    .map(g -> (g.daDat() ? "[X]" : "[ ]") + g.soCho())
                    .collect(Collectors.joining(" "));
            System.out.println("    " + gheStr);
        }
        
        System.out.println();
        System.out.println("✓ Test 3 hoàn tất");
        System.out.println();
    }

    /**
     * Test 4: Tính giá vé.
     */
    private void testTinhGiaVe() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("TEST 4: TÍNH GIÁ VÉ");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        String maChuyenTau = "SE1_201225_DNA_NTR";
        String maLoaiToa = "G_NGAM"; // Giường nằm
        String maLoaiVe = "VT01";    // Người lớn
        String maKM = null;           // Không có khuyến mãi
        
        System.out.println("Tính giá vé:");
        System.out.println("  Chuyến: " + maChuyenTau);
        System.out.println("  Loại toa: " + maLoaiToa);
        System.out.println("  Loại vé: " + maLoaiVe);
        
        BigDecimal giaVe = banVeService.tinhGiaVe(maChuyenTau, maLoaiToa, maLoaiVe, maKM);
        System.out.println("  Giá vé (không KM): " + formatCurrency(giaVe));
        
        // Test với các loại vé khác nhau
        System.out.println();
        System.out.println("So sánh giá theo loại vé:");
        
        String[] loaiVeArr = {"VT01", "VT02", "VT03", "VT04"};
        String[] tenLoaiVeArr = {"Người lớn", "Trẻ em", "Sinh viên", "Người cao tuổi"};
        
        for (int i = 0; i < loaiVeArr.length; i++) {
            BigDecimal gia = banVeService.tinhGiaVe(maChuyenTau, maLoaiToa, loaiVeArr[i], null);
            System.out.println("  " + tenLoaiVeArr[i] + " (" + loaiVeArr[i] + "): " + formatCurrency(gia));
        }
        
        // Test với khuyến mãi
        System.out.println();
        System.out.println("So sánh giá với khuyến mãi:");
        
        List<KhuyenMai> danhSachKM = banVeService.getDanhSachKhuyenMaiConHieuLuc();
        if (!danhSachKM.isEmpty()) {
            KhuyenMai km = danhSachKM.get(0);
            System.out.println("  Khuyến mãi: " + km.getTenKM() + " (" + km.getMaKM() + ")");
            BigDecimal giaSauKM = banVeService.tinhGiaVe(maChuyenTau, maLoaiToa, maLoaiVe, km.getMaKM());
            System.out.println("  Giá có KM: " + formatCurrency(giaSauKM));
        } else {
            System.out.println("  ⚠ Không có khuyến mãi nào đang hoạt động");
        }
        
        System.out.println();
        System.out.println("✓ Test 4 hoàn tất");
        System.out.println();
    }

    /**
     * Test 5: Tạo mã vé và mã hóa đơn.
     */
    private void testTaoMaVeVaHoaDon() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("TEST 5: TẠO MÃ VÉ VÀ MÃ HÓA ĐƠN");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        // Tạo 5 mã vé liên tiếp
        System.out.println("Tạo 5 mã vé liên tiếp:");
        for (int i = 0; i < 5; i++) {
            String maVe = banVeService.taoMaVe();
            System.out.println("  " + (i + 1) + ". " + maVe);
        }
        
        // Tạo 3 mã hóa đơn
        System.out.println();
        System.out.println("Tạo 3 mã hóa đơn:");
        String maNV = "ADMIN001";
        String soHieuCa = "01";
        for (int i = 0; i < 3; i++) {
            String maHD = banVeService.taoMaHoaDon(maNV, soHieuCa);
            System.out.println("  " + (i + 1) + ". " + maHD);
        }
        
        System.out.println();
        System.out.println("✓ Test 5 hoàn tất");
        System.out.println();
    }

    /**
     * Test 6: Lấy danh sách loại vé.
     */
    private void testLayDanhSachLoaiVe() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("TEST 6: LẤY DANH SÁCH LOẠI VÉ");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        List<LoaiVe> danhSachLoaiVe = banVeService.getDanhSachLoaiVe();
        
        System.out.println("Tìm thấy " + danhSachLoaiVe.size() + " loại vé:");
        
        for (LoaiVe lv : danhSachLoaiVe) {
            System.out.println("  • " + lv.getMaLoaiVe() + " - " + lv.getTenLoai());
            System.out.println("    Hệ số giảm giá: " + lv.getMucGiamGia());
            System.out.println("    Độ tuổi: " + lv.getTuoiMin() + " - " + lv.getTuoiMax());
        }
        
        System.out.println();
        System.out.println("✓ Test 6 hoàn tất");
        System.out.println();
    }

    /**
     * Test 7: Lấy danh sách khuyến mãi.
     */
    private void testLayDanhSachKhuyenMai() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("TEST 7: LẤY DANH SÁCH KHUYẾN MÃI");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        List<KhuyenMai> danhSachKM = banVeService.getDanhSachKhuyenMaiConHieuLuc();
        
        System.out.println("Tìm thấy " + danhSachKM.size() + " khuyến mãi đang hoạt động:");
        
        for (KhuyenMai km : danhSachKM) {
            System.out.println("  • " + km.getMaKM() + " - " + km.getTenKM());
            System.out.println("    Loại: " + km.getLoaiKM());
            System.out.println("    Giá trị giảm: " + km.getGiaTriGiam());
            System.out.println("    Điều kiện: " + km.getDkApDung() + " = " + km.getGiaTriDK());
            System.out.println("    Thời gian: " + km.getNgayBatDau() + " -> " + km.getNgayKetThuc());
            System.out.println();
        }
        
        System.out.println("✓ Test 7 hoàn tất");
        System.out.println();
    }

    /**
     * Format số tiền thành chuỗi VND.
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VND";
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " VND";
    }
}
