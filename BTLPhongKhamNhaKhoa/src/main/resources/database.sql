DROP DATABASE QuanLyPhongKham;
CREATE DATABASE QuanLyPhongKham;
USE QuanLyPhongKham;
-- ALTER DATABASE QuanLyPhongKham CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
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
CREATE TABLE ResetPassword (
    idReset INT AUTO_INCREMENT PRIMARY KEY,
    idNguoiDung INT,
    idOTP INT,
    token NVARCHAR(255) UNIQUE NOT NULL,
    thoiGianHetHan DATETIME NOT NULL,
    daSuDung BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (idNguoiDung) REFERENCES NguoiDung(idNguoiDung),
    FOREIGN KEY (idOTP) REFERENCES OTP(idOTP)
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
-- ALTER TABLE LichHen MODIFY COLUMN trangThai VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE HoSoBenhAn (
    idHoSo INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT NOT NULL,
    chuanDoan NVARCHAR(500) NOT NULL,
    ghiChu NVARCHAR(255),
    ngayTao DATE,
    trangThai NVARCHAR(20) DEFAULT N'Mới', -- Sử dụng NVARCHAR cho ENUM để hỗ trợ Unicode
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan) ON DELETE CASCADE
);
-- ALTER TABLE HoSoBenhAn MODIFY COLUMN trangThai VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
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
    soDienThoai NVARCHAR(15)
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

CREATE TABLE YeuCauLichHen (
    idYeuCau INT AUTO_INCREMENT PRIMARY KEY,
    idBenhNhan INT,
    ngayYeuCau DATE,
    FOREIGN KEY (idBenhNhan) REFERENCES BenhNhan(idBenhNhan)
);
-- Thêm dữ liệu mẫu vào bảng NguoiDung
INSERT INTO NguoiDung (hoTen, email, matKhau, soDienThoai, ngaySinh, gioiTinh, vaiTro) VALUES
(N'Nguyễn Văn A', N'admin@gmail.com', N'admin123', N'0987654321', '1990-05-20', N'Nam', N'Admin'),
(N'Trần Thị B', N'bacsi@gmail.com', N'bacsi123', N'0912345678', '1985-09-15', N'Nữ', N'Bác sĩ'),
(N'Tuấn Đạt', N'tuandat@gmail.com', N'bacsi123', N'0907001320', '2005-08-06', N'Nam', N'Bác sĩ'),
(N'Lê Văn C', N'letan@gmail.com', N'letan123', N'0905123456', '1992-07-10', N'Nam', N'Lễ tân'),
(N'Hoàng Thị D', N'ketoan@gmail.com', N'ketoan123', N'0934567890', '1988-11-30', N'Nữ', N'Kế toán'),
(N'Phạm Văn E', N'quankho@gmail.com', N'quankho123', N'0976543210', '1995-03-25', N'Nam', N'Quản kho');
INSERT INTO NguoiDung (hoTen, email, matKhau, soDienThoai, ngaySinh, gioiTinh, vaiTro) VALUES
(N'Nguyễn Thị F', N'nguoidung1@gmail.com', N'user123', N'0963333333', '2000-01-15', N'Nữ', N'Người dùng'),
(N'Đinh Công G', N'nguoidung2@gmail.com', N'password456', N'0911222222', '1998-06-22', N'Nam', N'Người dùng'),
(N'Vũ Thu H', N'vuthuh@example.com', N'secure789', N'0909888888', '2003-12-01', N'Nữ', N'Người dùng');
-- Thêm dữ liệu mẫu vào bảng BenhNhan
INSERT INTO BenhNhan (hoTen, ngaySinh, gioiTinh, soDienThoai, cccd, diaChi) VALUES
(N'Trần Minh Hoàng', '1990-07-15', N'Nam', N'0978123456', N'234567890123', N'Đà Nẵng'),
(N'Phạm Thị Lan', '1985-12-30', N'Nữ', N'0967543210', N'345678901234', N'Hải Phòng'),
(N'Đỗ Văn Tuấn', '2000-04-18', N'Nam', N'0987654321', N'456789012345', N'Cần Thơ'),
(N'Vũ Ngọc Mai', '1998-11-25', N'Nữ', N'0934567890', N'567890123456', N'Bình Dương'),
(N'Ngô Đức Tài', '1993-06-10', N'Nam', N'0923456789', N'678901234567', N'Huế'),
(N'Ngô Đức Anh', '1999-06-10', N'Nam', N'0923451289', N'678943234567', N'Huế'),
(N'Lương Thị Hạnh', '1987-09-05', N'Nữ', N'0912123456', N'789012345678', N'Nghệ An');

