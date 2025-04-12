package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.BenhNhanController;
import model.BenhNhan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BenhNhanUI extends JPanel {
	private BenhNhanController qlBenhNhan;
    private JTable tableBenhNhan;
    private DefaultTableModel tableModel;
    private JTextField txtHoTen, txtNgaySinh, txtGioiTinh, txtSoDienThoai, txtCccd, txtDiaChi;
    private JDialog inputDialog;
    private JButton btnThem, btnSua, btnXoa, btnTimKiem;
    private JTextField txtTimKiem;
    private Color buttonBackgroundColor = new Color(240, 240, 240); // Màu nền mặc định của nút
    private Color buttonHoverColor = new Color(220, 220, 220); // Màu nền khi hover

    public BenhNhanUI() {
        qlBenhNhan = new BenhNhanController();
        initialize();
    }
    private void initialize() {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 10, 5);

        // Tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5)); // Đổi sang FlowLayout.RIGHT để căn phải
        searchPanel.setPreferredSize(new Dimension(500, 40)); // Đặt kích thước cố định cho panel tìm kiếm
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(12); // Làm nhỏ thanh tìm kiếm
        txtTimKiem.setPreferredSize(new Dimension(150, 30)); // Đặt kích thước cố định cho ô nhập
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(txtTimKiem);
        btnTimKiem = new JButton("Tìm");
        btnTimKiem.setPreferredSize(new Dimension(80, 30));
        btnTimKiem.setFont(new Font("Arial", Font.PLAIN, 14));
        btnTimKiem.setBackground(buttonBackgroundColor);
        btnTimKiem.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnTimKiem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnTimKiem.setBackground(buttonHoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnTimKiem.setBackground(buttonBackgroundColor);
            }
        });
        btnTimKiem.addActionListener(e -> timKiemBenhNhan());
        searchPanel.add(btnTimKiem);
        add(searchPanel, gbc); // Thêm thanh tìm kiếm vào grid

        // Bảng bệnh nhân
        gbc.gridy++;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Họ tên");
        tableModel.addColumn("Ngày sinh");
        tableModel.addColumn("Giới tính");
        tableModel.addColumn("Số điện thoại");
        tableModel.addColumn("CCCD");
        tableModel.addColumn("Địa chỉ");
        tableBenhNhan = new JTable(tableModel);
        tableBenhNhan.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(tableBenhNhan);
        add(scrollPane, gbc);

        // Nút chức năng
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnThem = createButton("Thêm");
        btnSua = createButton("Sửa");
        btnXoa = createButton("Xóa");
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        add(buttonPanel, gbc);

        // Sự kiện cho các nút
        btnThem.addActionListener(e -> showInputDialog(true));
        btnSua.addActionListener(e -> showInputDialog(false));
        btnXoa.addActionListener(e -> xoaBenhNhan());

        // Tạo dialog nhập liệu
        createInputDialog();

        // Load dữ liệu bệnh nhân từ database
        loadDanhSachBenhNhan();
    }


    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(80, 30));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(buttonBackgroundColor);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(buttonHoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(buttonBackgroundColor);
            }
        });
        return button;
    }

    private void createInputDialog() {
        inputDialog = new JDialog();
        inputDialog.setTitle("Thông tin bệnh nhân");
        inputDialog.setLayout(new GridBagLayout()); // Sử dụng GridBagLayout cho JDialog

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2, 5, 10)); // Tăng khoảng cách theo chiều dọc

        inputPanel.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        txtHoTen.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(txtHoTen);

        inputPanel.add(new JLabel("Ngày sinh (yyyy-MM-dd):"));
        txtNgaySinh = new JTextField();
        txtNgaySinh.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(txtNgaySinh);

        inputPanel.add(new JLabel("Giới tính:"));
        txtGioiTinh = new JTextField();
        txtGioiTinh.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(txtGioiTinh);

        inputPanel.add(new JLabel("Số điện thoại:"));
        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(txtSoDienThoai);

        inputPanel.add(new JLabel("CCCD:"));
        txtCccd = new JTextField();
        txtCccd.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(txtCccd);

        inputPanel.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        txtDiaChi.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(txtDiaChi);

        JButton btnLuu = new JButton("Lưu");
        btnLuu.setPreferredSize(new Dimension(80, 30));
        btnLuu.setFont(new Font("Arial", Font.PLAIN, 14));
        btnLuu.setBackground(buttonBackgroundColor);
        btnLuu.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnLuu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLuu.setBackground(buttonHoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLuu.setBackground(buttonBackgroundColor);
            }
        });
        btnLuu.addActionListener(e -> luuBenhNhan());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 10, 10, 10); // Thêm lề trên 20px

        inputDialog.add(inputPanel, gbc);

        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 20, 10); // Thêm lề dưới 20px

        inputDialog.add(btnLuu, gbc);

        inputDialog.pack();
        inputDialog.setLocationRelativeTo(this);
    }

    private void showInputDialog(boolean isThem) {
        if (!isThem) {
            int selectedRow = tableBenhNhan.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bệnh nhân để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            txtHoTen.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtNgaySinh.setText((String) tableModel.getValueAt(selectedRow, 2));
            txtGioiTinh.setText((String) tableModel.getValueAt(selectedRow, 3));
            txtSoDienThoai.setText((String) tableModel.getValueAt(selectedRow, 4));
            txtCccd.setText((String) tableModel.getValueAt(selectedRow, 5));
            txtDiaChi.setText((String) tableModel.getValueAt(selectedRow, 6));
        } else {
            clearInputFields();
        }
        inputDialog.setVisible(true);
    }
    private void loadDanhSachBenhNhan() {
        try {
            List<BenhNhan> danhSach = qlBenhNhan.layDanhSachBenhNhan();
            tableModel.setRowCount(0);
            for (BenhNhan benhNhan : danhSach) {
                tableModel.addRow(new Object[]{
                        benhNhan.getIdBenhNhan(),
                        benhNhan.getHoTen(),
                        new SimpleDateFormat("yyyy-MM-dd").format(benhNhan.getNgaySinh()),
                        benhNhan.getGioiTinh(),
                        benhNhan.getSoDienThoai(),
                        benhNhan.getCccd(),
                        benhNhan.getDiaChi()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu bệnh nhân: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themBenhNhan() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date utilDate = dateFormat.parse(txtNgaySinh.getText());
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            BenhNhan benhNhan = new BenhNhan(
                    0,
                    txtHoTen.getText(),
                    sqlDate,
                    txtGioiTinh.getText(),
                    txtSoDienThoai.getText(),
                    txtCccd.getText(),
                    txtDiaChi.getText()
            );
            qlBenhNhan.themBenhNhan(benhNhan);
            loadDanhSachBenhNhan();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Thêm bệnh nhân thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ParseException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm bệnh nhân: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaBenhNhan() {
        int selectedRow = tableBenhNhan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bệnh nhân để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int idBenhNhan = (int) tableModel.getValueAt(selectedRow, 0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date utilDate = dateFormat.parse(txtNgaySinh.getText());
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            BenhNhan benhNhan = new BenhNhan(
                    idBenhNhan,
                    txtHoTen.getText(),
                    sqlDate,
                    txtGioiTinh.getText(),
                    txtSoDienThoai.getText(),
                    txtCccd.getText(),
                    txtDiaChi.getText()
            );
            qlBenhNhan.capNhatBenhNhan(benhNhan);
            loadDanhSachBenhNhan();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Cập nhật bệnh nhân thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ParseException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật bệnh nhân: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaBenhNhan() {
        int selectedRow = tableBenhNhan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bệnh nhân để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idBenhNhan = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            qlBenhNhan.xoaBenhNhan(idBenhNhan);
            loadDanhSachBenhNhan();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Xóa bệnh nhân thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa bệnh nhân: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputFields() {
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        txtGioiTinh.setText("");
        txtSoDienThoai.setText("");
        txtCccd.setText("");
        txtDiaChi.setText("");
    }
    private void luuBenhNhan() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date utilDate = dateFormat.parse(txtNgaySinh.getText());
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            BenhNhan benhNhan = new BenhNhan(
                    (btnSua.isEnabled() && tableBenhNhan.getSelectedRow() != -1) ? (int) tableModel.getValueAt(tableBenhNhan.getSelectedRow(), 0) : 0,
                    txtHoTen.getText(),
                    sqlDate,
                    txtGioiTinh.getText(),
                    txtSoDienThoai.getText(),
                    txtCccd.getText(),
                    txtDiaChi.getText()
            );
            if (btnSua.isEnabled() && tableBenhNhan.getSelectedRow() != -1) {
                qlBenhNhan.capNhatBenhNhan(benhNhan);
                JOptionPane.showMessageDialog(this, "Cập nhật bệnh nhân thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                qlBenhNhan.themBenhNhan(benhNhan);
                JOptionPane.showMessageDialog(this, "Thêm bệnh nhân thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            loadDanhSachBenhNhan();
            inputDialog.setVisible(false);
        } catch (SQLException | ParseException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu bệnh nhân: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }private void timKiemBenhNhan() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadDanhSachBenhNhan();
            return;
        }

        try {
            List<BenhNhan> danhSach = qlBenhNhan.layDanhSachBenhNhan();
            tableModel.setRowCount(0);

            for (BenhNhan benhNhan : danhSach) {
                if (benhNhan.getHoTen().toLowerCase().contains(keyword) ||
                    benhNhan.getSoDienThoai().toLowerCase().contains(keyword) ||
                    benhNhan.getCccd().toLowerCase().contains(keyword) ||
                    benhNhan.getDiaChi().toLowerCase().contains(keyword)) {

                    tableModel.addRow(new Object[]{
                            benhNhan.getIdBenhNhan(),
                            benhNhan.getHoTen(),
                            new SimpleDateFormat("yyyy-MM-dd").format(benhNhan.getNgaySinh()),
                            benhNhan.getGioiTinh(),
                            benhNhan.getSoDienThoai(),
                            benhNhan.getCccd(),
                            benhNhan.getDiaChi()
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm bệnh nhân: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

}