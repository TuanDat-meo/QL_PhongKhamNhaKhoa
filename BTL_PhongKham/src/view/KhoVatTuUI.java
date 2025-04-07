package view;

import controller.KhoVatTuController;
import model.KhoVatTu;
import model.NhaCungCap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class KhoVatTuUI extends JPanel implements ActionListener, MouseListener {

    private KhoVatTuController controller;
    private JTable tblKhoVatTu;
    private DefaultTableModel tblModel;
    private JTextField txtTimKiem;
    private JButton btnTimKiem, btnThemMoi;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet, menuItemChinhSua, menuItemXoa;
    private JDialog themSuaDialog;
    private JTextField txtTenVatTuDialog, txtSoLuongDialog, txtDonViTinhDialog;
    private JComboBox<NhaCungCap> cmbNhaCungCapDialog;
    private JComboBox<String> cmbPhanLoaiDialog;
    private JButton btnLuuDialog, btnHuyDialog;
    private KhoVatTu vatTuDangChon;

    public KhoVatTuUI() {
        controller = new KhoVatTuController();
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Panel phía trên (chứa thêm mới và tìm kiếm)
        JPanel pnlTop = new JPanel(new BorderLayout(10, 5));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(pnlTop, BorderLayout.NORTH);

        // Nút thêm mới ở bên trái
        btnThemMoi = new JButton("Thêm mới");
        Dimension buttonSize = new Dimension(100, 30);
        btnThemMoi.setPreferredSize(buttonSize);
        btnThemMoi.setMaximumSize(buttonSize);
        btnThemMoi.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlTop.add(btnThemMoi, BorderLayout.WEST);
        btnThemMoi.addActionListener(this);

        // Panel chứa thanh tìm kiếm ở bên phải
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        pnlTimKiem.add(lblTimKiem);
        txtTimKiem = new JTextField(20);
        pnlTimKiem.add(txtTimKiem);
        btnTimKiem = new JButton("Tìm");
        pnlTimKiem.add(btnTimKiem);
        btnTimKiem.addActionListener(this);
        txtTimKiem.addActionListener(this);

        pnlTop.add(pnlTimKiem, BorderLayout.EAST);

        // Bảng vật tư
        tblModel = new DefaultTableModel(new Object[]{"ID", "Mã NCC", "Tên Vật Tư", "Số Lượng", "Đơn Vị Tính", "Nhà Cung Cấp", "Phân Loại"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKhoVatTu = new JTable(tblModel);
        tblKhoVatTu.addMouseListener(this);
        JScrollPane scrollPane = new JScrollPane(tblKhoVatTu);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Popup Menu
        popupMenu = new JPopupMenu();
        menuItemXemChiTiet = new JMenuItem("Xem chi tiết");
        menuItemChinhSua = new JMenuItem("Chỉnh sửa");
        menuItemXoa = new JMenuItem("Xóa");
        menuItemXemChiTiet.addActionListener(this);
        menuItemChinhSua.addActionListener(this);
        menuItemXoa.addActionListener(this);
        popupMenu.add(menuItemXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(menuItemChinhSua);
        popupMenu.add(menuItemXoa);

        // Dialog thêm/sửa
        themSuaDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm/Sửa Vật Tư", true);
        themSuaDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; themSuaDialog.add(new JLabel("Tên Vật Tư:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; txtTenVatTuDialog = new JTextField(20); themSuaDialog.add(txtTenVatTuDialog, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; themSuaDialog.add(new JLabel("Số Lượng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; txtSoLuongDialog = new JTextField(10); themSuaDialog.add(txtSoLuongDialog, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; themSuaDialog.add(new JLabel("Đơn Vị Tính:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; txtDonViTinhDialog = new JTextField(10); themSuaDialog.add(txtDonViTinhDialog, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; themSuaDialog.add(new JLabel("Nhà Cung Cấp:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; cmbNhaCungCapDialog = new JComboBox<>(); themSuaDialog.add(cmbNhaCungCapDialog, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; themSuaDialog.add(new JLabel("Phân Loại:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; cmbPhanLoaiDialog = new JComboBox<>(); themSuaDialog.add(cmbPhanLoaiDialog, gbc);
        JPanel pnlButtonDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuuDialog = new JButton("Lưu");
        btnHuyDialog = new JButton("Hủy");
        btnLuuDialog.addActionListener(this);
        btnHuyDialog.addActionListener(this);
        pnlButtonDialog.add(btnLuuDialog);
        pnlButtonDialog.add(btnHuyDialog);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST; themSuaDialog.add(pnlButtonDialog, gbc);
        themSuaDialog.pack();
        themSuaDialog.setLocationRelativeTo(this);

        // Load dữ liệu cho các JComboBox
        loadNhaCungCapForDialog();
        loadPhanLoaiForDialog();
    }

    private void loadNhaCungCapForDialog() {
        List<NhaCungCap> nhaCungCaps = controller.getAllNhaCungCap();
        cmbNhaCungCapDialog.removeAllItems();
        for (NhaCungCap ncc : nhaCungCaps) {
            cmbNhaCungCapDialog.addItem(ncc);
        }
        cmbNhaCungCapDialog.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof NhaCungCap) {
                    value = ((NhaCungCap) value).getTenNCC();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }

    private void loadPhanLoaiForDialog() {
        List<String> phanLoais = controller.getAllPhanLoai();
        cmbPhanLoaiDialog.removeAllItems();
        for (String phanLoai : phanLoais) {
            cmbPhanLoaiDialog.addItem(phanLoai);
        }
        cmbPhanLoaiDialog.setSelectedIndex(-1);
    }

    private void loadData() {
        List<KhoVatTu> danhSachVatTu = controller.getAllKhoVatTu();
        hienThiDanhSachVatTu(danhSachVatTu);
    }

    private void hienThiDanhSachVatTu(List<KhoVatTu> danhSach) {
        tblModel.setRowCount(0);
        if (danhSach.isEmpty()) {
            return;
        }

        String phanLoaiDauTien = danhSach.get(0).getPhanLoai();
        boolean cungPhanLoai = true;
        int tongSoLuong = 0;

        for (KhoVatTu vatTu : danhSach) {
            String tenNCC = controller.getTenNhaCungCap(vatTu.getMaNCC());
            tblModel.addRow(new Object[]{vatTu.getIdVatTu(), vatTu.getMaNCC(), vatTu.getTenVatTu(), vatTu.getSoLuong(), vatTu.getDonViTinh(), tenNCC, vatTu.getPhanLoai()});
            tongSoLuong += vatTu.getSoLuong();
            if (!vatTu.getPhanLoai().equals(phanLoaiDauTien)) {
                cungPhanLoai = false;
            }
        }

        if (cungPhanLoai && !danhSach.isEmpty()) {
            tblModel.addRow(new Object[]{"", "", "Tổng Số Lượng:", tongSoLuong, "", "", phanLoaiDauTien});
        }
    }

    private void clearInputFieldsDialog() {
        txtTenVatTuDialog.setText("");
        txtSoLuongDialog.setText("");
        txtDonViTinhDialog.setText("");
        cmbNhaCungCapDialog.setSelectedIndex(-1);
        cmbPhanLoaiDialog.setSelectedIndex(-1);
    }

    private void timKiemVatTu() {
        String tuKhoa = txtTimKiem.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tblModel);
        tblKhoVatTu.setRowSorter(sorter);
        if (tuKhoa.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + tuKhoa));
        }
    }

    private void xemChiTiet() {
        if (vatTuDangChon != null) {
            String maNCC = vatTuDangChon.getMaNCC();
            String tenNCC = controller.getTenNhaCungCap(maNCC);
            String nhaCungCapHienThi = (tenNCC != null && !tenNCC.isEmpty()) ? tenNCC : "Không tìm thấy (Mã: " + maNCC + ")";
            JOptionPane.showMessageDialog(this,
                    "ID: " + vatTuDangChon.getIdVatTu() + "\n" +
                            "Mã NCC: " + maNCC + "\n" +
                            "Tên: " + vatTuDangChon.getTenVatTu() + "\n" +
                            "Số lượng: " + vatTuDangChon.getSoLuong() + "\n" +
                            "Đơn vị tính: " + vatTuDangChon.getDonViTinh() + "\n" +
                            "Nhà cung cấp: " + nhaCungCapHienThi + "\n" +
                            "Phân loại: " + vatTuDangChon.getPhanLoai(),
                    "Chi tiết vật tư", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vật tư.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void hienThiDialogThemSua(KhoVatTu vatTu) {
        clearInputFieldsDialog();
        vatTuDangChon = vatTu;
        if (vatTu != null) {
            themSuaDialog.setTitle("Chỉnh sửa Vật Tư");
            txtTenVatTuDialog.setText(vatTu.getTenVatTu());
            txtSoLuongDialog.setText(String.valueOf(vatTu.getSoLuong()));
            txtDonViTinhDialog.setText(vatTu.getDonViTinh());
            // Chọn nhà cung cấp
            for (int i = 0; i < cmbNhaCungCapDialog.getItemCount(); i++) {
                if (cmbNhaCungCapDialog.getItemAt(i).getMaNCC().equals(vatTu.getMaNCC())) {
                    cmbNhaCungCapDialog.setSelectedIndex(i);
                    break;
                }
            }
            cmbPhanLoaiDialog.setSelectedItem(vatTu.getPhanLoai());
        } else {
            themSuaDialog.setTitle("Thêm mới Vật Tư");
        }
        themSuaDialog.setVisible(true);
    }

    private void luuVatTu() {
        String tenVatTu = txtTenVatTuDialog.getText();
        String soLuongStr = txtSoLuongDialog.getText();
        String donViTinh = txtDonViTinhDialog.getText();
        NhaCungCap selectedNCC = (NhaCungCap) cmbNhaCungCapDialog.getSelectedItem();
        String phanLoai = (String) cmbPhanLoaiDialog.getSelectedItem();

        if (!tenVatTu.isEmpty() && !soLuongStr.isEmpty() && !donViTinh.isEmpty() && selectedNCC != null && phanLoai != null) {
            try {
                int soLuong = Integer.parseInt(soLuongStr);
                KhoVatTu vatTuMoi = new KhoVatTu(
                        vatTuDangChon != null ? vatTuDangChon.getIdVatTu() : 0,
                        tenVatTu,
                        soLuong,
                        donViTinh,
                        selectedNCC.getMaNCC(),
                        phanLoai
                );
                boolean success;
                if (vatTuDangChon == null) { // Thêm mới
                    success = controller.addKhoVatTu(vatTuMoi);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Thêm vật tư thành công.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Thêm vật tư thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else { // Chỉnh sửa
                    success = controller.updateKhoVatTu(vatTuMoi);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Cập nhật vật tư thành công.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Cập nhật vật tư thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                    vatTuDangChon = null;
                }
                if (success) {
                    loadData();
                    themSuaDialog.setVisible(false);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(themSuaDialog, "Vui lòng nhập đầy đủ thông tin.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void xoaVatTu() {
        if (vatTuDangChon != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa vật tư này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.deleteKhoVatTu(vatTuDangChon.getIdVatTu())) {
                    JOptionPane.showMessageDialog(this, "Xóa vật tư thành công.");
                    loadData();
                    vatTuDangChon = null;
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa vật tư thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vật tư để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnThemMoi) {
            hienThiDialogThemSua(null);
        } else if (e.getSource() == btnLuuDialog) {
            luuVatTu();
        } else if (e.getSource() == btnHuyDialog) {
            themSuaDialog.setVisible(false);
            vatTuDangChon = null;
        } else if (e.getSource() == menuItemXemChiTiet) {
            xemChiTiet();
        } else if (e.getSource() == menuItemChinhSua) {
            int selectedRow = tblKhoVatTu.getSelectedRow();
            if (selectedRow != -1) {
                vatTuDangChon = new KhoVatTu(
                        (int) tblModel.getValueAt(selectedRow, 0),
                        (String) tblModel.getValueAt(selectedRow, 2), // Tên Vật Tư
                        (int) tblModel.getValueAt(selectedRow, 3),    // Số Lượng
                        (String) tblModel.getValueAt(selectedRow, 4), // Đơn Vị Tính
                        (String) tblModel.getValueAt(selectedRow, 1), // Mã NCC
                        (String) tblModel.getValueAt(selectedRow, 6)  // Phân Loại
                );
                hienThiDialogThemSua(vatTuDangChon);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một vật tư để chỉnh sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == menuItemXoa) {
            xoaVatTu();
        } else if (e.getSource() == btnTimKiem || e.getSource() == txtTimKiem) {
            timKiemVatTu();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = tblKhoVatTu.rowAtPoint(e.getPoint());
            if (row >= 0 && row < tblKhoVatTu.getRowCount()) {
                tblKhoVatTu.setRowSelectionInterval(row, row);
                int selectedRow = tblKhoVatTu.getSelectedRow();
                vatTuDangChon = new KhoVatTu(
                        (int) tblModel.getValueAt(selectedRow, 0),
                        (String) tblModel.getValueAt(selectedRow, 2), // Tên Vật Tư
                        (int) tblModel.getValueAt(selectedRow, 3),    // Số Lượng
                        (String) tblModel.getValueAt(selectedRow, 4), // Đơn Vị Tính
                        (String) tblModel.getValueAt(selectedRow, 1), // Mã NCC
                        (String) tblModel.getValueAt(selectedRow, 6)  // Phân Loại
                );
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            } else {
                vatTuDangChon = null;
            }
        } else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            int row = tblKhoVatTu.rowAtPoint(e.getPoint());
            if (row >= 0 && row < tblKhoVatTu.getRowCount()) {
                tblKhoVatTu.setRowSelectionInterval(row, row);
                int selectedRow = tblKhoVatTu.getSelectedRow();
                vatTuDangChon = new KhoVatTu(
                        (int) tblModel.getValueAt(selectedRow, 0),
                        (String) tblModel.getValueAt(selectedRow, 2), // Tên Vật Tư
                        (int) tblModel.getValueAt(selectedRow, 3),    // Số Lượng
                        (String) tblModel.getValueAt(selectedRow, 4), // Đơn Vị Tính
                        (String) tblModel.getValueAt(selectedRow, 1), // Mã NCC
                        (String) tblModel.getValueAt(selectedRow, 6)  // Phân Loại
                );
                hienThiDialogThemSua(vatTuDangChon); // Mở dialog chỉnh sửa khi double-click
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Không cần xử lý
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Không cần xử lý
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Không cần xử lý
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Không cần xử lý
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Quản Lý Kho Vật Tư");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 600);
//        frame.setLocationRelativeTo(null);
//        frame.add(new KhoVatTuUI());
//        frame.setVisible(true);
//    }
}