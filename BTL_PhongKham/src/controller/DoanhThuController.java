package controller;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import connect.connectMySQL;

public class DoanhThuController {
    private Connection conn;

    public DoanhThuController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Cập nhật tổng doanh thu hàng tháng
    public void capNhatDoanhThu(int thang, int nam) {
        String sql = "SELECT SUM(tongTien) AS tong FROM HoaDon WHERE MONTH(ngayTao) = ? AND YEAR(ngayTao) = ?";
        String insertSql = "INSERT INTO DoanhThu (thangNam, tongDoanhThu) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double tongDoanhThu = rs.getDouble("tong");
                if (tongDoanhThu > 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setDate(1, Date.valueOf(nam + "-" + thang + "-01"));
                        insertStmt.setDouble(2, tongDoanhThu);
                        insertStmt.executeUpdate();
                    }
                    System.out.println("✅ Đã cập nhật doanh thu tháng " + thang + "/" + nam + ": " + tongDoanhThu);
                } else {
                    System.out.println("⚠️ Không có doanh thu trong tháng " + thang + "/" + nam);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật doanh thu: " + e.getMessage());
        }
    }

    // 2. Hiển thị doanh thu từng tháng
    public List<Object[]> hienThiDoanhThu() {  // ĐẢM BẢO TRẢ VỀ List<Object[]>
        List<Object[]> dataList = new ArrayList<>();
        String sql = "SELECT thangNam, tongDoanhThu FROM DoanhThu ORDER BY thangNam DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Date thangNam = rs.getDate("thangNam");
                double tongDoanhThu = rs.getDouble("tongDoanhThu");

                // Thêm vào danh sách
                dataList.add(new Object[]{thangNam.toString(), tongDoanhThu});
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy doanh thu: " + e.getMessage());
        }
        return dataList;  // TRẢ VỀ danh sách thay vì void
    }
}