-- Thêm dữ liệu mẫu vào bảng PhongKham
INSERT INTO PhongKham (tenPhong, diaChi) VALUES
(N'Phòng khám Hà Nội', N'123 Nguyễn Trãi, Hà Nội'),
(N'Phòng khám TP.HCM', N'456 Lê Lợi, Quận 1, TP.HCM'),
(N'Phòng khám Đà Nẵng', N'789 Trần Phú, Đà Nẵng'),
(N'Phòng khám Hải Phòng', N'101 Lạch Tray, Hải Phòng'),
(N'Phòng khám Cần Thơ', N'202 Nguyễn Văn Cừ, Cần Thơ');

-- Thêm dữ liệu mẫu vào bảng BacSi
INSERT INTO BacSi (hoTenBacSi, idNguoiDung, idPhongKham, chuyenKhoa, bangCap, kinhNghiem) VALUES
(N'Nguyễn Văn A', 1, 1, N'Răng Hàm Mặt', N'Bằng Tiến sĩ Y khoa - Đại học Y Hà Nội', 15),
(N'Trần Thị B', 2, 2, N'Răng Hàm Mặt', N'Bằng Bác sĩ - Đại học Y Dược TP.HCM', 10),
(N'Lê Văn C', 3, 1, N'Chỉnh nha', N'Bằng Thạc sĩ - Đại học Y Hà Nội', 8),
(N'Phạm Thị D', 4, 3, N'Phẫu thuật nha khoa', N'Bằng Bác sĩ - Đại học Y Huế', 12),
(N'Hoàng Minh E', 5, 2, N'Nha chu', N'Bằng Tiến sĩ - Đại học Y Dược TP.HCM', 20);

-- Thêm dữ liệu mẫu vào bảng NhaCungCap
INSERT INTO NhaCungCap (tenNCC, diaChi, soDienThoai) VALUES
(N'Công ty Dược phẩm ABC', N'10 Nguyễn Huệ, Quận 1, TP.HCM', N'02812345678'),
(N'Thiết bị Y tế XYZ', N'25 Trần Hưng Đạo, Hà Nội', N'02498765432'),
(N'Vật tư Tiêu hao Minh Anh', N'5 Lê Duẩn, Đà Nẵng', N'02365432109'),
(N'Dụng cụ Y khoa Bình An', N'30 Hoàng Diệu, Hải Phòng', N'02251122334'),
(N'Hóa chất Xét nghiệm Sài Gòn', N'78 Võ Văn Tần, Quận 3, TP.HCM', N'02855667788'),
(N'Công ty TNHH Trang Thiết Bị Y Tế Đức Minh', N'Khu Công Nghiệp A, Biên Hòa, Đồng Nai', N'02519988776'),
(N'Nhà phân phối Dược phẩm Toàn Cầu', N'Lô B Khu Đô Thị Mới, Hà Đông, Hà Nội', N'02433445566'),
(N'Cửa hàng Vật tư Y tế Thanh Xuân', N'15 Nguyễn Trãi, Thanh Xuân, Hà Nội', N'0909112233'),
(N'Đại lý Thiết bị Nha Khoa Phương Nam', N'42 Đường 3 Tháng 2, Quận 10, TP.HCM', N'0933445566'),
(N'Xưởng sản xuất Bông Băng Việt', N'Khu Công Nghiệp Sóng Thần, Dĩ An, Bình Dương', N'02748899001'),
(N'Dược phẩm Đông Á', N'12 Phan Chu Trinh, Hà Nội', N'02477788990'),
(N'Vật tư Y tế Hồng Hà', N'68 Nguyễn Chí Thanh, Đà Nẵng', N'02369990001'),
(N'Thiết bị Y tế Phương Đông', N'99 Lê Thánh Tôn, Quận 1, TP.HCM', N'02866677788'),
(N'Dược phẩm An Khang', N'20 Trần Phú, Nha Trang', N'02581234567'),
(N'Vật tư Nha Khoa Sài Gòn', N'35 Nguyễn Văn Linh, Quận 7, TP.HCM', N'02899988877');

