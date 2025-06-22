package view;

import controller.LuongController;
import model.Luong;
import util.CustomBorder;
import util.DataChangeListener;
import util.ExportManager;
import util.RoundedPanel;
import util.ExportManager.MessageCallback;
import util.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class LuongUI extends JPanel implements MessageCallback, DataChangeListener {
    // Color scheme
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
    private Color highlightColor = new Color(237, 187, 85); // Màu highlight

    // Font settings
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private Font totalRowFont = new Font("Segoe UI", Font.BOLD, 14);

    // UI Components
    private JTable tblLuong;
    private JTable tableTotalRow;
    private DefaultTableModel modelLuong;
    private DefaultTableModel modelTotalRow;
    private JTextField txtTimKiem;
    private JButton btnThem;
    private JButton btnTimKiem;
    private JButton btnXuatFile;
    private JPopupMenu popupMenu;
    private TableRowSorter<DefaultTableModel> sorterLuong;
    private ExportManager exportManager;

    private LuongController controller;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    private List<Object[]> originalData;
    private int highlightedRowId = -1; // ID của bản ghi đang được highlight
    private javax.swing.Timer highlightTimer; // Timer để tắt highlight
    
    public enum NotificationType {
        SUCCESS(new Color(86, 156, 104), "Thành công"),
        WARNING(new Color(237, 187, 85), "Cảnh báo"),
        ERROR(new Color(192, 80, 77), "Lỗi");

        public final Color color; // public field
        public final String title; // public field

        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }

    public LuongUI() {
        initializePanel();
        initializeFormatters();
        initializeFields();
        buildTablePanel();
        buildHeaderPanel();
        buildButtonPanel();
        createPopupMenu();
        setupHighlightEventListeners();

        controller = new LuongController(this);
        exportManager = new ExportManager(this, modelLuong, this);

        SwingUtilities.invokeLater(() -> {
            controller.loadLuongData();
            btnXuatFile.setEnabled(modelLuong.getRowCount() > 0);
        });
    }

    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void initializeFormatters() {
        currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setMaximumFractionDigits(0);
        currencyFormat.setMinimumFractionDigits(0);
        dateFormat = new SimpleDateFormat("MM/yyyy");
    }

    private void initializeFields() {
        originalData = new ArrayList<>();
        txtTimKiem = createStyledTextField(false);
    }

    private JTextField createStyledTextField(boolean isReadOnly) {
        JTextField textField = new JTextField();
        textField.setFont(regularFont);
        textField.setPreferredSize(new Dimension(230, 38));
        textField.setMinimumSize(new Dimension(230, 38));
        textField.setMaximumSize(new Dimension(230, 38));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new CustomBorder(8, borderColor),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        textField.setBackground(isReadOnly ? new Color(220, 220, 220) : Color.WHITE);
        textField.setEditable(!isReadOnly);
        textField.setHorizontalAlignment(JTextField.LEFT);
        return textField;
    }

    private JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(regularFont);
        dateChooser.setPreferredSize(new Dimension(230, 38));
        dateChooser.setBorder(new CustomBorder(8, borderColor));
        dateChooser.setDateFormatString("MM/yyyy");
        JTextField dateTextField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateTextField.setFont(regularFont);
        dateTextField.setBorder(new EmptyBorder(5, 12, 5, 12));
        dateTextField.setBackground(Color.WHITE);
        return dateChooser;
    }

    private JLabel createErrorLabel() {
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        errorLabel.setForeground(new Color(220, 53, 69));
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        errorLabel.setPreferredSize(new Dimension(300, 16));
        errorLabel.setMinimumSize(new Dimension(300, 16));
        return errorLabel;
    }

    private void buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("QUẢN LÝ LƯƠNG");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(regularFont);
        searchLabel.setForeground(textColor);
        txtTimKiem.setPreferredSize(new Dimension(220, 38));
        btnTimKiem = createStyledButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(120, 38));
        searchPanel.add(searchLabel);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        btnTimKiem.addActionListener(e -> timKiem());
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiem();
                }
            }
        });
    }

    private void buildTablePanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);

        JPanel tablePanel = new RoundedPanel(15, true);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initializeTable();
        styleTable();
        JPanel tablesContainer = new JPanel(new BorderLayout());
        tablesContainer.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(tblLuong);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.add(tableTotalRow, BorderLayout.CENTER);

        tablesContainer.add(scrollPane, BorderLayout.CENTER);
        tablesContainer.add(totalPanel, BorderLayout.SOUTH);

        tablePanel.add(tablesContainer, BorderLayout.CENTER);
        wrapperPanel.add(tablePanel, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);
    }

    private void initializeTable() {
        String[] columns = {"ID", "Nhân viên", "Tháng/Năm", "Lương cơ bản", "Thưởng", "Khấu trừ", "Tổng lương"};
        modelLuong = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblLuong = new JTable(modelLuong);
        sorterLuong = new TableRowSorter<>(modelLuong);
        tblLuong.setRowSorter(sorterLuong);

        modelTotalRow = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTotalRow = new JTable(modelTotalRow);
        modelTotalRow.addRow(new Object[]{null, null, null, 0.0, 0.0, 0.0, 0.0});
    }

    private void styleTable() {
        tblLuong.setFont(tableFont);
        tblLuong.setRowHeight(40);
        tblLuong.setShowGrid(false);
        tblLuong.setIntercellSpacing(new Dimension(0, 0));
        tblLuong.setSelectionBackground(new Color(229, 243, 255));
        tblLuong.setSelectionForeground(textColor);
        tblLuong.setFocusable(false);
        tblLuong.setAutoCreateRowSorter(true);
        JTableHeader header = tblLuong.getTableHeader();
        header.setFont(tableHeaderFont);
        header.setBackground(tableHeaderColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 107, 161)));
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        tblLuong.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int modelRow = table.convertRowIndexToModel(row);
                int rowId = Integer.parseInt(modelLuong.getValueAt(modelRow, 0).toString());

                // Xử lý màu nền
                if (!isSelected) {
                    if (highlightedRowId > 0 && rowId == highlightedRowId) {
                        c.setBackground(highlightColor);
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : tableStripeColor);
                    }
                } else {
                    c.setBackground(table.getSelectionBackground());
                }

                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                if (column >= 3 && value instanceof Double) {
                    ((JLabel) c).setText(currencyFormat.format((Double) value));
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });

        tableTotalRow.setFont(totalRowFont);
        tableTotalRow.setRowHeight(45);
        tableTotalRow.setTableHeader(null);
        tableTotalRow.setShowGrid(false);
        tableTotalRow.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(totalRowColor);
                if (column == 0) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                    ((JLabel) c).setFont(totalRowFont);
                } else if (column >= 3 && value instanceof Double) {
                    ((JLabel) c).setText(currencyFormat.format((Double) value));
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) c).setFont(totalRowFont);
                } else {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
    }

    private void buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnXuatFile = createRoundedButton("Xuất file", warningColor, buttonTextColor, 10, false);
        btnXuatFile.setPreferredSize(new Dimension(100, 45));
        btnXuatFile.addActionListener(e -> {
            if (modelLuong.getRowCount() == 0) {
                showNotification("Không có dữ liệu để xuất!", NotificationType.WARNING);
                return;
            }
            exportManager.showExportOptions(primaryColor, secondaryColor, buttonTextColor);
        });

        btnThem = createRoundedButton("Thêm mới", successColor, buttonTextColor, 10, false);
        btnThem.setPreferredSize(new Dimension(100, 45));
        btnThem.addActionListener(e -> showLuongDialog(null, DialogMode.ADD));

        buttonPanel.add(btnXuatFile);
        buttonPanel.add(btnThem);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        return createRoundedButton(text, primaryColor, buttonTextColor, 10, false);
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
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(
                        reducedPadding ? 8 : 8,
                        reducedPadding ? 8 : 15,
                        reducedPadding ? 8 : 8,
                        reducedPadding ? 8 : 15
                )
        ));

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

    private enum DialogMode {
        ADD, EDIT, VIEW
    }

    private void showLuongDialog(Luong luong, DialogMode mode) {
        String title;
        switch (mode) {
            case ADD:
                title = "Thêm Mới Lương";
                break;
            case EDIT:
                title = "Chỉnh Sửa Lương";
                break;
            case VIEW:
                title = "Chi Tiết Lương";
                break;
            default:
                title = "Chi Tiết Lương";
                break;
        }

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) {
            showNotification("Không tìm thấy cửa sổ cha!", NotificationType.ERROR);
            return;
        }

        if (mode == DialogMode.VIEW) {
            showChiTietLuongDialog(topFrame, luong);
        } else {
            JDialog inputDialog = new JDialog(topFrame, title, true);
            inputDialog.setSize(480, 550);
            inputDialog.setLocationRelativeTo(null);
            inputDialog.setResizable(false);
            inputDialog.setLayout(new BorderLayout());

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);

            // Header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(primaryColor);
            headerPanel.setBorder(new EmptyBorder(18, 25, 18, 25));
            JLabel titleLabel = new JLabel(title.toUpperCase());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel, BorderLayout.WEST);
            mainPanel.add(headerPanel, BorderLayout.NORTH);

            // Form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 4, 0, 4);
            gbc.weightx = 1.0;

            Color requiredFieldColor = new Color(255, 0, 0);
            Map<JComponent, JLabel> errorLabels = new HashMap<>();

            // Input fields
            JTextField txtIdLuong = createStyledTextField(true);
            txtIdLuong.setVisible(false);
            JComboBox<String> cmbNhanVien = new JComboBox<>();
            cmbNhanVien.setFont(regularFont);
            cmbNhanVien.setPreferredSize(new Dimension(230, 32));
            cmbNhanVien.setBorder(null);
            JDateChooser dateThangNam = createStyledDateChooser();
            JTextField txtLuongCoBan = createStyledTextField(false);
            JTextField txtThuong = createStyledTextField(false);
            JTextField txtKhauTru = createStyledTextField(false);
            JTextField txtTongLuong = createStyledTextField(true);

            controller.loadNhanVienComboBox(cmbNhanVien);

            // Nhân viên
            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel lblNhanVien = new JLabel("Nhân viên: ");
            lblNhanVien.setFont(regularFont);
            JPanel nhanVienLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            nhanVienLabelPanel.setBackground(Color.WHITE);
            nhanVienLabelPanel.add(lblNhanVien);
            if (mode != DialogMode.VIEW) {
                JLabel starNhanVien = new JLabel("*");
                starNhanVien.setForeground(requiredFieldColor);
                starNhanVien.setFont(regularFont);
                nhanVienLabelPanel.add(starNhanVien);
            }
            formPanel.add(nhanVienLabelPanel, gbc);

            gbc.gridx = 1;
            formPanel.add(cmbNhanVien, gbc);

            gbc.insets = new Insets(0, 4, 4, 4);
            gbc.gridx = 1;
            gbc.gridy++;
            formPanel.add(Box.createVerticalStrut(5), gbc);

            // Tháng/Năm
            gbc.insets = new Insets(4, 4, 0, 4);
            gbc.gridx = 0;
            gbc.gridy++;
            JLabel lblThangNam = new JLabel("Tháng/Năm: ");
            lblThangNam.setFont(regularFont);
            JPanel thangNamLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            thangNamLabelPanel.setBackground(Color.WHITE);
            thangNamLabelPanel.add(lblThangNam);
            if (mode != DialogMode.VIEW) {
                JLabel starThangNam = new JLabel("*");
                starThangNam.setForeground(requiredFieldColor);
                starThangNam.setFont(regularFont);
                thangNamLabelPanel.add(starThangNam);
            }
            formPanel.add(thangNamLabelPanel, gbc);

            gbc.gridx = 1;
            formPanel.add(dateThangNam, gbc);

            gbc.insets = new Insets(0, 4, 4, 4);
            gbc.gridx = 1;
            gbc.gridy++;
            JLabel lblThangNamError = createErrorLabel();
            formPanel.add(lblThangNamError, gbc);
            errorLabels.put(dateThangNam, lblThangNamError);

            // Lương cơ bản
            gbc.insets = new Insets(4, 4, 0, 4);
            gbc.gridx = 0;
            gbc.gridy++;
            JLabel lblLuongCoBan = new JLabel("Lương cơ bản: ");
            lblLuongCoBan.setFont(regularFont);
            JPanel luongCoBanLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            luongCoBanLabelPanel.setBackground(Color.WHITE);
            luongCoBanLabelPanel.add(lblLuongCoBan);
            if (mode != DialogMode.VIEW) {
                JLabel starLuongCoBan = new JLabel("*");
                starLuongCoBan.setForeground(requiredFieldColor);
                starLuongCoBan.setFont(regularFont);
                luongCoBanLabelPanel.add(starLuongCoBan);
            }
            formPanel.add(luongCoBanLabelPanel, gbc);

            gbc.gridx = 1;
            formPanel.add(txtLuongCoBan, gbc);

            gbc.insets = new Insets(0, 4, 4, 4);
            gbc.gridx = 1;
            gbc.gridy++;
            JLabel lblLuongCoBanError = createErrorLabel();
            formPanel.add(lblLuongCoBanError, gbc);
            errorLabels.put(txtLuongCoBan, lblLuongCoBanError);

            // Thưởng
            gbc.insets = new Insets(4, 4, 0, 4);
            gbc.gridx = 0;
            gbc.gridy++;
            JLabel lblThuong = new JLabel("Thưởng: ");
            lblThuong.setFont(regularFont);
            JPanel thuongLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            thuongLabelPanel.setBackground(Color.WHITE);
            thuongLabelPanel.add(lblThuong);
            if (mode != DialogMode.VIEW) {
                JLabel starThuong = new JLabel("*");
                starThuong.setForeground(requiredFieldColor);
                starThuong.setFont(regularFont);
                thuongLabelPanel.add(starThuong);
            }
            formPanel.add(thuongLabelPanel, gbc);

            gbc.gridx = 1;
            formPanel.add(txtThuong, gbc);

            gbc.insets = new Insets(0, 4, 4, 4);
            gbc.gridx = 1;
            gbc.gridy++;
            JLabel lblThuongError = createErrorLabel();
            formPanel.add(lblThuongError, gbc);
            errorLabels.put(txtThuong, lblThuongError);

            // Khấu trừ
            gbc.insets = new Insets(4, 4, 0, 4);
            gbc.gridx = 0;
            gbc.gridy++;
            JLabel lblKhauTru = new JLabel("Khấu trừ: ");
            lblKhauTru.setFont(regularFont);
            JPanel khauTruLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            khauTruLabelPanel.setBackground(Color.WHITE);
            khauTruLabelPanel.add(lblKhauTru);
            if (mode != DialogMode.VIEW) {
                JLabel starKhauTru = new JLabel("*");
                starKhauTru.setForeground(requiredFieldColor);
                starKhauTru.setFont(regularFont);
                khauTruLabelPanel.add(starKhauTru);
            }
            formPanel.add(khauTruLabelPanel, gbc);

            gbc.gridx = 1;
            formPanel.add(txtKhauTru, gbc);

            gbc.insets = new Insets(0, 4, 4, 4);
            gbc.gridx = 1;
            gbc.gridy++;
            JLabel lblKhauTruError = createErrorLabel();
            formPanel.add(lblKhauTruError, gbc);
            errorLabels.put(txtKhauTru, lblKhauTruError);

            // Tổng lương
            gbc.insets = new Insets(4, 4, 0, 4);
            gbc.gridx = 0;
            gbc.gridy++;
            JLabel lblTongLuong = new JLabel("Tổng lương: ");
            lblTongLuong.setFont(regularFont);
            JPanel tongLuongLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            tongLuongLabelPanel.setBackground(Color.WHITE);
            tongLuongLabelPanel.add(lblTongLuong);
            formPanel.add(tongLuongLabelPanel, gbc);

            gbc.gridx = 1;
            formPanel.add(txtTongLuong, gbc);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            buttonPanel.setBackground(backgroundColor);
            buttonPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
            Dimension buttonSize = new Dimension(90, 36);

            boolean editable = mode != DialogMode.VIEW;
            cmbNhanVien.setEnabled(editable);
            dateThangNam.setEnabled(editable);
            txtLuongCoBan.setEditable(editable);
            txtThuong.setEditable(editable);
            txtKhauTru.setEditable(editable);

            if (mode == DialogMode.ADD || mode == DialogMode.EDIT) {
                updateTongLuong(txtTongLuong, txtLuongCoBan, txtThuong, txtKhauTru);
                setupEnterKeyNavigation(new JComponent[]{
                        cmbNhanVien,
                        dateThangNam.getDateEditor().getUiComponent(),
                        txtLuongCoBan,
                        txtThuong,
                        txtKhauTru
                });
                txtLuongCoBan.getDocument().addDocumentListener(new SimpleDocumentListener() {
                    @Override
                    public void update() {
                        updateTongLuong(txtTongLuong, txtLuongCoBan, txtThuong, txtKhauTru);
                    }
                });
                txtThuong.getDocument().addDocumentListener(new SimpleDocumentListener() {
                    @Override
                    public void update() {
                        updateTongLuong(txtTongLuong, txtLuongCoBan, txtThuong, txtKhauTru);
                    }
                });
                txtKhauTru.getDocument().addDocumentListener(new SimpleDocumentListener() {
                    @Override
                    public void update() {
                        updateTongLuong(txtTongLuong, txtLuongCoBan, txtThuong, txtKhauTru);
                    }
                });
            }

            if (mode == DialogMode.ADD) {
                txtIdLuong.setText("");
                txtLuongCoBan.setText("");
                txtThuong.setText("");
                txtKhauTru.setText("");
                txtTongLuong.setText("");
                controller.loadNhanVienComboBox(cmbNhanVien);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                dateThangNam.setDate(calendar.getTime());
                resetAllValidationErrors(errorLabels);
            } else if (mode == DialogMode.EDIT) {
                displayLuongDetails(luong, txtIdLuong, cmbNhanVien, dateThangNam, txtLuongCoBan, txtThuong, txtKhauTru, txtTongLuong);
                resetAllValidationErrors(errorLabels);
            }

            if (mode == DialogMode.ADD || mode == DialogMode.EDIT) {
                JButton btnLuu = createRoundedButton("Lưu", successColor, buttonTextColor, 10, false);
                btnLuu.setPreferredSize(buttonSize);
                btnLuu.addActionListener(e -> {
                    boolean success = mode == DialogMode.ADD ?
                            themLuong(txtIdLuong, cmbNhanVien, dateThangNam, txtLuongCoBan, txtThuong, txtKhauTru, errorLabels) :
                            suaLuong(txtIdLuong, cmbNhanVien, dateThangNam, txtLuongCoBan, txtThuong, txtKhauTru, errorLabels);
                    if (success) {
                        inputDialog.dispose();
                    }
                });

                JButton btnHuy = createRoundedButton("Hủy", Color.WHITE, textColor, 10, false);
                btnHuy.setBorder(new LineBorder(borderColor, 1));
                btnHuy.setPreferredSize(buttonSize);
                btnHuy.addActionListener(e -> inputDialog.dispose());

                buttonPanel.add(btnHuy);
                buttonPanel.add(btnLuu);
                inputDialog.getRootPane().setDefaultButton(btnLuu);
            }

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            inputDialog.setContentPane(mainPanel);
            inputDialog.setVisible(true);
        }
    }

    private void showChiTietLuongDialog(JFrame parent, Luong luong) {
        JDialog dialog = new JDialog(parent, "Chi Tiết Lương", true);
        dialog.setSize(500, 490);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("CHI TIẾT LƯƠNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Mã lương: " + luong.getIdLuong());
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

        // Lấy tên nhân viên từ controller
        JComboBox<String> tempComboBox = new JComboBox<>();
        controller.loadNhanVienComboBox(tempComboBox);
        String tenNhanVien = "";
        for (int i = 0; i < tempComboBox.getItemCount(); i++) {
            String hoTen = tempComboBox.getItemAt(i);
            if (controller.getIdNguoiDungByHoTen(hoTen) == luong.getIdNguoiDung()) {
                tenNhanVien = hoTen;
                break;
            }
        }
        String thangNam = dateFormat.format(luong.getThangNam());
        String luongCoBan = currencyFormat.format(luong.getLuongCoBan()) + " VNĐ";
        String thuong = currencyFormat.format(luong.getThuong()) + " VNĐ";
        String khauTru = currencyFormat.format(luong.getKhauTru()) + " VNĐ";
        String tongLuong = currencyFormat.format(luong.getTongLuong()) + " VNĐ";

        addDetailField(detailsPanel, "Nhân viên:", tenNhanVien);
        addDetailField(detailsPanel, "Tháng/Năm:", thangNam);
        addDetailField(detailsPanel, "Lương cơ bản:", luongCoBan);
        addDetailField(detailsPanel, "Thưởng:", thuong);
        addDetailField(detailsPanel, "Khấu trừ:", khauTru);
        addDetailField(detailsPanel, "Tổng lương:", tongLuong);

        detailsPanel.add(Box.createVerticalGlue());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        Dimension buttonSize = new Dimension(90, 36);

        JButton deleteButton = createRoundedButton("Xóa", accentColor, buttonTextColor, 10, true);
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.setMinimumSize(buttonSize);
        deleteButton.setMaximumSize(buttonSize);
        deleteButton.addActionListener(e -> {
            dialog.dispose();
            xoaLuongAction();
        });

        JButton editButton = createRoundedButton("Chỉnh sửa", warningColor, buttonTextColor, 10, true);
        editButton.setPreferredSize(buttonSize);
        editButton.setMinimumSize(buttonSize);
        editButton.setMaximumSize(buttonSize);
        editButton.addActionListener(e -> {
            dialog.dispose();
            showLuongDialog(luong, DialogMode.EDIT);
        });

        JButton closeButton = createRoundedButton("Đóng", primaryColor, buttonTextColor, 10, false);
        closeButton.setPreferredSize(buttonSize);
        closeButton.setMinimumSize(buttonSize);
        closeButton.setMaximumSize(buttonSize);
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);

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

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(450, 1));

        panel.add(fieldPanel);
        panel.add(separator);
    }

    private void updateTongLuong(JTextField txtTongLuong, JTextField txtLuongCoBan, JTextField txtThuong, JTextField txtKhauTru) {
        try {
            String luongCoBanText = txtLuongCoBan.getText().trim();
            String thuongText = txtThuong.getText().trim();
            String khauTruText = txtKhauTru.getText().trim();

            double luongCoBan = luongCoBanText.isEmpty() ? 0 : Double.parseDouble(luongCoBanText);
            double thuong = thuongText.isEmpty() ? 0 : Double.parseDouble(thuongText);
            double khauTru = khauTruText.isEmpty() ? 0 : Double.parseDouble(khauTruText);

            double tongLuong = luongCoBan + thuong - khauTru;
            txtTongLuong.setText(String.valueOf((long) tongLuong));
        } catch (NumberFormatException e) {
            txtTongLuong.setText("");
        }
    }

    private void setupEnterKeyNavigation(JComponent[] components) {
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
            }
        }
    }

    private void resetAllValidationErrors(Map<JComponent, JLabel> errorLabels) {
        for (Map.Entry<JComponent, JLabel> entry : errorLabels.entrySet()) {
            ValidationUtils.clearValidationError(entry.getKey(), entry.getValue());
        }
    }

    private boolean themLuong(JTextField txtIdLuong, JComboBox<String> cmbNhanVien, JDateChooser dateThangNam,
                              JTextField txtLuongCoBan, JTextField txtThuong, JTextField txtKhauTru,
                              Map<JComponent, JLabel> errorLabels) {
        resetAllValidationErrors(errorLabels);
        boolean isValid = true;

        String selectedNhanVien = (String) cmbNhanVien.getSelectedItem();
        java.util.Date selectedDate = dateThangNam.getDate();
        String luongCoBanText = txtLuongCoBan.getText().trim();
        String thuongText = txtThuong.getText().trim();
        String khauTruText = txtKhauTru.getText().trim();

        // Validate Nhân viên
        if (selectedNhanVien == null || selectedNhanVien.isEmpty()) {
            showNotification("Nhân viên không được để trống", NotificationType.ERROR);
            isValid = false;
        }

        // Validate Tháng/Năm
        if (!ValidationUtils.validateThangNam(selectedDate, dateThangNam, errorLabels.get(dateThangNam))) {
            ValidationUtils.setErrorBorder(dateThangNam);
            isValid = false;
        } else {
            ValidationUtils.setValidBorder(dateThangNam);
        }

        // Validate Lương cơ bản
        if (luongCoBanText.isEmpty()) {
            ValidationUtils.showValidationError(txtLuongCoBan, errorLabels.get(txtLuongCoBan),
                    "Lương cơ bản không được để trống");
            ValidationUtils.setErrorBorder(txtLuongCoBan);
            isValid = false;
        } else {
            try {
                double luongCoBan = Double.parseDouble(luongCoBanText);
                if (luongCoBan < 0) {
                    ValidationUtils.showValidationError(txtLuongCoBan, errorLabels.get(txtLuongCoBan),
                            "Lương cơ bản không hợp lệ");
                    ValidationUtils.setErrorBorder(txtLuongCoBan);
                    isValid = false;
                } else {
                    ValidationUtils.setValidBorder(txtLuongCoBan);
                }
            } catch (NumberFormatException e) {
                ValidationUtils.showValidationError(txtLuongCoBan, errorLabels.get(txtLuongCoBan),
                        "Lương cơ bản không hợp lệ");
                ValidationUtils.setErrorBorder(txtLuongCoBan);
                isValid = false;
            }
        }

        // Validate Thưởng
        if (thuongText.isEmpty()) {
            ValidationUtils.showValidationError(txtThuong, errorLabels.get(txtThuong), "Thưởng không được để trống");
            ValidationUtils.setErrorBorder(txtThuong);
            isValid = false;
        } else {
            try {
                double thuong = Double.parseDouble(thuongText);
                if (thuong < 0) {
                    ValidationUtils.showValidationError(txtThuong, errorLabels.get(txtThuong), "Thưởng không hợp lệ");
                    ValidationUtils.setErrorBorder(txtThuong);
                    isValid = false;
                } else {
                    ValidationUtils.setValidBorder(txtThuong);
                }
            } catch (NumberFormatException e) {
                ValidationUtils.showValidationError(txtThuong, errorLabels.get(txtThuong), "Thưởng sai định dạng");
                ValidationUtils.setErrorBorder(txtThuong);
                isValid = false;
            }
        }

        // Validate Khấu trừ
        if (khauTruText.isEmpty()) {
            ValidationUtils.showValidationError(txtKhauTru, errorLabels.get(txtKhauTru),
                    "Khấu trừ không được để trống");
            ValidationUtils.setErrorBorder(txtKhauTru);
            isValid = false;
        } else {
            try {
                double khauTru = Double.parseDouble(khauTruText);
                if (khauTru < 0) {
                    ValidationUtils.showValidationError(txtKhauTru, errorLabels.get(txtKhauTru),
                            "Khấu trừ không hợp lệ");
                    ValidationUtils.setErrorBorder(txtKhauTru);
                    isValid = false;
                } else {
                    ValidationUtils.setValidBorder(txtKhauTru);
                }
            } catch (NumberFormatException e) {
                ValidationUtils.showValidationError(txtKhauTru, errorLabels.get(txtKhauTru), "Khấu trừ sai định dạng");
                ValidationUtils.setErrorBorder(txtKhauTru);
                isValid = false;
            }
        }

        if (!isValid) {
            showNotification("Vui lòng kiểm tra các trường dữ liệu!", NotificationType.ERROR);
            return false;
        }

        try {
            int idNguoiDung = controller.getIdNguoiDungByHoTen(selectedNhanVien);
            if (idNguoiDung == -1) {
                showNotification("Không tìm thấy nhân viên!", NotificationType.ERROR);
                return false;
            }
            Date thangNam = new Date(selectedDate.getTime());
            double luongCoBan = Double.parseDouble(luongCoBanText);
            double thuong = Double.parseDouble(thuongText);
            double khauTru = Double.parseDouble(khauTruText);

            final double MAX_SALARY_VALUE = 2147483647;
            if (luongCoBan > MAX_SALARY_VALUE || thuong > MAX_SALARY_VALUE || khauTru > MAX_SALARY_VALUE) {
                showNotification("Giá trị lương hoặc thưởng quá lớn!", NotificationType.ERROR);
                return false;
            }

            boolean success = controller.themLuong(idNguoiDung, thangNam, luongCoBan, thuong, khauTru);
            if (success) {
                showNotification("Thêm lương thành công!", NotificationType.SUCCESS);
                return true;
            } else {
                showNotification("Thêm lương thất bại!", NotificationType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showNotification("Dữ liệu số không hợp lệ!", NotificationType.ERROR);
            return false;
        }
    }

    private boolean suaLuong(JTextField txtIdLuong, JComboBox<String> cmbNhanVien, JDateChooser dateThangNam,
                             JTextField txtLuongCoBan, JTextField txtThuong, JTextField txtKhauTru,
                             Map<JComponent, JLabel> errorLabels) {
        resetAllValidationErrors(errorLabels);
        boolean isValid = true;

        String idLuongText = txtIdLuong.getText().trim();
        String selectedNhanVien = (String) cmbNhanVien.getSelectedItem();
        java.util.Date selectedDate = dateThangNam.getDate();
        String luongCoBanText = txtLuongCoBan.getText().trim();
        String thuongText = txtThuong.getText().trim();
        String khauTruText = txtKhauTru.getText().trim();

        // Validate ID Lương
        int idLuong = 0;
        if (idLuongText.isEmpty()) {
            showNotification("ID lương không được để trống", NotificationType.ERROR);
            isValid = false;
        } else {
            try {
                idLuong = Integer.parseInt(idLuongText);
                if (idLuong <= 0) {
                    showNotification("ID lương không hợp lệ", NotificationType.ERROR);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                showNotification("ID lương không hợp lệ", NotificationType.ERROR);
                isValid = false;
            }
        }

        // Validate Nhân viên
        if (selectedNhanVien == null || selectedNhanVien.isEmpty()) {
            showNotification("Nhân viên không được để trống", NotificationType.ERROR);
            isValid = false;
        }

        // Validate Tháng/Năm
        if (!ValidationUtils.validateThangNam(selectedDate, dateThangNam, errorLabels.get(dateThangNam))) {
            ValidationUtils.setErrorBorder(dateThangNam);
            isValid = false;
        } else {
            ValidationUtils.setValidBorder(dateThangNam);
        }

        // Validate Lương cơ bản
        if (luongCoBanText.isEmpty()) {
            ValidationUtils.showValidationError(txtLuongCoBan, errorLabels.get(txtLuongCoBan), "Lương cơ bản không được để trống");
            ValidationUtils.setErrorBorder(txtLuongCoBan);
            isValid = false;
        } else {
            try {
                double luongCoBan = Double.parseDouble(luongCoBanText);
                if (luongCoBan < 0) {
                    ValidationUtils.showValidationError(txtLuongCoBan, errorLabels.get(txtLuongCoBan), "Lương cơ bản không hợp lệ");
                    ValidationUtils.setErrorBorder(txtLuongCoBan);
                    isValid = false;
                } else {
                    ValidationUtils.setValidBorder(txtLuongCoBan);
                }
            } catch (NumberFormatException e) {
                ValidationUtils.showValidationError(txtLuongCoBan, errorLabels.get(txtLuongCoBan), "Lương cơ bản không hợp lệ");
                ValidationUtils.setErrorBorder(txtLuongCoBan);
                isValid = false;
            }
        }

        // Validate Thưởng
        if (thuongText.isEmpty()) {
            ValidationUtils.showValidationError(txtThuong, errorLabels.get(txtThuong), "Thưởng không được để trống");
            ValidationUtils.setErrorBorder(txtThuong);
            isValid = false;
        } else {
            try {
                double thuong = Double.parseDouble(thuongText);
                if (thuong < 0) {
                    ValidationUtils.showValidationError(txtThuong, errorLabels.get(txtThuong), "Thưởng không hợp lệ");
                    ValidationUtils.setErrorBorder(txtThuong);
                    isValid = false;
                } else {
                    ValidationUtils.setValidBorder(txtThuong);
                }
            } catch (NumberFormatException e) {
                ValidationUtils.showValidationError(txtThuong, errorLabels.get(txtThuong), "Thưởng sai định dạng");
                ValidationUtils.setErrorBorder(txtThuong);
                isValid = false;
            }
        }

        // Validate Khấu trừ
        if (khauTruText.isEmpty()) {
            ValidationUtils.showValidationError(txtKhauTru, errorLabels.get(txtKhauTru), "Khấu trừ không được để trống");
            ValidationUtils.setErrorBorder(txtKhauTru);
            isValid = false;
        } else {
            try {
                double khauTru = Double.parseDouble(khauTruText);
                if (khauTru < 0) {
                    ValidationUtils.showValidationError(txtKhauTru, errorLabels.get(txtKhauTru), "Khấu trừ không hợp lệ");
                    ValidationUtils.setErrorBorder(txtKhauTru);
                    isValid = false;
                } else {
                    ValidationUtils.setValidBorder(txtKhauTru);
                }
            } catch (NumberFormatException e) {
                ValidationUtils.showValidationError(txtKhauTru, errorLabels.get(txtKhauTru), "Khấu trừ sai định dạng");
                ValidationUtils.setErrorBorder(txtKhauTru);
                isValid = false;
            }
        }

        if (!isValid) {
            showNotification("Vui lòng kiểm tra các trường dữ liệu!", NotificationType.ERROR);
            return false;
        }

        try {
            int idNguoiDung = controller.getIdNguoiDungByHoTen(selectedNhanVien);
            if (idNguoiDung == -1) {
                showNotification("Không tìm thấy nhân viên!", NotificationType.ERROR);
                return false;
            }
            Date thangNam = new Date(selectedDate.getTime());
            double luongCoBan = Double.parseDouble(luongCoBanText);
            double thuong = Double.parseDouble(thuongText);
            double khauTru = Double.parseDouble(khauTruText);

            final double MAX_SALARY_VALUE = 2147483647;
            if (luongCoBan > MAX_SALARY_VALUE || thuong > MAX_SALARY_VALUE || khauTru > MAX_SALARY_VALUE) {
                showNotification("Giá trị lương hoặc thưởng quá lớn!", NotificationType.ERROR);
                return false;
            }

            boolean success = controller.suaLuong(idLuong, idNguoiDung, thangNam, luongCoBan, thuong, khauTru);
            if (success) {
                showNotification("Sửa lương thành công!", NotificationType.SUCCESS);
                return true;
            } else {
                showNotification("Sửa lương thất bại!", NotificationType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showNotification("Dữ liệu số không hợp lệ!", NotificationType.ERROR);
            return false;
        }
    }

    private void displayLuongDetails(Luong luong, JTextField txtIdLuong, JComboBox<String> cmbNhanVien,
                                     JDateChooser dateThangNam, JTextField txtLuongCoBan,
                                     JTextField txtThuong, JTextField txtKhauTru, JTextField txtTongLuong) {
        txtIdLuong.setText(String.valueOf(luong.getIdLuong()));
        for (int i = 0; i < cmbNhanVien.getItemCount(); i++) {
            String hoTen = cmbNhanVien.getItemAt(i);
            if (controller.getIdNguoiDungByHoTen(hoTen) == luong.getIdNguoiDung()) {
                cmbNhanVien.setSelectedIndex(i);
                break;
            }
        }
        dateThangNam.setDate(luong.getThangNam());
        txtLuongCoBan.setText(String.valueOf((long) luong.getLuongCoBan()));
        txtThuong.setText(String.valueOf((long) luong.getThuong()));
        txtKhauTru.setText(String.valueOf((long) luong.getKhauTru()));
        txtTongLuong.setText(String.valueOf((long) luong.getTongLuong()));
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(new LineBorder(borderColor, 1));

        JMenuItem menuItemView = createStyledMenuItem("Xem chi tiết");
        menuItemView.addActionListener(e -> xemChiTietLuong());

        JMenuItem menuItemEdit = createStyledMenuItem("Chỉnh sửa");
        menuItemEdit.addActionListener(e -> suaLuongAction());

        JMenuItem menuItemDelete = createStyledMenuItem("Xóa");
        menuItemDelete.setForeground(accentColor);
        menuItemDelete.addActionListener(e -> xoaLuongAction());

        popupMenu.add(menuItemView);
        popupMenu.addSeparator();
        popupMenu.add(menuItemEdit);
        popupMenu.addSeparator();
        popupMenu.add(menuItemDelete);

        tblLuong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = tblLuong.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tblLuong.getRowCount()) {
                    tblLuong.setRowSelectionInterval(row, row);
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        xemChiTietLuong();
                    }
                } else {
                    tblLuong.clearSelection();
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
        menuItem.setBackground(Color.WHITE);
        menuItem.setForeground(textColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(tableStripeColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE);
            }
        });

        return menuItem;
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
                g2d.setColor(type.color); // Sử dụng type.color
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        toastPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel titleLabel = new JLabel(type.title); // Sử dụng type.title
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
    public void loadLuongData(Object[] rowData, int highlightId) {
        // Kiểm tra kiểu dữ liệu
        if (!(rowData[0] instanceof Integer)) {
            System.err.println("Dữ liệu ID Lương không phải Integer: " + rowData[0]);
            rowData[0] = 0;
        }
        for (int i = 3; i <= 6; i++) {
            if (!(rowData[i] instanceof Double)) {
                System.err.println("Dữ liệu cột " + i + " không phải Double: " + rowData[i]);
                try {
                    rowData[i] = Double.parseDouble(rowData[i].toString());
                } catch (NumberFormatException e) {
                    System.err.println("Không thể chuyển đổi cột " + i + ": " + rowData[i]);
                    rowData[i] = 0.0;
                }
            }
        }

        if (highlightId > 0 && rowData[0].equals(highlightId)) {
            modelLuong.insertRow(0, rowData);
            originalData.add(0, Arrays.copyOf(rowData, rowData.length));

            highlightedRowId = highlightId;
            tblLuong.setRowSelectionInterval(0, 0);
            tblLuong.scrollRectToVisible(tblLuong.getCellRect(0, 0, true));

            if (highlightTimer != null && highlightTimer.isRunning()) {
                highlightTimer.stop();
            }
            highlightTimer = new javax.swing.Timer(10000, e -> resetHighlightState());
            highlightTimer.setRepeats(false);
            highlightTimer.start();

            tblLuong.repaint();
        } else {
            modelLuong.addRow(rowData);
            originalData.add(Arrays.copyOf(rowData, rowData.length));
        }

        double totalLuongCoBan = 0.0, totalThuong = 0.0, totalKhauTru = 0.0, totalTongLuong = 0.0;
        for (int i = 0; i < modelLuong.getRowCount(); i++) {
            totalLuongCoBan += (Double) modelLuong.getValueAt(i, 3);
            totalThuong += (Double) modelLuong.getValueAt(i, 4);
            totalKhauTru += (Double) modelLuong.getValueAt(i, 5);
            totalTongLuong += (Double) modelLuong.getValueAt(i, 6);
        }
        updateTotalRow(totalLuongCoBan, totalThuong, totalKhauTru, totalTongLuong);

        btnXuatFile.setEnabled(modelLuong.getRowCount() > 0);
    }

    public void loadLuongData(Object[] rowData) {
        loadLuongData(rowData, -1);
    }

    public void clearTable() {
        modelLuong.setRowCount(0);
        originalData.clear();
        btnXuatFile.setEnabled(false);
    }

    public void updateTotalRow(double totalLuongCoBan, double totalThuong, double totalKhauTru, double totalTongLuong) {
        if (modelTotalRow.getRowCount() == 0) {
            modelTotalRow.addRow(new Object[]{"Tổng:", "", "", 0.0, 0.0, 0.0, 0.0});
        }
        modelTotalRow.setValueAt("Tổng:", 0, 0);
        modelTotalRow.setValueAt("", 0, 1);
        modelTotalRow.setValueAt("", 0, 2);
        modelTotalRow.setValueAt(totalLuongCoBan, 0, 3);
        modelTotalRow.setValueAt(totalThuong, 0, 4);
        modelTotalRow.setValueAt(totalKhauTru, 0, 5);
        modelTotalRow.setValueAt(totalTongLuong, 0, 6);
        tableTotalRow.repaint();
    }

    private void xemChiTietLuong() {
        int selectedRow = tblLuong.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tblLuong.convertRowIndexToModel(selectedRow);
            try {
                int idLuong = Integer.parseInt(modelLuong.getValueAt(modelRow, 0).toString());
                Luong luong = controller.getLuongById(idLuong);
                if (luong != null) {
                    showLuongDialog(luong, DialogMode.VIEW);
                } else {
                    showNotification("Không tìm thấy thông tin lương!", NotificationType.ERROR);
                }
            } catch (NumberFormatException e) {
                showNotification("ID lương không hợp lệ!", NotificationType.ERROR);
            }
        } else {
            showNotification("Vui lòng chọn một dòng để xem chi tiết!", NotificationType.WARNING);
        }
    }

    private void suaLuongAction() {
        int selectedRow = tblLuong.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tblLuong.convertRowIndexToModel(selectedRow);
            try {
                int idLuong = Integer.parseInt(modelLuong.getValueAt(modelRow, 0).toString());
                Luong luong = controller.getLuongById(idLuong);
                if (luong != null) {
                    showLuongDialog(luong, DialogMode.EDIT);
                } else {
                    showNotification("Không tìm thấy thông tin lương!", NotificationType.ERROR);
                }
            } catch (NumberFormatException e) {
                showNotification("ID lương không hợp lệ!", NotificationType.ERROR);
            }
        } else {
            showNotification("Vui lòng chọn một dòng để sửa!", NotificationType.WARNING);
        }
    }

    private void xoaLuongAction() {
        int selectedRow = tblLuong.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tblLuong.convertRowIndexToModel(selectedRow);
            try {
                int idLuong = Integer.parseInt(modelLuong.getValueAt(modelRow, 0).toString());
                int choice = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Bạn có chắc chắn muốn xóa lương này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = controller.xoaLuong(idLuong);
                    if (success) {
                        showNotification("Xóa lương thành công!", NotificationType.SUCCESS);
                    } else {
                        showNotification("Xóa lương thất bại!", NotificationType.ERROR);
                    }
                }
            } catch (NumberFormatException e) {
                showNotification("ID lương không hợp lệ!", NotificationType.ERROR);
            }
        } else {
            showNotification("Vui lòng chọn một dòng để xóa!", NotificationType.WARNING);
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            controller.loadLuongData();
            showNotification("Dữ liệu đã được làm mới!", NotificationType.SUCCESS);
            return;
        }
        controller.timKiemLuong(keyword);
        int rowCount = modelLuong.getRowCount();
        if (rowCount > 0) {
            showNotification(String.format("Tìm thấy %d kết quả phù hợp!", rowCount), NotificationType.SUCCESS);
        } else {
            showNotification("Không tìm thấy kết quả phù hợp!", NotificationType.WARNING);
        }
    }

    private long lastHighlightTime = 0; // Thời gian kích hoạt highlight gần nhất
    private final long HIGHLIGHT_PROTECTION_MS = 2000; // 2 giây bảo vệ

    private void setupHighlightEventListeners() {
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, tblLuong);
        if (scrollPane != null) {
            scrollPane.addMouseWheelListener(e -> {
                if (highlightedRowId > 0) {
                    resetHighlightState();
                }
            });

            scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
                if (highlightedRowId > 0 && e.getValueIsAdjusting()) {
                    resetHighlightState();
                }
            });
        }

        tblLuong.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (highlightedRowId > 0 && isNavigationKey(e.getKeyCode())) {
                    resetHighlightState();
                }
            }

            private boolean isNavigationKey(int keyCode) {
                return keyCode == KeyEvent.VK_UP ||
                       keyCode == KeyEvent.VK_DOWN ||
                       keyCode == KeyEvent.VK_PAGE_UP ||
                       keyCode == KeyEvent.VK_PAGE_DOWN ||
                       keyCode == KeyEvent.VK_HOME ||
                       keyCode == KeyEvent.VK_END;
            }
        });

        tblLuong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (highlightedRowId > 0) {
                    resetHighlightState();
                }
            }
        });
    }

    private void resetHighlightState() {
        if (highlightTimer != null && highlightTimer.isRunning()) {
            highlightTimer.stop();
        }
        highlightedRowId = -1;
        refreshData(); // Làm mới dữ liệu thay vì chỉ repaint
    }

    @Override
    public void showSuccessToast(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }

    @Override
    public void showErrorMessage(String title, String message) {
        showNotification(message, NotificationType.ERROR);
    }

    @Override
    public void showMessage(String message, String title, int messageType) {
        NotificationType type;
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                type = NotificationType.ERROR;
                break;
            case JOptionPane.WARNING_MESSAGE:
                type = NotificationType.WARNING;
                break;
            default:
                type = NotificationType.SUCCESS;
                break;
        }
        showNotification(message, type);
    }

    @Override
    public void onDataChanged() {
        clearTable();
        controller.loadLuongData();
    }

    public DefaultTableModel getModelLuong() {
        return modelLuong;
    }

    public NumberFormat getCurrencyFormat() {
        return currencyFormat;
    }

    private interface SimpleDocumentListener extends DocumentListener {
        void update();

        @Override
        default void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        default void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        default void changedUpdate(DocumentEvent e) {
            update();
        }
    }
    public void refreshData() {
        modelLuong.setRowCount(0);
        originalData.clear();
        updateTotalRow(0.0, 0.0, 0.0, 0.0);
        tblLuong.repaint();
        controller.loadLuongData();
    }
}