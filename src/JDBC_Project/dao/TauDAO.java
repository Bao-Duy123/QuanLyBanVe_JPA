package JDBC_Project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.ConnectDB;
import entity.ChuyenTau;
import entity.Tau; // Đảm bảo bạn đã import entity.Tau


public class TauDAO {

    private final ArrayList<Tau> danhSachTau;

    /**
     * Lấy tất cả các tàu có trong CSDL.
     * @return List<Tau> danh sách các tàu.
     */
    public List<Tau> layTatCa() {
        List<Tau> danhSachTau = new ArrayList<>();
        String sql = "SELECT SoHieu, TrangThai FROM Tau";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String soHieu = rs.getString("SoHieu");
                String trangThai = rs.getString("TrangThai");

                Tau tau = new Tau(soHieu, trangThai);
                danhSachTau.add(tau);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả Tàu: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachTau;
    }

    /**
     * Tìm tàu theo ID (int).
     * @param id ID (int) của tàu.
     * @return null - Không thể hoàn thiện.
     */
    Tau timTheoId(int id) {
        // TODO: Không thể hoàn thiện hàm này.
        // Bảng [Tau] trong CSDL (QuanLyVeTauTest2) của bạn không có cột [id] (int).
        // Khóa chính là [SoHieu] (varchar/varchar(10)).
        System.err.println("LỖI DAO: Bảng Tau không có cột 'id'. Phương thức timTheoId(int id) không thể thực thi.");
        return null;
    }

    /**
     * Tìm tàu theo Mã Tàu (Số Hiệu).
     * Hàm này (non-static) gọi hàm static getTauById đã có.
     * @param maTau Mã tàu (Số Hiệu) cần tìm.
     * @return Đối tượng Tau nếu tìm thấy, ngược lại là null.
     */
    Tau timTheoMa(String maTau) {
        // Gọi phương thức static getTauById đã có sẵn trong file của bạn
        return TauDAO.getTauById(maTau);
    }

    /**
     * Cập nhật thông tin một đối tượng Tàu (chủ yếu là trạng thái).
     * @param tau Đối tượng Tàu chứa thông tin cần cập nhật.
     */
    void capNhat(Tau tau) {
        if (tau == null || tau.getSoHieu() == null) {
            System.err.println("Lỗi: Không thể cập nhật Tàu với thông tin null.");
            return;
        }

        String sql = "UPDATE Tau SET TrangThai = ? WHERE SoHieu = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, tau.getTrangThai());
            pstmt.setString(2, tau.getSoHieu());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật Tàu " + tau.getSoHieu() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Constructor (Giữ nguyên)
    public TauDAO() {
        danhSachTau = new ArrayList<Tau>();
    }

    /**
     * Lấy thông tin chi tiết về một đối tượng Tau dựa trên Mã Tàu.
     * @param maTau Mã số hiệu tàu (varchar(10) trong CSDL).
     * @return Đối tượng Tau nếu tìm thấy, ngược lại là null.
     */
    public static Tau getTauById(String maTau) {
        Tau tau = null;

        // Câu truy vấn: Lấy tất cả thông tin của tàu dựa trên số hiệu (MaTau)
        String sql = "SELECT SoHieu, TrangThai FROM Tau WHERE SoHieu = ?";

        // Sử dụng try-with-resources để đảm bảo Connection và PreparedStatement được đóng
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            // Đặt tham số cho truy vấn
            pstmt.setString(1, maTau);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String soHieu = rs.getString("SoHieu");
                    String trangThai = rs.getString("TrangThai");

                    // Giả định entity.Tau có constructor (String, String)
                    // Dựa trên CSDL QuanLyVeTauTest2
                    tau = new Tau(soHieu, trangThai);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm thông tin Tàu theo Mã " + maTau + ": " + e.getMessage());
            e.printStackTrace();
            // Trả về null nếu có lỗi CSDL
            return null;
        }
        return tau;
    }

    /**
     * Lấy danh sách Số Hiệu Tàu từ CSDL.
     * @return Danh sách các chuỗi SoHieu.
     * @throws SQLException Nếu có lỗi xảy ra khi truy vấn CSDL.
     */
    public static List<String> layDanhSachMaTau() throws SQLException {
        List<String> danhSachMaTau = new ArrayList<>();
        // Giả sử ConnectDB là lớp quản lý kết nối của bạn
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT SoHieu FROM Tau");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                danhSachMaTau.add(rs.getString("SoHieu"));
            }
        }
        // Lưu ý: Đảm bảo ConnectDB.getConnection() trả về một Connection mới hoặc
        // quản lý việc đóng/mở Connection hiệu quả. Dùng try-with-resources sẽ tự đóng PreparedStatement và ResultSet.
        return danhSachMaTau;
    }

}