package model;

public class PhongKham {
    private int idPhongKham;
    private String tenPhong;
    private String diaChi;
    
    public PhongKham() {
    }
    
    public PhongKham(int idPhongKham, String tenPhong, String diaChi) {
        this.idPhongKham = idPhongKham;
        this.tenPhong = tenPhong;
        this.diaChi = diaChi;
    }
    
    public int getIdPhongKham() {
        return idPhongKham;
    }
    
    public void setIdPhongKham(int idPhongKham) {
        this.idPhongKham = idPhongKham;
    }
    
    public String getTenPhong() {
        return tenPhong;
    }
    
    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }
    
    public String getDiaChi() {
        return diaChi;
    }
    
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    
    @Override
    public String toString() {
        return tenPhong;
    }
}