package JPA_Project.gui;

import JPA_Project.entity.*;
import JPA_Project.repository.ChiTietHoaDonRepository;
import JPA_Project.repository.HoaDonRepository;
import JPA_Project.repository.NhanVienRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * PopUpChiTietHoaDonJPA - Popup hiển thị chi tiết hóa đơn.
 */
public class PopUpChiTietHoaDonJPA extends JPanel {

    private String maHoaDon;
    private HoaDonRepository hoaDonRepo = new HoaDonRepository();
    private ChiTietHoaDonRepository cthdRepo = new ChiTietHoaDonRepository();
    private NhanVienRepository nvRepo = new NhanVienRepository();
    private DecimalFormat dfMoney = new DecimalFormat("#,###");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    private JTable tblHanhKhach;
    private JButton btnDong, btnIn;
    private JLabel lblMaHD, lblMaChuyen, lblGaDi, lblGaDen, lblNgayDi, lblGioDi, lblNgayDen, lblGioDen;
    private JLabel lblSoHieuTau, lblTenKH, lblSDT, lblGioiTinh;
    private JLabel lblNgayLap, lblTongTien, lblPhuongThuc, lblTenNV, lblSoTien;
    private JLabel lblKhuyenMai, lblNoiDungKM;
    private JLabel lblSoLuongVe;

    public PopUpChiTietHoaDonJPA(String maHD) {
        this.maHoaDon = maHD;
        initComponents();
        loadData(maHD);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(900, 700));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = createMiddlePanel();
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblMaHD = createLabel("Mã HĐ", true);
        lblMaChuyen = createLabel("Mã chuyến", false);
        lblSoHieuTau = createLabel("Số hiệu tàu", false);
        lblNgayLap = createLabel("Ngày lập", false);

        lblGaDi = createLabel("Ga đi", false);
        lblGaDen = createLabel("Ga đến", false);
        lblNgayDi = createLabel("Ngày đi", false);
        lblGioDi = createLabel("Giờ đi", false);

        lblNgayDen = createLabel("Ngày đến (dự kiến)", false);
        lblGioDen = createLabel("Giờ đến (dự kiến)", false);
        panel.add(new JLabel());
        panel.add(new JLabel());

        panel.add(createInfoPair("Mã HĐ:", lblMaHD));
        panel.add(createInfoPair("Mã chuyến:", lblMaChuyen));
        panel.add(createInfoPair("Số hiệu tàu:", lblSoHieuTau));
        panel.add(createInfoPair("Ngày lập:", lblNgayLap));

        panel.add(createInfoPair("Ga đi:", lblGaDi));
        panel.add(createInfoPair("Ga đến:", lblGaDen));
        panel.add(createInfoPair("Ngày đi:", lblNgayDi));
        panel.add(createInfoPair("Giờ đi:", lblGioDi));

        panel.add(createInfoPair("Ngày đến:", lblNgayDen));
        panel.add(createInfoPair("Giờ đến:", lblGioDen));
        panel.add(new JLabel());
        panel.add(new JLabel());

