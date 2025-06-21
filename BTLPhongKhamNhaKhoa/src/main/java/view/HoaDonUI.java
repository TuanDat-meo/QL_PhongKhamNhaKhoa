package view;

// CÁC IMPORT CẦN THIẾT (BAO GỒM CẢ THƯ VIỆN JCALENDAR)
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import controller.HoaDonController;
import controller.BenhNhanController;
import model.HoaDon;
import model.ThanhToanBenhNhan;
import util.ExportManager;
import util.ExportManager.MessageCallback;
import model.BenhNhan;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;


public class HoaDonUI extends JPanel implements MessageCallback {
    private Color primaryColor = new Color(79, 129, 189);
    private Color secondaryColor = new Color(141, 180, 226);
    private Color accentColor = new Color(192, 80, 77);
    private Color successColor = new Color(86, 156, 104);
    private Color warningColor = new Color(237, 187, 85);
    private Color backgroundColor = new Color(248, 249, 250);
    private Color textColor = new Color(33, 37, 41);
    private Color panelColor = new Color(255, 255, 255);
    private Color buttonTextColor = Color.WHITE;
    private Color tableHeaderColor = new Color(79, 129, 189);
    private Color tableStripeColor = new Color(245, 247, 250);
    private Color borderColor = new Color(222, 226, 230);
    private Color totalRowColor = new Color(232, 240, 254);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private Font totalRowFont = new Font("Segoe UI", Font.BOLD, 14);
    private HoaDonController hoaDonController;
    private BenhNhanController benhNhanController;
    private JTable tableHoaDon;
    private JTable tableTotalRow;
    private DefaultTableModel tableModel;
    private DefaultTableModel modelTotalRow;
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JButton btnThem;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet;
    private JMenuItem menuItemSua;
    private JMenuItem menuItemXoa;
    private NumberFormat currencyFormat;
    private double totalAmount = 0.0;
    private ExportManager exportManager;
    private java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("dd/MM/yyyy");

    public HoaDonUI() {
        initializeControllers();
        initializePanel();
        initializeFormatters();
        buildHeaderPanel();
        buildTablePanel();
        buildButtonPanel();
        setupEventListeners();
        setupPopupMenu();
        loadTableData();
    }
    
