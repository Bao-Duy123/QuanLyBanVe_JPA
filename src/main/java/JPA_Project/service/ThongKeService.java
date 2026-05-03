package JPA_Project.service;

import JPA_Project.entity.HoaDon;
import JPA_Project.entity.Ve;
import JPA_Project.repository.HoaDonRepository;
import JPA_Project.repository.VeRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeService {
    private final HoaDonRepository hoaDonRepository;
    private final VeRepository veRepository;

    public ThongKeService() {
        this.hoaDonRepository = new HoaDonRepository();
        this.veRepository = new VeRepository();
    }

    public ThongKeService(HoaDonRepository hoaDonRepository, VeRepository veRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.veRepository = veRepository;
    }

    public static class ThongKe {
        // Public fields for simpler access
        public double doanhThu;
        public int soVe;
        public int soHoaDon;
        public int soVeDaBan;
        public int soVeTra;
        public double tyLeLapDay;
        public double tangTruong;
        public Map<String, Double> topGa;

        public ThongKe() {
            this.topGa = new HashMap<>();
            this.soHoaDon = 0;
            this.soVeDaBan = 0;
            this.soVeTra = 0;
            this.tyLeLapDay = 0.0;
            this.tangTruong = 0.0;
        }

        public ThongKe(double doanhThu, int soVe, Map<String, Double> topGa) {
            this.doanhThu = doanhThu;
            this.soVe = soVe;
            this.topGa = topGa != null ? topGa : new HashMap<>();
            this.soHoaDon = soVe;
            this.soVeDaBan = soVe;
            this.soVeTra = 0;
            this.tyLeLapDay = 0.0;
            this.tangTruong = 0.0;
        }

        // Backward compatibility getters
        public double getDoanhThu() { return doanhThu; }
        public int getSoVe() { return soVe; }
        public int getSoHoaDon() { return soHoaDon; }
        public int getSoVeDaBan() { return soVeDaBan; }
        public int getSoVeTra() { return soVeTra; }
        public double getTyLeLapDay() { return tyLeLapDay; }
        public double getTangTruong() { return tangTruong; }
        public Map<String, Double> getTopGa() { return topGa; }
    }

    /**
     * Get today's statistics - simplified version
     */
    public ThongKe getThongKeNgay() {
        ThongKe tk = new ThongKe();
        LocalDateTime today = LocalDateTime.now();
        
        try {
            List<HoaDon> allHoaDons = hoaDonRepository.findAll();
            
            // Filter for today
            List<HoaDon> todayHoaDons = allHoaDons.stream()
                    .filter(h -> h.getNgayLap() != null)
                    .filter(h -> {
                        LocalDateTime ngayLap = h.getNgayLap();
                        return !ngayLap.isBefore(today.toLocalDate().atStartOfDay()) 
                            && ngayLap.isBefore(today.toLocalDate().atStartOfDay().plusDays(1));
                    })
                    .toList();
            
            tk.soHoaDon = todayHoaDons.size();
            tk.soVeDaBan = todayHoaDons.size();
            tk.doanhThu = todayHoaDons.stream().mapToDouble(HoaDon::getTongTien).sum();
            
        } catch (Exception e) {
            System.err.println("Error getting today's stats: " + e.getMessage());
        }
        
        return tk;
    }

    public ThongKe thongKeThang(int thang, int nam) {
        LocalDateTime tuNgay = LocalDateTime.of(nam, thang, 1, 0, 0);
        LocalDateTime denNgay = tuNgay.plusMonths(1);

        List<HoaDon> hoaDons = hoaDonRepository.findAll();

        double doanhThu = hoaDons.stream()
                .filter(h -> h.getNgayLap() != null)
                .filter(h -> !h.getNgayLap().isBefore(tuNgay) && h.getNgayLap().isBefore(denNgay))
                .mapToDouble(HoaDon::getTongTien)
                .sum();

        int soVe = (int) hoaDons.stream()
                .filter(h -> h.getNgayLap() != null)
                .filter(h -> !h.getNgayLap().isBefore(tuNgay) && h.getNgayLap().isBefore(denNgay))
                .count();

        Map<String, Double> topGa = thongKeTopGaTheoKhoangThoiGian(tuNgay, denNgay);

        return new ThongKe(doanhThu, soVe, topGa);
    }

    public ThongKe thongKeNgay(LocalDateTime ngay) {
        LocalDateTime tuNgay = ngay.toLocalDate().atStartOfDay();
        LocalDateTime denNgay = tuNgay.plusDays(1);

        List<HoaDon> hoaDons = hoaDonRepository.findAll();

        double doanhThu = hoaDons.stream()
                .filter(h -> h.getNgayLap() != null)
                .filter(h -> !h.getNgayLap().isBefore(tuNgay) && h.getNgayLap().isBefore(denNgay))
                .mapToDouble(HoaDon::getTongTien)
                .sum();

        int soVe = (int) hoaDons.stream()
                .filter(h -> h.getNgayLap() != null)
                .filter(h -> !h.getNgayLap().isBefore(tuNgay) && h.getNgayLap().isBefore(denNgay))
                .count();

        Map<String, Double> topGa = thongKeTopGaTheoKhoangThoiGian(tuNgay, denNgay);

        return new ThongKe(doanhThu, soVe, topGa);
    }

    private Map<String, Double> thongKeTopGaTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        Map<String, Double> gaThongKe = new HashMap<>();

        List<Ve> ves = veRepository.findByTrangThai("DA_BAN");
        for (Ve ve : ves) {
            if (ve.getChuyenTau() != null && ve.getChuyenTau().getGaDen() != null) {
                String tenGa = ve.getChuyenTau().getGaDen().getTenGa();
                if (tenGa != null) {
                    double giaVe = ve.getGiaVe();
                    gaThongKe.merge(tenGa, giaVe, Double::sum);
                }
            }
        }

        return gaThongKe;
    }
}
