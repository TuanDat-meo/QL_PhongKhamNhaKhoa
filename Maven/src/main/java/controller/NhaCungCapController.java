package controller;

import model.NhaCungCap;
import connect.connectMySQL;

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
        }
    }

    public List<NhaCungCap> layDanhSachNhaCungCap() {
        List<NhaCungCap> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap";
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
                        return generatedKeys.getString(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thêm nhà cung cấp vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public boolean suaNhaCungCap(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap SET TenNCC = ?, DiaChi = ?, SoDienThoai = ? WHERE idNCC = ?";
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

    public boolean xoaNhaCungCap(String maNCC) {
        // Kiểm tra có vật tư nào liên quan đến nhà cung cấp này không
        String checkQuery = "SELECT COUNT(*) FROM KhoVatTu WHERE idNCC = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, maNCC);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Có vật tư liên quan, không thể xóa, chỉ có thể đánh dấu là ngừng cung cấp
                String getNccQuery = "SELECT TenNCC FROM NhaCungCap WHERE idNCC = ?";
                try (PreparedStatement getNccStmt = conn.prepareStatement(getNccQuery)) {
                    getNccStmt.setString(1, maNCC);
                    ResultSet nccRs = getNccStmt.executeQuery();
                    
                    if (nccRs.next()) {
                        String tenNCC = nccRs.getString("TenNCC");
                        String newTenNCC = tenNCC + " (ngừng cung cấp)";
                        
                        // Cập nhật tên nhà cung cấp để đánh dấu
                        String updateNccQuery = "UPDATE NhaCungCap SET TenNCC = ? WHERE idNCC = ?";
                        try (PreparedStatement updateNccStmt = conn.prepareStatement(updateNccQuery)) {
                            updateNccStmt.setString(1, newTenNCC);
                            updateNccStmt.setString(2, maNCC);
                            boolean success = updateNccStmt.executeUpdate() > 0;
                            
                            if (success) {
                                JOptionPane.showMessageDialog(
                                    null,
                                    "Nhà cung cấp đã được đánh dấu là ngừng cung cấp.",
                                    "Thành công",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                            }
                            return success;
                        }
                    }
                }
            } else {
                // Không có vật tư liên quan, tiến hành xóa nhà cung cấp bình thường
                String sql = "DELETE FROM NhaCungCap WHERE idNCC = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, maNCC);
                    boolean success = pstmt.executeUpdate() > 0;
                    if (success) {
                        JOptionPane.showMessageDialog(
                            null,
                            "Nhà cung cấp đã được xóa thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    return success;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMessage = "Lỗi khi xóa nhà cung cấp khỏi CSDL!";
            
            // Kiểm tra lỗi khóa ngoại (foreign key constraint)
            if (e.getErrorCode() == 1451) {
                errorMessage = "Không thể xóa nhà cung cấp này vì đang được sử dụng trong hệ thống!";
            }
            
            JOptionPane.showMessageDialog(null, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

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