        return panel;
    }

    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(1, 4, 15, 5));
        infoPanel.setBackground(Color.WHITE);

        lblTenKH = createLabel("Khách hàng", false);
        lblSDT = createLabel("SĐT", false);
        lblGioiTinh = createLabel("Giới tính", false);
        lblSoLuongVe = createLabel("Số vé", false);

        infoPanel.add(createInfoPair("Khách hàng:", lblTenKH));
        infoPanel.add(createInfoPair("SĐT:", lblSDT));
        infoPanel.add(createInfoPair("Giới tính:", lblGioiTinh));
        infoPanel.add(createInfoPair("Số vé:", lblSoLuongVe));

        panel.add(infoPanel, BorderLayout.NORTH);

        String[] columnNames = {"STT", "Họ tên", "Giới tính", "CCCD", "Loại vé", "Số lượng", "Đơn giá"};
        tblHanhKhach = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tblHanhKhach.setFont(new Font("Arial", Font.PLAIN, 13));
        tblHanhKhach.setRowHeight(30);
        tblHanhKhach.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tblHanhKhach.getTableHeader().setBackground(new Color(100, 100, 100));
        tblHanhKhach.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tblHanhKhach);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        leftPanel.setBackground(Color.WHITE);

        lblTongTien = createLabel("Tổng cộng", false);
        lblKhuyenMai = createLabel("Khuyến mãi", false);
        lblNoiDungKM = createLabel("Nội dung KM", false);
        lblPhuongThuc = createLabel("Phương thức", false);

        leftPanel.add(createInfoPair("Tổng cộng:", lblTongTien));
        leftPanel.add(createInfoPair("Khuyến mãi:", lblKhuyenMai));
        leftPanel.add(createInfoPair("Nội dung KM:", lblNoiDungKM));
        leftPanel.add(createInfoPair("Phương thức:", lblPhuongThuc));

        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 10, 5));
        rightPanel.setBackground(Color.WHITE);

        lblSoTien = createLabel("Số tiền thanh toán", false);
        lblSoTien.setFont(new Font("Arial", Font.BOLD, 18));
        lblSoTien.setForeground(new Color(0, 102, 204));

        lblTenNV = createLabel("Người lập", false);

        rightPanel.add(createInfoPair("SỐ TIỀN PHẢI THANH TOÁN:", lblSoTien));
        rightPanel.add(createInfoPair("Người lập:", lblTenNV));

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnIn = new JButton("In hóa đơn");
        btnIn.setFont(new Font("Arial", Font.BOLD, 14));
        btnIn.setBackground(new Color(0, 150, 50));
        btnIn.setForeground(Color.WHITE);
        btnIn.setFocusPainted(false);
        btnIn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Tính năng in đang được phát triển!");
        });

        btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Arial", Font.BOLD, 14));
        btnDong.setBackground(new Color(200, 50, 50));
        btnDong.setForeground(Color.WHITE);
        btnDong.setFocusPainted(false);
        btnDong.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });

        buttonPanel.add(btnIn);
        buttonPanel.add(btnDong);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(panel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        return southPanel;
    }

    private JPanel createInfoPair(String label, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(Color.GRAY);
        panel.add(lbl, BorderLayout.WEST);
        value.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createLabel(String text, boolean title) {
        JLabel label = new JLabel(text);
        label.setFont(title ? new Font("Arial", Font.BOLD, 16) : new Font("Arial", Font.PLAIN, 13));
        return label;
    }

    private void loadData(String maHD) {
        try {
            hoaDonRepo.findByMaHDWithDetails(maHD).ifPresent(hd -> {
                lblMaHD.setText(hd.getMaHD());
                lblNgayLap.setText(hd.getNgayLap() != null ? hd.getNgayLap().format(dateTimeFormatter) : "");

                if (hd.getKhachHang() != null) {
                    lblTenKH.setText(hd.getKhachHang().getHoTen());
                    lblSDT.setText(hd.getKhachHang().getSdt() != null ? hd.getKhachHang().getSdt() : "");
                    lblGioiTinh.setText(hd.getKhachHang().getGioiTinh() != null ? hd.getKhachHang().getGioiTinh() : "");
                }

                lblTongTien.setText(dfMoney.format(hd.getTongCong()) + " VNĐ");
                lblSoTien.setText(dfMoney.format(hd.getTongTien()) + " VNĐ");
                lblPhuongThuc.setText(hd.getPhuongThuc() != null ? hd.getPhuongThuc() : "Tiền mặt");

                if (hd.getKhuyenMai() != null) {
                    lblKhuyenMai.setText(hd.getKhuyenMai().getGiaTriGiam() + "%");
                    lblNoiDungKM.setText(hd.getKhuyenMai().getTenKM());
                } else {
                    lblKhuyenMai.setText("Không có");
                    lblNoiDungKM.setText("-");
                }

                nvRepo.findByMaNV(hd.getMaNVLap()).ifPresent(nv -> {
                    lblTenNV.setText(nv.getHoTen());
                });
            });

            List<ChiTietHoaDon> chiTietList = cthdRepo.findByMaHD(maHD);
            DefaultTableModel model = (DefaultTableModel) tblHanhKhach.getModel();
            model.setRowCount(0);

            int stt = 1;
            for (ChiTietHoaDon cthd : chiTietList) {
                String hoTen = "";
                String gioiTinh = "";
                String cccd = "";
                String loaiVe = "";

                if (cthd.getVe() != null && cthd.getVe().getKhachHang() != null) {
                    hoTen = cthd.getVe().getKhachHang().getHoTen();
                    gioiTinh = cthd.getVe().getKhachHang().getGioiTinh() != null ? cthd.getVe().getKhachHang().getGioiTinh() : "";
                    cccd = cthd.getVe().getKhachHang().getSoCCCD() != null ? cthd.getVe().getKhachHang().getSoCCCD() : "";
                }
                if (cthd.getVe() != null && cthd.getVe().getLoaiVe() != null) {
                    loaiVe = cthd.getVe().getLoaiVe().getTenLoai();
                }

                model.addRow(new Object[]{
                        stt++,
                        hoTen,
                        gioiTinh,
                        cccd,
                        loaiVe,
                        cthd.getSoLuong(),
                        dfMoney.format(cthd.getDonGia()) + " VNĐ"
                });
            }
            lblSoLuongVe.setText(String.valueOf(chiTietList.size()));

        } catch (Exception e) {
            System.err.println("Lỗi load chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showDialog(String maHD) {
        JFrame frame = new JFrame("Chi tiết hóa đơn: " + maHD);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new PopUpChiTietHoaDonJPA(maHD));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showDialog("HD0117102500020001"));
    }
}
