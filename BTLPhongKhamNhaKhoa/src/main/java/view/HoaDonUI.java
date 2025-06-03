package view;

import controller.HoaDonController;
import controller.BenhNhanController;
// import controller.DoanhThuController; // Import này có vẻ không được dùng trực tiếp trong HoaDonUI này
import model.HoaDon;
import model.ThanhToanBenhNhan;
import util.ExportManager;
import util.ExportManager.MessageCallback;
// import view.DoanhThuUI.NotificationType; // Được thay thế bằng enum NotificationType nội bộ
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
import java.util.Date; // Đảm bảo import này có
import java.util.List; // Đảm bảo import này có
import java.util.Locale; // Đảm bảo import này có
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// import java.util.Map; // Cần nếu dùng gạch ngang chữ
// import java.awt.font.TextAttribute; // Cần nếu dùng gạch ngang chữ
// import java.util.HashMap; // Cần nếu dùng gạch ngang chữ


public class HoaDonUI extends JPanel implements MessageCallback {
    private Color primaryColor = new Color(79, 129, 189);     // Professional blue
    private Color secondaryColor = new Color(141, 180, 226);  // Lighter blue
    private Color accentColor = new Color(192, 80, 77);       // Refined red for delete
    private Color successColor = new Color(86, 156, 104);     // Elegant green for add
    private Color warningColor = new Color(237, 187, 85);     // Softer yellow for edit
    private Color backgroundColor = new Color(248, 249, 250); // Extremely light gray background
    private Color textColor = new Color(33, 37, 41);          // Near-black text
    private Color panelColor = new Color(255, 255, 255);      // White panels
    private Color buttonTextColor = Color.WHITE;
    private Color tableHeaderColor = new Color(79, 129, 189); // Match primary color
    private Color tableStripeColor = new Color(245, 247, 250); // Very light stripe
    private Color borderColor = new Color(222, 226, 230);     // Light gray borders
    private Color totalRowColor = new Color(232, 240, 254);   // Light blue for total row

    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private Font totalRowFont = new Font("Segoe UI", Font.BOLD, 14);

    private HoaDonController hoaDonController;
    private BenhNhanController benhNhanController;

