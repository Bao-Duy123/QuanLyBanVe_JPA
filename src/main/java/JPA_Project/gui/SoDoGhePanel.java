package JPA_Project.gui;

import JPA_Project.dto.GheDTO;
import JPA_Project.repository.ChoDatRepository;
import JPA_Project.entity.ChoDat;
import JPA_Project.network.NetworkManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * SoDoGhePanel - Component vẽ sơ đồ ghế tàu với 2 chế độ hiển thị.
 *
 * Chế độ 1 - KHOANG: Mỗi khoang 6 ghế (grid 3 hàng x 2 cột)
 * Chế độ 2 - CENTER_AISLE: 4 hàng liên tục, có lối đi ở giữa giữa hàng 2 và 3
 *
 * Trạng thái màu sắc:
 * - Trống (Available): Màu xám nhạt (LIGHT_GRAY)
 * - Đã đặt (Occupied): Màu đen (BLACK), chữ trắng, không click được
 * - Đang chọn (Selected): Màu xanh dương (BLUE), chữ trắng
 */
public class SoDoGhePanel extends JPanel {

    // ================================================================================
    // ENUMS & CONSTANTS
    // ================================================================================

    public enum DisplayMode {
        KHOANG,      // Mỗi khoang 6 ghế (3 hàng x 2 cột)
        CENTER_AISLE // 4 hàng liên tục, lối đi ở giữa giữa hàng 2 và 3
    }

    // Màu sắc
    private static final Color MAU_GHE_TRONG = Color.LIGHT_GRAY;
    private static final Color MAU_GIUONG_TRONG = new Color(255, 243, 176); // Vàng nhạt cho giường trống
    private static final Color MAU_GHE_DAT = Color.BLACK;
    private static final Color MAU_GHE_CHON = Color.BLUE;
    private static final Color VIEN_GHE = Color.GRAY;
    private static final Color MAU_NEN_CHINH = Color.WHITE;
    private static final Color MAU_NEN_KHOANG = new Color(245, 245, 250);

    // Kích thước
    private static final int CHIEU_RONG_GHE = 40;
    private static final int CHIEU_CAO_GHE = 40;
    private static final int KHOANG_CACH_NUT = 5;
    private static final int KHOANG_CACH_HANG = 10;
    private static final int CHIEU_RONG_LOI_DI = 20;

    // Số ghế mỗi khoang (chế độ KHOANG)
    private static final int GHE_MOI_KHOANG = 6;
    private static final int HANG_MOI_KHOANG = 3;
    private static final int COT_MOI_KHOANG = 2;

    // Số hàng (chế độ CENTER_AISLE)
    private static final int TONG_HANG = 4;

    // ================================================================================
    // FIELDS
    // ================================================================================

    private final ChoDatRepository choDatRepository;
    private DisplayMode displayMode;
    private Consumer<GheDTO> onSeatSelected;

    private String maToaHienTai = "";
    private String loaiToaHienTai = "";    // Mã loại toa (ví dụ: G_GHE, G_NGAM)
    private String tenLoaiToaHienTai = ""; // Tên loại toa (ví dụ: Ghế ngồi mềm, Giường nằm khoang 6)
    private String maChuyenTauHienTai = "";
    private boolean laLoaiGiuong = false;  // True = giường nằm, False = ghế ngồi

    private Map<String, JButton> seatButtonsMap = new HashMap<>();
    private Set<String> selectedSeats = new LinkedHashSet<>();
    
    // Realtime support
    private NetworkManager networkManager;
    private boolean isSubscribed = false;

    // UI Components
    private JPanel seatContainer;
    private JLabel lblTieuDeToa;
    private JLabel lblTongGhe;

    // ================================================================================
    // CONSTRUCTORS
    // ================================================================================

    public SoDoGhePanel() {
        this(DisplayMode.KHOANG);
    }

