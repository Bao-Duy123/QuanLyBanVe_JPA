package JPA_Project.test;

import JPA_Project.gui.ManHinhQuanLyGiaVeJPA;

import javax.swing.*;

public class TestManHinhQuanLyGiaVeJPA {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hệ Thống Quản Lý Giá Vé - JPA Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ManHinhQuanLyGiaVeJPA panel = new ManHinhQuanLyGiaVeJPA();
            frame.setContentPane(panel);

            frame.setSize(1200, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    panel.shutdown();
                }
            });
        });
    }
}
