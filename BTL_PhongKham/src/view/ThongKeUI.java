package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThongKeUI extends JPanel {
    private JTabbedPane tabbedPane;
    private ThongKeDoanhThuPanel doanhThuPanel;
    private ThongKeBacSiPanel bacSiPanel;
    private ThongKeLichHenKhachHangPanel lichHenKhachHangPanel;
    private ThongKeKhoVatTuPanel khoVatTuPanel;

    public ThongKeUI() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Khởi tạo các panel thống kê
        doanhThuPanel = new ThongKeDoanhThuPanel();
        bacSiPanel = new ThongKeBacSiPanel();
        lichHenKhachHangPanel = new ThongKeLichHenKhachHangPanel();
        khoVatTuPanel = new ThongKeKhoVatTuPanel();
        
        // Thêm các panel vào JTabbedPane với tiêu đề và icon
        ImageIcon doanhThuIcon = new ImageIcon(getClass().getResource("/icons/doanhthu.png"));
        ImageIcon bacSiIcon = new ImageIcon(getClass().getResource("/icons/bacsi.png"));
        ImageIcon lichHenIcon = new ImageIcon(getClass().getResource("/icons/lichhen.png"));
        ImageIcon khoVatTuIcon = new ImageIcon(getClass().getResource("/icons/khovattu.png"));
        
        tabbedPane.addTab("Doanh Thu", doanhThuIcon, doanhThuPanel);
        tabbedPane.addTab("Bác sĩ", bacSiIcon, bacSiPanel);
        tabbedPane.addTab("Lịch hẹn & Khách hàng", lichHenIcon, lichHenKhachHangPanel);
        tabbedPane.addTab("Kho vật tư", khoVatTuIcon, khoVatTuPanel);
        
        // Thiết lập tab appearance
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