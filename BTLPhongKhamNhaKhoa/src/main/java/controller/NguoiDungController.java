package controller;

import model.BacSi;
import model.NguoiDung;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
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

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            //System.out.println("Mật khẩu nhập: " + Base64.getEncoder().encodeToString(hashedBytes));

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Lỗi thuật toán mã hóa mật khẩu: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public static NguoiDung checkLoginAndGetUser(String emailOrPhone, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        NguoiDung user = null;

        try {
            connection = connectMySQL.getConnection();
            String query = "SELECT * FROM NguoiDung WHERE (Email = ? OR SoDienThoai = ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, emailOrPhone);
            statement.setString(2, emailOrPhone);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("MatKhau");

                // Kiểm tra xem mật khẩu đã được hash chưa
                // Mật khẩu hash SHA-256 base64 thường có độ dài ~44 ký tự
                boolean isHashed = storedPassword.length() > 20 && isBase64(storedPassword);

                boolean passwordMatch = false;

                if (isHashed) {
                    // Mật khẩu đã hash, so sánh bình thường
                    //System.out.println("Mật khẩu đã hash, tiến hành so sánh hash");
                    passwordMatch = storedPassword.equals(hashPassword(password));
                } else {
                    // Mật khẩu chưa hash (dữ liệu cũ), so sánh plaintext
                    //System.out.println("Mật khẩu chưa hash (dữ liệu cũ), so sánh plaintext");
                    //System.out.println("Stored password: " + storedPassword);
                    //System.out.println("Input password: " + password);
                    passwordMatch = storedPassword.equals(password);

                    // Nếu đăng nhập thành công, tự động hash và cập nhật mật khẩu
                    if (passwordMatch) {
                        //System.out.println("Đăng nhập thành công, tiến hành hash mật khẩu...");
                        updatePasswordToHashed(resultSet.getInt("IdNguoiDung"), password);
                    }
                }

                if (passwordMatch) {
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
            }

        } catch (SQLException e) {
            System.err.println("Lỗi xác thực đăng nhập: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return user;
    }

    // Helper method để kiểm tra xem string có phải base64 không
    private static boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Method để cập nhật mật khẩu từ plaintext sang hash
    private static void updatePasswordToHashed(int userId, String plainPassword) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectMySQL.getConnection();
            connection.setAutoCommit(true); // Đảm bảo auto-commit được bật

            String query = "UPDATE NguoiDung SET MatKhau = ? WHERE IdNguoiDung = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, hashPassword(plainPassword));
            statement.setInt(2, userId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                //System.out.println("Đã cập nhật mật khẩu hash cho user ID: " + userId);
            } else {
                System.out.println("Không tìm thấy user ID: " + userId + " để cập nhật");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật mật khẩu hash: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void migrateAllPlaintextPasswords() {
        Connection connection = null;
        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectMySQL.getConnection();

            // Lấy tất cả user có mật khẩu chưa hash
            String selectQuery = "SELECT IdNguoiDung, MatKhau FROM NguoiDung";
            selectStatement = connection.prepareStatement(selectQuery);
            resultSet = selectStatement.executeQuery();

            String updateQuery = "UPDATE NguoiDung SET MatKhau = ? WHERE IdNguoiDung = ?";
            updateStatement = connection.prepareStatement(updateQuery);

            int migrated = 0;

            while (resultSet.next()) {
                int userId = resultSet.getInt("IdNguoiDung");
                String currentPassword = resultSet.getString("MatKhau");

                // Kiểm tra xem mật khẩu đã hash chưa
                if (currentPassword != null && !isBase64(currentPassword)) {
                    // Mật khẩu chưa hash, tiến hành hash
                    updateStatement.setString(1, hashPassword(currentPassword));
                    updateStatement.setInt(2, userId);
                    updateStatement.executeUpdate();
                    migrated++;
                }
            }

            //System.out.println("Đã migrate " + migrated + " mật khẩu plaintext sang hash");

        } catch (SQLException e) {
            System.err.println("Lỗi migrate mật khẩu: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (selectStatement != null) selectStatement.close();
                if (updateStatement != null) updateStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // GIẢI PHÁP 3: Cập nhật method checkLogin trong QLUser
    public static boolean checkLogin(String email, String password) {
        String sql = "SELECT MatKhau FROM NguoiDung WHERE email = ?";

        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("MatKhau");

                // Kiểm tra xem mật khẩu đã hash chưa
                boolean isHashed = storedPassword.length() > 20 && isBase64(storedPassword);

                if (isHashed) {
                    // So sánh mật khẩu hash
                    return storedPassword.equals(NguoiDungController.hashPassword(password));
                } else {
                    // So sánh mật khẩu plaintext (dữ liệu cũ)
                    boolean match = storedPassword.equals(password);

                    // Nếu match, tự động hash mật khẩu
                    if (match) {
                        updatePasswordToHashed(email, password);
                    }

                    return match;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method cho QLUser
    private static void updatePasswordToHashed(String email, String plainPassword) {
        String sql = "UPDATE NguoiDung SET MatKhau = ? WHERE email = ?";

        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, NguoiDungController.hashPassword(plainPassword));
            pstmt.setString(2, email);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Method test để cập nhật mật khẩu (có thể gọi riêng để test)
    public static boolean testUpdatePassword(int userId, String newPassword) {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean success = false;

        try {
            connection = connectMySQL.getConnection();

            // Trước khi update, kiểm tra mật khẩu hiện tại
            String selectQuery = "SELECT MatKhau FROM NguoiDung WHERE IdNguoiDung = ?";
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setInt(1, userId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String currentPassword = rs.getString("MatKhau");
                System.out.println("Mật khẩu hiện tại: " + currentPassword);
            }
            rs.close();
            selectStmt.close();

            // Thực hiện update
            String updateQuery = "UPDATE NguoiDung SET MatKhau = ? WHERE IdNguoiDung = ?";
            statement = connection.prepareStatement(updateQuery);
            String hashedPassword = hashPassword(newPassword);
            System.out.println("Mật khẩu sẽ update thành: " + hashedPassword);

            statement.setString(1, hashedPassword);
            statement.setInt(2, userId);

            int rowsUpdated = statement.executeUpdate();
            success = rowsUpdated > 0;

            System.out.println("Số dòng được update: " + rowsUpdated);

            // Kiểm tra lại sau khi update
            selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setInt(1, userId);
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                String updatedPassword = rs.getString("MatKhau");
                System.out.println("Mật khẩu sau khi update: " + updatedPassword);
            }
            rs.close();
            selectStmt.close();

        } catch (SQLException e) {
            System.err.println("Lỗi test update mật khẩu: " + e.getMessage());
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
            statement.setString(4, hashPassword(nguoiDung.getMatKhau()));
            statement.setDate(5, nguoiDung.getNgaySinh());
            statement.setString(6, nguoiDung.getGioiTinh());
            String vaiTroDB = (nguoiDung.getVaiTro() != null && nguoiDung.getVaiTro().equals("Người dùng")) ? null : nguoiDung.getVaiTro();
            statement.setString(7, vaiTroDB);

            int rowsInserted = statement.executeUpdate();
            success = (rowsInserted > 0);

        } catch (SQLException e) {
            //System.err.println("Lỗi đăng ký người dùng: " + e.getMessage());
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
                statement.setString(4, hashPassword(user.getMatKhau()));
                String vaiTroDB = (user.getVaiTro() != null && user.getVaiTro().equals("Người dùng")) ? null : user.getVaiTro();
                statement.setString(5, vaiTroDB);
                statement.setInt(6, user.getIdNguoiDung());
            } else {
                // Không thay đổi mật khẩu
                String query = "UPDATE NguoiDung SET HoTen = ?, Email = ?, " +
                              "SoDienThoai = ?, VaiTro = ? WHERE IdNguoiDung = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, user.getHoTen());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getSoDienThoai());
                String vaiTroDB = (user.getVaiTro() != null && user.getVaiTro().equals("Người dùng")) ? null : user.getVaiTro();
                statement.setString(4, vaiTroDB);
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
            statement.setString(4, hashPassword(user.getMatKhau()));
            statement.setDate(5, user.getNgaySinh());
            statement.setString(6, user.getGioiTinh());
            String vaiTroDB = (user.getVaiTro() != null && user.getVaiTro().equals("Người dùng")) ? null : user.getVaiTro();
            statement.setString(7, vaiTroDB);

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
            String gender = "Nam"; // Default gender to "Nam"
            Date birthDate = null;     // Default birth date (null)
            String role = null;      // Default role to null

            String query = "INSERT INTO NguoiDung (HoTen, Email, SoDienThoai, MatKhau, NgaySinh, GioiTinh, VaiTro) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, hashPassword(password));
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