package controller;

import connect.connectMySQL;
import view.DoanhThuUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoanhThuController {

    private DoanhThuUI view;
    private Connection conn;
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    private HoaDonController hoaDonController; // Reference to HoaDonController

    public DoanhThuController(DoanhThuUI view) {
        this.view = view;
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Constructor không tham số cho phép khởi tạo từ HoaDonController
    public DoanhThuController() {
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }
    
    // Method to set HoaDonController reference
    public void setHoaDonController(HoaDonController hoaDonController) {
        this.hoaDonController = hoaDonController;
    }
    
    public void loadDoanhThuData() {
        if (view == null) {
            return; // No view to display data
        }

        DefaultTableModel modelDoanhThu = view.getModelDoanhThu();
        modelDoanhThu.setRowCount(0);
        
        // Clear the original data list in the view before loading new data
        view.clearOriginalData();
        
        double totalRevenue = 0;

        // Sửa query để lấy tongDoanhThu từ bảng DoanhThu thay vì tongTien từ bảng HoaDon
        String sql = "SELECT dt.idDoanhThu, dt.idHoaDon, bn.hoTen, dt.thangNam, dt.tongDoanhThu, hd.trangThai " +
                     "FROM DoanhThu dt " +
                     "JOIN HoaDon hd ON dt.idHoaDon = hd.idHoaDon " +
                     "JOIN BenhNhan bn ON hd.idBenhNhan = bn.idBenhNhan " +
                     "WHERE hd.trangThai = 'DaThanhToan'";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
             
            while (resultSet.next()) {
                int idDoanhThu = resultSet.getInt("idDoanhThu");
                int idHoaDon = resultSet.getInt("idHoaDon");
                String hoTenBenhNhan = resultSet.getString("hoTen");
                Date thangNam = resultSet.getDate("thangNam");
                // Lấy tongDoanhThu từ bảng DoanhThu
                double tongDoanhThu = resultSet.getDouble("tongDoanhThu");
                String trangThai = resultSet.getString("trangThai");
                
                Object[] rowData = new Object[]{idDoanhThu, idHoaDon, hoTenBenhNhan, 
                                               monthYearFormat.format(thangNam), tongDoanhThu, trangThai};
                                               
                // Instead of directly adding to the model, use the view's method
                // which will add to both the model and the original data list
                view.loadDoanhThuData(rowData);
                
                totalRevenue += tongDoanhThu;
            }

            // Update total row in UI using the new method
            view.updateTotalRow(totalRevenue);

        } catch (SQLException e) {
            e.printStackTrace();
            if (view != null) {
                JOptionPane.showMessageDialog(view, "Lỗi truy vấn dữ liệu doanh thu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean themDoanhThu(java.util.Date thangNam, double tongDoanhThu, int idHoaDon) {
        // Kiểm tra xem hóa đơn có hợp lệ không
        if (!kiemTraHoaDon(idHoaDon)) {
            if (view != null) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(view, "Hóa đơn không tồn tại hoặc không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                });
            }
            return false;
        }
        
        // Đảm bảo Hóa đơn được cập nhật trạng thái thành "DaThanhToan"
        capNhatTrangThaiHoaDon(idHoaDon, "DaThanhToan");
        
        // Lấy tổng tiền từ hóa đơn nếu không có giá trị được cung cấp hoặc giá trị là 0
        if (tongDoanhThu <= 0) {
            tongDoanhThu = layTongTienHoaDon(idHoaDon);
        }
        
        String sql = "INSERT INTO DoanhThu (thangNam, tongDoanhThu, idHoaDon) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setBigDecimal(2, java.math.BigDecimal.valueOf(tongDoanhThu));
            preparedStatement.setInt(3, idHoaDon);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                if (view != null) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(view, "Thêm doanh thu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        loadDoanhThuData(); // Tải lại dữ liệu sau khi thêm (gọi trên EDT)
                    });
                }
                return true;
            } else {
                if (view != null) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(view, "Thêm doanh thu thất bại! (Kiểm tra ID Hóa đơn)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (view != null) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(view, "Lỗi thêm doanh thu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                });
            }
            return false;
        }
    }
    
    // Phương thức mới để lấy tổng tiền từ hóa đơn
    private double layTongTienHoaDon(int idHoaDon) {
        double tongTien = 0;
        String sql = "SELECT tongTien FROM HoaDon WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tongTien = resultSet.getDouble("tongTien");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongTien;
    }
    public double getHoaDonAmount(int idHoaDon) {
        // This method should be implemented in DoanhThuController to retrieve 
        // the invoice amount from the database
        try {
            // Connect to database and execute query to get invoice amount
            String query = "SELECT TongTien FROM HoaDon WHERE IDHoaDon = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idHoaDon);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("TongTien");
            }
            return 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    // Phương thức mới để kiểm tra xem hóa đơn có tồn tại không
    private boolean kiemTraHoaDon(int idHoaDon) {
        String sql = "SELECT idHoaDon FROM HoaDon WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Phương thức để thêm doanh thu từ hóa đơn đã thanh toán
    public boolean themDoanhThuTuHoaDon(int idHoaDon, double tongTien) {
        // Lấy thời gian hiện tại
        Calendar cal = Calendar.getInstance();
        java.util.Date currentDate = cal.getTime();
        
        // Gọi phương thức themDoanhThu đã có
        return themDoanhThu(currentDate, tongTien, idHoaDon);
    }

    public void xoaDoanhThu(int idDoanhThu) {
        // Lấy idHoaDon trước khi xóa
        int idHoaDon = -1;
        try {
            String selectSql = "SELECT idHoaDon FROM DoanhThu WHERE idDoanhThu = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, idDoanhThu);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    idHoaDon = rs.getInt("idHoaDon");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String sql = "DELETE FROM DoanhThu WHERE idDoanhThu = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idDoanhThu);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                // Nếu xóa thành công và có idHoaDon hợp lệ, cập nhật trạng thái hóa đơn thành "ChuaThanhToan"
                if (idHoaDon > 0) {
                    // Kiểm tra xem hóa đơn có trong bảng doanh thu khác không
                    if (!kiemTraHoaDonTrongDoanhThuKhac(idHoaDon, idDoanhThu)) {
                        capNhatTrangThaiHoaDon(idHoaDon, "ChuaThanhToan");
                    }
                }
                
                if (view != null) {
                    JOptionPane.showMessageDialog(view, "Xóa doanh thu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadDoanhThuData(); // Tải lại dữ liệu sau khi xóa
                }
            } else {
                if (view != null) {
                    JOptionPane.showMessageDialog(view, "Không tìm thấy doanh thu để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (view != null) {
                JOptionPane.showMessageDialog(view, "Lỗi xóa doanh thu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void suaDoanhThu(int idDoanhThu, java.util.Date thangNam, double tongDoanhThu, int idHoaDon) {
        // Kiểm tra xem hóa đơn có hợp lệ không
        if (!kiemTraHoaDon(idHoaDon)) {
            if (view != null) {
                JOptionPane.showMessageDialog(view, "Hóa đơn không tồn tại hoặc không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        // Kiểm tra xem có sự thay đổi idHoaDon không
        int oldIdHoaDon = -1;
        try {
            String selectSql = "SELECT idHoaDon FROM DoanhThu WHERE idDoanhThu = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, idDoanhThu);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    oldIdHoaDon = rs.getInt("idHoaDon");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Cập nhật trạng thái của hóa đơn mới thành "DaThanhToan"
        capNhatTrangThaiHoaDon(idHoaDon, "DaThanhToan");
        
        // Nếu idHoaDon thay đổi, đặt lại trạng thái của hóa đơn cũ
        if (oldIdHoaDon != idHoaDon && oldIdHoaDon > 0) {
            // Kiểm tra xem hóa đơn cũ có doanh thu nào khác không
            if (!kiemTraHoaDonTrongDoanhThuKhac(oldIdHoaDon, idDoanhThu)) {
                capNhatTrangThaiHoaDon(oldIdHoaDon, "ChuaThanhToan");
            }
        }
        
        // Lấy tổng tiền từ hóa đơn nếu không có giá trị được cung cấp hoặc giá trị là 0
        if (tongDoanhThu <= 0) {
            tongDoanhThu = layTongTienHoaDon(idHoaDon);
        }
        
        String sql = "UPDATE DoanhThu SET thangNam = ?, tongDoanhThu = ?, idHoaDon = ? WHERE idDoanhThu = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setBigDecimal(2, java.math.BigDecimal.valueOf(tongDoanhThu));
            preparedStatement.setInt(3, idHoaDon);
            preparedStatement.setInt(4, idDoanhThu);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                if (view != null) {
                    JOptionPane.showMessageDialog(view, "Sửa doanh thu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadDoanhThuData(); // Tải lại dữ liệu sau khi sửa
                }
            } else {
                if (view != null) {
                    JOptionPane.showMessageDialog(view, "Không tìm thấy doanh thu để sửa! (Kiểm tra ID Hóa đơn)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (view != null) {
                JOptionPane.showMessageDialog(view, "Lỗi sửa doanh thu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Phương thức để kiểm tra xem hóa đơn có trong bảng doanh thu khác không (trừ idDoanhThu hiện tại)
    private boolean kiemTraHoaDonTrongDoanhThuKhac(int idHoaDon, int idDoanhThu) {
        String sql = "SELECT COUNT(*) AS count FROM DoanhThu WHERE idHoaDon = ? AND idDoanhThu != ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            preparedStatement.setInt(2, idDoanhThu);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Phương thức cập nhật trạng thái cho hóa đơn - không cập nhật tổng tiền
    private void capNhatTrangThaiHoaDon(int idHoaDon, String trangThai) {
        // Sử dụng HoaDonController nếu đã được thiết lập
        if (hoaDonController != null) {
            // Lấy hóa đơn từ controller
            model.HoaDon hoaDon = hoaDonController.layHoaDonTheoId(idHoaDon);
            if (hoaDon != null) {
                hoaDon.setTrangThai(trangThai);
                hoaDonController.capNhatHoaDon(hoaDon);
                return;
            }
        }
        
        // Nếu không có hoaDonController hoặc không tìm thấy hóa đơn, cập nhật trực tiếp qua SQL
        String sql = "UPDATE HoaDon SET trangThai = ? WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, trangThai);
            preparedStatement.setInt(2, idHoaDon);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi cập nhật trạng thái hóa đơn: " + e.getMessage());
        }
    }
    
    // Phương thức để kiểm tra xem một hóa đơn đã có trong doanh thu chưa
    public boolean kiemTraHoaDonTrongDoanhThu(int idHoaDon) {
        String sql = "SELECT COUNT(*) AS count FROM DoanhThu WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Phương thức để xóa bản ghi doanh thu liên quan đến một hóa đơn
    public boolean xoaDoanhThuTheoHoaDonId(int idHoaDon) {
        String sql = "DELETE FROM DoanhThu WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            int affectedRows = preparedStatement.executeUpdate();
            
            // Cập nhật trạng thái hóa đơn thành "ChuaThanhToan" nếu có hóa đơn bị xóa
            if (affectedRows > 0) {
                capNhatTrangThaiHoaDon(idHoaDon, "ChuaThanhToan");
            }
            
            return affectedRows >= 0; // Trả về true ngay cả khi không có bản ghi nào bị xóa
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> getAvailableHoaDonIDs() throws SQLException {
        List<Integer> hoaDonIDs = new ArrayList<>();
        String sql = "SELECT idHoaDon FROM hoadon WHERE trangThai = 'DaThanhToan' AND idHoaDon NOT IN (SELECT idHoaDon FROM doanhthu WHERE idHoaDon IS NOT NULL)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                hoaDonIDs.add(rs.getInt("idHoaDon"));
            }
        }
        return hoaDonIDs;
    }
    
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}