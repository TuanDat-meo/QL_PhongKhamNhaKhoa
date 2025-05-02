// model/ChiTietDonThuoc.java
package model;

public class ChiTietDonThuoc {
    private int idChiTietDon;
    private int idDonThuoc;
    private int idThuoc;
    private int soLuong;
    private String huongDanSuDung;
    private Thuoc thuoc; // Thêm thuộc tính để tham chiếu đến thông tin thuốc

    public ChiTietDonThuoc() {
        this.thuoc = new Thuoc();
    }

    public int getIdChiTietDon() {
        return idChiTietDon;
    }

    public void setIdChiTietDon(int idChiTietDon) {
        this.idChiTietDon = idChiTietDon;
    }

    public int getIdDonThuoc() {
        return idDonThuoc;
    }

    public void setIdDonThuoc(int idDonThuoc) {
        this.idDonThuoc = idDonThuoc;
    }

    public int getIdThuoc() {
        return idThuoc;
    }

    public void setIdThuoc(int idThuoc) {
        this.idThuoc = idThuoc;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getHuongDanSuDung() {
        return huongDanSuDung;
    }

    public void setHuongDanSuDung(String huongDanSuDung) {
        this.huongDanSuDung = huongDanSuDung;
    }

    public Thuoc getThuoc() {
        return thuoc;
    }

    public void setThuoc(Thuoc thuoc) {
        this.thuoc = thuoc;
    }
}