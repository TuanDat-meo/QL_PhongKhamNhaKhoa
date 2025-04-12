package view;

import javax.swing.*;
import java.awt.*;

public class ThongTinNguoiDungUI extends JPanel {
    public ThongTinNguoiDungUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Thông tin người dùng giả định
        JLabel userInfoLabel = new JLabel("<html><b>Thông tin Người Dùng</b><br/>Tên: Nguyễn Văn A<br/>Chức vụ: Bác sĩ<br/>Email: nguyen.a@smilecare.com</html>");
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(userInfoLabel, BorderLayout.CENTER);
    }
}
