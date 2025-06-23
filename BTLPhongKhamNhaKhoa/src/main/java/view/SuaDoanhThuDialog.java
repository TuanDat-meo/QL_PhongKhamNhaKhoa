package view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import util.ValidationUtils;
import com.toedter.calendar.JDateChooser;

public class SuaDoanhThuDialog extends JDialog {
    private JTextField txtIdDoanhThu;
    private JTextField txtIdHoaDon;
    private JDateChooser dateChooserThangNam;
    private JTextField txtTongDoanhThu;
    private JButton btnSua;
    private JButton btnHuy;
    private DoanhThuUI mainUI;
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    private DecimalFormat currencyFormat = new DecimalFormat("#,###.##");
    private Map<JComponent, JLabel> errorLabels = new HashMap<>();

    // Định nghĩa màu sắc và font từ DoanhThuUI
    private static final Color primaryColor = new Color(79, 129, 189);
    private static final Color backgroundColor = new Color(248, 249, 250);
    private static final Color textColor = new Color(33, 37, 41);
    private static final Color borderColor = new Color(222, 226, 230);
    private static final Color successColor = new Color(86, 156, 104);
    private static final Color errorColor = new Color(220, 53, 69);
    private static final Color buttonTextColor = Color.WHITE;
    private static final Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font errorFont = new Font("Segoe UI", Font.ITALIC, 11);
    private static final Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

    // Enum NotificationType
    public enum NotificationType {
        SUCCESS(successColor, "Thành công"),
        ERROR(errorColor, "Lỗi");

        final Color color;
        final String title;

        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }

    public SuaDoanhThuDialog(JFrame parent, Object[] data, DoanhThuUI mainUI) {
        super(parent, "Sửa Thông Tin Doanh Thu", true);
        this.mainUI = mainUI;

        setSize(480, 510);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        initializeUI(data);
        setupEnterKeyNavigation();
        getRootPane().setDefaultButton(btnSua);
    }

