package view;

import controller.KhoVatTuController;
import model.KhoVatTu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ThongKeKhoVatTuPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private KhoVatTuController controller;

    private JComboBox<String> comboDonViTinh;
    private JComboBox<String> comboMaNCC;
    private JComboBox<String> comboPhanLoai;
    private JTextField txtNgayNhap;

    public ThongKeKhoVatTuPanel() {
        setLayout(new BorderLayout());

        controller = new KhoVatTuController();

        JLabel titleLabel = new JLabel("Thống kê Kho vật tư");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout());
        comboDonViTinh = new JComboBox<>(new String[]{"Tất cả", "hộp", "cái", "lọ", "bịch", "ống"});
        comboMaNCC = new JComboBox<>(new String[]{"Tất cả", "1", "2", "3", "4", "5"});
        comboPhanLoai = new JComboBox<>(new String[]{"Tất cả", "Vật tư tiêu hao", "Dụng cụ tiêm", "Dược phẩm", "Vật liệu phục hình", "Thiết bị dụng cụ", "Vật tư phụ trợ"});
        txtNgayNhap = new JTextField(10);

        filterPanel.add(new JLabel("Đơn vị tính:"));
        filterPanel.add(comboDonViTinh);
        filterPanel.add(new JLabel("Mã NCC:"));
        filterPanel.add(comboMaNCC);
        filterPanel.add(new JLabel("Phân loại:"));
        filterPanel.add(comboPhanLoai);
        filterPanel.add(new JLabel("Ngày nhập:"));
        filterPanel.add(txtNgayNhap);

        JButton btnLoc = new JButton("Lọc");
        filterPanel.add(btnLoc);

        JButton btnXuat = new JButton("Xuất");
        filterPanel.add(btnXuat);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem exportWord = new JMenuItem("Xuất ra Word");
        JMenuItem exportExcel = new JMenuItem("Xuất ra Excel");
        JMenuItem exportPDF = new JMenuItem("Xuất ra PDF");

        popupMenu.add(exportWord);
        popupMenu.add(exportExcel);
        popupMenu.add(exportPDF);

        btnXuat.addActionListener(e -> popupMenu.show(btnXuat, 0, btnXuat.getHeight()));

        // Gắn sự kiện đúng cho các nút xuất
        exportWord.addActionListener(e -> exportWord());
        exportExcel.addActionListener(e -> exportExcel());
        exportPDF.addActionListener(e -> exportPDF());

        add(filterPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{
                "Tên vật tư", "Số lượng", "Số lượng còn lại", "Đơn vị tính", "ID nhà cung cấp", "Phân loại", "Ngày nhập", "Hạn sử dụng"
        });

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        hienThiDanhSachVatTu();

        btnLoc.addActionListener(this::locVatTu);
    }

    private void hienThiDanhSachVatTu() {
        try {
            List<KhoVatTu> danhSach = controller.getAllKhoVatTu();
            tableModel.setRowCount(0);

            for (KhoVatTu vt : danhSach) {
                tableModel.addRow(new Object[]{
                        vt.getTenVatTu(),
                        vt.getSoLuong(),
                        vt.getSoLuongConLai(),
                        vt.getDonViTinh(),
                        vt.getMaNCC(),
                        vt.getPhanLoai(),
                        vt.getNgayNhap(),
                        vt.getHanSuDung(),
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách vật tư: " + e.getMessage());
        }
    }

    private void locVatTu(ActionEvent e) {
        String donViTinh = comboDonViTinh.getSelectedItem().toString();
        String maNCCStr = comboMaNCC.getSelectedItem().toString();
        String phanLoai = comboPhanLoai.getSelectedItem().toString();
        String ngayNhap = txtNgayNhap.getText().trim();

        try {
            List<KhoVatTu> danhSach = controller.getAllKhoVatTu();
            List<KhoVatTu> ketQua = new ArrayList<>();
            for (KhoVatTu vt : danhSach) {
                boolean match = true;

                if (!donViTinh.equals("Tất cả") && !vt.getDonViTinh().equalsIgnoreCase(donViTinh)) {
                    match = false;
                }
                if (!maNCCStr.equals("Tất cả") && !vt.getMaNCC().equals(maNCCStr)) {
                    match = false;
                }
                if (!phanLoai.equals("Tất cả") && !vt.getPhanLoai().equalsIgnoreCase(phanLoai)) {
                    match = false;
                }
                if (!ngayNhap.isEmpty() && vt.getNgayNhap() != null) {
                    if (!vt.getNgayNhap().equals(ngayNhap)) {
                        match = false;
                    }
                }

                if (match) {
                    ketQua.add(vt);
                }
            }

            tableModel.setRowCount(0);
            for (KhoVatTu vt : ketQua) {
                tableModel.addRow(new Object[]{
                        vt.getTenVatTu(),
                        vt.getSoLuong(),
                        vt.getSoLuongConLai(),
                        vt.getDonViTinh(),
                        vt.getMaNCC(),
                        vt.getPhanLoai(),
                        vt.getNgayNhap(),
                        vt.getHanSuDung(),
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc vật tư: " + ex.getMessage());
        }
    }

    // Xuất file Word
    public void exportWord() {
        try {
            XWPFDocument document = new XWPFDocument();
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Danh sách vật tư");

            XWPFTable table = document.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("Tên vật tư");
            headerRow.addNewTableCell().setText("Số lượng");
            headerRow.addNewTableCell().setText("Số lượng còn lại");
            headerRow.addNewTableCell().setText("Đơn vị tính");
            headerRow.addNewTableCell().setText("ID nhà cung cấp");
            headerRow.addNewTableCell().setText("Phân loại");
            headerRow.addNewTableCell().setText("Ngày nhập");
            headerRow.addNewTableCell().setText("Hạn sử dụng");

            List<KhoVatTu> danhSach = controller.getAllKhoVatTu();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (KhoVatTu vt : danhSach) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(vt.getTenVatTu());
                row.getCell(1).setText(String.valueOf(vt.getSoLuong()));
                row.getCell(2).setText(String.valueOf(vt.getSoLuongConLai()));
                row.getCell(3).setText(vt.getDonViTinh());
                row.getCell(4).setText(vt.getMaNCC());
                row.getCell(5).setText(vt.getPhanLoai());
                row.getCell(6).setText(vt.getNgayNhap());
                String hanSuDungStr = (vt.getHanSuDung() != null) ? sdf.format(vt.getHanSuDung()) : "";
                row.getCell(7).setText(hanSuDungStr);
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("DanhSachVatTu.docx"));
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                FileOutputStream out = new FileOutputStream(fileChooser.getSelectedFile());
                document.write(out);
                out.close();
                JOptionPane.showMessageDialog(this, "Xuất Word thành công!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Word: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Xuất file Excel
    public void exportExcel() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Danh sách vật tư");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Tên vật tư", "Số lượng", "Số lượng còn lại", "Đơn vị tính",
                    "ID nhà cung cấp", "Phân loại", "Ngày nhập", "Hạn sử dụng"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            List<KhoVatTu> danhSach = controller.getAllKhoVatTu();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            int rowNum = 1;
            for (KhoVatTu vt : danhSach) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(vt.getTenVatTu());
                row.createCell(1).setCellValue(vt.getSoLuong());
                row.createCell(2).setCellValue(vt.getSoLuongConLai());
                row.createCell(3).setCellValue(vt.getDonViTinh());
                row.createCell(4).setCellValue(vt.getMaNCC());
                row.createCell(5).setCellValue(vt.getPhanLoai());
                row.createCell(6).setCellValue(vt.getNgayNhap());
                String hanSuDungStr = (vt.getHanSuDung() != null) ? sdf.format(vt.getHanSuDung()) : "";
                row.createCell(7).setCellValue(hanSuDungStr);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("DanhSachVatTu.xlsx"));
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                FileOutputStream out = new FileOutputStream(fileChooser.getSelectedFile());
                workbook.write(out);
                out.close();
                workbook.close();
                JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Xuất file PDF
    public void exportPDF() {
        try {
            Document document = new Document();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("DanhSachVatTu.pdf"));
            int option = fileChooser.showSaveDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile()));
                document.open();

                com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Danh sách vật tư");
                title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(title);
                document.add(new com.itextpdf.text.Paragraph(" "));

                PdfPTable pdfTable = new PdfPTable(8);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{2, 1, 1, 1, 1, 1, 1, 1});

                String[] headers = {"Tên vật tư", "Số lượng", "Số lượng còn lại", "Đơn vị tính",
                        "ID nhà cung cấp", "Phân loại", "Ngày nhập", "Hạn sử dụng"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }

                List<KhoVatTu> danhSach = controller.getAllKhoVatTu();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                for (KhoVatTu vt : danhSach) {
                    pdfTable.addCell(vt.getTenVatTu());
                    pdfTable.addCell(String.valueOf(vt.getSoLuong()));
                    pdfTable.addCell(String.valueOf(vt.getSoLuongConLai()));
                    pdfTable.addCell(vt.getDonViTinh());
                    pdfTable.addCell(vt.getMaNCC());
                    pdfTable.addCell(vt.getPhanLoai());
                    pdfTable.addCell(vt.getNgayNhap());
                    String hanSuDungStr = (vt.getHanSuDung() != null) ? sdf.format(vt.getHanSuDung()) : "";
                    pdfTable.addCell(hanSuDungStr);
                }

                document.add(pdfTable);
                document.close();
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
            }
        } catch (DocumentException | java.io.IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}