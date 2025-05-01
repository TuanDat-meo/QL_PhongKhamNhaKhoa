package view;

import controller.KhoVatTuController;
import model.KhoVatTu;
import model.NhaCungCap;
import util.ExportManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

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
    private KhoVatTu vatTuDangChon;
    private ExportManager exportManager;
    
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
        loadData();
    }

    private void initialize() {
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(20, 10, 20, 10));
        setBackground(backgroundColor);

        // Header Panel with Title and Search
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

        // Table Panel
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

        // Create input dialog
        createInputDialog();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 10, 5, 10));

        // Title Panel with text
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ KHO VẬT TƯ");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Search Panel with rounded styling
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        
        txtTimKiem = new JTextField(18);
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(220, 38));
        
        // Create rounded border with padding
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(10, borderColor), 
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
                
        // Add key listener for Enter key
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemVatTu();
                }
            }
        });

        btnTimKiem = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 10);
        btnTimKiem.setPreferredSize(new Dimension(120, 38));
        btnTimKiem.addActionListener(e -> timKiemVatTu());

        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        
        // Create a panel with shadow effect
        RoundedPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create table model and table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        tableModel.addColumn("ID");
        tableModel.addColumn("Mã NCC");
        tableModel.addColumn("Tên Vật Tư");
        tableModel.addColumn("Số Lượng");
        tableModel.addColumn("Đơn Vị Tính");
        tableModel.addColumn("Nhà Cung Cấp");
        tableModel.addColumn("Phân Loại");

        tblKhoVatTu = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                
                // Center the content
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                }
                
                // Add alternating row colors
                if (!comp.getBackground().equals(getSelectionBackground())) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                }
                return comp;
            }
        };
        
        // Set default cell renderer to center all content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Apply center renderer to all columns
        for (int i = 0; i < tblKhoVatTu.getColumnCount(); i++) {
            tblKhoVatTu.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        tblKhoVatTu.setFont(tableFont);
        tblKhoVatTu.setRowHeight(40);
        tblKhoVatTu.setShowGrid(false);
        tblKhoVatTu.setIntercellSpacing(new Dimension(0, 0));
        tblKhoVatTu.setSelectionBackground(new Color(229, 243, 255));
        tblKhoVatTu.setSelectionForeground(textColor);
        tblKhoVatTu.setFocusable(false);
        tblKhoVatTu.setAutoCreateRowSorter(true);
        tblKhoVatTu.setBorder(null);

        // Style table header
        JTableHeader header = tblKhoVatTu.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
        header.setReorderingAllowed(false);
        
        // Center the header text
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Set column widths
        TableColumnModel columnModel = tblKhoVatTu.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(150);
        columnModel.getColumn(6).setPreferredWidth(120);

        // Add event listener for table selection
        tblKhoVatTu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblKhoVatTu.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tblKhoVatTu.getRowCount()) {
                    // Ensure the row is selected
                    tblKhoVatTu.setRowSelectionInterval(row, row);
                    
                    try {
                        // Get ID from the selected row
                        Object idValue = tblKhoVatTu.getValueAt(row, 0);
                        if (idValue != null && !idValue.toString().isEmpty()) {
                            int id = Integer.parseInt(idValue.toString());
                            // Get full information of the vat tu
                            vatTuDangChon = new KhoVatTu(
                                id,
                                tblKhoVatTu.getValueAt(row, 2).toString(), // Tên Vật Tư
                                Integer.parseInt(tblKhoVatTu.getValueAt(row, 3).toString()), // Số Lượng
                                tblKhoVatTu.getValueAt(row, 4).toString(), // Đơn Vị Tính
                                tblKhoVatTu.getValueAt(row, 1).toString(), // Mã NCC
                                tblKhoVatTu.getValueAt(row, 6).toString()  // Phân Loại
                            );
                        } else {
                            vatTuDangChon = null;
                        }
                    } catch (Exception ex) {
                        vatTuDangChon = null;
                    }
                    
                    // Show context menu on right-click
                    if (SwingUtilities.isRightMouseButton(e)) {
                        showPopupMenu(e.getComponent(), e.getX(), e.getY());
                    } 
                    // Double-click to open edit dialog
                    else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                        if (vatTuDangChon != null) {
                            hienThiDialogThemSua(vatTuDangChon);
                        }
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // Alternative approach for right-click context menu
                if (e.isPopupTrigger()) {
                    int row = tblKhoVatTu.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tblKhoVatTu.setRowSelectionInterval(row, row);
                        showPopupMenu(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblKhoVatTu);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        createPopupMenu();

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
        
        
        btnThemMoi = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10);
        btnThemMoi.setPreferredSize(new Dimension(100, 45));
        btnThemMoi.addActionListener(e -> hienThiDialogThemSua(null));
        
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThemMoi);

        return buttonPanel;
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        
        menuItemXemChiTiet = new JMenuItem("Xem chi tiết");
        menuItemXemChiTiet.setFont(regularFont);
        menuItemXemChiTiet.setForeground(textColor);
        
        menuItemChinhSua = new JMenuItem("Chỉnh sửa");
        menuItemChinhSua.setFont(regularFont);
        menuItemChinhSua.setForeground(primaryColor);
        
        menuItemXoa = new JMenuItem("Xóa");
        menuItemXoa.setFont(regularFont);
        menuItemXoa.setForeground(accentColor);
        
        MouseAdapter menuItemHover = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();
                item.setBackground(new Color(245, 247, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();
                item.setBackground(null);
            }
        };
        
        menuItemXemChiTiet.addMouseListener(menuItemHover);
        menuItemChinhSua.addMouseListener(menuItemHover);
        menuItemXoa.addMouseListener(menuItemHover);
        
        menuItemXemChiTiet.addActionListener(this);
        menuItemChinhSua.addActionListener(this);
        menuItemXoa.addActionListener(this);
        
        popupMenu.add(menuItemXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(menuItemChinhSua);
        popupMenu.add(menuItemXoa);
        popupMenu.setBackground(Color.WHITE);
    }

    private void showPopupMenu(Component component, int x, int y) {
        if (vatTuDangChon != null) {
            popupMenu.show(component, x, y);
        }
    }

    private void createInputDialog() {
        themSuaDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm/Sửa Vật Tư", true);
        themSuaDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        contentPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Tên Vật Tư
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lblTenVatTu = new JLabel("Tên Vật Tư:");
        lblTenVatTu.setFont(regularFont);
        contentPanel.add(lblTenVatTu, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtTenVatTuDialog = new JTextField();
        txtTenVatTuDialog.setFont(regularFont);
        txtTenVatTuDialog.setPreferredSize(new Dimension(0, 35));
        contentPanel.add(txtTenVatTuDialog, gbc);
        
        // Số Lượng
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblSoLuong = new JLabel("Số Lượng:");
        lblSoLuong.setFont(regularFont);
        contentPanel.add(lblSoLuong, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtSoLuongDialog = new JTextField();
        txtSoLuongDialog.setFont(regularFont);
        txtSoLuongDialog.setPreferredSize(new Dimension(0, 35));
        contentPanel.add(txtSoLuongDialog, gbc);
        
        // Đơn Vị Tính
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel lblDonViTinh = new JLabel("Đơn Vị Tính:");
        lblDonViTinh.setFont(regularFont);
        contentPanel.add(lblDonViTinh, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDonViTinhDialog = new JTextField();
        txtDonViTinhDialog.setFont(regularFont);
        txtDonViTinhDialog.setPreferredSize(new Dimension(0, 35));
        contentPanel.add(txtDonViTinhDialog, gbc);
        
        // Nhà Cung Cấp
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel lblNhaCungCap = new JLabel("Nhà Cung Cấp:");
        lblNhaCungCap.setFont(regularFont);
        contentPanel.add(lblNhaCungCap, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbNhaCungCapDialog = new JComboBox<>();
        cmbNhaCungCapDialog.setFont(regularFont);
        cmbNhaCungCapDialog.setPreferredSize(new Dimension(0, 35));
        contentPanel.add(cmbNhaCungCapDialog, gbc);
        
        // Phân Loại
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        JLabel lblPhanLoai = new JLabel("Phân Loại:");
        lblPhanLoai.setFont(regularFont);
        contentPanel.add(lblPhanLoai, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbPhanLoaiDialog = new JComboBox<>();
        cmbPhanLoaiDialog.setFont(regularFont);
        cmbPhanLoaiDialog.setPreferredSize(new Dimension(0, 35));
        contentPanel.add(cmbPhanLoaiDialog, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 10));
        
        btnHuyDialog = createRoundedButton("Hủy", accentColor, buttonTextColor, 8);
        btnHuyDialog.setPreferredSize(new Dimension(100, 40));
        btnHuyDialog.addActionListener(e -> themSuaDialog.setVisible(false));
        
        btnLuuDialog = createRoundedButton("Lưu", primaryColor, buttonTextColor, 8);
        btnLuuDialog.setPreferredSize(new Dimension(100, 40));
        btnLuuDialog.addActionListener(e -> luuVatTu());
        
        buttonPanel.add(btnHuyDialog);
        buttonPanel.add(btnLuuDialog);
        
        themSuaDialog.add(contentPanel, BorderLayout.CENTER);
        themSuaDialog.add(buttonPanel, BorderLayout.SOUTH);
        themSuaDialog.pack();
        themSuaDialog.setSize(new Dimension(500, 350));
        themSuaDialog.setLocationRelativeTo(this);
        
        // Load data for JComboBoxes
        loadNhaCungCapForDialog();
        loadPhanLoaiForDialog();
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
            protected void paintBorder(Graphics g) {
                // Leave this method empty to prevent border drawing
                // No code here means no border will be drawn
            }
        };
        
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(buttonFont);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void loadNhaCungCapForDialog() {
        List<NhaCungCap> nhaCungCaps = controller.getAllNhaCungCap();
        cmbNhaCungCapDialog.removeAllItems();
        for (NhaCungCap ncc : nhaCungCaps) {
            cmbNhaCungCapDialog.addItem(ncc);
        }
        cmbNhaCungCapDialog.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof NhaCungCap) {
                    value = ((NhaCungCap) value).getTenNCC();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }

    private void loadPhanLoaiForDialog() {
        List<String> phanLoais = controller.getAllPhanLoai();
        cmbPhanLoaiDialog.removeAllItems();
        for (String phanLoai : phanLoais) {
            cmbPhanLoaiDialog.addItem(phanLoai);
        }
        cmbPhanLoaiDialog.setSelectedIndex(-1);
    }

    private void loadData() {
        List<KhoVatTu> danhSachVatTu = controller.getAllKhoVatTu();
        hienThiDanhSachVatTu(danhSachVatTu);
    }

    private void hienThiDanhSachVatTu(List<KhoVatTu> danhSach) {
        tableModel.setRowCount(0);
        if (danhSach.isEmpty()) {
            return;
        }

        String phanLoaiDauTien = danhSach.get(0).getPhanLoai();
        boolean cungPhanLoai = true;
        int tongSoLuong = 0;

        for (KhoVatTu vatTu : danhSach) {
            String tenNCC = controller.getTenNhaCungCap(vatTu.getMaNCC());
            tableModel.addRow(new Object[]{vatTu.getIdVatTu(), vatTu.getMaNCC(), vatTu.getTenVatTu(), vatTu.getSoLuong(), vatTu.getDonViTinh(), tenNCC, vatTu.getPhanLoai()});
            tongSoLuong += vatTu.getSoLuong();
            if (!vatTu.getPhanLoai().equals(phanLoaiDauTien)) {
                cungPhanLoai = false;
            }
        }

        if (cungPhanLoai && !danhSach.isEmpty()) {
            tableModel.addRow(new Object[]{"", "", "Tổng Số Lượng:", tongSoLuong, "", "", phanLoaiDauTien});
        }
    }

    private void clearInputFieldsDialog() {
        txtTenVatTuDialog.setText("");
        txtSoLuongDialog.setText("");
        txtDonViTinhDialog.setText("");
        cmbNhaCungCapDialog.setSelectedIndex(-1);
        cmbPhanLoaiDialog.setSelectedIndex(-1);
    }

    private void timKiemVatTu() {
        String tuKhoa = txtTimKiem.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tblKhoVatTu.setRowSorter(sorter);
        if (tuKhoa.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + tuKhoa));
        }
    }
    private void xemChiTiet() {
        if (vatTuDangChon != null) {
            String maNCC = vatTuDangChon.getMaNCC();
            String tenNCC = controller.getTenNhaCungCap(maNCC);
            String nhaCungCapHienThi = (tenNCC != null && !tenNCC.isEmpty()) ? tenNCC : "Không tìm thấy (Mã: " + maNCC + ")";
            
            JDialog chiTietDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chi tiết vật tư", true);
            chiTietDialog.setLayout(new BorderLayout());
            
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            contentPanel.setBackground(Color.WHITE);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Style for labels
            Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
            Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);
            
            // ID
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
            JLabel lblId = new JLabel("ID:");
            lblId.setFont(labelFont);
            contentPanel.add(lblId, gbc);
            
            gbc.gridx = 1; gbc.weightx = 0.7;
            JLabel valId = new JLabel(String.valueOf(vatTuDangChon.getIdVatTu()));
            valId.setFont(valueFont);
            contentPanel.add(valId, gbc);
            
            // Mã NCC
            gbc.gridx = 0; gbc.gridy = 1;
            JLabel lblMaNcc = new JLabel("Mã NCC:");
            lblMaNcc.setFont(labelFont);
            contentPanel.add(lblMaNcc, gbc);
            
            gbc.gridx = 1;
            JLabel valMaNcc = new JLabel(maNCC);
            valMaNcc.setFont(valueFont);
            contentPanel.add(valMaNcc, gbc);
            
            // Tên
            gbc.gridx = 0; gbc.gridy = 2;
            JLabel lblTen = new JLabel("Tên:");
            lblTen.setFont(labelFont);
            contentPanel.add(lblTen, gbc);
            
            gbc.gridx = 1;
            JLabel valTen = new JLabel(vatTuDangChon.getTenVatTu());
            valTen.setFont(valueFont);
            contentPanel.add(valTen, gbc);
            
            // Số lượng
            gbc.gridx = 0; gbc.gridy = 3;
            JLabel lblSoLuong = new JLabel("Số lượng:");
            lblSoLuong.setFont(labelFont);
            contentPanel.add(lblSoLuong, gbc);
            
            gbc.gridx = 1;
            JLabel valSoLuong = new JLabel(String.valueOf(vatTuDangChon.getSoLuong()));
            valSoLuong.setFont(valueFont);
            contentPanel.add(valSoLuong, gbc);
            
            // Đơn vị tính
            gbc.gridx = 0; gbc.gridy = 4;
            JLabel lblDvt = new JLabel("Đơn vị tính:");
            lblDvt.setFont(labelFont);
            contentPanel.add(lblDvt, gbc);
            
            gbc.gridx = 1;
            JLabel valDvt = new JLabel(vatTuDangChon.getDonViTinh());
            valDvt.setFont(valueFont);
            contentPanel.add(valDvt, gbc);
            
            // Nhà cung cấp
            gbc.gridx = 0; gbc.gridy = 5;
            JLabel lblNcc = new JLabel("Nhà cung cấp:");
            lblNcc.setFont(labelFont);
            contentPanel.add(lblNcc, gbc);
            
            gbc.gridx = 1;
            JLabel valNcc = new JLabel(nhaCungCapHienThi);
            valNcc.setFont(valueFont);
            contentPanel.add(valNcc, gbc);
            
            // Phân loại
            gbc.gridx = 0; gbc.gridy = 6;
            JLabel lblPhanLoai = new JLabel("Phân loại:");
            lblPhanLoai.setFont(labelFont);
            contentPanel.add(lblPhanLoai, gbc);
            
            gbc.gridx = 1;
            JLabel valPhanLoai = new JLabel(vatTuDangChon.getPhanLoai());
            valPhanLoai.setFont(valueFont);
            contentPanel.add(valPhanLoai, gbc);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton btnDong = createRoundedButton("Đóng", primaryColor, buttonTextColor, 8);
            btnDong.addActionListener(e -> chiTietDialog.dispose());
            buttonPanel.add(btnDong);
            
            chiTietDialog.add(contentPanel, BorderLayout.CENTER);
            chiTietDialog.add(buttonPanel, BorderLayout.SOUTH);
            chiTietDialog.pack();
            chiTietDialog.setSize(400, 350);
            chiTietDialog.setLocationRelativeTo(this);
            chiTietDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một vật tư.", 
                "Cảnh báo", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    private void hienThiDialogThemSua(KhoVatTu vatTu) {
        clearInputFieldsDialog();
        vatTuDangChon = vatTu;
        if (vatTu != null) {
            themSuaDialog.setTitle("Chỉnh sửa Vật Tư");
            txtTenVatTuDialog.setText(vatTu.getTenVatTu());
            txtSoLuongDialog.setText(String.valueOf(vatTu.getSoLuong()));
            txtDonViTinhDialog.setText(vatTu.getDonViTinh());
            // Chọn nhà cung cấp
            for (int i = 0; i < cmbNhaCungCapDialog.getItemCount(); i++) {
                if (cmbNhaCungCapDialog.getItemAt(i).getMaNCC().equals(vatTu.getMaNCC())) {
                    cmbNhaCungCapDialog.setSelectedIndex(i);
                    break;
                }
            }
            cmbPhanLoaiDialog.setSelectedItem(vatTu.getPhanLoai());
        } else {
            themSuaDialog.setTitle("Thêm mới Vật Tư");
        }
        themSuaDialog.setVisible(true);
    }

    private void luuVatTu() {
        String tenVatTu = txtTenVatTuDialog.getText().trim();
        String soLuongStr = txtSoLuongDialog.getText().trim();
        String donViTinh = txtDonViTinhDialog.getText().trim();
        NhaCungCap selectedNCC = (NhaCungCap) cmbNhaCungCapDialog.getSelectedItem();
        String phanLoai = (String) cmbPhanLoaiDialog.getSelectedItem();

        if (tenVatTu.isEmpty() || soLuongStr.isEmpty() || donViTinh.isEmpty() || selectedNCC == null || phanLoai == null) {
            JOptionPane.showMessageDialog(themSuaDialog, 
                "Vui lòng nhập đầy đủ thông tin.", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int soLuong = Integer.parseInt(soLuongStr);
            if (soLuong < 0) {
                JOptionPane.showMessageDialog(themSuaDialog, 
                    "Số lượng không được nhỏ hơn 0.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            KhoVatTu vatTuMoi = new KhoVatTu(
                vatTuDangChon != null ? vatTuDangChon.getIdVatTu() : 0,
                tenVatTu,
                soLuong,
                donViTinh,
                selectedNCC.getMaNCC(),
                phanLoai
            );

            boolean success;
            if (vatTuDangChon == null) { // Thêm mới
                success = controller.addKhoVatTu(vatTuMoi);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Thêm vật tư thành công.");
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm vật tư thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else { // Chỉnh sửa
                success = controller.updateKhoVatTu(vatTuMoi);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Cập nhật vật tư thành công.");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật vật tư thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                vatTuDangChon = null;
            }
            
            if (success) {
                loadData();
                themSuaDialog.setVisible(false);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(themSuaDialog, 
                "Số lượng phải là số nguyên.", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaVatTu() {
        if (vatTuDangChon != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa vật tư này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.deleteKhoVatTu(vatTuDangChon.getIdVatTu())) {
                    JOptionPane.showMessageDialog(this, "Xóa vật tư thành công.");
                    loadData();
                    vatTuDangChon = null;
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa vật tư thất bại.", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một vật tư để xóa.", 
                "Cảnh báo", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnThemMoi) {
            hienThiDialogThemSua(null);
        } else if (e.getSource() == btnLuuDialog) {
            luuVatTu();
        } else if (e.getSource() == btnHuyDialog) {
            themSuaDialog.setVisible(false);
            vatTuDangChon = null;
        } else if (e.getSource() == menuItemXemChiTiet) {
            xemChiTiet();
        } else if (e.getSource() == menuItemChinhSua) {
            if (vatTuDangChon != null) {
                hienThiDialogThemSua(vatTuDangChon);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn một vật tư để chỉnh sửa.", 
                    "Cảnh báo", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == menuItemXoa) {
            xoaVatTu();
        } else if (e.getSource() == btnTimKiem) {
            timKiemVatTu();
        } else if (e.getSource() == btnXuatFile) {
            exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor);
        }
    }
    
    // Inner class cho panel với góc bo tròn
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private boolean hasShadow;
        private int shadowSize = 6;
        private int shadowOpacity = 15;
        
        public RoundedPanel(int radius, boolean shadow) {
            super();
            this.cornerRadius = radius;
            this.hasShadow = shadow;
            setOpaque(false);
            // Add some padding to account for the shadow
            if (shadow) {
                setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth() - (shadowSize * 2);
            int height = getHeight() - (shadowSize * 2);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw shadow if enabled
            if (hasShadow) {
                // Create a softer, more realistic shadow using multiple layers
                for (int i = 0; i < shadowSize; i++) {
                    int opacity = shadowOpacity - (i * 2);
                    if (opacity < 0) opacity = 0;
                    
                    graphics.setColor(new Color(0, 0, 0, opacity));
                    graphics.fillRoundRect(
                        shadowSize - i, // X position gets closer as shadow fades
                        shadowSize - i, // Y position gets closer as shadow fades
                        width + (i * 2), // Width increases as shadow spreads
                        height + (i * 2), // Height increases as shadow spreads
                        arcs.width + i, 
                        arcs.height + i
                    );
                }
            }
            
            // Draw panel background
            graphics.setColor(getBackground());
            graphics.fillRoundRect(shadowSize, shadowSize, width, height, arcs.width, arcs.height);
            
            // Remove this border drawing code to eliminate the black outline
            // Only draw border if explicitly specified and not null
            if (getBorder() != null && !(getBorder() instanceof EmptyBorder)) {
                graphics.setColor(getForeground());
                graphics.drawRoundRect(shadowSize, shadowSize, width, height, arcs.width, arcs.height);
            }
        }
    }
    
    // Inner class cho border tùy chỉnh với góc bo tròn
    class CustomBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color color;
        
        public CustomBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 8;
            insets.top = insets.bottom = 4;
            return insets;
        }
    }
    
    // Hàm main để chạy thử ứng dụng
    public static void main(String[] args) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Kho Vật Tư");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setMinimumSize(new Dimension(800, 500));
            frame.setLocationRelativeTo(null);
            frame.setIconImage(new ImageIcon("icon/warehouse.png").getImage());
            frame.add(new KhoVatTuUI());
            frame.setVisible(true);
        });
    }

	@Override
	public void showSuccessToast(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showErrorMessage(String title, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showMessage(String message, String title, int messageType) {
		// TODO Auto-generated method stub
		
	}
}
           