package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import model.LichHen;

public class ThongKeLichHenController extends ThongKeController {
    
    public ThongKeLichHenController() {
        super(); // Gọi constructor của lớp cha để khởi tạo kết nối
    }
    public Map<String, Integer> thongKeLichHenTheoTrangThai(Date tuNgay, Date denNgay) {
        Map<String, Integer> ketQua = new HashMap<>();
        
        String sql = "SELECT trangThai, COUNT(*) AS soLuong " +
                     "FROM LichHen " +
                     "WHERE ngayHen BETWEEN ? AND ? " +
                     "GROUP BY trangThai";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String trangThai = rs.getString("trangThai");
                int soLuong = rs.getInt("soLuong");
                ketQua.put(trangThai, soLuong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê lịch hẹn theo trạng thái: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
    public List<Map<String, Object>> thongKeLichHenTheoBacSi(Date tuNgay, Date denNgay) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        
        String sql = "SELECT bs.idBacSi, bs.hoTenBacSi, COUNT(*) AS soLichHen " +
                     "FROM LichHen lh " +
                     "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                     "WHERE lh.ngayHen BETWEEN ? AND ? " +
                     "GROUP BY bs.idBacSi, bs.hoTenBacSi " +
                     "ORDER BY soLichHen DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> bacSiInfo = new HashMap<>();
                bacSiInfo.put("idBacSi", rs.getInt("idBacSi"));
                bacSiInfo.put("hoTenBacSi", rs.getString("hoTenBacSi"));
                bacSiInfo.put("soLichHen", rs.getInt("soLichHen"));
                ketQua.add(bacSiInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê lịch hẹn theo bác sĩ: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
    public List<Map<String, Object>> thongKeLichHenTheoPhongKham(Date tuNgay, Date denNgay) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        
        String sql = "SELECT pk.idPhongKham, pk.tenPhong, COUNT(*) AS soLichHen " +
                     "FROM LichHen lh " +
                     "JOIN PhongKham pk ON lh.idPhongKham = pk.idPhongKham " +
                     "WHERE lh.ngayHen BETWEEN ? AND ? " +
                     "GROUP BY pk.idPhongKham, pk.tenPhong " +
                     "ORDER BY soLichHen DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> phongKhamInfo = new HashMap<>();
                phongKhamInfo.put("idPhongKham", rs.getInt("idPhongKham"));
                phongKhamInfo.put("tenPhong", rs.getString("tenPhong"));
                phongKhamInfo.put("soLichHen", rs.getInt("soLichHen"));
                ketQua.add(phongKhamInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê lịch hẹn theo phòng khám: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
    public Map<String, Integer> thongKeLichHenTheoNgay(Date tuNgay, Date denNgay) {
        Map<String, Integer> ketQua = new HashMap<>();
        
        String sql = "SELECT ngayHen, COUNT(*) AS soLuong " +
                     "FROM LichHen " +
                     "WHERE ngayHen BETWEEN ? AND ? " +
                     "GROUP BY ngayHen " +
                     "ORDER BY ngayHen";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Date ngayHen = rs.getDate("ngayHen");
                int soLuong = rs.getInt("soLuong");
                ketQua.put(dateFormat.format(ngayHen), soLuong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê lịch hẹn theo ngày: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
    public Map<String, Integer> thongKeLichHenTheoThang(Date tuNgay, Date denNgay) {
        Map<String, Integer> ketQua = new HashMap<>();
        
        String sql = "SELECT YEAR(ngayHen) AS nam, MONTH(ngayHen) AS thang, COUNT(*) AS soLuong " +
                     "FROM LichHen " +
                     "WHERE ngayHen BETWEEN ? AND ? " +
                     "GROUP BY YEAR(ngayHen), MONTH(ngayHen) " +
                     "ORDER BY nam, thang";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int thang = rs.getInt("thang");
                int nam = rs.getInt("nam");
                int soLuong = rs.getInt("soLuong");
                
                // Tạo Date object từ tháng/năm để định dạng
                Date thangNam = new Date(nam - 1900, thang - 1, 1);
                ketQua.put(monthYearFormat.format(thangNam), soLuong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê lịch hẹn theo tháng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
    public List<LichHen> layDanhSachLichHen(Date tuNgay, Date denNgay) {
        List<LichHen> dsLichHen = new ArrayList<>();
        
        String sql = "SELECT lh.idLichHen, lh.idBacSi, bs.hoTenBacSi, bn.hoTen, lh.ngayHen, pk.tenPhong, " +
                     "lh.gioHen, lh.trangThai, lh.moTa " +
                     "FROM LichHen lh " +
                     "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                     "JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                     "JOIN PhongKham pk ON lh.idPhongKham = pk.idPhongKham " +
                     "WHERE lh.ngayHen BETWEEN ? AND ? " +
                     "ORDER BY lh.ngayHen, lh.gioHen";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LichHen lichHen = new LichHen();
                lichHen.setIdLichHen(rs.getInt("idLichHen"));
                lichHen.setIdBacSi(rs.getInt("idBacSi"));
                lichHen.setHoTenBacSi(rs.getString("hoTenBacSi"));
                lichHen.setHoTenBenhNhan(rs.getString("hoTen"));
                lichHen.setNgayHen(rs.getDate("ngayHen"));
                lichHen.setTenPhong(rs.getString("tenPhong"));
                lichHen.setGioHen(rs.getTime("gioHen"));
                lichHen.setTrangThai(rs.getString("trangThai"));
                lichHen.setMoTa(rs.getString("moTa"));
                dsLichHen.add(lichHen);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return dsLichHen;
    }
    public Map<String, Double> thongKeTyLeHoanThanhLichHen(Date tuNgay, Date denNgay) {
        Map<String, Double> ketQua = new HashMap<>();
        
        String sql = "SELECT trangThai, COUNT(*) AS soLuong, " +
                     "(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM LichHen WHERE ngayHen BETWEEN ? AND ?)) AS tyLePhanTram " +
                     "FROM LichHen " +
                     "WHERE ngayHen BETWEEN ? AND ? " +
                     "GROUP BY trangThai";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            stmt.setDate(3, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(4, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String trangThai = rs.getString("trangThai");
                double tyLe = rs.getDouble("tyLePhanTram");
                ketQua.put(trangThai, tyLe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê tỷ lệ hoàn thành lịch hẹn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
    public Map<String, Integer> thongKeBenhNhanTheoLoai(Date tuNgay, Date denNgay) {
        Map<String, Integer> ketQua = new HashMap<>();
        
        String sql = "SELECT bn.loaiBenhNhan, COUNT(DISTINCT lh.idBenhNhan) AS soBenhNhan " +
                     "FROM LichHen lh " +
                     "JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                     "WHERE lh.ngayHen BETWEEN ? AND ? " +
                     "GROUP BY bn.loaiBenhNhan";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String loaiBenhNhan = rs.getString("loaiBenhNhan");
                int soBenhNhan = rs.getInt("soBenhNhan");
                ketQua.put(loaiBenhNhan, soBenhNhan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê bệnh nhân theo loại: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
    public List<Map<String, Object>> thongKeThoiGianTrungBinhTheoBacSi(Date tuNgay, Date denNgay) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        
        String sql = "SELECT bs.idBacSi, bs.hoTenBacSi, AVG(lh.thoiGianKham) AS thoiGianTrungBinh " +
                     "FROM LichHen lh " +
                     "JOIN BacSi bs ON lh.idBacSi = bs.idBacSi " +
                     "WHERE lh.ngayHen BETWEEN ? AND ? AND lh.thoiGianKham IS NOT NULL " +
                     "GROUP BY bs.idBacSi, bs.hoTenBacSi " +
                     "ORDER BY thoiGianTrungBinh DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> bacSiInfo = new HashMap<>();
                bacSiInfo.put("idBacSi", rs.getInt("idBacSi"));
                bacSiInfo.put("hoTenBacSi", rs.getString("hoTenBacSi"));
                bacSiInfo.put("thoiGianTrungBinh", rs.getDouble("thoiGianTrungBinh"));
                ketQua.add(bacSiInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê thời gian trung bình theo bác sĩ: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
    public Map<Integer, Integer> thongKeLichHenTheoGio(Date tuNgay, Date denNgay) {
        Map<Integer, Integer> ketQua = new HashMap<>();
        
        String sql = "SELECT HOUR(gioHen) AS gio, COUNT(*) AS soLuong " +
                     "FROM LichHen " +
                     "WHERE ngayHen BETWEEN ? AND ? " +
                     "GROUP BY HOUR(gioHen) " +
                     "ORDER BY gio";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int gio = rs.getInt("gio");
                int soLuong = rs.getInt("soLuong");
                ketQua.put(gio, soLuong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê lịch hẹn theo giờ: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
    public Map<String, Integer> thongKeBenhNhanMoiVaQuayLai(Date tuNgay, Date denNgay) {
        Map<String, Integer> ketQua = new HashMap<>();
        
        // Đếm bệnh nhân mới (lần đầu tiên có lịch hẹn trong khoảng thời gian)
        String sqlBenhNhanMoi = "SELECT COUNT(DISTINCT bn.idBenhNhan) AS soBenhNhanMoi " +
                               "FROM BenhNhan bn " +
                               "WHERE bn.idBenhNhan IN " +
                               "(SELECT MIN(lh1.idBenhNhan) " +
                               " FROM LichHen lh1 " +
                               " WHERE lh1.ngayHen BETWEEN ? AND ? " +
                               " GROUP BY lh1.idBenhNhan " +
                               " HAVING MIN(lh1.idBenhNhan) NOT IN " +
                               " (SELECT DISTINCT lh2.idBenhNhan FROM LichHen lh2 WHERE lh2.ngayHen < ?))";
        
        // Đếm bệnh nhân quay lại (đã có lịch hẹn trước khoảng thời gian)
        String sqlBenhNhanQuayLai = "SELECT COUNT(DISTINCT lh1.idBenhNhan) AS soBenhNhanQuayLai " +
                                   "FROM LichHen lh1 " +
                                   "WHERE lh1.ngayHen BETWEEN ? AND ? " +
                                   "AND lh1.idBenhNhan IN " +
                                   "(SELECT DISTINCT lh2.idBenhNhan FROM LichHen lh2 WHERE lh2.ngayHen < ?)";
        
        try {
            // Thực hiện truy vấn đếm bệnh nhân mới
            try (PreparedStatement stmt = conn.prepareStatement(sqlBenhNhanMoi)) {
                stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
                stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
                stmt.setDate(3, new java.sql.Date(tuNgay.getTime()));
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    ketQua.put("Bệnh nhân mới", rs.getInt("soBenhNhanMoi"));
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlBenhNhanQuayLai)) {
                stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
                stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
                stmt.setDate(3, new java.sql.Date(tuNgay.getTime()));
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    ketQua.put("Bệnh nhân quay lại", rs.getInt("soBenhNhanQuayLai"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê bệnh nhân mới và quay lại: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return ketQua;
    }
}