package model;

import java.time.LocalDate; // Import cho LocalDate để xử lý ngày tháng

public class NhaCungCap {
    private String maNCC;
    private String tenNCC;
    private String diaChi;
    private String soDienThoai;
    private LocalDate ngayDangKy; // Thuộc tính mới
    private String maSoThue;     // Thuộc tính mới

    public NhaCungCap() {
        // Constructor mặc định
    }

    // Constructor đầy đủ với các thuộc tính mới
    public NhaCungCap(String maNCC, String tenNCC, String diaChi, String soDienThoai,
                      LocalDate ngayDangKy, String maSoThue) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.ngayDangKy = ngayDangKy;
        this.maSoThue = maSoThue;
    }

    // --- Getters và Setters cho các thuộc tính cũ ---
    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    // --- Getters và Setters cho các thuộc tính mới ---
    public LocalDate getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(LocalDate ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public String getMaSoThue() {
        return maSoThue;
    }

    public void setMaSoThue(String maSoThue) {
        this.maSoThue = maSoThue;
    }

    @Override
    public String toString() {
        return "NhaCungCap{" +
                "maNCC='" + maNCC + '\'' +
                ", tenNCC='" + tenNCC + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", ngayDangKy=" + ngayDangKy +
                ", maSoThue='" + maSoThue + '\'' +
                '}';
    }
}