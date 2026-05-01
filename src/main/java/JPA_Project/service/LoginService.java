package JPA_Project.service;

import JPA_Project.entity.KhuyenMai;
import JPA_Project.entity.NhanVien;
import JPA_Project.entity.TaiKhoan;
import JPA_Project.repository.NhanVienRepository;
import JPA_Project.repository.TaiKhoanRepository;

import java.util.List;
import java.util.Optional;

public class LoginService {
    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public LoginService() {
        this.nhanVienRepository = new NhanVienRepository();
        this.taiKhoanRepository = new TaiKhoanRepository();
    }

    public LoginService(NhanVienRepository nhanVienRepository, TaiKhoanRepository taiKhoanRepository) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public Optional<NhanVien> dangNhap(String tenDangNhap, String matKhau) {
        Optional<TaiKhoan> taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap);
        
        if (taiKhoan.isEmpty()) {
            return Optional.empty();
        }
        
        TaiKhoan tk = taiKhoan.get();
        if (!tk.getMatKhau().equals(matKhau)) {
            return Optional.empty();
        }
        
        return nhanVienRepository.findByMaNV(tk.getMaNV());
    }

    public boolean doiMatKhau(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        Optional<TaiKhoan> taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap);
        
        if (taiKhoan.isEmpty()) {
            return false;
        }
        
        TaiKhoan tk = taiKhoan.get();
        if (!tk.getMatKhau().equals(matKhauCu)) {
            return false;
        }
        
        tk.setMatKhau(matKhauMoi);
        taiKhoanRepository.save(tk);
        return true;
    }
    //lấy thông tin tất cả tài khoản
    public Optional<TaiKhoan> getTaiKhoanByTenDangNhap(String tenDangNhap) {
        return taiKhoanRepository.findByTenDangNhap(tenDangNhap);
    }

    public static void main(String[] args) {
        //test nhanh lấy toàn bộ tài khoản trong csdl
        LoginService service = new LoginService();
        List<TaiKhoan> allKM = service.taiKhoanRepository.findAll();
        System.out.println("Danh sách khuyến mãi:");
        for (TaiKhoan lv : allKM) {
            System.out.println(lv.getTenDangNhap() + " - " + lv.getMatKhau());
        }

    }
}
