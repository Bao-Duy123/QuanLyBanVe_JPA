package JPA_Project.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * JPA Utility class - Sử dụng persistence-unit "mssql-pu" cho database QuanLyVeTau_JPA
 */
public class JPAUtil {
    private static EntityManagerFactory emf;
    private static final String PERSISTENCE_UNIT_NAME = "mssql-pu";
    private static boolean jpaAvailable = false;

    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            jpaAvailable = true;
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
            jpaAvailable = false;
        }
    }
    
    /**
     * Kiểm tra JPA có khả dụng không (có kết nối CSDL)
     */
    public static boolean isAvailable() {
        return jpaAvailable && emf != null && emf.isOpen();
    }
    
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
    
    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            throw new IllegalStateException("EntityManagerFactory is not available");
        }
        return emf.createEntityManager();
    }
    
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    
    public static EntityManager createEntityManager() {
        return getEntityManager();
    }
}
