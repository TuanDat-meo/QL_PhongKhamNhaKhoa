package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connect.connectMySQL;

public class NhaCungCapController {
    private Connection conn;

    public NhaCungCapController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Thêm nhà cung cấp
    public boolean themNhaCungCap(String tenNCC, String diaChi, String soDienThoai) {
        String sql = "INSERT INTO NhaCungCap (tenNCC, diaChi, soDienThoai) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenNCC);
            stmt.setString(2, diaChi);
            stmt.setString(3, soDienThoai);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm nhà cung cấp: " + e.getMessage());
            return false;
        }
    }

    // 2. Lấy danh sách nhà cung cấp
    public List<String> layDanhSachNCC() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ncc = "ID: " + rs.getInt("idNCC") + ", Tên: " + rs.getString("tenNCC") +
                        ", Địa chỉ: " + rs.getString("diaChi") + ", SĐT: " + rs.getString("soDienThoai");
                list.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách nhà cung cấp: " + e.getMessage());
        }
        return list;
    }

    // 3. Cập nhật thông tin nhà cung cấp
    public boolean capNhatNCC(int idNCC, String tenNCC, String diaChi, String soDienThoai) {
        String sql = "UPDATE NhaCungCap SET tenNCC = ?, diaChi = ?, soDienThoai = ? WHERE idNCC = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenNCC);
            stmt.setString(2, diaChi);
            stmt.setString(3, soDienThoai);
            stmt.setInt(4, idNCC);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật nhà cung cấp: " + e.getMessage());
            return false;
        }
    }

    // 4. Xóa nhà cung cấp
    public boolean xoaNCC(int idNCC) {
        String sql = "DELETE FROM NhaCungCap WHERE idNCC = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idNCC);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa nhà cung cấp: " + e.getMessage());
            return false;
        }
    }
}
