package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connect.connectMySQL;

public class QLUser {
	public static boolean checkLogin(String email, String password) {
        String sql = "SELECT * FROM NguoiDung WHERE email = ? AND matKhau = ?";

        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, NguoiDungController.hashPassword(password));
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Nếu có kết quả, trả về true (đăng nhập thành công)

        } catch (SQLException e) {
            e.printStackTrace(); // Chỉ in lỗi nếu có lỗi kết nối database
        }
        return false;
    }
    
    public static boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM NguoiDung WHERE email = ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM NguoiDung WHERE soDienThoai = ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}