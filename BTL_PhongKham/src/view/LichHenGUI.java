package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import controller.LichHenController;
import model.LichHen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LichHenGUI extends JPanel {
    private JPanel calendarPanel;
    private JLabel weekLabel;
    private JButton prevWeekButton, nextWeekButton;
    private LichHenController qlLichHen;
    private LocalDate currentWeekStart;
    private JDateChooser dateChooser;
    private JPanel searchResultsPanel;
    private JButton addAppointmentButton;

    public LichHenGUI() {
        qlLichHen = new LichHenController();
        currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        prevWeekButton = new JButton("<");
        nextWeekButton = new JButton(">");
        weekLabel = new JLabel(getFormattedWeek(), SwingConstants.CENTER);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
        dateChooser.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                LocalDate selectedDate = ((java.util.Date) evt.getNewValue()).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                currentWeekStart = selectedDate.with(java.time.DayOfWeek.MONDAY);
                updateCalendar();
            }
        });
        topPanel.add(prevWeekButton);
        topPanel.add(weekLabel);
        topPanel.add(dateChooser);
        topPanel.add(nextWeekButton);

        addAppointmentButton = new JButton("Thêm");
        addAppointmentButton.addActionListener(e -> addAppointment());
        topPanel.add(addAppointmentButton);

        prevWeekButton.addActionListener(e -> changeWeek(-1));
        nextWeekButton.addActionListener(e -> changeWeek(1));

        JTextField searchField = new JTextField(10);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch(searchField.getText());
            }
        });
        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);

        JButton searchButton = new JButton("Tìm");
        searchButton.addActionListener(e -> performSearch(searchField.getText()));
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        calendarPanel = new JPanel(new GridLayout(1, 7));
        add(calendarPanel, BorderLayout.CENTER);

        searchResultsPanel = new JPanel();
        searchResultsPanel.setLayout(new BoxLayout(searchResultsPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(searchResultsPanel), BorderLayout.SOUTH);

        updateCalendar();
    }
    private void performSearch(String query) {
        searchResultsPanel.removeAll();

        if (query.isEmpty()) {
            searchResultsPanel.revalidate();
            searchResultsPanel.repaint();
            updateCalendar();
            return;
        }

        // Lọc các lịch hẹn phù hợp với truy vấn tìm kiếm
        List<LichHen> searchResults = new ArrayList<>();
        for (LichHen lichHen : qlLichHen.getAllLichHen()) {
            if (lichHen.getHoTenBacSi().toLowerCase().contains(query.toLowerCase()) ||
                    lichHen.getHoTenBenhNhan().toLowerCase().contains(query.toLowerCase()) ||
                    lichHen.getTenPhong().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(lichHen);
            }
        }

        // Cập nhật lại bảng kết quả tìm kiếm
        for (LichHen lichHen : searchResults) {
            JPanel resultPanel = new JPanel();
            resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel resultLabel = new JLabel(lichHen.toString());
            resultPanel.add(resultLabel);

            resultPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showAppointmentDetails(lichHen);
                    highlightAppointment(lichHen);
                    scrollToAppointment(lichHen); // Cuộn đến lịch hẹn đã chọn
                }
            });
            searchResultsPanel.add(resultPanel);
        }

        // Cập nhật lại bảng lịch hẹn
        updateCalendarWithSearchResults(searchResults);

        searchResultsPanel.revalidate();
        searchResultsPanel.repaint();
    }

    private void updateCalendarWithSearchResults(List<LichHen> searchResults) {
        calendarPanel.removeAll();
        LocalDate date = currentWeekStart;
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};

        for (int i = 0; i < 7; i++) {
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel dayLabel = new JLabel(days[i] + " (" + date.format(DateTimeFormatter.ofPattern("d/M")) + ")", SwingConstants.CENTER);
            dayLabel.setOpaque(true);
            dayLabel.setBackground(new Color(173, 216, 230));
            dayPanel.add(dayLabel, BorderLayout.NORTH);

            JPanel morningPanel = new JPanel(new GridLayout(0, 1));
            morningPanel.setBorder(BorderFactory.createTitledBorder("Sáng"));
            JPanel afternoonPanel = new JPanel(new GridLayout(0, 1));
            afternoonPanel.setBorder(BorderFactory.createTitledBorder("Chiều"));

            // Lọc các lịch hẹn theo ngày
            for (LichHen lichHen : searchResults) {
                if (Date.valueOf(date).equals(lichHen.getNgayHen())) {
                    JTextArea eventText = new JTextArea(
                            "BS:" + lichHen.getHoTenBacSi() + "\n" +
                            "BN:" + lichHen.getHoTenBenhNhan() + "\n" +
                            "Phòng:" + lichHen.getTenPhong() + "\n" +
                            "Giờ:" + lichHen.getGioHen().toString()
                    );
                    eventText.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            showPopupMenu(evt, lichHen);
                        }
                    });
                    eventText.setFont(new Font("Arial", Font.PLAIN, 10));
                    eventText.setEditable(false);
                    eventText.setOpaque(true);
                    eventText.setBackground(getColorByStatus(lichHen.getTrangThai()));

                    eventText.setName(lichHen.getHoTenBenhNhan());
                    dayPanel.setName(lichHen.getHoTenBenhNhan());

                    LocalTime gioHen = lichHen.getGioHen().toLocalTime();
                    if (gioHen.isBefore(LocalTime.of(12, 0))) {
                        morningPanel.add(eventText);
                    } else {
                        afternoonPanel.add(eventText);
                    }
                }
            }

            JPanel contentPanel = new JPanel(new GridLayout(2, 1));
            contentPanel.add(morningPanel);
            contentPanel.add(afternoonPanel);
            dayPanel.add(contentPanel, BorderLayout.CENTER);
            calendarPanel.add(dayPanel);
            date = date.plusDays(1);
        }

        weekLabel.setText(getFormattedWeek());
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }


    private void highlightAppointment(LichHen lichHen) {
        for (Component comp : calendarPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getName() != null && panel.getName().equals(lichHen.getHoTenBenhNhan())) {
                    panel.setBackground(Color.YELLOW);
                    break;
                }
            }
        }
    }

    private void scrollToAppointment(LichHen lichHen) {
        for (Component comp : calendarPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getName() != null && panel.getName().equals(lichHen.getHoTenBenhNhan())) {
                    JScrollPane scrollPane = (JScrollPane) calendarPanel.getParent();
                    int panelY = panel.getY();
                    scrollPane.getVerticalScrollBar().setValue(panelY);
                    break;
                }
            }
        }
    }
    private void showAppointmentDetails(LichHen lichHen) {
        // Tạo một cửa sổ chi tiết hoặc panel mới để hiển thị thông tin
        JFrame detailFrame = new JFrame("Chi tiết lịch hẹn");
        detailFrame.setSize(280, 300); // Điều chỉnh kích thước cho phù hợp
        detailFrame.setLayout(new BorderLayout());

        // Tạo panel để chứa thông tin chi tiết
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridLayout(6, 2, 5, 5)); // 6 dòng (bỏ mô tả nếu không cần), mỗi dòng có 2 cột (label và giá trị)

        // Thêm khung vào panel (TitledBorder hoặc LineBorder)
        detailPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "Thông tin lịch hẹn", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), 
            Color.BLACK
        ));

        // Lấy thông tin bác sĩ, bệnh nhân, trạng thái, phòng khám từ đối tượng LichHen
        String hoTenBacSi = lichHen.getHoTenBacSi();
        String hoTenBenhNhan = lichHen.getHoTenBenhNhan();
        java.sql.Date ngayHen = lichHen.getNgayHen();
        java.sql.Time gioHen = lichHen.getGioHen();
        String tenPhongKham = lichHen.getTenPhong();
        String trangThai = lichHen.getTrangThai();
        String moTa = lichHen.getMoTa();

        // Định dạng giờ hẹn
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String gioHenString = timeFormat.format(gioHen); // Chuyển đối tượng Time thành chuỗi giờ

        // Thêm các thành phần vào panel
        detailPanel.add(new JLabel("Tên bác sĩ:"));
        detailPanel.add(new JLabel(hoTenBacSi));
        detailPanel.add(new JLabel("Tên bệnh nhân:"));
        detailPanel.add(new JLabel(hoTenBenhNhan));
        detailPanel.add(new JLabel("Ngày hẹn:"));
        detailPanel.add(new JLabel(ngayHen.toString())); // Hiển thị ngày hẹn
        detailPanel.add(new JLabel("Giờ hẹn (HH:mm):"));
        detailPanel.add(new JLabel(gioHenString)); // Hiển thị giờ hẹn
        detailPanel.add(new JLabel("Tên phòng khám:"));
        detailPanel.add(new JLabel(tenPhongKham)); // Hiển thị tên phòng khám
        detailPanel.add(new JLabel("Trạng thái:"));
        detailPanel.add(new JLabel(trangThai)); // Hiển thị trạng thái
        // Bỏ dòng này nếu không muốn hiển thị mô tả trong cửa sổ chi tiết riêng
        // detailPanel.add(new JLabel("Mô tả:"));
        // detailPanel.add(new JLabel(moTa)); // Hiển thị mô tả

        // Thêm panel vào cửa sổ chi tiết
        detailFrame.add(detailPanel, BorderLayout.CENTER);

        // Thêm nút đóng cửa sổ
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> detailFrame.dispose()); // Đóng cửa sổ khi nhấn nút

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        detailFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Hiển thị cửa sổ chi tiết
        detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailFrame.setLocationRelativeTo(this); // Hiển thị cửa sổ ở giữa panel lịch hẹn
        detailFrame.setVisible(true);
    }
    private void addAppointment() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Lấy danh sách bác sĩ
        List<String> bacSiList = qlLichHen.danhSachBacSi();
        JComboBox<String> comboBacSi = new JComboBox<>(bacSiList.toArray(new String[0]));

        // Lấy danh sách bệnh nhân
        List<String> benhNhanList = qlLichHen.danhSachBenhNhan();
        JComboBox<String> comboBenhNhan = new JComboBox<>(benhNhanList.toArray(new String[0]));

        // Lấy danh sách phòng khám
        List<String> phongKhamList = qlLichHen.danhSachPhongKham();
        JComboBox<String> comboPhongKham = new JComboBox<>(phongKhamList.toArray(new String[0]));

        // Các trường nhập liệu khác
        JDateChooser dateChooserNgayHen = new JDateChooser();
        JTextField txtGioHen = new JTextField();
        dateChooserNgayHen.setDate(Calendar.getInstance().getTime());
        JComboBox<LichHen.TrangThaiLichHen> statusComboBox = new JComboBox<>(LichHen.TrangThaiLichHen.values());
        JTextField txtMoTa = new JTextField();

        // Thêm các phần tử vào panel
        panel.add(new JLabel("Tên bác sĩ:"));
        panel.add(comboBacSi);
        panel.add(new JLabel("Tên bệnh nhân:"));
        panel.add(comboBenhNhan);
        panel.add(new JLabel("Ngày hẹn:"));
        panel.add(dateChooserNgayHen);
        panel.add(new JLabel("Giờ hẹn (HH:mm):"));
        panel.add(txtGioHen);
        panel.add(new JLabel("Phòng khám:"));
        panel.add(comboPhongKham);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(statusComboBox);
        panel.add(new JLabel("Mô tả:"));
        panel.add(txtMoTa);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm Lịch Hẹn",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String hoTenBacSi = ((String) comboBacSi.getSelectedItem()).trim();
                String hoTenBenhNhan = ((String) comboBenhNhan.getSelectedItem()).trim();
                String tenPhongKham = ((String) comboPhongKham.getSelectedItem()).trim();
                System.out.println("Tên bác sĩ đã chọn: " + hoTenBacSi);
                System.out.println("Tên bệnh nhân đã chọn: " + hoTenBenhNhan);
                System.out.println("Tên phòng khám đã chọn: " + tenPhongKham);

                // Lấy ID từ các tên đã chọn
                int idBacSi = qlLichHen.getBacSiIdFromName(hoTenBacSi);
                int idBenhNhan = qlLichHen.getBenhNhanIdFromName(hoTenBenhNhan);
                int idPhongKham = qlLichHen.getPhongKhamIdFromName(tenPhongKham);

                if (idBacSi == -1 || idBenhNhan == -1 || idPhongKham == -1) {
                    JOptionPane.showMessageDialog(this, "Tên bác sĩ, bệnh nhân hoặc phòng khám không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.util.Date ngayHenDate = dateChooserNgayHen.getDate();
                java.sql.Date ngayHen = new java.sql.Date(ngayHenDate.getTime());
                LocalTime gioHen = LocalTime.parse(txtGioHen.getText());
                java.sql.Time timeGioHen = java.sql.Time.valueOf(gioHen);
                LichHen.TrangThaiLichHen trangThaiEnum = (LichHen.TrangThaiLichHen) statusComboBox.getSelectedItem();
                String moTa = txtMoTa.getText();

                LichHen lichHen = new LichHen(0, idBacSi, hoTenBacSi, idBenhNhan, hoTenBenhNhan, ngayHen, idPhongKham, tenPhongKham, timeGioHen, trangThaiEnum, moTa);

                qlLichHen.datLichHen(lichHen);

                JOptionPane.showMessageDialog(this, "Thêm lịch hẹn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                updateCalendar();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu! Kiểm tra lại định dạng giờ (HH:mm).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Lỗi định dạng giờ (HH:mm).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu! Kiểm tra lại định dạng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private String getFormattedWeek() {
        LocalDate endOfWeek = currentWeekStart.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        return currentWeekStart.format(formatter) + " - " + endOfWeek.format(formatter);
    }

    private void changeWeek(int delta) {
        currentWeekStart = currentWeekStart.plusWeeks(delta);
        updateCalendar();
    }

    private void updateCalendar() {
        calendarPanel.removeAll();
        LocalDate date = currentWeekStart;
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};

        for (int i = 0; i < 7; i++) {
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel dayLabel = new JLabel(days[i] + " (" + date.format(DateTimeFormatter.ofPattern("d/M")) + ")", SwingConstants.CENTER);
            dayLabel.setOpaque(true);
            dayLabel.setBackground(new Color(173, 216, 230));
            dayPanel.add(dayLabel, BorderLayout.NORTH);

            JPanel morningPanel = new JPanel(new GridLayout(0, 1));
            morningPanel.setBorder(BorderFactory.createTitledBorder("Sáng"));
            JPanel afternoonPanel = new JPanel(new GridLayout(0, 1));
            afternoonPanel.setBorder(BorderFactory.createTitledBorder("Chiều"));

            List<LichHen> lichHenList = qlLichHen.getLichHenByDate(Date.valueOf(date));

            if (lichHenList != null) {
                lichHenList.sort(Comparator.comparing(LichHen::getGioHen));

                for (LichHen info : lichHenList) {
                    JTextArea eventText = new JTextArea(
                            "BS:" + info.getHoTenBacSi() + "\n" +
                                    "BN:" + info.getHoTenBenhNhan() + "\n" +
                                    "Phòng:" + info.getTenPhong() + "\n" +
                                    "Giờ:" + info.getGioHen().toString()
                    );
                    eventText.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            showPopupMenu(evt, info);
                        }
                    });
                    eventText.setFont(new Font("Arial", Font.PLAIN, 10));
                    eventText.setEditable(false);
                    eventText.setOpaque(true);
                    eventText.setBackground(getColorByStatus(info.getTrangThai()));

                    eventText.setName(info.getHoTenBenhNhan());
                    dayPanel.setName(info.getHoTenBenhNhan());

                    LocalTime gioHen = info.getGioHen().toLocalTime();
                    if (gioHen.isBefore(LocalTime.of(12, 0))) {
                        morningPanel.add(eventText);
                    } else {
                        afternoonPanel.add(eventText);
                    }
                }
            }

            JPanel contentPanel = new JPanel(new GridLayout(2, 1));
            contentPanel.add(morningPanel);
            contentPanel.add(afternoonPanel);
            dayPanel.add(contentPanel, BorderLayout.CENTER);
            calendarPanel.add(dayPanel);
            date = date.plusDays(1);
        }

        weekLabel.setText(getFormattedWeek());
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void showPopupMenu(java.awt.event.MouseEvent evt, LichHen lichHen) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem detailItem = new JMenuItem("Chi tiết");
        detailItem.addActionListener(e -> showDetails(lichHen));
        popupMenu.add(detailItem);

        JMenuItem editItem = new JMenuItem("Chỉnh sửa");
        editItem.addActionListener(e -> editAppointment(lichHen));
        popupMenu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Xóa");
        deleteItem.addActionListener(e -> deleteAppointment(lichHen));
        popupMenu.add(deleteItem);

        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }

    private void showDetails(LichHen lichHen) {
        JOptionPane.showMessageDialog(this,
                "Bác sĩ: " + lichHen.getHoTenBacSi() + "\n" +
                        "Bệnh nhân: " + lichHen.getHoTenBenhNhan() + "\n" +
                        "Phòng khám: " + lichHen.getTenPhong() + "\n" +
                        "Giờ hẹn: " + lichHen.getGioHen().toString() + "\n" +
                        "Trạng thái: " + lichHen.getTrangThai(),
                "Chi tiết lịch hẹn",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void editAppointment(LichHen lichHen) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Lấy danh sách bác sĩ và bệnh nhân
        List<String> bacSiList = qlLichHen.danhSachBacSi();
        List<String> benhNhanList = qlLichHen.danhSachBenhNhan();

        // Tạo Map từ List
        Map<String, Integer> bacSiMap = new LinkedHashMap<>();
        for (String ten : bacSiList) {
            int id = qlLichHen.getBacSiIdFromName(ten);
            bacSiMap.put(ten, id);
        }
        Map<String, Integer> benhNhanMap = new LinkedHashMap<>();
        for (String ten : benhNhanList) {
            int id = qlLichHen.getBenhNhanIdFromName(ten);
            benhNhanMap.put(ten, id);
        }

        // ComboBox chọn bác sĩ
        JComboBox<String> comboBacSi = new JComboBox<>(bacSiMap.keySet().toArray(new String[0]));
        for (String ten : bacSiMap.keySet()) {
            if (bacSiMap.get(ten).equals(lichHen.getIdBacSi())) {
                comboBacSi.setSelectedItem(ten);
                break;
            }
        }

        // ComboBox chọn bệnh nhân
        JComboBox<String> comboBenhNhan = new JComboBox<>(benhNhanMap.keySet().toArray(new String[0]));
        for (String ten : benhNhanMap.keySet()) {
            if (benhNhanMap.get(ten).equals(lichHen.getIdBenhNhan())) {
                comboBenhNhan.setSelectedItem(ten);
                break;
            }
        }

        // Ngày hẹn
        JDateChooser dateChooserNgayHen = new JDateChooser();
        dateChooserNgayHen.setDateFormatString("yyyy-MM-dd");
        dateChooserNgayHen.setDate(java.sql.Date.valueOf(lichHen.getNgayHen().toLocalDate()));

        JTextField txtTenPhongKham = new JTextField(String.valueOf(lichHen.getTenPhong()));
        JTextField txtGioHen = new JTextField(lichHen.getGioHen().toString());

        String[] statuses = {"Chờ xác nhận", "Đã xác nhận", "Đã hủy"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setSelectedItem(lichHen.getTrangThai());

        JTextField txtMoTa = new JTextField(lichHen.getMoTa());

        panel.add(new JLabel("Bác sĩ:"));
        panel.add(comboBacSi);
        panel.add(new JLabel("Bệnh nhân:"));
        panel.add(comboBenhNhan);
        panel.add(new JLabel("Ngày hẹn:"));
        panel.add(dateChooserNgayHen);
        panel.add(new JLabel("Phòng khám:"));
        panel.add(txtTenPhongKham);
        panel.add(new JLabel("Giờ hẹn (HH:mm):"));
        panel.add(txtGioHen);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(statusComboBox);
        panel.add(new JLabel("Mô tả:"));
        panel.add(txtMoTa);

        int result = JOptionPane.showConfirmDialog(this, panel, "Chỉnh sửa lịch hẹn",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                lichHen.setIdBacSi(bacSiMap.get(comboBacSi.getSelectedItem()));
                lichHen.setIdBenhNhan(benhNhanMap.get(comboBenhNhan.getSelectedItem()));

                java.util.Date selectedDate = dateChooserNgayHen.getDate();
                if (selectedDate != null) {
                    lichHen.setNgayHen(new java.sql.Date(selectedDate.getTime()));
                } else {
                    throw new IllegalArgumentException("Ngày hẹn không hợp lệ!");
                }

                String tenPhong = txtTenPhongKham.getText(); // Lấy tên phòng khám từ txtTenPhong
                int idPhongKham = qlLichHen.getPhongKhamIdFromName(tenPhong); // Lấy ID từ tên

                if (idPhongKham == -1) {
                    JOptionPane.showMessageDialog(this, "Tên phòng khám không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return; // Dừng cập nhật nếu tên phòng khám không tồn tại
                }

                lichHen.setIdPhongKham(idPhongKham); // Sử dụng ID phòng khám lấy được
                LocalTime newGioHen = LocalTime.parse(txtGioHen.getText());
                lichHen.setGioHen(java.sql.Time.valueOf(newGioHen));
                lichHen.setTrangThai((String) statusComboBox.getSelectedItem());
                lichHen.setMoTa(txtMoTa.getText());

                qlLichHen.updateLichHen(lichHen);

                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                updateCalendar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu! Kiểm tra lại định dạng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void deleteAppointment(LichHen lichHen) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa lịch hẹn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            qlLichHen.deleteLichHen(lichHen.getIdLichHen());

            JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            updateCalendar();
        }
    }

    private Color getColorByStatus(String status) {
        switch (status) {
            case "Chờ xác nhận":
                return new Color(255, 255, 102);
            case "Đã xác nhận":
                return new Color(144, 238, 144);
            case "Đã hủy":
                return new Color(255, 99, 71);
            default:
                return Color.WHITE;
        }
    }
}