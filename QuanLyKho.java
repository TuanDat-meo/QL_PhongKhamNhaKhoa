package lib;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class QuanLyKho extends JFrame {
    private JButton btnThuoc, btnVatTu, btnQuanAo, btnNguyenVatLieu;

    public QuanLyKho() {
        setTitle("Quản Lý Kho - Phòng Khám Nha Khoa");

        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 2, 20, 20));

        btnThuoc = createStyledButton("Thuốc");
        btnVatTu = createStyledButton("Vật Tư Y Tế");
        btnQuanAo = createStyledButton("Quần Áo Y Tế");
        btnNguyenVatLieu = createStyledButton("Nguyên Vật Liệu");

        add(btnThuoc);
        add(btnVatTu);
        add(btnQuanAo);
        add(btnNguyenVatLieu);

        btnThuoc.addActionListener(e -> openManagementWindow("Thuốc"));
        btnVatTu.addActionListener(e -> openManagementWindow("Vật Tư Y Tế"));
        btnQuanAo.addActionListener(e -> openManagementWindow("Quần Áo Y Tế"));
        btnNguyenVatLieu.addActionListener(e -> openManagementWindow("Nguyên Vật Liệu"));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(200, 100));
        return button;
    }

    private void openManagementWindow(String category) {
        new QuanLySanPham(category).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuanLyKho().setVisible(true));
    }
}

// Lớp quản lý sản phẩm
@SuppressWarnings("serial")
class QuanLySanPham extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtTen, txtSoLuong, txtMaSP, txtGiaNhap, txtGiaBan, txtTonKho, txtNguoiNhan, txtMaDonHang, txtGhiChu, txtDonVi;
    private JTextField txtMaNCC, txtTenNCC;

    public QuanLySanPham(String category) {
        setTitle("Quản Lý " + category);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Bảng dữ liệu
        String[] columnNames = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn vị", "Giá nhập", "Giá bán", "Mã NCC", "Tên NCC", "Tồn kho", "Người nhận", "Mã Đơn Hàng", "Ghi chú"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(7, 4, 10, 10));
        inputPanel.add(new JLabel("Mã SP:")); txtMaSP = new JTextField(); inputPanel.add(txtMaSP);
        inputPanel.add(new JLabel("Tên sản phẩm:")); txtTen = new JTextField(); inputPanel.add(txtTen);
        inputPanel.add(new JLabel("Số lượng:")); txtSoLuong = new JTextField(); inputPanel.add(txtSoLuong);
        inputPanel.add(new JLabel("Đơn vị:")); txtDonVi = new JTextField(); inputPanel.add(txtDonVi);
        inputPanel.add(new JLabel("Giá nhập:")); txtGiaNhap = new JTextField(); inputPanel.add(txtGiaNhap);
        inputPanel.add(new JLabel("Giá bán:")); txtGiaBan = new JTextField(); inputPanel.add(txtGiaBan);
        inputPanel.add(new JLabel("Tồn kho:")); txtTonKho = new JTextField(); inputPanel.add(txtTonKho);
        inputPanel.add(new JLabel("Người nhận:")); txtNguoiNhan = new JTextField(); inputPanel.add(txtNguoiNhan);
        inputPanel.add(new JLabel("Mã Đơn Hàng:")); txtMaDonHang = new JTextField(); inputPanel.add(txtMaDonHang);
        inputPanel.add(new JLabel("Ghi chú:")); txtGhiChu = new JTextField(); inputPanel.add(txtGhiChu);

        // Trường nhà cung cấp
        inputPanel.add(new JLabel("Mã NCC:"));
        txtMaNCC = new JTextField(); txtMaNCC.setEditable(false);
        inputPanel.add(txtMaNCC);

        inputPanel.add(new JLabel("Tên NCC:"));
        txtTenNCC = new JTextField(); txtTenNCC.setEditable(false);
        inputPanel.add(txtTenNCC);
        
        // Nút chọn nhà cung cấp và nhập hàng
        JButton btnNhaCungCap = new JButton("Chọn NCC");
        JButton btnNhapHang = new JButton("Nhập hàng"); 

        inputPanel.add(btnNhaCungCap);
        inputPanel.add(btnNhapHang);

        // Thêm sự kiện nhập hàng
        btnNhapHang.addActionListener(e -> {
            NhapHangFrame nhapHangFrame = new NhapHangFrame(tableModel);
            nhapHangFrame.setVisible(true);
        });
        // Thêm sự kiện chọn nhà cung cấp
        btnNhaCungCap.addActionListener(e -> {
            ChonNCCFrame chonNCCFrame = new ChonNCCFrame(this);
            chonNCCFrame.setVisible(true);
        });
        
        // Panel nút chức năng
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnDelete = new JButton("Xóa");
        JButton btnUpdate = new JButton("Cập nhật");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnUpdate);

        btnAdd.addActionListener(this::addProduct);
        btnDelete.addActionListener(this::deleteProduct);
        btnUpdate.addActionListener(this::updateProduct);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addProduct(ActionEvent e) {
        String[] rowData = {
            txtMaSP.getText(), txtTen.getText(), txtSoLuong.getText(), txtDonVi.getText(), txtGiaNhap.getText(),
            txtGiaBan.getText(), txtMaNCC.getText(), txtTenNCC.getText(), txtTonKho.getText(),
            txtNguoiNhan.getText(), txtMaDonHang.getText(), txtGhiChu.getText()
        };
        tableModel.addRow(rowData);
    }
    private void deleteProduct(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xóa.");
        }
    }

    private void updateProduct(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.setValueAt(txtMaSP.getText(), selectedRow, 0);
            tableModel.setValueAt(txtTen.getText(), selectedRow, 1);
            tableModel.setValueAt(txtSoLuong.getText(), selectedRow, 2);
            tableModel.setValueAt(txtDonVi.getText(), selectedRow, 3);
            tableModel.setValueAt(txtGiaNhap.getText(), selectedRow, 4);
            tableModel.setValueAt(txtGiaBan.getText(), selectedRow, 5);
            tableModel.setValueAt(txtMaNCC.getText(), selectedRow, 6);
            tableModel.setValueAt(txtTenNCC.getText(), selectedRow, 7);
            tableModel.setValueAt(txtTonKho.getText(), selectedRow, 8);
            tableModel.setValueAt(txtNguoiNhan.getText(), selectedRow, 9);
            tableModel.setValueAt(txtMaDonHang.getText(), selectedRow, 10);
            tableModel.setValueAt(txtGhiChu.getText(), selectedRow, 11);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để cập nhật.");
        }
    }

    public void setNCC(String maNCC, String tenNCC) {
        txtMaNCC.setText(maNCC);
        txtTenNCC.setText(tenNCC);
    }

}
// Form Chọn NCC
@SuppressWarnings("serial")
class ChonNCCFrame extends JFrame {
    @SuppressWarnings({ "unused", "unused" })
	private JTextField txtMaNCC, txtTenNCC, txtDiaChi, txtSDT;
    private JButton btnXacNhan, btnHuy;
    private QuanLySanPham parent;

