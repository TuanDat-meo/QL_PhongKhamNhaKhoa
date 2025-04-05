package view;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class ThemDoanhThuDialog extends JDialog {
    private JDateChooser dateChooserThangNam;
    private JTextField txtTongDoanhThu;
    private JTextField txtIdHoaDon;
    private JButton btnThem;
    private JButton btnHuy;
    private DoanhThuUI mainUI;

    public ThemDoanhThuDialog(JFrame parent, DoanhThuUI mainUI) { // Thay đổi tham số constructor
        super(parent, "Thêm Mới Doanh Thu", true); // Gọi super với JFrame cha, tiêu đề và modal
        this.mainUI = mainUI;
        setLayout(new FlowLayout());

        add(new JLabel("Tháng/Năm:"));
        dateChooserThangNam = new JDateChooser();
        add(dateChooserThangNam);

        add(new JLabel("Tổng Thu:"));
        txtTongDoanhThu = new JTextField(15);
        add(txtTongDoanhThu);

        add(new JLabel("ID Hóa Đơn:"));
        txtIdHoaDon = new JTextField(10);
        add(txtIdHoaDon);

        btnThem = new JButton("Thêm");
        btnHuy = new JButton("Hủy");
        add(btnThem);
        add(btnHuy);

        btnThem.addActionListener(e -> {
            Date thangNam = dateChooserThangNam.getDate();
            String tongDoanhThuStr = txtTongDoanhThu.getText();
            String idHoaDonStr = txtIdHoaDon.getText();
            if (thangNam != null && !tongDoanhThuStr.isEmpty() && !idHoaDonStr.isEmpty()) {
                try {
                    double tongDoanhThu = Double.parseDouble(tongDoanhThuStr);
                    int idHoaDon = Integer.parseInt(idHoaDonStr);
                    mainUI.getDoanhThuController().themDoanhThu(null, tongDoanhThu, idHoaDon);
                    mainUI.getDoanhThuController().loadDoanhThuData(); // Load lại dữ liệu
                    dispose(); // Đóng dialog sau khi thêm thành công
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent); // Sử dụng JFrame parent để định vị
        setVisible(true); // Đặt dialog hiển thị sau khi đã cấu hình xong
    }
}