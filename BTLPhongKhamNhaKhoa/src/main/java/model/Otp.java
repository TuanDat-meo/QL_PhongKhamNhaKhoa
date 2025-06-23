package model;

import java.sql.Timestamp;

public class Otp {
    private int idOTP;
    private int idNguoiDung;
    private String maOTP;
    private Timestamp thoiGianHetHan;
    private boolean daSuDung;
    private String loai;

    public Otp() {}

    public Otp(int idOTP, int idNguoiDung, String maOTP, Timestamp thoiGianHetHan, boolean daSuDung, String loai) {
        this.idOTP = idOTP;
        this.idNguoiDung = idNguoiDung;
        this.maOTP = maOTP;
        this.thoiGianHetHan = thoiGianHetHan;
        this.daSuDung = daSuDung;
        this.loai = loai;
    }

    public int getIdOTP() { return idOTP; }
    public void setIdOTP(int idOTP) { this.idOTP = idOTP; }

    public int getIdNguoiDung() { return idNguoiDung; }
    public void setIdNguoiDung(int idNguoiDung) { this.idNguoiDung = idNguoiDung; }

    public String getMaOTP() { return maOTP; }
    public void setMaOTP(String maOTP) { this.maOTP = maOTP; }

    public Timestamp getThoiGianHetHan() { return thoiGianHetHan; }
    public void setThoiGianHetHan(Timestamp thoiGianHetHan) { this.thoiGianHetHan = thoiGianHetHan; }

    public boolean isDaSuDung() { return daSuDung; }
    public void setDaSuDung(boolean daSuDung) { this.daSuDung = daSuDung; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
} 