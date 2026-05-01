package JPA_Project.db;

import jakarta.persistence.EntityManager;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Transaction helper - Wrapper cho db.Tx
 * Tạo EntityManager mới cho mỗi call (thread-safe)
 */
public final class Tx {
    private Tx() {
    }

    public static <T> T inTx(Function<EntityManager, T> work) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            T result = work.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public static void inTxVoid(Consumer<EntityManager> work) {
        inTx(em -> {
            work.accept(em);
            return null;
        });
    }

    public static <T> T noTx(Function<EntityManager, T> work) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return work.apply(em);
        } finally {
            em.close();
        }
    }
}
