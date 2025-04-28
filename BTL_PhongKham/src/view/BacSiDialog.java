package view;

import controller.BacSiController;
import model.BacSi;
import model.NguoiDung;
import model.NguoiDungItem;
import model.PhongKham;
import model.PhongKhamItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BacSiDialog extends JDialog {
    private JTextField nameField;
    private JTextField specialtyField;
    private JTextField degreeField;
    private JTextField experienceField;
    private JComboBox<NguoiDungItem> userComboBox;
    private JComboBox<PhongKhamItem> clinicComboBox;
    
    private BacSi currentBacSi;
    private boolean confirmed = false;
    private BacSiController bacSiController;
    
    public BacSiDialog(JFrame parent, BacSi bacSi) {
        super(parent, bacSi == null ? "Thêm Bác Sĩ Mới" : "Chỉnh Sửa Bác Sĩ");
        this.currentBacSi = bacSi;
        this.bacSiController = new BacSiController();
        
        setSize(600, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Form panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
        saveButton.addActionListener(e -> saveDoctor());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load data if editing
        if (bacSi != null) {
            loadDoctorData();
        }
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // User selection (only available when adding new)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Người Dùng:");
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        userComboBox = new JComboBox<>();
        
        // Load available users if adding new
        if (currentBacSi == null) {
            List<NguoiDung> availableUsers = bacSiController.getAllDoctorUsers();
            for (NguoiDung user : availableUsers) {
                userComboBox.addItem(new NguoiDungItem(user));
            }
        } else {
            // Just add the current user
            NguoiDung user = bacSiController.getNguoiDungById(currentBacSi.getIdNguoiDung());
            if (user != null) {
                userComboBox.addItem(new NguoiDungItem(user));
            }
            userComboBox.setEnabled(false); // Can't change user when editing
        }
        
        panel.add(userComboBox, gbc);
        
        // Doctor name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("Họ Tên Bác Sĩ:");
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);
        
        // Specialty
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel specialtyLabel = new JLabel("Chuyên Khoa:");
        panel.add(specialtyLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        specialtyField = new JTextField(20);
        panel.add(specialtyField, gbc);
        
        // Degree
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel degreeLabel = new JLabel("Bằng Cấp:");
        panel.add(degreeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        degreeField = new JTextField(20);
        panel.add(degreeField, gbc);
        
        // Experience
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel experienceLabel = new JLabel("Kinh Nghiệm (năm):");
        panel.add(experienceLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        experienceField = new JTextField(20);
        panel.add(experienceField, gbc);
        
        // Clinic selection
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JLabel clinicLabel = new JLabel("Phòng Khám:");
        panel.add(clinicLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        clinicComboBox = new JComboBox<>();
        
        // Load available clinics
        List<PhongKham> clinics = bacSiController.getAllPhongKham();
        for (PhongKham clinic : clinics) {
            clinicComboBox.addItem(new PhongKhamItem(clinic));
        }
        
        panel.add(clinicComboBox, gbc);
        
        return panel;
    }
    
    private void loadDoctorData() {
        // Set fields with current doctor data
        nameField.setText(currentBacSi.getHoTenBacSi());
        specialtyField.setText(currentBacSi.getChuyenKhoa());
        degreeField.setText(currentBacSi.getBangCap());
        experienceField.setText(String.valueOf(currentBacSi.getKinhNghiem()));
        
        // Select the current clinic
        PhongKham currentClinic = bacSiController.getPhongKhamById(currentBacSi.getIdPhongKham());
        for (int i = 0; i < clinicComboBox.getItemCount(); i++) {
            PhongKhamItem item = clinicComboBox.getItemAt(i);
            if (item.getPhongKham().getIdPhongKham() == currentClinic.getIdPhongKham()) {
                clinicComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void saveDoctor() {
        // Validate inputs
        if (nameField.getText().trim().isEmpty() ||
            specialtyField.getText().trim().isEmpty() ||
            degreeField.getText().trim().isEmpty() ||
            experienceField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Vui lòng điền đầy đủ thông tin.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate experience is a number
        int experience;
        try {
            experience = Integer.parseInt(experienceField.getText().trim());
            if (experience < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Kinh nghiệm phải là số nguyên dương.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get selected clinic
        PhongKhamItem selectedClinic = (PhongKhamItem) clinicComboBox.getSelectedItem();
        if (selectedClinic == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn phòng khám.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create or update doctor
        if (currentBacSi == null) {
            // Create new doctor
            NguoiDungItem selectedUser = (NguoiDungItem) userComboBox.getSelectedItem();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn người dùng.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            BacSi newBacSi = new BacSi();
            newBacSi.setIdNguoiDung(selectedUser.getNguoiDung().getIdNguoiDung());
            newBacSi.setHoTenBacSi(nameField.getText().trim());
            newBacSi.setChuyenKhoa(specialtyField.getText().trim());
            newBacSi.setBangCap(degreeField.getText().trim());
            newBacSi.setKinhNghiem(experience);
            newBacSi.setIdPhongKham(selectedClinic.getPhongKham().getIdPhongKham());
            
            boolean success = bacSiController.addBacSi(newBacSi);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Thêm bác sĩ thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể thêm bác sĩ. Vui lòng thử lại sau.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Update existing doctor
            currentBacSi.setHoTenBacSi(nameField.getText().trim());
            currentBacSi.setChuyenKhoa(specialtyField.getText().trim());
            currentBacSi.setBangCap(degreeField.getText().trim());
            currentBacSi.setKinhNghiem(experience);
            currentBacSi.setIdPhongKham(selectedClinic.getPhongKham().getIdPhongKham());
            
            boolean success = bacSiController.updateBacSi(currentBacSi);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật bác sĩ thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể cập nhật bác sĩ. Vui lòng thử lại sau.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}