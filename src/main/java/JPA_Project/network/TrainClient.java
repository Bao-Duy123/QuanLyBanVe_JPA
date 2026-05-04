package JPA_Project.network;

import JPA_Project.entity.Ve;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;


public class TrainClient {
    public static final int DEFAULT_PORT = 8888;
    public static final int DEFAULT_TIMEOUT = 30000; // 30 giây
    
    private String serverHost;
    private int serverPort;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    
    private boolean isConnected = false;
    private String clientName;
    
    // Callback listeners cho realtime updates
    private Consumer<SeatUpdate> seatUpdateListener;
    private Thread listenerThread;
    private volatile boolean listening = false;
    
    // Cache ghế trên client
    private final Map<String, String> localSeatCache = new ConcurrentHashMap<>();
    
    public TrainClient() {
        this("localhost", DEFAULT_PORT);
    }
    
    public TrainClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.clientName = "Client-" + System.currentTimeMillis();
    }
    
    public TrainClient(String serverHost, int serverPort, String clientName) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.clientName = clientName;
    }
    
    /**
     * Kết nối đến server
     */
    public boolean connect() {
        try {
            System.out.println("[" + clientName + "] Dang ket noi den " + serverHost + ":" + serverPort + "...");
            
            socket = new Socket(serverHost, serverPort);
            socket.setSoTimeout(DEFAULT_TIMEOUT);
            
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            
            isConnected = true;
            System.out.println("[" + clientName + "] Da ket noi thanh cong!");
            
            // Bắt đầu lắng nghe messages từ server
            startListening();
            
            return true;
        } catch (UnknownHostException e) {
            System.err.println("[" + clientName + "] Khong tim thay server: " + serverHost);
            return false;
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Khong the ket noi: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ngắt kết nối
     */
    public void disconnect() {
        listening = false;
        
        try {
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
        } catch (Exception e) {
            // Ignore
        }
        
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();
            
            isConnected = false;
            localSeatCache.clear();
            System.out.println("[" + clientName + "] Da ngat ket noi.");
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi khi ngat ket noi: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }
    
    /**
     * Đăng ký listener để nhận seat updates realtime
     */
    public void setOnSeatUpdate(Consumer<SeatUpdate> listener) {
        this.seatUpdateListener = listener;
    }
    
    /**
     * Bắt đầu lắng nghe messages từ server
     */
    private void startListening() {
        listening = true;
        listenerThread = new Thread(() -> {
            System.out.println("[" + clientName + "] Bat dau lang nghe server...");
            
            while (listening && isConnected()) {
                try {
                    String message = input.readUTF();
                    xuLyServerMessage(message);
                } catch (SocketTimeoutException e) {
                    // Timeout là bình thường, tiếp tục lắng nghe
                } catch (IOException e) {
                    if (listening) {
                        System.err.println("[" + clientName + "] Mat ket noi: " + e.getMessage());
                    }
                    break;
                }
            }
            
            System.out.println("[" + clientName + "] Da ngung lang nghe.");
        });
        
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
    
    /**
     * Xử lý message từ server
     */
    private void xuLyServerMessage(String message) {
        System.out.println("[" + clientName + "] Nhan tu server: " + message);
        
        if (message.startsWith("SEAT_UPDATE|")) {
            // SEAT_UPDATE|maChuyenTau|maChoDat|status
            String[] parts = message.split("\\|");
            if (parts.length >= 4) {
                String maChuyenTau = parts[1];
                String maChoDat = parts[2];
                String status = parts[3];
                
                // Cập nhật cache local
                localSeatCache.put(maChuyenTau + "|" + maChoDat, status);
                
                // Gọi listener
                if (seatUpdateListener != null) {
                    SeatUpdate update = new SeatUpdate(maChuyenTau, maChoDat, status);
                    seatUpdateListener.accept(update);
                }
            }
        } else if (message.startsWith("SEAT_STATUS_LIST|")) {
            // Nhận danh sách trạng thái ghế
            System.out.println("[" + clientName + "] Nhan danh sach trang thai ghe...");
        } else if ("END_SEAT_LIST".equals(message)) {
            System.out.println("[" + clientName + "] Da nhan xong danh sach ghe");
        } else if (message.startsWith("BROADCAST|")) {
            // Thông báo broadcast
            System.out.println("[" + clientName + "] [Broadcast] " + message.substring(10));
        }
    }
    
    /**
     * Ping server
     */
    public boolean ping() {
        if (!isConnected()) return false;
        
        try {
            output.writeUTF("PING");
            output.flush();
            
            String response = input.readUTF();
            return "PONG".equals(response);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Đăng ký theo dõi chuyến tàu để nhận realtime updates
     */
    public void subscribeChuyenTau(String maChuyenTau) {
        if (!isConnected()) return;
        
        try {
            output.writeUTF("SUBSCRIBE|" + maChuyenTau);
            output.flush();
            System.out.println("[" + clientName + "] Da subscribe chuyen tau: " + maChuyenTau);
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi subscribe: " + e.getMessage());
        }
    }
    
    /**
     * Hủy đăng ký theo dõi
     */
    public void unsubscribe(String maChuyenTau) {
        if (!isConnected()) return;
        
        // Xóa cache của chuyến tàu
        localSeatCache.entrySet().removeIf(e -> e.getKey().startsWith(maChuyenTau + "|"));
    }
    
    // =============================================
    // REPOSITORY CALLS - Gọi server để lấy dữ liệu
    // =============================================
    
    /**
     * Lấy danh sách tất cả Ga
     * @return List<String> với format "maGa|tenGa"
     */
    public List<String> getAllGa() {
        if (!isConnected()) return new ArrayList<>();
        
        try {
            output.writeUTF("GET_ALL_GA");
            output.flush();
            
            int size = input.readInt();
            if (size < 0) {
                String error = input.readUTF();
                System.err.println("[" + clientName + "] Loi getAllGa: " + error);
                return new ArrayList<>();
            }
            
            List<String> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add(input.readUTF());
            }
            return result;
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi getAllGa: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy danh sách tất cả Chuyến tàu
     * @return List<String> với format "maChuyenTau|maTau|maTuyen|ngayKhoiHanh"
     */
    public List<String> getAllChuyenTau() {
        if (!isConnected()) return new ArrayList<>();
        
        try {
            output.writeUTF("GET_ALL_CHUYEN_TAU");
            output.flush();
            
            int size = input.readInt();
            if (size < 0) {
                String error = input.readUTF();
                System.err.println("[" + clientName + "] Loi getAllChuyenTau: " + error);
                return new ArrayList<>();
            }
            
            List<String> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add(input.readUTF());
            }
            return result;
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi getAllChuyenTau: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy danh sách Toa theo chuyến tàu
     * @return List<String> với format "maToa|maLoaiToa|tenLoaiToa"
     */
    public List<String> getToaByChuyenTau(String maChuyenTau) {
        if (!isConnected()) return new ArrayList<>();
        
        try {
            output.writeUTF("GET_TOA_BY_CHUYEN");
            output.writeUTF(maChuyenTau);
            output.flush();
            
            int size = input.readInt();
            if (size < 0) {
                String error = input.readUTF();
                System.err.println("[" + clientName + "] Loi getToaByChuyen: " + error);
                return new ArrayList<>();
            }
            
            List<String> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add(input.readUTF());
            }
            return result;
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi getToaByChuyen: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy danh sách Chỗ đặt theo toa
     * @return List<String> với format "maChoDat|soCho|trangThai"
     */
    public List<String> getChoDatByToa(String maToa) {
        if (!isConnected()) return new ArrayList<>();
        
        try {
            output.writeUTF("GET_CHO_DAT_BY_TOA");
            output.writeUTF(maToa);
            output.flush();
            
            int size = input.readInt();
            if (size < 0) {
                String error = input.readUTF();
                System.err.println("[" + clientName + "] Loi getChoDatByToa: " + error);
                return new ArrayList<>();
            }
            
            List<String> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add(input.readUTF());
            }
            return result;
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi getChoDatByToa: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Kiểm tra ghế có trống không (từ cache local)
     */
    public boolean isGheTrong(String maChuyenTau, String maChoDat) {
        String key = maChuyenTau + "|" + maChoDat;
        return !localSeatCache.containsKey(key) || !"DA_DAT".equals(localSeatCache.get(key));
    }
    
    /**
     * Lấy trạng thái ghế từ cache
     */
    public String getSeatStatus(String maChuyenTau, String maChoDat) {
        return localSeatCache.get(maChuyenTau + "|" + maChoDat);
    }
    
    /**
     * Đặt ghế đơn lẻ (realtime)
     */
    public boolean bookSeat(String maChuyenTau, String maChoDat) {
        if (!isConnected()) {
            System.err.println("[" + clientName + "] Chua ket noi server!");
            return false;
        }
        
        try {
            output.writeUTF("BOOK_SEAT");
            output.writeUTF(maChuyenTau);
            output.writeUTF(maChoDat);
            output.flush();
            
            boolean success = input.readBoolean();
            String msg = input.readUTF();
            
            System.out.println("[" + clientName + "] Book seat: " + msg);
            
            if (success) {
                localSeatCache.put(maChuyenTau + "|" + maChoDat, "DA_DAT");
            }
            
            return success;
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi book seat: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Hủy đặt ghế đơn lẻ (realtime)
     */
    public boolean cancelSeat(String maChuyenTau, String maChoDat) {
        if (!isConnected()) {
            System.err.println("[" + clientName + "] Chua ket noi server!");
            return false;
        }
        
        try {
            output.writeUTF("CANCEL_SEAT");
            output.writeUTF(maChuyenTau);
            output.writeUTF(maChoDat);
            output.flush();
            
            boolean success = input.readBoolean();
            String msg = input.readUTF();
            
            System.out.println("[" + clientName + "] Cancel seat: " + msg);
            
            if (success) {
                localSeatCache.remove(maChuyenTau + "|" + maChoDat);
            }
            
            return success;
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi cancel seat: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tìm kiếm vé
     */
    public List<Ve> searchVe(String hoTen, String sdt, String cccd, String maVe) {
        List<Ve> result = new ArrayList<>();
        
        if (!isConnected()) {
            System.err.println("[" + clientName + "] Chua ket noi server!");
            return result;
        }
        
        try {
            output.writeUTF("SEARCH_VE");
            output.writeUTF(hoTen != null ? hoTen : "");
            output.writeUTF(sdt != null ? sdt : "");
            output.writeUTF(cccd != null ? cccd : "");
            output.writeUTF(maVe != null ? maVe : "");
            output.flush();
            
            int size = input.readInt();
            if (size < 0) {
                String error = input.readUTF();
                System.err.println("[" + clientName + "] Loi tim kiem: " + error);
                return result;
            }
            
            for (int i = 0; i < size; i++) {
                String vMaVe = input.readUTF();
                String vTenKH = input.readUTF();
                String vMaChuyen = input.readUTF();
                String vGiaVe = input.readUTF();
                String vTrangThai = input.readUTF();
                
                Ve ve = new Ve();
                ve.setMaVe(vMaVe);
                ve.setMaChuyenTau(vMaChuyen);
                try {
                    ve.setGiaVe(Double.parseDouble(vGiaVe));
                } catch (NumberFormatException e) {
                    ve.setGiaVe(0.0);
                }
                ve.setTrangThai(vTrangThai);
                
                result.add(ve);
            }
            
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi tim kiem: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Đặt vé (gọi RPC đến server)
     */
    public boolean datVe(String maChuyenTau, List<String> dsMaCho) {
        if (!isConnected()) {
            System.err.println("[" + clientName + "] Chua ket noi server!");
            return false;
        }
        
        try {
            output.writeUTF("DAT_VE");
            output.writeUTF(maChuyenTau);
            output.writeInt(dsMaCho.size());
            for (String maCho : dsMaCho) {
                output.writeUTF(maCho);
            }
            output.flush();
            
            boolean success = input.readBoolean();
            String msg = input.readUTF();
            
            System.out.println("[" + clientName + "] Dat ve: " + msg);
            return success;
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi dat ve: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Hủy vé
     */
    public boolean huyVe(String maChuyenTau, List<String> dsMaCho) {
        if (!isConnected()) {
            System.err.println("[" + clientName + "] Chua ket noi server!");
            return false;
        }
        
        try {
            output.writeUTF("HUY_VE");
            output.writeUTF(maChuyenTau);
            output.writeInt(dsMaCho.size());
            for (String maCho : dsMaCho) {
                output.writeUTF(maCho);
            }
            output.flush();
            
            boolean success = input.readBoolean();
            String msg = input.readUTF();
            
            System.out.println("[" + clientName + "] Huy ve: " + msg);
            return success;
        } catch (IOException e) {
            System.err.println("[" + clientName + "] Loi huy ve: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy thông tin tổng hợp
     */
    public int[] getTongHop() {
        if (!isConnected()) {
            return new int[]{-1, 0, 0};
        }
        
        try {
            output.writeUTF("GET_TONG_HOP");
            output.flush();
            
            int activeClients = input.readInt();
            int bookedSeats = input.readInt();
            boolean serverRunning = input.readBoolean();
            
            return new int[]{activeClients, bookedSeats, serverRunning ? 1 : 0};
        } catch (IOException e) {
            return new int[]{-1, 0, 0};
        }
    }
    
    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }
    
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    
    public String getConnectionInfo() {
        if (isConnected()) {
            try {
                InetAddress addr = socket.getInetAddress();
                return "Ket noi " + addr.getHostAddress() + ":" + socket.getPort();
            } catch (Exception e) {
                return "Ket noi (chi tiet khong kha dung)";
            }
        }
        return "Chua ket noi";
    }
    
    // ==================== SEAT UPDATE CLASS ====================
    
    public static class SeatUpdate {
        public final String maChuyenTau;
        public final String maChoDat;
        public final String status;
        
        public SeatUpdate(String maChuyenTau, String maChoDat, String status) {
            this.maChuyenTau = maChuyenTau;
            this.maChoDat = maChoDat;
            this.status = status;
        }
        
        public boolean isBooked() {
            return "DA_DAT".equals(status);
        }
        
        public boolean isAvailable() {
            return "TRONG".equals(status);
        }
        
        @Override
        public String toString() {
            return "SeatUpdate{maChuyenTau='" + maChuyenTau + "', maChoDat='" + maChoDat + "', status='" + status + "'}";
        }
    }
    
    // ==================== MAIN ====================
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Train Ticket Client - Test Connection");
        System.out.println("========================================");
        
        TrainClient client = new TrainClient("localhost", TrainClient.DEFAULT_PORT, "TestClient");
        
        if (client.connect()) {
            System.out.println("\n--- Ping ---");
            System.out.println("Ping result: " + client.ping());
            
            System.out.println("\n--- Subscribe test ---");
            client.subscribeChuyenTau("CT001");
            
            System.out.println("\n--- Book seat test ---");
            boolean bookResult = client.bookSeat("CT001", "C01-1");
            System.out.println("Book C01-1: " + (bookResult ? "Thanh cong" : "That bai"));
            
            System.out.println("\n--- Tong hop ---");
            int[] tongHop = client.getTongHop();
            System.out.println("Client active: " + tongHop[0]);
            System.out.println("Ghe da dat: " + tongHop[1]);
            System.out.println("Server running: " + (tongHop[2] == 1 ? "Co" : "Khong"));
            
            System.out.println("\n--- Cancel seat test ---");
            boolean cancelResult = client.cancelSeat("CT001", "C01-1");
            System.out.println("Cancel C01-1: " + (cancelResult ? "Thanh cong" : "That bai"));
            
            client.disconnect();
        }
        
        System.out.println("\nKet thuc test.");
    }
}
