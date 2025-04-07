package controller;

import model.NhaCungCap;
import connect.connectMySQL; // Lớp kết nối MySQL của bạn

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapController {

    private Connection conn;

    public NhaCungCapController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi kết nối đến cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            // Có thể có logic xử lý khác ở đây, ví dụ: tắt ứng dụng
        }
    }

    public List<NhaCungCap> layDanhSachNhaCungCap() {
        List<NhaCungCap> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getString("idNCC")); // Sửa ở đây
                ncc.setTenNCC(rs.getString("TenNCC"));
                ncc.setDiaChi(rs.getString("DiaChi"));
                ncc.setSoDienThoai(rs.getString("SoDienThoai"));
                danhSach.add(ncc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi truy vấn danh sách nhà cung cấp từ CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return danhSach;
    }

    public String themNhaCungCap(NhaCungCap ncc) {
        String sql = "INSERT INTO NhaCungCap (TenNCC, DiaChi, SoDienThoai) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ncc.getTenNCC());
            pstmt.setString(2, ncc.getDiaChi());
            pstmt.setString(3, ncc.getSoDienThoai());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getString(1); // Lấy ID vừa được tạo
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thêm nhà cung cấp vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return null; // Trả về null nếu thêm thất bại
    }

    public boolean suaNhaCungCap(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap SET TenNCC = ?, DiaChi = ?, SoDienThoai = ? WHERE MaNCC = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ncc.getTenNCC());
            pstmt.setString(2, ncc.getDiaChi());
            pstmt.setString(3, ncc.getSoDienThoai());
            pstmt.setString(4, ncc.getMaNCC());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật thông tin nhà cung cấp trong CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

 // Trong NhaCungCapController.java
    public boolean xoaNhaCungCap(String maNCC) {
    	String sql = "DELETE FROM NhaCungCap WHERE idNCC = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maNCC);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa nhà cung cấp khỏi CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    // Phương thức đóng kết nối (nên được gọi khi không còn sử dụng Controller nữa)
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi đóng kết nối cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public List<NhaCungCap> getAllNhaCungCap() {
        List<NhaCungCap> danhSach = new ArrayList<>();
        String sql = "SELECT idNCC, TenNCC, DiaChi, SoDienThoai FROM NhaCungCap";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("TenNCC"));
                ncc.setDiaChi(rs.getString("DiaChi"));
                ncc.setSoDienThoai(rs.getString("SoDienThoai"));
                danhSach.add(ncc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi truy vấn danh sách nhà cung cấp từ CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return danhSach;
    }
}