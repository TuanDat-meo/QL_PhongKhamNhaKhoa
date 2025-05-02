// model.ThanhToanBenhNhan.java
package model;

public class ThanhToanBenhNhan {
    private int idThanhToan;
    private int idHoaDon;
    private double soTien;
    private String hinhThucThanhToan;
    private String maQR;
    private String trangThai;

    public ThanhToanBenhNhan() {
    }

    public ThanhToanBenhNhan(int idHoaDon, double soTien, String hinhThucThanhToan, String maQR, String trangThai) {
        this.idHoaDon = idHoaDon;
        this.soTien = soTien;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.maQR = maQR;
        this.trangThai = trangThai;
    }

    public int getIdThanhToan() {
        return idThanhToan;
    }

    public void setIdThanhToan(int idThanhToan) {
        this.idThanhToan = idThanhToan;
    }

    public int getIdHoaDon() {
        return idHoaDon;
    }

    public void setIdHoaDon(int idHoaDon) {
        this.idHoaDon = idHoaDon;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public String getMaQR() {
        return maQR;
    }

    public void setMaQR(String maQR) {
        this.maQR = maQR;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "ThanhToanBenhNhan{" +
                "idThanhToan=" + idThanhToan +
                ", idHoaDon=" + idHoaDon +
                ", soTien=" + soTien +
                ", hinhThucThanhToan='" + hinhThucThanhToan + '\'' +
                ", maQR='" + maQR + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}