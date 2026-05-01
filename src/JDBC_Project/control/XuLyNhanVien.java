package JDBC_Project.control;

import dao.NhanVienDao;
import entity.NhanVien;

public class XuLyNhanVien {
    NhanVienDao nhanVienDAO = new NhanVienDao();

    public static NhanVien layThongTinNhanVienChoCaLamViec(String maNV) {
        // Gọi DAO để truy vấn CSDL
        return NhanVienDao.getNhanVienByMaNV(maNV);
    }
    public static String layEmailTheoMa(String maNV) {
        return NhanVienDao.layEmailTheoMa(maNV);
    }
}


// --- Ví dụ tại màn hình đăng nhập ---
// TaiKhoan taiKhoan = XuLyTaiKhoan.authenticate(tenDangNhap, matKhau);
// if (taiKhoan != null) {
//     String maNV = taiKhoan.getMaNV();
//
//     XuLyNhanVien xuLy = new XuLyNhanVien();
//     NhanVien nv = xuLy.layThongTinNhanVienChoCaLamViec(maNV);
//
//     if (nv != null) {
//         CaLamViec.getInstance().batDauCa(nv);
//         // ... chuyển màn hình ...
//     }
// }