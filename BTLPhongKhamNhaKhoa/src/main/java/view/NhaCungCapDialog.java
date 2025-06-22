package view;

import controller.NhaCungCapController;
import model.NhaCungCap;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class NhaCungCapDialog extends JDialog {

    // --- Components ---
    private JTextField txtTenNCC;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;
    private JTextField txtMaSoThue;
    private JTextField txtNgayDangKy;
    private JButton btnShowCalendar;

    // --- Error Labels for validation ---
    private JLabel lblTenNCCError;
    private JLabel lblDiaChiError;
    private JLabel lblSoDienThoaiError;
    private JLabel lblMaSoThueError;
    private JLabel lblNgayDangKyError;

    private JButton btnLuu;
    private JButton btnHuy;

    // --- Controller & Data ---
    private NhaCungCapController controller;
    private NhaCungCap nhaCungCapToEdit;
    private NhaCungCapUI nhaCungCapUI;

    // --- Styling ---
    private final Color primaryColor = new Color(79, 129, 189);
    private final Color successColor = new Color(86, 156, 104);
    private final Color errorColor = new Color(192, 80, 77);
    private final Color panelColor = Color.WHITE;
    private final Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font errorFont = new Font("Segoe UI", Font.ITALIC, 12);
    private final Border defaultBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
    );
    private final Border errorBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(errorColor, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
    );

    public NhaCungCapDialog(JFrame parent, NhaCungCapController ctrl, NhaCungCap ncc, NhaCungCapUI ui) {
        super(parent, (ncc == null ? "Thêm Nhà Cung Cấp" : "Sửa Nhà Cung Cấp"), true);
        this.controller = ctrl;
        this.nhaCungCapToEdit = ncc;
        this.nhaCungCapUI = ui;

        initUI();
        populateData();
        setupActionListeners();

        pack(); // Adjust dialog size to fit components
        setLocationRelativeTo(parent); // Center dialog relative to parent
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        headerPanel.setBackground(primaryColor);
        JLabel titleLabel = new JLabel(nhaCungCapToEdit == null ? "THÊM NHÀ CUNG CẤP MỚI" : "CHỈNH SỬA THÔNG TIN NHÀ CUNG CẤP");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        return headerPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(panelColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Row 0: Mã NCC (Auto) ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        inputPanel.add(createLabel("Mã NCC (Tự động):"), gbc);
        JTextField txtMaNCC = new JTextField("Tự động");
        txtMaNCC.setEnabled(false);
        txtMaNCC.setFont(regularFont);
        txtMaNCC.setBackground(new Color(236, 240, 241));
        gbc.gridx = 1; gbc.weightx = 1;
        inputPanel.add(txtMaNCC, gbc);

        // --- Row 1: Tên NCC ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        inputPanel.add(createRequiredLabel("Tên NCC:"), gbc);
        txtTenNCC = createTextField();
        gbc.gridx = 1; gbc.weightx = 1;
        inputPanel.add(txtTenNCC, gbc);
        lblTenNCCError = createErrorLabel();
        gbc.gridy = 2;
        inputPanel.add(lblTenNCCError, gbc);

        // --- Row 2: Địa chỉ ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        inputPanel.add(createRequiredLabel("Địa chỉ:"), gbc);
        txtDiaChi = createTextField();
        gbc.gridx = 1; gbc.weightx = 1;
        inputPanel.add(txtDiaChi, gbc);
        lblDiaChiError = createErrorLabel();
        gbc.gridy = 4;
        inputPanel.add(lblDiaChiError, gbc);

        // --- Row 3: Số điện thoại ---
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        inputPanel.add(createRequiredLabel("Số điện thoại:"), gbc);
        txtSoDienThoai = createTextField();
        gbc.gridx = 1; gbc.weightx = 1;
        inputPanel.add(txtSoDienThoai, gbc);
        lblSoDienThoaiError = createErrorLabel();
        gbc.gridy = 6;
        inputPanel.add(lblSoDienThoaiError, gbc);

        // --- Row 4: Mã số thuế ---
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        inputPanel.add(createRequiredLabel("Mã số thuế:"), gbc); // Changed to required
        txtMaSoThue = createTextField();
        gbc.gridx = 1; gbc.weightx = 1;
        inputPanel.add(txtMaSoThue, gbc);
        lblMaSoThueError = createErrorLabel();
        gbc.gridy = 8;
        inputPanel.add(lblMaSoThueError, gbc);

        // --- Row 5: Ngày đăng ký ---
        gbc.gridx = 0; gbc.gridy = 9; gbc.weightx = 0;
        inputPanel.add(createRequiredLabel("Ngày đăng ký:"), gbc);
        
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setBackground(panelColor);
        txtNgayDangKy = createTextField();
        txtNgayDangKy.setEditable(false);
        txtNgayDangKy.setBackground(panelColor);

        // Get a simple calendar icon
        ImageIcon calendarIcon = new ImageIcon(new byte[] {
            71, 73, 70, 56, 57, 97, 16, 0, 16, 0, -111, 0, 0, -1, -1, -1, 0, 0, 0, 33, -7, 4, 1, 0, 0, 0, 0, 44, 0, 0, 0, 0, 16, 0, 16, 0, 0, 2, 59, -100, -113, -87, 11, -11, -28, 73, 104, -88, 115, 66, -88, 8, 86, 12, 70, 107, -74, 98, -10, 112, 103, -111, 123, -127, -29, -60, 110, 118, 5, 87, 43, 117, 39, -65, 84, -94, 6, 23, -48, -107, 12, 42, 68, -84, -108, 115, -115, -4, 65, 43, 40, -42, 1, 10, 40, 6, 8, 32, 16, 24, 9, 4, 18, 5, 0, 59
        });
        btnShowCalendar = new JButton(calendarIcon);
        btnShowCalendar.setPreferredSize(new Dimension(35, 35));
        btnShowCalendar.setMargin(new Insets(0,0,0,0));
        btnShowCalendar.setBorder(defaultBorder);

        datePanel.add(txtNgayDangKy, BorderLayout.CENTER);
        datePanel.add(btnShowCalendar, BorderLayout.EAST);
        
        gbc.gridx = 1; gbc.weightx = 1;
        inputPanel.add(datePanel, gbc);
        lblNgayDangKyError = createErrorLabel();
        gbc.gridy = 10;
        inputPanel.add(lblNgayDangKyError, gbc);

        return inputPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)));
        
        btnLuu = createRoundedButton("Lưu", successColor, Color.WHITE, 8);
        btnLuu.setPreferredSize(new Dimension(100, 38));
        
        btnHuy = createRoundedButton("Hủy", new Color(108, 117, 125), Color.WHITE, 8);
        btnHuy.setPreferredSize(new Dimension(100, 38));
        
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        
        return buttonPanel;
    }
    
    private void populateData() {
        if (nhaCungCapToEdit != null) { // Edit mode
            ((JTextField) ((JPanel) getContentPane().getComponent(1)).getComponent(1)).setText(nhaCungCapToEdit.getMaNCC());
            txtTenNCC.setText(nhaCungCapToEdit.getTenNCC());
            txtDiaChi.setText(nhaCungCapToEdit.getDiaChi());
            txtSoDienThoai.setText(nhaCungCapToEdit.getSoDienThoai());
            txtMaSoThue.setText(nhaCungCapToEdit.getMaSoThue());
            if (nhaCungCapToEdit.getNgayDangKy() != null) {
                txtNgayDangKy.setText(nhaCungCapToEdit.getNgayDangKy().format(DateTimeFormatter.ISO_LOCAL_DATE));
            } else {
                txtNgayDangKy.setText("");
            }
        } else { // Add mode
            txtNgayDangKy.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }
    
    private void setupActionListeners() {
        btnLuu.addActionListener(e -> saveNhaCungCap());
        btnHuy.addActionListener(e -> dispose());
        btnShowCalendar.addActionListener(e -> {
            LocalDate currentDate = LocalDate.now();
            try {
                currentDate = LocalDate.parse(txtNgayDangKy.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ignored) {}

            DatePicker picker = new DatePicker(this, currentDate);
            LocalDate selectedDate = picker.pickDate();
            
            if (selectedDate != null) {
                txtNgayDangKy.setText(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
                resetFieldValidation(txtNgayDangKy, lblNgayDangKyError);
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        resetValidation();

        // 1. Tên NCC
        if (txtTenNCC.getText().trim().isEmpty()) {
            showError(txtTenNCC, lblTenNCCError, "Vui lòng nhập tên nhà cung cấp.");
            isValid = false;
        }

        // 2. Địa chỉ
        if (txtDiaChi.getText().trim().isEmpty()) {
            showError(txtDiaChi, lblDiaChiError, "Vui lòng nhập địa chỉ.");
            isValid = false;
        }
        
        // 3. Số điện thoại
        String soDienThoai = txtSoDienThoai.getText().trim();
        if (soDienThoai.isEmpty()) {
             showError(txtSoDienThoai, lblSoDienThoaiError, "Vui lòng nhập số điện thoại.");
             isValid = false;
        } else if (!soDienThoai.matches("^0\\d{9}$")) {
            showError(txtSoDienThoai, lblSoDienThoaiError, "Số điện thoại không hợp lệ (gồm 10 số, bắt đầu bằng 0).");
            isValid = false;
        }
        
        // 4. Mã số thuế (Required)
        String maSoThue = txtMaSoThue.getText().trim();
        if (maSoThue.isEmpty()) {
            showError(txtMaSoThue, lblMaSoThueError, "Vui lòng nhập mã số thuế.");
            isValid = false;
        } else if (!Pattern.matches("\\d{10}|\\d{13}", maSoThue)) {
             showError(txtMaSoThue, lblMaSoThueError, "Mã số thuế phải là 10 hoặc 13 chữ số.");
             isValid = false;
        }

        // 5. Ngày đăng ký
        String ngayDangKyStr = txtNgayDangKy.getText().trim();
        if (ngayDangKyStr.isEmpty()) {
            showError(txtNgayDangKy, lblNgayDangKyError, "Vui lòng chọn ngày đăng ký.");
            isValid = false;
        } else {
            try {
                LocalDate ngayDangKy = LocalDate.parse(ngayDangKyStr, DateTimeFormatter.ISO_LOCAL_DATE);
                if (ngayDangKy.isAfter(LocalDate.now())) {
                    showError(txtNgayDangKy, lblNgayDangKyError, "Ngày đăng ký không được ở tương lai.");
                    isValid = false;
                }
            } catch (DateTimeParseException ex) {
                showError(txtNgayDangKy, lblNgayDangKyError, "Định dạng ngày không hợp lệ.");
                isValid = false;
            }
        }
        
        return isValid;
    }

    private void saveNhaCungCap() {
        if (!validateInputs()) {
            return;
        }

        String tenNCC = txtTenNCC.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String maSoThue = txtMaSoThue.getText().trim();
        LocalDate ngayDangKy = LocalDate.parse(txtNgayDangKy.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);

        if (nhaCungCapToEdit == null) { // Thêm mới
            NhaCungCap nccMoi = new NhaCungCap(null, tenNCC, diaChi, soDienThoai, ngayDangKy, maSoThue);
            String newId = controller.themNhaCungCap(nccMoi);
            if (newId != null) {
                nhaCungCapUI.showNotification("Thêm nhà cung cấp thành công!", NhaCungCapUI.NotificationType.SUCCESS);
                // THAY ĐỔI: Gọi onDataChanged với ID mới để highlight
                nhaCungCapUI.onDataChanged(newId);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else { // Sửa
            nhaCungCapToEdit.setTenNCC(tenNCC);
            nhaCungCapToEdit.setDiaChi(diaChi);
            nhaCungCapToEdit.setSoDienThoai(soDienThoai);
            nhaCungCapToEdit.setMaSoThue(maSoThue);
            nhaCungCapToEdit.setNgayDangKy(ngayDangKy);
            if (controller.suaNhaCungCap(nhaCungCapToEdit)) {
                nhaCungCapUI.showNotification("Cập nhật thành công!", NhaCungCapUI.NotificationType.SUCCESS);
                // THAY ĐỔI: Gọi onDataChanged với ID đã sửa để highlight
                nhaCungCapUI.onDataChanged(nhaCungCapToEdit.getMaNCC());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // --- Helper methods ---

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(boldFont);
        return label;
    }

    private JLabel createRequiredLabel(String text) {
        JLabel label = new JLabel("<html>" + text + " <font color='red'>*</font></html>");
        label.setFont(boldFont);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(25);
        textField.setFont(regularFont);
        textField.setBorder(defaultBorder);
        return textField;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(errorFont);
        label.setForeground(errorColor);
        return label;
    }
    
    private void showError(JTextField field, JLabel errorLabel, String message) {
        if (field != null) field.setBorder(errorBorder);
        errorLabel.setText(message);
    }
     private void showError(JPanel fieldPanel, JLabel errorLabel, String message) {
        fieldPanel.setBorder(errorBorder);
        errorLabel.setText(message);
    }
    
    private void resetFieldValidation(JTextField field, JLabel errorLabel) {
        field.setBorder(defaultBorder);
        errorLabel.setText(" ");
    }
    
    private void resetValidation() {
        resetFieldValidation(txtTenNCC, lblTenNCCError);
        resetFieldValidation(txtDiaChi, lblDiaChiError);
        resetFieldValidation(txtSoDienThoai, lblSoDienThoaiError);
        resetFieldValidation(txtMaSoThue, lblMaSoThueError);
        
        // Reset border for the date panel's text field
        txtNgayDangKy.setBorder(defaultBorder);
        lblNgayDangKyError.setText(" ");
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
            @Override public boolean isOpaque() { return false; }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        return button;
    }

    /**
     * A custom DatePicker dialog.
     */
    private class DatePicker extends JDialog {
        private LocalDate selectedDate;
        private JSpinner yearSpinner;
        private JComboBox<String> monthComboBox;
        private JPanel calendarPanel;
        private YearMonth currentYearMonth;
        
        private final String[] MONTHS = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};

        DatePicker(Dialog owner, LocalDate initialDate) {
            super(owner, "Chọn ngày", true);
            this.selectedDate = initialDate;
            this.currentYearMonth = YearMonth.from(initialDate);
            
            setLayout(new BorderLayout(10, 10));
            
            add(createHeaderPanel(), BorderLayout.NORTH);
            
            calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
            calendarPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
            add(calendarPanel, BorderLayout.CENTER);
            
            updateCalendar();
            
            pack();
            setResizable(false);
            setLocationRelativeTo(owner);
        }

        private JPanel createHeaderPanel() {
            JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            
            JButton prevMonth = new JButton("<");
            prevMonth.addActionListener(e -> {
                currentYearMonth = currentYearMonth.minusMonths(1);
                updateControls();
                updateCalendar();
            });

            JButton nextMonth = new JButton(">");
            nextMonth.addActionListener(e -> {
                currentYearMonth = currentYearMonth.plusMonths(1);
                updateControls();
                updateCalendar();
            });

            monthComboBox = new JComboBox<>(MONTHS);
            monthComboBox.addActionListener(e -> {
                currentYearMonth = YearMonth.of(currentYearMonth.getYear(), monthComboBox.getSelectedIndex() + 1);
                updateCalendar();
            });
            
            yearSpinner = new JSpinner(new SpinnerNumberModel(currentYearMonth.getYear(), 1900, 2100, 1));
            yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
            yearSpinner.addChangeListener(e -> {
                currentYearMonth = YearMonth.of((int)yearSpinner.getValue(), currentYearMonth.getMonthValue());
                updateCalendar();
            });
            
            updateControls();

            header.add(prevMonth);
            header.add(monthComboBox);
            header.add(yearSpinner);
            header.add(nextMonth);
            
            return header;
        }

        private void updateControls() {
            yearSpinner.setValue(currentYearMonth.getYear());
            monthComboBox.setSelectedIndex(currentYearMonth.getMonthValue() - 1);
        }

        private void updateCalendar() {
            calendarPanel.removeAll();
            
            String[] headers = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
            for(String header : headers) {
                JLabel lbl = new JLabel(header, SwingConstants.CENTER);
                lbl.setFont(boldFont);
                calendarPanel.add(lbl);
            }
            
            LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
            int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
            
            for(int i = 1; i < dayOfWeek; i++) {
                calendarPanel.add(new JLabel(""));
            }
            
            for(int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
                LocalDate date = currentYearMonth.atDay(day);
                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.setMargin(new Insets(2,2,2,2));
                dayButton.setFont(regularFont);
                dayButton.setFocusable(false);
                
                if (date.equals(LocalDate.now())) {
                    dayButton.setBorder(BorderFactory.createLineBorder(primaryColor, 1));
                }
                
                if (date.equals(selectedDate)) {
                    dayButton.setBackground(primaryColor);
                    dayButton.setForeground(Color.WHITE);
                }
                
                dayButton.addActionListener(e -> {
                    selectedDate = date;
                    dispose();
                });
                
                calendarPanel.add(dayButton);
            }
            
            calendarPanel.revalidate();
            calendarPanel.repaint();
            pack();
        }

        public LocalDate pickDate() {
            setVisible(true);
            return this.selectedDate;
        }
    }
}