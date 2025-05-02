package controller;

import java.sql.*;
import java.util.*;
import model.DichVu;

public class DichVuController {
    private Connection conn;

    public DichVuController(Connection conn) {
        this.conn = conn;
    }

    public List<DichVu> getDanhSach() throws SQLException {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            list.add(new DichVu(
                rs.getInt("idDichVu"),
                rs.getString("tenDichVu"),
                rs.getDouble("gia")
            ));
        }
        return list;
    }

    public void themDichVu(String ten, double gia) throws SQLException {
        String sql = "INSERT INTO DichVu (tenDichVu, gia) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, ten);
        ps.setDouble(2, gia);
        ps.executeUpdate();
    }

    public void xoaDichVu(int id) throws SQLException {
        String sql = "DELETE FROM DichVu WHERE idDichVu = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
}