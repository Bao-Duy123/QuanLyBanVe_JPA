package JPA_Project.service;

import JPA_Project.db.Tx;
import JPA_Project.entity.NhanVien;
import JPA_Project.entity.TaiKhoan;
import JPA_Project.repository.NhanVienRepository;
import JPA_Project.repository.TaiKhoanRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * NhanVienService - Tầng Service xử lý nghiệp vụ quản lý nhân viên.
 * Tách biệt hoàn toàn Logic Nghiệp vụ ra khỏi GUI.
 * Sử dụng JPA (EntityManager) để truy cập database.
 * Hỗ trợ đa luồng với ExecutorServiceManager.
 * 
 * Các phương thức chính:
 * - getAllTaiKhoanWithNhanVien: Lấy tất cả tài khoản kèm nhân viên
 * - searchNhanVien: Tìm kiếm nhân viên theo nhiều tiêu chí
 * - addNhanVien: Thêm nhân viên mới
 * - updateNhanVien: Cập nhật thông tin nhân viên
 * - softDeleteNhanVien: Xóa mềm nhân viên (Ngừng hoạt động)
 * - getStatistics: Lấy thống kê nhân viên
 * - getNextMaNV: Lấy mã nhân viên tiếp theo theo prefix
 */
public class NhanVienService {

    private final NhanVienRepository nhanVienRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final ExecutorService executorService;

    public NhanVienService() {
        this.nhanVienRepo = new NhanVienRepository();
        this.taiKhoanRepo = new TaiKhoanRepository();
        this.executorService = ExecutorServiceManager.getInstance();
    }

    /**
     * Lấy tất cả tài khoản kèm thông tin nhân viên (JOIN FETCH).
     */
    public List<TaiKhoan> getAllTaiKhoanWithNhanVien() {
        return Tx.noTx(em -> {
            String jpql = "SELECT tk FROM TaiKhoan tk JOIN FETCH tk.nhanVien";
            return em.createQuery(jpql, TaiKhoan.class).getResultList();
        });
    }

    /**
     * Lấy tất cả tài khoản kèm thông tin nhân viên (Async - đa luồng).
     */
    public CompletableFuture<List<TaiKhoan>> getAllTaiKhoanWithNhanVienAsync() {
        return CompletableFuture.supplyAsync(this::getAllTaiKhoanWithNhanVien, executorService);
    }

    /**
     * Tìm tài khoản theo mã nhân viên.
     */
    public Optional<TaiKhoan> findTaiKhoanByMaNV(String maNV) {
        return Tx.noTx(em -> {
            String jpql = "SELECT tk FROM TaiKhoan tk JOIN FETCH tk.nhanVien WHERE tk.maNV = :maNV";
            var result = em.createQuery(jpql, TaiKhoan.class)
                    .setParameter("maNV", maNV)
                    .getResultList();
            return result.isEmpty() ? Optional.<TaiKhoan>empty() : Optional.of(result.get(0));
        });
    }

    /**
     * Tìm nhân viên theo mã NV.
     */
    public Optional<NhanVien> findNhanVienByMaNV(String maNV) {
        return nhanVienRepo.findByMaNV(maNV);
    }

