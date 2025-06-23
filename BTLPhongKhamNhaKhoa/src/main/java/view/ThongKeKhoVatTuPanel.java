package view;

import controller.KhoVatTuController;
import controller.ThongKeKhoVatTuController;
import model.KhoVatTu;
import model.NhaCungCap;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ThongKeKhoVatTuPanel extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);    // Xanh dương đậm
    private static final Color SECONDARY_COLOR = new Color(66, 165, 245);  // Xanh dương nhạt
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Xám nhạt
    private static final Color TEXT_COLOR = new Color(33, 33, 33);         // Đen nhạt
    private static final Color BUTTON_COLOR = new Color(25, 118, 210);     // Xanh dương cho nút
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;            // Chữ trắng cho nút
    private static final Color[] CHART_COLORS = {
        new Color(25, 118, 210),  // Xanh dương
        new Color(244, 67, 54),   // Đỏ
        new Color(76, 175, 80),   // Xanh lá
        new Color(255, 193, 7),   // Vàng
        new Color(156, 39, 176),  // Tím
        new Color(255, 87, 34),   // Cam
        new Color(0, 188, 212),   // Lục lam
        new Color(233, 30, 99),   // Hồng
        new Color(63, 81, 181),   // Indigo
        new Color(139, 195, 74),  // Xanh lá nhạt
        new Color(121, 85, 72)    // Nâu
    };    
    private ThongKeKhoVatTuController controller;    
    private JComboBox<String> cboPhanLoai;
    private JComboBox<String> cboNhaCungCap;
    private JTable tblKetQua;
    private DefaultTableModel modelTable;
    private JPanel pnlChart;
    private JTabbedPane tabbedPane;
    private JLabel lblTongVatTu;
    private JLabel lblTongSoLuong;
    private JTextField txtSoLuongToiThieu;
    private JPanel statPanel;    
    public ThongKeKhoVatTuPanel() {
        controller = new ThongKeKhoVatTuController();
        initComponents();
        setupStyle();
        loadData();
    }    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));        
        JPanel pnlHeader = createHeaderPanel();        
        JPanel pnlControl = createControlPanel();        
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(pnlHeader, BorderLayout.NORTH);
        topPanel.add(pnlControl, BorderLayout.CENTER);        
        add(topPanel, BorderLayout.NORTH);
        createContentPanel();
    }    
    private JPanel createHeaderPanel() {
        JPanel pnlHeader = new JPanel(new BorderLayout(10, 10));
        pnlHeader.setBackground(BACKGROUND_COLOR);        
        JLabel lblTitle = new JLabel("THỐNG KÊ KHO VẬT TƯ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);        
        statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.X_AXIS));
        statPanel.setBackground(Color.WHITE);
        statPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));        
        lblTongVatTu = new JLabel("Tổng số vật tư: 0");
        lblTongVatTu.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongVatTu.setForeground(TEXT_COLOR);        
        lblTongSoLuong = new JLabel("Tổng số lượng: 0");
        lblTongSoLuong.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongSoLuong.setForeground(TEXT_COLOR);        
        statPanel.add(lblTongVatTu);
        statPanel.add(Box.createHorizontalStrut(30));
        statPanel.add(new JSeparator(JSeparator.VERTICAL));
        statPanel.add(Box.createHorizontalStrut(30));
        statPanel.add(lblTongSoLuong);        
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        pnlHeader.add(statPanel, BorderLayout.SOUTH);        
        return pnlHeader;
    }    
    private JPanel createControlPanel() {
        JPanel pnlControl = new JPanel();
        pnlControl.setLayout(new BoxLayout(pnlControl, BoxLayout.Y_AXIS));
        pnlControl.setBackground(Color.WHITE);
        pnlControl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));        
        JPanel pnlFilter = new JPanel(new GridBagLayout());
        pnlFilter.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);        
        JLabel lblPhanLoai = new JLabel("Phân loại:");
        lblPhanLoai.setFont(new Font("Arial", Font.BOLD, 13));
        cboPhanLoai = createStyledComboBox();
        cboPhanLoai.addItem("Tất cả");        
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlFilter.add(lblPhanLoai, gbc);        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        pnlFilter.add(cboPhanLoai, gbc);        
        JLabel lblNhaCungCap = new JLabel("Nhà cung cấp:");
        lblNhaCungCap.setFont(new Font("Arial", Font.BOLD, 13));
        cboNhaCungCap = createStyledComboBox();
        cboNhaCungCap.addItem("Tất cả");        
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        pnlFilter.add(lblNhaCungCap, gbc);        
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        pnlFilter.add(cboNhaCungCap, gbc);        
        JLabel lblSoLuongToiThieu = new JLabel("Số lượng tối thiểu:");
        lblSoLuongToiThieu.setFont(new Font("Arial", Font.BOLD, 13));
        txtSoLuongToiThieu = createStyledTextField();        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        pnlFilter.add(lblSoLuongToiThieu, gbc);        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        pnlFilter.add(txtSoLuongToiThieu, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnLoc = createStyledButton("Lọc");
        btnLoc.addActionListener(e -> filterData());
        
        JButton btnVatTuDuoiNguong = createStyledButton("Vật tư dưới ngưỡng");
        btnVatTuDuoiNguong.addActionListener(e -> showVatTuDuoiNguong());        
        JButton btnRefresh = createStyledButton("Làm mới");
        btnRefresh.addActionListener(e -> {
            txtSoLuongToiThieu.setText("");
            cboPhanLoai.setSelectedIndex(0);
            cboNhaCungCap.setSelectedIndex(0);
            loadData();
        });        
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnVatTuDuoiNguong);
        buttonPanel.add(btnLoc);
        
        pnlControl.add(pnlFilter);
        pnlControl.add(Box.createVerticalStrut(10));
        pnlControl.add(buttonPanel);
        
        cboPhanLoai.addActionListener(e -> {
            if (cboPhanLoai.getSelectedIndex() >= 0) {
                filterByPhanLoai();
            }
        });        
        cboNhaCungCap.addActionListener(e -> {
            if (cboNhaCungCap.getSelectedIndex() >= 0) {
                filterByNhaCungCap();
            }
        });        
        return pnlControl;
    }    
    private void createContentPanel() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setBackground(BACKGROUND_COLOR);
        
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"ID", "Tên vật tư", "Số lượng", "Đơn vị tính", "Nhà cung cấp", "Phân loại"};
        modelTable = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };        
        tblKetQua = new JTable(modelTable);
        setupTableStyle();
        JScrollPane scrollPane = new JScrollPane(tblKetQua);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        pnlTable.add(scrollPane, BorderLayout.CENTER);
        
        JPanel barChartPanel = createBarChart();
        barChartPanel.setBackground(Color.WHITE);
        barChartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel pieChartPanel = createPieChart();
        pieChartPanel.setBackground(Color.WHITE);
        pieChartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tabbedPane.addTab("Bảng dữ liệu", pnlTable);
        tabbedPane.addTab("Biểu đồ cột", barChartPanel);
        tabbedPane.addTab("Biểu đồ tròn", pieChartPanel);
        
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = PRIMARY_COLOR;
                lightHighlight = SECONDARY_COLOR;
                shadow = PRIMARY_COLOR;
                darkShadow = PRIMARY_COLOR;
                focus = SECONDARY_COLOR.darker();
            }
        });        
        add(tabbedPane, BorderLayout.CENTER);
    }    
    private void setupTableStyle() {
        tblKetQua.setRowHeight(30);
        tblKetQua.setShowGrid(true);
        tblKetQua.setGridColor(new Color(230, 230, 230));
        tblKetQua.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblKetQua.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKetQua.setSelectionBackground(new Color(232, 240, 254));
        tblKetQua.setSelectionForeground(TEXT_COLOR);
        JTableHeader header = tblKetQua.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setForeground(Color.WHITE);
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(100, 35));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < tblKetQua.getColumnCount(); i++) {
            tblKetQua.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        tblKetQua.getColumnModel().getColumn(0).setPreferredWidth(60);    // ID
        tblKetQua.getColumnModel().getColumn(1).setPreferredWidth(200);   // Tên
        tblKetQua.getColumnModel().getColumn(2).setPreferredWidth(80);    // Số lượng
        tblKetQua.getColumnModel().getColumn(3).setPreferredWidth(80);    // Đơn vị
        tblKetQua.getColumnModel().getColumn(4).setPreferredWidth(150);   // NCC
        tblKetQua.getColumnModel().getColumn(5).setPreferredWidth(100);   // Phân loại
        
        tblKetQua.setFont(new Font("Arial", Font.PLAIN, 13));
    }
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        // Sử dụng viền mặc định của JComboBox nhưng đổi màu thành đen
        comboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        // Đặt kích thước bằng với ô Số lượng tối thiểu (100, 30)
        comboBox.setPreferredSize(new Dimension(100, 30));
        return comboBox;
    }
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setPreferredSize(new Dimension(100, 30));
        return textField;
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 20, 35));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_COLOR.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });        
        return button;
    }    
    private void setupStyle() {
        UIManager.put("ComboBox.selectionBackground", SECONDARY_COLOR);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("TextField.caretForeground", PRIMARY_COLOR);
        UIManager.put("TextField.selectionBackground", SECONDARY_COLOR);
        UIManager.put("TextField.selectionForeground", Color.WHITE);
    }    
    private void loadData() {
        try {
            int tongVatTu = controller.getTongSoVatTu();
            int tongSoLuong = controller.getTongSoLuongVatTu();
            lblTongVatTu.setText("Tổng số vật tư: " + tongVatTu);
            lblTongSoLuong.setText("Tổng số lượng: " + tongSoLuong);
            
            List<String> danhSachPhanLoai = controller.getAllPhanLoai();
            cboPhanLoai.removeAllItems();
            cboPhanLoai.addItem("Tất cả");
            for (String phanLoai : danhSachPhanLoai) {
                cboPhanLoai.addItem(phanLoai);
            }
            List<NhaCungCap> danhSachNCC = controller.getAllNhaCungCap();
            cboNhaCungCap.removeAllItems();
            cboNhaCungCap.addItem("Tất cả");
            for (NhaCungCap ncc : danhSachNCC) {
                cboNhaCungCap.addItem(ncc.getTenNCC());
            }            
            KhoVatTuController khoController = new KhoVatTuController();
            updateTableData(khoController.getAllKhoVatTu());            
            updateCharts();
        } catch (Exception ex) {
            showNotification("Lỗi khi tải dữ liệu: " + ex.getMessage(), NotificationType.ERROR);
        }
    }    
    private void updateTableData(List<KhoVatTu> danhSachVatTu) {
        modelTable.setRowCount(0);
        if (danhSachVatTu != null) {
            for (KhoVatTu vatTu : danhSachVatTu) {
                String tenNCC = controller.getTenNhaCungCap(vatTu.getMaNCC());
                modelTable.addRow(new Object[]{
                    vatTu.getIdVatTu(),
                    vatTu.getTenVatTu(),
                    vatTu.getSoLuong(),
                    vatTu.getDonViTinh(),
                    tenNCC,
                    vatTu.getPhanLoai()
                });
            }
        }
        if (danhSachVatTu == null || danhSachVatTu.isEmpty()) {
            modelTable.addRow(new Object[]{"", "Không có dữ liệu", "", "", "", ""});
        }
    }    
    private void filterData() {
        try {
            String phanLoai = cboPhanLoai.getSelectedIndex() > 0 ? cboPhanLoai.getSelectedItem().toString() : null;
            String tenNCC = cboNhaCungCap.getSelectedIndex() > 0 ? cboNhaCungCap.getSelectedItem().toString() : null;
            
            int soLuongToiThieu = 0;
            try {
                if (!txtSoLuongToiThieu.getText().trim().isEmpty()) {
                    soLuongToiThieu = Integer.parseInt(txtSoLuongToiThieu.getText().trim());
                }
            } catch (NumberFormatException e) {
                showNotification("Số lượng tối thiểu phải là số nguyên!", NotificationType.ERROR);
                return;
            }
            List<KhoVatTu> ketQuaLoc = locVatTu(phanLoai, tenNCC, soLuongToiThieu);
            
            updateTableData(ketQuaLoc);
            if (ketQuaLoc.isEmpty()) {
                showNotification("Không tìm thấy vật tư thỏa mãn điều kiện lọc!", NotificationType.WARNING);
            } else {
                showNotification("Đã lọc được " + ketQuaLoc.size() + " vật tư thỏa mãn điều kiện!", NotificationType.SUCCESS);
            }
        } catch (Exception ex) {
            showNotification("Lỗi khi lọc dữ liệu: " + ex.getMessage(), NotificationType.ERROR);
        }
    }
    private List<KhoVatTu> locVatTu(String phanLoai, String tenNCC, int soLuongToiThieu) {
        KhoVatTuController khoController = new KhoVatTuController();
        List<KhoVatTu> allVatTu = khoController.getAllKhoVatTu();
        List<KhoVatTu> ketQua = new ArrayList<>();        
        String maNCC = null;
        if (tenNCC != null) {
            maNCC = khoController.getMaNhaCungCapTheoTen(tenNCC);
        }        
        for (KhoVatTu vatTu : allVatTu) {
            boolean matchPhanLoai = phanLoai == null || vatTu.getPhanLoai().equals(phanLoai);
            boolean matchNCC = maNCC == null || vatTu.getMaNCC().equals(maNCC);
            boolean matchSoLuong = vatTu.getSoLuong() >= soLuongToiThieu;
            
            if (matchPhanLoai && matchNCC && matchSoLuong) {
                ketQua.add(vatTu);
            }
        }        
        return ketQua;
    }    
    private void filterByPhanLoai() {
        try {
            String phanLoai = cboPhanLoai.getSelectedItem().toString();
            if (phanLoai.equals("Tất cả")) {
                KhoVatTuController khoController = new KhoVatTuController();
                updateTableData(khoController.getAllKhoVatTu());
            } else {
                List<KhoVatTu> danhSachVatTu = controller.getVatTuTheoPhanLoai(phanLoai);
                updateTableData(danhSachVatTu);
            }
        } catch (Exception ex) {
            showNotification("Lỗi khi lọc theo phân loại: " + ex.getMessage(), NotificationType.ERROR);
        }
    }    
    private void filterByNhaCungCap() {
        try {
            String tenNCC = cboNhaCungCap.getSelectedItem().toString();
            if (tenNCC.equals("Tất cả")) {
                KhoVatTuController khoController = new KhoVatTuController();
                updateTableData(khoController.getAllKhoVatTu());
                return;
            }
            KhoVatTuController khoController = new KhoVatTuController();
            String maNCC = khoController.getMaNhaCungCapTheoTen(tenNCC);            
            if (maNCC != null) {
                List<KhoVatTu> danhSachVatTu = controller.getVatTuTheoNCC(maNCC);
                updateTableData(danhSachVatTu);
            }
        } catch (Exception ex) {
            showNotification("Lỗi khi lọc theo nhà cung cấp: " + ex.getMessage(), NotificationType.ERROR);
        }
    }    
    private void showVatTuDuoiNguong() {
        try {
            int soLuongToiThieu = 10; 
            try {
                if (!txtSoLuongToiThieu.getText().trim().isEmpty()) {
                    soLuongToiThieu = Integer.parseInt(txtSoLuongToiThieu.getText().trim());
                }
            } catch (NumberFormatException e) {
                showNotification("Số lượng tối thiểu phải là số nguyên!", NotificationType.ERROR);
                return;
            }
            
            List<KhoVatTu> danhSachVatTu = controller.getVatTuDuoiNguong(soLuongToiThieu);
            updateTableData(danhSachVatTu);
            
            showNotification("Có " + danhSachVatTu.size() + " vật tư có số lượng dưới " + soLuongToiThieu, NotificationType.WARNING);
        } catch (Exception ex) {
            showNotification("Lỗi khi hiển thị vật tư dưới ngưỡng: " + ex.getMessage(), NotificationType.ERROR);
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
    private JPanel createBarChart() {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                Map<String, Integer> thongKe = controller.getTongSoLuongTheoPhanLoai();
                if (thongKe.isEmpty()) return;
                int padding = 50;
                int labelPadding = 20;
                int width = getWidth() - 2 * padding;
                int height = getHeight() - 2 * padding;
                int bottom = height + padding;
                int barWidth = width / (thongKe.size() * 2);
                int maxValue = 0;
                for (Integer value : thongKe.values()) {
                    if (value > maxValue) {
                        maxValue = value;
                    }
                }
                if (maxValue == 0) maxValue = 1;                
                // Vẽ tiêu đề
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("Thống kê số lượng vật tư theo phân loại", 
                    getWidth() / 2 - 150, 30);                
                // Vẽ trục và nhãn
                g2d.setColor(Color.BLACK);
                g2d.drawLine(padding, padding, padding, bottom);
                g2d.drawLine(padding, bottom, padding + width, bottom);                
                // Vẽ các cột
                int x = padding + barWidth;
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                
                // Điều chỉnh khoảng cách giữa các cột
                int barSpacing = Math.max(barWidth / 2, 20); // Đảm bảo khoảng cách tối thiểu giữa các cột
                
                for (Map.Entry<String, Integer> entry : thongKe.entrySet()) {
                    String phanLoai = entry.getKey();
                    int value = entry.getValue();                    
                    // Tính chiều cao của cột
                    int barHeight = (int) ((double) value / maxValue * height);
                    // Vẽ cột
                    g2d.setColor(new Color(44, 102, 230));
                    g2d.fillRect(x, bottom - barHeight, barWidth, barHeight);                    
                    // Vẽ khung cột
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, bottom - barHeight, barWidth, barHeight);                    
                    // Vẽ giá trị
                    g2d.drawString(String.valueOf(value), x + barWidth/2 - g2d.getFontMetrics().stringWidth(String.valueOf(value))/2, 
                                  bottom - barHeight - 5);
                    
                    // Vẽ nhãn phân loại với khả năng xuống dòng
                    drawWrappedText(g2d, phanLoai, x, bottom + labelPadding, barWidth);
                    
                    x += (barWidth + barSpacing);
                }
            }
            
            // Hàm để vẽ text có thể xuống dòng nếu quá dài
            private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
                FontMetrics fm = g2d.getFontMetrics();
                if (fm.stringWidth(text) <= maxWidth) {
                    // Nếu text vừa với chiều rộng, vẽ bình thường và căn giữa
                    int textX = x + (maxWidth - fm.stringWidth(text)) / 2;
                    g2d.drawString(text, textX, y);
                    return;
                }
                
                // Chia text thành nhiều dòng
                java.util.List<String> lines = new ArrayList<>();
                String[] words = text.split(" ");
                StringBuilder currentLine = new StringBuilder();
                
                for (String word : words) {
                    if (fm.stringWidth(currentLine + " " + word) <= maxWidth) {
                        if (currentLine.length() > 0) currentLine.append(" ");
                        currentLine.append(word);
                    } else {
                        if (currentLine.length() > 0) {
                            lines.add(currentLine.toString());
                            currentLine = new StringBuilder(word);
                        } else {
                            // Nếu một từ dài hơn maxWidth, buộc phải thêm nó vào một dòng riêng
                            lines.add(word);
                        }
                    }
                }
                
                // Thêm dòng cuối cùng nếu còn
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                
                // Vẽ từng dòng
                int lineHeight = fm.getHeight();
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    int textX = x + (maxWidth - fm.stringWidth(line)) / 2;
                    g2d.drawString(line, textX, y + i * lineHeight);
                }
            }
        };        
        chartPanel.setPreferredSize(new Dimension(600, 400));        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }
    private JPanel createPieChart() {
        JPanel containerPanel = new JPanel(new BorderLayout(10, 0));
        containerPanel.setBackground(Color.WHITE);
        JPanel chartPanel = new JPanel() {
            private double scale = 1.0;
            private int translateX = 0;
            private int translateY = 0;            
            {
                // Thêm khả năng zoom và pan cho biểu đồ
                MouseAdapter mouseHandler = new MouseAdapter() {
                    private Point lastPoint;
                    
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        if (e.getWheelRotation() < 0) {
                            scale *= 1.1; // Zoom in
                        } else {
                            scale /= 1.1; // Zoom out
                        }
                        // Giới hạn mức zoom
                        scale = Math.max(0.5, Math.min(scale, 2.0));
                        repaint();
                    }
                    
                    @Override
                    public void mousePressed(MouseEvent e) {
                        lastPoint = e.getPoint();
                    }
                    
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        // Di chuyển biểu đồ khi kéo chuột
                        if (lastPoint != null) {
                            translateX += e.getX() - lastPoint.x;
                            translateY += e.getY() - lastPoint.y;
                            lastPoint = e.getPoint();
                            repaint();
                        }
                    }
                    
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        lastPoint = null;
                    }
                };
                
                addMouseListener(mouseHandler);
                addMouseMotionListener(mouseHandler);
                addMouseWheelListener(mouseHandler);                
            }            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Map<String, Integer> thongKe = controller.getTongSoLuongTheoPhanLoai();
                if (thongKe.isEmpty()) return;
                int total = 0;
                for (Integer value : thongKe.values()) {
                    total += value;
                }
                if (total == 0) return;
                g2d.translate(translateX, translateY);
                g2d.scale(scale, scale);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.min(getWidth(), getHeight()) / 3;
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("Tỷ lệ vật tư theo phân loại", centerX - 120, 30);
                
                Color[] colors = CHART_COLORS;
                
                double startAngle = 0;
                int colorIndex = 0;
                
                for (Map.Entry<String, Integer> entry : thongKe.entrySet()) {
                    int value = entry.getValue();
                    double arcAngle = 360.0 * value / total;                    
                    Color color = colors[colorIndex % colors.length];
                    g2d.setColor(color);
                    g2d.fillArc(centerX - radius, centerY - radius, 
                        radius * 2, radius * 2, 
                        (int) startAngle, (int) arcAngle);
                    g2d.setColor(Color.WHITE);
                    g2d.drawArc(centerX - radius, centerY - radius, 
                        radius * 2, radius * 2, 
                        (int) startAngle, (int) arcAngle);
                    startAngle += arcAngle;
                    colorIndex++;
                }
                
                g2d.dispose();
            }
        };        
        JPanel legendPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Map<String, Integer> thongKe = controller.getTongSoLuongTheoPhanLoai();
                if (thongKe.isEmpty()) return;                
                int total = 0;
                for (Integer value : thongKe.values()) {
                    total += value;
                }
                if (total == 0) return;
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("Danh sách phân loại", 10, 20);
                int y = 40;
                int colorIndex = 0;
                Color[] colors = CHART_COLORS;
                
                for (Map.Entry<String, Integer> entry : thongKe.entrySet()) {
                    String phanLoai = entry.getKey();
                    int value = entry.getValue();
                    double percentage = 100.0 * value / total;                    
                    Color color = colors[colorIndex % colors.length];
                    g2d.setColor(color);
                    
                    g2d.fillRect(10, y, 15, 15);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(10, y, 15, 15);
                    String text = phanLoai;
                    g2d.drawString(text, 35, y + 12);
                    String valueText = value + " (" + String.format("%.1f", percentage) + "%)";
                    g2d.drawString(valueText, 35, y + 28);
                    y += 45;
                    colorIndex++;
                }                
                g2d.dispose();
            }
        };
        JScrollPane legendScrollPane = new JScrollPane(legendPanel);
        legendScrollPane.setPreferredSize(new Dimension(200, 0)); // Chiều rộng cố định cho legend
        legendScrollPane.setBorder(BorderFactory.createEmptyBorder());
        int legendHeight = 45 * controller.getTongSoLuongTheoPhanLoai().size() + 50;
        legendPanel.setPreferredSize(new Dimension(180, legendHeight));
        
        containerPanel.add(chartPanel, BorderLayout.CENTER);
        containerPanel.add(legendScrollPane, BorderLayout.EAST);
        
        return containerPanel;
    }
    
    private void updateCharts() {
        try {
            tabbedPane.setComponentAt(1, createBarChart());            
            tabbedPane.setComponentAt(2, createPieChart());
            tabbedPane.revalidate();
            tabbedPane.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi cập nhật biểu đồ: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}