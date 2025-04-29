package view;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import controller.LuongController;

public class ThemLuongDialog extends JDialog {
    private JComboBox<String> cboNhanVien;
    private JDateChooser dateChooserThangNam;
    private JTextField txtLuongCoBan;
    private JTextField txtThuong;
    private JTextField txtKhauTru;
    private JButton btnThem;
    private JButton btnHuy;
    private LuongUI mainUI;
    private LuongController luongController;

    public ThemLuongDialog(JFrame parent, LuongUI mainUI, LuongController luongController) {
        super(parent, "Thêm Mới Lương", true);
        this.mainUI = mainUI;
        this.luongController = luongController;
        
        // Sử dụng GridBagLayout thay vì FlowLayout để bố trí rõ ràng hơn
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nhân Viên
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nhân Viên:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        // Sử dụng ComboBox thay vì TextField để chọn nhân viên
        cboNhanVien = new JComboBox<>();
        luongController.loadNhanVienComboBox(cboNhanVien);
        add(cboNhanVien, gbc);

        // Tháng/Năm
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Tháng/Năm:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        dateChooserThangNam = new JDateChooser();
        dateChooserThangNam.setPreferredSize(new Dimension(150, 25));
        dateChooserThangNam.setDateFormatString("MM/yyyy");
        dateChooserThangNam.setDate(new Date()); // Đặt ngày hiện tại làm mặc định
        add(dateChooserThangNam, gbc);

        // Lương Cơ Bản
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Lương Cơ Bản:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        txtLuongCoBan = new JTextField(15);
        add(txtLuongCoBan, gbc);

        // Thưởng
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Thưởng:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        txtThuong = new JTextField(15);
        txtThuong.setText("0"); // Đặt giá trị mặc định
        add(txtThuong, gbc);

        // Khấu Trừ
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Khấu Trừ:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        txtKhauTru = new JTextField(15);
        txtKhauTru.setText("0"); // Đặt giá trị mặc định
        add(txtKhauTru, gbc);

        // Panel cho các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnThem = new JButton("Thêm");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnThem);
        buttonPanel.add(btnHuy);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        btnThem.addActionListener(e -> {
            String nhanVienSelected = (String) cboNhanVien.getSelectedItem();
            int idNguoiDung = luongController.getIdNguoiDungByHoTen(nhanVienSelected);
            Date thangNam = dateChooserThangNam.getDate();
            String luongCoBanStr = txtLuongCoBan.getText();
            String thuongStr = txtThuong.getText();
            String khauTruStr = txtKhauTru.getText();
            
            if (idNguoiDung > 0 && thangNam != null && !luongCoBanStr.isEmpty() && !thuongStr.isEmpty() && !khauTruStr.isEmpty()) {
                try {
                    java.sql.Date sqlDateThangNam = new java.sql.Date(thangNam.getTime());
                    double luongCoBan = Double.parseDouble(luongCoBanStr);
                    double thuong = Double.parseDouble(thuongStr);
                    double khauTru = Double.parseDouble(khauTruStr);
                    luongController.themLuong(idNguoiDung, sqlDateThangNam, luongCoBan, thuong, khauTru);
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dispose());

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}