package controller;

import connect.connectMySQL;
import model.Luong;
import view.LuongUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class LuongController {
    private LuongUI view;
    private Connection conn;
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    private Map<String, Integer> nhanVienMap = new HashMap<>();
    private String vaiTroNguoiDung = "";
    private DecimalFormat numberFormat = new DecimalFormat("#,###");
    
    public LuongController(LuongUI view) {
        this.view = view;
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setVaiTroNguoiDung(String vaiTro) {
        this.vaiTroNguoiDung = vaiTro;
    }
    
    private boolean kiemTraQuyenThayDoi() {
        return !"NguoiDung".equals(vaiTroNguoiDung);
    }
    
    public void loadLuongData(int highlightId) {
        DefaultTableModel modelLuong = view.getModelLuong();
        modelLuong.setRowCount(0);
        view.clearTable();

        double totalLuongCoBan = 0;
        double totalThuong = 0;
        double totalKhauTru = 0;
        double totalTongLuong = 0;

        String sql = "SELECT ln.idLuong, nd.idNguoiDung, nd.hoTen, ln.thangNam, ln.luongCoBan, ln.thuong, ln.khauTru, ln.tongLuong " +
                     "FROM LuongNhanVien ln " +
                     "JOIN NguoiDung nd ON ln.idNguoiDung = nd.idNguoiDung";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int idLuong = resultSet.getInt("idLuong");
                String hoTen = resultSet.getString("hoTen");
                Date thangNam = resultSet.getDate("thangNam");
                double luongCoBan = resultSet.getDouble("luongCoBan");
                double thuong = resultSet.getDouble("thuong");
                double khauTru = resultSet.getDouble("khauTru");
                double tongLuong = resultSet.getDouble("tongLuong");

                String thangNamFormatted = monthYearFormat.format(thangNam);
                Object[] rowData = new Object[]{idLuong, hoTen, thangNamFormatted, luongCoBan, thuong, khauTru, tongLuong};

                // Nếu idLuong khớp với highlightId, gọi loadLuongData với highlightId
                if (idLuong == highlightId) {
                    view.loadLuongData(rowData, highlightId);
                } else {
                    view.loadLuongData(rowData);
                }

                totalLuongCoBan += luongCoBan;
                totalThuong += thuong;
                totalKhauTru += khauTru;
                totalTongLuong += tongLuong;
            }

            view.updateTotalRow(totalLuongCoBan, totalThuong, totalKhauTru, totalTongLuong);
        } catch (SQLException e) {
            e.printStackTrace();
            view.showNotification("Lỗi truy vấn dữ liệu lương: " + e.getMessage(), LuongUI.NotificationType.ERROR);
        }
    }

    // Giữ nguyên phương thức loadLuongData() hiện tại cho các trường hợp không cần highlight
    public void loadLuongData() {
        loadLuongData(-1); // Gọi với highlightId = -1 khi không cần highlight
    }
    
    public void loadNhanVienComboBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        nhanVienMap.clear();
        
        String sql = "SELECT idNguoiDung, hoTen FROM NguoiDung";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int idNguoiDung = resultSet.getInt("idNguoiDung");
                String hoTen = resultSet.getString("hoTen");
                comboBox.addItem(hoTen);
                nhanVienMap.put(hoTen, idNguoiDung);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (view != null) {
                JOptionPane.showMessageDialog(view, "Lỗi tải danh sách nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public int getIdNguoiDungByHoTen(String hoTen) {
        if (nhanVienMap.containsKey(hoTen)) {
            return nhanVienMap.get(hoTen);
        }
        
        // Nếu không có trong map, truy vấn CSDL
        String sql = "SELECT idNguoiDung FROM NguoiDung WHERE hoTen = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, hoTen);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int idNguoiDung = resultSet.getInt("idNguoiDung");
                    nhanVienMap.put(hoTen, idNguoiDung); // Cập nhật map
                    return idNguoiDung;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (view != null) {
                JOptionPane.showMessageDialog(view, "Lỗi tìm ID người dùng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        return -1;
    }
    
    private boolean kiemTraTonTaiLuong(int idNguoiDung, Date thangNam) {
        String sql = "SELECT COUNT(*) FROM LuongNhanVien WHERE idNguoiDung = ? AND MONTH(thangNam) = MONTH(?) AND YEAR(thangNam) = YEAR(?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idNguoiDung);
            preparedStatement.setDate(2, thangNam);
            preparedStatement.setDate(3, thangNam);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean themLuong(int idNguoiDung, Date thangNam, double luongCoBan, double thuong, double khauTru) {
        if (!kiemTraQuyenThayDoi()) {
            view.showNotification("Không có quyền thêm lương!", LuongUI.NotificationType.ERROR);
            return false;
        }

        if (idNguoiDung <= 0 || thangNam == null || luongCoBan < 0 || thuong < 0 || khauTru < 0) {
            view.showNotification("Dữ liệu không hợp lệ!", LuongUI.NotificationType.ERROR);
            return false;
        }

        if (kiemTraTonTaiLuong(idNguoiDung, thangNam)) {
            view.showNotification("Lương đã tồn tại cho tháng này!", LuongUI.NotificationType.ERROR);
            return false;
        }

        String sql = "INSERT INTO LuongNhanVien (idNguoiDung, thangNam, luongCoBan, thuong, khauTru, tongLuong) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, idNguoiDung);
            preparedStatement.setDate(2, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setDouble(3, luongCoBan);
            preparedStatement.setDouble(4, thuong);
            preparedStatement.setDouble(5, khauTru);
            double tongLuong = luongCoBan + thuong - khauTru;
            preparedStatement.setDouble(6, tongLuong);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                // Lấy idLuong vừa thêm
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newIdLuong = generatedKeys.getInt(1);
                        // Tải lại dữ liệu với highlightId
                        view.clearTable();
                        loadLuongData(newIdLuong);
                        return true;
                    }
                }
            }
            view.showNotification("Thêm lương thất bại!", LuongUI.NotificationType.ERROR);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            view.showNotification("Lỗi SQL: " + e.getMessage(), LuongUI.NotificationType.ERROR);
            return false;
        }
    }

    public boolean xoaLuong(int idLuong) {
        if (!kiemTraQuyenThayDoi()) {
            return false; // Không có quyền
        }

        if (idLuong <= 0) {
            return false; // ID không hợp lệ
        }

        String sql = "DELETE FROM LuongNhanVien WHERE idLuong = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idLuong);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                loadLuongData(); // Tải lại dữ liệu sau khi xóa
                return true; // Xóa thành công
            }
            return false; // Không tìm thấy bản ghi để xóa
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Lỗi SQL
        }
    }
    
    public boolean suaLuong(int idLuong, int idNguoiDung, Date thangNam, double luongCoBan, double thuong, double khauTru) {
        if (!kiemTraQuyenThayDoi()) {
            view.showNotification("Không có quyền sửa lương!", LuongUI.NotificationType.ERROR);
            return false;
        }

        if (idLuong <= 0 || idNguoiDung <= 0 || thangNam == null || luongCoBan < 0 || thuong < 0 || khauTru < 0) {
            view.showNotification("Dữ liệu không hợp lệ!", LuongUI.NotificationType.ERROR);
            return false;
        }

        String checkSql = "SELECT COUNT(*) FROM LuongNhanVien WHERE idNguoiDung = ? AND MONTH(thangNam) = MONTH(?) AND YEAR(thangNam) = YEAR(?) AND idLuong != ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, idNguoiDung);
            checkStmt.setDate(2, thangNam);
            checkStmt.setDate(3, thangNam);
            checkStmt.setInt(4, idLuong);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    view.showNotification("Lương đã tồn tại cho tháng này!", LuongUI.NotificationType.ERROR);
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            view.showNotification("Lỗi kiểm tra dữ liệu: " + e.getMessage(), LuongUI.NotificationType.ERROR);
            return false;
        }

        String sql = "UPDATE LuongNhanVien SET idNguoiDung = ?, thangNam = ?, luongCoBan = ?, thuong = ?, khauTru = ?, tongLuong = ? WHERE idLuong = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idNguoiDung);
            preparedStatement.setDate(2, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setDouble(3, luongCoBan);
            preparedStatement.setDouble(4, thuong);
            preparedStatement.setDouble(5, khauTru);
            double tongLuong = luongCoBan + thuong - khauTru;
            preparedStatement.setDouble(6, tongLuong);
            preparedStatement.setInt(7, idLuong);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                // Tải lại dữ liệu với highlightId
                view.clearTable();
                loadLuongData(idLuong);
                return true;
            }
            view.showNotification("Sửa lương thất bại!", LuongUI.NotificationType.ERROR);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            view.showNotification("Lỗi SQL: " + e.getMessage(), LuongUI.NotificationType.ERROR);
            return false;
        }
    }
    
    public Luong getLuongById(int idLuong) {
        String sql = "SELECT * FROM LuongNhanVien WHERE idLuong = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idLuong);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Luong luong = new Luong();
                    luong.setIdLuong(resultSet.getInt("idLuong"));
                    luong.setIdNguoiDung(resultSet.getInt("idNguoiDung"));
                    luong.setThangNam(resultSet.getDate("thangNam"));
                    luong.setLuongCoBan(resultSet.getDouble("luongCoBan"));
                    luong.setThuong(resultSet.getDouble("thuong"));
                    luong.setKhauTru(resultSet.getDouble("khauTru"));
                    luong.setTongLuong(resultSet.getDouble("tongLuong"));
                    return luong;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (view != null) {
                JOptionPane.showMessageDialog(view, "Lỗi truy vấn dữ liệu lương: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    public void timKiemLuong(String tuKhoa) {
        // Clear the current table data
        view.clearTable();
        
        double totalLuongCoBan = 0;
        double totalThuong = 0;
        double totalKhauTru = 0;
        double totalTongLuong = 0;
        
        String sql = "SELECT ln.idLuong, nd.idNguoiDung, nd.hoTen, ln.thangNam, ln.luongCoBan, ln.thuong, ln.khauTru, ln.tongLuong " +
                    "FROM LuongNhanVien ln " +
                    "JOIN NguoiDung nd ON ln.idNguoiDung = nd.idNguoiDung " +
                    "WHERE LOWER(nd.hoTen) LIKE ? OR LOWER(DATE_FORMAT(ln.thangNam, '%m/%Y')) LIKE ? " +
                    "OR CAST(ln.luongCoBan AS CHAR) LIKE ? " +
                    "OR CAST(ln.thuong AS CHAR) LIKE ? " +
                    "OR CAST(ln.khauTru AS CHAR) LIKE ? " +
                    "OR CAST(ln.tongLuong AS CHAR) LIKE ?";
        
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            String searchPattern = "%" + tuKhoa.toLowerCase() + "%";
            // Set all parameters to the same search pattern
            for (int i = 1; i <= 6; i++) {
                preparedStatement.setString(i, searchPattern);
            }
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                int resultCount = 0;
                
                while (resultSet.next()) {
                    resultCount++;
                    int idLuong = resultSet.getInt("idLuong");
                    String hoTen = resultSet.getString("hoTen");
                    Date thangNam = resultSet.getDate("thangNam");
                    double luongCoBan = resultSet.getDouble("luongCoBan");
                    double thuong = resultSet.getDouble("thuong");
                    double khauTru = resultSet.getDouble("khauTru");
                    double tongLuong = resultSet.getDouble("tongLuong");
                    
                    // Định dạng ngày tháng
                    String thangNamFormatted = monthYearFormat.format(thangNam);
                    
                    // Use actual numeric values instead of formatted strings
                    Object[] rowData = new Object[]{idLuong, hoTen, thangNamFormatted, luongCoBan, thuong, khauTru, tongLuong};
                    
                    // Add row to table model and store in original data
                    view.loadLuongData(rowData);
                    
                    // Update totals
                    totalLuongCoBan += luongCoBan;
                    totalThuong += thuong;
                    totalKhauTru += khauTru;
                    totalTongLuong += tongLuong;
                }
                view.updateTotalRow(totalLuongCoBan, totalThuong, totalKhauTru, totalTongLuong);
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    }
}