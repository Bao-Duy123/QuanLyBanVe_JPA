package JPA_Project.repository;

import JPA_Project.entity.LoaiToa;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

/**
 * LoaiToa Repository - Wrapper cho repository.LoaiToaRepository
 */
public class LoaiToaRepository extends BaseRepository<LoaiToa, String> {

    public Optional<LoaiToa> findByMaLoaiToa(String maLoaiToa) {
        return Optional.ofNullable(findById(maLoaiToa));
    }

    public List<LoaiToa> findAllOrderByTenLoaiToa() {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT lt FROM LoaiToa lt ORDER BY lt.tenLoaiToa",
                        LoaiToa.class)
                .getResultList());
    }
}
