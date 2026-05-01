package JPA_Project.repository;

import JPA_Project.entity.Tuyen;
import JPA_Project.db.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Tuyen Repository - Wrapper cho repository.TuyenRepository
 */
public class TuyenRepository extends BaseRepository<Tuyen, String> {

    public Optional<Tuyen> findByMaTuyen(String maTuyen) {
        return Optional.ofNullable(findById(maTuyen));
    }

    public List<Tuyen> findAllOrderByTenTuyen() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT t FROM Tuyen t ORDER BY t.tenTuyen";
            return em.createQuery(jpql, Tuyen.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Tuyen> findByGaDauOrGaCuoi(String maGa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT t FROM Tuyen t WHERE t.gaDau = :maGa OR t.gaCuoi = :maGa";
            return em.createQuery(jpql, Tuyen.class)
                    .setParameter("maGa", maGa)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
