DROP DATABASE QuanLyPhongKham;
CREATE DATABASE QuanLyPhongKham;
USE QuanLyPhongKham;

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
INSERT INTO NguoiDung (hoTen, email, matKhau, soDienThoai, ngaySinh, gioiTinh, vaiTro) VALUES
('Nguyễn Văn A', 'admin@gmail.com', 'admin123', '0987654321', '1990-05-20', 'Nam', 'Admin'),
('Trần Thị B', 'bacsi@gmail.com', 'bacsi123', '0912345678', '1985-09-15', 'Nữ', 'Bác sĩ'),
('Lê Văn C', 'letan@gmail.com', 'letan123', '0905123456', '1992-07-10', 'Nam', 'Lễ tân'),
('Hoàng Thị D', 'ketoan@gmail.com', 'ketoan123', '0934567890', '1988-11-30', 'Nữ', 'Kế toán'),
('Phạm Văn E', 'quankho@gmail.com', 'quankho123', '0976543210', '1995-03-25', 'Nam', 'Quan kho');
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
INSERT INTO BenhNhan (hoTen, ngaySinh, gioiTinh, soDienThoai, cccd, diaChi) VALUES
('Trần Minh Hoàng', '1990-07-15', 'Nam', '0978123456', '234567890123', 'Đà Nẵng'),
('Phạm Thị Lan', '1985-12-30', 'Nữ', '0967543210', '345678901234', 'Hải Phòng'),
('Đỗ Văn Tuấn', '2000-04-18', 'Nam', '0987654321', '456789012345', 'Cần Thơ'),
('Vũ Ngọc Mai', '1998-11-25', 'Nữ', '0934567890', '567890123456', 'Bình Dương'),
('Ngô Đức Tài', '1993-06-10', 'Nam', '0923456789', '678901234567', 'Huế'),
('Ngô Đức Anh', '1999-06-10', 'Nam', '0923451289', '678943234567', 'Huế'),
('Lương Thị Hạnh', '1987-09-05', 'Nữ', '0912123456', '789012345678', 'Nghệ An');


CREATE TABLE PhongKham (
    idPhongKham INT AUTO_INCREMENT PRIMARY KEY,
    tenPhong VARCHAR(100),
    diaChi TEXT
);

CREATE TABLE BacSi (
    idBacSi INT AUTO_INCREMENT PRIMARY KEY,
    hoTenBacSi VARCHAR(100),
    idNguoiDung INT UNIQUE,
    idPhongKham INT,
    chuyenKhoa VARCHAR(100),
    bangCap TEXT,
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
    trangThai ENUM('Chờ xác nhận', 'Đã xác nhận', 'Đã hủy') DEFAULT 'Chờ xác nhận',
	moTa TEXT,
    FOREIGN KEY (idBacSi) REFERENCES BacSi(idBacSi),
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan),
    FOREIGN KEY (idPhongKham) REFERENCES PhongKham(idPhongKham)
);

CREATE TABLE HoSoBenhAn (
    idHoSo INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT NOT NULL,
    chuanDoan VARCHAR(500) NOT NULL,
    ghiChu TEXT,
    ngayTao DATE,
    trangThai ENUM('Mới', 'Đang điều trị', 'Hoàn tất') DEFAULT 'Mới', -- Theo dõi trạng thái
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
    idHoaDon INT NOT NULL,
    idDichVu INT NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    donGia DECIMAL(10,2) NOT NULL CHECK (donGia >= 0),
    thanhTien DECIMAL(10,2) GENERATED ALWAYS AS (soLuong * donGia) STORED,
    FOREIGN KEY (idHoaDon) REFERENCES HoaDon(idHoaDon) ON DELETE CASCADE,
    FOREIGN KEY (idDichVu) REFERENCES DichVu(idDichVu) ON DELETE CASCADE
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
INSERT INTO HoSoBenhAn (idBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai) VALUES
(1, 'Viêm họng cấp tính', 'Bệnh nhân có triệu chứng đau họng và sốt nhẹ.', '2024-10-26', 'Mới'),
(2, 'Sâu răng', 'Cần trám răng số 21.', '2024-10-26', 'Đang điều trị'),
(3, 'Gãy xương cẳng tay', 'Bệnh nhân bị ngã xe.', '2024-10-27', 'Hoàn tất'),
(4, 'Viêm da cơ địa', 'Da bị khô và ngứa.', '2024-10-27', 'Đang điều trị'),
(5, 'Viêm xoang', 'Bệnh nhân có triệu chứng nghẹt mũi và đau đầu.', '2024-10-28', 'Mới'),
(6, 'Răng khôn mọc lệch', 'Cần nhổ răng khôn.', '2024-10-28', 'Đang điều trị'),
(7, 'Viêm loét dạ dày', 'Bệnh nhân có triệu chứng đau bụng và ợ chua.', '2024-10-29', 'Hoàn tất'),
(1, 'Viêm phế quản', 'Bệnh nhân có triệu chứng ho và khó thở.', '2024-10-29', 'Đang điều trị'),
(2, 'Nhiễm trùng nướu', 'Nướu bị sưng và chảy máu.', '2024-10-30', 'Mới'),
(3, 'Bong gân cổ chân', 'Bệnh nhân bị trẹo chân khi chơi thể thao.', '2024-10-30', 'Đang điều trị');


CREATE TABLE BaoHiemYTe (
    idBHYT INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    mucHoTro DECIMAL(5,2),  -- Mức hỗ trợ (ví dụ: 80.00 nghĩa là hỗ trợ 80%)
    ngayHetHan DATE,  -- Ngày hết hạn bảo hiểm
    loaiBHYT ENUM('ToanPhan', 'MotPhan', 'KhongHoTro') DEFAULT 'MotPhan',
    trangThai ENUM('ConHieuLuc', 'HetHieuLuc') DEFAULT 'ConHieuLuc',
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan)
);
CREATE TABLE Thuoc (
    idThuoc INT AUTO_INCREMENT PRIMARY KEY,
    tenThuoc VARCHAR(100),
    donViTinh VARCHAR(50),
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
    huongDanSuDung TEXT,
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

CREATE TABLE YeuCauLichHen (
    idYeuCau INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    ngayYeuCau DATE,
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan)
);
CREATE TABLE ThanhToanBenhNhan (
    idThanhToan INT AUTO_INCREMENT PRIMARY KEY,
    idHoaDon INT NOT NULL,
    soTien DECIMAL(10,2) NOT NULL CHECK (soTien > 0),
    hinhThucThanhToan ENUM('TienMat', 'ChuyenKhoan', 'QR', 'TheTinDung') NOT NULL,
    maQR TEXT DEFAULT NULL,
    trangThai ENUM('DangXuLy', 'ThanhCong', 'ThatBai') NOT NULL DEFAULT 'DangXuLy',
    FOREIGN KEY (idHoaDon) REFERENCES HoaDon(idHoaDon) ON DELETE CASCADE
);
INSERT INTO PhongKham (tenPhong, diaChi) VALUES
('Phòng khám Hà Nội', '123 Nguyễn Trãi, Hà Nội'),
('Phòng khám TP.HCM', '456 Lê Lợi, Quận 1, TP.HCM'),
('Phòng khám Đà Nẵng', '789 Trần Phú, Đà Nẵng'),
('Phòng khám Hải Phòng', '101 Lạch Tray, Hải Phòng'),
('Phòng khám Cần Thơ', '202 Nguyễn Văn Cừ, Cần Thơ');

