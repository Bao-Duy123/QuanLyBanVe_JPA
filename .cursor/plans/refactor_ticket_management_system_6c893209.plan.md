---
name: Refactor Ticket Management System
overview: Tái cấu trúc dự án Quản Lý Bán Vé Tàu thành ứng dụng Desktop đa luồng sử dụng Java Swing + JPA, phù hợp với môn Lập trình phân tán. Giữ nguyên GUI, đơn giản hóa nghiệp vụ, tối ưu multi-threading.
todos:
  - id: step1-pom
    content: Tao cau truc du an Maven moi voi pom.xml (JPA, Hibernate, MySQL)
    status: pending
  - id: step2-jpa
    content: Tao persistence.xml va JPAUtil
    status: pending
  - id: step3-entities
    content: Tao 8 Entity classes (ChuyenTau, Ve, KhachHang, Tuyen, Ga, HoaDon, KhuyenMai, NhanVien)
    status: pending
  - id: step4-repos
    content: Tao Repository layer voi BaseRepository
    status: pending
  - id: step5-executor
    content: Tao ExecutorServiceManager (Thread Pool singleton)
    status: pending
  - id: step6-services
    content: Tao Service layer voi multi-threading (BanVeService, ThongKeService)
    status: pending
  - id: step7-gui
    content: Tao GUI (LoginFrame, BanVePanel, TraCuuPanel, ThongKePanel, QuanLyPanel)
    status: pending
  - id: step8-test
    content: Tich hop SwingWorker vao GUI, test multi-threading
    status: pending
  - id: step9-data
    content: Tao du lieu mau va hoan thien
    status: pending
isProject: false
---

# Kế Hoạch Tái Cấu Trúc Dự Án Quản Lý Bán Vé Tàu

## Mục Tiêu
Tạo dự án mới đơn giản hơn, tập trung vào **đa luồng (multi-threading)** và **JPA**, phù hợp học tập môn Lập trình phân tán.

---

## 1. Phạm Vi Dự Án Mới

### 1.1 Entities (8 bảng - vừa phải)

```
ChuyenTau    - Chuyến tàu (mã, ngày, giờ, ga đi, ga đến, tuyến)
Ve           - Vé (mã, giá, trạng thái, loại vé, chuyến tàu, khách hàng)
KhachHang    - Khách hàng (mã, tên, CCCD, SĐT, giới tính)
Tuyen        - Tuyến (mã, tên, ga đầu, ga cuối, đơn giá/km)
Ga           - Ga (mã, tên, địa chỉ)
HoaDon       - Hóa đơn (mã, khách hàng, nhân viên, ngày, tổng tiền)
KhuyenMai    - Khuyến mãi (mã, tên, loại, giá trị, ngày bắt đầu/kết thúc)
NhanVien     - Nhân viên (mã, tên, chức vụ, SĐT)
```

### 1.2 Tính Năng Chính

| Tính năng | Mô tả |
|------------|-------|
| **Đăng nhập** | Xác thực tài khoản nhân viên |
| **Bán vé** | Chọn chuyến tàu → chọn ghế → nhập khách hàng → thanh toán |
| **Tra cứu vé** | Tìm vé theo mã, CCCD, ngày |
| **Tra cứu hóa đơn** | Tìm hóa đơn theo ngày, khách hàng |
| **Quản lý khuyến mãi** | CRUD khuyến mãi |
| **Thống kê** | Doanh thu theo ngày/tháng |

---

## 2. Kiến Trúc Đề Xuất

### 2.1 Layered Architecture

```
┌─────────────────────────────────────────────┐
│                 GUI Layer                   │
│         (Swing - JPanel, JFrame)            │
├─────────────────────────────────────────────┤
│              Service Layer                  │
│    (Business Logic + Multi-threading)       │
├─────────────────────────────────────────────┤
│            Repository Layer                 │
│        (JPA - EntityManager)               │
├─────────────────────────────────────────────┤
│              Database Layer                 │
│           (MySQL/SQL Server)               │
└─────────────────────────────────────────────┘
```

