package view;

import controller.KhoVatTuController;
import model.KhoVatTu;
import model.NhaCungCap;
import util.ExportManager;
// import view.DoanhThuUI.NotificationType; // Sử dụng NotificationType nội bộ

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList; // Thêm import này
import java.util.List;
import java.util.regex.Pattern;

public class KhoVatTuUI extends JPanel implements ActionListener, ExportManager.MessageCallback {

    private KhoVatTuController controller;
    private JTable tblKhoVatTu;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private JButton btnTimKiem, btnThemMoi, btnXuatFile;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet, menuItemChinhSua, menuItemXoa;
    private JDialog themSuaDialog;
    private JTextField txtTenVatTuDialog, txtSoLuongDialog, txtDonViTinhDialog;
    private JComboBox<NhaCungCap> cmbNhaCungCapDialog;
    private JComboBox<String> cmbPhanLoaiDialog;
    private JButton btnLuuDialog, btnHuyDialog;
    private KhoVatTu vatTuDangChon; // Vật tư đang được chọn trên bảng
    private ExportManager exportManager;
    private JLabel lblTongSoLuongHienThi; // Nhãn để hiển thị tổng số lượng sau khi lọc/sắp xếp

    // Modern Theme Colors
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

    // Font settings
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);

    public KhoVatTuUI() {
        controller = new KhoVatTuController();
        initialize(); // Gọi initialize trước để tableModel được tạo
        // exportManager được khởi tạo sau khi tableModel đã có
        exportManager = new ExportManager(this, tableModel, this);
        loadData();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10)); // Thêm khoảng cách giữa các component
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Tăng padding chung
        setBackground(backgroundColor);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel tableSectionPanel = createTablePanel(); // Đổi tên để bao gồm cả label tổng
        add(tableSectionPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        createInputDialog(); // Tạo dialog thêm/sửa
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0)); // Giảm khoảng cách dọc
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0)); // Padding dưới

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Xóa padding ngang
        titlePanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("QUẢN LÝ KHO VẬT TƯ");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        txtTimKiem = new JTextField(20); // Tăng độ rộng một chút
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(250, 38));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(8, borderColor), // Bo tròn nhẹ hơn
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemVatTu();
                }
            }
        });
        btnTimKiem = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 8);
        btnTimKiem.setFont(buttonFont.deriveFont(Font.PLAIN, 13f)); // Font nhỏ hơn cho nút tìm kiếm
        btnTimKiem.setPreferredSize(new Dimension(110, 38));
        btnTimKiem.addActionListener(this); // Sử dụng actionPerformed chung

        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() { // Đổi tên thành createTableSectionPanel
        JPanel tableSectionPanel = new JPanel(new BorderLayout(0,10)); // Panel chính cho bảng và label tổng
        tableSectionPanel.setBackground(backgroundColor);

        RoundedPanel tableWrapperPanel = new RoundedPanel(15, true); // Panel bo tròn chứa bảng
        tableWrapperPanel.setLayout(new BorderLayout());
        tableWrapperPanel.setBackground(panelColor);
        tableWrapperPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
             @Override
            public Class<?> getColumnClass(int columnIndex) { // Giúp sắp xếp số đúng cách
                if (columnIndex == 0 || columnIndex == 3) { // ID, Số Lượng
                    return Integer.class;
                }
                return String.class;
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Mã NCC");
        tableModel.addColumn("Tên Vật Tư");
        tableModel.addColumn("Số Lượng");
        tableModel.addColumn("ĐV Tính"); // Viết tắt
        tableModel.addColumn("Nhà Cung Cấp");
        tableModel.addColumn("Phân Loại");

        tblKhoVatTu = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int viewRow, int viewColumn) {
                Component comp = super.prepareRenderer(renderer, viewRow, viewColumn);
                
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                     ((JLabel) comp).setBorder(new EmptyBorder(0,5,0,5)); // Padding cho cell
                }

                if (isRowSelected(viewRow)) {
                    comp.setBackground(getSelectionBackground());
                    comp.setForeground(getSelectionForeground());
                } else {
                    comp.setBackground(viewRow % 2 == 0 ? Color.WHITE : tableStripeColor);
                    comp.setForeground(textColor);
                }
                comp.setFont(tableFont);

                try {
                    int modelRow = convertRowIndexToModel(viewRow);
                    Object idObj = tableModel.getValueAt(modelRow, 0);

                    if (idObj != null && !idObj.toString().isEmpty()) { // Bỏ qua hàng tổng
                        int quantityColumnIndex = 3;
                        Object quantityObj = tableModel.getValueAt(modelRow, quantityColumnIndex);
                        if (quantityObj != null) {
                            int soLuong = Integer.parseInt(quantityObj.toString());
                            if (soLuong < 500) {
                                comp.setForeground(accentColor);
                                comp.setFont(tableFont.deriveFont(Font.BOLD));
                            }
                        }
                    } else { // Style cho hàng tổng (nếu có và nằm trong model)
                         if (comp instanceof JLabel && viewColumn == 2) { // Cột "Tên Vật Tư" của hàng tổng
                             ((JLabel) comp).setHorizontalAlignment(JLabel.RIGHT);
                             comp.setFont(tableFont.deriveFont(Font.BOLD));
                         } else if (comp instanceof JLabel && viewColumn == 3) { // Cột "Số Lượng" của hàng tổng
                             comp.setFont(tableFont.deriveFont(Font.BOLD));
                         }
                    }
                } catch (Exception e) { /* Bỏ qua lỗi parse */ }
                
                return comp;
            }
        };
        
        tblKhoVatTu.setRowSorter(new TableRowSorter<>(tableModel)); // Kích hoạt sắp xếp
        
        tblKhoVatTu.setFont(tableFont);
        tblKhoVatTu.setRowHeight(38); // Giảm chiều cao hàng một chút
        tblKhoVatTu.setShowGrid(false);
        tblKhoVatTu.setIntercellSpacing(new Dimension(0, 0));
        tblKhoVatTu.setSelectionBackground(new Color(184, 207, 229)); // Màu chọn nhạt hơn
        tblKhoVatTu.setSelectionForeground(textColor);
        tblKhoVatTu.setFocusable(true); // Cho phép focus để dùng phím mũi tên
        // tblKhoVatTu.setAutoCreateRowSorter(true); // Đã set ở trên
        tblKhoVatTu.setBorder(null);

        JTableHeader header = tblKhoVatTu.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Giảm chiều cao header
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor)); // Viền dưới nhẹ nhàng
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = tblKhoVatTu.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);  // ID
        columnModel.getColumn(1).setPreferredWidth(70);  // Mã NCC
        columnModel.getColumn(2).setPreferredWidth(220); // Tên Vật Tư
        columnModel.getColumn(3).setPreferredWidth(70);  // Số Lượng
        columnModel.getColumn(4).setPreferredWidth(80);  // Đơn Vị Tính
        columnModel.getColumn(5).setPreferredWidth(180); // Nhà Cung Cấp
        columnModel.getColumn(6).setPreferredWidth(100); // Phân Loại

        setupPopupMenu(); // Tạo popup menu
        tblKhoVatTu.addMouseListener(new MouseAdapter() { // Listener cho popup và double click
             @Override
            public void mousePressed(MouseEvent e) {
                showPopupOrDetails(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupOrDetails(e);
            }
        });


        JScrollPane scrollPane = new JScrollPane(tblKhoVatTu);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor)); // Viền cho scroll pane
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tableWrapperPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Label hiển thị tổng số lượng (thay cho hàng tổng trong bảng)
        lblTongSoLuongHienThi = new JLabel("Tổng số lượng hiển thị: 0");
        lblTongSoLuongHienThi.setFont(regularFont.deriveFont(Font.ITALIC));
        lblTongSoLuongHienThi.setBorder(new EmptyBorder(5, 5, 0, 0));
        lblTongSoLuongHienThi.setForeground(textColor);

        tableSectionPanel.add(tableWrapperPanel, BorderLayout.CENTER);
        tableSectionPanel.add(lblTongSoLuongHienThi, BorderLayout.SOUTH);


        return tableSectionPanel;
    }
    
    private void showPopupOrDetails(MouseEvent e) {
        int viewRow = tblKhoVatTu.rowAtPoint(e.getPoint());
        if (viewRow >= 0 && viewRow < tblKhoVatTu.getRowCount()) {
            if (!tblKhoVatTu.isRowSelected(viewRow)) { // Nếu hàng chưa được chọn, chọn nó
                tblKhoVatTu.setRowSelectionInterval(viewRow, viewRow);
            }
            // Lấy modelRow sau khi đã đảm bảo hàng được chọn
            int modelRow = tblKhoVatTu.convertRowIndexToModel(viewRow); 
            Object idValue = tableModel.getValueAt(modelRow, 0);

            if (idValue != null && !idValue.toString().isEmpty()) { // Là hàng dữ liệu
                try {
                    vatTuDangChon = createKhoVatTuFromRow(modelRow);
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        xemChiTiet();
                    }
                } catch (NumberFormatException ex) {
                    vatTuDangChon = null; // Lỗi parse -> không có vật tư nào được chọn hợp lệ
                }
            } else { // Hàng tổng hoặc hàng không hợp lệ
                vatTuDangChon = null;
                tblKhoVatTu.clearSelection(); // Bỏ chọn nếu nhấp vào hàng không hợp lệ
            }
        } else { // Nhấp ra ngoài các hàng
             if (!e.isPopupTrigger()) { // Không bỏ chọn nếu đang cố mở popup
                tblKhoVatTu.clearSelection();
                vatTuDangChon = null;
            }
        }
    }


    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5)); // Tăng padding dọc
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // Padding trên

        btnXuatFile = createRoundedButton("Xuất Excel", warningColor, buttonTextColor, 8);
        btnXuatFile.setFont(buttonFont);
        btnXuatFile.setPreferredSize(new Dimension(120, 40));
        btnXuatFile.addActionListener(this);

        btnThemMoi = createRoundedButton("Thêm Mới", successColor, buttonTextColor, 8);
        btnThemMoi.setFont(buttonFont);
        btnThemMoi.setPreferredSize(new Dimension(120, 40));
        btnThemMoi.addActionListener(this);

        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThemMoi);
        return buttonPanel;
    }

    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor.darker(), 1)); // Viền đậm hơn cho popup

        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết Vật Tư");
        menuItemChinhSua = createStyledMenuItem("Chỉnh Sửa Vật Tư");
        menuItemXoa = createStyledMenuItem("Xóa Vật Tư Này");
        menuItemXoa.setForeground(accentColor); // Màu đỏ cho xóa

        popupMenu.add(menuItemXemChiTiet);
        popupMenu.add(menuItemChinhSua);
        popupMenu.addSeparator();
        popupMenu.add(menuItemXoa);

        menuItemXemChiTiet.addActionListener(this);
        menuItemChinhSua.addActionListener(this);
        menuItemXoa.addActionListener(this);
    }
    
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont.deriveFont(13f)); // Font nhỏ hơn chút cho menu item
        menuItem.setBackground(Color.WHITE); // Nền trắng
        menuItem.setForeground(textColor);
        menuItem.setOpaque(true);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // Padding
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng hover
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(tableStripeColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE);
            }
        });
        return menuItem;
    }

    // createKhoVatTuFromRow, setupEventListeners, showPopupMenu, showNotification giữ nguyên như code bạn cung cấp
    // hoặc điều chỉnh lại createKhoVatTuFromRow nếu có thay đổi model
     private KhoVatTu createKhoVatTuFromRow(int modelRow) {
        // Kiểm tra modelRow hợp lệ
        if (modelRow < 0 || modelRow >= tableModel.getRowCount()) {
            return null;
        }
        try {
            int idVatTu = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
            // Mã NCC có thể null hoặc rỗng nếu không bắt buộc
            Object maNCCObj = tableModel.getValueAt(modelRow, 1);
            String maNCC = (maNCCObj != null) ? maNCCObj.toString() : "";

            String tenVatTu = tableModel.getValueAt(modelRow, 2).toString();
            int soLuong = Integer.parseInt(tableModel.getValueAt(modelRow, 3).toString());
            String donViTinh = tableModel.getValueAt(modelRow, 4).toString();
            // Tên NCC (cột 5) chỉ để hiển thị, không cần lấy cho đối tượng KhoVatTu model
            String phanLoai = tableModel.getValueAt(modelRow, 6).toString();
            
            return new KhoVatTu(idVatTu, tenVatTu, soLuong, donViTinh, maNCC, phanLoai);
        } catch (NumberFormatException e) {
            System.err.println("Lỗi parse số từ bảng tại hàng: " + modelRow + " - " + e.getMessage());
            return null; // Trả về null nếu có lỗi parse
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo KhoVatTu từ hàng: " + modelRow + " - " + e.getMessage());
            return null;
        }
    }


    // Phương thức này không còn cần thiết nữa vì đã tích hợp vào createHeaderPanel và createButtonPanel
    // private void setupEventListeners() { ... }

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
    
    // Định nghĩa lại enum NotificationType (nếu không import từ DoanhThuUI)
    public enum NotificationType {
        SUCCESS(new Color(34, 139, 34, 220), "Thành công"), // Green with alpha
        WARNING(new Color(255, 165, 0, 220), "Cảnh báo"),   // Orange with alpha
        ERROR(new Color(220, 20, 60, 220), "Lỗi");        // Red with alpha
        
        private final Color color;
        private final String title; // Title có thể không cần dùng trong toast này
        
        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }

    private void createInputDialog() {
        themSuaDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm/Sửa Vật Tư", true);
        themSuaDialog.setLayout(new BorderLayout(0,10)); // Khoảng cách giữa content và button panel
        themSuaDialog.setBackground(Color.WHITE); // Nền dialog
        themSuaDialog.getRootPane().setBorder(BorderFactory.createLineBorder(borderColor.darker(), 1));


        // Header cho dialog
        JPanel dialogHeaderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dialogHeaderPanel.setBackground(primaryColor);
        dialogHeaderPanel.setBorder(new EmptyBorder(10,10,10,10));
        JLabel dialogTitleLabel = new JLabel("Thông Tin Vật Tư"); // Title sẽ được set lại khi mở dialog
        dialogTitleLabel.setFont(titleFont.deriveFont(16f));
        dialogTitleLabel.setForeground(Color.WHITE);
        dialogHeaderPanel.add(dialogTitleLabel);
        themSuaDialog.add(dialogHeaderPanel, BorderLayout.NORTH);


        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        contentPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8); // Giảm padding
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Dimension fieldDim = new Dimension(0, 36); // Chiều cao chuẩn cho input fields

        // Tên Vật Tư
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; // Label chiếm ít không gian hơn
        JLabel lblTenVatTu = new JLabel("Tên Vật Tư:");
        lblTenVatTu.setFont(regularFont);
        contentPanel.add(lblTenVatTu, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; // Field chiếm nhiều hơn
        txtTenVatTuDialog = new JTextField(20);
        txtTenVatTuDialog.setFont(regularFont);
        txtTenVatTuDialog.setPreferredSize(fieldDim);
        contentPanel.add(txtTenVatTuDialog, gbc);
        
        // Số Lượng
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblSoLuong = new JLabel("Số Lượng:");
        lblSoLuong.setFont(regularFont);
        contentPanel.add(lblSoLuong, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtSoLuongDialog = new JTextField(10);
        txtSoLuongDialog.setFont(regularFont);
        txtSoLuongDialog.setPreferredSize(fieldDim);
        contentPanel.add(txtSoLuongDialog, gbc);
        
        // Đơn Vị Tính
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblDonViTinh = new JLabel("Đơn Vị Tính:");
        lblDonViTinh.setFont(regularFont);
        contentPanel.add(lblDonViTinh, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDonViTinhDialog = new JTextField(15);
        txtDonViTinhDialog.setFont(regularFont);
        txtDonViTinhDialog.setPreferredSize(fieldDim);
        contentPanel.add(txtDonViTinhDialog, gbc);
        
        // Nhà Cung Cấp
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblNhaCungCap = new JLabel("Nhà Cung Cấp:");
        lblNhaCungCap.setFont(regularFont);
        contentPanel.add(lblNhaCungCap, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbNhaCungCapDialog = new JComboBox<>();
        cmbNhaCungCapDialog.setFont(regularFont);
        cmbNhaCungCapDialog.setPreferredSize(fieldDim);
        cmbNhaCungCapDialog.setBackground(Color.WHITE);
        contentPanel.add(cmbNhaCungCapDialog, gbc);
        
        // Phân Loại
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        JLabel lblPhanLoai = new JLabel("Phân Loại:");
        lblPhanLoai.setFont(regularFont);
        contentPanel.add(lblPhanLoai, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbPhanLoaiDialog = new JComboBox<>();
        cmbPhanLoaiDialog.setFont(regularFont);
        cmbPhanLoaiDialog.setPreferredSize(fieldDim);
        cmbPhanLoaiDialog.setBackground(Color.WHITE);
        contentPanel.add(cmbPhanLoaiDialog, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 10, 5)); // Tăng padding trên
        
        btnHuyDialog = createRoundedButton("Hủy", new Color(108,117,125), buttonTextColor, 8); // Màu xám cho Hủy
        btnHuyDialog.setFont(buttonFont);
        btnHuyDialog.setPreferredSize(new Dimension(100, 38));
        btnHuyDialog.addActionListener(this);
        
        btnLuuDialog = createRoundedButton("Lưu", successColor, buttonTextColor, 8); // Dùng successColor
        btnLuuDialog.setFont(buttonFont);
        btnLuuDialog.setPreferredSize(new Dimension(100, 38));
        btnLuuDialog.addActionListener(this);
        
        buttonPanel.add(btnHuyDialog);
        buttonPanel.add(btnLuuDialog);
        
        themSuaDialog.add(contentPanel, BorderLayout.CENTER);
        themSuaDialog.add(buttonPanel, BorderLayout.SOUTH);
        themSuaDialog.pack(); // Pack trước khi setSize để có preferredSize
        themSuaDialog.setMinimumSize(new Dimension(550, 420)); // Kích thước tối thiểu
        themSuaDialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(this)); // Đảm bảo parent là Frame
        
        // Load data cho JComboBoxes chỉ một lần khi dialog được tạo
        loadNhaCungCapForDialog();
        loadPhanLoaiForDialog();
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius) {
        JButton button = new JButton(text) {
            private Color currentBgColor = bgColor;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(currentBgColor); // Sử dụng màu nền hiện tại (thay đổi khi hover)
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
                super.paintComponent(g); // Vẽ text của button
            }
             @Override
            public void setBackground(Color bg) {
                // Ghi đè để lưu màu nền vào currentBgColor thay vì màu nền mặc định của component
                currentBgColor = bg;
                super.setBackground(bg); // Vẫn gọi super để đảm bảo các hành vi khác nếu có
            }
        };
        
        button.setBackground(bgColor); // Set màu nền ban đầu
        button.setForeground(fgColor);
        button.setFont(buttonFont);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false); // Để paintComponent tùy chỉnh hoạt động
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8,15,8,15)); // Padding cho text

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker()); // Làm tối màu khi hover
                button.repaint(); // Yêu cầu vẽ lại
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor); // Trả lại màu gốc
                button.repaint();
            }
        });
        return button;
    }


    private void loadNhaCungCapForDialog() {
        List<NhaCungCap> nhaCungCaps = controller.getAllNhaCungCap();
        cmbNhaCungCapDialog.removeAllItems(); // Xóa các item cũ
        cmbNhaCungCapDialog.addItem(null); // Thêm một lựa chọn "trống" hoặc "Chọn NCC"
        for (NhaCungCap ncc : nhaCungCaps) {
            cmbNhaCungCapDialog.addItem(ncc);
        }
        cmbNhaCungCapDialog.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NhaCungCap) {
                    setText(((NhaCungCap) value).getTenNCC());
                } else if (value == null && index == -1) { // Hiển thị khi chưa chọn gì
                     setText("Chọn nhà cung cấp...");
                     setForeground(Color.GRAY);
                } else if (value == null) { // Item null trong danh sách
                    setText("--- Chọn NCC ---");
                }
                return this;
            }
        });
        if (cmbNhaCungCapDialog.getItemCount() > 0 && cmbNhaCungCapDialog.getItemAt(0) == null){
            // Để placeholder không bị tính là một lựa chọn hợp lệ khi chưa chọn gì
            // Nếu item đầu là null, thì setSelectedIndex(-1) sẽ không hiển thị gì.
            // Hoặc cmbNhaCungCapDialog.setSelectedItem(null);
        }
    }

    private void loadPhanLoaiForDialog() {
        List<String> phanLoais = controller.getAllPhanLoai(); // Nên lấy từ controller
        cmbPhanLoaiDialog.removeAllItems();
        cmbPhanLoaiDialog.addItem("Chọn phân loại..."); // Placeholder
        for (String phanLoai : phanLoais) {
            cmbPhanLoaiDialog.addItem(phanLoai);
        }
         cmbPhanLoaiDialog.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                 if (value == null || (index == -1 && "Chọn phân loại...".equals(value.toString()))) {
                     setText("Chọn phân loại...");
                     setForeground(Color.GRAY);
                 }
                return this;
            }
        });
        cmbPhanLoaiDialog.setSelectedItem("Chọn phân loại..."); // Đặt placeholder làm lựa chọn mặc định
    }

    private void loadData() {
        List<KhoVatTu> danhSachVatTu = controller.getAllKhoVatTu();
        hienThiDanhSachVatTu(danhSachVatTu);
    }

    private void hienThiDanhSachVatTu(List<KhoVatTu> danhSach) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        if (danhSach == null || danhSach.isEmpty()) {
             lblTongSoLuongHienThi.setText("Tổng số lượng hiển thị: 0");
            return;
        }

        int tongSoLuongTrongBang = 0;
        for (KhoVatTu vatTu : danhSach) {
            String tenNCC = controller.getTenNhaCungCap(vatTu.getMaNCC());
            if(tenNCC == null || tenNCC.trim().isEmpty()) tenNCC = "N/A";

            tableModel.addRow(new Object[]{
                vatTu.getIdVatTu(),
                vatTu.getMaNCC() != null ? vatTu.getMaNCC() : "N/A",
                vatTu.getTenVatTu(),
                vatTu.getSoLuong(),
                vatTu.getDonViTinh(),
                tenNCC,
                vatTu.getPhanLoai()
            });
            tongSoLuongTrongBang += vatTu.getSoLuong();
        }
        // Cập nhật tổng số lượng cho các hàng đang hiển thị (sau khi lọc/sắp xếp hoặc tải mới)
        updateTongSoLuongHienThi();
    }
    
    private void updateTongSoLuongHienThi() {
        int rowCount = tblKhoVatTu.getRowCount(); // Số hàng đang hiển thị trên view (đã qua filter/sort)
        int currentTotalQuantity = 0;
        for (int i = 0; i < rowCount; i++) {
            try {
                // Lấy giá trị từ view, sau đó convert về model row index để lấy giá trị từ model
                int modelRow = tblKhoVatTu.convertRowIndexToModel(i);
                Object idObj = tableModel.getValueAt(modelRow, 0); // Cột ID
                // Chỉ tính tổng cho các hàng dữ liệu thực sự, bỏ qua hàng tổng cũ (nếu có)
                if(idObj != null && !idObj.toString().isEmpty()){
                    Object soLuongObj = tableModel.getValueAt(modelRow, 3); // Cột Số Lượng
                    if (soLuongObj != null) {
                        currentTotalQuantity += Integer.parseInt(soLuongObj.toString());
                    }
                }
            } catch (NumberFormatException e) {
                // Bỏ qua nếu không parse được số (ví dụ hàng tổng cũ)
            }
        }
        lblTongSoLuongHienThi.setText("Tổng số lượng hiển thị: " + currentTotalQuantity);
    }


    private void clearInputFieldsDialog() {
        txtTenVatTuDialog.setText("");
        txtSoLuongDialog.setText("");
        txtDonViTinhDialog.setText("");
        cmbNhaCungCapDialog.setSelectedItem(null); // Hoặc setSelectedIndex(0) nếu item đầu là placeholder
        cmbPhanLoaiDialog.setSelectedItem("Chọn phân loại..."); // Hoặc setSelectedIndex(0)
        vatTuDangChon = null; // Quan trọng: Reset vật tư đang chọn khi mở dialog để thêm mới
    }

    private void timKiemVatTu() {
        String tuKhoa = txtTimKiem.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) tblKhoVatTu.getRowSorter();
        if (sorter == null) { // Đảm bảo sorter đã được khởi tạo
            sorter = new TableRowSorter<>(tableModel);
            tblKhoVatTu.setRowSorter(sorter);
        }

        if (tuKhoa.isEmpty()) {
            sorter.setRowFilter(null); // Xóa filter
            showNotification("Đã làm mới dữ liệu!", NotificationType.SUCCESS);
        } else {
            // (?i) cho case-insensitive search
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(tuKhoa)));
            if (tblKhoVatTu.getRowCount() == 0) {
                showNotification("Không tìm thấy vật tư nào khớp.", NotificationType.WARNING);
            } else {
                showNotification("Tìm thấy " + tblKhoVatTu.getRowCount() + " vật tư.", NotificationType.SUCCESS);
            }
        }
        updateTongSoLuongHienThi(); // Cập nhật tổng sau khi lọc
    }

    private void xemChiTiet() {
        if (vatTuDangChon != null) {
            String maNCC = vatTuDangChon.getMaNCC();
            String tenNCC = controller.getTenNhaCungCap(maNCC);
            String nhaCungCapHienThi = (tenNCC != null && !tenNCC.isEmpty()) ? tenNCC : "Không xác định (Mã: " + (maNCC != null ? maNCC : "N/A") + ")";
            
            JDialog chiTietDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi Tiết Vật Tư (ID: "+vatTuDangChon.getIdVatTu()+")", true);
            chiTietDialog.setLayout(new BorderLayout(0,10));
            chiTietDialog.setBackground(Color.WHITE);
            chiTietDialog.getRootPane().setBorder(BorderFactory.createLineBorder(borderColor.darker()));


            JPanel headerDlgPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            headerDlgPanel.setBackground(primaryColor);
            headerDlgPanel.setBorder(new EmptyBorder(10,10,10,10));
            JLabel titleDlgLabel = new JLabel("Chi Tiết Vật Tư (ID: "+vatTuDangChon.getIdVatTu()+")");
            titleDlgLabel.setFont(titleFont.deriveFont(16f));
            titleDlgLabel.setForeground(Color.WHITE);
            headerDlgPanel.add(titleDlgLabel);
            chiTietDialog.add(headerDlgPanel, BorderLayout.NORTH);


            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
            contentPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(7, 7, 7, 7);
            gbc.anchor = GridBagConstraints.WEST;
            
            Font labelFont = regularFont.deriveFont(Font.BOLD);
            Font valueFont = regularFont;

            // ID Vật Tư (Không cần thiết vì đã có trên title)
            // Tên Vật Tư
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
            JLabel lblTen = new JLabel("Tên Vật Tư:"); lblTen.setFont(labelFont); contentPanel.add(lblTen, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel valTen = new JLabel(vatTuDangChon.getTenVatTu()); valTen.setFont(valueFont); contentPanel.add(valTen, gbc);
            
            // Số lượng
            gbc.gridy++; gbc.gridx = 0;
            JLabel lblSoLuong = new JLabel("Số Lượng Hiện Tại:"); lblSoLuong.setFont(labelFont); contentPanel.add(lblSoLuong, gbc);
            gbc.gridx = 1;
            JLabel valSoLuong = new JLabel(String.valueOf(vatTuDangChon.getSoLuong())); valSoLuong.setFont(valueFont); contentPanel.add(valSoLuong, gbc);
            
            // Đơn vị tính
            gbc.gridy++; gbc.gridx = 0;
            JLabel lblDvt = new JLabel("Đơn Vị Tính:"); lblDvt.setFont(labelFont); contentPanel.add(lblDvt, gbc);
            gbc.gridx = 1;
            JLabel valDvt = new JLabel(vatTuDangChon.getDonViTinh()); valDvt.setFont(valueFont); contentPanel.add(valDvt, gbc);
            
            // Nhà cung cấp
            gbc.gridy++; gbc.gridx = 0;
            JLabel lblNcc = new JLabel("Nhà Cung Cấp:"); lblNcc.setFont(labelFont); contentPanel.add(lblNcc, gbc);
            gbc.gridx = 1;
            JLabel valNcc = new JLabel(nhaCungCapHienThi); valNcc.setFont(valueFont); contentPanel.add(valNcc, gbc);

            // Mã NCC
            gbc.gridy++; gbc.gridx = 0;
            JLabel lblMaNcc = new JLabel("Mã NCC:"); lblMaNcc.setFont(labelFont); contentPanel.add(lblMaNcc, gbc);
            gbc.gridx = 1;
            JLabel valMaNcc = new JLabel(vatTuDangChon.getMaNCC() != null ? vatTuDangChon.getMaNCC() : "N/A"); valMaNcc.setFont(valueFont); contentPanel.add(valMaNcc, gbc);

            // Phân loại
            gbc.gridy++; gbc.gridx = 0;
            JLabel lblPhanLoai = new JLabel("Phân Loại:"); lblPhanLoai.setFont(labelFont); contentPanel.add(lblPhanLoai, gbc);
            gbc.gridx = 1;
            JLabel valPhanLoai = new JLabel(vatTuDangChon.getPhanLoai()); valPhanLoai.setFont(valueFont); contentPanel.add(valPhanLoai, gbc);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); // Thêm padding dưới
            buttonPanel.setBackground(Color.WHITE);
            JButton btnDong = createRoundedButton("Đóng", secondaryColor, textColor, 8);
            btnDong.setFont(buttonFont);
            btnDong.setPreferredSize(new Dimension(100,38));
            btnDong.addActionListener(e -> chiTietDialog.dispose());
            buttonPanel.add(btnDong);
            
            chiTietDialog.add(contentPanel, BorderLayout.CENTER);
            chiTietDialog.add(buttonPanel, BorderLayout.SOUTH);
            chiTietDialog.pack();
            chiTietDialog.setMinimumSize(new Dimension(450, chiTietDialog.getPreferredSize().height)); // Set min width
            chiTietDialog.setLocationRelativeTo((JFrame)SwingUtilities.getWindowAncestor(this));
            chiTietDialog.setVisible(true);
        } else {
            showNotification("Vui lòng chọn một vật tư để xem.", NotificationType.WARNING);
        }
    }

    private void hienThiDialogThemSua(KhoVatTu vatTu) {
        // Load lại data cho combobox mỗi khi mở dialog để đảm bảo dữ liệu mới nhất
        loadNhaCungCapForDialog();
        loadPhanLoaiForDialog();
        clearInputFieldsDialog(); // Xóa và reset vatTuDangChon trước

        vatTuDangChon = vatTu; // Gán lại sau khi clear
        JLabel dialogTitleLabel = (JLabel) ((JPanel)themSuaDialog.getContentPane().getComponent(0)).getComponent(0); // Lấy label title từ header

        if (vatTu != null) { // Chế độ sửa
            dialogTitleLabel.setText("Chỉnh Sửa Vật Tư (ID: " + vatTu.getIdVatTu() + ")");
            txtTenVatTuDialog.setText(vatTu.getTenVatTu());
            txtSoLuongDialog.setText(String.valueOf(vatTu.getSoLuong()));
            txtDonViTinhDialog.setText(vatTu.getDonViTinh());
            
            boolean nccFound = false;
            for (int i = 0; i < cmbNhaCungCapDialog.getItemCount(); i++) {
                NhaCungCap nccItem = cmbNhaCungCapDialog.getItemAt(i);
                if (nccItem != null && nccItem.getMaNCC().equals(vatTu.getMaNCC())) {
                    cmbNhaCungCapDialog.setSelectedIndex(i);
                    nccFound = true;
                    break;
                }
            }
            if (!nccFound) cmbNhaCungCapDialog.setSelectedItem(null); // Nếu không tìm thấy, để trống

            cmbPhanLoaiDialog.setSelectedItem(vatTu.getPhanLoai());
            if (cmbPhanLoaiDialog.getSelectedItem() == null && vatTu.getPhanLoai() != null) {
                // Nếu phân loại không có trong list, có thể thêm vào (hoặc báo lỗi)
                // cmbPhanLoaiDialog.addItem(vatTu.getPhanLoai());
                // cmbPhanLoaiDialog.setSelectedItem(vatTu.getPhanLoai());
                 cmbPhanLoaiDialog.setSelectedItem("Chọn phân loại..."); // Hoặc để trống
            }


        } else { // Chế độ thêm mới
            dialogTitleLabel.setText("Thêm Mới Vật Tư");
            // clearInputFieldsDialog() đã được gọi ở trên
        }
        themSuaDialog.pack(); // Pack lại để điều chỉnh kích thước nếu title thay đổi
        themSuaDialog.setMinimumSize(new Dimension(550, 420));
        themSuaDialog.setLocationRelativeTo((JFrame)SwingUtilities.getWindowAncestor(this));
        themSuaDialog.setVisible(true);
    }

    private void luuVatTu() {
        String tenVatTu = txtTenVatTuDialog.getText().trim();
        String soLuongStr = txtSoLuongDialog.getText().trim();
        String donViTinh = txtDonViTinhDialog.getText().trim();
        Object selectedNCCObj = cmbNhaCungCapDialog.getSelectedItem();
        Object selectedPhanLoaiObj = cmbPhanLoaiDialog.getSelectedItem();

        if (tenVatTu.isEmpty()) {
            showNotification("Tên vật tư không được để trống.", NotificationType.WARNING);
            txtTenVatTuDialog.requestFocus(); return;
        }
        if (soLuongStr.isEmpty()) {
            showNotification("Số lượng không được để trống.", NotificationType.WARNING);
            txtSoLuongDialog.requestFocus(); return;
        }
        if (donViTinh.isEmpty()) {
            showNotification("Đơn vị tính không được để trống.", NotificationType.WARNING);
            txtDonViTinhDialog.requestFocus(); return;
        }
        if (selectedNCCObj == null || !(selectedNCCObj instanceof NhaCungCap)) {
            showNotification("Vui lòng chọn nhà cung cấp hợp lệ.", NotificationType.WARNING);
            cmbNhaCungCapDialog.requestFocus(); return;
        }
        if (selectedPhanLoaiObj == null || selectedPhanLoaiObj.toString().equals("Chọn phân loại...")) {
            showNotification("Vui lòng chọn phân loại.", NotificationType.WARNING);
            cmbPhanLoaiDialog.requestFocus(); return;
        }
        
        NhaCungCap selectedNCC = (NhaCungCap) selectedNCCObj;
        String phanLoai = selectedPhanLoaiObj.toString();

        try {
            int soLuong = Integer.parseInt(soLuongStr);
            if (soLuong < 0) {
                showNotification("Số lượng không được âm.", NotificationType.ERROR);
                txtSoLuongDialog.requestFocus(); return;
            }

            KhoVatTu vatTuMoiHoacCapNhat = new KhoVatTu(
                (vatTuDangChon != null) ? vatTuDangChon.getIdVatTu() : 0, // ID = 0 cho thêm mới
                tenVatTu, soLuong, donViTinh, selectedNCC.getMaNCC(), phanLoai
            );

            boolean success;
            String actionMessage;

            if (vatTuDangChon == null) { // Thêm mới
                success = controller.addKhoVatTu(vatTuMoiHoacCapNhat);
                actionMessage = success ? "Thêm vật tư thành công!" : "Thêm vật tư thất bại.";
            } else { // Chỉnh sửa
                success = controller.updateKhoVatTu(vatTuMoiHoacCapNhat);
                actionMessage = success ? "Cập nhật vật tư thành công!" : "Cập nhật vật tư thất bại.";
            }
            
            if (success) {
                loadData();
                themSuaDialog.setVisible(false);
                showNotification(actionMessage, NotificationType.SUCCESS);
            } else {
                showNotification(actionMessage, NotificationType.ERROR);
            }
        } catch (NumberFormatException ex) {
            showNotification("Số lượng phải là một số nguyên hợp lệ.", NotificationType.ERROR);
            txtSoLuongDialog.requestFocus();
        } catch (Exception ex) { // Bắt các lỗi khác từ controller
            showNotification("Đã xảy ra lỗi: " + ex.getMessage(), NotificationType.ERROR);
        }
    }

    private void xoaVatTu() {
        if (vatTuDangChon == null) {
            showNotification("Vui lòng chọn một vật tư để xóa.", NotificationType.WARNING);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa vật tư '" + vatTuDangChon.getTenVatTu() + "' (ID: " + vatTuDangChon.getIdVatTu() + ")?",
                "Xác Nhận Xóa Vật Tư",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteKhoVatTu(vatTuDangChon.getIdVatTu())) {
                loadData();
                showNotification("Đã xóa vật tư thành công!", NotificationType.SUCCESS);
                vatTuDangChon = null; // Reset sau khi xóa
            } else {
                showNotification("Xóa vật tư thất bại. Có thể vật tư đang được sử dụng.", NotificationType.ERROR);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnThemMoi) {
            hienThiDialogThemSua(null);
        } else if (source == btnLuuDialog) {
            luuVatTu();
        } else if (source == btnHuyDialog) {
            themSuaDialog.setVisible(false);
            // vatTuDangChon không cần reset ở đây vì hienThiDialogThemSua sẽ làm
        } else if (source == menuItemXemChiTiet) {
            if (vatTuDangChon != null) xemChiTiet();
            else showNotification("Vui lòng chọn vật tư từ bảng.", NotificationType.WARNING);
        } else if (source == menuItemChinhSua) {
            if (vatTuDangChon != null) hienThiDialogThemSua(vatTuDangChon);
            else showNotification("Vui lòng chọn vật tư từ bảng.", NotificationType.WARNING);
        } else if (source == menuItemXoa) {
            if (vatTuDangChon != null) xoaVatTu();
            else showNotification("Vui lòng chọn vật tư từ bảng.", NotificationType.WARNING);
        } else if (source == btnTimKiem) {
            timKiemVatTu();
        } else if (source == btnXuatFile) {
            // Tạo lại ExportManager với tableModel hiện tại
            exportManager = new ExportManager(this, tableModel, this);
            exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor);
        }
    }
    
    // --- Inner classes cho UI styling (RoundedPanel, CustomBorder) ---
    // (Giữ nguyên các inner class RoundedPanel và CustomBorder như bạn đã cung cấp)
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private boolean hasShadow;
        private int shadowSize = 5; // Kích thước bóng
        private int shadowOpacity = 25; // Độ mờ của bóng (0-255)

        public RoundedPanel(int radius, boolean shadow) {
            super();
            this.cornerRadius = radius;
            this.hasShadow = shadow;
            setOpaque(false);
            if (shadow) {
                setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelX = shadowSize;
            int panelY = shadowSize;
            int panelWidth = getWidth() - (2 * shadowSize);
            int panelHeight = getHeight() - (2 * shadowSize);

            if (hasShadow) {
                for (int i = 0; i < shadowSize; i++) {
                    float alpha = (float) shadowOpacity * (1.0f - (float) i / shadowSize) / 255.0f;
                    if (alpha < 0) alpha = 0; if (alpha > 1) alpha = 1; // Giữ alpha trong khoảng [0,1]

                    g2.setColor(new Color(0, 0, 0, (int) (alpha * 255 * 0.5f))); // Bóng đen mờ hơn
                    g2.fillRoundRect(panelX - i, panelY - i,
                                     panelWidth + 2 * i, panelHeight + 2 * i,
                                     cornerRadius + i, cornerRadius + i);
                }
            }

            g2.setColor(getBackground());
            g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, cornerRadius, cornerRadius);
            
            // Vẽ border cho panel nếu muốn
            g2.setColor(borderColor); 
            g2.drawRoundRect(panelX, panelY, panelWidth -1 , panelHeight -1, cornerRadius, cornerRadius);

            g2.dispose();
            // Gọi super.paintComponent SAU KHI vẽ nền tùy chỉnh để các component con được vẽ lên trên
            super.paintComponent(g);
        }
    }
    
    class CustomBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color color;
        private int thickness;

        public CustomBorder(int radius, Color color) {
            this(radius, color, 1); // Mặc định độ dày là 1
        }
        public CustomBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.color);
            g2.setStroke(new BasicStroke(this.thickness));
            // Vẽ từ x,y và giảm width, height đi 1 để border nằm gọn trong component
            g2.drawRoundRect(x + thickness/2, y + thickness/2, 
                             width - thickness, height - thickness, 
                             radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            // Padding cho nội dung bên trong border
            return new Insets(thickness + radius/3, thickness + radius/2, thickness + radius/3, thickness + radius/2);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = thickness + radius/2;
            insets.top = insets.bottom = thickness + radius/3;
            return insets;
        }
    }

    // Implement MessageCallback methods
    @Override
    public void showSuccessToast(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }

    @Override
    public void showErrorMessage(String title, String message) {
        showNotification(title != null && !title.isEmpty() ? title + ": " + message : message, NotificationType.ERROR);
    }

    @Override
    public void showMessage(String message, String title, int messageType) {
        NotificationType type = NotificationType.SUCCESS; // Mặc định
        if (messageType == JOptionPane.ERROR_MESSAGE) type = NotificationType.ERROR;
        else if (messageType == JOptionPane.WARNING_MESSAGE) type = NotificationType.WARNING;
        // Bạn có thể thêm các loại khác nếu cần
        
        showNotification(title != null && !title.isEmpty() ? title + ": " + message : message, type);
    }
}