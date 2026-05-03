package JPA_Project.repository;

import JPA_Project.entity.Ga;
import JPA_Project.entity.GaTrongTuyen;
import JPA_Project.entity.GaTrongTuyenId;
import JPA_Project.entity.Tuyen;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

/**
 * GaTrongTuyen Repository - Quản lý Ga trong Tuyến
 */
public class GaTrongTuyenRepository extends BaseRepository<GaTrongTuyen, GaTrongTuyenId> {

    public List<GaTrongTuyen> findByMaTuyen(String maTuyen) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT gtt FROM GaTrongTuyen gtt " +
                        "LEFT JOIN FETCH gtt.ga " +
                        "WHERE gtt.maTuyen = :maTuyen ORDER BY gtt.thuTuGa",
                        GaTrongTuyen.class)
                .setParameter("maTuyen", maTuyen)
                .getResultList());
    }

    public Optional<GaTrongTuyen> findByMaTuyenVaMaGa(String maTuyen, String maGa) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT gtt FROM GaTrongTuyen gtt WHERE gtt.maTuyen = :maTuyen AND gtt.maGa = :maGa",
                        GaTrongTuyen.class)
                .setParameter("maTuyen", maTuyen)
                .setParameter("maGa", maGa)
                .getResultStream()
                .findFirst());
    }

    public GaTrongTuyen saveWithReturn(GaTrongTuyen entity) {
        return Tx.inTx(em -> {
            GaTrongTuyen existing = em.find(GaTrongTuyen.class,
                    new GaTrongTuyenId(entity.getMaTuyen(), entity.getMaGa()));
            if (existing != null) {
                existing.setThuTuGa(entity.getThuTuGa());
                existing.setThoiGianDung(entity.getThoiGianDung());
                return em.merge(existing);
            } else {
                Tuyen tuyen = em.find(Tuyen.class, entity.getMaTuyen());
                Ga ga = em.find(Ga.class, entity.getMaGa());
                entity.setTuyen(tuyen);
                entity.setGa(ga);
                em.persist(entity);
                return entity;
            }
        });
    }

    public void save(GaTrongTuyen entity) {
        Tx.inTxVoid(em -> {
            GaTrongTuyen existing = em.find(GaTrongTuyen.class,
                    new GaTrongTuyenId(entity.getMaTuyen(), entity.getMaGa()));
            if (existing != null) {
                existing.setThuTuGa(entity.getThuTuGa());
                existing.setThoiGianDung(entity.getThoiGianDung());
                em.merge(existing);
            } else {
                Tuyen tuyen = em.find(Tuyen.class, entity.getMaTuyen());
                Ga ga = em.find(Ga.class, entity.getMaGa());
                entity.setTuyen(tuyen);
                entity.setGa(ga);
                em.persist(entity);
            }
        });
    }

    public GaTrongTuyen update(GaTrongTuyen entity) {
        return Tx.inTx(em -> {
            GaTrongTuyen existing = em.find(GaTrongTuyen.class,
                    new GaTrongTuyenId(entity.getMaTuyen(), entity.getMaGa()));
            if (existing != null) {
                existing.setThuTuGa(entity.getThuTuGa());
                existing.setThoiGianDung(entity.getThoiGianDung());
                return em.merge(existing);
            }
            return null;
        });
    }

    public void delete(String maTuyen, String maGa) {
        Tx.inTxVoid(em -> {
            GaTrongTuyenId id = new GaTrongTuyenId(maTuyen, maGa);
            GaTrongTuyen entity = em.find(GaTrongTuyen.class, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }
}
