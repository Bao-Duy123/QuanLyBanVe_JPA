package JPA_Project.repository;

import JPA_Project.entity.KhuyenMai;
import JPA_Project.db.Tx;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * KhuyenMai Repository - Wrapper cho repository.KhuyenMaiRepository
 */
public class KhuyenMaiRepository extends BaseRepository<KhuyenMai, String> {

    public Optional<KhuyenMai> findByMaKM(String maKM) {
        return Optional.ofNullable(findById(maKM));
    }

    public List<KhuyenMai> findConHieuLuc(LocalDateTime thoiDiem) {
        return Tx.noTx(em -> em.createQuery(
                        "select km from KhuyenMai km where km.ngayBatDau <= :thoiDiem and km.ngayKetThuc >= :thoiDiem",
                        KhuyenMai.class)
                .setParameter("thoiDiem", thoiDiem)
                .getResultList());
    }
}
