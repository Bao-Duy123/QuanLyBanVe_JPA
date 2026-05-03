package JPA_Project.service;

import JPA_Project.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

/**
 * DataAccessService - Service trung gian để truy cập dữ liệu
 * Tự động quyết định gọi qua network (Server Mode) hay trực tiếp (Local Mode)
 */
public class DataAccessService {
    
    private static DataAccessService instance;
    
    private DataAccessService() {}
    
    public static synchronized DataAccessService getInstance() {
        if (instance == null) {
            instance = new DataAccessService();
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
    // GA - Lấy danh sách Ga đi và Ga đến
    // =============================================
    
    /**
     * Lấy danh sách Ga từ server hoặc database
     * @return List<String> với format "maGa|tenGa"
     */
    public List<String> getAllGa() {
        if (isServerMode()) {
            // Gọi qua network
            return NetworkManager.getInstance().getAllGa();
        } else {
            // Gọi trực tiếp từ database
            return getGaFromDatabase();
        }
    }
    
    private List<String> getGaFromDatabase() {
        // TODO: Implement khi cần gọi trực tiếp
        return new ArrayList<>();
    }
    
    // =============================================
    // CHUYEN TAU
    // =============================================
    
    public List<String> getAllChuyenTau() {
        if (isServerMode()) {
            return NetworkManager.getInstance().getAllChuyenTau();
        } else {
            return getChuyenTauFromDatabase();
        }
    }
    
    private List<String> getChuyenTauFromDatabase() {
        // TODO: Implement khi cần gọi trực tiếp
        return new ArrayList<>();
    }
    
    // =============================================
    // TOA
    // =============================================
    
    public List<String> getToaByChuyenTau(String maChuyenTau) {
        if (isServerMode()) {
            return NetworkManager.getInstance().getToaByChuyenTau(maChuyenTau);
        } else {
            return getToaFromDatabase(maChuyenTau);
        }
    }
    
    private List<String> getToaFromDatabase(String maChuyenTau) {
        // TODO: Implement khi cần gọi trực tiếp
        return new ArrayList<>();
    }
    
    // =============================================
    // CHO DAT (Ghế)
    // =============================================
    
    public List<String> getChoDatByToa(String maToa) {
        if (isServerMode()) {
            return NetworkManager.getInstance().getChoDatByToa(maToa);
        } else {
            return getChoDatFromDatabase(maToa);
        }
    }
    
    private List<String> getChoDatFromDatabase(String maToa) {
        // TODO: Implement khi cần gọi trực tiếp
        return new ArrayList<>();
    }
    
    // =============================================
    // SEAT OPERATIONS
    // =============================================
    
    public boolean isGheTrong(String maChuyenTau, String maChoDat) {
        if (isServerMode()) {
            return NetworkManager.getInstance().isGheTrong(maChuyenTau, maChoDat);
        } else {
            // Local mode - cần implement
            return true;
        }
    }
    
    public boolean bookSeat(String maChuyenTau, String maChoDat) {
        if (isServerMode()) {
            return NetworkManager.getInstance().bookSeat(maChuyenTau, maChoDat);
        } else {
            // Local mode - cần implement
            return true;
        }
    }
    
    public boolean cancelSeat(String maChuyenTau, String maChoDat) {
        if (isServerMode()) {
            return NetworkManager.getInstance().cancelSeat(maChuyenTau, maChoDat);
        } else {
            // Local mode - cần implement
            return true;
        }
    }
}
