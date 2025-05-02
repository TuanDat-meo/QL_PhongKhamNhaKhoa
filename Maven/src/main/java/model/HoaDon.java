package model;

import java.util.Date;

public class HoaDon {
    private int idHoaDon;
    private int idBenhNhan;
    private Date ngayTao;
    private double tongTien;
    private String trangThai;

    // Constructors
    public HoaDon() {
    }

    public HoaDon(int idHoaDon, int idBenhNhan, Date ngayTao, double tongTien, String trangThai) {
        this.idHoaDon = idHoaDon;
        this.idBenhNhan = idBenhNhan;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public int getIdHoaDon() {
        return idHoaDon;
    }

    public void setIdHoaDon(int idHoaDon) {
        this.idHoaDon = idHoaDon;
    }

    public int getIdBenhNhan() {
        return idBenhNhan;
    }

    public void setIdBenhNhan(int idBenhNhan) {
        this.idBenhNhan = idBenhNhan;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "idHoaDon=" + idHoaDon +
                ", idBenhNhan=" + idBenhNhan +
                ", ngayTao=" + ngayTao +
                ", tongTien=" + tongTien +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
