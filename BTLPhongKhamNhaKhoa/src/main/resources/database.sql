DROP DATABASE QuanLyPhongKham;
CREATE DATABASE QuanLyPhongKham;
USE QuanLyPhongKham;

CREATE TABLE NguoiDung (
    idNguoiDung INT AUTO_INCREMENT PRIMARY KEY,
    hoTen NVARCHAR(100),
    email NVARCHAR(100) UNIQUE,
    matKhau NVARCHAR(255),
    soDienThoai NVARCHAR(15),
    ngaySinh DATE,
    gioiTinh NVARCHAR(10), -- Sử dụng NVARCHAR cho ENUM để hỗ trợ Unicode
    vaiTro NVARCHAR(20)    -- Sử dụng NVARCHAR cho ENUM để hỗ trợ Unicode
); 
CREATE TABLE OTP (
    idOTP INT AUTO_INCREMENT PRIMARY KEY,
    idNguoiDung INT,
    maOTP NVARCHAR(10),
    thoiGianHetHan DATETIME,
    daSuDung BOOLEAN,
    loai NVARCHAR(20), -- Sử dụng NVARCHAR cho ENUM để hỗ trợ Unicode
    FOREIGN KEY (idNguoiDung) REFERENCES NguoiDung(idNguoiDung)
);
CREATE TABLE BenhNhan (
    idBenhNhan INT AUTO_INCREMENT PRIMARY KEY,
    hoTen NVARCHAR(100),
    ngaySinh DATE,
    gioiTinh NVARCHAR(10), -- Sử dụng NVARCHAR cho ENUM để hỗ trợ Unicode
    soDienThoai NVARCHAR(15),
    cccd NVARCHAR(12) UNIQUE NOT NULL,
    diaChi NVARCHAR(255)
);
CREATE TABLE PhongKham (
    idPhongKham INT AUTO_INCREMENT PRIMARY KEY,
    tenPhong NVARCHAR(100),
    diaChi NVARCHAR(255)
);
CREATE TABLE BacSi (
    idBacSi INT AUTO_INCREMENT PRIMARY KEY,
    hoTenBacSi NVARCHAR(100),
    idNguoiDung INT UNIQUE,
    idPhongKham INT,
    chuyenKhoa NVARCHAR(100),
    bangCap NVARCHAR(255),
    kinhNghiem INT CHECK (kinhNghiem >= 0),
    FOREIGN KEY (idNguoiDung) REFERENCES NguoiDung(idNguoiDung),
    FOREIGN KEY (idPhongKham) REFERENCES PhongKham(idPhongKham)
);
CREATE TABLE LichHen (
    idLichHen INT AUTO_INCREMENT PRIMARY KEY,
    idBacSi INT,
    idBenhNhan INT,
    ngayHen DATE,
    idPhongKham INT,
    gioHen TIME,
    trangThai NVARCHAR(20) DEFAULT N'Chờ xác nhận', -- Sử dụng NVARCHAR cho ENUM để hỗ trợ Unicode
    moTa NVARCHAR(255),
    FOREIGN KEY (idBacSi) REFERENCES BacSi(idBacSi),
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan),
    FOREIGN KEY (idPhongKham) REFERENCES PhongKham(idPhongKham)
);
CREATE TABLE HoSoBenhAn (
    idHoSo INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT NOT NULL,
    chuanDoan NVARCHAR(500) NOT NULL,
    ghiChu NVARCHAR(255),
    ngayTao DATE,
    trangThai NVARCHAR(20) DEFAULT N'Mới', -- Sử dụng NVARCHAR cho ENUM để hỗ trợ Unicode
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan) ON DELETE CASCADE
);
CREATE TABLE LuongNhanVien (
    idLuong INT AUTO_INCREMENT PRIMARY KEY,
    idNguoiDung INT,
    thangNam DATE,
    luongCoBan DECIMAL(10,2),
    thuong DECIMAL(10,2),
    khauTru DECIMAL(10,2),
    tongLuong DECIMAL(10,2),
    FOREIGN KEY (idNguoiDung) REFERENCES NguoiDung(idNguoiDung)
);
CREATE TABLE NhaCungCap (
    idNCC INT AUTO_INCREMENT PRIMARY KEY,
    tenNCC NVARCHAR(100),
    diaChi NVARCHAR(255),
    soDienThoai NVARCHAR(15),
    MaSoThue NVARCHAR(20), -- Cột mới: Mã số thuế của nhà cung cấp
    NgayDangKy DATE          -- Cột mới: Ngày đăng ký của nhà cung cấp
);
CREATE TABLE DichVu (
    idDichVu INT AUTO_INCREMENT PRIMARY KEY,
    tenDichVu NVARCHAR(100),
    gia DECIMAL(10,2)
);
CREATE TABLE KhoVatTu (
    idVatTu INT AUTO_INCREMENT PRIMARY KEY,
    tenVatTu NVARCHAR(100),
    soLuong INT,
    donViTinh NVARCHAR(50),
    idNCC INT,
    phanLoai NVARCHAR(50),
    FOREIGN KEY (idNCC) REFERENCES NhaCungCap(idNCC)
);
CREATE TABLE HoaDon (
    idHoaDon INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    ngayTao DATE,
    tongTien DECIMAL(10,2) NOT NULL CHECK (tongTien >= 0),
    trangThai NVARCHAR(20) NOT NULL DEFAULT N'ChuaThanhToan', -- Sử dụng NVARCHAR cho ENUM để hỗ trợ Unicode
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan) ON DELETE CASCADE
);
-- note
CREATE TABLE ChiTietHoaDon (
    idChiTiet INT AUTO_INCREMENT PRIMARY KEY,
    idHoaDon INT NOT NULL,
    idDichVu INT NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    donGia DECIMAL(10,2) NOT NULL CHECK (donGia >= 0),
    thanhTien DECIMAL(10,2) GENERATED ALWAYS AS (soLuong * donGia) STORED,
    FOREIGN KEY (idHoaDon) REFERENCES HoaDon(idHoaDon) ON DELETE CASCADE,
    FOREIGN KEY (idDichVu) REFERENCES DichVu(idDichVu) ON DELETE CASCADE
);
CREATE TABLE ThanhToanBenhNhan (
    idThanhToan INT AUTO_INCREMENT PRIMARY KEY,
    idHoaDon INT NOT NULL,
    soTien DECIMAL(10,2) NOT NULL CHECK (soTien > 0),
    hinhThucThanhToan NVARCHAR(20) NOT NULL,
    maQR NVARCHAR(255) DEFAULT NULL,
    trangThai NVARCHAR(20) NOT NULL DEFAULT N'DangXuLy',
    FOREIGN KEY (idHoaDon) REFERENCES HoaDon(idHoaDon) ON DELETE CASCADE
);
CREATE TABLE DieuTri (
    idDieuTri INT AUTO_INCREMENT PRIMARY KEY,
    idHoSo INT,
    idBacSi INT,
    moTa NVARCHAR(255),
    ngayDieuTri DATE,
    FOREIGN KEY (idHoSo) REFERENCES HoSoBenhAn(idHoSo),
    FOREIGN KEY (idBacSi) REFERENCES BacSi(idBacSi)
);

CREATE TABLE Thuoc (
    idThuoc INT AUTO_INCREMENT PRIMARY KEY,
    tenThuoc NVARCHAR(100),
    donViTinh NVARCHAR(50),
    gia DECIMAL(10,2)
);

CREATE TABLE DonThuoc (
    idDonThuoc INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    idBacSi INT,
    ngayKeDon DATE,
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan),
    FOREIGN KEY (idBacSi) REFERENCES BacSi(idBacSi)
);

CREATE TABLE ChiTietDonThuoc (
    idChiTietDon INT AUTO_INCREMENT PRIMARY KEY,
    idDonThuoc INT,
    idThuoc INT,
    soLuong INT,
    huongDanSuDung NVARCHAR(255),
    FOREIGN KEY (idDonThuoc) REFERENCES DonThuoc(idDonThuoc),
    FOREIGN KEY (idThuoc) REFERENCES Thuoc(idThuoc)
);

CREATE TABLE DoanhThu (
    idDoanhThu INT AUTO_INCREMENT PRIMARY KEY,
    thangNam DATE,
    tongDoanhThu DECIMAL(15,2),
    idHoaDon INT,
    FOREIGN KEY (idHoaDon) REFERENCES HoaDon(idHoaDon)
);