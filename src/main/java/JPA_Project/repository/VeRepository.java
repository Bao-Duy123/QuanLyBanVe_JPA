package JPA_Project.repository;

import JPA_Project.entity.Ve;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

public class VeRepository extends BaseRepository<Ve, String> {

    public Optional<Ve> findByMaVe(String maVe) {
        return Optional.ofNullable(findById(maVe));
    }

    public List<Ve> findByTrangThai(String trangThai) {
        return Tx.noTx(em -> em.createQuery(
                        "select v from Ve v where v.trangThai = :trangThai",
                        Ve.class)
                .setParameter("trangThai", trangThai)
                .getResultList());
    }

    public List<Ve> findByMaChuyenTauVaMaCho(String maChuyenTau, String maChoDat) {
        return Tx.noTx(em -> em.createQuery(
                        "select v from Ve v where v.maChuyenTau = :maChuyenTau and v.maChoDat = :maChoDat",
                        Ve.class)
                .setParameter("maChuyenTau", maChuyenTau)
                .setParameter("maChoDat", maChoDat)
                .getResultList());
    }

    public List<Ve> findTop5DaBanMoiNhat() {
        return Tx.noTx(em -> em.createQuery(
                        "select v from Ve v where v.trangThai = :trangThai order by v.maVe desc",
                        Ve.class)
                .setParameter("trangThai", "DA_BAN")
                .setMaxResults(5)
                .getResultList());
    }

    public List<Ve> findTop5DaBanMoiNhatDetailed() {
        return Tx.noTx(em -> em.createQuery(
                        "select v from Ve v " +
                                "left join fetch v.khachHang " +
                                "left join fetch v.chuyenTau ct " +
                                "left join fetch ct.gaDi " +
                                "left join fetch ct.gaDen " +
                                "left join fetch v.choDat " +
                                "where v.trangThai = :trangThai order by v.maVe desc",
                        Ve.class)
                .setParameter("trangThai", "DA_BAN")
                .setMaxResults(5)
                .getResultList());
    }

    public List<Ve> timVeTheoKhachHang(String hoTen, String sdt, String cccd, String maVe) {
        return Tx.noTx(em -> {
            StringBuilder jpql = new StringBuilder("select v from Ve v left join v.khachHang kh where 1=1");
            if (maVe != null && !maVe.isBlank()) jpql.append(" and v.maVe = :maVe");
            if (hoTen != null && !hoTen.isBlank()) jpql.append(" and lower(kh.hoTen) like :hoTen");
            if (sdt != null && !sdt.isBlank()) jpql.append(" and kh.sdt like :sdt");
            if (cccd != null && !cccd.isBlank()) jpql.append(" and kh.soCCCD like :cccd");

            var query = em.createQuery(jpql.toString(), Ve.class);
            if (maVe != null && !maVe.isBlank()) query.setParameter("maVe", maVe);
            if (hoTen != null && !hoTen.isBlank()) query.setParameter("hoTen", "%" + hoTen.toLowerCase() + "%");
            if (sdt != null && !sdt.isBlank()) query.setParameter("sdt", "%" + sdt + "%");
            if (cccd != null && !cccd.isBlank()) query.setParameter("cccd", "%" + cccd + "%");
            return query.getResultList();
        });
    }

    public List<Ve> timVeTheoKhachHangDetailed(String hoTen, String sdt, String cccd, String maVe) {
        return Tx.noTx(em -> {
            StringBuilder jpql = new StringBuilder(
                    "select v from Ve v " +
                            "left join fetch v.khachHang kh " +
                            "left join fetch v.chuyenTau ct " +
                            "left join fetch ct.gaDi " +
                            "left join fetch ct.gaDen " +
                            "left join fetch v.choDat " +
                            "where 1=1");
            if (maVe != null && !maVe.isBlank()) jpql.append(" and v.maVe = :maVe");
            if (hoTen != null && !hoTen.isBlank()) jpql.append(" and lower(kh.hoTen) like :hoTen");
            if (sdt != null && !sdt.isBlank()) jpql.append(" and kh.sdt like :sdt");
            if (cccd != null && !cccd.isBlank()) jpql.append(" and kh.soCCCD like :cccd");

            var query = em.createQuery(jpql.toString(), Ve.class);
            if (maVe != null && !maVe.isBlank()) query.setParameter("maVe", maVe);
            if (hoTen != null && !hoTen.isBlank()) query.setParameter("hoTen", "%" + hoTen.toLowerCase() + "%");
            if (sdt != null && !sdt.isBlank()) query.setParameter("sdt", "%" + sdt + "%");
            if (cccd != null && !cccd.isBlank()) query.setParameter("cccd", "%" + cccd + "%");
            return query.getResultList();
        });
    }

    public Optional<Ve> findDetailedByMaVe(String maVe) {
        return Tx.noTx(em -> em.createQuery(
                        "select v from Ve v " +
                                "left join fetch v.khachHang " +
                                "left join fetch v.chuyenTau ct " +
                                "left join fetch ct.gaDi " +
                                "left join fetch ct.gaDen " +
                                "left join fetch v.choDat " +
                                "where v.maVe = :maVe",
                        Ve.class)
                .setParameter("maVe", maVe)
                .getResultStream()
                .findFirst());
    }

    public Optional<Ve> findChiTietVeChoTraVe(String maVe, String sdt) {
        return Tx.noTx(em -> {
            String jpql = "select v from Ve v " +
                    "left join fetch v.khachHang kh " +
                    "left join fetch v.chuyenTau ct " +
                    "left join fetch ct.gaDi " +
                    "left join fetch ct.gaDen " +
                    "left join fetch v.choDat " +
                    "where v.trangThai <> :trangThaiHuy and ((:maVe is not null and v.maVe = :maVe) or (:sdt is not null and kh.sdt = :sdt))";
            return em.createQuery(jpql, Ve.class)
                    .setParameter("trangThaiHuy", "DA-HUY")
                    .setParameter("maVe", (maVe == null || maVe.isBlank()) ? null : maVe)
                    .setParameter("sdt", (sdt == null || sdt.isBlank()) ? null : sdt)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst();
        });
    }

    public Optional<String> findLastMaVeByPrefix(String prefix) {
        return Tx.noTx(em -> em.createQuery(
                        "select v.maVe from Ve v where v.maVe like :prefix order by v.maVe desc",
                        String.class)
                .setParameter("prefix", prefix + "%")
                .setMaxResults(1)
                .getResultStream()
                .findFirst());
    }

    public boolean capNhatTrangThai(String maVe, String trangThai) {
        return Tx.inTx(em -> {
            Ve ve = em.find(Ve.class, maVe);
            if (ve == null) {
                return false;
            }
            ve.setTrangThai(trangThai);
            return true;
        });
    }
}
