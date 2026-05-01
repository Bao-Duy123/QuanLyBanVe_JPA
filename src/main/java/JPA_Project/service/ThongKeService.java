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
        private double doanhThu;
        private int soVe;
        private Map<String, Double> topGa;

        public ThongKe() {
            this.topGa = new HashMap<>();
        }

        public ThongKe(double doanhThu, int soVe, Map<String, Double> topGa) {
            this.doanhThu = doanhThu;
            this.soVe = soVe;
            this.topGa = topGa != null ? topGa : new HashMap<>();
        }

        public double getDoanhThu() { return doanhThu; }
        public void setDoanhThu(double doanhThu) { this.doanhThu = doanhThu; }
        public int getSoVe() { return soVe; }
        public void setSoVe(int soVe) { this.soVe = soVe; }
        public Map<String, Double> getTopGa() { return topGa; }
        public void setTopGa(Map<String, Double> topGa) { this.topGa = topGa; }
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
