package JPA_Project.gui;

import JPA_Project.entity.LoaiToa;
import JPA_Project.repository.LoaiToaRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoaiToaJPAPanel extends JPanel {

    private final LoaiToaRepository loaiToaRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private JTable tableLoaiToa;
    private DefaultTableModel tableModel;

    private JTextField txtMaLoaiToa, txtTenLoaiToa, txtHeSo;
    private JButton btnCapNhat, btnLamMoi;
    private JLabel lblStatus;

    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);

    public LoaiToaJPAPanel() {
        loaiToaRepository = new LoaiToaRepository();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        khoiTaoGiaoDien();
        taiDuLieuVaoBang();
    }

    private void khoiTaoGiaoDien() {
        JLabel lblTitle = new JLabel("QUẢN LÝ LOẠI TOA TÀU");
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        khoiTaoBang();
        JScrollPane scrollPane = new JScrollPane(tableLoaiToa);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Danh sách các loại toa hiện có",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.ITALIC, 12)));
        add(scrollPane, BorderLayout.CENTER);

        add(taoPanelChiTiet(), BorderLayout.EAST);
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
        txtMaLoaiToa.setEditable(false);
        txtMaLoaiToa.setBackground(new Color(240, 240, 240));

        txtTenLoaiToa = new JTextField();
        txtHeSo = new JTextField();

        addFormField(form, "Mã Loại Toa:", txtMaLoaiToa, gbc, 0);
        addFormField(form, "Tên Loại Toa:", txtTenLoaiToa, gbc, 1);
        addFormField(form, "Hệ Số Nhân:", txtHeSo, gbc, 2);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnCapNhat = new JButton("Cập Nhật");
        btnCapNhat.setPreferredSize(new Dimension(100, 35));
        btnCapNhat.setBackground(PRIMARY_COLOR);
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setEnabled(false);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setPreferredSize(new Dimension(100, 35));

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnCapNhat);

        btnCapNhat.addActionListener(e -> capNhatLoaiToa());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.BLUE);

        mainSidePanel.add(form, BorderLayout.NORTH);
        mainSidePanel.add(lblStatus, BorderLayout.CENTER);
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
        lblStatus.setText("Đang tải dữ liệu...");

        Future<List<LoaiToa>> future = executor.submit(() -> loaiToaRepository.findAllOrderByTenLoaiToa());

        SwingUtilities.invokeLater(() -> {
            try {
                List<LoaiToa> ds = future.get();
                for (LoaiToa l : ds) {
                    tableModel.addRow(new Object[]{ l.getMaLoaiToa(), l.getTenLoaiToa(), l.getHeSo() });
                }
                lblStatus.setText("Đã tải " + ds.size() + " loại toa");
            } catch (InterruptedException | ExecutionException e) {
                lblStatus.setText("Lỗi: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
            }
        });
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

            LoaiToa loai = LoaiToa.builder()
                    .maLoaiToa(ma)
                    .tenLoaiToa(ten)
                    .heSo(heSo)
                    .build();

            executor.execute(() -> {
                try {
                    loaiToaRepository.save(loai);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Cập nhật loại toa thành công!");
                        taiDuLieuVaoBang();
                        lamMoiForm();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Hệ số giá phải là số thực hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        JFrame frame = new JFrame("Hệ Thống Quản Lý Toa Tàu - JPA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new LoaiToaJPAPanel());
        frame.setVisible(true);
    }
}
