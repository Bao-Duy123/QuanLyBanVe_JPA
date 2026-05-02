package JPA_Project.repository;

import JPA_Project.entity.Tuyen;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

/**
 * Tuyen Repository - Wrapper cho repository.TuyenRepository
 */
public class TuyenRepository extends BaseRepository<Tuyen, String> {

    public Optional<Tuyen> findByMaTuyen(String maTuyen) {
        return Optional.ofNullable(findById(maTuyen));
    }

    public List<Tuyen> findAllOrderByTenTuyen() {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT t FROM Tuyen t ORDER BY t.tenTuyen",
                        Tuyen.class)
                .getResultList());
    }

    public List<Tuyen> findByGaDauOrGaCuoi(String maGa) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT t FROM Tuyen t WHERE t.gaDau = :maGa OR t.gaCuoi = :maGa",
                        Tuyen.class)
                .setParameter("maGa", maGa)
                .getResultList());
    }

    public static void main(String[] args) {
        //test nhanh
        //lấy toàn bộ tuyến + ga trong tuyến trong csdl

    }
}
