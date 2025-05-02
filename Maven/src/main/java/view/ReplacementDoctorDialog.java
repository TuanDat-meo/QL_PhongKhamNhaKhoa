package view;

import controller.BacSiController;
import model.BacSi;
import model.BacSiItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReplacementDoctorDialog extends JDialog {
    private JComboBox<BacSiItem> doctorComboBox;
    private JRadioButton replaceRadio;
    private JRadioButton noReplaceRadio;
    private boolean confirmed = false;
    private int selectedDoctorId = -1;
    
    public ReplacementDoctorDialog(JFrame parent, BacSi currentDoctor, List<BacSi> replacementDoctors) {
        super(parent, "Chọn Bác Sĩ Thay Thế", true);
        
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin"));
        
        String info = "<html><body style='width: 400px'>" +
                      "Bác sĩ <b>" + currentDoctor.getHoTenBacSi() + "</b> có các lịch hẹn hoặc " +
                      "điều trị chưa hoàn thành. Vui lòng chọn bác sĩ khác cùng chuyên khoa " +
                      "<b>" + currentDoctor.getChuyenKhoa() + "</b> để thay thế, hoặc chọn " +
                      "không thay thế nếu bạn muốn xóa tất cả các lịch hẹn và điều trị liên quan." +
                      "</body></html>";
        
        JLabel infoLabel = new JLabel(info);
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        JPanel optionsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Lựa Chọn"));
        
        ButtonGroup group = new ButtonGroup();
        replaceRadio = new JRadioButton("Thay thế bác sĩ này bằng bác sĩ khác cùng chuyên khoa");
        noReplaceRadio = new JRadioButton("Không thay thế (xóa tất cả lịch hẹn và điều trị liên quan)");
        
        group.add(replaceRadio);
        group.add(noReplaceRadio);
        
        replaceRadio.setSelected(true);
        
        optionsPanel.add(replaceRadio);
        
        // Doctor selection panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel doctorLabel = new JLabel("Chọn bác sĩ thay thế:");
        doctorComboBox = new JComboBox<>();
        
        for (BacSi doctor : replacementDoctors) {
            doctorComboBox.addItem(new BacSiItem(doctor));
        }
        
        selectionPanel.add(doctorLabel);
        selectionPanel.add(doctorComboBox);
        optionsPanel.add(selectionPanel);
        
        // Third option
        optionsPanel.add(noReplaceRadio);
        
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Enable/disable combo box based on radio selection
        replaceRadio.addActionListener(e -> doctorComboBox.setEnabled(true));
        noReplaceRadio.addActionListener(e -> doctorComboBox.setEnabled(false));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("Xác Nhận");
        JButton cancelButton = new JButton("Hủy");
        
        okButton.addActionListener(e -> {
            confirmed = true;
            if (replaceRadio.isSelected() && doctorComboBox.getSelectedItem() != null) {
                BacSiItem selected = (BacSiItem) doctorComboBox.getSelectedItem();
                selectedDoctorId = selected.getBacSi().getIdBacSi();
            } else {
                selectedDoctorId = -1;
            }
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public int getSelectedDoctorId() {
        return selectedDoctorId;
    }
}