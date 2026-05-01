package JDBC_Project.gui.Panel.ManHinhTab;

import dao.LoaiToaDAO;
import entity.LoaiToa;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ManHinhQuanLyLoaiToa extends JPanel {

    private final LoaiToaDAO loaiToaDAO;
    private JTable tableLoaiToa;
    private DefaultTableModel tableModel;

    private JTextField txtMaLoaiToa, txtTenLoaiToa, txtHeSo;
    private JButton btnCapNhat, btnLamMoi;

    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);

    public ManHinhQuanLyLoaiToa() {
        loaiToaDAO = new LoaiToaDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Tiêu đề ---
        JLabel lblTitle = new JLabel("QUẢN LÝ LOẠI TOA TÀU");
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // --- Center: Bảng dữ liệu ---
        khoiTaoBang();
        JScrollPane scrollPane = new JScrollPane(tableLoaiToa);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Danh sách các loại toa hiện có",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.ITALIC, 12)));
        add(scrollPane, BorderLayout.CENTER);

        // --- East: Panel Chi tiết ---
        add(taoPanelChiTiet(), BorderLayout.EAST);

        taiDuLieuVaoBang();
    }

    private void khoiTaoBang() {
        String[] columns = {"Mã Loại Toa", "Tên Loại Toa", "Hệ Số Giá"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableLoaiToa = new JTable(tableModel);
        tableLoaiToa.setRowHeight(30);
        tableLoaiToa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableLoaiToa.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        tableLoaiToa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableLoaiToa.getSelectedRow();
                if (row >= 0) hienThiChiTiet(row);
            }
        });
    }

    private JPanel taoPanelChiTiet() {
        JPanel mainSidePanel = new JPanel(new BorderLayout());
        mainSidePanel.setPreferredSize(new Dimension(350, 0));
        mainSidePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "Thông tin loại toa"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaLoaiToa = new JTextField();
        txtMaLoaiToa.setEditable(false); // Mã thường không cho sửa trực tiếp
        txtMaLoaiToa.setBackground(new Color(240, 240, 240));

        txtTenLoaiToa = new JTextField();
        txtHeSo = new JTextField();

        addFormField(form, "Mã Loại Toa:", txtMaLoaiToa, gbc, 0);
        addFormField(form, "Tên Loại Toa:", txtTenLoaiToa, gbc, 1);
        addFormField(form, "Hệ Số Nhân:", txtHeSo, gbc, 2);

        // Panel chứa nút
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnCapNhat = new JButton("Cập Nhật");
        btnCapNhat.setPreferredSize(new Dimension(100, 35));
        btnCapNhat.setBackground(PRIMARY_COLOR);
        btnCapNhat.setEnabled(false);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setPreferredSize(new Dimension(100, 35));

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnCapNhat);

        btnCapNhat.addActionListener(e -> capNhatLoaiToa());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        mainSidePanel.add(form, BorderLayout.NORTH);
        mainSidePanel.add(pnlButtons, BorderLayout.SOUTH);

        return mainSidePanel;
    }

    private void addFormField(JPanel pnl, String label, JComponent comp, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        pnl.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        comp.setPreferredSize(new Dimension(0, 30));
        pnl.add(comp, gbc);
    }

    private void taiDuLieuVaoBang() {
        tableModel.setRowCount(0);
        // Lưu ý: Đảm bảo LoaiToaDAO có phương thức getAllLoaiToa()
        List<LoaiToa> ds = loaiToaDAO.getAllLoaiToa();
        for (LoaiToa l : ds) {
            tableModel.addRow(new Object[]{ l.getMaLoaiCho(), l.getTenLoaiCho(), l.getHeSo() });
        }
    }

    private void hienThiChiTiet(int row) {
        txtMaLoaiToa.setText(tableModel.getValueAt(row, 0).toString());
        txtTenLoaiToa.setText(tableModel.getValueAt(row, 1).toString());
        txtHeSo.setText(tableModel.getValueAt(row, 2).toString());
        btnCapNhat.setEnabled(true);
    }

    private void lamMoiForm() {
        txtMaLoaiToa.setText("");
        txtTenLoaiToa.setText("");
        txtHeSo.setText("");
        tableLoaiToa.clearSelection();
        btnCapNhat.setEnabled(false);
    }

    private void capNhatLoaiToa() {
        try {
            String ma = txtMaLoaiToa.getText();
            String ten = txtTenLoaiToa.getText();
            double heSo = Double.parseDouble(txtHeSo.getText().trim());

            LoaiToa loai = new LoaiToa(ma, ten, heSo);
            // Lưu ý: Đảm bảo LoaiToaDAO có phương thức updateLoaiToa()
            if (loaiToaDAO.updateLoaiToa(loai)) {
                JOptionPane.showMessageDialog(this, "Cập nhật loại toa thành công!");
                taiDuLieuVaoBang();
                lamMoiForm();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Hệ số giá phải là số thực hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Thiết lập giao diện giống hệ điều hành
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        JFrame frame = new JFrame("Hệ Thống Quản Lý Toa Tàu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new ManHinhQuanLyLoaiToa());
        frame.setVisible(true);
    }
}