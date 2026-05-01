package JPA_Project.repository;

import JPA_Project.entity.LoaiVe;
import JPA_Project.db.Tx;

import java.util.Optional;

/**
 * LoaiVe Repository - Wrapper cho repository.LoaiVeRepository
 */
public class LoaiVeRepository extends BaseRepository<LoaiVe, String> {

    public Optional<Double> findMucGiamGia(String maLoaiVe) {
        return Tx.noTx(em -> em.createQuery(
                        "select lv.mucGiamGia from LoaiVe lv where lv.maLoaiVe = :maLoaiVe",
                        Double.class)
                .setParameter("maLoaiVe", maLoaiVe)
                .getResultStream()
                .findFirst());
    }
}