    public SoDoGhePanel(DisplayMode displayMode) {
        this.displayMode = displayMode;
        this.choDatRepository = new ChoDatRepository();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 5));
        setBackground(MAU_NEN_CHINH);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header với tiêu đề
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Scrollable seat container
        seatContainer = new JPanel();
        seatContainer.setLayout(new BoxLayout(seatContainer, BoxLayout.X_AXIS));
        seatContainer.setBackground(MAU_NEN_CHINH);
        seatContainer.setAlignmentX(LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(seatContainer);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        // Panel chính với TitledBorder
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Chọn vị trí của ghế",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        // Footer với chú thích màu
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        // Hiển thị placeholder ban đầu
        showPlaceholder("Chọn một toa để xem sơ đồ ghế");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_CHINH);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(5, 8, 5, 8)));

        lblTieuDeToa = new JLabel("TOA - ");
        lblTieuDeToa.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDeToa.setForeground(new Color(0, 102, 153));

        lblTongGhe = new JLabel("");
        lblTongGhe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTongGhe.setForeground(Color.GRAY);

        panel.add(lblTieuDeToa, BorderLayout.WEST);
        panel.add(lblTongGhe, BorderLayout.EAST);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setBackground(MAU_NEN_CHINH);

        // Hiển thị legend dựa trên loại toa
        if (laLoaiGiuong) {
            panel.add(createLegendItem(MAU_GIUONG_TRONG, "Giường trống", Color.BLACK));
        } else {
            panel.add(createLegendItem(MAU_GHE_TRONG, "Ghế trống", Color.BLACK));
        }
        panel.add(createLegendItem(MAU_GHE_CHON, "Đang chọn", Color.WHITE));
        panel.add(createLegendItem(MAU_GHE_DAT, "Đã đặt", Color.WHITE));
        return panel;
    }

    private JLabel createLegendItem(Color bgColor, String text, Color textColor) {
        JLabel label = new JLabel("  " + text + "  ");
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setForeground(textColor);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return label;
    }

    // ================================================================================
    // PUBLIC METHODS
    // ================================================================================

    public void setDisplayMode(DisplayMode mode) {
        this.displayMode = mode;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public void setOnSeatSelected(Consumer<GheDTO> callback) {
        this.onSeatSelected = callback;
    }
    
    /**
     * Thiết lập NetworkManager cho realtime seat updates
     */
    public void setNetworkManager(NetworkManager manager) {
        this.networkManager = manager;
    }
    
    /**
     * Subscribe chuyến tàu để nhận realtime updates
     */
    public void subscribeToUpdates(String maChuyenTau) {
        this.maChuyenTauHienTai = maChuyenTau;
        
        if (networkManager != null && networkManager.isConnected()) {
            networkManager.addSeatUpdateListener(this::onSeatUpdateFromServer);
            networkManager.subscribeChuyenTau(maChuyenTau);
            isSubscribed = true;
            System.out.println("[SoDoGhePanel] Da subscribe chuyen tau: " + maChuyenTau);
        }
    }
    
    /**
     * Hủy subscribe
     */
    public void unsubscribeFromUpdates() {
        if (networkManager != null && isSubscribed) {
            networkManager.removeSeatUpdateListener(this::onSeatUpdateFromServer);
            networkManager.unsubscribeChuyenTau(maChuyenTauHienTai);
            isSubscribed = false;
            System.out.println("[SoDoGhePanel] Da unsubscribe");
        }
    }
    
    /**
     * Xử lý khi nhận seat update từ server
     */
    private void onSeatUpdateFromServer(String maChuyenTau, String maChoDat, String status) {
        // Chỉ xử lý nếu đúng chuyến tàu đang xem
        if (!maChuyenTau.equals(maChuyenTauHienTai)) return;
        
        System.out.println("[SoDoGhePanel] Seat update: " + maChoDat + " -> " + status);
        
        // Cập nhật UI trên EDT
        SwingUtilities.invokeLater(() -> {
            JButton btn = seatButtonsMap.get(maChoDat);
            if (btn == null) return;
            
            if ("DA_DAT".equals(status)) {
                // Ghế đã được đặt bởi client khác
                // Kiểm tra nếu ghế đang được chọn bởi user hiện tại
                if (selectedSeats.contains(maChoDat)) {
                    // Bỏ chọn ghế này
                    selectedSeats.remove(maChoDat);
                    JOptionPane.showMessageDialog(this, 
                        "Ghế " + maChoDat + " vừa được đặt bởi client khác!\nGhế đã được bỏ chọn.",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
                
                btn.setOpaque(true);
                btn.setBorderPainted(false);
                btn.setBackground(MAU_GHE_DAT);
                btn.setForeground(Color.WHITE);
                btn.setEnabled(false);
                btn.setToolTipText(maChoDat + " - Đã đặt (bởi client khác)");
                
            } else if ("TRONG".equals(status)) {
                // Ghế đã được trả về
                btn.setOpaque(true);
                btn.setBorderPainted(false);
                btn.setBackground(laLoaiGiuong ? MAU_GIUONG_TRONG : MAU_GHE_TRONG);
                btn.setForeground(Color.BLACK);
                btn.setEnabled(true);
                btn.setToolTipText(maChoDat + " - Trống");
            }
            
            // Cập nhật tổng số
            updateTongSoGhe();
        });
    }
    
    private void updateTongSoGhe() {
        int tongSo = seatButtonsMap.size();
        int daDat = 0;
        for (JButton btn : seatButtonsMap.values()) {
            if (!btn.isEnabled() || MAU_GHE_DAT.equals(btn.getBackground())) {
                daDat++;
            }
        }
        lblTongGhe.setText("Tổng: " + tongSo + " ghế | Đã đặt: " + daDat + " | Trống: " + (tongSo - daDat));
    }

    /**
     * Render sơ đồ ghế dựa trên danh sách GheDTO.
     *
     * @param seats Danh sách ghế
     * @param loaiToa Mã loại toa (ví dụ: G_GHE, G_NGAM)
     * @param tenLoaiToa Tên loại toa để xác định màu sắc (ví dụ: "Ghế ngồi mềm", "Giường nằm khoang 6")
     * @param maToa Mã toa để hiển thị tiêu đề
     */
    public void renderSeats(List<GheDTO> seats, String loaiToa, String tenLoaiToa, String maToa) {
        seatContainer.removeAll();
        seatButtonsMap.clear();
        selectedSeats.clear();

        this.maToaHienTai = maToa != null ? maToa : "";
        this.loaiToaHienTai = loaiToa != null ? loaiToa.toUpperCase() : "";
        this.tenLoaiToaHienTai = tenLoaiToa != null ? tenLoaiToa : "";

        // Xác định loại ghế và màu sắc dựa trên tên loại toa
        this.laLoaiGiuong = xacDinhLaGiuongNam(tenLoaiToa);

        // Luôn xác định display mode đúng theo loại toa hiện tại
        // Giường nằm -> KHOANG (mỗi khoang 6 giường)
        // Ghế ngồi -> CENTER_AISLE (4 hàng có lối đi giữa)
        if (this.laLoaiGiuong) {
            displayMode = DisplayMode.KHOANG;
        } else {
            displayMode = DisplayMode.CENTER_AISLE;
        }

        capNhatTieuDe();

        if (seats == null || seats.isEmpty()) {
            showPlaceholder("Toa này không có ghế nào!");
            return;
        }

        switch (displayMode) {
            case KHOANG:
                renderTheoKhoang(seats);
                break;
            case CENTER_AISLE:
                renderTheoLoiDiGiua(seats);
                break;
        }

        updateSeatColors();
        revalidate();
        repaint();
    }

    /**
     * Render sơ đồ ghế (backward compatible - không có tenLoaiToa).
     */
    public void renderSeats(List<GheDTO> seats, String loaiToa, String maToa) {
        renderSeats(seats, loaiToa, loaiToa, maToa);
    }

    public void renderSeats(List<GheDTO> seats, String loaiToa) {
        renderSeats(seats, loaiToa, loaiToa, "");
    }

    /**
     * Xác định có phải là toa giường nằm hay không dựa trên tên loại toa.
     */
    private boolean xacDinhLaGiuongNam(String tenLoaiToa) {
        if (tenLoaiToa == null || tenLoaiToa.isEmpty()) return false;

        String upper = tenLoaiToa.toUpperCase();

        // Nếu chứa từ khóa ghế ngồi -> không phải giường nằm
        if (upper.contains("GHẾ") || upper.contains("GHE") ||
            upper.contains("SEAT") || upper.contains("CHAIR")) {
            return false;
        }

        // Nếu chứa từ khóa giường nằm -> là giường nằm
        if (upper.contains("NGÀM") || upper.contains("GIƯỜNG") ||
            upper.contains("GIUONG") || upper.contains("NẰM") ||
            upper.contains("BED") || upper.contains("SLEEPER")) {
            return true;
        }

        return false;
    }

    /**
     * Cập nhật trạng thái ghế đã chọn từ bên ngoài.
     */
    public void updateSelectedSeats(Set<String> selectedMaCho) {
        this.selectedSeats.clear();
        if (selectedMaCho != null) {
            this.selectedSeats.addAll(selectedMaCho);
        }
        updateSeatColors();
    }

    public void clear() {
        seatContainer.removeAll();
        showPlaceholder("Chọn một toa để xem sơ đồ ghế");
        seatButtonsMap.clear();
        selectedSeats.clear();
        lblTongGhe.setText("");
        revalidate();
        repaint();
    }

    /**
     * Lấy tập hợp các mã ghế đang được chọn.
     */
    public Set<String> getSelectedSeats() {
        return new LinkedHashSet<>(selectedSeats);
    }

    /**
     * Đánh dấu ghế đã đặt (gọi từ bên ngoài sau khi load dữ liệu ChoDat).
     */
    public void danhDauGheDaDat(List<String> danhSachMaChoDaDat) {
        if (danhSachMaChoDaDat == null || danhSachMaChoDaDat.isEmpty()) return;

        for (String maCho : danhSachMaChoDaDat) {
            JButton btn = seatButtonsMap.get(maCho);
            if (btn != null) {
                btn.setOpaque(true);
                btn.setBorderPainted(false);
                btn.setBackground(MAU_GHE_DAT);
                btn.setForeground(Color.WHITE);
                btn.setEnabled(false);
                btn.setToolTipText(maCho + " - Đã đặt");
            }
        }
    }

    private void capNhatTieuDe() {
        lblTieuDeToa.setText("TOA: " + maToaHienTai);
    }

    // ================================================================================
    // CHẾ ĐỘ 1: THEO KHOANG (Mỗi khoang 6 ghế - 3 hàng x 2 cột)
    // ================================================================================

    private void renderTheoKhoang(List<GheDTO> seats) {
        // Sắp xếp ghế theo số
        List<GheDTO> sortedSeats = seats.stream()
                .sorted(Comparator.comparingInt(g -> {
                    try { return Integer.parseInt(g.soCho()); }
                    catch (NumberFormatException e) { return 0; }
                }))
                .collect(Collectors.toList());

        // Panel chính chứa các khoang
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBackground(MAU_NEN_CHINH);
        mainPanel.setAlignmentX(LEFT_ALIGNMENT);

        // Chia thành các khoang, mỗi khoang 6 ghế
        int soKhoang = (int) Math.ceil((double) sortedSeats.size() / GHE_MOI_KHOANG);

        for (int k = 0; k < soKhoang; k++) {
            int startIdx = k * GHE_MOI_KHOANG;
            int endIdx = Math.min(startIdx + GHE_MOI_KHOANG, sortedSeats.size());
            List<GheDTO> khoangSeats = sortedSeats.subList(startIdx, endIdx);

            JPanel khoangPanel = taoPanelKhoang(k + 1, khoangSeats);
            mainPanel.add(khoangPanel);

            if (k < soKhoang - 1) {
                mainPanel.add(Box.createHorizontalStrut(15));
            }
        }

        seatContainer.add(mainPanel);

        // Cập nhật thông tin tổng
        int tongSo = seats.size();
        int daDat = (int) seats.stream().filter(GheDTO::daDat).count();
        lblTongGhe.setText("Tổng: " + tongSo + " ghế | Đã đặt: " + daDat + " | Trống: " + (tongSo - daDat));
    }

    private JPanel taoPanelKhoang(int soKhoang, List<GheDTO> seats) {
        JPanel khoangPanel = new JPanel();
        khoangPanel.setLayout(new BoxLayout(khoangPanel, BoxLayout.Y_AXIS));
        khoangPanel.setBackground(MAU_NEN_KHOANG);
        khoangPanel.setAlignmentY(TOP_ALIGNMENT);
        khoangPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "Khoang " + soKhoang,
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 11)
                ),
                new EmptyBorder(8, 10, 8, 10)
        ));

        // Grid 3 hàng x 2 cột
        JPanel gridPanel = new JPanel(new GridLayout(HANG_MOI_KHOANG, COT_MOI_KHOANG, KHOANG_CACH_NUT, KHOANG_CACH_NUT));
        gridPanel.setBackground(MAU_NEN_KHOANG);

        for (int i = 0; i < GHE_MOI_KHOANG; i++) {
            if (i < seats.size()) {
                GheDTO ghe = seats.get(i);
                JButton btn = taoNutGhe(ghe);
                gridPanel.add(btn);
                seatButtonsMap.put(ghe.maChoDat(), btn);
            } else {
                gridPanel.add(Box.createVerticalStrut(CHIEU_CAO_GHE));
            }
        }

        khoangPanel.add(gridPanel);
        return khoangPanel;
    }

    // ================================================================================
    // CHẾ ĐỘ 2: LỐI ĐI Ở GIỮA (4 hàng, lối đi giữa hàng 2 và 3)
    // ================================================================================

    private void renderTheoLoiDiGiua(List<GheDTO> seats) {
        // Sắp xếp ghế theo số
        List<GheDTO> sortedSeats = seats.stream()
                .sorted(Comparator.comparingInt(g -> {
                    try { return Integer.parseInt(g.soCho()); }
                    catch (NumberFormatException e) { return 0; }
                }))
                .collect(Collectors.toList());

        // Panel chính
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(MAU_NEN_CHINH);
        mainPanel.setAlignmentX(LEFT_ALIGNMENT);

        // Chia thành 4 hàng, mỗi hàng có số ghế bằng nhau
        int soGheMoiHang = (int) Math.ceil((double) sortedSeats.size() / TONG_HANG);

        for (int hang = 0; hang < TONG_HANG; hang++) {
            int startIdx = hang * soGheMoiHang;
            int endIdx = Math.min(startIdx + soGheMoiHang, sortedSeats.size());

            if (startIdx < sortedSeats.size()) {
                List<GheDTO> hangSeats = sortedSeats.subList(startIdx, endIdx);
                JPanel panelHang = taoHangLoiDiGiua(hang + 1, hangSeats);
                mainPanel.add(panelHang);

                if (hang < TONG_HANG - 1) {
                    mainPanel.add(Box.createVerticalStrut(KHOANG_CACH_HANG));
                }

                // Thêm lối đi sau hàng 2 (giảm kích thước)
                if (hang == 1) {
                    mainPanel.add(Box.createVerticalStrut(5));
                    JPanel loiDiPanel = taoLoiDi();
                    mainPanel.add(loiDiPanel);
                    mainPanel.add(Box.createVerticalStrut(3));
                }
            }
        }

        seatContainer.add(mainPanel);

        // Cập nhật thông tin tổng
        int tongSo = seats.size();
        int daDat = (int) seats.stream().filter(GheDTO::daDat).count();
        lblTongGhe.setText("Tổng: " + tongSo + " ghế | Đã đặt: " + daDat + " | Trống: " + (tongSo - daDat));
    }

    private JPanel taoHangLoiDiGiua(int soHang, List<GheDTO> seats) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(MAU_NEN_CHINH);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        // Label hàng
        JLabel lblHang = new JLabel("Hàng " + soHang + ": ");
        lblHang.setFont(new Font("Arial", Font.BOLD, 12));
        lblHang.setForeground(Color.DARK_GRAY);
        lblHang.setPreferredSize(new Dimension(65, CHIEU_CAO_GHE));
        lblHang.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblHang);

        // Tính số ghế mỗi bên (lối đi ở giữa)
        int soGheMotBen = (int) Math.ceil((double) seats.size() / 2);

        // Panel bên trái
        JPanel panelTrai = new JPanel(new GridLayout(1, soGheMotBen, KHOANG_CACH_NUT, 0));
        panelTrai.setBackground(MAU_NEN_CHINH);

        // Panel lối đi
        JPanel aislePanel = new JPanel();
        aislePanel.setBackground(MAU_NEN_CHINH);
        aislePanel.setPreferredSize(new Dimension(CHIEU_RONG_LOI_DI, CHIEU_CAO_GHE));
        aislePanel.setMaximumSize(new Dimension(CHIEU_RONG_LOI_DI, CHIEU_CAO_GHE));

        // Panel bên phải
        JPanel panelPhai = new JPanel(new GridLayout(1, soGheMotBen, KHOANG_CACH_NUT, 0));
        panelPhai.setBackground(MAU_NEN_CHINH);

        // Phân chia ghế
        for (int i = 0; i < seats.size(); i++) {
            GheDTO ghe = seats.get(i);
            JButton btn = taoNutGhe(ghe);

            if (i < soGheMotBen) {
                panelTrai.add(btn);
                seatButtonsMap.put(ghe.maChoDat(), btn);
            } else {
                panelPhai.add(btn);
                seatButtonsMap.put(ghe.maChoDat(), btn);
            }
        }

        // Thêm các ô trống nếu cần
        while (panelTrai.getComponentCount() < soGheMotBen) {
            panelTrai.add(Box.createHorizontalStrut(CHIEU_RONG_GHE));
        }
        while (panelPhai.getComponentCount() < soGheMotBen) {
            panelPhai.add(Box.createHorizontalStrut(CHIEU_RONG_GHE));
        }

        panel.add(panelTrai);
        panel.add(aislePanel);
        panel.add(panelPhai);

        return panel;
    }

    private JPanel taoLoiDi() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(MAU_NEN_CHINH);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(65 + (CHIEU_RONG_GHE + KHOANG_CACH_NUT) * 2 + CHIEU_RONG_LOI_DI, 18));
        panel.setMaximumSize(new Dimension(65 + (CHIEU_RONG_GHE + KHOANG_CACH_NUT) * 2 + CHIEU_RONG_LOI_DI, 18));

        JLabel label = new JLabel("  LỐI ĐI  ");
        label.setFont(new Font("Arial", Font.ITALIC, 9));
        label.setForeground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);

        return panel;
    }

    // ================================================================================
    // HELPER METHODS
    // ================================================================================

    private JButton taoNutGhe(GheDTO ghe) {
        JButton btn = new JButton(ghe.soCho());
        btn.setPreferredSize(new Dimension(CHIEU_RONG_GHE, CHIEU_CAO_GHE));
        btn.setMinimumSize(new Dimension(CHIEU_RONG_GHE, CHIEU_CAO_GHE));
        btn.setMaximumSize(new Dimension(CHIEU_RONG_GHE, CHIEU_CAO_GHE));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createLineBorder(VIEN_GHE, 1));
        btn.setOpaque(true);

        // Đặt màu dựa trên trạng thái
        if (ghe.daDat()) {
            btn.setBackground(MAU_GHE_DAT);
            btn.setForeground(Color.WHITE);
            btn.setEnabled(false);
            btn.setToolTipText(ghe.maChoDat() + " - Đã đặt");
        } else {
            btn.setBackground(MAU_GHE_TRONG);
            btn.setForeground(Color.BLACK);
            btn.setToolTipText(ghe.maChoDat() + " - Trống");
        }

        btn.addActionListener(e -> xuLyClickGhe(ghe));

        return btn;
    }

    private void xuLyClickGhe(GheDTO ghe) {
        if (ghe.daDat()) {
            JOptionPane.showMessageDialog(this,
                    "Ghế " + ghe.soCho() + " đã được đặt!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Toggle selection
        if (selectedSeats.contains(ghe.maChoDat())) {
            selectedSeats.remove(ghe.maChoDat());
        } else {
            selectedSeats.add(ghe.maChoDat());
        }

        updateSeatColors();

        // Gọi callback nếu có
        if (onSeatSelected != null) {
            onSeatSelected.accept(ghe);
        }
    }

    private void updateSeatColors() {
        Color mauGheTrong = laLoaiGiuong ? MAU_GIUONG_TRONG : MAU_GHE_TRONG;

        for (Map.Entry<String, JButton> entry : seatButtonsMap.entrySet()) {
            String maCho = entry.getKey();
            JButton btn = entry.getValue();

            if (!btn.isEnabled()) continue; // Bỏ qua ghế đã đặt

            if (selectedSeats.contains(maCho)) {
                btn.setOpaque(true);
                btn.setBorderPainted(false);
                btn.setBackground(MAU_GHE_CHON);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setOpaque(true);
                btn.setBorderPainted(false);
                btn.setBackground(mauGheTrong);
                btn.setForeground(Color.BLACK);
            }
        }
    }

    private void showPlaceholder(String message) {
        seatContainer.removeAll();
        seatContainer.setLayout(new BoxLayout(seatContainer, BoxLayout.X_AXIS));
        seatContainer.add(Box.createHorizontalGlue());

        JLabel placeholder = new JLabel(message);
        placeholder.setFont(new Font("Arial", Font.ITALIC, 14));
        placeholder.setForeground(Color.GRAY);
        placeholder.setAlignmentX(CENTER_ALIGNMENT);
        seatContainer.add(placeholder);

        seatContainer.add(Box.createHorizontalGlue());
    }

    // ================================================================================
    // MAIN TEST
    // ================================================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Sơ Đồ Ghế - 2 Chế Độ Hiển Thị");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 500);
            frame.setLayout(new BorderLayout());

            // Tạo panel với chế độ KHOANG
            SoDoGhePanel soDoGhePanel = new SoDoGhePanel(DisplayMode.KHOANG);
            frame.add(soDoGhePanel, BorderLayout.CENTER);

            // Tạo panel chọn chế độ
            JPanel controlPanel = new JPanel(new FlowLayout());
            JButton btnModeKhoang = new JButton("Chế độ Khoang (3x2)");
            JButton btnModeAisle = new JButton("Chế độ Lối đi giữa");
            JButton btnTest = new JButton("Load Test Data");
            controlPanel.add(btnModeKhoang);
            controlPanel.add(btnModeAisle);
            controlPanel.add(btnTest);
            frame.add(controlPanel, BorderLayout.NORTH);

            // Tạo test data (48 ghế)
            List<GheDTO> testSeats = new ArrayList<>();
            for (int i = 1; i <= 48; i++) {
                GheDTO ghe = new GheDTO(
                    "TOA01-C" + String.format("%02d", i),
                    "TOA01",
                    String.format("%02d", i),
                    null,   // khoang
                    null,   // tang
                    i == 5 || i == 12 || i == 25 || i == 36 // Ghế đã đặt
                );
                testSeats.add(ghe);
            }

            btnModeKhoang.addActionListener(e -> {
                soDoGhePanel.setDisplayMode(DisplayMode.KHOANG);
                soDoGhePanel.renderSeats(testSeats, "GHMEM", "TOA GHMEM-01");
            });

            btnModeAisle.addActionListener(e -> {
                soDoGhePanel.setDisplayMode(DisplayMode.CENTER_AISLE);
                soDoGhePanel.renderSeats(testSeats, "GHMEM", "TOA GHMEM-01");
            });

            btnTest.addActionListener(e -> {
                soDoGhePanel.renderSeats(testSeats, "GHMEM", "TOA GHMEM-01");
            });

            // Mặc định hiển thị
            soDoGhePanel.renderSeats(testSeats, "GHMEM", "TOA GHMEM-01");

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
