package JDBC_Project.control;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.geom.RoundRectangle2D;

public class VeSoDoTau extends JPanel {

    private String maTuyen;
    private String thoiGianDi;
    private String thoiGianDen;

    // Kích thước cố định cho hình dáng đồ họa (Đã giảm)
    private static final int GOC_BO_TRON = 15;
    private static final int CHIEU_CAO_THAN = 100;
    private static final int CHIEU_RONG_THAN = 130;
    private static final Color MAU_XANH_TAU = new Color(0, 153, 204);

    public VeSoDoTau(String maTuyen, String thoiGianDi, String thoiGianDen) {
        this.maTuyen = maTuyen;
        this.thoiGianDi = thoiGianDi;
        this.thoiGianDen = thoiGianDen;

        // Thiết lập kích thước Panel chính
        this.setPreferredSize(new Dimension(CHIEU_RONG_THAN, CHIEU_CAO_THAN + 30));
        this.setOpaque(false); // Nền trong suốt để thấy đồ họa vẽ

        // --- Cấu hình Nội dung (JPanel màu trắng nằm bên trong) ---
        JPanel panelNoiDung = taoPanelNoiDung();

        // Sử dụng FlowLayout để căn chỉnh Panel nội dung vào giữa thân tàu
        // Giá trị 25px là khoảng cách từ đỉnh của JPanel chính đến Panel nội dung.
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 25));
        this.add(panelNoiDung);
    }

    /**
     * Tạo Panel chứa thông tin văn bản (phần màu trắng).
     */
    private JPanel taoPanelNoiDung() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);

        // Kích thước của phần nội dung trắng
        contentPanel.setPreferredSize(new Dimension(CHIEU_RONG_THAN - 20, CHIEU_CAO_THAN - 40));

        // Thêm padding bên trong
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridx = 0;

        // --- Hàng 0: Tiêu đề "TG đi:" ---
        JLabel tieuDeDi = new JLabel("TG đi:");
        tieuDeDi.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(tieuDeDi, gbc);

        // --- Hàng 1: Ngày/Giờ đi ---
        JLabel giaTriDi = new JLabel(thoiGianDi);
        giaTriDi.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(giaTriDi, gbc);

        // --- Khoảng cách ---
        gbc.gridy = 2;
        contentPanel.add(Box.createVerticalStrut(3), gbc);

        // --- Hàng 3: Tiêu đề "TG đến:" ---
        JLabel tieuDeDen = new JLabel("TG đến:");
        tieuDeDen.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(tieuDeDen, gbc);

        // --- Hàng 4: Ngày/Giờ đến ---
        JLabel giaTriDen = new JLabel(thoiGianDen);
        giaTriDen.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(giaTriDen, gbc);

        return contentPanel;
    }

    /**
     * Phương thức chịu trách nhiệm vẽ đồ họa (Thân tàu, bánh xe, đường ray).
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int xStart = 0;
        int yStart = 5;

        // 1. Vẽ Thân Tàu (Màu xanh, hình chữ nhật bo tròn)
        RoundRectangle2D thanTau = new RoundRectangle2D.Double(
                xStart, yStart, CHIEU_RONG_THAN, CHIEU_CAO_THAN, GOC_BO_TRON, GOC_BO_TRON);

        g2d.setColor(MAU_XANH_TAU);
        g2d.fill(thanTau);

        // 2. Vẽ Phần Hiển thị Thông tin (Nền trắng bo tròn bên trong)
        RoundRectangle2D khuVucThongTin = new RoundRectangle2D.Double(
                xStart + 10, yStart + 30, CHIEU_RONG_THAN - 20, CHIEU_CAO_THAN - 45, GOC_BO_TRON, GOC_BO_TRON);

        g2d.setColor(Color.WHITE);
        g2d.fill(khuVucThongTin);

        // 3. Vẽ Phần Mã Tàu ("SE8" trên nền xanh)
        g2d.setColor(MAU_XANH_TAU);
        g2d.fillRoundRect(xStart + 5, yStart, 40, 25, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(maTuyen, xStart + 8, yStart + 18);

        // 4. Vẽ Bánh Xe (Màu xám đậm)
        int kichThuocBanhXe = 20;
        int viTriBanhXeY = yStart + CHIEU_CAO_THAN - kichThuocBanhXe / 2;

        g2d.setColor(Color.DARK_GRAY); // Chỉnh lại màu xám đậm cho bánh xe
        // Bánh xe 1
        g2d.fillOval(xStart + CHIEU_RONG_THAN / 4 - kichThuocBanhXe / 2, viTriBanhXeY, kichThuocBanhXe, kichThuocBanhXe);
        // Bánh xe 2
        g2d.fillOval(xStart + (int)(CHIEU_RONG_THAN * 0.75) - kichThuocBanhXe / 2, viTriBanhXeY, kichThuocBanhXe, kichThuocBanhXe);

        // 5. Vẽ Đường Viền Dưới Dày (Màu đen)
        int doDayVien = 6;
        int viTriVienY = viTriBanhXeY - (doDayVien / 2); // Nằm ngay trên tâm bánh xe

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(doDayVien));
        g2d.drawLine(xStart, viTriVienY, xStart + CHIEU_RONG_THAN, viTriVienY);

        // 6. Vẽ Đường Ray (Màu đen)
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(4));
        int viTriRayY = yStart + CHIEU_CAO_THAN + 10;

        // Ray dọc 1
        g2d.drawLine(xStart, viTriRayY, xStart + CHIEU_RONG_THAN, viTriRayY);
        // Ray dọc 2 (hơi chéo xuống)
        g2d.drawLine(xStart + 10, viTriRayY + 5, xStart + CHIEU_RONG_THAN - 10, viTriRayY + 5);

        // Thanh ngang
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < 3; i++) {
            int viTriThanhNgangX = xStart + 10 + i * 40;
            g2d.drawLine(viTriThanhNgangX, viTriRayY, viTriThanhNgangX + 10, viTriRayY + 5);
        }

        g2d.dispose();
    }


     /**
      * Phương thức Main để chạy Demo (Để kiểm tra trong lớp VeSoDoTau)
      */
     public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
             JFrame frame = new JFrame("Demo Vẽ Sơ Đồ Tàu");
             frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             frame.setLayout(new FlowLayout());
//
             VeSoDoTau ticket = new VeSoDoTau("SE8", "18/12/2025 06:00", "19/12/2025 16:10");
             VeSoDoTau ticket2 = new VeSoDoTau("SE2", "18/12/2025 09:00", "19/12/2025 21:10");
//
             frame.add(ticket);
             frame.add(ticket2);
             frame.pack();
             frame.setLocationRelativeTo(null);
             frame.setVisible(true);
         });
     }
}
