-- Xóa database nếu tồn tại
DROP DATABASE IF EXISTS phongkhamnhakhoa;

-- Tạo database
CREATE DATABASE phongkhamnhakhoa;
USE phongkhamnhakhoa;

-- Tạo các bảng
CREATE TABLE NguoiDung (
    idNguoiDung INT AUTO_INCREMENT PRIMARY KEY,
    hoTen VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    matKhau VARCHAR(255),
    soDienThoai VARCHAR(15),
    ngaySinh DATE,
    gioiTinh ENUM('Nam', 'Nữ', 'Khác'),
    vaiTro ENUM('Admin', 'Bác sĩ', 'Lễ tân', 'Kế toán', 'Quan kho')
);

CREATE TABLE OTP (
    idOTP INT AUTO_INCREMENT PRIMARY KEY,
    idNguoiDung INT,
    maOTP VARCHAR(10),
    thoiGianHetHan DATETIME,
    daSuDung BOOLEAN,
    loai ENUM('DangKy', 'QuenMatKhau', 'XacThuc'),
    FOREIGN KEY (idNguoiDung) REFERENCES NguoiDung(idNguoiDung)
);

CREATE TABLE ResetPassword (
    idReset INT AUTO_INCREMENT PRIMARY KEY,
    idNguoiDung INT,
    idOTP INT,
    token VARCHAR(255) UNIQUE NOT NULL,
    thoiGianHetHan DATETIME NOT NULL,
    daSuDung BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (idNguoiDung) REFERENCES NguoiDung(idNguoiDung),
    FOREIGN KEY (idOTP) REFERENCES OTP(idOTP)
);

CREATE TABLE BenhNhan (
    idBenhNhan INT AUTO_INCREMENT PRIMARY KEY,
    hoTen VARCHAR(100),
    ngaySinh DATE,
    gioiTinh ENUM('Nam', 'Nữ', 'Khác'),
    soDienThoai VARCHAR(15),
    cccd VARCHAR(12) UNIQUE NOT NULL,
    diaChi TEXT
);

CREATE TABLE PhongKham (
    idPhongKham INT AUTO_INCREMENT PRIMARY KEY,
    tenPhong VARCHAR(100),
    diaChi TEXT
);

CREATE TABLE BacSi (
    idBacSi INT AUTO_INCREMENT PRIMARY KEY,
    idNguoiDung INT UNIQUE,
    idPhongKham INT,
    chuyenKhoa VARCHAR(100),
    bangCap TEXT,
    kinhNghiem INT,
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
    trangThai ENUM('Chờ xác nhận', 'Đã xác nhận', 'Đã hủy') DEFAULT 'Chờ xác nhận',
    moTa TEXT,
    FOREIGN KEY (idBacSi) REFERENCES BacSi(idBacSi),
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan),
    FOREIGN KEY (idPhongKham) REFERENCES PhongKham(idPhongKham)
);

CREATE TABLE HoSoBenhAn (
    idHoSo INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    chuanDoan TEXT,
    ghiChu TEXT,
    ngayTao DATE,
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan)
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
    tenNCC VARCHAR(100),
    diaChi TEXT,
    soDienThoai VARCHAR(15)
);

CREATE TABLE KhoVatTu (
    idVatTu INT AUTO_INCREMENT PRIMARY KEY,
    tenVatTu VARCHAR(100),
    soLuong INT,
    donViTinh VARCHAR(50),
    idNCC INT,
    FOREIGN KEY (idNCC) REFERENCES NhaCungCap(idNCC)
);

CREATE TABLE DichVu (
    idDichVu INT AUTO_INCREMENT PRIMARY KEY,
    tenDichVu VARCHAR(100),
    gia DECIMAL(10,2)
);

CREATE TABLE HoaDon (
    idHoaDon INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    ngayTao DATE,
    tongTien DECIMAL(10,2) NOT NULL CHECK (tongTien >= 0),
    trangThai ENUM('ChuaThanhToan', 'DaThanhToan') NOT NULL DEFAULT 'ChuaThanhToan',
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan) ON DELETE CASCADE
);

CREATE TABLE ChiTietHoaDon (
    idChiTiet INT AUTO_INCREMENT PRIMARY KEY,
    idHoaDon INT,
    idDichVu INT,
    soLuong INT,
    donGia DECIMAL(10,2),
    FOREIGN KEY (idHoaDon) REFERENCES HoaDon(idHoaDon),
    FOREIGN KEY (idDichVu) REFERENCES DichVu(idDichVu)
);

CREATE TABLE DieuTri (
    idDieuTri INT AUTO_INCREMENT PRIMARY KEY,
    idHoSo INT,
    idBacSi INT,
    moTa TEXT,
    ngayDieuTri DATE,
    FOREIGN KEY (idHoSo) REFERENCES HoSoBenhAn(idHoSo),
    FOREIGN KEY (idBacSi) REFERENCES BacSi(idBacSi)
);

