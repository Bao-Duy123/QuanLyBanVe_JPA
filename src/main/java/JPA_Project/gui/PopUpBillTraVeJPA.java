package JPA_Project.gui;

import JPA_Project.entity.Ve;
import JPA_Project.entity.KhachHang;
import JPA_Project.entity.ChuyenTau;
import JPA_Project.entity.ChoDat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * PopUpBillTraVeJPA - Popup hiển thị hóa đơn trả vé tàu.
 */
public class PopUpBillTraVeJPA extends JPanel {

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_VALUE = new Font("Segoe UI", Font.BOLD, 14);
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public PopUpBillTraVeJPA(Ve ve, double tienHoanTra, String lyDo) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 25, 20, 25));
        setPreferredSize(new Dimension(500, 650));

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

        JPanel pContent = new JPanel();
        pContent.setLayout(new BoxLayout(pContent, BoxLayout.Y_AXIS));
        pContent.setOpaque(false);
        pContent.add(Box.createVerticalStrut(20));

        KhachHang kh = ve.getKhachHang();
        ChuyenTau ct = ve.getChuyenTau();
        ChoDat cd = ve.getChoDat();

        String tenNV = JPA_Project.service.CaLamViec.getInstance().getNhanVienDangNhap() != null 
                ? JPA_Project.service.CaLamViec.getInstance().getNhanVienDangNhap().getHoTen() 
                : "Nhân viên";

        addInfoRow(pContent, "Khách hàng:", getHoTenKhachHang(ve));
        addInfoRow(pContent, "Số CMND/CCCD:", kh != null ? kh.getSoCCCD() : "---");
        addInfoRow(pContent, "Số điện thoại:", kh != null ? kh.getSdt() : "---");
        addInfoRow(pContent, "Người thực hiện:", tenNV);

        pContent.add(new JSeparator());
        pContent.add(Box.createVerticalStrut(10));

        JLabel lblSection1 = new JLabel("THÔNG TIN VÉ");
        lblSection1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pContent.add(lblSection1);
        pContent.add(Box.createVerticalStrut(10));

        addInfoRow(pContent, "Tuyến đường:", getHanhTrinh(ct));
        addInfoRow(pContent, "Thời gian khởi hành:", getThoiGianKhoiHanh(ct));
        addInfoRow(pContent, "Mã tàu:", ct != null ? ct.getMaTau() : "---");
        addInfoRow(pContent, "Toa:", cd != null ? cd.getMaToa() : "---");
        addInfoRow(pContent, "Số ghế:", cd != null ? cd.getSoCho() : "---");

        pContent.add(new JSeparator());
        pContent.add(Box.createVerticalStrut(10));

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

    private String getHoTenKhachHang(Ve ve) {
        if (ve.getKhachHang() != null) {
            return ve.getKhachHang().getHoTen();
        }
        return "Khách hàng";
    }

    private String getHanhTrinh(ChuyenTau ct) {
        if (ct == null) return "---";
        String gaDi = ct.getGaDi() != null ? ct.getGaDi().getTenGa() : "";
        String gaDen = ct.getGaDen() != null ? ct.getGaDen().getTenGa() : "";
        return gaDi + " - " + gaDen;
    }

    private String getThoiGianKhoiHanh(ChuyenTau ct) {
        if (ct == null) return "---";
        String ngay = ct.getNgayKhoiHanh() != null ? ct.getNgayKhoiHanh().toString() : "";
        String gio = ct.getGioKhoiHanh() != null ? ct.getGioKhoiHanh().toString() : "";
        return ngay + " " + gio;
    }
}
