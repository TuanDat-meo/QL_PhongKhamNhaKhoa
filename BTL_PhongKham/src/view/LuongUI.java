// LuongUI.java
package view;

import controller.LuongController;
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

public class LuongUI extends JPanel {
    private DefaultTableModel modelLuong;
    private JTable tableLuong;
    private JButton btnThemMoiLuong;
    private JTextField txtTimKiemLuong;
    private JButton btnTimKiemLuong;
    private TableRowSorter<DefaultTableModel> sorterLuong;
    private JPopupMenu popupMenuLuong;
    private JMenuItem menuItemSuaLuong;
    private JMenuItem menuItemXoaLuong;

    private LuongController luongController;
    private NumberFormat currencyFormat;

    public LuongUI() {
        setLayout(new BorderLayout());

        // Khởi tạo NumberFormat mặc định (ví dụ: theo Locale VN)
        Locale localeVN = new Locale("vi", "VN");
        currencyFormat = NumberFormat.getInstance(localeVN);
        currencyFormat.setMinimumFractionDigits(0);
        currencyFormat.setGroupingUsed(true);

        luongController = new LuongController(this);

        setBorder(BorderFactory.createTitledBorder(null, "Quản lý Lương Nhân Viên", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 14)));
        modelLuong = new DefaultTableModel(new Object[]{"ID", "Họ Tên", "Tháng/Năm", "Lương Cơ Bản", "Thưởng", "Khấu Trừ", "Tổng Lương"}, 0);
        tableLuong = new JTable(modelLuong);
        sorterLuong = new TableRowSorter<>(modelLuong);
        tableLuong.setRowSorter(sorterLuong);
        JScrollPane scrollPaneLuong = new JScrollPane(tableLuong);
        add(scrollPaneLuong, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnThemMoiLuong = new JButton("Thêm Mới");
        buttonPanel.add(btnThemMoiLuong);
        topPanel.add(buttonPanel, BorderLayout.WEST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtTimKiemLuong = new JTextField(15);
        btnTimKiemLuong = new JButton("Tìm");
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtTimKiemLuong);
        searchPanel.add(btnTimKiemLuong);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Thêm ActionListener cho nút tìm kiếm Lương
        btnTimKiemLuong.addActionListener(e -> filterLuong());

        // Thêm KeyListener cho ô tìm kiếm Lương để xử lý sự kiện Enter
        txtTimKiemLuong.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterLuong();
                }
            }
        });

        // Thêm MouseListener để hiển thị Popup Menu cho Lương
        tableLuong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupMenu(e, tableLuong, popupMenuLuong);
            }
        });

        // ActionListener cho các mục trong Popup Menu Lương
        popupMenuLuong = new JPopupMenu();
        menuItemSuaLuong = new JMenuItem("Sửa");
        menuItemXoaLuong = new JMenuItem("Xóa");
        popupMenuLuong.add(menuItemSuaLuong);
        popupMenuLuong.add(menuItemXoaLuong);

        menuItemSuaLuong.addActionListener(e -> {
            if (tableLuong.getSelectedRow() != -1) {
                suaLuongAction();
            }
        });
        menuItemXoaLuong.addActionListener(e -> {
            if (tableLuong.getSelectedRow() != -1) {
                xoaLuongAction();
            }
        });

        // ActionListener cho nút "Thêm Mới" Lương
        btnThemMoiLuong.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                ThemLuongDialog themLuongDialog = new ThemLuongDialog(topFrame, this, luongController);
                themLuongDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy cửa sổ cha!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tải dữ liệu ban đầu
        luongController.loadLuongData();
    }

    private void filterLuong() {
        String text = txtTimKiemLuong.getText();
        if (text.trim().length() == 0) {
            sorterLuong.setRowFilter(null);
        } else {
            sorterLuong.setRowFilter(RowFilter.regexFilter("(?i)" + text));
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

    private void suaLuongAction() {
        int selectedRow = tableLuong.getSelectedRow();
        if (selectedRow >= 0) {
            Object[] data = new Object[modelLuong.getColumnCount()];
            for (int i = 0; i < data.length; i++) {
                data[i] = modelLuong.getValueAt(selectedRow, i);
            }
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                SuaLuongDialog suaLuongDialog = new SuaLuongDialog(topFrame, data, this, luongController);
                suaLuongDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy cửa sổ cha!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaLuongAction() {
        int selectedRow = tableLuong.getSelectedRow();
        if (selectedRow >= 0) {
            int idLuong = (int) modelLuong.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa thông tin lương này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                luongController.xoaLuong(idLuong);
                luongController.loadLuongData(); // Tải lại dữ liệu
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng lương để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public LuongController getLuongController() {
        return luongController;
    }

    public DefaultTableModel getModelLuong() {
        return modelLuong;
    }

    public void loadLuongData(Object[] rowData) {
        modelLuong.addRow(rowData);
    }

    public void updateLuongRow(int row, Object[] rowData) {
        for (int i = 0; i < rowData.length; i++) {
            modelLuong.setValueAt(rowData[i], row, i);
        }
    }

    public void removeLuongRow(int row) {
        modelLuong.removeRow(row);
    }

    public NumberFormat getCurrencyFormat() {
        return currencyFormat;
    }
}