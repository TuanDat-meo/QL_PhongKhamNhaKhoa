package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.LichHen;
import connect.connectMySQL;



public class LichHenController {
    private Connection conn;

    public LichHenController() {
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean datLichHen(LichHen lichHen) {
        String sql = "INSERT INTO LichHen (idBacSi, idBenhNhan, ngayHen, idPhongKham, gioHen, trangThai, moTa) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, lichHen.getIdBacSi());
            stmt.setInt(2, lichHen.getIdBenhNhan());
            stmt.setDate(3, lichHen.getNgayHen());
            stmt.setInt(4, lichHen.getIdPhongKham());
            stmt.setTime(5, lichHen.getGioHen());
            stmt.setString(6, lichHen.getTrangThai());
            stmt.setString(7, lichHen.getMoTa());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    lichHen.setIdLichHen(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<LichHen> getLichHenByUserId(int userId) {
        List<LichHen> dsLichHen = new ArrayList<>();
        String sql = "SELECT lh.idLichHen, lh.idBacSi, bs.hoTenBacSi, bn.hoTen, lh.ngayHen, pk.tenPhong, " +
                     "lh.gioHen, lh.trangThai, lh.moTa " +
                     "FROM LichHen lh " +
                     "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                     "JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                     "JOIN PhongKham pk ON lh.idPhongKham = pk.idPhongKham " +
                     "WHERE bn.idNguoiDung = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dsLichHen.add(mapResultSetToLichHen(rs));
            }
        } catch (SQLException e) {
//            System.err.println("Lỗi khi lấy lịch hẹn theo người dùng: " + e.getMessage());
        }
        return dsLichHen;
    }

    public LichHen getLichHenById(int lichHenId) {
        String sql = "SELECT lh.idLichHen, lh.idBacSi, bs.hoTenBacSi, bn.hoTen, lh.ngayHen, pk.tenPhong, " +
                     "lh.gioHen, lh.trangThai, lh.moTa " +
                     "FROM LichHen lh " +
                     "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                     "JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                     "JOIN PhongKham pk ON lh.idPhongKham = pk.idPhongKham " +
                     "WHERE lh.idLichHen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lichHenId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLichHen(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch hẹn theo ID: " + e.getMessage());
        }
        return null;
    }
    public List<LichHen> getLichHenByDate(java.util.Date date) {
        List<LichHen> dsLichHen = new ArrayList<>();
        String sql = "SELECT lh.idLichHen, lh.idBacSi, bs.hoTenBacSi, bn.hoTen, lh.ngayHen, pk.tenPhong, " +
                "lh.gioHen, lh.trangThai, lh.moTa " +
                "FROM LichHen lh " +
                "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                "JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                "JOIN PhongKham pk ON lh.idPhongKham = pk.idPhongKham " +
                "WHERE lh.ngayHen = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Chuyển đổi java.util.Date -> java.sql.Date
            stmt.setDate(1, new java.sql.Date(date.getTime()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dsLichHen.add(mapResultSetToLichHen(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch hẹn theo ngày: " + e.getMessage());
        }
        return dsLichHen;
    }

    public List<LichHen> layLichHenTheoBacSi(int idBacSi) {
        List<LichHen> dsLichHen = new ArrayList<>();
        String sql = "SELECT lh.idLichHen, bs.tenBacSi, bn.hoTen, lh.ngayHen, pk.tenPhong, lh.gioHen, lh.trangThai, lh.moTa " +
                     "FROM LichHen lh " +
                     "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                     "JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                     "JOIN PhongKham pk ON lh.idPhongKham = pk.idPhongKham " +
                     "WHERE lh.idBacSi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBacSi);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dsLichHen.add(mapResultSetToLichHen(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsLichHen;
    }
    public boolean deleteLichHen(int idLichHen) {
        String sql = "DELETE FROM LichHen WHERE idLichHen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLichHen);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có dòng bị xóa
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa lịch hẹn: " + e.getMessage());
        }
        return false;
    }
    public boolean updateLichHen(LichHen lichHen) {
        int idBacSi = getIdBacSi(lichHen.getHoTenBacSi());
        int idBenhNhan = getIdBenhNhan(lichHen.getHoTenBenhNhan());

        if (idBacSi == -1 || idBenhNhan == -1) {
            System.err.println("Lỗi: Không tìm thấy bác sĩ hoặc bệnh nhân.");
            return false;
        }

        String sql = "UPDATE LichHen SET idBacSi = ?, idBenhNhan = ?, ngayHen = ?, idPhongKham = ?, gioHen = ?, trangThai = ?, moTa = ? WHERE idLichHen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBacSi);
            stmt.setInt(2, idBenhNhan);
            stmt.setDate(3, lichHen.getNgayHen());
            stmt.setInt(4, lichHen.getIdPhongKham());
            stmt.setTime(5, lichHen.getGioHen());
            stmt.setString(6, lichHen.getTrangThai());
            stmt.setString(7, lichHen.getMoTa());
            stmt.setInt(8, lichHen.getIdLichHen());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getIdBacSi(String hoTenBacSi) {
        String sql = "SELECT idBacSi FROM BacSi WHERE hoTenBacSi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hoTenBacSi);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idBacSi");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getIdBenhNhan(String hoTenBenhNhan) {
        String sql = "SELECT idBenhNhan FROM BenhNhan WHERE hoTen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hoTenBenhNhan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idBenhNhan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
 // Trong QLLichHen.java
 // In LichHenController.java
// In LichHenController.java
public List<String> danhSachBacSi() {
    List<String> danhSach = new ArrayList<>();
    try {
        String sql = "SELECT hoTenBacSi FROM BacSi";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String hoTenBacSi = resultSet.getString("hoTenBacSi").trim(); // Trim here!
            danhSach.add(hoTenBacSi);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return danhSach;
}

public List<String> danhSachBenhNhan() {
    List<String> danhSach = new ArrayList<>();
    try {
        String sql = "SELECT hoTen FROM BenhNhan";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String hoTenBenhNhan = resultSet.getString("hoTen").trim(); // Trim here!
            danhSach.add(hoTenBenhNhan);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return danhSach;
}

public List<String> danhSachPhongKham() {
    List<String> danhSach = new ArrayList<>();
    try {
        String sql = "SELECT tenPhong FROM PhongKham";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String tenPhong = resultSet.getString("tenPhong").trim(); // Trim here!
            danhSach.add(tenPhong);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return danhSach;
}


    public LichHen mapResultSetToLichHen(ResultSet rs) throws SQLException {
        LichHen lichHen = new LichHen();
        lichHen.setIdLichHen(rs.getInt("idLichHen"));
        lichHen.setIdBacSi(rs.getInt("idBacSi")); // Đảm bảo cột này có trong SELECT
        lichHen.setHoTenBacSi(rs.getString("hoTenBacSi"));
        lichHen.setHoTenBenhNhan(rs.getString("hoTen"));
        lichHen.setNgayHen(rs.getDate("ngayHen"));
        lichHen.setTenPhong(rs.getString("tenPhong"));
        lichHen.setGioHen(rs.getTime("gioHen"));
        lichHen.setTrangThai(rs.getString("trangThai"));
        lichHen.setMoTa(rs.getString("moTa"));
        return lichHen;
    }
    // In LichHenController.java
// In LichHenController.java
public int getBacSiIdFromName(String hoTenBacSi) {
    String sql = "SELECT idBacSi FROM BacSi WHERE hoTenBacSi = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, hoTenBacSi.trim()); // Trim here!
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("idBacSi");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    System.err.println("Không tìm thấy ID bác sĩ cho tên: '" + hoTenBacSi + "'");
    return -1;
}

public int getBenhNhanIdFromName(String hoTenBenhNhan) {
    String sql = "SELECT idBenhNhan FROM BenhNhan WHERE hoTen = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, hoTenBenhNhan.trim()); // Trim here!
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("idBenhNhan");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    System.err.println("Không tìm thấy ID bệnh nhân cho tên: '" + hoTenBenhNhan + "'");
    return -1;
}

public int getPhongKhamIdFromName(String tenPhong) {
    String sql = "SELECT idPhongKham FROM PhongKham WHERE tenPhong = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, tenPhong.trim()); // Trim here!
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("idPhongKham");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    System.err.println("Không tìm thấy ID phòng khám cho tên: '" + tenPhong + "'");
    return -1;
}


    public List<LichHen> getAllLichHen() {
        List<LichHen> dsLichHen = new ArrayList<>();
        String sql = "SELECT lh.idLichHen, lh.idBacSi, bs.hoTenBacSi, bn.hoTen, lh.ngayHen, pk.tenPhong, " +
                     "lh.gioHen, lh.trangThai, lh.moTa " +
                     "FROM LichHen lh " +
                     "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                     "JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                     "JOIN PhongKham pk ON lh.idPhongKham = pk.idPhongKham";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dsLichHen.add(mapResultSetToLichHen(rs));
            }
        } catch (SQLException e) {
//            System.err.println("Lỗi khi lấy tất cả lịch hẹn: " + e.getMessage());
        }
        return dsLichHen;
    }
    public LichHen getLichHenByDateAndTime(java.util.Date date, String timeSlot, String patientName) {
        String sql = "SELECT lh.idLichHen, lh.idBacSi, bs.hoTenBacSi, bn.hoTen, lh.ngayHen, pk.tenPhong, " +
                     "lh.gioHen, lh.trangThai, lh.moTa " +
                     "FROM LichHen lh " +
                     "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                     "JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                     "JOIN PhongKham pk ON lh.idPhongKham = pk.idPhongKham " +
                     "WHERE lh.ngayHen = ? AND TIME_FORMAT(lh.gioHen, '%H:%i') = ? AND bn.hoTen LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            stmt.setString(2, timeSlot);
            stmt.setString(3, "%" + patientName + "%");
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLichHen(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm lịch hẹn theo ngày và giờ: " + e.getMessage());
        }
        return null;
    }



}
