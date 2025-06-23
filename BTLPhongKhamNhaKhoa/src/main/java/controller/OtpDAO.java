package controller;

import connect.connectMySQL;
import model.Otp;

import java.sql.*;

public class OtpDAO {
    // Thêm mới OTP
    public static boolean insertOtp(Otp otp) {
        String sql = "INSERT INTO otp (idNguoiDung, maOTP, thoiGianHetHan, daSuDung, loai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, otp.getIdNguoiDung());
            pstmt.setString(2, otp.getMaOTP());
            pstmt.setTimestamp(3, otp.getThoiGianHetHan());
            pstmt.setBoolean(4, otp.isDaSuDung());
            pstmt.setString(5, otp.getLoai());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy OTP hợp lệ (chưa hết hạn, chưa dùng)
    public static Otp getValidOtp(int idNguoiDung, String maOTP, String loai) {
        String sql = "SELECT * FROM otp WHERE idNguoiDung = ? AND maOTP = ? AND daSuDung = 0 AND loai = ? AND thoiGianHetHan > NOW()";
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idNguoiDung);
            pstmt.setString(2, maOTP);
            pstmt.setString(3, loai);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Otp otp = new Otp();
                otp.setIdOTP(rs.getInt("idOTP"));
                otp.setIdNguoiDung(rs.getInt("idNguoiDung"));
                otp.setMaOTP(rs.getString("maOTP"));
                otp.setThoiGianHetHan(rs.getTimestamp("thoiGianHetHan"));
                otp.setDaSuDung(rs.getBoolean("daSuDung"));
                otp.setLoai(rs.getString("loai"));
                return otp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Đánh dấu OTP đã dùng
    public static boolean markOtpUsed(int idOTP) {
        String sql = "UPDATE otp SET daSuDung = 1 WHERE idOTP = ?";
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idOTP);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
} 