CREATE TABLE BaoHiemYTe (
    idBHYT INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    mucHoTro DECIMAL(5,2),
    ngayHetHan DATE,
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan)
);

CREATE TABLE Thuoc (
    idThuoc INT AUTO_INCREMENT PRIMARY KEY,
    tenThuoc VARCHAR(100),
    donViTinh VARCHAR(50),
    gia DECIMAL(10,2)
);

CREATE TABLE DoanhThu (
    idDoanhThu INT AUTO_INCREMENT PRIMARY KEY,
    thangNam DATE,
    tongDoanhThu DECIMAL(15,2),
    idHoaDon INT,
    FOREIGN KEY (idHoaDon) REFERENCES HoaDon(idHoaDon)
);

CREATE TABLE DonThuoc (
    idDonThuoc INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    idBacSi INT,
    ngayKeDon DATE,
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan),
    FOREIGN KEY (idBacSi) REFERENCES BacSi(idBacSi)
);
ALTER TABLE DonThuoc ADD COLUMN chiTiet TEXT;

CREATE TABLE ChiTietDonThuoc (
    idChiTietDon INT AUTO_INCREMENT PRIMARY KEY,
    idDonThuoc INT,
    idThuoc INT,
    soLuong INT,
    huongDanSuDung TEXT,
    FOREIGN KEY (idDonThuoc) REFERENCES DonThuoc(idDonThuoc),
    FOREIGN KEY (idThuoc) REFERENCES Thuoc(idThuoc)
);

CREATE TABLE YeuCauLichHen (
    idYeuCau INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    ngayYeuCau DATE,
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan)
);

CREATE TABLE ThanhToanBenhNhan (
    idThanhToan INT AUTO_INCREMENT PRIMARY KEY,
    idHoaDon INT,
    soTien DECIMAL(10,2),
    hinhThucThanhToan VARCHAR(50),
    maQR TEXT,
    FOREIGN KEY (idHoaDon) REFERENCES HoaDon(idHoaDon)
);

-- Thêm dữ liệu vào bảng NguoiDung
INSERT INTO NguoiDung (hoTen, email, matKhau, soDienThoai, ngaySinh, gioiTinh, vaiTro) 
VALUES ('Nguyen Van A', 'nguyenvana@example.com', 'password123', '0123456789', '1990-01-01', 'Nam', 'Bác sĩ');

-- Thêm dữ liệu vào bảng BenhNhan
INSERT INTO BenhNhan (hoTen, ngaySinh, gioiTinh, soDienThoai, cccd, diaChi) 
VALUES ('Tran Thi B', '1995-05-10', 'Nữ', '0987654321', '123456789012', 'Hà Nội');

-- Thêm dữ liệu vào bảng PhongKham
INSERT INTO PhongKham (tenPhong, diaChi) VALUES ('Phòng 1', 'Hà Nội');

-- Thêm dữ liệu vào bảng BacSi
INSERT INTO BacSi (idNguoiDung, idPhongKham, chuyenKhoa, bangCap, kinhNghiem) 
VALUES (1, 1, 'Nha khoa', 'Thạc sĩ', 5);

-- Thêm dữ liệu vào bảng LichHen
INSERT INTO LichHen (idBacSi, idBenhNhan, ngayHen, idPhongKham, gioHen, trangThai, moTa) 
VALUES (1, 1, '2025-03-15', 1, '10:00:00', 'Đã xác nhận', 'Khám răng');

-- Thêm dữ liệu vào bảng HoaDon
INSERT INTO HoaDon (idBenhNhan, ngayTao, tongTien, trangThai) 
VALUES (1, '2025-03-01', 5000000, 'DaThanhToan');

-- Thêm dữ liệu vào bảng DoanhThu
INSERT INTO DoanhThu (thangNam, tongDoanhThu, idHoaDon)
VALUES ('2025-03-01', 5000000, 1);

-- Thêm dữ liệu vào bảng LuongNhanVien
INSERT INTO LuongNhanVien (idNguoiDung, thangNam, luongCoBan, thuong, khauTru, tongLuong) 
VALUES (1, '2025-03-01', 8000000, 2000000, 500000, 9500000);

-- Thêm dữ liệu vào bảng DonThuoc
INSERT INTO DonThuoc (idBenhNhan, idBacSi, ngayKeDon) 
VALUES (1, 1, '2025-03-15');

-- Thêm dữ liệu mẫu vào bảng ThanhToanBenhNhan
INSERT INTO ThanhToanBenhNhan (idHoaDon, soTien, hinhThucThanhToan, maQR)
VALUES (1, 5000000.00, 'ChuyenKhoan', 'QR12345');