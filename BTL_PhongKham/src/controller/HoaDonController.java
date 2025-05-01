package controller;

import model.HoaDon;
import model.ThanhToanBenhNhan;
import connect.connectMySQL;
import view.DoanhThuUI;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing patient invoices (HoaDon) and their payments.
 * Integrates with DoanhThuController for revenue tracking.
 */
public class HoaDonController {
    private static final Logger LOGGER = Logger.getLogger(HoaDonController.class.getName());
    private List<HoaDon> danhSachHoaDon;
    private Connection conn;
    private DoanhThuController doanhThuController;
    private DoanhThuUI doanhThuUI;

    /**
     * Constructor initializes the connection to database and loads invoices
     */
    public HoaDonController() {
        this.danhSachHoaDon = new ArrayList<>();
        this.doanhThuController = new DoanhThuController();
        this.doanhThuController.setHoaDonController(this);
        
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
            loadAllHoaDonFromDB();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi kết nối CSDL hoặc tải dữ liệu hóa đơn", e);
        }
    }
    
    /**
     * Sets the DoanhThuUI reference for UI updates
     * @param doanhThuUI UI component for revenue display
     */
    public void setDoanhThuUI(DoanhThuUI doanhThuUI) {
        this.doanhThuUI = doanhThuUI;
    }

    /**
     * Refreshes the revenue data display
     */
    private void refreshDoanhThuUI() {
        if (this.doanhThuController != null) {
            this.doanhThuController.loadDoanhThuData();
            if (this.doanhThuUI != null) {
                this.doanhThuUI.refreshData();
            }
        }
    }

    /**
     * Loads all invoices from database
     */
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
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu hóa đơn từ CSDL", e);
        }
    }

    /**
     * Returns the list of all invoices
     * @return List of invoices
     */
    public List<HoaDon> layDanhSachHoaDon() {
        return danhSachHoaDon;
    }

    /**
     * Gets an invoice by ID
     * @param id Invoice ID
     * @return HoaDon object or null if not found
     */
    public HoaDon layHoaDonTheoId(int id) {
        // First check in local list
        for (HoaDon hoaDon : danhSachHoaDon) {
            if (hoaDon.getIdHoaDon() == id) {
                return hoaDon;
            }
        }
        
        // If not found, try to find in database
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
            LOGGER.log(Level.SEVERE, "Lỗi khi truy vấn hóa đơn theo ID: " + id, e);
        }
        
        return null;
    }

    /**
     * Adds a new invoice
     * @param hoaDon Invoice to add
     * @return true if successful, false otherwise
     */
    public boolean themHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            LOGGER.log(Level.WARNING, "Không thể thêm hóa đơn null");
            return false;
        }
        
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
                        
                        // If invoice is marked as paid, automatically add to revenue
                        if ("DaThanhToan".equals(hoaDon.getTrangThai())) {
                            themHoaDonVaoDoanhThu(hoaDon);
                            refreshDoanhThuUI();
                        }
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm hóa đơn mới", e);
        }
        return false;
    }

    /**
     * Adds an invoice to revenue records
     * @param hoaDon Invoice to add to revenue
     */
    private void themHoaDonVaoDoanhThu(HoaDon hoaDon) {
        if (hoaDon == null || !"DaThanhToan".equals(hoaDon.getTrangThai())) {
            return;
        }
        
        // Check if invoice already exists in revenue records
        if (doanhThuController.kiemTraHoaDonTrongDoanhThu(hoaDon.getIdHoaDon())) {
            capNhatDoanhThuTheoHoaDon(hoaDon);
            return;
        }
        
        // Convert invoice date to month-year format for revenue
        Date ngayTao = hoaDon.getNgayTao();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ngayTao);
        cal.set(Calendar.DAY_OF_MONTH, 1); // Always set to first day of month
        
        // Add revenue record
        doanhThuController.themDoanhThu(cal.getTime(), hoaDon.getTongTien(), hoaDon.getIdHoaDon());
    }

    /**
     * Updates revenue record for an invoice
     * @param hoaDon Invoice to update revenue for
     */
    private void capNhatDoanhThuTheoHoaDon(HoaDon hoaDon) {
        if (hoaDon == null || !"DaThanhToan".equals(hoaDon.getTrangThai())) {
            return;
        }
        
        // Check if invoice exists in revenue records
        if (!doanhThuController.kiemTraHoaDonTrongDoanhThu(hoaDon.getIdHoaDon())) {
            themHoaDonVaoDoanhThu(hoaDon);
            return;
        }
        
        // Convert invoice date to month-year format
        Date ngayTao = hoaDon.getNgayTao();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ngayTao);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        // Get revenue ID to update
        int idDoanhThu = getDoanhThuIdFromHoaDon(hoaDon.getIdHoaDon());
        if (idDoanhThu > 0) {
            doanhThuController.suaDoanhThu(idDoanhThu, cal.getTime(), hoaDon.getTongTien(), hoaDon.getIdHoaDon());
        }
    }

    /**
     * Gets revenue ID associated with an invoice
     * @param idHoaDon Invoice ID
     * @return Revenue ID or -1 if not found
     */
    private int getDoanhThuIdFromHoaDon(int idHoaDon) {
        String sql = "SELECT idDoanhThu FROM DoanhThu WHERE idHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idHoaDon);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idDoanhThu");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy ID doanh thu từ hóa đơn ID: " + idHoaDon, e);
        }
        return -1;
    }

    /**
     * Deletes an invoice
     * @param id Invoice ID to delete
     * @return true if successful, false otherwise
     */
    public boolean xoaHoaDon(int id) {
        boolean daXoaDoanhThu = false;
        
        // Delete associated revenue record first
        try {
            daXoaDoanhThu = doanhThuController.xoaDoanhThuTheoHoaDonId(id);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Lỗi khi xóa doanh thu liên quan đến hóa đơn ID: " + id, e);
        }
        
        // Next delete the invoice itself
        String sql = "DELETE FROM HoaDon WHERE idHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                danhSachHoaDon.removeIf(hoaDon -> hoaDon.getIdHoaDon() == id);
                
                // If revenue was deleted and UI exists, refresh it
                if (daXoaDoanhThu) {
                    refreshDoanhThuUI();
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa hóa đơn ID: " + id, e);
        }
        return false;
    }

    /**
     * Gets payment information for an invoice
     * @param idHoaDon Invoice ID
     * @return Payment object or null if not found
     */
    public ThanhToanBenhNhan layThanhToanTheoIdHoaDon(int idHoaDon) {
        String sql = "SELECT idThanhToan, idHoaDon, soTien, hinhThucThanhToan, maQR, trangThai " +
                     "FROM ThanhToanBenhNhan WHERE idHoaDon = ?";
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
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy thông tin thanh toán cho hóa đơn ID: " + idHoaDon, e);
        }
        return null;
    }

    /**
     * Adds a new payment record
     * @param thanhToan Payment information to add
     * @return true if successful, false otherwise
     */
    public boolean themThanhToan(ThanhToanBenhNhan thanhToan) {
        if (thanhToan == null) {
            LOGGER.log(Level.WARNING, "Không thể thêm thanh toán null");
            return false;
        }
        
        String sql = "INSERT INTO ThanhToanBenhNhan (idHoaDon, soTien, hinhThucThanhToan, maQR, trangThai) " +
                     "VALUES (?, ?, ?, ?, ?)";
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
                        
                        // If payment is successful, update invoice status
                        if ("ThanhToanThanhCong".equals(thanhToan.getTrangThai())) {
                            HoaDon hoaDon = layHoaDonTheoId(thanhToan.getIdHoaDon());
                            if (hoaDon != null) {
                                hoaDon.setTrangThai("DaThanhToan");
                                // Update total amount if different from payment amount
                                if (Math.abs(hoaDon.getTongTien() - thanhToan.getSoTien()) > 0.001) {
                                    hoaDon.setTongTien(thanhToan.getSoTien());
                                }
                                capNhatHoaDon(hoaDon);
                            }
                        }
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm thanh toán mới", e);
        }
        return false;
    }

    /**
     * Updates a payment record
     * @param thanhToan Payment information to update
     * @return true if successful, false otherwise
     */
    public boolean capNhatThanhToan(ThanhToanBenhNhan thanhToan) {
        if (thanhToan == null) {
            LOGGER.log(Level.WARNING, "Không thể cập nhật thanh toán null");
            return false;
        }
        
        String trangThaiCu = null;
        ThanhToanBenhNhan thanhToanCu = layThanhToanTheoIdHoaDon(thanhToan.getIdHoaDon());
        if (thanhToanCu != null) {
            trangThaiCu = thanhToanCu.getTrangThai();
        }
        
        String sql = "UPDATE ThanhToanBenhNhan SET soTien = ?, hinhThucThanhToan = ?, maQR = ?, trangThai = ? " +
                     "WHERE idThanhToan = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, thanhToan.getSoTien());
            pstmt.setString(2, thanhToan.getHinhThucThanhToan());
            pstmt.setString(3, thanhToan.getMaQR());
            pstmt.setString(4, thanhToan.getTrangThai());
            pstmt.setInt(5, thanhToan.getIdThanhToan());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // If payment status changes to successful or amount changes
                boolean statusChangedToSuccess = "ThanhToanThanhCong".equals(thanhToan.getTrangThai()) && 
                                              !"ThanhToanThanhCong".equals(trangThaiCu);
                
                boolean amountChanged = "ThanhToanThanhCong".equals(thanhToan.getTrangThai()) && 
                                     thanhToanCu != null && 
                                     Math.abs(thanhToan.getSoTien() - thanhToanCu.getSoTien()) > 0.001;
                
                if (statusChangedToSuccess || amountChanged) {
                    HoaDon hoaDon = layHoaDonTheoId(thanhToan.getIdHoaDon());
                    if (hoaDon != null) {
                        hoaDon.setTrangThai("DaThanhToan");
                        // Update total amount if different from payment amount
                        if (Math.abs(hoaDon.getTongTien() - thanhToan.getSoTien()) > 0.001) {
                            hoaDon.setTongTien(thanhToan.getSoTien());
                        }
                        capNhatHoaDon(hoaDon);
                    }
                } else if (!"ThanhToanThanhCong".equals(thanhToan.getTrangThai()) && 
                           "ThanhToanThanhCong".equals(trangThaiCu)) {
                    // If status changes from successful to unsuccessful
                    HoaDon hoaDon = layHoaDonTheoId(thanhToan.getIdHoaDon());
                    if (hoaDon != null && "DaThanhToan".equals(hoaDon.getTrangThai())) {
                        hoaDon.setTrangThai("ChuaThanhToan");
                        capNhatHoaDon(hoaDon);
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật thanh toán ID: " + thanhToan.getIdThanhToan(), e);
        }
        return false;
    }

    /**
     * Updates an invoice
     * @param hoaDon Invoice to update
     * @return true if successful, false otherwise
     */
    public boolean capNhatHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            LOGGER.log(Level.WARNING, "Không thể cập nhật hóa đơn null");
            return false;
        }
        
        // Save old status and amount before updating
        String trangThaiCu = null;
        double tongTienCu = 0;
        
        // First check in local list
        for (HoaDon hd : danhSachHoaDon) {
            if (hd.getIdHoaDon() == hoaDon.getIdHoaDon()) {
                trangThaiCu = hd.getTrangThai();
                tongTienCu = hd.getTongTien();
                break;
            }
        }
        
        // If not found in list, try database
        if (trangThaiCu == null) {
            HoaDon hdCu = layHoaDonTheoId(hoaDon.getIdHoaDon());
            if (hdCu != null) {
                trangThaiCu = hdCu.getTrangThai();
                tongTienCu = hdCu.getTongTien();
            }
        }
        
        String sql = "UPDATE HoaDon SET idBenhNhan = ?, ngayTao = ?, tongTien = ?, trangThai = ? " +
                     "WHERE idHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hoaDon.getIdBenhNhan());
            pstmt.setDate(2, new java.sql.Date(hoaDon.getNgayTao().getTime()));
            pstmt.setDouble(3, hoaDon.getTongTien());
            pstmt.setString(4, hoaDon.getTrangThai());
            pstmt.setInt(5, hoaDon.getIdHoaDon());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Update local list
                boolean found = false;
                for (int i = 0; i < danhSachHoaDon.size(); i++) {
                    if (danhSachHoaDon.get(i).getIdHoaDon() == hoaDon.getIdHoaDon()) {
                        danhSachHoaDon.set(i, hoaDon);
                        found = true;
                        break;
                    }
                }
                
                // If not found in list, add it
                if (!found) {
                    danhSachHoaDon.add(hoaDon);
                }
                
                // Check if status changed to "DaThanhToan"
                if ("DaThanhToan".equals(hoaDon.getTrangThai()) && !"DaThanhToan".equals(trangThaiCu)) {
                    themHoaDonVaoDoanhThu(hoaDon);
                } else if (!"DaThanhToan".equals(hoaDon.getTrangThai()) && "DaThanhToan".equals(trangThaiCu)) {
                    doanhThuController.xoaDoanhThuTheoHoaDonId(hoaDon.getIdHoaDon());
                } else if ("DaThanhToan".equals(hoaDon.getTrangThai()) && "DaThanhToan".equals(trangThaiCu)) {
                    // Check if amount changed
                    boolean tongTienThayDoi = Math.abs(hoaDon.getTongTien() - tongTienCu) > 0.001;
                    
                    if (tongTienThayDoi) {
                        capNhatDoanhThuTheoHoaDon(hoaDon);
                    }
                }
                
                // Refresh UI
                refreshDoanhThuUI();
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật hóa đơn ID: " + hoaDon.getIdHoaDon(), e);
        }
        return false;
    }

    /**
     * Cancels a payment
     * @param idThanhToan Payment ID to cancel
     * @return true if successful, false otherwise
     */
    public boolean huyThanhToan(int idThanhToan) {
        // First get payment information
        String sqlSelect = "SELECT idHoaDon FROM ThanhToanBenhNhan WHERE idThanhToan = ?";
        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
            pstmtSelect.setInt(1, idThanhToan);
            ResultSet rs = pstmtSelect.executeQuery();
            
            if (rs.next()) {
                int idHoaDon = rs.getInt("idHoaDon");
                
                // Update payment status to "HuyThanhToan"
                String sqlUpdate = "UPDATE ThanhToanBenhNhan SET trangThai = 'HuyThanhToan' WHERE idThanhToan = ?";
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setInt(1, idThanhToan);
                    pstmtUpdate.executeUpdate();
                    
                    // Update invoice status to "ChuaThanhToan"
                    HoaDon hoaDon = layHoaDonTheoId(idHoaDon);
                    if (hoaDon != null && "DaThanhToan".equals(hoaDon.getTrangThai())) {
                        hoaDon.setTrangThai("ChuaThanhToan");
                        return capNhatHoaDon(hoaDon);
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi hủy thanh toán ID: " + idThanhToan, e);
        }
        return false;
    }
    
    /**
     * Synchronizes total amount between invoice and payment
     * @param idHoaDon Invoice ID to synchronize
     * @return true if successful, false otherwise
     */
    public boolean dongBoTongTienHoaDonVaThanhToan(int idHoaDon) {
        HoaDon hoaDon = layHoaDonTheoId(idHoaDon);
        ThanhToanBenhNhan thanhToan = layThanhToanTheoIdHoaDon(idHoaDon);
        
        if (hoaDon != null && thanhToan != null && "DaThanhToan".equals(hoaDon.getTrangThai())) {
            // If amounts differ, update invoice to match payment amount
            if (Math.abs(hoaDon.getTongTien() - thanhToan.getSoTien()) > 0.001) {
                hoaDon.setTongTien(thanhToan.getSoTien());
                return capNhatHoaDon(hoaDon);
            }
            return true;
        }
        return false;
    }

    /**
     * Closes database connection
     */
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi đóng kết nối CSDL", e);
            }
        }
        if (doanhThuController != null) {
            doanhThuController.closeConnection();
        }
    }
}