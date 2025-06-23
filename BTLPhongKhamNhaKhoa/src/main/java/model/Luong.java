package model;

import java.sql.Date;

public class Luong {
    private int idLuong;
    private int idNguoiDung;
    private Date thangNam;
    private double luongCoBan;
    private double thuong;
    private double khauTru;
    private double tongLuong;
    
    public Luong() {
    }
    
    public Luong(int idLuong, int idNguoiDung, Date thangNam, double luongCoBan, double thuong, double khauTru, double tongLuong) {
        this.idLuong = idLuong;
        this.idNguoiDung = idNguoiDung;
        this.thangNam = thangNam;
        this.luongCoBan = luongCoBan;
        this.thuong = thuong;
        this.khauTru = khauTru;
        this.tongLuong = tongLuong;
    }

    public int getIdLuong() {
        return idLuong;
    }

    public void setIdLuong(int idLuong) {
        this.idLuong = idLuong;
    }

    public int getIdNguoiDung() {
        return idNguoiDung;
    }

    public void setIdNguoiDung(int idNguoiDung) {
        this.idNguoiDung = idNguoiDung;
    }

    public Date getThangNam() {
        return thangNam;
    }

    public void setThangNam(Date thangNam) {
        this.thangNam = thangNam;
    }

    public double getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(double luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    public double getThuong() {
        return thuong;
    }

    public void setThuong(double thuong) {
        this.thuong = thuong;
    }

    public double getKhauTru() {
        return khauTru;
    }

    public void setKhauTru(double khauTru) {
        this.khauTru = khauTru;
    }

    public double getTongLuong() {
        return tongLuong;
    }

    public void setTongLuong(double tongLuong) {
        this.tongLuong = tongLuong;
    }
    public double tinhTongLuong() {
        return this.luongCoBan + this.thuong - this.khauTru;
    }
    public void capNhatTongLuong() {
        this.tongLuong = tinhTongLuong();
    }
}