package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import controller.NguoiDungController;
import model.NguoiDung;

public class ThongTinNguoiDungUI extends JPanel {
    private JLabel idLabel;
    private JLabel hoTenLabel;
    private JLabel emailLabel;
    private JLabel soDienThoaiLabel;
    private JLabel ngaySinhLabel;
    private JLabel gioiTinhLabel;
    private JLabel vaiTroLabel;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private NguoiDungController nguoiDungController;
    private int userId; // Lưu trữ ID của người dùng cần hiển thị

    public ThongTinNguoiDungUI(int userId, NguoiDungController controller) {
        this.userId = userId;
        this.nguoiDungController = controller;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Tiêu đề
        JLabel titleLabel = new JLabel("Thông Tin Người Dùng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // ID Người Dùng
        JLabel idTitle = new JLabel("ID:");
        add(idTitle, gbc);
        gbc.gridx++;
        idLabel = new JLabel();
        add(idLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        // Họ và Tên
        JLabel hoTenTitle = new JLabel("Họ và Tên:");
        add(hoTenTitle, gbc);
        gbc.gridx++;
        hoTenLabel = new JLabel();
        add(hoTenLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        // Email
        JLabel emailTitle = new JLabel("Email:");
        add(emailTitle, gbc);
        gbc.gridx++;
        emailLabel = new JLabel();
        add(emailLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        // Số Điện Thoại
        JLabel soDienThoaiTitle = new JLabel("Số Điện Thoại:");
        add(soDienThoaiTitle, gbc);
        gbc.gridx++;
        soDienThoaiLabel = new JLabel();
        add(soDienThoaiLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        // Ngày Sinh
        JLabel ngaySinhTitle = new JLabel("Ngày Sinh:");
        add(ngaySinhTitle, gbc);
        gbc.gridx++;
        ngaySinhLabel = new JLabel();
        add(ngaySinhLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        // Giới Tính
        JLabel gioiTinhTitle = new JLabel("Giới Tính:");
        add(gioiTinhTitle, gbc);
        gbc.gridx++;
        gioiTinhLabel = new JLabel();
        add(gioiTinhLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        // Vai Trò
        JLabel vaiTroTitle = new JLabel("Vai Trò:");
        add(vaiTroTitle, gbc);
        gbc.gridx++;
        vaiTroLabel = new JLabel();
        add(vaiTroLabel, gbc);

        // Căn chỉnh các label bên phải
        gbc.anchor = GridBagConstraints.EAST;
        for (int i = 1; i < getComponentCount(); i += 2) {
            if (getComponent(i) instanceof JLabel) {
                ((JLabel) getComponent(i)).setHorizontalAlignment(SwingConstants.LEFT);
            }
        }

        // Load dữ liệu ban đầu
        loadUserData(userId);
    }

    public void loadUserData(int userId) {
        try {
            NguoiDung user = nguoiDungController.getNguoiDungById(userId);
            if (user != null) {
                idLabel.setText(String.valueOf(user.getIdNguoiDung()));
                hoTenLabel.setText(user.getHoTen());
                emailLabel.setText(user.getEmail());
                soDienThoaiLabel.setText(user.getSoDienThoai());
                ngaySinhLabel.setText(user.getNgaySinh() != null ? dateFormat.format(user.getNgaySinh()) : "");
                gioiTinhLabel.setText(user.getGioiTinh());
                vaiTroLabel.setText(user.getVaiTro());
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng với ID: " + userId, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu người dùng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                NguoiDungController controller = new NguoiDungController();
//                // Thay 1 bằng ID người dùng bạn muốn hiển thị
//                ThongTinNguoiDungUI panel = new ThongTinNguoiDungUI(1, controller);
//                JFrame frame = new JFrame("Thông tin Người Dùng");
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.getContentPane().add(panel);
//                frame.pack();
//                frame.setLocationRelativeTo(null);
//                frame.setVisible(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(null, "Lỗi khởi tạo: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//    }
}