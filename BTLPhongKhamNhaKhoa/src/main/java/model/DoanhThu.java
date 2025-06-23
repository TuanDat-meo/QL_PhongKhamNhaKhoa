package model;

import java.util.Date;

public class DoanhThu {
    private int idDoanhThu;
    private Date thangNam;
    private double tongDoanhThu;
    private int idHoaDon; // Có thể là null nếu là doanh thu tổng theo tháng

    public DoanhThu() {
    }

    public DoanhThu(int idDoanhThu, Date thangNam, double tongDoanhThu, int idHoaDon) {
        this.idDoanhThu = idDoanhThu;
        this.thangNam = thangNam;
        this.tongDoanhThu = tongDoanhThu;
        this.idHoaDon = idHoaDon;
    }

    public int getIdDoanhThu() {
        return idDoanhThu;
    }

    public void setIdDoanhThu(int idDoanhThu) {
        this.idDoanhThu = idDoanhThu;
    }

    public Date getThangNam() {
        return thangNam;
    }

    public void setThangNam(Date thangNam) {
        this.thangNam = thangNam;
    }

    public double getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(double tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    public int getIdHoaDon() {
        return idHoaDon;
    }

    public void setIdHoaDon(int idHoaDon) {
        this.idHoaDon = idHoaDon;
    }
}