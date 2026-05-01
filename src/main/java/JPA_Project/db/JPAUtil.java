package JPA_Project.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * JPA Utility class - Sử dụng persistence-unit "sql_rut_gon" cho database QuanLyVeTau_JPA
 */
public class JPAUtil {
    private static EntityManagerFactory emf;
    private static final String PERSISTENCE_UNIT_NAME = "mssql-pu";
//    private static final String PERSISTENCE_UNIT_NAME = "sql_rut_gon";

    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new RuntimeException("EntityManagerFactory chưa được khởi tạo");
        }
        return emf.createEntityManager();
    }

    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (JPAUtil.class) {
                if (emf == null) {
                    emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                }
            }
        }
        return emf;
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void shutdown() {
        EntityManagerFactory factory = emf;
        emf = null;
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
