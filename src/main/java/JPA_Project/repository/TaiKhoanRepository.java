package JPA_Project.repository;

import JPA_Project.entity.TaiKhoan;
import JPA_Project.db.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.Optional;

/**
 * TaiKhoan Repository - Wrapper cho repository.TaiKhoanRepository
 */
public class TaiKhoanRepository extends BaseRepository<TaiKhoan, String> {

    public Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT tk FROM TaiKhoan tk WHERE tk.tenDangNhap = :tenDangNhap";
            var result = em.createQuery(jpql, TaiKhoan.class)
                    .setParameter("tenDangNhap", tenDangNhap)
                    .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }

    public Optional<TaiKhoan> findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT tk FROM TaiKhoan tk WHERE tk.email = :email";
            var result = em.createQuery(jpql, TaiKhoan.class)
                    .setParameter("email", email)
                    .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }

    public boolean existsByTenDangNhap(String tenDangNhap) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(tk) FROM TaiKhoan tk WHERE tk.tenDangNhap = :tenDangNhap";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("tenDangNhap", tenDangNhap)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        //test nhanh

    }
}
