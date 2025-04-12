package controller;

import java.sql.*;

import connect.connectMySQL;

public class ThanhToanController {
    private Connection conn;

    public ThanhToanController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Hiển thị phương thức thanh toán
    public void hienThiPhuongThucThanhToan() {
        System.out.println("Chọn phương thức thanh toán:");
        System.out.println("1 - Tiền mặt");
        System.out.println("2 - Chuyển khoản");
        System.out.println("3 - QR Code");
    }

    // 2. Xử lý thanh toán
    public void thanhToan(int idHoaDon, String hinhThuc, double soTien, String maQR) {
        String sql = "INSERT INTO ThanhToanBenhNhan (idHoaDon, soTien, hinhThucThanhToan, maQR) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHoaDon);
            stmt.setDouble(2, soTien);
            stmt.setString(3, hinhThuc);
            stmt.setString(4, maQR);

            stmt.executeUpdate();
            System.out.println("Thanh toán thành công với phương thức: " + hinhThuc);

            // Cập nhật trạng thái hóa đơn thành "Đã Thanh Toán"
            String updateHoaDon = "UPDATE HoaDon SET trangThai = 'DaThanhToan' WHERE idHoaDon = ?";
            try (PreparedStatement stmtUpdate = conn.prepareStatement(updateHoaDon)) {
                stmtUpdate.setInt(1, idHoaDon);
                stmtUpdate.executeUpdate();
                System.out.println("Cập nhật trạng thái hóa đơn: Đã thanh toán.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thanh toán: " + e.getMessage());
        }
    }

    // 3. Hiển thị mã QR khi chọn thanh toán QR Code
    public void hienThiMaQR(int idHoaDon) {
        System.out.println("⚡ Quét mã QR để thanh toán hóa đơn #" + idHoaDon);
        System.out.println("[MA QR CODE HIEN THI O DAY]");
    }
}
