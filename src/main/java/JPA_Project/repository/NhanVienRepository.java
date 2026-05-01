package JPA_Project.repository;

import JPA_Project.entity.NhanVien;
import JPA_Project.db.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;
import java.util.Optional;

/**
 * NhanVien Repository - Wrapper cho repository.NhanVienRepository
 */
public class NhanVienRepository extends BaseRepository<NhanVien, String> {

    public Optional<NhanVien> findByMaNV(String maNV) {
        System.out.println("[DEBUG-NhanVienRepo] findByMaNV called with: " + maNV);
        NhanVien nv = findById(maNV);
        if (nv != null) {
            System.out.println("[DEBUG-NhanVienRepo] Found NhanVien - MaNV: " + nv.getMaNV() 
                + ", HoTen: " + nv.getHoTen() 
                + ", ChucVu: " + nv.getChucVu());
        } else {
            System.out.println("[DEBUG-NhanVienRepo] NhanVien not found for MaNV: " + maNV);
        }
        return Optional.ofNullable(nv);
    }

    public Optional<NhanVien> findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT nv FROM NhanVien nv JOIN nv.taiKhoan tk WHERE tk.email = :email";
            Query query = em.createQuery(jpql, NhanVien.class);
            query.setParameter("email", email);
            List<NhanVien> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public List<NhanVien> findByChucVu(String chucVu) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT nv FROM NhanVien nv WHERE nv.chucVu = :chucVu";
            return em.createQuery(jpql, NhanVien.class)
                    .setParameter("chucVu", chucVu)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<NhanVien> findAllWithTaiKhoan() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT nv FROM NhanVien nv LEFT JOIN FETCH nv.taiKhoan";
            return em.createQuery(jpql, NhanVien.class).getResultList();
        } finally {
            em.close();
        }
    }
}
