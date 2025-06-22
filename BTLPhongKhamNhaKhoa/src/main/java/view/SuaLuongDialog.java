//package view;
//
//import com.toedter.calendar.JDateChooser;
//import javax.swing.*;
//import java.awt.*;
//import java.util.Date;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import controller.LuongController;
//
//public class SuaLuongDialog extends JDialog {
//    private JTextField txtIdLuong;
//    private JComboBox<String> cboNhanVien;
//    private JDateChooser dateChooserThangNam;
//    private JTextField txtLuongCoBan;
//    private JTextField txtThuong;
//    private JTextField txtKhauTru;
//    private JButton btnSua;
//    private JButton btnHuy;
//    private LuongUI mainUI;
//    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
//    private LuongController luongController;
//
//    public SuaLuongDialog(JFrame parent, Object[] data, LuongUI mainUI, LuongController luongController) {
//        super(parent, "Sửa Thông Tin Lương", true);
//        this.mainUI = mainUI;
//        this.luongController = luongController;
//        
//        // Sử dụng GridBagLayout thay vì FlowLayout để bố trí rõ ràng hơn
//        setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        // ID Lương
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        add(new JLabel("ID:"), gbc);
//        
//        gbc.gridx = 1;
//        gbc.gridy = 0;
//        txtIdLuong = new JTextField(10);
//        txtIdLuong.setText(data[0].toString());
//        txtIdLuong.setEnabled(false);
//        add(txtIdLuong, gbc);
//
//        // Nhân Viên (ID và Họ Tên)
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        add(new JLabel("Nhân Viên:"), gbc);
//        
//        gbc.gridx = 1;
//        gbc.gridy = 1;
//        // Thay thế TextField bằng ComboBox để chọn nhân viên
//        cboNhanVien = new JComboBox<>();
//        luongController.loadNhanVienComboBox(cboNhanVien);
//        // Chọn nhân viên hiện tại trong ComboBox
//        cboNhanVien.setSelectedItem(data[1].toString());
//        add(cboNhanVien, gbc);
//
//        // Tháng/Năm
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        add(new JLabel("Tháng/Năm:"), gbc);
//        
//        gbc.gridx = 1;
//        gbc.gridy = 2;
//        dateChooserThangNam = new JDateChooser();
//        dateChooserThangNam.setPreferredSize(new Dimension(150, 25));
//        dateChooserThangNam.setDateFormatString("MM/yyyy");
//        try {
//            if (data[2] != null) {
//                if (data[2] instanceof Date) {
//                    dateChooserThangNam.setDate((Date) data[2]);
//                } else {
//                    dateChooserThangNam.setDate(monthYearFormat.parse(data[2].toString()));
//                }
//            }
//        } catch (ParseException e) {
//            dateChooserThangNam.setDate(new Date());
//            e.printStackTrace();
//        }
//        add(dateChooserThangNam, gbc);
//
//        // Lương Cơ Bản
//        gbc.gridx = 0;
//        gbc.gridy = 3;
//        add(new JLabel("Lương Cơ Bản:"), gbc);
//        
//        gbc.gridx = 1;
//        gbc.gridy = 3;
//        txtLuongCoBan = new JTextField(15);
//        // Chuyển số định dạng thành chuỗi không định dạng để dễ chỉnh sửa
//        try {
//            double luongCoBan = 0;
//            if (data[3] instanceof Number) {
//                luongCoBan = ((Number) data[3]).doubleValue();
//            } else {
//                String luongStr = data[3].toString().replaceAll("[^\\d]", "");
//                luongCoBan = Double.parseDouble(luongStr);
//            }
//            txtLuongCoBan.setText(String.valueOf(luongCoBan));
//        } catch (Exception e) {
//            txtLuongCoBan.setText("0");
//        }
//        add(txtLuongCoBan, gbc);
//
//        // Thưởng
//        gbc.gridx = 0;
//        gbc.gridy = 4;
//        add(new JLabel("Thưởng:"), gbc);
//        
//        gbc.gridx = 1;
//        gbc.gridy = 4;
//        txtThuong = new JTextField(15);
//        try {
//            double thuong = 0;
//            if (data[4] instanceof Number) {
//                thuong = ((Number) data[4]).doubleValue();
//            } else {
//                String thuongStr = data[4].toString().replaceAll("[^\\d]", "");
//                thuong = Double.parseDouble(thuongStr);
//            }
//            txtThuong.setText(String.valueOf(thuong));
//        } catch (Exception e) {
//            txtThuong.setText("0");
//        }
//        add(txtThuong, gbc);
//
//        // Khấu Trừ
//        gbc.gridx = 0;
//        gbc.gridy = 5;
//        add(new JLabel("Khấu Trừ:"), gbc);
//        
//        gbc.gridx = 1;
//        gbc.gridy = 5;
//        txtKhauTru = new JTextField(15);
//        try {
//            double khauTru = 0;
//            if (data[5] instanceof Number) {
//                khauTru = ((Number) data[5]).doubleValue();
//            } else {
//                String khauTruStr = data[5].toString().replaceAll("[^\\d]", "");
//                khauTru = Double.parseDouble(khauTruStr);
//            }
//            txtKhauTru.setText(String.valueOf(khauTru));
//        } catch (Exception e) {
//            txtKhauTru.setText("0");
//        }
//        add(txtKhauTru, gbc);
//
//        // Panel cho các nút
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        btnSua = new JButton("Sửa");
//        btnHuy = new JButton("Hủy");
//        buttonPanel.add(btnSua);
//        buttonPanel.add(btnHuy);
//        
//        gbc.gridx = 0;
//        gbc.gridy = 6;
//        gbc.gridwidth = 2;
//        add(buttonPanel, gbc);
//
//        btnSua.addActionListener(e -> {
//            String idLuongStr = txtIdLuong.getText();
//            String nhanVienSelected = (String) cboNhanVien.getSelectedItem();
//            int idNguoiDung = luongController.getIdNguoiDungByHoTen(nhanVienSelected);
//            Date thangNam = dateChooserThangNam.getDate();
//            String luongCoBanStr = txtLuongCoBan.getText();
//            String thuongStr = txtThuong.getText();
//            String khauTruStr = txtKhauTru.getText();
//            
//            if (!idLuongStr.isEmpty() && idNguoiDung > 0 && thangNam != null && !luongCoBanStr.isEmpty() && !thuongStr.isEmpty() && !khauTruStr.isEmpty()) {
//                try {
//                    int idLuong = Integer.parseInt(idLuongStr);
//                    java.sql.Date sqlDateThangNam = new java.sql.Date(thangNam.getTime());
//                    double luongCoBan = Double.parseDouble(luongCoBanStr);
//                    double thuong = Double.parseDouble(thuongStr);
//                    double khauTru = Double.parseDouble(khauTruStr);
//                    luongController.suaLuong(idLuong, idNguoiDung, sqlDateThangNam, luongCoBan, thuong, khauTru);
//                    dispose();
//                } catch (NumberFormatException ex) {
//                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        btnHuy.addActionListener(e -> dispose());
//
//        setSize(400, 300);
//        setLocationRelativeTo(parent);
//    }
//}