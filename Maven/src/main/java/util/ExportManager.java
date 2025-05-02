package util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Lớp tiện ích để xuất dữ liệu từ JTable sang các định dạng CSV và khác
 */
public class ExportManager {
    // Constants
    private static final int DIALOG_WIDTH = 480;
    private static final int DIALOG_HEIGHT = 330;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 36;
    private static final int CONTENT_PADDING = 15;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Interface để gọi lại thông báo đến màn hình chính
    public interface MessageCallback {
        void showSuccessToast(String message);
        void showErrorMessage(String title, String message);
		void showMessage(String message, String title, int messageType);
    }

    // Fields
    private final Component parentComponent;
    private final DefaultTableModel tableModel;
    private final MessageCallback callback;
    private final String defaultFileName;

    public ExportManager(Component parentComponent, DefaultTableModel tableModel, MessageCallback callback) {
        this.parentComponent = parentComponent;
        this.tableModel = tableModel;
        this.callback = callback;
        this.defaultFileName = generateDefaultFileName();
    }
    public void showExportOptions(Color primaryColor, Color secondaryColor, Color buttonTextColor) {
        // Tạo dialog chính
        JDialog exportDialog = createDialog();
        
        // Tạo panel chính với BorderLayout để tổ chức nội dung tốt hơn
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(CONTENT_PADDING, CONTENT_PADDING, CONTENT_PADDING, CONTENT_PADDING));
        
