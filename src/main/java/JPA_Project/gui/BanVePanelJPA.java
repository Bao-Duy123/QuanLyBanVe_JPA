package JPA_Project.gui;

import JPA_Project.dto.*;
import JPA_Project.entity.*;
import JPA_Project.service.BanVeService;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * BanVePanelJPA - Panel chính chứa workflow bán vé hoàn chỉnh.
 * Bao gồm cả ManHinhBanVeJPA và ManHinhXacNhanBanVeJPA trong CardLayout.
 * Hoạt động như một standalone component trong project JPA.
 * 
 * Luồng:
 * 1. ManHinhBanVeJPA - Chọn ghế, nhập thông tin khách
 * 2. ManHinhXacNhanBanVeJPA - Xác nhận thanh toán
 * 3. Quay về ManHinhBanVeJPA sau khi kết thúc
 */
public class BanVePanelJPA extends JPanel {

    // ================================================================================
    // CARD LAYOUT
    // ================================================================================
    private static final String CARD_BAN_VE = "BanVe";
    private static final String CARD_XAC_NHAN = "XacNhan";

    private CardLayout cardLayout;
    private JPanel cardContainer;

    // ================================================================================
    // SUB-PANELS
    // ================================================================================
    private ManHinhBanVeJPA manHinhBanVe;
    private ManHinhXacNhanBanVeJPA manHinhXacNhan;

    // ================================================================================
    // DATA
    // ================================================================================
    private List<TicketDTO> danhSachVeDaChon;
    private PassengerDTO khachHangDaiDien;
    private ChuyenTauDTO chuyenTauHienTai;
    private Map<String, PassengerDTO> thongTinKhachHang;

    // ================================================================================
    // CONSTRUCTOR
    // ================================================================================
    public BanVePanelJPA() {
        this(null);
    }

    public BanVePanelJPA(NhanVien nhanVien) {
        khoiTaoGiaoDien(nhanVien);
    }

    /**
     * Khởi tạo giao diện với CardLayout.
     */
    private void khoiTaoGiaoDien(NhanVien nhanVien) {
        setLayout(new BorderLayout());

        // CardLayout Container
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(Color.WHITE);

        // Khởi tạo các panel con
        manHinhBanVe = new ManHinhBanVeJPA(nhanVien);
        manHinhXacNhan = new ManHinhXacNhanBanVeJPA(null, null, null, null, nhanVien);

        // Thêm vào CardLayout
        cardContainer.add(manHinhBanVe, CARD_BAN_VE);
        cardContainer.add(manHinhXacNhan, CARD_XAC_NHAN);

        add(cardContainer, BorderLayout.CENTER);

        // Thiết lập callbacks
        thietLapCallbacks();

        // Hiển thị panel bán vé đầu tiên
        cardLayout.show(cardContainer, CARD_BAN_VE);
    }

    /**
     * Thiết lập các callbacks để chuyển đổi giữa các màn hình.
     */
    private void thietLapCallbacks() {
        // Callback khi nhấn "Tiếp theo" ở ManHinhBanVe
        manHinhBanVe.setOnTiepTheoCallback(danhSachVe -> {
            this.danhSachVeDaChon = danhSachVe;
            this.chuyenTauHienTai = manHinhBanVe.getChuyenTauHienTai();
            this.thongTinKhachHang = manHinhBanVe.getThongTinKhachHang();
            
            // Lấy khách hàng đại diện (người đầu tiên)
            if (danhSachVe != null && !danhSachVe.isEmpty()) {
                this.khachHangDaiDien = danhSachVe.get(0).khachHang();
            }

            // Chuyển sang màn hình xác nhận
            manHinhXacNhan.setData(
                    danhSachVe,
                    khachHangDaiDien,
                    chuyenTauHienTai,
                    thongTinKhachHang
            );
            cardLayout.show(cardContainer, CARD_XAC_NHAN);
        });

        // Callback khi nhấn "Quay lại" ở ManHinhXacNhan
        manHinhXacNhan.setOnQuayLai(() -> {
            cardLayout.show(cardContainer, CARD_BAN_VE);
        });

        // Callback khi nhấn "Kết thúc" hoặc "Hủy" ở ManHinhXacNhan
        manHinhXacNhan.setOnKetThuc(() -> {
            // Reset và quay về màn hình bán vé
            manHinhBanVe.resetAllData();
            manHinhXacNhan.reset();
            danhSachVeDaChon = null;
            khachHangDaiDien = null;
            chuyenTauHienTai = null;
            thongTinKhachHang = null;
            cardLayout.show(cardContainer, CARD_BAN_VE);
        });
    }

    // ================================================================================
    // PUBLIC METHODS
    // ================================================================================

    /**
     * Quay về màn hình bán vé.
     */
    public void quayVeManHinhBanVe() {
        cardLayout.show(cardContainer, CARD_BAN_VE);
    }

    /**
     * Chuyển sang màn hình xác nhận.
     */
    public void chuyenSangXacNhan() {
        cardLayout.show(cardContainer, CARD_XAC_NHAN);
    }

    /**
     * Reset tất cả dữ liệu.
     */
    public void resetAll() {
        manHinhBanVe.resetAllData();
        manHinhXacNhan.reset();
        danhSachVeDaChon = null;
        khachHangDaiDien = null;
        chuyenTauHienTai = null;
        thongTinKhachHang = null;
        cardLayout.show(cardContainer, CARD_BAN_VE);
    }

    /**
     * Lấy danh sách vé đã chọn.
     */
    public List<TicketDTO> getDanhSachVeDaChon() {
        return danhSachVeDaChon;
    }

    /**
     * Lấy chuyến tàu hiện tại.
     */
    public ChuyenTauDTO getChuyenTauHienTai() {
        return chuyenTauHienTai;
    }

    /**
     * Lấy thông tin khách hàng.
     */
    public Map<String, PassengerDTO> getThongTinKhachHang() {
        return thongTinKhachHang;
    }

    /**
     * Main method để test standalone.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Bán vé tàu - JPA Version");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);

            BanVePanelJPA panel = new BanVePanelJPA();
            frame.add(panel);

            frame.setVisible(true);
        });
    }
}
