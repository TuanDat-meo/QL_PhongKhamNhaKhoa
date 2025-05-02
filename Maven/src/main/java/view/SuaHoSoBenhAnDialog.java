package view;

import controller.HoSoBenhAnController;
import controller.BenhNhanController;
import model.HoSoBenhAn;
import model.BenhNhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SuaHoSoBenhAnDialog extends JDialog {
    // Controllers and data
    private HoSoBenhAnController hoSoBenhAnController;
    private BenhNhanController benhNhanController;
    private Map<String, Integer> tenBenhNhanToId;
    private HoSoBenhAnUI hoSoBenhAnUI;
    private int idHoSoCanSua;

    // UI Components
    private JComboBox<String> cmbTenBenhNhanSua;
    private JTextField txtChuanDoanSua;
    private JTextArea txtGhiChuSua;
    private JTextField txtNgayTaoSua;
    private JComboBox<String> cmbTrangThaiSua;
    
    // Color Scheme
    private final Color primaryColor = new Color(41, 128, 185);
    private final Color backgroundColor = new Color(240, 240, 240);
    private final Color textColor = new Color(44, 62, 80);
    private final Color buttonColor = new Color(52, 152, 219);
    private final Color buttonTextColor = Color.WHITE;
    private final Color selectionBackground = new Color(52, 152, 219, 50);
    private final Color borderColor = new Color(200, 200, 200);
    private final Color fieldBackground = new Color(255, 255, 255);
    private final Color focusedBorderColor = new Color(41, 128, 185);
    
    // Font configurations
    private final Font labelFont = new Font("Arial", Font.BOLD, 12);
    private final Font inputFont = new Font("Arial", Font.PLAIN, 12);
    private final Font titleFont = new Font("Arial", Font.BOLD, 18);
    private final Font buttonFont = new Font("Arial", Font.BOLD, 12);
    
    // Dimension constants
    private final Dimension inputDimension = new Dimension(250, 30);
    private final Dimension textAreaDimension = new Dimension(250, 80);
    private final Insets formInsets = new Insets(8, 5, 8, 5);
    private final Insets buttonPadding = new Insets(8, 15, 8, 15);
    private final Insets fieldPadding = new Insets(5, 8, 5, 8);

    public SuaHoSoBenhAnDialog(JFrame owner, String title, boolean modal,
                               HoSoBenhAnController hoSoBenhAnController,
                               BenhNhanController benhNhanController,
                               Map<String, Integer> tenBenhNhanToId,
                               HoSoBenhAnUI hoSoBenhAnUI,
                               int idHoSoCanSua,
                               String tenBenhNhanHienTai,
                               String chuanDoanHienTai,
                               String ghiChuHienTai,
                               Date ngayTaoHienTai,
                               String trangThaiHienTai) {
        super(owner, title, modal);
        this.hoSoBenhAnController = hoSoBenhAnController;
        this.benhNhanController = benhNhanController;
        this.tenBenhNhanToId = tenBenhNhanToId;
        this.hoSoBenhAnUI = hoSoBenhAnUI;
        this.idHoSoCanSua = idHoSoCanSua;

        initComponents();
        loadCurrentData(tenBenhNhanHienTai, chuanDoanHienTai, ghiChuHienTai, ngayTaoHienTai, trangThaiHienTai);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(new Dimension(500, 400));
        setLocationRelativeTo(owner);
    }

    private void loadCurrentData(String tenBenhNhanHienTai, String chuanDoanHienTai, 
                                String ghiChuHienTai, Date ngayTaoHienTai, String trangThaiHienTai) {
        cmbTenBenhNhanSua.setSelectedItem(tenBenhNhanHienTai);
        txtChuanDoanSua.setText(chuanDoanHienTai);
        txtGhiChuSua.setText(ghiChuHienTai);
        txtNgayTaoSua.setText(formatDate(ngayTaoHienTai));
        cmbTrangThaiSua.setSelectedItem(trangThaiHienTai);
    }

    private void initComponents() {
        // Main panel setup
        JPanel mainPanel = createMainPanel();
        
        // Title panel
        JPanel titlePanel = createTitlePanel();
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to dialog
        setContentPane(mainPanel);
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("CẬP NHẬT HỒ SƠ BỆNH ÁN");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(titleLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(new EmptyBorder(15, 10, 15, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = formInsets;
        
        // Add form components
        addFormComponents(panel, gbc);
        
        return panel;
    }
    
    private void addFormComponents(JPanel panel, GridBagConstraints gbc) {
        Dimension inputFieldDimension = new Dimension(250, 30);

        // Tên Bệnh nhân
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        panel.add(createStyledLabel("Tên Bệnh nhân:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        cmbTenBenhNhanSua = createStyledComboBox();
        cmbTenBenhNhanSua.setPreferredSize(inputFieldDimension);
        cmbTenBenhNhanSua.setModel(new DefaultComboBoxModel<>(tenBenhNhanToId.keySet().toArray(new String[0])));
        panel.add(cmbTenBenhNhanSua, gbc);

        // Chuẩn đoán
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        panel.add(createStyledLabel("Chuẩn đoán:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        txtChuanDoanSua = createStyledTextField();
        panel.add(txtChuanDoanSua, gbc);

        // Ghi chú
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        panel.add(createStyledLabel("Ghi chú:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        txtGhiChuSua = createStyledTextArea();
        JScrollPane ghiChuScrollPane = createStyledScrollPane(txtGhiChuSua);
        ghiChuScrollPane.setPreferredSize(new Dimension(250, 80));
        panel.add(ghiChuScrollPane, gbc);

        // Ngày tạo
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        panel.add(createStyledLabel("Ngày tạo:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        txtNgayTaoSua = createStyledTextField();
        txtNgayTaoSua.setPreferredSize(inputFieldDimension);
        panel.add(txtNgayTaoSua, gbc);

        // Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.2;
        panel.add(createStyledLabel("Trạng thái:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        cmbTrangThaiSua = createStyledComboBox();
        cmbTrangThaiSua.setPreferredSize(inputFieldDimension);
        cmbTrangThaiSua.setModel(new DefaultComboBoxModel<>(new String[]{"Mới", "Đang điều trị", "Hoàn tất"}));
        panel.add(cmbTrangThaiSua, gbc);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(backgroundColor);
        
        JButton btnHuy = createStyledButton("Hủy");
        btnHuy.setBackground(new Color(190, 190, 190));
        btnHuy.addActionListener(e -> {
            setVisible(false);
            dispose();
        });
        
        JButton btnLuu = createStyledButton("Lưu Thay Đổi");
        btnLuu.addActionListener(e -> luuThayDoi());
        
        panel.add(btnHuy);
        panel.add(btnLuu);
        
        return panel;
    }
    
    // UI Component Style Creation Methods
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        label.setForeground(textColor);
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(inputFont);
        textField.setForeground(textColor);
        textField.setBackground(fieldBackground);
        textField.setBorder(createTextBorder());
        textField.setPreferredSize(inputDimension);
        
        // Add focus listener for better visual feedback
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(new CompoundBorder(
                    new LineBorder(focusedBorderColor),
                    new EmptyBorder(fieldPadding)
                ));
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(createTextBorder());
            }
        });
        
        return textField;
    }
    
    private JTextArea createStyledTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setFont(inputFont);
        textArea.setForeground(textColor);
        textArea.setBackground(fieldBackground);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(null);
        
        return textArea;
    }

    private JScrollPane createStyledScrollPane(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(createTextBorder());
        scrollPane.setViewportBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getViewport().setBackground(fieldBackground);
        
        // Add focus listener to improve visual feedback
        textArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                scrollPane.setBorder(new CompoundBorder(
                    new LineBorder(focusedBorderColor),
                    new EmptyBorder(fieldPadding)
                ));
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                scrollPane.setBorder(createTextBorder());
            }
        });
        
        return scrollPane;
    }
    
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(inputFont);
        comboBox.setForeground(textColor);
        comboBox.setBackground(fieldBackground);
        comboBox.setBorder(createTextBorder());
        comboBox.setPreferredSize(inputDimension);
        comboBox.setRenderer(new StyledComboBoxRenderer());
        
        // Customizing the ComboBox UI
        comboBox.setUI(new CustomComboBoxUI());
        
        // Add focus listener for better visual feedback
        comboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                comboBox.setBorder(new CompoundBorder(
                    new LineBorder(focusedBorderColor),
                    new EmptyBorder(fieldPadding)
                ));
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                comboBox.setBorder(createTextBorder());
            }
        });
        
        return comboBox;
    }
    
    private class CustomComboBoxUI extends BasicComboBoxUI {
        @Override
        protected ComboPopup createPopup() {
            return new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    JScrollPane scroller = new JScrollPane(list, 
                                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    scroller.getVerticalScrollBar().setUI(new ModernScrollBarUI());
                    return scroller;
                }
                
                @Override
                protected void configureList() {
                    super.configureList();
                    list.setSelectionBackground(selectionBackground);
                    list.setSelectionForeground(textColor);
                    list.setFont(inputFont);
                    list.setBackground(fieldBackground);
                    list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }
                
                @Override
                protected void configurePopup() {
                    super.configurePopup();
                    setBorder(BorderFactory.createLineBorder(borderColor));
                    setBackground(fieldBackground);
                }
            };
        }
        
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton();
            button.setBackground(fieldBackground);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setIcon(createDropdownIcon());
            return button;
        }
        
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(fieldBackground);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        private Icon createDropdownIcon() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(primaryColor);
                    
                    int width = getIconWidth();
                    int height = getIconHeight();
                    int[] xPoints = {x, x + width, x + width/2};
                    int[] yPoints = {y, y, y + height};
                    g2.fillPolygon(xPoints, yPoints, 3);
                    g2.dispose();
                }
                
                @Override
                public int getIconWidth() {
                    return 10;
                }
                
                @Override
                public int getIconHeight() {
                    return 5;
                }
            };
        }
    }
    
    // Custom ScrollBar UI
    private class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = primaryColor;
            this.thumbDarkShadowColor = primaryColor;
            this.thumbHighlightColor = primaryColor;
            this.thumbLightShadowColor = primaryColor;
            this.trackColor = fieldBackground;
            this.trackHighlightColor = fieldBackground;
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Rounded scrollbar
            int arc = 8;
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, 
                            thumbBounds.width, thumbBounds.height, arc, arc);
            g2.dispose();
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setBackground(buttonColor);
        button.setForeground(buttonTextColor);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(buttonPadding));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusable(false);
        
        return button;
    }
    
    private javax.swing.border.Border createTextBorder() {
        return new CompoundBorder(
            new LineBorder(borderColor),
            new EmptyBorder(fieldPadding)
        );
    }
    
    // ComboBox Renderer
    private class StyledComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            renderer.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            
            if (isSelected) {
                renderer.setBackground(selectionBackground);
                renderer.setForeground(textColor);
            } else {
                renderer.setBackground(fieldBackground);
                renderer.setForeground(textColor);
            }
            
            return renderer;
        }
    }

    // Business Logic Methods
    private String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        }
        return "";
    }

    private Date parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            showWarningMessage("Định dạng ngày không hợp lệ (yyyy-MM-dd).");
            return null;
        }
    }

    private void luuThayDoi() {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        // Get data from form
        String tenBenhNhanMoi = (String) cmbTenBenhNhanSua.getSelectedItem();
        Integer idBenhNhanMoi = tenBenhNhanToId.get(tenBenhNhanMoi);
        String chuanDoanMoi = txtChuanDoanSua.getText().trim();
        String ghiChuMoi = txtGhiChuSua.getText();
        Date ngayTaoMoi = parseDate(txtNgayTaoSua.getText());
        if (ngayTaoMoi == null) return;
        String trangThaiMoi = (String) cmbTrangThaiSua.getSelectedItem();

        // Create updated object
        HoSoBenhAn hoSoBenhAnDaSua = new HoSoBenhAn(idBenhNhanMoi, chuanDoanMoi, ghiChuMoi, ngayTaoMoi, trangThaiMoi);
        hoSoBenhAnDaSua.setIdHoSo(idHoSoCanSua);
        
        // Save to database
        try {
            hoSoBenhAnController.suaHoSoBenhAn(hoSoBenhAnDaSua);
            hoSoBenhAnUI.lamMoiDanhSach();
            dispose();
            showInfoMessage("Sửa hồ sơ bệnh án thành công.");
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật hồ sơ bệnh án: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        String tenBenhNhan = (String) cmbTenBenhNhanSua.getSelectedItem();
        if (tenBenhNhan == null || tenBenhNhan.trim().isEmpty()) {
            showWarningMessage("Vui lòng chọn tên bệnh nhân.");
            return false;
        }
        
        Integer idBenhNhan = tenBenhNhanToId.get(tenBenhNhan);
        if (idBenhNhan == null) {
            showErrorMessage("Không tìm thấy ID bệnh nhân.");
            return false;
        }
        
        String chuanDoan = txtChuanDoanSua.getText().trim();
        if (chuanDoan.isEmpty()) {
            showWarningMessage("Vui lòng nhập chuẩn đoán.");
            return false;
        }
        
        return true;
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}