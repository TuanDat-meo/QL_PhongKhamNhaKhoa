package controller;

import connect.connectMySQL;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Lớp trừu tượng cung cấp các thành phần chung cho các controller thống kê
 * Vai trò như lớp cha cho ThongKeDoanhThuController và ThongKeBacSiController
 */
public abstract class ThongKeController {
    
    protected Connection conn;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    protected SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    protected SimpleDateFormat weekDisplayFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    protected ThongKeController() {
        initConnection();
    }
    
    /**
     * Khởi tạo kết nối đến cơ sở dữ liệu
     */
    protected void initConnection() {
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Đóng kết nối đến cơ sở dữ liệu
     */
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Kiểm tra trạng thái kết nối đến cơ sở dữ liệu
     * @return true nếu kết nối hoạt động, false nếu ngược lại
     */
    public boolean isConnectionActive() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}