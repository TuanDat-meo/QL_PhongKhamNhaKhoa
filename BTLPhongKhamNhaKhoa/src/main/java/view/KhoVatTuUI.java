package view;

import controller.KhoVatTuController;
import model.KhoVatTu;
import model.NhaCungCap;
import util.ExportManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.Arrays;

public class KhoVatTuUI extends JPanel implements ActionListener, ExportManager.MessageCallback {

    private KhoVatTuController controller;
    private JTable tblKhoVatTu;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private JButton btnTimKiem, btnThemMoi, btnXuatFile;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet, menuItemChinhSua, menuItemXoa;
    private JDialog themSuaDialog;
    // --- Thay đổi: txtDonViTinhDialog thành JComboBox ---
    private JTextField txtTenVatTuDialog, txtSoLuongDialog;
    private JComboBox<String> cmbDonViTinhDialog;
    private JComboBox<NhaCungCap> cmbNhaCungCapDialog;
    private JComboBox<String> cmbPhanLoaiDialog;
    private JButton btnLuuDialog, btnHuyDialog;
    private KhoVatTu vatTuDangChon;
    private ExportManager exportManager;
    private JLabel lblTongSoLuongHienThi;

    // --- Biến cho hiệu ứng highlight ---
    private Timer highlightTimer;
    private int highlightedModelIndex = -1;
    private long highlightStartTime;
    private final int HIGHLIGHT_DURATION = 2000; // 2 giây
    private final Color HIGHLIGHT_COLOR = new Color(255, 255, 150); // Màu vàng nhạt

