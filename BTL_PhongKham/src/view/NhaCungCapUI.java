package view;

import controller.NhaCungCapController;
import model.NhaCungCap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class NhaCungCapUI extends JPanel {
    private NhaCungCapController nhaCungCapController;
    private DefaultTableModel nhaCungCapTableModel;
    private JTable nhaCungCapTable;

    private JTextField txtMaNCC;
    private JTextField txtTenNCC;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;

    private JButton btnThem;
    private JButton btnLamMoi;
    private JButton btnTimKiem;

    private JFrame parentFrame;

    private JPopupMenu popupMenu;
    private JMenuItem menuItemSua;
    private JMenuItem menuItemXoa;

    private JTextField txtTimKiem;
    private JLabel lblTimKiem;

    public NhaCungCapUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTimKiem = new JLabel("Tìm kiếm:");
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm");
        searchPanel.add(lblTimKiem);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        add(searchPanel, BorderLayout.NORTH);

        // Panel bảng
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh Sách Nhà Cung Cấp"));
        nhaCungCapTableModel = new DefaultTableModel(new Object[]{"Mã NCC", "Tên NCC", "Địa Chỉ", "Số Điện Thoại"}, 0);
        nhaCungCapTable = new JTable(nhaCungCapTableModel);
        JScrollPane tableScrollPane = new JScrollPane(nhaCungCapTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        popupMenu = new JPopupMenu();
        menuItemSua = new JMenuItem("Sửa");
        menuItemXoa = new JMenuItem("Xóa");
        popupMenu.add(menuItemSua);
        popupMenu.add(menuItemXoa);
        menuItemSua.addActionListener(e -> suaNhaCungCapTuPopup());
        menuItemXoa.addActionListener(e -> xoaNhaCungCapTuPopup());
        nhaCungCapTable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent evt) {
                int r = nhaCungCapTable.rowAtPoint(evt.getPoint());
                if (r >= 0 && r < nhaCungCapTable.getRowCount()) {
                    nhaCungCapTable.setRowSelectionInterval(r, r);
                } else {
                    nhaCungCapTable.clearSelection();
                }
                if (evt.isPopupTrigger() && evt.getComponent() instanceof JTable) {
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });

        // Panel thông tin chi tiết
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Chi Tiết"));
        JPanel detailPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        JLabel lblMaNCCDetail = new JLabel("Mã NCC:");
        txtMaNCC = new JTextField(10);
        txtMaNCC.setEditable(false);
        JLabel lblTenNCCDetail = new JLabel("Tên NCC:");
        txtTenNCC = new JTextField(20);
        txtTenNCC.setEditable(false);
        JLabel lblDiaChiDetail = new JLabel("Địa Chỉ:");
        txtDiaChi = new JTextField(30);
        txtDiaChi.setEditable(false);
        JLabel lblSoDienThoaiDetail = new JLabel("Số Điện Thoại:");
        txtSoDienThoai = new JTextField(15);
        txtSoDienThoai.setEditable(false);
        detailPanel.add(lblMaNCCDetail);
        detailPanel.add(txtMaNCC);
        detailPanel.add(lblTenNCCDetail);
        detailPanel.add(txtTenNCC);
        detailPanel.add(lblDiaChiDetail);
        detailPanel.add(txtDiaChi);
        detailPanel.add(lblSoDienThoaiDetail);
        detailPanel.add(txtSoDienThoai);
        infoPanel.add(detailPanel);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnThem = new JButton("Thêm");
        btnLamMoi = new JButton("Làm Mới");
        buttonPanel.add(btnThem);
        buttonPanel.add(btnLamMoi);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        nhaCungCapTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = nhaCungCapTable.getSelectedRow();
                if (selectedRow != -1) {
                    txtMaNCC.setText(String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0)));
                    txtTenNCC.setText(String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 1)));
                    txtDiaChi.setText(String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 2)));
                    txtSoDienThoai.setText(String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 3)));
                } else {
                    clearDetailFields();
                }
            }
        });

        btnThem.addActionListener(e -> {
            NhaCungCapDialog dialog = new NhaCungCapDialog(getParentFrame(), getNhaCungCapController(), null, NhaCungCapUI.this);
            dialog.setVisible(true);
        });

        btnLamMoi.addActionListener(e -> lamMoiDanhSach());

        txtTimKiem.addActionListener(e -> filterDanhSach());
        btnTimKiem.addActionListener(e -> filterDanhSach());

        lamMoiDanhSach();
    }

    // Getter cho parentFrame
    private JFrame getParentFrame() {
        if (parentFrame == null) {
            parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        }
        return parentFrame;
    }

    // Getter cho nhaCungCapController
    private NhaCungCapController getNhaCungCapController() {
        if (nhaCungCapController == null) {
            // Cảnh báo: Khởi tạo Controller mà không có thông tin kết nối DB có thể gây lỗi
            // Trong ứng dụng thực tế, bạn cần có một cách quản lý Controller tốt hơn (ví dụ: Dependency Injection)
            nhaCungCapController = new NhaCungCapController();
        }
        return nhaCungCapController;
    }

    private void filterDanhSach() {
        String searchText = txtTimKiem.getText().toLowerCase();
        nhaCungCapTableModel.setRowCount(0);
        List<NhaCungCap> danhSachNCC = getNhaCungCapController().layDanhSachNhaCungCap();

        List<NhaCungCap> danhSachDaLoc = danhSachNCC.stream()
                .filter(ncc -> String.valueOf(ncc.getMaNCC()).toLowerCase().contains(searchText) ||
                               String.valueOf(ncc.getTenNCC()).toLowerCase().contains(searchText) ||
                               String.valueOf(ncc.getDiaChi()).toLowerCase().contains(searchText) ||
                               String.valueOf(ncc.getSoDienThoai()).toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        for (NhaCungCap ncc : danhSachDaLoc) {
            Object[] rowData = {ncc.getMaNCC(), ncc.getTenNCC(), ncc.getDiaChi(), ncc.getSoDienThoai()};
            nhaCungCapTableModel.addRow(rowData);
        }
        clearDetailFields();
    }

    private void suaNhaCungCapTuPopup() {
        int selectedRow = nhaCungCapTable.getSelectedRow();
        if (selectedRow != -1) {
            String maNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0));
            String tenNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 1));
            String diaChi = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 2));
            String soDienThoai = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 3));

            NhaCungCap nccToEdit = new NhaCungCap(maNCC, tenNCC, diaChi, soDienThoai);
            NhaCungCapDialog dialog = new NhaCungCapDialog(getParentFrame(), getNhaCungCapController(), nccToEdit, NhaCungCapUI.this);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhà cung cấp để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhaCungCapTuPopup() {
        int selectedRow = nhaCungCapTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhà cung cấp để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0));

        int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhà cung cấp này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            if (getNhaCungCapController().xoaNhaCungCap(maNCC)) {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lamMoiDanhSach();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void lamMoiDanhSach() {
        nhaCungCapTableModel.setRowCount(0);
        List<NhaCungCap> danhSachNCC = getNhaCungCapController().layDanhSachNhaCungCap();
        for (NhaCungCap ncc : danhSachNCC) {
            Object[] rowData = {ncc.getMaNCC(), ncc.getTenNCC(), ncc.getDiaChi(), ncc.getSoDienThoai()};
            nhaCungCapTableModel.addRow(rowData);
        }
        clearDetailFields();
    }

    private void clearDetailFields() {
        txtMaNCC.setText("");
        txtTenNCC.setText("");
        txtDiaChi.setText("");
        txtSoDienThoai.setText("");
    }

    public void cleanup() {
        if (nhaCungCapController != null) {
            nhaCungCapController.closeConnection();
        }
    }
}