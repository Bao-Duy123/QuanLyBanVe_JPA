package JPA_Project.network;

import java.util.ArrayList;
import java.util.List;

/**
 * TrainClient - Client kết nối đến TrainServer
 * Sử dụng cho các ứng dụng cần gọi repository từ xa
 */
public class RemoteRepositoryClient {
    
    private String host;
    private int port;
    private String clientName;
    
    public RemoteRepositoryClient(String host, int port, String clientName) {
        this.host = host;
        this.port = port;
        this.clientName = clientName;
    }
    
    /**
     * Gọi repository từ xa
     * @param request Yêu cầu chứa action, entityType, params
     * @return Response chứa kết quả
     */
    public RepositoryResponse callRepository(RepositoryRequest request) {
        // TODO: Implement network call
        // Trả về error nếu chưa implement
        return RepositoryResponse.error("Chua implement RemoteRepositoryClient");
    }
    
    /**
     * Lấy danh sách tất cả Ga
     * @return List<String> với format "maGa|tenGa"
     */
    public List<String> getAllGa() {
        RepositoryRequest req = new RepositoryRequest(
            RepositoryRequest.ACTION_FIND_ALL,
            RepositoryRequest.ENTITY_GA
        );
        RepositoryResponse resp = callRepository(req);
        if (resp.isSuccess() && resp.getDataList() != null) {
            List<String> result = new ArrayList<>();
            for (Object item : resp.getDataList()) {
                result.add(item.toString());
            }
            return result;
        }
        return new ArrayList<>();
    }
    
    /**
     * Lấy danh sách Chuyến tàu
     * @return List<String> với format "maChuyenTau|maTau|maTuyen|ngayKhoiHanh"
     */
    public List<String> getAllChuyenTau() {
        RepositoryRequest req = new RepositoryRequest(
            RepositoryRequest.ACTION_FIND_ALL,
            RepositoryRequest.ENTITY_CHUYEN_TAU
        );
        RepositoryResponse resp = callRepository(req);
        if (resp.isSuccess() && resp.getDataList() != null) {
            List<String> result = new ArrayList<>();
            for (Object item : resp.getDataList()) {
                result.add(item.toString());
            }
            return result;
        }
        return new ArrayList<>();
    }
    
    /**
     * Lấy danh sách Ghế theo toa
     */
    public List<String> getChoDatByToa(String maToa) {
        RepositoryRequest req = new RepositoryRequest(
            RepositoryRequest.ACTION_CUSTOM,
            RepositoryRequest.ENTITY_CHO_DAT
        );
        req.setParams(new Object[]{maToa});
        RepositoryResponse resp = callRepository(req);
        if (resp.isSuccess() && resp.getDataList() != null) {
            List<String> result = new ArrayList<>();
            for (Object item : resp.getDataList()) {
                result.add(item.toString());
            }
            return result;
        }
        return new ArrayList<>();
    }
}
