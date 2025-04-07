package controller;

import connect.connectMySQL;
import view.ThongKeDoanhThuPanel;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ThongKeController {

    private ThongKeDoanhThuPanel view;
    private Connection conn;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
    private SimpleDateFormat weekDisplayFormat = new SimpleDateFormat("dd/MM/yyyy");

    public ThongKeController(ThongKeDoanhThuPanel view) {
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

    public void thongKeDoanhThuTheoNgay(Date tuNgay, Date denNgay) {
        List<Object[]> dataThongKe = new ArrayList<>();
        String sql = "SELECT DATE(ngayTao) AS ngay, SUM(tongTien) AS tongDoanhThu " +
                     "FROM HoaDon " +
                     "WHERE ngayTao BETWEEN ? AND ? " +
                     "GROUP BY ngay " +
                     "ORDER BY ngay";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, tuNgay);
            preparedStatement.setDate(2, denNgay);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Date ngay = resultSet.getDate("ngay");
                double tongDoanhThu = resultSet.getDouble("tongDoanhThu");
                dataThongKe.add(new Object[]{dateFormat.format(ngay), tongDoanhThu});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi truy vấn thống kê doanh thu theo ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        view.hienThiThongKe(dataThongKe);
    }

    public void thongKeDoanhThuTheoTuanTong(Date tuNgay, Date denNgay) {
        List<Object[]> dataThongKe = new ArrayList<>();
        String sql = "SELECT SUM(tongTien) AS tongDoanhThu " +
                     "FROM HoaDon " +
                     "WHERE ngayTao BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, tuNgay);
            preparedStatement.setDate(2, denNgay);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double tongDoanhThu = resultSet.getDouble("tongDoanhThu");
                String thoiGian = weekDisplayFormat.format(tuNgay) + " - " + weekDisplayFormat.format(denNgay);
                dataThongKe.add(new Object[]{thoiGian, tongDoanhThu});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi truy vấn thống kê doanh thu theo tuần!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        view.hienThiThongKe(dataThongKe);
    }

    public void thongKeDoanhThuTheoThang(int nam, int thang) {
        List<Object[]> dataThongKe = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(ngayTao, '%Y-%m-%d') AS ngay, SUM(tongTien) AS tongDoanhThu " +
                     "FROM HoaDon " +
                     "WHERE YEAR(ngayTao) = ? AND MONTH(ngayTao) = ? " +
                     "GROUP BY ngay " +
                     "ORDER BY ngay";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, nam);
            preparedStatement.setInt(2, thang);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Date ngay = resultSet.getDate("ngay");
                double tongDoanhThu = resultSet.getDouble("tongDoanhThu");
                dataThongKe.add(new Object[]{dateFormat.format(ngay), tongDoanhThu});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi truy vấn thống kê doanh thu theo tháng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        view.hienThiThongKe(dataThongKe);
    }

    public void thongKeDoanhThuTheoNam(int nam) {
        List<Object[]> dataThongKe = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(ngayTao, '%Y-%m') AS thang, SUM(tongTien) AS tongDoanhThu " +
                     "FROM HoaDon " +
                     "WHERE YEAR(ngayTao) = ? " +
                     "GROUP BY thang " +
                     "ORDER BY thang";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, nam);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String thangNam = resultSet.getString("thang");
                double tongDoanhThu = resultSet.getDouble("tongDoanhThu");
                LocalDate date = LocalDate.parse(thangNam + "-01");
                dataThongKe.add(new Object[]{monthYearFormat.format(Date.valueOf(date)), tongDoanhThu});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi truy vấn thống kê doanh thu theo năm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        view.hienThiThongKe(dataThongKe);
    }

    public void thongKeDoanhThuTheoCacNgay(List<Date> danhSachNgay) {
        List<Object[]> dataThongKeChiTiet = new ArrayList<>();
        if (danhSachNgay.isEmpty()) {
            view.hienThiThongKeChiTiet(dataThongKeChiTiet);
            return;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < danhSachNgay.size(); i++) {
            placeholders.append("?");
            if (i < danhSachNgay.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT DATE(ngayTao) AS ngay, SUM(tongTien) AS tongDoanhThu " +
                     "FROM HoaDon " +
                     "WHERE ngayTao IN (" + placeholders.toString() + ") " +
                     "GROUP BY ngay " +
                     "ORDER BY ngay";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (int i = 0; i < danhSachNgay.size(); i++) {
                preparedStatement.setDate(i + 1, danhSachNgay.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Date ngay = resultSet.getDate("ngay");
                double tongDoanhThu = resultSet.getDouble("tongDoanhThu");
                dataThongKeChiTiet.add(new Object[]{dateFormat.format(ngay), tongDoanhThu});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi truy vấn thống kê chi tiết theo ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        view.hienThiThongKeChiTiet(dataThongKeChiTiet);
    }

    public void thongKeDoanhThuTheoCacThangTrongNam(List<Date> danhSachNgayDauThang) {
        List<Object[]> dataThongKeChiTiet = new ArrayList<>();
        if (danhSachNgayDauThang.isEmpty()) {
            view.hienThiThongKeChiTiet(dataThongKeChiTiet);
            return;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < danhSachNgayDauThang.size(); i++) {
            placeholders.append("?");
            if (i < danhSachNgayDauThang.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT DATE_FORMAT(ngayTao, '%Y-%m') AS thang, SUM(tongTien) AS tongDoanhThu " +
                     "FROM HoaDon " +
                     "WHERE DATE(ngayTao) IN (" + placeholders.toString() + ") " +
                     "GROUP BY thang " +
                     "ORDER BY thang";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (int i = 0; i < danhSachNgayDauThang.size(); i++) {
                preparedStatement.setDate(i + 1, danhSachNgayDauThang.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String thangNam = resultSet.getString("thang");
                double tongDoanhThu = resultSet.getDouble("tongDoanhThu");
                LocalDate date = LocalDate.parse(thangNam + "-01");
                dataThongKeChiTiet.add(new Object[]{monthYearFormat.format(Date.valueOf(date)), tongDoanhThu});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi truy vấn thống kê chi tiết theo tháng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        view.hienThiThongKeChiTiet(dataThongKeChiTiet);
    }
}