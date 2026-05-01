package JPA_Project.service;

import JPA_Project.entity.KhuyenMai;
import JPA_Project.repository.KhuyenMaiRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class KhuyenMaiService {
    private final KhuyenMaiRepository khuyenMaiRepository;

    public KhuyenMaiService() {
        this.khuyenMaiRepository = new KhuyenMaiRepository();
    }

    public KhuyenMaiService(KhuyenMaiRepository khuyenMaiRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
    }

    public List<KhuyenMai> getAllKhuyenMai() {
        return khuyenMaiRepository.findAll();
    }

    public Optional<KhuyenMai> getKhuyenMaiById(String maKM) {
        return khuyenMaiRepository.findByMaKM(maKM);
    }

    public List<KhuyenMai> getKhuyenMaiConHieuLuc() {
        return khuyenMaiRepository.findConHieuLuc(LocalDateTime.now());
    }

    public KhuyenMai save(KhuyenMai khuyenMai) {
        khuyenMaiRepository.save(khuyenMai);
        return khuyenMai;
    }

    public void delete(KhuyenMai khuyenMai) {
        khuyenMaiRepository.delete(khuyenMai);
    }

    public KhuyenMai taoKhuyenMai(String maKM, String tenKM, String loaiKM,
                                   BigDecimal giaTriGiam,
                                   LocalDateTime ngayBD,
                                   LocalDateTime ngayKT) {
        KhuyenMai km = new KhuyenMai();
        km.setMaKM(maKM);
        km.setTenKM(tenKM);
        km.setLoaiKM(loaiKM);
        km.setGiaTriGiam(giaTriGiam);
        km.setNgayBatDau(ngayBD);
        km.setNgayKetThuc(ngayKT);
        km.setTrangThai("HOAT_DONG");

        return save(km);
    }

    public boolean kiemTraMaKhuyenMai(String maKM) {
        return khuyenMaiRepository.findByMaKM(maKM).isPresent();
    }

    public void capNhatTrangThai(String maKM, String trangThai) {
        Optional<KhuyenMai> optKM = khuyenMaiRepository.findByMaKM(maKM);
        if (optKM.isPresent()) {
            KhuyenMai km = optKM.get();
            km.setTrangThai(trangThai);
            save(km);
        }
    }
}
