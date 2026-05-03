package JPA_Project.repository;

import JPA_Project.entity.ChuyenTau;
import JPA_Project.db.Tx;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ChuyenTau Repository - Wrapper cho repository.ChuyenTauRepository
 */
public class ChuyenTauRepository extends BaseRepository<ChuyenTau, String> {

    public Optional<ChuyenTau> findByMaChuyenTau(String maChuyenTau) {
        return Optional.ofNullable(findById(maChuyenTau));
    }

    public List<ChuyenTau> findByNgayKhoiHanh(LocalDate ngayKhoiHanh) {
        return Tx.noTx(em -> em.createQuery(
                        "select ct from ChuyenTau ct where ct.ngayKhoiHanh = :ngayKhoiHanh",
                        ChuyenTau.class)
                .setParameter("ngayKhoiHanh", ngayKhoiHanh)
                .getResultList());
    }

    public List<ChuyenTau> findAllOrdered() {
        return Tx.noTx(em -> em.createQuery(
                        "select ct from ChuyenTau ct " +
                                "left join fetch ct.gaDi " +
                                "left join fetch ct.gaDen " +
                                "left join fetch ct.tau " +
                                "left join fetch ct.nhanVien " +
                                "left join fetch ct.tuyen " +
                                "order by ct.ngayKhoiHanh desc, ct.gioKhoiHanh desc",
                        ChuyenTau.class)
                .getResultList());
    }

    public List<ChuyenTau> findByGaDiGaDenNgay(String maGaDi, String maGaDen, LocalDate ngayDi) {
        System.out.println("findByGaDiGaDenNgay: " + maGaDi + " -> " + maGaDen + " ngay " + ngayDi);

        return Tx.noTx(em -> {
            // Sử dụng JPQL với JOIN FETCH để eager load tất cả related entities
            String jpql = "SELECT ct FROM ChuyenTau ct " +
                    "LEFT JOIN FETCH ct.gaDi " +
                    "LEFT JOIN FETCH ct.gaDen " +
                    "LEFT JOIN FETCH ct.tuyen " +
                    "LEFT JOIN FETCH ct.tau " +
                    "LEFT JOIN FETCH ct.nhanVien " +
                    "WHERE UPPER(FUNCTION('RTRIM', FUNCTION('LTRIM', ct.gaDi.maGa))) = UPPER(:gaDi) " +
                    "AND UPPER(FUNCTION('RTRIM', FUNCTION('LTRIM', ct.gaDen.maGa))) = UPPER(:gaDen) " +
                    "AND ct.ngayKhoiHanh = :ngayDi " +
                    "ORDER BY ct.gioKhoiHanh ASC";

            List<ChuyenTau> results = em.createQuery(jpql, ChuyenTau.class)
                    .setParameter("gaDi", maGaDi.trim())
                    .setParameter("gaDen", maGaDen.trim())
                    .setParameter("ngayDi", ngayDi)
                    .getResultList();

            System.out.println("Tìm thấy " + results.size() + " chuyến tàu");
            return results;
        });
    }

    public Optional<ChuyenTau> findDetailedByMaChuyenTau(String maChuyenTau) {
        return Tx.noTx(em -> em.createQuery(
                        "select ct from ChuyenTau ct " +
                                "left join fetch ct.gaDi " +
                                "left join fetch ct.gaDen " +
                                "left join fetch ct.tau " +
                                "left join fetch ct.nhanVien " +
                                "left join fetch ct.tuyen " +
                                "where ct.maChuyenTau = :maChuyenTau",
                        ChuyenTau.class)
                .setParameter("maChuyenTau", maChuyenTau)
                .getResultStream()
                .findFirst());
    }

    public List<ChuyenTau> findByMaTuyenVaNgay(String maTuyen, LocalDate ngayDi) {
        return Tx.noTx(em -> em.createQuery(
                        "select ct from ChuyenTau ct where ct.maTuyen = :maTuyen and ct.ngayKhoiHanh = :ngayDi order by ct.gioKhoiHanh asc",
                        ChuyenTau.class)
                .setParameter("maTuyen", maTuyen)
                .setParameter("ngayDi", ngayDi)
                .getResultList());
    }

    public boolean existsByMaTuyenVaNgay(String maTuyen, LocalDate ngayDi) {
        Long count = Tx.noTx(em -> em.createQuery(
                        "select count(ct) from ChuyenTau ct where ct.maTuyen = :maTuyen and ct.ngayKhoiHanh = :ngayDi",
                        Long.class)
                .setParameter("maTuyen", maTuyen)
                .setParameter("ngayDi", ngayDi)
                .getSingleResult());
        return count != null && count > 0;
    }

    public boolean existsByMaChuyenTauPrefix(String prefix) {
        return Tx.noTx(em -> !em.createQuery(
                        "select ct.maChuyenTau from ChuyenTau ct where ct.maChuyenTau like :prefix",
                        String.class)
                .setParameter("prefix", prefix)
                .setMaxResults(1)
                .getResultList().isEmpty());
    }

    public static void main(String[] args) {
        //test findByGaDiGaDenNgay
        //mẫu VALUES (N'SE1_201225_DNA_NTR', N'SE1       ', N'SPT2', N'ADMIN001', N'DNA', N'NTR', CAST(N'2025-12-20' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
        ChuyenTauRepository repository = new ChuyenTauRepository();
        List<ChuyenTau> chuyenTaus = repository.findByGaDiGaDenNgay("DNA", "NTR", LocalDate.of(2026, 05, 02));
        System.out.println("Tìm thấy " + chuyenTaus.size() + " chuyến tàu từ ga DNA đến ga NTR vào ngày 2025-12-20:");
    }//mẫu VALUES (N'SE1_201225_DNA_NTR', N'SE1       ', N'SPT2', N'ADMIN001', N'DNA', N'NTR', CAST(N'2025-12-20' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
}
