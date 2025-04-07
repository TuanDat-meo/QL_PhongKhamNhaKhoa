// view/HoSoBenhAnUI.java
package view;

import controller.HoSoBenhAnController;
import controller.BenhNhanController;
import controller.DonThuocController;
import model.HoSoBenhAn;
import model.BenhNhan;
import model.DonThuoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HoSoBenhAnUI extends JPanel {
    private HoSoBenhAnController hoSoBenhAnController;
    private BenhNhanController benhNhanController;
    private DonThuocController donThuocController;
    private DefaultTableModel hoSoBenhAnTableModel;
    private JTable hoSoBenhAnTable;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemSua;
    private JMenuItem menuItemXoa;
    private JMenuItem menuItemXemChiTiet;

    private Map<String, Integer> tenBenhNhanToId;

    private JButton btnThem;
    private JButton btnLamMoi;
    private JButton btnTimKiem; // Thêm nút tìm kiếm

    private JTextField txtTimKiem;
    private JLabel lblTimKiem;

    private ThemHoSoBenhAnDialog themHoSoDialog;

    public HoSoBenhAnUI() throws SQLException {
        this.hoSoBenhAnController = new HoSoBenhAnController();
        this.benhNhanController = new BenhNhanController();
        this.donThuocController = new DonThuocController();
        setLayout(new BorderLayout(10, 10));

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTimKiem = new JLabel("Tìm kiếm:");
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm"); // Khởi tạo nút Tìm kiếm
        searchPanel.add(lblTimKiem);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem); // Thêm nút Tìm kiếm vào panel
        add(searchPanel, BorderLayout.NORTH);

        // Panel bảng Hồ sơ bệnh án
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh Sách Hồ Sơ Bệnh Án"));
        hoSoBenhAnTableModel = new DefaultTableModel(new Object[]{"ID HS", "Tên BN", "Chuẩn đoán", "Ghi chú", "Ngày tạo", "Trạng thái"}, 0);
        hoSoBenhAnTable = new JTable(hoSoBenhAnTableModel);
        JScrollPane tableScrollPane = new JScrollPane(hoSoBenhAnTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Panel nút chức năng (chỉ còn Thêm và Làm mới)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnThem = new JButton("Thêm");
        btnLamMoi = new JButton("Làm mới");
        buttonPanel.add(btnThem);
        buttonPanel.add(btnLamMoi);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load danh sách bệnh nhân
        loadDanhSachBenhNhan();

        // Tạo dialog thêm hồ sơ bệnh án (ban đầu không hiển thị)
        themHoSoDialog = new ThemHoSoBenhAnDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm Hồ sơ Bệnh án",
                true,
                hoSoBenhAnController,
                benhNhanController,
                tenBenhNhanToId,
                this
        );

        // Thêm bộ lắng nghe cho nút Thêm
        btnThem.addActionListener(e -> themHoSoDialog.setVisible(true));
        btnLamMoi.addActionListener(e -> lamMoiDanhSach());
        txtTimKiem.addActionListener(e -> timKiemHoSoBenhAn()); // Enter để tìm kiếm
        btnTimKiem.addActionListener(e -> timKiemHoSoBenhAn()); // Nút Tìm kiếm

        // Tạo popup menu
        popupMenu = new JPopupMenu();
        menuItemXemChiTiet = new JMenuItem("Xem chi tiết");
        menuItemSua = new JMenuItem("Sửa");
        menuItemXoa = new JMenuItem("Xóa");

        popupMenu.add(menuItemXemChiTiet);
        popupMenu.add(menuItemSua);
        popupMenu.add(menuItemXoa);

        // Thêm MouseListener cho bảng để hiển thị popup
        hoSoBenhAnTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = hoSoBenhAnTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < hoSoBenhAnTable.getRowCount()) {
                        hoSoBenhAnTable.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        // Thêm bộ lắng nghe cho các menu item trong popup
        menuItemXemChiTiet.addActionListener(e -> xemChiTietHoSoBenhAn());
        menuItemSua.addActionListener(e -> hienThiDialogSuaHoSoBenhAn());
        menuItemXoa.addActionListener(e -> xoaHoSoBenhAn());

        // Hiển thị danh sách ban đầu
        lamMoiDanhSach();
    }

    private void loadDanhSachBenhNhan() throws SQLException {
        List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
        tenBenhNhanToId = danhSachBenhNhan.stream()
                .collect(Collectors.toMap(BenhNhan::getHoTen, BenhNhan::getIdBenhNhan));
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(tenBenhNhanToId.keySet().toArray(new String[0]));
        if (themHoSoDialog != null) {
            themHoSoDialog.setCmbTenBenhNhanModel(comboBoxModel);
        } else {
            // Trường hợp dialog chưa được khởi tạo (có thể xảy ra nếu load trước khi UI hoàn thành)
            // Có thể xử lý khác nếu cần, nhưng việc khởi tạo dialog trong constructor là hợp lý hơn.
        }
    }

    public void lamMoiDanhSach() {
        hoSoBenhAnTableModel.setRowCount(0);
        List<HoSoBenhAn> danhSachHoSoBenhAn = hoSoBenhAnController.layDanhSachHoSoBenhAn();
        for (HoSoBenhAn hsbA : danhSachHoSoBenhAn) {
            BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hsbA.getIdBenhNhan());
            String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "N/A";
            Object[] rowData = {hsbA.getIdHoSo(), tenBenhNhan, hsbA.getChuanDoan(), hsbA.getGhiChu(), hsbA.getNgayTao(), hsbA.getTrangThai()};
            hoSoBenhAnTableModel.addRow(rowData);
        }
    }
    private void timKiemHoSoBenhAn() {
        String searchText = txtTimKiem.getText().toLowerCase();
        hoSoBenhAnTableModel.setRowCount(0);
        List<HoSoBenhAn> danhSachHoSoBenhAn = hoSoBenhAnController.layDanhSachHoSoBenhAn();

        List<HoSoBenhAn> danhSachTimKiem = danhSachHoSoBenhAn.stream()
                .filter(hsbA -> {
                    BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hsbA.getIdBenhNhan());
                    String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "";
                    return String.valueOf(hsbA.getIdHoSo()).toLowerCase().contains(searchText) ||
                           tenBenhNhan.toLowerCase().contains(searchText) ||
                           hsbA.getChuanDoan().toLowerCase().contains(searchText) ||
                           hsbA.getTrangThai().toLowerCase().contains(searchText);
                })
                .collect(Collectors.toList());

        for (HoSoBenhAn hsbA : danhSachTimKiem) {
            BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hsbA.getIdBenhNhan());
            String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "N/A";
            Object[] rowData = {hsbA.getIdHoSo(), tenBenhNhan, hsbA.getChuanDoan(), hsbA.getGhiChu(), hsbA.getNgayTao(), hsbA.getTrangThai()};
            hoSoBenhAnTableModel.addRow(rowData);
        }
    }

    private void hienThiDialogSuaHoSoBenhAn() {
        int selectedRow = hoSoBenhAnTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hồ sơ bệnh án để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idHoSo = (int) hoSoBenhAnTableModel.getValueAt(selectedRow, 0);
        HoSoBenhAn hoSoBenhAnCanSua = hoSoBenhAnController.timKiemHoSoBenhAnTheoId(idHoSo);

        if (hoSoBenhAnCanSua != null) {
        	BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hoSoBenhAnCanSua.getIdBenhNhan());
            String tenBenhNhanHienTai = (benhNhan != null) ? benhNhan.getHoTen() : "";

            SuaHoSoBenhAnDialog suaDialog = new SuaHoSoBenhAnDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Sửa Hồ sơ Bệnh án",
                    true,
                    hoSoBenhAnController,
                    benhNhanController,
                    tenBenhNhanToId,
                    this,
                    idHoSo,
                    tenBenhNhanHienTai,
                    hoSoBenhAnCanSua.getChuanDoan(),
                    hoSoBenhAnCanSua.getGhiChu(),
                    hoSoBenhAnCanSua.getNgayTao(),
                    hoSoBenhAnCanSua.getTrangThai()
            );
            suaDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ bệnh án để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaHoSoBenhAn() {
    	int selectedRow = hoSoBenhAnTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hồ sơ bệnh án để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idHoSo = (int) hoSoBenhAnTableModel.getValueAt(selectedRow, 0);
        int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa hồ sơ bệnh án này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            hoSoBenhAnController.xoaHoSoBenhAn(idHoSo);
            lamMoiDanhSach();
            JOptionPane.showMessageDialog(this, "Xóa hồ sơ bệnh án thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xemChiTietHoSoBenhAn() {
        int selectedRow = hoSoBenhAnTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hồ sơ bệnh án để xem chi tiết.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idHoSo = (int) hoSoBenhAnTableModel.getValueAt(selectedRow, 0);
        HoSoBenhAn hoSoBenhAn = hoSoBenhAnController.timKiemHoSoBenhAnTheoId(idHoSo);

        if (hoSoBenhAn != null) {
            // Corrected line: Using hoSoBenhAn
            BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hoSoBenhAn.getIdBenhNhan());
            String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "N/A";
            List<DonThuoc> danhSachDonThuoc = donThuocController.layDanhSachDonThuocTheoHoSoBenhAnId(idHoSo);

            JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
            ChiTietHoSoBenhAnDialog chiTietDialog = new ChiTietHoSoBenhAnDialog(
                    owner,
                    "Chi tiết Hồ sơ Bệnh án",
                    true,
                    hoSoBenhAn.getIdHoSo(),
                    tenBenhNhan,
                    hoSoBenhAn.getChuanDoan(),
                    hoSoBenhAn.getGhiChu(),
                    formatDate(hoSoBenhAn.getNgayTao()),
                    hoSoBenhAn.getTrangThai(),
                    danhSachDonThuoc
            );
            chiTietDialog.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ bệnh án với ID: " + idHoSo, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        }
        return "";
    }

    // Phương thức main
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                JFrame frame = new JFrame("Quản lý Hồ sơ Bệnh án");
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.getContentPane().add(new HoSoBenhAnUI());
//                frame.setPreferredSize(new Dimension(900, 700));
//                frame.pack();
//                frame.setLocationRelativeTo(null);
//                frame.setVisible(true);
//            } catch (SQLException e) {
//                JOptionPane.showMessageDialog(null, "Lỗi khi khởi tạo ứng dụng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//                e.printStackTrace();
//            }
//        });
//    }
}