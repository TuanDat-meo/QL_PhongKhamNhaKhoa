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

public class DoanhThuController {

    private DoanhThuUI view;
    private Connection conn;
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");

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
    public void loadDoanhThuData() {
        DefaultTableModel modelDoanhThu = view.getModelDoanhThu();
        modelDoanhThu.setRowCount(0);
        double totalRevenue = 0;
        String sql = "SELECT dt.idDoanhThu, dt.idHoaDon, bn.hoTen, dt.thangNam, hd.tongTien, hd.trangThai " +
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
                double tongDoanhThu = resultSet.getDouble("tongTien"); // Lấy tongTien từ bảng HoaDon
                String trangThai = resultSet.getString("trangThai");
                modelDoanhThu.addRow(new Object[]{idDoanhThu, idHoaDon, hoTenBenhNhan, monthYearFormat.format(thangNam), tongDoanhThu, trangThai});
                totalRevenue += tongDoanhThu;
            }
            // Thêm hàng tổng vào cuối
            modelDoanhThu.addRow(new Object[]{null, null, null, "Tổng:", totalRevenue, null});
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi truy vấn dữ liệu doanh thu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void themDoanhThu(Date thangNam, double tongDoanhThu, int idHoaDon) {
        String sql = "INSERT INTO DoanhThu (thangNam, tongDoanhThu, idHoaDon) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setDouble(2, tongDoanhThu);
            preparedStatement.setInt(3, idHoaDon);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(view, "Thêm doanh thu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDoanhThuData(); // Tải lại dữ liệu sau khi thêm
            } else {
                JOptionPane.showMessageDialog(view, "Thêm doanh thu thất bại! (Kiểm tra ID Hóa đơn)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi thêm doanh thu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void xoaDoanhThu(int idDoanhThu) {
        String sql = "DELETE FROM DoanhThu WHERE idDoanhThu = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idDoanhThu);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(view, "Xóa doanh thu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDoanhThuData(); // Tải lại dữ liệu sau khi xóa
            } else {
                JOptionPane.showMessageDialog(view, "Không tìm thấy doanh thu để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi xóa doanh thu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void suaDoanhThu(int idDoanhThu, java.util.Date thangNam, double tongDoanhThu, int idHoaDon) {
        String sql = "UPDATE DoanhThu SET thangNam = ?, tongDoanhThu = ?, idHoaDon = ? WHERE idDoanhThu = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setDouble(2, tongDoanhThu);
            preparedStatement.setInt(3, idHoaDon);
            preparedStatement.setInt(4, idDoanhThu);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(view, "Sửa doanh thu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDoanhThuData(); // Tải lại dữ liệu sau khi sửa
            } else {
                JOptionPane.showMessageDialog(view, "Không tìm thấy doanh thu để sửa! (Kiểm tra ID Hóa đơn)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi sửa doanh thu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}