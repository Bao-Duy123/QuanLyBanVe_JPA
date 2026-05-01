package JPA_Project.repository;

import JPA_Project.entity.ChiTietHoaDon;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

/**
 * ChiTietHoaDon Repository - Wrapper cho repository.ChiTietHoaDonRepository
 */
public class ChiTietHoaDonRepository extends BaseRepository<ChiTietHoaDon, String> {

    public List<ChiTietHoaDon> findByMaHD(String maHD) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT cthd FROM ChiTietHoaDon cthd WHERE cthd.hoaDon.maHD = :maHD",
                        ChiTietHoaDon.class)
                .setParameter("maHD", maHD)
                .getResultList());
    }

    public Optional<ChiTietHoaDon> findByMaHDAndMaVe(String maHD, String maVe) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT cthd FROM ChiTietHoaDon cthd WHERE cthd.hoaDon.maHD = :maHD AND cthd.ve.maVe = :maVe",
                        ChiTietHoaDon.class)
                .setParameter("maHD", maHD)
                .setParameter("maVe", maVe)
                .getResultStream()
                .findFirst());
    }

    public double tinhTongTienByMaHD(String maHD) {
        Double sum = Tx.noTx(em -> em.createQuery(
                        "SELECT SUM(cthd.giaTien) FROM ChiTietHoaDon cthd WHERE cthd.hoaDon.maHD = :maHD",
                        Double.class)
                .setParameter("maHD", maHD)
                .getSingleResult());
        return sum != null ? sum : 0.0;
    }
}
