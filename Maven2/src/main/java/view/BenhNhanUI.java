package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import com.toedter.calendar.JDateChooser;
import controller.BenhNhanController;
import model.BenhNhan;
import util.CustomBorder;
import util.ExportManager;
import util.RoundedPanel;
import util.ValidationUtils;
import view.DoanhThuUI.NotificationType;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class BenhNhanUI extends JPanel implements ExportManager.MessageCallback {
    private BenhNhanController qlBenhNhan;
    private JTable tableBenhNhan;
    private DefaultTableModel tableModel;
    private JTextField txtHoTen, txtSoDienThoai, txtCccd, txtDiaChi;
    private JDateChooser dateChooserNgaySinh;
    private JComboBox<String> cbGioiTinh;
    private JDialog inputDialog;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet, menuItemSuaBenhNhan, menuItemXoaBenhNhan;
    private JButton btnThem, btnXoa, btnTimKiem;
    private JTextField txtTimKiem;
    private JButton btnXuatFile;
    private ExportManager exportManager;
    
    // Error message labels for validation
    private Map<JComponent, JLabel> errorLabels = new HashMap<>();
    
    // Modern Theme Colors
    private Color primaryColor = new Color(79, 129, 189); // Professional blue
    private Color secondaryColor = new Color(141, 180, 226); // Lighter blue
    private Color accentColor = new Color(192, 80, 77); // Refined red for delete
    private Color successColor = new Color(86, 156, 104); // Elegant green for add
    private Color warningColor = new Color(237, 187, 85); // Softer yellow for edit
    private Color backgroundColor = new Color(248, 249, 250); // Extremely light gray background
    private Color textColor = new Color(33, 37, 41); // Near-black text
    private Color panelColor = new Color(255, 255, 255); // White panels
    private Color buttonTextColor = Color.WHITE;
    private Color tableHeaderColor = new Color(79, 129, 189); // Match primary color
    private Color tableStripeColor = new Color(245, 247, 250); // Very light stripe
    private Color borderColor = new Color(222, 226, 230); // Light gray borders
    private Color errorColor = new Color(220, 53, 69); // Bootstrap-like error color
    private Color errorBackgroundColor = new Color(248, 215, 218); // Light red for error backgrounds
    
    // Font settings
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font errorFont = new Font("Segoe UI", Font.ITALIC, 11);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);

    public BenhNhanUI() {
        qlBenhNhan = new BenhNhanController();
        initialize();
        exportManager = new ExportManager(this, tableModel, this);
    }
    private void initialize() {
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(backgroundColor);
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
  
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        setupEventListeners();

        createInputDialog();
        loadDanhSachBenhNhan();
    }
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
       
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ BỆNH NHÂN");
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
        txtTimKiem.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemBenhNhan();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        btnTimKiem = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 10);
        btnTimKiem.setPreferredSize(new Dimension(120, 38));
        btnTimKiem.addActionListener(e -> timKiemBenhNhan());

        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Họ tên");
        tableModel.addColumn("Ngày sinh");
        tableModel.addColumn("Giới tính");
        tableModel.addColumn("Số điện thoại");
        tableModel.addColumn("CCCD");
        tableModel.addColumn("Địa chỉ");
        tableBenhNhan = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                }
                if (!comp.getBackground().equals(getSelectionBackground())) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                }
                return comp;
            }
        };        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                for (int i = 0; i < tableBenhNhan.getColumnCount(); i++) {
            tableBenhNhan.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }        
        tableBenhNhan.setFont(tableFont);
        tableBenhNhan.setRowHeight(40);
        tableBenhNhan.setShowGrid(false);
        tableBenhNhan.setIntercellSpacing(new Dimension(0, 0));
        tableBenhNhan.setSelectionBackground(new Color(229, 243, 255));
        tableBenhNhan.setSelectionForeground(textColor);
        tableBenhNhan.setFocusable(false);
        tableBenhNhan.setAutoCreateRowSorter(true);
        tableBenhNhan.setBorder(null);

        JTableHeader header = tableBenhNhan.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
        header.setReorderingAllowed(false);
        
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = tableBenhNhan.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50); // ID column narrow
        columnModel.getColumn(1).setPreferredWidth(150); // Họ tên column wider
        columnModel.getColumn(2).setPreferredWidth(100); // Ngày sinh
        columnModel.getColumn(3).setPreferredWidth(80); // Giới tính
        columnModel.getColumn(4).setPreferredWidth(120); // Số điện thoại
        columnModel.getColumn(5).setPreferredWidth(120); // CCCD
        columnModel.getColumn(6).setPreferredWidth(200); // Địa chỉ wider

        setupPopupMenu();
        JScrollPane scrollPane = new JScrollPane(tableBenhNhan);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        return wrapperPanel;
    }
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor));
        btnThem = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThem.setPreferredSize(new Dimension(100, 45));
        btnThem.addActionListener(e -> showInputDialog(true));
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThem);
        return buttonPanel;
    }
    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));        
        menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemSuaBenhNhan = createStyledMenuItem("Chỉnh Sửa");
        menuItemXoaBenhNhan = createStyledMenuItem("Xóa");        
        menuItemXoaBenhNhan.setForeground(accentColor);        
        popupMenu.add(menuItemXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(menuItemSuaBenhNhan);
        popupMenu.addSeparator();
        popupMenu.add(menuItemXoaBenhNhan);
        menuItemXemChiTiet.addActionListener(e -> {
            if (tableBenhNhan.getSelectedRow() != -1) {
                xemChiTietBenhNhan();
            }
        });

        menuItemSuaBenhNhan.addActionListener(e -> {
            if (tableBenhNhan.getSelectedRow() != -1) {
                showInputDialog(false); 
            }
        });
        
        menuItemXoaBenhNhan.addActionListener(e -> {
            if (tableBenhNhan.getSelectedRow() != -1) {
                xoaBenhNhan();
            }
        });
        
        tableBenhNhan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }            
            private void showPopupMenu(MouseEvent e) {
                int row = tableBenhNhan.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tableBenhNhan.getRowCount()) {
                    tableBenhNhan.setRowSelectionInterval(row, row);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    private void setupEventListeners() {
        btnTimKiem.addActionListener(e -> {
            if (txtTimKiem.getText().trim().isEmpty()) {
                loadDanhSachBenhNhan();
                showSuccessToast("Dữ liệu đã được làm mới!");
            } else {
                timKiemBenhNhan();
            }
        });
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (txtTimKiem.getText().trim().isEmpty()) {
                        loadDanhSachBenhNhan();
                        showSuccessToast("Dữ liệu đã được làm mới!");
                    } else {
                        timKiemBenhNhan();
                    }
                }
            }
        });        
        tableBenhNhan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = tableBenhNhan.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tableBenhNhan.getRowCount()) {
                    tableBenhNhan.setRowSelectionInterval(row, row);                    
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        xemChiTietBenhNhan();
                    }
                } else {
                    tableBenhNhan.clearSelection();
                }
            }            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuItem.setBackground(Color.WHITE);        
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(new Color(240, 240, 240));
            }            
            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE);
            }
        });
        
        return menuItem;
    }
    private void showChiTietBenhNhanDialog(JFrame parent, int idBenhNhan, 
            String hoTen, String gioiTinh, String ngaySinh, String soDienThoai, String diaChi) {
        JDialog dialog = new JDialog(parent, "Chi Tiết Bệnh Nhân", true);
        dialog.setSize(500, 450);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("CHI TIẾT BỆNH NHÂN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Mã bệnh nhân: " + idBenhNhan);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        
        JPanel titlePanelWrapper = new JPanel(new BorderLayout());
        titlePanelWrapper.setBackground(primaryColor);
        titlePanelWrapper.add(titleLabel, BorderLayout.NORTH);
        titlePanelWrapper.add(subtitleLabel, BorderLayout.CENTER);
        
        headerPanel.add(titlePanelWrapper, BorderLayout.CENTER);
        
        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(Color.WHITE);
        
//        addDetailField(detailsPanel, "ID Bệnh Nhân:", String.valueOf(idBenhNhan));
        addDetailField(detailsPanel, "Họ Tên:", hoTen);
        addDetailField(detailsPanel, "Giới Tính:", gioiTinh);
        addDetailField(detailsPanel, "Ngày Sinh:", ngaySinh);
        addDetailField(detailsPanel, "Số Điện Thoại:", soDienThoai);
        addDetailField(detailsPanel, "Địa Chỉ:", diaChi);
        
        detailsPanel.add(Box.createVerticalGlue());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JButton btnXemHoSo = createRoundedButton("Xem hồ sơ BN", primaryColor, Color.WHITE, 10);
        btnXemHoSo.addActionListener(e -> {
            dialog.dispose();
//            xemChiTietHoSoBenhAn();
        });
        JButton editButton = createRoundedButton("Chỉnh Sửa", warningColor, buttonTextColor, 10);
        editButton.addActionListener(e -> {
            dialog.dispose();
            showInputDialog(false); 
        });
        
        JButton closeButton = createRoundedButton("Đóng", Color.WHITE, textColor, 10);
        closeButton.setBorder(new LineBorder(borderColor, 1));
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        
        // Add components to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    private void addDetailField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(15, 0));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(450, 40));
        fieldPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        fieldPanel.setBackground(Color.WHITE);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setPreferredSize(new Dimension(120, 30));
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(textColor);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(regularFont);
        valueComponent.setForeground(textColor);
        
        fieldPanel.add(labelComponent, BorderLayout.WEST);
        fieldPanel.add(valueComponent, BorderLayout.CENTER);
        
        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(450, 1));
        
        panel.add(fieldPanel);
        panel.add(separator);
    }
    private void xemChiTietBenhNhan() {
        int selectedRow = tableBenhNhan.getSelectedRow();
        if (selectedRow == -1) {
            showNotification("Vui lòng chọn bệnh nhân để xem chi tiết.", NotificationType.WARNING);
            return;
        }
        int modelRow = tableBenhNhan.convertRowIndexToModel(selectedRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        String hoTen = (String) tableModel.getValueAt(modelRow, 1);
        String ngaySinh = (String) tableModel.getValueAt(modelRow, 2);
        String gioiTinh = (String) tableModel.getValueAt(modelRow, 3);
        String soDienThoai = (String) tableModel.getValueAt(modelRow, 4);
        String cccd = (String) tableModel.getValueAt(modelRow, 5);
        String diaChi = (String) tableModel.getValueAt(modelRow, 6);

        // Get parent window for modal dialog
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JFrame parent = parentWindow instanceof JFrame ? (JFrame) parentWindow : null;
        
        // Use the more fully-featured showChiTietBenhNhanDialog method
        showChiTietBenhNhanDialog(parent, id, hoTen, gioiTinh, ngaySinh, soDienThoai, diaChi);
    }
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
            
            @Override
            public boolean isOpaque() {
                return false;
            }
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
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(bgColor));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - 0.1f));
    }
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(regularFont);
        Dimension fieldSize = new Dimension(300, 35);
        textField.setPreferredSize(fieldSize);
        textField.setMinimumSize(fieldSize);
        textField.setMaximumSize(fieldSize);
        textField.setBorder(new CompoundBorder(
                new CustomBorder(8, borderColor),
                new EmptyBorder(5, 12, 5, 12)));
        textField.setBackground(Color.WHITE);
        return textField;
    } 
    private JLabel createErrorLabel() {
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(errorFont);
        errorLabel.setForeground(errorColor);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        errorLabel.setVisible(true);
        errorLabel.setPreferredSize(new Dimension(300, 16));
        errorLabel.setMinimumSize(new Dimension(300, 16));
        return errorLabel;
    }
    private void showValidationError(JComponent component, String message) {
        if (component instanceof JTextField) {
            component.setBorder(new CompoundBorder(
                    new LineBorder(errorColor, 1, true),
                    new EmptyBorder(5, 12, 5, 12)));
        } else if (component instanceof JDateChooser) {
            JTextField dateField = (JTextField) ((JDateChooser) component).getDateEditor().getUiComponent();
            dateField.setBorder(new CompoundBorder(
                    new LineBorder(errorColor, 1, true),
                    new EmptyBorder(5, 12, 5, 12)));
        } else if (component instanceof JComboBox) {
            component.setBorder(new LineBorder(errorColor, 1, true));
        }
        JLabel errorLabel = errorLabels.get(component);
        if (errorLabel != null) {
            errorLabel.setText("<html><div style='width: 245px;'>" + message + "</div></html>");
            errorLabel.setVisible(true);
        }
    }
    private void clearValidationError(JComponent component) {
        if (component instanceof JTextField) {
            component.setBorder(new CompoundBorder(
                    new CustomBorder(8, borderColor),
                    new EmptyBorder(5, 12, 5, 12)));
        } else if (component instanceof JDateChooser) {
            component.setBorder(new CustomBorder(8, borderColor));
            JTextField dateField = (JTextField) ((JDateChooser) component).getDateEditor().getUiComponent();
            dateField.setBorder(new EmptyBorder(5, 12, 5, 12));
        } else if (component instanceof JComboBox) {
            component.setBorder(null);
        }
        JLabel errorLabel = errorLabels.get(component);
        if (errorLabel != null) {
            errorLabel.setText(" ");
            errorLabel.setVisible(true);
        }
    }
    private void createInputDialog() {
        Color requiredFieldColor = new Color(255, 0, 0);
        
        inputDialog = new JDialog();
        inputDialog.setTitle("Thông tin bệnh nhân");
        inputDialog.setModal(true);
        inputDialog.setSize(480, 510); // Tăng chiều cao để có thêm không gian cho các field
        inputDialog.setLocationRelativeTo(null);
        inputDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel titleLabel = new JLabel("THÊM MỚI BỆNH NHÂN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 4, 0, 4); // Tăng khoảng cách dọc giữa các fields
        gbc.weightx = 1.0;

        // Họ tên field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblHoTen = new JLabel("Họ tên: ");
        lblHoTen.setFont(regularFont);
        JPanel hoTenLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hoTenLabelPanel.setBackground(Color.WHITE);
        hoTenLabelPanel.add(lblHoTen);
        JLabel starHoTen = new JLabel("*");
        starHoTen.setForeground(requiredFieldColor);
        starHoTen.setFont(regularFont);
        hoTenLabelPanel.add(starHoTen);
        formPanel.add(hoTenLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtHoTen = createStyledTextField();
        txtHoTen.setPreferredSize(new Dimension(230, 32));
        formPanel.add(txtHoTen, gbc);
        
        // Error label for Họ tên
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblHoTenError = createErrorLabel();
        formPanel.add(lblHoTenError, gbc);
        errorLabels.put(txtHoTen, lblHoTenError);
        
        // Ngày sinh field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4); // Tăng khoảng cách phía trên
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblNgaySinh = new JLabel("Ngày sinh: ");
        lblNgaySinh.setFont(regularFont);
        JPanel ngaySinhLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ngaySinhLabelPanel.setBackground(Color.WHITE);
        ngaySinhLabelPanel.add(lblNgaySinh);
        JLabel starNgaySinh = new JLabel("*");
        starNgaySinh.setForeground(requiredFieldColor);
        starNgaySinh.setFont(regularFont);
        ngaySinhLabelPanel.add(starNgaySinh);
        formPanel.add(ngaySinhLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dateChooserNgaySinh = createStyledDateChooser();
        dateChooserNgaySinh.setPreferredSize(new Dimension(230, 32));
        formPanel.add(dateChooserNgaySinh, gbc);
        
        // Error label for Ngày sinh
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblNgaySinhError = createErrorLabel();
        formPanel.add(lblNgaySinhError, gbc);
        errorLabels.put(dateChooserNgaySinh, lblNgaySinhError);
        
        // Giới tính field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4); // Tăng khoảng cách phía trên
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblGioiTinh = new JLabel("Giới tính: ");
        lblGioiTinh.setFont(regularFont);
        JPanel gioiTinhLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        gioiTinhLabelPanel.setBackground(Color.WHITE);
        gioiTinhLabelPanel.add(lblGioiTinh);
        formPanel.add(gioiTinhLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        String[] genders = {"Nam", "Nữ", "Khác"};
        cbGioiTinh = new JComboBox<>(genders);
        cbGioiTinh.setFont(regularFont);
        cbGioiTinh.setPreferredSize(new Dimension(230, 32));
        formPanel.add(cbGioiTinh, gbc);
        
        // No error label needed for combobox
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        formPanel.add(Box.createVerticalStrut(10), gbc); // Giảm kích thước strut
        
        // Số điện thoại field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4); // Tăng khoảng cách phía trên
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblSoDienThoai = new JLabel("Số điện thoại: ");
        lblSoDienThoai.setFont(regularFont);
        JPanel soDienThoaiLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        soDienThoaiLabelPanel.setBackground(Color.WHITE);
        soDienThoaiLabelPanel.add(lblSoDienThoai);
        JLabel starSoDienThoai = new JLabel("*");
        starSoDienThoai.setForeground(requiredFieldColor);
        starSoDienThoai.setFont(regularFont);
        soDienThoaiLabelPanel.add(starSoDienThoai);
        formPanel.add(soDienThoaiLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtSoDienThoai = createStyledTextField();
        txtSoDienThoai.setPreferredSize(new Dimension(230, 32));
        formPanel.add(txtSoDienThoai, gbc);
        
        // Error label for Số điện thoại
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblSoDienThoaiError = createErrorLabel();
        formPanel.add(lblSoDienThoaiError, gbc);
        errorLabels.put(txtSoDienThoai, lblSoDienThoaiError);
        
        // CCCD field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4); // Tăng khoảng cách phía trên
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblCccd = new JLabel("CCCD: ");
        lblCccd.setFont(regularFont);
        JPanel cccdLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cccdLabelPanel.setBackground(Color.WHITE);
        cccdLabelPanel.add(lblCccd);
        JLabel starCccd = new JLabel("*");
        starCccd.setForeground(requiredFieldColor);
        starCccd.setFont(regularFont);
        cccdLabelPanel.add(starCccd);
        formPanel.add(cccdLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtCccd = createStyledTextField();
        txtCccd.setPreferredSize(new Dimension(230, 32));
        formPanel.add(txtCccd, gbc);
        
        // Error label for CCCD
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblCccdError = createErrorLabel();
        formPanel.add(lblCccdError, gbc);
        errorLabels.put(txtCccd, lblCccdError);
        
        // Địa chỉ field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4); // Tăng khoảng cách phía trên
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblDiaChi = new JLabel("Địa chỉ: ");
        lblDiaChi.setFont(regularFont);
        JPanel diaChiLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        diaChiLabelPanel.setBackground(Color.WHITE);
        diaChiLabelPanel.add(lblDiaChi);
        JLabel starDiaChi = new JLabel("*");
        starDiaChi.setForeground(requiredFieldColor);
        starDiaChi.setFont(regularFont);
        diaChiLabelPanel.add(starDiaChi);
        formPanel.add(diaChiLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtDiaChi = createStyledTextField();
        txtDiaChi.setPreferredSize(new Dimension(230, 32));
        formPanel.add(txtDiaChi, gbc);
        
        // Error label for Địa chỉ
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblDiaChiError = createErrorLabel();
        formPanel.add(lblDiaChiError, gbc);
        errorLabels.put(txtDiaChi, lblDiaChiError);

        // Button panel (dialog footer)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        Dimension buttonSize = new Dimension(90, 36);
        JButton btnLuu = createRoundedButton("Lưu", successColor, buttonTextColor, 10);
        btnLuu.setPreferredSize(buttonSize);
        btnLuu.setMinimumSize(buttonSize);
        btnLuu.setMaximumSize(buttonSize);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.addActionListener(e -> luuBenhNhan());

        JButton btnHuy = createRoundedButton("Hủy", accentColor, buttonTextColor, 10);
        btnHuy.setBorder(new LineBorder(borderColor, 1));
        btnHuy.setPreferredSize(buttonSize);
        btnHuy.setMinimumSize(buttonSize);
        btnHuy.setMaximumSize(buttonSize);
        btnHuy.setFocusPainted(false);
        btnHuy.setBorderPainted(false);
        btnHuy.addActionListener(e -> {
            resetAllValidationErrors();
            inputDialog.setVisible(false);
        });
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog content pane
        inputDialog.setContentPane(mainPanel);
        
        // Set up Enter key navigation between fields
        setupEnterKeyNavigation();
        
        // Set default button (responds to Enter key)
        inputDialog.getRootPane().setDefaultButton(btnLuu);
    }
    private void resetAllValidationErrors() {
        clearValidationError(txtHoTen);
        clearValidationError(txtSoDienThoai);
        clearValidationError(txtCccd);
        clearValidationError(txtDiaChi);
        clearValidationError(dateChooserNgaySinh);
        JTextField dateField = (JTextField) dateChooserNgaySinh.getDateEditor().getUiComponent();
        dateField.setBorder(new EmptyBorder(5, 12, 5, 12));
        for (JLabel errorLabel : errorLabels.values()) {
            errorLabel.setText(" ");
            errorLabel.setVisible(true);
        }
    }
    private JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(regularFont);
        dateChooser.setPreferredSize(new Dimension(300, 35)); // Đồng nhất với các thành phần khác
        dateChooser.setBorder(new CustomBorder(8, borderColor));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        
        JTextField dateTextField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateTextField.setFont(regularFont);
        dateTextField.setBorder(new EmptyBorder(5, 12, 5, 12));
        
        return dateChooser;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, boolean required) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        labelPanel.setBackground(panelColor);
        
        JLabel label = new JLabel(labelText);
        label.setFont(regularFont);
        label.setForeground(textColor);
        labelPanel.add(label);
        
        if (required) {
            JLabel requiredLabel = new JLabel("*");
            requiredLabel.setFont(regularFont);
            requiredLabel.setForeground(errorColor);
            labelPanel.add(requiredLabel);
        }
        
        panel.add(labelPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(component, gbc);
    }
    private void showInputDialog(boolean isThem) {
    	resetAllValidationErrors();
    	Container contentPane = inputDialog.getContentPane();
        JPanel mainPanel = (JPanel) contentPane;
        JPanel headerPanel = (JPanel) mainPanel.getComponent(0); // Get the NORTH component (headerPanel)
        JLabel headerTitle = (JLabel) headerPanel.getComponent(0); // Get the CENTER component (titleLabel)
        
        if (!isThem) {
            int selectedRow = tableBenhNhan.getSelectedRow();
            if (selectedRow == -1) {
                showWarningMessage("Vui lòng chọn bệnh nhân để sửa.");
                return;
            }
            inputDialog.setTitle("Chỉnh sửa thông tin bệnh nhân");
            headerTitle.setText("CHỈNH SỬA THÔNG TIN BỆNH NHÂN");

            txtHoTen.setText((String) tableModel.getValueAt(selectedRow, 1));
            
            // Thiết lập giá trị cho JDateChooser thay vì txtNgaySinh
            try {
                String dateString = (String) tableModel.getValueAt(selectedRow, 2);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(dateString);
                dateChooserNgaySinh.setDate(date);
            } catch (ParseException e) {
                showErrorMessage("Lỗi định dạng ngày", "Không thể đọc định dạng ngày tháng");
            }
            
            String gioiTinhValue = (String) tableModel.getValueAt(selectedRow, 3);
            for (int i = 0; i < cbGioiTinh.getItemCount(); i++) {
                if (cbGioiTinh.getItemAt(i).equals(gioiTinhValue)) {
                    cbGioiTinh.setSelectedIndex(i);
                    break;
                }
            }
            
            txtSoDienThoai.setText((String) tableModel.getValueAt(selectedRow, 4));
            txtCccd.setText((String) tableModel.getValueAt(selectedRow, 5));
            txtDiaChi.setText((String) tableModel.getValueAt(selectedRow, 6));
        } else {
            inputDialog.setTitle("Thêm bệnh nhân mới");
            headerTitle.setText("THÊM MỚI BỆNH NHÂN");
            clearInputFields();
        }
        inputDialog.setLocationRelativeTo(this);
        inputDialog.setVisible(true);
        
    }
    
    private void loadDanhSachBenhNhan() {
        try {
            List<BenhNhan> danhSach = qlBenhNhan.layDanhSachBenhNhan();
            tableModel.setRowCount(0);
            for (BenhNhan benhNhan : danhSach) {
                tableModel.addRow(new Object[]{
                        benhNhan.getIdBenhNhan(),
                        benhNhan.getHoTen(),
                        new SimpleDateFormat("yyyy-MM-dd").format(benhNhan.getNgaySinh()),
                        benhNhan.getGioiTinh(),
                        benhNhan.getSoDienThoai(),
                        benhNhan.getCccd(),
                        benhNhan.getDiaChi()
                });
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tải dữ liệu bệnh nhân", e.getMessage());
        }
    }

    private void luuBenhNhan() {
        String hoTen = txtHoTen.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String cccd = txtCccd.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        Date ngaySinh = dateChooserNgaySinh.getDate();
        String gioiTinh = cbGioiTinh.getSelectedItem().toString();
        
        // Clear all previous validation errors
        clearValidationError(txtHoTen);
        clearValidationError(txtSoDienThoai);
        clearValidationError(txtCccd);
        clearValidationError(txtDiaChi);
        clearValidationError(dateChooserNgaySinh);
        
        boolean isValid = true;
        
        if (!ValidationUtils.validateHoTen(hoTen, txtHoTen)) {
            showValidationError(txtHoTen, ValidationUtils.getErrorMessage(txtHoTen));
            txtHoTen.requestFocus();
            isValid = false;
        }
        
        if (!ValidationUtils.validateSoDienThoai(soDienThoai, txtSoDienThoai)) {
            showValidationError(txtSoDienThoai, ValidationUtils.getErrorMessage(txtSoDienThoai));
            if (isValid) {
                txtSoDienThoai.requestFocus();
                isValid = false;
            }
        }
        
        if (!ValidationUtils.validateCCCD(cccd, txtCccd)) {
            showValidationError(txtCccd, ValidationUtils.getErrorMessage(txtCccd));
            if (isValid) {
                txtCccd.requestFocus();
                isValid = false;
            }
        }
        
        if (!ValidationUtils.validateDiaChi(diaChi, txtDiaChi)) {
            showValidationError(txtDiaChi, ValidationUtils.getErrorMessage(txtDiaChi));
            if (isValid) {
                txtDiaChi.requestFocus();
                isValid = false;
            }
        }
        
        if (!ValidationUtils.validateNgaySinh(ngaySinh, dateChooserNgaySinh)) {
            showValidationError(dateChooserNgaySinh, ValidationUtils.getErrorMessage(dateChooserNgaySinh));
            if (isValid) {
                dateChooserNgaySinh.requestFocus();
                isValid = false;
            }
        }
        
        if (!isValid) {
            return;
        }
        
        hoTen = ValidationUtils.sanitizeInput(hoTen);
        soDienThoai = ValidationUtils.sanitizeInput(soDienThoai);
        cccd = ValidationUtils.sanitizeInput(cccd);
        diaChi = ValidationUtils.sanitizeInput(diaChi);
        
        try {
            // Lấy ngày từ JDateChooser
            java.sql.Date sqlDate = new java.sql.Date(ngaySinh.getTime());
            
            int selectedRow = tableBenhNhan.getSelectedRow();
            int idBenhNhan = (selectedRow != -1) ? (int) tableModel.getValueAt(selectedRow, 0) : 0;
            
            BenhNhan benhNhan = new BenhNhan(
                idBenhNhan,
                hoTen,
                sqlDate,
                gioiTinh,
                soDienThoai,
                cccd,
                diaChi
            );
            
            if (selectedRow != -1 && !inputDialog.getTitle().contains("Thêm")) {
                qlBenhNhan.capNhatBenhNhan(benhNhan);
                showSuccessToast("Thông tin bệnh nhân đã được cập nhật thành công!");
            } else {
                qlBenhNhan.themBenhNhan(benhNhan);
                showSuccessToast("Bệnh nhân mới đã được thêm thành công!");
            }
            
            loadDanhSachBenhNhan();
            inputDialog.setVisible(false);
            
        } catch (SQLException e) {
            showErrorMessage("Lỗi cơ sở dữ liệu", e.getMessage());
        }
    }
    private void xoaBenhNhan() {
        int selectedRow = tableBenhNhan.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Vui lòng chọn bệnh nhân để xóa.");
            return;
        }
        int modelRow = tableBenhNhan.convertRowIndexToModel(selectedRow);
        int idBenhNhan = (int) tableModel.getValueAt(modelRow, 0);
        String tenBenhNhan = (String) tableModel.getValueAt(modelRow, 1);
        JDialog confirmDialog = new JDialog();
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setModal(true);
        confirmDialog.setSize(400, 200);
        confirmDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel messagePanel = new JPanel(new BorderLayout(15, 0));
        messagePanel.setBackground(Color.WHITE);
        
        JLabel messageLabel = new JLabel("<html>Bạn có chắc chắn muốn xóa bệnh nhân <b>" + tenBenhNhan + "</b>?</html>");
        messageLabel.setFont(regularFont);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        panel.add(messagePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton cancelButton = createRoundedButton("Hủy", new Color(158, 158, 158), Color.WHITE, 8);
        cancelButton.addActionListener(e -> confirmDialog.dispose());
        
        JButton deleteButton = createRoundedButton("Xóa", accentColor, Color.WHITE, 8);
        deleteButton.addActionListener(e -> {
            try {
                qlBenhNhan.xoaBenhNhan(idBenhNhan);                
                confirmDialog.dispose();
                SwingUtilities.invokeLater(() -> {
                    loadDanhSachBenhNhan();
                    showSuccessToast("Bệnh nhân đã được xóa thành công!");
                });
            } catch (SQLException ex) {
                showErrorMessage("Lỗi khi xóa bệnh nhân", ex.getMessage());
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);        
        confirmDialog.setContentPane(panel);
        confirmDialog.setVisible(true);
    }
    private void timKiemBenhNhan() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadDanhSachBenhNhan();
            return;
        }
        try {
            List<BenhNhan> danhSach = qlBenhNhan.layDanhSachBenhNhan();
            tableModel.setRowCount(0);
            for (BenhNhan benhNhan : danhSach) {
                if (benhNhan.getHoTen().toLowerCase().contains(keyword) ||
                        (benhNhan.getSoDienThoai() != null && benhNhan.getSoDienThoai().toLowerCase().contains(keyword)) ||
                        (benhNhan.getCccd() != null && benhNhan.getCccd().toLowerCase().contains(keyword)) ||
                        (benhNhan.getDiaChi() != null && benhNhan.getDiaChi().toLowerCase().contains(keyword))) {

                    tableModel.addRow(new Object[]{
                            benhNhan.getIdBenhNhan(),
                            benhNhan.getHoTen(),
                            new SimpleDateFormat("yyyy-MM-dd").format(benhNhan.getNgaySinh()),
                            benhNhan.getGioiTinh(),
                            benhNhan.getSoDienThoai(),
                            benhNhan.getCccd(),
                            benhNhan.getDiaChi()
                    });
                }
            }
            if (tableModel.getRowCount() == 0) {
                showNotification("Không tìm thấy kết quả nào cho: '" + keyword + "'", NotificationType.WARNING);
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tìm kiếm", e.getMessage());
        }
    }
    private void clearInputFields() {
        txtHoTen.setText("");
        dateChooserNgaySinh.setDate(null); // Đặt lại JDateChooser
        cbGioiTinh.setSelectedIndex(0);
        txtSoDienThoai.setText("");
        txtCccd.setText("");
        txtDiaChi.setText("");
    }
    @Override
    public void showSuccessToast(String message) {
        JDialog toastDialog = new JDialog();
        toastDialog.setUndecorated(true);
        toastDialog.setAlwaysOnTop(true);        
        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(successColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        toastPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));               
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);
        toastPanel.add(messageLabel);        
        toastDialog.add(toastPanel);
        toastDialog.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toastDialog.setLocation(
                screenSize.width - toastDialog.getWidth() - 20,
                screenSize.height - toastDialog.getHeight() - 60
        );
        
        toastDialog.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                toastDialog.dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    @Override
    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
    private void setupEnterKeyNavigation() {
        JComponent[] components = new JComponent[] {
            txtHoTen,
            dateChooserNgaySinh.getDateEditor().getUiComponent(), // Get the text field component
            cbGioiTinh,
            txtSoDienThoai,
            txtCccd,
            txtDiaChi
        };
        for (int i = 0; i < components.length - 1; i++) {
            final int nextIndex = i + 1;
            if (components[i] instanceof JTextField) {
                ((JTextField) components[i]).addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            components[nextIndex].requestFocus();
                        }
                    }
                });
            }
        }
        if (components[components.length - 1] instanceof JTextField) {
            ((JTextField) components[components.length - 1]).addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        luuBenhNhan();
                    }
                }
            });
        }
        cbGioiTinh.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSoDienThoai.requestFocus();
                }
            }
        });
        ((JTextField) dateChooserNgaySinh.getDateEditor().getUiComponent()).addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cbGioiTinh.requestFocus();
                }
            }
        });
    }
    public enum NotificationType {
        SUCCESS(new Color(86, 156, 104), "Thành công"),
        WARNING(new Color(237, 187, 85), "Cảnh báo"),
        ERROR(new Color(192, 80, 77), "Lỗi");
        
        private final Color color;
        private final String title;
        
        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }
    private void showNotification(String message, NotificationType type) {
        JDialog toastDialog = new JDialog();
        toastDialog.setUndecorated(true);
        toastDialog.setAlwaysOnTop(true);

        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(type.color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        toastPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel titleLabel = new JLabel(type.title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        toastPanel.add(titleLabel);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.WHITE);
        toastPanel.add(messageLabel);
        toastDialog.add(toastPanel);
        toastDialog.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toastDialog.setLocation(
            screenSize.width - toastDialog.getWidth() - 20,
            screenSize.height - toastDialog.getHeight() - 60
        );
        toastDialog.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                toastDialog.dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    @Override
	public void showMessage(String message, String title, int messageType) {
	}
    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Cảnh báo",
            JOptionPane.WARNING_MESSAGE
        );
    }
}