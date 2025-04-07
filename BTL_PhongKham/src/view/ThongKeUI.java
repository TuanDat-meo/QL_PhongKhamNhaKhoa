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

        tabbedPane = new JTabbedPane();

        // Khởi tạo các panel riêng biệt
        doanhThuPanel = new ThongKeDoanhThuPanel();
        bacSiPanel = new ThongKeBacSiPanel();
        lichHenKhachHangPanel = new ThongKeLichHenKhachHangPanel();
        khoVatTuPanel = new ThongKeKhoVatTuPanel();

        // Thêm các panel vào JTabbedPane với tiêu đề tương ứng
        tabbedPane.addTab("Doanh Thu", doanhThuPanel);
        tabbedPane.addTab("Bác sĩ", bacSiPanel);
        tabbedPane.addTab("Lịch hẹn & Khách hàng", lichHenKhachHangPanel);
        tabbedPane.addTab("Kho vật tư", khoVatTuPanel);

        add(tabbedPane, BorderLayout.CENTER);

        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Thống Kê");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new ThongKeUI());
            frame.setSize(800, 600);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}