    private Map<JComponent, JLabel> errorLabelMap;
    private Map<JComponent, JLabel> mainLabelMap;

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
        initialize();
        exportManager = new ExportManager(this, tableModel, this);
        loadData(null);
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(backgroundColor);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        createInputDialog();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
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
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(250, 38));
        styleTextField(txtTimKiem); // Áp dụng style
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemVatTu();
                }
            }
        });
        btnTimKiem = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 8);
        btnTimKiem.setFont(buttonFont.deriveFont(Font.PLAIN, 13f));
        btnTimKiem.setPreferredSize(new Dimension(110, 38));
        btnTimKiem.addActionListener(this);

        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tableSectionPanel = new JPanel(new BorderLayout(0,10));
        tableSectionPanel.setBackground(backgroundColor);

        RoundedPanel tableWrapperPanel = new RoundedPanel(15, true);
        tableWrapperPanel.setLayout(new BorderLayout());
        tableWrapperPanel.setBackground(panelColor);
        tableWrapperPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 3) return Integer.class;
                return String.class;
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Mã NCC");
        tableModel.addColumn("Tên Vật Tư");
        tableModel.addColumn("Số Lượng");
        tableModel.addColumn("ĐV Tính");
        tableModel.addColumn("Nhà Cung Cấp");
        tableModel.addColumn("Phân Loại");

        tblKhoVatTu = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int viewRow, int viewColumn) {
                Component comp = super.prepareRenderer(renderer, viewRow, viewColumn);
                int modelRow = convertRowIndexToModel(viewRow);
                
                if (modelRow == highlightedModelIndex) {
                    long elapsed = System.currentTimeMillis() - highlightStartTime;
                    if (elapsed < HIGHLIGHT_DURATION) {
                        float progress = (float) elapsed / HIGHLIGHT_DURATION;
                        Color endColor = isRowSelected(viewRow) ? getSelectionBackground() : (modelRow % 2 == 0 ? Color.WHITE : tableStripeColor);
                        
                        int red = (int) (HIGHLIGHT_COLOR.getRed() * (1 - progress) + endColor.getRed() * progress);
                        int green = (int) (HIGHLIGHT_COLOR.getGreen() * (1 - progress) + endColor.getGreen() * progress);
                        int blue = (int) (HIGHLIGHT_COLOR.getBlue() * (1 - progress) + endColor.getBlue() * progress);
                        
                        comp.setBackground(new Color(red, green, blue));
                    } else {
                        highlightedModelIndex = -1;
                        if (highlightTimer != null) highlightTimer.stop();
                    }
                } else {
                     if (isRowSelected(viewRow)) {
                        comp.setBackground(getSelectionBackground());
                    } else {
                        comp.setBackground(modelRow % 2 == 0 ? Color.WHITE : tableStripeColor);
                    }
                }
                
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                     ((JLabel) comp).setBorder(new EmptyBorder(0,5,0,5));
                }
                
                comp.setForeground(textColor);
                comp.setFont(tableFont);

                try {
                    Object idObj = tableModel.getValueAt(modelRow, 0);
                    if (idObj != null && !idObj.toString().isEmpty()) {
                        Object quantityObj = tableModel.getValueAt(modelRow, 3);
                        if (quantityObj != null) {
                            int soLuong = Integer.parseInt(quantityObj.toString());
                            if (soLuong < 500) {
                                comp.setForeground(accentColor);
                                comp.setFont(tableFont.deriveFont(Font.BOLD));
                            }
                        }
                    }
                } catch (Exception e) { /* Bỏ qua lỗi */ }
                
                return comp;
            }
        };
        
        tblKhoVatTu.setRowSorter(new TableRowSorter<>(tableModel));
        
        tblKhoVatTu.setRowHeight(38);
        tblKhoVatTu.setShowGrid(false);
        tblKhoVatTu.setIntercellSpacing(new Dimension(0, 0));
        tblKhoVatTu.setSelectionBackground(new Color(184, 207, 229));
        tblKhoVatTu.setSelectionForeground(textColor);
        tblKhoVatTu.setFocusable(true);
        tblKhoVatTu.setBorder(null);

        JTableHeader header = tblKhoVatTu.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = tblKhoVatTu.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(70);
        columnModel.getColumn(2).setPreferredWidth(220);
        columnModel.getColumn(3).setPreferredWidth(70);
        columnModel.getColumn(4).setPreferredWidth(80);
        columnModel.getColumn(5).setPreferredWidth(180);
        columnModel.getColumn(6).setPreferredWidth(100);

        setupPopupMenu();
        tblKhoVatTu.addMouseListener(new MouseAdapter() {
             @Override
             public void mousePressed(MouseEvent e) { showPopupOrDetails(e); }
             @Override
             public void mouseReleased(MouseEvent e) { showPopupOrDetails(e); }
        });

        JScrollPane scrollPane = new JScrollPane(tblKhoVatTu);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tableWrapperPanel.add(scrollPane, BorderLayout.CENTER);
        
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
            if (!tblKhoVatTu.isRowSelected(viewRow)) {
                tblKhoVatTu.setRowSelectionInterval(viewRow, viewRow);
            }
            int modelRow = tblKhoVatTu.convertRowIndexToModel(viewRow);
            Object idValue = tableModel.getValueAt(modelRow, 0);

            if (idValue != null && !idValue.toString().isEmpty()) {
                try {
                    vatTuDangChon = createKhoVatTuFromRow(modelRow);
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        xemChiTiet();
                    }
                } catch (NumberFormatException ex) {
                    vatTuDangChon = null;
                }
            } else {
                vatTuDangChon = null;
                tblKhoVatTu.clearSelection();
            }
        } else {
            if (!e.isPopupTrigger()) {
                tblKhoVatTu.clearSelection();
                vatTuDangChon = null;
            }
        }
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

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
        popupMenu.setBorder(new LineBorder(borderColor.darker(), 1));

        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết Vật Tư");
        menuItemChinhSua = createStyledMenuItem("Chỉnh Sửa Vật Tư");
        menuItemXoa = createStyledMenuItem("Xóa Vật Tư Này");
        menuItemXoa.setForeground(accentColor);

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
        menuItem.setFont(regularFont.deriveFont(13f));
        menuItem.setBackground(Color.WHITE);
        menuItem.setForeground(textColor);
        menuItem.setOpaque(true);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { menuItem.setBackground(tableStripeColor.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { menuItem.setBackground(Color.WHITE); }
        });
        return menuItem;
    }

    private KhoVatTu createKhoVatTuFromRow(int modelRow) {
        if (modelRow < 0 || modelRow >= tableModel.getRowCount()) {
            return null;
        }
        try {
            int idVatTu = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
            Object maNCCObj = tableModel.getValueAt(modelRow, 1);
            String maNCC = (maNCCObj != null) ? maNCCObj.toString() : "";
            String tenVatTu = tableModel.getValueAt(modelRow, 2).toString();
            int soLuong = Integer.parseInt(tableModel.getValueAt(modelRow, 3).toString());
            String donViTinh = tableModel.getValueAt(modelRow, 4).toString();
            String phanLoai = tableModel.getValueAt(modelRow, 6).toString();
            
            return new KhoVatTu(idVatTu, tenVatTu, soLuong, donViTinh, maNCC, phanLoai);
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo KhoVatTu từ hàng: " + modelRow + " - " + e.getMessage());
            return null;
        }
    }

    private void showNotification(String message, NotificationType type) {
        // Implementation remains the same
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
            public boolean isOpaque() { return false; }
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
    
    public enum NotificationType {
        SUCCESS(new Color(34, 139, 34, 220), "Thành công"),
        WARNING(new Color(255, 165, 0, 220), "Cảnh báo"),
        ERROR(new Color(220, 20, 60, 220), "Lỗi");
        private final Color color;
        private final String title;
        NotificationType(Color color, String title) { this.color = color; this.title = title; }
    }

    private void createInputDialog() {
        themSuaDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        themSuaDialog.setLayout(new BorderLayout(0, 10));
        themSuaDialog.setBackground(Color.WHITE);
        themSuaDialog.getRootPane().setBorder(BorderFactory.createLineBorder(borderColor.darker(), 1));
        
        errorLabelMap = new HashMap<>();
        mainLabelMap = new HashMap<>();

        JPanel dialogHeaderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dialogHeaderPanel.setBackground(primaryColor);
        dialogHeaderPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel dialogTitleLabel = new JLabel();
        dialogTitleLabel.setFont(titleFont.deriveFont(16f));
        dialogTitleLabel.setForeground(Color.WHITE);
        dialogHeaderPanel.add(dialogTitleLabel);
        themSuaDialog.add(dialogHeaderPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Tên Vật Tư ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; gbc.insets = new Insets(6, 8, 0, 8);
        JLabel lblTenVatTu = new JLabel("Tên Vật Tư:"); lblTenVatTu.setFont(regularFont); contentPanel.add(lblTenVatTu, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtTenVatTuDialog = new JTextField(20); styleTextField(txtTenVatTuDialog); contentPanel.add(txtTenVatTuDialog, gbc);
        mainLabelMap.put(txtTenVatTuDialog, lblTenVatTu);
        gbc.gridy++; gbc.insets = new Insets(0, 8, 6, 8);
        JLabel errTenVatTu = new JLabel(" "); errTenVatTu.setFont(smallFont.deriveFont(Font.ITALIC)); contentPanel.add(errTenVatTu, gbc);
        errorLabelMap.put(txtTenVatTuDialog, errTenVatTu);

        // --- Số Lượng ---
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0.3; gbc.insets = new Insets(6, 8, 0, 8);
        JLabel lblSoLuong = new JLabel("Số Lượng:"); lblSoLuong.setFont(regularFont); contentPanel.add(lblSoLuong, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtSoLuongDialog = new JTextField(10); styleTextField(txtSoLuongDialog); contentPanel.add(txtSoLuongDialog, gbc);
        mainLabelMap.put(txtSoLuongDialog, lblSoLuong);
        gbc.gridy++; gbc.insets = new Insets(0, 8, 6, 8);
        JLabel errSoLuong = new JLabel(" "); errSoLuong.setFont(smallFont.deriveFont(Font.ITALIC)); contentPanel.add(errSoLuong, gbc);
        errorLabelMap.put(txtSoLuongDialog, errSoLuong);

        // --- Đơn Vị Tính (Thay đổi thành JComboBox) ---
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0.3; gbc.insets = new Insets(6, 8, 0, 8);
        JLabel lblDonViTinh = new JLabel("Đơn Vị Tính:"); lblDonViTinh.setFont(regularFont); contentPanel.add(lblDonViTinh, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbDonViTinhDialog = new JComboBox<>();
        styleComboBox(cmbDonViTinhDialog);
        cmbDonViTinhDialog.setEditable(true); // Cho phép người dùng nhập mới
        mainLabelMap.put(cmbDonViTinhDialog, lblDonViTinh);
        contentPanel.add(cmbDonViTinhDialog, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 8, 6, 8);
        JLabel errDonViTinh = new JLabel(" "); errDonViTinh.setFont(smallFont.deriveFont(Font.ITALIC)); contentPanel.add(errDonViTinh, gbc);
        errorLabelMap.put(cmbDonViTinhDialog, errDonViTinh);

        // --- Nhà Cung Cấp ---
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0.3; gbc.insets = new Insets(6, 8, 0, 8);
        JLabel lblNhaCungCap = new JLabel("Nhà Cung Cấp:"); lblNhaCungCap.setFont(regularFont); contentPanel.add(lblNhaCungCap, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbNhaCungCapDialog = new JComboBox<>();
        styleComboBoxWithPlaceholder(cmbNhaCungCapDialog, "Chọn nhà cung cấp...");
        mainLabelMap.put(cmbNhaCungCapDialog, lblNhaCungCap);
        contentPanel.add(cmbNhaCungCapDialog, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 8, 6, 8);
        JLabel errNhaCungCap = new JLabel(" "); errNhaCungCap.setFont(smallFont.deriveFont(Font.ITALIC)); contentPanel.add(errNhaCungCap, gbc);
        errorLabelMap.put(cmbNhaCungCapDialog, errNhaCungCap);

        // --- Phân Loại ---
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0.3; gbc.insets = new Insets(6, 8, 0, 8);
        JLabel lblPhanLoai = new JLabel("Phân Loại:"); lblPhanLoai.setFont(regularFont); contentPanel.add(lblPhanLoai, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbPhanLoaiDialog = new JComboBox<>();
        styleComboBoxWithPlaceholder(cmbPhanLoaiDialog, "Chọn phân loại...");
        mainLabelMap.put(cmbPhanLoaiDialog, lblPhanLoai);
        contentPanel.add(cmbPhanLoaiDialog, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 8, 6, 8);
        JLabel errPhanLoai = new JLabel(" "); errPhanLoai.setFont(smallFont.deriveFont(Font.ITALIC)); contentPanel.add(errPhanLoai, gbc);
        errorLabelMap.put(cmbPhanLoaiDialog, errPhanLoai);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 10, 5));
        btnHuyDialog = createRoundedButton("Hủy", new Color(108,117,125), buttonTextColor, 8);
        btnHuyDialog.setFont(buttonFont);
        btnHuyDialog.setPreferredSize(new Dimension(100, 38));
        btnHuyDialog.addActionListener(this);
        btnLuuDialog = createRoundedButton("Lưu", successColor, buttonTextColor, 8);
        btnLuuDialog.setFont(buttonFont);
        btnLuuDialog.setPreferredSize(new Dimension(100, 38));
        btnLuuDialog.addActionListener(this);
        buttonPanel.add(btnHuyDialog);
        buttonPanel.add(btnLuuDialog);
        
        themSuaDialog.add(contentPanel, BorderLayout.CENTER);
        themSuaDialog.add(buttonPanel, BorderLayout.SOUTH);
        themSuaDialog.pack();
        themSuaDialog.setMinimumSize(new Dimension(550, 520));
        themSuaDialog.setLocationRelativeTo((Frame) SwingUtilities.getWindowAncestor(this));
        
        loadDialogComboBoxData();
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(buttonFont);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8,15,8,15));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    private void loadDialogComboBoxData() {
        // --- Load Nhà Cung Cấp ---
        List<NhaCungCap> nhaCungCaps = controller.getAllNhaCungCap();
        cmbNhaCungCapDialog.removeAllItems();
        // KHÔNG thêm item null vào model nữa, để nó không xuất hiện trong danh sách
        for (NhaCungCap ncc : nhaCungCaps) {
            cmbNhaCungCapDialog.addItem(ncc);
        }
        // Đặt mục đã chọn thành null để trình chỉnh sửa hiển thị placeholder
        cmbNhaCungCapDialog.setSelectedItem(null);


        // --- Load Phân Loại ---
        List<String> phanLoais = controller.getAllPhanLoai();
        cmbPhanLoaiDialog.removeAllItems();
        // KHÔNG thêm item null vào model nữa
        for (String phanLoai : phanLoais) {
            cmbPhanLoaiDialog.addItem(phanLoai);
        }
        // Đặt mục đã chọn thành null để trình chỉnh sửa hiển thị placeholder
        cmbPhanLoaiDialog.setSelectedItem(null);


        // --- Load Đơn Vị Tính ---
        String[] donViTinhs = {"Chai", "Lọ", "Cuộn", "Thùng", "Cái", "Hộp", "Tuýp"};
        cmbDonViTinhDialog.removeAllItems();
        for (String dvt : donViTinhs) {
            cmbDonViTinhDialog.addItem(dvt);
        }
        cmbDonViTinhDialog.setSelectedItem(null); // Để trống ban đầu
    }

    private void hienThiDanhSachVatTu(List<KhoVatTu> danhSach) {
        tableModel.setRowCount(0);
        if (danhSach == null || danhSach.isEmpty()) {
            lblTongSoLuongHienThi.setText("Tổng số lượng hiển thị: 0");
            return;
        }
        for (KhoVatTu vatTu : danhSach) {
            String tenNCC = controller.getTenNhaCungCap(vatTu.getMaNCC());
            if(tenNCC == null || tenNCC.trim().isEmpty()) tenNCC = "N/A";
            tableModel.addRow(new Object[]{ vatTu.getIdVatTu(), vatTu.getMaNCC() != null ? vatTu.getMaNCC() : "N/A",
                vatTu.getTenVatTu(), vatTu.getSoLuong(), vatTu.getDonViTinh(), tenNCC, vatTu.getPhanLoai() });
        }
        updateTongSoLuongHienThi();
    }
    
    private void updateTongSoLuongHienThi() {
        int rowCount = tblKhoVatTu.getRowCount();
        int currentTotalQuantity = 0;
        for (int i = 0; i < rowCount; i++) {
            try {
                int modelRow = tblKhoVatTu.convertRowIndexToModel(i);
                Object idObj = tableModel.getValueAt(modelRow, 0);
                if(idObj != null && !idObj.toString().isEmpty()){
                    Object soLuongObj = tableModel.getValueAt(modelRow, 3);
                    if (soLuongObj != null) {
                        currentTotalQuantity += Integer.parseInt(soLuongObj.toString());
                    }
                }
            } catch (NumberFormatException e) { /* Bỏ qua */ }
        }
        lblTongSoLuongHienThi.setText("Tổng số lượng hiển thị: " + currentTotalQuantity);
    }

    private void clearInputFieldsDialog() {
        // --- Cập nhật để reset cmbDonViTinhDialog ---
        clearAllErrors(txtTenVatTuDialog, txtSoLuongDialog, cmbDonViTinhDialog, cmbNhaCungCapDialog, cmbPhanLoaiDialog);
        txtTenVatTuDialog.setText("");
        txtSoLuongDialog.setText("");
        cmbDonViTinhDialog.setSelectedItem(null);
        cmbNhaCungCapDialog.setSelectedItem(null);
        cmbPhanLoaiDialog.setSelectedItem(null);
        vatTuDangChon = null;
    }

    private void timKiemVatTu() {
        String tuKhoa = txtTimKiem.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) tblKhoVatTu.getRowSorter();
        if (sorter == null) {
            sorter = new TableRowSorter<>(tableModel);
            tblKhoVatTu.setRowSorter(sorter);
        }
        if (tuKhoa.isEmpty()) {
            sorter.setRowFilter(null);
            showNotification("Đã làm mới dữ liệu!", NotificationType.SUCCESS);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(tuKhoa)));
            if (tblKhoVatTu.getRowCount() == 0) {
                showNotification("Không tìm thấy vật tư nào khớp.", NotificationType.WARNING);
            } else {
                showNotification("Tìm thấy " + tblKhoVatTu.getRowCount() + " vật tư.", NotificationType.SUCCESS);
            }
        }
        updateTongSoLuongHienThi();
    }
    
    // --- Các phương thức xử lý sự kiện chính ---

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnThemMoi) hienThiDialogThemSua(null);
        else if (source == btnLuuDialog) luuVatTu();
        else if (source == btnHuyDialog) themSuaDialog.setVisible(false);
        else if (source == menuItemXemChiTiet) xemChiTiet();
        else if (source == menuItemChinhSua) hienThiDialogThemSua(vatTuDangChon);
        else if (source == menuItemXoa) xoaVatTu();
        else if (source == btnTimKiem) timKiemVatTu();
        else if (source == btnXuatFile) {
            exportManager.setTableModel(tableModel);
            exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor);
        }
    }

    private void xemChiTiet() {
        if (vatTuDangChon == null) {
            showNotification("Vui lòng chọn một vật tư để xem.", NotificationType.WARNING);
            return;
        }
        
        String maNCC = vatTuDangChon.getMaNCC();
        String tenNCC = controller.getTenNhaCungCap(maNCC);
        String nhaCungCapHienThi = (tenNCC != null && !tenNCC.isEmpty()) ? tenNCC : "Không xác định";
        
        JDialog chiTietDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi Tiết Vật Tư", true);
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

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblTen = new JLabel("Tên Vật Tư:"); lblTen.setFont(labelFont); contentPanel.add(lblTen, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel valTen = new JLabel(vatTuDangChon.getTenVatTu()); valTen.setFont(valueFont); contentPanel.add(valTen, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        JLabel lblSoLuong = new JLabel("Số Lượng Hiện Tại:"); lblSoLuong.setFont(labelFont); contentPanel.add(lblSoLuong, gbc);
        gbc.gridx = 1;
        JLabel valSoLuong = new JLabel(String.valueOf(vatTuDangChon.getSoLuong())); valSoLuong.setFont(valueFont); contentPanel.add(valSoLuong, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        JLabel lblDvt = new JLabel("Đơn Vị Tính:"); lblDvt.setFont(labelFont); contentPanel.add(lblDvt, gbc);
        gbc.gridx = 1;
        JLabel valDvt = new JLabel(vatTuDangChon.getDonViTinh()); valDvt.setFont(valueFont); contentPanel.add(valDvt, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        JLabel lblNcc = new JLabel("Nhà Cung Cấp:"); lblNcc.setFont(labelFont); contentPanel.add(lblNcc, gbc);
        gbc.gridx = 1;
        JLabel valNcc = new JLabel(nhaCungCapHienThi); valNcc.setFont(valueFont); contentPanel.add(valNcc, gbc);

        gbc.gridy++; gbc.gridx = 0;
        JLabel lblMaNcc = new JLabel("Mã NCC:"); lblMaNcc.setFont(labelFont); contentPanel.add(lblMaNcc, gbc);
        gbc.gridx = 1;
        JLabel valMaNcc = new JLabel(vatTuDangChon.getMaNCC() != null ? vatTuDangChon.getMaNCC() : "N/A"); valMaNcc.setFont(valueFont); contentPanel.add(valMaNcc, gbc);

        gbc.gridy++; gbc.gridx = 0;
        JLabel lblPhanLoai = new JLabel("Phân Loại:"); lblPhanLoai.setFont(labelFont); contentPanel.add(lblPhanLoai, gbc);
        gbc.gridx = 1;
        JLabel valPhanLoai = new JLabel(vatTuDangChon.getPhanLoai()); valPhanLoai.setFont(valueFont); contentPanel.add(valPhanLoai, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnSua = createRoundedButton("Sửa", warningColor, buttonTextColor, 8);
        btnSua.setFont(buttonFont);
        btnSua.setPreferredSize(new Dimension(100,38));
        btnSua.addActionListener(e -> {
            chiTietDialog.dispose();
            hienThiDialogThemSua(vatTuDangChon);
        });
        
        JButton btnDong = createRoundedButton("Đóng", new Color(108,117,125), buttonTextColor, 8);
        btnDong.setFont(buttonFont);
        btnDong.setPreferredSize(new Dimension(100,38));
        btnDong.addActionListener(e -> chiTietDialog.dispose());
        
        buttonPanel.add(btnSua);
        buttonPanel.add(btnDong);
        
        chiTietDialog.add(contentPanel, BorderLayout.CENTER);
        chiTietDialog.add(buttonPanel, BorderLayout.SOUTH);
        chiTietDialog.pack();
        chiTietDialog.setMinimumSize(new Dimension(450, chiTietDialog.getPreferredSize().height + 10));
        chiTietDialog.setLocationRelativeTo((JFrame)SwingUtilities.getWindowAncestor(this));
        chiTietDialog.setVisible(true);
    }

    private void hienThiDialogThemSua(KhoVatTu vatTu) {
        loadDialogComboBoxData(); // Tải lại dữ liệu cho các combobox
        clearInputFieldsDialog();

        vatTuDangChon = vatTu;
        JLabel dialogTitleLabel = (JLabel) ((JPanel)themSuaDialog.getContentPane().getComponent(0)).getComponent(0);

        if (vatTu != null) {
            dialogTitleLabel.setText("Chỉnh Sửa Vật Tư (ID: " + vatTu.getIdVatTu() + ")");
            txtTenVatTuDialog.setText(vatTu.getTenVatTu());
            txtSoLuongDialog.setText(String.valueOf(vatTu.getSoLuong()));
            // --- Cập nhật: set giá trị cho cmbDonViTinhDialog ---
            cmbDonViTinhDialog.setSelectedItem(vatTu.getDonViTinh());
            
            for (int i = 0; i < cmbNhaCungCapDialog.getItemCount(); i++) {
                NhaCungCap nccItem = cmbNhaCungCapDialog.getItemAt(i);
                if (nccItem != null && nccItem.getMaNCC().equals(vatTu.getMaNCC())) {
                    cmbNhaCungCapDialog.setSelectedIndex(i); break;
                }
            }
            cmbPhanLoaiDialog.setSelectedItem(vatTu.getPhanLoai());
        } else {
            dialogTitleLabel.setText("Thêm Mới Vật Tư");
        }
        themSuaDialog.pack();
        themSuaDialog.setMinimumSize(new Dimension(550, 520));
        themSuaDialog.setLocationRelativeTo((JFrame)SwingUtilities.getWindowAncestor(this));
        themSuaDialog.setVisible(true);
    }

    private void luuVatTu() {
        // --- Cập nhật để kiểm tra cmbDonViTinhDialog ---
        clearAllErrors(txtTenVatTuDialog, txtSoLuongDialog, cmbDonViTinhDialog, cmbNhaCungCapDialog, cmbPhanLoaiDialog);
        boolean isFormValid = true;

        String tenVatTu = txtTenVatTuDialog.getText().trim();
        String soLuongStr = txtSoLuongDialog.getText().trim();
        Object selectedDVTObj = cmbDonViTinhDialog.getSelectedItem();
        String donViTinh = (selectedDVTObj != null) ? selectedDVTObj.toString().trim() : "";
        Object selectedNCCObj = cmbNhaCungCapDialog.getSelectedItem();
        Object selectedPhanLoaiObj = cmbPhanLoaiDialog.getSelectedItem();

        if (tenVatTu.isEmpty()) { setError(txtTenVatTuDialog, "Tên vật tư không được để trống."); isFormValid = false; }
        if (donViTinh.isEmpty()) { setError(cmbDonViTinhDialog, "Đơn vị tính không được để trống."); isFormValid = false; }
        if (soLuongStr.isEmpty()) {
            setError(txtSoLuongDialog, "Số lượng không được để trống."); isFormValid = false;
        } else {
            try {
                if (Integer.parseInt(soLuongStr) < 0) {
                    setError(txtSoLuongDialog, "Số lượng không được là số âm."); isFormValid = false;
                }
            } catch (NumberFormatException e) {
                setError(txtSoLuongDialog, "Số lượng phải là một số nguyên hợp lệ."); isFormValid = false;
            }
        }
        if (selectedNCCObj == null || !(selectedNCCObj instanceof NhaCungCap)) { setError(cmbNhaCungCapDialog, "Vui lòng chọn nhà cung cấp."); isFormValid = false; }
        if (selectedPhanLoaiObj == null) { setError(cmbPhanLoaiDialog, "Vui lòng chọn phân loại."); isFormValid = false; }
        
        if (!isFormValid) return;

        try {
            NhaCungCap selectedNCC = (NhaCungCap) selectedNCCObj;
            KhoVatTu vatTu = new KhoVatTu(
                (vatTuDangChon != null) ? vatTuDangChon.getIdVatTu() : 0,
                tenVatTu, Integer.parseInt(soLuongStr), donViTinh, selectedNCC.getMaNCC(), selectedPhanLoaiObj.toString()
            );

            boolean success;
            if (vatTuDangChon == null) {
                success = controller.addKhoVatTu(vatTu);
                if (success) { loadData(Integer.MAX_VALUE); showNotification("Thêm vật tư thành công!", NotificationType.SUCCESS); }
                else { showNotification("Thêm vật tư thất bại.", NotificationType.ERROR); }
            } else {
                int idToUpdate = vatTuDangChon.getIdVatTu();
                success = controller.updateKhoVatTu(vatTu);
                if (success) { loadData(idToUpdate); showNotification("Cập nhật vật tư thành công!", NotificationType.SUCCESS); }
                else { showNotification("Cập nhật vật tư thất bại.", NotificationType.ERROR); }
            }
            if (success) themSuaDialog.setVisible(false);
        } catch (Exception ex) {
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
            "Xác Nhận Xóa Vật Tư", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteKhoVatTu(vatTuDangChon.getIdVatTu())) {
                loadData(null);
                showNotification("Đã xóa vật tư thành công!", NotificationType.SUCCESS);
                vatTuDangChon = null;
            } else {
                showNotification("Xóa vật tư thất bại. Có thể vật tư đang được sử dụng.", NotificationType.ERROR);
            }
        }
    }
    
    // --- Các phương thức hỗ trợ ---

    private void loadData(Integer idToHighlight) {
        tableModel.setRowCount(0);
        List<KhoVatTu> danhSach = controller.getAllKhoVatTu();
        danhSach.sort(Comparator.comparing(KhoVatTu::getIdVatTu).reversed());

        if (idToHighlight != null && idToHighlight != Integer.MAX_VALUE) {
            for (int i = 0; i < danhSach.size(); i++) {
                if (danhSach.get(i).getIdVatTu() == idToHighlight) {
                    KhoVatTu itemToMove = danhSach.remove(i);
                    danhSach.add(0, itemToMove);
                    break;
                }
            }
        }
        hienThiDanhSachVatTu(danhSach);
        if (idToHighlight != null) {
            startHighlightingRow(0);
        }
    }

    private void startHighlightingRow(int modelIndex) {
        if (modelIndex < 0) return;
        highlightedModelIndex = modelIndex;
        highlightStartTime = System.currentTimeMillis();
        int viewIndex = tblKhoVatTu.convertRowIndexToView(modelIndex);
        if (viewIndex >= 0) {
            tblKhoVatTu.scrollRectToVisible(tblKhoVatTu.getCellRect(viewIndex, 0, true));
            tblKhoVatTu.setRowSelectionInterval(viewIndex, viewIndex);
        }
        if (highlightTimer != null && highlightTimer.isRunning()) {
            highlightTimer.stop();
        }
        highlightTimer = new Timer(50, e -> {
            if (System.currentTimeMillis() - highlightStartTime > HIGHLIGHT_DURATION) {
                highlightedModelIndex = -1;
                ((Timer) e.getSource()).stop();
            }
            tblKhoVatTu.repaint();
        });
        highlightTimer.start();
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(regularFont);
        textField.setPreferredSize(new Dimension(0, 36));
        textField.setBorder(BorderFactory.createCompoundBorder(new CustomBorder(8, borderColor), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }
    
    // --- Phương thức styleComboBox cơ bản ---
    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(regularFont);
        comboBox.setPreferredSize(new Dimension(0, 38));
        comboBox.setBackground(Color.WHITE);
        // Custom border để trông giống text field
        comboBox.setBorder(BorderFactory.createCompoundBorder(new CustomBorder(8, borderColor), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
    }
    
    // --- Thêm: Lớp Editor tùy chỉnh cho Placeholder ---
    private class PlaceholderComboBoxEditor extends BasicComboBoxEditor {
        private String placeholder;

        public PlaceholderComboBoxEditor(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected JTextField createEditorComponent() {
            JTextField editor = super.createEditorComponent();
            editor.setOpaque(false);
            return editor;
        }
        
        @Override
        public Component getEditorComponent() {
            JTextField editor = (JTextField) super.getEditorComponent();
            return editor;
        }

        @Override
        public Object getItem() {
            Object item = super.getItem();
            if (item == null) {
                return null;
            }
            if (item.equals(placeholder)) {
                return null; // Trả về null nếu người dùng không nhập gì
            }
            return item;
        }

        @Override
        public void setItem(Object anObject) {
            JTextField editor = (JTextField) super.getEditorComponent();
            if (anObject == null) {
                editor.setText(placeholder);
                editor.setForeground(Color.GRAY);
                editor.setFont(regularFont.deriveFont(Font.ITALIC));
            } else {
                super.setItem(anObject);
                editor.setForeground(textColor);
                editor.setFont(regularFont);
            }
        }
    }
    
    // --- Thêm: Phương thức mới để style ComboBox với Placeholder ---
    private <T> void styleComboBoxWithPlaceholder(JComboBox<T> comboBox, String placeholder) {
        styleComboBox(comboBox); // Áp dụng style cơ bản

        // Sử dụng Renderer để hiển thị placeholder trong danh sách thả xuống
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText(placeholder);
                    setFont(getFont().deriveFont(Font.ITALIC));
                    setForeground(Color.GRAY);
                } else if (value instanceof NhaCungCap) {
                    setText(((NhaCungCap) value).getTenNCC());
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });

        // Sử dụng Editor để hiển thị placeholder trong ô chính của combobox
        comboBox.setEditor(new PlaceholderComboBoxEditor(placeholder));
        comboBox.setSelectedItem(null); // Kích hoạt placeholder
    }

    private void setError(JComponent component, String message) {
        Border errorBorder = new LineBorder(accentColor, 1, true);
        if (component instanceof JTextField) {
             component.setBorder(BorderFactory.createCompoundBorder(new CustomBorder(8, accentColor), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        } else if (component instanceof JComboBox) {
             component.setBorder(BorderFactory.createCompoundBorder(new CustomBorder(8, accentColor), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        } else {
            component.setBorder(errorBorder);
        }
        
        if (errorLabelMap.containsKey(component)) {
            JLabel errorLabel = errorLabelMap.get(component);
            errorLabel.setText("<html><i>" + message + "</i></html>");
            errorLabel.setForeground(accentColor);
        }
        if (mainLabelMap.containsKey(component)) {
            JLabel mainLabel = mainLabelMap.get(component);
            String originalText = mainLabel.getText().replace("*", "").replace(":", "").trim();
            mainLabel.setText("<html>" + originalText + ": <span style='color:red;'>*</span></html>");
        }
    }

    private void clearAllErrors(JComponent... components) {
        for (JComponent component : components) {
            if (component instanceof JTextField) {
                styleTextField((JTextField) component);
            } else if (component instanceof JComboBox) {
                // Áp dụng lại style gốc, không cần placeholder ở đây
                 styleComboBox((JComboBox<?>) component);
            }
            if (errorLabelMap.containsKey(component)) {
                errorLabelMap.get(component).setText(" ");
            }
            if (mainLabelMap.containsKey(component)) {
                JLabel mainLabel = mainLabelMap.get(component);
                String text = mainLabel.getText();
                if (text.startsWith("<html>")) {
                    mainLabel.setText(text.replaceAll("<[^>]*>", "").replace("*", "").trim() + ":");
                }
            }
        }
    }

    class RoundedPanel extends JPanel {
        private int r; private boolean s;
        public RoundedPanel(int radius, boolean shadow) { super(); r=radius; s=shadow; setOpaque(false); if(s) setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = 5, y = 5, w = getWidth() - 10, h = getHeight() - 10;
            if (s) {
                for (int i=0; i<5; i++) {
                    g2.setColor(new Color(0,0,0, 0.1f * (1.0f - (float)i/5)));
                    g2.fillRoundRect(x-i, y-i, w+2*i, h+2*i, r+i, r+i);
                }
            }
            g2.setColor(getBackground()); g2.fillRoundRect(x, y, w, h, r, r);
            g2.setColor(borderColor); g2.drawRoundRect(x, y, w-1, h-1, r, r);
            g2.dispose(); super.paintComponent(g);
        }
    }
    
    class CustomBorder extends javax.swing.border.AbstractBorder {
        private int r; private Color c; private int t;
        public CustomBorder(int radius, Color color) { this(radius, color, 1); }
        public CustomBorder(int radius, Color color, int thickness) { r=radius; c=color; t=thickness; }
        @Override
        public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.setStroke(new BasicStroke(t));
            g2.drawRoundRect(x+t/2, y+t/2, w-t, h-t, r, r);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(t+2, t+4, t+2, t+4); }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) { insets.left=insets.right=t+4; insets.top=insets.bottom=t+2; return insets; }
    }

    @Override
    public void showSuccessToast(String message) { showNotification(message, NotificationType.SUCCESS); }
    @Override
    public void showErrorMessage(String title, String message) { showNotification(title != null && !title.isEmpty() ? title + ": " + message : message, NotificationType.ERROR); }
    @Override
    public void showMessage(String message, String title, int messageType) {
        NotificationType type = NotificationType.SUCCESS;
        if (messageType == JOptionPane.ERROR_MESSAGE) type = NotificationType.ERROR;
        else if (messageType == JOptionPane.WARNING_MESSAGE) type = NotificationType.WARNING;
        showNotification(title != null && !title.isEmpty() ? title + ": " + message : message, type);
    }
}