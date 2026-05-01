package JPA_Project.service;

import JPA_Project.db.JPAUtil;
import JPA_Project.db.Tx;
import JPA_Project.dto.*;
import JPA_Project.entity.*;
import JPA_Project.repository.*;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * BanVeService - Tầng Service xử lý nghiệp vụ bán vé tàu.
 * Tách biệt hoàn toàn Logic Nghiệp vụ ra khỏi GUI.
 * Sử dụng JPA (EntityManager) để truy cập database.
 * 
 * Các phương thức chính:
 * - findChuyenTau: Tìm chuyến tàu theo ga đi, ga đến, ngày đi
 * - getDanhSachToaByChuyenTau: Lấy danh sách toa của chuyến tàu
 * - getSoDoGheByToa: Lấy sơ đồ ghế và trạng thái
 * - tinhGiaVe: Tính giá vé dựa trên khoảng cách, loại toa, loại vé
 * - thucHienBanVe: Thực hiện bán vé với Pessimistic Locking
 */
public class BanVeService {

    // ================================================================================
    // REPOSITORIES
    // ================================================================================
    private final ChuyenTauRepository chuyenTauRepo;
    private final ToaRepository toaRepo;
    private final ChoDatRepository choDatRepo;
    private final VeRepository veRepo;
    private final LoaiVeRepository loaiVeRepo;
    private final LoaiToaRepository loaiToaRepo;
    private final KhuyenMaiRepository khuyenMaiRepo;
    private final KhachHangRepository khachHangRepo;
    private final HoaDonRepository hoaDonRepo;
    private final GaTrongTuyenRepository gaTrongTuyenRepo;
    private final ChiTietHoaDonRepository chiTietHoaDonRepo;

    // Bộ đếm cho việc tạo mã (thread-safe)
    private final AtomicInteger maVeCounter;
    private final AtomicInteger maHDCounter;

    // ================================================================================
    // CONSTRUCTORS
    // ================================================================================
    public BanVeService() {
        this.chuyenTauRepo = new ChuyenTauRepository();
        this.toaRepo = new ToaRepository();
        this.choDatRepo = new ChoDatRepository();
        this.veRepo = new VeRepository();
        this.loaiVeRepo = new LoaiVeRepository();
        this.loaiToaRepo = new LoaiToaRepository();
        this.khuyenMaiRepo = new KhuyenMaiRepository();
        this.khachHangRepo = new KhachHangRepository();
        this.hoaDonRepo = new HoaDonRepository();
        this.gaTrongTuyenRepo = new GaTrongTuyenRepository();
        this.chiTietHoaDonRepo = new ChiTietHoaDonRepository();
        this.maVeCounter = new AtomicInteger(1);
        this.maHDCounter = new AtomicInteger(1);
    }

    // ================================================================================
    // 1. TÌM KIẾM CHUYẾN TÀU
    // ================================================================================

