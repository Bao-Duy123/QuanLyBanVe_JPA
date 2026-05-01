package JPA_Project.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import JPA_Project.db.JPAUtil;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Base Repository - Wrapper cho repository.BaseRepository
 */
public abstract class BaseRepository<T, ID> {
    protected Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseRepository() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void save(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public T findById(ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    protected List<T> executeQuery(String jpql, Object... params) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Query query = em.createQuery(jpql, entityClass);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
