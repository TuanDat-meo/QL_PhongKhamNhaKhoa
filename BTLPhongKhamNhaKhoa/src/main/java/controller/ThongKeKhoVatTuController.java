package controller;

import connect.connectMySQL;
import model.KhoVatTu;
import model.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeKhoVatTuController {
    private Connection conn;
    public ThongKeKhoVatTuController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Map<String, Integer> getTongSoLuongTheoPhanLoai() {
        Map<String, Integer> tongSoLuongTheoPhanLoai = new HashMap<>();
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
    public List<KhoVatTu> getVatTuTheoPhanLoai(String phanLoai) {
        List<KhoVatTu> danhSachVatTu = new ArrayList<>();
        String sql = "SELECT * FROM KhoVatTu WHERE phanLoai = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phanLoai);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KhoVatTu vatTu = new KhoVatTu();
                vatTu.setIdVatTu(rs.getInt("idVatTu"));
                vatTu.setTenVatTu(rs.getString("tenVatTu"));
                vatTu.setSoLuong(rs.getInt("soLuong"));
                vatTu.setDonViTinh(rs.getString("donViTinh"));
                vatTu.setMaNCC(rs.getString("idNCC"));
                vatTu.setPhanLoai(rs.getString("phanLoai"));
                danhSachVatTu.add(vatTu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachVatTu;
    }
    public List<KhoVatTu> getVatTuTheoNCC(String maNCC) {
        List<KhoVatTu> danhSachVatTu = new ArrayList<>();
        String sql = "SELECT * FROM KhoVatTu WHERE idNCC = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maNCC);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KhoVatTu vatTu = new KhoVatTu();
                vatTu.setIdVatTu(rs.getInt("idVatTu"));
                vatTu.setTenVatTu(rs.getString("tenVatTu"));
                vatTu.setSoLuong(rs.getInt("soLuong"));
                vatTu.setDonViTinh(rs.getString("donViTinh"));
                vatTu.setMaNCC(rs.getString("idNCC"));
                vatTu.setPhanLoai(rs.getString("phanLoai"));
                danhSachVatTu.add(vatTu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachVatTu;
    }
    public Map<String, Integer> getTongSoLuongTheoNCC() {
        Map<String, Integer> tongSoLuongTheoNCC = new HashMap<>();
        String sql = "SELECT idNCC, SUM(soLuong) AS tongSoLuong FROM KhoVatTu GROUP BY idNCC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String maNCC = rs.getString("idNCC");
                int tongSoLuong = rs.getInt("tongSoLuong");
                tongSoLuongTheoNCC.put(maNCC, tongSoLuong);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongSoLuongTheoNCC;
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
    public String getTenNhaCungCap(String maNCC) {
        String tenNCC = null;
        String sql = "SELECT tenNCC FROM NhaCungCap WHERE idNCC = ?";
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
    public List<KhoVatTu> getVatTuDuoiNguong(int soLuongToiThieu) {
        List<KhoVatTu> danhSachVatTu = new ArrayList<>();
        String sql = "SELECT * FROM KhoVatTu WHERE soLuong < ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, soLuongToiThieu);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KhoVatTu vatTu = new KhoVatTu();
                vatTu.setIdVatTu(rs.getInt("idVatTu"));
                vatTu.setTenVatTu(rs.getString("tenVatTu"));
                vatTu.setSoLuong(rs.getInt("soLuong"));
                vatTu.setDonViTinh(rs.getString("donViTinh"));
                vatTu.setMaNCC(rs.getString("idNCC"));
                vatTu.setPhanLoai(rs.getString("phanLoai"));
                danhSachVatTu.add(vatTu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachVatTu;
    }
    public int getTongSoVatTu() {
        int tongSo = 0;
        String sql = "SELECT COUNT(*) AS tongSo FROM KhoVatTu";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                tongSo = rs.getInt("tongSo");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongSo;
    }
    public int getTongSoLuongVatTu() {
        int tongSoLuong = 0;
        String sql = "SELECT SUM(soLuong) AS tongSoLuong FROM KhoVatTu";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                tongSoLuong = rs.getInt("tongSoLuong");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongSoLuong;
    }
}