package model;

import java.sql.Date;

public class DieuTri {
    private int idDieuTri;
    private int idHoSo;
    private int idBacSi;
    private String moTa;
    private Date ngayDieuTri;

    public DieuTri() {
    }

    public DieuTri(int idDieuTri, int idHoSo, int idBacSi, String moTa, Date ngayDieuTri) {
        this.idDieuTri = idDieuTri;
        this.idHoSo = idHoSo;
        this.idBacSi = idBacSi;
        this.moTa = moTa;
        this.ngayDieuTri = ngayDieuTri;
    }

    // Getters and setters
    public int getIdDieuTri() {
        return idDieuTri;
    }

    public void setIdDieuTri(int idDieuTri) {
        this.idDieuTri = idDieuTri;
    }

    public int getIdHoSo() {
        return idHoSo;
    }

    public void setIdHoSo(int idHoSo) {
        this.idHoSo = idHoSo;
    }

    public int getIdBacSi() {
        return idBacSi;
    }

    public void setIdBacSi(int idBacSi) {
        this.idBacSi = idBacSi;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Date getNgayDieuTri() {
        return ngayDieuTri;
    }

    public void setNgayDieuTri(Date ngayDieuTri) {
        this.ngayDieuTri = ngayDieuTri;
    }
}