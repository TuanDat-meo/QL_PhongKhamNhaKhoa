package util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

// Thêm imports cho Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Thêm imports cho PDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Rectangle;

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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");

            // Tạo font đậm cho tiêu đề
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            // Tạo CellStyle cho tiêu đề
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            XSSFColor headerColor = new XSSFColor(new java.awt.Color(41, 128, 185), new DefaultIndexedColorMap());
            headerCellStyle.setFillForegroundColor(headerColor);
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Tạo CellStyle cho số điện thoại, CCCD, ID - định dạng văn bản, căn giữa
            CellStyle textCenterCellStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            textCenterCellStyle.setDataFormat(format.getFormat("@"));
            textCenterCellStyle.setBorderBottom(BorderStyle.THIN);
            textCenterCellStyle.setBorderTop(BorderStyle.THIN);
            textCenterCellStyle.setBorderLeft(BorderStyle.THIN);
            textCenterCellStyle.setBorderRight(BorderStyle.THIN);
            textCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Tạo CellStyle cho ngày hẹn, giờ hẹn, thời gian - căn giữa
            CellStyle dateCenterCellStyle = workbook.createCellStyle();
            dateCenterCellStyle.setDataFormat(format.getFormat("dd/mm/yyyy"));
            dateCenterCellStyle.setBorderBottom(BorderStyle.THIN);
            dateCenterCellStyle.setBorderTop(BorderStyle.THIN);
            dateCenterCellStyle.setBorderLeft(BorderStyle.THIN);
            dateCenterCellStyle.setBorderRight(BorderStyle.THIN);
            dateCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Tạo CellStyle cho số lượng - căn giữa
            CellStyle numberCenterCellStyle = workbook.createCellStyle();
            numberCenterCellStyle.setBorderBottom(BorderStyle.THIN);
            numberCenterCellStyle.setBorderTop(BorderStyle.THIN);
            numberCenterCellStyle.setBorderLeft(BorderStyle.THIN);
            numberCenterCellStyle.setBorderRight(BorderStyle.THIN);
            numberCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Tạo CellStyle cho số, căn phải
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setBorderBottom(BorderStyle.THIN);
            numberCellStyle.setBorderTop(BorderStyle.THIN);
            numberCellStyle.setBorderLeft(BorderStyle.THIN);
            numberCellStyle.setBorderRight(BorderStyle.THIN);
            numberCellStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Tạo CellStyle cho văn bản thông thường, căn trái
            CellStyle normalCellStyle = workbook.createCellStyle();
            normalCellStyle.setBorderBottom(BorderStyle.THIN);
            normalCellStyle.setBorderTop(BorderStyle.THIN);
            normalCellStyle.setBorderLeft(BorderStyle.THIN);
            normalCellStyle.setBorderRight(BorderStyle.THIN);
            normalCellStyle.setAlignment(HorizontalAlignment.LEFT);

            // Tạo CellStyle cho đơn vị tính, phân loại - căn giữa
            CellStyle centerCellStyle = workbook.createCellStyle();
            centerCellStyle.setBorderBottom(BorderStyle.THIN);
            centerCellStyle.setBorderTop(BorderStyle.THIN);
            centerCellStyle.setBorderLeft(BorderStyle.THIN);
            centerCellStyle.setBorderRight(BorderStyle.THIN);
            centerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Ghi tiêu đề cột
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(tableModel.getColumnName(i));
                cell.setCellStyle(headerCellStyle);
            }

            // Định dạng để kiểm tra chuỗi ngày tháng
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);

            // Ghi dữ liệu
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = tableModel.getValueAt(i, j);
                    String colName = tableModel.getColumnName(j).toLowerCase();

                    // Kiểm tra các cột đặc biệt
                    boolean isIdColumn = colName.contains("id");
                    boolean isQuantityColumn = colName.contains("số lượng");
                    boolean isDateColumn = colName.contains("ngày hẹn");
                    boolean isTimeColumn = colName.contains("giờ hẹn") || colName.contains("thời gian");
                    boolean isUnitColumn = colName.contains("đơn vị tính");
                    boolean isCategoryColumn = colName.contains("phân loại");
                    boolean isStatusColumn = colName.contains("trạng thái");
                    boolean isMonthYearColumn = colName.contains("tháng/năm");

                    if (value == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(normalCellStyle);
                    } else if (isIdColumn || isTimeColumn || isQuantityColumn || isStatusColumn || isMonthYearColumn) {
                        // Xử lý cột ID, giờ hẹn, thời gian, số lượng, trạng thái, tháng/năm: căn giữa
                        if (value instanceof Number) {
                            cell.setCellValue(String.format("%.0f", ((Number)value).doubleValue()));
                            cell.setCellStyle(isQuantityColumn ? numberCenterCellStyle : textCenterCellStyle);
                        } else {
                            cell.setCellValue(value.toString());
                            cell.setCellStyle(textCenterCellStyle);
                        }
                    } else if (isDateColumn) {
                        // Xử lý cột ngày hẹn: căn giữa
                        if (value instanceof Date) {
                            cell.setCellValue((Date)value);
                            cell.setCellStyle(dateCenterCellStyle);
                        } else if (value instanceof String) {
                            String strValue = (String) value;
                            try {
                                sdf.parse(strValue);
                                cell.setCellValue(strValue);
                                cell.setCellStyle(dateCenterCellStyle);
                            } catch (ParseException e) {
                                cell.setCellValue(strValue);
                                cell.setCellStyle(normalCellStyle);
                            }
                        } else {
                            cell.setCellValue(value.toString());
                            cell.setCellStyle(normalCellStyle);
                        }
                    } else if (isUnitColumn || isCategoryColumn) {
                        // Xử lý cột đơn vị tính, phân loại: căn giữa
                        cell.setCellValue(value.toString());
                        cell.setCellStyle(centerCellStyle);
                    } else if (value instanceof Number && 
                              (colName.contains("cccd") || 
                               colName.contains("phone") || 
                               colName.contains("điện thoại") || 
                               colName.contains("thoại"))) {
                        // Số điện thoại, CCCD: căn giữa
                        cell.setCellValue(String.format("%.0f", ((Number)value).doubleValue()));
                        cell.setCellStyle(textCenterCellStyle);
                    } else if (value instanceof Date) {
                        // Ngày tháng khác (không phải ngày hẹn): căn trái
                        cell.setCellValue((Date)value);
                        cell.setCellStyle(normalCellStyle);
                    } else if (value instanceof Number) {
                        // Số (không phải số điện thoại/CCCD): căn phải
                        cell.setCellValue(((Number)value).doubleValue());
                        cell.setCellStyle(numberCellStyle);
                    } else {
                        // Văn bản thông thường: căn trái
                        cell.setCellValue(value.toString());
                        cell.setCellStyle(normalCellStyle);
                    }
                }
            }

            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi workbook ra file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
        }
    }
    
    /**
     * Xuất dữ liệu sang định dạng PDF
     */
    private void exportToPDF(File file) throws Exception {
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 50, 36);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        
        // Thêm header và footer
        writer.setPageEvent(new PDFHeaderFooter());
        
        document.open();
        
        // Nhúng font hỗ trợ tiếng Việt (Times New Roman)
        BaseFont bf = BaseFont.createFont("c:/windows/fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bf, 16, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(bf, 11, com.itextpdf.text.Font.NORMAL);
        
        // Thêm tiêu đề
        Paragraph title = new Paragraph("BÁO CÁO DỮ LIỆU", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Thêm ngày tháng
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = "Ngày xuất: " + sdf.format(new Date());
        Paragraph dateParagraph = new Paragraph(dateString, contentFont);
        dateParagraph.setAlignment(Element.ALIGN_RIGHT);
        dateParagraph.setSpacingAfter(20);
        document.add(dateParagraph);
        
        // Tính độ rộng tối ưu cho mỗi cột
        int columnCount = tableModel.getColumnCount();
        float[] columnWidths = new float[columnCount];
        float totalWidth = 0;
        
        // Định dạng số với dấu phân cách hàng nghìn
        DecimalFormat numberFormat = new DecimalFormat("#,###");
        
        // Tính độ dài tối đa của nội dung trong mỗi cột
        for (int j = 0; j < columnCount; j++) {
            float maxWidth = bf.getWidthPoint(tableModel.getColumnName(j), 12); // Độ dài tiêu đề
            String colName = tableModel.getColumnName(j).toLowerCase();
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Object value = tableModel.getValueAt(i, j);
                String text;
                if (value == null) {
                    text = "";
                } else if (value instanceof Date) {
                    text = sdf.format((Date)value);
                } else if (value instanceof Number && 
                          (colName.contains("cccd") || 
                           colName.contains("phone") || 
                           colName.contains("điện thoại") || 
                           colName.contains("thoại"))) {
                    text = numberFormat.format(((Number)value).longValue());
                } else if (value instanceof Number) {
                    text = numberFormat.format(((Number)value).longValue());
                } else {
                    text = value.toString();
                }
                float textWidth = bf.getWidthPoint(text, 11); // Độ dài nội dung
                maxWidth = Math.max(maxWidth, textWidth);
            }
            columnWidths[j] = maxWidth + 20; // Thêm padding
            totalWidth += columnWidths[j];
        }
        
        // Chuẩn hóa độ rộng để đảm bảo tổng độ rộng vừa với trang
        float pageWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        for (int j = 0; j < columnCount; j++) {
            columnWidths[j] = (columnWidths[j] / totalWidth) * pageWidth;
        }
        
        // Tạo bảng với độ rộng cột đã tính
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        
        // Thiết lập header cho bảng
        for (int i = 0; i < columnCount; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(tableModel.getColumnName(i), headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(41, 128, 185)); // Màu #2980b9
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Thiết lập nội dung bảng
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object value = tableModel.getValueAt(i, j);
                String colName = tableModel.getColumnName(j).toLowerCase();
                String text;
                
                if (value == null) {
                    text = "";
                } else if (value instanceof Date) {
                    text = sdf.format((Date)value);
                } else if (value instanceof Number && 
                          (colName.contains("cccd") || 
                           colName.contains("phone") || 
                           colName.contains("điện thoại") || 
                           colName.contains("thoại"))) {
                    text = numberFormat.format(((Number)value).longValue());
                } else if (value instanceof Number) {
                    text = numberFormat.format(((Number)value).longValue());
                } else {
                    text = value.toString();
                }
                
                // Tạo ô
                PdfPCell cell = new PdfPCell(new Phrase(text, contentFont));
                cell.setPadding(5);
                
                // Căn chỉnh dựa trên tên cột và kiểu dữ liệu
                boolean isIdColumn = colName.contains("id");
                boolean isQuantityColumn = colName.contains("số lượng");
                boolean isDateColumn = colName.contains("ngày hẹn");
                boolean isTimeColumn = colName.contains("giờ hẹn") || colName.contains("thời gian");
                boolean isUnitColumn = colName.contains("đơn vị tính");
                boolean isCategoryColumn = colName.contains("phân loại");
                boolean isStatusColumn = colName.contains("trạng thái");
                boolean isMonthYearColumn = colName.contains("tháng/năm");
                
                if (isIdColumn || isQuantityColumn || isDateColumn || isTimeColumn || 
                    isStatusColumn || isMonthYearColumn) {
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                } else if (isUnitColumn || isCategoryColumn) {
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                } else if (value instanceof Number && 
                          !colName.contains("cccd") && 
                          !colName.contains("phone") && 
                          !colName.contains("điện thoại") && 
                          !colName.contains("thoại")) {
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                } else {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }
                
                table.addCell(cell);
            }
        }
        
        // Thêm bảng vào tài liệu
        document.add(table);
        
        // Đóng tài liệu
        document.close();
    }
    
    /**
     * Class để tạo header/footer cho PDF
     */
    private class PDFHeaderFooter extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // Footer
            PdfContentByte cb = writer.getDirectContent();
            com.itextpdf.text.Font font = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 8);
            
            // Tạo ô chứa số trang
            Phrase footer = new Phrase("Trang " + writer.getPageNumber(), font);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, 
                (document.right() - document.left()) / 2 + document.leftMargin(), 
                document.bottom() - 10, 0);
        }
    }
    
    private void exportToCSV(File file) throws IOException {
        // Thêm BOM UTF-8 để Excel nhận dạng đúng tiếng Việt
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Thêm BOM (Byte Order Mark) cho UTF-8
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);
            
            // Tiếp tục ghi nội dung CSV với UTF-8
            try (OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter bw = new BufferedWriter(osw)) {
                
                // Ghi tiêu đề cột
                writeCSVHeader(bw);
                
                // Ghi dữ liệu hàng
                writeCSVData(bw);
            }
        }
    }
    
    private void writeCSVHeader(BufferedWriter bw) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            String columnName = tableModel.getColumnName(i);
            
            sb.append(escapeCSV(columnName));
            if (i < tableModel.getColumnCount() - 1) {
                sb.append(",");
            }
        }
        
        bw.write(sb.toString());
        bw.newLine();
    }
    
    private void writeCSVData(BufferedWriter bw) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            StringBuilder sb = new StringBuilder();
            
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object value = tableModel.getValueAt(i, j);
                String cellText;
                String colName = tableModel.getColumnName(j).toLowerCase();
                
                // Xử lý đặc biệt cho các kiểu dữ liệu khác nhau
                if (value == null) {
                    cellText = "";
                } else if (value instanceof Date) {
                    // Định dạng ngày tháng
                    cellText = sdf.format((Date)value);
                } else if (value instanceof Number && 
                          (colName.contains("cccd") || 
                           colName.contains("phone") || 
                           colName.contains("điện thoại") || 
                           colName.contains("thoại"))) {
                    // Định dạng số điện thoại và CCCD như văn bản (không có dấu phẩy, không ký hiệu khoa học)
                    cellText = String.format("%.0f", ((Number) value).doubleValue());
                } else {
                    cellText = value.toString();
                }
                
                sb.append(escapeCSV(cellText));
                
                if (j < tableModel.getColumnCount() - 1) {
                    sb.append(",");
                }
            }
            
            bw.write(sb.toString());
            bw.newLine();
        }
    }
    private String normalizeString(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // Bước 1: Chuẩn hóa Unicode (ví dụ: chuyển "ế" thành "e")
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKD);
        
        // Bước 2: Loại bỏ các ký tự dấu
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        normalized = pattern.matcher(normalized).replaceAll("");
        
        // Bước 3: Thay thế các ký tự đặc biệt bằng khoảng trắng hoặc ký tự an toàn
        normalized = normalized.replaceAll("[^\\p{ASCII}]", "");
        
        // Bước 4: Loại bỏ các ký tự có thể gây vấn đề cho CSV
        normalized = normalized.replace("\r", " ").replace("\n", " ");
        
        // Trả về chuỗi đã xử lý
        return normalized.trim();
    }
    private String escapeCSV(String text) {
        if (text == null || text.isEmpty()) {
            return "\"\"";
        }
        
        // Luôn đặt trong dấu ngoặc kép để đảm bảo tính nhất quán và hỗ trợ Unicode
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
    
    private String generateDefaultFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return "export_data_" + sdf.format(new Date());
    }
}