package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connect.connectMySQL;
import model.HoSoBenhAn;

public class HoSoBenhAnController {
    private Connection conn;

    public HoSoBenhAnController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<HoSoBenhAn> getHoSoBenhAnByBenhNhanId(int benhNhanId) throws SQLException {
        List<HoSoBenhAn> hoSoList = new ArrayList<>();
        String sql = "SELECT * FROM HoSoBenhAn WHERE idBenhNhan = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, benhNhanId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                HoSoBenhAn hoSo = new HoSoBenhAn();
                hoSo.setIdHoSo(resultSet.getInt("idHoSo"));
                hoSo.setIdBenhNhan(resultSet.getInt("idBenhNhan"));
                hoSo.setChuanDoan(resultSet.getString("chuanDoan"));
                hoSo.setGhiChu(resultSet.getString("ghiChu"));
                hoSo.setNgayTao(resultSet.getDate("ngayTao"));
                hoSo.setTrangThai(resultSet.getString("trangThai"));
                hoSoList.add(hoSo);
            }
        }
        return hoSoList;
    }

    public boolean addHoSoBenhAn(HoSoBenhAn hoSo) throws SQLException {
        String sql = "INSERT INTO HoSoBenhAn (idBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, hoSo.getIdBenhNhan());
            preparedStatement.setString(2, hoSo.getChuanDoan());
            preparedStatement.setString(3, hoSo.getGhiChu());
            preparedStatement.setDate(4, hoSo.getNgayTao());
            preparedStatement.setString(5, hoSo.getTrangThai());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean updateHoSoBenhAn(HoSoBenhAn hoSo) throws SQLException {
        String sql = "UPDATE HoSoBenhAn SET idBenhNhan = ?, chuanDoan = ?, ghiChu = ?, ngayTao = ?, trangThai = ? WHERE idHoSo = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, hoSo.getIdBenhNhan());
            preparedStatement.setString(2, hoSo.getChuanDoan());
            preparedStatement.setString(3, hoSo.getGhiChu());
            preparedStatement.setDate(4, hoSo.getNgayTao());
            preparedStatement.setString(5, hoSo.getTrangThai());
            preparedStatement.setInt(6, hoSo.getIdHoSo());
            return preparedStatement.executeUpdate() > 0;
        }
    }
    public List<HoSoBenhAn> getAllHoSoBenhAn() throws SQLException {
        List<HoSoBenhAn> hoSoList = new ArrayList<>();
        String sql = "SELECT * FROM HoSoBenhAn";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                HoSoBenhAn hoSo = new HoSoBenhAn();
                hoSo.setIdHoSo(resultSet.getInt("idHoSo"));
                hoSo.setIdBenhNhan(resultSet.getInt("idBenhNhan"));
                hoSo.setChuanDoan(resultSet.getString("chuanDoan"));
                hoSo.setGhiChu(resultSet.getString("ghiChu"));
                hoSo.setNgayTao(resultSet.getDate("ngayTao"));
                hoSo.setTrangThai(resultSet.getString("trangThai"));
                hoSoList.add(hoSo);
            }
        }
        return hoSoList;
    }
    public boolean deleteHoSoBenhAn(int idHoSo) throws SQLException {
        String sql = "DELETE FROM HoSoBenhAn WHERE idHoSo = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoSo);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public List<HoSoBenhAn> searchHoSoBenhAn(String keyword) throws SQLException {
        List<HoSoBenhAn> hoSoList = new ArrayList<>();
        String sql = "SELECT * FROM HoSoBenhAn WHERE CONCAT(idHoSo, idBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai) LIKE ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + keyword + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                HoSoBenhAn hoSo = new HoSoBenhAn();
                hoSo.setIdHoSo(resultSet.getInt("idHoSo"));
                hoSo.setIdBenhNhan(resultSet.getInt("idBenhNhan"));
                hoSo.setChuanDoan(resultSet.getString("chuanDoan"));
                hoSo.setGhiChu(resultSet.getString("ghiChu"));
                hoSo.setNgayTao(resultSet.getDate("ngayTao"));
                hoSo.setTrangThai(resultSet.getString("trangThai"));
                hoSoList.add(hoSo);
            }
        }
        return hoSoList;
    }

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}