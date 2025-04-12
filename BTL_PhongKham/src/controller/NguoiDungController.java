package controller;

import model.NguoiDung;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import connect.connectMySQL;

public class NguoiDungController {
    private Connection connection;

    public NguoiDungController() {
        try {
            this.connection = connectMySQL.getConnection();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối đến database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra thông tin đăng nhập và trả về đối tượng NguoiDung nếu đăng nhập thành công
     * 
     * @param emailOrPhone Email hoặc số điện thoại của người dùng
     * @param password Mật khẩu của người dùng
     * @return Đối tượng NguoiDung nếu đăng nhập thành công, null nếu thất bại
     */
    public static NguoiDung checkLoginAndGetUser(String emailOrPhone, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        NguoiDung user = null;

        try {
            connection = connectMySQL.getConnection();
            
            // Truy vấn kiểm tra đăng nhập bằng email hoặc số điện thoại
            String query = "SELECT * FROM NguoiDung WHERE (Email = ? OR SoDienThoai = ?) AND MatKhau = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, emailOrPhone);
            statement.setString(2, emailOrPhone);
            statement.setString(3, password);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                user = new NguoiDung();
                user.setIdNguoiDung(resultSet.getInt("IdNguoiDung"));
                user.setHoTen(resultSet.getString("HoTen"));
                user.setEmail(resultSet.getString("Email"));
                user.setSoDienThoai(resultSet.getString("SoDienThoai"));
                user.setMatKhau(resultSet.getString("MatKhau"));
                user.setNgaySinh(resultSet.getDate("NgaySinh"));
                user.setGioiTinh(resultSet.getString("GioiTinh"));
                user.setVaiTro(resultSet.getString("VaiTro"));
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi xác thực đăng nhập: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }
        
        return user;
    }

    /**
     * Lấy thông tin người dùng theo ID
     * 
     * @param userId ID của người dùng cần lấy thông tin
     * @return Đối tượng NguoiDung chứa thông tin người dùng, null nếu không tìm thấy
     * @throws SQLException Nếu có lỗi truy vấn SQL
     */
    public NguoiDung getNguoiDungById(int userId) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        NguoiDung user = null;

        try {
            String query = "SELECT * FROM NguoiDung WHERE IdNguoiDung = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                user = new NguoiDung();
                user.setIdNguoiDung(resultSet.getInt("IdNguoiDung"));
                user.setHoTen(resultSet.getString("HoTen"));
                user.setEmail(resultSet.getString("Email"));
                user.setSoDienThoai(resultSet.getString("SoDienThoai"));
                user.setMatKhau(resultSet.getString("MatKhau"));
                user.setNgaySinh(resultSet.getDate("NgaySinh"));
                user.setGioiTinh(resultSet.getString("GioiTinh"));
                user.setVaiTro(resultSet.getString("VaiTro"));
            }
            
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        
        return user;
    }

    /**
     * Đăng ký người dùng mới
     * 
     * @param nguoiDung Đối tượng NguoiDung chứa thông tin đăng ký
     * @return true nếu đăng ký thành công, false nếu thất bại
     */
    public boolean registerUser(NguoiDung nguoiDung) {
        PreparedStatement statement = null;
        boolean success = false;

        try {
            // Kiểm tra xem email hoặc số điện thoại đã tồn tại chưa
            if (isEmailExists(nguoiDung.getEmail()) || isPhoneExists(nguoiDung.getSoDienThoai())) {
                return false;
            }

            String query = "INSERT INTO NguoiDung (HoTen, Email, SoDienThoai, MatKhau, NgaySinh, GioiTinh, VaiTro) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, nguoiDung.getHoTen());
            statement.setString(2, nguoiDung.getEmail());
            statement.setString(3, nguoiDung.getSoDienThoai());
            statement.setString(4, nguoiDung.getMatKhau());
            statement.setDate(5, nguoiDung.getNgaySinh());
            statement.setString(6, nguoiDung.getGioiTinh());
            statement.setString(7, nguoiDung.getVaiTro() != null ? nguoiDung.getVaiTro() : "USER"); // Mặc định là USER

            int rowsInserted = statement.executeUpdate();
            success = (rowsInserted > 0);
            
        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return success;
    }

    /**
     * Cập nhật thông tin người dùng
     * 
     * @param nguoiDung Đối tượng NguoiDung chứa thông tin cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateUser(NguoiDung nguoiDung) {
        PreparedStatement statement = null;
        boolean success = false;

        try {
            String query = "UPDATE NguoiDung SET HoTen = ?, Email = ?, SoDienThoai = ?, " +
                          "NgaySinh = ?, GioiTinh = ?, VaiTro = ? WHERE IdNguoiDung = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, nguoiDung.getHoTen());
            statement.setString(2, nguoiDung.getEmail());
            statement.setString(3, nguoiDung.getSoDienThoai());
            statement.setDate(4, nguoiDung.getNgaySinh());
            statement.setString(5, nguoiDung.getGioiTinh());
            statement.setString(6, nguoiDung.getVaiTro());
            statement.setInt(7, nguoiDung.getIdNguoiDung());

            int rowsUpdated = statement.executeUpdate();
            success = (rowsUpdated > 0);
            
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return success;
    }

    /**
     * Cập nhật mật khẩu người dùng
     * 
     * @param userId ID của người dùng cần đổi mật khẩu
     * @param newPassword Mật khẩu mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updatePassword(int userId, String newPassword) {
        PreparedStatement statement = null;
        boolean success = false;

        try {
            String query = "UPDATE NguoiDung SET MatKhau = ? WHERE IdNguoiDung = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, newPassword);
            statement.setInt(2, userId);

            int rowsUpdated = statement.executeUpdate();
            success = (rowsUpdated > 0);
            
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật mật khẩu: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return success;
    }

    /**
     * Kiểm tra xem email đã tồn tại trong hệ thống chưa
     * 
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa
     * @throws SQLException Nếu có lỗi truy vấn SQL
     */
    public boolean isEmailExists(String email) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean exists = false;

        try {
            String query = "SELECT COUNT(*) FROM NguoiDung WHERE Email = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
            
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        
        return exists;
    }

    /**
     * Kiểm tra xem số điện thoại đã tồn tại trong hệ thống chưa
     * 
     * @param phone Số điện thoại cần kiểm tra
     * @return true nếu số điện thoại đã tồn tại, false nếu chưa
     * @throws SQLException Nếu có lỗi truy vấn SQL
     */
    public boolean isPhoneExists(String phone) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean exists = false;

        try {
            String query = "SELECT COUNT(*) FROM NguoiDung WHERE SoDienThoai = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, phone);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
            
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        
        return exists;
    }

    /**
     * Lấy thông tin người dùng thông qua email hoặc số điện thoại
     * 
     * @param emailOrPhone Email hoặc số điện thoại của người dùng
     * @return Đối tượng NguoiDung nếu tìm thấy, null nếu không tìm thấy
     */
    public NguoiDung getNguoiDungByEmailOrPhone(String emailOrPhone) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        NguoiDung user = null;

        try {
            String query = "SELECT * FROM NguoiDung WHERE Email = ? OR SoDienThoai = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, emailOrPhone);
            statement.setString(2, emailOrPhone);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                user = new NguoiDung();
                user.setIdNguoiDung(resultSet.getInt("IdNguoiDung"));
                user.setHoTen(resultSet.getString("HoTen"));
                user.setEmail(resultSet.getString("Email"));
                user.setSoDienThoai(resultSet.getString("SoDienThoai"));
                user.setMatKhau(resultSet.getString("MatKhau"));
                user.setNgaySinh(resultSet.getDate("NgaySinh"));
                user.setGioiTinh(resultSet.getString("GioiTinh"));
                user.setVaiTro(resultSet.getString("VaiTro"));
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return user;
    }

    /**
     * Đóng các tài nguyên database
     */
    private static void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            // Không đóng connection ở đây vì nó được quản lý bởi DatabaseConnection
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}