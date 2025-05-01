package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThongKeUI extends JPanel {
    private JTabbedPane tabbedPane;
    private ThongKeDoanhThuPanel doanhThuPanel;
    private ThongKeBacSiPanel bacSiPanel;
    private ThongKeLichHenPanel lichHenKhachHangPanel;
    private ThongKeKhoVatTuPanel khoVatTuPanel;

    public ThongKeUI() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        doanhThuPanel = new ThongKeDoanhThuPanel();
        bacSiPanel = new ThongKeBacSiPanel();
        lichHenKhachHangPanel = new ThongKeLichHenPanel();
        khoVatTuPanel = new ThongKeKhoVatTuPanel();
        tabbedPane.addTab("Doanh Thu", doanhThuPanel);
        tabbedPane.addTab("Bác sĩ", bacSiPanel);
        tabbedPane.addTab("Lịch hẹn & Khách hàng", lichHenKhachHangPanel);
        tabbedPane.addTab("Kho vật tư", khoVatTuPanel);

        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(240, 240, 240));

        add(tabbedPane, BorderLayout.CENTER);
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Phòng Khám - Thống Kê");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new ThongKeUI());
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}