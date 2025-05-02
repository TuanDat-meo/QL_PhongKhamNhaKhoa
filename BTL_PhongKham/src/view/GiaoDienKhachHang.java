package view;

import controller.LichHenController;
import controller.DichVuController;
import connect.connectMySQL;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

/**
 * Giao diện chính của khách hàng
 */
public class GiaoDienKhachHang extends JFrame {
    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    
    // Các panel cho các tab chức năng
    private TabDatLichHen panelDatLichHen;
    private TabLichHenCaNhan panelLichHenCaNhan;
    private TabDichVu panelDichVu;
    
    // Controllers
    private LichHenController lichHenController;
    private DichVuController dichVuController;
    
    // Thông tin người dùng hiện tại
    private int idBenhNhan;
    private String hoTenBenhNhan;
    
    /**
     * Khởi tạo giao diện khách hàng
     * @param idBenhNhan ID của bệnh nhân đang đăng nhập
     * @param hoTenBenhNhan Họ tên của bệnh nhân đang đăng nhập
     */
    public GiaoDienKhachHang(int idBenhNhan, String hoTenBenhNhan) {
        this.idBenhNhan = idBenhNhan;
        this.hoTenBenhNhan = hoTenBenhNhan;
        
        // Khởi tạo controllers
        lichHenController = new LichHenController();
        try {
            Connection conn = connectMySQL.getConnection();
            dichVuController = new DichVuController(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
        
        // Thiết lập giao diện
        setTitle("Hệ thống Phòng khám - Giao diện Khách hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        // Panel thông tin người dùng
        JPanel panelThongTin = new JPanel();
        panelThongTin.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblXinChao = new JLabel("Xin chào, " + hoTenBenhNhan);
        lblXinChao.setFont(new Font("Tahoma", Font.BOLD, 14));
        JButton btnDangXuat = new JButton("Đăng xuất");
        panelThongTin.add(lblXinChao);
        panelThongTin.add(btnDangXuat);
        contentPane.add(panelThongTin, BorderLayout.NORTH);
        
        // Tạo TabPane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        
        // Khởi tạo các panel và thêm vào tabPane
        initTabs();
        
        // Thêm sự kiện đăng xuất
        btnDangXuat.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc chắn muốn đăng xuất?", 
                    "Xác nhận đăng xuất", 
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                // Hiển thị form đăng nhập
                // new FormDangNhap().setVisible(true);
                JOptionPane.showMessageDialog(this, "Đã đăng xuất thành công!");
                System.exit(0);
            }
        });
    }
    
    /**
     * Khởi tạo các tab
     */
    private void initTabs() {
        // Tab Đặt lịch hẹn
        panelDatLichHen = new TabDatLichHen(lichHenController, idBenhNhan, hoTenBenhNhan);
        tabbedPane.addTab("Đặt lịch hẹn", null, panelDatLichHen, "Đặt lịch hẹn khám bệnh");
        
        // Tab Lịch hẹn cá nhân
        panelLichHenCaNhan = new TabLichHenCaNhan(lichHenController, idBenhNhan, hoTenBenhNhan);
        tabbedPane.addTab("Lịch hẹn cá nhân", null, panelLichHenCaNhan, "Xem và quản lý lịch hẹn cá nhân");
        
        // Tab Dịch vụ
        panelDichVu = new TabDichVu(dichVuController);
        tabbedPane.addTab("Dịch vụ", null, panelDichVu, "Xem danh sách dịch vụ");
        
        // Thiết lập tương tác giữa các tab
        setupTabInteractions();
    }
    
    /**
     * Thiết lập tương tác giữa các tab
     */
    private void setupTabInteractions() {
        // Khi cần chuyển từ tab lịch hẹn cá nhân sang tab đặt lịch để cập nhật
        panelLichHenCaNhan.setCapNhatLichHenCallback((idLichHen) -> {
            panelDatLichHen.capNhatLichHen(idLichHen);
            tabbedPane.setSelectedIndex(0);
        });
        
        // Khi đặt lịch thành công, cập nhật danh sách lịch hẹn và chuyển sang tab lịch hẹn
        panelDatLichHen.setDatLichThanhCongCallback(() -> {
            panelLichHenCaNhan.loadLichHenCaNhan();
            tabbedPane.setSelectedIndex(1);
        });
    }

    /**
     * Phương thức main để chạy ứng dụng
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Thiết lập look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Chạy giao diện
        SwingUtilities.invokeLater(() -> {
            // Thường thì sẽ có form đăng nhập trước, sau đó mới mở form này
            // Ở đây chỉ là ví dụ với id và tên bệnh nhân cố định
            new GiaoDienKhachHang(1, "Nguyễn Văn A").setVisible(true);
        });
    }
}