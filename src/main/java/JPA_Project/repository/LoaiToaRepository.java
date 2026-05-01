package JPA_Project.repository;

import JPA_Project.entity.LoaiToa;
import JPA_Project.db.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * LoaiToa Repository - Wrapper cho repository.LoaiToaRepository
 */
public class LoaiToaRepository extends BaseRepository<LoaiToa, String> {

    public Optional<LoaiToa> findByMaLoaiToa(String maLoaiToa) {
        return Optional.ofNullable(findById(maLoaiToa));
    }

    public List<LoaiToa> findAllOrderByTenLoaiToa() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT lt FROM LoaiToa lt ORDER BY lt.tenLoaiToa";
            return em.createQuery(jpql, LoaiToa.class).getResultList();
        } finally {
            em.close();
        }
    }
}
