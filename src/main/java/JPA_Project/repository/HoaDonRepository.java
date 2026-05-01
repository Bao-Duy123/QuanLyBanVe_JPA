package JPA_Project.repository;

import JPA_Project.entity.HoaDon;
import JPA_Project.db.Tx;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class HoaDonRepository extends BaseRepository<HoaDon, String> {

    public Optional<HoaDon> findByMaHD(String maHD) {
        return Optional.ofNullable(findById(maHD));
    }

    public List<HoaDon> findByMaKhachHang(String maKhachHang) {
        return Tx.noTx(em -> em.createQuery(
                        "select h from HoaDon h where h.maKhachHang = :maKhachHang order by h.ngayLap desc",
                        HoaDon.class)
                .setParameter("maKhachHang", maKhachHang)
                .getResultList());
    }

    public List<HoaDon> findBySoDienThoai(String soDienThoai) {
        return Tx.noTx(em -> em.createQuery(
                        "select h from HoaDon h join h.khachHang kh where kh.sdt = :sdt order by h.ngayLap desc",
                        HoaDon.class)
                .setParameter("sdt", soDienThoai)
                .getResultList());
    }

    public List<HoaDon> findByCccd(String cccd) {
        return Tx.noTx(em -> em.createQuery(
                        "select h from HoaDon h join h.khachHang kh where kh.soCCCD = :cccd order by h.ngayLap desc",
                        HoaDon.class)
                .setParameter("cccd", cccd)
                .getResultList());
    }

    public List<HoaDon> findBySoDienThoaiVaThangNam(String soDienThoai, int thang, int nam) {
        LocalDateTime tuNgay = LocalDateTime.of(nam, thang, 1, 0, 0);
        LocalDateTime denNgay = tuNgay.plusMonths(1);
        return Tx.noTx(em -> em.createQuery(
                        "select h from HoaDon h join h.khachHang kh where kh.sdt = :sdt and h.ngayLap >= :tuNgay and h.ngayLap < :denNgay order by h.ngayLap desc",
                        HoaDon.class)
                .setParameter("sdt", soDienThoai)
                .setParameter("tuNgay", tuNgay)
                .setParameter("denNgay", denNgay)
                .getResultList());
    }

    public List<HoaDon> findByCccdVaThangNam(String cccd, int thang, int nam) {
        LocalDateTime tuNgay = LocalDateTime.of(nam, thang, 1, 0, 0);
        LocalDateTime denNgay = tuNgay.plusMonths(1);
        return Tx.noTx(em -> em.createQuery(
                        "select h from HoaDon h join h.khachHang kh where kh.soCCCD = :cccd and h.ngayLap >= :tuNgay and h.ngayLap < :denNgay order by h.ngayLap desc",
                        HoaDon.class)
                .setParameter("cccd", cccd)
                .setParameter("tuNgay", tuNgay)
                .setParameter("denNgay", denNgay)
                .getResultList());
    }
}
