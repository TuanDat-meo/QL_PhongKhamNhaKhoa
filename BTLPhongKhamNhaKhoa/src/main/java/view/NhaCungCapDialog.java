package view;

import controller.NhaCungCapController;
import model.NhaCungCap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NhaCungCapDialog extends JDialog {

    private JTextField txtMaNCC;
    private JTextField txtTenNCC;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;
    private JButton btnLuu;
    private JButton btnHuy;
    private NhaCungCapController controller;
    private NhaCungCap nhaCungCapToEdit;
    private NhaCungCapUI nhaCungCapUI;

    public NhaCungCapDialog(JFrame parent, NhaCungCapController ctrl, NhaCungCap ncc, NhaCungCapUI ui) {
        super(parent, (ncc == null ? "Thêm Nhà Cung Cấp" : "Sửa Nhà Cung Cấp"), true);
        this.controller = ctrl;
        this.nhaCungCapToEdit = ncc;
        this.nhaCungCapUI = ui;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        JLabel lblMaNCC = new JLabel("Mã NCC:");
        txtMaNCC = new JTextField(10);
        txtMaNCC.setEnabled(ncc != null);
        if (ncc == null) {
            lblMaNCC.setText("Mã NCC (Tự động)");
        }
        JLabel lblTenNCC = new JLabel("Tên NCC:");
        txtTenNCC = new JTextField(20);
        JLabel lblDiaChi = new JLabel("Địa Chỉ:");
        txtDiaChi = new JTextField(30);
        JLabel lblSoDienThoai = new JLabel("Số Điện Thoại:");
        txtSoDienThoai = new JTextField(15);

        inputPanel.add(lblMaNCC);
        inputPanel.add(txtMaNCC);
        inputPanel.add(lblTenNCC);
        inputPanel.add(txtTenNCC);
        inputPanel.add(lblDiaChi);
        inputPanel.add(txtDiaChi);
        inputPanel.add(lblSoDienThoai);
        inputPanel.add(txtSoDienThoai);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);

        contentPane.add(inputPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        if (nhaCungCapToEdit != null) {
            txtMaNCC.setText(nhaCungCapToEdit.getMaNCC());
            txtTenNCC.setText(nhaCungCapToEdit.getTenNCC());
            txtDiaChi.setText(nhaCungCapToEdit.getDiaChi());
            txtSoDienThoai.setText(nhaCungCapToEdit.getSoDienThoai());
        }

        btnLuu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tenNCC = txtTenNCC.getText();
                String diaChi = txtDiaChi.getText();
                String soDienThoai = txtSoDienThoai.getText();

                if (tenNCC.isEmpty()) {
                    JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Tên NCC không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                NhaCungCap nccMoi = new NhaCungCap(null, tenNCC, diaChi, soDienThoai); // Mã NCC là null để DB tự động tạo
                String newId = controller.themNhaCungCap(nccMoi);

                if (newId != null) {
                    JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Thêm nhà cung cấp thành công với ID: " + newId, "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                    // Hiển thị ID vừa tạo trong ô txtMaNCC
                    txtMaNCC.setText(newId);
                    txtMaNCC.setEnabled(true); // Cho phép hiển thị (có thể không cần thiết)

                    nhaCungCapUI.lamMoiDanhSach(); // Làm mới danh sách để ID hiển thị trong bảng
                    // Không đóng dialog ngay lập tức để người dùng thấy ID
                    dispose(); 

                } else {
                    JOptionPane.showMessageDialog(NhaCungCapDialog.this, "Thêm nhà cung cấp thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnHuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(parent);
    }
}