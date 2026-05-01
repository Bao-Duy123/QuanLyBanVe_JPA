package JDBC_Project.service;

import dao.*;
import entity.*;
import java.sql.SQLException;
import java.util.List;

public class NghiepVuTinhGiaVe {
    // Các hằng số cấu hình
    private static final long DON_GIA_MOI_KM = 1000;

    private static final GaTrongTuyenDao gaTrongTuyenDao = new GaTrongTuyenDao();
    private static final ToaDAO toaDao = new ToaDAO();
    private static final LoaiToaDAO loaiChoDatDao = new LoaiToaDAO();
    private static final LoaiVeDAO loaiVeDao = new LoaiVeDAO();
    private static final TuyenDao tuyenDao = new TuyenDao();

    /**
     * Hàm tính giá vé tổng thể
     */
    public static long tinhGiaVe(String maGaDi, String maGaDen, String maChuyenTau, ChoDat cho, String maLoaiVe) throws Exception {
        // 1. Lấy mã tuyến từ mã chuyến (VD: SE1_251218 -> SE1)
        String maTuyen = maChuyenTau.split("_")[0];

        // 2. Tính khoảng cách
        int khoangCachKm = 0;
        try {
            khoangCachKm = gaTrongTuyenDao.tinhKhoangCachGiuaHaiGa(maTuyen, maGaDi, maGaDen);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi tính khoảng cách: " + e.getMessage());
        }
        // lấy đơn gia theo km từ tuyến dao
        int donGiaTheoKm = tuyenDao.layGiaDonGia(maTuyen);

        // 3. Tính giá cơ bản
        long basePrice = khoangCachKm * donGiaTheoKm;

        // 4. Lấy hệ số toa (Loại chỗ: Ghế mềm, Giường nằm...)
        double heSoToa = layHeSoToa(cho.getMaToa(), maChuyenTau);

        // 5. Lấy hệ số loại vé (Đối tượng: Sinh viên, Trẻ em...)
        double heSoLoaiVe = loaiVeDao.getHeSoByMaLoaiVe(maLoaiVe);

        // 6. Công thức cuối cùng
        double finalPrice = basePrice * heSoToa * heSoLoaiVe;

        return roundUpToNextTen(Math.round(finalPrice));
    }

    private static double layHeSoToa(String maToa, String maChuyenTau) throws Exception {
        ChuyenTauDao ctDao = new ChuyenTauDao();
        ChuyenTau ct = ctDao.layChuyenTauBangMa(maChuyenTau);
        if (ct == null) throw new Exception("Không tìm thấy thông tin chuyến tàu.");

        List<Toa> toas = toaDao.layToaTheoMaTau(ct.getMaTau());
        String loaiToa = null;
        for (Toa t : toas) {
            if (t.getMaToa().equals(maToa)) {
                loaiToa = t.getLoaiToa();
                break;
            }
        }

        if (loaiToa == null) throw new Exception("Không xác định được loại toa.");
        return loaiChoDatDao.getLoaiToaByMa(loaiToa).getHeSo();
    }

    private static long roundUpToNextTen(long value) {
        return ((value + 9) / 10) * 10;
    }
}
