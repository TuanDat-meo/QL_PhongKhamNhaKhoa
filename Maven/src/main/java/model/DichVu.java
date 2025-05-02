package model;

public class DichVu {
    private int id;
    private String tenDichVu;
    private double gia;

    public DichVu(int id, String tenDichVu, double gia) {
        this.id = id;
        this.tenDichVu = tenDichVu;
        this.gia = gia;
    }

    public int getId() { return id; }
    public String getTenDichVu() { return tenDichVu; }
    public double getGia() { return gia; }

    public void setId(int id) { this.id = id; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }
    public void setGia(double gia) { this.gia = gia; }
}
