package JPA_Project.service;

import JPA_Project.db.Tx;
import JPA_Project.entity.*;
import JPA_Project.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TraCuuService - Service xử lý tra cứu vé và hóa đơn.
 */
public class TraCuuService {

    private final VeRepository veRepo;
    private final HoaDonRepository hoaDonRepo;
    private final KhachHangRepository khachHangRepo;
    private final ChuyenTauRepository chuyenTauRepo;
    private final ChiTietHoaDonRepository chiTietHoaDonRepo;

    public TraCuuService() {
        this.veRepo = new VeRepository();
        this.hoaDonRepo = new HoaDonRepository();
        this.khachHangRepo = new KhachHangRepository();
        this.chuyenTauRepo = new ChuyenTauRepository();
        this.chiTietHoaDonRepo = new ChiTietHoaDonRepository();
    }

    /**
     * Tìm vé theo mã vé.
     */
    public Optional<Ve> timVeTheoMa(String maVe) {
        return veRepo.findByMaVe(maVe);
    }

    /**
     * Tìm hóa đơn theo mã.
     */
    public Optional<HoaDon> timHoaDonTheoMa(String maHD) {
        return Optional.ofNullable(hoaDonRepo.findByMaHD(maHD));
    }

    /**
     * Tìm hóa đơn theo ngày lập.
     */
    public List<HoaDon> timHoaDonTheoNgay(LocalDate ngay) {
        return hoaDonRepo.findByNgayLap(ngay);
    }

    /**
     * Tìm hóa đơn theo mã nhân viên lập.
     */
    public List<HoaDon> timHoaDonTheoMaNV(String maNV) {
        return hoaDonRepo.findByMaNVLap(maNV);
    }

    /**
     * Tìm hóa đơn theo mã khách hàng.
     */
    public List<HoaDon> timHoaDonTheoMaKH(String maKH) {
        return hoaDonRepo.findByMaKhachHang(maKH);
    }

    /**
     * Tìm vé theo mã khách hàng.
     */
    public List<Ve> timVeTheoMaKH(String maKH) {
        return veRepo.findByMaKhachHang(maKH);
    }

    /**
     * Tìm vé theo CCCD khách hàng.
     */
    public Optional<KhachHang> timKhachHangTheoCCCD(String cccd) {
        return Optional.ofNullable(khachHangRepo.findByCccd(cccd));
    }

    /**
     * Lấy chi tiết hóa đơn.
     */
    public List<ChiTietHoaDon> layChiTietHoaDon(String maHD) {
        return chiTietHoaDonRepo.findByMaHD(maHD);
    }

    /**
     * Tra cứu vé nâng cao.
     */
    public List<Ve> traCuuVeNangCao(String maVe, String cccd, LocalDate ngayDi, String gaDi, String gaDen) {
        List<Ve> ketQua = new ArrayList<>();

        if (maVe != null && !maVe.isBlank()) {
            Optional<Ve> ve = veRepo.findByMaVe(maVe);
            ve.ifPresent(ketQua::add);
            return ketQua;
        }

        if (cccd != null && !cccd.isBlank()) {
            Optional<KhachHang> kh = timKhachHangTheoCCCD(cccd);
            if (kh.isPresent()) {
                ketQua.addAll(veRepo.findByMaKhachHang(kh.get().getMaKH()));
            }
            return ketQua;
        }

        if (ngayDi != null) {
            List<ChuyenTau> chuyenTau = chuyenTauRepo.findByNgayKhoiHanh(ngayDi);
            for (ChuyenTau ct : chuyenTau) {
                ketQua.addAll(veRepo.findByMaChuyenTau(ct.getMaChuyenTau()));
            }
        }

        return ketQua;
    }

    /**
     * DTO cho kết quả tra cứu hóa đơn.
     */
    public static class HoaDonTraCuuDTO {
        public String maHD;
        public String ngayLap;
        public String tenNV;
        public String tenKH;
        public double tongTien;
        public String phuongThuc;
        public int soVe;
        public String trangThai;

        public HoaDonTraCuuDTO(HoaDon hd, String tenNV, String tenKH, int soVe) {
            this.maHD = hd.getMaHD();
            this.ngayLap = hd.getNgayLap() != null ? hd.getNgayLap().toString() : "";
            this.tenNV = tenNV;
            this.tenKH = tenKH;
            this.tongTien = hd.getTongTien();
            this.phuongThuc = hd.getPhuongThuc();
            this.soVe = soVe;
            this.trangThai = hd.getLoaiHoaDon();
        }
    }

    /**
     * DTO cho kết quả tra cứu vé.
     */
    public static class VeTraCuuDTO {
        public String maVe;
        public String maChuyenTau;
        public String tuyen;
        public String ngayDi;
        public String gioDi;
        public String tenKH;
        public String maCho;
        public String toa;
        public double giaVe;
        public String trangThai;

        public VeTraCuuDTO(Ve ve, ChuyenTau ct, KhachHang kh) {
            this.maVe = ve.getMaVe();
            this.maChuyenTau = ct != null ? ct.getMaChuyenTau() : "";
            if (ct != null) {
                String gaDi = ct.getGaDi() != null ? ct.getGaDi().getTenGa() : "";
                String gaDen = ct.getGaDen() != null ? ct.getGaDen().getTenGa() : "";
                this.tuyen = gaDi + " - " + gaDen;
                this.ngayDi = ct.getNgayKhoiHanh() != null ? ct.getNgayKhoiHanh().toString() : "";
                this.gioDi = ct.getGioKhoiHanh() != null ? ct.getGioKhoiHanh().toString() : "";
            } else {
                this.tuyen = "";
                this.ngayDi = "";
                this.gioDi = "";
            }
            this.tenKH = kh != null ? kh.getHoTen() : "";
            this.maCho = ve.getMaChoDat();
            this.giaVe = ve.getGiaVe();
            this.trangThai = ve.getTrangThai();
            this.toa = ve.getMaChoDat() != null ? ve.getMaChoDat().split("-")[0] : "";
        }
    }
}