    private void initializeUI(Object[] data) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(18, 25, 18, 25));
        JLabel titleLabel = new JLabel("CHỈNH SỬA THÔNG TIN DOANH THU");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);
        gbc.weightx = 1.0;

        // ID Doanh Thu
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblIdDoanhThu = new JLabel("ID Doanh Thu: ");
        lblIdDoanhThu.setFont(regularFont);
        JPanel idDoanhThuLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        idDoanhThuLabelPanel.setBackground(backgroundColor);
        idDoanhThuLabelPanel.add(lblIdDoanhThu);
        formPanel.add(idDoanhThuLabelPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtIdDoanhThu = createStyledTextField(data[0].toString(), false);
        txtIdDoanhThu.setBackground(new Color(245, 245, 245));
        formPanel.add(txtIdDoanhThu, gbc);

        // ID Hóa Đơn
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblIdHoaDon = new JLabel("ID Hóa Đơn: ");
        lblIdHoaDon.setFont(regularFont);
        JPanel idHoaDonLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        idHoaDonLabelPanel.setBackground(backgroundColor);
        idHoaDonLabelPanel.add(lblIdHoaDon);
        JLabel redStar1 = new JLabel("*");
        redStar1.setForeground(errorColor);
        redStar1.setFont(regularFont);
        idHoaDonLabelPanel.add(redStar1);
        formPanel.add(idHoaDonLabelPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtIdHoaDon = createStyledTextField(data[1].toString(), true);
        formPanel.add(txtIdHoaDon, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 8, 4);
        JLabel idHoaDonErrorLabel = createErrorLabel();
        formPanel.add(idHoaDonErrorLabel, gbc);
        errorLabels.put(txtIdHoaDon, idHoaDonErrorLabel);

        // Tháng/Năm
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(8, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblThangNam = new JLabel("Tháng/Năm: ");
        lblThangNam.setFont(regularFont);
        JPanel thangNamLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        thangNamLabelPanel.setBackground(backgroundColor);
        thangNamLabelPanel.add(lblThangNam);
        JLabel redStar2 = new JLabel("*");
        redStar2.setForeground(errorColor);
        redStar2.setFont(regularFont);
        thangNamLabelPanel.add(redStar2);
        formPanel.add(thangNamLabelPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dateChooserThangNam = createStyledDateChooser();
        try {
            dateChooserThangNam.setDate(monthYearFormat.parse(data[3].toString()));
        } catch (ParseException e) {
            dateChooserThangNam.setDate(new Date());
        }
        formPanel.add(dateChooserThangNam, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 8, 4);
        JLabel thangNamErrorLabel = createErrorLabel();
        formPanel.add(thangNamErrorLabel, gbc);
        errorLabels.put(dateChooserThangNam, thangNamErrorLabel);

        // Tổng Doanh Thu
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(8, 4, 8, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblTongDoanhThu = new JLabel("Tổng Doanh Thu: ");
        lblTongDoanhThu.setFont(regularFont);
        JPanel tongDoanhThuLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tongDoanhThuLabelPanel.setBackground(backgroundColor);
        tongDoanhThuLabelPanel.add(lblTongDoanhThu);
        formPanel.add(tongDoanhThuLabelPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        String tongDoanhThuFormatted;
        try {
            double value = Double.parseDouble(data[4].toString().replace(" VND", ""));
            tongDoanhThuFormatted = currencyFormat.format(value) + " VND";
        } catch (NumberFormatException e) {
            tongDoanhThuFormatted = data[4].toString();
        }
        txtTongDoanhThu = createStyledTextField(tongDoanhThuFormatted, false);
        txtTongDoanhThu.setBackground(new Color(245, 245, 245));
        formPanel.add(txtTongDoanhThu, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        Dimension buttonSize = new Dimension(90, 36);
        btnHuy = createRoundedButton("Hủy", Color.WHITE, textColor, 10);
        btnHuy.setBorder(new LineBorder(borderColor, 1));
        btnHuy.setPreferredSize(buttonSize);
        btnHuy.setMinimumSize(buttonSize);
        btnHuy.setMaximumSize(buttonSize);
        btnHuy.addActionListener(e -> {
            resetAllValidationErrors();
            dispose();
        });

        btnSua = createRoundedButton("Lưu", successColor, buttonTextColor, 10);
        btnSua.setPreferredSize(buttonSize);
        btnSua.setMinimumSize(buttonSize);
        btnSua.setMaximumSize(buttonSize);
        btnSua.addActionListener(e -> luuDoanhThu());

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnSua);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Xử lý viền động cho ID Hóa Đơn
        txtIdHoaDon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                ValidationUtils.clearValidationError(txtIdHoaDon, errorLabels.get(txtIdHoaDon));
                String idText = txtIdHoaDon.getText().trim();
                if (!idText.isEmpty()) {
                    try {
                        int id = Integer.parseInt(idText);
                        double amt = mainUI.getDoanhThuController().getHoaDonAmount(id);
                        if (amt > 0) {
                            txtTongDoanhThu.setText(currencyFormat.format(amt) + " VND");
                            txtTongDoanhThu.setCaretPosition(0);
                            txtIdHoaDon.setForeground(Color.BLACK);
                            txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                                new model.CustomBorder(8, successColor),
                                BorderFactory.createEmptyBorder(5, 12, 5, 12)
                            ));
                        } else {
                            txtTongDoanhThu.setText("Không tìm thấy hóa đơn");
                            txtIdHoaDon.setForeground(Color.RED);
                            txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                                new model.CustomBorder(8, errorColor),
                                BorderFactory.createEmptyBorder(5, 12, 5, 12)
                            ));
                        }
                    } catch (NumberFormatException ex) {
                        txtTongDoanhThu.setText("ID không hợp lệ");
                        txtIdHoaDon.setForeground(Color.RED);
                        txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                            new model.CustomBorder(8, errorColor),
                            BorderFactory.createEmptyBorder(5, 12, 5, 12)
                        ));
                    }
                } else {
                    txtTongDoanhThu.setText("");
                    txtIdHoaDon.setForeground(Color.BLACK);
                    txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                        new model.CustomBorder(8, borderColor),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)
                    ));
                }
            }
        });

        // Xử lý viền động và kiểm tra cho Tháng/Năm
        JTextField dateTextField = (JTextField) dateChooserThangNam.getDateEditor().getUiComponent();
        dateTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                ValidationUtils.clearValidationError(dateChooserThangNam, errorLabels.get(dateChooserThangNam));
                String text = dateTextField.getText().trim();
                if (!text.isEmpty()) {
                    try {
                        new SimpleDateFormat("MM/yyyy").parse(text);
                        dateTextField.setForeground(Color.BLACK);
                    } catch (ParseException ex) {
                        dateTextField.setForeground(Color.RED);
                    }
                } else {
                    dateTextField.setForeground(Color.BLACK);
                }
            }
        });

        dateChooserThangNam.getDateEditor().addPropertyChangeListener("date", evt -> {
            ValidationUtils.clearValidationError(dateChooserThangNam, errorLabels.get(dateChooserThangNam));
            if (evt.getNewValue() != null) {
                try {
                    String text = dateTextField.getText();
                    new SimpleDateFormat("MM/yyyy").parse(text);
                    dateTextField.setForeground(Color.BLACK);
                } catch (ParseException ex) {
                    dateTextField.setForeground(Color.RED);
                }
            } else {
                dateTextField.setForeground(Color.BLACK);
            }
        });
    }

    private JTextField createStyledTextField(String text, boolean editable) {
        JTextField textField = new JTextField(text);
        textField.setFont(regularFont);
        textField.setForeground(textColor);
        textField.setEnabled(editable);
        textField.setPreferredSize(new Dimension(230, 38));
        textField.setMinimumSize(new Dimension(230, 38));
        textField.setMaximumSize(new Dimension(230, 38));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new model.CustomBorder(8, borderColor),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        textField.setBackground(editable ? Color.WHITE : new Color(245, 245, 245));
        textField.setOpaque(true);
        textField.setHorizontalAlignment(JTextField.LEFT);
        return textField;
    }

    private JLabel createErrorLabel() {
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(errorFont);
        errorLabel.setForeground(errorColor);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        errorLabel.setPreferredSize(new Dimension(230, 16));
        errorLabel.setMinimumSize(new Dimension(230, 16));
        return errorLabel;
    }

    private JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(regularFont);
        dateChooser.setPreferredSize(new Dimension(230, 38));
        dateChooser.setBorder(new model.CustomBorder(8, borderColor));
        JTextField dateTextField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateTextField.setFont(regularFont);
        dateTextField.setBorder(new EmptyBorder(5, 12, 5, 12));
        dateTextField.setBackground(Color.WHITE);
        dateTextField.setOpaque(true);
        dateTextField.setHorizontalAlignment(JTextField.LEFT);
        dateChooser.setDateFormatString("MM/yyyy");
        return dateChooser;
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2d.dispose();
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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(darkenColor(bgColor));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - 0.1f));
    }

    private void resetAllValidationErrors() {
        ValidationUtils.clearValidationError(txtIdHoaDon, errorLabels.get(txtIdHoaDon));
        ValidationUtils.clearValidationError(dateChooserThangNam, errorLabels.get(dateChooserThangNam));
    }

    private void luuDoanhThu() {
        String idDoanhThuStr = txtIdDoanhThu.getText().trim();
        String idHoaDonStr = txtIdHoaDon.getText().trim();
        String dateText = ((JTextField) dateChooserThangNam.getDateEditor().getUiComponent()).getText().trim();
        Date thangNam = dateChooserThangNam.getDate();

        resetAllValidationErrors();

        boolean isValid = true;

        // Đặt lại viền mặc định trước khi kiểm tra
        txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
            new model.CustomBorder(8, borderColor),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        JTextField dateTextField = (JTextField) dateChooserThangNam.getDateEditor().getUiComponent();
        dateTextField.setBorder(BorderFactory.createCompoundBorder(
            new model.CustomBorder(8, borderColor),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        // Lấy ID Doanh Thu mà không kiểm tra lỗi
        int idDoanhThu = 0;
        try {
            idDoanhThu = Integer.parseInt(idDoanhThuStr);
        } catch (NumberFormatException e) {
            // Không hiển thị lỗi, chỉ tiếp tục
        }

        // Validate ID Hóa Đơn
        int idHoaDon = 0;
        double tongDoanhThu = 0.0;
        if (!ValidationUtils.validateRequired(idHoaDonStr, txtIdHoaDon, "ID Hóa Đơn")) {
            ValidationUtils.showValidationError(txtIdHoaDon, errorLabels.get(txtIdHoaDon), "ID Hóa Đơn không được để trống");
            txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                new model.CustomBorder(8, errorColor),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            txtIdHoaDon.requestFocus();
            isValid = false;
        } else {
            try {
                idHoaDon = Integer.parseInt(idHoaDonStr);
                tongDoanhThu = mainUI.getDoanhThuController().getHoaDonAmount(idHoaDon);
                if (tongDoanhThu <= 0) {
                    ValidationUtils.showValidationError(txtIdHoaDon, errorLabels.get(txtIdHoaDon), "Không tìm thấy hóa đơn");
                    txtTongDoanhThu.setText("Không tìm thấy hóa đơn");
                    txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                        new model.CustomBorder(8, errorColor),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)
                    ));
                    txtIdHoaDon.requestFocus();
                    isValid = false;
                } else {
                    txtTongDoanhThu.setText(currencyFormat.format(tongDoanhThu) + " VND");
                    txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                        new model.CustomBorder(8, successColor),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)
                    ));
                }
            } catch (NumberFormatException ex) {
                ValidationUtils.showValidationError(txtIdHoaDon, errorLabels.get(txtIdHoaDon), "ID Hóa Đơn không hợp lệ");
                txtTongDoanhThu.setText("ID không hợp lệ");
                txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                    new model.CustomBorder(8, errorColor),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
                txtIdHoaDon.requestFocus();
                isValid = false;
            }
        }

        // Validate Tháng/Năm
        if (dateText.isEmpty()) {
            ValidationUtils.showValidationError(dateChooserThangNam, errorLabels.get(dateChooserThangNam), "Tháng/Năm không được để trống");
            dateTextField.setBorder(BorderFactory.createCompoundBorder(
                new model.CustomBorder(8, errorColor),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            dateChooserThangNam.requestFocus();
            isValid = false;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                sdf.setLenient(false);
                thangNam = sdf.parse(dateText);
                dateTextField.setBorder(BorderFactory.createCompoundBorder(
                    new model.CustomBorder(8, successColor),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
            } catch (ParseException ex) {
                ValidationUtils.showValidationError(dateChooserThangNam, errorLabels.get(dateChooserThangNam), "Tháng/Năm không hợp lệ");
                dateTextField.setBorder(BorderFactory.createCompoundBorder(
                    new model.CustomBorder(8, errorColor),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
                dateChooserThangNam.requestFocus();
                isValid = false;
            }
        }

        // Hiển thị thông báo lỗi nếu không hợp lệ
        if (!isValid) {
            String tongThuText = txtTongDoanhThu.getText().trim();
            if (idHoaDonStr.isEmpty()) {
                showNotification("ID Hóa Đơn không được để trống!", NotificationType.ERROR);
            } else if (tongThuText.equals("Không tìm thấy hóa đơn") || tongThuText.equals("ID không hợp lệ")) {
                showNotification("ID không hợp lệ hoặc không tìm thấy hóa đơn!", NotificationType.ERROR);
            }
            if (dateText.isEmpty()) {
                showNotification("Tháng/Năm không được để trống!", NotificationType.ERROR);
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                    sdf.setLenient(false);
                    sdf.parse(dateText);
                } catch (ParseException ex) {
                    showNotification("Tháng/Năm không hợp lệ!", NotificationType.ERROR);
                }
            }
            return;
        }

        // Gọi suaDoanhThu
        try {
            mainUI.getDoanhThuController().suaDoanhThu(idDoanhThu, thangNam, tongDoanhThu, idHoaDon);
            txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                new model.CustomBorder(8, successColor),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            dateTextField.setBorder(BorderFactory.createCompoundBorder(
                new model.CustomBorder(8, successColor),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            dispose();
        } catch (Exception ex) {
            txtIdHoaDon.setBorder(BorderFactory.createCompoundBorder(
                new model.CustomBorder(8, errorColor),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            dateTextField.setBorder(BorderFactory.createCompoundBorder(
                new model.CustomBorder(8, errorColor),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            showNotification("Lỗi khi lưu doanh thu!", NotificationType.ERROR);
        }
    }

    private void setupEnterKeyNavigation() {
        JComponent[] components = new JComponent[]{
                txtIdHoaDon,
                dateChooserThangNam.getDateEditor().getUiComponent()
        };
        for (int i = 0; i < components.length - 1; i++) {
            final int nextIndex = i + 1;
            components[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        components[nextIndex].requestFocus();
                    }
                }
            });
        }
    }

    private void showSuccessToast(String message) {
        JDialog toastDialog = new JDialog((Frame) null, false);
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
                SwingUtilities.invokeLater(toastDialog::dispose);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showNotification(String message, NotificationType type) {
        JDialog toastDialog = new JDialog((Frame) null, false);
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
        JLabel messageLabel = new JLabel("<html><div style='width: 300px;'>" + message + "</div></html>");
        messageLabel.setFont(regularFont);
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
                SwingUtilities.invokeLater(toastDialog::dispose);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}