INSERT INTO BacSi (hoTenBacSi, idNguoiDung, idPhongKham, chuyenKhoa, bangCap, kinhNghiem) VALUES
('Nguyễn Văn A', 1, 1, 'Răng Hàm Mặt', 'Bằng Tiến sĩ Y khoa - Đại học Y Hà Nội', 15),
('Trần Thị B', 2, 2, 'Nha khoa tổng quát', 'Bằng Bác sĩ - Đại học Y Dược TP.HCM', 10),
('Lê Văn C', 3, 1, 'Chỉnh nha', 'Bằng Thạc sĩ - Đại học Y Hà Nội', 8),
('Phạm Thị D', 4, 3, 'Phẫu thuật nha khoa', 'Bằng Bác sĩ - Đại học Y Huế', 12),
('Hoàng Minh E', 5, 2, 'Nha chu', 'Bằng Tiến sĩ - Đại học Y Dược TP.HCM', 20);

INSERT INTO LichHen (idBacSi, idBenhNhan, ngayHen, idPhongKham, gioHen, trangThai, moTa) VALUES
(1, 1, '2025-03-28', 1, '09:00:00', 'Chờ xác nhận', 'Khám tổng quát'),
(2, 2, '2025-03-28', 2, '10:00:00', 'Đã xác nhận', 'Tái khám răng sâu'),
(3, 3, '2025-03-29', 1, '14:00:00', 'Đã xác nhận', 'Nhổ răng khôn'),
(1, 4, '2025-03-30', 3, '08:30:00', 'Đã xác nhận', 'Làm răng sứ'),
(2, 5, '2025-03-30', 2, '15:00:00', 'Đã hủy', 'Cạo vôi răng'),
(3, 6, '2025-03-31', 1, '10:30:00', 'Chờ xác nhận', 'Chỉnh nha'),
(1, 2, '2025-04-01', 3, '11:15:00', 'Chờ xác nhận', 'Tư vấn niềng răng'),
(2, 3, '2025-04-02', 1, '13:45:00', 'Đã xác nhận', 'Nhổ răng sâu'),
(3, 4, '2025-04-03', 2, '09:30:00', 'Chờ xác nhận', 'Kiểm tra tổng quát'),
(1, 5, '2025-04-04', 3, '16:00:00', 'Đã hủy', 'Bọc răng sứ');
INSERT INTO DieuTri (idHoSo, idBacSi, moTa, ngayDieuTri) VALUES
(1, 1, 'Kê đơn thuốc kháng sinh và hướng dẫn bệnh nhân nghỉ ngơi.', '2024-03-01'),
(2, 2, 'Tiến hành trám răng số 21.', '2024-03-02'),
(3, 3, 'Đặt nẹp và theo dõi tình trạng hồi phục.', '2024-03-03'),
(4, 4, 'Kê thuốc bôi ngoài da và tư vấn cách chăm sóc da.', '2024-03-04'),
(5, 5, 'Đề xuất nội soi và kê đơn thuốc kháng viêm.', '2024-03-05'),
(6, 2, 'Thực hiện nhổ răng khôn, hướng dẫn bệnh nhân chăm sóc sau khi nhổ.', '2024-03-06'),
(7, 3, 'Kê đơn thuốc chống loét dạ dày và hướng dẫn chế độ ăn uống.', '2024-03-07'),
(8, 1, 'Hỗ trợ thở oxy và kê thuốc giãn phế quản.', '2024-03-08'),
(9, 2, 'Điều trị nướu bị sưng và vệ sinh răng miệng.', '2024-03-09'),
(10, 3, 'Băng bó và hướng dẫn bệnh nhân tập vật lý trị liệu.', '2024-03-10');