package view;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import controller.LuongController; // Import LuongController

public class SuaLuongDialog extends JDialog {
    private JTextField txtIdLuong;
    private JTextField txtIdNguoiDung;
    private JDateChooser dateChooserThangNam;
    private JTextField txtLuongCoBan;
    private JTextField txtThuong;
    private JTextField txtKhauTru;
    private JButton btnSua;
    private JButton btnHuy;
    private LuongUI mainUI; // Thay DoanhThuUI bằng LuongUI
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    private LuongController luongController; // Thêm LuongController

    public SuaLuongDialog(JFrame parent, Object[] data, LuongUI mainUI, LuongController luongController) { // Cập nhật constructor
        super(parent, "Sửa Thông Tin Lương", true);
        this.mainUI = mainUI;
        this.luongController = luongController; // Khởi tạo LuongController
        setLayout(new FlowLayout());

        add(new JLabel("ID:"));
        txtIdLuong = new JTextField(8);
        txtIdLuong.setText(data[0].toString());
        txtIdLuong.setEnabled(false);
        add(txtIdLuong);

        add(new JLabel("ID Nhân Viên:"));
        txtIdNguoiDung = new JTextField(5);
        txtIdNguoiDung.setText(data[1].toString());
        add(txtIdNguoiDung);

        add(new JLabel("Tháng/Năm:"));
        dateChooserThangNam = new JDateChooser();
        try {
            dateChooserThangNam.setDate(monthYearFormat.parse(data[2].toString()));
        } catch (ParseException e) {
            dateChooserThangNam.setDate(null);
            e.printStackTrace();
        }
        add(dateChooserThangNam);

        add(new JLabel("Lương Cơ Bản:"));
        txtLuongCoBan = new JTextField(10);
        txtLuongCoBan.setText(data[3].toString());
        add(txtLuongCoBan);

        add(new JLabel("Thưởng:"));
        txtThuong = new JTextField(10);
        txtThuong.setText(data[4].toString());
        add(txtThuong);

        add(new JLabel("Khấu Trừ:"));
        txtKhauTru = new JTextField(10);
        txtKhauTru.setText(data[5].toString());
        add(txtKhauTru);

        btnSua = new JButton("Sửa");
        btnHuy = new JButton("Hủy");
        add(btnSua);
        add(btnHuy);

        btnSua.addActionListener(e -> {
            String idLuongStr = txtIdLuong.getText();
            String idNguoiDungStr = txtIdNguoiDung.getText();
            Date thangNam = dateChooserThangNam.getDate();
            String luongCoBanStr = txtLuongCoBan.getText();
            String thuongStr = txtThuong.getText();
            String khauTruStr = txtKhauTru.getText();
            if (!idLuongStr.isEmpty() && !idNguoiDungStr.isEmpty() && thangNam != null && !luongCoBanStr.isEmpty() && !thuongStr.isEmpty() && !khauTruStr.isEmpty()) {
                try {
                    int idLuong = Integer.parseInt(idLuongStr);
                    int idNguoiDung = Integer.parseInt(idNguoiDungStr);
                    java.sql.Date sqlDateThangNam = new java.sql.Date(thangNam.getTime());
                    double luongCoBan = Double.parseDouble(luongCoBanStr);
                    double thuong = Double.parseDouble(thuongStr);
                    double khauTru = Double.parseDouble(khauTruStr);
                    luongController.suaLuong(idLuong, idNguoiDung, sqlDateThangNam, luongCoBan, thuong, khauTru);
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