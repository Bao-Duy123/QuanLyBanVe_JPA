package JPA_Project.repository;

import JPA_Project.entity.KhachHang;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

public class KhachHangRepository extends BaseRepository<KhachHang, String> {

    public Optional<KhachHang> findByMaKH(String maKH) {
        return Optional.ofNullable(findById(maKH));
    }

    public KhachHang findByCccd(String cccd) {
        return Tx.noTx(em -> {
            List<KhachHang> results = em.createQuery(
                            "select k from KhachHang k where k.soCCCD = :cccd",
                            KhachHang.class)
                    .setParameter("cccd", cccd)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        });
    }

    public Optional<KhachHang> findBySoDienThoai(String sdt) {
        return Tx.noTx(em -> em.createQuery(
                        "select k from KhachHang k where k.sdt = :sdt",
                        KhachHang.class)
                .setParameter("sdt", sdt)
                .getResultStream()
                .findFirst());
    }

    public List<KhachHang> searchByHoTen(String hoTen) {
        return Tx.noTx(em -> em.createQuery(
                        "select k from KhachHang k where lower(k.hoTen) like :hoTen",
                        KhachHang.class)
                .setParameter("hoTen", "%" + hoTen.toLowerCase() + "%")
                .getResultList());
    }

    public List<KhachHang> findAllOrderByHoTen() {
        return Tx.noTx(em -> em.createQuery(
                        "select k from KhachHang k order by k.hoTen",
                        KhachHang.class)
                .getResultList());
    }

    public void save(KhachHang entity) {
        Tx.inTxVoid(em -> {
            KhachHang existing = em.find(KhachHang.class, entity.getMaKH());
            if (existing != null) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }
        });
    }
}
