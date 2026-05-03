package JPA_Project.gui;

import JPA_Project.entity.Ve;
import JPA_Project.entity.KhachHang;
import JPA_Project.entity.ChuyenTau;
import JPA_Project.entity.ChoDat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * PopUpVeTauJPA - Popup hiển thị thông tin vé tàu với mã QR.
 */
public class PopUpVeTauJPA extends JPanel {

    private Ve ve;
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final int QR_SIZE = 180;

    public PopUpVeTauJPA(Ve ve) {
        this.ve = ve;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);

        JPanel mainContent = new JPanel(new GridLayout(1, 2, 10, 0));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainContent.add(createTicketInfoPanel());
        mainContent.add(createQRCodePanel());

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createTicketInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createTitleLabel("Thông tin hành trình"));
        panel.add(createLine("Ga đi - Ga đến:", getHanhTrinh()));
        panel.add(createLine("Tàu:", getMaTau()));
        panel.add(createLine("Ngày đi:", getNgayKhoiHanh()));
        panel.add(createLine("Giờ đi:", getGioKhoiHanh()));
        panel.add(createLine("Toa:", getMaToa()));
        panel.add(createLine("Chỗ:", getSoCho() + " (" + getLoaiGhe() + ")"));

        panel.add(Box.createVerticalStrut(15));

        panel.add(createTitleLabel("Thông tin hành khách"));
        panel.add(createLine("Họ tên:", getHoTenKhachHang()));
        panel.add(createLine("Giấy tờ:", getCCCD()));
        panel.add(createLine("Loại vé:", getTenLoaiVe()));

        panel.add(Box.createVerticalStrut(15));

        panel.add(createTitleLabel("Giá vé: " + formatVnd(ve.getGiaVe())));
        panel.add(createLineNote("(Giá vé trên đã có bảo hiểm, dịch vụ đi kèm và thuế GTGT)"));

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createQRCodePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

        JLabel qrLabel = new JLabel();
        qrLabel.setPreferredSize(new Dimension(180, 180));
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String maVeData = ve.getMaVe();
        try {
            java.awt.image.BufferedImage qrImage = JPA_Project.service.QRCodeService.generateQRCodeImage(maVeData, QR_SIZE);
            ImageIcon icon = new ImageIcon(qrImage);
            qrLabel.setIcon(icon);
        } catch (Exception e) {
            qrLabel.setText("Lỗi tạo QR");
            qrLabel.setForeground(Color.RED);
            System.err.println("Lỗi tạo QR Code: " + e.getMessage());
        }

        panel.add(qrLabel, BorderLayout.NORTH);

        JPanel footerInfo = new JPanel(new GridLayout(3, 1));
        footerInfo.setOpaque(false);
        footerInfo.setBorder(new EmptyBorder(10, 0, 0, 0));

        footerInfo.add(createLine("Mã vé:", ve.getMaVe()));
        footerInfo.add(createLine("Đại lý bán vé:", "VNPay App"));

        panel.add(footerInfo, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createLine(String label, String value) {
        JLabel lbl = new JLabel("<html><b>" + label + "</b> " + value + "</html>");
        lbl.setFont(FONT_NORMAL);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel("<html><b>" + text + "</b></html>");
        lbl.setFont(FONT_BOLD);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel createLineNote(String note) {
        JLabel lbl = new JLabel("<html><i>" + note + "</i></html>");
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private String getHanhTrinh() {
        if (ve.getChuyenTau() != null) {
            ChuyenTau ct = ve.getChuyenTau();
            String gaDi = ct.getGaDi() != null ? ct.getGaDi().getTenGa() : "N/A";
            String gaDen = ct.getGaDen() != null ? ct.getGaDen().getTenGa() : "N/A";
            return gaDi + " - " + gaDen;
        }
        return "N/A";
    }

    private String getMaTau() {
        if (ve.getChuyenTau() != null && ve.getChuyenTau().getMaTau() != null) {
            return ve.getChuyenTau().getMaTau();
        }
        return "N/A";
    }

    private String getNgayKhoiHanh() {
        if (ve.getChuyenTau() != null && ve.getChuyenTau().getNgayKhoiHanh() != null) {
            return ve.getChuyenTau().getNgayKhoiHanh().toString();
        }
        return "N/A";
    }

    private String getGioKhoiHanh() {
        if (ve.getChuyenTau() != null && ve.getChuyenTau().getGioKhoiHanh() != null) {
            return ve.getChuyenTau().getGioKhoiHanh().toString();
        }
        return "N/A";
    }

    private String getMaToa() {
        if (ve.getChoDat() != null) {
            return ve.getChoDat().getMaToa();
        }
        return "N/A";
    }

    private String getSoCho() {
        if (ve.getChoDat() != null) {
            return ve.getChoDat().getSoCho();
        }
        return "N/A";
    }

    private String getLoaiGhe() {
        if (ve.getChoDat() != null) {
            return ve.getChoDat().getMaCho();
        }
        return "N/A";
    }

    private String getHoTenKhachHang() {
        if (ve.getKhachHang() != null) {
            return ve.getKhachHang().getHoTen();
        }
        return "N/A";
    }

    private String getCCCD() {
        if (ve.getKhachHang() != null) {
            return ve.getKhachHang().getSoCCCD();
        }
        return "N/A";
    }

    private String getTenLoaiVe() {
        if (ve.getLoaiVe() != null) {
            return ve.getLoaiVe().getTenLoai();
        }
        return "Người lớn";
    }

    private String formatVnd(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        return nf.format(amount);
    }
}