### 2.2 Multi-threading Architecture

```
Main Thread (UI)
    │
    ├── SwingWorker #1: Tải danh sách chuyến tàu
    ├── SwingWorker #2: Xử lý thanh toán
    ├── SwingWorker #3: Tải thống kê
    │
    └── ExecutorService (Thread Pool)
            │
            ├── Thread #1: Kiểm tra vé trùng lặp
            ├── Thread #2: Tính giá vé
            └── Thread #3: Gửi thông báo
```

---

## 3. Cấu Trúc Thư Mục Mới

```
QuanLyBanVeTau_Simple/
├── pom.xml
├── src/main/java/
│   ├── entity/
│   │   ├── ChuyenTau.java
│   │   ├── Ve.java
│   │   ├── KhachHang.java
│   │   ├── Tuyen.java
│   │   ├── Ga.java
│   │   ├── HoaDon.java
│   │   ├── KhuyenMai.java
│   │   └── NhanVien.java
│   ├── repository/
│   │   ├── BaseRepository.java
│   │   ├── ChuyenTauRepository.java
│   │   ├── VeRepository.java
│   │   ├── KhachHangRepository.java
│   │   ├── TuyenRepository.java
│   │   ├── GaRepository.java
│   │   ├── HoaDonRepository.java
│   │   ├── KhuyenMaiRepository.java
│   │   └── NhanVienRepository.java
│   ├── service/
│   │   ├── ExecutorServiceManager.java      // Thread Pool singleton
│   │   ├── BanVeService.java                // Nghiệp vụ bán vé
│   │   ├── ThongKeService.java              // Thống kê đa luồng
│   │   ├── KhuyenMaiService.java
│   │   └── PaymentService.java             // Xử lý thanh toán
│   ├── gui/
│   │   ├── MainFrame.java
│   │   ├── LoginFrame.java
│   │   ├── BanVePanel.java
│   │   ├── TraCuuPanel.java
│   │   ├── ThongKePanel.java
│   │   └── QuanLyPanel.java
│   ├── db/
│   │   └── JPAUtil.java
│   └── Main.java
└── src/main/resources/
    └── META-INF/
        └── persistence.xml
```

---

## 4. Chi Tiết Multi-threading

### 4.1 Thread Pool (ExecutorServiceManager)

```java
public class ExecutorServiceManager {
    private static final int THREAD_POOL_SIZE = 10;
    private static ExecutorService executor;
    
    public static ExecutorService getInstance() {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        }
        return executor;
    }
    
    public static <T> Future<T> submit(Callable<T> task) {
        return getInstance().submit(task);
    }
}
```

### 4.2 SwingWorker cho UI

```java
// Ví dụ: Tải danh sách chuyến tàu không chặn UI
private class LoadChuyenTauWorker extends SwingWorker<List<ChuyenTau>, Void> {
    @Override
    protected List<ChuyenTau> doInBackground() {
        return chuyenTauRepo.findByNgayKhoiHanh(ngay);
    }
    
    @Override
    protected void done() {
        List<ChuyenTau> result = get();
        updateTable(result);
    }
}
```

### 4.3 Callable & Future

```java
// Tính giá vé trong thread pool
Future<Double> giaVeFuture = ExecutorServiceManager.submit(() -> {
    return banVeService.tinhGiaVe(chuyenTau, loaiVe, khuyenMai);
});
double giaVe = giaVeFuture.get(); // Lấy kết quả
```

---

## 5. Nghiệp Vụ Đơn Giản Hóa

### 5.1 Bán Vé (đa luồng)

