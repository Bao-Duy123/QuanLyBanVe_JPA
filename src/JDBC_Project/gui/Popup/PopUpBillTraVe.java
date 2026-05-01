package JDBC_Project.gui.Popup;

import entity.Ve;
import entity.KhachHang;
import entity.ChuyenTau;
import entity.ChoDat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class PopUpBillTraVe extends JPanel {
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_VALUE = new Font("Segoe UI", Font.BOLD, 14);
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public PopUpBillTraVe(Ve ve, double tienHoanTra, String lyDo) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 25, 20, 25));
        setPreferredSize(new Dimension(500, 650));

        // 1. Tiêu đề hóa đơn
        JPanel pTitle = new JPanel(new GridLayout(2, 1));
        pTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("HÓA ĐƠN TRẢ VÉ TÀU", JLabel.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(new Color(0, 102, 204));

        JLabel lblMaHD = new JLabel("Mã vé: " + ve.getMaVe(), JLabel.CENTER);
        lblMaHD.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        pTitle.add(lblTitle);
        pTitle.add(lblMaHD);
        add(pTitle, BorderLayout.NORTH);

        // 2. Nội dung chi tiết
        JPanel pContent = new JPanel();
        pContent.setLayout(new BoxLayout(pContent, BoxLayout.Y_AXIS));
        pContent.setOpaque(false);
        pContent.add(Box.createVerticalStrut(20));

        // Lấy thông tin từ Entity
        KhachHang kh = ve.getKhachHangChiTiet();
        ChuyenTau ct = ve.getChuyenTauChiTiet();
        ChoDat cd = ve.getChoDatChiTiet();

        // Phần thông tin chung
        addInfoRow(pContent, "Khách hàng:", ve.getTenKhachHang());
        addInfoRow(pContent, "Số CMND/CCCD:", (kh != null) ? kh.getSoCCCD() : "---");
        addInfoRow(pContent, "Số điện thoại:", (kh != null) ? kh.getSdt() : "---");
        addInfoRow(pContent, "Người thực hiện:", "Trần Nam Sơn"); // Tên nhân viên đăng nhập

        pContent.add(new JSeparator());
        pContent.add(Box.createVerticalStrut(10));

        // Phần thông tin vé
        JLabel lblSection1 = new JLabel("THÔNG TIN VÉ");
        lblSection1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pContent.add(lblSection1);
        pContent.add(Box.createVerticalStrut(10));

        addInfoRow(pContent, "Tuyến đường:", (ct != null) ? ct.getGaDi().getTenGa() + " - " + ct.getGaDen().getTenGa() : "---");
        addInfoRow(pContent, "Thời gian khởi hành:", (ct != null) ? ct.getNgayKhoiHanh() + " " + ct.getGioKhoiHanh() : "---");
        addInfoRow(pContent, "Mã tàu:", (ct != null) ? ct.getMaTau() : "---");
        addInfoRow(pContent, "Toa:", (cd != null) ? cd.getMaToa() : "---");
        addInfoRow(pContent, "Số ghế:", (cd != null) ? cd.getSoCho() : "---");

        pContent.add(new JSeparator());
        pContent.add(Box.createVerticalStrut(10));

        // Phần chi tiết thanh toán
        JLabel lblSection2 = new JLabel("CHI TIẾT THANH TOÁN");
        lblSection2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pContent.add(lblSection2);
        pContent.add(Box.createVerticalStrut(10));

        double giaGoc = ve.getGiaVe();
        double phiHoan = giaGoc - tienHoanTra;

        addInfoRow(pContent, "Giá vé gốc:", df.format(giaGoc));
        addInfoRow(pContent, "Phí hoàn trả:", df.format(phiHoan));
        addInfoRow(pContent, "Số tiền hoàn trả:", df.format(tienHoanTra), Color.RED);
        addInfoRow(pContent, "Lý do trả:", lyDo);

        add(pContent, BorderLayout.CENTER);

        // 3. Chân trang (Lời cảm ơn/Ký tên)
        JPanel pFooter = new JPanel(new BorderLayout());
        pFooter.setOpaque(false);
        JLabel lblLoiCamOn = new JLabel("<html><i>Cảm ơn quý khách, hẹn gặp lại!</i></html>", JLabel.CENTER);
        pFooter.add(lblLoiCamOn, BorderLayout.SOUTH);
        add(pFooter, BorderLayout.SOUTH);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        addInfoRow(panel, label, value, Color.BLACK);
    }

    private void addInfoRow(JPanel panel, String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(FONT_LABEL);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(FONT_VALUE);
        lblValue.setForeground(valueColor);

        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        panel.add(row);
        panel.add(Box.createVerticalStrut(5));
    }
}