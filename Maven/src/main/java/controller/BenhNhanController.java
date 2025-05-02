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
            
            // Thiết lập character set cho kết nối
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET NAMES 'utf8'");
                stmt.execute("SET CHARACTER SET utf8");
                stmt.execute("SET character_set_client = utf8");
                stmt.execute("SET character_set_connection = utf8");
                stmt.execute("SET character_set_results = utf8");
                stmt.execute("SET collation_connection = utf8_general_ci");
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
        try {
            // Bước 1: Xóa tất cả các bản ghi từ bảng DieuTri liên quan đến Hồ Sơ Bệnh Án của Bệnh Nhân này
            String sqlDieuTri = "DELETE FROM DieuTri WHERE idHoSo IN (SELECT idHoSo FROM HoSoBenhAn WHERE idBenhNhan = ?)";
            try (PreparedStatement stmtDieuTri = conn.prepareStatement(sqlDieuTri)) {
                stmtDieuTri.setInt(1, idBenhNhan);
                stmtDieuTri.executeUpdate();
            }

            // Bước 2: Xóa tất cả các bản ghi từ bảng DoanhThu liên quan đến các Hóa Đơn của Bệnh Nhân này
            String sqlDoanhThu = "DELETE FROM DoanhThu WHERE idHoaDon IN (SELECT idHoaDon FROM HoaDon WHERE idBenhNhan = ?)";
            try (PreparedStatement stmtDoanhThu = conn.prepareStatement(sqlDoanhThu)) {
                stmtDoanhThu.setInt(1, idBenhNhan);
                stmtDoanhThu.executeUpdate();
            }

            // Bước 3: Xóa tất cả các bản ghi từ ChiTietDonThuoc liên quan đến các DonThuoc của BenhNhan này
            String sqlChiTietDonThuoc = "DELETE FROM ChiTietDonThuoc WHERE idDonThuoc IN (SELECT idDonThuoc FROM DonThuoc WHERE idBenhNhan = ?)";
            try (PreparedStatement stmtChiTietDonThuoc = conn.prepareStatement(sqlChiTietDonThuoc)) {
                stmtChiTietDonThuoc.setInt(1, idBenhNhan);
                stmtChiTietDonThuoc.executeUpdate();
            }

            // Bước 4: Xóa tất cả các bản ghi từ DonThuoc liên quan đến BenhNhan này
            String sqlDonThuoc = "DELETE FROM DonThuoc WHERE idBenhNhan = ?";
            try (PreparedStatement stmtDonThuoc = conn.prepareStatement(sqlDonThuoc)) {
                stmtDonThuoc.setInt(1, idBenhNhan);
                stmtDonThuoc.executeUpdate();
            }

            // Bước 5: Xóa tất cả các bản ghi từ bảng HoaDon liên quan đến BenhNhan này
            String sqlHoaDon = "DELETE FROM HoaDon WHERE idBenhNhan = ?";
            try (PreparedStatement stmtHoaDon = conn.prepareStatement(sqlHoaDon)) {
                stmtHoaDon.setInt(1, idBenhNhan);
                stmtHoaDon.executeUpdate();
            }

            // Bước 6: Xóa tất cả các bản ghi từ bảng HoSoBenhAn liên quan đến Bệnh Nhân này
            String sqlHoSoBenhAn = "DELETE FROM HoSoBenhAn WHERE idBenhNhan = ?";
            try (PreparedStatement stmtHoSoBenhAn = conn.prepareStatement(sqlHoSoBenhAn)) {
                stmtHoSoBenhAn.setInt(1, idBenhNhan);
                stmtHoSoBenhAn.executeUpdate();
            }

            // Bước 7: Xóa tất cả các bản ghi từ LichHen liên quan đến BenhNhan này
            String sqlLichHen = "DELETE FROM LichHen WHERE idBenhNhan = ?";
            try (PreparedStatement stmtLichHen = conn.prepareStatement(sqlLichHen)) {
                stmtLichHen.setInt(1, idBenhNhan);
                stmtLichHen.executeUpdate();
            }

            // Bước 8: Cuối cùng, xóa bản ghi của bệnh nhân
            String sqlBenhNhan = "DELETE FROM BenhNhan WHERE idBenhNhan = ?";
            try (PreparedStatement stmtBenhNhan = conn.prepareStatement(sqlBenhNhan)) {
                stmtBenhNhan.setInt(1, idBenhNhan);
                stmtBenhNhan.executeUpdate();
            }
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
    public BenhNhan timKiemBenhNhanTheoId(int idBenhNhan) {
        BenhNhan benhNhan = null;
        String sql = "SELECT idBenhNhan, hoTen, ngaySinh, gioiTinh, soDienThoai, cccd, diaChi FROM BenhNhan WHERE idBenhNhan = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idBenhNhan);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                benhNhan = new BenhNhan();
                benhNhan.setIdBenhNhan(rs.getInt("idBenhNhan"));
                benhNhan.setHoTen(rs.getString("hoTen"));
                benhNhan.setNgaySinh(rs.getDate("ngaySinh"));
                benhNhan.setGioiTinh(rs.getString("gioiTinh"));
                benhNhan.setSoDienThoai(rs.getString("soDienThoai"));
                benhNhan.setCccd(rs.getString("cccd"));
                benhNhan.setDiaChi(rs.getString("diaChi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return benhNhan;
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
    public List<BenhNhan> getAllBenhNhan() {
        List<BenhNhan> danhSach = new ArrayList<>();
        String sql = "SELECT idBenhNhan, hoTen, ngaySinh, gioiTinh, soDienThoai, cccd, diaChi FROM BenhNhan";
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
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi SQLException ở đây, ví dụ: log lỗi hoặc thông báo cho người dùng
        }
        return danhSach;
    }
}