    public ChonNCCFrame(QuanLySanPham parent) {
        this.parent = parent;  // Nhận tham chiếu đến form quản lý sản phẩm

        setTitle("Chọn Nhà Cung Cấp");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Mã NCC:", "Tên NCC:", "Địa chỉ:", "SĐT:"};
        JTextField[] textFields = {txtMaNCC = new JTextField(), txtTenNCC = new JTextField(),
                txtDiaChi = new JTextField(), txtSDT = new JTextField()};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            add(textFields[i], gbc);
        }

        // Nút xác nhận và hủy
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 1;
        btnHuy = new JButton("Hủy");
        add(btnHuy, gbc);

        gbc.gridx = 1;
        btnXacNhan = new JButton("Xác nhận");
        add(btnXacNhan, gbc);

        btnXacNhan.addActionListener(e -> chonNCC());
        btnHuy.addActionListener(e -> dispose());
    }
    public void setNCC(String maNCC, String tenNCC) {
        txtMaNCC.setText(maNCC);
        txtTenNCC.setText(tenNCC);
    }
    private void chonNCC() {
        if (parent != null) {
            parent.setNCC(txtMaNCC.getText(), txtTenNCC.getText());
        }
        dispose();
    }

   

}

// Form Nhập Hàng
@SuppressWarnings("serial")
class NhapHangFrame extends JFrame {
    private JTextField txtMaSP, txtTenSP, txtSoLuong, txtGiaNhap, txtMaDonHang, txtNguoiNhan, txtGhiChu;
    private JButton btnXacNhan, btnHuy;
    private DefaultTableModel tableModel;

    public NhapHangFrame(DefaultTableModel tableModel) {
        this.tableModel = tableModel;

        setTitle("Nhập Hàng");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Danh sách label và text field
        String[] labels = {"Mã SP:", "Tên sản phẩm:", "Số lượng:", "Giá nhập:", "Mã đơn hàng:", "Người nhận:", "Ghi chú:"};
        JTextField[] textFields = {txtMaSP = new JTextField(), txtTenSP = new JTextField(), txtSoLuong = new JTextField(),
                txtGiaNhap = new JTextField(), txtMaDonHang = new JTextField(), txtNguoiNhan = new JTextField(), txtGhiChu = new JTextField()};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            add(textFields[i], gbc);
        }

        // Nút xác nhận và hủy
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 1;
        btnHuy = new JButton("Hủy");
        add(btnHuy, gbc);

        gbc.gridx = 1;
        btnXacNhan = new JButton("Xác nhận");
        add(btnXacNhan, gbc);

        btnXacNhan.addActionListener(this::nhapHang);
        btnHuy.addActionListener(e -> dispose());
    }

    private void nhapHang(ActionEvent e) {
        tableModel.addRow(new Object[]{txtMaSP.getText(), txtTenSP.getText(), txtSoLuong.getText(), "", txtGiaNhap.getText(),
                "", "", "", "", txtNguoiNhan.getText(), txtMaDonHang.getText(), txtGhiChu.getText()});
        dispose();
    }
}
