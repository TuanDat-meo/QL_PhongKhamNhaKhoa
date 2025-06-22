package view;

import model.DieuTri;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TreatmentsDialog extends JDialog {
    private Color primaryColor = new Color(79, 129, 189);
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
    private Color tableSelectionBackground = new Color(230, 244, 255);
    private Color tableSelectionForeground = new Color(33, 37, 41);

    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 16);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 12);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 11);
    private Font menuItemFont = new Font("Segoe UI", Font.PLAIN, 12);
    
    private List<DieuTri> treatments;
    private JTable treatmentTable;
    private int selectedTreatmentIndex = -1;

    public TreatmentsDialog(JFrame parent, List<DieuTri> treatments) {
        super(parent, "Thông Tin Điều Trị", true);
        this.treatments = treatments;
        initializeDialog();
        setupComponents(treatments);
    }

    private void initializeDialog() {
        setSize(700, 450);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(backgroundColor);
    }

    private void setupComponents(List<DieuTri> treatments) {
        setLayout(new BorderLayout(0, 0));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = createMainPanel(treatments);
        add(mainPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(panelColor);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
        headerPanel.setPreferredSize(new Dimension(0, 50));

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setBackground(panelColor);

        JLabel titleLabel = new JLabel("THÔNG TIN ĐIỀU TRỊ");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);

        titlePanel.add(titleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Center the header content vertically
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(panelColor);
        wrapperPanel.add(headerPanel, BorderLayout.CENTER);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        return wrapperPanel;
    }

    private JPanel createMainPanel(List<DieuTri> treatments) {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create styled table
        JTable table = createStyledTable(treatments);
        JScrollPane scrollPane = new JScrollPane(table);
        styleScrollPane(scrollPane);

        // Info panel above table
        JPanel infoPanel = createInfoPanel(treatments.size());
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createInfoPanel(int treatmentCount) {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(panelColor);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Left side - treatment count
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(panelColor);
        
        JLabel countLabel = new JLabel("Tổng số lần điều trị: " + treatmentCount);
        countLabel.setFont(regularFont);
        countLabel.setForeground(textColor);

        leftPanel.add(countLabel);

        // Right side - status legend
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(panelColor);

        // Legend items
        rightPanel.add(createLegendItem("Đang điều trị", primaryColor));
        rightPanel.add(createLegendItem("Hoàn thành", successColor));
        rightPanel.add(createLegendItem("Cần theo dõi", warningColor));

        infoPanel.add(leftPanel, BorderLayout.WEST);
        infoPanel.add(rightPanel, BorderLayout.EAST);

        return infoPanel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        item.setBackground(panelColor);

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(10, 10));
        colorBox.setBorder(BorderFactory.createLineBorder(darkenColor(color), 1));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(textColor);

        item.add(colorBox);
        item.add(label);

        return item;
    }

    private JTable createStyledTable(List<DieuTri> treatments) {
        String[] columns = {
            "Mã ĐT", "Mã Hồ Sơ", "Mã Bác Sĩ", "Mô Tả Điều Trị", "Ngày Điều Trị", "Trạng Thái"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        // Add data from treatments list
        for (DieuTri treatment : treatments) {
            String status = determineStatus(treatment);
            Object[] rowData = {
                "DT" + String.format("%03d", treatment.getIdDieuTri()),
                "HS" + String.format("%03d", treatment.getIdHoSo()),
                "BS" + String.format("%03d", treatment.getIdBacSi()),
                treatment.getMoTa() != null ? treatment.getMoTa() : "Chưa có mô tả",
                treatment.getNgayDieuTri() != null ? treatment.getNgayDieuTri().toString() : "Chưa xác định",
                status
            };
            model.addRow(rowData);
        }

        JTable table = new JTable(model);
        this.treatmentTable = table;
        styleTable(table);

        return table;
    }

    private String determineStatus(DieuTri treatment) {
        // Simple logic to determine status - you can modify this based on your business logic
        if (treatment.getMoTa() != null && treatment.getMoTa().toLowerCase().contains("hoàn thành")) {
            return "Hoàn thành";
        } else if (treatment.getMoTa() != null && treatment.getMoTa().toLowerCase().contains("theo dõi")) {
            return "Cần theo dõi";
        } else {
            return "Đang điều trị";
        }
    }

    private void styleTable(JTable table) {
        table.setFont(tableFont);
        table.setForeground(textColor);
        table.setBackground(panelColor);
        table.setSelectionBackground(tableSelectionBackground);
        table.setSelectionForeground(tableSelectionForeground);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add mouse listeners for double-click and right-click
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                    selectedTreatmentIndex = row;
                    
                    if (e.getClickCount() == 2) {
                        // Double click - show details
                        showTreatmentDetailDialog(selectedTreatmentIndex);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        // Right click - show popup menu
                        showRowPopupMenu(e.getX(), e.getY());
                    }
                }
            }
        });

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(buttonTextColor);
        header.setPreferredSize(new Dimension(0, 35));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);   // Mã ĐT
        table.getColumnModel().getColumn(1).setPreferredWidth(70);   // Mã Hồ Sơ
        table.getColumnModel().getColumn(2).setPreferredWidth(70);   // Mã Bác Sĩ
        table.getColumnModel().getColumn(3).setPreferredWidth(250);  // Mô Tả
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Ngày
        table.getColumnModel().getColumn(5).setPreferredWidth(100);  // Trạng Thái

        // Custom cell renderer for alternating row colors and status colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(panelColor);
                    } else {
                        c.setBackground(tableStripeColor);
                    }
                }

                // Color status column
                if (column == 5 && value != null) {
                    String status = value.toString();
                    if (!isSelected) {
                        switch (status) {
                            case "Hoàn thành":
                                setForeground(successColor);
                                break;
                            case "Cần theo dõi":
                                setForeground(warningColor);
                                break;
                            case "Đang điều trị":
                                setForeground(primaryColor);
                                break;
                            default:
                                setForeground(textColor);
                        }
                    }
                    setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    if (!isSelected) {
                        setForeground(textColor);
                    }
                    setFont(tableFont);
                }

                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    // New method for showing popup menu
    private void showRowPopupMenu(int x, int y) {
        if (selectedTreatmentIndex < 0 || selectedTreatmentIndex >= treatments.size()) {
            return;
        }

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));

        DieuTri selectedTreatment = treatments.get(selectedTreatmentIndex);

        if (selectedTreatment != null) {
            // View Details menu item
            JMenuItem viewDetailsItem = createMenuItem("Xem Chi Tiết");
            viewDetailsItem.addActionListener(e -> showTreatmentDetailDialog(selectedTreatmentIndex));
            popupMenu.add(viewDetailsItem);

            popupMenu.addSeparator();

            // Edit menu item
            JMenuItem editItem = createMenuItem("Chỉnh Sửa");
            editItem.addActionListener(e -> showTreatmentDetailDialog(selectedTreatmentIndex));
            popupMenu.add(editItem);

            popupMenu.addSeparator();

            // Delete menu item (optional)
            JMenuItem deleteItem = createMenuItem("Xóa");
            deleteItem.setForeground(accentColor);
            deleteItem.addActionListener(e -> deleteTreatment());
            popupMenu.add(deleteItem);

            popupMenu.show(treatmentTable, x, y);
        }
    }

    // Helper method to create menu items
    private JMenuItem createMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(menuItemFont);
        menuItem.setForeground(textColor);
        menuItem.setBackground(panelColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        // Add hover effect
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(tableSelectionBackground);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(panelColor);
            }
        });

        return menuItem;
    }

    // New method for deleting treatment with custom confirmation dialog
    private void deleteTreatment() {
        if (selectedTreatmentIndex < 0 || selectedTreatmentIndex >= treatments.size()) {
            return;
        }

        DieuTri selectedTreatment = treatments.get(selectedTreatmentIndex);
        
        // Create custom confirmation dialog
        JDialog confirmDialog = new JDialog(this);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setModal(true);
        confirmDialog.setSize(400, 200);
        confirmDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel messagePanel = new JPanel(new BorderLayout(15, 0));
        messagePanel.setBackground(Color.WHITE);
        
        JLabel messageLabel = new JLabel("<html>Bạn có chắc chắn muốn xóa điều trị này?<br><b>Mã điều trị: DT" + 
            String.format("%03d", selectedTreatment.getIdDieuTri()) + "</b></html>");
        messageLabel.setFont(regularFont);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        panel.add(messagePanel, BorderLayout.CENTER);
        
        JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        confirmButtonPanel.setBackground(Color.WHITE);
        
        JButton confirmCancelButton = createRoundedButton("Hủy", new Color(158, 158, 158), Color.WHITE, 8);
        confirmCancelButton.addActionListener(cancelEvent -> confirmDialog.dispose());
        
        JButton confirmDeleteButton = createRoundedButton("Xóa", accentColor, Color.WHITE, 8);
        confirmDeleteButton.addActionListener(confirmEvent -> {
            confirmDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            confirmDeleteButton.setEnabled(false);
            confirmDeleteButton.setText("Đang xóa...");
            
            try {
                // Remove from list
                treatments.remove(selectedTreatmentIndex);
                
                // Refresh table
                refreshTable();
                
                confirmDialog.dispose();
                
                // Show success message with custom dialog
                showCustomMessageDialog("Xóa thành công",
                    "Điều trị đã được xóa thành công!",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Reset selection
                selectedTreatmentIndex = -1;
                
            } catch (Exception ex) {
                showCustomMessageDialog("Lỗi hệ thống",
                    "Đã xảy ra lỗi khi xóa điều trị:\n" + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                confirmDialog.setCursor(Cursor.getDefaultCursor());
                confirmDeleteButton.setEnabled(true);
                confirmDeleteButton.setText("Xóa");
            }
        });
        
        confirmButtonPanel.add(confirmCancelButton);
        confirmButtonPanel.add(confirmDeleteButton);
        panel.add(confirmButtonPanel, BorderLayout.SOUTH);
        
        confirmDialog.setContentPane(panel);
        confirmDialog.setVisible(true);
    }

    // Custom message dialog method
    private void showCustomMessageDialog(String title, String message, int messageType) {
        JDialog messageDialog = new JDialog(this);
        messageDialog.setTitle(title);
        messageDialog.setModal(true);
        messageDialog.setSize(350, 160);
        messageDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel messagePanel = new JPanel(new BorderLayout(15, 0));
        messagePanel.setBackground(Color.WHITE);
        
        JLabel messageLabel = new JLabel("<html>" + message + "</html>");
        messageLabel.setFont(regularFont);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        panel.add(messagePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        Color buttonColor = messageType == JOptionPane.INFORMATION_MESSAGE ? successColor : accentColor;
        JButton okButton = createRoundedButton("OK", buttonColor, Color.WHITE, 8);
        okButton.addActionListener(e -> messageDialog.dispose());
        
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        messageDialog.setContentPane(panel);
        messageDialog.setVisible(true);
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        scrollPane.getViewport().setBackground(panelColor);
        scrollPane.setBackground(panelColor);
        
        // Style scrollbars
        JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
        JScrollBar hScrollBar = scrollPane.getHorizontalScrollBar();
        
        vScrollBar.setBackground(backgroundColor);
        hScrollBar.setBackground(backgroundColor);
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(backgroundColor);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(backgroundColor);

        // Close button
        JButton closeButton = createRoundedButton("Đóng", accentColor, buttonTextColor, 6);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(closeButton);

        footerPanel.add(buttonPanel, BorderLayout.EAST);

        return footerPanel;
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
    
    private void showTreatmentDetailDialog(int selectedRow) {
        if (selectedRow < 0 || selectedRow >= treatments.size()) return;
        
        DieuTri treatment = treatments.get(selectedRow);
        TreatmentDetailDialog detailDialog = new TreatmentDetailDialog(this, treatment);
        detailDialog.setVisible(true);
        
        // Refresh table if treatment was modified
        if (detailDialog.isModified()) {
            refreshTable();
        }
    }
    
    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) treatmentTable.getModel();
        model.setRowCount(0);
        
        // Re-add data from treatments list
        for (DieuTri treatment : treatments) {
            String status = determineStatus(treatment);
            Object[] rowData = {
                "DT" + String.format("%03d", treatment.getIdDieuTri()),
                "HS" + String.format("%03d", treatment.getIdHoSo()),
                "BS" + String.format("%03d", treatment.getIdBacSi()),
                treatment.getMoTa() != null ? treatment.getMoTa() : "Chưa có mô tả",
                treatment.getNgayDieuTri() != null ? treatment.getNgayDieuTri().toString() : "Chưa xác định",
                status
            };
            model.addRow(rowData);
        }
        
        treatmentTable.repaint();
    }
    
    // Inner class for Treatment Detail Dialog
    private class TreatmentDetailDialog extends JDialog {
        private DieuTri treatment;
        private boolean modified = false;
        private JTextField descriptionField;
        private JTextField dateField;
        private JComboBox<String> statusComboBox;
        
        public TreatmentDetailDialog(Dialog parent, DieuTri treatment) {
            super(parent, "Chi Tiết Điều Trị", true);
            this.treatment = treatment;
            initializeDetailDialog();
            setupDetailComponents();
            loadTreatmentData();
        }        
        private void initializeDetailDialog() {
            setSize(500, 350);
            setLocationRelativeTo(getParent());
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            getContentPane().setBackground(backgroundColor);
        }        
        private void setupDetailComponents() {
            setLayout(new BorderLayout(0, 0));
            
            // Header
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(panelColor);
            headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
            headerPanel.setPreferredSize(new Dimension(0, 60));
            
            JLabel titleLabel = new JLabel("CHI TIẾT ĐIỀU TRỊ");
            titleLabel.setFont(titleFont);
            titleLabel.setForeground(primaryColor);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            headerPanel.add(titleLabel, BorderLayout.WEST);
            add(headerPanel, BorderLayout.NORTH);
            
            // Main content
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBackground(backgroundColor);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 0, 8, 15);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Treatment ID (read-only)
            gbc.gridx = 0; gbc.gridy = 0;
            mainPanel.add(createLabel("Mã điều trị:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField idField = createReadOnlyField("DT" + String.format("%03d", treatment.getIdDieuTri()));
            mainPanel.add(idField, gbc);
            
            // Medical Record ID (read-only)
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            mainPanel.add(createLabel("Mã hồ sơ:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField recordIdField = createReadOnlyField("HS" + String.format("%03d", treatment.getIdHoSo()));
            mainPanel.add(recordIdField, gbc);
            
            // Doctor ID (read-only)
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            mainPanel.add(createLabel("Mã bác sĩ:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField doctorIdField = createReadOnlyField("BS" + String.format("%03d", treatment.getIdBacSi()));
            mainPanel.add(doctorIdField, gbc);
            
            // Description (editable)
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            mainPanel.add(createLabel("Mô tả điều trị:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            descriptionField = createEditableField("");
            mainPanel.add(descriptionField, gbc);
            
            // Date (editable)
            gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            mainPanel.add(createLabel("Ngày điều trị:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            dateField = createEditableField("");
            mainPanel.add(dateField, gbc);
            
            // Status (editable)
            gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            mainPanel.add(createLabel("Trạng thái:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            String[] statusOptions = {"Đang điều trị", "Hoàn thành", "Cần theo dõi"};
            statusComboBox = new JComboBox<>(statusOptions);
            statusComboBox.setFont(regularFont);
            statusComboBox.setBackground(panelColor);
            statusComboBox.setBorder(BorderFactory.createLineBorder(borderColor, 1));
            mainPanel.add(statusComboBox, gbc);
            
            add(mainPanel, BorderLayout.CENTER);
            
            // Footer with buttons
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
            footerPanel.setBackground(backgroundColor);
            
            JButton saveButton = createRoundedButton("Lưu", successColor, buttonTextColor, 6);
            saveButton.addActionListener(e -> saveTreatment());
            
            JButton cancelButton = createRoundedButton("Hủy", accentColor, buttonTextColor, 6);
            cancelButton.addActionListener(e -> dispose());
            
            footerPanel.add(saveButton);
            footerPanel.add(cancelButton);
            add(footerPanel, BorderLayout.SOUTH);
        }
        
        private JLabel createLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(regularFont);
            label.setForeground(textColor);
            label.setPreferredSize(new Dimension(120, 30));
            return label;
        }
        
        private JTextField createReadOnlyField(String text) {
            JTextField field = new JTextField(text);
            field.setFont(regularFont);
            field.setEditable(false);
            field.setBackground(tableStripeColor);
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
            return field;
        }
        
        private JTextField createEditableField(String text) {
            JTextField field = new JTextField(text);
            field.setFont(regularFont);
            field.setBackground(panelColor);
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
            return field;
        }
        
        private void loadTreatmentData() {
            descriptionField.setText(treatment.getMoTa() != null ? treatment.getMoTa() : "");
            dateField.setText(treatment.getNgayDieuTri() != null ? treatment.getNgayDieuTri().toString() : "");
            statusComboBox.setSelectedItem(determineStatus(treatment));
        }
        
        private void saveTreatment() {
            // Update treatment object
            treatment.setMoTa(descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim());
            
            // For date parsing, you might want to add proper date validation
            try {
                if (!dateField.getText().trim().isEmpty()) {
                    // Assuming the date is in YYYY-MM-DD format
                    // You might need to adjust this based on your DieuTri class's date handling
                    // treatment.setNgayDieuTri(Date.valueOf(dateField.getText().trim()));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Định dạng ngày không hợp lệ. Vui lòng sử dụng định dạng YYYY-MM-DD", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            modified = true;
            JOptionPane.showMessageDialog(this, 
                "Thông tin điều trị đã được cập nhật thành công!", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
        
        public boolean isModified() {
            return modified;
        }
    }
}