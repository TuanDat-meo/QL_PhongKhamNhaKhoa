package view;

import javax.swing.*;
import java.awt.*;

public class ThongKeLichHenKhachHangPanel extends JPanel {

    public ThongKeLichHenKhachHangPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel labelLichHenKhachHang = new JLabel("Nội dung thống kê Lịch hẹn & Khách hàng");
        add(labelLichHenKhachHang);
        // Thêm các component và logic hiển thị thống kê lịch hẹn và khách hàng tại đây
    }
}