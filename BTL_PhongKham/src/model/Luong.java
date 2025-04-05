package model;

public class Luong {
    private String hoTen;
    private String thangNam;
    private double luongCoBan, thuong, khauTru, tongLuong;

    public Luong(String hoTen, String thangNam, double luongCoBan, double thuong, double khauTru, double tongLuong) {
        this.hoTen = hoTen;
        this.thangNam = thangNam;
        this.luongCoBan = luongCoBan;
        this.thuong = thuong;
        this.khauTru = khauTru;
        this.tongLuong = tongLuong;
    }

    public Object[] toRow() {
        return new Object[]{ hoTen, thangNam, luongCoBan, thuong, khauTru, tongLuong };
    }
}
