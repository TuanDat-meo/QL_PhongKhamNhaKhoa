package view;

import controller.HoaDonController;
import controller.BenhNhanController;
import model.HoaDon;
import model.ThanhToanBenhNhan;
import model.BenhNhan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class HoaDonUI extends JPanel {
    private HoaDonController hoaDonController;
    private BenhNhanController benhNhanController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnThem;
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private TableRowSorter<DefaultTableModel> sorter;

    public HoaDonUI() {
        hoaDonController = new HoaDonController();
        benhNhanController = new BenhNhanController();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Quản lý Hóa Đơn"));

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        txtTimKiem = new JTextField(15);
        btnTimKiem = new JButton("Tìm");
        searchPanel.add(lblTimKiem);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "ID Bệnh Nhân", "Tên Bệnh Nhân", "Ngày Tạo", "Tổng Tiền", "Trạng Thái"}, 0);
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnThem = new JButton("Thêm Hóa Đơn");
        buttonPanel.add(btnThem);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        loadTableData();

        // Sự kiện chọn dòng để hiển thị popup lựa chọn
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Nhấn một lần
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int idHoaDon = (int) tableModel.getValueAt(row, 0);
                        hienThiPopupLuaChon(idHoaDon);
                    }
                }
            }
        });

        // Sự kiện nút Thêm
        btnThem.addActionListener(e -> hienThiFormThemHoaDon());

        // Sự kiện nút tìm kiếm
        btnTimKiem.addActionListener(e -> filterTable());

        // Sự kiện cho ô tìm kiếm khi nhấn Enter
        txtTimKiem.addActionListener(e -> filterTable());
    }

    private void filterTable() {
        String text = txtTimKiem.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<HoaDon> danhSach = hoaDonController.layDanhSachHoaDon();
        for (HoaDon hd : danhSach) {
            BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hd.getIdBenhNhan());
            tableModel.addRow(new Object[]{
                hd.getIdHoaDon(),
                hd.getIdBenhNhan(),
                benhNhan != null ? benhNhan.getHoTen() : "N/A",
                hd.getNgayTao(),
                hd.getTongTien(),
                hd.getTrangThai()
            });
        }
    }

    private void hienThiPopupLuaChon(int idHoaDon) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem menuItemXemChiTiet = new JMenuItem("Xem chi tiết");
        menuItemXemChiTiet.addActionListener(e -> {
            HoaDon hoaDon = hoaDonController.layHoaDonTheoId(idHoaDon);
            if (hoaDon != null) {
                hienThiPopupChiTiet(hoaDon);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn với ID: " + idHoaDon);
            }
        });
        popupMenu.add(menuItemXemChiTiet);

        JMenuItem menuItemSua = new JMenuItem("Sửa");
        menuItemSua.addActionListener(e -> {
            HoaDon hoaDon = hoaDonController.layHoaDonTheoId(idHoaDon);
            if (hoaDon != null) {
                hienThiPopupSua(hoaDon);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn với ID: " + idHoaDon);
            }
        });
        popupMenu.add(menuItemSua);

        JMenuItem menuItemXoa = new JMenuItem("Xóa");
        menuItemXoa.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hóa đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Gọi phương thức xóa hóa đơn từ controller
                try {
					if (hoaDonController.xoaDoanhThuTheoHoaDonId(idHoaDon)) {
					    loadTableData();
					    JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công.");
					} else {
					    JOptionPane.showMessageDialog(this, "Không thể xóa hóa đơn hoặc đã xảy ra lỗi.");
					}
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        popupMenu.add(menuItemXoa);

        // Hiển thị popup tại vị trí chuột
        Point mousePosition = table.getMousePosition();
        if (mousePosition != null) {
            popupMenu.show(table, mousePosition.x, mousePosition.y);
        }
    }

    private void hienThiPopupChiTiet(HoaDon hoaDon) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chi tiết Hóa Đơn", true);
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("ID Hóa Đơn:"));
        panel.add(new JLabel(String.valueOf(hoaDon.getIdHoaDon())));

        panel.add(new JLabel("ID Bệnh Nhân:"));
        panel.add(new JLabel(String.valueOf(hoaDon.getIdBenhNhan())));

        BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hoaDon.getIdBenhNhan());
        panel.add(new JLabel("Tên Bệnh Nhân:"));
        panel.add(new JLabel(benhNhan != null ? benhNhan.getHoTen() : "N/A"));

        panel.add(new JLabel("Ngày Tạo:"));
        panel.add(new JLabel(hoaDon.getNgayTao().toString()));

        panel.add(new JLabel("Tổng Tiền:"));
        panel.add(new JLabel(String.valueOf(hoaDon.getTongTien())));

        panel.add(new JLabel("Trạng Thái:"));
        panel.add(new JLabel(hoaDon.getTrangThai()));

        ThanhToanBenhNhan thanhToan = hoaDonController.layThanhToanTheoIdHoaDon(hoaDon.getIdHoaDon());
        panel.add(new JLabel("Phương thức TT:"));
        panel.add(new JLabel(thanhToan != null ? thanhToan.getHinhThucThanhToan() : "Chưa có"));

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private void hienThiPopupSua(HoaDon hoaDon) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sửa Hóa Đơn", true);
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblIdHoaDon = new JLabel("ID Hóa Đơn:");
        JTextField txtIdHoaDon = new JTextField(String.valueOf(hoaDon.getIdHoaDon()));
        txtIdHoaDon.setEnabled(false);
        JLabel lblIdBenhNhan = new JLabel("ID Bệnh Nhân:");
        JTextField txtIdBenhNhan = new JTextField(String.valueOf(hoaDon.getIdBenhNhan()));
        JLabel lblTongTien = new JLabel("Tổng Tiền:");
        JTextField txtTongTien = new JTextField(String.valueOf(hoaDon.getTongTien()));
        JLabel lblTrangThai = new JLabel("Trạng Thái:");
        JTextField txtTrangThai = new JTextField(hoaDon.getTrangThai());

        panel.add(lblIdHoaDon);
        panel.add(txtIdHoaDon);
        panel.add(lblIdBenhNhan);
        panel.add(txtIdBenhNhan);
        panel.add(lblTongTien);
        panel.add(txtTongTien);
        panel.add(lblTrangThai);
        panel.add(txtTrangThai);

        JButton btnCapNhat = new JButton("Cập nhật");
        panel.add(new JLabel()); // Để căn chỉnh
        panel.add(btnCapNhat);

        btnCapNhat.addActionListener(e -> {
            try {
                int idBenhNhanMoi = Integer.parseInt(txtIdBenhNhan.getText().trim());
                double tongTienMoi = Double.parseDouble(txtTongTien.getText().trim());
                String trangThaiMoi = txtTrangThai.getText().trim();

                hoaDon.setIdBenhNhan(idBenhNhanMoi);
                hoaDon.setTongTien(tongTienMoi);
                hoaDon.setTrangThai(trangThaiMoi);

                hoaDonController.capNhatHoaDon(hoaDon);
                loadTableData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thành công.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho ID Bệnh Nhân và Tổng Tiền.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật hóa đơn: " + ex.getMessage());
            }
        });

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private void hienThiFormThemHoaDon() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm Hóa Đơn", true);
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField txtIdBenhNhan = new JTextField();
        JTextField txtTongTien = new JTextField();
        JTextField txtTrangThai = new JTextField();

        panel.add(new JLabel("ID Bệnh Nhân:"));
        panel.add(txtIdBenhNhan);
        panel.add(new JLabel("Tổng Tiền:"));
        panel.add(txtTongTien);
        panel.add(new JLabel("Trạng Thái:"));
        panel.add(txtTrangThai);

        JButton btnThemmoi = new JButton("Thêm");
        panel.add(new JLabel("")); // Để căn chỉnh nút
        panel.add(btnThemmoi);

        btnThemmoi.addActionListener(e -> {
            try {
                int idBenhNhan = Integer.parseInt(txtIdBenhNhan.getText().trim());
                double tongTien = Double.parseDouble(txtTongTien.getText().trim());
                String trangThai = txtTrangThai.getText().trim();

                HoaDon hoaDon = new HoaDon();
                hoaDon.setIdBenhNhan(idBenhNhan);
                hoaDon.setNgayTao(new Date());
                hoaDon.setTongTien(tongTien);
                hoaDon.setTrangThai(trangThai);

                hoaDonController.themHoaDon(hoaDon);
                loadTableData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho ID Bệnh Nhân và Tổng Tiền.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm hóa đơn: " + ex.getMessage());
            }
        });

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Quản lý Hóa Đơn");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 600);
//        frame.setLocationRelativeTo(null);
//        frame.setContentPane(new HoaDonUI());
//        frame.setVisible(true);
//    }
}