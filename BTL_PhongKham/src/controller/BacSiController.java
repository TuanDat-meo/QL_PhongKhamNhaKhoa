// BacSiController.java - Controller with integrated DAO functionality
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
    
    // Get all doctors with related info
    public List<BacSi> getAllBacSi() {
        List<BacSi> bacSiList = new ArrayList<>();
        String sql = "SELECT bs.*, pk.tenPhong, nd.email, nd.soDienThoai " +
                     "FROM BacSi bs " +
                     "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                     "JOIN NguoiDung nd ON bs.idNguoiDung = nd.idNguoiDung " +
                     "ORDER BY bs.idBacSi";
        
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
                
                // Related info
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
    
    // Search doctors by name, specialty or clinic name
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
                    
                    // Related info
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
    
    // Get a doctor by ID
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
                    
                    // Related info
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
    
    // Add a new doctor
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
    
    // Update doctor's information
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
    
    // Delete a doctor and handle related records
 // Delete a doctor and handle related records by deleting appointments and treatments
    public boolean deleteBacSi(int id) {
        Connection conn = null;
        try {
            conn = connectMySQL.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // First, delete all future appointments for the doctor
            if (!deleteFutureAppointments(conn, id)) {
                conn.rollback();
                return false;
            }
            
            // Delete all treatments related to this doctor
            if (!deleteDoctorTreatments(conn, id)) {
                conn.rollback();
                return false;
            }
            
            // Also delete prescriptions (DonThuoc) if any
            if (!deleteDoctorPrescriptions(conn, id)) {
                conn.rollback();
                return false;
            }
            
            // Now delete the doctor
            String sql = "DELETE FROM BacSi WHERE idBacSi = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows > 0) {
                    conn.commit(); // Commit the transaction
                    return true;
                } else {
                    conn.rollback(); // Rollback if doctor couldn't be deleted
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete future appointments for a doctor
    private boolean deleteFutureAppointments(Connection conn, int bacSiId) throws SQLException {
        // Get current date
        java.util.Date today = new java.util.Date();
        java.sql.Date currentDate = new java.sql.Date(today.getTime());
        
        // Delete future appointments
        String sql = "DELETE FROM LichHen WHERE idBacSi = ? AND ngayHen >= ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bacSiId);
            stmt.setDate(2, currentDate);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow for proper transaction handling
        }
    }

    // Delete all treatments by a doctor
    private boolean deleteDoctorTreatments(Connection conn, int bacSiId) throws SQLException {
        String sql = "DELETE FROM DieuTri WHERE idBacSi = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bacSiId);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow for proper transaction handling
        }
    }

    // Delete all prescriptions by a doctor
    private boolean deleteDoctorPrescriptions(Connection conn, int bacSiId) throws SQLException {
        String sql = "DELETE FROM DonThuoc WHERE idBacSi = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bacSiId);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow for proper transaction handling
        }
    }

    // Get doctor's specialty
    public String getDoctorSpecialty(Connection conn, int doctorId) throws SQLException {
        String sql = "SELECT chuyenKhoa FROM BacSi WHERE idBacSi = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String specialty = rs.getString("chuyenKhoa");
                    System.out.println("Retrieved specialty: " + specialty + " for doctor ID: " + doctorId);
                    return specialty;
                }
            }
        }
        
        System.out.println("No specialty found for doctor ID: " + doctorId);
        return null;
    }
 // Add these methods to BacSiController.java

 // Reassign future appointments from one doctor to another
 public boolean reassignFutureAppointments(int oldDoctorId, int newDoctorId) {
     // Get current date
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

 // Reassign all treatments from one doctor to another
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

 // Reassign all prescriptions from one doctor to another
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

 // Get doctors of the same specialty
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
                 
                 // Related info
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

 // Modified deleteBacSi method with doctor replacement
 public boolean deleteBacSiWithReplacement(int id, int replacementDoctorId) {
     Connection conn = null;
     try {
         conn = connectMySQL.getConnection();
         conn.setAutoCommit(false); // Start transaction
         
         // If replacement doctor is provided, reassign appointments and treatments
         if (replacementDoctorId > 0) {
             // Reassign future appointments
             String appointmentSql = "UPDATE LichHen SET idBacSi = ? WHERE idBacSi = ? AND ngayHen >= CURRENT_DATE()";
             try (PreparedStatement stmt = conn.prepareStatement(appointmentSql)) {
                 stmt.setInt(1, replacementDoctorId);
                 stmt.setInt(2, id);
                 stmt.executeUpdate();
             }
             
             // Reassign treatments
             String treatmentSql = "UPDATE DieuTri SET idBacSi = ? WHERE idBacSi = ?";
             try (PreparedStatement stmt = conn.prepareStatement(treatmentSql)) {
                 stmt.setInt(1, replacementDoctorId);
                 stmt.setInt(2, id);
                 stmt.executeUpdate();
             }
             
             // Reassign prescriptions
             String prescriptionSql = "UPDATE DonThuoc SET idBacSi = ? WHERE idBacSi = ?";
             try (PreparedStatement stmt = conn.prepareStatement(prescriptionSql)) {
                 stmt.setInt(1, replacementDoctorId);
                 stmt.setInt(2, id);
                 stmt.executeUpdate();
             }
         } else {
             // No replacement doctor, delete future appointments
             if (!deleteFutureAppointments(conn, id)) {
                 conn.rollback();
                 return false;
             }
             
             // Delete all treatments related to this doctor
             if (!deleteDoctorTreatments(conn, id)) {
                 conn.rollback();
                 return false;
             }
             
             // Also delete prescriptions (DonThuoc) if any
             if (!deleteDoctorPrescriptions(conn, id)) {
                 conn.rollback();
                 return false;
             }
         }
         
         // Now delete the doctor
         String sql = "DELETE FROM BacSi WHERE idBacSi = ?";
         try (PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, id);
             int affectedRows = stmt.executeUpdate();
             
             if (affectedRows > 0) {
                 conn.commit(); // Commit the transaction
                 return true;
             } else {
                 conn.rollback(); // Rollback if doctor couldn't be deleted
                 return false;
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
         try {
             if (conn != null) {
                 conn.rollback(); // Rollback on error
             }
         } catch (SQLException ex) {
             ex.printStackTrace();
         }
         return false;
     } finally {
         try {
             if (conn != null) {
                 conn.setAutoCommit(true); // Reset auto-commit
                 conn.close();
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
 }

 // Check if a doctor has future appointments
 public boolean hasFutureAppointments(int bacSiId) {
     // Get current date
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

 // Get doctors of the same specialty, excluding the one to be deleted
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
    // Check if doctor has the same specialty
    private boolean hasSameSpecialty(Connection conn, int doctorId, String specialty) throws SQLException {
        if (specialty == null) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM BacSi WHERE idBacSi = ? AND chuyenKhoa = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.setString(2, specialty);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    // Reassign all treatments to a new doctor
    private boolean reassignTreatments(Connection conn, int oldDoctorId, int newDoctorId) throws SQLException {
        String sql = "UPDATE DieuTri SET idBacSi = ? WHERE idBacSi = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newDoctorId);
            stmt.setInt(2, oldDoctorId);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow for proper transaction handling
        }
    }
    
    // Reassign all prescriptions to a new doctor
    private boolean reassignPrescriptions(Connection conn, int oldDoctorId, int newDoctorId) throws SQLException {
        String sql = "UPDATE DonThuoc SET idBacSi = ? WHERE idBacSi = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newDoctorId);
            stmt.setInt(2, oldDoctorId);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow for proper transaction handling
        }
    }

    // Check if doctor has any treatments
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
            
            // First, log the doctor ID and get the specialty
            System.out.println("Finding replacements for doctor ID: " + doctorId);
            
            // Get the specialty of the doctor to be deleted
            String specialtySql = "SELECT chuyenKhoa FROM BacSi WHERE idBacSi = ?";
            String specialty = null;
            
            try (PreparedStatement stmt = conn.prepareStatement(specialtySql)) {
                stmt.setInt(1, doctorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        specialty = rs.getString("chuyenKhoa");
                        System.out.println("Doctor specialty: " + specialty);
                    } else {
                        System.out.println("No doctor found with ID: " + doctorId);
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
                        System.out.println("Found " + count + " potential replacement doctors");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
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
    
    // Cancel future appointments for a doctor - Fixed to handle Vietnamese text
    private boolean cancelFutureAppointments(Connection conn, int bacSiId) throws SQLException {
        // Get current date
        java.util.Date today = new java.util.Date();
        java.sql.Date currentDate = new java.sql.Date(today.getTime());
        
        // Update status of future appointments to "Đã hủy" without using COLLATE or N prefix
        String sql = "UPDATE LichHen SET trangThai = ? " +
                "WHERE idBacSi = ? AND ngayHen >= ? AND trangThai != ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Use setString directly for Vietnamese text
            stmt.setString(1, "Đã hủy");
            stmt.setInt(2, bacSiId);
            stmt.setDate(3, currentDate);
            stmt.setString(4, "Đã hủy");
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow for proper transaction handling
        }
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