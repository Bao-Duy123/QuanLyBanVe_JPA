package JPA_Project.gui;

import JPA_Project.entity.Tuyen;
import JPA_Project.repository.TuyenRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DonGiaTuyenJPAPanel extends JPanel {

    private final TuyenRepository tuyenRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private JTable tableTuyen;
    private DefaultTableModel tableModel;
    private JTextField txtMaTuyen, txtTenTuyen, txtDonGia;
    private JButton btnCapNhat;
    private JLabel lblStatus;

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public DonGiaTuyenJPAPanel() {
        tuyenRepository = new TuyenRepository();
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        khoiTaoGiaoDien();
        taiDuLieu();
    }

    private void khoiTaoGiaoDien() {
        JLabel lblHeader = new JLabel("QUẢN LÝ ĐƠN GIÁ THEO TUYẾN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(41, 128, 185));
        add(lblHeader, BorderLayout.NORTH);

        khoiTaoBang();
        JScrollPane scrollPane = new JScrollPane(tableTuyen);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelDong = taoPanelNhapLieu();
        add(panelDong, BorderLayout.EAST);

        tableTuyen.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableTuyen.getSelectedRow();
                if (row >= 0) {
                    txtMaTuyen.setText(tableModel.getValueAt(row, 0).toString());
                    txtTenTuyen.setText(tableModel.getValueAt(row, 1).toString());
                    Object donGiaObj = tableModel.getValueAt(row, 2);
                    if (donGiaObj instanceof Number) {
                        txtDonGia.setText(String.valueOf(((Number) donGiaObj).intValue()));
                    } else {
                        txtDonGia.setText(donGiaObj.toString());
                    }
                }
            }
        });

        btnCapNhat.addActionListener(e -> capNhatDonGia());

        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.BLUE);
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void khoiTaoBang() {
        String[] columns = {"Mã Tuyến", "Tên Tuyến", "Đơn giá (VNĐ/KM)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableTuyen = new JTable(tableModel);
        tableTuyen.setRowHeight(30);
        tableTuyen.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableTuyen.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableTuyen.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        tableTuyen.getColumnModel().getColumn(0).setPreferredWidth(80);
        tableTuyen.getColumnModel().getColumn(0).setMaxWidth(100);
    }

    private JPanel taoPanelNhapLieu() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(320, 0));

        JPanel panelForm = new JPanel(new GridLayout(4, 1, 10, 10));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin chi tiết"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        txtMaTuyen = new JTextField();
        txtMaTuyen.setEditable(false);
        txtMaTuyen.setBackground(new Color(236, 240, 241));

        txtTenTuyen = new JTextField();
        txtTenTuyen.setEditable(false);
        txtTenTuyen.setBackground(new Color(236, 240, 241));

        txtDonGia = new JTextField();
        txtDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelForm.add(createFieldGroup("Mã Tuyến:", txtMaTuyen));
        panelForm.add(createFieldGroup("Tên Tuyến:", txtTenTuyen));
        panelForm.add(createFieldGroup("Đơn giá/KM (VNĐ):", txtDonGia));

        btnCapNhat = new JButton("LƯU THAY ĐỔI");
        btnCapNhat.setPreferredSize(new Dimension(0, 45));
        btnCapNhat.setBackground(new Color(46, 204, 113));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCapNhat.setFocusPainted(false);

        panel.add(panelForm, BorderLayout.NORTH);
        panel.add(btnCapNhat, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFieldGroup(String labelText, JTextField textField) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(label, BorderLayout.NORTH);
        p.add(textField, BorderLayout.CENTER);
        return p;
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        lblStatus.setText("Đang tải dữ liệu...");

        Future<List<Tuyen>> future = executor.submit(() -> tuyenRepository.findAllOrderByTenTuyen());

        SwingUtilities.invokeLater(() -> {
            try {
                List<Tuyen> danhSachTuyen = future.get();
                for (Tuyen t : danhSachTuyen) {
                    tableModel.addRow(new Object[]{t.getMaTuyen(), t.getTenTuyen(), t.getDonGiaKM()});
                }
                lblStatus.setText("Đã tải " + danhSachTuyen.size() + " tuyến");
            } catch (InterruptedException | ExecutionException e) {
                lblStatus.setText("Lỗi: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
            }
        });
    }

    private void capNhatDonGia() {
        String ma = txtMaTuyen.getText().trim();
        String giaStr = txtDonGia.getText().trim();

        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tuyến từ bảng!");
            return;
        }

        try {
            double giaMoi = Double.parseDouble(giaStr);
            if (giaMoi < 0) {
                JOptionPane.showMessageDialog(this, "Lỗi: Đơn giá không được âm!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Tuyen tuyen = Tuyen.builder()
                    .maTuyen(ma)
                    .donGiaKM((int) giaMoi)
                    .build();

            executor.execute(() -> {
                try {
                    tuyenRepository.save(tuyen);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Đã cập nhật đơn giá cho tuyến " + ma + " thành công!");
                        taiDuLieu();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Cập nhật thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    });
                }
            });

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Đơn giá phải là một con số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Đơn Giá Theo Tuyến - JPA");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new DonGiaTuyenJPAPanel());
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
