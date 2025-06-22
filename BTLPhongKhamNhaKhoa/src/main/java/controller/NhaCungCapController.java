package controller;

import model.NhaCungCap;
import connect.connectMySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate; // Import cho LocalDate
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapController {

    private Connection conn;

    public NhaCungCapController() {
        try {
            this.conn = connectMySQL.getConnection();
        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi kết nối CSDL, không hiển thị JOptionPane ở đây
        }
    }

    public List<NhaCungCap> layDanhSachNhaCungCap() {
        List<NhaCungCap> danhSach = new ArrayList<>();
        // Cập nhật câu SQL để lấy TẤT CẢ CÁC TRƯỜNG, bao gồm MaSoThue và NgayDangKy
        String sql = "SELECT idNCC, TenNCC, DiaChi, SoDienThoai, MaSoThue, NgayDangKy FROM NhaCungCap";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("TenNCC"));
                ncc.setDiaChi(rs.getString("DiaChi"));
                ncc.setSoDienThoai(rs.getString("SoDienThoai"));
                ncc.setMaSoThue(rs.getString("MaSoThue")); // Gán giá trị cho trường mới
                // Chuyển đổi từ java.sql.Date sang LocalDate, xử lý trường hợp cột NgayDangKy là NULL trong DB
                ncc.setNgayDangKy(rs.getDate("NgayDangKy") != null ? rs.getDate("NgayDangKy").toLocalDate() : null);
                danhSach.add(ncc);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi truy vấn
            // UI sẽ xử lý thông báo
        }
        return danhSach;
    }

    public String themNhaCungCap(NhaCungCap ncc) {
        // Cập nhật câu SQL để thêm TẤT CẢ CÁC TRƯỜNG, bao gồm MaSoThue và NgayDangKy
        String sql = "INSERT INTO NhaCungCap (TenNCC, DiaChi, SoDienThoai, MaSoThue, NgayDangKy) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ncc.getTenNCC());
            pstmt.setString(2, ncc.getDiaChi());
            pstmt.setString(3, ncc.getSoDienThoai());
            pstmt.setString(4, ncc.getMaSoThue()); // Gán giá trị cho trường mới
            // Chuyển đổi từ LocalDate sang java.sql.Date, xử lý trường hợp NgayDangKy là NULL
            pstmt.setDate(5, ncc.getNgayDangKy() != null ? java.sql.Date.valueOf(ncc.getNgayDangKy()) : null);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Trả về ID tự tăng dưới dạng String
                        return String.valueOf(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi khi thêm nhà cung cấp
            // UI sẽ xử lý thông báo
        }
        return null; // Trả về null nếu thất bại
    }

    public boolean suaNhaCungCap(NhaCungCap ncc) {
        // Cập nhật câu SQL để sửa TẤT CẢ CÁC TRƯỜNG, bao gồm MaSoThue và NgayDangKy
        String sql = "UPDATE NhaCungCap SET TenNCC = ?, DiaChi = ?, SoDienThoai = ?, MaSoThue = ?, NgayDangKy = ? WHERE idNCC = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ncc.getTenNCC());
            pstmt.setString(2, ncc.getDiaChi());
            pstmt.setString(3, ncc.getSoDienThoai());
            pstmt.setString(4, ncc.getMaSoThue()); // Gán giá trị cho trường mới
            // Chuyển đổi từ LocalDate sang java.sql.Date, xử lý trường hợp NgayDangKy là NULL
            pstmt.setDate(5, ncc.getNgayDangKy() != null ? java.sql.Date.valueOf(ncc.getNgayDangKy()) : null);
            pstmt.setString(6, ncc.getMaNCC()); // idNCC là tham số cuối cùng để xác định bản ghi cần sửa
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi cập nhật
            // UI sẽ xử lý thông báo
        }
        return false;
    }

    public boolean xoaNhaCungCap(String maNCC) {
        // Giữ nguyên logic kiểm tra khóa ngoại và đánh dấu "ngừng cung cấp"
        String checkQuery = "SELECT COUNT(*) FROM KhoVatTu WHERE idNCC = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, maNCC);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                String getNccQuery = "SELECT TenNCC FROM NhaCungCap WHERE idNCC = ?";
                try (PreparedStatement getNccStmt = conn.prepareStatement(getNccQuery)) {
                    getNccStmt.setString(1, maNCC);
                    ResultSet nccRs = getNccStmt.executeQuery();

                    if (nccRs.next()) {
                        String tenNCC = nccRs.getString("TenNCC");
                        String newTenNCC = tenNCC + " (ngừng cung cấp)";

                        String updateNccQuery = "UPDATE NhaCungCap SET TenNCC = ? WHERE idNCC = ?";
                        try (PreparedStatement updateNccStmt = conn.prepareStatement(updateNccQuery)) {
                            updateNccStmt.setString(1, newTenNCC);
                            updateNccStmt.setString(2, maNCC);
                            return updateNccStmt.executeUpdate() > 0;
                        }
                    }
                }
            } else {
                String sql = "DELETE FROM NhaCungCap WHERE idNCC = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, maNCC);
                    return pstmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi xóa
            // UI sẽ xử lý thông báo, có thể kiểm tra e.getErrorCode() == 1451 cho lỗi khóa ngoại
        }
        return false;
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Log lỗi đóng kết nối
            }
        }
    }

    // Phương thức để lấy thông tin nhà cung cấp đầy đủ theo ID
    public NhaCungCap getNhaCungCapById(String id) {
        // Cập nhật câu SQL để lấy TẤT CẢ CÁC TRƯỜNG
        String sql = "SELECT idNCC, TenNCC, DiaChi, SoDienThoai, MaSoThue, NgayDangKy FROM NhaCungCap WHERE idNCC = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    NhaCungCap ncc = new NhaCungCap();
                    ncc.setMaNCC(rs.getString("idNCC"));
                    ncc.setTenNCC(rs.getString("TenNCC"));
                    ncc.setDiaChi(rs.getString("DiaChi"));
                    ncc.setSoDienThoai(rs.getString("SoDienThoai"));
                    ncc.setMaSoThue(rs.getString("MaSoThue")); // Gán giá trị cho trường mới
                    ncc.setNgayDangKy(rs.getDate("NgayDangKy") != null ? rs.getDate("NgayDangKy").toLocalDate() : null); // Gán giá trị cho trường mới
                    return ncc;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi
        }
        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }
}