        // Panel chứa tiêu đề và logo (nếu có)
        JPanel headerPanel = createHeaderPanel(primaryColor);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel chứa các tùy chọn
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Panel chứa các nút
        JPanel buttonPanel = createButtonPanel(primaryColor, secondaryColor, buttonTextColor, exportDialog);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Hiển thị dialog
        exportDialog.setContentPane(mainPanel);
        exportDialog.setVisible(true);
    }
    private JDialog createDialog() {
        JDialog exportDialog = new JDialog(SwingUtilities.getWindowAncestor(parentComponent), "Xuất dữ liệu");
        exportDialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        exportDialog.setLocationRelativeTo(parentComponent);
        exportDialog.setResizable(false);
        exportDialog.setModal(true);
        return exportDialog;
    }
    private JPanel createHeaderPanel(Color primaryColor) {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(Color.WHITE);
        
        // Tiêu đề
        JLabel titleLabel = new JLabel("Xuất dữ liệu");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(primaryColor);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Thêm đường kẻ phía dưới tiêu đề
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(headerPanel, BorderLayout.CENTER);
        titlePanel.add(separator, BorderLayout.SOUTH);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        return titlePanel;
    }
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        // Panel tên tệp tin
        JPanel fileNamePanel = createFileNamePanel();
        contentPanel.add(fileNamePanel);
        
        // Thêm khoảng trống
        contentPanel.add(Box.createVerticalStrut(12));
        
        // Panel định dạng
        JPanel formatPanel = createFormatPanel();
        contentPanel.add(formatPanel);
        
        return contentPanel;
    }
    private JPanel createFileNamePanel() {
        JPanel fileNamePanel = new JPanel(new BorderLayout(10, 5));
        fileNamePanel.setBackground(Color.WHITE);
        fileNamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        fileNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Label tên tệp tin
        JLabel fileNameLabel = new JLabel("Tên tệp tin:");
        fileNameLabel.setFont(LABEL_FONT);
        
        // Panel chứa label để canh chỉnh
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.add(fileNameLabel);
        fileNamePanel.add(labelPanel, BorderLayout.NORTH);
        
        // Trường nhập tên tệp tin
        JTextField fileNameField = new JTextField(defaultFileName);
        fileNameField.setFont(LABEL_FONT);
        fileNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 204, 204), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fileNameField.setName("fileNameField");
        fileNamePanel.add(fileNameField, BorderLayout.CENTER);
        
        return fileNamePanel;
    }
    private JPanel createFormatPanel() {
        JPanel formatPanel = new JPanel(new BorderLayout());
        formatPanel.setBackground(Color.WHITE);
        formatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formatPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                "Định dạng"));
        formatPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Panel chứa các radio button theo hàng ngang
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Các tùy chọn định dạng
        ButtonGroup formatGroup = new ButtonGroup();
        
        JRadioButton csvRadio = createFormatRadioButton("CSV (.csv)", true);
        csvRadio.setName("csvRadio");
        formatGroup.add(csvRadio);
        radioPanel.add(csvRadio);
        
        JRadioButton excelRadio = createFormatRadioButton("Excel (.xlsx)", false);
        excelRadio.setName("excelRadio");
        formatGroup.add(excelRadio);
        radioPanel.add(excelRadio);
        
        JRadioButton pdfRadio = createFormatRadioButton("PDF (.pdf)", false);
        pdfRadio.setName("pdfRadio");
        formatGroup.add(pdfRadio);
        radioPanel.add(pdfRadio);
        
        formatPanel.add(radioPanel, BorderLayout.CENTER);
        
        return formatPanel;
    }
    private JRadioButton createFormatRadioButton(String text, boolean selected) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setFont(LABEL_FONT);
        radioButton.setSelected(selected);
        radioButton.setBackground(Color.WHITE);
        radioButton.setFocusPainted(false);
        return radioButton;
    }
    private JPanel createButtonPanel(Color primaryColor, Color secondaryColor, Color buttonTextColor, JDialog exportDialog) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Nút Hủy
        JButton cancelButton = createButton("Hủy", new Color(240, 240, 240), new Color(80, 80, 80));
        cancelButton.addActionListener(e -> exportDialog.dispose());
        
        // Nút Xuất
        JButton exportButton = createButton("Xuất", primaryColor, buttonTextColor);
        exportButton.addActionListener(e -> handleExport(exportDialog));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(exportButton);
        
        return buttonPanel;
    }
    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        
        // Thêm hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkenColor(bgColor));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0.0f, hsb[2] - 0.1f));
    }
    private void handleExport(JDialog exportDialog) {
        // Lấy tên tệp từ TextField
        JTextField fileNameField = (JTextField) findComponentByName(exportDialog, "fileNameField");
        String fileName = fileNameField.getText().trim();
        
        if (fileName.isEmpty()) {
            callback.showErrorMessage("Lỗi", "Vui lòng nhập tên tệp tin");
            return;
        }
        
        // Xác định định dạng từ RadioButton được chọn
        String format = determineSelectedFormat(exportDialog);
        
        // Tiến hành xuất dữ liệu
        exportData(fileName, format);
        exportDialog.dispose();
    }
    private Component findComponentByName(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            } else if (comp instanceof Container) {
                Component found = findComponentByName((Container) comp, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    private String determineSelectedFormat(JDialog dialog) {
        JRadioButton csvRadio = (JRadioButton) findComponentByName(dialog, "csvRadio");
        JRadioButton excelRadio = (JRadioButton) findComponentByName(dialog, "excelRadio");
        JRadioButton pdfRadio = (JRadioButton) findComponentByName(dialog, "pdfRadio");
        
        if (csvRadio.isSelected()) return "csv";
        if (excelRadio.isSelected()) return "excel";
        if (pdfRadio.isSelected()) return "pdf";
        
        return "csv"; // Mặc định
    }
    private void exportData(String baseFileName, String format) {
        // Chuẩn bị thông tin cho định dạng tệp tin
        FileFormatInfo formatInfo = getFileFormatInfo(format);
        
        // Đảm bảo tên tệp tin có phần mở rộng
        if (!baseFileName.toLowerCase().endsWith("." + formatInfo.extension)) {
            baseFileName += "." + formatInfo.extension;
        }
        
        // Cấu hình file chooser
        JFileChooser fileChooser = configureFileChooser(baseFileName, formatInfo);
        
        // Hiển thị hộp thoại lưu
        int result = fileChooser.showSaveDialog(parentComponent);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = ensureFileExtension(fileChooser.getSelectedFile(), formatInfo.extension);
            
            try {
                // Xuất dữ liệu theo định dạng
                switch (format) {
                    case "csv":
                        exportToCSV(selectedFile);
                        break;
                    case "excel":
                        exportToExcel(selectedFile);
                        break;
                    case "pdf":
                        exportToPDF(selectedFile);
                        break;
                }
                callback.showSuccessToast("Xuất dữ liệu thành công!");
            } catch (IOException e) {
                callback.showErrorMessage("Lỗi khi xuất dữ liệu", e.getMessage());
            } catch (Exception e) {
                callback.showErrorMessage("Lỗi không xác định", e.getMessage());
            }
        }
    }
    private static class FileFormatInfo {
        String extension;
        String description;
        
        FileFormatInfo(String extension, String description) {
            this.extension = extension;
            this.description = description;
        }
    }
    private FileFormatInfo getFileFormatInfo(String format) {
        switch (format) {
            case "excel":
                return new FileFormatInfo("xlsx", "Excel Files (*.xlsx)");
            case "pdf":
                return new FileFormatInfo("pdf", "PDF Files (*.pdf)");
            case "csv":
            default:
                return new FileFormatInfo("csv", "CSV Files (*.csv)");
        }
    }
    private JFileChooser configureFileChooser(String baseFileName, FileFormatInfo formatInfo) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu tệp tin");
        fileChooser.setSelectedFile(new File(baseFileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                formatInfo.description, formatInfo.extension));
        return fileChooser;
    }
    private File ensureFileExtension(File file, String extension) {
        String filePath = file.getAbsolutePath();
        if (!filePath.toLowerCase().endsWith("." + extension)) {
            return new File(filePath + "." + extension);
        }
        return file;
    }
    private void exportToExcel(File file) throws IOException {
        JOptionPane.showMessageDialog(parentComponent,
            "Chức năng xuất Excel cần thư viện Apache POI.\n" +
            "Vui lòng thêm thư viện và cập nhật mã nguồn.",
            "Chức năng chưa khả dụng",
            JOptionPane.INFORMATION_MESSAGE);
    }
    private void exportToPDF(File file) throws Exception {
        JOptionPane.showMessageDialog(parentComponent,
            "Chức năng xuất PDF cần thư viện iText.\n" +
            "Vui lòng thêm thư viện và cập nhật mã nguồn.",
            "Chức năng chưa khả dụng",
            JOptionPane.INFORMATION_MESSAGE);
    }
    private void exportToCSV(File file) throws IOException {
        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            // Ghi tiêu đề cột
            writeCSVHeader(bw);
            
            // Ghi dữ liệu hàng
            writeCSVData(bw);
        }
    }
    private void writeCSVHeader(BufferedWriter bw) throws IOException {
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            bw.write(escapeCSV(tableModel.getColumnName(i)));
            if (i < tableModel.getColumnCount() - 1) {
                bw.write(",");
            }
        }
        bw.newLine();
    }
    private void writeCSVData(BufferedWriter bw) throws IOException {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object value = tableModel.getValueAt(i, j);
                String cellText = value != null ? value.toString() : "";
                bw.write(escapeCSV(cellText));
                
                if (j < tableModel.getColumnCount() - 1) {
                    bw.write(",");
                }
            }
            bw.newLine();
        }
    }
    private String escapeCSV(String text) {
        if (text == null) {
            return "";
        }
        
        boolean needQuotes = text.contains(",") || text.contains("\"") || text.contains("\n");
        
        if (needQuotes) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        } else {
            return text;
        }
    }
    private String generateDefaultFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return "export_data_" + sdf.format(new Date());
    }
}