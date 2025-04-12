package controller;

import model.HoaDon;
import model.ThanhToanBenhNhan;
import connect.connectMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDonController {
    private List<HoaDon> danhSachHoaDon;
    private Connection conn;
    private DoanhThuController doanhThuController;

    public HoaDonController() {
        this.danhSachHoaDon = new ArrayList<>();
        this.doanhThuController = new DoanhThuController();  // Khởi tạo DoanhThuController để tích hợp
        // Thiết lập tham chiếu hai chiều
        this.doanhThuController.setHoaDonController(this);
        
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
        
        // Nếu không tìm thấy trong danh sách, thử tìm trong database
        String sql = "SELECT idHoaDon, idBenhNhan, ngayTao, tongTien, trangThai FROM HoaDon WHERE idHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                HoaDon hoaDon = new HoaDon();
                hoaDon.setIdHoaDon(rs.getInt("idHoaDon"));
                hoaDon.setIdBenhNhan(rs.getInt("idBenhNhan"));
                hoaDon.setNgayTao(rs.getDate("ngayTao"));
                hoaDon.setTongTien(rs.getDouble("tongTien"));
                hoaDon.setTrangThai(rs.getString("trangThai"));
                return hoaDon;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                        
                        // Nếu hóa đơn có trạng thái là "DaThanhToan" thì tự động thêm vào doanh thu
                        if ("DaThanhToan".equals(hoaDon.getTrangThai())) {
                            themHoaDonVaoDoanhThu(hoaDon);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void capNhatHoaDon(HoaDon hoaDon) {
        // Lưu trạng thái cũ của hóa đơn trước khi cập nhật
        String trangThaiCu = null;
        double tongTienCu = 0;
        for (HoaDon hd : danhSachHoaDon) {
            if (hd.getIdHoaDon() == hoaDon.getIdHoaDon()) {
                trangThaiCu = hd.getTrangThai();
                tongTienCu = hd.getTongTien();
                break;
            }
        }
        
        // Nếu không tìm thấy trong danh sách, thử lấy từ database
        if (trangThaiCu == null) {
            HoaDon hdCu = layHoaDonTheoId(hoaDon.getIdHoaDon());
            if (hdCu != null) {
                trangThaiCu = hdCu.getTrangThai();
                tongTienCu = hdCu.getTongTien();
            }
        }
        
        String sql = "UPDATE HoaDon SET idBenhNhan = ?, ngayTao = ?, tongTien = ?, trangThai = ? WHERE idHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hoaDon.getIdBenhNhan());
            pstmt.setDate(2, new java.sql.Date(hoaDon.getNgayTao().getTime()));
            pstmt.setDouble(3, hoaDon.getTongTien());
            pstmt.setString(4, hoaDon.getTrangThai());
            pstmt.setInt(5, hoaDon.getIdHoaDon());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Cập nhật danh sách local nếu tìm thấy
                boolean found = false;
                for (int i = 0; i < danhSachHoaDon.size(); i++) {
                    if (danhSachHoaDon.get(i).getIdHoaDon() == hoaDon.getIdHoaDon()) {
                        danhSachHoaDon.set(i, hoaDon);
                        found = true;
                        break;
                    }
                }
                
                // Nếu không tìm thấy trong danh sách thì thêm vào
                if (!found) {
                    danhSachHoaDon.add(hoaDon);
                }
                
                // Kiểm tra xem trạng thái có thay đổi thành "DaThanhToan" hay không
                if ("DaThanhToan".equals(hoaDon.getTrangThai()) && !"DaThanhToan".equals(trangThaiCu)) {
                    // Nếu trạng thái mới là "DaThanhToan" và trạng thái cũ không phải, thêm vào doanh thu
                    themHoaDonVaoDoanhThu(hoaDon);
                } else if (!"DaThanhToan".equals(hoaDon.getTrangThai()) && "DaThanhToan".equals(trangThaiCu)) {
                    // Nếu trạng thái mới không phải "DaThanhToan" nhưng trạng thái cũ là, xóa khỏi doanh thu
                    doanhThuController.xoaDoanhThuTheoHoaDonId(hoaDon.getIdHoaDon());
                } else if ("DaThanhToan".equals(hoaDon.getTrangThai()) && "DaThanhToan".equals(trangThaiCu)) {
                    // Kiểm tra xem tổng tiền có thay đổi không
                    if (Math.abs(hoaDon.getTongTien() - tongTienCu) > 0.001) {
                        // Cập nhật doanh thu nếu số tiền thay đổi
                        capNhatDoanhThuTheoHoaDon(hoaDon);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void xoaHoaDon(int id) {
        // Trước khi xóa hóa đơn, cần xóa bản ghi trong DoanhThu
        try {
            doanhThuController.xoaDoanhThuTheoHoaDonId(id);
        } catch (Exception e) {
            e.printStackTrace();
            // Tiếp tục xóa HoaDon ngay cả khi xóa DoanhThu thất bại
        }
        
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
    
    // Phương thức để thêm hóa đơn vào doanh thu
    private void themHoaDonVaoDoanhThu(HoaDon hoaDon) {
        // Kiểm tra xem hóa đơn đã tồn tại trong doanh thu chưa
        if (!doanhThuController.kiemTraHoaDonTrongDoanhThu(hoaDon.getIdHoaDon())) {
            doanhThuController.themDoanhThuTuHoaDon(hoaDon.getIdHoaDon(), hoaDon.getTongTien());
        }
    }
    
    // Phương thức để cập nhật doanh thu theo hóa đơn
    private void capNhatDoanhThuTheoHoaDon(HoaDon hoaDon) {
        // Xóa doanh thu cũ và tạo mới (cách tiếp cận đơn giản)
        if (doanhThuController.xoaDoanhThuTheoHoaDonId(hoaDon.getIdHoaDon())) {
            doanhThuController.themDoanhThuTuHoaDon(hoaDon.getIdHoaDon(), hoaDon.getTongTien());
        }
    }

    // Phương thức liên quan đến ThanhToanBenhNhan
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
                        
                        // Nếu thanh toán thành công, cập nhật trạng thái hóa đơn
                        if ("ThanhToanThanhCong".equals(thanhToan.getTrangThai())) {
                            HoaDon hoaDon = layHoaDonTheoId(thanhToan.getIdHoaDon());
                            if (hoaDon != null) {
                                hoaDon.setTrangThai("DaThanhToan");
                                // Cập nhật tổng tiền từ số tiền thanh toán nếu cần
                                if (Math.abs(hoaDon.getTongTien() - thanhToan.getSoTien()) > 0.001) {
                                    hoaDon.setTongTien(thanhToan.getSoTien());
                                }
                                capNhatHoaDon(hoaDon);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void capNhatThanhToan(ThanhToanBenhNhan thanhToan) {
        String trangThaiCu = null;
        ThanhToanBenhNhan thanhToanCu = layThanhToanTheoIdHoaDon(thanhToan.getIdHoaDon());
        if (thanhToanCu != null) {
            trangThaiCu = thanhToanCu.getTrangThai();
        }
        
        String sql = "UPDATE ThanhToanBenhNhan SET soTien = ?, hinhThucThanhToan = ?, maQR = ?, trangThai = ? WHERE idThanhToan = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, thanhToan.getSoTien());
            pstmt.setString(2, thanhToan.getHinhThucThanhToan());
            pstmt.setString(3, thanhToan.getMaQR());
            pstmt.setString(4, thanhToan.getTrangThai());
            pstmt.setInt(5, thanhToan.getIdThanhToan());
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Nếu trạng thái thanh toán thay đổi thành công hoặc số tiền thay đổi
                if (("ThanhToanThanhCong".equals(thanhToan.getTrangThai()) && !"ThanhToanThanhCong".equals(trangThaiCu)) ||
                    ("ThanhToanThanhCong".equals(thanhToan.getTrangThai()) && thanhToanCu != null && 
                     Math.abs(thanhToan.getSoTien() - thanhToanCu.getSoTien()) > 0.001)) {
                    
                    HoaDon hoaDon = layHoaDonTheoId(thanhToan.getIdHoaDon());
                    if (hoaDon != null) {
                        hoaDon.setTrangThai("DaThanhToan");
                        // Cập nhật tổng tiền từ số tiền thanh toán nếu thay đổi
                        if (Math.abs(hoaDon.getTongTien() - thanhToan.getSoTien()) > 0.001) {
                            hoaDon.setTongTien(thanhToan.getSoTien());
                        }
                        capNhatHoaDon(hoaDon);
                    }
                } else if (!"ThanhToanThanhCong".equals(thanhToan.getTrangThai()) && "ThanhToanThanhCong".equals(trangThaiCu)) {
                    // Nếu trạng thái chuyển từ thành công sang không thành công
                    HoaDon hoaDon = layHoaDonTheoId(thanhToan.getIdHoaDon());
                    if (hoaDon != null && "DaThanhToan".equals(hoaDon.getTrangThai())) {
                        hoaDon.setTrangThai("ChuaThanhToan");
                        capNhatHoaDon(hoaDon);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Phương thức hủy thanh toán
    public void huyThanhToan(int idThanhToan) {
        // Trước tiên cần lấy thông tin thanh toán
        String sqlSelect = "SELECT idHoaDon FROM ThanhToanBenhNhan WHERE idThanhToan = ?";
        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
            pstmtSelect.setInt(1, idThanhToan);
            ResultSet rs = pstmtSelect.executeQuery();
            
            if (rs.next()) {
                int idHoaDon = rs.getInt("idHoaDon");
                
                // Cập nhật trạng thái thanh toán thành "HuyThanhToan"
                String sqlUpdate = "UPDATE ThanhToanBenhNhan SET trangThai = 'HuyThanhToan' WHERE idThanhToan = ?";
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setInt(1, idThanhToan);
                    pstmtUpdate.executeUpdate();
                    
                    // Cập nhật trạng thái hóa đơn thành "ChuaThanhToan"
                    HoaDon hoaDon = layHoaDonTheoId(idHoaDon);
                    if (hoaDon != null && "DaThanhToan".equals(hoaDon.getTrangThai())) {
                        hoaDon.setTrangThai("ChuaThanhToan");
                        capNhatHoaDon(hoaDon);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Phương thức để kiểm tra và đồng bộ tổng tiền giữa HoaDon và ThanhToan
    public void dongBoTongTienHoaDonVaThanhToan(int idHoaDon) {
        HoaDon hoaDon = layHoaDonTheoId(idHoaDon);
        ThanhToanBenhNhan thanhToan = layThanhToanTheoIdHoaDon(idHoaDon);
        
        if (hoaDon != null && thanhToan != null && "DaThanhToan".equals(hoaDon.getTrangThai())) {
            // Nếu số tiền khác nhau, cập nhật lại hóa đơn theo số tiền thanh toán
            if (Math.abs(hoaDon.getTongTien() - thanhToan.getSoTien()) > 0.001) {
                hoaDon.setTongTien(thanhToan.getSoTien());
                capNhatHoaDon(hoaDon);
            }
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
        if (doanhThuController != null) {
            doanhThuController.closeConnection();
        }
    }
}