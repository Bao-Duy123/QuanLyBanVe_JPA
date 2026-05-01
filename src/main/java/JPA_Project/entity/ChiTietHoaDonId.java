package JPA_Project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDonId implements Serializable {
    private String maHD;
    private String maVe;
}
