package JDBC_Project.gui.Panel.ManHinhTab;

import dao.LoaiVeDAO;
import entity.LoaiVe;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ManHinhQuanLyLoaiVe extends JPanel {

    private final LoaiVeDAO loaiVeDAO;
    private JTable tableLoaiVe;
    private DefaultTableModel tableModel;

    private JTextField txtMaLoaiVe, txtTenLoai, txtMucGiaGiam;
    private JSpinner spinTuoiMin, spinTuoiMax;
    private JButton btnCapNhat, btnLamMoi;

    // Màu sắc và Font chủ đạo
    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);

    public ManHinhQuanLyLoaiVe() {
        loaiVeDAO = new LoaiVeDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Tạo khoảng cách với mép cửa sổ

        // --- Tiêu đề trên cùng ---
        JLabel lblTitle = new JLabel("QUẢN LÝ THÔNG TIN LOẠI VÉ");
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // --- Center: Bảng dữ liệu ---
        khoiTaoBang();
        JScrollPane scrollPane = new JScrollPane(tableLoaiVe);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Danh sách loại vé",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.ITALIC, 12)));
        add(scrollPane, BorderLayout.CENTER);

        // --- East: Panel Chi tiết (Giới hạn kích thước) ---
        add(taoPanelChiTiet(), BorderLayout.EAST);

        taiDuLieuVaoBang();
    }

    private void khoiTaoBang() {
        String[] columns = {"Mã Vé", "Tên Loại", "Giảm Giá (%)", "Tuổi Min", "Tuổi Max"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableLoaiVe = new JTable(tableModel);
        tableLoaiVe.setRowHeight(25);
        tableLoaiVe.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Chỉnh độ rộng cột
        tableLoaiVe.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableLoaiVe.getColumnModel().getColumn(2).setPreferredWidth(100);

        tableLoaiVe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableLoaiVe.getSelectedRow();
                if (row >= 0) hienThiChiTiet(row);
            }
        });
    }

    private JPanel taoPanelChiTiet() {
        // Cố định chiều rộng là 350px
        JPanel mainSidePanel = new JPanel(new BorderLayout());
        mainSidePanel.setPreferredSize(new Dimension(350, 0));
        mainSidePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "Thông tin chi tiết"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Khởi tạo components
        txtMaLoaiVe = new JTextField();
        txtMaLoaiVe.setEditable(false);
        txtMaLoaiVe.setBackground(new Color(240, 240, 240));
        txtTenLoai = new JTextField();
        txtMucGiaGiam = new JTextField();
        spinTuoiMin = new JSpinner(new SpinnerNumberModel(0, 0, 150, 1));
        spinTuoiMax = new JSpinner(new SpinnerNumberModel(100, 0, 150, 1));

        // Thêm các dòng vào form
        addFormField(form, "Mã Loại Vé:", txtMaLoaiVe, gbc, 0);
        addFormField(form, "Tên Loại:", txtTenLoai, gbc, 1);
        addFormField(form, "Hệ số giảm (0-1):", txtMucGiaGiam, gbc, 2);
        addFormField(form, "Tuổi tối thiểu:", spinTuoiMin, gbc, 3);
        addFormField(form, "Tuổi tối đa:", spinTuoiMax, gbc, 4);

        // Nút chức năng
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnCapNhat = new JButton("Cập Nhật");
        btnCapNhat.setPreferredSize(new Dimension(100, 30));
        btnCapNhat.setBackground(PRIMARY_COLOR);
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setEnabled(false);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setPreferredSize(new Dimension(100, 30));

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnCapNhat);

        btnCapNhat.addActionListener(e -> capNhatLoaiVe());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        mainSidePanel.add(form, BorderLayout.NORTH);
        mainSidePanel.add(pnlButtons, BorderLayout.SOUTH);

        return mainSidePanel;
    }

    private void addFormField(JPanel pnl, String label, JComponent comp, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        pnl.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        comp.setPreferredSize(new Dimension(0, 25));
        pnl.add(comp, gbc);
    }

    // --- Các hàm Logic xử lý giữ nguyên từ code của bạn ---

    private void taiDuLieuVaoBang() {
        tableModel.setRowCount(0);
        List<LoaiVe> danhSach = loaiVeDAO.getAllLoaiVe();
        for (LoaiVe lv : danhSach) {
            double phanTramGiam = (1.0 - lv.getMucGiamGia()) * 100;
            String phanTramGiamHienThi = String.format("%.0f%%", phanTramGiam);
            tableModel.addRow(new Object[]{
                    lv.getMaLoaiVe(), lv.getTenLoai(), phanTramGiamHienThi, lv.getTuoiMin(), lv.getTuoiMax()
            });
        }
    }

    private void hienThiChiTiet(int rowIndex) {
        txtMaLoaiVe.setText(tableModel.getValueAt(rowIndex, 0).toString());
        txtTenLoai.setText(tableModel.getValueAt(rowIndex, 1).toString());

        String giamGiaHienThi = tableModel.getValueAt(rowIndex, 2).toString();
        double giamGiaPhanTram = Double.parseDouble(giamGiaHienThi.replace("%", ""));
        double heSoGiam = 1.0 - (giamGiaPhanTram / 100.0);
        txtMucGiaGiam.setText(String.format("%.2f", heSoGiam));

        spinTuoiMin.setValue(tableModel.getValueAt(rowIndex, 3));
        spinTuoiMax.setValue(tableModel.getValueAt(rowIndex, 4));

        btnCapNhat.setEnabled(true);
    }

    private void lamMoiForm() {
        txtMaLoaiVe.setText("");
        txtTenLoai.setText("");
        txtMucGiaGiam.setText("");
        spinTuoiMin.setValue(0);
        spinTuoiMax.setValue(100);
        tableLoaiVe.clearSelection();
        btnCapNhat.setEnabled(false);
    }

    private void capNhatLoaiVe() {
        LoaiVe updated = layDuLieuTuForm();
        if (updated != null) {
            if (loaiVeDAO.updateLoaiVe(updated)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                taiDuLieuVaoBang();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private LoaiVe layDuLieuTuForm() {
        try {
            String ma = txtMaLoaiVe.getText().trim();
            String ten = txtTenLoai.getText().trim();
            double heSo = Double.parseDouble(txtMucGiaGiam.getText().trim().replace(",", "."));
            int min = (int) spinTuoiMin.getValue();
            int max = (int) spinTuoiMax.getValue();

            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên không được để trống.");
                return null;
            }
            return new LoaiVe(ma, ten, heSo, min, max);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra lại định dạng số!");
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        JFrame frame = new JFrame("Quản Lý Loại Vé");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new ManHinhQuanLyLoaiVe());
        frame.setSize(1000, 500); // Kích thước tổng thể hợp lý
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}