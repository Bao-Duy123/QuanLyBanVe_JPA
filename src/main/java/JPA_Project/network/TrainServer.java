package JPA_Project.network;

import JPA_Project.entity.Ve;
import JPA_Project.entity.HoaDon;
import JPA_Project.entity.ChoDat;
import JPA_Project.entity.Ga;
import JPA_Project.entity.ChuyenTau;
import JPA_Project.entity.Toa;
import JPA_Project.repository.VeRepository;
import JPA_Project.repository.HoaDonRepository;
import JPA_Project.repository.ChoDatRepository;
import JPA_Project.repository.GaRepository;
import JPA_Project.repository.ChuyenTauRepository;
import JPA_Project.repository.ToaRepository;
import JPA_Project.service.BanVeService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class TrainServer {
    
    public static final int PORT = 8888;
    private static final String SERVER_NAME = "Train Ticket Server";
    
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private final Map<String, ClientHandler> activeClients = new ConcurrentHashMap<>();
    
    // Cache ghế đã đặt: Key = "maChuyenTau|maChoDat", Value = "DA_DAT"/"TRONG"
    private final Map<String, String> seatStatusCache = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
    
    // Danh sách client theo dõi chuyến tàu: maChuyenTau -> Set<ClientHandler>
    private final Map<String, Set<ClientHandler>> chuyenTauSubscribers = new ConcurrentHashMap<>();
    
    private final VeRepository veRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ChoDatRepository choDatRepository;
    private final GaRepository gaRepository;
    private final ChuyenTauRepository chuyenTauRepository;
    private final ToaRepository toaRepository;
    
    private boolean isRunning = false;
    
    public TrainServer() {
        this.veRepository = new VeRepository();
        this.hoaDonRepository = new HoaDonRepository();
        this.choDatRepository = new ChoDatRepository();
        this.gaRepository = new GaRepository();
        this.chuyenTauRepository = new ChuyenTauRepository();
        this.toaRepository = new ToaRepository();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            threadPool = Executors.newFixedThreadPool(30);
            isRunning = true;
            
            // Khởi tạo cache từ CSDL
            khoiTaoSeatCache();
            
            System.out.println("========================================");
            System.out.println("  " + SERVER_NAME);
            System.out.println("  Port: " + PORT);
            System.out.println("  Status: RUNNING");
            System.out.println("  Cache seats: " + seatStatusCache.size() + " entries");
            System.out.println("========================================");
            System.out.println();
            
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    String clientId = "Client-" + System.currentTimeMillis();
                    ClientHandler handler = new ClientHandler(clientSocket, clientId);
                    activeClients.put(clientId, handler);
                    threadPool.execute(handler);
                    
                    System.out.println("[" + clientId + "] Kết nối mới từ " 
                            + clientSocket.getInetAddress().getHostAddress());
                } catch (SocketException e) {
                    if (isRunning) {
                        System.err.println("Socket error: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Khởi tạo cache ghế từ CSDL
     */
    private void khoiTaoSeatCache() {
        try {
            List<Ve> allVe = veRepository.findByTrangThai("DA_BAN");
            for (Ve ve : allVe) {
                String key = taoSeatKey(ve.getMaChuyenTau(), ve.getMaChoDat());
                if (key != null) {
                    seatStatusCache.put(key, "DA_DAT");
                }
            }
            System.out.println("[Server] Da khoi tao cache voi " + seatStatusCache.size() + " ghe da dat");
        } catch (Exception e) {
            System.out.println("[Server] Loi khoi tao cache: " + e.getMessage());
        }
    }
    
    private String taoSeatKey(String maChuyenTau, String maChoDat) {
        if (maChuyenTau == null || maChoDat == null) return null;
        return maChuyenTau + "|" + maChoDat;
    }
    
    public void stop() {
        isRunning = false;
        try {
            for (ClientHandler handler : activeClients.values()) {
                handler.close();
            }
            activeClients.clear();
            
            if (threadPool != null) {
                threadPool.shutdown();
            }
            
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            System.out.println("Server đã dừng.");
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
    
    public int getActiveClientCount() {
        return activeClients.size();
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    // ========== BROADCAST METHODS ==========
    
    /**
     * Cập nhật trạng thái ghế trong cache
     */
    public void updateSeatStatus(String maChuyenTau, String maChoDat, String status) {
        String key = taoSeatKey(maChuyenTau, maChoDat);
        if (key != null) {
            cacheLock.writeLock().lock();
            try {
                if ("TRONG".equals(status)) {
                    seatStatusCache.remove(key);
                } else {
                    seatStatusCache.put(key, status);
                }
            } finally {
                cacheLock.writeLock().unlock();
            }
        }
    }
    
    /**
     * Kiểm tra ghế có trống không
     */
    public boolean isGheTrong(String maChuyenTau, String maChoDat) {
        String key = taoSeatKey(maChuyenTau, maChoDat);
        if (key == null) return true;
        
        cacheLock.readLock().lock();
        try {
            return !seatStatusCache.containsKey(key);
        } finally {
            cacheLock.readLock().unlock();
        }
    }
    
    /**
     * Đăng ký client theo dõi chuyến tàu
     */
    public void subscribeChuyenTau(String maChuyenTau, ClientHandler handler) {
        chuyenTauSubscribers.computeIfAbsent(maChuyenTau, k -> ConcurrentHashMap.newKeySet()).add(handler);
        System.out.println("[Server] Client " + handler.clientId + " subscribe chuyen tau " + maChuyenTau);
    }
    
    /**
     * Hủy đăng ký client
     */
    public void unsubscribeClient(ClientHandler handler) {
        for (Set<ClientHandler> subscribers : chuyenTauSubscribers.values()) {
            subscribers.remove(handler);
        }
    }
    
    /**
     * Broadcast cập nhật ghế đến tất cả client đang theo dõi chuyến tàu
     */
    public void broadcastSeatUpdate(String maChuyenTau, String maChoDat, String status, String clientIdGoc) {
        Set<ClientHandler> subscribers = chuyenTauSubscribers.get(maChuyenTau);
        if (subscribers == null || subscribers.isEmpty()) return;
        
        // Cập nhật cache
        updateSeatStatus(maChuyenTau, maChoDat, status);
        
        final String message = "SEAT_UPDATE|" + maChuyenTau + "|" + maChoDat + "|" + status;
        
        for (ClientHandler handler : subscribers) {
            // Không gửi lại cho client đã thực hiện thao tác
            if (!handler.clientId.equals(clientIdGoc)) {
                handler.sendMessage(message);
            }
        }
        
        System.out.println("[Server] Broadcast seat update: " + message + " to " + subscribers.size() + " clients");
    }
    
    /**
     * Broadcast thông tin đến tất cả client
     */
    public void broadcastToAll(String message, String excludeClientId) {
        for (ClientHandler handler : activeClients.values()) {
            if (!handler.clientId.equals(excludeClientId)) {
                handler.sendMessage(message);
            }
        }
    }
    
    // ========== CLIENT HANDLER ==========
    
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final String clientId;
        private DataInputStream input;
        private DataOutputStream output;
        private String currentChuyenTau;
        
        public ClientHandler(Socket socket, String clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }
        
        @Override
        public void run() {
            try {
                input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                
                System.out.println("[" + clientId + "] Dang xu ly...");
                
                while (isRunning && !socket.isClosed()) {
                    try {
                        String command = input.readUTF();
                        handleCommand(command);
                    } catch (EOFException | SocketException e) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("[" + clientId + "] Loi ket noi: " + e.getMessage());
            } finally {
                close();
                activeClients.remove(clientId);
                unsubscribeClient(this);
                System.out.println("[" + clientId + "] Da ngat ket noi. Client active: " + activeClients.size());
            }
        }
        
        private synchronized void handleCommand(String command) throws IOException {
            System.out.println("[" + clientId + "] Nhan lenh: " + command);
            
            if (command.startsWith("SUBSCRIBE|")) {
                // SUBSCRIBE|maChuyenTau
                String maChuyenTau = command.substring(9);
                subscribeChuyenTau(maChuyenTau, this);
                this.currentChuyenTau = maChuyenTau;
                
                // Gửi trạng thái ghế hiện tại cho client
                guiSeatStatusHienTai(maChuyenTau);
                return;
            }
            
            switch (command) {
                case "PING":
                    output.writeUTF("PONG");
                    output.flush();
                    break;
                    
                case "SEARCH_VE":
                    handleSearchVe();
                    break;
                    
                case "GET_SEAT_STATUS":
                    handleGetSeatStatus();
                    break;
                    
                case "BOOK_SEAT":
                    handleBookSeat();
                    break;
                    
                case "CANCEL_SEAT":
                    handleCancelSeat();
                    break;
                    
                case "DAT_VE":
                    handleDatVe();
                    break;
                    
                case "HUY_VE":
                    handleHuyVe();
                    break;
                    
                case "GET_HOA_DON":
                    handleGetHoaDon();
                    break;
                    
                case "GET_TONG_HOP":
                    handleGetTongHop();
                    break;
                    
                // === Repository calls ===
                case "GET_ALL_GA":
                    handleGetAllGa();
                    break;
                    
                case "GET_ALL_CHUYEN_TAU":
                    handleGetAllChuyenTau();
                    break;
                    
                case "GET_TOA_BY_CHUYEN":
                    handleGetToaByChuyen();
                    break;
                    
                case "GET_CHO_DAT_BY_TOA":
                    handleGetChoDatByToa();
                    break;
                    
                default:
                    output.writeUTF("ERROR: Lenh khong hop le");
                    output.flush();
            }
        }
        
        /**
         * Gửi trạng thái tất cả ghế cho chuyến tàu
         */
        private void guiSeatStatusHienTai(String maChuyenTau) throws IOException {
            output.writeUTF("SEAT_STATUS_LIST|" + maChuyenTau);
            output.flush();
            
            int count = 0;
            cacheLock.readLock().lock();
            try {
                for (Map.Entry<String, String> entry : seatStatusCache.entrySet()) {
                    if (entry.getKey().startsWith(maChuyenTau + "|")) {
                        String maChoDat = entry.getKey().substring(maChuyenTau.length() + 1);
                        output.writeUTF(maChoDat + "|" + entry.getValue());
                        output.flush();
                        count++;
                    }
                }
            } finally {
                cacheLock.readLock().unlock();
            }
            
            output.writeUTF("END_SEAT_LIST");
            output.flush();
            
            System.out.println("[" + clientId + "] Gui " + count + " seat statuses cho " + maChuyenTau);
        }
        
        private void handleSearchVe() throws IOException {
            try {
                String hoTen = input.readUTF();
                String sdt = input.readUTF();
                String cccd = input.readUTF();
                String maVe = input.readUTF();
                
                List<Ve> dsVe = veRepository.timVeTheoKhachHangDetailed(
                    hoTen.isEmpty() ? null : hoTen,
                    sdt.isEmpty() ? null : sdt,
                    cccd.isEmpty() ? null : cccd,
                    maVe.isEmpty() ? null : maVe
                );
                
                output.writeInt(dsVe.size());
                output.flush();
                
                for (Ve ve : dsVe) {
                    output.writeUTF(ve.getMaVe() != null ? ve.getMaVe() : "");
                    output.writeUTF(ve.getKhachHang() != null ? ve.getKhachHang().getHoTen() : "");
                    output.writeUTF(ve.getMaChuyenTau() != null ? ve.getMaChuyenTau() : "");
                    output.writeUTF(ve.getGiaVe() != null ? String.valueOf(ve.getGiaVe()) : "0");
                    output.writeUTF(ve.getTrangThai() != null ? ve.getTrangThai() : "");
                    output.flush();
                }
            } catch (Exception e) {
                output.writeInt(-1);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        private void handleGetSeatStatus() throws IOException {
            try {
                String maChuyenTau = input.readUTF();
                
                // Kiểm tra ghế trống dựa trên cache
                List<ChoDat> allCho = choDatRepository.findAll();
                
                output.writeInt(allCho.size());
                output.flush();
                
                for (ChoDat cho : allCho) {
                    String key = taoSeatKey(maChuyenTau, cho.getMaCho());
                    boolean isEmpty = !seatStatusCache.containsKey(key);
                    
                    output.writeUTF(cho.getMaCho());
                    output.writeBoolean(isEmpty);
                    output.flush();
                }
            } catch (Exception e) {
                output.writeInt(-1);
                output.flush();
            }
        }
        
        private void handleBookSeat() throws IOException {
            try {
                String maChuyenTau = input.readUTF();
                String maChoDat = input.readUTF();
                
                String key = taoSeatKey(maChuyenTau, maChoDat);
                
                // Kiểm tra và đánh dấu ghế
                cacheLock.writeLock().lock();
                boolean wasEmpty;
                try {
                    wasEmpty = !seatStatusCache.containsKey(key);
                    if (wasEmpty) {
                        seatStatusCache.put(key, "DA_DAT");
                    }
                } finally {
                    cacheLock.writeLock().unlock();
                }
                
                output.writeBoolean(wasEmpty);
                output.writeUTF(wasEmpty ? "Dat ghe thanh cong" : "Ghe da duoc dat");
                output.flush();
                
                if (wasEmpty) {
                    // Broadcast cho các client khác
                    broadcastSeatUpdate(maChuyenTau, maChoDat, "DA_DAT", clientId);
                }
            } catch (Exception e) {
                output.writeBoolean(false);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        private void handleCancelSeat() throws IOException {
            try {
                String maChuyenTau = input.readUTF();
                String maChoDat = input.readUTF();
                
                String key = taoSeatKey(maChuyenTau, maChoDat);
                
                cacheLock.writeLock().lock();
                boolean wasBooked;
                try {
                    wasBooked = seatStatusCache.containsKey(key);
                    if (wasBooked) {
                        seatStatusCache.remove(key);
                    }
                } finally {
                    cacheLock.writeLock().unlock();
                }
                
                output.writeBoolean(wasBooked);
                output.writeUTF(wasBooked ? "Huy ghe thanh cong" : "Ghe chua duoc dat");
                output.flush();
                
                if (wasBooked) {
                    broadcastSeatUpdate(maChuyenTau, maChoDat, "TRONG", clientId);
                }
            } catch (Exception e) {
                output.writeBoolean(false);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        private void handleDatVe() throws IOException {
            try {
                String maChuyenTau = input.readUTF();
                int soLuong = input.readInt();
                
                // Nhận danh sách ghế
                List<String> dsMaCho = new ArrayList<>();
                for (int i = 0; i < soLuong; i++) {
                    dsMaCho.add(input.readUTF());
                }
                
                // Kiểm tra tất cả ghế
                cacheLock.writeLock().lock();
                boolean allEmpty = true;
                List<String> dsGheTrong = new ArrayList<>();
                try {
                    for (String maCho : dsMaCho) {
                        String key = taoSeatKey(maChuyenTau, maCho);
                        if (seatStatusCache.containsKey(key)) {
                            allEmpty = false;
                            break;
                        } else {
                            dsGheTrong.add(maCho);
                        }
                    }
                    
                    if (allEmpty) {
                        // Đánh dấu tất cả ghế
                        for (String maCho : dsMaCho) {
                            String key = taoSeatKey(maChuyenTau, maCho);
                            seatStatusCache.put(key, "DA_DAT");
                        }
                    }
                } finally {
                    cacheLock.writeLock().unlock();
                }
                
                output.writeBoolean(allEmpty);
                output.writeUTF(allEmpty ? "Dat " + soLuong + " ghe thanh cong" : "Mot so ghe da duoc dat");
                output.flush();
                
                if (allEmpty) {
                    // Broadcast cho các client
                    for (String maCho : dsMaCho) {
                        broadcastSeatUpdate(maChuyenTau, maCho, "DA_DAT", clientId);
                    }
                }
            } catch (Exception e) {
                output.writeBoolean(false);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        private void handleHuyVe() throws IOException {
            try {
                String maChuyenTau = input.readUTF();
                int soLuong = input.readInt();
                
                List<String> dsMaCho = new ArrayList<>();
                for (int i = 0; i < soLuong; i++) {
                    dsMaCho.add(input.readUTF());
                }
                
                // Xóa khỏi cache
                cacheLock.writeLock().lock();
                try {
                    for (String maCho : dsMaCho) {
                        String key = taoSeatKey(maChuyenTau, maCho);
                        seatStatusCache.remove(key);
                    }
                } finally {
                    cacheLock.writeLock().unlock();
                }
                
                output.writeBoolean(true);
                output.writeUTF("Huy " + soLuong + " ghe thanh cong");
                output.flush();
                
                // Broadcast
                for (String maCho : dsMaCho) {
                    broadcastSeatUpdate(maChuyenTau, maCho, "TRONG", clientId);
                }
            } catch (Exception e) {
                output.writeBoolean(false);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        private void handleGetHoaDon() throws IOException {
            try {
                String maHD = input.readUTF();
                
                hoaDonRepository.findByMaHDWithDetails(maHD).ifPresentOrElse(
                    hd -> {
                        try {
                            output.writeBoolean(true);
                            output.writeUTF(hd.getMaHD() != null ? hd.getMaHD() : "");
                            output.writeUTF(hd.getKhachHang() != null ? hd.getKhachHang().getHoTen() : "");
                            output.writeUTF(String.valueOf(hd.getTongTien()));
                            output.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> {
                        try {
                            output.writeBoolean(false);
                            output.writeUTF("Khong tim thay hoa don");
                            output.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                );
            } catch (Exception e) {
                output.writeBoolean(false);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        private void handleGetTongHop() throws IOException {
            output.writeInt(activeClients.size());
            output.writeInt(seatStatusCache.size());
            output.writeBoolean(isRunning);
            output.flush();
        }
        
        // =============================================
        // REPOSITORY CALL HANDLERS
        // =============================================
        
        private void handleGetAllGa() throws IOException {
            try {
                List<Ga> dsGa = gaRepository.findAll();
                output.writeInt(dsGa.size());
                for (Ga ga : dsGa) {
                    output.writeUTF(ga.getMaGa() + "|" + ga.getTenGa());
                }
                output.flush();
            } catch (Exception e) {
                output.writeInt(-1);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        private void handleGetAllChuyenTau() throws IOException {
            try {
                List<ChuyenTau> dsChuyen = chuyenTauRepository.findAll();
                output.writeInt(dsChuyen.size());
                for (ChuyenTau ct : dsChuyen) {
                    String data = ct.maChuyenTau + "|" + 
                                  ct.maTau + "|" +
                                  ct.maTuyen + "|" +
                                  ct.ngayKhoiHanh;
                    output.writeUTF(data);
                }
                output.flush();
            } catch (Exception e) {
                output.writeInt(-1);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        private void handleGetToaByChuyen() throws IOException {
            try {
                String maChuyenTau = input.readUTF();
                // Tìm chuyến tàu để lấy mã tàu
                ChuyenTau chuyen = chuyenTauRepository.findById(maChuyenTau);
                if (chuyen == null) {
                    output.writeInt(0);
                    output.flush();
                    return;
                }
                
                String maTau = chuyen.maTau;
                List<Toa> dsToa = toaRepository.findByMaTauWithLoaiToa(maTau);
                output.writeInt(dsToa.size());
                for (Toa toa : dsToa) {
                    String maLoai = toa.getLoaiToa() != null ? toa.getLoaiToa() : "";
                    String tenLoai = toa.getLoaiToaRef() != null ? toa.getLoaiToaRef().getTenLoaiToa() : "";
                    String data = toa.getMaToa() + "|" + maLoai + "|" + tenLoai;
                    output.writeUTF(data);
                }
                output.flush();
            } catch (Exception e) {
                output.writeInt(-1);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        private void handleGetChoDatByToa() throws IOException {
            try {
                String maToa = input.readUTF();
                List<ChoDat> dsCho = choDatRepository.findByMaToa(maToa);
                output.writeInt(dsCho.size());
                for (ChoDat cho : dsCho) {
                    // Trạng thái: kiểm tra xem có vé không
                    boolean daDat = cho.getDanhSachVe() != null && !cho.getDanhSachVe().isEmpty();
                    String trangThai = daDat ? "DA_DAT" : "TRONG";
                    String data = cho.getMaCho() + "|" + cho.getSoCho() + "|" + trangThai;
                    output.writeUTF(data);
                }
                output.flush();
            } catch (Exception e) {
                output.writeInt(-1);
                output.writeUTF("Loi: " + e.getMessage());
                output.flush();
            }
        }
        
        public void sendMessage(String message) {
            try {
                output.writeUTF(message);
                output.flush();
            } catch (IOException e) {
                System.err.println("[" + clientId + "] Loi gui message: " + e.getMessage());
            }
        }
        
        public void close() {
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client handler: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        TrainServer server = new TrainServer();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nDang tat server...");
            server.stop();
        }));
        
        server.start();
    }
}
