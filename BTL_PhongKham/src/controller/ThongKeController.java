package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connect.connectMySQL;

public class ThongKeController {
    private Connection conn;

    public ThongKeController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Thống kê số lượng bệnh nhân
    public int demSoBenhNhan() {
        String sql = "SELECT COUNT(*) FROM BenhNhan";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đếm số lượng bệnh nhân: " + e.getMessage());
        }
        return 0;
    }

    // 2. Thống kê số lượng lịch hẹn
    public int demSoLichHen() {
        String sql = "SELECT COUNT(*) FROM LichHen";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đếm số lượng lịch hẹn: " + e.getMessage());
        }
        return 0;
    }

    // 3. Thống kê số lượng đơn thuốc
    public int demSoDonThuoc() {
        String sql = "SELECT COUNT(*) FROM DonThuoc";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đếm số lượng đơn thuốc: " + e.getMessage());
        }
        return 0;
    }

    // 4. Hiển thị thống kê
    public void hienThiThongKe() {
        int soBenhNhan = demSoBenhNhan();
        int soLichHen = demSoLichHen();
        int soDonThuoc = demSoDonThuoc();

        System.out.println("📊 Thống kê hệ thống:");
        System.out.println("- Số lượng bệnh nhân: " + soBenhNhan);
        System.out.println("- Số lượng lịch hẹn: " + soLichHen);
        System.out.println("- Số lượng đơn thuốc: " + soDonThuoc);
    }
}
