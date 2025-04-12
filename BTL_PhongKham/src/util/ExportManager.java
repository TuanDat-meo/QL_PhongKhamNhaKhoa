package util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Component;
import java.util.prefs.Preferences;
import java.util.List;
import model.BacSi;
public class ExportManager {
    private Component parentComponent;
    private DefaultTableModel tableModel;
    private Preferences prefs = Preferences.userNodeForPackage(ExportManager.class);
    private static final String LAST_EXCEL_PATH = "lastExcelPath";
    private static final String LAST_CSV_PATH = "lastCsvPath";
    
    // Interface để hiển thị thông báo
    public interface MessageCallback {
        void showSuccessToast(String message);
        void showErrorMessage(String title, String message);
    }
    
    private MessageCallback messageCallback;
    
    // Constructor
    public ExportManager(Component parentComponent, DefaultTableModel tableModel, MessageCallback messageCallback) {
        this.parentComponent = parentComponent;
        this.tableModel = tableModel;
        this.messageCallback = messageCallback;
    }
    
    // Hiển thị hộp thoại lựa chọn định dạng xuất file
    public void showExportOptions(Color primaryColor, Color secondaryColor, Color buttonTextColor) {
        JDialog exportDialog = new JDialog();
        exportDialog.setTitle("Xuất dữ liệu");
        exportDialog.setModal(true);
        exportDialog.setSize(300, 200);
        exportDialog.setLocationRelativeTo(parentComponent);
        
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Chọn định dạng xuất file:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(primaryColor);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnXuatExcel = createRoundedButton("Excel (.xls)", primaryColor, buttonTextColor, 10);
        btnXuatExcel.addActionListener(e -> {
            exportDialog.dispose();
            exportToExcelXML();
        });
        
        JButton btnXuatCSV = createRoundedButton("CSV (.csv)", secondaryColor, buttonTextColor, 10);
        btnXuatCSV.addActionListener(e -> {
            exportDialog.dispose();
            exportToCSV();
        });
        
        buttonPanel.add(btnXuatExcel);
        buttonPanel.add(btnXuatCSV);
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        exportDialog.setContentPane(panel);
        exportDialog.setVisible(true);
    }
    public void exportDoctorList(JTable table, List<BacSi> doctorList, String exportType) {
        // Lưu lại tableModel hiện tại
        DefaultTableModel currentModel = this.tableModel;
        
        // Sử dụng tableModel từ table được truyền vào
        this.tableModel = (DefaultTableModel) table.getModel();
        
        try {
            if ("excel".equalsIgnoreCase(exportType)) {
                exportDoctorToExcelXML(doctorList);
            } else if ("csv".equalsIgnoreCase(exportType)) {
                exportDoctorToCSV(doctorList);
            } else {
                // Hiển thị hộp thoại lựa chọn định dạng xuất
                JDialog exportDialog = new JDialog();
                exportDialog.setTitle("Xuất danh sách bác sĩ");
                exportDialog.setModal(true);
                exportDialog.setSize(300, 200);
                exportDialog.setLocationRelativeTo(parentComponent);
                
                JPanel panel = new JPanel(new BorderLayout(10, 15));
                panel.setBackground(Color.WHITE);
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                JLabel titleLabel = new JLabel("Chọn định dạng xuất file:");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                titleLabel.setForeground(new Color(76, 175, 80));  // Primary color
                panel.add(titleLabel, BorderLayout.NORTH);
                
                JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
                buttonPanel.setBackground(Color.WHITE);
                
                JButton btnXuatExcel = createRoundedButton("Excel (.xls)", new Color(76, 175, 80), Color.WHITE, 10);
                btnXuatExcel.addActionListener(e -> {
                    exportDialog.dispose();
                    exportDoctorToExcelXML(doctorList);
                });
                
                JButton btnXuatCSV = createRoundedButton("CSV (.csv)", new Color(33, 150, 243), Color.WHITE, 10);
                btnXuatCSV.addActionListener(e -> {
                    exportDialog.dispose();
                    exportDoctorToCSV(doctorList);
                });
                
                buttonPanel.add(btnXuatExcel);
                buttonPanel.add(btnXuatCSV);
                panel.add(buttonPanel, BorderLayout.CENTER);
                
                exportDialog.setContentPane(panel);
                exportDialog.setVisible(true);
            }
        } finally {
            // Khôi phục lại tableModel ban đầu
            this.tableModel = currentModel;
        }
    }
    