    /**
     * Tìm chuyến tàu theo ga đi, ga đến và ngày đi.
     * Sử dụng findChuyenByGa từ Repository (hoặc native query).
     *
     * @param maGaDi Mã ga đi
     * @param maGaDen Mã ga đến  
     * @param ngayDi Ngày đi
     * @return Danh sách ChuyenTauDTO chứa thông tin chuyến tàu
     */
    public List<ChuyenTauDTO> findChuyenTau(String maGaDi, String maGaDen, LocalDate ngayDi) {
        System.out.println("[BanVeService] Tìm chuyến tàu: " + maGaDi + " -> " + maGaDen + " ngày " + ngayDi);
        
        // Gọi repository để lấy danh sách chuyến tàu
        List<ChuyenTau> danhSachChuyen = chuyenTauRepo.findByGaDiGaDenNgay(maGaDi, maGaDen, ngayDi);
        
        if (danhSachChuyen == null || danhSachChuyen.isEmpty()) {
            System.out.println("[BanVeService] Không tìm thấy chuyến tàu nào.");
            return Collections.emptyList();
        }

        // Chuyển đổi sang DTO với thông tin chi tiết
        return danhSachChuyen.stream()
                .map(this::chuyenTauSangDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi entity ChuyenTau sang DTO với thông tin đầy đủ.
     */
    private ChuyenTauDTO chuyenTauSangDTO(ChuyenTau ct) {
        // Lấy thông tin ga chi tiết (trong transaction riêng để tránh LazyInitializationException)
        String tenGaDi = ct.getGaDi() != null ? ct.getGaDi().getTenGa() : "Ga đi";
        String tenGaDen = ct.getGaDen() != null ? ct.getGaDen().getTenGa() : "Ga đến";
        String maTau = ct.getMaTau() != null ? ct.getMaTau() : "";
        String tenTuyen = "";
        
        if (ct.getTuyen() != null) {
            tenTuyen = ct.getTuyen().getTenTuyen() != null ? ct.getTuyen().getTenTuyen() : ct.getTuyen().getMaTuyen();
        }
        
        // Lấy số toa của tàu
        int soToa = laySoLuongToaCuaTau(maTau);
        
        String trangThai = ct.getThct() != null ? ct.getThct().toString() : "";

        return new ChuyenTauDTO(
                ct.getMaChuyenTau(),
                ct.getMaTuyen(),
                tenTuyen,
                maTau,
                tenGaDi,
                tenGaDen,
                ct.getGaDi() != null ? ct.getGaDi().getMaGa() : "",
                ct.getGaDen() != null ? ct.getGaDen().getMaGa() : "",
                ct.getNgayKhoiHanh(),
                ct.getGioKhoiHanh(),
                ct.getNgayDenDuKien(),
                ct.getGioDenDuKien(),
                soToa,
                trangThai
        );
    }

    /**
     * Lấy số lượng toa của tàu.
     */
    private int laySoLuongToaCuaTau(String maTau) {
        if (maTau == null || maTau.isBlank()) return 0;
        List<Toa> danhSachToa = toaRepo.findByMaTau(maTau);
        return danhSachToa != null ? danhSachToa.size() : 0;
    }

    // ================================================================================
    // 2. LẤY DANH SÁCH TOA THEO CHUYẾN TÀU
    // ================================================================================

    /**
     * Lấy danh sách toa của một chuyến tàu.
     *
     * @param maChuyenTau Mã chuyến tàu
     * @return Danh sách ToaDTO chứa thông tin toa
     */
    public List<ToaDTO> getDanhSachToaByChuyenTau(String maChuyenTau) {
        System.out.println("[BanVeService] Lấy danh sách toa cho chuyến: " + maChuyenTau);
        
        // Lấy thông tin chuyến tàu để biết mã tàu
        Optional<ChuyenTau> optChuyen = chuyenTauRepo.findByMaChuyenTau(maChuyenTau);
        if (optChuyen.isEmpty()) {
            System.out.println("[BanVeService] Không tìm thấy chuyến tàu: " + maChuyenTau);
            return Collections.emptyList();
        }
        
        String maTau = optChuyen.get().getMaTau();
        if (maTau == null || maTau.isBlank()) {
            System.out.println("[BanVeService] Chuyến tàu không có mã tàu.");
            return Collections.emptyList();
        }

        // Lấy danh sách toa với thông tin loại toa
        List<Toa> danhSachToa = toaRepo.findByMaTauWithLoaiToa(maTau);
        
        if (danhSachToa == null || danhSachToa.isEmpty()) {
            System.out.println("[BanVeService] Không có toa nào cho tàu: " + maTau);
            return Collections.emptyList();
        }

        // Chuyển đổi sang DTO
        return danhSachToa.stream()
                .map(this::toaSangDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi entity Toa sang DTO.
     */
    private ToaDTO toaSangDTO(Toa toa) {
        String tenLoaiToa = "Toa thường";
        double heSo = 1.0;
        String loaiToaCode = toa.getLoaiToa();
        
        if (toa.getLoaiToaRef() != null) {
            tenLoaiToa = toa.getLoaiToaRef().getTenLoaiToa() != null 
                    ? toa.getLoaiToaRef().getTenLoaiToa() 
                    : "Toa";
            heSo = toa.getLoaiToaRef().getHeSo();
        }

        int soLuongGhe = laySoLuongChoCuaToa(toa.getMaToa());

        // Xác định vị trí toa (dựa vào thứ tự)
        String viTriToa = layViTriToa(toa.getMaToa());

        return new ToaDTO(
                toa.getMaToa(),
                tenLoaiToa,
                loaiToaCode,
                heSo,
                soLuongGhe,
                viTriToa
        );
    }

    /**
     * Lấy số lượng chỗ của một toa.
     */
    private int laySoLuongChoCuaToa(String maToa) {
        List<ChoDat> danhSachCho = choDatRepo.findByMaToa(maToa);
        return danhSachCho != null ? danhSachCho.size() : 0;
    }

    /**
     * Xác định vị trí toa (đầu, giữa, cuối) dựa vào mã toa.
     * Ví dụ: SPT1 -> đầu, SPT2 -> giữa, SPT3 -> cuối
     */
    private String layViTriToa(String maToa) {
        if (maToa == null || maToa.length() < 4) return "Không xác định";
        
        // Giả định: lấy ký tự cuối là số thứ tự toa
        String phanCuoi = maToa.substring(maToa.length() - 1);
        try {
            int stt = Integer.parseInt(phanCuoi);
            if (stt == 1) return "Đầu tàu";
            if (stt >= 3) return "Cuối tàu";
            return "Giữa tàu";
        } catch (NumberFormatException e) {
            return "Không xác định";
        }
    }

    // ================================================================================
    // 3. LẤY SƠ ĐỒ GHẾ THEO TOA
    // ================================================================================

    /**
     * Lấy danh sách ghế và trạng thái (Đã bán/Trống) của một toa.
     *
     * @param maToa Mã toa cần lấy sơ đồ
     * @param maChuyenTau Mã chuyến tàu để xác định trạng thái ghế
     * @return Danh sách GheDTO với trạng thái đã đặt/chưa đặt
     */
    public List<GheDTO> getSoDoGheByToa(String maToa, String maChuyenTau) {
        System.out.println("[BanVeService] Lấy sơ đồ ghế: Toa=" + maToa + ", Chuyến=" + maChuyenTau);
        
        // Lấy tất cả chỗ của toa
        List<ChoDat> danhSachCho = choDatRepo.findByMaToa(maToa);
        
        if (danhSachCho == null || danhSachCho.isEmpty()) {
            System.out.println("[BanVeService] Toa " + maToa + " không có chỗ nào.");
            return Collections.emptyList();
        }

        // Lấy danh sách vé đã bán cho chuyến tàu này
        Set<String> cacMaChoDaBan = layMaChoDaBan(maChuyenTau);

        // Chuyển đổi sang DTO với trạng thái
        return danhSachCho.stream()
                .map(cho -> new GheDTO(
                        cho.getMaCho(),
                        cho.getMaToa(),
                        cho.getSoCho(),
                        cho.getKhoang(),
                        cho.getTang(),
                        cacMaChoDaBan.contains(cho.getMaCho())
                ))
                .sorted(Comparator
                        .comparing((GheDTO g) -> g.tang() != null ? g.tang() : 0)
                        .thenComparing(g -> g.khoang() != null ? g.khoang() : 0)
                        .thenComparing(GheDTO::soCho, Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * Lấy tập hợp các mã chỗ đã bán cho một chuyến tàu.
     */
    private Set<String> layMaChoDaBan(String maChuyenTau) {
        List<Ve> danhSachVeDaBan = veRepo.findByTrangThai("DA_BAN");
        
        return danhSachVeDaBan.stream()
                .filter(ve -> maChuyenTau.equals(ve.getMaChuyenTau()))
                .map(Ve::getMaChoDat)
                .filter(maCho -> maCho != null && !maCho.isBlank())
                .collect(Collectors.toSet());
    }

    /**
     * Kiểm tra ghế có đang trống hay không.
     *
     * @param maChoDat Mã chỗ đặt
     * @param maChuyenTau Mã chuyến tàu
     * @return true nếu ghế trống, false nếu đã bán
     */
    public boolean kiemTraGheTrong(String maChoDat, String maChuyenTau) {
        if (maChoDat == null || maChuyenTau == null) return false;
        
        List<Ve> veList = veRepo.findByMaChuyenTauVaMaCho(maChuyenTau, maChoDat);
        return veList == null || veList.isEmpty();
    }

    // ================================================================================
    // 4. TÍNH GIÁ VÉ (NÂNG CAO)
    // ================================================================================

    /**
     * Tính giá vé dựa trên:
     * - Khoảng cách ga (từ GA_TRONG_TUYEN)
     * - Loại toa (Hệ số từ LoaiToa)
     * - Loại vé (Mức giảm giá từ LoaiVe)
     * - Khuyến mãi (nếu có)
     *
     * @param maChuyenTau Mã chuyến tàu
     * @param maLoaiToa Mã loại toa (ví dụ: G_NGAM, G_GHE)
     * @param maLoaiVe Mã loại vé (ví dụ: VT01, VT02, VT03, VT04)
     * @param maKM Mã khuyến mãi (có thể null)
     * @return Giá vé đã tính (BigDecimal)
     */
    public BigDecimal tinhGiaVe(String maChuyenTau, String maLoaiToa, String maLoaiVe, String maKM) {
        System.out.println("[BanVeService] Tính giá vé: Chuyến=" + maChuyenTau + ", Toa=" + maLoaiToa + ", Vé=" + maLoaiVe);
        
        // 1. Lấy thông tin chuyến tàu
        Optional<ChuyenTau> optChuyen = chuyenTauRepo.findByMaChuyenTau(maChuyenTau);
        if (optChuyen.isEmpty()) {
            System.err.println("[BanVeService] Không tìm thấy chuyến tàu: " + maChuyenTau);
            return BigDecimal.ZERO;
        }
        ChuyenTau chuyen = optChuyen.get();

        // 2. Tính khoảng cách từ ga đi đến ga đến
        int khoangCach = tinhKhoangCachGa(chuyen.getMaTuyen(), 
                chuyen.getGaDi() != null ? chuyen.getGaDi().getMaGa() : null,
                chuyen.getGaDen() != null ? chuyen.getGaDen().getMaGa() : null);

        // 3. Lấy đơn giá của tuyến
        int donGia = layDonGiaTuyen(chuyen.getMaTuyen());

        // 4. Lấy hệ số loại toa
        double heSoToa = layHeSoLoaiToa(maLoaiToa);

        // 5. Lấy hệ số giảm giá loại vé (hệ số nhân)
        double heSoLoaiVe = layHeSoLoaiVe(maLoaiVe);

        // 6. Tính giá cơ bản
        // Công thức: Khoảng cách * Đơn giá * Hệ số toa * Hệ số loại vé / 1000
        double giaCoBan = (double) khoangCach * donGia * heSoToa * heSoLoaiVe / 1000.0;

        System.out.println("[BanVeService] Giá cơ bản: " + khoangCach + "km * " + donGia + " * " 
                + heSoToa + " * " + heSoLoaiVe + " = " + giaCoBan);

        // 7. Áp dụng khuyến mãi (nếu có)
        double giaSauKM = giaCoBan;
        if (maKM != null && !maKM.isBlank()) {
            giaSauKM = apDungKhuyenMai(giaCoBan, maKM);
        }

        // Làm tròn về 500 đồng gần nhất
        return lamTron500((long) giaSauKM);
    }

    /**
     * Tính khoảng cách giữa hai ga trong tuyến.
     */
    private int tinhKhoangCachGa(String maTuyen, String maGaDi, String maGaDen) {
        if (maTuyen == null || maGaDi == null || maGaDen == null) {
            return 500; // Giá trị mặc định
        }

        return Tx.noTx(em -> {
            // Lấy khoảng cách tích lũy của ga đi
            var optGaDi = gaTrongTuyenRepo.findByMaTuyenVaMaGa(maTuyen, maGaDi);
            int khoangCachDi = optGaDi.map(GaTrongTuyen::getKhoangCachTichLuy).orElse(0);

            // Lấy khoảng cách tích lũy của ga đến
            var optGaDen = gaTrongTuyenRepo.findByMaTuyenVaMaGa(maTuyen, maGaDen);
            int khoangCachDen = optGaDen.map(GaTrongTuyen::getKhoangCachTichLuy).orElse(0);

            // Khoảng cách = |Khoảng cách đến - Khoảng cách đi|
            return Math.abs(khoangCachDen - khoangCachDi);
        });
    }

    /**
     * Lấy đơn giá của tuyến.
     */
    private int layDonGiaTuyen(String maTuyen) {
        if (maTuyen == null || maTuyen.isBlank()) {
            return 3000; // Giá trị mặc định
        }

        return Tx.noTx(em -> {
            var query = em.createQuery(
                    "SELECT t.donGiaKM FROM Tuyen t WHERE t.maTuyen = :maTuyen", 
                    Integer.class);
            query.setParameter("maTuyen", maTuyen);
            List<Integer> results = query.getResultList();
            return results.isEmpty() ? 3000 : results.get(0);
        });
    }

    /**
     * Lấy hệ số loại toa.
     */
    private double layHeSoLoaiToa(String maLoaiToa) {
        if (maLoaiToa == null || maLoaiToa.isBlank()) {
            return 1.0;
        }

        return Tx.noTx(em -> {
            var query = em.createQuery(
                    "SELECT lt.heSo FROM LoaiToa lt WHERE lt.maLoaiToa = :maLoaiToa",
                    Double.class);
            query.setParameter("maLoaiToa", maLoaiToa);
            List<Double> results = query.getResultList();
            return results.isEmpty() ? 1.0 : results.get(0);
        });
    }

    /**
     * Lấy hệ số loại vé (mức giảm giá - hệ số nhân).
     * VT01 = 1.0 (Người lớn)
     * VT02 = 0.75 (Trẻ em)
     * VT03 = 0.5 (Sinh viên)
     * VT04 = 0.9 (Người cao tuổi)
     */
    private double layHeSoLoaiVe(String maLoaiVe) {
        if (maLoaiVe == null || maLoaiVe.isBlank()) {
            return 1.0;
        }

        Optional<Double> mucGiamGia = loaiVeRepo.findMucGiamGia(maLoaiVe);
        return mucGiamGia.orElse(1.0);
    }

    /**
     * Áp dụng khuyến mãi vào giá vé.
     */
    private double apDungKhuyenMai(double giaGoc, String maKM) {
        Optional<KhuyenMai> optKM = khuyenMaiRepo.findByMaKM(maKM);
        if (optKM.isEmpty()) {
            return giaGoc;
        }

        KhuyenMai km = optKM.get();
        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra khuyến mãi còn hiệu lực
        if (km.getNgayBatDau() != null && km.getNgayKetThuc() != null) {
            if (km.getNgayBatDau().isAfter(now) || km.getNgayKetThuc().isBefore(now)) {
                System.out.println("[BanVeService] Khuyến mãi " + maKM + " đã hết hạn hoặc chưa bắt đầu.");
                return giaGoc;
            }
        }

        BigDecimal giaTriGiam = km.getGiaTriGiam();
        if (giaTriGiam == null) {
            return giaGoc;
        }

        String loaiKM = km.getLoaiKM();
        if ("PHAN_TRAM_GIA".equals(loaiKM)) {
            // Giảm theo phần trăm
            double phanTram = giaTriGiam.doubleValue() / 100.0;
            return giaGoc * (1 - phanTram);
        } else if ("CO_DINH".equals(loaiKM)) {
            // Giảm cố định
            return Math.max(0, giaGoc - giaTriGiam.doubleValue());
        }

        return giaGoc;
    }

    /**
     * Làm tròn giá về 500 đồng gần nhất.
     */
    private BigDecimal lamTron500(long gia) {
        long lamTron = (long) Math.ceil(gia / 500.0) * 500;
        return BigDecimal.valueOf(lamTron);
    }

    // ================================================================================
    // 5. TẠO MÃ HÓA ĐƠN VÀ MÃ VÉ
    // ================================================================================

    /**
     * Tạo mã vé mới với format: VE[DDMMYY][STT6 chữ số].
     * Thread-safe với AtomicInteger.
     */
    public synchronized String taoMaVe() {
        LocalDate today = LocalDate.now();
        String ngayStr = today.format(DateTimeFormatter.ofPattern("ddMMyy"));
        String prefix = "VE" + ngayStr;

        // Lấy STT lớn nhất từ DB để tránh trùng lặp
        Optional<String> lastMaVe = veRepo.findLastMaVeByPrefix(prefix);
        int nextSeq = 1;
        if (lastMaVe.isPresent()) {
            String last = lastMaVe.get();
            if (last.length() > prefix.length()) {
                try {
                    nextSeq = Integer.parseInt(last.substring(prefix.length())) + 1;
                } catch (NumberFormatException e) {
                    nextSeq = 1;
                }
            }
        }

        String maVe = prefix + String.format("%06d", nextSeq);
        System.out.println("[BanVeService] Tạo mã vé mới: " + maVe);
        return maVe;
    }

    /**
     * Tạo mã hóa đơn mới với format: HD[CC][DDMMYY][MaNV 4 ký tự cuối][STT 4 chữ số].
     * 
     * @param maNV Mã nhân viên lập hóa đơn
     * @param soHieuCa Số hiệu ca làm việc
     * @return Mã hóa đơn mới
     */
    public String taoMaHoaDon(String maNV, String soHieuCa) {
        String ngayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        
        // Lấy 4 ký tự cuối của mã NV
        String maNVRutGon = "0000";
        if (maNV != null && maNV.length() >= 4) {
            maNVRutGon = maNV.substring(maNV.length() - 4);
        }

        // Tạo prefix đầy đủ
        String prefix = "HD" + (soHieuCa != null ? soHieuCa : "01") + ngayStr + maNVRutGon;

        // Tìm STT lớn nhất
        int nextSeq = laySTTMaxHoaDon(prefix);

        String maHD = prefix + String.format("%04d", nextSeq);
        System.out.println("[BanVeService] Tạo mã hóa đơn mới: " + maHD);
        return maHD;
    }

    /**
     * Lấy STT lớn nhất của hóa đơn theo prefix.
     */
    private int laySTTMaxHoaDon(String prefix) {
        return Tx.noTx(em -> {
            String jpql = "SELECT h.maHD FROM HoaDon h WHERE h.maHD LIKE :prefix ORDER BY h.maHD DESC";
            List<String> results = em.createQuery(jpql, String.class)
                    .setParameter("prefix", prefix + "%")
                    .setMaxResults(1)
                    .getResultList();

            if (results.isEmpty()) {
                return 1;
            }

            String lastMaHD = results.get(0);
            if (lastMaHD.length() > prefix.length()) {
                try {
                    return Integer.parseInt(lastMaHD.substring(prefix.length())) + 1;
                } catch (NumberFormatException e) {
                    return 1;
                }
            }
            return 1;
        });
    }

    // ================================================================================
    // 6. THỰC HIỆN BÁN VÉ (VỚI PESSIMISTIC LOCKING)
    // ================================================================================

    /**
     * Thực hiện bán vé với Pessimistic Locking để chống tranh chấp ghế.
     * 
     * Quy trình:
     * 1. Khóa và kiểm tra các ghế có trống không (Pessimistic Lock)
     * 2. Tạo hoặc cập nhật thông tin khách hàng
     * 3. Tạo hóa đơn
     * 4. Tạo các vé và Chi tiết hóa đơn
     * 5. Commit transaction
     *
     * @param listVe Danh sách vé cần tạo
     * @param khachHangDaiDien Thông tin khách hàng đại diện
     * @param maChuyenTau Mã chuyến tàu
     * @param maNV Mã nhân viên bán vé
     * @param maKM Mã khuyến mãi (có thể null)
     * @param phuongThucThanhToan Hình thức thanh toán
     * @return BanVeResultDTO chứa kết quả giao dịch
     */
    public BanVeResultDTO thucHienBanVe(
            List<TicketDTO> listVe,
            PassengerDTO khachHangDaiDien,
            String maChuyenTau,
            String maNV,
            String maKM,
            String phuongThucThanhToan) {

        System.out.println("[BanVeService] Bắt đầu giao dịch bán vé. Số vé: " + listVe.size());

        if (listVe == null || listVe.isEmpty()) {
            return BanVeResultDTO.failure("Danh sách vé trống.");
        }

        try {
            // Thực hiện trong transaction với Pessimistic Locking
            return Tx.inTx(em -> {
                // ====================================================================
                // BƯỚC 1: KHÓA VÀ KIỂM TRA CÁC GHẾ (PESSIMISTIC LOCKING)
                // ====================================================================
                List<String> cacMaCho = listVe.stream()
                        .map(TicketDTO::maChoDat)
                        .collect(Collectors.toList());

                // Lock các ghế để kiểm tra và đánh dấu
                for (String maCho : cacMaCho) {
                    // Lock ghế với Pessimistic Write Lock
                    List<ChoDat> choList = em.createQuery(
                            "SELECT c FROM ChoDat c WHERE c.maCho = :maCho", ChoDat.class)
                            .setParameter("maCho", maCho)
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                            .getResultList();

                    if (choList.isEmpty()) {
                        throw new RuntimeException("Không tìm thấy chỗ: " + maCho);
                    }

                    // Kiểm tra xem ghế đã được bán chưa
                    List<Ve> veDaTonTai = em.createQuery(
                            "SELECT v FROM Ve v WHERE v.maChuyenTau = :maChuyen AND v.maChoDat = :maCho AND v.trangThai = :trangThai",
                            Ve.class)
                            .setParameter("maChuyen", maChuyenTau)
                            .setParameter("maCho", maCho)
                            .setParameter("trangThai", "DA_BAN")
                            .getResultList();

                    if (!veDaTonTai.isEmpty()) {
                        throw new RuntimeException("Ghế " + maCho + " đã có người đặt!");
                    }
                }

                System.out.println("[BanVeService] Tất cả ghế đã được lock và kiểm tra thành công.");

                // ====================================================================
                // BƯỚC 2: XỬ LÝ KHÁCH HÀNG
                // ====================================================================
                Map<String, KhachHang> khachHangMap = xuLyKhachHang(em, listVe, khachHangDaiDien);
                
                // Lấy mã KH đại diện (người đầu tiên trong danh sách)
                String maKHDaiDien = khachHangMap.values().iterator().next().getMaKH();

                // ====================================================================
                // BƯỚC 3: TÍNH TOÁN GIÁ VÉ VÀ TẠO DANH SÁCH VÉ
                // ====================================================================
                List<Ve> danhSachVeMoi = new ArrayList<>();
                List<TicketResultDTO> ticketResults = new ArrayList<>();
                
                double tongTienGoc = 0;
                double tongGiamGia = 0;
                double tongTienPhaiTra = 0;

                // Lấy thông tin chuyến tàu một lần
                ChuyenTau chuyenTau = em.find(ChuyenTau.class, maChuyenTau);
                if (chuyenTau == null) {
                    throw new RuntimeException("Không tìm thấy chuyến tàu: " + maChuyenTau);
                }

                // Lấy thông tin tuyến cho việc tính khoảng cách
                Tuyen tuyen = chuyenTau.getTuyen();
                int donGia = tuyen != null ? tuyen.getDonGiaKM() : 3000;

                // Lấy thông tin khuyến mãi (nếu có)
                KhuyenMai khuyenMai = null;
                if (maKM != null && !maKM.isBlank()) {
                    khuyenMai = em.find(KhuyenMai.class, maKM);
                }

                // Lấy thông tin loại toa cho mỗi ghế
                Map<String, LoaiToa> loaiToaMap = new HashMap<>();
                for (TicketDTO ticket : listVe) {
                    String maToa = ticket.maChoDat().split("-")[0]; // Lấy mã toa từ mã chỗ
                    if (!loaiToaMap.containsKey(maToa)) {
                        // Tìm loại toa từ toa
                        List<Toa> toaList = em.createQuery(
                                "SELECT t FROM Toa t JOIN FETCH t.loaiToaRef WHERE t.maToa = :maToa", Toa.class)
                                .setParameter("maToa", maToa)
                                .getResultList();
                        if (!toaList.isEmpty() && toaList.get(0).getLoaiToaRef() != null) {
                            loaiToaMap.put(maToa, toaList.get(0).getLoaiToaRef());
                        }
                    }
                }

                // Tính khoảng cách ga một lần
                int khoangCach = 500;
                if (chuyenTau.getGaDi() != null && chuyenTau.getGaDen() != null) {
                    khoangCach = tinhKhoangCachGa(chuyenTau.getMaTuyen(),
                            chuyenTau.getGaDi().getMaGa(),
                            chuyenTau.getGaDen().getMaGa());
                }

                int sttVe = 0;
                for (TicketDTO ticket : listVe) {
                    sttVe++;
                    
                    // Lấy thông tin khách hàng cho vé này
                    KhachHang kh = khachHangMap.get(ticket.maChoDat());
                    String maKh = kh != null ? kh.getMaKH() : maKHDaiDien;

                    // Lấy thông tin loại toa
                    String maToa = ticket.maChoDat().split("-")[0];
                    LoaiToa loaiToa = loaiToaMap.get(maToa);
                    double heSoToa = loaiToa != null ? loaiToa.getHeSo() : 1.0;

                    // Tính giá vé
                    double giaGoc = tinhGiaGoc(khoangCach, donGia, heSoToa, ticket.maLoaiVe());
                    double giaSauGiam = giaGoc;

                    // Áp dụng khuyến mãi (nếu có)
                    if (khuyenMai != null) {
                        giaSauGiam = giaGoc;
                        // Tính giảm giá cho từng vé (nếu KM cố định thì chia đều)
                        double giamGiaTrenVe = tinhGiamGiaMoiVe(giaGoc, khuyenMai, listVe.size(), sttVe);
                        giaSauGiam = Math.max(0, giaGoc - giamGiaTrenVe);
                    }

                    tongTienGoc += giaGoc;
                    tongGiamGia += (giaGoc - giaSauGiam);
                    tongTienPhaiTra += giaSauGiam;

                    // Tạo mã vé
                    String maVe = taoMaVe();

                    // Tạo entity Vé
                    Ve ve = new Ve();
                    ve.setMaVe(maVe);
                    ve.setMaChuyenTau(maChuyenTau);
                    ve.setMaChoDat(ticket.maChoDat());
                    ve.setMaNV(maNV);
                    ve.setMaKhachHang(maKh);
                    ve.setMaLoaiVe(ticket.maLoaiVe());
                    ve.setGiaVe(giaSauGiam);
                    ve.setTrangThai("DA_BAN");

                    em.persist(ve);
                    danhSachVeMoi.add(ve);

                    // Thêm vào ticket results
                    ticketResults.add(new TicketResultDTO(
                            maVe,
                            ticket.maChoDat(),
                            kh != null ? kh.getHoTen() : "",
                            ticket.maLoaiVe(),
                            BigDecimal.valueOf(giaGoc),
                            BigDecimal.valueOf(giaSauGiam),
                            BigDecimal.valueOf(giaGoc - giaSauGiam)
                    ));
                }

                // ====================================================================
                // BƯỚC 4: TẠO HÓA ĐƠN
                // ====================================================================
                String maHD = taoMaHoaDon(maNV, "01");
                HoaDon hoaDon = new HoaDon();
                hoaDon.setMaHD(maHD);
                hoaDon.setMaKhachHang(maKHDaiDien);
                hoaDon.setMaNVLap(maNV);
                hoaDon.setMaKM(maKM);
                hoaDon.setTongTien(tongTienPhaiTra);
                hoaDon.setTongCong(tongTienPhaiTra);
                hoaDon.setNgayLap(LocalDateTime.now());
                hoaDon.setPhuongThuc(phuongThucThanhToan);
                hoaDon.setLoaiHoaDon("Bán vé trực tiếp");

                em.persist(hoaDon);

                // ====================================================================
                // BƯỚC 5: TẠO CHI TIẾT HÓA ĐƠN
                // ====================================================================
                for (Ve ve : danhSachVeMoi) {
                    ChiTietHoaDon cthd = new ChiTietHoaDon();
                    cthd.setMaHD(maHD);
                    cthd.setMaVe(ve.getMaVe());
                    cthd.setDonGia(ve.getGiaVe());
                    cthd.setSoLuong(1);
                    // Set relationships for @MapsId
                    cthd.setHoaDon(hoaDon);
                    cthd.setVe(ve);
                    em.persist(cthd);
                }

                System.out.println("[BanVeService] Giao dịch bán vé thành công. Mã HD: " + maHD);
                System.out.println("[BanVeService] Tổng tiền: " + tongTienPhaiTra);

                return BanVeResultDTO.success(
                        maHD,
                        maKHDaiDien,
                        BigDecimal.valueOf(tongTienPhaiTra),
                        BigDecimal.valueOf(tongGiamGia),
                        ticketResults
                );
            });

        } catch (Exception e) {
            System.err.println("[BanVeService] Lỗi khi bán vé: " + e.getMessage());
            e.printStackTrace();
            return BanVeResultDTO.failure("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Xử lý thông tin khách hàng: tìm hoặc tạo mới.
     */
    private Map<String, KhachHang> xuLyKhachHang(
            jakarta.persistence.EntityManager em,
            List<TicketDTO> listVe,
            PassengerDTO khachHangDaiDien) {

        Map<String, KhachHang> result = new HashMap<>();
        LocalDate homNay = LocalDate.now();
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));

        // Đếm số KH cần tạo hôm nay để tạo mã
        int sttHienTai = demSoKhachHangHomNay(em, homNay);

        for (TicketDTO ticket : listVe) {
            PassengerDTO kh = ticket.khachHang();
            if (kh == null) {
                kh = khachHangDaiDien;
            }

            KhachHang khachHang = null;

            // Tìm khách hàng theo CCCD
            if (kh.cccd() != null && !kh.cccd().isBlank()) {
                List<KhachHang> existing = em.createQuery(
                        "SELECT k FROM KhachHang k WHERE k.soCCCD = :cccd", KhachHang.class)
                        .setParameter("cccd", kh.cccd())
                        .getResultList();
                if (!existing.isEmpty()) {
                    khachHang = existing.get(0);
                    // Cập nhật thông tin mới
                    khachHang.setHoTen(kh.hoTen());
                    khachHang.setSdt(kh.sdt());
                    khachHang.setNgaySinh(kh.ngaySinh());
                    khachHang.setGioiTinh(kh.gioiTinh());
                    em.merge(khachHang);
                }
            }

            // Tạo mới nếu không tìm thấy
            if (khachHang == null) {
                sttHienTai++;
                String maKH = String.format("KH%s%04d", ngayStr, sttHienTai);
                khachHang = KhachHang.builder()
                        .maKH(maKH)
                        .hoTen(kh.hoTen() != null ? kh.hoTen() : "Khách vãng lai")
                        .soCCCD(kh.cccd() != null ? kh.cccd() : "000000000000")
                        .sdt(kh.sdt())
                        .ngaySinh(kh.ngaySinh())
                        .gioiTinh(kh.gioiTinh())
                        .build();
                em.persist(khachHang);
            }

            result.put(ticket.maChoDat(), khachHang);
        }

        return result;
    }

    /**
     * Đếm số khách hàng đã tạo hôm nay.
     */
    private int demSoKhachHangHomNay(jakarta.persistence.EntityManager em, LocalDate ngay) {
        String pattern = "KH" + ngay.format(DateTimeFormatter.ofPattern("ddMMyy")) + "%";
        List<Long> count = em.createQuery(
                "SELECT COUNT(k) FROM KhachHang k WHERE k.maKH LIKE :pattern", Long.class)
                .setParameter("pattern", pattern)
                .getResultList();
        return count.isEmpty() ? 0 : count.get(0).intValue();
    }

    /**
     * Tính giá gốc dựa trên khoảng cách, đơn giá, hệ số toa và loại vé.
     */
    private double tinhGiaGoc(int khoangCach, int donGia, double heSoToa, String maLoaiVe) {
        double heSoLoaiVe = layHeSoLoaiVe(maLoaiVe);
        return (double) khoangCach * donGia * heSoToa * heSoLoaiVe / 1000.0;
    }

    /**
     * Tính giảm giá cho mỗi vé (hỗ trợ chia đều cho KM cố định).
     */
    private double tinhGiamGiaMoiVe(double giaGoc, KhuyenMai km, int soLuongVe, int sttVe) {
        if (km == null || km.getGiaTriGiam() == null) {
            return 0;
        }

        String loaiKM = km.getLoaiKM();
        if ("PHAN_TRAM_GIA".equals(loaiKM)) {
            // Giảm theo phần trăm
            return giaGoc * km.getGiaTriGiam().doubleValue() / 100.0;
        } else if ("CO_DINH".equals(loaiKM)) {
            // Giảm cố định: chia đều cho các vé (trừ vé cuối cùng)
            double giamCoDinh = km.getGiaTriGiam().doubleValue();
            if (sttVe < soLuongVe) {
                return Math.round(giamCoDinh / soLuongVe);
            } else {
                // Vé cuối: lấy phần còn lại để xử lý sai số
                return giamCoDinh - Math.round(giamCoDinh / soLuongVe) * (soLuongVe - 1);
            }
        }
        return 0;
    }

    // ================================================================================
    // 7. CÁC PHƯƠNG THỨC HỖ TRỢ
    // ================================================================================

    /**
     * Lấy danh sách loại vé.
     */
    public List<LoaiVe> getDanhSachLoaiVe() {
        return Tx.noTx(em -> 
            em.createQuery("SELECT lv FROM LoaiVe lv ORDER BY lv.maLoaiVe", LoaiVe.class)
                .getResultList()
        );
    }

    /**
     * Lấy danh sách khuyến mãi còn hiệu lực.
     */
    public List<KhuyenMai> getDanhSachKhuyenMaiConHieuLuc() {
        return khuyenMaiRepo.findConHieuLuc(LocalDateTime.now());
    }

    /**
     * Lấy thông tin chi tiết một chuyến tàu.
     */
    public Optional<ChuyenTauDTO> getChiTietChuyenTau(String maChuyenTau) {
        return chuyenTauRepo.findByMaChuyenTau(maChuyenTau)
                .map(this::chuyenTauSangDTO);
    }

    /**
     * Lấy thông tin một khách hàng theo CCCD.
     */
    public Optional<KhachHang> getKhachHangByCCCD(String cccd) {
        if (cccd == null || cccd.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(khachHangRepo.findByCccd(cccd));
    }

    /**
     * Lấy thông tin một khách hàng theo mã.
     */
    public Optional<KhachHang> getKhachHangByMa(String maKH) {
        return khachHangRepo.findByMaKH(maKH);
    }

    /**
     * Kiểm tra ghế đã bán hay chưa (phiên bản đơn giản).
     */
    public boolean kiemTraGheDaBan(String maChoDat, String maChuyenTau) {
        return !kiemTraGheTrong(maChoDat, maChuyenTau);
    }

    /**
     * Lấy thông tin loại toa.
     */
    public Optional<LoaiToa> getLoaiToa(String maLoaiToa) {
        return loaiToaRepo.findByMaLoaiToa(maLoaiToa);
    }

    /**
     * Lấy thông tin loại vé.
     */
    public Optional<LoaiVe> getLoaiVe(String maLoaiVe) {
        return Tx.noTx(em -> {
            List<LoaiVe> results = em.createQuery(
                    "SELECT lv FROM LoaiVe lv WHERE lv.maLoaiVe = :maLoaiVe", LoaiVe.class)
                    .setParameter("maLoaiVe", maLoaiVe)
                    .getResultList();
            return results.isEmpty() ? Optional.<LoaiVe>empty() : Optional.of(results.get(0));
        });
    }

    public static void main(String[] args) {
        BanVeService service = new BanVeService();
        List<LoaiVe> loaiVeList = service.getDanhSachLoaiVe();
        System.out.println("Danh sách loại vé:");
        for (LoaiVe lv : loaiVeList) {
            System.out.println("- " + lv.getMaLoaiVe() + ": " + lv.getTenLoaiVe() + " (Mức giảm: " + lv.getMucGiamGia() + "%)");
        }

    }
}