    /**
     * Tìm kiếm nhân viên theo nhiều tiêu chí.
     */
    public List<TaiKhoan> searchNhanVien(String searchBy, String searchTerm, String status) {
        return Tx.noTx(em -> {
            StringBuilder jpql = new StringBuilder("SELECT tk FROM TaiKhoan tk JOIN FETCH tk.nhanVien WHERE tk.trangThai = :status");
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                switch (searchBy) {
                    case "Mã nhân viên":
                        jpql.append(" AND tk.nhanVien.maNV LIKE :searchTerm");
                        break;
                    case "Số điện thoại":
                        jpql.append(" AND tk.nhanVien.sdt LIKE :searchTerm");
                        break;
                    case "Số CCCD":
                        jpql.append(" AND tk.nhanVien.soCCCD LIKE :searchTerm");
                        break;
                    case "Họ tên nhân viên":
                        jpql.append(" AND tk.nhanVien.hoTen LIKE :searchTerm");
                        break;
                }
            }
            
            jpql.append(" ORDER BY tk.nhanVien.maNV");
            
            var query = em.createQuery(jpql.toString(), TaiKhoan.class);
            query.setParameter("status", status);
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                query.setParameter("searchTerm", "%" + searchTerm + "%");
            }
            
            return query.getResultList();
        });
    }

    /**
     * Tìm kiếm nhân viên (Async - đa luồng).
     */
    public CompletableFuture<List<TaiKhoan>> searchNhanVienAsync(String searchBy, String searchTerm, String status) {
        return CompletableFuture.supplyAsync(() -> searchNhanVien(searchBy, searchTerm, status), executorService);
    }

    /**
     * Thêm nhân viên mới kèm tài khoản.
     * Transaction: đảm bảo tính toàn vẹn dữ liệu.
     */
    public boolean addNhanVien(NhanVien nv, TaiKhoan tk) {
        try {
            return Tx.inTx(em -> {
                if (nv == null || tk == null) {
                    throw new IllegalArgumentException("Thông tin Nhân viên hoặc Tài khoản không hợp lệ.");
                }
                
                if (nv.getHoTen() == null || nv.getHoTen().trim().isEmpty()) {
                    throw new IllegalArgumentException("Họ tên không được trống.");
                }
                
                if (nv.getGioiTinh() == null) {
                    throw new IllegalArgumentException("Giới tính không được trống.");
                }
                
                if (tk.getMatKhau() == null || tk.getMatKhau().isEmpty()) {
                    throw new IllegalArgumentException("Mật khẩu không được trống.");
                }

                // Tạo mã nhân viên mới
                String prefix = getPrefixByChucVu(nv.getChucVu());
                String newMaNV = generateNewMaNV(em, prefix);
                
                // Cập nhật mã vào entity
                nv.setMaNV(newMaNV);
                tk.setMaNV(newMaNV);
                tk.setTenDangNhap(newMaNV);
                
                if (tk.getNgayTao() == null) {
                    tk.setNgayTao(LocalDate.now());
                }
                
                if (tk.getTrangThai() == null) {
                    tk.setTrangThai("Đang hoạt động");
                }
                
                // Thiết lập mối quan hệ
                tk.setNhanVien(nv);
                
                // Lưu nhân viên trước
                em.persist(nv);
                
                // Lưu tài khoản
                em.persist(tk);
                
                System.out.println("[NhanVienService] Thêm nhân viên thành công: " + newMaNV);
                return true;
            });
        } catch (Exception e) {
            System.err.println("[NhanVienService] Lỗi khi thêm nhân viên: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thêm nhân viên: " + e.getMessage(), e);
        }
    }

    /**
     * Thêm nhân viên (Async - đa luồng).
     */
    public CompletableFuture<Boolean> addNhanVienAsync(NhanVien nv, TaiKhoan tk) {
        return CompletableFuture.supplyAsync(() -> addNhanVien(nv, tk), executorService);
    }

    /**
     * Cập nhật thông tin nhân viên.
     */
    public boolean updateNhanVien(NhanVien nv, TaiKhoan tk) {
        try {
            return Tx.inTx(em -> {
                if (nv == null || nv.getMaNV() == null || nv.getMaNV().isEmpty()) {
                    throw new IllegalArgumentException("Mã nhân viên không hợp lệ.");
                }
                
                // Tìm và cập nhật NhanVien
                NhanVien existingNV = em.find(NhanVien.class, nv.getMaNV());
                if (existingNV == null) {
                    throw new IllegalArgumentException("Không tìm thấy nhân viên: " + nv.getMaNV());
                }
                
                // Cập nhật các trường
                existingNV.setHoTen(nv.getHoTen());
                existingNV.setSoCCCD(nv.getSoCCCD());
                existingNV.setNgaySinh(nv.getNgaySinh());
                existingNV.setEmail(nv.getEmail());
                existingNV.setSdt(nv.getSdt());
                existingNV.setGioiTinh(nv.getGioiTinh());
                existingNV.setDiaChi(nv.getDiaChi());
                existingNV.setNgayVaoLam(nv.getNgayVaoLam());
                existingNV.setChucVu(nv.getChucVu());
                
                // Cập nhật TaiKhoan
                TaiKhoan existingTK = em.find(TaiKhoan.class, nv.getMaNV());
                if (existingTK != null) {
                    existingTK.setMatKhau(tk.getMatKhau());
                    existingTK.setTrangThai(tk.getTrangThai());
                }
                
                System.out.println("[NhanVienService] Cập nhật nhân viên thành công: " + nv.getMaNV());
                return true;
            });
        } catch (Exception e) {
            System.err.println("[NhanVienService] Lỗi khi cập nhật nhân viên: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật nhân viên: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật nhân viên (Async - đa luồng).
     */
    public CompletableFuture<Boolean> updateNhanVienAsync(NhanVien nv, TaiKhoan tk) {
        return CompletableFuture.supplyAsync(() -> updateNhanVien(nv, tk), executorService);
    }

    /**
     * Xóa mềm nhân viên (Đặt trạng thái thành "Ngừng hoạt động").
     */
    public boolean softDeleteNhanVien(String maNV) {
        try {
            return Tx.inTx(em -> {
                TaiKhoan tk = em.find(TaiKhoan.class, maNV);
                if (tk == null) {
                    System.err.println("[NhanVienService] Không tìm thấy tài khoản: " + maNV);
                    return false;
                }
                
                tk.setTrangThai("Ngừng hoạt động");
                System.out.println("[NhanVienService] Xóa mềm nhân viên thành công: " + maNV);
                return true;
            });
        } catch (Exception e) {
            System.err.println("[NhanVienService] Lỗi khi xóa mềm nhân viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa mềm nhân viên (Async - đa luồng).
     */
    public CompletableFuture<Boolean> softDeleteNhanVienAsync(String maNV) {
        return CompletableFuture.supplyAsync(() -> softDeleteNhanVien(maNV), executorService);
    }

    /**
     * Lấy thống kê nhân viên (Tổng số, Nhân viên bán vé, Quản lý).
     */
    public Map<String, Integer> getStatistics() {
        return Tx.noTx(em -> {
            java.util.Map<String, Integer> stats = new java.util.HashMap<>();
            
            // Tổng số tài khoản đang hoạt động
            Long total = em.createQuery(
                    "SELECT COUNT(tk) FROM TaiKhoan tk WHERE tk.trangThai = 'Đang hoạt động'", Long.class)
                    .getSingleResult();
            stats.put("total", total.intValue());
            
            // Số nhân viên bán vé
            Long nhanVien = em.createQuery(
                    "SELECT COUNT(nv) FROM NhanVien nv WHERE nv.chucVu = 'Nhân viên bán vé' " +
                    "AND EXISTS (SELECT tk FROM TaiKhoan tk WHERE tk.maNV = nv.maNV AND tk.trangThai = 'Đang hoạt động')", Long.class)
                    .getSingleResult();
            stats.put("nhanVien", nhanVien.intValue());
            
            // Số quản lý (Quản lý + Trưởng phòng)
            Long quanLy = em.createQuery(
                    "SELECT COUNT(nv) FROM NhanVien nv WHERE nv.chucVu IN ('Quản lý', 'Trưởng phòng') " +
                    "AND EXISTS (SELECT tk FROM TaiKhoan tk WHERE tk.maNV = nv.maNV AND tk.trangThai = 'Đang hoạt động')", Long.class)
                    .getSingleResult();
            stats.put("quanLy", quanLy.intValue());
            
            return stats;
        });
    }

    /**
     * Lấy thống kê (Async - đa luồng).
     */
    public CompletableFuture<Map<String, Integer>> getStatisticsAsync() {
        return CompletableFuture.supplyAsync(this::getStatistics, executorService);
    }

    /**
     * Lấy mã nhân viên tiếp theo theo prefix.
     */
    public String getNextMaNV(String chucVu) {
        String prefix = getPrefixByChucVu(chucVu);
        return Tx.noTx(em -> generateNewMaNV(em, prefix));
    }

    /**
     * Lấy mã nhân viên tiếp theo (Async - đa luồng).
     */
    public CompletableFuture<String> getNextMaNVAsync(String chucVu) {
        return CompletableFuture.supplyAsync(() -> getNextMaNV(chucVu), executorService);
    }

    /**
     * Kiểm tra mã nhân viên đã tồn tại chưa.
     */
    public boolean isMaNVExists(String maNV) {
        return nhanVienRepo.findByMaNV(maNV).isPresent();
    }

    // ================================================================================
    // HELPER METHODS
    // ================================================================================

    /**
     * Lấy prefix theo chức vụ.
     */
    private String getPrefixByChucVu(String chucVu) {
        if (chucVu == null) return "NV";
        switch (chucVu) {
            case "Nhân viên bán vé": return "NVBV";
            case "Quản lý": return "NVQL";
            case "Trưởng phòng": return "NVTP";
            default: return "NV";
        }
    }

    /**
     * Tạo mã nhân viên mới với định dạng: [Prefix][4 số].
     */
    private String generateNewMaNV(jakarta.persistence.EntityManager em, String prefix) {
        String jpql = "SELECT nv.maNV FROM NhanVien nv WHERE nv.maNV LIKE :prefix ORDER BY nv.maNV DESC";
        List<String> results = em.createQuery(jpql, String.class)
                .setParameter("prefix", prefix + "%")
                .setMaxResults(1)
                .getResultList();
        
        int nextNumber = 1;
        if (!results.isEmpty()) {
            String lastMaNV = results.get(0);
            try {
                String numberPart = lastMaNV.substring(prefix.length());
                if (!numberPart.isEmpty()) {
                    nextNumber = Integer.parseInt(numberPart) + 1;
                }
            } catch (NumberFormatException e) {
                System.err.println("[NhanVienService] Không thể phân tích số từ mã NV: " + lastMaNV);
            }
        }
        
        return String.format("%s%04d", prefix, nextNumber);
    }
}
