package view;

import controller.ThongKeController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class ThongKeDoanhThuPanel extends JPanel {

    private ThongKeController thongKeController;
    private JTable tableThongKe;
    private DefaultTableModel tableModelThongKe;
    private JComboBox<String> cmbLoaiThongKe;
    private JPanel panelTuyChon;
    private JButton btnThongKe;
    private JButton btnXemChiTiet; // Nút Xem chi tiết
    private JButton btnBoLoc; // Nút Bộ lọc mới
    private JPopupMenu popupBoLoc; // Popup menu cho bộ lọc

    // Các component cho bộ lọc theo tuần
    private JButton btnTuanTruoc;
    private JButton btnTuanSau;
    private JLabel lblTuanHienTai;
    private Date ngayDauTuanHienTai;

    // Các component cho bộ lọc theo tháng
    private JComboBox<Integer> cmbThang;
    private JComboBox<Integer> cmbNamThang;
    private JButton btnXemChiTietThang; // Nút Xem chi tiết cho tháng

    // Các component cho bộ lọc theo năm
    private JComboBox<Integer> cmbNamNam;
    private JButton btnXemChiTietNam; // Nút Xem chi tiết cho năm

    // Các component cho bộ lọc theo ngày
    private JComboBox<Integer> cmbNgayNgay;
    private JComboBox<Integer> cmbThangNgay;
    private JComboBox<Integer> cmbNamNgay;

    // Các component cho bộ lọc tùy chọn (Tuần, Tháng, Năm) trong Popup
    private JComboBox<Integer> cmbTuanTuyChonPopup;
    private JComboBox<Integer> cmbThangTuyChonPopup;
    private JComboBox<Integer> cmbNamTuyChonPopup;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font controlFont = new Font("Segoe UI", Font.PLAIN, 14);

    public ThongKeDoanhThuPanel() {
        thongKeController = new ThongKeController(this);
        ngayDauTuanHienTai = getNgayDauTuanHienTai();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel chọn loại thống kê (phía trên bên trái)
        JPanel chonLoaiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblChonLoai = new JLabel("Xem thống kê theo:");
        lblChonLoai.setFont(labelFont);
        cmbLoaiThongKe = new JComboBox<>(new String[]{"Ngày", "Tuần (Tổng)"});
        cmbLoaiThongKe.setFont(controlFont);
        chonLoaiPanel.add(lblChonLoai);
        chonLoaiPanel.add(cmbLoaiThongKe);
        add(chonLoaiPanel, BorderLayout.NORTH);

        // Panel tùy chọn (ở giữa phía trên)
        panelTuyChon = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Sử dụng FlowLayout cho nút Bộ lọc
        btnBoLoc = new JButton("Bộ lọc");
        btnBoLoc.setFont(controlFont);
        panelTuyChon.add(btnBoLoc);
        add(panelTuyChon, BorderLayout.PAGE_START);

        // Tạo PopupMenu cho bộ lọc
        popupBoLoc = new JPopupMenu();
        JMenuItem menuItemTuan = new JMenuItem("Tuần");
        JMenuItem menuItemThang = new JMenuItem("Tháng");
        JMenuItem menuItemNam = new JMenuItem("Năm");

        popupBoLoc.add(menuItemTuan);
        popupBoLoc.add(menuItemThang);
        popupBoLoc.add(menuItemNam);

        // Table hiển thị thống kê (ở giữa)
        tableModelThongKe = new DefaultTableModel(new String[]{"Thời gian", "Tổng Doanh Thu"}, 0); // Cột "Thời gian" chung
        tableThongKe = new JTable(tableModelThongKe);
        tableThongKe.setFont(controlFont);
        JScrollPane scrollPane = new JScrollPane(tableThongKe);
        add(scrollPane, BorderLayout.CENTER); // Đặt JScrollPane ở CENTER

        // Panel nút chức năng (phía dưới)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnThongKe = new JButton("Thống kê");
        btnThongKe.setFont(controlFont);
        btnXemChiTiet = new JButton("Xem chi tiết");
        btnXemChiTiet.setFont(controlFont);
        btnXemChiTiet.setEnabled(false); // Ban đầu vô hiệu hóa nút Xem chi tiết (nút chung)
        buttonPanel.add(btnThongKe);
        // Không thêm btnXemChiTiet chung vào buttonPanel nữa
        add(buttonPanel, BorderLayout.SOUTH); // Đặt nút ở SOUTH

        // Hiển thị bộ lọc tuần mặc định
        hienThiBoLocTuan();
        loadDataForCurrentWeek(); // Tải dữ liệu ban đầu cho tuần (chế độ "Ngày")

        // Action Listener cho combobox loại thống kê
        cmbLoaiThongKe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String loaiThongKe = (String) cmbLoaiThongKe.getSelectedItem();
                panelTuyChon.removeAll();
                panelTuyChon.add(btnBoLoc); // Luôn hiển thị nút Bộ lọc
                btnXemChiTiet.setEnabled(false); // Reset trạng thái nút Xem chi tiết chung
                switch (loaiThongKe) {
                    case "Ngày":
                        hienThiBoLocTuan(); // Dùng bộ lọc tuần cho "Ngày"
                        loadDataForCurrentWeek();
                        break;
                    case "Tuần (Tổng)":
                        hienThiBoLocTuanChiTiet(); // Hiển thị bộ lọc tuần
                        break;
                }
                panelTuyChon.revalidate();
                panelTuyChon.repaint();
            }
        });

        // Action Listener cho nút Thống kê
        btnThongKe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String loaiThongKe = (String) cmbLoaiThongKe.getSelectedItem();
                // Không cần kích hoạt btnXemChiTiet chung ở đây nữa, nút chi tiết riêng đã được thêm vào bộ lọc
                switch (loaiThongKe) {
                    case "Ngày":
                        loadDataForCurrentWeek(); // Hiển thị từng ngày trong tuần
                        break;
                    case "Tuần (Tổng)":
                        thongKeTheoTuanTong();
                        break;
                    // Các trường hợp lọc theo tuần, tháng, năm sẽ được xử lý khi chọn từ popup
                }
            }
        });

        // Action Listener cho nút Bộ lọc
        btnBoLoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupBoLoc.show(btnBoLoc, 0, btnBoLoc.getHeight());
            }
        });

        // Action Listener cho các mục trong PopupMenu
        menuItemTuan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hienThiBoLocTuanTuyChonPopup();
            }
        });

        menuItemThang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hienThiBoLocThangTuyChonPopup();
            }
        });

        menuItemNam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hienThiBoLocNamTuyChonPopup();
            }
        });

        // Action Listener cho nút Xem chi tiết chung (có thể không cần thiết nữa)
        btnXemChiTiet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logic cho nút Xem chi tiết chung (nếu bạn vẫn muốn giữ)
            }
        });
    }

    private GridBagConstraints getConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.insets = new Insets(5, 5, 5, 5); // Thêm insets cho các component
        return gbc;
    }

    private Date getNgayDauTuanHienTai() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getNgayCuoiTuan(Date ngayDauTuan) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ngayDauTuan);
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        return calendar.getTime();
    }

    private String getFormattedDate(Date date) {
        return dateFormat.format(date);
    }

    private void loadDataForCurrentWeek() {
        Date ngayCuoiTuan = getNgayCuoiTuan(ngayDauTuanHienTai);
        thongKeController.thongKeDoanhThuTheoNgay(new java.sql.Date(ngayDauTuanHienTai.getTime()), new java.sql.Date(ngayCuoiTuan.getTime()));
        updateTuanHienTaiLabel();
    }

    private void updateTuanHienTaiLabel() {
        if (lblTuanHienTai != null) {
            lblTuanHienTai.setText("Tuần: " + getFormattedDate(ngayDauTuanHienTai) + " - " + getFormattedDate(getNgayCuoiTuan(ngayDauTuanHienTai)));
        }
    }

    private void changeWeek(int direction) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ngayDauTuanHienTai);
        calendar.add(Calendar.WEEK_OF_YEAR, direction);
        ngayDauTuanHienTai = calendar.getTime();
        loadDataForCurrentWeek();
    }

    private void hienThiBoLocTuan() {
        panelTuyChon.removeAll();
        panelTuyChon.add(btnBoLoc); // Luôn hiển thị nút Bộ lọc
        panelTuyChon.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        btnTuanTruoc = new JButton("<< Tuần trước");
        btnTuanTruoc.setFont(controlFont);
        lblTuanHienTai = new JLabel("Tuần: " + getFormattedDate(ngayDauTuanHienTai) + " - " + getFormattedDate(getNgayCuoiTuan(ngayDauTuanHienTai)));
        lblTuanHienTai.setFont(labelFont);
        btnTuanSau = new JButton("Tuần sau >>");
        btnTuanSau.setFont(controlFont);

        btnTuanTruoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeWeek(-1);
            }
        });

        btnTuanSau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeWeek(1);
            }
        });

        panelTuyChon.add(btnTuanTruoc);
        panelTuyChon.add(lblTuanHienTai);
        panelTuyChon.add(btnTuanSau);
        panelTuyChon.revalidate();
        panelTuyChon.repaint();
    }

    private void hienThiBoLocTuanChiTiet() {
        hienThiBoLocTuan();
        // Không cần thêm nút "Xem chi tiết" riêng cho tuần vì đã có logic thống kê tổng
    }

    private void hienThiBoLocTuanTuyChonPopup() {
        panelTuyChon.removeAll();
        panelTuyChon.add(btnBoLoc); // Luôn hiển thị nút Bộ lọc
        panelTuyChon.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel lblTuan = new JLabel("Tuần:");
        lblTuan.setFont(labelFont);
        cmbTuanTuyChonPopup = new JComboBox<>();
        cmbTuanTuyChonPopup.setFont(controlFont);
        Calendar cal = Calendar.getInstance();
        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        for (int i = 1; i <= 53; i++) { // Số tuần tối đa trong năm
            cmbTuanTuyChonPopup.addItem(i);
        }
        cmbTuanTuyChonPopup.setSelectedItem(currentWeek);

        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(labelFont);
        cmbNamTuyChonPopup = new JComboBox<>();
        cmbNamTuyChonPopup.setFont(controlFont);
        int currentYear = cal.get(Calendar.YEAR);
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            cmbNamTuyChonPopup.addItem(i);
        }
        cmbNamTuyChonPopup.setSelectedItem(currentYear);

        JButton btnThongKeTuyChon = new JButton("Thống kê");
        btnThongKeTuyChon.setFont(controlFont);
        btnThongKeTuyChon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thongKeTheoTuanTuyChonPopup();
            }
        });

        panelTuyChon.add(lblTuan);
        panelTuyChon.add(cmbTuanTuyChonPopup);
        panelTuyChon.add(lblNam);
        panelTuyChon.add(cmbNamTuyChonPopup);
        panelTuyChon.add(btnThongKeTuyChon);

        panelTuyChon.revalidate();
        panelTuyChon.repaint();
    }

    private void hienThiBoLocThangTuyChonPopup() {
        panelTuyChon.removeAll();
        panelTuyChon.add(btnBoLoc); // Luôn hiển thị nút Bộ lọc
        panelTuyChon.setLayout(new GridBagLayout());

        JLabel lblThang = new JLabel("Tháng:");
        lblThang.setFont(labelFont);
        cmbThangTuyChonPopup = new JComboBox<>();
        cmbThangTuyChonPopup.setFont(controlFont);
        for (int i = 1; i <= 12; i++) {
            cmbThangTuyChonPopup.addItem(i);
        }
        cmbThangTuyChonPopup.setSelectedItem(Calendar.getInstance().get(Calendar.MONTH) + 1);

        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(labelFont);
        cmbNamTuyChonPopup = new JComboBox<>();
        cmbNamTuyChonPopup.setFont(controlFont);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            cmbNamTuyChonPopup.addItem(i);
        }
        cmbNamTuyChonPopup.setSelectedItem(currentYear);

        JButton btnThongKeTuyChon = new JButton("Thống kê");
        btnThongKeTuyChon.setFont(controlFont);
        btnThongKeTuyChon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thongKeTheoThangTuyChonPopup();
            }
        });

        panelTuyChon.add(lblThang, getConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));
        panelTuyChon.add(cmbThangTuyChonPopup, getConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
        panelTuyChon.add(lblNam, getConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));
        panelTuyChon.add(cmbNamTuyChonPopup, getConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
        panelTuyChon.add(btnThongKeTuyChon, getConstraints(2, 0, 1, 2, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE)); // Span 2 rows
        panelTuyChon.revalidate();
        panelTuyChon.repaint();

        // Gán lại cmbThang và cmbNamThang để sử dụng trong thống kê chi tiết (nếu cần)
        cmbThang = cmbThangTuyChonPopup;
        cmbNamThang = cmbNamTuyChonPopup;
    }

    private void hienThiBoLocThang() {
        panelTuyChon.removeAll();
        panelTuyChon.add(btnBoLoc); // Luôn hiển thị nút Bộ lọc
        panelTuyChon.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel lblThang = new JLabel("Tháng:");
        lblThang.setFont(labelFont);
        cmbThang = new JComboBox<>();
        cmbThang.setFont(controlFont);
        for (int i = 1; i <= 12; i++) {
            cmbThang.addItem(i);
        }
        cmbThang.setSelectedItem(Calendar.getInstance().get(Calendar.MONTH) + 1);

        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(labelFont);
        cmbNamThang = new JComboBox<>();
        cmbNamThang.setFont(controlFont);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            cmbNamThang.addItem(i);
        }
        cmbNamThang.setSelectedItem(currentYear);

        JButton btnXemChiTietThang = new JButton("Xem chi tiết");
        btnXemChiTietThang.setFont(controlFont);
        btnXemChiTietThang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hienThiChiTietThang();
            }
        });

        panelTuyChon.add(lblThang);
        panelTuyChon.add(cmbThang);
        panelTuyChon.add(lblNam);
        panelTuyChon.add(cmbNamThang);
        panelTuyChon.add(btnThongKe); // Thêm lại nút Thống kê
        panelTuyChon.add(btnXemChiTietThang);

        panelTuyChon.revalidate();
        panelTuyChon.repaint();
    }

    private void hienThiBoLocNamTuyChonPopup() {
        panelTuyChon.removeAll();
        panelTuyChon.add(btnBoLoc); // Luôn hiển thị nút Bộ lọc
        panelTuyChon.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(labelFont);
        cmbNamTuyChonPopup = new JComboBox<>();
        cmbNamTuyChonPopup.setFont(controlFont);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 10; i <= currentYear + 10; i++) {
            cmbNamTuyChonPopup.addItem(i);
        }
        cmbNamTuyChonPopup.setSelectedItem(currentYear);

        JButton btnThongKeTuyChon = new JButton("Thống kê");
        btnThongKeTuyChon.setFont(controlFont);
        btnThongKeTuyChon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thongKeTheoNamTuyChonPopup();
            }
        });

        panelTuyChon.add(lblNam);
        panelTuyChon.add(cmbNamTuyChonPopup);
        panelTuyChon.add(btnThongKeTuyChon);

        panelTuyChon.revalidate();
        panelTuyChon.repaint();

        // Gán lại cmbNamNam để sử dụng trong thống kê chi tiết (nếu cần)
        cmbNamNam = cmbNamTuyChonPopup;
    }

    private void hienThiBoLocNam() {
        panelTuyChon.removeAll();
        panelTuyChon.add(btnBoLoc); // Luôn hiển thị nút Bộ lọc
        panelTuyChon.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(labelFont);
        cmbNamNam = new JComboBox<>();
        cmbNamNam.setFont(controlFont);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 10; i <= currentYear + 10; i++) {
            cmbNamNam.addItem(i);
        }
        cmbNamNam.setSelectedItem(currentYear);

        JButton btnXemChiTietNam = new JButton("Xem chi tiết");
        btnXemChiTietNam.setFont(controlFont);
        btnXemChiTietNam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hienThiChiTietNam();
            }
        });

        panelTuyChon.add(lblNam);
        panelTuyChon.add(cmbNamNam);
        panelTuyChon.add(btnThongKe); // Thêm lại nút Thống kê
        panelTuyChon.add(btnXemChiTietNam);

        panelTuyChon.revalidate();
        panelTuyChon.repaint();
    }

    private void thongKeTheoNgayTuyChon() {
        if (cmbNgayNgay != null && cmbThangNgay != null && cmbNamNgay != null) {
            int ngay = (int) cmbNgayNgay.getSelectedItem();
            int thang = (int) cmbThangNgay.getSelectedItem();
            int nam = (int) cmbNamNgay.getSelectedItem();
            Calendar calendar = Calendar.getInstance();
            calendar.set(nam, thang - 1, ngay);
            java.sql.Date selectedDate = new java.sql.Date(calendar.getTimeInMillis());
            thongKeController.thongKeDoanhThuTheoNgay(selectedDate, selectedDate);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày, tháng và năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thongKeTheoTuanTuyChonPopup() {
        if (cmbTuanTuyChonPopup != null && cmbNamTuyChonPopup != null) {
            int tuan = (int) cmbTuanTuyChonPopup.getSelectedItem();
            int nam = (int) cmbNamTuyChonPopup.getSelectedItem();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, nam);
            calendar.set(Calendar.WEEK_OF_YEAR, tuan);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date ngayDauTuan = calendar.getTime();
            calendar.add(Calendar.DAY_OF_WEEK, 6);
            Date ngayCuoiTuan = calendar.getTime();
            thongKeController.thongKeDoanhThuTheoNgay(new java.sql.Date(ngayDauTuan.getTime()), new java.sql.Date(ngayCuoiTuan.getTime()));
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tuần và năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thongKeTheoThangTuyChonPopup() {
        if (cmbThangTuyChonPopup != null && cmbNamTuyChonPopup != null) {
            int thang = (int) cmbThangTuyChonPopup.getSelectedItem();
            int nam = (int) cmbNamTuyChonPopup.getSelectedItem();
            thongKeController.thongKeDoanhThuTheoThang(nam, thang);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng và năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thongKeTheoNamTuyChonPopup() {
        if (cmbNamTuyChonPopup != null) {
            int nam = (int) cmbNamTuyChonPopup.getSelectedItem();
            thongKeController.thongKeDoanhThuTheoNam(nam);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thongKeTheoThang() {
        if (cmbThang != null && cmbNamThang != null) {
            int thang = (int) cmbThang.getSelectedItem();
            int nam = (int) cmbNamThang.getSelectedItem();
            thongKeController.thongKeDoanhThuTheoThang(nam, thang);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng và năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thongKeTheoNam() {
        if (cmbNamNam != null) {
            int nam = (int) cmbNamNam.getSelectedItem();
            thongKeController.thongKeDoanhThuTheoNam(nam);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thongKeTheoTuanTong() {
        Date ngayCuoiTuan = getNgayCuoiTuan(ngayDauTuanHienTai);
        thongKeController.thongKeDoanhThuTheoTuanTong(new java.sql.Date(ngayDauTuanHienTai.getTime()), new java.sql.Date(ngayCuoiTuan.getTime()));
    }

    public void hienThiThongKe(java.util.List<Object[]> data) {
        tableModelThongKe.setRowCount(0);
        for (Object[] rowData : data) {
            tableModelThongKe.addRow(rowData);
        }
    }

    private void hienThiChiTietTuan() {
        Date ngayDauTuan = ngayDauTuanHienTai;
        Date ngayCuoiTuan = getNgayCuoiTuan(ngayDauTuan);
        thongKeController.thongKeDoanhThuTheoNgay(new java.sql.Date(ngayDauTuan.getTime()), new java.sql.Date(ngayCuoiTuan.getTime()));
        hienThiTieuDeChiTiet("Chi tiết doanh thu tuần: " + getFormattedDate(ngayDauTuan) + " - " + getFormattedDate(ngayCuoiTuan));
    }

    private void hienThiChiTietThang() {
        if (cmbThang != null && cmbNamThang != null) {
            int thang = (int) cmbThang.getSelectedItem();
            int nam = (int) cmbNamThang.getSelectedItem();
            // Lấy danh sách các ngày trong tháng
            List<java.sql.Date> danhSachNgayTrongThang = getDanhSachNgayTrongThang(nam, thang);
            thongKeController.thongKeDoanhThuTheoCacNgay(danhSachNgayTrongThang);
            hienThiTieuDeChiTiet("Chi tiết doanh thu tháng " + thang + "/" + nam);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng và năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<java.sql.Date> getDanhSachNgayTrongThang(int nam, int thang) {
        List<java.sql.Date> danhSach = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, nam);
        calendar.set(Calendar.MONTH, thang - 1);
        int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= lastDayOfMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            danhSach.add(new java.sql.Date(calendar.getTimeInMillis()));
        }
        return danhSach;
    }

    private void hienThiChiTietNam() {
        if (cmbNamNam != null) {
            int nam = (int) cmbNamNam.getSelectedItem();
            // Lấy danh sách các tháng trong năm
            List<java.sql.Date> danhSachNgayDauThang = getDanhSachNgayDauThangTrongNam(nam);
            thongKeController.thongKeDoanhThuTheoCacThangTrongNam(danhSachNgayDauThang);
            hienThiTieuDeChiTiet("Chi tiết doanh thu năm " + nam + " (theo tháng)");
        } else {
            JOptionPane.showInputDialog(this, "Vui lòng chọn năm!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<java.sql.Date> getDanhSachNgayDauThangTrongNam(int nam) {
        List<java.sql.Date> danhSach = new ArrayList<>();
        for (int thang = 0; thang < 12; thang++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, nam);
            calendar.set(Calendar.MONTH, thang);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            danhSach.add(new java.sql.Date(calendar.getTimeInMillis()));
        }
        return danhSach;
    }

    private void hienThiTieuDeChiTiet(String tieuDe) {
        DefaultTableModel chiTietTableModel = new DefaultTableModel(new String[]{"Thời gian", "Tổng Doanh Thu"}, 0);
        JTable chiTietTable = new JTable(chiTietTableModel);
        chiTietTable.setFont(controlFont);
        JScrollPane chiTietScrollPane = new JScrollPane(chiTietTable);

        JFrame frameChiTiet = new JFrame(tieuDe);
        frameChiTiet.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameChiTiet.setSize(500, 300);
        frameChiTiet.setLocationRelativeTo(this);
        frameChiTiet.getContentPane().add(chiTietScrollPane);
        frameChiTiet.setVisible(true);
        tableThongKe = chiTietTable; // Cập nhật bảng hiển thị chính để hiển thị chi tiết
        tableModelThongKe = chiTietTableModel;
        revalidate();
        repaint();
    }

    public void hienThiThongKeChiTiet(java.util.List<Object[]> data) {
        if (tableModelThongKe != null) {
            tableModelThongKe.setRowCount(0);
            for (Object[] rowData : data) {
                tableModelThongKe.addRow(rowData);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Thống kê Doanh Thu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new ThongKeDoanhThuPanel());
        frame.setVisible(true);
    }
}