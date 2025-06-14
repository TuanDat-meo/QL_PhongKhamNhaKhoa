package controller;

import model.BacSi;
import model.NguoiDung;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;

import java.sql.Date;

import connect.connectMySQL;

public class NguoiDungController {
    private static Connection connection;

    public NguoiDungController() {
        try {
            this.connection = connectMySQL.getConnection();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối đến database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static NguoiDung checkLoginAndGetUser(String emailOrPhone, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        NguoiDung user = null;

        try {
            connection = connectMySQL.getConnection();
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
    public java.util.List<NguoiDung> getAllUsers() throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        java.util.List<NguoiDung> userList = new java.util.ArrayList<>();

        try {
            String query = "SELECT * FROM NguoiDung ORDER BY IdNguoiDung DESC";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                NguoiDung user = new NguoiDung();
                user.setIdNguoiDung(resultSet.getInt("IdNguoiDung"));
                user.setHoTen(resultSet.getString("HoTen"));
                user.setEmail(resultSet.getString("Email"));
                user.setSoDienThoai(resultSet.getString("SoDienThoai"));
                user.setMatKhau(resultSet.getString("MatKhau"));
                user.setNgaySinh(resultSet.getDate("NgaySinh"));
                user.setGioiTinh(resultSet.getString("GioiTinh"));
                user.setVaiTro(resultSet.getString("VaiTro"));
                userList.add(user);
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        
        return userList;
    }
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
            statement.setString(7, nguoiDung.getVaiTro() != null ? nguoiDung.getVaiTro() : null); // Mặc định là USER

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
    public void updateUser(NguoiDung user) throws SQLException {
        PreparedStatement statement = null;

        try {
            // Nếu có thay đổi mật khẩu
            if (user.getMatKhau() != null && !user.getMatKhau().isEmpty()) {
                String query = "UPDATE NguoiDung SET HoTen = ?, Email = ?, " +
                              "SoDienThoai = ?, MatKhau = ?, VaiTro = ? WHERE IdNguoiDung = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, user.getHoTen());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getSoDienThoai());
                statement.setString(4, user.getMatKhau());
                statement.setString(5, user.getVaiTro());
                statement.setInt(6, user.getIdNguoiDung());
            } else {
                // Không thay đổi mật khẩu
                String query = "UPDATE NguoiDung SET HoTen = ?, Email = ?, " +
                              "SoDienThoai = ?, VaiTro = ? WHERE IdNguoiDung = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, user.getHoTen());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getSoDienThoai());
                statement.setString(4, user.getVaiTro());
                statement.setInt(5, user.getIdNguoiDung());
            }

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated <= 0) {
                throw new SQLException("Không thể cập nhật thông tin người dùng.");
            }
        } finally {
            if (statement != null) statement.close();
        }
    }
    public void deleteUser(int userId) throws SQLException {
        Connection connection = null;
        PreparedStatement checkStatement = null;
        PreparedStatement deleteStatement = null;
        
        try {
            connection = connectMySQL.getConnection();
            connection.setAutoCommit(false); // Start transaction
            
            // 1. Check if user is linked to a doctor
            String checkDoctorQuery = "SELECT idBacSi FROM bacsi WHERE idNguoiDung = ?";
            checkStatement = connection.prepareStatement(checkDoctorQuery);
            checkStatement.setInt(1, userId);
            ResultSet rs = checkStatement.executeQuery();
            
            if (rs.next()) {
                int doctorId = rs.getInt("idBacSi");
                BacSiController bacSiController = new BacSiController();
                
                // Check if the doctor has future appointments
                if (bacSiController.hasFutureAppointments(doctorId)) {
                    // Give options to the user
                    String[] options = {"Chuyển lịch hẹn sang bác sĩ khác", "Hủy lịch hẹn", "Hủy thao tác xóa"};
                    int choice = JOptionPane.showOptionDialog(
                        null,
                        "Bác sĩ này có lịch hẹn trong tương lai. Bạn muốn:",
                        "Cảnh báo",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[2]
                    );
                    
                    if (choice == 0) { // Reassign appointments
                        // Get doctors with the same specialty
                        List<BacSi> replacementDoctors = bacSiController.getPotentialReplacementDoctors(doctorId);
                        
                        if (replacementDoctors.isEmpty()) {
                            connection.rollback();
                            throw new SQLException("Không tìm thấy bác sĩ thay thế cùng chuyên khoa");
                        }
                        
                        // Create a dialog to select replacement doctor
                        BacSi[] doctorsArray = replacementDoctors.toArray(new BacSi[0]);
                        String[] doctorNames = new String[doctorsArray.length];
                        for (int i = 0; i < doctorsArray.length; i++) {
                            doctorNames[i] = doctorsArray[i].getHoTenBacSi() + " - " + doctorsArray[i].getChuyenKhoa();
                        }
                        
                        String selectedDoctor = (String) JOptionPane.showInputDialog(
                            null,
                            "Chọn bác sĩ thay thế:",
                            "Chuyển lịch hẹn",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            doctorNames,
                            doctorNames[0]
                        );
                        
                        if (selectedDoctor == null) {
                            connection.rollback();
                            return; // User cancelled
                        }
                        
                        // Find the selected doctor's ID
                        int replacementDoctorId = -1;
                        for (int i = 0; i < doctorNames.length; i++) {
                            if (doctorNames[i].equals(selectedDoctor)) {
                                replacementDoctorId = doctorsArray[i].getIdBacSi();
                                break;
                            }
                        }
                        
                        // Reassign appointments, treatments, prescriptions
                        bacSiController.reassignFutureAppointments(doctorId, replacementDoctorId);
                        bacSiController.reassignAllTreatments(doctorId, replacementDoctorId);
                        bacSiController.reassignAllPrescriptions(doctorId, replacementDoctorId);
                    } 
                    else if (choice == 1) { // Cancel appointments
                        // Ask for confirmation before canceling appointments
                        int confirmCancel = JOptionPane.showConfirmDialog(
                            null,
                            "Bạn có chắc muốn hủy tất cả lịch hẹn trong tương lai không?",
                            "Xác nhận hủy lịch hẹn",
                            JOptionPane.YES_NO_OPTION
                        );
                        
                        if (confirmCancel != JOptionPane.YES_OPTION) {
                            connection.rollback();
                            return; // User cancelled
                        }
                        
                        // Use deleteBacSi which will handle canceling future appointments
                    } 
                    else {
                        connection.rollback();
                        return; // User cancelled deletion
                    }
                }
                
                // Now handle the doctor deletion
                if (!bacSiController.deleteBacSi(doctorId)) {
                    connection.rollback();
                    throw new SQLException("Không thể xóa thông tin bác sĩ liên quan");
                }
            }
            
            // Now proceed with user deletion
            String deleteQuery = "DELETE FROM nguoidung WHERE idNguoiDung = ?";
            deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, userId);
            int rowsDeleted = deleteStatement.executeUpdate();
            
            if (rowsDeleted <= 0) {
                connection.rollback();
                throw new SQLException("Không thể xóa người dùng với ID: " + userId);
            }
            
            // Commit if everything is successful
            connection.commit();
            JOptionPane.showMessageDialog(null, "Đã xóa người dùng thành công");
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Re-throw the exception
        } finally {
            if (checkStatement != null) checkStatement.close();
            if (deleteStatement != null) deleteStatement.close();
            if (connection != null) {
                connection.setAutoCommit(true); // Reset auto-commit
                connection.close();
            }
        }
    }
    public java.util.List<String> getAllRoles() throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        java.util.List<String> roleList = new java.util.ArrayList<>();

        try {
            String query = "SELECT DISTINCT vaiTro FROM NguoiDung ORDER BY vaiTro";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String role = resultSet.getString("vaiTro");
                if (role != null && !role.isEmpty()) {
                    roleList.add(role);
                }
            }
            
            // Nếu danh sách trống, thêm các vai trò mặc định
            if (roleList.isEmpty()) {
                roleList.add("Admin");
                roleList.add("Nhân viên");
                roleList.add("Khách hàng");
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        
        return roleList;
    }
    public java.util.List<NguoiDung> searchUsers(String keyword) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        java.util.List<NguoiDung> userList = new java.util.ArrayList<>();

        try {
            String query = "SELECT * FROM NguoiDung WHERE HoTen LIKE ? OR Email LIKE ? OR SoDienThoai LIKE ?";
            statement = connection.prepareStatement(query);
            String searchPattern = "%" + keyword + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                NguoiDung user = new NguoiDung();
                user.setIdNguoiDung(resultSet.getInt("IdNguoiDung"));
                user.setHoTen(resultSet.getString("HoTen"));
                user.setEmail(resultSet.getString("Email"));
                user.setSoDienThoai(resultSet.getString("SoDienThoai"));
                user.setMatKhau(resultSet.getString("MatKhau"));
                user.setNgaySinh(resultSet.getDate("NgaySinh"));
                user.setGioiTinh(resultSet.getString("GioiTinh"));
                user.setVaiTro(resultSet.getString("VaiTro"));
                userList.add(user);
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        
        return userList;
    }
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
    public static boolean isPhoneExists(String phone) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean exists = false;

        try {
            connection = connectMySQL.getConnection();
            String query = "SELECT COUNT(*) FROM NguoiDung WHERE SoDienThoai = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, phone);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking phone existence: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return exists;
    }
    
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
    public void addUser(NguoiDung user) throws SQLException {
        PreparedStatement statement = null;

        try {
            // Kiểm tra xem email hoặc số điện thoại đã tồn tại chưa
            if (isEmailExists(user.getEmail()) || isPhoneExists(user.getSoDienThoai())) {
                throw new SQLException("Email hoặc số điện thoại đã tồn tại trong hệ thống.");
            }

            String query = "INSERT INTO NguoiDung (HoTen, Email, SoDienThoai, MatKhau, NgaySinh, GioiTinh, VaiTro) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, user.getHoTen());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getSoDienThoai());
            statement.setString(4, user.getMatKhau());
            statement.setDate(5, user.getNgaySinh());
            statement.setString(6, user.getGioiTinh());
            statement.setString(7, user.getVaiTro());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted <= 0) {
                throw new SQLException("Không thể thêm người dùng mới.");
            }
        } finally {
            if (statement != null) statement.close();
        }
    }
    public static boolean registerUser(String name, String email, String phone, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean success = false;

        try {
            connection = connectMySQL.getConnection();
            
            // Default values for new user
            String gender = "Unknown"; // Default gender
            Date birthDate = null;     // Default birth date (null)
            String role = null;      // Default role
            
            String query = "INSERT INTO NguoiDung (HoTen, Email, SoDienThoai, MatKhau, NgaySinh, GioiTinh, VaiTro) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, password);
            statement.setDate(5, birthDate);
            statement.setString(6, gender);
            statement.setString(7, role);

            int rowsInserted = statement.executeUpdate();
            success = (rowsInserted > 0);
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return success;
    }
    public static boolean isEmailExists(String email) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean exists = false;

        try {
            connection = connectMySQL.getConnection();
            String query = "SELECT COUNT(*) FROM NguoiDung WHERE Email = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return exists;
    }
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