-- Thêm dữ liệu mẫu vào bảng KhoVatTu
INSERT INTO KhoVatTu (tenVatTu, soLuong, donViTinh, idNCC, phanLoai) VALUES
(N'Bơm tiêm 5ml', 1000, N'cái', 1, N'Vật tư tiêu hao'),
(N'Gạc vô trùng', 5000, N'gói', 3, N'Vật tư tiêu hao'),
(N'Nước muối sinh lý', 2000, N'chai', 1, N'Dung dịch'),
(N'Khẩu trang y tế', 10000, N'chiếc', 3, N'Vật tư tiêu hao'),
(N'Kim tiêm', 8000, N'cái', 2, N'Vật tư tiêu hao'),
(N'Băng dính y tế', 3000, N'cuộn', 3, N'Vật tư tiêu hao'),
(N'Cồn 70 độ', 1000, N'lít', 5, N'Dung dịch'),
(N'Máy đo huyết áp điện tử', 200, N'cái', 2, N'Thiết bị'),
(N'Nhiệt kế điện tử', 300, N'cái', 2, N'Thiết bị'),
(N'Oxy già', 500, N'chai', 5, N'Dung dịch'),
(N'Kéo phẫu thuật', 150, N'cái', 4, N'Dụng cụ y tế'),
(N'Panh y tế', 180, N'cái', 4, N'Dụng cụ y tế'),
(N'Chỉ khâu phẫu thuật', 800, N'gói', 4, N'Vật tư tiêu hao'),
(N'Máy xét nghiệm đường huyết', 100, N'cái', 6, N'Thiết bị xét nghiệm'),
(N'Que thử đường huyết', 500, N'hộp', 6, N'Vật tư xét nghiệm'),
(N'Máy hút dịch', 50, N'cái', 6, N'Thiết bị'),
(N'Bông y tế', 7000, N'gói', 3, N'Vật tư tiêu hao'),
(N'Máy thở', 20, N'cái', 2, N'Thiết bị hỗ trợ'),
(N'Monitor theo dõi bệnh nhân', 30, N'cái', 2, N'Thiết bị theo dõi'),
(N'Ghế nha khoa', 15, N'cái', 9, N'Thiết bị nha khoa'),
(N'Tay khoan nha khoa', 50, N'cái', 9, N'Thiết bị nha khoa'),
(N'Vật liệu trám răng', 300, N'tuýp', 9, N'Vật liệu nha khoa'),
(N'Bột bó xương', 400, N'kg', 7, N'Vật tư tiêu hao'),
(N'Nẹp cố định xương', 250, N'cái', 7, N'Dụng cụ y tế'),
(N'Xe lăn', 60, N'cái', 7, N'Thiết bị hỗ trợ'),
(N'Băng thun', 1800, N'cuộn', 3, N'Vật tư tiêu hao'),
(N'Thuốc sát trùng Povidine', 600, N'chai', 5, N'Dung dịch sát trùng'),
(N'Găng tay y tế (có bột)', 12000, N'đôi', 3, N'Vật tư tiêu hao'),
(N'Găng tay y tế (không bột)', 9000, N'đôi', 3, N'Vật tư tiêu hao'),
(N'Áo choàng phẫu thuật', 400, N'chiếc', 4, N'Vật tư tiêu hao');

