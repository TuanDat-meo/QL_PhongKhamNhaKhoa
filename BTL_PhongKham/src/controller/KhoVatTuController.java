package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connect.connectMySQL;

public class KhoVatTuController {
    private Connection conn;

    public KhoVatTuController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Thêm vật tư vào kho
    public boolean themVatTu(String tenVatTu, int soLuong, String donViTinh, int idNCC) {
        String sql = "INSERT INTO KhoVatTu (tenVatTu, soLuong, donViTinh, idNCC) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenVatTu);
            stmt.setInt(2, soLuong);
            stmt.setString(3, donViTinh);
            stmt.setInt(4, idNCC);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm vật tư: " + e.getMessage());
            return false;
        }
    }

    // 2. Lấy danh sách vật tư
    public List<String> layDanhSachVatTu() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT k.idVatTu, k.tenVatTu, k.soLuong, k.donViTinh, n.tenNCC " +
                     "FROM KhoVatTu k JOIN NhaCungCap n ON k.idNCC = n.idNCC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String vt = "ID: " + rs.getInt("idVatTu") +
                        ", Tên: " + rs.getString("tenVatTu") +
                        ", Số lượng: " + rs.getInt("soLuong") +
                        ", Đơn vị: " + rs.getString("donViTinh") +
                        ", Nhà cung cấp: " + rs.getString("tenNCC");
                list.add(vt);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách vật tư: " + e.getMessage());
        }
        return list;
    }

    // 3. Cập nhật thông tin vật tư
    public boolean capNhatVatTu(int idVatTu, String tenVatTu, int soLuong, String donViTinh, int idNCC) {
        String sql = "UPDATE KhoVatTu SET tenVatTu = ?, soLuong = ?, donViTinh = ?, idNCC = ? WHERE idVatTu = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenVatTu);
            stmt.setInt(2, soLuong);
            stmt.setString(3, donViTinh);
            stmt.setInt(4, idNCC);
            stmt.setInt(5, idVatTu);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật vật tư: " + e.getMessage());
            return false;
        }
    }

    // 4. Xóa vật tư khỏi kho
    public boolean xoaVatTu(int idVatTu) {
        String sql = "DELETE FROM KhoVatTu WHERE idVatTu = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVatTu);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa vật tư: " + e.getMessage());
            return false;
        }
    }
}
