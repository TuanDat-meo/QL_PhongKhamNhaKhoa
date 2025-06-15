package view;

import controller.NhaCungCapController;
import model.NhaCungCap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate; // Import cho LocalDate
import java.time.format.DateTimeFormatter; // Import cho định dạng ngày tháng
import java.time.format.DateTimeParseException; // Import cho lỗi phân tích ngày tháng

public class NhaCungCapDialog extends JDialog {

    private JTextField txtMaNCC;
    private JTextField txtTenNCC;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;
    private JTextField txtMaSoThue;   // Trường mới
    private JTextField txtNgayDangKy; // Trường mới
    
    private JButton btnLuu;
    private JButton btnHuy;
    private NhaCungCapController controller;
    private NhaCungCap nhaCungCapToEdit; // Đối tượng NCC nếu đang chỉnh sửa
    private NhaCungCapUI nhaCungCapUI;   // Tham chiếu đến UI cha để làm mới bảng

    public NhaCungCapDialog(JFrame parent, NhaCungCapController ctrl, NhaCungCap ncc, NhaCungCapUI ui) {
        super(parent, (ncc == null ? "Thêm Nhà Cung Cấp" : "Sửa Nhà Cung Cấp"), true);
        this.controller = ctrl;
        this.nhaCungCapToEdit = ncc;
        this.nhaCungCapUI = ui;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // Thay đổi số hàng trong GridLayout để chứa các trường mới (4 -> 6)
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        JLabel lblMaNCC = new JLabel("Mã NCC:");
        txtMaNCC = new JTextField(10);
        txtMaNCC.setEnabled(false); // Mã NCC không cho phép sửa thủ công

        JLabel lblTenNCC = new JLabel("Tên NCC:");
        txtTenNCC = new JTextField(20);
        JLabel lblDiaChi = new JLabel("Địa Chỉ:");
        txtDiaChi = new JTextField(30);
        JLabel lblSoDienThoai = new JLabel("Số Điện Thoại:");
        txtSoDienThoai = new JTextField(15);
        JLabel lblMaSoThue = new JLabel("Mã Số Thuế:");
        txtMaSoThue = new JTextField(15);
        JLabel lblNgayDangKy = new JLabel("Ngày Đăng Ký (YYYY-MM-DD):");
        txtNgayDangKy = new JTextField(15);

        inputPanel.add(lblMaNCC);
        inputPanel.add(txtMaNCC);
        inputPanel.add(lblTenNCC);
        inputPanel.add(txtTenNCC);
        inputPanel.add(lblDiaChi);
        inputPanel.add(txtDiaChi);
        inputPanel.add(lblSoDienThoai);
        inputPanel.add(txtSoDienThoai);
        inputPanel.add(lblMaSoThue);    // Thêm trường mới
        inputPanel.add(txtMaSoThue);
        inputPanel.add(lblNgayDangKy);  // Thêm trường mới
        inputPanel.add(txtNgayDangKy);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);

        contentPane.add(inputPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // Nếu là chế độ sửa, điền dữ liệu hiện có vào các trường
        if (nhaCungCapToEdit != null) {
            txtMaNCC.setText(nhaCungCapToEdit.getMaNCC());
            txtTenNCC.setText(nhaCungCapToEdit.getTenNCC());
            txtDiaChi.setText(nhaCungCapToEdit.getDiaChi());
            txtSoDienThoai.setText(nhaCungCapToEdit.getSoDienThoai());
            txtMaSoThue.setText(nhaCungCapToEdit.getMaSoThue()); // Điền dữ liệu cho trường mới
            if (nhaCungCapToEdit.getNgayDangKy() != null) {
                txtNgayDangKy.setText(nhaCungCapToEdit.getNgayDangKy().format(DateTimeFormatter.ISO_LOCAL_DATE));
            } else {
                txtNgayDangKy.setText(""); // Đảm bảo ô trống nếu ngày là null
            }
        } else {
            // Nếu là chế độ thêm mới, hiển thị gợi ý cho Mã NCC
            lblMaNCC.setText("Mã NCC (Tự động)");
            txtMaNCC.setText("Tự động");
            // Đặt ngày hiện tại làm mặc định cho ngày đăng ký khi thêm mới (tùy chọn)
            txtNgayDangKy.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        btnLuu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lấy dữ liệu và thực hiện Validation
                String tenNCC = txtTenNCC.getText().trim();
                String diaChi = txtDiaChi.getText().trim();
                String soDienThoai = txtSoDienThoai.getText().trim();
                String maSoThue = txtMaSoThue.getText().trim();       // Lấy dữ liệu trường mới
                String ngayDangKyStr = txtNgayDangKy.getText().trim(); // Lấy dữ liệu trường mới

                if (tenNCC.isEmpty()) {
                    JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Tên NCC không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validation định dạng số điện thoại
                if (!soDienThoai.matches("^0\\d{9,10}$")) { // Bắt đầu bằng 0, có 10 hoặc 11 chữ số tổng cộng
                    JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Số điện thoại không hợp lệ. Vui lòng nhập định dạng 0xxxxxxxx (10-11 số).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate ngayDangKy = null;
                if (!ngayDangKyStr.isEmpty()) {
                    try {
                        ngayDangKy = LocalDate.parse(ngayDangKyStr, DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Ngày đăng ký không hợp lệ. Vui lòng nhập định dạng YYYY-MM-DD.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                if (nhaCungCapToEdit == null) { // Thêm mới nhà cung cấp
                    // Tạo đối tượng NhaCungCap với TẤT CẢ các trường
                    NhaCungCap nccMoi = new NhaCungCap(null, tenNCC, diaChi, soDienThoai, ngayDangKy, maSoThue); 

                    String newId = controller.themNhaCungCap(nccMoi);

                    if (newId != null) {
                        JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Thêm nhà cung cấp thành công với ID: " + newId, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        nhaCungCapUI.onDataChanged(); // Thông báo cho UI cha làm mới bảng
                        dispose(); // Đóng dialog sau khi thành công
                    } else {
                        JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Thêm nhà cung cấp thất bại. Vui lòng kiểm tra lại thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else { // Sửa thông tin nhà cung cấp
                    nhaCungCapToEdit.setTenNCC(tenNCC);
                    nhaCungCapToEdit.setDiaChi(diaChi);
                    nhaCungCapToEdit.setSoDienThoai(soDienThoai);
                    nhaCungCapToEdit.setMaSoThue(maSoThue);     // Cập nhật trường mới
                    nhaCungCapToEdit.setNgayDangKy(ngayDangKy); // Cập nhật trường mới

                    if (controller.suaNhaCungCap(nhaCungCapToEdit)) {
                        JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Cập nhật nhà cung cấp thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        nhaCungCapUI.onDataChanged(); // Thông báo cho UI cha làm mới bảng
                        dispose(); // Đóng dialog sau khi thành công
                    } else {
                        JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Cập nhật nhà cung cấp thất bại. Vui lòng kiểm tra lại thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnHuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Đóng dialog
            }
        });

        pack();
        setLocationRelativeTo(parent); // Hiển thị dialog ở giữa frame cha
    }
}