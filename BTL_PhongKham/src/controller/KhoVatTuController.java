package controller;

import connect.connectMySQL;
import model.KhoVatTu;
import model.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhoVatTuController {

    private Connection conn;

    public KhoVatTuController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<KhoVatTu> getAllKhoVatTu() {
        List<KhoVatTu> danhSachVatTu = new ArrayList<>();
        String sql = "SELECT * FROM KhoVatTu";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                KhoVatTu vatTu = new KhoVatTu();
                vatTu.setIdVatTu(rs.getInt("idVatTu"));
                vatTu.setTenVatTu(rs.getString("tenVatTu"));
                vatTu.setSoLuong(rs.getInt("soLuong"));
                vatTu.setDonViTinh(rs.getString("donViTinh"));
                vatTu.setMaNCC(rs.getString("idNCC")); // Sử dụng String maNCC
                vatTu.setPhanLoai(rs.getString("phanLoai"));
                danhSachVatTu.add(vatTu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachVatTu;
    }

    public boolean addKhoVatTu(KhoVatTu vatTu) {
        String sql = "INSERT INTO KhoVatTu (tenVatTu, soLuong, donViTinh, idNCC, phanLoai) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vatTu.getTenVatTu());
            pstmt.setInt(2, vatTu.getSoLuong());
            pstmt.setString(3, vatTu.getDonViTinh());
            pstmt.setString(4, vatTu.getMaNCC()); // Sử dụng String maNCC
            pstmt.setString(5, vatTu.getPhanLoai());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateKhoVatTu(KhoVatTu vatTu) {
        String sql = "UPDATE KhoVatTu SET tenVatTu = ?, soLuong = ?, donViTinh = ?, idNCC = ?, phanLoai = ? WHERE idVatTu = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vatTu.getTenVatTu());
            pstmt.setInt(2, vatTu.getSoLuong());
            pstmt.setString(3, vatTu.getDonViTinh());
            pstmt.setString(4, vatTu.getMaNCC()); // Sử dụng String maNCC
            pstmt.setString(5, vatTu.getPhanLoai());
            pstmt.setInt(6, vatTu.getIdVatTu());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteKhoVatTu(int idVatTu) {
        String sql = "DELETE FROM KhoVatTu WHERE idVatTu = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVatTu);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<NhaCungCap> getAllNhaCungCap() {
        List<NhaCungCap> danhSachNCC = new ArrayList<>();
        String sql = "SELECT idNCC, tenNCC FROM NhaCungCap";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                danhSachNCC.add(ncc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachNCC;
    }
    public java.util.Map<String, Integer> getTongSoLuongTheoPhanLoai() {
        java.util.Map<String, Integer> tongSoLuongTheoPhanLoai = new java.util.HashMap<>();
        String sql = "SELECT phanLoai, SUM(soLuong) AS tongSoLuong FROM KhoVatTu GROUP BY phanLoai ORDER BY phanLoai";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String phanLoai = rs.getString("phanLoai");
                int tongSoLuong = rs.getInt("tongSoLuong");
                tongSoLuongTheoPhanLoai.put(phanLoai, tongSoLuong);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongSoLuongTheoPhanLoai;
    }
    public List<String> getAllPhanLoai() {
        List<String> danhSachPhanLoai = new ArrayList<>();
        String sql = "SELECT DISTINCT phanLoai FROM KhoVatTu ORDER BY phanLoai";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                danhSachPhanLoai.add(rs.getString("phanLoai"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachPhanLoai;
    }
    public String getTenNhaCungCap(String maNCC) {
        String tenNCC = null;
        String sql = "SELECT tenNCC FROM NhaCungCap WHERE idNCC = ?"; // Truy vấn bảng NhaCungCap
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maNCC);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tenNCC = rs.getString("tenNCC");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tenNCC;
    }
    public String getMaNhaCungCapTheoTen(String tenNCC) {
        String maNCC = null;
        String sql = "SELECT idNCC FROM NhaCungCap WHERE tenNCC = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tenNCC);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                maNCC = rs.getString("idNCC");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maNCC;
    }
}