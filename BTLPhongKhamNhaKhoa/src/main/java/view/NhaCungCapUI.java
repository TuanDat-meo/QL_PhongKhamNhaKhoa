package view;

import controller.NhaCungCapController;
import model.NhaCungCap;
import util.CustomBorder;
import util.DataChangeListener;
import util.ExportManager;
import util.RoundedPanel;
import util.ExportManager.MessageCallback;
import view.DoanhThuUI.NotificationType; // Giả định đây là lớp NotificationType đúng

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter; // Import cho định dạng ngày tháng
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NhaCungCapUI extends JPanel implements MessageCallback, DataChangeListener {
    private NhaCungCapController nhaCungCapController;
    private DefaultTableModel nhaCungCapTableModel;
    private JTable nhaCungCapTable;

    private JButton btnThem;
    private JButton btnXuatFile;
    private JButton btnTimKiem;

    private JFrame parentFrame;

    private JPopupMenu popupMenu;
    private JMenuItem menuItemXemChiTiet;
    private JMenuItem menuItemSua;
    private JMenuItem menuItemXoa;

    private JTextField txtTimKiem;
    private JLabel lblTimKiem;

    // Modern styling properties (giữ nguyên)
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

    // Font settings (giữ nguyên)
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private Font totalRowFont = new Font("Segoe UI", Font.BOLD, 14);

    private ExportManager exportManager;

    public NhaCungCapUI() {
        initializeUI();
        setupEventListeners();
        
        exportManager = new ExportManager(this, nhaCungCapTableModel, this);
        
        lamMoiDanhSach(); // Tải dữ liệu từ CSDL khi khởi tạo
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 10, 20, 10));
        setBackground(backgroundColor);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        setupPopupMenu();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 10, 15, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ NHÀ CUNG CẤP");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);

        lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(regularFont);
        lblTimKiem.setForeground(textColor);
        
        txtTimKiem = new JTextField(18);
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(220, 38));
        
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor), 
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
                
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterDanhSach();
                }
            }
        });

        btnTimKiem = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 10);
        btnTimKiem.setPreferredSize(new Dimension(120, 38));

        searchPanel.add(lblTimKiem);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        RoundedPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Cập nhật tên cột cho bảng để khớp với model NhaCungCap mới (6 cột)
        nhaCungCapTableModel = new DefaultTableModel(
            new Object[]{"ID", "Tên NCC", "Địa chỉ", "Số điện thoại", "Mã số thuế", "Ngày đăng ký"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        nhaCungCapTable = new JTable(nhaCungCapTableModel) {
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
        
        for (int i = 0; i < nhaCungCapTable.getColumnCount(); i++) {
            nhaCungCapTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        nhaCungCapTable.setFont(tableFont);
        nhaCungCapTable.setRowHeight(40);
        nhaCungCapTable.setShowGrid(false);
        nhaCungCapTable.setIntercellSpacing(new Dimension(0, 0));
        nhaCungCapTable.setSelectionBackground(new Color(229, 243, 255));
        nhaCungCapTable.setSelectionForeground(textColor);
        nhaCungCapTable.setFocusable(false);
        nhaCungCapTable.setAutoCreateRowSorter(true);
        nhaCungCapTable.setBorder(null);

        JTableHeader header = nhaCungCapTable.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
        header.setReorderingAllowed(false);
        
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        // Điều chỉnh lại độ rộng cột phù hợp với 6 cột mới
        TableColumnModel columnModel = nhaCungCapTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(150); // Tên NCC
        columnModel.getColumn(2).setPreferredWidth(200); // Địa chỉ
        columnModel.getColumn(3).setPreferredWidth(100); // Số điện thoại
        columnModel.getColumn(4).setPreferredWidth(100); // Mã số thuế
        columnModel.getColumn(5).setPreferredWidth(100); // Ngày đăng ký
        
        JScrollPane tableScrollPane = new JScrollPane(nhaCungCapTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        return wrapperPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        
        btnThem = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThem.setPreferredSize(new Dimension(100, 45));
        
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThem);
        
        return buttonPanel;
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

        nhaCungCapTable.addMouseListener(new MouseAdapter() {
            @Override
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
                
                if (evt.getClickCount() == 2 && !evt.isPopupTrigger()) {
                    xemChiTietNhaCungCap();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger() && evt.getComponent() instanceof JTable) {
                    int r = nhaCungCapTable.rowAtPoint(evt.getPoint());
                    if (r >= 0 && r < nhaCungCapTable.getRowCount()) {
                        nhaCungCapTable.setRowSelectionInterval(r, r);
                    } else {
                        nhaCungCapTable.clearSelection();
                    }
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
    }
    
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setBackground(panelColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return menuItem;
    }       
    
    private void setupEventListeners() {
        nhaCungCapTable.getSelectionModel().addListSelectionListener(e -> {
            // Logic ở đây không cần thiết phải cập nhật JTextField
        });

        btnTimKiem.addActionListener(e -> {
            if (txtTimKiem.getText().trim().isEmpty()) {
                lamMoiDanhSach();
                showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            } else {
                filterDanhSach();
            }
        });
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (txtTimKiem.getText().trim().isEmpty()) {
                        lamMoiDanhSach();
                        showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
                    } else {
                        filterDanhSach();
                    }
                }
            }
        });

        btnThem.addActionListener(e -> {
            NhaCungCapDialog dialog = new NhaCungCapDialog(getParentFrame(), getNhaCungCapController(), null, this);
            dialog.setVisible(true);
        });

        btnXuatFile.addActionListener(e -> exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor));
        
        menuItemXemChiTiet.addActionListener(e -> xemChiTietNhaCungCap());
        menuItemSua.addActionListener(e -> suaNhaCungCapTuPopup());
        menuItemXoa.addActionListener(e -> xoaNhaCungCapTuPopup());
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
    
    public void showNotification(String message, NotificationType type) {
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
    
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    @Override
    public void showSuccessToast(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }

    private void xemChiTietNhaCungCap() {
        int selectedRow = nhaCungCapTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedRow = nhaCungCapTable.convertRowIndexToModel(selectedRow);
            String maNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0));
            
            NhaCungCap ncc = getNhaCungCapController().getNhaCungCapById(maNCC);

            if (ncc != null) {
                JPanel detailsPanel = new JPanel();
                detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                detailsPanel.setBackground(panelColor);
                detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                
                // Hiển thị TẤT CẢ các trường chi tiết
                addDetailField(detailsPanel, "Mã NCC:", ncc.getMaNCC());
                addDetailField(detailsPanel, "Tên NCC:", ncc.getTenNCC());
                addDetailField(detailsPanel, "Địa chỉ:", ncc.getDiaChi());
                addDetailField(detailsPanel, "Số điện thoại:", ncc.getSoDienThoai());
                addDetailField(detailsPanel, "Mã số thuế:", ncc.getMaSoThue() != null ? ncc.getMaSoThue() : "N/A"); // Hiển thị mã số thuế
                addDetailField(detailsPanel, "Ngày đăng ký:", ncc.getNgayDangKy() != null ? ncc.getNgayDangKy().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A"); // Hiển thị ngày đăng ký
                
                JDialog detailDialog = new JDialog(getParentFrame(), "Chi tiết nhà cung cấp", true);
                detailDialog.setLayout(new BorderLayout());
                
                JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                headerPanel.setBackground(primaryColor);
                headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                
                JLabel headerLabel = new JLabel("Chi tiết nhà cung cấp");
                headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                headerLabel.setForeground(Color.WHITE);
                headerPanel.add(headerLabel);
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(panelColor);
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
                
                JButton closeButton = createRoundedButton("Đóng", primaryColor, Color.WHITE, 8);
                closeButton.addActionListener(e -> detailDialog.dispose());
                buttonPanel.add(closeButton);
                
                detailDialog.add(headerPanel, BorderLayout.NORTH);
                detailDialog.add(detailsPanel, BorderLayout.CENTER);
                detailDialog.add(buttonPanel, BorderLayout.SOUTH);
                
                detailDialog.setSize(450, 380); // Điều chỉnh kích thước phù hợp với số lượng trường
                detailDialog.setLocationRelativeTo(this);
                detailDialog.setResizable(false);
                detailDialog.setVisible(true);
            } else {
                showErrorMessage("Không tìm thấy thông tin nhà cung cấp này trong CSDL.");
            }
        } else {
            showErrorMessage("Vui lòng chọn một nhà cung cấp để xem chi tiết.");
        }
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
        
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(450, 1));
        
        panel.add(fieldPanel);
        panel.add(separator);
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

    private JFrame getParentFrame() {
        if (parentFrame == null) {
            parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        }
        return parentFrame;
    }

    private NhaCungCapController getNhaCungCapController() {
        if (nhaCungCapController == null) {
            nhaCungCapController = new NhaCungCapController();
        }
        return nhaCungCapController;
    }

    private void filterDanhSach() {
        String searchText = txtTimKiem.getText().toLowerCase();
        nhaCungCapTableModel.setRowCount(0);
        List<NhaCungCap> danhSachNCC = getNhaCungCapController().layDanhSachNhaCungCap();

        // Cập nhật filter để tìm kiếm trên các trường mới
        List<NhaCungCap> danhSachDaLoc = danhSachNCC.stream()
                .filter(ncc -> String.valueOf(ncc.getMaNCC()).toLowerCase().contains(searchText) ||
                               String.valueOf(ncc.getTenNCC()).toLowerCase().contains(searchText) ||
                               String.valueOf(ncc.getDiaChi()).toLowerCase().contains(searchText) ||
                               String.valueOf(ncc.getSoDienThoai()).toLowerCase().contains(searchText) ||
                               (ncc.getMaSoThue() != null && ncc.getMaSoThue().toLowerCase().contains(searchText)) || // Tìm kiếm theo Mã số thuế
                               (ncc.getNgayDangKy() != null && ncc.getNgayDangKy().format(DateTimeFormatter.ISO_LOCAL_DATE).toLowerCase().contains(searchText))) // Tìm kiếm theo Ngày đăng ký
                .collect(Collectors.toList());

        for (NhaCungCap ncc : danhSachDaLoc) {
            Object[] rowData = {
                ncc.getMaNCC(), 
                ncc.getTenNCC(), 
                ncc.getDiaChi(),
                ncc.getSoDienThoai(),
                ncc.getMaSoThue(), // Thêm Mã số thuế vào hàng
                ncc.getNgayDangKy() != null ? ncc.getNgayDangKy().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A" // Thêm Ngày đăng ký vào hàng
            };
            nhaCungCapTableModel.addRow(rowData);
        }
        if (danhSachDaLoc.isEmpty()) {
            showNotification("Không tìm thấy kết quả phù hợp!", NotificationType.WARNING);
        } else {
            showNotification("Tìm thấy " + danhSachDaLoc.size() + " kết quả phù hợp!", NotificationType.SUCCESS);
        }
        updateTableAppearance();
    }

    private void updateTableAppearance() {
        SwingUtilities.invokeLater(() -> {
            nhaCungCapTable.repaint();
        });
    }

    private void suaNhaCungCapTuPopup() {
        int selectedRow = nhaCungCapTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedRow = nhaCungCapTable.convertRowIndexToModel(selectedRow);
            String maNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0));
            
            NhaCungCap nccToEdit = getNhaCungCapController().getNhaCungCapById(maNCC);

            if (nccToEdit != null) {
                NhaCungCapDialog dialog = new NhaCungCapDialog(getParentFrame(), getNhaCungCapController(), nccToEdit, this);
                dialog.setVisible(true);
            } else {
                showErrorMessage("Không thể tải thông tin nhà cung cấp để chỉnh sửa. Vui lòng thử lại.");
            }
        } else {
            showErrorMessage("Vui lòng chọn một nhà cung cấp để sửa.");
        }
    }

    private void xoaNhaCungCapTuPopup() {
        int selectedRow = nhaCungCapTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Vui lòng chọn một nhà cung cấp để xóa.");
            return;
        }

        String maNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 0));
        String tenNCC = String.valueOf(nhaCungCapTableModel.getValueAt(selectedRow, 1));

        int option = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc chắn muốn xóa nhà cung cấp '" + tenNCC + "' (Mã: " + maNCC + ")?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            if (getNhaCungCapController().xoaNhaCungCap(maNCC)) {
                showInfoMessage("Thao tác xóa/đánh dấu ngừng cung cấp thành công.");
                lamMoiDanhSach();
            } else {
                showErrorMessage("Không thể xóa nhà cung cấp. Có thể nhà cung cấp này có dữ liệu liên quan hoặc có lỗi xảy ra.");
            }
        }
    }

    public void lamMoiDanhSach() {
        nhaCungCapTableModel.setRowCount(0);
        List<NhaCungCap> danhSachNCC = getNhaCungCapController().layDanhSachNhaCungCap();
        
        // Chỉ hiển thị dữ liệu từ CSDL, không thêm dữ liệu mẫu bằng code
        if (danhSachNCC.isEmpty()) {
            showNotification("Chưa có nhà cung cấp nào trong hệ thống. Vui lòng thêm mới.", NotificationType.WARNING);
        } else {
            for (NhaCungCap ncc : danhSachNCC) {
                Object[] rowData = {
                    ncc.getMaNCC(), 
                    ncc.getTenNCC(), 
                    ncc.getDiaChi(),
                    ncc.getSoDienThoai(),
                    ncc.getMaSoThue(), // Thêm Mã số thuế vào hàng
                    ncc.getNgayDangKy() != null ? ncc.getNgayDangKy().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A" // Thêm Ngày đăng ký vào hàng
                };
                nhaCungCapTableModel.addRow(rowData);
            }
        }
        updateTableAppearance();
    }

    public void cleanup() {
        if (nhaCungCapController != null) {
            nhaCungCapController.closeConnection();
        }
    }

    @Override
    public void onDataChanged() {
        lamMoiDanhSach();
    }
}