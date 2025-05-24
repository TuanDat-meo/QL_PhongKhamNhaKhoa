package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connect.connectMySQL;
import model.DieuTri;

public class DieuTriController {
    private Connection conn;

    public DieuTriController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DieuTri> getDieuTriByHoSoId(int hoSoId) throws SQLException {
        List<DieuTri> dieuTriList = new ArrayList<>();
        String sql = "SELECT * FROM DieuTri WHERE idHoSo = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, hoSoId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                DieuTri dieuTri = new DieuTri();
                dieuTri.setIdDieuTri(resultSet.getInt("idDieuTri"));
                dieuTri.setIdHoSo(resultSet.getInt("idHoSo"));
                dieuTri.setIdBacSi(resultSet.getInt("idBacSi"));
                dieuTri.setMoTa(resultSet.getString("moTa"));
                dieuTri.setNgayDieuTri(resultSet.getDate("ngayDieuTri"));
                dieuTriList.add(dieuTri);
            }
        }
        return dieuTriList;
    }

    public boolean addDieuTri(DieuTri dieuTri) throws SQLException {
        String sql = "INSERT INTO DieuTri (idHoSo, idBacSi, moTa, ngayDieuTri) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, dieuTri.getIdHoSo());
            preparedStatement.setInt(2, dieuTri.getIdBacSi());
            preparedStatement.setString(3, dieuTri.getMoTa());
            preparedStatement.setDate(4, dieuTri.getNgayDieuTri());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean updateDieuTri(DieuTri dieuTri) throws SQLException {
        String sql = "UPDATE DieuTri SET idHoSo = ?, idBacSi = ?, moTa = ?, ngayDieuTri = ? WHERE idDieuTri = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, dieuTri.getIdHoSo());
            preparedStatement.setInt(2, dieuTri.getIdBacSi());
            preparedStatement.setString(3, dieuTri.getMoTa());
            preparedStatement.setDate(4, dieuTri.getNgayDieuTri());
            preparedStatement.setInt(5, dieuTri.getIdDieuTri());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteDieuTri(int idDieuTri) throws SQLException {
        String sql = "DELETE FROM DieuTri WHERE idDieuTri = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idDieuTri);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public List<DieuTri> searchDieuTri(String keyword) throws SQLException {
        List<DieuTri> dieuTriList = new ArrayList<>();
        String sql = "SELECT * FROM DieuTri WHERE CONCAT(idDieuTri, idHoSo, idBacSi, moTa, ngayDieuTri) LIKE ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + keyword + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                DieuTri dieuTri = new DieuTri();
                dieuTri.setIdDieuTri(resultSet.getInt("idDieuTri"));
                dieuTri.setIdHoSo(resultSet.getInt("idHoSo"));
                dieuTri.setIdBacSi(resultSet.getInt("idBacSi"));
                dieuTri.setMoTa(resultSet.getString("moTa"));
                dieuTri.setNgayDieuTri(resultSet.getDate("ngayDieuTri"));
                dieuTriList.add(dieuTri);
            }
        }
        return dieuTriList;
    }

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}