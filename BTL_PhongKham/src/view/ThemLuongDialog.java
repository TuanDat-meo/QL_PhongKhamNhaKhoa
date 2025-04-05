package view;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import controller.LuongController; // Import LuongController

public class ThemLuongDialog extends JDialog {
    private JTextField txtIdNguoiDung;
    private JDateChooser dateChooserThangNam;
    private JTextField txtLuongCoBan;
    private JTextField txtThuong;
    private JTextField txtKhauTru;
    private JButton btnThem;
    private JButton btnHuy;
    private LuongUI mainUI; // Thay DoanhThuUI bằng LuongUI
    private LuongController luongController; // Thêm LuongController

    public ThemLuongDialog(JFrame parent, LuongUI mainUI, LuongController luongController) { // Cập nhật constructor
        super(parent, "Thêm Mới Lương", true);
        this.mainUI = mainUI;
        this.luongController = luongController; // Khởi tạo LuongController
        setLayout(new FlowLayout());

        add(new JLabel("ID Nhân Viên:"));
        txtIdNguoiDung = new JTextField(5);
        add(txtIdNguoiDung);

        add(new JLabel("Tháng/Năm:"));
        dateChooserThangNam = new JDateChooser();
        add(dateChooserThangNam);

        add(new JLabel("Lương Cơ Bản:"));
        txtLuongCoBan = new JTextField(10);
        add(txtLuongCoBan);

        add(new JLabel("Thưởng:"));
        txtThuong = new JTextField(10);
        add(txtThuong);

        add(new JLabel("Khấu Trừ:"));
        txtKhauTru = new JTextField(10);
        add(txtKhauTru);

        btnThem = new JButton("Thêm");
        btnHuy = new JButton("Hủy");
        add(btnThem);
        add(btnHuy);

        btnThem.addActionListener(e -> {
            String idNguoiDungStr = txtIdNguoiDung.getText();
            Date thangNam = dateChooserThangNam.getDate();
            String luongCoBanStr = txtLuongCoBan.getText();
            String thuongStr = txtThuong.getText();
            String khauTruStr = txtKhauTru.getText();
            if (!idNguoiDungStr.isEmpty() && thangNam != null && !luongCoBanStr.isEmpty() && !thuongStr.isEmpty() && !khauTruStr.isEmpty()) {
                try {
                    int idNguoiDung = Integer.parseInt(idNguoiDungStr);
                    java.sql.Date sqlDateThangNam = new java.sql.Date(thangNam.getTime());
                    double luongCoBan = Double.parseDouble(luongCoBanStr);
                    double thuong = Double.parseDouble(thuongStr);
                    double khauTru = Double.parseDouble(khauTruStr);
                    luongController.themLuong(idNguoiDung, sqlDateThangNam, luongCoBan, thuong, khauTru);
                    mainUI.getLuongController().loadLuongData(); // Load lại dữ liệu thông qua LuongUI
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }
}