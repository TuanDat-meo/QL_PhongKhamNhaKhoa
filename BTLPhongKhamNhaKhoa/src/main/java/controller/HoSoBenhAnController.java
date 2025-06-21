package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.HoSoBenhAn;
import connect.connectMySQL;

public class HoSoBenhAnController {
    private Connection conn;

    public HoSoBenhAnController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tìm ID bệnh nhân bằng tên (fallback method khi appointment.getIdBenhNhan() = 0)
     */
    public int timIdBenhNhanBangTen(String hoTenBenhNhan) {
        int idBenhNhan = 0;
        try {
            String query = "SELECT idBenhNhan FROM BenhNhan WHERE hoTen = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, hoTenBenhNhan);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                idBenhNhan = resultSet.getInt("idBenhNhan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idBenhNhan;
    }

    /**
     * Debug method: Hiển thị tất cả bệnh nhân trong database
     */
    public void debugTatCaBenhNhan() {
        try {
            String query = "SELECT idBenhNhan, hoTen FROM BenhNhan ORDER BY idBenhNhan";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                // Debug output removed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Debug method: Hiển thị tất cả appointment trong database
     */
    public void debugTatCaAppointment() {
        try {
            String query = "SELECT lh.idLichHen, lh.idBenhNhan, bn.hoTen, lh.ngayHen " +
                          "FROM LichHen lh " +
                          "LEFT JOIN BenhNhan bn ON lh.idBenhNhan = bn.idBenhNhan " +
                          "ORDER BY lh.idLichHen";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                // Debug output removed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<HoSoBenhAn> layDanhSachHoSoBenhAn() {
        List<HoSoBenhAn> danhSachHoSoBenhAn = new ArrayList<>();
        try {
            String query = "SELECT * FROM HoSoBenhAn";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                HoSoBenhAn hoSoBenhAn = new HoSoBenhAn();
                hoSoBenhAn.setIdHoSo(resultSet.getInt("idHoSo"));
                hoSoBenhAn.setIdBenhNhan(resultSet.getInt("idBenhNhan"));
                hoSoBenhAn.setChuanDoan(resultSet.getString("chuanDoan"));
                hoSoBenhAn.setGhiChu(resultSet.getString("ghiChu"));
                hoSoBenhAn.setNgayTao(resultSet.getDate("ngayTao"));
                hoSoBenhAn.setTrangThai(resultSet.getString("trangThai"));

                danhSachHoSoBenhAn.add(hoSoBenhAn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachHoSoBenhAn;
    }

    public List<HoSoBenhAn> layHoSoBenhAnTheoIdBenhNhan(int idBenhNhan) {
        List<HoSoBenhAn> danhSachHoSoBenhAn = new ArrayList<>();
        try {
            String query = "SELECT * FROM HoSoBenhAn WHERE idBenhNhan = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, idBenhNhan);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                HoSoBenhAn hoSoBenhAn = new HoSoBenhAn();
                hoSoBenhAn.setIdHoSo(resultSet.getInt("idHoSo"));
                hoSoBenhAn.setIdBenhNhan(resultSet.getInt("idBenhNhan"));
                hoSoBenhAn.setChuanDoan(resultSet.getString("chuanDoan"));
                hoSoBenhAn.setGhiChu(resultSet.getString("ghiChu"));
                hoSoBenhAn.setNgayTao(resultSet.getDate("ngayTao"));
                hoSoBenhAn.setTrangThai(resultSet.getString("trangThai"));

                danhSachHoSoBenhAn.add(hoSoBenhAn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachHoSoBenhAn;
    }

    public void themHoSoBenhAn(HoSoBenhAn hoSoBenhAn) {
        try {
            String query = "INSERT INTO HoSoBenhAn (idBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, hoSoBenhAn.getIdBenhNhan());
            preparedStatement.setString(2, hoSoBenhAn.getChuanDoan());
            preparedStatement.setString(3, hoSoBenhAn.getGhiChu());
            preparedStatement.setDate(4, new java.sql.Date(hoSoBenhAn.getNgayTao().getTime()));
            preparedStatement.setString(5, hoSoBenhAn.getTrangThai());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void suaHoSoBenhAn(HoSoBenhAn hoSoBenhAn) {
        try {
            String query = "UPDATE HoSoBenhAn SET chuanDoan = ?, ghiChu = ?, ngayTao = ?, trangThai = ? WHERE idHoSo = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, hoSoBenhAn.getChuanDoan());
            preparedStatement.setString(2, hoSoBenhAn.getGhiChu());
            preparedStatement.setDate(3, new java.sql.Date(hoSoBenhAn.getNgayTao().getTime()));
            preparedStatement.setString(4, hoSoBenhAn.getTrangThai());
            preparedStatement.setInt(5, hoSoBenhAn.getIdHoSo());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean xoaHoSoBenhAn(int idHoSo) {
        String sqlXoaDieuTri = "DELETE FROM DieuTri WHERE idHoSo = ?";
        String sqlXoaHoSo = "DELETE FROM HoSoBenhAn WHERE idHoSo = ?";
        PreparedStatement pstmtXoaDieuTri = null;
        PreparedStatement pstmtXoaHoSo = null;

        try {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Xóa các bản ghi liên quan trong bảng DieuTri
            pstmtXoaDieuTri = conn.prepareStatement(sqlXoaDieuTri);
            pstmtXoaDieuTri.setInt(1, idHoSo);
            pstmtXoaDieuTri.executeUpdate();

            // Xóa bản ghi trong bảng HoSoBenhAn
            pstmtXoaHoSo = conn.prepareStatement(sqlXoaHoSo);
            pstmtXoaHoSo.setInt(1, idHoSo);
            int affectedRows = pstmtXoaHoSo.executeUpdate();

            conn.commit(); // Commit transaction nếu cả hai thao tác thành công
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction nếu có lỗi
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (pstmtXoaDieuTri != null) pstmtXoaDieuTri.close();
                if (pstmtXoaHoSo != null) pstmtXoaHoSo.close();
                if (conn != null) conn.setAutoCommit(true); // Đặt lại auto-commit
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public HoSoBenhAn timKiemHoSoBenhAnTheoId(int idHoSo) {
        HoSoBenhAn hoSoBenhAn = null;
        try {
            String query = "SELECT * FROM HoSoBenhAn WHERE idHoSo = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, idHoSo);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                hoSoBenhAn = new HoSoBenhAn();
                hoSoBenhAn.setIdHoSo(resultSet.getInt("idHoSo"));
                hoSoBenhAn.setIdBenhNhan(resultSet.getInt("idBenhNhan"));
                hoSoBenhAn.setChuanDoan(resultSet.getString("chuanDoan"));
                hoSoBenhAn.setGhiChu(resultSet.getString("ghiChu"));
                hoSoBenhAn.setNgayTao(resultSet.getDate("ngayTao"));
                hoSoBenhAn.setTrangThai(resultSet.getString("trangThai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hoSoBenhAn;
    }
}