```java
public class BanVeService {
    // Thread-safe counter cho mã vé
    private final AtomicInteger counter = new AtomicInteger(1);
    
    public Ve banVe(ChuyenTau chuyenTau, KhachHang kh, LoaiVe loaiVe, KhuyenMai km) {
        // 1. Tính giá (trong thread pool)
        double giaVe = tinhGiaVe(chuyenTau, loaiVe, km);
        
        // 2. Tạo vé
        Ve ve = new Ve();
        ve.setMaVe("VE" + counter.getAndIncrement());
        ve.setGiaVe(giaVe);
        ve.setChuyenTau(chuyenTau);
        ve.setKhachHang(kh);
        ve.setTrangThai(TrangThaiVe.DA_BAN);
        
        // 3. Lưu vào DB
        return veRepo.save(ve);
    }
    
    // Synchronized để đảm bảo thread-safe
    public synchronized double tinhGiaVe(...) {
        // Công thức: khoangCach * donGia * heSoLoaiVe
    }
}
```

### 5.2 Thống Kê (đa luồng song song)

```java
public class ThongKeService {
    public ThongKe thongKeThang(int thang, int nam) {
        ExecutorService executor = ExecutorServiceManager.getInstance();
        
        // Chạy song song 3 tác vụ
        Future<Double> doanhThuFuture = executor.submit(() -> tinhDoanhThu(thang, nam));
        Future<Integer> soVeFuture = executor.submit(() -> demSoVe(thang, nam));
        Future<Map<String, Double>> topGaFuture = executor.submit(() -> thongKeTopGa(thang, nam));
        
        // Tổng hợp kết quả
        return new ThongKe(
            doanhThuFuture.get(),
            soVeFuture.get(),
            topGaFuture.get()
        );
    }
}
```

---

## 6. Công Nghệ Sử Dụng

| Thành phần | Công nghệ |
|------------|-----------|
| Ngôn ngữ | Java 17 |
| UI | Swing (javax.swing) |
| ORM | JPA / Hibernate 6.x |
| Database | MySQL (hoặc SQL Server) |
| Build | Maven |
| Concurrency | java.util.concurrent (ExecutorService, SwingWorker, Future) |

---

## 7. Các Bước Thực Hiện

### Giai đoạn 1: Cơ sở hạ tầng (Foundation)
1. Tạo project Maven mới
2. Cấu hình pom.xml (Hibernate, JPA, MySQL connector)
3. Tạo persistence.xml
4. Tạo JPAUtil (EntityManager singleton)
5. Tạo BaseRepository

### Giai đoạn 2: Entities & Repositories
1. Tạo 8 Entity classes
2. Tạo 8 Repository classes
3. Test CRUD cơ bản

### Giai đoạn 3: Services & Multi-threading
1. Tạo ExecutorServiceManager (Thread Pool)
2. Tạo BanVeService với đa luồng
3. Tạo ThongKeService với parallel tasks
4. Tạo KhuyenMaiService

### Giai đoạn 4: GUI
1. Tạo LoginFrame
2. Tạo MainFrame với tabbed pane
3. Tạo BanVePanel
4. Tạo TraCuuPanel
5. Tạo ThongKePanel
6. Tạo QuanLyPanel

### Giai đoạn 5: Tích hợp & Test
1. Kết nối Service với GUI
2. Tích hợp SwingWorker
3. Test đa luồng
4. Tạo dữ liệu mẫu

---

## 8. So Sánh Dự Án Cũ vs Mới

| Khía cạnh | Dự án cũ | Dự án mới |
|-----------|-----------|-----------|
| Entities | 22 | 8 |
| GUI screens | 25+ | 6 |
| Multi-threading | Cơ bản (2 chỗ) | Toàn diện (4+ service) |
| Code structure | Hỗn hợp (DAO+JPA) | Layered rõ ràng |
| Độ phức tạp | Cao | Trung bình |
| Phù hợp học tập | Không | Có |

---

## 9. Tài Liệu Học Tập Kèm Theo

Dự án mới sẽ minh họa các khái niệm Lập trình phân tán:
- **Thread Pool**: ExecutorServiceManager
- **SwingWorker**: Xử lý UI không chặn
- **Callable/Future**: Lấy kết quả từ thread
- **Synchronized**: Thread-safe operations
- **Atomic**: AtomicInteger cho counters
- **Parallel Tasks**: Thống kê chạy song song
