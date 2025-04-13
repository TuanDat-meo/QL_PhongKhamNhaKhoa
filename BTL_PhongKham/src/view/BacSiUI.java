package view;

import controller.BacSiController;
import model.BacSi;
import model.NguoiDung;
import model.PhongKham;
import model.LichHen;
import model.DieuTri;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import connect.connectMySQL;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BacSiUI extends JFrame {
    private BacSiController bacSiController;
    
    // UI Components
    private JTable bacSiTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton refreshButton;
    
    // Current selected doctor ID
    private int currentBacSiId = -1;
    
    public BacSiUI() {
        bacSiController = new BacSiController();
        
        setTitle("Quản Lý Bác Sĩ");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        loadBacSiData();
    }
    
    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add to frame
        add(mainPanel);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel searchLabel = new JLabel("Tìm kiếm bác sĩ:");
        searchField = new JTextField(20);
        searchButton = new JButton("Tìm Kiếm");
        refreshButton = new JButton("Làm Mới");
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBacSi();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                loadBacSiData();
            }
        });
        
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh Sách Bác Sĩ"));
        
        // Table columns
        String[] columns = {
            "ID", "Họ Tên", "Chuyên Khoa", "Bằng Cấp", "Kinh Nghiệm", "Phòng Khám", "Email", "Số Điện Thoại"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        bacSiTable = new JTable(tableModel);
        bacSiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bacSiTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Add mouse listener for row clicks
        bacSiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = bacSiTable.getSelectedRow();
                if (selectedRow >= 0) {
                    currentBacSiId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    
                    // Show popup menu at the location of mouse click
                    showRowPopupMenu(e.getX(), e.getY());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(bacSiTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showRowPopupMenu(int x, int y) {
        // Create popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Get doctor data for the selected row
        BacSi selectedBacSi = bacSiController.getBacSiById(currentBacSiId);
        
        if (selectedBacSi != null) {
            // Menu title (doctor name)
            JMenuItem titleItem = new JMenuItem("Bác sĩ: " + selectedBacSi.getHoTenBacSi());
            titleItem.setFont(titleItem.getFont().deriveFont(Font.BOLD));
            titleItem.setEnabled(false);
            popupMenu.add(titleItem);
            popupMenu.addSeparator();
            
            // View details option
            JMenuItem viewDetailsItem = new JMenuItem("Xem Chi Tiết");
            viewDetailsItem.addActionListener(e -> showBacSiDetails(selectedBacSi));
            popupMenu.add(viewDetailsItem);
            
            // Edit option
            JMenuItem editItem = new JMenuItem("Chỉnh Sửa");
            editItem.addActionListener(e -> showEditBacSiDialog());
            popupMenu.add(editItem);
            
            // Delete option
            JMenuItem deleteItem = new JMenuItem("Xóa");
            deleteItem.addActionListener(e -> deleteBacSi());
            popupMenu.add(deleteItem);
            
            popupMenu.addSeparator();
            
            // View appointments option
            JMenuItem appointmentsItem = new JMenuItem("Xem Lịch Hẹn");
            appointmentsItem.addActionListener(e -> showAppointments());
            popupMenu.add(appointmentsItem);
            
            // View treatments option
            JMenuItem treatmentsItem = new JMenuItem("Xem Điều Trị");
            treatmentsItem.addActionListener(e -> showTreatments());
            popupMenu.add(treatmentsItem);
            
            // Show the popup menu
            popupMenu.show(bacSiTable, x, y);
        }
    }
    
    private void showBacSiDetails(BacSi bacSi) {
        // Create a dialog to show doctor details
        JDialog detailsDialog = new JDialog(this, "Chi Tiết Bác Sĩ", true);
        detailsDialog.setSize(500, 400);
        detailsDialog.setLocationRelativeTo(this);
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Doctor name header
        JLabel nameLabel = new JLabel(bacSi.getHoTenBacSi());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(nameLabel);
        
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Create details fields with consistent formatting
        addDetailField(detailsPanel, "ID:", String.valueOf(bacSi.getIdBacSi()));
        addDetailField(detailsPanel, "Chuyên Khoa:", bacSi.getChuyenKhoa());
        addDetailField(detailsPanel, "Bằng Cấp:", bacSi.getBangCap());
        addDetailField(detailsPanel, "Kinh Nghiệm:", bacSi.getKinhNghiem() + " năm");
        addDetailField(detailsPanel, "Phòng Khám:", bacSi.getTenPhong());
        addDetailField(detailsPanel, "Email:", bacSi.getEmailNguoiDung());
        addDetailField(detailsPanel, "Số Điện Thoại:", bacSi.getSoDienThoaiNguoiDung());
        
        detailsPanel.add(Box.createVerticalGlue());
        
        // Add buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton editButton = new JButton("Chỉnh Sửa");
        editButton.addActionListener(e -> {
            detailsDialog.dispose();
            showEditBacSiDialog();
        });
        
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        
        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        
        // Add everything to the dialog
        detailsDialog.setLayout(new BorderLayout());
        detailsDialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setVisible(true);
    }
    
    private void addDetailField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(450, 30));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setPreferredSize(new Dimension(120, 25));
        labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));
        
        JLabel valueComponent = new JLabel(value);
        
        fieldPanel.add(labelComponent, BorderLayout.WEST);
        fieldPanel.add(valueComponent, BorderLayout.CENTER);
        
        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        addButton = new JButton("Thêm Bác Sĩ");
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddBacSiDialog();
            }
        });
        
        panel.add(addButton);
        
        return panel;
    }
    
    private void loadBacSiData() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all doctors
        List<BacSi> bacSiList = bacSiController.getAllBacSi();
        
        // Add to table
        for (BacSi bacSi : bacSiList) {
            Object[] rowData = {
                bacSi.getIdBacSi(),
                bacSi.getHoTenBacSi(),
                bacSi.getChuyenKhoa(),
                bacSi.getBangCap(),
                bacSi.getKinhNghiem() + " năm",
                bacSi.getTenPhong(),
                bacSi.getEmailNguoiDung(),
                bacSi.getSoDienThoaiNguoiDung()
            };
            tableModel.addRow(rowData);
        }
        
        // Reset selection
        currentBacSiId = -1;
    }
    
    private void searchBacSi() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadBacSiData();
            return;
        }
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Search doctors
        List<BacSi> bacSiList = bacSiController.searchBacSi(searchTerm);
        
        // Add to table
        for (BacSi bacSi : bacSiList) {
            Object[] rowData = {
                bacSi.getIdBacSi(),
                bacSi.getHoTenBacSi(),
                bacSi.getChuyenKhoa(),
                bacSi.getBangCap(),
                bacSi.getKinhNghiem() + " năm",
                bacSi.getTenPhong(),
                bacSi.getEmailNguoiDung(),
                bacSi.getSoDienThoaiNguoiDung()
            };
            tableModel.addRow(rowData);
        }
        
        // Reset selection
        currentBacSiId = -1;
    }
    
    private void showAddBacSiDialog() {
        BacSiDialog dialog = new BacSiDialog(this, null);
        dialog.setVisible(true);
        
        // Reload data if doctor was added
        if (dialog.isConfirmed()) {
            loadBacSiData();
        }
    }
    
    private void showEditBacSiDialog() {
        if (currentBacSiId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một bác sĩ để chỉnh sửa.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get doctor data
        BacSi bacSi = bacSiController.getBacSiById(currentBacSiId);
        if (bacSi == null) {
            JOptionPane.showMessageDialog(this, 
                "Không thể tìm thấy thông tin bác sĩ.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show dialog
        BacSiDialog dialog = new BacSiDialog(this, bacSi);
        dialog.setVisible(true);
        
        // Reload data if doctor was updated
        if (dialog.isConfirmed()) {
            loadBacSiData();
        }
    }
    
    private void deleteBacSi() {
        if (currentBacSiId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một bác sĩ để xóa.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get doctor info
        BacSi currentDoctor = bacSiController.getBacSiById(currentBacSiId);
        if (currentDoctor == null) {
            JOptionPane.showMessageDialog(this, 
                "Không thể tìm thấy thông tin bác sĩ.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if doctor has future appointments or treatments
        boolean hasFutureAppointments = bacSiController.hasFutureAppointments(currentBacSiId);
        boolean hasTreatments = false;
        
        try (Connection conn = connectMySQL.getConnection()) {
            hasTreatments = bacSiController.hasTreatments(conn, currentBacSiId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // If there are no records to reassign, just confirm and delete
        if (!hasFutureAppointments && !hasTreatments) {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa bác sĩ này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            
            boolean success = bacSiController.deleteBacSiWithReplacement(currentBacSiId, -1);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Đã xóa bác sĩ thành công.", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadBacSiData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể xóa bác sĩ. Vui lòng thử lại sau.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        // Get other doctors with the same specialty
        List<BacSi> replacementDoctors = bacSiController.getOtherDoctorsBySpecialty(currentBacSiId);
        
        if (replacementDoctors.isEmpty()) {
            // No replacement doctors available, ask if user wants to delete anyway
            String message = "Bác sĩ này có ";
            if (hasFutureAppointments) message += "lịch hẹn trong tương lai ";
            if (hasFutureAppointments && hasTreatments) message += "và ";
            if (hasTreatments) message += "điều trị ";
            message += "nhưng không có bác sĩ cùng chuyên khoa để thay thế.\n\n" +
                       "Nếu tiếp tục, tất cả lịch hẹn và điều trị của bác sĩ sẽ bị xóa.\n" +
                       "Bạn có chắc chắn muốn xóa bác sĩ này?";
            
            int choice = JOptionPane.showConfirmDialog(this, message,
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            
            boolean success = bacSiController.deleteBacSi(currentBacSiId);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Đã xóa bác sĩ thành công.", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadBacSiData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể xóa bác sĩ. Vui lòng thử lại sau.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        // Show dialog to select replacement doctor
        ReplacementDoctorDialog dialog = new ReplacementDoctorDialog(this, currentDoctor, replacementDoctors);
        dialog.setVisible(true);
        
        // Get selected replacement doctor
        if (dialog.isConfirmed()) {
            int replacementDoctorId = dialog.getSelectedDoctorId();
            
            String message;
            if (replacementDoctorId > 0) {
                BacSi replacementDoctor = bacSiController.getBacSiById(replacementDoctorId);
                message = "Bạn đã chọn bác sĩ " + replacementDoctor.getHoTenBacSi() + " để thay thế.\n\n" +
                         "Tất cả lịch hẹn và điều trị của bác sĩ " + currentDoctor.getHoTenBacSi() + 
                         " sẽ được chuyển cho bác sĩ " + replacementDoctor.getHoTenBacSi() + ".\n\n" +
                         "Bạn có chắc chắn muốn xóa bác sĩ " + currentDoctor.getHoTenBacSi() + "?";
            } else {
                message = "Bạn đã chọn không thay thế bác sĩ.\n\n" +
                         "Tất cả lịch hẹn và điều trị của bác sĩ " + currentDoctor.getHoTenBacSi() + " sẽ bị xóa.\n\n" +
                         "Bạn có chắc chắn muốn xóa bác sĩ " + currentDoctor.getHoTenBacSi() + "?";
            }
            
            int choice = JOptionPane.showConfirmDialog(this, message,
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            
            boolean success = bacSiController.deleteBacSiWithReplacement(currentBacSiId, replacementDoctorId);
            if (success) {
                String resultMessage;
                if (replacementDoctorId > 0) {
                    BacSi replacementDoctor = bacSiController.getBacSiById(replacementDoctorId);
                    resultMessage = "Đã xóa bác sĩ " + currentDoctor.getHoTenBacSi() + " thành công.\n" +
                                   "Tất cả lịch hẹn và điều trị đã được chuyển cho bác sĩ " + 
                                   replacementDoctor.getHoTenBacSi() + ".";
                } else {
                    resultMessage = "Đã xóa bác sĩ " + currentDoctor.getHoTenBacSi() + " thành công.";
                }
                
                JOptionPane.showMessageDialog(this, resultMessage, 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadBacSiData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể xóa bác sĩ. Vui lòng thử lại sau.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Inner class for doctor replacement dialog
    private class ReplacementDoctorDialog extends JDialog {
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
            
            // Information panel
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
            
            // Options panel
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

    // Helper class for the doctor dropdown
    private class BacSiItem {
        private BacSi bacSi;
        
        public BacSiItem(BacSi bacSi) {
            this.bacSi = bacSi;
        }
        
        public BacSi getBacSi() {
            return bacSi;
        }
        
        @Override
        public String toString() {
            return bacSi.getHoTenBacSi() + " - " + bacSi.getChuyenKhoa() + 
                   " (" + bacSi.getTenPhong() + ")";
        }
    }
    
    private void showAppointments() {
        if (currentBacSiId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một bác sĩ để xem lịch hẹn.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get doctor's future appointments
        List<LichHen> appointments = bacSiController.getFutureAppointments(currentBacSiId);
        
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Bác sĩ này không có lịch hẹn nào trong tương lai.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show appointments in dialog
        AppointmentsDialog dialog = new AppointmentsDialog(this, appointments);
        dialog.setVisible(true);
    }
    
    private void showTreatments() {
        if (currentBacSiId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một bác sĩ để xem điều trị.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get doctor's treatments
        List<DieuTri> treatments = bacSiController.getDieuTriByBacSi(currentBacSiId);
        
        if (treatments.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Bác sĩ này không có điều trị nào.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show treatments in dialog
        TreatmentsDialog dialog = new TreatmentsDialog(this, treatments);
        dialog.setVisible(true);
    }
    
    // Inner class for Add/Edit dialog
    private class BacSiDialog extends JDialog {
        private JTextField nameField;
        private JTextField specialtyField;
        private JTextField degreeField;
        private JTextField experienceField;
        private JComboBox<NguoiDungItem> userComboBox;
        private JComboBox<PhongKhamItem> clinicComboBox;
        
        private BacSi currentBacSi;
        private boolean confirmed = false;
        
        public BacSiDialog(JFrame parent, BacSi bacSi) {
            super(parent, bacSi == null ? "Thêm Bác Sĩ Mới" : "Chỉnh Sửa Bác Sĩ");
            this.currentBacSi = bacSi;
            
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
    
    // Helper classes for combo boxes
    private class NguoiDungItem {
        private NguoiDung nguoiDung;
        
        public NguoiDungItem(NguoiDung nguoiDung) {
            this.nguoiDung = nguoiDung;
        }
        
        public NguoiDung getNguoiDung() {
            return nguoiDung;
        }
        
        @Override
        public String toString() {
            return nguoiDung.getHoTen() + " (" + nguoiDung.getEmail() + ")";
        }
    }
    
    private class PhongKhamItem {
        private PhongKham phongKham;
        
        public PhongKhamItem(PhongKham phongKham) {
            this.phongKham = phongKham;
        }
        
        public PhongKham getPhongKham() {
            return phongKham;
        }
        
        @Override
        public String toString() {
            return phongKham.getTenPhong() + " - " + phongKham.getDiaChi();
        }
    }
    
    // Inner class for appointments view dialog
    private class AppointmentsDialog extends JDialog {
        public AppointmentsDialog(JFrame parent, List<LichHen> appointments) {
            super(parent, "Lịch Hẹn", true);
            setSize(800, 500);
            setLocationRelativeTo(parent);
            
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Create table with appointments
            String[] columns = {
                "ID", "Bệnh Nhân", "Ngày Hẹn", "Giờ Hẹn", "Lý Do", "Trạng Thái"
            };
            
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Add appointment data
            for (LichHen appointment : appointments) {
                Object[] rowData = {
                    appointment.getIdLichHen(),
                    appointment.getHoTenBenhNhan(),
                    appointment.getNgayHen(),
                    appointment.getGioHen(),
                    appointment.getMoTa(),
                    appointment.getTrangThai()
                };
                model.addRow(rowData);
            }
            
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Close button
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton closeButton = new JButton("Đóng");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
    }
    
    // Inner class for treatments view dialog
    private class TreatmentsDialog extends JDialog {
        public TreatmentsDialog(JFrame parent, List<DieuTri> treatments) {
            super(parent, "Điều Trị", true);
            setSize(800, 500);
            setLocationRelativeTo(parent);
            
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Create table with treatments
            String[] columns = {
                "ID", "Bệnh Nhân", "Tên Điều Trị", "Ngày Bắt Đầu", "Ngày Kết Thúc", "Trạng Thái"
            };
            
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Add treatment data
            for (DieuTri treatment : treatments) {
                Object[] rowData = {
                    treatment.getIdDieuTri(),
                    treatment.getTenBenhNhan(),
                    treatment.getTenDieuTri(),
                    treatment.getNgayBatDau(),
                    treatment.getNgayKetThuc(),
                    treatment.getTrangThai()
                };
                model.addRow(rowData);
            }
            
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Close button
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton closeButton = new JButton("Đóng");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            BacSiUI bacSiUI = new BacSiUI();
            bacSiUI.setVisible(true);
        });
    }
}