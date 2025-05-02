package model;

import model.BacSi;

public class BacSiItem {
    private BacSi bacSi;
    
    public BacSiItem(BacSi bacSi) {
        this.bacSi = bacSi;
    }
    
    public BacSi getBacSi() {
        return bacSi;
    }
    
    @Override
    public String toString() {
        return bacSi.getHoTenBacSi() + " - " + bacSi.getChuyenKhoa() + 
               " (" + bacSi.getTenPhong() + ")";
    }
}