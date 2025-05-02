package model;

import model.NguoiDung;

public class NguoiDungItem {
    private NguoiDung nguoiDung;
    
    public NguoiDungItem(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }
    
    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }
    
    @Override
    public String toString() {
        return nguoiDung.getHoTen() + " (" + nguoiDung.getEmail() + ")";
    }
}
