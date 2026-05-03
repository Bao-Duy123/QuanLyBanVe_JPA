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
import java.util.List;

/**
 * ManHinhQuanLyDonGiaTuyenJPA - Quản lý đơn giá theo tuyến
 */
public class ManHinhQuanLyDonGiaTuyenJPA extends JPanel {
    
    private final TuyenRepository tuyenRepository;
    private JTable tableTuyen;
    private DefaultTableModel tableModel;
    private JTextField txtMaTuyen, txtTenTuyen, txtDonGia;
    private JButton btnCapNhat;
    
    public ManHinhQuanLyDonGiaTuyenJPA() {
        tuyenRepository = new TuyenRepository();
        
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Tiêu đề
        JLabel lblHeader = new JLabel("QUẢN LÝ ĐƠN GIÁ THEO TUYẾN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(41, 128, 185));
        add(lblHeader, BorderLayout.NORTH);
        
        // Bảng
        khoiTaoBang();
        JScrollPane scrollPane = new JScrollPane(tableTuyen);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel nhập liệu
        JPanel panelDong = taoPanelNhapLieu();
        add(panelDong, BorderLayout.EAST);
        
        // Sự kiện
        tableTuyen.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableTuyen.getSelectedRow();
                if (row >= 0) {
                    txtMaTuyen.setText(tableModel.getValueAt(row, 0).toString());
                    txtTenTuyen.setText(tableModel.getValueAt(row, 1).toString());
                    txtDonGia.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });
        
        btnCapNhat.addActionListener(e -> capNhatDonGia());
        
        taiDuLieu();
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
        panel.setBackground(Color.WHITE);
        
        JPanel panelForm = new JPanel(new GridLayout(4, 1, 10, 10));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin chi tiết"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panelForm.setBackground(Color.WHITE);
        
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
        p.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(label, BorderLayout.NORTH);
        p.add(textField, BorderLayout.CENTER);
        return p;
    }
    
    private void taiDuLieu() {
        tableModel.setRowCount(0);
        List<Tuyen> danhSachTuyen = tuyenRepository.findAll();
        for (Tuyen t : danhSachTuyen) {
            tableModel.addRow(new Object[]{
                    t.getMaTuyen(), 
                    t.getTenTuyen(), 
                    t.getDonGiaKM()
            });
        }
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
            
            Tuyen tuyen = tuyenRepository.findById(ma);
            if (tuyen != null) {
                tuyen.setDonGiaKM((int) giaMoi);
                tuyenRepository.save(tuyen);
                JOptionPane.showMessageDialog(this, "Đã cập nhật đơn giá cho tuyến " + ma + " thành công!");
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy tuyến cần cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Đơn giá phải là một con số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Đơn Giá Theo Tuyến - JPA");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ManHinhQuanLyDonGiaTuyenJPA());
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
