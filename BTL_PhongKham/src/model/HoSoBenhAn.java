package model;

import java.sql.Date;

public class HoSoBenhAn {
    private int idHoSo;
    private int idBenhNhan;
    private String chuanDoan;
    private String ghiChu;
    private Date ngayTao;
    private String trangThai;

    public HoSoBenhAn() {
    }

    public HoSoBenhAn(int idHoSo, int idBenhNhan, String chuanDoan, String ghiChu, Date ngayTao, String trangThai) {
        this.idHoSo = idHoSo;
        this.idBenhNhan = idBenhNhan;
        this.chuanDoan = chuanDoan;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
    }

    // Getters and setters
    public int getIdHoSo() {
        return idHoSo;
    }

    public void setIdHoSo(int idHoSo) {
        this.idHoSo = idHoSo;
    }

    public int getIdBenhNhan() {
        return idBenhNhan;
    }

    public void setIdBenhNhan(int idBenhNhan) {
        this.idBenhNhan = idBenhNhan;
    }

    public String getChuanDoan() {
        return chuanDoan;
    }

    public void setChuanDoan(String chuanDoan) {
        this.chuanDoan = chuanDoan;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}