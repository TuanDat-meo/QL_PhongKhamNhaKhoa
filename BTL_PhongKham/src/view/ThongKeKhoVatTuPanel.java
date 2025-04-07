package view;

import javax.swing.*;
import java.awt.*;

public class ThongKeKhoVatTuPanel extends JPanel {

    public ThongKeKhoVatTuPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel labelKhoVatTu = new JLabel("Nội dung thống kê Kho vật tư");
        add(labelKhoVatTu);
        // Thêm các component và logic hiển thị thống kê kho vật tư tại đây
    }
}