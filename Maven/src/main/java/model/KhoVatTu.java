package model;

public class KhoVatTu {
    private int idVatTu;
    private String tenVatTu;
    private int soLuong;
    private String donViTinh;
    private String maNCC; // Sử dụng String maNCC để phù hợp với NhaCungCap
    private String phanLoai;

    public KhoVatTu() {
    }

    public KhoVatTu(int idVatTu, String tenVatTu, int soLuong, String donViTinh, String maNCC, String phanLoai) {
        this.idVatTu = idVatTu;
        this.tenVatTu = tenVatTu;
        this.soLuong = soLuong;
        this.donViTinh = donViTinh;
        this.maNCC = maNCC;
        this.phanLoai = phanLoai;
    }

    public int getIdVatTu() {
        return idVatTu;
    }

    public void setIdVatTu(int idVatTu) {
        this.idVatTu = idVatTu;
    }

    public String getTenVatTu() {
        return tenVatTu;
    }

    public void setTenVatTu(String tenVatTu) {
        this.tenVatTu = tenVatTu;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public String getPhanLoai() {
        return phanLoai;
    }

    public void setPhanLoai(String phanLoai) {
        this.phanLoai = phanLoai;
    }

    @Override
    public String toString() {
        return "KhoVatTu{" +
               "idVatTu=" + idVatTu +
               ", tenVatTu='" + tenVatTu + '\'' +
               ", soLuong=" + soLuong +
               ", donViTinh='" + donViTinh + '\'' +
               ", maNCC='" + maNCC + '\'' +
               ", phanLoai='" + phanLoai + '\'' +
               '}';
    }
}