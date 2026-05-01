package JPA_Project.converter;

import JPA_Project.entity.TrangThaiChuyenTau;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class TrangThaiChuyenTauConverter implements AttributeConverter<TrangThaiChuyenTau, String> {

    private static final Map<String, TrangThaiChuyenTau> LEGACY_MAP = new HashMap<>();
    static {
        LEGACY_MAP.put("DANG_MO_BAN_VE", TrangThaiChuyenTau.CHO_KHOI_HANH);
        LEGACY_MAP.put("DA_HUY", TrangThaiChuyenTau.DA_HUY);
        LEGACY_MAP.put("CHUA_MO_BAN_VE", TrangThaiChuyenTau.CHUA_MO_BAN_VE);
        LEGACY_MAP.put("DA_DEN", TrangThaiChuyenTau.DA_DEN);
    }

    @Override
    public String convertToDatabaseColumn(TrangThaiChuyenTau attribute) {
        if (attribute == null) return "Chờ khởi hành";
        return attribute.getTenHienThi();
    }

    @Override
    public TrangThaiChuyenTau convertToEntityAttribute(String dbData) {
        if (dbData == null) return TrangThaiChuyenTau.CHO_KHOI_HANH;
        
        // Kiểm tra ánh xạ tên enum cũ (DANG_MO_BAN_VE, etc)
        if (LEGACY_MAP.containsKey(dbData)) {
            return LEGACY_MAP.get(dbData);
        }
        
        // Kiểm tra theo tenHienThi
        for (TrangThaiChuyenTau tt : TrangThaiChuyenTau.values()) {
            if (tt.getTenHienThi().equals(dbData) || tt.name().equals(dbData)) {
                return tt;
            }
        }
        return TrangThaiChuyenTau.CHO_KHOI_HANH;
    }
}
