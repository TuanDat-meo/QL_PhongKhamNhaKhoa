package controller;

import java.sql.*;

import connect.connectMySQL;

public class HoaDonController {
    private Connection conn;

    public HoaDonController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Hiển thị hóa đơn của bệnh nhân
    public void hienThiHoaDon(int idBenhNhan) {
        String sql = "SELECT idHoaDon, ngayTao, tongTien, trangThai FROM HoaDon WHERE idBenhNhan = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBenhNhan);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Hóa đơn của bệnh nhân ID " + idBenhNhan + ":");
            while (rs.next()) {
                int idHoaDon = rs.getInt("idHoaDon");
                Date ngayTao = rs.getDate("ngayTao");
                double tongTien = rs.getDouble("tongTien");
                String trangThai = rs.getString("trangThai");

                System.out.println(" - Hóa đơn #" + idHoaDon + " | Ngày tạo: " + ngayTao + " | Tổng tiền: " + tongTien + " | Trạng thái: " + trangThai);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn: " + e.getMessage());
        }
    }

    // 2. Cập nhật tổng tiền hóa đơn sau khi áp dụng BHYT
    public void apDungBaoHiem(int idHoaDon, int idBenhNhan) {
        String getHoaDonSQL = "SELECT tongTien FROM HoaDon WHERE idHoaDon = ?";
        String getBHYTSQL = "SELECT mucHoTro FROM BaoHiemYTe WHERE idBenhNhan = ? AND trangThai = 'ConHieuLuc'";
        String updateHoaDonSQL = "UPDATE HoaDon SET tongTien = ? WHERE idHoaDon = ?";

        try (PreparedStatement stmtHoaDon = conn.prepareStatement(getHoaDonSQL);
             PreparedStatement stmtBHYT = conn.prepareStatement(getBHYTSQL);
             PreparedStatement stmtUpdate = conn.prepareStatement(updateHoaDonSQL)) {

            // Lấy tổng tiền của hóa đơn
            stmtHoaDon.setInt(1, idHoaDon);
            ResultSet rsHoaDon = stmtHoaDon.executeQuery();
            if (!rsHoaDon.next()) {
                System.out.println("Hóa đơn không tồn tại.");
                return;
            }
            double tongTien = rsHoaDon.getDouble("tongTien");

            // Kiểm tra bảo hiểm y tế
            stmtBHYT.setInt(1, idBenhNhan);
            ResultSet rsBHYT = stmtBHYT.executeQuery();
            if (rsBHYT.next()) {
                double mucHoTro = rsBHYT.getDouble("mucHoTro");
                double tienSauBHYT = tongTien - (tongTien * mucHoTro / 100);

                // Cập nhật tổng tiền sau khi áp dụng BHYT
                stmtUpdate.setDouble(1, tienSauBHYT);
                stmtUpdate.setInt(2, idHoaDon);
                stmtUpdate.executeUpdate();

                System.out.println("Bảo hiểm đã áp dụng! Tổng tiền giảm từ " + tongTien + " xuống " + tienSauBHYT);
            } else {
                System.out.println("Không có bảo hiểm hợp lệ, giữ nguyên tổng tiền: " + tongTien);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi áp dụng bảo hiểm: " + e.getMessage());
        }
    }
}
