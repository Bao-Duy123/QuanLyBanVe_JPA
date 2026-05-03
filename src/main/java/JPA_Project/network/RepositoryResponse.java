package JPA_Project.network;

import java.io.Serializable;
import java.util.List;

/**
 * Response từ server chứa kết quả trả về
 */
public class RepositoryResponse implements Serializable {
    
    private boolean success;
    private Object data;
    private List<?> dataList;
    private String errorMessage;
    
    public RepositoryResponse() {}
    
    public static RepositoryResponse success(Object data) {
        RepositoryResponse resp = new RepositoryResponse();
        resp.success = true;
        resp.data = data;
        return resp;
    }
    
    public static RepositoryResponse successList(List<?> dataList) {
        RepositoryResponse resp = new RepositoryResponse();
        resp.success = true;
        resp.dataList = dataList;
        return resp;
    }
    
    public static RepositoryResponse error(String message) {
        RepositoryResponse resp = new RepositoryResponse();
        resp.success = false;
        resp.errorMessage = message;
        return resp;
    }
    
    // Getters & Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public List<?> getDataList() { return dataList; }
    public void setDataList(List<?> dataList) { this.dataList = dataList; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