    private void initializeControllers() {
        hoaDonController = new HoaDonController();
        benhNhanController = new BenhNhanController();
        exportManager = new ExportManager(this, tableModel, this);
    }
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setFocusable(true);
    }
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
    private void initializeFormatters() {
        Locale localeVN = new Locale("vi", "VN");
        currencyFormat = NumberFormat.getInstance(localeVN);
        currencyFormat.setMinimumFractionDigits(0);
        currencyFormat.setGroupingUsed(true);
        dateFormatter.setLenient(false);
    }
    private void buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("QUẢN LÝ HÓA ĐƠN");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        txtTimKiem = new JTextField(18);
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(220, 38));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        btnTimKiem = createStyledButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(120, 38));
        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }
    private void buildTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initializeTable();
        styleTable();
        JPanel tablesContainer = new JPanel(new BorderLayout());
        tablesContainer.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(tableHoaDon);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.add(tableTotalRow, BorderLayout.CENTER);
        tablesContainer.add(scrollPane, BorderLayout.CENTER);
        tablesContainer.add(totalPanel, BorderLayout.SOUTH);
        tablePanel.add(tablesContainer, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);
    }
    private void initializeTable() {
        String[] columns = {"ID", "ID Bệnh Nhân", "Tên Bệnh Nhân", "Ngày Tạo", "Tổng Tiền", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) return Integer.class;
                if (columnIndex == 4) return Double.class;
                return String.class;
            }
        };
        tableHoaDon = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                     if (!(renderer instanceof DefaultTableCellRenderer && ((DefaultTableCellRenderer)renderer).getBackground().equals(getSelectionBackground()))) {
                        comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                     }
                }
                return comp;
            }
        };
        sorter = new TableRowSorter<>(tableModel);
        tableHoaDon.setRowSorter(sorter);
        modelTotalRow = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableTotalRow = new JTable(modelTotalRow) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                comp.setBackground(totalRowColor);
                return comp;
            }
        };
        modelTotalRow.addRow(new Object[]{null, null, null, "Tổng:", 0.0, null});
    }
    private void styleTable() {
        styleMainTable(tableHoaDon);
        tableTotalRow.setFont(totalRowFont);
        tableTotalRow.setRowHeight(45);
        tableTotalRow.setTableHeader(null);
        tableTotalRow.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(totalRowColor);
                setFont(totalRowFont);
                if (column == 3) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setText(value != null ? value.toString() : "");
                } else if (column == 4 && value instanceof Double) {
                    setText(currencyFormat.format((Double) value) + " VND");
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setText("");
                    setHorizontalAlignment(SwingConstants.CENTER);
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }
    private void styleMainTable(JTable table) {
        table.setFont(tableFont);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(229, 243, 255));
        table.setSelectionForeground(textColor);
        table.setFocusable(false);
        table.setBorder(null);
        if (table == tableHoaDon) {
            JTableHeader header = table.getTableHeader();
            header.setFont(tableHeaderFont);
            header.setBackground(tableHeaderColor);
            header.setForeground(Color.WHITE);
            header.setPreferredSize(new Dimension(header.getWidth(), 45));
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
            header.setReorderingAllowed(false);
            ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        }
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        DefaultTableCellRenderer defaultCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                } else {
                    c.setBackground(table.getSelectionBackground());
                }
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                if (value != null) {
                     setText(value.toString());
                }
                return c;
            }
        };
        for(int i=0; i < 4; i++) {
            if (i != 2) {
                 table.getColumnModel().getColumn(i).setCellRenderer(defaultCellRenderer);
            }
        }
        DefaultTableCellRenderer leftAlignRenderer = new DefaultTableCellRenderer() {
             @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                } else {
                    c.setBackground(table.getSelectionBackground());
                }
                ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 5));
                 if (value != null) {
                     setText(value.toString());
                }
                return c;
            }
        };
        table.getColumnModel().getColumn(2).setCellRenderer(leftAlignRenderer);
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JLabel)c).setHorizontalAlignment(SwingConstants.RIGHT);
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 10));
                if (value instanceof Double) {
                    setText(currencyFormat.format((Double) value) + " ");
                } else if (value != null) {
                    setText(value.toString());
                }
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                } else {
                     c.setBackground(table.getSelectionBackground());
                     c.setForeground(table.getSelectionForeground());
                }
                return c;
            }
        };
        table.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
        if (table == tableHoaDon) {
            int statusColumnIndex = 5;
            table.getColumnModel().getColumn(statusColumnIndex).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                             boolean isSelected, boolean hasFocus,
                                                             int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground()); 
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                        c.setForeground(textColor);
                    }
                    if (value != null) {
                        String trangThai = value.toString();
                        Font originalFont = c.getFont();
                        c.setFont(originalFont.deriveFont(Font.PLAIN));
                        if ("DaThanhToan".equalsIgnoreCase(trangThai)) {
                            c.setForeground(isSelected ? new Color(0,100,0) : new Color(34, 139, 34));
                            c.setFont(originalFont.deriveFont(Font.BOLD));
                        } else if ("ChuaThanhToan".equalsIgnoreCase(trangThai)) {
                            c.setForeground(isSelected ? new Color(139,0,0) : new Color(220, 20, 60));
                        } else if ("DangXuLy".equalsIgnoreCase(trangThai)) {
                            c.setForeground(isSelected ? new Color(204, 120, 0) :new Color(255, 140, 0));
                        } else if ("DaHuy".equalsIgnoreCase(trangThai)) {
                            c.setForeground(isSelected ? Color.DARK_GRAY : Color.GRAY);
                        }
                    }
                    return c;
                }
            });
        }
    }
    private void buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JButton btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> {
             if (exportManager == null) {
                exportManager = new ExportManager(HoaDonUI.this, tableModel, HoaDonUI.this);
            } else {
                 exportManager.setTableModel(tableModel);
            }
            exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor);
        });
        btnThem = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThem.setPreferredSize(new Dimension(100, 45));
        btnThem.addActionListener(e -> {
            try {
                hienThiFormThemHoaDon();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showNotification("Lỗi khi hiển thị form thêm hóa đơn: " + ex.getMessage(), NotificationType.ERROR);
            }
        });
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThem);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    private void setupEventListeners() {
        btnTimKiem.addActionListener(e -> {
            if (txtTimKiem.getText().trim().isEmpty()) {
                loadTableData();
                showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            } else {
                filterTable();
            }
        });
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (txtTimKiem.getText().trim().isEmpty()) {
                        loadTableData();
                        showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
                    } else {
                        filterTable();
                    }
                }
            }
        });
        tableHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = tableHoaDon.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tableHoaDon.getRowCount()) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        tableHoaDon.setRowSelectionInterval(row, row);
                        showPopupMenu(e);
                    } else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                        tableHoaDon.setRowSelectionInterval(row, row);
                        xemChiTietHoaDon();
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                         tableHoaDon.setRowSelectionInterval(row, row);
                    }
                } else {
                    if (!e.isPopupTrigger()){
                        tableHoaDon.clearSelection();
                    }
                }
            }
        });
    }
    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));
        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemSua = createStyledMenuItem("Chỉnh Sửa");
        menuItemXoa = createStyledMenuItem("Xóa");
        menuItemXoa.setForeground(accentColor);
        popupMenu.add(menuItemXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(menuItemSua);
        popupMenu.addSeparator();
        popupMenu.add(menuItemXoa);
        menuItemXemChiTiet.addActionListener(e -> {
            if (tableHoaDon.getSelectedRow() != -1) {
                xemChiTietHoaDon();
            } else {
                showNotification("Vui lòng chọn một hóa đơn.", NotificationType.WARNING);
            }
        });
        menuItemSua.addActionListener(e -> {
            if (tableHoaDon.getSelectedRow() != -1) {
                try {
                    suaHoaDon();
                } catch (SQLException e1) {
                    showNotification("Lỗi SQL khi chuẩn bị sửa: " + e1.getMessage(), NotificationType.ERROR);
                }
            } else {
                showNotification("Vui lòng chọn một hóa đơn.", NotificationType.WARNING);
            }
        });
        menuItemXoa.addActionListener(e -> {
            if (tableHoaDon.getSelectedRow() != -1) {
                xoaHoaDon();
            } else {
                showNotification("Vui lòng chọn một hóa đơn.", NotificationType.WARNING);
            }
        });
    }
    private void showPopupMenu(MouseEvent e) {
        if (tableHoaDon.getSelectedRow() >= 0) {
             popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    private JButton createStyledButton(String text) {
        return createRoundedButton(text, primaryColor, buttonTextColor, 10);
    }
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() -1 , getHeight() -1 , radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override
            public boolean isOpaque() { return false; }
        };
        button.setFont(buttonFont);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addMouseListener(new MouseAdapter() {
            private Color originalBgColor = bgColor;
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(originalBgColor));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBgColor);
            }
        });
        return button;
    }
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setBackground(Color.WHITE);
        menuItem.setForeground(textColor);
        menuItem.setOpaque(true);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(tableStripeColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE);
            }
        });
        return menuItem;
    }
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    public void loadTableData() {
        tableModel.setRowCount(0);
        totalAmount = 0.0;
        List<HoaDon> danhSach = hoaDonController.layDanhSachHoaDon();
        for (HoaDon hd : danhSach) {
            BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hd.getIdBenhNhan());
            tableModel.addRow(new Object[]{
                hd.getIdHoaDon(),
                hd.getIdBenhNhan(),
                benhNhan != null ? benhNhan.getHoTen() : "N/A",
                dateFormatter.format(hd.getNgayTao()),
                hd.getTongTien(),
                hd.getTrangThai()
            });
            totalAmount += hd.getTongTien();
        }
        sorter = new TableRowSorter<>(tableModel);
        tableHoaDon.setRowSorter(sorter);
        updateTotalRow();
    }
    private void updateTotalRow() {
        if (modelTotalRow.getRowCount() > 0) {
            modelTotalRow.setValueAt("Tổng:", 0, 3);
            modelTotalRow.setValueAt(totalAmount, 0, 4);
        } else {
             modelTotalRow.addRow(new Object[]{null, null, null, "Tổng:", totalAmount, null});
        }
    }
    private void filterTable() {
        String searchText = txtTimKiem.getText().trim();
        if (searchText.isEmpty()) {
            loadTableData();
            return;
        }
        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String normalizedSearch = normalizeVietnameseString(searchText.toLowerCase());
                for (int i = 0; i < entry.getValueCount(); i++) {
                    if (entry.getValue(i) != null) {
                        String valueStr;
                        if (entry.getValue(i) instanceof Double) {
                            valueStr = String.valueOf(entry.getValue(i));
                             String formattedValue = currencyFormat.format((Double) entry.getValue(i));
                             if (formattedValue.toLowerCase().contains(searchText.toLowerCase())) return true;
                        } else {
                            valueStr = entry.getValue(i).toString();
                        }
                        String normalizedValue = normalizeVietnameseString(valueStr.toLowerCase());
                        if (normalizedValue.contains(normalizedSearch)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        sorter.setRowFilter(rf);
        updateTotalForFilteredRows();
        if (tableHoaDon.getRowCount() == 0) {
            showNotification("Không tìm thấy kết quả nào cho: '" + searchText + "'", NotificationType.WARNING);
        }
    }
    private String normalizeVietnameseString(String str) {
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }
    private void updateTotalForFilteredRows() {
        double filteredTotal = 0.0;
        for (int i = 0; i < tableHoaDon.getRowCount(); i++) {
            Object amountObj = tableHoaDon.getValueAt(i, 4);
            if(amountObj instanceof Double) {
                filteredTotal += (Double) amountObj;
            }
        }
        if (modelTotalRow.getRowCount() > 0) {
            modelTotalRow.setValueAt(filteredTotal, 0, 4);
        }
    }
    private void xemChiTietHoaDon() {
        int selectedViewRow = tableHoaDon.getSelectedRow();
        if (selectedViewRow >= 0) {
            int modelRow = tableHoaDon.convertRowIndexToModel(selectedViewRow);
            int idHoaDon = (int) tableModel.getValueAt(modelRow, 0);
            HoaDon hoaDon = hoaDonController.layHoaDonTheoId(idHoaDon);
            if (hoaDon != null) {
                hienThiPopupChiTiet(hoaDon);
            } else {
                showNotification("Không tìm thấy hóa đơn với ID: " + idHoaDon, NotificationType.ERROR);
            }
        } else {
            showNotification("Vui lòng chọn một hóa đơn để xem chi tiết!", NotificationType.WARNING);
        }
    }
    private void suaHoaDon() throws SQLException {
        int selectedViewRow = tableHoaDon.getSelectedRow();
        if (selectedViewRow >= 0) {
            int modelRow = tableHoaDon.convertRowIndexToModel(selectedViewRow);
            int idHoaDon = (int) tableModel.getValueAt(modelRow, 0);
            HoaDon hoaDon = hoaDonController.layHoaDonTheoId(idHoaDon);
            if (hoaDon != null) {
                hienThiPopupSua(hoaDon);
            } else {
                showNotification("Không tìm thấy hóa đơn với ID: " + idHoaDon, NotificationType.ERROR);
            }
        } else {
            showNotification("Vui lòng chọn một hóa đơn để chỉnh sửa!", NotificationType.WARNING);
        }
    }
    private void xoaHoaDon() {
        int selectedViewRow = tableHoaDon.getSelectedRow();
        if (selectedViewRow >= 0) {
            int modelRow = tableHoaDon.convertRowIndexToModel(selectedViewRow);
            int idHoaDon = (int) tableModel.getValueAt(modelRow, 0);
            int choice = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Bạn có chắc chắn muốn xóa hóa đơn ID: " + idHoaDon + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = hoaDonController.xoaHoaDon(idHoaDon);
                if (success) {
                    loadTableData();
                    showNotification("Đã xóa hóa đơn ID: " + idHoaDon + " thành công!", NotificationType.SUCCESS);
                } else {
                    showNotification("Lỗi khi xóa hóa đơn ID: " + idHoaDon, NotificationType.ERROR);
                }
            }
        } else {
            showNotification("Vui lòng chọn một hóa đơn để xóa!", NotificationType.WARNING);
        }
    }

    private void hienThiFormThemHoaDon() throws SQLException {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Hóa Đơn Mới", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                dialog.getRootPane().requestFocusInWindow();
            }
        });

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabelDialog = new JLabel("THÊM HÓA ĐƠN MỚI");
        titleLabelDialog.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabelDialog.setForeground(Color.WHITE);
        headerPanel.add(titleLabelDialog, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
        DefaultComboBoxModel<String> benhNhanComboBoxModel = new DefaultComboBoxModel<>();
        for (BenhNhan bn : danhSachBenhNhan) {
            benhNhanComboBoxModel.addElement(bn.getHoTen() + " (ID: " + bn.getIdBenhNhan() + ")");
        }
        JComboBox<String> cmbTenBenhNhan = new JComboBox<>(benhNhanComboBoxModel);
        styleComboBox(cmbTenBenhNhan, "Chọn bệnh nhân...");
        cmbTenBenhNhan.setSelectedIndex(-1);
        cmbTenBenhNhan.setName("cmbBenhNhan");

        JDateChooser dateChooserNgayTao = new JDateChooser();
        dateChooserNgayTao.setDate(new Date());
        styleDateChooser(dateChooserNgayTao);

        JTextField txtTongTien = new JTextField();
        styleTextField(txtTongTien);
        txtTongTien.setHorizontalAlignment(JTextField.RIGHT);

        String[] trangThaiOptions = {"Chưa thanh toán", "Đã thanh toán", "Đang xử lý", "Đã hủy"};
        JComboBox<String> cmbTrangThai = new JComboBox<>(trangThaiOptions);
        styleComboBox(cmbTrangThai, "Chọn trạng thái...");
        cmbTrangThai.setSelectedIndex(-1);
        cmbTrangThai.setName("cmbTrangThai");


        addFormField(formPanel, "Bệnh Nhân:", cmbTenBenhNhan);
        addFormField(formPanel, "Ngày Tạo:", dateChooserNgayTao);
        addFormField(formPanel, "Tổng Tiền (VND):", txtTongTien);
        addFormField(formPanel, "Trạng Thái:", cmbTrangThai);
        formPanel.add(Box.createVerticalStrut(15));

        JPanel buttonPanelDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanelDialog.setBackground(Color.WHITE);
        buttonPanelDialog.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton cancelButton = createRoundedButton("Hủy", new Color(108, 117, 125), buttonTextColor, 10);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton submitButton = createRoundedButton("Lưu", successColor, buttonTextColor, 10);
        submitButton.addActionListener(e -> {
            clearAllErrors(cmbTenBenhNhan, dateChooserNgayTao, txtTongTien, cmbTrangThai);
            boolean isFormValid = true;

            if (cmbTenBenhNhan.getSelectedItem() == null) {
                setError(cmbTenBenhNhan, "Vui lòng chọn một bệnh nhân.");
                isFormValid = false;
            }

            Date ngayTao = dateChooserNgayTao.getDate();
            if (ngayTao == null) {
                setError(dateChooserNgayTao, "Vui lòng chọn ngày tạo hợp lệ.");
                isFormValid = false;
            } else {
                Calendar calToday = Calendar.getInstance();
                if (ngayTao.after(calToday.getTime())) {
                    setError(dateChooserNgayTao, "Ngày tạo không được lớn hơn ngày hiện tại.");
                    isFormValid = false;
                }
            }

            if (txtTongTien.getText().trim().isEmpty()) {
                setError(txtTongTien, "Vui lòng nhập tổng tiền.");
                isFormValid = false;
            } else {
                try {
                    Number parsedNumber = currencyFormat.parse(txtTongTien.getText().trim());
                    double tongTien = parsedNumber.doubleValue();
                    if (tongTien < 0) {
                        setError(txtTongTien, "Tổng tiền không thể là số âm.");
                        isFormValid = false;
                    }
                } catch (java.text.ParseException ex) {
                    setError(txtTongTien, "Tổng tiền không hợp lệ. Vui lòng nhập một số.");
                    isFormValid = false;
                }
            }

            if (cmbTrangThai.getSelectedItem() == null) {
                setError(cmbTrangThai, "Vui lòng chọn trạng thái cho hóa đơn.");
                isFormValid = false;
            }
            
            if (!isFormValid) {
                showNotification("Vui lòng điền đúng và đủ thông tin.", NotificationType.WARNING);
                return;
            }

            try {
                String selectedBenhNhanStr = (String) cmbTenBenhNhan.getSelectedItem();
                int idBenhNhan = -1;
                Pattern patternId = Pattern.compile("\\(ID: (\\d+)\\)");
                Matcher matcherId = patternId.matcher(selectedBenhNhanStr);
                if (matcherId.find()) {
                    idBenhNhan = Integer.parseInt(matcherId.group(1));
                }

                double tongTienValue = currencyFormat.parse(txtTongTien.getText().trim()).doubleValue();
                String trangThaiUI = (String) cmbTrangThai.getSelectedItem();
                String trangThaiController;
                 switch (trangThaiUI) {
                    case "Đã thanh toán": trangThaiController = "DaThanhToan"; break;
                    case "Đang xử lý": trangThaiController = "DangXuLy"; break;
                    case "Đã hủy": trangThaiController = "DaHuy"; break;
                    default: trangThaiController = "ChuaThanhToan";
                }

                HoaDon hoaDon = new HoaDon();
                hoaDon.setIdBenhNhan(idBenhNhan);
                hoaDon.setNgayTao(ngayTao);
                hoaDon.setTongTien(tongTienValue);
                hoaDon.setTrangThai(trangThaiController);

                boolean success = hoaDonController.themHoaDon(hoaDon);
                if (success) {
                    loadTableData();
                    dialog.dispose();
                    showNotification("Thêm hóa đơn thành công!", NotificationType.SUCCESS);
                } else {
                    showNotification("Thêm hóa đơn thất bại. Vui lòng thử lại.", NotificationType.ERROR);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showNotification("Lỗi khi thêm hóa đơn: " + ex.getMessage(), NotificationType.ERROR);
            }
        });

        buttonPanelDialog.add(submitButton);
        buttonPanelDialog.add(cancelButton);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanelDialog, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    private void hienThiPopupChiTiet(HoaDon hoaDon) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi Tiết Hóa Đơn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(480, 420);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabelDialog = new JLabel("CHI TIẾT HÓA ĐƠN");
        titleLabelDialog.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabelDialog.setForeground(Color.WHITE);
        headerPanel.add(titleLabelDialog, BorderLayout.CENTER);
        JPanel detailContentPanel = new JPanel();
        detailContentPanel.setLayout(new BoxLayout(detailContentPanel, BoxLayout.Y_AXIS));
        detailContentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        detailContentPanel.setBackground(Color.WHITE);
        BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hoaDon.getIdBenhNhan());
        ThanhToanBenhNhan thanhToan = hoaDonController.layThanhToanTheoIdHoaDon(hoaDon.getIdHoaDon());
        addDetailField(detailContentPanel, "ID Hóa Đơn:", String.valueOf(hoaDon.getIdHoaDon()));
        addDetailField(detailContentPanel, "Tên Bệnh Nhân:", benhNhan != null ? benhNhan.getHoTen() : "Không rõ");
        addDetailField(detailContentPanel, "ID Bệnh Nhân:", String.valueOf(hoaDon.getIdBenhNhan()));
        addDetailField(detailContentPanel, "Ngày Tạo:", dateFormatter.format(hoaDon.getNgayTao()));
        addDetailField(detailContentPanel, "Tổng Tiền:", currencyFormat.format(hoaDon.getTongTien()) + " VND");
        String trangThaiDisplay;
        switch (hoaDon.getTrangThai()) {
            case "DaThanhToan": trangThaiDisplay = "Đã thanh toán"; break;
            case "ChuaThanhToan": trangThaiDisplay = "Chưa thanh toán"; break;
            case "DangXuLy": trangThaiDisplay = "Đang xử lý"; break;
            case "DaHuy": trangThaiDisplay = "Đã hủy"; break;
            default: trangThaiDisplay = hoaDon.getTrangThai();
        }
        addDetailField(detailContentPanel, "Trạng Thái HĐ:", trangThaiDisplay);
        if (thanhToan != null) {
            addDetailField(detailContentPanel, "ID Thanh Toán:", String.valueOf(thanhToan.getIdThanhToan()));
            addDetailField(detailContentPanel, "Phương thức TT:", thanhToan.getHinhThucThanhToan() != null ? thanhToan.getHinhThucThanhToan() : "Chưa có");
            String trangThaiTTDisplay;
             switch (thanhToan.getTrangThai()) {
                case "ThanhToanThanhCong": trangThaiTTDisplay = "Thành công"; break;
                case "ThanhToanThatBai": trangThaiTTDisplay = "Thất bại"; break;
                case "HuyThanhToan": trangThaiTTDisplay = "Đã hủy"; break;
                case "DangChoXuLy": trangThaiTTDisplay = "Đang chờ"; break;
                default: trangThaiTTDisplay = thanhToan.getTrangThai();
            }
            addDetailField(detailContentPanel, "Trạng Thái TT:", trangThaiTTDisplay);
            if (thanhToan.getMaQR() != null && !thanhToan.getMaQR().isEmpty()) {
                 addDetailField(detailContentPanel, "Mã QR:", thanhToan.getMaQR());
            }
        } else {
            addDetailField(detailContentPanel, "Thanh Toán:", "Chưa có thông tin thanh toán");
        }
        JPanel buttonPanelDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanelDialog.setBackground(Color.WHITE);
        buttonPanelDialog.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton closeButton = createRoundedButton("Đóng", secondaryColor, textColor, 10);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanelDialog.add(closeButton);
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(detailContentPanel), BorderLayout.CENTER);
        dialog.add(buttonPanelDialog, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void hienThiPopupSua(HoaDon hoaDon) throws SQLException {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh Sửa Hóa Đơn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabelDialog = new JLabel("CHỈNH SỬA HÓA ĐƠN (ID: " + hoaDon.getIdHoaDon() + ")");
        titleLabelDialog.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabelDialog.setForeground(Color.WHITE);
        headerPanel.add(titleLabelDialog, BorderLayout.CENTER);
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
        DefaultComboBoxModel<String> benhNhanComboBoxModel = new DefaultComboBoxModel<>();
        BenhNhan benhNhanHienTai = benhNhanController.timKiemBenhNhanTheoId(hoaDon.getIdBenhNhan());
        String tenBenhNhanSelected = null;
        if (benhNhanHienTai != null) {
             tenBenhNhanSelected = benhNhanHienTai.getHoTen() + " (ID: " + benhNhanHienTai.getIdBenhNhan() + ")";
        }
        for (BenhNhan bn : danhSachBenhNhan) {
             benhNhanComboBoxModel.addElement(bn.getHoTen() + " (ID: " + bn.getIdBenhNhan() + ")");
        }
        JComboBox<String> cmbTenBenhNhan = new JComboBox<>(benhNhanComboBoxModel);
        styleComboBox(cmbTenBenhNhan, "Chọn bệnh nhân...");
        cmbTenBenhNhan.setSelectedItem(tenBenhNhanSelected);
        
        JDateChooser dateChooserNgayTao = new JDateChooser();
        dateChooserNgayTao.setDate(hoaDon.getNgayTao());
        styleDateChooser(dateChooserNgayTao);

        JTextField txtTongTien = new JTextField(currencyFormat.format(hoaDon.getTongTien()));
        styleTextField(txtTongTien);
        txtTongTien.setHorizontalAlignment(JTextField.RIGHT);

        String[] trangThaiOptions = {"Chưa thanh toán", "Đã thanh toán", "Đang xử lý", "Đã hủy"};
        JComboBox<String> cmbTrangThai = new JComboBox<>(trangThaiOptions);
        String currentTrangThaiUI;
        switch (hoaDon.getTrangThai()) {
            case "DaThanhToan": currentTrangThaiUI = "Đã thanh toán"; break;
            case "ChuaThanhToan": currentTrangThaiUI = "Chưa thanh toán"; break;
            case "DangXuLy": currentTrangThaiUI = "Đang xử lý"; break;
            case "DaHuy": currentTrangThaiUI = "Đã hủy"; break;
            default: currentTrangThaiUI = null;
        }
        styleComboBox(cmbTrangThai, "Chọn trạng thái...");
        cmbTrangThai.setSelectedItem(currentTrangThaiUI);

        addFormField(formPanel, "Bệnh Nhân:", cmbTenBenhNhan);
        addFormField(formPanel, "Ngày Tạo:", dateChooserNgayTao);
        addFormField(formPanel, "Tổng Tiền (VND):", txtTongTien);
        addFormField(formPanel, "Trạng Thái:", cmbTrangThai);
        formPanel.add(Box.createVerticalStrut(15));
        JPanel buttonPanelDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanelDialog.setBackground(Color.WHITE);
        buttonPanelDialog.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton cancelButton = createRoundedButton("Hủy", new Color(108, 117, 125), buttonTextColor, 10);
        cancelButton.addActionListener(e -> dialog.dispose());
        JButton submitButton = createRoundedButton("Cập nhật", warningColor, buttonTextColor, 10);
        submitButton.addActionListener(e -> {
            try {
                String selectedBenhNhanStr = (String) cmbTenBenhNhan.getSelectedItem();
                 if (selectedBenhNhanStr == null) {
                    showNotification("Vui lòng chọn bệnh nhân.", NotificationType.WARNING);
                    return;
                }
                int idBenhNhanMoi = -1;
                Pattern patternId = Pattern.compile("\\(ID: (\\d+)\\)");
                Matcher matcherId = patternId.matcher(selectedBenhNhanStr);
                if (matcherId.find()) {
                    idBenhNhanMoi = Integer.parseInt(matcherId.group(1));
                }
                 if (idBenhNhanMoi == -1) {
                     showNotification("Không thể xác định ID bệnh nhân mới.", NotificationType.ERROR);
                    return;
                }
                hoaDon.setIdBenhNhan(idBenhNhanMoi);
                Date ngayTaoMoi = dateChooserNgayTao.getDate();
                if(ngayTaoMoi == null) {
                     showNotification("Ngày tạo không hợp lệ.", NotificationType.WARNING);
                    return;
                }
                hoaDon.setNgayTao(ngayTaoMoi);

                double tongTienMoi;
                try {
                    Number parsedNumber = currencyFormat.parse(txtTongTien.getText().trim());
                    tongTienMoi = parsedNumber.doubleValue();
                     if (tongTienMoi < 0) {
                         showNotification("Tổng tiền không thể âm.", NotificationType.WARNING);
                        return;
                    }
                    hoaDon.setTongTien(tongTienMoi);
                } catch (java.text.ParseException ex) {
                    showNotification("Tổng tiền không hợp lệ.", NotificationType.WARNING);
                    return;
                }
                String trangThaiUIMoi = (String) cmbTrangThai.getSelectedItem();
                if(trangThaiUIMoi == null){
                     showNotification("Trạng thái mới không hợp lệ.", NotificationType.ERROR);
                    return;
                }
                String trangThaiControllerMoi;
                switch (trangThaiUIMoi) {
                    case "Đã thanh toán": trangThaiControllerMoi = "DaThanhToan"; break;
                    case "Chưa thanh toán": trangThaiControllerMoi = "ChuaThanhToan"; break;
                    case "Đang xử lý": trangThaiControllerMoi = "DangXuLy"; break;
                    case "Đã hủy": trangThaiControllerMoi = "DaHuy"; break;
                    default:
                        showNotification("Trạng thái mới không hợp lệ.", NotificationType.ERROR);
                        return;
                }
                hoaDon.setTrangThai(trangThaiControllerMoi);
                boolean success = hoaDonController.capNhatHoaDon(hoaDon);
                if (success) {
                    loadTableData();
                    dialog.dispose();
                    showNotification("Cập nhật hóa đơn ID: " + hoaDon.getIdHoaDon() + " thành công!", NotificationType.SUCCESS);
                } else {
                    showNotification("Cập nhật hóa đơn thất bại.", NotificationType.ERROR);
                }
            } catch (Exception ex) {
                showNotification("Lỗi khi cập nhật hóa đơn: " + ex.getMessage(), NotificationType.ERROR);
            }
        });
        buttonPanelDialog.add(submitButton);
        buttonPanelDialog.add(cancelButton);
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanelDialog, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
        JLabel label = new JLabel(labelText);
        label.setFont(regularFont);
        label.setPreferredSize(new Dimension(120, 30));
        fieldPanel.add(label, BorderLayout.WEST);
        JPanel fieldWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        fieldWrapper.setBackground(Color.WHITE);
        fieldWrapper.add(field);
        fieldPanel.add(fieldWrapper, BorderLayout.CENTER);
        panel.add(fieldPanel);
    }
    private void addDetailField(JPanel panel, String labelText, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(new EmptyBorder(6, 0, 6, 0));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(140, 25));
        label.setForeground(textColor.darker());
        JLabel valueLabel = new JLabel(value != null && !value.isEmpty() ? value : "---");
        valueLabel.setFont(regularFont);
        valueLabel.setForeground(textColor);
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(valueLabel, BorderLayout.CENTER);
        panel.add(fieldPanel);
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(regularFont);
        textField.setPreferredSize(new Dimension(250, 38));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(8, borderColor.brighter()),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        textField.setBackground(new Color(250, 250, 255));
    }
    
    private void styleComboBox(JComboBox<?> comboBox, String prompt) {
        comboBox.setFont(regularFont);
        comboBox.setPreferredSize(new Dimension(250, 38));
        comboBox.setBackground(Color.WHITE);
        
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                if (value == null) {
                    label.setText(prompt);
                    label.setForeground(Color.GRAY);
                } else {
                    label.setText(value.toString());
                    if (!isSelected) {
                        label.setForeground(textColor);
                    }
                }
                return label;
            }
        });
        
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor.brighter(), 1, true),
                BorderFactory.createEmptyBorder(0, 5, 0, 0))
        );
    }

    private void styleDateChooser(JDateChooser dateChooser) {
        dateChooser.setPreferredSize(new Dimension(250, 38));
        dateChooser.setFont(regularFont);
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setBorder(null);

        JTextFieldDateEditor editor = (JTextFieldDateEditor) dateChooser.getDateEditor();
        styleTextField(editor);
    }

    private void setError(JComponent component, String message) {
        component.setToolTipText(message);
        Color errorColor = accentColor;
        
        if (component instanceof JDateChooser) {
            JTextFieldDateEditor editor = (JTextFieldDateEditor) ((JDateChooser) component).getDateEditor();
            editor.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(8, errorColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else if (component instanceof JTextField) {
             component.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(8, errorColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else if (component instanceof JComboBox) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(errorColor, 1, true),
                BorderFactory.createEmptyBorder(0, 5, 0, 0))
            );
        }
    }
    
    private void clearAllErrors(JComponent... components) {
        for (JComponent component : components) {
            component.setToolTipText(null);
            if (component instanceof JTextField) {
                styleTextField((JTextField) component);
            } else if (component instanceof JComboBox) {
                if ("cmbBenhNhan".equals(component.getName())) {
                    styleComboBox((JComboBox<?>) component, "Chọn bệnh nhân...");
                } else {
                    styleComboBox((JComboBox<?>) component, "Chọn trạng thái...");
                }
            } else if (component instanceof JDateChooser) {
                styleDateChooser((JDateChooser) component);
            }
        }
    }
    private void showNotification(String message, NotificationType type) {
        JDialog toastDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this));
        toastDialog.setUndecorated(true);
        toastDialog.setAlwaysOnTop(true);
        toastDialog.setFocusableWindowState(false);
        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
            }
             @Override
            public boolean isOpaque() {
                return false;
            }
        };
        toastPanel.setBackground(type.color);
        toastPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        messageLabel.setForeground(Color.WHITE);
        toastPanel.add(messageLabel);
        toastDialog.add(toastPanel);
        toastDialog.pack();
        GraphicsConfiguration gc = getGraphicsConfiguration();
        Rectangle screenBounds = gc.getBounds();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        int x = screenBounds.x + screenBounds.width - toastDialog.getWidth() - screenInsets.right - 15;
        int y = screenBounds.y + screenBounds.height - toastDialog.getHeight() - screenInsets.bottom - 15;
        toastDialog.setLocation(x,y);
        Timer fadeInTimer = new Timer(20, null);
        final float[] opacity = {0f};
        fadeInTimer.addActionListener(ae -> {
            opacity[0] += 0.05f;
            if (opacity[0] >= 1f) {
                opacity[0] = 1f;
                fadeInTimer.stop();
                 Timer autoCloseTimer = new Timer(2500, eClose -> toastDialog.dispose());
                 autoCloseTimer.setRepeats(false);
                 autoCloseTimer.start();
            }
            toastDialog.setOpacity(opacity[0]);
        });
        toastDialog.setOpacity(0f);
        toastDialog.setVisible(true);
        fadeInTimer.start();
    }
    private enum NotificationType {
        SUCCESS(new Color(0, 153, 51, 230), "Thành công"),
        WARNING(new Color(255, 153, 0, 230), "Cảnh báo"),
        ERROR(new Color(204, 0, 0, 230), "Lỗi");
        private final Color color;
        private final String title;
        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }
    private class CustomBorder extends LineBorder {
        private int radius;
        public CustomBorder(int radius, Color color) {
            super(color, 1);
            this.radius = radius;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getLineColor());
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
         @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius/2;
            return insets;
        }
    }
    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private boolean hasShadow;
        private int shadowSize = 3; 
        private int shadowOpacity = 30;
        public RoundedPanel(int radius, boolean hasShadow) {
            super();
            this.cornerRadius = radius;
            this.hasShadow = hasShadow;
            setOpaque(false);
            if (hasShadow) {
                setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            if (hasShadow) {
                 for (int i = 0; i < shadowSize; i++) {
                    float alpha = (float)shadowOpacity * (1.0f - (float)i / shadowSize) / 255.0f;
                    if (alpha < 0) alpha = 0;
                    if (alpha > 1) alpha = 1;
                    g2.setColor(new Color(0, 0, 0, (int)(alpha * 20)));
                    g2.fillRoundRect(i, i, panelWidth - i * 2, panelHeight - i * 2, cornerRadius, cornerRadius);
                }
            }
            g2.setColor(getBackground());
            g2.fillRoundRect(shadowSize, shadowSize, 
                             panelWidth - 2 * shadowSize, panelHeight - 2 * shadowSize, 
                             cornerRadius, cornerRadius);
            g2.dispose();
             super.paintComponent(g);
        }
    }
    @Override
    public void showSuccessToast(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }
    @Override
    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    @Override
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}