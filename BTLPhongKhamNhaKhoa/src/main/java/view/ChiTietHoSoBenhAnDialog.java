package view;

import model.ChiTietDonThuoc;
import model.DonThuoc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ChiTietHoSoBenhAnDialog extends JDialog {
    // Color scheme
    private Color primaryColor = new Color(79, 129, 189);    // Professional blue
    private Color secondaryColor = new Color(141, 180, 226); // Lighter blue
    private Color accentColor = new Color(192, 80, 77);      // Refined red for delete
    private Color successColor = new Color(86, 156, 104);    // Elegant green for add
    private Color warningColor = new Color(237, 187, 85);    // Softer yellow for edit
    private Color backgroundColor = new Color(248, 249, 250); // Extremely light gray background
    private Color textColor = new Color(33, 37, 41);         // Near-black text
    private Color panelColor = new Color(255, 255, 255);     // White panels
    private Color buttonTextColor = Color.WHITE;
    private Color tableHeaderColor = new Color(79, 129, 189); // Match primary color
    private Color tableStripeColor = new Color(245, 247, 250); // Very light stripe
    private Color borderColor = new Color(222, 226, 230);    // Light gray borders

    // Font settings - reduced sizes for better fit
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 11);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14); // Updated to match HoSoBenhAnUI
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 12);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 12);
    
    // Added fields for parent reference and medical record information
    private JFrame owner;
    private int idHoSo;
    private String tenBenhNhan;
    private String chuanDoan;
    private String ghiChu;
    private String ngayTao;
    private String trangThai;
    private HoSoBenhAnUI parentUI; // Added reference to parent UI for refresh

    public ChiTietHoSoBenhAnDialog(JFrame owner, String title, boolean modal,
                                 int idHoSo, String tenBenhNhan, String chuanDoan,
                                 String ghiChu, String ngayTao, String trangThai,
                                 List<DonThuoc> danhSachDonThuoc) {
        super(owner, title, modal);
        this.owner = owner;
        this.idHoSo = idHoSo;
        this.tenBenhNhan = tenBenhNhan;
        this.chuanDoan = chuanDoan;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        setupUI(idHoSo, tenBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai, danhSachDonThuoc);
    }
    
    // Added constructor that includes parentUI reference
    public ChiTietHoSoBenhAnDialog(JFrame owner, String title, boolean modal,
                                 int idHoSo, String tenBenhNhan, String chuanDoan,
                                 String ghiChu, String ngayTao, String trangThai,
                                 List<DonThuoc> danhSachDonThuoc, HoSoBenhAnUI parentUI) {
        super(owner, title, modal);
        this.owner = owner;
        this.idHoSo = idHoSo;
        this.tenBenhNhan = tenBenhNhan;
        this.chuanDoan = chuanDoan;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.parentUI = parentUI;
        setupUI(idHoSo, tenBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai, danhSachDonThuoc);
    }
    
    private void setupUI(int idHoSo, String tenBenhNhan, String chuanDoan,
                       String ghiChu, String ngayTao, String trangThai,
                       List<DonThuoc> danhSachDonThuoc) {
        // Set dialog properties
        setLayout(new BorderLayout(8, 8));  // Giảm khoảng cách giữa các thành phần chính
        getContentPane().setBackground(backgroundColor);
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));  // Giảm padding cho toàn bộ dialog
        
        // Create title panel
        JLabel titleLabel = new JLabel("Chi Tiết Hồ Sơ Bệnh Án");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));  // Giảm khoảng cách với phần nội dung
        add(titleLabel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(8, 10));  // Giảm khoảng cách giữa thông tin và đơn thuốc
        contentPanel.setBackground(backgroundColor);

        // Patient information panel
        JPanel thongTinPanel = createThongTinPanel(idHoSo, tenBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai);
        contentPanel.add(thongTinPanel, BorderLayout.NORTH);

        // Prescription panel
        JPanel donThuocPanel = createDonThuocPanel(danhSachDonThuoc);
        contentPanel.add(donThuocPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Final dialog setup
        pack();
        // Reduced sizes based on the screenshot
        setMinimumSize(new Dimension(550, 450));
        // Set a preferred size to match the screenshot
        setPreferredSize(new Dimension(580, 480));
        // Set maximum size to prevent excessive growth  
        setMaximumSize(new Dimension(600, 500));
        setLocationRelativeTo(getOwner());
    }
    
    private JPanel createThongTinPanel(int idHoSo, String tenBenhNhan, String chuanDoan, 
                                      String ghiChu, String ngayTao, String trangThai) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1, true),
            new EmptyBorder(8, 8, 8, 8)  // Giảm padding bên trong panel
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 8); // Giảm khoảng cách giữa label và field
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel idLabel = new JLabel("ID Hồ sơ:");
        idLabel.setFont(regularFont);
        idLabel.setForeground(textColor);
        panel.add(idLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.35;
        JLabel idField = createInfoField(String.valueOf(idHoSo));
        panel.add(idField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.15;
        gbc.insets = new Insets(4, 12, 4, 8); // Tăng khoảng cách với cột bên trái
        JLabel tenLabel = new JLabel("Tên Bệnh nhân:");
        tenLabel.setFont(regularFont);
        tenLabel.setForeground(textColor);
        panel.add(tenLabel, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.35;
        gbc.insets = new Insets(4, 4, 4, 4); // Khôi phục lại insets
        JLabel tenField = createInfoField(tenBenhNhan);
        panel.add(tenField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        gbc.insets = new Insets(4, 4, 4, 8); // Khôi phục lại insets
        JLabel ngayLabel = new JLabel("Ngày tạo:");
        ngayLabel.setFont(regularFont);
        ngayLabel.setForeground(textColor);
        panel.add(ngayLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.35;
        JLabel ngayField = createInfoField(ngayTao);
        panel.add(ngayField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.15;
        gbc.insets = new Insets(4, 12, 4, 8); // Tăng khoảng cách với cột bên trái
        JLabel trangThaiLabel = new JLabel("Trạng thái:");
        trangThaiLabel.setFont(regularFont);
        trangThaiLabel.setForeground(textColor);
        panel.add(trangThaiLabel, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.35;
        gbc.insets = new Insets(4, 4, 4, 4); // Khôi phục lại insets
        JLabel trangThaiField = createInfoField(trangThai);
        panel.add(trangThaiField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.15; gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 4, 4, 8); // Tăng khoảng cách với hàng trên
        JLabel chuanDoanLabel = new JLabel("Chuẩn đoán:");
        chuanDoanLabel.setFont(regularFont);
        chuanDoanLabel.setForeground(textColor);
        panel.add(chuanDoanLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.85; gbc.gridwidth = 3;
        gbc.insets = new Insets(8, 4, 4, 4); // Tăng khoảng cách với hàng trên
        JScrollPane chuanDoanScrollPane = createScrollableTextArea(chuanDoan, 30);
        panel.add(chuanDoanScrollPane, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.15; gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 4, 4, 8); // Tăng khoảng cách với hàng trên
        JLabel ghiChuLabel = new JLabel("Ghi chú:");
        ghiChuLabel.setFont(regularFont);
        ghiChuLabel.setForeground(textColor);
        panel.add(ghiChuLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.85; gbc.gridwidth = 3;
        gbc.insets = new Insets(8, 4, 4, 4); // Tăng khoảng cách với hàng trên
        JScrollPane ghiChuScrollPane = createScrollableTextArea(ghiChu, 30);
        panel.add(ghiChuScrollPane, gbc);
        
        return panel;
    }    
    private JLabel createInfoField(String text) {
        JLabel field = new JLabel(text);
        field.setFont(regularFont);
        field.setForeground(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)  // Giảm padding bên trong field
        ));
        field.setBackground(new Color(252, 252, 252));
        field.setOpaque(true);
        return field;
    }    
    private JScrollPane createScrollableTextArea(String text, int height) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(regularFont);
        textArea.setForeground(textColor);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(new Color(252, 252, 252));
        textArea.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6)); // Giảm padding bên trong text area
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, height));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }    
    private JPanel createDonThuocPanel(List<DonThuoc> danhSachDonThuoc) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));  // Giảm khoảng cách giữa tiêu đề và bảng
        panel.setBackground(panelColor);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 1),
            "Đơn Thuốc"
        );
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        titledBorder.setTitleColor(primaryColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            titledBorder,
            BorderFactory.createEmptyBorder(8, 8, 8, 8)  // Giảm padding bên trong panel
        ));
        
        DefaultTableModel donThuocTableModel = new DefaultTableModel(
            new Object[]{"ID ĐT", "Tên thuốc", "Số lượng", "Hướng dẫn sử dụng"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };        
        JTable donThuocTable = new JTable(donThuocTableModel);
        
        donThuocTable.setFont(tableFont);
        donThuocTable.setRowHeight(22);  // Giảm chiều cao của dòng để tiết kiệm không gian
        donThuocTable.setIntercellSpacing(new Dimension(4, 2));  // Giảm khoảng cách giữa các ô
        donThuocTable.setShowGrid(true);
        donThuocTable.setGridColor(new Color(240, 240, 240));
        
        JTableHeader header = donThuocTable.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 26));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        donThuocTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        donThuocTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        donThuocTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        donThuocTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        donThuocTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        donThuocTable.getColumnModel().getColumn(3).setPreferredWidth(230);
        donThuocTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setToolTipText((String)value); // Show full text on hover
                setBorder(BorderFactory.createCompoundBorder(
                    getBorder(),
                    BorderFactory.createEmptyBorder(0, 4, 0, 4) // Giảm padding bên trong ô
                ));
                return this;
            }
        });
        populateTableData(donThuocTableModel, danhSachDonThuoc);
        
        JScrollPane donThuocScrollPane = new JScrollPane(donThuocTable);
        donThuocScrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        
        int tableHeight = Math.min(150, donThuocTable.getRowHeight() * (donThuocTable.getRowCount() + 1));
        donThuocScrollPane.setPreferredSize(new Dimension(donThuocScrollPane.getPreferredSize().width, tableHeight));
        
        panel.add(donThuocScrollPane, BorderLayout.CENTER);
        
        return panel;
    }    
    private void populateTableData(DefaultTableModel model, List<DonThuoc> danhSachDonThuoc) {
        if (danhSachDonThuoc != null && !danhSachDonThuoc.isEmpty()) {
            for (DonThuoc donThuoc : danhSachDonThuoc) {
                if (donThuoc.getChiTietDonThuocs() != null && !donThuoc.getChiTietDonThuocs().isEmpty()) {
                    for (ChiTietDonThuoc chiTiet : donThuoc.getChiTietDonThuocs()) {
                        Object[] rowData = {
                            donThuoc.getIdDonThuoc(),
                            chiTiet.getThuoc().getTenThuoc(),
                            chiTiet.getSoLuong(),
                            chiTiet.getHuongDanSuDung()
                        };
                        model.addRow(rowData);
                    }
                } else {
                    model.addRow(new Object[]{donThuoc.getIdDonThuoc(), "Không có thuốc", "", ""});
                }
            }
        } else {
            model.addRow(new Object[]{"", "Không có đơn thuốc", "", ""});
        }
    }    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Khoảng cách giống HoSoBenhAnUI
        panel.setBackground(backgroundColor);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));  // Khoảng cách giống HoSoBenhAnUI
        
        // Sử dụng kích thước button giống với nút "Lưu" trong HoSoBenhAnUI
        Dimension buttonSize = new Dimension(90, 36);
        
        JButton btnChinhSua = createRoundedButton("Chỉnh Sửa", warningColor, buttonTextColor, 10, true); // true để giảm padding
        btnChinhSua.setPreferredSize(buttonSize);
        btnChinhSua.setMinimumSize(buttonSize);
        btnChinhSua.setMaximumSize(buttonSize);
        btnChinhSua.addActionListener(e -> {
            try {
                openEditDialog();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Không thể mở form chỉnh sửa: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });        
        
        JButton btnDong = createRoundedButton("Đóng", primaryColor, buttonTextColor, 10, false); // false để padding bình thường
        btnDong.setPreferredSize(buttonSize);
        btnDong.setMinimumSize(buttonSize);
        btnDong.setMaximumSize(buttonSize);
        btnDong.addActionListener(e -> dispose());
        
        panel.add(btnChinhSua);
        panel.add(btnDong);
        
        return panel;
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
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    private void openEditDialog() throws Exception {
        if (parentUI == null) {
            JOptionPane.showMessageDialog(this,
                    "Không thể chỉnh sửa. Hãy đóng và mở lại cửa sổ này.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.dispose();
        parentUI.hienThiDialogSuaHoSoBenhAnTheoId(this.idHoSo);
    }
}