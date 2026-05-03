package JPA_Project.gui;

import JPA_Project.dto.GheDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

/**
 * KhachHangInfoPanel - Component hiển thị và nhập thông tin khách hàng.
 * Tách biệt hoàn toàn UI khách hàng khỏi ManHinhBanVeJPA.
 */
public class KhachHangInfoPanel extends JPanel {

    // ================================================================================
    // COLORS
    // ================================================================================
    private static final Color COLOR_ORANGE = new Color(255, 165, 0);
    private static final Color COLOR_BLUE_LIGHT = new Color(52, 152, 219);
    private static final Color COLOR_RED = new Color(220, 53, 69);

    // ================================================================================
    // CALLBACKS
    // ================================================================================
    private BiConsumer<String, KhachHangInfo> onInfoChanged;  // maCho, info
    private Consumer<String> onRemoveSeat;  // maCho
    private BiConsumer<String, String> onPassengerChanged;  // maCho, maLoaiVe

    // ================================================================================
    // STATE
    // ================================================================================
    private Map<String, KhachHangInfoPanel.KhachHangInfo> customerInfoMap = new LinkedHashMap<>();
    private Map<String, JPanel> customerPanelMap = new LinkedHashMap<>();
    private Map<String, JLabel> priceLabelMap = new HashMap<>();

    // ================================================================================
    // UI COMPONENTS
    // ================================================================================
    private JPanel customerContainer;
    private JScrollPane scrollPane;

    // ================================================================================
    // INNER CLASS - Customer Info Data
    // ================================================================================
    public static class KhachHangInfo {
        private String maCho;
        private String soCho;
        private String maToa;
        private String hoTen;
        private String cccd;
        private String sdt;
        private String ngaySinh;
        private String maLoaiVe;
        private BigDecimal giaVe;

        public KhachHangInfo(String maCho, String soCho, String maToa, String maLoaiVe, BigDecimal giaVe) {
            this.maCho = maCho;
            this.soCho = soCho;
            this.maToa = maToa;
            this.maLoaiVe = maLoaiVe != null ? maLoaiVe : "VT01";
            this.giaVe = giaVe;
        }

        // Getters and setters
        public String getMaCho() { return maCho; }
        public String getSoCho() { return soCho; }
        public String getMaToa() { return maToa; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getCccd() { return cccd; }
        public void setCccd(String cccd) { this.cccd = cccd; }
        public String getSdt() { return sdt; }
        public void setSdt(String sdt) { this.sdt = sdt; }
        public String getNgaySinh() { return ngaySinh; }
        public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
        public String getMaLoaiVe() { return maLoaiVe; }
        public void setMaLoaiVe(String maLoaiVe) { this.maLoaiVe = maLoaiVe; }
        public BigDecimal getGiaVe() { return giaVe; }
        public void setGiaVe(BigDecimal giaVe) { this.giaVe = giaVe; }
    }

    // ================================================================================
    // CONSTRUCTOR
    // ================================================================================
    public KhachHangInfoPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel title = new JLabel("Thông tin hành khách");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Customer list container
        customerContainer = new JPanel();
        customerContainer.setLayout(new BoxLayout(customerContainer, BoxLayout.Y_AXIS));
        customerContainer.setOpaque(false);
        customerContainer.setBorder(new EmptyBorder(5, 5, 5, 5));

        showPlaceholder();

        scrollPane = new JScrollPane(customerContainer);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ================================================================================
    // PUBLIC METHODS
    // ================================================================================

    /**
     * Set callback when customer info changes.
     */
    public void setOnInfoChanged(BiConsumer<String, KhachHangInfo> callback) {
        this.onInfoChanged = callback;
    }

    /**
     * Set callback when customer is removed.
     */
    public void setOnRemoveSeat(Consumer<String> callback) {
        this.onRemoveSeat = callback;
    }

    /**
     * Set callback when ticket type changes.
     */
    public void setOnPassengerChanged(BiConsumer<String, String> callback) {
        this.onPassengerChanged = callback;
    }

    /**
     * Add a new seat with customer info.
     */
    public void addSeat(GheDTO ghe, String maLoaiVe, BigDecimal giaVe) {
        String maCho = ghe.maChoDat();
        
        KhachHangInfo info = new KhachHangInfo(
                maCho,
                ghe.soCho(),
                ghe.maToa(),
                maLoaiVe,
                giaVe
        );
        customerInfoMap.put(maCho, info);
        
        JPanel panel = createCustomerPanel(info);
        customerPanelMap.put(maCho, panel);
        
        updateCustomerList();
    }

    /**
     * Remove a seat from the list.
     */
    public void removeSeat(String maCho) {
        customerInfoMap.remove(maCho);
        customerPanelMap.remove(maCho);
        priceLabelMap.remove(maCho);
        updateCustomerList();
    }

    /**
     * Update price for a specific seat.
     */
    public void updatePrice(String maCho, BigDecimal giaVe) {
        KhachHangInfo info = customerInfoMap.get(maCho);
        if (info != null) {
            info.setGiaVe(giaVe);
            JLabel priceLabel = priceLabelMap.get(maCho);
            if (priceLabel != null) {
                priceLabel.setText(formatCurrency(giaVe));
            }
        }
    }

    /**
     * Get all customer info.
     */
    public Map<String, KhachHangInfo> getAllCustomerInfo() {
        return new LinkedHashMap<>(customerInfoMap);
    }

