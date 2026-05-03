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

    public List<HoaDon> findByCccdDetailed(String cccd) {
        return Tx.noTx(em -> em.createQuery(
                        "select distinct h from HoaDon h left join fetch h.khachHang where h.maKhachHang in (select kh.maKhachHang from KhachHang kh where kh.soCCCD = :cccd) order by h.ngayLap desc",
                        HoaDon.class)
                .setParameter("cccd", cccd)
                .getResultList());
    }

    public List<HoaDon> findBySoDienThoaiDetailed(String sdt) {
        return Tx.noTx(em -> em.createQuery(
                        "select distinct h from HoaDon h left join fetch h.khachHang where h.maKhachHang in (select kh.maKhachHang from KhachHang kh where kh.sdt = :sdt) order by h.ngayLap desc",
                        HoaDon.class)
                .setParameter("sdt", sdt)
                .getResultList());
    }

    public List<HoaDon> findByMaHDDetailed(String maHD) {
        return Tx.noTx(em -> em.createQuery(
                        "select distinct h from HoaDon h " +
                                "left join fetch h.khachHang " +
                                "left join fetch h.khuyenMai " +
                                "where h.maHD = :maHD",
                        HoaDon.class)
                .setParameter("maHD", maHD)
                .getResultList());
    }

    public List<HoaDon> findAllWithDetails() {
        return Tx.noTx(em -> em.createQuery(
                        "select distinct h from HoaDon h " +
                                "left join fetch h.khachHang " +
                                "left join fetch h.khuyenMai " +
                                "order by h.ngayLap desc",
                        HoaDon.class)
                .getResultList());
    }

    public Optional<HoaDon> findByMaHDWithDetails(String maHD) {
        return Tx.noTx(em -> em.createQuery(
                        "select distinct h from HoaDon h " +
                                "left join fetch h.khachHang " +
                                "left join fetch h.khuyenMai " +
                                "left join fetch h.chiTietHoaDons cthd " +
                                "left join fetch cthd.ve " +
                                "where h.maHD = :maHD",
                        HoaDon.class)
                .setParameter("maHD", maHD)
                .getResultStream()
                .findFirst());
    }
}
