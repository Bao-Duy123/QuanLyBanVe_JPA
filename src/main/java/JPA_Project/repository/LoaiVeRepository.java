package JPA_Project.repository;

import JPA_Project.entity.LoaiVe;
import JPA_Project.db.Tx;

import java.util.List;
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

    public List<LoaiVe> findAllOrderByTenLoai() {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT lv FROM LoaiVe lv ORDER BY lv.tenLoai",
                        LoaiVe.class)
                .getResultList());
    }

    public void save(LoaiVe entity) {
        Tx.inTxVoid(em -> {
            LoaiVe existing = em.find(LoaiVe.class, entity.getMaLoaiVe());
            if (existing != null) {
                existing.setTenLoai(entity.getTenLoai());
                existing.setMucGiamGia(entity.getMucGiamGia());
                existing.setTuoiMin(entity.getTuoiMin());
                existing.setTuoiMax(entity.getTuoiMax());
                em.merge(existing);
            } else {
                em.persist(entity);
            }
        });
    }
}
