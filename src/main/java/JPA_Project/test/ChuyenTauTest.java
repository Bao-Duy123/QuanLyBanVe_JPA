package JPA_Project.test;

import JPA_Project.entity.ChuyenTau;
import JPA_Project.repository.ChuyenTauRepository;

import java.time.LocalDate;
import java.util.List;

public class ChuyenTauTest {
    public static void main(String[] args) {
        //test findByGaDiGaDenNgay
        //mau (N'CT001', N'T01', N'TAU01', '2026-05-01', '06:00:00', N'GA001', N'GA004', '2026-05-02', '05:30:00', N'NV001', N'DANG_MO_BAN_VE'),
        //mẫu VALUES (N'SE1_201225_DNA_NTR', N'SE1       ', N'SPT2', N'ADMIN001', N'DNA', N'NTR', CAST(N'2025-12-20' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
        ChuyenTauRepository repository = new ChuyenTauRepository();
        List<ChuyenTau> chuyenTaus = repository.findByGaDiGaDenNgay("DNA", "NTR", LocalDate.of(2025, 12, 20));
        System.out.println("Tìm thấy " + chuyenTaus.size() + " Chuyến tàu: gồm: ");
        for (ChuyenTau ct : chuyenTaus) {
            System.out.println(ct.getMaChuyenTau() + " - " + ct.getTuyen().getTenTuyen() + " - " + ct.getGaDi().getTenGa() + " -> " + ct.getGaDen().getTenGa() + " vào ngày " + ct.getNgayKhoiHanh() + " lúc " + ct.getGioKhoiHanh());
        }
    }
}

