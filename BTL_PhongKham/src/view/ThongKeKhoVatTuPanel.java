package view;

import controller.KhoVatTuController;
import model.KhoVatTu;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

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

    private JTable tableNhapTrongThang;
    private DefaultTableModel tableModelNhapTrongThang;
    private JTable tableTonThangTruoc;
    private DefaultTableModel tableModelTonThangTruoc;
    private JTable tableHetHan;
    private DefaultTableModel tableModelHetHan;

    private KhoVatTuController controller;

    private JComboBox<Integer> ngayComboBox;
    private JComboBox<Integer> thangComboBox;
    private JComboBox<Integer> namComboBox;
    private JComboBox<String> comboDonViTinh;
    private JComboBox<String> comboMaNCC;
    private JComboBox<String> comboPhanLoai;
    private JButton btnThongKe;
    private JButton btnLocKhac;
    private JButton btnReset;
    private JButton btnExport;
    private JPopupMenu exportPopupMenu;
    private JMenuItem exportWordItem;
    private JMenuItem exportExcelItem;
    private JMenuItem exportPdfItem;
    private JLabel lblThangNam;

    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Calendar currentCalendar;
    private List<KhoVatTu> allKhoVatTu;

    public ThongKeKhoVatTuPanel() {
        setLayout(new BorderLayout());
        controller = new KhoVatTuController();
        allKhoVatTu = controller.getAllKhoVatTu();
        currentCalendar = Calendar.getInstance();

        JLabel titleLabel = new JLabel("Thống kê Kho vật tư");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanelRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        filterPanelRow1.add(new JLabel("Ngày nhập:"));
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
        btnThongKe.addActionListener(e -> hienThiThongKeTheoNgayThangNam());
        filterPanelRow1.add(btnThongKe);

        JPanel filterPanelRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        filterPanelRow2.add(new JLabel("Đơn vị tính:"));
        comboDonViTinh = new JComboBox<>();
        comboDonViTinh.addItem("Tất cả");
        List<String> uniqueDVT = allKhoVatTu.stream().map(KhoVatTu::getDonViTinh).distinct().sorted().collect(Collectors.toList());
        uniqueDVT.forEach(comboDonViTinh::addItem);
        comboDonViTinh.setPreferredSize(new Dimension(120, 30));
        filterPanelRow2.add(comboDonViTinh);

        filterPanelRow2.add(new JLabel("Mã NCC:"));
        comboMaNCC = new JComboBox<>();
        comboMaNCC.addItem("Tất cả");
        List<String> uniqueNCC = allKhoVatTu.stream().map(KhoVatTu::getMaNCC).distinct().sorted().collect(Collectors.toList());
        uniqueNCC.forEach(comboMaNCC::addItem);
        comboMaNCC.setPreferredSize(new Dimension(100, 30));
        filterPanelRow2.add(comboMaNCC);

        filterPanelRow2.add(new JLabel("Phân loại:"));
        comboPhanLoai = new JComboBox<>();
        comboPhanLoai.addItem("Tất cả");
        List<String> uniquePL = allKhoVatTu.stream().map(KhoVatTu::getPhanLoai).distinct().sorted().collect(Collectors.toList());
        uniquePL.forEach(comboPhanLoai::addItem);
        comboPhanLoai.setPreferredSize(new Dimension(150, 30));
        filterPanelRow2.add(comboPhanLoai);

        btnLocKhac = new JButton("Lọc");
        btnLocKhac.addActionListener(e -> locVatTu());
        filterPanelRow2.add(btnLocKhac);

        btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> resetFilters());
        filterPanelRow2.add(btnReset);

        btnExport = new JButton("Xuất");
        exportPopupMenu = new JPopupMenu();
        exportWordItem = new JMenuItem("Word");
        exportExcelItem = new JMenuItem("Excel");
        exportPdfItem = new JMenuItem("PDF");

        exportWordItem.addActionListener(e -> exportWord());
        exportExcelItem.addActionListener(e -> exportExcel());
        exportPdfItem.addActionListener(e -> exportPDF());

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

        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Phần 1: Vật tư đã nhập trong tháng
        JPanel panelNhapTrongThang = new JPanel(new BorderLayout());
        panelNhapTrongThang.add(new JLabel("Vật tư đã nhập trong tháng:", JLabel.CENTER), BorderLayout.NORTH);
        tableModelNhapTrongThang = new DefaultTableModel();
        tableModelNhapTrongThang.setColumnIdentifiers(new String[]{
                "Tên vật tư", "Số lượng", "Đơn vị tính", "Ngày nhập", "Hạn sử dụng"
        });
        tableNhapTrongThang = new JTable(tableModelNhapTrongThang);
        panelNhapTrongThang.add(new JScrollPane(tableNhapTrongThang), BorderLayout.CENTER);
        contentPanel.add(panelNhapTrongThang);

        // Phần 2: Vật tư tồn từ tháng trước
        JPanel panelTonThangTruoc = new JPanel(new BorderLayout());
        panelTonThangTruoc.add(new JLabel("Vật tư tồn từ tháng trước:", JLabel.CENTER), BorderLayout.NORTH);
        tableModelTonThangTruoc = new DefaultTableModel();
        tableModelTonThangTruoc.setColumnIdentifiers(new String[]{
                "Tên vật tư", "Số lượng còn lại", "Đơn vị tính", "Hạn sử dụng"
        });
        tableTonThangTruoc = new JTable(tableModelTonThangTruoc);
        panelTonThangTruoc.add(new JScrollPane(tableTonThangTruoc), BorderLayout.CENTER);
        contentPanel.add(panelTonThangTruoc);

        // Phần 3: Vật tư đã hết hạn sử dụng
        JPanel panelHetHan = new JPanel(new BorderLayout());
        panelHetHan.add(new JLabel("Vật tư đã hết hạn sử dụng:", JLabel.CENTER), BorderLayout.NORTH);
        tableModelHetHan = new DefaultTableModel();
        tableModelHetHan.setColumnIdentifiers(new String[]{
                "Tên vật tư", "Số lượng còn lại", "Đơn vị tính", "Hạn sử dụng"
        });
        tableHetHan = new JTable(tableModelHetHan);
        panelHetHan.add(new JScrollPane(tableHetHan), BorderLayout.CENTER);
        contentPanel.add(panelHetHan);

        add(contentPanel, BorderLayout.CENTER);

        thangComboBox.addActionListener(e -> updateDaysInMonth());
        namComboBox.addActionListener(e -> updateDaysInMonth());

        updateDaysInMonth();
        hienThiThongKeTheoThangNam(currentCalendar.get(Calendar.MONTH) + 1, currentCalendar.get(Calendar.YEAR));
    }

    private void updateDaysInMonth() {
        Integer selectedThang = (Integer) thangComboBox.getSelectedItem();
        Integer selectedNam = (Integer) namComboBox.getSelectedItem();
        ngayComboBox.removeAllItems();
        ngayComboBox.addItem(null);

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

    private void hienThiThongKeTheoNgayThangNam() {
        Integer selectedNgay = (Integer) ngayComboBox.getSelectedItem();
        Integer selectedThang = (Integer) thangComboBox.getSelectedItem();
        Integer selectedNam = (Integer) namComboBox.getSelectedItem();
        hienThiThongKe(selectedNgay, selectedThang, selectedNam);
    }

    private void hienThiThongKeTheoThangNam(Integer thang, Integer nam) {
        hienThiThongKe(null, thang, nam);
    }

    private void hienThiThongKe(Integer ngay, Integer thang, Integer nam) {
        try {
            List<KhoVatTu> danhSach = controller.getAllKhoVatTu();

            final Calendar startOfMonth = Calendar.getInstance();
            if (thang != null && nam != null) {
                startOfMonth.set(Calendar.YEAR, nam);
                startOfMonth.set(Calendar.MONTH, thang - 1);
            }
            startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
            startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
            startOfMonth.set(Calendar.MINUTE, 0);
            startOfMonth.set(Calendar.SECOND, 0);
            startOfMonth.set(Calendar.MILLISECOND, 0);
            final Date firstDayOfMonth = startOfMonth.getTime();

            final Calendar endOfMonth = Calendar.getInstance();
            if (thang != null && nam != null) {
                endOfMonth.set(Calendar.YEAR, nam);
                endOfMonth.set(Calendar.MONTH, thang - 1);
            }
            endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
            endOfMonth.set(Calendar.MINUTE, 59);
            endOfMonth.set(Calendar.SECOND, 59);
            endOfMonth.set(Calendar.MILLISECOND, 999);
            final Date lastDayOfMonth = endOfMonth.getTime();

            final Calendar selectedDateCal = Calendar.getInstance();
            final boolean isDateSelected = (ngay != null && thang != null && nam != null);
            if (isDateSelected) {
                selectedDateCal.set(Calendar.YEAR, nam);
                selectedDateCal.set(Calendar.MONTH, thang - 1);
                selectedDateCal.set(Calendar.DAY_OF_MONTH, ngay);
                selectedDateCal.set(Calendar.HOUR_OF_DAY, 0);
                selectedDateCal.set(Calendar.MINUTE, 0);
                selectedDateCal.set(Calendar.SECOND, 0);
                selectedDateCal.set(Calendar.MILLISECOND, 0);
            }

            final Date filterDateStart;
            final Date filterDateEnd;

            if (isDateSelected) {
                filterDateStart = selectedDateCal.getTime();
                final Calendar nextDay = (Calendar) selectedDateCal.clone();
                nextDay.add(Calendar.DAY_OF_MONTH, 1);
                filterDateEnd = nextDay.getTime();
            } else if (thang != null && nam != null) {
                filterDateStart = firstDayOfMonth;
                filterDateEnd = lastDayOfMonth;
            } else {
                filterDateStart = new Date(0);
                filterDateEnd = new Date(Long.MAX_VALUE);
            }


            // Phần 1: Vật tư đã nhập trong khoảng thời gian
            List<KhoVatTu> nhapTrongThang = danhSach.stream()
                    .filter(vt -> {
                        try {
                            final Date ngayNhapDate = dateFormat.parse(vt.getNgayNhap());
                            return ngayNhapDate.after(filterDateStart) && ngayNhapDate.before(filterDateEnd);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            populateTableNhapTrongThang(nhapTrongThang);

            // Phần 2: Vật tư tồn từ tháng trước
            final Calendar prevMonthEnd = (Calendar) startOfMonth.clone();
            prevMonthEnd.add(Calendar.DAY_OF_MONTH, -1);
            final Date endOfPrevMonth = prevMonthEnd.getTime();

            List<KhoVatTu> tonThangTruoc = danhSach.stream()
                    .filter(vt -> {
                        if (vt.getSoLuongConLai() > 0) {
                            try {
                                final Date ngayNhapDate = dateFormat.parse(vt.getNgayNhap());
                                return ngayNhapDate.before(firstDayOfMonth) &&
                                       (vt.getHanSuDung() == null || vt.getHanSuDung().after(endOfPrevMonth));
                            } catch (Exception e) {
                                return false;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            populateTableTonThangTruoc(tonThangTruoc);

            // Phần 3: Vật tư đã hết hạn sử dụng
            List<KhoVatTu> hetHan = danhSach.stream()
                    .filter(vt -> vt.getHanSuDung() != null && vt.getHanSuDung().before(filterDateEnd))
                    .collect(Collectors.toList());
            populateTableHetHan(hetHan);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách vật tư: " + e.getMessage());
        }
    }

    private void populateTableNhapTrongThang(List<KhoVatTu> danhSach) {
        tableModelNhapTrongThang.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (KhoVatTu vt : danhSach) {
            tableModelNhapTrongThang.addRow(new Object[]{
                    vt.getTenVatTu(),
                    vt.getSoLuong(),
                    vt.getDonViTinh(),
                    vt.getNgayNhap() != null ? vt.getNgayNhap() : "",
                    vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : ""
            });
        }
    }

    private void populateTableTonThangTruoc(List<KhoVatTu> danhSach) {
        tableModelTonThangTruoc.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (KhoVatTu vt : danhSach) {
            tableModelTonThangTruoc.addRow(new Object[]{
                    vt.getTenVatTu(),
                    vt.getSoLuongConLai(),
                    vt.getDonViTinh(),
                    vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : ""
            });
        }
    }

    private void populateTableHetHan(List<KhoVatTu> danhSach) {
        tableModelHetHan.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (KhoVatTu vt : danhSach) {
            tableModelHetHan.addRow(new Object[]{
                    vt.getTenVatTu(),
                    vt.getSoLuongConLai(),
                    vt.getDonViTinh(),
                    vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : ""
            });
        }
    }

    private void locVatTu() {
        final String donViTinh = (String) comboDonViTinh.getSelectedItem();
        final String maNCCStr = (String) comboMaNCC.getSelectedItem();
        final String phanLoai = (String) comboPhanLoai.getSelectedItem();
        final Integer selectedNgay = (Integer) ngayComboBox.getSelectedItem();
        final Integer selectedThang = (Integer) thangComboBox.getSelectedItem();
        final Integer selectedNam = (Integer) namComboBox.getSelectedItem();

        List<KhoVatTu> filteredList = allKhoVatTu.stream()
                .filter(vt ->
                        ("Tất cả".equals(donViTinh) || vt.getDonViTinh().equalsIgnoreCase(donViTinh)) &&
                        ("Tất cả".equals(maNCCStr) || vt.getMaNCC().equals(maNCCStr)) &&
                        ("Tất cả".equals(phanLoai) || vt.getPhanLoai().equalsIgnoreCase(phanLoai))
                )
                .collect(Collectors.toList());

        hienThiThongKe(selectedNgay, selectedThang, selectedNam, filteredList);
    }

    private void hienThiThongKe(final Integer ngay, final Integer thang, final Integer nam, final List<KhoVatTu> danhSachLoc) {
        try {
            final Calendar startOfMonth = Calendar.getInstance();
            if (thang != null && nam != null) {
                startOfMonth.set(Calendar.YEAR, nam);
                startOfMonth.set(Calendar.MONTH, thang - 1);
            }
            startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
            startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
            startOfMonth.set(Calendar.MINUTE, 0);
            startOfMonth.set(Calendar.SECOND, 0);
            startOfMonth.set(Calendar.MILLISECOND, 0);
            final Date firstDayOfMonth = startOfMonth.getTime();

            final Calendar endOfMonth = Calendar.getInstance();
            if (thang != null && nam != null) {
                endOfMonth.set(Calendar.YEAR, nam);
                endOfMonth.set(Calendar.MONTH, thang - 1);
            }
            endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
            endOfMonth.set(Calendar.MINUTE, 59);
            endOfMonth.set(Calendar.SECOND, 59);
            endOfMonth.set(Calendar.MILLISECOND, 999);
            final Date lastDayOfMonth = endOfMonth.getTime();

            final Calendar selectedDateCal = Calendar.getInstance();
            final boolean isDateSelected = (ngay != null && thang != null && nam != null);
            if (isDateSelected) {
                selectedDateCal.set(Calendar.YEAR, nam);
                selectedDateCal.set(Calendar.MONTH, thang - 1);
                selectedDateCal.set(Calendar.DAY_OF_MONTH, ngay);
                selectedDateCal.set(Calendar.HOUR_OF_DAY, 0);
                selectedDateCal.set(Calendar.MINUTE, 0);
                selectedDateCal.set(Calendar.SECOND, 0);
                selectedDateCal.set(Calendar.MILLISECOND, 0);
            }

            final Date filterDateStart;
            final Date filterDateEnd;

            if (isDateSelected) {
                filterDateStart = selectedDateCal.getTime();
                final Calendar nextDay = (Calendar) selectedDateCal.clone();
                nextDay.add(Calendar.DAY_OF_MONTH, 1);
                filterDateEnd = nextDay.getTime();
            } else if (thang != null && nam != null) {
                filterDateStart = firstDayOfMonth;
                filterDateEnd = lastDayOfMonth;
            } else {
                filterDateStart = new Date(0);
                filterDateEnd = new Date(Long.MAX_VALUE);
            }


            // Phần 1: Vật tư đã nhập trong khoảng thời gian
            List<KhoVatTu> nhapTrongThang = danhSachLoc.stream()
                    .filter(vt -> {
                        try {
                            final Date ngayNhapDate = dateFormat.parse(vt.getNgayNhap());
                            return ngayNhapDate.after(filterDateStart) && ngayNhapDate.before(filterDateEnd);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            populateTableNhapTrongThang(nhapTrongThang);

            // Phần 2: Vật tư tồn từ tháng trước
            final Calendar prevMonthEnd = (Calendar) startOfMonth.clone();
            prevMonthEnd.add(Calendar.DAY_OF_MONTH, -1);
            final Date endOfPrevMonth = prevMonthEnd.getTime();

            List<KhoVatTu> tonThangTruoc = danhSachLoc.stream()
                    .filter(vt -> {
                        if (vt.getSoLuongConLai() > 0) {
                            try {
                                final Date ngayNhapDate = dateFormat.parse(vt.getNgayNhap());
                                return ngayNhapDate.before(firstDayOfMonth) &&
                                       (vt.getHanSuDung() == null || vt.getHanSuDung().after(endOfPrevMonth));
                            } catch (Exception e) {
                                return false;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            populateTableTonThangTruoc(tonThangTruoc);

            // Phần 3: Vật tư đã hết hạn sử dụng
            List<KhoVatTu> hetHan = danhSachLoc.stream()
                    .filter(vt -> vt.getHanSuDung() != null && vt.getHanSuDung().before(filterDateEnd))
                    .collect(Collectors.toList());
            populateTableHetHan(hetHan);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách vật tư: " + e.getMessage());
        }
    }

    private void resetFilters() {
        ngayComboBox.setSelectedItem(null);
        thangComboBox.setSelectedItem(currentCalendar.get(Calendar.MONTH) + 1);
        namComboBox.setSelectedItem(currentCalendar.get(Calendar.YEAR));
        comboDonViTinh.setSelectedItem("Tất cả");
        comboMaNCC.setSelectedItem("Tất cả");
        comboPhanLoai.setSelectedItem("Tất cả");
        hienThiThongKeTheoThangNam(currentCalendar.get(Calendar.MONTH) + 1, currentCalendar.get(Calendar.YEAR));
        updateDaysInMonth();
    }

    // Xuất file Word
    public void exportWord() {
        try {
            XWPFDocument document = new XWPFDocument();
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Thống kê Kho vật tư - Tháng " + monthYearFormat.format(currentCalendar.getTime()));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            final Calendar startOfMonth = (Calendar) currentCalendar.clone();
            startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
            startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
            startOfMonth.set(Calendar.MINUTE, 0);
            startOfMonth.set(Calendar.SECOND, 0);
            startOfMonth.set(Calendar.MILLISECOND, 0);
            final Calendar endOfMonth = (Calendar) currentCalendar.clone();
            endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
            endOfMonth.set(Calendar.MINUTE, 59);
            endOfMonth.set(Calendar.SECOND, 59);
            endOfMonth.set(Calendar.MILLISECOND, 999);
            final Calendar endOfCurrentMonth = (Calendar) currentCalendar.clone();
            endOfCurrentMonth.set(Calendar.DAY_OF_MONTH, endOfCurrentMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            endOfCurrentMonth.set(Calendar.HOUR_OF_DAY, 23);
            endOfCurrentMonth.set(Calendar.MINUTE, 59);
            endOfCurrentMonth.set(Calendar.SECOND, 59);
            endOfCurrentMonth.set(Calendar.MILLISECOND, 999);
            final Date endOfMonthDate = endOfCurrentMonth.getTime();

            // Phần 1: Vật tư đã nhập trong tháng
            XWPFParagraph paraNhap = document.createParagraph();
            XWPFRun runNhap = paraNhap.createRun();
            runNhap.setText("Vật tư đã nhập trong tháng:");
            XWPFTable tableNhap = document.createTable();
            // Thêm header cho bảng nhập trong tháng
            XWPFTableRow headerRowNhap = tableNhap.getRow(0);
            headerRowNhap.getCell(0).setText("Tên vật tư");
            headerRowNhap.addNewTableCell().setText("Số lượng");
            headerRowNhap.addNewTableCell().setText("Đơn vị tính");
            headerRowNhap.addNewTableCell().setText("Ngày nhập");
            headerRowNhap.addNewTableCell().setText("Hạn sử dụng");

            List<KhoVatTu> nhapTrongThang = controller.getAllKhoVatTu().stream()
                    .filter(vt -> {
                        try {
                            final Date ngayNhapDate = dateFormat.parse(vt.getNgayNhap());
                            return ngayNhapDate.after(startOfMonth.getTime()) && ngayNhapDate.before(endOfMonth.getTime());
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

            for (KhoVatTu vt : nhapTrongThang) {
                XWPFTableRow row = tableNhap.createRow();
                row.getCell(0).setText(vt.getTenVatTu());
                row.getCell(1).setText(String.valueOf(vt.getSoLuong()));
                row.getCell(2).setText(vt.getDonViTinh());
                row.getCell(3).setText(vt.getNgayNhap());
                row.getCell(4).setText(vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : "");
            }
            document.createParagraph(); // Khoảng trắng giữa các phần

            // Phần 2: Vật tư tồn từ tháng trước
            XWPFParagraph paraTon = document.createParagraph();
            XWPFRun runTon = paraTon.createRun();
            runTon.setText("Vật tư tồn từ tháng trước:");
            XWPFTable tableTon = document.createTable();
            // Thêm header cho bảng tồn tháng trước
            XWPFTableRow headerRowTon = tableTon.getRow(0);
            headerRowTon.getCell(0).setText("Tên vật tư");
            headerRowTon.addNewTableCell().setText("Số lượng còn lại");
            headerRowTon.addNewTableCell().setText("Đơn vị tính");
            headerRowTon.addNewTableCell().setText("Hạn sử dụng");

            List<KhoVatTu> tonThangTruoc = controller.getAllKhoVatTu().stream()
                    .filter(vt -> vt.getSoLuongConLai() > 0 && !nhapTrongThang.contains(vt))
                    .collect(Collectors.toList());

            for (KhoVatTu vt : tonThangTruoc) {
                XWPFTableRow row = tableTon.createRow();
                row.getCell(0).setText(vt.getTenVatTu());
                row.getCell(1).setText(String.valueOf(vt.getSoLuongConLai()));
                row.getCell(2).setText(vt.getDonViTinh());
                row.getCell(3).setText(vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : "");
            }
            document.createParagraph(); // Khoảng trắng giữa các phần

            // Phần 3: Vật tư đã hết hạn sử dụng
            XWPFParagraph paraHetHan = document.createParagraph();
            XWPFRun runHetHan = paraHetHan.createRun();
            runHetHan.setText("Vật tư đã hết hạn sử dụng:");
            XWPFTable tableHetHan = document.createTable();
            // Thêm header cho bảng hết hạn
            XWPFTableRow headerRowHetHan = tableHetHan.getRow(0);
            headerRowHetHan.getCell(0).setText("Tên vật tư");
            headerRowHetHan.addNewTableCell().setText("Số lượng còn lại");
            headerRowHetHan.addNewTableCell().setText("Đơn vị tính");
            headerRowHetHan.addNewTableCell().setText("Hạn sử dụng");

            List<KhoVatTu> hetHan = controller.getAllKhoVatTu().stream()
                    .filter(vt -> vt.getHanSuDung() != null && vt.getHanSuDung().before(endOfMonthDate))
                    .collect(Collectors.toList());

            for (KhoVatTu vt : hetHan) {
                XWPFTableRow row = tableHetHan.createRow();
                row.getCell(0).setText(vt.getTenVatTu());
                row.getCell(1).setText(String.valueOf(vt.getSoLuongConLai()));
                row.getCell(2).setText(vt.getDonViTinh());
                row.getCell(3).setText(vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : "");
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("ThongKeVatTu_" + monthYearFormat.format(currentCalendar.getTime()) + ".docx"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Word Documents (*.docx)", "docx");
            fileChooser.setFileFilter(filter);
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            final Calendar startOfMonth = (Calendar) currentCalendar.clone();
            startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
            startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
            startOfMonth.set(Calendar.MINUTE, 0);
            startOfMonth.set(Calendar.SECOND, 0);
            startOfMonth.set(Calendar.MILLISECOND, 0);
            final Calendar endOfMonth = (Calendar) currentCalendar.clone();
            endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
            endOfMonth.set(Calendar.MINUTE, 59);
            endOfMonth.set(Calendar.SECOND, 59);
            endOfMonth.set(Calendar.MILLISECOND, 999);
            final Calendar endOfCurrentMonth = (Calendar) currentCalendar.clone();
            endOfCurrentMonth.set(Calendar.DAY_OF_MONTH, endOfCurrentMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            endOfCurrentMonth.set(Calendar.HOUR_OF_DAY, 23);
            endOfCurrentMonth.set(Calendar.MINUTE, 59);
            endOfCurrentMonth.set(Calendar.SECOND, 59);
            endOfCurrentMonth.set(Calendar.MILLISECOND, 999);
            final Date endOfMonthDate = endOfCurrentMonth.getTime();

            // Sheet 1: Vật tư đã nhập trong tháng
            Sheet sheetNhap = workbook.createSheet("Nhập trong tháng");
            Row headerRowNhap = sheetNhap.createRow(0);
            String[] headersNhap = {"Tên vật tư", "Số lượng", "Đơn vị tính", "Ngày nhập", "Hạn sử dụng"};
            for (int i = 0; i < headersNhap.length; i++) {
                Cell cell = headerRowNhap.createCell(i);
                cell.setCellValue(headersNhap[i]);
            }
            List<KhoVatTu> nhapTrongThang = controller.getAllKhoVatTu().stream()
                    .filter(vt -> {
                        try {
                            final Date ngayNhapDate = dateFormat.parse(vt.getNgayNhap());
                            return ngayNhapDate.after(startOfMonth.getTime()) && ngayNhapDate.before(endOfMonth.getTime());
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            int rowNumNhap = 1;
            for (KhoVatTu vt : nhapTrongThang) {
                Row row = sheetNhap.createRow(rowNumNhap++);
                row.createCell(0).setCellValue(vt.getTenVatTu());
                row.createCell(1).setCellValue(vt.getSoLuong());
                row.createCell(2).setCellValue(vt.getDonViTinh());
                row.createCell(3).setCellValue(vt.getNgayNhap());
                row.createCell(4).setCellValue(vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : "");
            }
            for (int i = 0; i < headersNhap.length; i++) {
                sheetNhap.autoSizeColumn(i);
            }

            // Sheet 2: Vật tư tồn từ tháng trước
            Sheet sheetTon = workbook.createSheet("Tồn tháng trước");
            Row headerRowTon = sheetTon.createRow(0);
            String[] headersTon = {"Tên vật tư", "Số lượng còn lại", "Đơn vị tính", "Hạn sử dụng"};
            for (int i = 0; i < headersTon.length; i++) {
                Cell cell = headerRowTon.createCell(i);
                cell.setCellValue(headersTon[i]);
            }
            List<KhoVatTu> tonThangTruoc = controller.getAllKhoVatTu().stream()
                    .filter(vt -> vt.getSoLuongConLai() > 0 && !nhapTrongThang.contains(vt))
                    .collect(Collectors.toList());
            int rowNumTon = 1;
            for (KhoVatTu vt : tonThangTruoc) {
                Row row = sheetTon.createRow(rowNumTon++);
                row.createCell(0).setCellValue(vt.getTenVatTu());
                row.createCell(1).setCellValue(vt.getSoLuongConLai());
                row.createCell(2).setCellValue(vt.getDonViTinh());
                row.createCell(3).setCellValue(vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : "");
            }
            for (int i = 0; i < headersTon.length; i++) {
                sheetTon.autoSizeColumn(i);
            }

            // Sheet 3: Vật tư đã hết hạn sử dụng
            Sheet sheetHetHan = workbook.createSheet("Hết hạn sử dụng");
            Row headerRowHetHan = sheetHetHan.createRow(0);
            String[] headersHetHan = {"Tên vật tư", "Số lượng còn lại", "Đơn vị tính", "Hạn sử dụng"};
            for (int i = 0; i < headersHetHan.length; i++) {
                Cell cell = headerRowHetHan.createCell(i);
                cell.setCellValue(headersHetHan[i]);
            }
            List<KhoVatTu> hetHan = controller.getAllKhoVatTu().stream()
                    .filter(vt -> vt.getHanSuDung() != null && vt.getHanSuDung().before(endOfMonthDate))
                    .collect(Collectors.toList());
            int rowNumHetHan = 1;
            for (KhoVatTu vt : hetHan) {
                Row row = sheetHetHan.createRow(rowNumHetHan++);
                row.createCell(0).setCellValue(vt.getTenVatTu());
                row.createCell(1).setCellValue(vt.getSoLuongConLai());
                row.createCell(2).setCellValue(vt.getDonViTinh());
                row.createCell(3).setCellValue(vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : "");
            }
            for (int i = 0; i < headersHetHan.length; i++) {
                sheetHetHan.autoSizeColumn(i);
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("ThongKeVatTu_" + monthYearFormat.format(currentCalendar.getTime()) + ".xlsx"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx");
            fileChooser.setFileFilter(filter);
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
            fileChooser.setSelectedFile(new File("ThongKeVatTu_" + monthYearFormat.format(currentCalendar.getTime()) + ".pdf"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
            fileChooser.setFileFilter(filter);
            int option = fileChooser.showSaveDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile()));
                document.open();

                com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Thống kê Kho vật tư - Tháng " + monthYearFormat.format(currentCalendar.getTime()));
                title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(title);
                document.add(new com.itextpdf.text.Paragraph(" "));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final Calendar startOfMonth = (Calendar) currentCalendar.clone();
                startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
                startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
                startOfMonth.set(Calendar.MINUTE, 0);
                startOfMonth.set(Calendar.SECOND, 0);
                startOfMonth.set(Calendar.MILLISECOND, 0);
                final Calendar endOfMonth = (Calendar) currentCalendar.clone();
                endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
                endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
                endOfMonth.set(Calendar.MINUTE, 59);
                endOfMonth.set(Calendar.SECOND, 59);
                endOfMonth.set(Calendar.MILLISECOND, 999);
                final Calendar endOfCurrentMonth = (Calendar) currentCalendar.clone();
                endOfCurrentMonth.set(Calendar.DAY_OF_MONTH, endOfCurrentMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
                endOfCurrentMonth.set(Calendar.HOUR_OF_DAY, 23);
                endOfCurrentMonth.set(Calendar.MINUTE, 59);
                endOfCurrentMonth.set(Calendar.SECOND, 59);
                endOfCurrentMonth.set(Calendar.MILLISECOND, 999);
                final Date endOfMonthDate = endOfCurrentMonth.getTime();

                // Phần 1: Vật tư đã nhập trong tháng
                document.add(new com.itextpdf.text.Paragraph("Vật tư đã nhập trong tháng:"));
                PdfPTable tableNhap = new PdfPTable(5);
                tableNhap.setWidthPercentage(100);
                tableNhap.setWidths(new float[]{2, 1, 1, 1, 1});
                String[] headersNhap = {"Tên vật tư", "Số lượng", "Đơn vị tính", "Ngày nhập", "Hạn sử dụng"};
                for (String header : headersNhap) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    tableNhap.addCell(cell);
                }
                List<KhoVatTu> nhapTrongThang = controller.getAllKhoVatTu().stream()
                        .filter(vt -> {
                            try {
                                final Date ngayNhapDate = dateFormat.parse(vt.getNgayNhap());
                                return ngayNhapDate.after(startOfMonth.getTime()) && ngayNhapDate.before(endOfMonth.getTime());
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
                for (KhoVatTu vt : nhapTrongThang) {
                    tableNhap.addCell(vt.getTenVatTu());
                    tableNhap.addCell(String.valueOf(vt.getSoLuong()));
                    tableNhap.addCell(vt.getDonViTinh());
                    tableNhap.addCell(vt.getNgayNhap());
                    tableNhap.addCell(vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : "");
                }
                document.add(tableNhap);
                document.add(new com.itextpdf.text.Paragraph(" "));

                // Phần 2: Vật tư tồn từ tháng trước
                document.add(new com.itextpdf.text.Paragraph("Vật tư tồn từ tháng trước:"));
                PdfPTable tableTon = new PdfPTable(4);
                tableTon.setWidthPercentage(100);
                tableTon.setWidths(new float[]{2, 1, 1, 1});
                String[] headersTon = {"Tên vật tư", "Số lượng còn lại", "Đơn vị tính", "Hạn sử dụng"};
                for (String header : headersTon) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    tableTon.addCell(cell);
                }
                List<KhoVatTu> tonThangTruoc = controller.getAllKhoVatTu().stream()
                        .filter(vt -> vt.getSoLuongConLai() > 0 && !nhapTrongThang.contains(vt))
                        .collect(Collectors.toList());
                for (KhoVatTu vt : tonThangTruoc) {
                    tableTon.addCell(vt.getTenVatTu());
                    tableTon.addCell(String.valueOf(vt.getSoLuongConLai()));
                    tableTon.addCell(vt.getDonViTinh());
                    tableTon.addCell(vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : "");
                }
                document.add(tableTon);
                document.add(new com.itextpdf.text.Paragraph(" "));

                // Phần 3: Vật tư đã hết hạn sử dụng
                document.add(new com.itextpdf.text.Paragraph("Vật tư đã hết hạn sử dụng:"));
                PdfPTable tableHetHan = new PdfPTable(4);
                tableHetHan.setWidthPercentage(100);
                tableHetHan.setWidths(new float[]{2, 1, 1, 1});
                String[] headersHetHan = {"Tên vật tư", "Số lượng còn lại", "Đơn vị tính", "Hạn sử dụng"};
                for (String header : headersHetHan) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    tableHetHan.addCell(cell);
                }
                List<KhoVatTu> hetHan = controller.getAllKhoVatTu().stream()
                        .filter(vt -> vt.getHanSuDung() != null && vt.getHanSuDung().before(endOfMonthDate))
                        .collect(Collectors.toList());
                for (KhoVatTu vt : hetHan) {
                    tableHetHan.addCell(vt.getTenVatTu());
                    tableHetHan.addCell(String.valueOf(vt.getSoLuongConLai()));
                    tableHetHan.addCell(vt.getDonViTinh());
                    tableHetHan.addCell(vt.getHanSuDung() != null ? sdf.format(vt.getHanSuDung()) : "");
                }
                document.add(tableHetHan);

                document.close();
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
            }
        } catch (DocumentException | java.io.IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reloadData() {
        allKhoVatTu = controller.getAllKhoVatTu();
        comboDonViTinh.removeAllItems();
        comboDonViTinh.addItem("Tất cả");
        allKhoVatTu.stream().map(KhoVatTu::getDonViTinh).distinct().sorted().forEach(comboDonViTinh::addItem);

        comboMaNCC.removeAllItems();
        comboMaNCC.addItem("Tất cả");
        allKhoVatTu.stream().map(KhoVatTu::getMaNCC).distinct().sorted().forEach(comboMaNCC::addItem);

        comboPhanLoai.removeAllItems();
        comboPhanLoai.addItem("Tất cả");
        allKhoVatTu.stream().map(KhoVatTu::getPhanLoai).distinct().sorted().forEach(comboPhanLoai::addItem);

        updateDaysInMonth();
        hienThiThongKeTheoThangNam(currentCalendar.get(Calendar.MONTH) + 1, currentCalendar.get(Calendar.YEAR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Thống kê Kho vật tư");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new ThongKeKhoVatTuPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}