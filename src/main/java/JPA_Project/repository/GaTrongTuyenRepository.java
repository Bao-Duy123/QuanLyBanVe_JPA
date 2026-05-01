package JPA_Project.repository;

import JPA_Project.entity.GaTrongTuyen;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

/**
 * GaTrongTuyen Repository - Wrapper cho repository.GaTrongTuyenRepository
 */
public class GaTrongTuyenRepository extends BaseRepository<GaTrongTuyen, String> {

    public List<GaTrongTuyen> findByMaTuyen(String maTuyen) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT gtt FROM GaTrongTuyen gtt WHERE gtt.tuyen.maTuyen = :maTuyen ORDER BY gtt.thuTu",
                        GaTrongTuyen.class)
                .setParameter("maTuyen", maTuyen)
                .getResultList());
    }

    public Optional<GaTrongTuyen> findByMaTuyenVaMaGa(String maTuyen, String maGa) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT gtt FROM GaTrongTuyen gtt WHERE gtt.tuyen.maTuyen = :maTuyen AND gtt.ga.maGa = :maGa",
                        GaTrongTuyen.class)
                .setParameter("maTuyen", maTuyen)
                .setParameter("maGa", maGa)
                .getResultStream()
                .findFirst());
    }
}
