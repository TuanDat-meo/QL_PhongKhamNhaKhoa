package view;

import controller.BacSiController;
import model.BacSi;
import model.NguoiDung;
import model.NguoiDungItem;
import model.PhongKham;
import model.PhongKhamItem;
import util.CustomBorder;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BacSiDialog extends JDialog {
    private JTextField nameField;
    private JTextField specialtyField;
    private JTextField degreeField;
    private JTextField experienceField;
    private JComboBox<NguoiDungItem> userComboBox;
    private JComboBox<PhongKhamItem> clinicComboBox;    
    private JComboBox<String> specialtyComboBox;
    private BacSi currentBacSi;
    private boolean confirmed = false;
    private BacSiController bacSiController;
    private Map<JComponent, JLabel> errorLabels = new HashMap<>();
    
    private Color primaryColor = new Color(79, 129, 189);
    private Color secondaryColor = new Color(141, 180, 226);
    private Color accentColor = new Color(192, 80, 77);
    private Color successColor = new Color(86, 156, 104);
    private Color backgroundColor = new Color(248, 249, 250);
    private Color textColor = new Color(33, 37, 41);
    private Color buttonTextColor = Color.WHITE;
    private Color borderColor = new Color(222, 226, 230);
    private Color errorColor = new Color(220, 53, 69);
    
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font errorFont = new Font("Segoe UI", Font.ITALIC, 11);
    private int newDoctorId = -1;
    public int getNewDoctorId() {
        return newDoctorId;
    }
    public BacSiDialog(JFrame parent, BacSi bacSi) {
        super(parent, "Thêm Mới Bác Sĩ", true);
        this.currentBacSi = bacSi;
        this.bacSiController = new BacSiController();
        initializeComponents();
        
        if (bacSi != null) {
            setTitle("Chỉnh Sửa Thông Tin Bác Sĩ");
            loadDoctorData();
        }
    }    
    private void initializeComponents() {
        Color requiredFieldColor = new Color(255, 0, 0);
        setModal(true);
        setSize(480, 580); // Increased height for more fields
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header panel matching createInputDialog style
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(18, 25, 18, 25));
        
        JLabel titleLabel = new JLabel(currentBacSi == null ? "THÊM MỚI BÁC SĨ" : "CHỈNH SỬA THÔNG TIN BÁC SĨ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Form panel matching createInputDialog layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.weightx = 1.0;
        
        // User selection field (only for new doctor)
        if (currentBacSi == null) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.LINE_START;
            JLabel lblUser = new JLabel("Người Dùng: ");
            lblUser.setFont(regularFont);
            JPanel userLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            userLabelPanel.setBackground(Color.WHITE);
            userLabelPanel.add(lblUser);
            JLabel starUser = new JLabel("*");
            starUser.setForeground(requiredFieldColor);
            starUser.setFont(regularFont);
            userLabelPanel.add(starUser);
            formPanel.add(userLabelPanel, gbc);
            
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            userComboBox = createStyledComboBoxWithScroll();
            userComboBox.setPreferredSize(new Dimension(230, 32));
            
            // Add default selection option
            userComboBox.addItem(new NguoiDungItem(null)); // This will show "Lựa chọn"
            
            // Load available users
            List<NguoiDung> availableUsers = bacSiController.getAllDoctorUsers();
            for (NguoiDung user : availableUsers) {
                userComboBox.addItem(new NguoiDungItem(user));
            }
            
            // Set custom renderer
            userComboBox.setRenderer(new CustomComboBoxRenderer());
            
            formPanel.add(userComboBox, gbc);
            
            // Error label for User
            gbc.gridx = 1;
            gbc.gridy++;
            gbc.insets = new Insets(0, 4, 5, 4);
            JLabel lblUserError = createErrorLabel();
            formPanel.add(lblUserError, gbc);
            errorLabels.put(userComboBox, lblUserError);
            
            // Reset insets for next field
            gbc.insets = new Insets(3, 4, 0, 4);
        }
        
        // Doctor name field
        gbc.gridx = 0;
        gbc.gridy = currentBacSi == null ? 2 : 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblName = new JLabel("Họ Tên Bác Sĩ: ");
        lblName.setFont(regularFont);
        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nameLabelPanel.setBackground(Color.WHITE);
        nameLabelPanel.add(lblName);
        JLabel starName = new JLabel("*");
        starName.setForeground(requiredFieldColor);
        starName.setFont(regularFont);
        nameLabelPanel.add(starName);
        formPanel.add(nameLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        nameField = createStyledTextField();
        nameField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(nameField, gbc);
        
        // Error label for Name
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 5, 4);
        JLabel lblNameError = createErrorLabel();
        formPanel.add(lblNameError, gbc);
        errorLabels.put(nameField, lblNameError);
        
        // Specialty field - FIX: Initialize specialtyComboBox here
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblSpecialty = new JLabel("Chuyên Khoa: ");
        lblSpecialty.setFont(regularFont);
        JPanel specialtyLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        specialtyLabelPanel.setBackground(Color.WHITE);
        specialtyLabelPanel.add(lblSpecialty);
        JLabel starSpecialty = new JLabel("*");
        starSpecialty.setForeground(requiredFieldColor);
        starSpecialty.setFont(regularFont);
        specialtyLabelPanel.add(starSpecialty);
        formPanel.add(specialtyLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        // FIX: Initialize specialtyComboBox properly
        specialtyComboBox = createStyledComboBoxWithScroll();
        specialtyComboBox.setPreferredSize(new Dimension(230, 32));
        
        // Add dental specialties
        for (String specialty : getDentalSpecialties()) {
            specialtyComboBox.addItem(specialty);
        }
        
        // Set custom renderer
        specialtyComboBox.setRenderer(new CustomComboBoxRenderer());
        
        formPanel.add(specialtyComboBox, gbc);
        
        // Error label for Specialty
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 5, 4);
        JLabel lblSpecialtyError = createErrorLabel();
        formPanel.add(lblSpecialtyError, gbc);
        errorLabels.put(specialtyComboBox, lblSpecialtyError);
        
        // Degree field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblDegree = new JLabel("Bằng Cấp: ");
        lblDegree.setFont(regularFont);
        JPanel degreeLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        degreeLabelPanel.setBackground(Color.WHITE);
        degreeLabelPanel.add(lblDegree);
        JLabel starDegree = new JLabel("*");
        starDegree.setForeground(requiredFieldColor);
        starDegree.setFont(regularFont);
        degreeLabelPanel.add(starDegree);
        formPanel.add(degreeLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        degreeField = createStyledTextField();
        degreeField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(degreeField, gbc);
        
        // Error label for Degree
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 5, 4);
        JLabel lblDegreeError = createErrorLabel();
        formPanel.add(lblDegreeError, gbc);
        errorLabels.put(degreeField, lblDegreeError);
        
        // Experience field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblExperience = new JLabel("Kinh Nghiệm (năm): ");
        lblExperience.setFont(regularFont);
        JPanel experienceLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        experienceLabelPanel.setBackground(Color.WHITE);
        experienceLabelPanel.add(lblExperience);
        JLabel starExperience = new JLabel("*");
        starExperience.setForeground(requiredFieldColor);
        starExperience.setFont(regularFont);
        experienceLabelPanel.add(starExperience);
        formPanel.add(experienceLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        experienceField = createStyledTextField();
        experienceField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(experienceField, gbc);
        
        // Error label for Experience
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 5, 4);
        JLabel lblExperienceError = createErrorLabel();
        formPanel.add(lblExperienceError, gbc);
        errorLabels.put(experienceField, lblExperienceError);
        
        // Clinic field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblClinic = new JLabel("Phòng Khám: ");
        lblClinic.setFont(regularFont);
        JPanel clinicLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        clinicLabelPanel.setBackground(Color.WHITE);
        clinicLabelPanel.add(lblClinic);
        JLabel starClinic = new JLabel("*");
        starClinic.setForeground(requiredFieldColor);
        starClinic.setFont(regularFont);
        clinicLabelPanel.add(starClinic);
        formPanel.add(clinicLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        clinicComboBox = createStyledComboBoxWithScroll();
        clinicComboBox.setPreferredSize(new Dimension(230, 32));
        
        // Add default selection option
        clinicComboBox.addItem(new PhongKhamItem(null)); // This will show "Lựa chọn"
        
        // Load available clinics
        List<PhongKham> clinics = bacSiController.getAllPhongKham();
        for (PhongKham clinic : clinics) {
            clinicComboBox.addItem(new PhongKhamItem(clinic));
        }
        
        // Set custom renderer
        clinicComboBox.setRenderer(new CustomComboBoxRenderer());
        
        formPanel.add(clinicComboBox, gbc);
        
        // Error label for Clinic
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 5, 4);
        JLabel lblClinicError = createErrorLabel();
        formPanel.add(lblClinicError, gbc);
        errorLabels.put(clinicComboBox, lblClinicError);
        
        // Button panel matching createInputDialog style
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        Dimension buttonSize = new Dimension(90, 36);
        
        JButton btnLuu = createRoundedButton("Lưu", successColor, buttonTextColor, 10, false);
        btnLuu.setPreferredSize(buttonSize);
        btnLuu.setMinimumSize(buttonSize);
        btnLuu.setMaximumSize(buttonSize);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.addActionListener(e -> saveDoctor());
        
        JButton btnHuy = createRoundedButton("Hủy", accentColor, buttonTextColor, 10, false);
        btnHuy.setBorder(new LineBorder(borderColor, 1));
        btnHuy.setPreferredSize(buttonSize);
        btnHuy.setMinimumSize(buttonSize);
        btnHuy.setMaximumSize(buttonSize);
        btnHuy.setFocusPainted(false);
        btnHuy.setBorderPainted(false);
        btnHuy.addActionListener(e -> {
            resetAllValidationErrors();
            dispose();
        });
        
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set dialog content pane
        setContentPane(mainPanel);
        
        // Set up Enter key navigation between fields
        setupEnterKeyNavigation();
        
        // Set default button (responds to Enter key)
        getRootPane().setDefaultButton(btnLuu);
    }
    private String[] getDentalSpecialties() {
        return new String[]{
            "Lựa chọn",
            "Nha khoa tổng quát",
            "Nha khoa thẩm mỹ",
            "Chỉnh nha",
            "Nha chu",
            "Nội nha", 
            "Phẫu thuật hàm mặt",
            "Nha khoa trẻ em",
            "Cấy ghép Implant",
            "Phục hình răng",
            "Răng hàm mặt",
            "Nha khoa dự phòng",
            "X quang răng",
            "Bệnh lý miệng"
        };
    }
    private JPanel createScrollableFormPanel() {
        // Create the main form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.weightx = 1.0;
        
        Color requiredFieldColor = new Color(255, 0, 0);
        
        // User selection field (only for new doctor)
        if (currentBacSi == null) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.LINE_START;
            JLabel lblUser = new JLabel("Người Dùng: ");
            lblUser.setFont(regularFont);
            JPanel userLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            userLabelPanel.setBackground(Color.WHITE);
            userLabelPanel.add(lblUser);
            JLabel starUser = new JLabel("*");
            starUser.setForeground(requiredFieldColor);
            starUser.setFont(regularFont);
            userLabelPanel.add(starUser);
            formPanel.add(userLabelPanel, gbc);
            
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            userComboBox = createStyledComboBoxWithScroll();
            userComboBox.setPreferredSize(new Dimension(230, 32));
            
            // Add default selection option
            userComboBox.addItem(new NguoiDungItem(null)); // This will show "Lựa chọn"
            
            // Load available users with custom renderer for long text
            List<NguoiDung> availableUsers = bacSiController.getAllDoctorUsers();
            for (NguoiDung user : availableUsers) {
                userComboBox.addItem(new NguoiDungItem(user));
            }
            
            // Set custom renderer for combo box to handle long text
            userComboBox.setRenderer(new CustomComboBoxRenderer());
            
            formPanel.add(userComboBox, gbc);
            
            // Error label for User
            gbc.gridx = 1;
            gbc.gridy++;
            gbc.insets = new Insets(0, 4, 4, 4);
            JLabel lblUserError = createErrorLabel();
            formPanel.add(lblUserError, gbc);
            errorLabels.put(userComboBox, lblUserError);
        }
        
        // Doctor name field
        gbc.gridx = 0;
        gbc.gridy = currentBacSi == null ? 2 : 0;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblName = new JLabel("Họ Tên Bác Sĩ: ");
        lblName.setFont(regularFont);
        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nameLabelPanel.setBackground(Color.WHITE);
        nameLabelPanel.add(lblName);
        JLabel starName = new JLabel("*");
        starName.setForeground(requiredFieldColor);
        starName.setFont(regularFont);
        nameLabelPanel.add(starName);
        formPanel.add(nameLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        nameField = createStyledTextField();
        nameField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(nameField, gbc);
        
        // Error label for Name
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblNameError = createErrorLabel();
        formPanel.add(lblNameError, gbc);
        errorLabels.put(nameField, lblNameError);
        
        // Specialty field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblSpecialty = new JLabel("Chuyên Khoa: ");
        lblSpecialty.setFont(regularFont);
        JPanel specialtyLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        specialtyLabelPanel.setBackground(Color.WHITE);
        specialtyLabelPanel.add(lblSpecialty);
        JLabel starSpecialty = new JLabel("*");
        starSpecialty.setForeground(requiredFieldColor);
        starSpecialty.setFont(regularFont);
        specialtyLabelPanel.add(starSpecialty);
        formPanel.add(specialtyLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        specialtyComboBox = createStyledComboBoxWithScroll();
        specialtyComboBox.setPreferredSize(new Dimension(230, 32));
        
        // Thêm các chuyên khoa
        for (String specialty : getDentalSpecialties()) {
            specialtyComboBox.addItem(specialty);
        }
        
        // Set custom renderer cho specialty combo box
        specialtyComboBox.setRenderer(new CustomComboBoxRenderer());
        
        formPanel.add(specialtyComboBox, gbc);
        
        // Error label for Specialty
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblSpecialtyError = createErrorLabel();
        formPanel.add(lblSpecialtyError, gbc);
        errorLabels.put(specialtyComboBox, lblSpecialtyError);
        // Degree field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblDegree = new JLabel("Bằng Cấp: ");
        lblDegree.setFont(regularFont);
        JPanel degreeLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        degreeLabelPanel.setBackground(Color.WHITE);
        degreeLabelPanel.add(lblDegree);
        JLabel starDegree = new JLabel("*");
        starDegree.setForeground(requiredFieldColor);
        starDegree.setFont(regularFont);
        degreeLabelPanel.add(starDegree);
        formPanel.add(degreeLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        degreeField = createStyledTextField();
        degreeField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(degreeField, gbc);
        
        // Error label for Degree
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblDegreeError = createErrorLabel();
        formPanel.add(lblDegreeError, gbc);
        errorLabels.put(degreeField, lblDegreeError);
        
        // Experience field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblExperience = new JLabel("Kinh Nghiệm (năm): ");
        lblExperience.setFont(regularFont);
        JPanel experienceLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        experienceLabelPanel.setBackground(Color.WHITE);
        experienceLabelPanel.add(lblExperience);
        JLabel starExperience = new JLabel("*");
        starExperience.setForeground(requiredFieldColor);
        starExperience.setFont(regularFont);
        experienceLabelPanel.add(starExperience);
        formPanel.add(experienceLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        experienceField = createStyledTextField();
        experienceField.setPreferredSize(new Dimension(230, 32));
        formPanel.add(experienceField, gbc);
        
        // Error label for Experience
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblExperienceError = createErrorLabel();
        formPanel.add(lblExperienceError, gbc);
        errorLabels.put(experienceField, lblExperienceError);
        
        // Clinic field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(3, 4, 0, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lblClinic = new JLabel("Phòng Khám: ");
        lblClinic.setFont(regularFont);
        JPanel clinicLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        clinicLabelPanel.setBackground(Color.WHITE);
        clinicLabelPanel.add(lblClinic);
        JLabel starClinic = new JLabel("*");
        starClinic.setForeground(requiredFieldColor);
        starClinic.setFont(regularFont);
        clinicLabelPanel.add(starClinic);
        formPanel.add(clinicLabelPanel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        clinicComboBox = createStyledComboBoxWithScroll();
        clinicComboBox.setPreferredSize(new Dimension(230, 32));
        
        // Add default selection option
        clinicComboBox.addItem(new PhongKhamItem(null)); // This will show "Lựa chọn"
        
        // Load available clinics
        List<PhongKham> clinics = bacSiController.getAllPhongKham();
        for (PhongKham clinic : clinics) {
            clinicComboBox.addItem(new PhongKhamItem(clinic));
        }
        
        // Set custom renderer for clinic combo box
        clinicComboBox.setRenderer(new CustomComboBoxRenderer());
        
        formPanel.add(clinicComboBox, gbc);
        
        // Error label for Clinic
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(0, 4, 4, 4);
        JLabel lblClinicError = createErrorLabel();
        formPanel.add(lblClinicError, gbc);
        errorLabels.put(clinicComboBox, lblClinicError);
        
        // Wrap the form panel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        
        // Style the scroll bar
        styleScrollBar(scrollPane.getVerticalScrollBar());
        
        // Create a panel to contain the scroll pane
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(Color.WHITE);
        containerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return containerPanel;
    }
    
    // Enhanced ComboBox UI with both horizontal and vertical scroll capability
    private class ScrollableComboBoxUI extends MetalComboBoxUI {
        @Override
        protected ComboPopup createPopup() {
            return new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    // Create scroll pane with both scrollbars enabled
                    JScrollPane scroller = new JScrollPane(list,
                            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    
                    // Configure scroll increments
                    scroller.getHorizontalScrollBar().setUnitIncrement(16);
                    scroller.getVerticalScrollBar().setUnitIncrement(16);
                    scroller.getHorizontalScrollBar().setBlockIncrement(48);
                    scroller.getVerticalScrollBar().setBlockIncrement(48);
                    
                    // Style both scroll bars
                    styleScrollBar(scroller.getHorizontalScrollBar());
                    styleScrollBar(scroller.getVerticalScrollBar());
                    
                    // Ensure list supports horizontal scrolling
                    list.setPrototypeCellValue(null); // Remove prototype to allow natural sizing
                    
                    return scroller;
                }
                
                @Override
                public void show() {
                    // Calculate optimal dimensions for the popup
                    int maxWidth = comboBox.getWidth();
                    int totalHeight = 0;
                    
                    // Calculate the width needed for the longest item
                    FontMetrics fm = comboBox.getFontMetrics(comboBox.getFont());
                    for (int i = 0; i < comboBox.getItemCount(); i++) {
                        Object item = comboBox.getItemAt(i);
                        if (item != null) {
                            String text = item.toString();
                            int textWidth = fm.stringWidth(text) + 50; // Add padding for scrollbar and margins
                            maxWidth = Math.max(maxWidth, textWidth);
                        }
                    }
                    
                    // Set reasonable limits for popup width
                    maxWidth = Math.min(maxWidth, 600); // Maximum width limit
                    maxWidth = Math.max(maxWidth, comboBox.getWidth()); // Minimum width is combobox width
                    
                    // Calculate height based on number of visible items
                    int visibleItemCount = Math.min(comboBox.getItemCount(), comboBox.getMaximumRowCount());
                    int itemHeight = 28; // Estimated item height with padding
                    int popupHeight = visibleItemCount * itemHeight + 10; // Add some padding
                    
                    // Add extra height if horizontal scrollbar will be visible
                    if (maxWidth > comboBox.getWidth()) {
                        popupHeight += 20; // Height for horizontal scrollbar
                    }
                    
                    // Set the calculated dimensions
                    Dimension popupSize = new Dimension(maxWidth, popupHeight);
                    scroller.setPreferredSize(popupSize);
                    scroller.setMaximumSize(popupSize);
                    
                    // Configure the list to support proper scrolling
                    list.setVisibleRowCount(visibleItemCount);
                    list.setFixedCellWidth(-1); // Allow variable width
                    
                    super.show();
                }
            };
        }
    }    
    // Enhanced custom renderer for combo boxes with better text handling
    private class CustomComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                    boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (c instanceof JLabel && value != null) {
                JLabel label = (JLabel) c;
                String text = value.toString();
                
                // Set the full text without truncation
                label.setText(text);
                label.setFont(regularFont);
                
                // Set tooltip for all items
                label.setToolTipText(text);
                
                // Style for selected and unselected items
                if (isSelected) {
                    label.setBackground(primaryColor);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(textColor);
                }
                
                // Calculate and set the preferred size to accommodate full text
                FontMetrics fm = label.getFontMetrics(label.getFont());
                int textWidth = fm.stringWidth(text) + 20; // Add some padding
                int textHeight = fm.getHeight() + 8; // Add vertical padding
                
                label.setPreferredSize(new Dimension(textWidth, Math.max(textHeight, 25)));
                
                // Enable proper horizontal sizing
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            }
            
            return c;
        }
    }    
    private void styleScrollBar(JScrollBar scrollBar) {
        scrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = secondaryColor;
                this.trackColor = backgroundColor;
            }            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                
                // Different styling for horizontal vs vertical scrollbars
                if (scrollBar.getOrientation() == JScrollBar.HORIZONTAL) {
                    g2.fillRoundRect(thumbBounds.x, thumbBounds.y + 2, thumbBounds.width, 
                                   thumbBounds.height - 4, 8, 8);
                } else {
                    g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, 
                                   thumbBounds.height, 8, 8);
                }
                g2.dispose();
            }            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });        
        // Set appropriate size based on orientation
        if (scrollBar.getOrientation() == JScrollBar.HORIZONTAL) {
            scrollBar.setPreferredSize(new Dimension(0, 12));
        } else {
            scrollBar.setPreferredSize(new Dimension(12, 0));
        }
    }    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(regularFont);
        textField.setBorder(new CompoundBorder(
                new CustomBorder(8, borderColor),
                new EmptyBorder(5, 12, 5, 12)));
        textField.setBackground(Color.WHITE);        
        // Add tooltip support for long text
        textField.addCaretListener(e -> {
            String text = textField.getText();
            if (text.length() > 30) {
                textField.setToolTipText(text);
            } else {
                textField.setToolTipText(null);
            }
        });
        
        return textField;
    }    
    private JComboBox createStyledComboBox() {
        JComboBox comboBox = new JComboBox();
        comboBox.setFont(regularFont);
        comboBox.setBackground(Color.WHITE);
        comboBox.setMaximumRowCount(8);        
        return comboBox;
    }    
    private JComboBox createStyledComboBoxWithScroll() {
        JComboBox comboBox = new JComboBox();
        comboBox.setFont(regularFont);
        comboBox.setBackground(Color.WHITE);
        comboBox.setMaximumRowCount(8);
        comboBox.setUI(new ScrollableComboBoxUI());        
        return comboBox;
    }    
    private JLabel createErrorLabel() {
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(errorFont);
        errorLabel.setForeground(errorColor);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        errorLabel.setVisible(true);
        errorLabel.setPreferredSize(new Dimension(230, 16));
        errorLabel.setMinimumSize(new Dimension(230, 16));
        return errorLabel;
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
        
        if (reducedPadding) {
            button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        } else {
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }
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
    private void showValidationError(JComponent component, String message) {
        if (component instanceof JTextField) {
            component.setBorder(new CompoundBorder(
                    new LineBorder(errorColor, 1, true),
                    new EmptyBorder(5, 12, 5, 12)));
        } else if (component instanceof JComboBox) {
            component.setBorder(new LineBorder(errorColor, 1, true));
        }
        JLabel errorLabel = errorLabels.get(component);
        if (errorLabel != null) {
            errorLabel.setText("<html><div style='width: 200px;'>" + message + "</div></html>");
            errorLabel.setVisible(true);
        }
    }    
    private void clearValidationError(JComponent component) {
        if (component instanceof JTextField) {
            component.setBorder(new CompoundBorder(
                    new CustomBorder(8, borderColor),
                    new EmptyBorder(5, 12, 5, 12)));
        } else if (component instanceof JComboBox) {
            component.setBorder(null);
        }
        JLabel errorLabel = errorLabels.get(component);
        if (errorLabel != null) {
            errorLabel.setText(" ");
            errorLabel.setVisible(true);
        }
    }    
    private void resetAllValidationErrors() {
        clearValidationError(nameField);
        clearValidationError(specialtyComboBox); // THAY ĐỔI từ specialtyField
        clearValidationError(degreeField);
        clearValidationError(experienceField);
        clearValidationError(clinicComboBox);
        if (userComboBox != null) {
            clearValidationError(userComboBox);
        }        
        
        for (JLabel errorLabel : errorLabels.values()) {
            errorLabel.setText(" ");
            errorLabel.setVisible(true);
        }
    }
    private void loadDoctorData() {
        // If editing an existing doctor, fetch the latest data from database
        if (currentBacSi != null) {
            BacSi refreshedBacSi = bacSiController.getBacSiById(currentBacSi.getIdBacSi());
            if (refreshedBacSi != null) {
                currentBacSi = refreshedBacSi;
            }
        }
        // Set fields with current doctor data
        nameField.setText(currentBacSi.getHoTenBacSi());
        
        // Set specialty - tìm và chọn chuyên khoa phù hợp
        String currentSpecialty = currentBacSi.getChuyenKhoa();
        boolean specialtyFound = false;
        for (int i = 0; i < specialtyComboBox.getItemCount(); i++) {
            if (specialtyComboBox.getItemAt(i).equals(currentSpecialty)) {
                specialtyComboBox.setSelectedIndex(i);
                specialtyFound = true;
                break;
            }
        }
        // Nếu không tìm thấy chuyên khoa trong danh sách, thêm vào và chọn
        if (!specialtyFound && currentSpecialty != null && !currentSpecialty.trim().isEmpty()) {
            specialtyComboBox.addItem(currentSpecialty);
            specialtyComboBox.setSelectedItem(currentSpecialty);
        }
        
        degreeField.setText(currentBacSi.getBangCap());
        experienceField.setText(String.valueOf(currentBacSi.getKinhNghiem()));
        
        // Add the current user to combobox for editing
        if (userComboBox == null) {
            userComboBox = createStyledComboBox();
        }
        NguoiDung user = bacSiController.getNguoiDungById(currentBacSi.getIdNguoiDung());
        if (user != null) {
            userComboBox.removeAllItems();
            userComboBox.addItem(new NguoiDungItem(user));
            userComboBox.setEnabled(false); // Can't change user when editing
        }        
        
        // Select the current clinic - FIXED VERSION
        PhongKham currentClinic = bacSiController.getPhongKhamById(currentBacSi.getIdPhongKham());
        if (currentClinic != null) {
            for (int i = 0; i < clinicComboBox.getItemCount(); i++) {
                PhongKhamItem item = clinicComboBox.getItemAt(i);
                
                // Check if item and its PhongKham are not null before comparing
                if (item != null && item.getPhongKham() != null && 
                    item.getPhongKham().getIdPhongKham() == currentClinic.getIdPhongKham()) {
                    clinicComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            // If currentClinic is null, select the default option (first item which is null)
            clinicComboBox.setSelectedIndex(0);
        }
    }    
    private void saveDoctor() {
        // Clear all previous validation errors
        resetAllValidationErrors();
        
        String hoTenBacSi = nameField.getText().trim();
        String chuyenKhoa = (String) specialtyComboBox.getSelectedItem(); // THAY ĐỔI
        String bangCap = degreeField.getText().trim();
        String kinhNghiemStr = experienceField.getText().trim();
        
        boolean isValid = true;        
        // Validate doctor name
        if (hoTenBacSi.isEmpty()) {
            showValidationError(nameField, "Họ tên bác sĩ không được để trống");
            if (isValid) {
                nameField.requestFocus();
                isValid = false;
            }
        } else if (hoTenBacSi.length() < 2) {
            showValidationError(nameField, "Họ tên bác sĩ phải có ít nhất 2 ký tự");
            if (isValid) {
                nameField.requestFocus();
                isValid = false;
            }
        } else if (!hoTenBacSi.matches("^[\\p{L}\\s]+$")) {
            showValidationError(nameField, "Họ tên chỉ được chứa chữ cái và khoảng trắng");
            if (isValid) {
                nameField.requestFocus();
                isValid = false;
            }
        }        
        
        // Validate specialty - THAY ĐỔI VALIDATION
        if (chuyenKhoa == null || chuyenKhoa.equals("Lựa chọn") || chuyenKhoa.trim().isEmpty()) {
            showValidationError(specialtyComboBox, "Vui lòng chọn chuyên khoa");
            if (isValid) {
                specialtyComboBox.requestFocus();
                isValid = false;
            }
        }
        // Validate degree
        if (bangCap.isEmpty()) {
            showValidationError(degreeField, "Bằng cấp không được để trống");
            if (isValid) {
                degreeField.requestFocus();
                isValid = false;
            }
        } else if (bangCap.length() < 2) {
            showValidationError(degreeField, "Bằng cấp phải có ít nhất 2 ký tự");
            if (isValid) {
                degreeField.requestFocus();
                isValid = false;
            }
        }        
        // Validate experience
        int kinhNghiem;
        if (kinhNghiemStr.isEmpty()) {
            showValidationError(experienceField, "Kinh nghiệm không được để trống");
            if (isValid) {
                experienceField.requestFocus();
                isValid = false;
            }
        } else {
            try {
                kinhNghiem = Integer.parseInt(kinhNghiemStr);
                if (kinhNghiem < 0) {
                    showValidationError(experienceField, "Kinh nghiệm phải là số không âm");
                    if (isValid) {
                        experienceField.requestFocus();
                        isValid = false;
                    }
                } else if (kinhNghiem > 50) {
                    showValidationError(experienceField, "Kinh nghiệm không được vượt quá 50 năm");
                    if (isValid) {
                        experienceField.requestFocus();
                        isValid = false;
                    }
                }
            } catch (NumberFormatException e) {
                showValidationError(experienceField, "Kinh nghiệm phải là số nguyên");
                if (isValid) {
                    experienceField.requestFocus();
                    isValid = false;
                }
                kinhNghiem = -1; // Invalid value
            }
        }
        if (currentBacSi == null) {
            NguoiDungItem selectedUser = (NguoiDungItem) userComboBox.getSelectedItem();
            if (selectedUser == null || selectedUser.getNguoiDung() == null) {
                showValidationError(userComboBox, "Vui lòng chọn người dùng");
                if (isValid) {
                    userComboBox.requestFocus();
                    isValid = false;
                }
            }
        }        
        // Validate clinic selection
        PhongKhamItem selectedClinic = (PhongKhamItem) clinicComboBox.getSelectedItem();
        if (selectedClinic == null || selectedClinic.getPhongKham() == null) {
            showValidationError(clinicComboBox, "Vui lòng chọn phòng khám");
            if (isValid) {
                clinicComboBox.requestFocus();
                isValid = false;
            }
        }        
        if (!isValid) {
            return;
        }        
        // Get validated experience value
        kinhNghiem = Integer.parseInt(kinhNghiemStr);        
        // Create or update doctor
        try {
            if (currentBacSi == null) {
                // Create new doctor
                NguoiDungItem selectedUser = (NguoiDungItem) userComboBox.getSelectedItem();
                BacSi newBacSi = new BacSi();
                newBacSi.setIdNguoiDung(selectedUser.getNguoiDung().getIdNguoiDung());
                newBacSi.setHoTenBacSi(hoTenBacSi);
                newBacSi.setChuyenKhoa(chuyenKhoa);
                newBacSi.setBangCap(bangCap);
                newBacSi.setKinhNghiem(kinhNghiem);
                newBacSi.setIdPhongKham(selectedClinic.getPhongKham().getIdPhongKham());
                
                boolean success = bacSiController.addBacSi(newBacSi);
                if (success) {
                    showSuccessToast("Thêm bác sĩ thành công!");
                    confirmed = true;
                    firePropertyChange("doctorDataChanged", false, true);
                    dispose();
                } else {
                    showErrorMessage("Không thể thêm bác sĩ. Vui lòng thử lại sau.");
                }
            } else {
                // Update existing doctor
                currentBacSi.setHoTenBacSi(hoTenBacSi);
                currentBacSi.setChuyenKhoa(chuyenKhoa);
                currentBacSi.setBangCap(bangCap);
                currentBacSi.setKinhNghiem(kinhNghiem);
                currentBacSi.setIdPhongKham(selectedClinic.getPhongKham().getIdPhongKham());
                
                boolean success = bacSiController.updateBacSi(currentBacSi);
                if (success) {
                    showSuccessToast("Cập nhật bác sĩ thành công!");
                    confirmed = true;
                    firePropertyChange("doctorDataChanged", false, true);
                    dispose();
                } else {
                    showErrorMessage("Không thể cập nhật bác sĩ. Vui lòng thử lại sau.");
                }
            }
        } catch (Exception e) {
            showErrorMessage("Đã xảy ra lỗi: " + e.getMessage());
        }
    }
    
    private void showSuccessToast(String message) {
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
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this, message,"Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }    
    private void setupEnterKeyNavigation() {
        JComponent[] components;        
        if (currentBacSi == null) {
            components = new JComponent[] {
                userComboBox,nameField,specialtyComboBox,degreeField,experienceField,clinicComboBox // THAY ĐỔI
            };
        } else {
            components = new JComponent[] {
                nameField,specialtyComboBox,degreeField,experienceField,clinicComboBox // THAY ĐỔI
            };
        }      
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
                ((JComboBox) components[i]).addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            components[nextIndex].requestFocus();
                        }
                    }
                });
            }
        }
        JComponent lastComponent = components[components.length - 1];
        if (lastComponent instanceof JTextField) {
            ((JTextField) lastComponent).addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        saveDoctor();
                    }
                }
            });
        } else if (lastComponent instanceof JComboBox) {
            ((JComboBox) lastComponent).addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        saveDoctor();
                    }
                }
            });
        }
    }    
    public boolean isConfirmed() {
        return confirmed;
    }
}