    // private DefaultTableModel modelHoaDon; // Biến này có vẻ không được dùng, bạn đang dùng tableModel
    private JTable tableHoaDon;
    private JTable tableTotalRow;
    private DefaultTableModel tableModel; // Model chính cho tableHoaDon
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
        buildTablePanel(); // initializeTable() và styleTable() được gọi bên trong này
        buildButtonPanel();
        setupEventListeners();
        setupPopupMenu();
        loadTableData();
    }

    private void initializeControllers() {
        hoaDonController = new HoaDonController();
        benhNhanController = new BenhNhanController();
        // Nếu modelHoaDon không được dùng, có thể exportManager cần tableModel thay thế
        exportManager = new ExportManager(this, tableModel, this); // Sửa thành tableModel nếu modelHoaDon không dùng
    }

    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void initializeFormatters() {
        Locale localeVN = new Locale("vi", "VN");
        currencyFormat = NumberFormat.getInstance(localeVN);
        currencyFormat.setMinimumFractionDigits(0); // Hiển thị số nguyên cho tiền tệ nếu không có phần thập phân
        currencyFormat.setGroupingUsed(true);
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

        initializeTable(); // Khởi tạo tableModel và các bảng
        styleTable();      // Style các bảng, bao gồm cả việc thêm renderer tô màu

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
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) { // ID, ID Bệnh Nhân
                    return Integer.class;
                } else if (columnIndex == 4) { // Tổng Tiền
                    return Double.class;
                }
                // Tên Bệnh Nhân, Ngày Tạo (sau khi format), Trạng Thái là String
                return String.class;
            }
        };
        tableHoaDon = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                // Xử lý màu nền xen kẽ được thực hiện trong renderer tùy chỉnh hoặc ở đây nếu không có renderer tùy chỉnh cho tất cả các cột
                if (!isRowSelected(row)) { // Chỉ đặt màu nền xen kẽ nếu hàng không được chọn
                     if (!(renderer instanceof DefaultTableCellRenderer && ((DefaultTableCellRenderer)renderer).getBackground().equals(getSelectionBackground()))) {
                        comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                     }
                }
                return comp;
            }
        };
        sorter = new TableRowSorter<>(tableModel);
        tableHoaDon.setRowSorter(sorter);

        // Model và Table cho hàng tổng cộng
        modelTotalRow = new DefaultTableModel(columns, 0) { // Dùng cùng columns để căn chỉnh
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTotalRow = new JTable(modelTotalRow) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                comp.setBackground(totalRowColor); // Màu nền cố định cho hàng tổng
                return comp;
            }
        };
        modelTotalRow.addRow(new Object[]{null, null, null, "Tổng:", 0.0, null});
    }

    private void styleTable() {
        styleMainTable(tableHoaDon);
        // Không cần styleMainTable cho tableTotalRow nữa vì nó đã có renderer riêng và không có header
        // styleMainTable(tableTotalRow);
        tableTotalRow.setFont(totalRowFont);
        tableTotalRow.setRowHeight(45);
        tableTotalRow.setTableHeader(null); // Hàng tổng không cần header

        // Renderer cho hàng tổng cộng
        tableTotalRow.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(totalRowColor);
                setFont(totalRowFont); // Áp dụng font cho tất cả các cell của hàng tổng

                if (column == 3) { // Cột "Ngày Tạo" giờ là nhãn "Tổng:"
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setText(value != null ? value.toString() : "");
                } else if (column == 4 && value instanceof Double) { // Cột "Tổng Tiền"
                    setText(currencyFormat.format((Double) value) + " VND");
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setText(""); // Các cột khác để trống
                    setHorizontalAlignment(SwingConstants.CENTER);
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10)); // Thêm padding
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
        // table.setAutoCreateRowSorter(true); // Đã set rowSorter ở initializeTable
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

        // Preferred Widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);   // ID Bệnh Nhân
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Tên Bệnh Nhân
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Ngày Tạo
        table.getColumnModel().getColumn(4).setPreferredWidth(150);  // Tổng Tiền
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // Trạng Thái

        // Default renderer for most cells (handles alternating row color and centering)
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
                // Giá trị sẽ được setText bởi các renderer chuyên biệt nếu có
                if (value != null) {
                     setText(value.toString()); // Hiển thị giá trị gốc nếu không có renderer chuyên biệt ghi đè
                }
                return c;
            }
        };
        // Áp dụng default renderer này cho các cột chưa có renderer chuyên biệt
        // Cột ID, ID Bệnh Nhân, Tên Bệnh Nhân, Ngày Tạo sẽ dùng renderer này (nếu không có renderer nào khác ghi đè)
        for(int i=0; i < 4; i++) { // ID, ID BN, Ten BN, Ngay Tao
            if (i != 2) { // Tên bệnh nhân có thể muốn căn trái
                 table.getColumnModel().getColumn(i).setCellRenderer(defaultCellRenderer);
            }
        }
         // Căn trái cho tên bệnh nhân
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
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 5)); // Thêm padding trái
                 if (value != null) {
                     setText(value.toString());
                }
                return c;
            }
        };
        table.getColumnModel().getColumn(2).setCellRenderer(leftAlignRenderer); // Tên Bệnh Nhân


        // Renderer cho cột tiền tệ "Tổng Tiền"
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JLabel)c).setHorizontalAlignment(SwingConstants.RIGHT); // Căn phải cho tiền tệ
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 10)); // Padding phải
                if (value instanceof Double) {
                    setText(currencyFormat.format((Double) value) + " "); // Thêm khoảng trắng cuối
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

        // >>>>>>>>>>>>>>>>>> ĐOẠN CODE TÔ MÀU TRẠNG THÁI <<<<<<<<<<<<<<<<<<
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

                    // Xử lý màu nền trước
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground()); 
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                        c.setForeground(textColor); // Màu chữ mặc định khi không chọn
                    }

                    if (value != null) {
                        String trangThai = value.toString();
                        Font originalFont = c.getFont();
                        c.setFont(originalFont.deriveFont(Font.PLAIN)); // Reset font

                        // Chuỗi so sánh phải khớp với giá trị trong tableModel
                        if ("DaThanhToan".equalsIgnoreCase(trangThai)) {
                            c.setForeground(isSelected ? new Color(0,100,0) : new Color(34, 139, 34)); // Green
                            c.setFont(originalFont.deriveFont(Font.BOLD));
                        } else if ("ChuaThanhToan".equalsIgnoreCase(trangThai)) {
                            c.setForeground(isSelected ? new Color(139,0,0) : new Color(220, 20, 60));  // Red
                        } else if ("DangXuLy".equalsIgnoreCase(trangThai)) {
                            c.setForeground(isSelected ? new Color(204, 120, 0) :new Color(255, 140, 0)); // Orange
                        } else if ("DaHuy".equalsIgnoreCase(trangThai)) {
                            c.setForeground(isSelected ? Color.DARK_GRAY : Color.GRAY); // Gray
                            // Font gạch ngang cho "Đã hủy" (tùy chọn)
                            // Map<java.awt.font.TextAttribute, Object> attributes = new HashMap<>(originalFont.getAttributes());
                            // attributes.put(java.awt.font.TextAttribute.STRIKETHROUGH, java.awt.font.TextAttribute.STRIKETHROUGH_ON);
                            // c.setFont(originalFont.deriveFont(attributes));
                        } else {
                             // Giữ màu chữ đã set bởi isSelected hoặc mặc định nếu không khớp trạng thái nào
                        }
                    }
                    return c;
                }
            });
        }
        // >>>>>>>>>>>>>>>>>> KẾT THÚC CODE TÔ MÀU TRẠNG THÁI <<<<<<<<<<<<<<<<<<
    }


    private void buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> {
            // Tạo lại ExportManager với tableModel hiện tại thay vì gọi setTableModel
            exportManager = new ExportManager(HoaDonUI.this, tableModel, HoaDonUI.this);
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
                // tableHoaDon.setRowSorter(null); // Sorter đã được gán lại trong filterTable hoặc loadTableData
                loadTableData(); // Tải lại toàn bộ dữ liệu và reset sorter
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
            public void mouseReleased(MouseEvent e) { // Nên dùng mouseClicked cho double-click và popup
                int row = tableHoaDon.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tableHoaDon.getRowCount()) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        tableHoaDon.setRowSelectionInterval(row, row); // Chọn hàng khi nhấp chuột phải
                        showPopupMenu(e);
                    } else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                        tableHoaDon.setRowSelectionInterval(row, row);
                        xemChiTietHoaDon();
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                         tableHoaDon.setRowSelectionInterval(row, row); // Chọn hàng khi nhấp chuột trái
                    }
                } else {
                    if (!e.isPopupTrigger()){ // Để tránh clear selection khi popup đang hiển thị
                        tableHoaDon.clearSelection();
                    }
                }
            }
            // mousePressed có thể không cần thiết nếu mouseReleased xử lý tốt popup trigger trên các OS
            // @Override
            // public void mousePressed(MouseEvent e) {
            //     if (e.isPopupTrigger()) { // isPopupTrigger hoạt động khác nhau trên các OS
            //         int row = tableHoaDon.rowAtPoint(e.getPoint());
            //         if (row >= 0 && row < tableHoaDon.getRowCount()) {
            //            tableHoaDon.setRowSelectionInterval(row, row);
            //         }
            //         showPopupMenu(e);
            //     }
            // }
        });
    }

    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));

        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemSua = createStyledMenuItem("Chỉnh Sửa");
        menuItemXoa = createStyledMenuItem("Xóa");

        menuItemXoa.setForeground(accentColor); // Đặt màu cho mục Xóa

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
                    // e1.printStackTrace(); // Nên ghi log thay vì printStackTrace
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
        // int row = tableHoaDon.rowAtPoint(e.getPoint()); // Đã lấy row ở mouseReleased
        // if (row >= 0) { // Đảm bảo nhấp chuột phải vào một hàng hợp lệ
            // tableHoaDon.setRowSelectionInterval(row, row); // Chọn hàng đó
            if (tableHoaDon.getSelectedRow() >= 0) { // Kiểm tra lại hàng đã được chọn chưa
                 popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        // }
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
                // Vẽ nền với màu hiện tại của button (thay đổi khi hover)
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() -1 , getHeight() -1 , radius, radius);
                g2.dispose();
                // Vẽ text và icon (nếu có) lên trên
                super.paintComponent(g);
            }
            @Override
            public boolean isOpaque() { return false; } // Cho phép vẽ nền tùy chỉnh
        };

        button.setFont(buttonFont);
        button.setBackground(bgColor); // Màu nền ban đầu
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false); // Quan trọng để paintComponent tùy chỉnh hoạt động đúng
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Padding cho text

        // Hiệu ứng hover
        button.addMouseListener(new MouseAdapter() {
            private Color originalBgColor = bgColor; // Lưu màu gốc

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
        menuItem.setBackground(Color.WHITE); // Màu nền mặc định
        menuItem.setForeground(textColor);
        menuItem.setOpaque(true); // Cần thiết để setBackground có hiệu lực
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(tableStripeColor); // Màu khi hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE); // Trở lại màu mặc định
            }
        });
        return menuItem;
    }

    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        // Giảm độ sáng (brightness) đi một chút, nhưng không quá 0
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }

    public void loadTableData() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
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
                hd.getTrangThai() // Đây là giá trị String từ controller
            });
            totalAmount += hd.getTongTien();
        }
        // Gán lại sorter cho tableModel mỗi khi load data để đảm bảo filter hoạt động đúng
        // nếu tableModel được tạo mới hoặc có thay đổi cấu trúc (dù ở đây không có)
        sorter = new TableRowSorter<>(tableModel);
        tableHoaDon.setRowSorter(sorter);
        updateTotalRow();
    }

    private void updateTotalRow() {
        if (modelTotalRow.getRowCount() > 0) { // Chỉ cập nhật nếu hàng tổng đã tồn tại
            modelTotalRow.setValueAt("Tổng:", 0, 3);
            modelTotalRow.setValueAt(totalAmount, 0, 4);
        } else { // Nếu chưa có thì thêm mới (chỉ xảy ra lần đầu)
             modelTotalRow.addRow(new Object[]{null, null, null, "Tổng:", totalAmount, null});
        }
        // tableTotalRow.repaint(); // Không cần thiết, setValueAt tự repaint
    }

    private void filterTable() {
        String searchText = txtTimKiem.getText().trim(); // Không cần toLowerCase ở đây nữa
        
        if (searchText.isEmpty()) {
            // tableHoaDon.setRowSorter(null); // Không cần, loadTableData sẽ gán sorter mới
            loadTableData(); // Tải lại toàn bộ dữ liệu
            return;
        }
        
        // Tạo sorter mới mỗi lần filter để đảm bảo nó áp dụng đúng trên model hiện tại
        // sorter = new TableRowSorter<>(tableModel); // Đã được gán lại trong loadTableData nếu searchText rỗng
        // tableHoaDon.setRowSorter(sorter); // Hoặc có thể gán lại ở đây nếu không muốn loadTableData khi filter

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String normalizedSearch = normalizeVietnameseString(searchText.toLowerCase());
                for (int i = 0; i < entry.getValueCount(); i++) {
                    if (entry.getValue(i) != null) {
                        String valueStr;
                        if (entry.getValue(i) instanceof Double) {
                            // So sánh cả dạng số và dạng đã format tiền tệ
                            valueStr = String.valueOf(entry.getValue(i));
                             String formattedValue = currencyFormat.format((Double) entry.getValue(i));
                             if (formattedValue.toLowerCase().contains(searchText.toLowerCase())) return true;

                        } else if (entry.getValue(i) instanceof Date) { // Cột Ngày Tạo đã được format thành String trong model
                            valueStr = entry.getValue(i).toString(); // Nên là giá trị String đã format
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
        sorter.setRowFilter(rf); // Áp dụng filter
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
        for (int i = 0; i < tableHoaDon.getRowCount(); i++) { // Lấy từ view (đã filter)
            // Chỉ số cột "Tổng Tiền" trong tableModel là 4
            // Giá trị ở đây là Object, cần ép kiểu sang Double
            Object amountObj = tableHoaDon.getValueAt(i, 4); // Lấy giá trị từ cột Tổng Tiền trên view
            if(amountObj instanceof Double) {
                filteredTotal += (Double) amountObj;
            }
        }
        if (modelTotalRow.getRowCount() > 0) {
            modelTotalRow.setValueAt(filteredTotal, 0, 4); // Cập nhật giá trị tại cột tổng tiền (index 4)
        }
        // tableTotalRow.repaint(); // Không cần
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
                SwingUtilities.getWindowAncestor(this), // Parent component
                "Bạn có chắc chắn muốn xóa hóa đơn ID: " + idHoaDon + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = hoaDonController.xoaHoaDon(idHoaDon); // Giả sử controller trả về boolean
                if (success) {
                    loadTableData(); // Tải lại dữ liệu sau khi xóa
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
        dialog.setSize(500, 400); // Giảm chiều cao nếu không có chi tiết hóa đơn
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabelDialog = new JLabel("THÊM HÓA ĐƠN MỚI");
        titleLabelDialog.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Font nhỏ hơn cho dialog
        titleLabelDialog.setForeground(Color.WHITE);
        headerPanel.add(titleLabelDialog, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        // Tên Bệnh Nhân
        List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
        DefaultComboBoxModel<String> benhNhanComboBoxModel = new DefaultComboBoxModel<>();
        benhNhanComboBoxModel.addElement("Chọn bệnh nhân...");
        for (BenhNhan bn : danhSachBenhNhan) {
            benhNhanComboBoxModel.addElement(bn.getHoTen() + " (ID: " + bn.getIdBenhNhan() + ")");
        }
        JComboBox<String> cmbTenBenhNhan = new JComboBox<>(benhNhanComboBoxModel);
        styleComboBox(cmbTenBenhNhan);

        // Ngày Tạo (Sử dụng JDateChooser nếu có thư viện, nếu không thì dùng JTextField như cũ)
        JTextField txtNgayTao = new JTextField(dateFormatter.format(new Date())); // Hiển thị dd/MM/yyyy
        txtNgayTao.setToolTipText("Định dạng: dd/MM/yyyy");
        styleTextField(txtNgayTao);

        // Tổng Tiền
        JTextField txtTongTien = new JTextField();
        styleTextField(txtTongTien);
        txtTongTien.setHorizontalAlignment(JTextField.RIGHT); // Căn phải cho số tiền

        // Trạng Thái
        // Nếu đã chuyển sang dùng Enum TrangThaiHoaDon:
        // JComboBox<TrangThaiHoaDon> cmbTrangThai = new JComboBox<>(TrangThaiHoaDon.values());
        // Nếu vẫn dùng String cho UI:
        String[] trangThaiOptions = {"Chưa thanh toán", "Đã thanh toán", "Đang xử lý", "Đã hủy"};
        JComboBox<String> cmbTrangThai = new JComboBox<>(trangThaiOptions);
        styleComboBox(cmbTrangThai);


        addFormField(formPanel, "Bệnh Nhân:", cmbTenBenhNhan);
        addFormField(formPanel, "Ngày Tạo:", txtNgayTao);
        addFormField(formPanel, "Tổng Tiền (VND):", txtTongTien);
        addFormField(formPanel, "Trạng Thái:", cmbTrangThai);
        formPanel.add(Box.createVerticalStrut(15)); // Thêm khoảng trống


        JPanel buttonPanelDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanelDialog.setBackground(Color.WHITE); // Đồng bộ màu nền
        buttonPanelDialog.setBorder(new EmptyBorder(10, 0, 0, 0));


        JButton cancelButton = createRoundedButton("Hủy", new Color(108, 117, 125), buttonTextColor, 10);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton submitButton = createRoundedButton("Lưu", successColor, buttonTextColor, 10);
        submitButton.addActionListener(e -> {
            try {
                String selectedBenhNhanStr = (String) cmbTenBenhNhan.getSelectedItem();
                if (selectedBenhNhanStr == null || selectedBenhNhanStr.equals("Chọn bệnh nhân...")) {
                    showNotification("Vui lòng chọn bệnh nhân.", NotificationType.WARNING);
                    return;
                }
                // Trích xuất ID bệnh nhân từ chuỗi đã chọn
                int idBenhNhan = -1;
                Pattern patternId = Pattern.compile("\\(ID: (\\d+)\\)");
                Matcher matcherId = patternId.matcher(selectedBenhNhanStr);
                if (matcherId.find()) {
                    idBenhNhan = Integer.parseInt(matcherId.group(1));
                }
                if (idBenhNhan == -1) {
                     showNotification("Không thể xác định ID bệnh nhân.", NotificationType.ERROR);
                    return;
                }


                Date ngayTao;
                try {
                    // Parse ngày theo định dạng dd/MM/yyyy
                    ngayTao = dateFormatter.parse(txtNgayTao.getText().trim());
                } catch (Exception ex) {
                    showNotification("Ngày tạo không hợp lệ. Định dạng: dd/MM/yyyy.", NotificationType.WARNING);
                    return;
                }

                if (txtTongTien.getText().trim().isEmpty()) {
                    showNotification("Vui lòng nhập tổng tiền.", NotificationType.WARNING);
                    return;
                }
                double tongTien;
                try {
                     // Cho phép người dùng nhập số có dấu phẩy kiểu Việt Nam
                    Number parsedNumber = currencyFormat.parse(txtTongTien.getText().trim());
                    tongTien = parsedNumber.doubleValue();
                    if (tongTien < 0) {
                         showNotification("Tổng tiền không thể âm.", NotificationType.WARNING);
                        return;
                    }
                } catch (java.text.ParseException ex) {
                    showNotification("Tổng tiền không hợp lệ.", NotificationType.WARNING);
                    return;
                }


                String trangThaiUI = (String) cmbTrangThai.getSelectedItem();
                String trangThaiController; // Giá trị này sẽ được lưu vào DB
                // Ánh xạ từ giá trị hiển thị trên UI sang giá trị mà controller/DB hiểu
                switch (trangThaiUI) {
                    case "Đã thanh toán":
                        trangThaiController = "DaThanhToan";
                        break;
                    case "Chưa thanh toán":
                        trangThaiController = "ChuaThanhToan";
                        break;
                    case "Đang xử lý":
                        trangThaiController = "DangXuLy";
                        break;
                    case "Đã hủy":
                        trangThaiController = "DaHuy";
                        break;
                    default: // Trường hợp không mong muốn
                        showNotification("Trạng thái không hợp lệ.", NotificationType.ERROR);
                        return;
                }

                HoaDon hoaDon = new HoaDon();
                hoaDon.setIdBenhNhan(idBenhNhan);
                hoaDon.setNgayTao(ngayTao);
                hoaDon.setTongTien(tongTien);
                hoaDon.setTrangThai(trangThaiController); // Sử dụng giá trị đã ánh xạ

                // Giả sử controller.themHoaDon đã xử lý transaction và tạo ThanhToan nếu cần
                boolean success = hoaDonController.themHoaDon(hoaDon);
                if (success) {
                    loadTableData();
                    dialog.dispose();
                    showNotification("Thêm hóa đơn thành công!", NotificationType.SUCCESS);
                } else {
                    showNotification("Thêm hóa đơn thất bại. Vui lòng thử lại.", NotificationType.ERROR);
                }

            } catch (NumberFormatException ex) { // Này có thể không cần nếu parse bằng currencyFormat
                showNotification("Tổng tiền phải là một số hợp lệ.", NotificationType.ERROR);
            } catch (Exception ex) { // Bắt các lỗi khác
                // ex.printStackTrace(); // Ghi log chi tiết hơn
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
        dialog.setSize(480, 420); // Kích thước rộng hơn chút
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
        detailContentPanel.setBorder(new EmptyBorder(20, 25, 20, 25)); // Tăng padding
        detailContentPanel.setBackground(Color.WHITE);

        BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hoaDon.getIdBenhNhan());
        ThanhToanBenhNhan thanhToan = hoaDonController.layThanhToanTheoIdHoaDon(hoaDon.getIdHoaDon());

        addDetailField(detailContentPanel, "ID Hóa Đơn:", String.valueOf(hoaDon.getIdHoaDon()));
        addDetailField(detailContentPanel, "Tên Bệnh Nhân:", benhNhan != null ? benhNhan.getHoTen() : "Không rõ");
        addDetailField(detailContentPanel, "ID Bệnh Nhân:", String.valueOf(hoaDon.getIdBenhNhan()));
        addDetailField(detailContentPanel, "Ngày Tạo:", dateFormatter.format(hoaDon.getNgayTao())); // Dùng dateFormatter
        addDetailField(detailContentPanel, "Tổng Tiền:", currencyFormat.format(hoaDon.getTongTien()) + " VND");
        
        // Hiển thị trạng thái thân thiện hơn (nếu bạn dùng Enum thì hoaDon.getTrangThai().getDisplayName())
        String trangThaiDisplay;
        switch (hoaDon.getTrangThai()) {
            case "DaThanhToan": trangThaiDisplay = "Đã thanh toán"; break;
            case "ChuaThanhToan": trangThaiDisplay = "Chưa thanh toán"; break;
            case "DangXuLy": trangThaiDisplay = "Đang xử lý"; break;
            case "DaHuy": trangThaiDisplay = "Đã hủy"; break;
            default: trangThaiDisplay = hoaDon.getTrangThai(); // Hiển thị giá trị gốc nếu không khớp
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
        buttonPanelDialog.setBackground(Color.WHITE); // Đồng bộ màu nền
        buttonPanelDialog.setBorder(new EmptyBorder(10, 0, 0, 0)); // Padding trên cho nút

        JButton closeButton = createRoundedButton("Đóng", secondaryColor, textColor, 10);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanelDialog.add(closeButton);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(detailContentPanel), BorderLayout.CENTER); // Cho phép cuộn nếu nhiều chi tiết
        dialog.add(buttonPanelDialog, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }


    private void hienThiPopupSua(HoaDon hoaDon) throws SQLException {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh Sửa Hóa Đơn", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400); // Tương tự form thêm
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

        // ID Bệnh Nhân (hiển thị tên, nhưng lấy ID để cập nhật)
        List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
        DefaultComboBoxModel<String> benhNhanComboBoxModel = new DefaultComboBoxModel<>();
        BenhNhan benhNhanHienTai = benhNhanController.timKiemBenhNhanTheoId(hoaDon.getIdBenhNhan());
        String tenBenhNhanSelected = "Chọn bệnh nhân..."; // Mặc định
        if (benhNhanHienTai != null) {
             tenBenhNhanSelected = benhNhanHienTai.getHoTen() + " (ID: " + benhNhanHienTai.getIdBenhNhan() + ")";
        }
        // Add tất cả bệnh nhân vào, sau đó set selected item
        benhNhanComboBoxModel.addElement("Chọn bệnh nhân...");
        for (BenhNhan bn : danhSachBenhNhan) {
             benhNhanComboBoxModel.addElement(bn.getHoTen() + " (ID: " + bn.getIdBenhNhan() + ")");
        }
        JComboBox<String> cmbTenBenhNhan = new JComboBox<>(benhNhanComboBoxModel);
        cmbTenBenhNhan.setSelectedItem(tenBenhNhanSelected); // Chọn bệnh nhân hiện tại
        styleComboBox(cmbTenBenhNhan);


        // Ngày Tạo (khóa không cho sửa, hoặc cho sửa nếu nghiệp vụ cho phép)
        JTextField txtNgayTao = new JTextField(dateFormatter.format(hoaDon.getNgayTao()));
        // txtNgayTao.setEnabled(false); // Khóa nếu không cho sửa ngày tạo
        // txtNgayTao.setBackground(new Color(240, 240, 240));
        styleTextField(txtNgayTao);

        // Tổng Tiền
        JTextField txtTongTien = new JTextField(currencyFormat.format(hoaDon.getTongTien()));
        styleTextField(txtTongTien);
        txtTongTien.setHorizontalAlignment(JTextField.RIGHT);

        // Trạng Thái
        String[] trangThaiOptions = {"Chưa thanh toán", "Đã thanh toán", "Đang xử lý", "Đã hủy"};
        JComboBox<String> cmbTrangThai = new JComboBox<>(trangThaiOptions);
        // Ánh xạ giá trị từ controller/DB ("DaThanhToan") sang giá trị UI ("Đã thanh toán")
        String currentTrangThaiUI;
        switch (hoaDon.getTrangThai()) {
            case "DaThanhToan": currentTrangThaiUI = "Đã thanh toán"; break;
            case "ChuaThanhToan": currentTrangThaiUI = "Chưa thanh toán"; break;
            case "DangXuLy": currentTrangThaiUI = "Đang xử lý"; break;
            case "DaHuy": currentTrangThaiUI = "Đã hủy"; break;
            default: currentTrangThaiUI = "Chưa thanh toán"; // Mặc định
        }
        cmbTrangThai.setSelectedItem(currentTrangThaiUI);
        styleComboBox(cmbTrangThai);

        addFormField(formPanel, "Bệnh Nhân:", cmbTenBenhNhan);
        addFormField(formPanel, "Ngày Tạo:", txtNgayTao);
        addFormField(formPanel, "Tổng Tiền (VND):", txtTongTien);
        addFormField(formPanel, "Trạng Thái:", cmbTrangThai);
        formPanel.add(Box.createVerticalStrut(15));


        JPanel buttonPanelDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanelDialog.setBackground(Color.WHITE);
        buttonPanelDialog.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton cancelButton = createRoundedButton("Hủy", new Color(108, 117, 125), buttonTextColor, 10);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton submitButton = createRoundedButton("Cập nhật", warningColor, buttonTextColor, 10); // Màu warning cho sửa
        submitButton.addActionListener(e -> {
            try {
                String selectedBenhNhanStr = (String) cmbTenBenhNhan.getSelectedItem();
                 if (selectedBenhNhanStr == null || selectedBenhNhanStr.equals("Chọn bệnh nhân...")) {
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
                hoaDon.setIdBenhNhan(idBenhNhanMoi); // Cập nhật ID bệnh nhân

                Date ngayTaoMoi;
                 try {
                    ngayTaoMoi = dateFormatter.parse(txtNgayTao.getText().trim());
                    hoaDon.setNgayTao(ngayTaoMoi); // Cập nhật ngày tạo
                } catch (Exception ex) {
                    showNotification("Ngày tạo không hợp lệ. Định dạng: dd/MM/yyyy.", NotificationType.WARNING);
                    return;
                }

                double tongTienMoi;
                try {
                    Number parsedNumber = currencyFormat.parse(txtTongTien.getText().trim());
                    tongTienMoi = parsedNumber.doubleValue();
                     if (tongTienMoi < 0) {
                         showNotification("Tổng tiền không thể âm.", NotificationType.WARNING);
                        return;
                    }
                    hoaDon.setTongTien(tongTienMoi); // Cập nhật tổng tiền
                } catch (java.text.ParseException ex) {
                    showNotification("Tổng tiền không hợp lệ.", NotificationType.WARNING);
                    return;
                }


                String trangThaiUIMoi = (String) cmbTrangThai.getSelectedItem();
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
                hoaDon.setTrangThai(trangThaiControllerMoi); // Cập nhật trạng thái

                boolean success = hoaDonController.capNhatHoaDon(hoaDon);
                if (success) {
                    loadTableData();
                    dialog.dispose();
                    showNotification("Cập nhật hóa đơn ID: " + hoaDon.getIdHoaDon() + " thành công!", NotificationType.SUCCESS);
                } else {
                    showNotification("Cập nhật hóa đơn thất bại.", NotificationType.ERROR);
                }

            } catch (NumberFormatException ex) {
                showNotification("Tổng tiền phải là một số hợp lệ.", NotificationType.ERROR);
            } catch (Exception ex) {
                // ex.printStackTrace();
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
        fieldPanel.setBackground(Color.WHITE); // Match formPanel background
        fieldPanel.setBorder(new EmptyBorder(8, 0, 8, 0)); // Tăng padding dọc

        JLabel label = new JLabel(labelText);
        label.setFont(regularFont);
        label.setPreferredSize(new Dimension(120, 30)); // Giảm chiều rộng label một chút
        fieldPanel.add(label, BorderLayout.WEST);

        // Để field không bị kéo dài quá mức
        JPanel fieldWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        fieldWrapper.setBackground(Color.WHITE);
        fieldWrapper.add(field);
        fieldPanel.add(fieldWrapper, BorderLayout.CENTER);

        panel.add(fieldPanel);
    }

    private void addDetailField(JPanel panel, String labelText, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0)); // Giảm khoảng cách ngang
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(new EmptyBorder(6, 0, 6, 0)); // Giảm padding dọc

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(140, 25)); // Điều chỉnh kích thước label
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
        textField.setPreferredSize(new Dimension(250, 38)); // Tăng chiều rộng một chút
        textField.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(8, borderColor.brighter()), // Bo tròn nhẹ hơn, màu sáng hơn
                BorderFactory.createEmptyBorder(5, 10, 5, 10))); // Giảm padding ngang
        textField.setBackground(new Color(250, 250, 255)); // Màu nền hơi khác cho textfield
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(regularFont);
        comboBox.setPreferredSize(new Dimension(250, 38));
        comboBox.setBackground(Color.WHITE);
        // Custom renderer để thêm padding và style cho JComboBox
         comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (index == -1 && "Chọn bệnh nhân...".equals(value.toString())) { // Placeholder text
                    label.setForeground(Color.GRAY);
                }
                return label;
            }
        });
        // Phần border của JComboBox hơi khó style trực tiếp, CustomBorder có thể không áp dụng đẹp
        // Giữ border mặc định hoặc dùng một panel bao quanh nếu muốn custom border mạnh hơn
         comboBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor.brighter(), 1, true), // Bo tròn nhẹ với LineBorder
                BorderFactory.createEmptyBorder(0, 5, 0, 0)) // Padding bên trong cho mũi tên
        );
    }


    private void showNotification(String message, NotificationType type) {
        JDialog toastDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this)); // Gán parent frame
        toastDialog.setUndecorated(true);
        toastDialog.setAlwaysOnTop(true);
        toastDialog.setFocusableWindowState(false); // Không làm mất focus của cửa sổ chính


        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Quan trọng để vẽ các conponent con
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Vẽ nền bo tròn
                g2d.setColor(getBackground()); // Màu nền của toastPanel
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Bo tròn
                g2d.dispose();
            }
             @Override
            public boolean isOpaque() {
                return false; // Cho phép vẽ nền tùy chỉnh
            }
        };
        toastPanel.setBackground(type.color); // Màu nền chính của toast
        toastPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12)); // Căn giữa, tăng padding
        toastPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); // Bỏ border ngoài cùng nếu có

        // Icon (tùy chọn)
        // JLabel iconLabel = new JLabel(type.icon); // Cần định nghĩa icon cho NotificationType
        // toastPanel.add(iconLabel);

        // JLabel titleLabel = new JLabel(type.title);
        // titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        // titleLabel.setForeground(Color.WHITE);
        // toastPanel.add(titleLabel);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Tăng kích thước font
        messageLabel.setForeground(Color.WHITE);
        toastPanel.add(messageLabel);

        toastDialog.add(toastPanel);
        toastDialog.pack(); // Tính kích thước dựa trên component con

        // Định vị ở góc dưới bên phải màn hình chính
        GraphicsConfiguration gc = getGraphicsConfiguration();
        Rectangle screenBounds = gc.getBounds();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

        int x = screenBounds.x + screenBounds.width - toastDialog.getWidth() - screenInsets.right - 15;
        int y = screenBounds.y + screenBounds.height - toastDialog.getHeight() - screenInsets.bottom - 15;
        toastDialog.setLocation(x,y);


        // Animation mờ dần (tùy chọn)
        Timer fadeInTimer = new Timer(20, null);
        final float[] opacity = {0f};
        fadeInTimer.addActionListener(ae -> {
            opacity[0] += 0.05f;
            if (opacity[0] >= 1f) {
                opacity[0] = 1f;
                fadeInTimer.stop();
                // Bắt đầu timer cho việc tự động đóng
                 Timer autoCloseTimer = new Timer(2500, eClose -> toastDialog.dispose()); // Giảm thời gian hiển thị
                 autoCloseTimer.setRepeats(false);
                 autoCloseTimer.start();
            }
            toastDialog.setOpacity(opacity[0]);
        });

        toastDialog.setOpacity(0f); // Bắt đầu với trong suốt
        toastDialog.setVisible(true);
        fadeInTimer.start();
    }

    // Enum NotificationType nên được định nghĩa lại ở đây nếu DoanhThuUI không được import
    private enum NotificationType {
        SUCCESS(new Color(0, 153, 51, 230), "Thành công"), // Thêm alpha
        WARNING(new Color(255, 153, 0, 230), "Cảnh báo"),
        ERROR(new Color(204, 0, 0, 230), "Lỗi");

        private final Color color;
        private final String title; // Có thể không cần title nếu chỉ hiển thị message

        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }


    private class CustomBorder extends LineBorder {
        private int radius;

        public CustomBorder(int radius, Color color) {
            super(color, 1); // Độ dày border là 1
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Lấy màu từ LineBorder
            g2d.setColor(getLineColor());
            // Vẽ hình chữ nhật bo tròn
            // width-1 và height-1 để border nằm trong bounds của component
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
         @Override
        public Insets getBorderInsets(Component c) {
            // Trả về insets phù hợp để nội dung không bị đè lên border
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
        // Giảm shadowSize và shadowOpacity để bóng mờ nhẹ hơn
        private int shadowSize = 3; 
        private int shadowOpacity = 30; // Tăng nhẹ opacity cho bóng rõ hơn chút

        public RoundedPanel(int radius, boolean hasShadow) {
            super();
            this.cornerRadius = radius;
            this.hasShadow = hasShadow;
            setOpaque(false); // Quan trọng để vẽ nền tùy chỉnh
            if (hasShadow) {
                // EmptyBorder để tạo không gian cho bóng đổ
                setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Không gọi super.paintComponent(g) nếu bạn muốn vẽ lại toàn bộ
            // hoặc gọi nó đầu tiên nếu bạn muốn vẽ đè lên.
            // Ở đây chúng ta tự vẽ hoàn toàn.
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            Shape clip = null;
            if (hasShadow) { // Vẽ bóng trước
                // Hình dạng của bóng (lớn hơn một chút và dịch chuyển)
                 for (int i = 0; i < shadowSize; i++) {
                    float alpha = (float)shadowOpacity * (1.0f - (float)i / shadowSize) / 255.0f;
                    if (alpha < 0) alpha = 0;
                    if (alpha > 1) alpha = 1;
                    g2.setColor(new Color(0, 0, 0, (int)(alpha * 20))); // Bóng đen mờ hơn
                    // Bóng đổ đều các phía
                    g2.fillRoundRect(i, i, panelWidth - i * 2, panelHeight - i * 2, cornerRadius, cornerRadius);
                }
            }

            // Vẽ nền của panel
            g2.setColor(getBackground()); // Lấy màu nền đã set cho panel
            // Vẽ hình chữ nhật bo tròn cho nội dung panel, có trừ đi phần shadow
            g2.fillRoundRect(shadowSize, shadowSize, 
                             panelWidth - 2 * shadowSize, panelHeight - 2 * shadowSize, 
                             cornerRadius, cornerRadius);

            // (Tùy chọn) Vẽ đường viền cho panel nếu muốn
            // g2.setColor(borderColor); // Hoặc một màu viền khác
            // g2.drawRoundRect(shadowSize, shadowSize, 
            //                  panelWidth - 2 * shadowSize -1 , panelHeight - 2 * shadowSize -1, 
            //                  cornerRadius, cornerRadius);
            
            g2.dispose();
            // Sau khi vẽ nền tùy chỉnh, gọi super.paintComponent(g) để vẽ các component con
             super.paintComponent(g);
        }
    }


    // Implement MessageCallback methods
    @Override
    public void showSuccessToast(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }

    @Override
    public void showErrorMessage(String title, String message) {
        // Có thể tùy chỉnh lại JOptionPane nếu muốn đồng bộ hơn
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMessage(String message, String title, int messageType) {
        // Có thể dùng JOptionPane chung hoặc tùy biến
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}