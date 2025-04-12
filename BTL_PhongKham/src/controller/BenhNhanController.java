package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connect.connectMySQL;
import model.BenhNhan;
public class BenhNhanController {
    private Connection conn;

    public BenhNhanController() {
    	try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BenhNhan> layDanhSachBenhNhan() throws SQLException {
        List<BenhNhan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM BenhNhan";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BenhNhan benhNhan = new BenhNhan(
                        rs.getInt("idBenhNhan"),
                        rs.getString("hoTen"),
                        rs.getDate("ngaySinh"),
                        rs.getString("gioiTinh"),
                        rs.getString("soDienThoai"),
                        rs.getString("cccd"),
                        rs.getString("diaChi")
                );
                danhSach.add(benhNhan);
            }
        }
        return danhSach;
    }

    public void themBenhNhan(BenhNhan benhNhan) throws SQLException {
        String sql = "INSERT INTO BenhNhan (hoTen, ngaySinh, gioiTinh, soDienThoai, cccd, diaChi) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, benhNhan.getHoTen());
            stmt.setDate(2, new java.sql.Date(benhNhan.getNgaySinh().getTime()));
            stmt.setString(3, benhNhan.getGioiTinh());
            stmt.setString(4, benhNhan.getSoDienThoai());
            stmt.setString(5, benhNhan.getCccd());
            stmt.setString(6, benhNhan.getDiaChi());
            stmt.executeUpdate();
        }
    }

    public void capNhatBenhNhan(BenhNhan benhNhan) throws SQLException {
        String sql = "UPDATE BenhNhan SET hoTen = ?, ngaySinh = ?, gioiTinh = ?, soDienThoai = ?, diaChi = ? WHERE idBenhNhan = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, benhNhan.getHoTen());
            stmt.setDate(2, new java.sql.Date(benhNhan.getNgaySinh().getTime()));
            stmt.setString(3, benhNhan.getGioiTinh());
            stmt.setString(4, benhNhan.getSoDienThoai());
            stmt.setString(5, benhNhan.getDiaChi());
            stmt.setInt(6, benhNhan.getIdBenhNhan());
            stmt.executeUpdate();
        }
    }
    public void xoaBenhNhan(int idBenhNhan) throws SQLException {
        // Xóa các bản ghi liên quan trong LichHen
        String sqlLichHen = "DELETE FROM LichHen WHERE idBenhNhan = ?";
        try (PreparedStatement stmtLichHen = conn.prepareStatement(sqlLichHen)) {
            stmtLichHen.setInt(1, idBenhNhan);
            stmtLichHen.executeUpdate();
        }

        // Xóa bệnh nhân
        String sqlBenhNhan = "DELETE FROM BenhNhan WHERE idBenhNhan = ?";
        try (PreparedStatement stmtBenhNhan = conn.prepareStatement(sqlBenhNhan)) {
            stmtBenhNhan.setInt(1, idBenhNhan);
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bệnh nhân: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    public List<BenhNhan> timKiemBenhNhan(String keyword) throws SQLException {
        List<BenhNhan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM BenhNhan WHERE LOWER(hoTen) LIKE LOWER(?) OR ngaySinh LIKE ? OR LOWER(gioiTinh) = LOWER(?) OR soDienThoai LIKE ? OR cccd LIKE ? OR diaChi LIKE ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + keyword + "%");
            preparedStatement.setString(2, "%" + keyword + "%");
            preparedStatement.setString(3, keyword);
            preparedStatement.setString(4, "%" + keyword + "%");
            preparedStatement.setString(5, "%" + keyword + "%");
            preparedStatement.setString(6, "%" + keyword + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    BenhNhan benhNhan = new BenhNhan(
                            resultSet.getInt("idBenhNhan"),
                            resultSet.getString("hoTen"),
                            resultSet.getDate("ngaySinh"),
                            resultSet.getString("gioiTinh"),
                            resultSet.getString("soDienThoai"),
                            resultSet.getString("cccd"),
                            resultSet.getString("diaChi")
                    );
                    danhSach.add(benhNhan);
                }
            }
        }
        return danhSach;
    }
    public List<BenhNhan> layBenhNhanTheoHoTen(String hoTen) throws SQLException {
        List<BenhNhan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM BenhNhan WHERE LOWER(hoTen) = LOWER(?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, hoTen);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    BenhNhan benhNhan = new BenhNhan(
                            resultSet.getInt("idBenhNhan"),
                            resultSet.getString("hoTen"),
                            resultSet.getDate("ngaySinh"),
                            resultSet.getString("gioiTinh"),
                            resultSet.getString("soDienThoai"),
                            resultSet.getString("cccd"),
                            resultSet.getString("diaChi")
                    );
                    danhSach.add(benhNhan);
                }
            }
        }
        return danhSach;
    }

}