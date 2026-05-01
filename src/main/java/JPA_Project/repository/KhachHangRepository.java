package JPA_Project.repository;

import JPA_Project.entity.KhachHang;
import JPA_Project.db.Tx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class KhachHangRepository extends BaseRepository<KhachHang, String> {

    public KhachHang findByCccd(String cccd) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT k FROM KhachHang k WHERE k.soCCCD = :cccd",
                        KhachHang.class)
                .setParameter("cccd", cccd)
                .getResultStream()
                .findFirst()
                .orElse(null));
    }

    public List<KhachHang> findBySdt(String sdt) {
        return Tx.noTx(em -> em.createQuery(
                        "SELECT k FROM KhachHang k WHERE k.sdt = :sdt",
                        KhachHang.class)
                .setParameter("sdt", sdt)
                .getResultList());
    }

    public Optional<KhachHang> findByMaKH(String maKH) {
        return Optional.ofNullable(findById(maKH));
    }

    public String taoMaKhachHangMoi() {
        LocalDate homNay = LocalDate.now();
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));

        return Tx.noTx(em -> {
            String jpql = "SELECT k.maKH FROM KhachHang k WHERE k.maKH LIKE :pattern ORDER BY k.maKH DESC";
            List<String> results = em.createQuery(jpql, String.class)
                    .setParameter("pattern", "KH" + ngayStr + "%")
                    .setMaxResults(1)
                    .getResultList();

            int nextNumber = 1;
            if (!results.isEmpty()) {
                String lastMaKH = results.get(0);
                if (lastMaKH.length() >= 4) {
                    try {
                        String lastSTT = lastMaKH.substring(lastMaKH.length() - 4);
                        nextNumber = Integer.parseInt(lastSTT) + 1;
                    } catch (NumberFormatException e) {
                        nextNumber = 1;
                    }
                }
            }

            String soThuTuStr = String.format("%04d", nextNumber);
            return "KH" + ngayStr + soThuTuStr;
        });
    }
}
