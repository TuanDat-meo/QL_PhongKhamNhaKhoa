package view;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SuaDoanhThuDialog extends JDialog {
    private JTextField txtIdDoanhThu;
    private JDateChooser dateChooserThangNam;
    private JTextField txtTongDoanhThu;
    private JTextField txtIdHoaDon;
    private JButton btnSua;
    private JButton btnHuy;
    private DoanhThuUI mainUI;
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");

    public SuaDoanhThuDialog(JFrame parent, Object[] data, DoanhThuUI mainUI) { // Thêm JFrame parent vào constructor
        super(parent, "Sửa Thông Tin Doanh Thu", true); // Gọi super với parent, title và modal
        this.mainUI = mainUI;
        setLayout(new FlowLayout());

        // Label và TextField cho ID Doanh Thu (không cho phép sửa)
        add(new JLabel("ID:"));
        txtIdDoanhThu = new JTextField(8);
        txtIdDoanhThu.setText(data[0].toString());
        txtIdDoanhThu.setEnabled(false); // Vô hiệu hóa để không cho người dùng sửa ID
        add(txtIdDoanhThu);

        // Label và JDateChooser cho Tháng/Năm
        add(new JLabel("Tháng/Năm:"));
        dateChooserThangNam = new JDateChooser();
        try {
            // Chuyển đổi chuỗi Tháng/Năm từ bảng sang đối tượng Date
            dateChooserThangNam.setDate(monthYearFormat.parse(data[3].toString())); // Sử dụng index 3 vì cột "Tháng/Năm" ở index 3
        } catch (ParseException e) {
            dateChooserThangNam.setDate(null);
            e.printStackTrace(); // In lỗi để debug nếu cần
        }
        add(dateChooserThangNam);

        // Label và TextField cho Tổng Thu
        add(new JLabel("Tổng Thu:"));
        txtTongDoanhThu = new JTextField(15);
        txtTongDoanhThu.setText(data[4].toString()); // Sử dụng index 4 vì cột "Tổng Thu" ở index 4
        add(txtTongDoanhThu);

        // Label và TextField cho ID Hóa Đơn
        add(new JLabel("ID Hóa Đơn:"));
        txtIdHoaDon = new JTextField(10);
        txtIdHoaDon.setText(data[1].toString()); // Sử dụng index 1 vì cột "ID Hóa Đơn" ở index 1
        add(txtIdHoaDon);

        // Nút Sửa và Hủy
        btnSua = new JButton("Sửa");
        btnHuy = new JButton("Hủy");
        add(btnSua);
        add(btnHuy);

        // ActionListener cho nút Sửa
        btnSua.addActionListener(e -> {
            String idDoanhThuStr = txtIdDoanhThu.getText();
            Date thangNam = dateChooserThangNam.getDate();
            String tongDoanhThuStr = txtTongDoanhThu.getText();
            String idHoaDonStr = txtIdHoaDon.getText();
            if (!idDoanhThuStr.isEmpty() && thangNam != null && !tongDoanhThuStr.isEmpty() && !idHoaDonStr.isEmpty()) {
                try {
                    int idDoanhThu = Integer.parseInt(idDoanhThuStr);
                    double tongDoanhThu = Double.parseDouble(tongDoanhThuStr);
                    int idHoaDon = Integer.parseInt(idHoaDonStr);
                    mainUI.getDoanhThuController().suaDoanhThu(idDoanhThu, thangNam, tongDoanhThu, idHoaDon);
                    mainUI.getDoanhThuController().loadDoanhThuData(); // Tải lại dữ liệu trên giao diện chính
                    dispose(); // Đóng dialog sau khi sửa thành công
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho Tổng Thu và ID Hóa Đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ActionListener cho nút Hủy
        btnHuy.addActionListener(e -> dispose()); // Đóng dialog khi nhấn nút Hủy

        pack(); // Đặt kích thước cửa sổ vừa đủ để chứa các thành phần
        setLocationRelativeTo(parent); // Hiển thị dialog ở giữa cửa sổ cha
    }
}