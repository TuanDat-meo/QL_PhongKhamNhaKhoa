// controller.HoaDonController.java
package controller;

import model.HoaDon;
import model.ThanhToanBenhNhan; // Import model ThanhToanBenhNhan
import connect.connectMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDonController {
    private List<HoaDon> danhSachHoaDon;
    private Connection conn;

    public HoaDonController() {
        this.danhSachHoaDon = new ArrayList<>();
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
            loadAllHoaDonFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAllHoaDonFromDB() {
        danhSachHoaDon.clear();
        String sql = "SELECT idHoaDon, idBenhNhan, ngayTao, tongTien, trangThai FROM HoaDon";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                HoaDon hoaDon = new HoaDon();
                hoaDon.setIdHoaDon(rs.getInt("idHoaDon"));
                hoaDon.setIdBenhNhan(rs.getInt("idBenhNhan"));
                hoaDon.setNgayTao(rs.getDate("ngayTao"));
                hoaDon.setTongTien(rs.getDouble("tongTien"));
                hoaDon.setTrangThai(rs.getString("trangThai"));
                danhSachHoaDon.add(hoaDon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<HoaDon> layDanhSachHoaDon() {
        return danhSachHoaDon;
    }

    public HoaDon layHoaDonTheoId(int id) {
        for (HoaDon hoaDon : danhSachHoaDon) {
            if (hoaDon.getIdHoaDon() == id) {
                return hoaDon;
            }
        }
        return null;
    }

    public void themHoaDon(HoaDon hoaDon) {
        String sql = "INSERT INTO HoaDon (idBenhNhan, ngayTao, tongTien, trangThai) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, hoaDon.getIdBenhNhan());
            pstmt.setDate(2, new java.sql.Date(hoaDon.getNgayTao().getTime()));
            pstmt.setDouble(3, hoaDon.getTongTien());
            pstmt.setString(4, hoaDon.getTrangThai());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        hoaDon.setIdHoaDon(generatedKeys.getInt(1));
                        danhSachHoaDon.add(hoaDon);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void capNhatHoaDon(HoaDon hoaDon) {
        String sql = "UPDATE HoaDon SET idBenhNhan = ?, ngayTao = ?, tongTien = ?, trangThai = ? WHERE idHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hoaDon.getIdBenhNhan());
            pstmt.setDate(2, new java.sql.Date(hoaDon.getNgayTao().getTime()));
            pstmt.setDouble(3, hoaDon.getTongTien());
            pstmt.setString(4, hoaDon.getTrangThai());
            pstmt.setInt(5, hoaDon.getIdHoaDon());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                for (int i = 0; i < danhSachHoaDon.size(); i++) {
                    if (danhSachHoaDon.get(i).getIdHoaDon() == hoaDon.getIdHoaDon()) {
                        danhSachHoaDon.set(i, hoaDon);
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void xoaHoaDon(int id) {
        String sql = "DELETE FROM HoaDon WHERE idHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                danhSachHoaDon.removeIf(hoaDon -> hoaDon.getIdHoaDon() == id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 // Trong HoaDonDAO.java (hoặc lớp DAO bạn đang dùng)
    public boolean xoaDoanhThuTheoHoaDonId(int idHoaDon) throws SQLException {
        String sql = "DELETE FROM DoanhThu WHERE idHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHoaDon);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows >= 0; // Trả về true nếu xóa thành công (có thể không có bản ghi nào)
        }
    }
    // Các phương thức liên quan đến ThanhToanBenhNhan

    public ThanhToanBenhNhan layThanhToanTheoIdHoaDon(int idHoaDon) {
        String sql = "SELECT idThanhToan, idHoaDon, soTien, hinhThucThanhToan, maQR, trangThai FROM ThanhToanBenhNhan WHERE idHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHoaDon);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ThanhToanBenhNhan thanhToan = new ThanhToanBenhNhan();
                thanhToan.setIdThanhToan(rs.getInt("idThanhToan"));
                thanhToan.setIdHoaDon(rs.getInt("idHoaDon"));
                thanhToan.setSoTien(rs.getDouble("soTien"));
                thanhToan.setHinhThucThanhToan(rs.getString("hinhThucThanhToan"));
                thanhToan.setMaQR(rs.getString("maQR"));
                thanhToan.setTrangThai(rs.getString("trangThai"));
                return thanhToan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void themThanhToan(ThanhToanBenhNhan thanhToan) {
        String sql = "INSERT INTO ThanhToanBenhNhan (idHoaDon, soTien, hinhThucThanhToan, maQR, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, thanhToan.getIdHoaDon());
            pstmt.setDouble(2, thanhToan.getSoTien());
            pstmt.setString(3, thanhToan.getHinhThucThanhToan());
            pstmt.setString(4, thanhToan.getMaQR());
            pstmt.setString(5, thanhToan.getTrangThai());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        thanhToan.setIdThanhToan(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void capNhatThanhToan(ThanhToanBenhNhan thanhToan) {
        String sql = "UPDATE ThanhToanBenhNhan SET soTien = ?, hinhThucThanhToan = ?, maQR = ?, trangThai = ? WHERE idThanhToan = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, thanhToan.getSoTien());
            pstmt.setString(2, thanhToan.getHinhThucThanhToan());
            pstmt.setString(3, thanhToan.getMaQR());
            pstmt.setString(4, thanhToan.getTrangThai());
            pstmt.setInt(5, thanhToan.getIdThanhToan());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}