-- Thêm dữ liệu mẫu vào bảng Thuoc
INSERT INTO Thuoc (tenThuoc, donViTinh, gia) VALUES
(N'Paracetamol', N'Viên', 5000),
(N'Amoxicillin', N'Viên', 12000),
(N'Ibuprofen', N'Viên', 8000),
(N'Salbutamol', N'Ống hít', 150000),
(N'Omeprazole', N'Viên nang', 10000),
(N'Cetirizine', N'Viên', 7000),
(N'Vitamin C', N'Viên', 3000),
(N'Prednisolone', N'Viên', 15000),
(N'Aspirin', N'Viên', 4000),
(N'Diazepam', N'Viên', 9000),
(N'Furosemide', N'Viên', 11000),
(N'Metformin', N'Viên', 6000),
(N'Thuốc nhỏ mắt', N'Lọ', 25000),
(N'Thuốc ho', N'Chai', 30000),
(N'Thuốc nhỏ mũi trẻ em', N'Lọ', 20000);

-- Thêm dữ liệu mẫu vào bảng DichVu
INSERT INTO DichVu (tenDichVu, gia) VALUES
(N'Khám tổng quát', 200000),
(N'Điều trị răng sâu', 500000),
(N'Nhổ răng', 300000),
(N'Trám răng', 400000),
(N'Tẩy trắng răng', 1500000),
(N'Cạo vôi răng', 250000),
(N'Niềng răng', 15000000),
(N'Bọc răng sứ', 5000000),
(N'Chụp X-quang', 150000),
(N'Cấy ghép Implant', 20000000);

-- Thêm dữ liệu mẫu vào bảng HoSoBenhAn
INSERT INTO HoSoBenhAn (idBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai) VALUES
(1, N'Viêm họng cấp tính', N'Bệnh nhân có triệu chứng đau họng và sốt nhẹ.', '2024-10-26', N'Mới'),
(2, N'Sâu răng', N'Cần trám răng số 21.', '2024-10-26', N'Đang điều trị'),
(3, N'Gãy xương cẳng tay', N'Bệnh nhân bị ngã xe.', '2024-10-27', N'Hoàn tất'),
(4, N'Viêm da cơ địa', N'Da bị khô và ngứa.', '2024-10-27', N'Đang điều trị'),
(5, N'Viêm xoang', N'Bệnh nhân có triệu chứng nghẹt mũi và đau đầu.', '2024-10-28', N'Mới'),
(6, N'Răng khôn mọc lệch', N'Cần nhổ răng khôn.', '2024-10-28', N'Đang điều trị'),
(7, N'Viêm loét dạ dày', N'Bệnh nhân có triệu chứng đau bụng và ợ chua.', '2024-10-29', N'Hoàn tất');

-- Thêm dữ liệu mẫu vào bảng LichHen
INSERT INTO LichHen (idBacSi, idBenhNhan, ngayHen, idPhongKham, gioHen, trangThai, moTa) VALUES
(1, 1, '2025-04-28', 1, '09:00:00', N'Chờ xác nhận', N'Khám tổng quát'),
(1, 1, '2025-04-28', 2, '11:00:00', N'Chờ xác nhận', N'Tái khám răng sâu'),
(1, 1, '2025-04-28', 1, '14:00:00', N'Đã xác nhận', N'Khám lại viêm lợi'),
(2, 2, '2025-04-28', 2, '10:00:00', N'Đã xác nhận', N'Tái khám răng sâu'),
(3, 3, '2025-04-29', 1, '14:00:00', N'Đã xác nhận', N'Nhổ răng khôn'),
(1, 4, '2025-04-30', 3, '08:30:00', N'Đã xác nhận', N'Làm răng sứ'),
(2, 5, '2025-04-30', 2, '15:00:00', N'Đã hủy', N'Cạo vôi răng');

