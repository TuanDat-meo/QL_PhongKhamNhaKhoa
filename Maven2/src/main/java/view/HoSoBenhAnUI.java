package view;

import controller.HoSoBenhAnController;
import controller.BenhNhanController;
import controller.DonThuocController;
import model.HoSoBenhAn;
import util.ExportManager;
import view.DoanhThuUI.NotificationType;
import model.BenhNhan;
import model.DonThuoc;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private ThemHoSoBenhAnDialog themHoSoDialog;
    private Map<String, Integer> tenBenhNhanToId;
    private ExportManager exportManager;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemSua;
    private JMenuItem menuItemXoa;
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

        // Header Panel with Title and Search
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // Table Panel
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        setupEventListeners();
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        loadDanhSachBenhNhan();
        
        // Create dialog 
        try {
            themHoSoDialog = new ThemHoSoBenhAnDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm Hồ sơ Bệnh án",
                true,
                hoSoBenhAnController,
                benhNhanController,
                tenBenhNhanToId,
                this
            );
        } catch (Exception e) {
            // Handle the exception when windowAncestor is null (happens during initial loading)
            SwingUtilities.invokeLater(() -> {
                try {
                    themHoSoDialog = new ThemHoSoBenhAnDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this),
                        "Thêm Hồ sơ Bệnh án",
                        true,
                        hoSoBenhAnController,
                        benhNhanController,
                        tenBenhNhanToId,
                        this
                    );
                } catch (Exception ex) {
                    showErrorDialog("Lỗi khởi tạo", "Không thể tạo dialog thêm hồ sơ bệnh án: " + ex.getMessage());
                }
            });
        }
        
        // Load initial data
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

        // Search panel on the right - MODIFY THIS SECTION
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        
        // Update to match DoanhThuUI styling
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
        for (int i = 0; i < hoSoBenhAnTable.getColumnCount(); i++) {
            hoSoBenhAnTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        hoSoBenhAnTable.setFont(tableFont);
        hoSoBenhAnTable.setRowHeight(40);
        hoSoBenhAnTable.setShowGrid(false);
        hoSoBenhAnTable.setIntercellSpacing(new Dimension(0, 0));
        hoSoBenhAnTable.setSelectionBackground(new Color(229, 243, 255));
        hoSoBenhAnTable.setSelectionForeground(textColor);
        hoSoBenhAnTable.setFocusable(false);
        hoSoBenhAnTable.setAutoCreateRowSorter(true);
        hoSoBenhAnTable.setBorder(null);

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
        setupPopupMenu();


        JScrollPane scrollPane = new JScrollPane(hoSoBenhAnTable);
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
        btnThem.addActionListener(e -> openThemHoSoDialog());
        
        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThem);

        return buttonPanel;
    }
    private void setupEventListeners() {
        btnTimKiem.addActionListener(e -> {
            if (txtTimKiem.getText().trim().isEmpty()) {
                // If search text is empty, refresh the list
                lamMoiDanhSach();
                showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            } else {
                // If there's search text, perform search
                timKiemHoSoBenhAn();
            }
        });        
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (txtTimKiem.getText().trim().isEmpty()) {
                        // If search text is empty, refresh the list
                        lamMoiDanhSach();
                        showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
                    } else {
                        // If there's search text, perform search
                        timKiemHoSoBenhAn();
                    }
                }
            }
        });
        
        // Handle table mouse events for popup menu and double-click
        hoSoBenhAnTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = hoSoBenhAnTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < hoSoBenhAnTable.getRowCount()) {
                    hoSoBenhAnTable.setRowSelectionInterval(row, row);
                    
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        xemChiTietHoSoBenhAn();
                    }
                } else {
                    hoSoBenhAnTable.clearSelection();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                int row = hoSoBenhAnTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < hoSoBenhAnTable.getRowCount()) {
                    hoSoBenhAnTable.setRowSelectionInterval(row, row);
                    
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
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

    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(regularFont);
        menuItem.setBackground(panelColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return menuItem;
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

    public void lamMoiDanhSach() {
        hoSoBenhAnTableModel.setRowCount(0);
        try {
            List<HoSoBenhAn> danhSachHoSoBenhAn = hoSoBenhAnController.layDanhSachHoSoBenhAn();
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
        } catch (Exception e) {
            showErrorMessage("Lỗi dữ liệu", "Không thể tải danh sách hồ sơ bệnh án: " + e.getMessage());
        }
    }

    private void timKiemHoSoBenhAn() {
        String searchText = txtTimKiem.getText().toLowerCase();
        hoSoBenhAnTableModel.setRowCount(0);
        
        try {
            List<HoSoBenhAn> danhSachHoSoBenhAn = hoSoBenhAnController.layDanhSachHoSoBenhAn();

            List<HoSoBenhAn> danhSachTimKiem = danhSachHoSoBenhAn.stream()
                    .filter(hsbA -> {
                        BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hsbA.getIdBenhNhan());
                        String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "";
                        return String.valueOf(hsbA.getIdHoSo()).toLowerCase().contains(searchText) ||
                               tenBenhNhan.toLowerCase().contains(searchText) ||
                               hsbA.getChuanDoan().toLowerCase().contains(searchText) ||
                               hsbA.getTrangThai().toLowerCase().contains(searchText);
                    })
                    .collect(Collectors.toList());

            for (HoSoBenhAn hsbA : danhSachTimKiem) {
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
            
            if (danhSachTimKiem.isEmpty()) {
            	showNotification("Không tìm thấy kết quả nào cho: '" + searchText + "'", NotificationType.WARNING);
            }
        } catch (Exception e) {
            showErrorMessage("Lỗi tìm kiếm", "Không thể thực hiện tìm kiếm: " + e.getMessage());
        }
    }

    private void openThemHoSoDialog() {
        if (themHoSoDialog != null) {
            themHoSoDialog.setVisible(true);
        } else {
            try {
                themHoSoDialog = new ThemHoSoBenhAnDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Thêm Hồ sơ Bệnh án",
                    true,
                    hoSoBenhAnController,
                    benhNhanController,
                    tenBenhNhanToId,
                    this
                );
                themHoSoDialog.setVisible(true);
            } catch (Exception e) {
                showErrorMessage("Lỗi", "Không thể mở form thêm hồ sơ: " + e.getMessage());
            }
        }
    }

    private void hienThiDialogSuaHoSoBenhAn() {
        int selectedRow = hoSoBenhAnTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Vui lòng chọn một hồ sơ bệnh án để sửa.");
            return;
        }

        try {
            int idHoSo = (int) hoSoBenhAnTableModel.getValueAt(selectedRow, 0);
            HoSoBenhAn hoSoBenhAnCanSua = hoSoBenhAnController.timKiemHoSoBenhAnTheoId(idHoSo);

            if (hoSoBenhAnCanSua != null) {
                BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hoSoBenhAnCanSua.getIdBenhNhan());
                String tenBenhNhanHienTai = (benhNhan != null) ? benhNhan.getHoTen() : "";

                SuaHoSoBenhAnDialog suaDialog = new SuaHoSoBenhAnDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Sửa Hồ sơ Bệnh án",
                    true,
                    hoSoBenhAnController,
                    benhNhanController,
                    tenBenhNhanToId,
                    this,
                    idHoSo,
                    tenBenhNhanHienTai,
                    hoSoBenhAnCanSua.getChuanDoan(),
                    hoSoBenhAnCanSua.getGhiChu(),
                    hoSoBenhAnCanSua.getNgayTao(),
                    hoSoBenhAnCanSua.getTrangThai()
                );
                suaDialog.setVisible(true);
            } else {
                showErrorMessage("Lỗi dữ liệu", "Không tìm thấy hồ sơ bệnh án để sửa.");
            }
        } catch (Exception e) {
            showErrorMessage("Lỗi", "Không thể mở form sửa hồ sơ: " + e.getMessage());
        }
    }

    private void xoaHoSoBenhAn() {
        int selectedRow = hoSoBenhAnTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Vui lòng chọn một hồ sơ bệnh án để xóa.");
            return;
        }

        try {
            int idHoSo = (int) hoSoBenhAnTableModel.getValueAt(selectedRow, 0);
            String tenBenhNhan = (String) hoSoBenhAnTableModel.getValueAt(selectedRow, 1);
            
            // Create a custom confirmation dialog
            JDialog confirmDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Xác nhận xóa", true);
            confirmDialog.setSize(400, 200);
            confirmDialog.setLocationRelativeTo(this);
            confirmDialog.setLayout(new BorderLayout());
            
            JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            contentPanel.setBackground(Color.WHITE);
            
            JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
            
            JPanel messagePanel = new JPanel(new BorderLayout());
            messagePanel.setBackground(Color.WHITE);
            
            JLabel titleLabel = new JLabel("Xác nhận xóa");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(accentColor);
            
            JLabel messageLabel = new JLabel("<html>Bạn có chắc chắn muốn xóa hồ sơ bệnh án của <b>" + tenBenhNhan + "</b>?</html>");
            messageLabel.setFont(regularFont);
            
            messagePanel.add(titleLabel, BorderLayout.NORTH);
            messagePanel.add(messageLabel, BorderLayout.CENTER);
            
            JPanel iconContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            iconContainer.setBackground(Color.WHITE);
            iconContainer.add(iconLabel);
            
            contentPanel.add(iconContainer, BorderLayout.WEST);
            contentPanel.add(messagePanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            JButton btnCancel = createRoundedButton("Hủy", new Color(153, 153, 153), Color.WHITE, 8);
            btnCancel.addActionListener(e -> confirmDialog.dispose());
            
            JButton btnConfirm = createRoundedButton("Xóa", accentColor, Color.WHITE, 8);
            btnConfirm.addActionListener(e -> {
                try {
                    hoSoBenhAnController.xoaHoSoBenhAn(idHoSo);
                    lamMoiDanhSach();
                    confirmDialog.dispose();
                    showSuccessMessage("Xóa hồ sơ bệnh án thành công.");
                } catch (Exception ex) {
                    showErrorMessage("Lỗi xóa", "Không thể xóa hồ sơ bệnh án: " + ex.getMessage());
                }
            });
            
            buttonPanel.add(btnCancel);
            buttonPanel.add(btnConfirm);
            
            confirmDialog.add(contentPanel, BorderLayout.CENTER);
            confirmDialog.add(buttonPanel, BorderLayout.SOUTH);
            confirmDialog.setVisible(true);
            
        } catch (Exception e) {
            showErrorMessage("Lỗi", "Không thể xóa hồ sơ bệnh án: " + e.getMessage());
        }
    }

    public boolean xemChiTietHoSoBenhAn(int idHoSo) {
        try {
            // Tìm kiếm hồ sơ bệnh án theo ID
            HoSoBenhAn hoSoBenhAn = hoSoBenhAnController.timKiemHoSoBenhAnTheoId(idHoSo);
            
            if (hoSoBenhAn == null) {
                showErrorMessage("Lỗi dữ liệu", "Không tìm thấy hồ sơ bệnh án.");
                return false;
            }
            
            // Tìm thông tin bệnh nhân
            BenhNhan benhNhan = benhNhanController.timKiemBenhNhanTheoId(hoSoBenhAn.getIdBenhNhan());
            String tenBenhNhan = (benhNhan != null) ? benhNhan.getHoTen() : "N/A";
            
            // Lấy danh sách đơn thuốc
            List<DonThuoc> danhSachDonThuoc = donThuocController.layDanhSachDonThuocTheoHoSoBenhAnId(idHoSo);
            
            // Tạo và hiển thị dialog chi tiết
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
                danhSachDonThuoc
            );
            
            chiTietDialog.setVisible(true);
            return true;
        } catch (NullPointerException e) {
            showErrorMessage("Lỗi dữ liệu", "Dữ liệu hồ sơ bệnh án không đầy đủ: " + e.getMessage());
            return false;
        } catch (ClassCastException e) {
            showErrorMessage("Lỗi dữ liệu", "Lỗi chuyển đổi kiểu dữ liệu: " + e.getMessage());
            return false;
        } catch (Exception e) {
            showErrorMessage("Lỗi", "Không thể mở chi tiết hồ sơ bệnh án: " + e.getMessage());
            e.printStackTrace(); // Ghi log lỗi để thuận tiện cho việc debug
            return false;
        }
    }
    private void xemChiTietHoSoBenhAn() {
        int selectedRow = hoSoBenhAnTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Vui lòng chọn một hồ sơ bệnh án để xem chi tiết.");
            return;
        }

        // Lấy ID hồ sơ từ hàng đã chọn
        int idHoSo = (int) hoSoBenhAnTableModel.getValueAt(selectedRow, 0);
        xemChiTietHoSoBenhAn(idHoSo);
    }
 // Helper method to format Date objects to String
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
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    // Custom rounded border implementation
    private class CustomBorder extends AbstractBorder {
        private int radius;
        private Color borderColor;
        
        public CustomBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius * 2, radius * 2);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = radius;
            return insets;
        }
    }

    // Custom panel with rounded corners and optional shadow
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

    // Methods for showing different message dialogs
    private void showSuccessMessage(String message) {
        JOptionPane optionPane = new JOptionPane(
            message,
            JOptionPane.INFORMATION_MESSAGE
        );
        JDialog dialog = optionPane.createDialog("Thành công");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
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

        // Position at bottom right
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toastDialog.setLocation(
            screenSize.width - toastDialog.getWidth() - 20,
            screenSize.height - toastDialog.getHeight() - 60
        );

        toastDialog.setVisible(true);

        // Auto-hide after 3 seconds
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

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(this),
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }

    // Implementation of ExportManager.MessageCallback interface
    @Override
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(this),
            message,
            title,
            messageType
        );
    }

	@Override
	public void showSuccessToast(String message) {
		// TODO Auto-generated method stub
		
	}
}
                		    