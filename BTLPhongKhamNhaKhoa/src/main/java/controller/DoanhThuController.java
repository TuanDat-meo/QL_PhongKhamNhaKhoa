package controller;

import connect.connectMySQL;
import view.DoanhThuUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoanhThuController {

    private DoanhThuUI view;
    private Connection conn;
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    private HoaDonController hoaDonController;

    // Định nghĩa màu sắc và font trước enum
    private final static Color successColor = new Color(86, 156, 104); // Elegant green
    private final static Color errorColor = new Color(220, 53, 69); // Bootstrap-like error color
    private final Color textColor = new Color(33, 37, 41); // Near-black text
    private final Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

    // Enum NotificationType
    public enum NotificationType {
        SUCCESS(successColor, "Thành công"),
        ERROR(errorColor, "Lỗi");

        final Color color;
        final String title;

        NotificationType(Color color, String title) {
            this.color = color;
            this.title = title;
        }
    }

    public DoanhThuController(DoanhThuUI view) {
        this.view = view;
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (view != null) {
                SwingUtilities.invokeLater(() -> showNotification("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), NotificationType.ERROR));
            }
        }
    }

    public DoanhThuController() {
        try {
            this.conn = connectMySQL.getConnection();
            if (this.conn == null) {
                throw new SQLException("Không thể kết nối CSDL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public void setHoaDonController(HoaDonController hoaDonController) {
        this.hoaDonController = hoaDonController;
    }

    public void loadDoanhThuData() {
        if (view == null) {
            return;
        }

        DefaultTableModel modelDoanhThu = view.getModelDoanhThu();
        modelDoanhThu.setRowCount(0);
        view.clearOriginalData();

        double totalRevenue = 0;
        String sql = "SELECT dt.idDoanhThu, dt.idHoaDon, bn.hoTen, dt.thangNam, dt.tongDoanhThu, hd.trangThai " +
                     "FROM DoanhThu dt " +
                     "JOIN HoaDon hd ON dt.idHoaDon = hd.idHoaDon " +
                     "JOIN BenhNhan bn ON hd.idBenhNhan = bn.idBenhNhan " +
                     "WHERE hd.trangThai = 'DaThanhToan'";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int idDoanhThu = resultSet.getInt("idDoanhThu");
                int idHoaDon = resultSet.getInt("idHoaDon");
                String hoTenBenhNhan = resultSet.getString("hoTen");
                Date thangNam = resultSet.getDate("thangNam");
                double tongDoanhThu = resultSet.getDouble("tongDoanhThu");
                String trangThai = resultSet.getString("trangThai");

                Object[] rowData = new Object[]{
                        idDoanhThu, idHoaDon, hoTenBenhNhan,
                        monthYearFormat.format(thangNam), tongDoanhThu, trangThai
                };
                view.loadDoanhThuData(rowData);
                totalRevenue += tongDoanhThu;
            }
            view.updateTotalRow(totalRevenue);
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi truy vấn dữ liệu doanh thu: " + e.getMessage(), NotificationType.ERROR));
        }
    }

    public int themDoanhThu(java.util.Date thangNam, double tongDoanhThu, int idHoaDon) {
        if (!kiemTraHoaDon(idHoaDon)) {
            SwingUtilities.invokeLater(() -> showNotification("Hóa đơn không tồn tại hoặc không hợp lệ!", NotificationType.ERROR));
            return -1;
        }

        capNhatTrangThaiHoaDon(idHoaDon, "DaThanhToan");
        if (tongDoanhThu <= 0) {
            tongDoanhThu = layTongTienHoaDon(idHoaDon);
        }

        String sql = "INSERT INTO DoanhThu (thangNam, tongDoanhThu, idHoaDon) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setDate(1, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setBigDecimal(2, java.math.BigDecimal.valueOf(tongDoanhThu));
            preparedStatement.setInt(3, idHoaDon);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                // Lấy ID vừa thêm
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        SwingUtilities.invokeLater(() -> {
                            showSuccessToast("Thêm doanh thu thành công!");
                            loadDoanhThuDataWithHighlight(newId); // Highlight bản ghi mới
                        });
                        return newId;
                    }
                }
            }
            SwingUtilities.invokeLater(() -> showNotification("Thêm doanh thu thất bại! Kiểm tra ID Hóa đơn.", NotificationType.ERROR));
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi thêm doanh thu: " + e.getMessage(), NotificationType.ERROR));
            return -1;
        }
    }

    private double layTongTienHoaDon(int idHoaDon) {
        double tongTien = 0;
        String sql = "SELECT tongTien FROM HoaDon WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tongTien = resultSet.getDouble("tongTien");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi lấy tổng tiền hóa đơn: " + e.getMessage(), NotificationType.ERROR));
        }
        return tongTien;
    }

    public double getHoaDonAmount(int idHoaDon) {
        String query = "SELECT tongTien FROM HoaDon WHERE idHoaDon = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idHoaDon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("tongTien");
            }
            return 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi lấy số tiền hóa đơn: " + e.getMessage(), NotificationType.ERROR));
            return 0.0;
        }
    }

    private boolean kiemTraHoaDon(int idHoaDon) {
        String sql = "SELECT idHoaDon FROM HoaDon WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi kiểm tra hóa đơn: " + e.getMessage(), NotificationType.ERROR));
            return false;
        }
    }

    public boolean themDoanhThuTuHoaDon(int idHoaDon, double tongTien) {
        Calendar cal = Calendar.getInstance();
        java.util.Date currentDate = cal.getTime();
        int newId = themDoanhThu(currentDate, tongTien, idHoaDon);
        return newId > 0; // Trả về true nếu thêm thành công (ID > 0), false nếu thất bại
    }

    public void xoaDoanhThu(int idDoanhThu) {
        int idHoaDon = -1;
        try {
            String selectSql = "SELECT idHoaDon FROM DoanhThu WHERE idDoanhThu = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, idDoanhThu);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    idHoaDon = rs.getInt("idHoaDon");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi lấy ID hóa đơn: " + e.getMessage(), NotificationType.ERROR));
        }

        String sql = "DELETE FROM DoanhThu WHERE idDoanhThu = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idDoanhThu);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                if (idHoaDon > 0 && !kiemTraHoaDonTrongDoanhThuKhac(idHoaDon, idDoanhThu)) {
                    capNhatTrangThaiHoaDon(idHoaDon, "ChuaThanhToan");
                }
                SwingUtilities.invokeLater(() -> {
                    showSuccessToast("Xóa doanh thu thành công!");
                    loadDoanhThuData();
                });
            } else {
                SwingUtilities.invokeLater(() -> showNotification("Không tìm thấy doanh thu để xóa!", NotificationType.ERROR));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi xóa doanh thu: " + e.getMessage(), NotificationType.ERROR));
        }
    }

    public void suaDoanhThu(int idDoanhThu, java.util.Date thangNam, double tongDoanhThu, int idHoaDon) {
        if (!kiemTraHoaDon(idHoaDon)) {
            SwingUtilities.invokeLater(() -> showNotification("Hóa đơn không tồn tại hoặc không hợp lệ!", NotificationType.ERROR));
            return;
        }

        int oldIdHoaDon = -1;
        try {
            String selectSql = "SELECT idHoaDon FROM DoanhThu WHERE idDoanhThu = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, idDoanhThu);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    oldIdHoaDon = rs.getInt("idHoaDon");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi lấy ID hóa đơn cũ: " + e.getMessage(), NotificationType.ERROR));
        }

        capNhatTrangThaiHoaDon(idHoaDon, "DaThanhToan");
        if (oldIdHoaDon != idHoaDon && oldIdHoaDon > 0) {
            if (!kiemTraHoaDonTrongDoanhThuKhac(oldIdHoaDon, idDoanhThu)) {
                capNhatTrangThaiHoaDon(oldIdHoaDon, "ChuaThanhToan");
            }
        }

        if (tongDoanhThu <= 0) {
            tongDoanhThu = layTongTienHoaDon(idHoaDon);
        }

        String sql = "UPDATE DoanhThu SET thangNam = ?, tongDoanhThu = ?, idHoaDon = ? WHERE idDoanhThu = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, new java.sql.Date(thangNam.getTime()));
            preparedStatement.setBigDecimal(2, java.math.BigDecimal.valueOf(tongDoanhThu));
            preparedStatement.setInt(3, idHoaDon);
            preparedStatement.setInt(4, idDoanhThu);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                SwingUtilities.invokeLater(() -> {
                    showSuccessToast("Sửa doanh thu thành công!");
                    loadDoanhThuDataWithHighlight(idDoanhThu); // Highlight bản ghi đã sửa
                });
            } else {
                SwingUtilities.invokeLater(() -> showNotification("Không tìm thấy doanh thu để sửa! Kiểm tra ID Hóa đơn.", NotificationType.ERROR));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi sửa doanh thu: " + e.getMessage(), NotificationType.ERROR));
        }
    }

    private boolean kiemTraHoaDonTrongDoanhThuKhac(int idHoaDon, int idDoanhThu) {
        String sql = "SELECT COUNT(*) AS count FROM DoanhThu WHERE idHoaDon = ? AND idDoanhThu != ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            preparedStatement.setInt(2, idDoanhThu);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi kiểm tra hóa đơn trong doanh thu: " + e.getMessage(), NotificationType.ERROR));
        }
        return false;
    }

    private void capNhatTrangThaiHoaDon(int idHoaDon, String trangThai) {
        if (hoaDonController != null) {
            model.HoaDon hoaDon = hoaDonController.layHoaDonTheoId(idHoaDon);
            if (hoaDon != null) {
                hoaDon.setTrangThai(trangThai);
                hoaDonController.capNhatHoaDon(hoaDon);
                return;
            }
        }

        String sql = "UPDATE HoaDon SET trangThai = ? WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, trangThai);
            preparedStatement.setInt(2, idHoaDon);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi cập nhật trạng thái hóa đơn: " + e.getMessage(), NotificationType.ERROR));
        }
    }

    public boolean kiemTraHoaDonTrongDoanhThu(int idHoaDon) {
        String sql = "SELECT COUNT(*) AS count FROM DoanhThu WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi kiểm tra hóa đơn trong doanh thu: " + e.getMessage(), NotificationType.ERROR));
        }
        return false;
    }

    public boolean xoaDoanhThuTheoHoaDonId(int idHoaDon) {
        String sql = "DELETE FROM DoanhThu WHERE idHoaDon = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, idHoaDon);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                capNhatTrangThaiHoaDon(idHoaDon, "ChuaThanhToan");
                SwingUtilities.invokeLater(() -> showSuccessToast("Xóa doanh thu theo hóa đơn thành công!"));
            }
            return affectedRows >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi xóa doanh thu theo hóa đơn: " + e.getMessage(), NotificationType.ERROR));
            return false;
        }
    }

    public List<Integer> getAvailableHoaDonIDs() throws SQLException {
        List<Integer> hoaDonIDs = new ArrayList<>();
        String sql = "SELECT idHoaDon FROM hoadon WHERE trangThai = 'DaThanhToan' AND idHoaDon NOT IN (SELECT idHoaDon FROM doanhthu WHERE idHoaDon IS NOT NULL)";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                hoaDonIDs.add(rs.getInt("idHoaDon"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi lấy danh sách ID hóa đơn: " + e.getMessage(), NotificationType.ERROR));
            throw e;
        }
        return hoaDonIDs;
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showNotification("Lỗi đóng kết nối cơ sở dữ liệu: " + e.getMessage(), NotificationType.ERROR));
            }
        }
    }

    private void showSuccessToast(String message) {
        if (view == null) {
            System.err.println("DoanhThuUI is null, cannot show toast: " + message);
            return;
        }

        // Sử dụng null làm parent để tránh lỗi nếu view không phải JFrame
        JDialog toastDialog = new JDialog((Frame) null, false);
        toastDialog.setUndecorated(true);
        toastDialog.setAlwaysOnTop(true);
        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(successColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        toastPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(boldFont);
        messageLabel.setForeground(Color.WHITE);
        toastPanel.add(messageLabel);
        toastDialog.add(toastPanel);
        toastDialog.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toastDialog.setLocation(
                screenSize.width - toastDialog.getWidth() - 20,
                screenSize.height - toastDialog.getHeight() - 60
        );
        toastDialog.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                SwingUtilities.invokeLater(toastDialog::dispose);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showNotification(String message, NotificationType type) {
        if (view == null) {
            System.err.println("DoanhThuUI is null, cannot show notification: " + message);
            return;
        }

        // Sử dụng null làm parent để tránh lỗi nếu view không phải JFrame
        JDialog toastDialog = new JDialog((Frame) null, false);
        toastDialog.setUndecorated(true);
        toastDialog.setAlwaysOnTop(true);
        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(type.color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        toastPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        JLabel titleLabel = new JLabel(type.title);
        titleLabel.setFont(boldFont);
        titleLabel.setForeground(Color.WHITE);
        toastPanel.add(titleLabel);
        JLabel messageLabel = new JLabel("<html><div style='width: 300px;'>" + message + "</div></html>");
        messageLabel.setFont(regularFont);
        messageLabel.setForeground(Color.WHITE);
        toastPanel.add(messageLabel);
        toastDialog.add(toastPanel);
        toastDialog.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toastDialog.setLocation(
                screenSize.width - toastDialog.getWidth() - 20,
                screenSize.height - toastDialog.getHeight() - 60
        );
        toastDialog.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                SwingUtilities.invokeLater(toastDialog::dispose);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public int getLastInsertedId() {
        String sql = "SELECT LAST_INSERT_ID() AS id";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi lấy ID doanh thu mới: " + e.getMessage(), NotificationType.ERROR));
        }
        return -1;
    }
    public void loadDoanhThuDataWithHighlight(int highlightId) {
        if (view == null) {
            return;
        }

        DefaultTableModel modelDoanhThu = view.getModelDoanhThu();
        modelDoanhThu.setRowCount(0);
        view.clearOriginalData();

        double totalRevenue = 0;
        String sql = "SELECT dt.idDoanhThu, dt.idHoaDon, bn.hoTen, dt.thangNam, dt.tongDoanhThu, hd.trangThai " +
                     "FROM DoanhThu dt " +
                     "JOIN HoaDon hd ON dt.idHoaDon = hd.idHoaDon " +
                     "JOIN BenhNhan bn ON hd.idBenhNhan = bn.idBenhNhan " +
                     "WHERE hd.trangThai = 'DaThanhToan'";
        
        List<Object[]> dataList = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int idDoanhThu = resultSet.getInt("idDoanhThu");
                int idHoaDon = resultSet.getInt("idHoaDon");
                String hoTenBenhNhan = resultSet.getString("hoTen");
                Date thangNam = resultSet.getDate("thangNam");
                double tongDoanhThu = resultSet.getDouble("tongDoanhThu");
                String trangThai = resultSet.getString("trangThai");

                Object[] rowData = new Object[]{
                        idDoanhThu, idHoaDon, hoTenBenhNhan,
                        monthYearFormat.format(thangNam), tongDoanhThu, trangThai
                };
                dataList.add(rowData);
                totalRevenue += tongDoanhThu;
            }

            // Sắp xếp dữ liệu: highlightId lên đầu, sau đó theo ID giảm dần
            dataList.sort((row1, row2) -> {
                int id1 = (Integer) row1[0];
                int id2 = (Integer) row2[0];
                if (id1 == highlightId) return -1;
                if (id2 == highlightId) return 1;
                return Integer.compare(id2, id1); // Sắp xếp giảm dần theo ID
            });

            // Thêm dữ liệu vào view
            for (Object[] rowData : dataList) {
                view.loadDoanhThuData(rowData, highlightId);
            }
            view.updateTotalRow(totalRevenue);
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> showNotification("Lỗi truy vấn dữ liệu doanh thu: " + e.getMessage(), NotificationType.ERROR));
        }
    }
}