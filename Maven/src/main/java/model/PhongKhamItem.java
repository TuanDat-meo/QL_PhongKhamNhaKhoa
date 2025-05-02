package model;

import model.PhongKham;

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
        return phongKham.getTenPhong() + " - " + phongKham.getDiaChi();
    }
}