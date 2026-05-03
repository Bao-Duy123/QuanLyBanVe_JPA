package JPA_Project.repository;

import JPA_Project.entity.ChiTietHoaDon;
import JPA_Project.db.Tx;

import java.util.List;

public class ChiTietHoaDonRepository extends BaseRepository<ChiTietHoaDon, String> {

    public List<ChiTietHoaDon> findByMaHD(String maHD) {
        return Tx.noTx(em -> em.createQuery(
                        "select c from ChiTietHoaDon c " +
                                "left join fetch c.ve " +
                                "where c.maHD = :maHD",
                        ChiTietHoaDon.class)
                .setParameter("maHD", maHD)
                .getResultList());
    }

    public List<ChiTietHoaDon> findByMaVe(String maVe) {
        return Tx.noTx(em -> em.createQuery(
                        "select c from ChiTietHoaDon c where c.maVe = :maVe",
                        ChiTietHoaDon.class)
                .setParameter("maVe", maVe)
                .getResultList());
    }

    public void save(ChiTietHoaDon entity) {
        Tx.inTxVoid(em -> em.persist(entity));
    }
}
