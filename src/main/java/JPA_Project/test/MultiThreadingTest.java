package JPA_Project.test;

import JPA_Project.service.BanVeService;
import JPA_Project.service.ExecutorServiceManager;
import JPA_Project.service.ThongKeService;
import JPA_Project.service.KhuyenMaiService;
import JPA_Project.entity.ChoDat;
import JPA_Project.entity.ChiTietHoaDon;
import JPA_Project.entity.ChuyenTau;
import JPA_Project.entity.Ga;
import JPA_Project.entity.HoaDon;
import JPA_Project.entity.KhachHang;
import JPA_Project.entity.KhuyenMai;
import JPA_Project.entity.LoaiToa;
import JPA_Project.entity.LoaiVe;
import JPA_Project.entity.NhanVien;
import JPA_Project.entity.TaiKhoan;
import JPA_Project.entity.Tau;
import JPA_Project.entity.Toa;
import JPA_Project.entity.Tuyen;
import JPA_Project.entity.Ve;
import JPA_Project.entity.GaTrongTuyen;
import JPA_Project.entity.GaTrongTuyenId;
import JPA_Project.entity.TrangThaiChuyenTau;
import JPA_Project.entity.ChiTietHoaDonId;
import JPA_Project.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Test class để kiểm tra các service và multi-threading.
 */
public class MultiThreadingTest {

    public static void main(String[] args) {
        System.out.println("=== TEST MULTI-THREADING ===\n");

        testThreadPool();
        testBanVeService();
        testThongKeService();
        testKhuyenMaiService();

        System.out.println("\n=== HOÀN TẤT TEST ===");
        ExecutorServiceManager.shutdown();
    }

    private static void testThreadPool() {
        System.out.println("--- Test Thread Pool ---");

        ExecutorService executor = ExecutorServiceManager.getInstance();
        System.out.println("Thread Pool đã được khởi tạo.");
        System.out.println("Active threads: " + ExecutorServiceManager.getActiveCount());

        Future<Integer> future1 = ExecutorServiceManager.submit(() -> {
            try {
                Thread.sleep(100);
                return 1;
            } catch (InterruptedException e) {
                return 0;
            }
        });

        Future<Integer> future2 = ExecutorServiceManager.submit(() -> {
            try {
                Thread.sleep(100);
                return 2;
            } catch (InterruptedException e) {
                return 0;
            }
        });

        try {
            int result1 = future1.get();
            int result2 = future2.get();
            System.out.println("Task 1 result: " + result1);
            System.out.println("Task 2 result: " + result2);
            System.out.println("Thread Pool test: PASSED\n");
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Thread Pool test: FAILED - " + e.getMessage() + "\n");
        }
    }

    private static void testBanVeService() {
        System.out.println("--- Test BanVeService ---");

        try {
            BanVeService banVeService = new BanVeService();

            String maVe = banVeService.taoMaVe();
            System.out.println("Generated MaVe: " + maVe);

            ChuyenTau chuyenTau = new ChuyenTau();
            chuyenTau.setMaChuyenTau("CT001");

            Tuyen tuyen = new Tuyen();
            tuyen.setDonGiaKM(5000);
            chuyenTau.setTuyen(tuyen);

            LoaiVe loaiVe = new LoaiVe();
            loaiVe.setMucGiamGia(1.0);

            // Test với các tham số String theo đúng signature mới
            BigDecimal giaVe = banVeService.tinhGiaVe("CT001", "G_GHE", "VT01", null);
            System.out.println("Giá vé tính được: " + giaVe);

            System.out.println("BanVeService test: PASSED\n");
        } catch (Exception e) {
            System.out.println("BanVeService test: FAILED - " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private static void testThongKeService() {
        System.out.println("--- Test ThongKeService ---");

        try {
            ThongKeService thongKeService = new ThongKeService();

            System.out.println("Thống kê tháng hiện tại...");
            ThongKeService.ThongKe thongKe = thongKeService.thongKeThang(
                    LocalDateTime.now().getMonthValue(),
                    LocalDateTime.now().getYear()
            );

            System.out.println("Doanh thu: " + thongKe.getDoanhThu());
            System.out.println("Số vé: " + thongKe.getSoVe());
            System.out.println("ThongKeService test: PASSED\n");
        } catch (Exception e) {
            System.out.println("ThongKeService test: FAILED - " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private static void testKhuyenMaiService() {
        System.out.println("--- Test KhuyenMaiService ---");

        try {
            KhuyenMaiService khuyenMaiService = new KhuyenMaiService();

            System.out.println("Lấy danh sách khuyến mãi còn hiệu lực...");
            var dsKM = khuyenMaiService.getKhuyenMaiConHieuLuc();
            System.out.println("Số lượng KM còn hiệu lực: " + dsKM.size());

            System.out.println("KhuyenMaiService test: PASSED\n");
        } catch (Exception e) {
            System.out.println("KhuyenMaiService test: FAILED - " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private static void testParallelTasks() {
        System.out.println("--- Test Parallel Tasks ---");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<Double> task1 = executor.submit(() -> {
            System.out.println("Task 1 đang chạy...");
            Thread.sleep(500);
            return 100.0;
        });

        Future<Double> task2 = executor.submit(() -> {
            System.out.println("Task 2 đang chạy...");
            Thread.sleep(300);
            return 200.0;
        });

        Future<Double> task3 = executor.submit(() -> {
            System.out.println("Task 3 đang chạy...");
            Thread.sleep(400);
            return 300.0;
        });

        try {
            double sum = task1.get() + task2.get() + task3.get();
            System.out.println("Tổng: " + sum);
            System.out.println("Parallel Tasks test: PASSED\n");
        } catch (Exception e) {
            System.out.println("Parallel Tasks test: FAILED - " + e.getMessage() + "\n");
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
