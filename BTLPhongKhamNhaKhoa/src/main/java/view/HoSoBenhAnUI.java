package view;

import controller.HoSoBenhAnController;
import controller.BenhNhanController;
import controller.DonThuocController;
import model.HoSoBenhAn;
import model.RoundedPanel;
import model.BacSi;
import model.BenhNhan;
import model.CustomBorder;
import model.DonThuoc;
import model.ExportManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HoSoBenhAnUI extends JPanel implements ExportManager.MessageCallback {
    private HoSoBenhAnController hoSoBenhAnController;
    private BenhNhanController benhNhanController;
    private DonThuocController donThuocController;
    private DefaultTableModel hoSoBenhAnTableModel;
    private JTable hoSoBenhAnTable;
    private JTextField txtTimKiem;
    private JButton btnThem;
    private JButton btnTimKiem;
    private JButton btnXuatFile;
    private Map<String, Integer> tenBenhNhanToId;
    private ExportManager exportManager;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemSua;
    private JMenuItem menuItemXoa;    
    private JLabel lblSoHoSo;
    // Dialog components
    private JDialog inputDialog;
    private JComboBox<String> cbBenhNhan;
    private JTextField txtChuanDoan;
    private JTextArea txtGhiChu;
    private JDateChooser dateChooserNgayTao;
    private JComboBox<String> cbTrangThai;
    private Map<JComponent, JLabel> errorLabels = new HashMap<>();
    private boolean isEditMode = false;
    private int currentEditingId = -1;
    private Timer highlightTimer;
    private int highlightedRowId = -1;
    private Color highlightColor = new Color(237, 187, 85);
    private MouseWheelListener scrollListener;
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
    // Font settings
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font errorFont = new Font("Segoe UI", Font.ITALIC, 11);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);

    public HoSoBenhAnUI() throws SQLException {
        this.hoSoBenhAnController = new HoSoBenhAnController();
        this.benhNhanController = new BenhNhanController();
        this.donThuocController = new DonThuocController();
        initialize();
        exportManager = new ExportManager(this, hoSoBenhAnTableModel, null);
    }
    private void initialize() {
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(backgroundColor);
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        setupScrollListener();
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
        
        setupEventListeners();
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        loadDanhSachBenhNhan();      
        createInputDialog();
        setupScrollListener();
        lamMoiDanhSach();
    }
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));        
        // Title panel on the left
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ HỒ SƠ BỆNH ÁN");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        // Search panel on the right
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        
        txtTimKiem = new JTextField(18);
        txtTimKiem.setFont(regularFont);
        txtTimKiem.setPreferredSize(new Dimension(220, 38));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(5, borderColor), 
                BorderFactory.createEmptyBorder(5, 10, 5, 5)));                
        txtTimKiem.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemHoSoBenhAn();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        btnTimKiem = createRoundedButton("Tìm kiếm", primaryColor, buttonTextColor, 10);
        btnTimKiem.setPreferredSize(new Dimension(120, 38));
        btnTimKiem.addActionListener(e -> timKiemHoSoBenhAn());

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
        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create table model and table
        hoSoBenhAnTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        hoSoBenhAnTableModel.addColumn("ID HS");
        hoSoBenhAnTableModel.addColumn("Tên BN");
        hoSoBenhAnTableModel.addColumn("Chuẩn đoán");
        hoSoBenhAnTableModel.addColumn("Ghi chú");
        hoSoBenhAnTableModel.addColumn("Ngày tạo");
        hoSoBenhAnTableModel.addColumn("Trạng thái");

        hoSoBenhAnTable = new JTable(hoSoBenhAnTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);                
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                }                
                
                // Kiểm tra xem có phải hàng cần highlight không
                int modelRow = convertRowIndexToModel(row);
                int rowId = (Integer) hoSoBenhAnTableModel.getValueAt(modelRow, 0);
                
                // ƯU TIÊN HIGHLIGHT HƠN SELECTION
                if (highlightedRowId > 0 && rowId == highlightedRowId) {
                    comp.setBackground(highlightColor); 
                    comp.setForeground(textColor); 
                } else if (isRowSelected(row)) {
                    // Chỉ áp dụng màu selection khi hàng được chọn và không phải highlight
                    comp.setBackground(getSelectionBackground());
                    comp.setForeground(getSelectionForeground());
                } else {
                    // Màu thông thường cho những hàng khác
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                    comp.setForeground(textColor);
                }
                
                return comp;
            }
            
            // Loại bỏ focus border
            @Override
            public boolean isFocusable() {
                return true;
            }
            
            // Override để loại bỏ focus painting
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
            
            // Tắt focus border
            @Override
            public void paintBorder(Graphics g) {
                // Không vẽ border khi focus
            }
        };        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Luôn đặt hasFocus = false để tắt focus border
                Component comp = super.getTableCellRendererComponent(table, value, 
                        isSelected, false, row, column);
                
                // Center align text
                ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                
                // Apply color logic
                int modelRow = table.convertRowIndexToModel(row);
                int rowId = (Integer) hoSoBenhAnTableModel.getValueAt(modelRow, 0);
                
                if (highlightedRowId > 0 && rowId == highlightedRowId) {
                    comp.setBackground(highlightColor); 
                    comp.setForeground(textColor); 
                } else if (isSelected) {
                    comp.setBackground(table.getSelectionBackground());
                    comp.setForeground(table.getSelectionForeground());
                } else {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                    comp.setForeground(textColor);
                }
                
                return comp;
            }
        };
        
        // Apply center renderer to all columns
        for (int i = 0; i < hoSoBenhAnTable.getColumnCount(); i++) {
            hoSoBenhAnTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        hoSoBenhAnTable.setFont(tableFont);
        hoSoBenhAnTable.setRowHeight(40);
        hoSoBenhAnTable.setShowGrid(false);
        hoSoBenhAnTable.setIntercellSpacing(new Dimension(0, 0));
        hoSoBenhAnTable.setSelectionBackground(new Color(229, 243, 255));
        hoSoBenhAnTable.setSelectionForeground(textColor);
        hoSoBenhAnTable.setFocusable(true); // Cho phép table nhận focus
        hoSoBenhAnTable.setAutoCreateRowSorter(true);
        hoSoBenhAnTable.setBorder(null);
        hoSoBenhAnTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component comp = super.getTableCellRendererComponent(table, value, 
                        isSelected, false, row, column); // Đặt hasFocus = false
                // Center align text
                ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);                
                // Apply your existing color logic
                int modelRow = table.convertRowIndexToModel(row);
                int rowId = (Integer) hoSoBenhAnTableModel.getValueAt(modelRow, 0);
                
                if (highlightedRowId > 0 && rowId == highlightedRowId) {
                    comp.setBackground(highlightColor); 
                    comp.setForeground(textColor); 
                } else if (isSelected) {
                    comp.setBackground(table.getSelectionBackground());
                    comp.setForeground(table.getSelectionForeground());
                } else {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                    comp.setForeground(textColor);
                }                
                return comp;
            }
        });   
        hoSoBenhAnTable.setUI(new javax.swing.plaf.basic.BasicTableUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);
            }
        });
        // Cải thiện việc quản lý selection
        hoSoBenhAnTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hoSoBenhAnTable.setRowSelectionAllowed(true);
        hoSoBenhAnTable.setColumnSelectionAllowed(false);
       
        // Style table header
        JTableHeader header = hoSoBenhAnTable.getTableHeader();
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
        TableColumnModel columnModel = hoSoBenhAnTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50); // ID HS
        columnModel.getColumn(1).setPreferredWidth(150); // Tên BN
        columnModel.getColumn(2).setPreferredWidth(150); // Chuẩn đoán
        columnModel.getColumn(3).setPreferredWidth(200); // Ghi chú
        columnModel.getColumn(4).setPreferredWidth(100); // Ngày tạo
        columnModel.getColumn(5).setPreferredWidth(100); // Trạng thái
        
        // Thêm KeyListener để xử lý phím tắt
        hoSoBenhAnTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int selectedRow = hoSoBenhAnTable.getSelectedRow();
                if (selectedRow == -1) return;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        // Xem chi tiết khi nhấn Enter hoặc Space
                        xemChiTietHoSoBenhAn();
                        break;
                    case KeyEvent.VK_F2:
                        // Sửa khi nhấn F2
                        if (e.isControlDown()) {
                            hienThiDialogSuaHoSoBenhAn();
                        }
                        break;
                    case KeyEvent.VK_DELETE:
                        // Xóa khi nhấn Delete (với xác nhận)
                        if (e.isControlDown()) {
                            xoaHoSoBenhAn();
                        }
                        break;
                }
            }
        });
        
        // Thêm FocusListener để xử lý focus events
        hoSoBenhAnTable.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // Khi table nhận focus, đảm bảo có hàng được chọn
                if (hoSoBenhAnTable.getSelectedRow() == -1 && hoSoBenhAnTable.getRowCount() > 0) {
                    hoSoBenhAnTable.setRowSelectionInterval(0, 0);
                }
                hoSoBenhAnTable.repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                // Giữ nguyên selection khi mất focus
                hoSoBenhAnTable.repaint();
            }
        });
        
        setupPopupMenu();
        JScrollPane scrollPane = new JScrollPane(hoSoBenhAnTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.addMouseWheelListener(scrollListener);
        
        // Cải thiện focus management cho scroll pane
        scrollPane.setFocusable(false); // Không cho scroll pane nhận focus
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        hoSoBenhAnTable.putClientProperty("JTable.focusCellBackground", new Color(229, 243, 255));
        hoSoBenhAnTable.putClientProperty("JTable.focusCellForeground", textColor);
        hoSoBenhAnTable.putClientProperty("JTable.focusCellHighlightBorder", null);

        return wrapperPanel;
    }
    private void createInputDialog() {
        Color requiredFieldColor = new Color(255, 0, 0);        
        inputDialog = new JDialog();
        inputDialog.setTitle("Thông tin hồ sơ bệnh án");
        inputDialog.setModal(true);
        inputDialog.setSize(520, 550); // Tăng chiều cao để có thêm không gian
        inputDialog.setLocationRelativeTo(null);
        inputDialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(18, 25, 18, 25));
        
        JLabel titleLabel = new JLabel("THÊM MỚI HỒ SƠ BỆNH ÁN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;        
        // Bệnh nhân field
        JLabel lblBenhNhan = new JLabel("Bệnh nhân: ");
        lblBenhNhan.setFont(regularFont);
        JPanel benhNhanLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        benhNhanLabelPanel.setBackground(Color.WHITE);
        benhNhanLabelPanel.add(lblBenhNhan);
        JLabel starBenhNhan = new JLabel("*");
        starBenhNhan.setForeground(requiredFieldColor);
        starBenhNhan.setFont(regularFont);
        benhNhanLabelPanel.add(starBenhNhan);
        formPanel.add(benhNhanLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        cbBenhNhan = createStyledComboBox();
        cbBenhNhan.setPreferredSize(new Dimension(270, 32));
        cbBenhNhan.setFocusable(false);
        formPanel.add(cbBenhNhan, gbc);
        
        // Error label for Bệnh nhân
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblBenhNhanError = createErrorLabel();
        formPanel.add(lblBenhNhanError, gbc);
        errorLabels.put(cbBenhNhan, lblBenhNhanError);
        
        // Chuẩn đoán field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblChuanDoan = new JLabel("Chuẩn đoán: ");
        lblChuanDoan.setFont(regularFont);
        JPanel chuanDoanLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chuanDoanLabelPanel.setBackground(Color.WHITE);
        chuanDoanLabelPanel.add(lblChuanDoan);
        JLabel starChuanDoan = new JLabel("*");
        starChuanDoan.setForeground(requiredFieldColor);
        starChuanDoan.setFont(regularFont);
        chuanDoanLabelPanel.add(starChuanDoan);
        formPanel.add(chuanDoanLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtChuanDoan = createStyledTextField();
        txtChuanDoan.setPreferredSize(new Dimension(270, 32));
        formPanel.add(txtChuanDoan, gbc);        
        // Error label for Chuẩn đoán
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblChuanDoanError = createErrorLabel();
        formPanel.add(lblChuanDoanError, gbc);
        errorLabels.put(txtChuanDoan, lblChuanDoanError);
        
        // Ghi chú field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblGhiChu = new JLabel("Ghi chú: ");
        lblGhiChu.setFont(regularFont);
        formPanel.add(lblGhiChu, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtGhiChu = createStyledTextArea();
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        scrollGhiChu.setPreferredSize(new Dimension(270, 80));
        scrollGhiChu.setBorder(new CustomBorder(8, borderColor));
        formPanel.add(scrollGhiChu, gbc);
        
        // Error label for Ghi chú
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblGhiChuError = createErrorLabel();
        formPanel.add(lblGhiChuError, gbc);
        errorLabels.put(txtGhiChu, lblGhiChuError);
        
        // Ngày tạo field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblNgayTao = new JLabel("Ngày tạo: ");
        lblNgayTao.setFont(regularFont);
        JPanel ngayTaoLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ngayTaoLabelPanel.setBackground(Color.WHITE);
        ngayTaoLabelPanel.add(lblNgayTao);
        JLabel starNgayTao = new JLabel("*");
        starNgayTao.setForeground(requiredFieldColor);
        starNgayTao.setFont(regularFont);
        ngayTaoLabelPanel.add(starNgayTao);
        formPanel.add(ngayTaoLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dateChooserNgayTao = createStyledDateChooser();
        dateChooserNgayTao.setPreferredSize(new Dimension(270, 32));
        dateChooserNgayTao.setDate(new Date()); // Set to current date by default
        formPanel.add(dateChooserNgayTao, gbc);        
        
        // Error label for Ngày tạo
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblNgayTaoError = createErrorLabel();
        formPanel.add(lblNgayTaoError, gbc);
        errorLabels.put(dateChooserNgayTao, lblNgayTaoError);        
        
        // Trạng thái field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblTrangThai = new JLabel("Trạng thái: ");
        lblTrangThai.setFont(regularFont);
        formPanel.add(lblTrangThai, gbc);        
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        String[] trangThais = {"Đang điều trị", "Đã xuất viện", "Chuyển viện"};
        cbTrangThai = new JComboBox<>(trangThais);
        cbTrangThai.setFont(regularFont);
        cbTrangThai.setPreferredSize(new Dimension(270, 32));
        formPanel.add(cbTrangThai, gbc);   
        
        // No error label needed for trạng thái combobox
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        formPanel.add(Box.createVerticalStrut(10), gbc);
        
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
        btnLuu.addActionListener(e -> luuHoSoBenhAn());

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

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        inputDialog.setContentPane(mainPanel);        
        inputDialog.getRootPane().setDefaultButton(btnLuu);        
        // Set up Enter key navigation
        setupEnterKeyNavigation();
    }    
    private void luuHoSoBenhAn() {
        // Clear all previous validation errors
        clearValidationError(cbBenhNhan);
        clearValidationError(txtChuanDoan);
        clearValidationError(txtGhiChu);
        clearValidationError(dateChooserNgayTao);
        
        boolean isValid = true;
        
        // Validate Bệnh nhân
        if (cbBenhNhan.getSelectedIndex() <= 0) {
            showValidationError(cbBenhNhan, "Vui lòng chọn bệnh nhân");
            if (isValid) {
                cbBenhNhan.requestFocus();
                isValid = false;
            }
        }        
        // Validate Chuẩn đoán
        String chuanDoan = txtChuanDoan.getText().trim();
        if (chuanDoan.isEmpty()) {
            showValidationError(txtChuanDoan, "Vui lòng nhập chuẩn đoán");
            if (isValid) {
                txtChuanDoan.requestFocus();
                isValid = false;
            }
        } else if (chuanDoan.length() > 500) {
            showValidationError(txtChuanDoan, "Chuẩn đoán không được vượt quá 500 ký tự");
            if (isValid) {
                txtChuanDoan.requestFocus();
                isValid = false;
            }
        }        
        // Validate Ngày tạo
        Date ngayTao = dateChooserNgayTao.getDate();
        if (ngayTao == null) {
            showValidationError(dateChooserNgayTao, "Vui lòng chọn ngày tạo");
            if (isValid) {
                dateChooserNgayTao.requestFocus();
                isValid = false;
            }
        } else if (ngayTao.after(new Date())) {
            showValidationError(dateChooserNgayTao, "Ngày tạo không được sau ngày hiện tại");
            if (isValid) {
                dateChooserNgayTao.requestFocus();
                isValid = false;
            }
        }
        // Validate Ghi chú (optional but check length if provided)
        String ghiChu = txtGhiChu.getText().trim();
        if (ghiChu.length() > 1000) {
            showValidationError(txtGhiChu, "Ghi chú không được vượt quá 1000 ký tự");
            if (isValid) {
                txtGhiChu.requestFocus();
                isValid = false;
            }
        }        
        if (!isValid) {
            return;
        }        
        try {
            String selectedBenhNhan = (String) cbBenhNhan.getSelectedItem();
            Integer idBenhNhan = tenBenhNhanToId.get(selectedBenhNhan);
            
            if (idBenhNhan == null) {
                showValidationError(cbBenhNhan, "Bệnh nhân không hợp lệ");
                cbBenhNhan.requestFocus();
                return;
            }
            String trangThai = (String) cbTrangThai.getSelectedItem();            
            java.sql.Date sqlDate = new java.sql.Date(ngayTao.getTime());            
            // Sử dụng biến isEditMode thay vì kiểm tra title
            int savedHoSoId = -1;
            if (isEditMode && currentEditingId != -1) {
            	// Chế độ chỉnh sửa
                HoSoBenhAn hoSoBenhAn = new HoSoBenhAn();
                hoSoBenhAn.setIdHoSo(currentEditingId);
                hoSoBenhAn.setIdBenhNhan(idBenhNhan);
                hoSoBenhAn.setChuanDoan(chuanDoan);
                hoSoBenhAn.setGhiChu(ghiChu);
                hoSoBenhAn.setNgayTao(sqlDate);
                hoSoBenhAn.setTrangThai(trangThai);
                
                // Update
                hoSoBenhAnController.suaHoSoBenhAn(hoSoBenhAn);
                savedHoSoId = currentEditingId;
                showNotification("Hồ sơ bệnh án đã được cập nhật thành công!", NotificationType.SUCCESS);
            } else {
                // Chế độ thêm mới
                HoSoBenhAn hoSoBenhAn = new HoSoBenhAn();
                hoSoBenhAn.setIdBenhNhan(idBenhNhan);
                hoSoBenhAn.setChuanDoan(chuanDoan);
                hoSoBenhAn.setGhiChu(ghiChu);
                hoSoBenhAn.setNgayTao(sqlDate);
                hoSoBenhAn.setTrangThai(trangThai);
                
                // Add new
                hoSoBenhAnController.themHoSoBenhAn(hoSoBenhAn);
                savedHoSoId = idBenhNhan;
                showNotification("Hồ sơ bệnh án mới đã được thêm thành công!", NotificationType.SUCCESS);
            }            
            // Reset trạng thái và refresh table
            isEditMode = false;
            currentEditingId = -1;
            inputDialog.setVisible(false);            
            // Kiểm tra xem có đang trong chế độ tìm kiếm không
            String searchText = txtTimKiem.getText().trim();
            if (searchText.isEmpty()) {
                // Highlight hồ sơ vừa lưu
                if (savedHoSoId > 0) {
                    lamMoiDanhSach(savedHoSoId);
                } else {
                    lamMoiDanhSach();
                }
            } else {
                timKiemHoSoBenhAn(); // Cập nhật kết quả tìm kiếm
            }       
        } catch (Exception e) {
            showErrorMessage("Lỗi", "Không thể lưu hồ sơ bệnh án: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void highlightHoSoBenhAn(int hoSoId) {
        highlightHoSoBenhAn(hoSoId, 10); // Default 10 seconds
    }
    public void highlightHoSoBenhAn(int hoSoId, int durationSeconds) {
        lamMoiDanhSachWithCustomDuration(hoSoId, durationSeconds);
    }
    public void lamMoiDanhSach() {
        lamMoiDanhSach(-1); // Gọi method với highlightId = -1 để không highlight
    }
    private void showInputDialog(boolean isThem) {
        resetAllValidationErrors();
        Container contentPane = inputDialog.getContentPane();
        JPanel mainPanel = (JPanel) contentPane;
        JPanel headerPanel = (JPanel) mainPanel.getComponent(0);
        JLabel headerTitle = (JLabel) headerPanel.getComponent(0);        
        if (!isThem) {
            int selectedRow = hoSoBenhAnTable.getSelectedRow();
            if (selectedRow == -1) {
                showWarningMessage("Vui lòng chọn hồ sơ bệnh án để sửa.");
                return;
            }            
            // Đặt chế độ chỉnh sửa
            isEditMode = true;
            currentEditingId = (int) hoSoBenhAnTableModel.getValueAt(selectedRow, 0);
            
            inputDialog.setTitle("Chỉnh sửa hồ sơ bệnh án");
            headerTitle.setText("CHỈNH SỬA HỒ SƠ BỆNH ÁN");

            // Lấy dữ liệu từ hàng được chọn
            String tenBenhNhan = (String) hoSoBenhAnTableModel.getValueAt(selectedRow, 1);
            String chuanDoan = (String) hoSoBenhAnTableModel.getValueAt(selectedRow, 2);
            String ghiChu = (String) hoSoBenhAnTableModel.getValueAt(selectedRow, 3);
            String ngayTaoStr = (String) hoSoBenhAnTableModel.getValueAt(selectedRow, 4);
            String trangThai = (String) hoSoBenhAnTableModel.getValueAt(selectedRow, 5);
            // Load danh sách bệnh nhân trước khi set giá trị
            loadBenhNhanToComboBox();            
            // Đặt giá trị cho các field
            cbBenhNhan.setSelectedItem(tenBenhNhan);
            txtChuanDoan.setText(chuanDoan);
            txtGhiChu.setText(ghiChu);
            cbTrangThai.setSelectedItem(trangThai);            
            // Đặt ngày tạo
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngayTao = sdf.parse(ngayTaoStr);
                dateChooserNgayTao.setDate(ngayTao);
            } catch (Exception e) {
                showErrorMessage("Lỗi định dạng ngày", "Không thể đọc định dạng ngày tháng");
            }
        } else {
            // Đặt chế độ thêm mới
            isEditMode = false;
            currentEditingId = -1;
            
            inputDialog.setTitle("Thêm hồ sơ bệnh án mới");
            headerTitle.setText("THÊM MỚI HỒ SƠ BỆNH ÁN");
            clearInputFields();
            loadBenhNhanToComboBox();
        }
        inputDialog.setLocationRelativeTo(this);
        inputDialog.setVisible(true);
    }    
    private void setupScrollListener() {
        Timer scrollEndTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (highlightedRowId > 0) {
                    highlightedRowId = -1;
                    hoSoBenhAnTable.repaint();                    
                    // Tải lại dữ liệu theo thứ tự bình thường
                    SwingUtilities.invokeLater(() -> {
                        lamMoiDanhSach(); // Load lại dữ liệu bình thường
                    });                    
                    // Dừng timer highlight chính nếu đang chạy
                    if (highlightTimer != null && highlightTimer.isRunning()) {
                        highlightTimer.stop();
                    }
                }
            }
        });
        scrollEndTimer.setRepeats(false);
        
        scrollListener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Chỉ restart timer, không reset highlight ngay lập tức
                if (scrollEndTimer.isRunning()) {
                    scrollEndTimer.stop();
                }
                scrollEndTimer.start();
            }
        };
    }
    private void setupEventListeners() {
        btnTimKiem.addActionListener(e -> {
            if (txtTimKiem.getText().trim().isEmpty()) {
                lamMoiDanhSach();
                showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            } else {
                timKiemHoSoBenhAn();
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
                        timKiemHoSoBenhAn();
                    }
                }
            }
        });        
        
        // Cải thiện mouse event handling
        hoSoBenhAnTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = hoSoBenhAnTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < hoSoBenhAnTable.getRowCount()) {
                    // Chọn hàng trước khi xử lý các event khác
                    hoSoBenhAnTable.setRowSelectionInterval(row, row);
                    
                    // Request focus để đảm bảo table có focus
                    if (!hoSoBenhAnTable.hasFocus()) {
                        hoSoBenhAnTable.requestFocusInWindow();
                    }
                    
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else {
                    // Click vào vùng trống - clear selection nhưng giữ focus
                    hoSoBenhAnTable.clearSelection();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = hoSoBenhAnTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < hoSoBenhAnTable.getRowCount()) {
                    if (e.isPopupTrigger()) {
                        hoSoBenhAnTable.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = hoSoBenhAnTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < hoSoBenhAnTable.getRowCount()) {
                    hoSoBenhAnTable.setRowSelectionInterval(row, row);
                    
                    if (e.getClickCount() == 2) {
                        // Double click để xem chi tiết
                        xemChiTietHoSoBenhAn();
                    }
                }
            }
        });
        
        // Thêm selection listener để xử lý thay đổi selection
        hoSoBenhAnTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Selection đã thay đổi và ổn định
                int selectedRow = hoSoBenhAnTable.getSelectedRow();
                if (selectedRow >= 0) {
                    // Có thể thêm logic xử lý khi selection thay đổi
                    // Ví dụ: cập nhật status bar, enable/disable buttons, etc.
                }
            }
        });
    }
    public void hienThiDialogSuaHoSoBenhAn() {
        int selectedRow = hoSoBenhAnTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Vui lòng chọn một hồ sơ bệnh án để sửa.");
            return;
        }        
        showInputDialog(false);
    }
    public void hienThiDialogSuaHoSoBenhAnTheoId(int idHoSo) {
        try {
            HoSoBenhAn hoSoBenhAnCanSua = hoSoBenhAnController.timKiemHoSoBenhAnTheoId(idHoSo);
            if (hoSoBenhAnCanSua != null) {
                // Tìm và chọn hàng tương ứng trong bảng
                for (int i = 0; i < hoSoBenhAnTableModel.getRowCount(); i++) {
                    int idInTable = (int) hoSoBenhAnTableModel.getValueAt(i, 0);
                    if (idInTable == idHoSo) {
                        hoSoBenhAnTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }                
                // Gọi showInputDialog với tham số false (không phải thêm mới)
                showInputDialog(false);
            } else {
                showErrorMessage("Lỗi dữ liệu", "Không tìm thấy hồ sơ bệnh án để sửa.");
            }
        } catch (Exception e) {
            showErrorMessage("Lỗi", "Không thể mở form sửa hồ sơ: " + e.getMessage());
        }
    }    
    private void loadDanhSachBenhNhan() {
        try {
            List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
            tenBenhNhanToId = danhSachBenhNhan.stream()
                    .collect(Collectors.toMap(BenhNhan::getHoTen, BenhNhan::getIdBenhNhan));
        } catch (SQLException e) {
            showErrorMessage("Lỗi dữ liệu", "Không thể tải danh sách bệnh nhân: " + e.getMessage());
        }
    }
    private void lamMoiDanhSach(int highlightId) {
        try {
            // Dừng timer cũ nếu đang chạy
            if (highlightTimer != null && highlightTimer.isRunning()) {
                highlightTimer.stop();
            }
            
            // Reset highlight state
            highlightedRowId = -1;
            
            List<HoSoBenhAn> danhSachHoSoBenhAn = hoSoBenhAnController.layDanhSachHoSoBenhAn();
            hoSoBenhAnTableModel.setRowCount(0);
            
            HoSoBenhAn highlightedHoSo = null;
            List<HoSoBenhAn> otherHoSo = new ArrayList<>();
            
            // Tách hồ sơ cần highlight và các hồ sơ khác
            if (highlightId > 0) {
                for (HoSoBenhAn hsbA : danhSachHoSoBenhAn) {
                    if (hsbA.getIdHoSo() == highlightId) {
                        highlightedHoSo = hsbA;
                    } else {
                        otherHoSo.add(hsbA);
                    }
                }
            }
            
            // Load dữ liệu vào table
            if (highlightId > 0 && highlightedHoSo != null) {
                // Thêm hồ sơ được highlight vào đầu bảng
                BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(highlightedHoSo.getIdBenhNhan());
                String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "N/A";
                Object[] rowData = {
                    highlightedHoSo.getIdHoSo(),
                    tenBenhNhan,
                    highlightedHoSo.getChuanDoan(),
                    highlightedHoSo.getGhiChu(),
                    formatDate(highlightedHoSo.getNgayTao()),
                    highlightedHoSo.getTrangThai()
                };
                hoSoBenhAnTableModel.addRow(rowData);
                
                // Thêm các hồ sơ khác
                for (HoSoBenhAn hsbA : otherHoSo) {
                    BenhNhan bn = benhNhanController.timKiemBenhNhanTheoId(hsbA.getIdBenhNhan());
                    String tenBN = (bn != null) ? bn.getHoTen() : "N/A";
                    Object[] otherRowData = {
                        hsbA.getIdHoSo(),
                        tenBN,
                        hsbA.getChuanDoan(),
                        hsbA.getGhiChu(),
                        formatDate(hsbA.getNgayTao()),
                        hsbA.getTrangThai()
                    };
                    hoSoBenhAnTableModel.addRow(otherRowData);
                }
            } else {
                // Load tất cả dữ liệu theo thứ tự bình thường
                for (HoSoBenhAn hsbA : danhSachHoSoBenhAn) {
                    BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hsbA.getIdBenhNhan());
                    String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "N/A";
                    Object[] rowData = {
                        hsbA.getIdHoSo(),
                        tenBenhNhan,
                        hsbA.getChuanDoan(),
                        hsbA.getGhiChu(),
                        formatDate(hsbA.getNgayTao()),
                        hsbA.getTrangThai()
                    };
                    hoSoBenhAnTableModel.addRow(rowData);
                }
            }
            
            // Cập nhật số lượng hồ sơ
            int soLuongHoSo = danhSachHoSoBenhAn.size();
            lblSoHoSo.setText("Tổng số hồ sơ: " + soLuongHoSo);
            
            // Đảm bảo table được update hoàn toàn
            hoSoBenhAnTableModel.fireTableDataChanged();
            
            // Nếu có ID cần highlight, thực hiện highlight
            if (highlightId > 0 && highlightedHoSo != null) {
                // Set highlight ID TRƯỚC KHI thực hiện UI operations
                highlightedRowId = highlightId;
                
                SwingUtilities.invokeLater(() -> {
                    // Scroll về đầu bảng trước
                    JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, hoSoBenhAnTable);
                    if (scrollPane != null) {
                        JViewport viewport = scrollPane.getViewport();
                        viewport.setViewPosition(new Point(0, 0));
                    }
                    
                    // Select hàng đầu tiên
                    if (hoSoBenhAnTable.getRowCount() > 0) {
                        hoSoBenhAnTable.setRowSelectionInterval(0, 0);
                    }
                    
                    // Force repaint để hiển thị highlight
                    hoSoBenhAnTable.repaint();
                    
                    // Tạo timer để tắt highlight sau 10 giây
                    highlightTimer = new Timer(10000, e -> {
                        highlightedRowId = -1;
                        hoSoBenhAnTable.repaint();
                        // Tự động tải lại dữ liệu theo thứ tự bình thường
                        SwingUtilities.invokeLater(() -> lamMoiDanhSach());
                    });
                    highlightTimer.setRepeats(false);
                    highlightTimer.start();
                });
            }
            
        } catch (Exception e) {
            showErrorMessage("Lỗi dữ liệu", "Không thể tải danh sách hồ sơ bệnh án: " + e.getMessage());
            lblSoHoSo.setText("Tổng số hồ sơ: 0");
        }
    }
    private void lamMoiDanhSachWithCustomDuration(int highlightId, int durationSeconds) {
        try {
            if (highlightTimer != null && highlightTimer.isRunning()) {
                highlightTimer.stop();
            }
            
            // Reset highlight state
            highlightedRowId = -1;
            
            List<HoSoBenhAn> danhSachHoSoBenhAn = hoSoBenhAnController.layDanhSachHoSoBenhAn();
            hoSoBenhAnTableModel.setRowCount(0);
            
            HoSoBenhAn highlightedHoSo = null;
            List<HoSoBenhAn> otherHoSo = new ArrayList<>();
            
            // Tách hồ sơ cần highlight
            if (highlightId > 0) {
                for (HoSoBenhAn hsbA : danhSachHoSoBenhAn) {
                    if (hsbA.getIdHoSo() == highlightId) {
                        highlightedHoSo = hsbA;
                    } else {
                        otherHoSo.add(hsbA);
                    }
                }
            }
            
            // Load data
            if (highlightedHoSo != null) {
                // Add highlighted record first
                BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(highlightedHoSo.getIdBenhNhan());
                String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "N/A";
                Object[] rowData = {
                    highlightedHoSo.getIdHoSo(),
                    tenBenhNhan,
                    highlightedHoSo.getChuanDoan(),
                    highlightedHoSo.getGhiChu(),
                    formatDate(highlightedHoSo.getNgayTao()),
                    highlightedHoSo.getTrangThai()
                };
                hoSoBenhAnTableModel.addRow(rowData);
                
                // Add other records
                for (HoSoBenhAn hsbA : otherHoSo) {
                    BenhNhan bn = benhNhanController.timKiemBenhNhanTheoId(hsbA.getIdBenhNhan());
                    String tenBN = (bn != null) ? bn.getHoTen() : "N/A";
                    Object[] otherRowData = {
                        hsbA.getIdHoSo(),
                        tenBN,
                        hsbA.getChuanDoan(),
                        hsbA.getGhiChu(),
                        formatDate(hsbA.getNgayTao()),
                        hsbA.getTrangThai()
                    };
                    hoSoBenhAnTableModel.addRow(otherRowData);
                }
                
                // Set highlight
                highlightedRowId = highlightId;
                hoSoBenhAnTableModel.fireTableDataChanged();
                
                SwingUtilities.invokeLater(() -> {
                    // Scroll to top
                    JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, hoSoBenhAnTable);
                    if (scrollPane != null) {
                        scrollPane.getViewport().setViewPosition(new Point(0, 0));
                    }
                    
                    // Select first row
                    if (hoSoBenhAnTable.getRowCount() > 0) {
                        hoSoBenhAnTable.setRowSelectionInterval(0, 0);
                    }
                    
                    hoSoBenhAnTable.repaint();
                    
                    // Custom duration timer
                    highlightTimer = new Timer(durationSeconds * 1000, e -> {
                        highlightedRowId = -1;
                        hoSoBenhAnTable.repaint();
                        SwingUtilities.invokeLater(() -> lamMoiDanhSach());
                    });
                    highlightTimer.setRepeats(false);
                    highlightTimer.start();
                });
            }
            
            // Cập nhật số lượng
            int soLuongHoSo = danhSachHoSoBenhAn.size();
            lblSoHoSo.setText("Tổng số hồ sơ: " + soLuongHoSo);
            
        } catch (Exception e) {
            showErrorMessage("Lỗi dữ liệu", "Không thể tải danh sách hồ sơ bệnh án: " + e.getMessage());
            lblSoHoSo.setText("Tổng số hồ sơ: 0");
        }
    }
    private void timKiemHoSoBenhAn() {
        String searchText = txtTimKiem.getText().trim();        
        // Nếu ô tìm kiếm trống, làm mới danh sách
        if (searchText.isEmpty()) {
            lamMoiDanhSach();
            showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            return;
        }        
        hoSoBenhAnTableModel.setRowCount(0);        
        try {
            List<HoSoBenhAn> danhSachHoSoBenhAn = hoSoBenhAnController.layDanhSachHoSoBenhAn();
            List<HoSoBenhAn> danhSachTimKiem = danhSachHoSoBenhAn.stream()
                    .filter(hsbA -> {
                        try {
                            BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hsbA.getIdBenhNhan());
                            String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "";
                            return String.valueOf(hsbA.getIdHoSo()).toLowerCase().contains(searchText.toLowerCase()) ||
                                   tenBenhNhan.toLowerCase().contains(searchText.toLowerCase()) ||
                                   hsbA.getChuanDoan().toLowerCase().contains(searchText.toLowerCase()) ||
                                   hsbA.getTrangThai().toLowerCase().contains(searchText.toLowerCase()) ||
                                   (hsbA.getGhiChu() != null && hsbA.getGhiChu().toLowerCase().contains(searchText.toLowerCase()));
                        } catch (Exception e) {
                            return false; // Nếu có lỗi khi tìm bệnh nhân, bỏ qua record này
                        }
                    })
                    .collect(Collectors.toList());
            for (HoSoBenhAn hsbA : danhSachTimKiem) {
                try {
                    BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hsbA.getIdBenhNhan());
                    String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "N/A";
                    Object[] rowData = {
                        hsbA.getIdHoSo(), 
                        tenBenhNhan, 
                        hsbA.getChuanDoan(), 
                        hsbA.getGhiChu(), 
                        formatDate(hsbA.getNgayTao()), 
                        hsbA.getTrangThai()
                    };
                    hoSoBenhAnTableModel.addRow(rowData);
                } catch (Exception e) {
                    // Log error và tiếp tục với record tiếp theo
                    System.err.println("Lỗi khi xử lý hồ sơ ID: " + hsbA.getIdHoSo() + " - " + e.getMessage());
                }
            }                        
            // Cập nhật số lượng kết quả tìm kiếm
            int soKetQua = danhSachTimKiem.size();
            lblSoHoSo.setText("Kết quả tìm kiếm: " + soKetQua + " hồ sơ");            
            // Cập nhật hiển thị bảng
            hoSoBenhAnTable.revalidate();
            hoSoBenhAnTable.repaint();            
            if (soKetQua == 0) {
                showNotification("Không tìm thấy kết quả nào cho: '" + searchText + "'", NotificationType.WARNING);
            } else {
                showNotification("Tìm thấy " + soKetQua + " kết quả phù hợp", NotificationType.SUCCESS);
            }            
        } catch (Exception e) {
            showErrorMessage("Lỗi tìm kiếm", "Không thể thực hiện tìm kiếm: " + e.getMessage());
            lblSoHoSo.setText("Lỗi: 0 hồ sơ");
        }
    }
    private JPanel createButtonPanel() {
    	JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(backgroundColor);
        
        lblSoHoSo = new JLabel("Tổng số hồ sơ: 0");
        lblSoHoSo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSoHoSo.setForeground(primaryColor);
        leftPanel.add(lblSoHoSo);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(backgroundColor);
        
        btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10, false);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor));
        
        btnThem = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10, false);
        btnThem.setPreferredSize(new Dimension(100, 45));
        btnThem.addActionListener(e -> showInputDialog(true));
        
        rightPanel.add(btnXuatFile);
        rightPanel.add(btnThem);
        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);
        return buttonPanel;
    }
    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));        
        JMenuItem menuItemXemChiTiet = createStyledMenuItem("Xem Chi Tiết");
        menuItemSua = createStyledMenuItem("Chỉnh Sửa");
        menuItemXoa = createStyledMenuItem("Xóa");        
        menuItemXoa.setForeground(accentColor);        
        popupMenu.add(menuItemXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(menuItemSua);
        popupMenu.addSeparator();
        popupMenu.add(menuItemXoa);
        menuItemXemChiTiet.addActionListener(e -> {
            if (hoSoBenhAnTable.getSelectedRow() != -1) {
                xemChiTietHoSoBenhAn();
            }
        });
        menuItemSua.addActionListener(e -> {
            if (hoSoBenhAnTable.getSelectedRow() != -1) {
                hienThiDialogSuaHoSoBenhAn();
            }
        });        
        menuItemXoa.addActionListener(e -> {
            if (hoSoBenhAnTable.getSelectedRow() != -1) {
                xoaHoSoBenhAn();
            }
        });
        hoSoBenhAnTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }            
            @Override
            public void mousePressed(MouseEvent e) {
                // Make sure the row is selected when right-clicking
                int row = hoSoBenhAnTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < hoSoBenhAnTable.getRowCount()) {
                    hoSoBenhAnTable.setRowSelectionInterval(row, row);
                } else {
                    hoSoBenhAnTable.clearSelection();
                }                
                showPopup(e);
            }            
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger() && hoSoBenhAnTable.getSelectedRow() != -1) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    private void xemChiTietHoSoBenhAn() {
        int selectedRow = hoSoBenhAnTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Vui lòng chọn một hồ sơ bệnh án để xem chi tiết.");
            return;
        }
        try {
            // Lấy ID hồ sơ từ hàng đã chọn
            int idHoSo = (int) hoSoBenhAnTableModel.getValueAt(selectedRow, 0);
            // Tìm kiếm hồ sơ bệnh án theo ID
            HoSoBenhAn hoSoBenhAn = hoSoBenhAnController.timKiemHoSoBenhAnTheoId(idHoSo);
            if (hoSoBenhAn == null) {
                showErrorMessage("Lỗi dữ liệu", "Không tìm thấy hồ sơ bệnh án.");
                return;
            }            
            // Tìm thông tin bệnh nhân
            BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hoSoBenhAn.getIdBenhNhan());
            String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "N/A";           
            // Lấy danh sách đơn thuốc
            List<DonThuoc> danhSachDonThuoc = donThuocController.layDanhSachDonThuocTheoHoSoBenhAnId(idHoSo);
            // Tạo và hiển thị dialog chi tiết với tham chiếu parentUI
            JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
            ChiTietHoSoBenhAnDialog chiTietDialog = new ChiTietHoSoBenhAnDialog(
                owner,
                "Chi tiết Hồ sơ Bệnh án",
                true,
                hoSoBenhAn.getIdHoSo(),
                tenBenhNhan,
                hoSoBenhAn.getChuanDoan(),
                hoSoBenhAn.getGhiChu(),
                formatDate(hoSoBenhAn.getNgayTao()),
                hoSoBenhAn.getTrangThai(),
                danhSachDonThuoc,
                this // Truyền tham chiếu parentUI
            );            
            chiTietDialog.setVisible(true);
        } catch (NullPointerException e) {
            showErrorMessage("Lỗi dữ liệu", "Dữ liệu hồ sơ bệnh án không đầy đủ: " + e.getMessage());
        } catch (ClassCastException e) {
            showErrorMessage("Lỗi dữ liệu", "Lỗi chuyển đổi kiểu dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            showErrorMessage("Lỗi", "Không thể mở chi tiết hồ sơ bệnh án: " + e.getMessage());
            e.printStackTrace(); // Ghi log lỗi để thuận tiện cho việc debug
        }
    }
    private void setupEnterKeyNavigation() {
        JComponent[] components = new JComponent[] {
            cbBenhNhan,
            txtChuanDoan,
            txtGhiChu,
            dateChooserNgayTao.getDateEditor().getUiComponent(),
            cbTrangThai
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
            } else if (components[i] instanceof JComboBox) {
                ((JComboBox<?>) components[i]).addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            components[nextIndex].requestFocus();
                        }
                    }
                });
            } else if (components[i] instanceof JTextArea) {
                ((JTextArea) components[i]).addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                            components[nextIndex].requestFocus();
                        }
                    }
                });
            }
        }        
        // For the last component
        if (components[components.length - 1] instanceof JComboBox) {
            ((JComboBox<?>) components[components.length - 1]).addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        luuHoSoBenhAn();
                    }
                }
            });
        }        
        // Special handling for date chooser
        ((JTextField) dateChooserNgayTao.getDateEditor().getUiComponent()).addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cbTrangThai.requestFocus();
                }
            }
        });
    }
    private void clearInputFields() {
        cbBenhNhan.setSelectedIndex(-1);
        txtChuanDoan.setText("");
        txtGhiChu.setText("");
        dateChooserNgayTao.setDate(new Date());
        cbTrangThai.setSelectedIndex(0);
    }
    private void loadBenhNhanToComboBox() {
        cbBenhNhan.removeAllItems();
        cbBenhNhan.addItem("-- Chọn bệnh nhân --");
        
        try {
            List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
            for (BenhNhan bn : danhSachBenhNhan) {
                cbBenhNhan.addItem(bn.getHoTen());
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi dữ liệu", "Không thể tải danh sách bệnh nhân: " + e.getMessage());
        }
    }
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(regularFont);
        Dimension fieldSize = new Dimension(270, 32); // Đồng bộ kích thước
        textField.setPreferredSize(fieldSize);
        textField.setMinimumSize(fieldSize);
        textField.setMaximumSize(fieldSize);
        textField.setBorder(new CompoundBorder(
                new CustomBorder(8, borderColor),
                new EmptyBorder(5, 12, 5, 12)));
        textField.setBackground(Color.WHITE);
        return textField;
    }
    private JTextArea createStyledTextArea() {
        JTextArea textArea = new JTextArea(3, 20);
        textArea.setFont(regularFont);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(5, 12, 5, 12));
        textArea.setBackground(Color.WHITE);
        return textArea;
    }
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(regularFont);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(270, 32)); // Đồng bộ kích thước
        return comboBox;
    }
    private JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(regularFont);
        dateChooser.setPreferredSize(new Dimension(270, 32)); // Đồng bộ kích thước
        dateChooser.setBorder(new CustomBorder(8, borderColor));
        dateChooser.setDateFormatString("dd/MM/yyyy");        
        JTextField dateTextField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateTextField.setFont(regularFont);
        dateTextField.setBorder(new EmptyBorder(5, 12, 5, 12));        
        return dateChooser;
    }
    private JLabel createErrorLabel() {
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(errorFont);
        errorLabel.setForeground(errorColor);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        errorLabel.setVisible(true);
        errorLabel.setPreferredSize(new Dimension(270, 16)); // Đồng bộ kích thước
        errorLabel.setMinimumSize(new Dimension(270, 16));
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
        } else if (component instanceof JTextArea) {
            JScrollPane parent = (JScrollPane) component.getParent().getParent();
            parent.setBorder(new LineBorder(errorColor, 1, true));
        }
        
        JLabel errorLabel = errorLabels.get(component);
        if (errorLabel != null) {
            errorLabel.setText("<html><div style='width: 245px;'>" + message + "</div></html>");
            errorLabel.setVisible(true);
        }
    }
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setBackground(panelColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return menuItem;
    }
    private void resetAllValidationErrors() {
        clearValidationError(cbBenhNhan);
        clearValidationError(txtChuanDoan);
        clearValidationError(txtGhiChu);
        clearValidationError(dateChooserNgayTao);
        
        for (JLabel errorLabel : errorLabels.values()) {
            errorLabel.setText(" ");
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
        } else if (component instanceof JTextArea) {
            JScrollPane parent = (JScrollPane) component.getParent().getParent();
            parent.setBorder(new CustomBorder(8, borderColor));
        }        
        JLabel errorLabel = errorLabels.get(component);
        if (errorLabel != null) {
            errorLabel.setText(" ");
            errorLabel.setVisible(true);
        }
    }
    
    private void xoaHoSoBenhAn() {
        int selectedRow = hoSoBenhAnTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Vui lòng chọn một hồ sơ bệnh án để xóa.");
            return;
        }

        try {
            int modelRow = hoSoBenhAnTable.convertRowIndexToModel(selectedRow);
            int idHoSo = (int) hoSoBenhAnTableModel.getValueAt(modelRow, 0);
            String tenBenhNhan = (String) hoSoBenhAnTableModel.getValueAt(modelRow, 1);

            // Create confirmation dialog similar to xoaBenhNhan
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

            JLabel messageLabel = new JLabel("<html>Bạn có chắc chắn muốn xóa hồ sơ bệnh án của <b>" + tenBenhNhan + "</b>?</html>");
            messageLabel.setFont(regularFont);
            messagePanel.add(messageLabel, BorderLayout.CENTER);

            panel.add(messagePanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(Color.WHITE);

            JButton cancelButton = createRoundedButton("Hủy", new Color(158, 158, 158), Color.WHITE, 8, false);
            cancelButton.addActionListener(e -> confirmDialog.dispose());

            JButton deleteButton = createRoundedButton("Xóa", accentColor, Color.WHITE, 8, false);
            deleteButton.addActionListener(e -> {
                try {
                    hoSoBenhAnController.xoaHoSoBenhAn(idHoSo);
                    confirmDialog.dispose();
                    SwingUtilities.invokeLater(() -> {
                        // Kiểm tra xem có đang trong chế độ tìm kiếm không
                        String searchText = txtTimKiem.getText().trim();
                        if (searchText.isEmpty()) {
                            lamMoiDanhSach();
                        } else {
                            timKiemHoSoBenhAn(); // Cập nhật kết quả tìm kiếm
                        }
                        showSuccessToast("Hồ sơ bệnh án đã được xóa thành công!");
                    });
                } catch (Exception ex) {
                    showErrorMessage("Lỗi khi xóa hồ sơ bệnh án", ex.getMessage());
                }
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(deleteButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);
            confirmDialog.setContentPane(panel);
            confirmDialog.setVisible(true);

        } catch (Exception e) {
            showErrorMessage("Lỗi", "Không thể xóa hồ sơ bệnh án: " + e.getMessage());
        }
    }
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius, boolean reducedPadding) {
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
        // Sử dụng padding khác nhau tùy theo button
        if (reducedPadding) {
            button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // Padding nhỏ hơn cho "Chỉnh Sửa"
        } else {
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Padding bình thường cho "Đóng"
        }

        // Add hover effect
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
    
    private String formatDate(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
    // Create a custom rounded button with specified parameters
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
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
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
    private void showWarningMessage(String message) {
        JOptionPane optionPane = new JOptionPane(
            message,
            JOptionPane.WARNING_MESSAGE
        );
        JDialog dialog = optionPane.createDialog("Cảnh báo");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
    @Override
    public void showErrorMessage(String title, String message) {
        JOptionPane optionPane = new JOptionPane(
            message,
            JOptionPane.ERROR_MESSAGE
        );
        JDialog dialog = optionPane.createDialog(title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
    @Override
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(this), message, title, messageType
        );
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
}
                		    