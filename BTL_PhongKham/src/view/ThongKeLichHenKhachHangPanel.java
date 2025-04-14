package view;

import controller.LichHenController;
import model.LichHen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ThongKeLichHenKhachHangPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private LichHenController controller;
    private JComboBox<Integer> ngayComboBox;
    private JComboBox<Integer> thangComboBox;
    private JComboBox<Integer> namComboBox;
    private JComboBox<String> phongKhamComboBox;
    private JComboBox<String> trangThaiComboBox;
    private JComboBox<String> bacSiComboBox;
    private JButton btnThongKe;
    private JButton btnLocKhac;
    private JButton btnReset;
    private JButton btnExport;
    private JPopupMenu exportPopupMenu;
    private JMenuItem exportWordItem;
    private JMenuItem exportExcelItem;
    private JMenuItem exportPdfItem;
    private Calendar currentCalendar;
    private List<LichHen> allLichHen;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    private JPanel filterPanelRow1;

    public ThongKeLichHenKhachHangPanel() {
        setLayout(new BorderLayout());
        controller = new LichHenController();
        allLichHen = controller.getAllLichHen();
        currentCalendar = Calendar.getInstance();

        JLabel titleLabel = new JLabel("Thống kê Lịch hẹn & Khách hàng");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        filterPanelRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        filterPanelRow1.add(new JLabel("Ngày hẹn:"));
        ngayComboBox = new JComboBox<>();
        ngayComboBox.setPreferredSize(new Dimension(60, 30));
        filterPanelRow1.add(ngayComboBox);

        filterPanelRow1.add(new JLabel("Tháng:"));
        thangComboBox = new JComboBox<>();
        thangComboBox.addItem(null);
        for (int i = 1; i <= 12; i++) {
            thangComboBox.addItem(i);
        }
        thangComboBox.setSelectedItem(currentCalendar.get(Calendar.MONTH) + 1);
        thangComboBox.setPreferredSize(new Dimension(80, 30));
        filterPanelRow1.add(thangComboBox);

        filterPanelRow1.add(new JLabel("Năm:"));
        namComboBox = new JComboBox<>();
        namComboBox.addItem(null);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            namComboBox.addItem(i);
        }
        namComboBox.setSelectedItem(currentYear);
        namComboBox.setPreferredSize(new Dimension(80, 30));
        filterPanelRow1.add(namComboBox);

        btnThongKe = new JButton("Thống kê");
        btnThongKe.addActionListener(e -> locDanhSachLichHen());
        filterPanelRow1.add(btnThongKe);

        JPanel filterPanelRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        filterPanelRow2.add(new JLabel("Phòng khám:"));
        phongKhamComboBox = new JComboBox<>();
        phongKhamComboBox.addItem("Tất cả");
        List<String> uniquePhongKham = allLichHen.stream().map(LichHen::getTenPhong).distinct().sorted().collect(Collectors.toList());
        uniquePhongKham.forEach(phongKhamComboBox::addItem);
        phongKhamComboBox.setPreferredSize(new Dimension(150, 30));
        filterPanelRow2.add(phongKhamComboBox);

        filterPanelRow2.add(new JLabel("Trạng thái:"));
        trangThaiComboBox = new JComboBox<>(new String[]{"Tất cả", "Chờ xác nhận", "Đã xác nhận", "Đã hủy"});
        trangThaiComboBox.setPreferredSize(new Dimension(150, 30));
        filterPanelRow2.add(trangThaiComboBox);

        filterPanelRow2.add(new JLabel("Bác sĩ:"));
        bacSiComboBox = new JComboBox<>();
        bacSiComboBox.addItem("Tất cả");
        List<String> uniqueBacSi = allLichHen.stream().map(LichHen::getHoTenBacSi).distinct().sorted().collect(Collectors.toList());
        uniqueBacSi.forEach(bacSiComboBox::addItem);
        bacSiComboBox.setPreferredSize(new Dimension(150, 30));
        filterPanelRow2.add(bacSiComboBox);

        btnLocKhac = new JButton("Lọc");
        btnLocKhac.addActionListener(e -> locDanhSachLichHenTheoKhac());
        filterPanelRow2.add(btnLocKhac);

        btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> resetFilters());
        filterPanelRow2.add(btnReset);

        btnExport = new JButton("Xuất");
        exportPopupMenu = new JPopupMenu();
        exportWordItem = new JMenuItem("Word");
        exportExcelItem = new JMenuItem("Excel");
        exportPdfItem = new JMenuItem("PDF");

        exportWordItem.addActionListener(e -> exportToWord());
        exportExcelItem.addActionListener(e -> exportToExcel());
        exportPdfItem.addActionListener(e -> exportToPdf());

        exportPopupMenu.add(exportWordItem);
        exportPopupMenu.add(exportExcelItem);
        exportPopupMenu.add(exportPdfItem);

        btnExport.addActionListener(e -> exportPopupMenu.show(btnExport, 0, btnExport.getHeight()));
        filterPanelRow2.add(btnExport);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.add(filterPanelRow1);
        filterPanel.add(filterPanelRow2);

        add(filterPanel, BorderLayout.PAGE_START);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{
                "ID Lịch hẹn", "Tên bác sĩ", "Tên bệnh nhân", "Ngày hẹn", "Giờ hẹn", "Phòng khám", "Trạng thái", "Mô tả"
        });
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        hienThiDanhSachLichHenTheoThangNam(currentCalendar.get(Calendar.MONTH) + 1, currentCalendar.get(Calendar.YEAR));

        thangComboBox.addActionListener(e -> updateDaysInMonth());
        namComboBox.addActionListener(e -> updateDaysInMonth());

        updateDaysInMonth(); // Khởi tạo số ngày ban đầu
    }

    private void updateDaysInMonth() {
        Integer selectedThang = (Integer) thangComboBox.getSelectedItem();
        Integer selectedNam = (Integer) namComboBox.getSelectedItem();
        ngayComboBox.removeAllItems();
        ngayComboBox.addItem(null); // Thêm tùy chọn không chọn ngày

        if (selectedThang != null && selectedNam != null) {
            int daysInMonth;
            if (selectedThang == 2) {
                daysInMonth = (selectedNam % 4 == 0 && (selectedNam % 100 != 0 || selectedNam % 400 == 0)) ? 29 : 28;
            } else if (selectedThang == 4 || selectedThang == 6 || selectedThang == 9 || selectedThang == 11) {
                daysInMonth = 30;
            } else {
                daysInMonth = 31;
            }
            for (int i = 1; i <= daysInMonth; i++) {
                ngayComboBox.addItem(i);
            }
        }
    }

    private void hienThiDanhSachLichHen(List<LichHen> danhSach) {
        tableModel.setRowCount(0);
        for (LichHen lh : danhSach) {
            tableModel.addRow(new Object[]{
                    lh.getIdLichHen(), lh.getHoTenBacSi(), lh.getHoTenBenhNhan(), sdf.format(lh.getNgayHen()),
                    lh.getGioHen(), lh.getTenPhong(), lh.getTrangThai(), lh.getMoTa()
            });
        }
    }

    private void hienThiDanhSachLichHenTheoThangNam(Integer thang, Integer nam) {
        List<LichHen> danhSachTheoThangNam = allLichHen.stream().filter(lh -> {
            Calendar lhCalendar = Calendar.getInstance();
            lhCalendar.setTime(lh.getNgayHen());
            boolean thangMatch = (thang == null) || (lhCalendar.get(Calendar.MONTH) + 1 == thang);
            boolean namMatch = (nam == null) || (lhCalendar.get(Calendar.YEAR) == nam);
            return thangMatch && namMatch;
        }).collect(Collectors.toList());
        hienThiDanhSachLichHen(danhSachTheoThangNam);
    }

    private void locDanhSachLichHen() {
        Integer selectedNgay = (Integer) ngayComboBox.getSelectedItem();
        Integer selectedThang = (Integer) thangComboBox.getSelectedItem();
        Integer selectedNam = (Integer) namComboBox.getSelectedItem();
        String selectedPhongKham = (String) phongKhamComboBox.getSelectedItem();
        String selectedTrangThai = (String) trangThaiComboBox.getSelectedItem();
        String selectedBacSi = (String) bacSiComboBox.getSelectedItem();

        List<LichHen> filteredList = allLichHen.stream().filter(lh -> {
            Calendar lhCalendar = Calendar.getInstance();
            lhCalendar.setTime(lh.getNgayHen());

            boolean ngayMatch = (selectedNgay == null) || (selectedNgay.equals(lhCalendar.get(Calendar.DAY_OF_MONTH)));
            boolean thangMatch = (selectedThang == null) || (selectedThang.equals(lhCalendar.get(Calendar.MONTH) + 1));
            boolean namMatch = (selectedNam == null) || (selectedNam.equals(lhCalendar.get(Calendar.YEAR)));
            boolean phongKhamMatch = "Tất cả".equals(selectedPhongKham) || lh.getTenPhong().equals(selectedPhongKham);
            boolean trangThaiMatch = "Tất cả".equals(selectedTrangThai) || lh.getTrangThai().equals(selectedTrangThai);
            boolean bacSiMatch = "Tất cả".equals(selectedBacSi) || lh.getHoTenBacSi().equals(selectedBacSi);

            return ngayMatch && thangMatch && namMatch && phongKhamMatch && trangThaiMatch && bacSiMatch;
        }).collect(Collectors.toList());

        hienThiDanhSachLichHen(filteredList);
    }

    private void locDanhSachLichHenTheoKhac() {
        String selectedPhongKham = (String) phongKhamComboBox.getSelectedItem();
        String selectedTrangThai = (String) trangThaiComboBox.getSelectedItem();
        String selectedBacSi = (String) bacSiComboBox.getSelectedItem();

        List<LichHen> filteredList = allLichHen.stream().filter(lh -> {
            boolean phongKhamMatch = "Tất cả".equals(selectedPhongKham) || lh.getTenPhong().equals(selectedPhongKham);
            boolean trangThaiMatch = "Tất cả".equals(selectedTrangThai) || lh.getTrangThai().equals(selectedTrangThai);
            boolean bacSiMatch = "Tất cả".equals(selectedBacSi) || lh.getHoTenBacSi().equals(selectedBacSi);
            return phongKhamMatch && trangThaiMatch && bacSiMatch;
        }).collect(Collectors.toList());

        hienThiDanhSachLichHen(filteredList);
    }

    private void resetFilters() {
        phongKhamComboBox.setSelectedItem("Tất cả");
        trangThaiComboBox.setSelectedItem("Tất cả");
        bacSiComboBox.setSelectedItem("Tất cả");

        ngayComboBox.setSelectedItem(null);
        thangComboBox.setSelectedItem(currentCalendar.get(Calendar.MONTH) + 1);
        namComboBox.setSelectedItem(currentCalendar.get(Calendar.YEAR));

        hienThiDanhSachLichHenTheoThangNam(currentCalendar.get(Calendar.MONTH) + 1, currentCalendar.get(Calendar.YEAR));
        updateDaysInMonth(); // Cập nhật lại số ngày khi reset tháng/năm
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".xlsx");
            }
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream outputStream = new FileOutputStream(selectedFile)) {
                Sheet sheet = workbook.createSheet("Thống kê Lịch hẹn");
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    headerRow.createCell(i).setCellValue(tableModel.getColumnName(i));
                }
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Row dataRow = sheet.createRow(i + 1);
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        dataRow.createCell(j).setCellValue(value != null ? value.toString() : "");
                    }
                }
                workbook.write(outputStream);
                JOptionPane.showMessageDialog(this, "Đã xuất dữ liệu ra file Excel thành công!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel: " + ex.getMessage());
            }
        }
    }

    private void exportToPdf() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
            }
            try (FileOutputStream outputStream = new FileOutputStream(selectedFile)) {
                Document document = new Document();
                PdfWriter.getInstance(document, outputStream);
                document.open();

                BaseFont baseFont = BaseFont.createFont("arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font font = new Font(baseFont, 10);
                Font titleFont = new Font(baseFont, 14, Font.BOLD);

                document.add(new Paragraph("Thống kê Lịch hẹn & Khách hàng", titleFont));
                document.add(new Paragraph("Ngày xuất: " + sdf.format(new Date()), font));
                document.add(new Paragraph(" "));

                PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount());
                pdfTable.setWidthPercentage(100);

                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    pdfTable.addCell(new Paragraph(tableModel.getColumnName(i), font));
                }

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        pdfTable.addCell(new Paragraph(value != null ? value.toString() : "", font));
                    }
                }

                document.add(pdfTable);
                document.close();
                JOptionPane.showMessageDialog(this, "Đã xuất dữ liệu ra file PDF thành công!");

            } catch (DocumentException | IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file PDF: " + ex.getMessage());
            }
        }
    }

    private void exportToWord() {
        try {
            XWPFDocument document = new XWPFDocument();
            XWPFParagraph titlePara = document.createParagraph();
            titlePara.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText("Thống kê Lịch hẹn & Khách hàng");
            titleRun.setFontSize(14);
            titleRun.setBold(true);

            XWPFParagraph datePara = document.createParagraph();
            datePara.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.RIGHT);
            XWPFRun dateRun = datePara.createRun();
            dateRun.setText("Ngày xuất: " + sdf.format(new Date()));
            dateRun.setFontSize(10);

            XWPFParagraph contentPara = document.createParagraph();
            XWPFRun contentRun = contentPara.createRun();

            XWPFTable wordTable = document.createTable();

            XWPFTableRow headerRow = wordTable.getRow(0);
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                XWPFTableCell cell = headerRow.getCell(i);
                if (cell == null) {
                    cell = headerRow.createCell();
                }
                cell.setText(tableModel.getColumnName(i));
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                XWPFTableRow dataRow = wordTable.createRow();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    XWPFTableCell cell = dataRow.getCell(j);
                    if (cell == null) {
                        cell = dataRow.createCell();
                    }
                    Object value = tableModel.getValueAt(i, j);
                    cell.setText(value != null ? value.toString() : "");
                }
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("ThongKeLichHen_" + monthYearFormat.format(currentCalendar.getTime()) + ".docx"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Word Documents (*.docx)", "docx");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (!selectedFile.getName().toLowerCase().endsWith(".docx")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".docx");
                }
                try (FileOutputStream outputStream = new FileOutputStream(selectedFile)) {
                    document.write(outputStream);
                    JOptionPane.showMessageDialog(this, "Đã xuất dữ liệu ra file Word thành công!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Word: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Word: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void reloadData() {
        allLichHen = controller.getAllLichHen();
        phongKhamComboBox.removeAllItems();
        phongKhamComboBox.addItem("Tất cả");
        List<String> uniquePhongKham = allLichHen.stream().map(LichHen::getTenPhong).distinct().sorted().collect(Collectors.toList());
        uniquePhongKham.forEach(phongKhamComboBox::addItem);

        bacSiComboBox.removeAllItems();
        bacSiComboBox.addItem("Tất cả");
        List<String> uniqueBacSi = allLichHen.stream().map(LichHen::getHoTenBacSi).distinct().sorted().collect(Collectors.toList());
        uniqueBacSi.forEach(bacSiComboBox::addItem);

        hienThiDanhSachLichHenTheoThangNam(currentCalendar.get(Calendar.MONTH) + 1, currentCalendar.get(Calendar.YEAR));
        updateDaysInMonth();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Thống kê Lịch hẹn & Khách hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new ThongKeLichHenKhachHangPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}