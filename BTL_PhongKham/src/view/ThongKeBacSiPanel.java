package view;

import javax.swing.*;
import java.awt.*;

public class ThongKeBacSiPanel extends JPanel {

    public ThongKeBacSiPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel labelBacSi = new JLabel("Nội dung thống kê Bác sĩ");
        add(labelBacSi);
        // Thêm các component và logic hiển thị thống kê bác sĩ tại đây
    }
}