package model;

public class BacSi {
    private int idBacSi;
    private int idNguoiDung;
    private String hoTenBacSi;
    private String chuyenKhoa;
    private String bangCap;
    private int kinhNghiem;
    private int idPhongKham;
    
    // Additional fields from joined tables
    private String tenPhong;
    private String emailNguoiDung;
    private String soDienThoaiNguoiDung;
    
    // Constructors
    public BacSi() {
        // Default constructor
    }
    
    public BacSi(int idBacSi, int idNguoiDung, String hoTenBacSi, String chuyenKhoa, 
                String bangCap, int kinhNghiem, int idPhongKham) {
        this.idBacSi = idBacSi;
        this.idNguoiDung = idNguoiDung;
        this.hoTenBacSi = hoTenBacSi;
        this.chuyenKhoa = chuyenKhoa;
        this.bangCap = bangCap;
        this.kinhNghiem = kinhNghiem;
        this.idPhongKham = idPhongKham;
    }
    
    // Getters and Setters
    public int getIdBacSi() {
        return idBacSi;
    }
    
    public void setIdBacSi(int idBacSi) {
        this.idBacSi = idBacSi;
    }
    
    public int getIdNguoiDung() {
        return idNguoiDung;
    }
    
    public void setIdNguoiDung(int idNguoiDung) {
        this.idNguoiDung = idNguoiDung;
    }
    
    public String getHoTenBacSi() {
        return hoTenBacSi;
    }
    
    public void setHoTenBacSi(String hoTenBacSi) {
        this.hoTenBacSi = hoTenBacSi;
    }
    
    public String getChuyenKhoa() {
        return chuyenKhoa;
    }
    
    public void setChuyenKhoa(String chuyenKhoa) {
        this.chuyenKhoa = chuyenKhoa;
    }
    
    public String getBangCap() {
        return bangCap;
    }
    
    public void setBangCap(String bangCap) {
        this.bangCap = bangCap;
    }
    
    public int getKinhNghiem() {
        return kinhNghiem;
    }
    
    public void setKinhNghiem(int kinhNghiem) {
        this.kinhNghiem = kinhNghiem;
    }
    
    public int getIdPhongKham() {
        return idPhongKham;
    }
    
    public void setIdPhongKham(int idPhongKham) {
        this.idPhongKham = idPhongKham;
    }
    
    // Additional getters and setters for joined fields
    public String getTenPhong() {
        return tenPhong;
    }
    
    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }
    
    public String getEmailNguoiDung() {
        return emailNguoiDung;
    }
    
    public void setEmailNguoiDung(String emailNguoiDung) {
        this.emailNguoiDung = emailNguoiDung;
    }
    
    public String getSoDienThoaiNguoiDung() {
        return soDienThoaiNguoiDung;
    }
    
    public void setSoDienThoaiNguoiDung(String soDienThoaiNguoiDung) {
        this.soDienThoaiNguoiDung = soDienThoaiNguoiDung;
    }
    
    @Override
    public String toString() {
        return "BacSi{" +
                "idBacSi=" + idBacSi +
                ", hoTenBacSi='" + hoTenBacSi + '\'' +
                ", chuyenKhoa='" + chuyenKhoa + '\'' +
                ", bangCap='" + bangCap + '\'' +
                ", kinhNghiem=" + kinhNghiem +
                ", tenPhong='" + tenPhong + '\'' +
                '}';
    }
}