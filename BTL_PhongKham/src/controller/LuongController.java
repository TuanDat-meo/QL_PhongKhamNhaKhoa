package controller;

import connect.connectMySQL;
import model.Luong;
import view.LuongUI; // Thay DoanhThuUI bằng LuongUI

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class LuongController {

    private LuongUI view; // Thay DoanhThuUI bằng LuongUI
    private Connection conn;

    public LuongController(LuongUI view) { // Thay DoanhThuUI bằng LuongUI
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

    public void loadLuongData() {
        DefaultTableModel modelLuong = view.getModelLuong();
        modelLuong.setRowCount(0);
        double totalLuongCoBan = 0;
        double totalThuong = 0;
        double totalKhauTru = 0;
        double totalTongLuong = 0;

        String sql = "SELECT ln.idLuong, nd.hoTen, ln.thangNam, ln.luongCoBan, ln.thuong, ln.khauTru, ln.tongLuong " +
                     "FROM LuongNhanVien ln " +
                     "JOIN NguoiDung nd ON ln.idNguoiDung = nd.idNguoiDung";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int idLuong = resultSet.getInt("idLuong");
                String hoTen = resultSet.getString("hoTen");
                Date thangNam = resultSet.getDate("thangNam");
                double luongCoBan = resultSet.getDouble("luongCoBan");
                double thuong = resultSet.getDouble("thuong");
                double khauTru = resultSet.getDouble("khauTru");
                double tongLuong = resultSet.getDouble("tongLuong");

                modelLuong.addRow(new Object[]{idLuong, hoTen, thangNam, luongCoBan, thuong, khauTru, tongLuong});

                totalLuongCoBan += luongCoBan;
                totalThuong += thuong;
                totalKhauTru += khauTru;
                totalTongLuong += tongLuong;
            }
            // Thêm hàng tổng vào cuối
            modelLuong.addRow(new Object[]{null, "Tổng:", null, totalLuongCoBan, totalThuong, totalKhauTru, totalTongLuong});

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi truy vấn dữ liệu lương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void themLuong(int idNguoiDung, Date thangNam, double luongCoBan, double thuong, double khauTru) {
        String sql = "INSERT INTO LuongNhanVien (idNguoiDung, thangNam, luongCoBan, thuong, khauTru, tongLuong) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idNguoiDung);
            preparedStatement.setDate(2, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setDouble(3, luongCoBan);
            preparedStatement.setDouble(4, thuong);
            preparedStatement.setDouble(5, khauTru);
            // Tính toán tổng lương
            double tongLuong = luongCoBan + thuong - khauTru;
            preparedStatement.setDouble(6, tongLuong);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(view, "Thêm thông tin lương thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadLuongData(); // Tải lại dữ liệu sau khi thêm
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thông tin lương thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi thêm thông tin lương: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void xoaLuong(int idLuong) {
        String sql = "DELETE FROM LuongNhanVien WHERE idLuong = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idLuong);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(view, "Xóa thông tin lương thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadLuongData(); // Tải lại dữ liệu sau khi xóa
            } else {
                JOptionPane.showMessageDialog(view, "Không tìm thấy thông tin lương để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi xóa thông tin lương: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void suaLuong(int idLuong, int idNguoiDung, Date thangNam, double luongCoBan, double thuong, double khauTru) {
        String sql = "UPDATE LuongNhanVien SET idNguoiDung = ?, thangNam = ?, luongCoBan = ?, thuong = ?, khauTru = ?, tongLuong = ? WHERE idLuong = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idNguoiDung);
            preparedStatement.setDate(2, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setDouble(3, luongCoBan);
            preparedStatement.setDouble(4, thuong);
            preparedStatement.setDouble(5, khauTru);
            // Tính toán tổng lương
            double tongLuong = luongCoBan + thuong - khauTru;
            preparedStatement.setDouble(6, tongLuong);
            preparedStatement.setInt(7, idLuong);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(view, "Sửa thông tin lương thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadLuongData(); // Tải lại dữ liệu sau khi sửa
            } else {
                JOptionPane.showMessageDialog(view, "Không tìm thấy thông tin lương để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi sửa thông tin lương: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}