    // Xuất danh sách bác sĩ ra Excel
    private void exportDoctorToExcelXML(List<BacSi> doctorList) {
        try {
            // Lấy đường dẫn file đã lưu trước đó (nếu có)
            String lastPath = prefs.get(LAST_EXCEL_PATH, null);
            File lastFile = (lastPath != null) ? new File(lastPath) : null;
            
            // Tạo JFileChooser để chọn vị trí lưu file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu file Excel");
            
            // Thiết lập filter cho file Excel
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xls)", "xls"));
            
            // Nếu có file đã lưu trước đó, đặt nó làm file mặc định
            if (lastFile != null && lastFile.exists()) {
                fileChooser.setSelectedFile(lastFile);
            } else {
                fileChooser.setSelectedFile(new File("danh_sach_bac_si.xls"));
            }
            
            int userSelection = fileChooser.showSaveDialog(parentComponent);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                // Đảm bảo file có đuôi .xls
                if (!fileToSave.getName().toLowerCase().endsWith(".xls")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".xls");
                }
                
                // Hiển thị xác nhận nếu file đã tồn tại
                if (fileToSave.exists()) {
                    int response = JOptionPane.showConfirmDialog(
                        parentComponent,
                        "File đã tồn tại. Bạn có muốn ghi đè lên file này không?",
                        "Xác nhận ghi đè",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (response != JOptionPane.YES_OPTION) {
                        return; // Người dùng không muốn ghi đè
                    }
                }
                
                // Lưu đường dẫn này để sử dụng lần sau
                prefs.put(LAST_EXCEL_PATH, fileToSave.getAbsolutePath());
                
                // Tạo XML Spreadsheet
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    // XML header
                    writer.write("<?xml version=\"1.0\"?>\n");
                    writer.write("<?mso-application progid=\"Excel.Sheet\"?>\n");
                    writer.write("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
                    writer.write(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
                    writer.write(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
                    writer.write(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
                    writer.write(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n");
                    
                    // Styles
                    writer.write(" <Styles>\n");
                    
                    // Default style
                    writer.write("  <Style ss:ID=\"Default\" ss:Name=\"Normal\">\n");
                    writer.write("   <Alignment ss:Vertical=\"Bottom\"/>\n");
                    writer.write("   <Borders/>\n");
                    writer.write("   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"/>\n");
                    writer.write("   <Interior/>\n");
                    writer.write("   <NumberFormat/>\n");
                    writer.write("   <Protection/>\n");
                    writer.write("  </Style>\n");
                    
                    // Header style
                    writer.write("  <Style ss:ID=\"Header\">\n");
                    writer.write("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n");
                    writer.write("   <Borders>\n");
                    writer.write("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("   </Borders>\n");
                    writer.write("   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#FFFFFF\" ss:Bold=\"1\"/>\n");
                    writer.write("   <Interior ss:Color=\"#4CAF50\" ss:Pattern=\"Solid\"/>\n");
                    writer.write("  </Style>\n");
                    
                    // Data style
                    writer.write("  <Style ss:ID=\"Data\">\n");
                    writer.write("   <Borders>\n");
                    writer.write("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("   </Borders>\n");
                    writer.write("  </Style>\n");
                    
                    // Data alternate row style
                    writer.write("  <Style ss:ID=\"DataAlt\">\n");
                    writer.write("   <Borders>\n");
                    writer.write("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("   </Borders>\n");
                    writer.write("   <Interior ss:Color=\"#F2F2F2\" ss:Pattern=\"Solid\"/>\n");
                    writer.write("  </Style>\n");
                    
                    writer.write(" </Styles>\n");
                    
                    // Worksheet
                    writer.write(" <Worksheet ss:Name=\"Danh sách bác sĩ\">\n");
                    writer.write("  <Table ss:ExpandedColumnCount=\"6\" ss:ExpandedRowCount=\"" + (tableModel.getRowCount() + 1) + "\" x:FullColumns=\"1\" x:FullRows=\"1\" ss:DefaultColumnWidth=\"100\">\n");
                    
                    // Cài đặt độ rộng cho các cột
                    writer.write("   <Column ss:Width=\"60\"/>\n");       // ID
                    writer.write("   <Column ss:Width=\"150\"/>\n");      // Họ tên bác sĩ
                    writer.write("   <Column ss:Width=\"150\"/>\n");      // Chuyên khoa
                    writer.write("   <Column ss:Width=\"150\"/>\n");      // Bằng cấp
                    writer.write("   <Column ss:Width=\"80\"/>\n");       // Kinh nghiệm
                    writer.write("   <Column ss:Width=\"80\"/>\n");       // ID Phòng khám
                    
                    // Header row
                    writer.write("   <Row ss:Height=\"30\">\n");
                    
                    String[] headers = {"ID", "Họ tên bác sĩ", "Chuyên khoa", "Bằng cấp", "Kinh nghiệm (năm)", "ID Phòng khám"};
                    for (String header : headers) {
                        writer.write("    <Cell ss:StyleID=\"Header\"><Data ss:Type=\"String\">" + escapeXML(header) + "</Data></Cell>\n");
                    }
                    
                    writer.write("   </Row>\n");
                    
                    // Data rows
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        // Xác định style cho hàng (hàng chẵn hay lẻ)
                        String rowStyleID = (i % 2 == 0) ? "Data" : "DataAlt";
                        
                        writer.write("   <Row>\n");
                        
                        for (int j = 0; j < tableModel.getColumnCount(); j++) {
                            Object value = tableModel.getValueAt(i, j);
                            String cellValue = (value != null) ? value.toString() : "";
                            
                            writer.write("    <Cell ss:StyleID=\"" + rowStyleID + "\"><Data ss:Type=\"String\">" + escapeXML(cellValue) + "</Data></Cell>\n");
                        }
                        
                        writer.write("   </Row>\n");
                    }
                    
                    writer.write("  </Table>\n");
                    writer.write("  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
                    writer.write("   <PageSetup>\n");
                    writer.write("    <Header x:Margin=\"0.3\"/>\n");
                    writer.write("    <Footer x:Margin=\"0.3\"/>\n");
                    writer.write("    <PageMargins x:Bottom=\"0.75\" x:Left=\"0.7\" x:Right=\"0.7\" x:Top=\"0.75\"/>\n");
                    writer.write("   </PageSetup>\n");
                    writer.write("   <Selected/>\n");
                    writer.write("   <Panes>\n");
                    writer.write("    <Pane>\n");
                    writer.write("     <Number>3</Number>\n");
                    writer.write("     <ActiveRow>1</ActiveRow>\n");
                    writer.write("     <ActiveCol>1</ActiveCol>\n");
                    writer.write("    </Pane>\n");
                    writer.write("   </Panes>\n");
                    writer.write("   <ProtectObjects>False</ProtectObjects>\n");
                    writer.write("   <ProtectScenarios>False</ProtectScenarios>\n");
                    writer.write("  </WorksheetOptions>\n");
                    writer.write(" </Worksheet>\n");
                    writer.write("</Workbook>");
                }
                
                messageCallback.showSuccessToast("Xuất danh sách bác sĩ ra file Excel thành công!");
            }
        } catch (IOException ex) {
            messageCallback.showErrorMessage("Lỗi xuất file Excel", ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Xuất danh sách bác sĩ ra CSV
    private void exportDoctorToCSV(List<BacSi> doctorList) {
        try {
            // Lấy đường dẫn file đã lưu trước đó (nếu có)
            String lastPath = prefs.get(LAST_CSV_PATH, null);
            File lastFile = (lastPath != null) ? new File(lastPath) : null;
            
            // Tạo JFileChooser để chọn vị trí lưu file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu file CSV");
            
            // Thiết lập filter cho file CSV
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
            
            // Nếu có file đã lưu trước đó, đặt nó làm file mặc định
            if (lastFile != null && lastFile.exists()) {
                fileChooser.setSelectedFile(lastFile);
            } else {
                fileChooser.setSelectedFile(new File("danh_sach_bac_si.csv"));
            }
            
            int userSelection = fileChooser.showSaveDialog(parentComponent);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                // Đảm bảo file có đuôi .csv
                if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
                }
                
                // Hiển thị xác nhận nếu file đã tồn tại
                if (fileToSave.exists()) {
                    int response = JOptionPane.showConfirmDialog(
                        parentComponent,
                        "File đã tồn tại. Bạn có muốn ghi đè lên file này không?",
                        "Xác nhận ghi đè",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (response != JOptionPane.YES_OPTION) {
                        return; // Người dùng không muốn ghi đè
                    }
                }
                
                // Lưu đường dẫn này để sử dụng lần sau
                prefs.put(LAST_CSV_PATH, fileToSave.getAbsolutePath());
                
                // Tạo FileWriter
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    // Viết header
                    writer.append("ID,Họ tên bác sĩ,Chuyên khoa,Bằng cấp,Kinh nghiệm (năm),ID Phòng khám\n");
                    
                    // Viết dữ liệu từ tableModel
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        // ID
                        writer.append(tableModel.getValueAt(i, 0).toString());
                        writer.append(',');
                        
                        // Họ tên bác sĩ - đặt trong dấu ngoặc kép để xử lý trường hợp có dấu phẩy
                        writer.append('"').append(escapeCSV(tableModel.getValueAt(i, 1).toString())).append('"');
                        writer.append(',');
                        
                        // Chuyên khoa
                        writer.append('"').append(escapeCSV(tableModel.getValueAt(i, 2).toString())).append('"');
                        writer.append(',');
                        
                        // Bằng cấp
                        writer.append('"').append(escapeCSV(tableModel.getValueAt(i, 3).toString())).append('"');
                        writer.append(',');
                        
                        // Kinh nghiệm
                        writer.append(tableModel.getValueAt(i, 4).toString());
                        writer.append(',');
                        
                        // ID Phòng khám
                        writer.append(tableModel.getValueAt(i, 5).toString());
                        writer.append('\n');
                    }
                }
                
                messageCallback.showSuccessToast("Xuất danh sách bác sĩ ra file CSV thành công!");
            }
        } catch (IOException ex) {
            messageCallback.showErrorMessage("Lỗi xuất file CSV", ex.getMessage());
            ex.printStackTrace();
        }
    }
    // Phương thức xuất dữ liệu ra file Excel XML (.xls)
    public void exportToExcelXML() {
        try {
            // Lấy đường dẫn file đã lưu trước đó (nếu có)
            String lastPath = prefs.get(LAST_EXCEL_PATH, null);
            File lastFile = (lastPath != null) ? new File(lastPath) : null;
            
            // Tạo JFileChooser để chọn vị trí lưu file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu file Excel");
            
            // Thiết lập filter cho file Excel
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xls)", "xls"));
            
            // Nếu có file đã lưu trước đó, đặt nó làm file mặc định
            if (lastFile != null && lastFile.exists()) {
                fileChooser.setSelectedFile(lastFile);
            } else {
                fileChooser.setSelectedFile(new File("danh_sach_benh_nhan.xls"));
            }
            
            int userSelection = fileChooser.showSaveDialog(parentComponent);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                // Đảm bảo file có đuôi .xls
                if (!fileToSave.getName().toLowerCase().endsWith(".xls")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".xls");
                }
                
                // Hiển thị xác nhận nếu file đã tồn tại
                if (fileToSave.exists()) {
                    int response = JOptionPane.showConfirmDialog(
                        parentComponent,
                        "File đã tồn tại. Bạn có muốn ghi đè lên file này không?",
                        "Xác nhận ghi đè",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (response != JOptionPane.YES_OPTION) {
                        return; // Người dùng không muốn ghi đè
                    }
                }
                
                // Lưu đường dẫn này để sử dụng lần sau
                prefs.put(LAST_EXCEL_PATH, fileToSave.getAbsolutePath());
                
                // Tạo XML Spreadsheet
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    // XML header
                    writer.write("<?xml version=\"1.0\"?>\n");
                    writer.write("<?mso-application progid=\"Excel.Sheet\"?>\n");
                    writer.write("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
                    writer.write(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
                    writer.write(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
                    writer.write(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
                    writer.write(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n");
                    
                    // Styles
                    writer.write(" <Styles>\n");
                    
                    // Default style
                    writer.write("  <Style ss:ID=\"Default\" ss:Name=\"Normal\">\n");
                    writer.write("   <Alignment ss:Vertical=\"Bottom\"/>\n");
                    writer.write("   <Borders/>\n");
                    writer.write("   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"/>\n");
                    writer.write("   <Interior/>\n");
                    writer.write("   <NumberFormat/>\n");
                    writer.write("   <Protection/>\n");
                    writer.write("  </Style>\n");
                    
                    // Header style
                    writer.write("  <Style ss:ID=\"Header\">\n");
                    writer.write("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n");
                    writer.write("   <Borders>\n");
                    writer.write("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("   </Borders>\n");
                    writer.write("   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#FFFFFF\" ss:Bold=\"1\"/>\n");
                    writer.write("   <Interior ss:Color=\"#4CAF50\" ss:Pattern=\"Solid\"/>\n");
                    writer.write("  </Style>\n");
                    
                    // Data style
                    writer.write("  <Style ss:ID=\"Data\">\n");
                    writer.write("   <Borders>\n");
                    writer.write("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("   </Borders>\n");
                    writer.write("  </Style>\n");
                    
                    // Data alternate row style
                    writer.write("  <Style ss:ID=\"DataAlt\">\n");
                    writer.write("   <Borders>\n");
                    writer.write("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
                    writer.write("   </Borders>\n");
                    writer.write("   <Interior ss:Color=\"#F2F2F2\" ss:Pattern=\"Solid\"/>\n");
                    writer.write("  </Style>\n");
                    
                    writer.write(" </Styles>\n");
                    
                    // Worksheet
                    writer.write(" <Worksheet ss:Name=\"Danh sách bệnh nhân\">\n");
                    writer.write("  <Table ss:ExpandedColumnCount=\"7\" ss:ExpandedRowCount=\"" + (tableModel.getRowCount() + 1) + "\" x:FullColumns=\"1\" x:FullRows=\"1\" ss:DefaultColumnWidth=\"100\">\n");
                    
                    // Cài đặt độ rộng cho các cột
                    writer.write("   <Column ss:Width=\"60\"/>\n");   // ID
                    writer.write("   <Column ss:Width=\"150\"/>\n");  // Họ tên
                    writer.write("   <Column ss:Width=\"100\"/>\n");  // Ngày sinh
                    writer.write("   <Column ss:Width=\"80\"/>\n");   // Giới tính
                    writer.write("   <Column ss:Width=\"100\"/>\n");  // SĐT
                    writer.write("   <Column ss:Width=\"100\"/>\n");  // CCCD
                    writer.write("   <Column ss:Width=\"200\"/>\n");  // Địa chỉ
                    
                    // Header row
                    writer.write("   <Row ss:Height=\"30\">\n");
                    
                    String[] headers = {"ID", "Họ tên", "Ngày sinh", "Giới tính", "Số điện thoại", "CCCD", "Địa chỉ"};
                    for (String header : headers) {
                        writer.write("    <Cell ss:StyleID=\"Header\"><Data ss:Type=\"String\">" + escapeXML(header) + "</Data></Cell>\n");
                    }
                    
                    writer.write("   </Row>\n");
                    
                    // Data rows
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        // Xác định style cho hàng (hàng chẵn hay lẻ)
                        String rowStyleID = (i % 2 == 0) ? "Data" : "DataAlt";
                        
                        writer.write("   <Row>\n");
                        
                        for (int j = 0; j < tableModel.getColumnCount(); j++) {
                            Object value = tableModel.getValueAt(i, j);
                            String cellValue = (value != null) ? value.toString() : "";
                            
                            writer.write("    <Cell ss:StyleID=\"" + rowStyleID + "\"><Data ss:Type=\"String\">" + escapeXML(cellValue) + "</Data></Cell>\n");
                        }
                        
                        writer.write("   </Row>\n");
                    }
                    
                    writer.write("  </Table>\n");
                    writer.write("  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
                    writer.write("   <PageSetup>\n");
                    writer.write("    <Header x:Margin=\"0.3\"/>\n");
                    writer.write("    <Footer x:Margin=\"0.3\"/>\n");
                    writer.write("    <PageMargins x:Bottom=\"0.75\" x:Left=\"0.7\" x:Right=\"0.7\" x:Top=\"0.75\"/>\n");
                    writer.write("   </PageSetup>\n");
                    writer.write("   <Selected/>\n");
                    writer.write("   <Panes>\n");
                    writer.write("    <Pane>\n");
                    writer.write("     <Number>3</Number>\n");
                    writer.write("     <ActiveRow>1</ActiveRow>\n");
                    writer.write("     <ActiveCol>1</ActiveCol>\n");
                    writer.write("    </Pane>\n");
                    writer.write("   </Panes>\n");
                    writer.write("   <ProtectObjects>False</ProtectObjects>\n");
                    writer.write("   <ProtectScenarios>False</ProtectScenarios>\n");
                    writer.write("  </WorksheetOptions>\n");
                    writer.write(" </Worksheet>\n");
                    writer.write("</Workbook>");
                }
                
                messageCallback.showSuccessToast("Xuất file Excel thành công!");
            }
        } catch (IOException ex) {
            messageCallback.showErrorMessage("Lỗi xuất file Excel", ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Phương thức xuất dữ liệu ra file CSV
    public void exportToCSV() {
        try {
            // Lấy đường dẫn file đã lưu trước đó (nếu có)
            String lastPath = prefs.get(LAST_CSV_PATH, null);
            File lastFile = (lastPath != null) ? new File(lastPath) : null;
            
            // Tạo JFileChooser để chọn vị trí lưu file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu file CSV");
            
            // Thiết lập filter cho file CSV
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
            
            // Nếu có file đã lưu trước đó, đặt nó làm file mặc định
            if (lastFile != null && lastFile.exists()) {
                fileChooser.setSelectedFile(lastFile);
            } else {
                fileChooser.setSelectedFile(new File("danh_sach_benh_nhan.csv"));
            }
            
            int userSelection = fileChooser.showSaveDialog(parentComponent);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                // Đảm bảo file có đuôi .csv
                if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
                }
                
                // Hiển thị xác nhận nếu file đã tồn tại
                if (fileToSave.exists()) {
                    int response = JOptionPane.showConfirmDialog(
                        parentComponent,
                        "File đã tồn tại. Bạn có muốn ghi đè lên file này không?",
                        "Xác nhận ghi đè",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (response != JOptionPane.YES_OPTION) {
                        return; // Người dùng không muốn ghi đè
                    }
                }
                
                // Lưu đường dẫn này để sử dụng lần sau
                prefs.put(LAST_CSV_PATH, fileToSave.getAbsolutePath());
                
                // Tạo FileWriter
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    // Viết header
                    writer.append("ID,Họ tên,Ngày sinh,Giới tính,Số điện thoại,CCCD,Địa chỉ\n");
                    
                    // Viết dữ liệu
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        // ID
                        writer.append(tableModel.getValueAt(i, 0).toString());
                        writer.append(',');
                        
                        // Họ tên - đặt trong dấu ngoặc kép để xử lý trường hợp có dấu phẩy
                        writer.append('"').append(escapeCSV(tableModel.getValueAt(i, 1).toString())).append('"');
                        writer.append(',');
                        
                        // Ngày sinh
                        writer.append('"').append(tableModel.getValueAt(i, 2).toString()).append('"');
                        writer.append(',');
                        
                        // Giới tính
                        writer.append('"').append(tableModel.getValueAt(i, 3).toString()).append('"');
                        writer.append(',');
                        
                        // Số điện thoại
                        writer.append('"').append(tableModel.getValueAt(i, 4).toString()).append('"');
                        writer.append(',');
                        
                        // CCCD
                        writer.append('"').append(tableModel.getValueAt(i, 5).toString()).append('"');
                        writer.append(',');
                        
                        // Địa chỉ
                        writer.append('"').append(escapeCSV(tableModel.getValueAt(i, 6).toString())).append('"');
                        writer.append('\n');
                    }
                }
                
                messageCallback.showSuccessToast("Xuất file CSV thành công!");
            }
        } catch (IOException ex) {
            messageCallback.showErrorMessage("Lỗi xuất file CSV", ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Phương thức hỗ trợ để escape các ký tự đặc biệt trong XML
    private String escapeXML(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }
    
    // Hàm hỗ trợ để escape các ký tự đặc biệt trong CSV
    private String escapeCSV(String value) {
        if (value == null) return "";
        // Thay thế dấu ngoặc kép bằng hai dấu ngoặc kép (quy tắc của CSV)
        return value.replace("\"", "\"\"");
    }
    
    // Tạo button có viền bo tròn
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, int radius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground().darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
            }
        };
        
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }
}