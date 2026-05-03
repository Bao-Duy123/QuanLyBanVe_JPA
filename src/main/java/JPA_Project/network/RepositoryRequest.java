package JPA_Project.network;

import java.io.Serializable;
import java.util.List;

/**
 * Request cho việc gọi repository từ xa
 */
public class RepositoryRequest implements Serializable {
    
    private String action;
    private String entityType;
    private String id;
    private Object data;
    private Object[] params;
    
    public static final String ACTION_FIND_ALL = "FIND_ALL";
    public static final String ACTION_FIND_BY_ID = "FIND_BY_ID";
    public static final String ACTION_SAVE = "SAVE";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_CUSTOM = "CUSTOM";
    
    // Entity types
    public static final String ENTITY_GA = "GA";
    public static final String ENTITY_CHUYEN_TAU = "CHUYEN_TAU";
    public static final String ENTITY_CHO_DAT = "CHO_DAT";
    public static final String ENTITY_TOA = "TOA";
    public static final String ENTITY_VE = "VE";
    public static final String ENTITY_HOA_DON = "HOA_DON";
    public static final String ENTITY_KHACH_HANG = "KHACH_HANG";
    public static final String ENTITY_GIA_VE = "GIA_VE";
    
    public RepositoryRequest() {}
    
    public RepositoryRequest(String action, String entityType) {
        this.action = action;
        this.entityType = entityType;
    }
    
    public RepositoryRequest(String action, String entityType, String id) {
        this.action = action;
        this.entityType = entityType;
        this.id = id;
    }
    
    // Getters & Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public Object[] getParams() { return params; }
    public void setParams(Object[] params) { this.params = params; }
}
