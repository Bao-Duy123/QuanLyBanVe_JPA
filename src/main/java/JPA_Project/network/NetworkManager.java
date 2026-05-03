package JPA_Project.network;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * NetworkManager - Singleton quản lý kết nối đến TrainServer
 * Cung cấp API đồng bộ cho toàn bộ ứng dụng
 */
public class NetworkManager {
    
    private static NetworkManager instance;
    
    public static final int DEFAULT_PORT = 8888;
    public static final String DEFAULT_HOST = "localhost";
    
    private TrainClient trainClient;
    private String serverHost;
    private int serverPort;
    
    // Trạng thái kết nối
    private boolean isConnected = false;
    private boolean isConnecting = false;
    
    // Listeners cho events
    private final List<ConnectionListener> connectionListeners = new CopyOnWriteArrayList<>();
    private final List<SeatUpdateListener> seatUpdateListeners = new CopyOnWriteArrayList<>();
    
    // Cache ghế
    private final Map<String, String> seatCache = new HashMap<>();
    
    // Thread-safe
    private final Object lock = new Object();
    
    private NetworkManager() {
        this.serverHost = DEFAULT_HOST;
        this.serverPort = DEFAULT_PORT;
    }
    
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }
    
    // ========== CONNECTION ==========
    
    public boolean connect(String host, int port) {
        return connect(host, port, null);
    }
    
    public boolean connect(String host, int port, String clientName) {
        synchronized (lock) {
            if (isConnecting || isConnected) {
                if (isConnected) {
                    System.out.println("[NetworkManager] Da ket noi roi");
                    return true;
                }
                return false;
            }
            
            isConnecting = true;
            this.serverHost = host;
            this.serverPort = port;
            
            String name = clientName != null ? clientName : "AppClient";
            trainClient = new TrainClient(host, port, name);
            
            // Đăng ký listener cho seat updates
            trainClient.setOnSeatUpdate(this::onSeatUpdateReceived);
            
            boolean success = trainClient.connect();
            
            isConnecting = false;
            isConnected = success;
            
            if (success) {
                notifyConnectionListeners(true, "Ket noi thanh cong!");
            } else {
                notifyConnectionListeners(false, "Khong the ket noi den server");
            }
            
            return success;
        }
    }
    
    public void disconnect() {
        synchronized (lock) {
            if (trainClient != null) {
                trainClient.disconnect();
            }
            isConnected = false;
            seatCache.clear();
            notifyConnectionListeners(false, "Da ngat ket noi");
        }
    }
    
    public boolean isConnected() {
        return isConnected && trainClient != null && trainClient.isConnected();
    }
    
    public String getConnectionInfo() {
        if (trainClient != null) {
            return trainClient.getConnectionInfo();
        }
        return "Chua ket noi";
    }
    
    // ========== SEAT MANAGEMENT ==========
    
    /**
     * Subscribe theo dõi chuyến tàu
     */
    public void subscribeChuyenTau(String maChuyenTau) {
        if (!isConnected()) return;
        trainClient.subscribeChuyenTau(maChuyenTau);
    }
    
    /**
     * Hủy subscribe
     */
    public void unsubscribeChuyenTau(String maChuyenTau) {
        if (!isConnected()) return;
        trainClient.unsubscribe(maChuyenTau);
        // Xóa cache
        seatCache.entrySet().removeIf(e -> e.getKey().startsWith(maChuyenTau + "|"));
    }
    
    /**
     * Kiểm tra ghế có trống không
     */
    public boolean isGheTrong(String maChuyenTau, String maChoDat) {
        String key = taoKey(maChuyenTau, maChoDat);
        return !seatCache.containsKey(key) || !"DA_DAT".equals(seatCache.get(key));
    }
    
    /**
     * Đặt ghế
     */
    public boolean bookSeat(String maChuyenTau, String maChoDat) {
        if (!isConnected()) {
            System.err.println("[NetworkManager] Chua ket noi!");
            return false;
        }
        
        boolean success = trainClient.bookSeat(maChuyenTau, maChoDat);
        
        // Cập nhật cache local
        if (success) {
            String key = taoKey(maChuyenTau, maChoDat);
            seatCache.put(key, "DA_DAT");
        }
        
        return success;
    }
    
    /**
     * Hủy đặt ghế
     */
    public boolean cancelSeat(String maChuyenTau, String maChoDat) {
        if (!isConnected()) {
            System.err.println("[NetworkManager] Chua ket noi!");
            return false;
        }
        
        boolean success = trainClient.cancelSeat(maChuyenTau, maChoDat);
        
        // Cập nhật cache local
        if (success) {
            String key = taoKey(maChuyenTau, maChoDat);
            seatCache.remove(key);
        }
        
        return success;
    }
    
    /**
     * Đặt nhiều ghế cùng lúc
     */
    public boolean bookSeats(String maChuyenTau, List<String> dsMaCho) {
        if (!isConnected()) return false;
        
        boolean allSuccess = true;
        for (String maCho : dsMaCho) {
            if (!bookSeat(maChuyenTau, maCho)) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }
    
    /**
     * Hủy đặt nhiều ghế
     */
    public boolean cancelSeats(String maChuyenTau, List<String> dsMaCho) {
        if (!isConnected()) return false;
        
        boolean allSuccess = true;
        for (String maCho : dsMaCho) {
            if (!cancelSeat(maChuyenTau, maCho)) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }
    
    // ========== REPOSITORY CALLS ==========
    
    /**
     * Lấy danh sách Ga từ server
     */
    public List<String> getAllGa() {
        if (!isConnected()) return new ArrayList<>();
        return trainClient.getAllGa();
    }
    
    /**
     * Lấy danh sách Chuyến tàu từ server
     */
    public List<String> getAllChuyenTau() {
        if (!isConnected()) return new ArrayList<>();
        return trainClient.getAllChuyenTau();
    }
    
    /**
     * Lấy danh sách Toa theo chuyến tàu
     */
    public List<String> getToaByChuyenTau(String maChuyenTau) {
        if (!isConnected()) return new ArrayList<>();
        return trainClient.getToaByChuyenTau(maChuyenTau);
    }
    
    /**
     * Lấy danh sách Chỗ đặt theo toa
     */
    public List<String> getChoDatByToa(String maToa) {
        if (!isConnected()) return new ArrayList<>();
        return trainClient.getChoDatByToa(maToa);
    }
    
    // ========== LISTENERS ==========
    
    public interface ConnectionListener {
        void onConnectionChanged(boolean connected, String message);
    }
    
    public interface SeatUpdateListener {
        void onSeatUpdate(String maChuyenTau, String maChoDat, String status);
    }
    
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }
    
    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
    
    public void addSeatUpdateListener(SeatUpdateListener listener) {
        seatUpdateListeners.add(listener);
    }
    
    public void removeSeatUpdateListener(SeatUpdateListener listener) {
        seatUpdateListeners.remove(listener);
    }
    
    private void notifyConnectionListeners(boolean connected, String message) {
        for (ConnectionListener listener : connectionListeners) {
            listener.onConnectionChanged(connected, message);
        }
    }
    
    private void onSeatUpdateReceived(TrainClient.SeatUpdate update) {
        // Cập nhật cache
        String key = taoKey(update.maChuyenTau, update.maChoDat);
        if ("TRONG".equals(update.status)) {
            seatCache.remove(key);
        } else {
            seatCache.put(key, update.status);
        }
        
        // Thông báo cho listeners
        for (SeatUpdateListener listener : seatUpdateListeners) {
            listener.onSeatUpdate(update.maChuyenTau, update.maChoDat, update.status);
        }
    }
    
    // ========== HELPERS ==========
    
    private String taoKey(String maChuyenTau, String maChoDat) {
        return maChuyenTau + "|" + maChoDat;
    }
    
    public void clearSeatCache() {
        seatCache.clear();
    }
    
    public int getBookedSeatCount() {
        int count = 0;
        for (String status : seatCache.values()) {
            if ("DA_DAT".equals(status)) count++;
        }
        return count;
    }
    
    public String getServerHost() {
        return serverHost;
    }
    
    public int getServerPort() {
        return serverPort;
    }
}
