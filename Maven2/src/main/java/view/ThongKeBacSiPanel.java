package view;

import controller.ThongKeBacSiController;
import controller.ThongKeController;
import model.BacSi;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Panel thống kê thông tin về bác sĩ
 */
public class ThongKeBacSiPanel extends JPanel {
    private JComboBox<String> cboLoaiThongKe;
    private JPanel pnlTieuChi;
    private JPanel pnlBieuDo;
    private JPanel pnlKetQua;
    private JTable tblKetQua;
    private DefaultTableModel modelKetQua;
    private JComboBox<String> cboChuyenKhoa;
    private JComboBox<String> cboPhongKham;
    private JComboBox<Integer> cboNam;
    private JComboBox<String> cboThang;
    private ThongKeBacSiController controller;
    private JPanel pnlBieuDoContainer;
    private JLabel lblTongSo;

    public ThongKeBacSiPanel() {
        controller = new ThongKeBacSiController(this);
        initComponents();
        setupEvents();
        loadInitialData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Loại thống kê
        JPanel pnlLoaiThongKe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLoaiThongKe.add(new JLabel("Loại thống kê:"));
        
        String[] loaiThongKe = {
            "Số lượng bác sĩ theo chuyên khoa", 
            "Bác sĩ có nhiều lịch hẹn nhất", 
            "Bác sĩ có nhiều ca điều trị nhất",
            "Phân bố bác sĩ theo phòng khám",
            "Thống kê bác sĩ theo kinh nghiệm"
        };
        
        cboLoaiThongKe = new JComboBox<>(loaiThongKe);
        pnlLoaiThongKe.add(cboLoaiThongKe);
        
        // Panel Tiêu chí
        pnlTieuChi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTieuChi.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Tiêu chí thống kê", 
                TitledBorder.LEFT, TitledBorder.TOP));
        
        // Thêm components cho Tiêu chí
        cboChuyenKhoa = new JComboBox<>();
        cboPhongKham = new JComboBox<>();
        cboNam = new JComboBox<>();
        cboThang = new JComboBox<>(new String[]{"Tất cả", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        
        // Thêm các năm gần đây
        int currentYear = java.time.Year.now().getValue();
        for (int i = currentYear; i >= currentYear - 5; i--) {
            cboNam.addItem(i);
        }
        
        JButton btnThongKe = new JButton("Thống kê");
        
        // Panel kết quả
        pnlKetQua = new JPanel(new BorderLayout(5, 5));
        pnlKetQua.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Kết quả thống kê", 
                TitledBorder.LEFT, TitledBorder.TOP));
        
        // Bảng kết quả
        modelKetQua = new DefaultTableModel();
        tblKetQua = new JTable(modelKetQua);
        JScrollPane scrollPane = new JScrollPane(tblKetQua);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        
        // Label hiển thị tổng số
        lblTongSo = new JLabel("Tổng số: 0");
        lblTongSo.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel pnlTongSo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTongSo.add(lblTongSo);
        
        pnlKetQua.add(scrollPane, BorderLayout.CENTER);
        pnlKetQua.add(pnlTongSo, BorderLayout.SOUTH);
        
        // Panel Biểu đồ
        pnlBieuDo = new JPanel(new BorderLayout());
        pnlBieuDo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Biểu đồ thống kê", 
                TitledBorder.LEFT, TitledBorder.TOP));
        
        pnlBieuDoContainer = new JPanel();
        pnlBieuDo.add(pnlBieuDoContainer, BorderLayout.CENTER);
        
        // Thêm tiêu chí mặc định
        updateTieuChi();
        
        // Thêm các panel vào panel chính
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlLoaiThongKe, BorderLayout.NORTH);
        pnlTop.add(pnlTieuChi, BorderLayout.CENTER);
        
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlCenter.add(pnlKetQua);
        pnlCenter.add(pnlBieuDo);
        
        add(pnlTop, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
        
        pnlTieuChi.add(btnThongKe);
    }
    
    private void setupEvents() {
        cboLoaiThongKe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTieuChi();
            }
        });
    }
    
    private void loadInitialData() {
        // Load danh sách chuyên khoa từ controller
        List<String> chuyenKhoaList = controller.getAllChuyenKhoa();
        cboChuyenKhoa.removeAllItems();
        cboChuyenKhoa.addItem("Tất cả");
        for (String chuyenKhoa : chuyenKhoaList) {
            cboChuyenKhoa.addItem(chuyenKhoa);
        }
        
        // Load danh sách phòng khám từ controller
        List<String> phongKhamList = controller.getAllPhongKham();
        cboPhongKham.removeAllItems();
        cboPhongKham.addItem("Tất cả");
        for (String phongKham : phongKhamList) {
            cboPhongKham.addItem(phongKham);
        }
        
        // Thực hiện thống kê mặc định
        thongKeBacSiTheoChuyenKhoa();
    }
    
    private void updateTieuChi() {
        pnlTieuChi.removeAll();
        
        String loaiThongKe = (String) cboLoaiThongKe.getSelectedItem();
        
        if (loaiThongKe.equals("Số lượng bác sĩ theo chuyên khoa")) {
            pnlTieuChi.add(new JLabel("Chuyên khoa:"));
            pnlTieuChi.add(cboChuyenKhoa);
            updateTableModelForChuyenKhoa();
        } else if (loaiThongKe.equals("Bác sĩ có nhiều lịch hẹn nhất")) {
            pnlTieuChi.add(new JLabel("Năm:"));
            pnlTieuChi.add(cboNam);
            pnlTieuChi.add(new JLabel("Tháng:"));
            pnlTieuChi.add(cboThang);
            updateTableModelForLichHen();
        } else if (loaiThongKe.equals("Bác sĩ có nhiều ca điều trị nhất")) {
            pnlTieuChi.add(new JLabel("Năm:"));
            pnlTieuChi.add(cboNam);
            pnlTieuChi.add(new JLabel("Tháng:"));
            pnlTieuChi.add(cboThang);
            updateTableModelForDieuTri();
        } else if (loaiThongKe.equals("Phân bố bác sĩ theo phòng khám")) {
            pnlTieuChi.add(new JLabel("Phòng khám:"));
            pnlTieuChi.add(cboPhongKham);
            updateTableModelForPhongKham();
        } else if (loaiThongKe.equals("Thống kê bác sĩ theo kinh nghiệm")) {
            pnlTieuChi.add(new JLabel("Chuyên khoa:"));
            pnlTieuChi.add(cboChuyenKhoa);
            updateTableModelForKinhNghiem();
        }
        
        JButton btnThongKe = new JButton("Thống kê");
        btnThongKe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thucHienThongKe();
            }
        });
        
        pnlTieuChi.add(btnThongKe);
        pnlTieuChi.revalidate();
        pnlTieuChi.repaint();
    }
    
    private void updateTableModelForChuyenKhoa() {
        modelKetQua.setColumnCount(0);
        modelKetQua.setRowCount(0);
        modelKetQua.addColumn("Chuyên khoa");
        modelKetQua.addColumn("Số lượng bác sĩ");
    }
    
    private void updateTableModelForLichHen() {
        modelKetQua.setColumnCount(0);
        modelKetQua.setRowCount(0);
        modelKetQua.addColumn("Mã bác sĩ");
        modelKetQua.addColumn("Họ tên bác sĩ");
        modelKetQua.addColumn("Chuyên khoa");
        modelKetQua.addColumn("Phòng khám");
        modelKetQua.addColumn("Số lịch hẹn");
    }
    
    private void updateTableModelForDieuTri() {
        modelKetQua.setColumnCount(0);
        modelKetQua.setRowCount(0);
        modelKetQua.addColumn("Mã bác sĩ");
        modelKetQua.addColumn("Họ tên bác sĩ");
        modelKetQua.addColumn("Chuyên khoa");
        modelKetQua.addColumn("Phòng khám");
        modelKetQua.addColumn("Số ca điều trị");
    }
    
    private void updateTableModelForPhongKham() {
        modelKetQua.setColumnCount(0);
        modelKetQua.setRowCount(0);
        modelKetQua.addColumn("Phòng khám");
        modelKetQua.addColumn("Số lượng bác sĩ");
    }
    
    private void updateTableModelForKinhNghiem() {
        modelKetQua.setColumnCount(0);
        modelKetQua.setRowCount(0);
        modelKetQua.addColumn("Khoảng kinh nghiệm");
        modelKetQua.addColumn("Số lượng bác sĩ");
    }
    
    private void thucHienThongKe() {
        String loaiThongKe = (String) cboLoaiThongKe.getSelectedItem();
        
        if (loaiThongKe.equals("Số lượng bác sĩ theo chuyên khoa")) {
            thongKeBacSiTheoChuyenKhoa();
        } else if (loaiThongKe.equals("Bác sĩ có nhiều lịch hẹn nhất")) {
            thongKeBacSiCoNhieuLichHen();
        } else if (loaiThongKe.equals("Bác sĩ có nhiều ca điều trị nhất")) {
            thongKeBacSiCoNhieuCaDieuTri();
        } else if (loaiThongKe.equals("Phân bố bác sĩ theo phòng khám")) {
            thongKeBacSiTheoPhongKham();
        } else if (loaiThongKe.equals("Thống kê bác sĩ theo kinh nghiệm")) {
            thongKeBacSiTheoKinhNghiem();
        }
    }
    
    private void thongKeBacSiTheoChuyenKhoa() {
        String chuyenKhoa = (String) cboChuyenKhoa.getSelectedItem();
        Map<String, Integer> data = controller.thongKeBacSiTheoChuyenKhoa(chuyenKhoa);
        
        modelKetQua.setRowCount(0);
        int tongSo = 0;
        
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            modelKetQua.addRow(new Object[]{entry.getKey(), entry.getValue()});
            tongSo += entry.getValue();
        }
        
        lblTongSo.setText("Tổng số: " + tongSo + " bác sĩ");
        
        // Hiển thị biểu đồ
        hienThiBieuDoCot(data, "Thống kê số lượng bác sĩ theo chuyên khoa");
    }
    
    private void thongKeBacSiCoNhieuLichHen() {
        int nam = (int) cboNam.getSelectedItem();
        String thangStr = (String) cboThang.getSelectedItem();
        int thang = thangStr.equals("Tất cả") ? 0 : Integer.parseInt(thangStr);
        
        List<Object[]> data = controller.thongKeBacSiCoNhieuLichHen(nam, thang);
        
        modelKetQua.setRowCount(0);
        int tongSo = 0;
        
        for (Object[] row : data) {
            modelKetQua.addRow(row);
            tongSo += (int) row[4]; // Cộng số lịch hẹn
        }
        
        lblTongSo.setText("Tổng số: " + tongSo + " lịch hẹn");
        
        // Hiển thị biểu đồ
        hienThiBieuDoTop10(data, "Top bác sĩ có nhiều lịch hẹn nhất", 1, 4);
    }
    
    private void thongKeBacSiCoNhieuCaDieuTri() {
        int nam = (int) cboNam.getSelectedItem();
        String thangStr = (String) cboThang.getSelectedItem();
        int thang = thangStr.equals("Tất cả") ? 0 : Integer.parseInt(thangStr);
        
        List<Object[]> data = controller.thongKeBacSiCoNhieuCaDieuTri(nam, thang);
        
        modelKetQua.setRowCount(0);
        int tongSo = 0;
        
        for (Object[] row : data) {
            modelKetQua.addRow(row);
            tongSo += (int) row[4]; // Cộng số ca điều trị
        }
        
        lblTongSo.setText("Tổng số: " + tongSo + " ca điều trị");
        
        // Hiển thị biểu đồ
        hienThiBieuDoTop10(data, "Top bác sĩ có nhiều ca điều trị nhất", 1, 4);
    }
    
    private void thongKeBacSiTheoPhongKham() {
        String phongKham = (String) cboPhongKham.getSelectedItem();
        Map<String, Integer> data = controller.thongKeBacSiTheoPhongKham(phongKham);
        
        modelKetQua.setRowCount(0);
        int tongSo = 0;
        
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            modelKetQua.addRow(new Object[]{entry.getKey(), entry.getValue()});
            tongSo += entry.getValue();
        }
        
        lblTongSo.setText("Tổng số: " + tongSo + " bác sĩ");
        
        // Hiển thị biểu đồ
        hienThiBieuDoCot(data, "Thống kê số lượng bác sĩ theo phòng khám");
    }
    
    private void thongKeBacSiTheoKinhNghiem() {
        String chuyenKhoa = (String) cboChuyenKhoa.getSelectedItem();
        Map<String, Integer> data = controller.thongKeBacSiTheoKinhNghiem(chuyenKhoa);
        
        modelKetQua.setRowCount(0);
        int tongSo = 0;
        
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            modelKetQua.addRow(new Object[]{entry.getKey(), entry.getValue()});
            tongSo += entry.getValue();
        }
        
        lblTongSo.setText("Tổng số: " + tongSo + " bác sĩ");
        
        // Hiển thị biểu đồ
        hienThiBieuDoCot(data, "Thống kê số lượng bác sĩ theo kinh nghiệm");
    }
    
    private int calculateScale(int maxValue) {
        if (maxValue <= 10) return 1;
        if (maxValue <= 20) return 2;
        if (maxValue <= 50) return 5;
        if (maxValue <= 100) return 10;
        if (maxValue <= 200) return 20;
        if (maxValue <= 500) return 50;
        if (maxValue <= 1000) return 100;
        
        int scale = 1;
        int tempValue = maxValue;
        
        while (tempValue > 10) {
            tempValue /= 10;
            scale *= 10;
        }
        
        if (tempValue <= 2) return scale / 5;
        if (tempValue <= 5) return scale / 2;
        return scale;
    }

    private void hienThiBieuDoCot(Map<String, Integer> data, String title) {
        pnlBieuDoContainer.removeAll();
        
        if (data == null || data.isEmpty()) {
            JLabel lblNoData = new JLabel("Không có dữ liệu để hiển thị biểu đồ", JLabel.CENTER);
            lblNoData.setFont(new Font("Arial", Font.BOLD, 14));
            pnlBieuDoContainer.setLayout(new BorderLayout());
            pnlBieuDoContainer.add(lblNoData, BorderLayout.CENTER);
            pnlBieuDoContainer.revalidate();
            pnlBieuDoContainer.repaint();
            return;
        }
        
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                int marginTop = 40;
                int marginBottom = 70; // Tăng marginBottom để có thêm không gian cho nhãn
                int marginLeft = 50;
                int marginRight = 20;
                
                int chartWidth = width - marginLeft - marginRight;
                int chartHeight = height - marginTop - marginBottom;
                
                int numBars = data.size();
                int barWidth = Math.min(50, (chartWidth) / (numBars * 2));
                int spacing = barWidth;
                
                int maxValue = 1;
                for (Integer value : data.values()) {
                    maxValue = Math.max(maxValue, value);
                }
                
                int yAxisScale = calculateScale(maxValue);
                int numYDivisions = maxValue / yAxisScale + (maxValue % yAxisScale > 0 ? 1 : 0);
                
                g2d.setColor(Color.BLACK);
                g2d.drawLine(marginLeft, marginTop, marginLeft, height - marginBottom);
                
                for (int i = 0; i <= numYDivisions; i++) {
                    int y = height - marginBottom - (i * yAxisScale * chartHeight / (numYDivisions * yAxisScale));
                    if (y >= marginTop) {
                        g2d.setColor(Color.LIGHT_GRAY);
                        g2d.drawLine(marginLeft, y, width - marginRight, y);
                        
                        g2d.setColor(Color.BLACK);
                        String yLabel = String.valueOf(i * yAxisScale);
                        FontMetrics fm = g2d.getFontMetrics();
                        int labelWidth = fm.stringWidth(yLabel);
                        g2d.drawString(yLabel, marginLeft - labelWidth - 5, y + fm.getAscent() / 2);
                    }
                }
                
                g2d.setColor(Color.BLACK);
                g2d.drawLine(marginLeft, height - marginBottom, width - marginRight, height - marginBottom);
                
                int x = marginLeft + spacing;
                int i = 0;
                
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    int value = entry.getValue();
                    
                    double ratio = (double) chartHeight / (numYDivisions * yAxisScale);
                    int barHeight = (int) (value * ratio);
                    
                    g2d.setColor(new Color(41, 128, 185));
                    int barX = x + i * (barWidth + spacing);
                    int barY = height - marginBottom - barHeight;
                    
                    g2d.fillRect(barX, barY, barWidth, barHeight);
                    
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(barX, barY, barWidth, barHeight);
                    
                    String valueText = String.valueOf(value);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(valueText);
                    int textX = barX + (barWidth - textWidth) / 2;
                    int textY = barY - 5;
                    
                    if (textY < marginTop) {
                        textY = barY + 15;
                        g2d.setColor(Color.WHITE);
                    } else {
                        g2d.setColor(Color.BLACK);
                    }
                    
                    g2d.drawString(valueText, textX, textY);
                    
                    // Xử lý nhãn với khả năng xuống dòng
                    String label = entry.getKey();
                    drawMultilineLabel(g2d, label, barX + barWidth / 2, height - marginBottom + 15, barWidth + spacing);
                    
                    i++;
                }
                
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
            }
        };
        
        chart.setPreferredSize(new Dimension(500, 300));
        pnlBieuDoContainer.setLayout(new BorderLayout());
        pnlBieuDoContainer.add(chart, BorderLayout.CENTER);
        
        pnlBieuDoContainer.revalidate();
        pnlBieuDoContainer.repaint();
    }

    private void hienThiBieuDoTop10(List<Object[]> data, String title, int labelIndex, int valueIndex) {
        pnlBieuDoContainer.removeAll();
        
        if (data == null || data.isEmpty()) {
            JLabel lblNoData = new JLabel("Không có dữ liệu để hiển thị biểu đồ", JLabel.CENTER);
            lblNoData.setFont(new Font("Arial", Font.BOLD, 14));
            pnlBieuDoContainer.setLayout(new BorderLayout());
            pnlBieuDoContainer.add(lblNoData, BorderLayout.CENTER);
            pnlBieuDoContainer.revalidate();
            pnlBieuDoContainer.repaint();
            return;
        }
        
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                int marginTop = 40;
                int marginBottom = 70; // Tăng marginBottom để có thêm không gian cho nhãn
                int marginLeft = 50;
                int marginRight = 20;
                
                int chartWidth = width - marginLeft - marginRight;
                int chartHeight = height - marginTop - marginBottom;
                
                int maxBars = Math.min(data.size(), 10);
                
                int barWidth = Math.min(50, chartWidth / (maxBars * 2));
                int spacing = barWidth;
                
                int maxValue = 1;
                for (int i = 0; i < maxBars; i++) {
                    Object[] row = data.get(i);
                    if (row[valueIndex] instanceof Integer) {
                        maxValue = Math.max(maxValue, (int) row[valueIndex]);
                    }
                }
                
                int yAxisScale = calculateScale(maxValue);
                int numYDivisions = maxValue / yAxisScale + (maxValue % yAxisScale > 0 ? 1 : 0);
                
                g2d.setColor(Color.BLACK);
                g2d.drawLine(marginLeft, marginTop, marginLeft, height - marginBottom);
                
                for (int i = 0; i <= numYDivisions; i++) {
                    int y = height - marginBottom - (i * yAxisScale * chartHeight / (numYDivisions * yAxisScale));
                    if (y >= marginTop) {
                        g2d.setColor(Color.LIGHT_GRAY);
                        g2d.drawLine(marginLeft, y, width - marginRight, y);
                        
                        g2d.setColor(Color.BLACK);
                        String yLabel = String.valueOf(i * yAxisScale);
                        FontMetrics fm = g2d.getFontMetrics();
                        int labelWidth = fm.stringWidth(yLabel);
                        g2d.drawString(yLabel, marginLeft - labelWidth - 5, y + fm.getAscent() / 2);
                    }
                }
                
                g2d.setColor(Color.BLACK);
                g2d.drawLine(marginLeft, height - marginBottom, width - marginRight, height - marginBottom);
                
                for (int i = 0; i < maxBars; i++) {
                    Object[] row = data.get(i);
                    String label = row[labelIndex].toString();
                    int value = row[valueIndex] instanceof Integer ? (int) row[valueIndex] : 0;
                    
                    double ratio = (double) chartHeight / (numYDivisions * yAxisScale);
                    int barHeight = (int) (value * ratio);
                    
                    g2d.setColor(new Color(41, 128, 185));
                    int barX = marginLeft + spacing + i * (barWidth + spacing);
                    int barY = height - marginBottom - barHeight;
                    
                    g2d.fillRect(barX, barY, barWidth, barHeight);
                    
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(barX, barY, barWidth, barHeight);
                    
                    String valueText = String.valueOf(value);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(valueText);
                    int textX = barX + (barWidth - textWidth) / 2;
                    int textY = barY - 5;
                    
                    if (textY < marginTop) {
                        textY = barY + 15;
                        g2d.setColor(Color.WHITE);
                    } else {
                        g2d.setColor(Color.BLACK);
                    }
                    
                    g2d.drawString(valueText, textX, textY);
                    
                    // Xử lý nhãn với khả năng xuống dòng
                    drawMultilineLabel(g2d, label, barX + barWidth / 2, height - marginBottom + 15, barWidth + spacing);
                }
                
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
            }
        };
        
        chart.setPreferredSize(new Dimension(500, 300));
        pnlBieuDoContainer.setLayout(new BorderLayout());
        pnlBieuDoContainer.add(chart, BorderLayout.CENTER);
        
        pnlBieuDoContainer.revalidate();
        pnlBieuDoContainer.repaint();
    }
    private void drawMultilineLabel(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        
        if (fm.stringWidth(text) <= maxWidth) {
            // Nếu văn bản ngắn, vẽ bình thường ở giữa
            g2d.drawString(text, x - fm.stringWidth(text) / 2, y);
            return;
        }
        
        // Chia văn bản thành nhiều dòng
        java.util.List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (fm.stringWidth(currentLine + " " + word) <= maxWidth) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    if (fm.stringWidth(word) > maxWidth) {
                        int endIndex = 0;
                        while (endIndex < word.length()) {
                            int startIndex = endIndex;
                            while (endIndex < word.length() && 
                                   fm.stringWidth(word.substring(startIndex, endIndex + 1)) <= maxWidth) {
                                endIndex++;
                            }
                            if (startIndex == endIndex) endIndex++;
                            lines.add(word.substring(startIndex, endIndex));
                        }
                    } else {
                        lines.add(word);
                    }
                }
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }      
        int lineHeight = fm.getHeight();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineWidth = fm.stringWidth(line);
            g2d.drawString(line, x - lineWidth / 2, y + i * lineHeight);
        }
    }
    public void hienThiKetQua(Vector<Vector<Object>> data, Vector<String> columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        tblKetQua.setModel(model);
        tblKetQua.repaint();
    }
}