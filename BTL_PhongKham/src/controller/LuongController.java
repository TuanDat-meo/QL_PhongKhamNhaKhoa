package controller;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import connect.connectMySQL;

public class LuongController {
    private Connection conn;

    public LuongController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Tính tổng lương của nhân viên
    public void tinhLuongNhanVien(int idNguoiDung, int thang, int nam, double luongCoBan, double thuong, double khauTru) {
        double tongLuong = luongCoBan + thuong - khauTru;

        String sql = "INSERT INTO LuongNhanVien (idNguoiDung, thangNam, luongCoBan, thuong, khauTru, tongLuong) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idNguoiDung);
            stmt.setDate(2, Date.valueOf(nam + "-" + thang + "-01"));
            stmt.setDouble(3, luongCoBan);
            stmt.setDouble(4, thuong);
            stmt.setDouble(5, khauTru);
            stmt.setDouble(6, tongLuong);
            stmt.executeUpdate();

            System.out.println("✅ Lương tháng " + thang + "/" + nam + " của nhân viên ID " + idNguoiDung + " đã cập nhật: " + tongLuong);
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tính lương: " + e.getMessage());
        }
    }

    // 2. Hiển thị bảng lương nhân viên
    public void hienThiLuongNhanVien() {
        String sql = "SELECT idNguoiDung, thangNam, luongCoBan, thuong, khauTru, tongLuong FROM LuongNhanVien ORDER BY thangNam DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("📋 Bảng lương nhân viên:");
            while (rs.next()) {
                int idNguoiDung = rs.getInt("idNguoiDung");
                Date thangNam = rs.getDate("thangNam");
                double luongCoBan = rs.getDouble("luongCoBan");
                double thuong = rs.getDouble("thuong");
                double khauTru = rs.getDouble("khauTru");
                double tongLuong = rs.getDouble("tongLuong");

                System.out.println("- ID: " + idNguoiDung + " | Tháng: " + thangNam + " | Lương: " + luongCoBan + " | Thưởng: " + thuong + " | Khấu trừ: " + khauTru + " | Tổng lương: " + tongLuong);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi hiển thị lương: " + e.getMessage());
        }
    }
}
