package model;

public class ThongKe {
    private int soLuongVatTu;
    private int soLuongNhaCungCap;
    private int soLuongBenhNhan;
    private int soLuongLichHen;
    public ThongKe() {
    }

    public ThongKe(int soLuongVatTu, int soLuongNhaCungCap, int soLuongBenhNhan, int soLuongLichHen) {
        this.soLuongVatTu = soLuongVatTu;
        this.soLuongNhaCungCap = soLuongNhaCungCap;
        this.soLuongBenhNhan = soLuongBenhNhan;
        this.soLuongLichHen = soLuongLichHen;
    }

    public int getSoLuongVatTu() {
        return soLuongVatTu;
    }

    public void setSoLuongVatTu(int soLuongVatTu) {
        this.soLuongVatTu = soLuongVatTu;
    }

    public int getSoLuongNhaCungCap() {
        return soLuongNhaCungCap;
    }

    public void setSoLuongNhaCungCap(int soLuongNhaCungCap) {
        this.soLuongNhaCungCap = soLuongNhaCungCap;
    }

    public int getSoLuongBenhNhan() {
        return soLuongBenhNhan;
    }

    public void setSoLuongBenhNhan(int soLuongBenhNhan) {
        this.soLuongBenhNhan = soLuongBenhNhan;
    }

    public int getSoLuongLichHen() {
        return soLuongLichHen;
    }

    public void setSoLuongLichHen(int soLuongLichHen) {
        this.soLuongLichHen = soLuongLichHen;
    }

    @Override
    public String toString() {
        return "ThongKe{" +
                "soLuongVatTu=" + soLuongVatTu +
                ", soLuongNhaCungCap=" + soLuongNhaCungCap +
                ", soLuongBenhNhan=" + soLuongBenhNhan +
                ", soLuongLichHen=" + soLuongLichHen +
                '}';
    }
}