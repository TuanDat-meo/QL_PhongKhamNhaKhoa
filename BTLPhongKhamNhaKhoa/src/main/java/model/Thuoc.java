package model;

public class Thuoc {
    private int idThuoc;
    private String tenThuoc;
    private String donViTinh;
    private double gia;

    public Thuoc() {
    }

    public int getIdThuoc() {
        return idThuoc;
    }

    public void setIdThuoc(int idThuoc) {
        this.idThuoc = idThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }
}