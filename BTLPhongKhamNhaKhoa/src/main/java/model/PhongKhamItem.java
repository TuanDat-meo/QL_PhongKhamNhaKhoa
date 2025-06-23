package model;

public class PhongKhamItem {
    private PhongKham phongKham;
    
    public PhongKhamItem(PhongKham phongKham) {
        this.phongKham = phongKham;
    }
    
    public PhongKham getPhongKham() {
        return phongKham;
    }
    
    @Override
    public String toString() {
    	if (phongKham == null) {
            return "-- Lựa chọn --";
        }
        return phongKham.getTenPhong() + " - " + phongKham.getDiaChi();
    }
}