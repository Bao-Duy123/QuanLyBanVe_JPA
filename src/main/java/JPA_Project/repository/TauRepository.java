package JPA_Project.repository;

import JPA_Project.entity.Tau;
import JPA_Project.db.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Tau Repository - Wrapper cho repository.TauRepository
 */
public class TauRepository extends BaseRepository<Tau, String> {

    public Optional<Tau> findBySoHieu(String soHieu) {
        return Optional.ofNullable(findById(soHieu));
    }

    public List<Tau> findByTrangThai(String trangThai) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT t FROM Tau t WHERE t.trangThai = :trangThai ORDER BY t.soHieu";
            return em.createQuery(jpql, Tau.class)
                    .setParameter("trangThai", trangThai)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Tau> findAllOrderBySoHieu() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT t FROM Tau t ORDER BY t.soHieu";
            return em.createQuery(jpql, Tau.class).getResultList();
        } finally {
            em.close();
        }
    }
}
