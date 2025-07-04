package model;

import java.sql.Date;
import java.sql.Time;

public class LichHen {
    private int idLichHen;
    private int idBacSi;
    private String hoTenBacSi;
    private int idBenhNhan;
    private String hoTenBenhNhan;
    private Date ngayHen;
    private int idPhongKham;
    private String tenPhong;
    private Time gioHen;
    private TrangThaiLichHen trangThai; // Đổi kiểu từ String thành TrangThaiLichHen
    private String moTa;

    // Constructor
    public LichHen() {
    }

    // Constructor đã sửa kiểu tham số trangThai
    public LichHen(int idLichHen, int idBacSi, String hoTenBacSi, int idBenhNhan, String hoTenBenhNhan, 
                   Date ngayHen, int idPhongKham, String tenPhong, Time gioHen, TrangThaiLichHen trangThai, String moTa) {
        this.idLichHen = idLichHen;
        this.idBacSi = idBacSi;
        this.hoTenBacSi = hoTenBacSi;
        this.idBenhNhan = idBenhNhan;
        this.hoTenBenhNhan = hoTenBenhNhan;
        this.ngayHen = ngayHen;
        this.idPhongKham = idPhongKham;
        this.tenPhong = tenPhong;
        this.gioHen = gioHen;
        this.trangThai = trangThai;
        this.moTa = moTa;
    }   
    public int getIdLichHen() {
        return idLichHen;
    }

    public void setIdLichHen(int idLichHen) {
        this.idLichHen = idLichHen;
    }

    public int getIdBacSi() {
        return idBacSi;
    }

    public void setIdBacSi(int idBacSi) {
        this.idBacSi = idBacSi;
    }

    public String getHoTenBacSi() {
        return hoTenBacSi;
    }

    public void setHoTenBacSi(String hoTenBacSi) {
        this.hoTenBacSi = hoTenBacSi;
    }

    public int getIdBenhNhan() {
        return idBenhNhan;
    }

    public void setIdBenhNhan(int idBenhNhan) {
        this.idBenhNhan = idBenhNhan;
    }

    public String getHoTenBenhNhan() {
        return hoTenBenhNhan;
    }

    public void setHoTenBenhNhan(String hoTenBenhNhan) {
        this.hoTenBenhNhan = hoTenBenhNhan;
    }

    public Date getNgayHen() {
        return ngayHen;
    }

    public void setNgayHen(Date ngayHen) {
        this.ngayHen = ngayHen;
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

    public Time getGioHen() {
        return gioHen;
    }

    public void setGioHen(Time gioHen) {
        this.gioHen = gioHen;
    }

    // Lấy giá trị trạng thái dưới dạng String
    public String getTrangThai() {
        return trangThai != null ? trangThai.getValue() : null;
    }

    // Set trạng thái từ String
    public void setTrangThai(String trangThai) {
        this.trangThai = TrangThaiLichHen.fromString(trangThai);
    }

    // Thêm setter cho enum trực tiếp
    public void setTrangThai(TrangThaiLichHen trangThai) {
        this.trangThai = trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        // Dùng StringBuilder để xây dựng chuỗi
        StringBuilder sb = new StringBuilder();
        sb.append("Lịch hẹn [ID: ").append(idLichHen)
          .append(", Bác sĩ: ").append(hoTenBacSi)
          .append(", Bệnh nhân: ").append(hoTenBenhNhan)
          .append(", Ngày hẹn: ").append(ngayHen)
          .append(", Phòng: ").append(tenPhong)
          .append(", Giờ hẹn: ").append(gioHen)
          .append(", Trạng thái: ").append(trangThai != null ? trangThai.getValue() : "null")
          .append(", Mô tả: ").append(moTa)
          .append("]");
        return sb.toString();
    }
}