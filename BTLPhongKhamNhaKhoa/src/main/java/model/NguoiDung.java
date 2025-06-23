package model;

import java.sql.Date;

public class NguoiDung {
    private int idNguoiDung;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String matKhau;
    private Date ngaySinh;
    private String gioiTinh;
    private String vaiTro;

    public NguoiDung() {
    }

    public NguoiDung(int idNguoiDung, String hoTen, String email, String soDienThoai, String matKhau, Date ngaySinh, String gioiTinh, String vaiTro) {
        this.idNguoiDung = idNguoiDung;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.vaiTro = vaiTro;
    }

    //  (dùng cho đăng ký người dùng mới)
    public NguoiDung(String hoTen, String email, String soDienThoai, String matKhau, Date ngaySinh, String gioiTinh, String vaiTro) {
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.vaiTro = vaiTro;
    }
    public int getIdNguoiDung() {
        return idNguoiDung;
    }

    public void setIdNguoiDung(int idNguoiDung) {
        this.idNguoiDung = idNguoiDung;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    @Override
    public String toString() {
        return "NguoiDung{" +
                "idNguoiDung=" + idNguoiDung +
                ", hoTen='" + hoTen + '\'' +
                ", email='" + email + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", vaiTro='" + vaiTro + '\'' +
                '}';
    }
}