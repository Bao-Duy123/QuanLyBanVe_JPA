package JPA_Project.repository;

import JPA_Project.entity.Toa;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

/**
 * Toa Repository - Wrapper cho repository.ToaRepository
 */
public class ToaRepository extends BaseRepository<Toa, String> {

    public Optional<Toa> findByMaToa(String maToa) {
        return Optional.ofNullable(findById(maToa));
    }

    public List<Toa> findByMaTau(String maTau) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT t FROM Toa t WHERE t.tau.soHieu = :maTau ORDER BY t.maToa",
                        Toa.class)
                .setParameter("maTau", maTau)
                .getResultList());
    }

    public List<Toa> findByMaTauWithLoaiToa(String maTau) {
        return Tx.noTx(em -> {
            // Query 1: Tìm theo tau.soHieu
            List<Toa> result = em.createQuery(
                            "SELECT t FROM Toa t LEFT JOIN FETCH t.loaiToaRef WHERE t.tau.soHieu = :maTau ORDER BY t.maToa",
                            Toa.class)
                    .setParameter("maTau", maTau)
                    .getResultList();
            
            // Nếu không có, thử tìm theo pattern maToa bắt đầu với mã tàu
            if (result.isEmpty()) {
                result = em.createQuery(
                            "SELECT t FROM Toa t LEFT JOIN FETCH t.loaiToaRef WHERE t.maToa LIKE :pattern ORDER BY t.maToa",
                            Toa.class)
                        .setParameter("pattern", maTau + "%")
                        .getResultList();
            }
            
            System.out.println("[ToaRepository] Tìm thấy " + result.size() + " toa cho tàu: " + maTau);
            return result;
        });
    }
}