-- Thêm dữ liệu mẫu vào bảng DieuTri
INSERT INTO DieuTri (idHoSo, idBacSi, moTa, ngayDieuTri) VALUES
(1, 1, N'Kê đơn thuốc kháng sinh và hướng dẫn bệnh nhân nghỉ ngơi.', '2024-03-01'),
(2, 2, N'Tiến hành trám răng số 21.', '2024-03-02'),
(3, 3, N'Đặt nẹp và theo dõi tình trạng hồi phục.', '2024-03-03'),
(4, 4, N'Kê thuốc bôi ngoài da và tư vấn cách chăm sóc da.', '2024-03-04'),
(5, 5, N'Đề xuất nội soi và kê đơn thuốc kháng viêm.', '2024-03-05');

-- Thêm dữ liệu mẫu vào bảng HoaDon
INSERT INTO HoaDon (idBenhNhan, ngayTao, tongTien, trangThai) VALUES
(1, '2025-03-01', 1500000.00, N'DaThanhToan'),
(2, '2025-03-05', 2200000.00, N'ChuaThanhToan'),
(3, '2025-03-10', 1800000.00, N'DaThanhToan'),
(4, '2025-03-15', 900000.00, N'DaThanhToan'),
(5, '2025-03-20', 3000000.00, N'DaThanhToan');
-- Thêm dữ liệu mẫu vào bảng DoanhThu
INSERT INTO DoanhThu (thangNam, tongDoanhThu, idHoaDon) VALUES
('2025-01-01', 12500000.00, 1),
('2025-02-01', 15700000.00, 2),
('2025-03-01', 18300000.00, 3),
('2025-04-01', 14800000.00, 4),
('2025-03-15', 9500000.00, 5),
('2025-01-15', 7800000.00, 1),
('2025-02-15', 11200000.00, 2),
('2025-03-20', 13400000.00, 3),
('2025-04-10', 8900000.00, 4),
('2025-01-25', 10600000.00, 5),
('2025-02-25', 16500000.00, 1),
('2025-03-25', 21000000.00, 2);
-- Thêm dữ liệu mẫu vào bảng LuongNhanVien
INSERT INTO LuongNhanVien (idNguoiDung, thangNam, luongCoBan, thuong, khauTru, tongLuong) VALUES
(1, '2025-03-01', 15000000.00, 2000000.00, 500000.00, 16500000.00), -- Admin
(2, '2025-03-01', 12000000.00, 1500000.00, 300000.00, 13200000.00), -- Bác sĩ 1
(3, '2025-03-01', 11000000.00, 1200000.00, 250000.00, 11950000.00), -- Bác sĩ 2
(4, '2025-03-01', 7000000.00, 500000.00, 100000.00, 7400000.00),  -- Lễ tân
(5, '2025-03-01', 8000000.00, 800000.00, 150000.00, 8650000.00),  -- Kế toán
(6, '2025-03-01', 6500000.00, 300000.00, 50000.00, 6750000.00);  -- Quản kho

INSERT INTO LuongNhanVien (idNguoiDung, thangNam, luongCoBan, thuong, khauTru, tongLuong) VALUES
(1, '2025-04-01', 15000000.00, 2200000.00, 550000.00, 16650000.00), -- Admin
(2, '2025-04-01', 12000000.00, 1600000.00, 320000.00, 13280000.00), -- Bác sĩ 1
(3, '2025-04-01', 11000000.00, 1300000.00, 270000.00, 12030000.00), -- Bác sĩ 2
(4, '2025-04-01', 7000000.00, 600000.00, 110000.00, 7490000.00),  -- Lễ tân
(5, '2025-04-01', 8000000.00, 900000.00, 160000.00, 8740000.00),  -- Kế toán
(6, '2025-04-01', 6500000.00, 400000.00, 60000.00, 6840000.00);  -- Quản kho