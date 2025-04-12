// DoanhThuUI.java
package view;

import controller.DoanhThuController;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

public class DoanhThuUI extends JPanel {
    private DefaultTableModel modelDoanhThu;
    private JTable tableDoanhThu;
    private JButton btnThemMoiDoanhThu;
    private JTextField txtTimKiemDoanhThu;
    private JButton btnTimKiemDoanhThu;
    private TableRowSorter<DefaultTableModel> sorterDoanhThu;
    private JPopupMenu popupMenuDoanhThu;
    private JMenuItem menuItemSuaDoanhThu;
    private JMenuItem menuItemXoaDoanhThu;

    private DoanhThuController doanhThuController;
    private NumberFormat currencyFormat;

    public DoanhThuUI() {
        setLayout(new BorderLayout());

        // Khởi tạo NumberFormat mặc định (ví dụ: theo Locale VN)
        @SuppressWarnings("deprecation")
		Locale localeVN = new Locale("vi", "VN");
        currencyFormat = NumberFormat.getInstance(localeVN);
        currencyFormat.setMinimumFractionDigits(0);
        currencyFormat.setGroupingUsed(true);

        doanhThuController = new DoanhThuController(this);

        setBorder(BorderFactory.createTitledBorder(null, "Quản lý Doanh Thu", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 14)));
        modelDoanhThu = new DefaultTableModel(new Object[]{"ID", "ID Hóa Đơn", "Tên Bệnh Nhân", "Tháng/Năm", "Tổng Thu", "Trạng Thái"}, 0);
        tableDoanhThu = new JTable(modelDoanhThu);
        sorterDoanhThu = new TableRowSorter<>(modelDoanhThu);
        tableDoanhThu.setRowSorter(sorterDoanhThu);
        JScrollPane scrollPaneDoanhThu = new JScrollPane(tableDoanhThu);
        add(scrollPaneDoanhThu, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnThemMoiDoanhThu = new JButton("Thêm Mới");
        buttonPanel.add(btnThemMoiDoanhThu);
        topPanel.add(buttonPanel, BorderLayout.WEST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtTimKiemDoanhThu = new JTextField(15);
        btnTimKiemDoanhThu = new JButton("Tìm");
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtTimKiemDoanhThu);
        searchPanel.add(btnTimKiemDoanhThu);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ActionListener cho nút tìm kiếm Doanh Thu
        btnTimKiemDoanhThu.addActionListener(e -> filterDoanhThu());

        // KeyListener cho ô tìm kiếm Doanh Thu để xử lý sự kiện Enter
        txtTimKiemDoanhThu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterDoanhThu();
                }
            }
        });

        // MouseListener để hiển thị Popup Menu cho Doanh Thu
        tableDoanhThu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupMenu(e, tableDoanhThu, popupMenuDoanhThu);
            }
        });

        // ActionListener cho các mục trong Popup Menu Doanh Thu
        popupMenuDoanhThu = new JPopupMenu();
        menuItemSuaDoanhThu = new JMenuItem("Sửa");
        menuItemXoaDoanhThu = new JMenuItem("Xóa");
        popupMenuDoanhThu.add(menuItemSuaDoanhThu);
        popupMenuDoanhThu.add(menuItemXoaDoanhThu);

        menuItemSuaDoanhThu.addActionListener(e -> {
            if (tableDoanhThu.getSelectedRow() != -1) {
                suaDoanhThuAction();
            }
        });
        menuItemXoaDoanhThu.addActionListener(e -> {
            if (tableDoanhThu.getSelectedRow() != -1) {
                xoaDoanhThuAction();
            }
        });

        // ActionListener cho nút "Thêm Mới" Doanh Thu
        btnThemMoiDoanhThu.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                ThemDoanhThuDialog themDoanhThuDialog = new ThemDoanhThuDialog(topFrame, this);
                themDoanhThuDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy cửa sổ cha!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tải dữ liệu ban đầu
        doanhThuController.loadDoanhThuData();
    }

    private void filterDoanhThu() {
        String text = txtTimKiemDoanhThu.getText();
        if (text.trim().length() == 0) {
            sorterDoanhThu.setRowFilter(null);
        } else {
            sorterDoanhThu.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void showPopupMenu(MouseEvent e, JTable table, JPopupMenu popupMenu) {
        int r = table.rowAtPoint(e.getPoint());
        if (r >= 0 && r < table.getRowCount()) {
            table.setRowSelectionInterval(r, r);
        } else {
            table.clearSelection();
        }

        int rowindex = table.getSelectedRow();
        if (rowindex < 0)
            return;
        if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void suaDoanhThuAction() {
        int selectedRow = tableDoanhThu.getSelectedRow();
        if (selectedRow >= 0) {
            Object[] data = new Object[modelDoanhThu.getColumnCount()];
            for (int i = 0; i < data.length; i++) {
                data[i] = modelDoanhThu.getValueAt(selectedRow, i);
            }
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                SuaDoanhThuDialog suaDoanhThuDialog = new SuaDoanhThuDialog(topFrame, data, this);
                suaDoanhThuDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy cửa sổ cha!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaDoanhThuAction() {
        int selectedRow = tableDoanhThu.getSelectedRow();
        if (selectedRow >= 0) {
            int idDoanhThu = (int) modelDoanhThu.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa doanh thu này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                doanhThuController.xoaDoanhThu(idDoanhThu);
                doanhThuController.loadDoanhThuData(); // Tải lại dữ liệu
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public DoanhThuController getDoanhThuController() {
        return doanhThuController;
    }

    public DefaultTableModel getModelDoanhThu() {
        return modelDoanhThu;
    }

    public void loadDoanhThuData(Object[] rowData) {
        modelDoanhThu.addRow(rowData);
    }

    public void updateDoanhThuRow(int row, Object[] rowData) {
        for (int i = 0; i < rowData.length; i++) {
            modelDoanhThu.setValueAt(rowData[i], row, i);
        }
    }

    public void removeDoanhThuRow(int row) {
        modelDoanhThu.removeRow(row);
    }

    public NumberFormat getCurrencyFormat() {
        return currencyFormat;
    }
}