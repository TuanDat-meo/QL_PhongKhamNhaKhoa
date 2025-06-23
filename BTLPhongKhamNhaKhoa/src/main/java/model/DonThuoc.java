// model/DonThuoc.java
package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DonThuoc {
    private int idDonThuoc;
    private int idBenhNhan;
    private int idBacSi;
    private Date ngayKeDon;
    private int idHoSoBenhAn; 
    private List<ChiTietDonThuoc> chiTietDonThuocs;

    public DonThuoc() {
        this.chiTietDonThuocs = new ArrayList<>();
    }

    public int getIdDonThuoc() {
        return idDonThuoc;
    }

    public void setIdDonThuoc(int idDonThuoc) {
        this.idDonThuoc = idDonThuoc;
    }

    public int getIdBenhNhan() {
        return idBenhNhan;
    }

    public void setIdBenhNhan(int idBenhNhan) {
        this.idBenhNhan = idBenhNhan;
    }

    public int getIdBacSi() {
        return idBacSi;
    }

    public void setIdBacSi(int idBacSi) {
        this.idBacSi = idBacSi;
    }

    public Date getNgayKeDon() {
        return ngayKeDon;
    }

    public void setNgayKeDon(Date ngayKeDon) {
        this.ngayKeDon = ngayKeDon;
    }

    public int getIdHoSoBenhAn() {
        return idHoSoBenhAn;
    }

    public void setIdHoSoBenhAn(int idHoSoBenhAn) {
        this.idHoSoBenhAn = idHoSoBenhAn;
    }

    public List<ChiTietDonThuoc> getChiTietDonThuocs() {
        return chiTietDonThuocs;
    }

    public void setChiTietDonThuocs(List<ChiTietDonThuoc> chiTietDonThuocs) {
        this.chiTietDonThuocs = chiTietDonThuocs;
    }
}