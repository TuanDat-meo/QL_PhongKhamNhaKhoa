package model;

import java.sql.Date;

public class DieuTri {
    private int idDieuTri;
    private int idHoSo;
    private int idBacSi;
    private String moTa;
    private Date ngayDieuTri;
    private String chuanDoan;
    private String tenBenhNhan;
    private String tenDieuTri;    // Added field for treatment name
    private Date ngayBatDau;      // Added field for start date
    private Date ngayKetThuc;     // Added field for end date
    private String trangThai;     // Added field for status

    public DieuTri() {
    }

    public DieuTri(int idDieuTri, int idHoSo, int idBacSi, String moTa, Date ngayDieuTri, 
                  String chuanDoan, String tenBenhNhan, String tenDieuTri, 
                  Date ngayBatDau, Date ngayKetThuc, String trangThai) {
        this.idDieuTri = idDieuTri;
        this.idHoSo = idHoSo;
        this.idBacSi = idBacSi;
        this.moTa = moTa;
        this.ngayDieuTri = ngayDieuTri;
        this.chuanDoan = chuanDoan;
        this.tenBenhNhan = tenBenhNhan;
        this.tenDieuTri = tenDieuTri;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

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

    public String getChuanDoan() {
        return chuanDoan;
    }

    public void setChuanDoan(String chuanDoan) {
        this.chuanDoan = chuanDoan;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }
    public String getTenDieuTri() {
        return tenDieuTri;
    }
    
    public void setTenDieuTri(String tenDieuTri) {
        this.tenDieuTri = tenDieuTri;
    }
    
    public Date getNgayBatDau() {
        return ngayBatDau;
    }
    
    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }
    
    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }
    
    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}