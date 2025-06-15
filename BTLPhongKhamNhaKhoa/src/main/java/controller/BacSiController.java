package controller;

import model.BacSi;
import model.NguoiDung;
import model.PhongKham;
import model.LichHen;
import model.DieuTri;
import connect.connectMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BacSiController {
    
    // Lấy tất cả bác sĩ với thông tin liên quan
    public List<BacSi> getAllBacSi() {
        List<BacSi> bacSiList = new ArrayList<>();
        String sql = "SELECT bs.*, pk.tenPhong, nd.email, nd.soDienThoai " +
                     "FROM BacSi bs " +
                     "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                     "JOIN NguoiDung nd ON bs.idNguoiDung = nd.idNguoiDung " +
                     "ORDER BY bs.idBacSi DESC";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                BacSi bacSi = new BacSi();
                bacSi.setIdBacSi(rs.getInt("idBacSi"));
                bacSi.setHoTenBacSi(rs.getString("hoTenBacSi"));
                bacSi.setIdNguoiDung(rs.getInt("idNguoiDung"));
                bacSi.setIdPhongKham(rs.getInt("idPhongKham"));
                bacSi.setChuyenKhoa(rs.getString("chuyenKhoa"));
                bacSi.setBangCap(rs.getString("bangCap"));
                bacSi.setKinhNghiem(rs.getInt("kinhNghiem"));
                
                // Thông tin liên quan
                bacSi.setTenPhong(rs.getString("tenPhong"));
                bacSi.setEmailNguoiDung(rs.getString("email"));
                bacSi.setSoDienThoaiNguoiDung(rs.getString("soDienThoai"));
                
                bacSiList.add(bacSi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bacSiList;
    }
    
    // Tìm kiếm bác sĩ theo tên, chuyên khoa hoặc tên phòng khám
    public List<BacSi> searchBacSi(String searchTerm) {
        List<BacSi> bacSiList = new ArrayList<>();
        String sql = "SELECT bs.*, pk.tenPhong, nd.email, nd.soDienThoai " +
                     "FROM BacSi bs " +
                     "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                     "JOIN NguoiDung nd ON bs.idNguoiDung = nd.idNguoiDung " +
                     "WHERE bs.hoTenBacSi LIKE ? OR bs.chuyenKhoa LIKE ? OR pk.tenPhong LIKE ? " +
                     "ORDER BY bs.idBacSi";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BacSi bacSi = new BacSi();
                    bacSi.setIdBacSi(rs.getInt("idBacSi"));
                    bacSi.setHoTenBacSi(rs.getString("hoTenBacSi"));
                    bacSi.setIdNguoiDung(rs.getInt("idNguoiDung"));
                    bacSi.setIdPhongKham(rs.getInt("idPhongKham"));
                    bacSi.setChuyenKhoa(rs.getString("chuyenKhoa"));
                    bacSi.setBangCap(rs.getString("bangCap"));
                    bacSi.setKinhNghiem(rs.getInt("kinhNghiem"));
                    
                    // Thông tin liên quan
                    bacSi.setTenPhong(rs.getString("tenPhong"));
                    bacSi.setEmailNguoiDung(rs.getString("email"));
                    bacSi.setSoDienThoaiNguoiDung(rs.getString("soDienThoai"));
                    
                    bacSiList.add(bacSi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bacSiList;
    }

    // Kiểm tra người dùng có phải là bác sĩ không
    public boolean isUserDoctor(int userId) {
        String sql = "SELECT COUNT(*) FROM BacSi WHERE idNguoiDung = ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    // Lấy thông tin bác sĩ theo ID
    public BacSi getBacSiById(int id) {
        BacSi bacSi = null;
        String sql = "SELECT bs.*, pk.tenPhong, nd.email, nd.soDienThoai " +
                     "FROM BacSi bs " +
                     "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                     "JOIN NguoiDung nd ON bs.idNguoiDung = nd.idNguoiDung " +
                     "WHERE bs.idBacSi = ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bacSi = new BacSi();
                    bacSi.setIdBacSi(rs.getInt("idBacSi"));
                    bacSi.setHoTenBacSi(rs.getString("hoTenBacSi"));
                    bacSi.setIdNguoiDung(rs.getInt("idNguoiDung"));
                    bacSi.setIdPhongKham(rs.getInt("idPhongKham"));
                    bacSi.setChuyenKhoa(rs.getString("chuyenKhoa"));
                    bacSi.setBangCap(rs.getString("bangCap"));
                    bacSi.setKinhNghiem(rs.getInt("kinhNghiem"));
                    
                    // Thông tin liên quan
                    bacSi.setTenPhong(rs.getString("tenPhong"));
                    bacSi.setEmailNguoiDung(rs.getString("email"));
                    bacSi.setSoDienThoaiNguoiDung(rs.getString("soDienThoai"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bacSi;
    }
    
    // Thêm bác sĩ mới
    public boolean addBacSi(BacSi bacSi) {
        String sql = "INSERT INTO BacSi (hoTenBacSi, idNguoiDung, idPhongKham, chuyenKhoa, bangCap, kinhNghiem) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, bacSi.getHoTenBacSi());
            stmt.setInt(2, bacSi.getIdNguoiDung());
            stmt.setInt(3, bacSi.getIdPhongKham());
            stmt.setString(4, bacSi.getChuyenKhoa());
            stmt.setString(5, bacSi.getBangCap());
            stmt.setInt(6, bacSi.getKinhNghiem());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bacSi.setIdBacSi(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Cập nhật thông tin bác sĩ
    public boolean updateBacSi(BacSi bacSi) {
        String sql = "UPDATE BacSi SET hoTenBacSi = ?, idNguoiDung = ?, idPhongKham = ?, " +
                     "chuyenKhoa = ?, bangCap = ?, kinhNghiem = ? WHERE idBacSi = ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bacSi.getHoTenBacSi());
            stmt.setInt(2, bacSi.getIdNguoiDung());
            stmt.setInt(3, bacSi.getIdPhongKham());
            stmt.setString(4, bacSi.getChuyenKhoa());
            stmt.setString(5, bacSi.getBangCap());
            stmt.setInt(6, bacSi.getKinhNghiem());
            stmt.setInt(7, bacSi.getIdBacSi());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa bác sĩ và tất cả dữ liệu liên quan
    public boolean deleteBacSi(int id) {
        Connection conn = null;
        try {
            conn = connectMySQL.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            
            // Bước 1: Xóa tất cả lịch hẹn của bác sĩ này
            String deleteAppointmentsSql = "DELETE FROM LichHen WHERE idBacSi = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteAppointmentsSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Bước 2: Xóa tất cả đơn thuốc của bác sĩ này
            String deletePrescriptionsSql = "DELETE FROM DonThuoc WHERE idBacSi = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deletePrescriptionsSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Bước 3: Xóa tất cả điều trị của bác sĩ này
            String deleteTreatmentsSql = "DELETE FROM DieuTri WHERE idBacSi = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteTreatmentsSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Bước 4: Xóa các bản ghi liên quan khác có thể tham chiếu đến bác sĩ này
            
            // Bước 5: Cuối cùng, xóa bác sĩ
            String deleteDoctorSql = "DELETE FROM BacSi WHERE idBacSi = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteDoctorSql)) {
                stmt.setInt(1, id);
                int doctorDeleted = stmt.executeUpdate();
                
                if (doctorDeleted > 0) {
                    conn.commit(); // Commit transaction
                    return true;
                } else {
                    conn.rollback(); // Rollback nếu không thể xóa bác sĩ
                    return false;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback khi có lỗi
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Khôi phục auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
   
    // Lấy chuyên khoa của bác sĩ
    public String getDoctorSpecialty(Connection conn, int doctorId) throws SQLException {
        String sql = "SELECT chuyenKhoa FROM BacSi WHERE idBacSi = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("chuyenKhoa");
                }
            }
        }
        
        return null;
    }

    // Chuyển giao lịch hẹn tương lai từ bác sĩ cũ sang bác sĩ mới
    public boolean reassignFutureAppointments(int oldDoctorId, int newDoctorId) {
        // Lấy ngày hiện tại
        java.util.Date today = new java.util.Date();
        java.sql.Date currentDate = new java.sql.Date(today.getTime());
        
        String sql = "UPDATE LichHen SET idBacSi = ? WHERE idBacSi = ? AND ngayHen >= ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newDoctorId);
            stmt.setInt(2, oldDoctorId);
            stmt.setDate(3, currentDate);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Chuyển giao tất cả điều trị từ bác sĩ cũ sang bác sĩ mới
    public boolean reassignAllTreatments(int oldDoctorId, int newDoctorId) {
        String sql = "UPDATE DieuTri SET idBacSi = ? WHERE idBacSi = ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newDoctorId);
            stmt.setInt(2, oldDoctorId);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Chuyển giao tất cả đơn thuốc từ bác sĩ cũ sang bác sĩ mới
    public boolean reassignAllPrescriptions(int oldDoctorId, int newDoctorId) {
        String sql = "UPDATE DonThuoc SET idBacSi = ? WHERE idBacSi = ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newDoctorId);
            stmt.setInt(2, oldDoctorId);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách bác sĩ theo chuyên khoa
    public List<BacSi> getDoctorsBySpecialty(String specialty) {
        List<BacSi> bacSiList = new ArrayList<>();
        String sql = "SELECT bs.*, pk.tenPhong, nd.email, nd.soDienThoai " +
                     "FROM BacSi bs " +
                     "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                     "JOIN NguoiDung nd ON bs.idNguoiDung = nd.idNguoiDung " +
                     "WHERE bs.chuyenKhoa = ? " +
                     "ORDER BY bs.hoTenBacSi";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, specialty);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BacSi bacSi = new BacSi();
                    bacSi.setIdBacSi(rs.getInt("idBacSi"));
                    bacSi.setHoTenBacSi(rs.getString("hoTenBacSi"));
                    bacSi.setIdNguoiDung(rs.getInt("idNguoiDung"));
                    bacSi.setIdPhongKham(rs.getInt("idPhongKham"));
                    bacSi.setChuyenKhoa(rs.getString("chuyenKhoa"));
                    bacSi.setBangCap(rs.getString("bangCap"));
                    bacSi.setKinhNghiem(rs.getInt("kinhNghiem"));
                    
                    // Thông tin liên quan
                    bacSi.setTenPhong(rs.getString("tenPhong"));
                    bacSi.setEmailNguoiDung(rs.getString("email"));
                    bacSi.setSoDienThoaiNguoiDung(rs.getString("soDienThoai"));
                    
                    bacSiList.add(bacSi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bacSiList;
    }

    // Xóa bác sĩ với bác sĩ thay thế
    public boolean deleteBacSiWithReplacement(int doctorIdToDelete, int replacementDoctorId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connectMySQL.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            
            // Bước 1: Cập nhật TẤT CẢ lịch hẹn trong tương lai
            String updateFutureAppointmentsSQL = 
                "UPDATE lichhen SET idBacSi = ? WHERE idBacSi = ? AND ngayHen >= CURDATE()";
            pstmt = conn.prepareStatement(updateFutureAppointmentsSQL);
            pstmt.setInt(1, replacementDoctorId);
            pstmt.setInt(2, doctorIdToDelete);
            pstmt.executeUpdate();
            pstmt.close();
            
            // Bước 2: Cập nhật TẤT CẢ điều trị liên quan đến bác sĩ
            String updateTreatmentsSQL = 
                "UPDATE dieutri SET idBacSi = ? WHERE idBacSi = ?";
            pstmt = conn.prepareStatement(updateTreatmentsSQL);
            pstmt.setInt(1, replacementDoctorId);
            pstmt.setInt(2, doctorIdToDelete);
            pstmt.executeUpdate();
            pstmt.close();
            
            // Bước 3: Cập nhật TẤT CẢ đơn thuốc liên quan đến bác sĩ (nếu có bảng này)
            try {
                String updatePrescriptionsSQL = 
                    "UPDATE donthuoc SET idBacSi = ? WHERE idBacSi = ?";
                pstmt = conn.prepareStatement(updatePrescriptionsSQL);
                pstmt.setInt(1, replacementDoctorId);
                pstmt.setInt(2, doctorIdToDelete);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (SQLException e) {
                // Bảng donthuoc có thể không tồn tại hoặc không có cột idBacSi
            }
            
            // Bước 4: Hủy TẤT CẢ lịch hẹn trong quá khứ (thay vì cập nhật)
            String cancelPastAppointmentsSQL = 
                "UPDATE lichhen SET trangThai = 'Đã hủy' WHERE idBacSi = ? AND ngayHen < CURDATE()";
            pstmt = conn.prepareStatement(cancelPastAppointmentsSQL);
            pstmt.setInt(1, doctorIdToDelete);
            pstmt.executeUpdate();
            pstmt.close();
            
            // Bước 5: Kiểm tra xem còn bản ghi nào tham chiếu đến bác sĩ không
            String checkReferencesSQL = 
                "SELECT COUNT(*) as total FROM lichhen WHERE idBacSi = ?";
            pstmt = conn.prepareStatement(checkReferencesSQL);
            pstmt.setInt(1, doctorIdToDelete);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int remainingReferences = rs.getInt("total");
            rs.close();
            pstmt.close();
            
            if (remainingReferences > 0) {
                // Nếu vẫn còn tham chiếu, xóa trực tiếp các bản ghi này
                String deleteRemainingSQL = "DELETE FROM lichhen WHERE idBacSi = ?";
                pstmt = conn.prepareStatement(deleteRemainingSQL);
                pstmt.setInt(1, doctorIdToDelete);
                pstmt.executeUpdate();
                pstmt.close();
            }
            
            // Bước 6: Kiểm tra các bảng khác có thể tham chiếu đến bác sĩ
            // Kiểm tra bảng dieutri
            String checkTreatmentSQL = "SELECT COUNT(*) as total FROM dieutri WHERE idBacSi = ?";
            pstmt = conn.prepareStatement(checkTreatmentSQL);
            pstmt.setInt(1, doctorIdToDelete);
            rs = pstmt.executeQuery();
            rs.next();
            rs.close();
            pstmt.close();
            
            // Bước 7: Cuối cùng mới xóa bác sĩ
            String deleteDoctorSQL = "DELETE FROM bacsi WHERE idBacSi = ?";
            pstmt = conn.prepareStatement(deleteDoctorSQL);
            pstmt.setInt(1, doctorIdToDelete);
            int doctorDeleted = pstmt.executeUpdate();
            pstmt.close();
            
            if (doctorDeleted > 0) {
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                // Xử lý lỗi rollback
            }
            
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Khôi phục auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                // Xử lý lỗi đóng kết nối
            }
        }
    }
    // Kiểm tra bác sĩ có lịch hẹn trong tương lai không
    public boolean hasFutureAppointments(int bacSiId) {
        // Lấy ngày hiện tại
        java.util.Date today = new java.util.Date();
        java.sql.Date currentDate = new java.sql.Date(today.getTime());
        
        String sql = "SELECT COUNT(*) FROM LichHen WHERE idBacSi = ? AND ngayHen >= ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bacSiId);
            stmt.setDate(2, currentDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

 public List<BacSi> getOtherDoctorsBySpecialty(int doctorId) {
     List<BacSi> bacSiList = new ArrayList<>();
     
     try (Connection conn = connectMySQL.getConnection()) {
         // First get the specialty of the doctor to be deleted
         String specialtySql = "SELECT chuyenKhoa FROM BacSi WHERE idBacSi = ?";
         String specialty = null;
         
         try (PreparedStatement stmt = conn.prepareStatement(specialtySql)) {
             stmt.setInt(1, doctorId);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     specialty = rs.getString("chuyenKhoa");
                 }
             }
         }
         
         if (specialty != null) {
             // Get other doctors with the same specialty
             String sql = "SELECT bs.*, pk.tenPhong, nd.email, nd.soDienThoai " +
                          "FROM BacSi bs " +
                          "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                          "JOIN NguoiDung nd ON bs.idNguoiDung = nd.idNguoiDung " +
                          "WHERE bs.chuyenKhoa = ? AND bs.idBacSi != ? " +
                          "ORDER BY bs.hoTenBacSi";
             
             try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                 stmt.setString(1, specialty);
                 stmt.setInt(2, doctorId);
                 
                 try (ResultSet rs = stmt.executeQuery()) {
                     while (rs.next()) {
                         BacSi bacSi = new BacSi();
                         bacSi.setIdBacSi(rs.getInt("idBacSi"));
                         bacSi.setHoTenBacSi(rs.getString("hoTenBacSi"));
                         bacSi.setIdNguoiDung(rs.getInt("idNguoiDung"));
                         bacSi.setIdPhongKham(rs.getInt("idPhongKham"));
                         bacSi.setChuyenKhoa(rs.getString("chuyenKhoa"));
                         bacSi.setBangCap(rs.getString("bangCap"));
                         bacSi.setKinhNghiem(rs.getInt("kinhNghiem"));
                         
                         // Related info
                         bacSi.setTenPhong(rs.getString("tenPhong"));
                         bacSi.setEmailNguoiDung(rs.getString("email"));
                         bacSi.setSoDienThoaiNguoiDung(rs.getString("soDienThoai"));
                         
                         bacSiList.add(bacSi);
                     }
                 }
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     
     return bacSiList;
 }

    public boolean hasTreatments(Connection conn, int bacSiId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DieuTri WHERE idBacSi = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bacSiId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow for proper transaction handling
        }
        
        return false;
    }
    
    // Check if doctor has any prescriptions
    public boolean hasPrescriptions(Connection conn, int bacSiId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DonThuoc WHERE idBacSi = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bacSiId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow for proper transaction handling
        }
        
        return false;
    }
    
    // Get potential replacement doctors for a doctor to be deleted
    public List<BacSi> getPotentialReplacementDoctors(int doctorId) {
        List<BacSi> replacementDoctors = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = connectMySQL.getConnection();
            String specialtySql = "SELECT chuyenKhoa FROM BacSi WHERE idBacSi = ?";
            String specialty = null;
            
            try (PreparedStatement stmt = conn.prepareStatement(specialtySql)) {
                stmt.setInt(1, doctorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        specialty = rs.getString("chuyenKhoa");
                    } else {
                    	return replacementDoctors;
                    }
                }
            }
            
            if (specialty != null) {
                // Get all doctors of the same specialty except the one to be deleted
                String sql = "SELECT bs.*, pk.tenPhong, nd.email, nd.soDienThoai " +
                             "FROM BacSi bs " +
                             "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                             "JOIN NguoiDung nd ON bs.idNguoiDung = nd.idNguoiDung " +
                             "WHERE bs.chuyenKhoa = ? AND bs.idBacSi != ? " +
                             "ORDER BY bs.hoTenBacSi";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, specialty);
                    stmt.setInt(2, doctorId);
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        int count = 0;
                        while (rs.next()) {
                            count++;
                            BacSi bacSi = new BacSi();
                            bacSi.setIdBacSi(rs.getInt("idBacSi"));
                            bacSi.setHoTenBacSi(rs.getString("hoTenBacSi"));
                            bacSi.setIdNguoiDung(rs.getInt("idNguoiDung"));
                            bacSi.setIdPhongKham(rs.getInt("idPhongKham"));
                            bacSi.setChuyenKhoa(rs.getString("chuyenKhoa"));
                            bacSi.setBangCap(rs.getString("bangCap"));
                            bacSi.setKinhNghiem(rs.getInt("kinhNghiem"));
                            
                            // Related info
                            bacSi.setTenPhong(rs.getString("tenPhong"));
                            bacSi.setEmailNguoiDung(rs.getString("email"));
                            bacSi.setSoDienThoaiNguoiDung(rs.getString("soDienThoai"));
                            
                            replacementDoctors.add(bacSi);
                        }
                    }
                }
            }
        } catch (SQLException e) {         
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return replacementDoctors;
    }    
    // Get future appointments for a doctor - Fixed to handle Vietnamese text
    public List<LichHen> getFutureAppointments(int bacSiId) {
        List<LichHen> lichHenList = new ArrayList<>();
        
        // Get current date
        java.util.Date today = new java.util.Date();
        java.sql.Date currentDate = new java.sql.Date(today.getTime());
        
        // Removed COLLATE clause and use parameterized query instead
        String sql = "SELECT lh.idLichHen, bs.hoTenBacSi, bn.hoTen, lh.ngayHen, pk.tenPhong, " +
                "lh.gioHen, lh.trangThai, lh.moTa " +
                "FROM LichHen lh " +
                "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                "JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                "JOIN PhongKham pk ON lh.idPhongKham = pk.idPhongKham " +
                "WHERE lh.idBacSi = ? AND lh.ngayHen >= ? AND lh.trangThai != ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bacSiId);
            stmt.setDate(2, currentDate);
            stmt.setString(3, "Đã hủy");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LichHen lichHen = new LichHen();
                    lichHen.setIdLichHen(rs.getInt("idLichHen"));
                    lichHen.setHoTenBacSi(rs.getString("hoTenBacSi"));
                    lichHen.setHoTenBenhNhan(rs.getString("hoTen"));
                    lichHen.setNgayHen(rs.getDate("ngayHen"));
                    lichHen.setTenPhong(rs.getString("tenPhong"));
                    lichHen.setGioHen(rs.getTime("gioHen"));
                    lichHen.setTrangThai(rs.getString("trangThai"));
                    lichHen.setMoTa(rs.getString("moTa"));
                    
                    lichHenList.add(lichHen);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return lichHenList;
    }
    
    // Get all treatments by a doctor
    public List<DieuTri> getDieuTriByBacSi(int bacSiId) {
        List<DieuTri> dieuTriList = new ArrayList<>();
        
        String sql = "SELECT dt.*, hs.chuanDoan, bn.hoTen AS tenBenhNhan " +
                     "FROM DieuTri dt " +
                     "JOIN HoSoBenhAn hs ON dt.idHoSo = hs.idHoSo " +
                     "JOIN BenhNhan bn ON hs.idBenhNhan = bn.idBenhNhan " +
                     "WHERE dt.idBacSi = ? " +
                     "ORDER BY dt.ngayDieuTri DESC";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bacSiId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DieuTri dieuTri = new DieuTri();
                    dieuTri.setIdDieuTri(rs.getInt("idDieuTri"));
                    dieuTri.setIdHoSo(rs.getInt("idHoSo"));
                    dieuTri.setIdBacSi(rs.getInt("idBacSi"));
                    dieuTri.setMoTa(rs.getString("moTa"));
                    dieuTri.setNgayDieuTri(rs.getDate("ngayDieuTri"));
                    
                    // Additional information
                    dieuTri.setChuanDoan(rs.getString("chuanDoan"));
                    dieuTri.setTenBenhNhan(rs.getString("tenBenhNhan"));
                    
                    dieuTriList.add(dieuTri);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return dieuTriList;
    }
    
    // Get all NguoiDung with 'Bác sĩ' role - Fixed to handle Vietnamese text
    public List<NguoiDung> getAllDoctorUsers() {
        List<NguoiDung> nguoiDungList = new ArrayList<>();
        String sql = "SELECT * FROM NguoiDung WHERE vaiTro = ? ORDER BY hoTen";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "Bác sĩ");
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                
                while (rs.next()) {
                    count++;
                    NguoiDung nguoiDung = new NguoiDung();
                    nguoiDung.setIdNguoiDung(rs.getInt("idNguoiDung"));
                    nguoiDung.setHoTen(rs.getString("hoTen"));
                    nguoiDung.setEmail(rs.getString("email"));
                    nguoiDung.setMatKhau(rs.getString("matKhau"));
                    nguoiDung.setSoDienThoai(rs.getString("soDienThoai"));
                    nguoiDung.setNgaySinh(rs.getDate("ngaySinh"));
                    nguoiDung.setGioiTinh(rs.getString("gioiTinh"));
                    nguoiDung.setVaiTro(rs.getString("vaiTro"));
                    
                    nguoiDungList.add(nguoiDung);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return nguoiDungList;
    }
    
    // Get NguoiDung by ID
    public NguoiDung getNguoiDungById(int id) {
        NguoiDung nguoiDung = null;
        String sql = "SELECT * FROM NguoiDung WHERE idNguoiDung = ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nguoiDung = new NguoiDung();
                    nguoiDung.setIdNguoiDung(rs.getInt("idNguoiDung"));
                    nguoiDung.setHoTen(rs.getString("hoTen"));
                    nguoiDung.setEmail(rs.getString("email"));
                    nguoiDung.setMatKhau(rs.getString("matKhau"));
                    nguoiDung.setSoDienThoai(rs.getString("soDienThoai"));
                    nguoiDung.setNgaySinh(rs.getDate("ngaySinh"));
                    nguoiDung.setGioiTinh(rs.getString("gioiTinh"));
                    nguoiDung.setVaiTro(rs.getString("vaiTro"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return nguoiDung;
    }
    
    // Get unused NguoiDung with 'Bác sĩ' role (not linked to any BacSi) - Fixed to handle Vietnamese text
    public List<NguoiDung> getUnusedDoctorUsers() {
        List<NguoiDung> nguoiDungList = new ArrayList<>();
        String sql = "SELECT nd.* FROM NguoiDung nd " +
                     "LEFT JOIN BacSi bs ON nd.idNguoiDung = bs.idNguoiDung " +
                     "WHERE nd.vaiTro = ? AND bs.idBacSi IS NULL " +
                     "ORDER BY nd.hoTen";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "Bác sĩ");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NguoiDung nguoiDung = new NguoiDung();
                    nguoiDung.setIdNguoiDung(rs.getInt("idNguoiDung"));
                    nguoiDung.setHoTen(rs.getString("hoTen"));
                    nguoiDung.setEmail(rs.getString("email"));
                    nguoiDung.setMatKhau(rs.getString("matKhau"));
                    nguoiDung.setSoDienThoai(rs.getString("soDienThoai"));
                    nguoiDung.setNgaySinh(rs.getDate("ngaySinh"));
                    nguoiDung.setGioiTinh(rs.getString("gioiTinh"));
                    nguoiDung.setVaiTro(rs.getString("vaiTro"));
                    
                    nguoiDungList.add(nguoiDung);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return nguoiDungList;
    }
    
    // Get all clinics
    
    // Get all clinics
    public List<PhongKham> getAllPhongKham() {
        List<PhongKham> phongKhamList = new ArrayList<>();
        String sql = "SELECT * FROM PhongKham ORDER BY tenPhong";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                PhongKham phongKham = new PhongKham();
                phongKham.setIdPhongKham(rs.getInt("idPhongKham"));
                phongKham.setTenPhong(rs.getString("tenPhong"));
                phongKham.setDiaChi(rs.getString("diaChi"));
                
                phongKhamList.add(phongKham);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return phongKhamList;
    }
    public PhongKham getPhongKhamById(int id) {
        PhongKham phongKham = null;
        String sql = "SELECT * FROM PhongKham WHERE idPhongKham = ?";
        
        try (Connection conn = connectMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    phongKham = new PhongKham();
                    phongKham.setIdPhongKham(rs.getInt("idPhongKham"));
                    phongKham.setTenPhong(rs.getString("tenPhong"));
                    phongKham.setDiaChi(rs.getString("diaChi"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return phongKham;
    }
}