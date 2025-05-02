package controller;

import connect.connectMySQL;
import model.BacSi;
import view.ThongKeBacSiPanel;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ThongKeBacSiController {

    private Connection conn;
    private ThongKeBacSiPanel bacSiView;

    public ThongKeBacSiController(ThongKeBacSiPanel view) {
        this.bacSiView = view;
        initConnection();
    }

    private void initConnection() {
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<String> getAllChuyenKhoa() {
        List<String> chuyenKhoaList = new ArrayList<>();
        String sql = "SELECT DISTINCT chuyenKhoa FROM BacSi ORDER BY chuyenKhoa";
        
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                chuyenKhoaList.add(resultSet.getString("chuyenKhoa"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(bacSiView, "Lỗi truy vấn danh sách chuyên khoa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return chuyenKhoaList;
    }
    
    public List<String> getAllPhongKham() {
        List<String> phongKhamList = new ArrayList<>();
        
        // Query the PhongKham table for names of clinics
        String sql = "SELECT tenPhong FROM PhongKham ORDER BY tenPhong";
            
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                phongKhamList.add(resultSet.getString("tenPhong"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(bacSiView, 
                "Lỗi truy vấn danh sách phòng khám: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return phongKhamList;
    }
    
    public Map<String, Integer> thongKeBacSiTheoChuyenKhoa(String chuyenKhoa) {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql;
        
        if (chuyenKhoa != null && !chuyenKhoa.equals("Tất cả")) {
            sql = "SELECT chuyenKhoa, COUNT(*) AS soLuong FROM BacSi WHERE chuyenKhoa = ? GROUP BY chuyenKhoa ORDER BY chuyenKhoa";
        } else {
            sql = "SELECT chuyenKhoa, COUNT(*) AS soLuong FROM BacSi GROUP BY chuyenKhoa ORDER BY chuyenKhoa";
        }
        
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            if (chuyenKhoa != null && !chuyenKhoa.equals("Tất cả")) {
                preparedStatement.setString(1, chuyenKhoa);
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                String tenChuyenKhoa = resultSet.getString("chuyenKhoa");
                int soLuong = resultSet.getInt("soLuong");
                result.put(tenChuyenKhoa, soLuong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(bacSiView, "Lỗi truy vấn thống kê bác sĩ theo chuyên khoa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return result;
    }
    
    public Map<String, Integer> thongKeBacSiTheoPhongKham(String phongKham) {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql;
        
        if (phongKham != null && !phongKham.equals("Tất cả")) {
            // Join BacSi with PhongKham to get the count by clinic name
            sql = "SELECT pk.tenPhong, COUNT(bs.idBacSi) AS soLuong FROM BacSi bs " +
                  "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                  "WHERE pk.tenPhong = ? GROUP BY pk.tenPhong ORDER BY pk.tenPhong";
        } else {
            // Get the count of doctors for all clinics
            sql = "SELECT pk.tenPhong, COUNT(bs.idBacSi) AS soLuong FROM BacSi bs " +
                  "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                  "GROUP BY pk.tenPhong ORDER BY pk.tenPhong";
        }
        
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            if (phongKham != null && !phongKham.equals("Tất cả")) {
                preparedStatement.setString(1, phongKham);
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                String tenPhongKham = resultSet.getString("tenPhong");
                int soLuong = resultSet.getInt("soLuong");
                result.put(tenPhongKham, soLuong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(bacSiView, "Lỗi truy vấn thống kê bác sĩ theo phòng khám!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return result;
    }
    
    public List<Object[]> thongKeBacSiCoNhieuLichHen(int nam, int thang) {
        List<Object[]> result = new ArrayList<>();
        String sql;
        
        if (thang > 0) {
            sql = "SELECT bs.idBacSi, bs.hoTenBacSi, bs.chuyenKhoa, pk.tenPhong, COUNT(lh.idLichHen) AS soLuongLichHen " +
                  "FROM BacSi bs " +
                  "LEFT JOIN LichHen lh ON bs.idBacSi = lh.idBacSi " +
                  "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                  "WHERE YEAR(lh.ngayHen) = ? AND MONTH(lh.ngayHen) = ? " +
                  "GROUP BY bs.idBacSi, bs.hoTenBacSi, bs.chuyenKhoa, pk.tenPhong " +
                  "ORDER BY soLuongLichHen DESC";
        } else {
            sql = "SELECT bs.idBacSi, bs.hoTenBacSi, bs.chuyenKhoa, pk.tenPhong, COUNT(lh.idLichHen) AS soLuongLichHen " +
                  "FROM BacSi bs " +
                  "LEFT JOIN LichHen lh ON bs.idBacSi = lh.idBacSi " +
                  "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                  "WHERE YEAR(lh.ngayHen) = ? " +
                  "GROUP BY bs.idBacSi, bs.hoTenBacSi, bs.chuyenKhoa, pk.tenPhong " +
                  "ORDER BY soLuongLichHen DESC";
        }
        
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, nam);
            if (thang > 0) {
                preparedStatement.setInt(2, thang);
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                int idBacSi = resultSet.getInt("idBacSi");
                String hoTen = resultSet.getString("hoTenBacSi");
                String chuyenKhoa = resultSet.getString("chuyenKhoa");
                String phongKham = resultSet.getString("tenPhong");
                int soLuongLichHen = resultSet.getInt("soLuongLichHen");
                
                result.add(new Object[]{idBacSi, hoTen, chuyenKhoa, phongKham, soLuongLichHen});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(bacSiView, "Lỗi truy vấn thống kê bác sĩ có nhiều lịch hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return result;
    }
    
    public List<Object[]> thongKeBacSiCoNhieuCaDieuTri(int nam, int thang) {
        List<Object[]> result = new ArrayList<>();
        String sql;
        
        if (thang > 0) {
            sql = "SELECT bs.idBacSi, bs.hoTenBacSi, bs.chuyenKhoa, pk.tenPhong, COUNT(dt.idDieuTri) AS soLuongDieuTri " +
                  "FROM BacSi bs " +
                  "LEFT JOIN DieuTri dt ON bs.idBacSi = dt.idBacSi " +
                  "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                  "WHERE YEAR(dt.ngayDieuTri) = ? AND MONTH(dt.ngayDieuTri) = ? " +
                  "GROUP BY bs.idBacSi, bs.hoTenBacSi, bs.chuyenKhoa, pk.tenPhong " +
                  "ORDER BY soLuongDieuTri DESC";
        } else {
            sql = "SELECT bs.idBacSi, bs.hoTenBacSi, bs.chuyenKhoa, pk.tenPhong, COUNT(dt.idDieuTri) AS soLuongDieuTri " +
                  "FROM BacSi bs " +
                  "LEFT JOIN DieuTri dt ON bs.idBacSi = dt.idBacSi " +
                  "JOIN PhongKham pk ON bs.idPhongKham = pk.idPhongKham " +
                  "WHERE YEAR(dt.ngayDieuTri) = ? " +
                  "GROUP BY bs.idBacSi, bs.hoTenBacSi, bs.chuyenKhoa, pk.tenPhong " +
                  "ORDER BY soLuongDieuTri DESC";
        }
        
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, nam);
            if (thang > 0) {
                preparedStatement.setInt(2, thang);
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                int idBacSi = resultSet.getInt("idBacSi");
                String hoTen = resultSet.getString("hoTenBacSi");
                String chuyenKhoa = resultSet.getString("chuyenKhoa");
                String phongKham = resultSet.getString("tenPhong");
                int soLuongDieuTri = resultSet.getInt("soLuongDieuTri");
                
                result.add(new Object[]{idBacSi, hoTen, chuyenKhoa, phongKham, soLuongDieuTri});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(bacSiView, "Lỗi truy vấn thống kê bác sĩ có nhiều ca điều trị!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        return result;
    }
    
    public Map<String, Integer> thongKeBacSiTheoKinhNghiem(String chuyenKhoa) {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql;
        
        // Khởi tạo các khoảng kinh nghiệm
        result.put("Dưới 5 năm", 0);
        result.put("5-10 năm", 0);
        result.put("10-15 năm", 0);
        result.put("15-20 năm", 0);
        result.put("Trên 20 năm", 0);
        
        if (chuyenKhoa != null && !chuyenKhoa.equals("Tất cả")) {
            sql = "SELECT kinhNghiem FROM BacSi WHERE chuyenKhoa = ?";
        } else {
            sql = "SELECT kinhNghiem FROM BacSi";
        }
        
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            if (chuyenKhoa != null && !chuyenKhoa.equals("Tất cả")) {
                preparedStatement.setString(1, chuyenKhoa);
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                int kinhNghiem = resultSet.getInt("kinhNghiem");
                
                // Phân loại theo khoảng kinh nghiệm
                if (kinhNghiem < 5) {
                    result.put("Dưới 5 năm", result.get("Dưới 5 năm") + 1);
                } else if (kinhNghiem >= 5 && kinhNghiem < 10) {
                    result.put("5-10 năm", result.get("5-10 năm") + 1);
                } else if (kinhNghiem >= 10 && kinhNghiem < 15) {
                    result.put("10-15 năm", result.get("10-15 năm") + 1);
                } else if (kinhNghiem >= 15 && kinhNghiem < 20) {
                    result.put("15-20 năm", result.get("15-20 năm") + 1);
                } else {
                    result.put("Trên 20 năm", result.get("Trên 20 năm") + 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(bacSiView, "Lỗi truy vấn thống kê bác sĩ theo kinh nghiệm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        // Loại bỏ các khoảng không có bác sĩ
        Map<String, Integer> filteredResult = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            if (entry.getValue() > 0) {
                filteredResult.put(entry.getKey(), entry.getValue());
            }
        }
        
        return filteredResult;
    }
}