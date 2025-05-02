package view;

import model.LichHen;
import controller.LichHenController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TabDatLichHen extends JPanel {
    // Components cho tab Đặt lịch hẹn
    private JComboBox<String> cboBacSi;
    private JComboBox<String> cboPhongKham;
    private JTextField txtNgayHen;
    private JTextField txtGioHen;
    private JTextArea txtMoTa;
    private JButton btnDatLich;
    private JButton btnLamMoi;
    
    // Controller
    private LichHenController lichHenController;
    
    // Thông tin người dùng hiện tại
    private int idBenhNhan;
    private String hoTenBenhNhan;
    
    // Biến để theo dõi chế độ cập nhật
    private boolean isUpdateMode = false;
    private int updateLichHenId = -1;
    
    // Reference to parent form để gọi loadLichHenCaNhan
    private GiaoDienKhachHang parentForm;
    
    /**
     * Khởi tạo tab Đặt lịch hẹn
     * @param parentForm Reference to parent form
     * @param lichHenController Bộ điều khiển lịch hẹn
     * @param idBenhNhan ID của bệnh nhân đang đăng nhập
     * @param hoTenBenhNhan Họ tên của bệnh nhân đang đăng nhập
     */
    public TabDatLichHen(GiaoDienKhachHang parentForm, LichHenController lichHenController, int idBenhNhan, String hoTenBenhNhan) {
        this.parentForm = parentForm;
        this.lichHenController = lichHenController;
        this.idBenhNhan = idBenhNhan;
        this.hoTenBenhNhan = hoTenBenhNhan;
        
        initComponents();
        loadDanhSachBacSi();
        loadDanhSachPhongKham();
    }
    
    /**
     * Khởi tạo các thành phần giao diện
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel nhập thông tin lịch hẹn
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Thông tin lịch hẹn", 
                TitledBorder.LEADING, 
                TitledBorder.TOP));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Bác sĩ
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblBacSi = new JLabel("Bác sĩ:");
        panelForm.add(lblBacSi, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        cboBacSi = new JComboBox<>();
        panelForm.add(cboBacSi, gbc);
        
        // Phòng khám
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel lblPhongKham = new JLabel("Phòng khám:");
        panelForm.add(lblPhongKham, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        cboPhongKham = new JComboBox<>();
        panelForm.add(cboPhongKham, gbc);
        
        // Ngày hẹn
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel lblNgayHen = new JLabel("Ngày hẹn (dd/MM/yyyy):");
        panelForm.add(lblNgayHen, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        txtNgayHen = new JTextField();
        panelForm.add(txtNgayHen, gbc);
        
        // Giờ hẹn
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel lblGioHen = new JLabel("Giờ hẹn (HH:mm):");
        panelForm.add(lblGioHen, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        txtGioHen = new JTextField();
        panelForm.add(txtGioHen, gbc);
        
        // Mô tả
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel lblMoTa = new JLabel("Mô tả triệu chứng:");
        panelForm.add(lblMoTa, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        txtMoTa = new JTextArea(4, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        panelForm.add(scrollMoTa, gbc);
        
        // Panel nút
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnDatLich = new JButton("Đặt lịch hẹn");
        btnLamMoi = new JButton("Làm mới");
        panelButton.add(btnDatLich);
        panelButton.add(btnLamMoi);
        
        // Thêm vào panel chính
        add(panelForm, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
        
        // Sự kiện nút Đặt lịch
        btnDatLich.addActionListener(e -> {
            if (isUpdateMode) {
                capNhatLichHenAction();
            } else {
                datLichHen();
            }
        });
        
        // Sự kiện nút Làm mới
        btnLamMoi.addActionListener(e -> {
            resetFormDatLich();
        });
    }
    
    /**
     * Load danh sách bác sĩ vào combobox
     */
    public void loadDanhSachBacSi() {
        cboBacSi.removeAllItems();
        List<String> dsBacSi = lichHenController.danhSachBacSi();
        for (String bacSi : dsBacSi) {
            cboBacSi.addItem(bacSi);
        }
    }
    
    /**
     * Load danh sách phòng khám vào combobox
     */
    public void loadDanhSachPhongKham() {
        cboPhongKham.removeAllItems();
        List<String> dsPhongKham = lichHenController.danhSachPhongKham();
        for (String phongKham : dsPhongKham) {
            cboPhongKham.addItem(phongKham);
        }
    }
    
    /**
     * Reset form đặt lịch về trạng thái ban đầu
     */
    public void resetFormDatLich() {
        txtNgayHen.setText("");
        txtGioHen.setText("");
        txtMoTa.setText("");
        if (cboBacSi.getItemCount() > 0) {
            cboBacSi.setSelectedIndex(0);
        }
        if (cboPhongKham.getItemCount() > 0) {
            cboPhongKham.setSelectedIndex(0);
        }
        
        // Reset lại chế độ đặt lịch
        if (isUpdateMode) {
            isUpdateMode = false;
            updateLichHenId = -1;
            btnDatLich.setText("Đặt lịch hẹn");
        }
    }
    
    /**
     * Cập nhật lịch hẹn đã chọn
     */
    public void capNhatLichHen(int idLichHen) {
        // Lấy thông tin lịch hẹn theo ID từ danh sách
        List<LichHen> dsLichHen = lichHenController.getAllLichHen();
        LichHen lichHenCapNhat = null;
        
        for (LichHen lichHen : dsLichHen) {
            if (lichHen.getIdLichHen() == idLichHen) {
                lichHenCapNhat = lichHen;
                break;
            }
        }
        
        if (lichHenCapNhat == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin lịch hẹn");
            return;
        }
        
        // Chuyển sang chế độ cập nhật
        isUpdateMode = true;
        updateLichHenId = idLichHen;
        btnDatLich.setText("Cập nhật lịch hẹn");
        
        // Đổ dữ liệu lên form
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        // Chọn bác sĩ
        for (int i = 0; i < cboBacSi.getItemCount(); i++) {
            if (cboBacSi.getItemAt(i).equals(lichHenCapNhat.getHoTenBacSi())) {
                cboBacSi.setSelectedIndex(i);
                break;
            }
        }
        
        // Chọn phòng khám
        for (int i = 0; i < cboPhongKham.getItemCount(); i++) {
            if (cboPhongKham.getItemAt(i).equals(lichHenCapNhat.getTenPhong())) {
                cboPhongKham.setSelectedIndex(i);
                break;
            }
        }
        
        // Thiết lập ngày giờ và mô tả
        txtNgayHen.setText(dateFormat.format(lichHenCapNhat.getNgayHen()));
        txtGioHen.setText(timeFormat.format(lichHenCapNhat.getGioHen()));
        txtMoTa.setText(lichHenCapNhat.getMoTa());
    }
    
    /**
     * Xử lý sự kiện đặt lịch hẹn
     */
    private void datLichHen() {
        // Kiểm tra dữ liệu nhập
        if (cboBacSi.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ");
            return;
        }
        
        if (cboPhongKham.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng khám");
            return;
        }
        
        String strNgayHen = txtNgayHen.getText().trim();
        if (strNgayHen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày hẹn");
            return;
        }
        
        String strGioHen = txtGioHen.getText().trim();
        if (strGioHen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giờ hẹn");
            return;
        }
        
        // Chuyển đổi dữ liệu
        Date ngayHen = null;
        Time gioHen = null;
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date utilDate = dateFormat.parse(strNgayHen);
            ngayHen = new Date(utilDate.getTime());
            
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            java.util.Date utilTime = timeFormat.parse(strGioHen);
            gioHen = new Time(utilTime.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày giờ không hợp lệ. Vui lòng nhập đúng định dạng (dd/MM/yyyy và HH:mm)");
            return;
        }
        
        // Kiểm tra ngày hẹn phải là ngày trong tương lai
        Calendar cal = Calendar.getInstance();
        if (ngayHen.before(new Date(cal.getTimeInMillis()))) {
            JOptionPane.showMessageDialog(this, "Ngày hẹn phải là ngày trong tương lai");
            return;
        }
        
        // Lấy dữ liệu từ form
        String hoTenBacSi = cboBacSi.getSelectedItem().toString();
        String tenPhong = cboPhongKham.getSelectedItem().toString();
        String moTa = txtMoTa.getText().trim();
        
        // Lấy IDs từ tên
        int idBacSi = lichHenController.getBacSiIdFromName(hoTenBacSi);
        int idPhongKham = lichHenController.getPhongKhamIdFromName(tenPhong);
        
        if (idBacSi == -1 || idPhongKham == -1) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin bác sĩ hoặc phòng khám");
            return;
        }
        
        // Kiểm tra trùng lịch
        if (kiemTraTrungLich(idBacSi, ngayHen, gioHen, -1)) {
            JOptionPane.showMessageDialog(this, "Bác sĩ đã có lịch hẹn vào thời gian này. Vui lòng chọn thời gian khác.");
            return;
        }
        
        // Tạo đối tượng LichHen mới
        LichHen lichHen = new LichHen();
        lichHen.setIdBacSi(idBacSi);
        lichHen.setHoTenBacSi(hoTenBacSi);
        lichHen.setIdBenhNhan(idBenhNhan);
        lichHen.setHoTenBenhNhan(hoTenBenhNhan);
        lichHen.setNgayHen(ngayHen);
        lichHen.setIdPhongKham(idPhongKham);
        lichHen.setTenPhong(tenPhong);
        lichHen.setGioHen(gioHen);
        lichHen.setTrangThai("Chờ xác nhận");
        lichHen.setMoTa(moTa);
        
        // Lưu lịch hẹn
        boolean ketQua = lichHenController.datLichHen(lichHen);
        
        if (ketQua) {
            JOptionPane.showMessageDialog(this, "Đặt lịch hẹn thành công!");
            // Reset form
            resetFormDatLich();
            
            // Cập nhật bảng lịch hẹn
            parentForm.loadLichHenCaNhan();
            
            // Chuyển sang tab lịch hẹn
            parentForm.chuyenTabLichHen();
        } else {
            JOptionPane.showMessageDialog(this, "Đặt lịch hẹn thất bại! Vui lòng kiểm tra lại thông tin.");
        }
    }
    
    /**
     * Xử lý sự kiện cập nhật lịch hẹn
     */
    private void capNhatLichHenAction() {
        // Kiểm tra và lấy dữ liệu từ form
        if (cboBacSi.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ");
            return;
        }
        
        if (cboPhongKham.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng khám");
            return;
        }
        
        String strNgayHen = txtNgayHen.getText().trim();
        if (strNgayHen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày hẹn");
            return;
        }
        
        String strGioHen = txtGioHen.getText().trim();
        if (strGioHen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giờ hẹn");
            return;
        }
        
        // Chuyển đổi dữ liệu
        Date ngayHen = null;
        Time gioHen = null;
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date utilDate = dateFormat.parse(strNgayHen);
            ngayHen = new Date(utilDate.getTime());
            
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            java.util.Date utilTime = timeFormat.parse(strGioHen);
            gioHen = new Time(utilTime.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày giờ không hợp lệ. Vui lòng nhập đúng định dạng (dd/MM/yyyy và HH:mm)");
            return;
        }
        
        // Kiểm tra ngày hẹn phải là ngày trong tương lai
        Calendar cal = Calendar.getInstance();
        if (ngayHen.before(new Date(cal.getTimeInMillis()))) {
            JOptionPane.showMessageDialog(this, "Ngày hẹn phải là ngày trong tương lai");
            return;
        }
        
        // Lấy dữ liệu từ form
        String hoTenBacSi = cboBacSi.getSelectedItem().toString();
        String tenPhong = cboPhongKham.getSelectedItem().toString();
        String moTa = txtMoTa.getText().trim();
        
        // Lấy IDs từ tên
        int idBacSi = lichHenController.getBacSiIdFromName(hoTenBacSi);
        int idPhongKham = lichHenController.getPhongKhamIdFromName(tenPhong);
        
        if (idBacSi == -1 || idPhongKham == -1) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin bác sĩ hoặc phòng khám");
            return;
        }
        
        // Kiểm tra trùng lịch (trừ lịch hẹn hiện tại)
        if (kiemTraTrungLich(idBacSi, ngayHen, gioHen, updateLichHenId)) {
            JOptionPane.showMessageDialog(this, "Bác sĩ đã có lịch hẹn vào thời gian này. Vui lòng chọn thời gian khác.");
            return;
        }
        
        // Lấy đối tượng LichHen hiện tại để cập nhật
        List<LichHen> dsLichHen = lichHenController.getAllLichHen();
        LichHen lichHenCapNhat = null;
        
        for (LichHen lichHen : dsLichHen) {
            if (lichHen.getIdLichHen() == updateLichHenId) {
                lichHenCapNhat = lichHen;
                break;
            }
        }
        
        if (lichHenCapNhat == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin lịch hẹn");
            return;
        }
        
        // Cập nhật thông tin lịch hẹn
        lichHenCapNhat.setIdBacSi(idBacSi);
        lichHenCapNhat.setHoTenBacSi(hoTenBacSi);
        lichHenCapNhat.setIdPhongKham(idPhongKham);
        lichHenCapNhat.setTenPhong(tenPhong);
        lichHenCapNhat.setNgayHen(ngayHen);
        lichHenCapNhat.setGioHen(gioHen);
        lichHenCapNhat.setMoTa(moTa);
        
        // Cập nhật lịch hẹn
        boolean ketQua = lichHenController.updateLichHen(lichHenCapNhat);
        
        if (ketQua) {
            JOptionPane.showMessageDialog(this, "Cập nhật lịch hẹn thành công!");
            // Reset form và quay lại chế độ đặt lịch
            resetFormDatLich();
            isUpdateMode = false;
            updateLichHenId = -1;
            btnDatLich.setText("Đặt lịch hẹn");
            
            // Cập nhật bảng lịch hẹn
            parentForm.loadLichHenCaNhan();
            
            // Chuyển sang tab lịch hẹn
            parentForm.chuyenTabLichHen();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật lịch hẹn thất bại! Vui lòng kiểm tra lại thông tin.");
        }
    }
    
    /**
     * Kiểm tra tính hợp lệ của ngày giờ nhập vào
     * @param strNgay chuỗi ngày (dd/MM/yyyy)
     * @param strGio chuỗi giờ (HH:mm)
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    private boolean kiemTraNgayGioHopLe(String strNgay, String strGio) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false); // Không cho phép chuyển đổi linh hoạt
            java.util.Date ngay = dateFormat.parse(strNgay);
            
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setLenient(false);
            java.util.Date gio = timeFormat.parse(strGio);
            
            // Kiểm tra ngày phải sau ngày hiện tại
            Calendar calHienTai = Calendar.getInstance();
            Calendar calNgayHen = Calendar.getInstance();
            calNgayHen.setTime(ngay);
            
            if (calNgayHen.before(calHienTai)) {
                JOptionPane.showMessageDialog(null, "Ngày hẹn phải là ngày trong tương lai");
                return false;
            }
            
            // Kiểm tra giờ hẹn hợp lệ (8:00 - 17:00)
            int gio24 = Integer.parseInt(strGio.split(":")[0]);
            if (gio24 < 8 || gio24 >= 17) {
                JOptionPane.showMessageDialog(null, "Giờ hẹn phải trong khoảng từ 8:00 đến 17:00");
                return false;
            }
            
            return true;
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Định dạng ngày giờ không hợp lệ");
            return false;
        }
    }
    
    /**
     * Kiểm tra xem lịch hẹn có trùng với lịch hẹn khác không
     * @param idBacSi ID bác sĩ
     * @param ngayHen Ngày hẹn
     * @param gioHen Giờ hẹn
     * @param idLichHen ID lịch hẹn hiện tại (nếu đang cập nhật)
     * @return true nếu trùng, false nếu không trùng
     */
    private boolean kiemTraTrungLich(int idBacSi, Date ngayHen, Time gioHen, int idLichHen) {
        List<LichHen> dsLichHen = lichHenController.getAllLichHen();
        
        for (LichHen lichHen : dsLichHen) {
            // Bỏ qua lịch hẹn hiện tại nếu đang cập nhật
            if (lichHen.getIdLichHen() == idLichHen) {
                continue;
            }
            
            // Kiểm tra trùng bác sĩ, ngày và giờ
            if (lichHen.getIdBacSi() == idBacSi 
                    && lichHen.getNgayHen().equals(ngayHen) 
                    && lichHen.getGioHen().equals(gioHen)
                    && !lichHen.getTrangThai().equals("Đã hủy")) {
                return true;
            }
        }
        
        return false;
    }
}