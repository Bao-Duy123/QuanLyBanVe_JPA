package JPA_Project.gui;

import JPA_Project.entity.Ga;
import JPA_Project.repository.GaRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class ManHinhDoiVeJPA extends JPanel {

    private static final Font GLOBAL_FONT = new Font("Arial", Font.PLAIN, 13);

    private JPanel pnlListTickets;
    private JTextField txtNhapThongTin;
    private JComboBox<String> cboTimKiemTheo;
    private JComboBox<String> cboGaDi;
    private JComboBox<String> cboGaDen;
    private com.toedter.calendar.JDateChooser dateChooserNgayDi;
    private JButton btnDoiToanBo;
    
    private GaRepository gaRepository;

    public ManHinhDoiVeJPA() {
        gaRepository = new GaRepository();
        init();
    }

    private void init() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JPanel pnlTop = createTopPanel();
        add(pnlTop, BorderLayout.NORTH);

        JPanel pnlResultArea = createResultArea();
        add(pnlResultArea, BorderLayout.CENTER);

        loadDataToCombobox();
    }

    private JPanel createTopPanel() {
        JPanel pnlTop = new JPanel(new GridBagLayout());
        pnlTop.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        pnlTop.add(createLabel("Tìm kiếm theo:"), gbc);

        gbc.gridx = 1;
        String[] searchOptions = {"Mã vé", "Số điện thoại", "Số CCCD/Định danh"};
        cboTimKiemTheo = new JComboBox<>(searchOptions);
        cboTimKiemTheo.setFont(GLOBAL_FONT);
        cboTimKiemTheo.setPreferredSize(new Dimension(150, 25));
        pnlTop.add(cboTimKiemTheo, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(5, 30, 5, 5);
        pnlTop.add(createLabel("Nhập thông tin:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        txtNhapThongTin = new JTextField();
        txtNhapThongTin.setFont(GLOBAL_FONT);
        txtNhapThongTin.setPreferredSize(new Dimension(200, 25));
        pnlTop.add(txtNhapThongTin, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        JPanel pnlBtnRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton btnTim = createStyledButton("Tìm", new Color(66, 133, 244), Color.WHITE);
        JButton btnXoaInput = createStyledButton("Xóa", new Color(234, 67, 53), Color.WHITE);
        pnlBtnRow1.add(btnTim);
        pnlBtnRow1.add(btnXoaInput);
        pnlTop.add(pnlBtnRow1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(15, 10, 5, 5);
        pnlTop.add(createLabel("Ga đi:"), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(15, 5, 5, 5);
        cboGaDi = new JComboBox<>();
        cboGaDi.setFont(GLOBAL_FONT);
        cboGaDi.setPreferredSize(new Dimension(150, 25));
        pnlTop.add(cboGaDi, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(15, 30, 5, 5);
        pnlTop.add(createLabel("Ga đến:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 5, 5, 5);
        cboGaDen = new JComboBox<>();
        cboGaDen.setFont(GLOBAL_FONT);
        pnlTop.add(cboGaDen, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        pnlTop.add(createLabel("Ngày khởi hành:"), gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.5;
        dateChooserNgayDi = new com.toedter.calendar.JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setDate(new Date());
        dateChooserNgayDi.setPreferredSize(new Dimension(130, 25));
        pnlTop.add(dateChooserNgayDi, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        JPanel pnlBtnRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton btnLoc = createStyledButton("Lọc", new Color(66, 133, 244), Color.WHITE);
        JButton btnXoaLoc = createStyledButton("Xóa", new Color(234, 67, 53), Color.WHITE);
        pnlBtnRow2.add(btnLoc);
        pnlBtnRow2.add(btnXoaLoc);
        pnlTop.add(pnlBtnRow2, gbc);

        btnTim.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng tìm kiếm đang phát triển!");
        });
        btnXoaInput.addActionListener(e -> txtNhapThongTin.setText(""));
        btnLoc.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng lọc đang phát triển!"));
        btnXoaLoc.addActionListener(e -> {
            cboGaDi.setSelectedIndex(0);
            cboGaDen.setSelectedIndex(0);
            dateChooserNgayDi.setDate(new Date());
        });

        return pnlTop;
    }

    private JPanel createResultArea() {
        JPanel pnlResultArea = new JPanel(new BorderLayout());
        JPanel pnlResultHeader = new JPanel(new BorderLayout());
        pnlResultHeader.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel lblKetQuaHeader = new JLabel("Kết quả tìm kiếm");
        lblKetQuaHeader.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setFont(GLOBAL_FONT);
        pnlResultHeader.add(lblKetQuaHeader, BorderLayout.WEST);
        pnlResultHeader.add(btnLamMoi, BorderLayout.EAST);

        pnlListTickets = new JPanel(new GridLayout(0, 2, 20, 20));
        pnlListTickets.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlListTicketsWrapper = new JPanel(new BorderLayout());
        pnlListTicketsWrapper.add(pnlListTickets, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(pnlListTicketsWrapper);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        pnlResultArea.add(pnlResultHeader, BorderLayout.NORTH);
        pnlResultArea.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnXoaTrang = createStyledButton("Xóa trắng", new Color(234, 67, 53), Color.WHITE);
        btnDoiToanBo = createStyledButton("Đổi toàn bộ", new Color(40, 70, 220), Color.WHITE);
        btnDoiToanBo.setEnabled(false);

        pnlFooter.add(btnXoaTrang);
        pnlFooter.add(btnDoiToanBo);
        pnlResultArea.add(pnlFooter, BorderLayout.SOUTH);

        btnLamMoi.addActionListener(e -> {
            pnlListTickets.removeAll();
            pnlListTickets.revalidate();
            pnlListTickets.repaint();
            btnDoiToanBo.setEnabled(false);
        });
        btnXoaTrang.addActionListener(e -> {
            txtNhapThongTin.setText("");
            cboGaDi.setSelectedIndex(0);
            cboGaDen.setSelectedIndex(0);
            dateChooserNgayDi.setDate(new Date());
            pnlListTickets.removeAll();
            pnlListTickets.repaint();
            btnDoiToanBo.setEnabled(false);
        });

        return pnlResultArea;
    }

    private void loadDataToCombobox() {
        try {
            List<Ga> danhSachGa = gaRepository.findAll();
            Vector<String> modelGaDi = new Vector<>();
            Vector<String> modelGaDen = new Vector<>();
            modelGaDi.add("");
            modelGaDen.add("");

            if (danhSachGa != null) {
                for (Ga ga : danhSachGa) {
                    modelGaDi.add(ga.getTenGa());
                    modelGaDen.add(ga.getTenGa());
                }
            }
            cboGaDi.setModel(new DefaultComboBoxModel<>(modelGaDi));
            cboGaDen.setModel(new DefaultComboBoxModel<>(modelGaDen));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(GLOBAL_FONT);
        return lbl;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(GLOBAL_FONT);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 30));
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Màn Hình Đổi Vé - JPA");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ManHinhDoiVeJPA mainPanel = new ManHinhDoiVeJPA();
            frame.add(mainPanel);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
