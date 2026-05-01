package JPA_Project.repository;

import JPA_Project.entity.ChoDat;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

public class ChoDatRepository extends BaseRepository<ChoDat, String> {

    public Optional<ChoDat> findByMaCho(String maCho) {
        return Optional.ofNullable(findById(maCho));
    }

    public List<ChoDat> findByMaToa(String maToa) {
        return Tx.noTx(em -> em.createQuery(
                        "select c from ChoDat c where c.maToa = :maToa",
                        ChoDat.class)
                .setParameter("maToa", maToa)
                .getResultList());
    }

    public List<ChoDat> findByMaChuyenTau(String maChuyenTau) {
        return Tx.noTx(em -> em.createQuery(
                        "select c from ChoDat c where c.maChuyenTau = :maChuyenTau",
                        ChoDat.class)
                .setParameter("maChuyenTau", maChuyenTau)
                .getResultList());
    }
}
