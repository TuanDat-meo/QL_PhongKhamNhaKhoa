// DonThuocController.java (controller)
package controller;

import model.ChiTietDonThuoc;
import model.DonThuoc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connect.connectMySQL;

public class DonThuocController {
    private Connection conn;

    public DonThuocController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DonThuoc> layDanhSachDonThuocTheoHoSoBenhAnId(int idHoSoBenhAn) {
        List<DonThuoc> danhSachDonThuoc = new ArrayList<>();
        String sql = "SELECT " +
                     "   dt.idDonThuoc, " +
                     "   t.idThuoc, " +
                     "   t.tenThuoc, " +
                     "   ctdt.soLuong, " +
                     "   ctdt.huongDanSuDung, " +
                     "   dt.ngayKeDon " +
                     "FROM HoSoBenhAn hsbA " + // Bắt đầu từ bảng HoSoBenhAn để lọc theo idHoSoBenhAn
                     "JOIN DonThuoc dt ON hsbA.idBenhNhan = dt.idBenhNhan " + // Liên kết với DonThuoc qua idBenhNhan
                     "JOIN ChiTietDonThuoc ctdt ON dt.idDonThuoc = ctdt.idDonThuoc " + // Liên kết với ChiTietDonThuoc
                     "JOIN Thuoc t ON ctdt.idThuoc = t.idThuoc " + // Liên kết với Thuoc
                     "WHERE hsbA.idHoSo = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHoSoBenhAn);
            ResultSet rs = pstmt.executeQuery();

            Map<Integer, DonThuoc> donThuocMap = new HashMap<>();

            while (rs.next()) {
                int idDonThuoc = rs.getInt("idDonThuoc");

                // Tạo mới DonThuoc nếu chưa tồn tại trong map
                DonThuoc donThuoc = donThuocMap.getOrDefault(idDonThuoc, new DonThuoc());
                donThuoc.setIdDonThuoc(idDonThuoc);
                donThuoc.setNgayKeDon(rs.getDate("ngayKeDon")); // Lấy ngày kê đơn

                // Tạo ChiTietDonThuoc
                ChiTietDonThuoc chiTiet = new ChiTietDonThuoc();
                chiTiet.setIdThuoc(rs.getInt("idThuoc"));
                chiTiet.getThuoc().setTenThuoc(rs.getString("tenThuoc"));
                chiTiet.setSoLuong(rs.getInt("soLuong"));
                chiTiet.setHuongDanSuDung(rs.getString("huongDanSuDung"));

                // Thêm ChiTietDonThuoc vào DonThuoc
                donThuoc.getChiTietDonThuocs().add(chiTiet);

                // Đặt idHoSoBenhAn (có thể cần thiết để hiển thị hoặc xử lý)
                donThuoc.setIdHoSoBenhAn(idHoSoBenhAn);

                donThuocMap.put(idDonThuoc, donThuoc);
            }

            danhSachDonThuoc.addAll(donThuocMap.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachDonThuoc;
    }

    // Các phương thức khác có thể có (ví dụ thêm đơn thuốc):
    public boolean themDonThuoc(DonThuoc donThuoc) {
        String sqlDonThuoc = "INSERT INTO DonThuoc (idBenhNhan, idBacSi, ngayKeDon) VALUES (?, ?, ?)";
        String sqlChiTietDonThuoc = "INSERT INTO ChiTietDonThuoc (idDonThuoc, idThuoc, soLuong, huongDanSuDung) VALUES (?, ?, ?, ?)";
        boolean success = false;
        PreparedStatement pstmtDonThuoc = null;
        PreparedStatement pstmtChiTietDonThuoc = null;
        ResultSet generatedKeys = null;

        try {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Thêm thông tin vào bảng DonThuoc
            pstmtDonThuoc = conn.prepareStatement(sqlDonThuoc, Statement.RETURN_GENERATED_KEYS);
            pstmtDonThuoc.setInt(1, donThuoc.getIdBenhNhan());
            pstmtDonThuoc.setInt(2, donThuoc.getIdBacSi());
            pstmtDonThuoc.setDate(3, new java.sql.Date(donThuoc.getNgayKeDon().getTime()));
            int affectedRowsDonThuoc = pstmtDonThuoc.executeUpdate();

            if (affectedRowsDonThuoc > 0) {
                // Lấy ID tự động tăng của đơn thuốc vừa được thêm
                generatedKeys = pstmtDonThuoc.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idDonThuoc = generatedKeys.getInt(1);
                    donThuoc.setIdDonThuoc(idDonThuoc); // Cập nhật ID cho đối tượng DonThuoc

                    // Thêm thông tin chi tiết thuốc vào bảng ChiTietDonThuoc
                    for (ChiTietDonThuoc chiTiet : donThuoc.getChiTietDonThuocs()) {
                        pstmtChiTietDonThuoc = conn.prepareStatement(sqlChiTietDonThuoc);
                        pstmtChiTietDonThuoc.setInt(1, donThuoc.getIdDonThuoc());
                        pstmtChiTietDonThuoc.setInt(2, chiTiet.getIdThuoc());
                        pstmtChiTietDonThuoc.setInt(3, chiTiet.getSoLuong());
                        pstmtChiTietDonThuoc.setString(4, chiTiet.getHuongDanSuDung());
                        pstmtChiTietDonThuoc.executeUpdate();
                    }
                    conn.commit(); // Commit transaction nếu tất cả thành công
                    success = true;
                } else {
                    conn.rollback(); // Rollback nếu không lấy được ID đơn thuốc
                }
            } else {
                conn.rollback(); // Rollback nếu không thêm được đơn thuốc
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback nếu có lỗi xảy ra
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmtDonThuoc != null) pstmtDonThuoc.close();
                if (pstmtChiTietDonThuoc != null) pstmtChiTietDonThuoc.close();
                if (conn != null) conn.setAutoCommit(true); // Đặt lại auto commit
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }

    // Các phương thức sửa, xóa, tìm kiếm tương tự sẽ được viết theo logic nghiệp vụ
}