    /**
     * Check if there are any customers.
     */
    public boolean isEmpty() {
        return customerInfoMap.isEmpty();
    }

    /**
     * Get total number of customers.
     */
    public int getCustomerCount() {
        return customerInfoMap.size();
    }

    /**
     * Calculate total price.
     */
    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (KhachHangInfo info : customerInfoMap.values()) {
            if (info.getGiaVe() != null) {
                total = total.add(info.getGiaVe());
            }
        }
        return total;
    }

    /**
     * Clear all customers.
     */
    public void clear() {
        customerInfoMap.clear();
        customerPanelMap.clear();
        priceLabelMap.clear();
        updateCustomerList();
    }

    // ================================================================================
    // PRIVATE METHODS - UI CREATION
    // ================================================================================

    private JPanel createCustomerPanel(KhachHangInfo info) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 248, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ORANGE, 1),
                new EmptyBorder(10, 10, 10, 10)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.setName(info.getMaCho());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(255, 228, 204));
        header.setOpaque(true);

        JLabel lblMaGhe = new JLabel("Ghế: " + info.getMaCho() + " (" + info.getSoCho() + ")");
        lblMaGhe.setFont(new Font("Arial", Font.BOLD, 13));
        lblMaGhe.setForeground(COLOR_BLUE_LIGHT);
        header.add(lblMaGhe, BorderLayout.WEST);

        JButton btnXoa = new JButton("✕");
        btnXoa.setFocusPainted(false);
        btnXoa.setBackground(COLOR_RED);
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setBorderPainted(false);
        btnXoa.setOpaque(true);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.addActionListener(e -> handleRemoveSeat(info.getMaCho()));
        header.add(btnXoa, BorderLayout.EAST);

        panel.add(header);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        Font fontLabel = new Font("Arial", Font.BOLD, 11);
        Font fontInput = new Font("Arial", Font.PLAIN, 11);

        // Row 1: Họ tên
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField txtHoTen = new JTextField(15);
        txtHoTen.setFont(fontInput);
        txtHoTen.setName(info.getMaCho() + "_hoTen");
        formPanel.add(txtHoTen, gbc);

        // Row 2: CCCD
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("CCCD:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField txtCCCD = new JTextField(15);
        txtCCCD.setFont(fontInput);
        txtCCCD.setName(info.getMaCho() + "_cccd");
        formPanel.add(txtCCCD, gbc);

        // Row 3: Đối tượng + Giá
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Đối tượng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<String> cbDoiTuong = new JComboBox<>(new String[]{"Người lớn", "Sinh viên", "Trẻ em", "Người cao tuổi"});
        cbDoiTuong.setFont(fontInput);
        cbDoiTuong.setName(info.getMaCho() + "_doiTuong");
        formPanel.add(cbDoiTuong, gbc);

        // Row 4: Giá vé
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Giá vé:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel lblGia = new JLabel(formatCurrency(info.getGiaVe()));
        lblGia.setFont(fontLabel);
        lblGia.setForeground(COLOR_ORANGE);
        priceLabelMap.put(info.getMaCho(), lblGia);
        formPanel.add(lblGia, gbc);

        panel.add(formPanel);

        // Add listeners
        txtHoTen.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                info.setHoTen(txtHoTen.getText().trim());
                notifyInfoChanged(info);
            }
        });

        txtCCCD.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                info.setCccd(txtCCCD.getText().trim());
                notifyInfoChanged(info);
            }
        });

        cbDoiTuong.addActionListener(e -> {
            String doiTuong = (String) cbDoiTuong.getSelectedItem();
            String newMaLoaiVe = convertToMaLoaiVe(doiTuong);
            info.setMaLoaiVe(newMaLoaiVe);
            if (onPassengerChanged != null) {
                onPassengerChanged.accept(info.getMaCho(), newMaLoaiVe);
            }
        });

        return panel;
    }

    private void handleRemoveSeat(String maCho) {
        if (onRemoveSeat != null) {
            onRemoveSeat.accept(maCho);
        }
    }

    private void notifyInfoChanged(KhachHangInfo info) {
        if (onInfoChanged != null) {
            onInfoChanged.accept(info.getMaCho(), info);
        }
    }

    private void updateCustomerList() {
        customerContainer.removeAll();

        if (customerInfoMap.isEmpty()) {
            showPlaceholder();
        } else {
            for (Map.Entry<String, JPanel> entry : customerPanelMap.entrySet()) {
                customerContainer.add(entry.getValue());
                customerContainer.add(Box.createVerticalStrut(8));
            }
        }

        revalidate();
        repaint();
    }

    private void showPlaceholder() {
        customerContainer.removeAll();
        JLabel placeholder = new JLabel("Chọn ghế để nhập thông tin hành khách");
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        placeholder.setForeground(Color.GRAY);
        customerContainer.add(Box.createVerticalGlue());
        customerContainer.add(placeholder);
        customerContainer.add(Box.createVerticalGlue());
    }

    private String convertToMaLoaiVe(String doiTuong) {
        if (doiTuong == null) return "VT01";
        switch (doiTuong) {
            case "Sinh viên": return "VT03";
            case "Trẻ em": return "VT02";
            case "Người cao tuổi": return "VT04";
            default: return "VT01";
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VND";
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " VND";
    }
}
