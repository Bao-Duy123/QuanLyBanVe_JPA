package JPA_Project.network;

import JPA_Project.db.JPAUtil;
import JPA_Project.entity.Ga;
import JPA_Project.entity.ChuyenTau;
import JPA_Project.entity.Toa;
import JPA_Project.entity.ChoDat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * RemoteRepository - Repository gọi qua network khi ở Server Mode
 * Nếu không kết nối Server, sẽ gọi trực tiếp JPA
 */
public class RemoteRepository {
    
    private static RemoteRepository instance;
    
    private RemoteRepository() {}
    
    public static synchronized RemoteRepository getInstance() {
        if (instance == null) {
            instance = new RemoteRepository();
        }
        return instance;
    }
    
    /**
     * Kiểm tra có đang ở Server Mode không
     */
    public boolean isServerMode() {
        return NetworkManager.getInstance().isConnected();
    }
    
    // =============================================
    // GA
    // =============================================
    
    public List<Ga> findAllGa() {
        if (isServerMode()) {
            // Gọi qua network
            return getGaFromServer();
        } else {
            // Gọi trực tiếp JPA
            return getGaFromDatabase();
        }
    }
    
    private List<Ga> getGaFromServer() {
        List<Ga> result = new ArrayList<>();
        try {
            List<String> gaData = NetworkManager.getInstance().getAllGa();
            for (String data : gaData) {
                String[] parts = data.split("\\|", 2);
                if (parts.length >= 2) {
                    Ga ga = new Ga();
                    ga.setMaGa(parts[0]);
                    ga.setTenGa(parts[1]);
                    result.add(ga);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy Ga từ server: " + e.getMessage());
        }
        return result;
    }
    
    private List<Ga> getGaFromDatabase() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Ga> query = em.createQuery("SELECT g FROM Ga g ORDER BY g.tenGa", Ga.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    // =============================================
    // CHUYEN TAU
    // =============================================
    
    public List<ChuyenTau> findAllChuyenTau() {
        if (isServerMode()) {
            return getChuyenTauFromServer();
        } else {
            return getChuyenTauFromDatabase();
        }
    }
    
    private List<ChuyenTau> getChuyenTauFromServer() {
        List<ChuyenTau> result = new ArrayList<>();
        try {
            List<String> dataList = NetworkManager.getInstance().getAllChuyenTau();
            for (String data : dataList) {
                String[] parts = data.split("\\|");
                if (parts.length >= 4) {
                    ChuyenTau ct = new ChuyenTau();
                    ct.setMaChuyenTau(parts[0]);
                    ct.setMaTau(parts[1]);
                    ct.setMaTuyen(parts[2]);
                    ct.setNgayKhoiHanh(java.time.LocalDate.parse(parts[3]));
                    result.add(ct);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy Chuyến tàu từ server: " + e.getMessage());
        }
        return result;
    }
    
    private List<ChuyenTau> getChuyenTauFromDatabase() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ChuyenTau> query = em.createQuery(
                "SELECT ct FROM ChuyenTau ct ORDER BY ct.ngayKhoiHanh DESC", 
                ChuyenTau.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    // =============================================
    // TOA
    // =============================================
    
    public List<Toa> findToaByMaChuyenTau(String maChuyenTau) {
        if (isServerMode()) {
            return getToaFromServer(maChuyenTau);
        } else {
            return getToaFromDatabase(maChuyenTau);
        }
    }
    
    private List<Toa> getToaFromServer(String maChuyenTau) {
        List<Toa> result = new ArrayList<>();
        try {
            List<String> dataList = NetworkManager.getInstance().getToaByChuyenTau(maChuyenTau);
            for (String data : dataList) {
                String[] parts = data.split("\\|");
                if (parts.length >= 3) {
                    Toa toa = new Toa();
                    toa.setMaToa(parts[0]);
                    toa.setLoaiToa(parts[1]);
                    result.add(toa);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy Toa từ server: " + e.getMessage());
        }
        return result;
    }
    
    private List<Toa> getToaFromDatabase(String maChuyenTau) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Tìm chuyến tàu trước
            ChuyenTau ct = em.find(ChuyenTau.class, maChuyenTau);
            if (ct == null) return new ArrayList<>();
            
            // Tìm toa theo mã tàu
            TypedQuery<Toa> query = em.createQuery(
                "SELECT t FROM Toa t WHERE t.tau.soHieu = :maTau OR t.maToa LIKE :pattern ORDER BY t.maToa",
                Toa.class
            );
            query.setParameter("maTau", ct.getMaTau());
            query.setParameter("pattern", ct.getMaTau() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    // =============================================
    // CHO DAT
    // =============================================
    
    public List<ChoDat> findChoDatByMaToa(String maToa) {
        if (isServerMode()) {
            return getChoDatFromServer(maToa);
        } else {
            return getChoDatFromDatabase(maToa);
        }
    }
    
    private List<ChoDat> getChoDatFromServer(String maToa) {
        List<ChoDat> result = new ArrayList<>();
        try {
            List<String> dataList = NetworkManager.getInstance().getChoDatByToa(maToa);
            for (String data : dataList) {
                String[] parts = data.split("\\|");
                if (parts.length >= 3) {
                    ChoDat cho = new ChoDat();
                    cho.setMaCho(parts[0]);
                    cho.setSoCho(parts[1]);
                    cho.setMaToa(maToa);
                    result.add(cho);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy Chỗ đặt từ server: " + e.getMessage());
        }
        return result;
    }
    
    private List<ChoDat> getChoDatFromDatabase(String maToa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ChoDat> query = em.createQuery(
                "SELECT c FROM ChoDat c WHERE c.maToa = :maToa ORDER BY c.soCho",
                ChoDat.class
            );
            query.setParameter("maToa", maToa);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
