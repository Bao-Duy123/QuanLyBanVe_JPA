package JPA_Project.service;

import JPA_Project.db.Tx;
import JPA_Project.entity.*;
import JPA_Project.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DoiVeService - Service xử lý nghiệp vụ đổi vé tàu.
 */
public class DoiVeService {

    private final HoaDonRepository hoaDonRepo;
    private final ChiTietHoaDonRepository chiTietHoaDonRepo;
    private final VeRepository veRepo;
    private final ChuyenTauRepository chuyenTauRepo;
    private final ChoDatRepository choDatRepo;
    private final KhachHangRepository khachHangRepo;
    private final BanVeService banVeService;

    public DoiVeService() {
        this.hoaDonRepo = new HoaDonRepository();
        this.chiTietHoaDonRepo = new ChiTietHoaDonRepository();
        this.veRepo = new VeRepository();
        this.chuyenTauRepo = new ChuyenTauRepository();
        this.choDatRepo = new ChoDatRepository();
        this.khachHangRepo = new KhachHangRepository();
        this.banVeService = new BanVeService();
    }

    /**
     * Tìm vé theo mã vé.
     */
    public Optional<Ve> findVeByMaVe(String maVe) {
        return veRepo.findByMaVe(maVe);
    }

    /**
     * Tìm vé theo mã hóa đơn.
     */
    public List<Ve> findVeByMaHD(String maHD) {
        return veRepo.findByMaHD(maHD);
    }

    /**
     * Lấy thông tin hóa đơn theo mã.
     */
    public Optional<HoaDon> findHoaDonByMaHD(String maHD) {
        HoaDon hd = hoaDonRepo.findByMaHD(maHD);
        return Optional.ofNullable(hd);
    }

    /**
     * Tính giá vé mới cho một ghế.
     */
    public BigDecimal tinhGiaVeMoi(String maChuyenTau, String maLoaiToa, String maLoaiVe) {
        return banVeService.tinhGiaVe(maChuyenTau, maLoaiToa, maLoaiVe, null);
    }

    /**
     * Lấy danh sách ghế trống của một toa trong chuyến tàu.
     */
    public List<ChoDat> getGheTrong(String maToa, String maChuyenTau) {
        List<ChoDat> tatCaGhe = choDatRepo.findByMaToa(maToa);
        List<Ve> veDaBan = veRepo.findByMaChuyenTau(maChuyenTau);

        List<String> maChoDaBan = veDaBan.stream()
                .map(Ve::getMaChoDat)
                .filter(mc -> mc != null)
                .toList();

        return tatCaGhe.stream()
                .filter(ghe -> !maChoDaBan.contains(ghe.getMaCho()))
                .toList();
    }

    /**
     * Kết quả giao dịch đổi vé.
     */
    public static class KetQuaDoiVe {
        public boolean thanhCong;
        public String maHD;
        public List<Ve> danhSachVeMoi;
        public String loiNhan;
        public BigDecimal chenhLech;

        public static KetQuaDoiVe thanhCong(String maHD, List<Ve> veMoi, BigDecimal chenhLech) {
            KetQuaDoiVe kq = new KetQuaDoiVe();
            kq.thanhCong = true;
            kq.maHD = maHD;
            kq.danhSachVeMoi = veMoi;
            kq.chenhLech = chenhLech;
            return kq;
        }

        public static KetQuaDoiVe thatBai(String loiNhan) {
            KetQuaDoiVe kq = new KetQuaDoiVe();
            kq.thanhCong = false;
            kq.loiNhan = loiNhan;
            return kq;
        }
    }

    /**
     * Thực hiện đổi vé.
     * @param listVeCu Danh sách vé cũ cần đổi
     * @param mapGheMoi Map từ mã vé cũ sang ghế mới
     * @param mapGiaMoi Map từ mã vé cũ sang giá mới
     * @param maNV Mã nhân viên thực hiện
     * @param tongChenhLech Tổng chênh lệch phải trả thêm hoặc hoàn tiền
     * @param phuongThucThanhToan Phương thức thanh toán
     * @return Kết quả giao dịch
     */
    public KetQuaDoiVe thucHienDoiVe(List<Ve> listVeCu,
                                      Map<String, ChoDat> mapGheMoi,
                                      Map<String, Long> mapGiaMoi,
                                      String maNV,
                                      long tongChenhLech,
                                      String phuongThucThanhToan) {
        try {
            return Tx.inTx(em -> {
                String maHD = banVeService.taoMaHoaDon(maNV, "01");

                String maKhachHang = listVeCu.isEmpty() ? null : listVeCu.get(0).getMaKhachHang();

                HoaDon hoaDon = new HoaDon();
                hoaDon.setMaHD(maHD);
                hoaDon.setMaKhachHang(maKhachHang);
                hoaDon.setMaNVLap(maNV);
                hoaDon.setTongTien((double) tongChenhLech);
                hoaDon.setTongCong((double) tongChenhLech);
                hoaDon.setNgayLap(LocalDateTime.now());
                hoaDon.setPhuongThuc(phuongThucThanhToan);
                hoaDon.setLoaiHoaDon("Đổi vé");

                em.persist(hoaDon);

                List<Ve> danhSachVeMoi = new ArrayList<>();

                for (Ve veCu : listVeCu) {
                    veCu.setTrangThai("DA_HUY");
                    em.merge(veCu);

                    ChiTietHoaDon cthdAm = new ChiTietHoaDon();
                    cthdAm.setMaHD(maHD);
                    cthdAm.setMaVe(veCu.getMaVe());
                    cthdAm.setSoLuong(-1);
                    cthdAm.setDonGia(veCu.getGiaVe());
                    cthdAm.setHoaDon(hoaDon);
                    cthdAm.setVe(veCu);
                    em.persist(cthdAm);

                    ChoDat gheMoi = mapGheMoi.get(veCu.getMaVe());
                    Long giaMoi = mapGiaMoi.get(veCu.getMaVe());

                    if (gheMoi != null && giaMoi != null) {
                        String maVeMoi = banVeService.taoMaVe();

                        Ve veMoi = new Ve();
                        veMoi.setMaVe(maVeMoi);
                        veMoi.setMaChuyenTau(veCu.getMaChuyenTau());
                        veMoi.setMaChoDat(gheMoi.getMaCho());
                        veMoi.setMaNV(maNV);
                        veMoi.setMaKhachHang(veCu.getMaKhachHang());
                        veMoi.setMaLoaiVe(veCu.getMaLoaiVe());
                        veMoi.setGiaVe(giaMoi.doubleValue());
                        veMoi.setTrangThai("DA_BAN");

                        em.persist(veMoi);
                        danhSachVeMoi.add(veMoi);

                        ChiTietHoaDon cthdDuong = new ChiTietHoaDon();
                        cthdDuong.setMaHD(maHD);
                        cthdDuong.setMaVe(maVeMoi);
                        cthdDuong.setSoLuong(1);
                        cthdDuong.setDonGia(giaMoi.doubleValue());
                        cthdDuong.setHoaDon(hoaDon);
                        cthdDuong.setVe(veMoi);
                        em.persist(cthdDuong);
                    }
                }

                System.out.println("[DoiVeService] Đổi vé thành công. Mã HD: " + maHD);
                return KetQuaDoiVe.thanhCong(maHD, danhSachVeMoi, BigDecimal.valueOf(tongChenhLech));
            });
        } catch (Exception e) {
            System.err.println("[DoiVeService] Lỗi khi đổi vé: " + e.getMessage());
            e.printStackTrace();
            return KetQuaDoiVe.thatBai(e.getMessage());
        }
    }
}
