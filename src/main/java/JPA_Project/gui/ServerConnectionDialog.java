package JPA_Project.gui;

import JPA_Project.network.NetworkManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ServerConnectionDialog - Dialog để kết nối đến Server
 * Cho phép chọn chế độ:
 * - Local Mode: Chạy độc lập, không cần server
 * - Server Mode: Kết nối đến server để đồng bộ realtime
 */
public class ServerConnectionDialog extends JDialog {
    
    private JRadioButton rdoLocalMode;
    private JRadioButton rdoServerMode;
    private JTextField txtServerHost;
    private JSpinner spnPort;
    private JButton btnConnect;
    private JButton btnSkip;
    private JLabel lblStatus;
    
    private boolean connected = false;
    private boolean localMode = false;
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;
    
    public ServerConnectionDialog(Frame parent) {
        super(parent, "Kết nối Server", true);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(450, 300);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        
        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel lblTitle = new JLabel("CHỌN CHẾ ĐỘ KẾT NỐI");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // ===== MAIN CONTENT =====
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        // Mode selection
        JPanel modePanel = new JPanel();
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
        modePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rdoLocalMode = new JRadioButton("Chế độ Local (Không cần server)");
        rdoLocalMode.setFont(new Font("Arial", Font.PLAIN, 14));
        rdoLocalMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        rdoLocalMode.setSelected(true);
        modePanel.add(rdoLocalMode);
        
        rdoServerMode = new JRadioButton("Chế độ Server (Kết nối nhiều máy)");
        rdoServerMode.setFont(new Font("Arial", Font.PLAIN, 14));
        rdoServerMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        modePanel.add(rdoServerMode);
        
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(rdoLocalMode);
        modeGroup.add(rdoServerMode);
        
        contentPanel.add(modePanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Server settings (collapsible based on mode)
        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.Y_AXIS));
        serverPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        serverPanel.setBorder(BorderFactory.createTitledBorder("Cài đặt Server"));
        
        JPanel hostPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        hostPanel.setBackground(Color.WHITE);
        hostPanel.add(new JLabel("Địa chỉ Server:"));
        txtServerHost = new JTextField(DEFAULT_HOST, 15);
        hostPanel.add(txtServerHost);
        serverPanel.add(hostPanel);
        
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        portPanel.setBackground(Color.WHITE);
        portPanel.add(new JLabel("Port:"));
        spnPort = new JSpinner(new SpinnerNumberModel(DEFAULT_PORT, 1, 65535, 1));
        ((JSpinner.DefaultEditor) spnPort.getEditor()).getTextField().setColumns(10);
        portPanel.add(spnPort);
        serverPanel.add(portPanel);
        
        contentPanel.add(serverPanel);
        
        // Status
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 12));
        lblStatus.setForeground(Color.GRAY);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lblStatus);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // ===== FOOTER =====
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        btnSkip = new JButton("Tiếp tục (Local)");
        btnSkip.setPreferredSize(new Dimension(140, 35));
        btnSkip.addActionListener(e -> {
            localMode = true;
            connected = true;
            dispose();
        });
        footerPanel.add(btnSkip);
        
        btnConnect = new JButton("Kết nối Server");
        btnConnect.setPreferredSize(new Dimension(140, 35));
        btnConnect.setBackground(new Color(46, 204, 113));
        btnConnect.setForeground(Color.WHITE);
        btnConnect.addActionListener(e -> connectToServer());
        footerPanel.add(btnConnect);
        
        add(footerPanel, BorderLayout.SOUTH);
        
        // Listeners
        rdoLocalMode.addActionListener(e -> updateUI());
        rdoServerMode.addActionListener(e -> updateUI());
        
        updateUI();
        
        // Enter key to connect
        txtServerHost.addActionListener(e -> connectToServer());
        getRootPane().setDefaultButton(btnConnect);
    }
    
    private void updateUI() {
        boolean serverMode = rdoServerMode.isSelected();
        txtServerHost.setEnabled(serverMode);
        spnPort.setEnabled(serverMode);
        btnConnect.setEnabled(serverMode);
        
        if (serverMode) {
            btnSkip.setText("Quay lại Local");
        } else {
            btnSkip.setText("Tiếp tục (Local)");
        }
        
        lblStatus.setText(" ");
    }
    
    private void connectToServer() {
        String host = txtServerHost.getText().trim();
        int port = (int) spnPort.getValue();
        
        lblStatus.setText("Đang kết nối...");
        lblStatus.setForeground(Color.BLUE);
        btnConnect.setEnabled(false);
        
        // Connect in background
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return NetworkManager.getInstance().connect(host, port, "AppClient");
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        lblStatus.setText("Kết nối thành công!");
                        lblStatus.setForeground(new Color(46, 204, 113));
                        connected = true;
                        localMode = false;
                        
                        // Delay close for user to see success
                        Timer timer = new Timer(1000, ev -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        lblStatus.setText("Kết nối thất bại! Kiểm tra server.");
                        lblStatus.setForeground(Color.RED);
                        btnConnect.setEnabled(true);
                    }
                } catch (Exception e) {
                    lblStatus.setText("Lỗi: " + e.getMessage());
                    lblStatus.setForeground(Color.RED);
                    btnConnect.setEnabled(true);
                }
            }
        }.execute();
    }
    
    public boolean isLocalMode() {
        return localMode;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public boolean isServerMode() {
        return !localMode && connected;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerConnectionDialog dialog = new ServerConnectionDialog(null);
            dialog.setVisible(true);
            
            if (dialog.isConnected()) {
                if (dialog.isLocalMode()) {
                    System.out.println("User chon che do LOCAL");
                } else {
                    System.out.println("Da ket noi server thanh cong!");
                }
            }
            System.exit(0);
        });
    }
}
