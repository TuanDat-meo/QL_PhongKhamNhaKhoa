USE QuanLyPhongKham;

-- Thêm dữ liệu vào bảng NguoiDung
INSERT INTO NguoiDung (hoTen, email, matKhau, soDienThoai, ngaySinh, gioiTinh, vaiTro) VALUES
(N'Nguyễn Văn Minh', N'admin@gmail.com', N'admin123', N'0987654321', '1985-05-20', N'Nam', N'Admin'),
(N'Trần Thị Hương', N'bacsi01@gmail.com', N'bacsi123', N'0912345678', '1982-09-15', N'Nữ', N'Bác sĩ'),
(N'Lê Tuấn Đạt', N'bacsi02@gmail.com', N'bacsi123', N'0907001320', '1988-08-06', N'Nam', N'Bác sĩ'),
(N'Phạm Thị Mai', N'bacsi03@gmail.com', N'bacsi123', N'0908765432', '1990-04-25', N'Nữ', N'Bác sĩ'),
(N'Hoàng Văn Nam', N'bacsi04@gmail.com', N'bacsi123', N'0901234567', '1986-11-10', N'Nam', N'Bác sĩ'),
(N'Lê Thị Thanh', N'letan@gmail.com', N'letan123', N'0905123456', '1992-07-10', N'Nữ', N'Lễ tân'),
(N'Hoàng Thị Lan', N'ketoan@gmail.com', N'ketoan123', N'0934567890', '1988-11-30', N'Nữ', N'Kế toán'),
(N'Phạm Văn Tùng', N'quankho@gmail.com', N'quankho123', N'0976543210', '1995-03-25', N'Nam', N'Quản kho');

-- Thêm người dùng thông thường (bệnh nhân tiềm năng) - Đảm bảo thông tin user khớp với bệnh nhân
INSERT INTO NguoiDung (hoTen, email, matKhau, soDienThoai, ngaySinh, gioiTinh, vaiTro) VALUES
(N'Trần Minh Hoàng', N'tranminhhoang@gmail.com', N'user123', N'0978123456', '1990-07-15', N'Nam', null),
(N'Phạm Thị Lan', N'phamthilan@gmail.com', N'user123', N'0967543210', '1985-12-30', N'Nữ', null),
(N'Đỗ Văn Tuấn', N'dovantuan@gmail.com', N'user123', N'0987654321', '2000-04-18', N'Nam', null),
(N'Vũ Ngọc Mai', N'vungocmai@gmail.com', N'user123', N'0934567890', '1998-11-25', N'Nữ', null),
(N'Ngô Đức Tài', N'ngoductai@gmail.com', N'user123', N'0923456789', '1993-06-10', N'Nam', null),
(N'Ngô Bảo Anh', N'ngobaoanh@gmail.com', N'user123', N'0923451289', '1999-06-10', N'Nam', null),
(N'Lương Thị Hạnh', N'luongthihanh@gmail.com', N'user123', N'0912123456', '1987-09-05', N'Nữ', null),
(N'Bùi Quang Minh', N'buiquangminh@gmail.com', N'user123', N'0912876543', '1995-03-12', N'Nam', null),
(N'Hoàng Thị Lan Anh', N'hoangthilananh@gmail.com', N'user123', N'0967123456', '1988-07-22', N'Nữ', null),
(N'Đặng Quốc Tuấn', N'dangquoctuan@gmail.com', N'user123', N'0923789456', '1982-11-30', N'Nam', null),
(N'Lê Thu Trang', N'lethutrang@gmail.com', N'user123', N'0912345687', '2001-05-18', N'Nữ', null),
(N'Nguyễn Văn Hòa', N'nguyenvanhoa@gmail.com', N'user123', N'0978912345', '1979-08-25', N'Nam', null),
(N'Trịnh Minh Thư', N'trinhminhthư@gmail.com', N'user123', N'0945678912', '2002-01-15', N'Nữ', null),
(N'Phan Thanh Hải', N'phanthanhhai@gmail.com', N'user123', N'0989123456', '1997-09-30', N'Nam', null),
(N'Dương Thị Hồng', N'duongthihong@gmail.com', N'user123', N'0967891234', '1994-12-20', N'Nữ', null);

-- Thêm dữ liệu mẫu vào bảng BenhNhan - Giữ sự nhất quán với thông tin người dùng
INSERT INTO BenhNhan (hoTen, ngaySinh, gioiTinh, soDienThoai, cccd, diaChi) VALUES
(N'Trần Minh Hoàng', '1990-07-15', N'Nam', N'0978123456', N'052090000123', N'23 Lê Lợi, Quận Hải Châu, Đà Nẵng'),
(N'Phạm Thị Lan', '1985-12-30', N'Nữ', N'0967543210', N'031085000234', N'45 Lạch Tray, Quận Ngô Quyền, Hải Phòng'),
(N'Đỗ Văn Tuấn', '2000-04-18', N'Nam', N'0987654321', N'001000012345', N'78 Nguyễn Văn Cừ, Quận Ninh Kiều, Cần Thơ'),
(N'Vũ Ngọc Mai', '1998-11-25', N'Nữ', N'0934567890', N'074098000456', N'12 Nguyễn Huệ, TP Thủ Dầu Một, Bình Dương'),
(N'Ngô Đức Tài', '1993-06-10', N'Nam', N'0923456789', N'046093000567', N'56 Trần Hưng Đạo, TP Huế, Thừa Thiên Huế'),
(N'Ngô Bảo Anh', '1999-06-10', N'Nam', N'0923451289', N'046099000321', N'34 Lê Lợi, TP Huế, Thừa Thiên Huế'),
(N'Lương Thị Hạnh', '1987-09-05', N'Nữ', N'0912123456', N'040087000678', N'45 Hồ Tùng Mậu, TP Vinh, Nghệ An'),
(N'Bùi Quang Minh', '1995-03-12', N'Nam', N'0912876543', N'001095000789', N'67 Láng Hạ, Quận Đống Đa, Hà Nội'),
(N'Hoàng Thị Lan Anh', '1988-07-22', N'Nữ', N'0967123456', N'001088000987', N'34 Trần Duy Hưng, Quận Cầu Giấy, Hà Nội'),
(N'Đặng Quốc Tuấn', '1982-11-30', N'Nam', N'0923789456', N'079082000654', N'89 Nguyễn Đình Chiểu, Quận 3, TP.HCM'),
(N'Lê Thu Trang', '2001-05-18', N'Nữ', N'0912345687', N'001001000321', N'12 Lý Thường Kiệt, Quận Hoàn Kiếm, Hà Nội'),
(N'Nguyễn Văn Hòa', '1979-08-25', N'Nam', N'0978912345', N'001079000123', N'56 Đội Cấn, Quận Ba Đình, Hà Nội'),
(N'Trịnh Minh Thư', '2002-01-15', N'Nữ', N'0945678912', N'001002000456', N'78 Thái Hà, Quận Đống Đa, Hà Nội'),
(N'Phan Thanh Hải', '1997-09-30', N'Nam', N'0989123456', N'079097000789', N'23 Võ Văn Tần, Quận 3, TP.HCM'),
(N'Dương Thị Hồng', '1994-12-20', N'Nữ', N'0967891234', N'079094000654', N'45 Trần Hưng Đạo, Quận 1, TP.HCM');

-- Thêm dữ liệu mẫu vào bảng PhongKham
INSERT INTO PhongKham (tenPhong, diaChi) VALUES
(N'Phòng khám Răng Hàm Mặt Hà Nội', N'123 Nguyễn Trãi, Quận Thanh Xuân, Hà Nội'),
(N'Phòng khám Nha khoa Sài Gòn', N'456 Lê Lợi, Quận 1, TP.HCM'),
(N'Phòng khám Chỉnh nha Đà Nẵng', N'789 Trần Phú, Quận Hải Châu, Đà Nẵng'),
(N'Phòng khám Nha khoa Biển Xanh', N'101 Lạch Tray, Quận Ngô Quyền, Hải Phòng'),
(N'Phòng khám Răng Hàm Mặt Miền Tây', N'202 Nguyễn Văn Cừ, Quận Ninh Kiều, Cần Thơ');

-- Thêm dữ liệu mẫu vào bảng BacSi - Giữ idNguoiDung đúng với người dùng vai trò bác sĩ
INSERT INTO BacSi (hoTenBacSi, idNguoiDung, idPhongKham, chuyenKhoa, bangCap, kinhNghiem) VALUES
(N'Trần Thị Hương', 2, 1, N'Răng Hàm Mặt', N'Tiến sĩ Y khoa - Đại học Y Hà Nội (2010)', 15),
(N'Lê Tuấn Đạt', 3, 2, N'Răng Hàm Mặt', N'Bác sĩ Chuyên khoa I - Đại học Y Dược TP.HCM (2012)', 10),
(N'Phạm Thị Mai', 4, 1, N'Chỉnh nha', N'Thạc sĩ Nha khoa - Đại học Y Hà Nội (2014)', 8),
(N'Hoàng Văn Nam', 5, 3, N'Phẫu thuật nha khoa', N'Bác sĩ Chuyên khoa II - Đại học Y Huế (2008)', 12),
(N'Nguyễn Văn Minh', 1, 2, N'Nha chu', N'Tiến sĩ Nha khoa - Đại học Y Dược TP.HCM (2005)', 20);

-- Thêm dữ liệu mẫu vào bảng NhaCungCap
INSERT INTO NhaCungCap (tenNCC, diaChi, soDienThoai, MaSoThue, NgayDangKy) VALUES
(N'Công ty Dược phẩm ABC', N'10 Nguyễn Huệ, Quận 1, TP.HCM', N'02812345678', N'123456789012', '2023-01-15'),
(N'Thiết bị Y tế XYZ', N'25 Trần Hưng Đạo, Quận Hoàn Kiếm, Hà Nội', N'02498765432', N'345678901234', '2022-12-30'),
(N'Vật tư Tiêu hao Minh Anh', N'5 Lê Duẩn, Quận Hải Châu, Đà Nẵng', N'02365432109', N'456789012345', '2023-04-18'),
(N'Dụng cụ Y khoa Bình An', N'30 Hoàng Diệu, Quận Ngô Quyền, Hải Phòng', N'02251122334', N'567890123456', '2022-11-25'),
(N'Hóa chất Xét nghiệm Sài Gòn', N'78 Võ Văn Tần, Quận 3, TP.HCM', N'02855667788', N'678901234567', '2023-06-10'),
(N'Công ty TNHH Trang Thiết Bị Y Tế Đức Minh', N'Khu Công Nghiệp Biên Hòa, Đồng Nai', N'02519988776', N'789012345678', '2023-02-20'),
(N'Dược phẩm Toàn Cầu', N'Lô B7 Khu Đô Thị Mới, Hà Đông, Hà Nội', N'02433445566', N'890123456789', '2022-09-01'),
(N'Vật tư Y tế Thanh Xuân', N'15 Nguyễn Trãi, Quận Thanh Xuân, Hà Nội', N'0909112233', N'901234567890', '2023-03-05'),
(N'Thiết bị Nha Khoa Phương Nam', N'42 Đường 3 Tháng 2, Quận 10, TP.HCM', N'0933445566', N'012345678901', '2023-07-22'),
(N'Bông Băng Việt', N'Khu Công Nghiệp Sóng Thần, TP Dĩ An, Bình Dương', N'02748899001', N'112233445566', '2022-08-11');

-- Thêm dữ liệu mẫu vào bảng KhoVatTu
INSERT INTO KhoVatTu (tenVatTu, soLuong, donViTinh, idNCC, phanLoai) VALUES
(N'Bơm tiêm 5ml', 1000, N'cái', 1, N'Vật tư tiêu hao'),
(N'Gạc vô trùng', 5000, N'gói', 3, N'Vật tư tiêu hao'),
(N'Nước muối sinh lý', 2000, N'chai', 1, N'Dung dịch'),
(N'Khẩu trang y tế', 10000, N'chiếc', 3, N'Vật tư tiêu hao'),
(N'Kim chích nha khoa', 8000, N'cái', 2, N'Vật tư tiêu hao'),
(N'Băng dính y tế', 3000, N'cuộn', 3, N'Vật tư tiêu hao'),
(N'Cồn 70 độ', 1000, N'lít', 5, N'Dung dịch'),
(N'Găng tay y tế (có bột)', 12000, N'đôi', 3, N'Vật tư tiêu hao'),
(N'Găng tay y tế (không bột)', 9000, N'đôi', 3, N'Vật tư tiêu hao'),
(N'Máy đo huyết áp điện tử', 200, N'cái', 2, N'Thiết bị'),
(N'Nhiệt kế điện tử', 300, N'cái', 2, N'Thiết bị'),
(N'Kéo phẫu thuật nha khoa', 150, N'cái', 4, N'Dụng cụ nha khoa'),
(N'Panh nha khoa', 180, N'cái', 4, N'Dụng cụ nha khoa'),
(N'Bông y tế', 7000, N'gói', 3, N'Vật tư tiêu hao'),
(N'Ghế nha khoa', 15, N'cái', 9, N'Thiết bị nha khoa'),
(N'Tay khoan nha khoa tốc độ cao', 50, N'cái', 9, N'Thiết bị nha khoa'),
(N'Vật liệu trám răng composite', 300, N'tuýp', 9, N'Vật liệu nha khoa'),
(N'Thuốc tê nha khoa', 500, N'ống', 1, N'Dược phẩm nha khoa'),
(N'Gương nha khoa', 200, N'cái', 9, N'Dụng cụ nha khoa'),
(N'Chỉ khâu nha khoa', 400, N'gói', 4, N'Vật tư tiêu hao'),
(N'Vật liệu lấy dấu', 250, N'hộp', 9, N'Vật liệu nha khoa'),
(N'Dung dịch súc miệng', 600, N'chai', 5, N'Dung dịch'),
(N'Máy chụp X-quang nha khoa', 10, N'cái', 9, N'Thiết bị nha khoa'),
(N'Mũi khoan nha khoa', 400, N'cái', 9, N'Dụng cụ nha khoa'),
(N'Đèn quang trùng hợp', 30, N'cái', 9, N'Thiết bị nha khoa');

-- Thêm dữ liệu mẫu vào bảng Thuoc
INSERT INTO Thuoc (tenThuoc, donViTinh, gia) VALUES
(N'Amoxicillin 500mg', N'Viên', 12000),
(N'Paracetamol 500mg', N'Viên', 5000),
(N'Ibuprofen 400mg', N'Viên', 8000),
(N'Metronidazole 250mg', N'Viên', 7000),
(N'Dexamethasone 0.5mg', N'Viên', 10000),
(N'Nước súc miệng Listerine', N'Chai', 75000),
(N'Gel Sensodyne - Kem đánh răng cho răng nhạy cảm', N'Tuýp', 85000),
(N'Cephalosporin 500mg', N'Viên', 15000),
(N'Thuốc giảm đau răng Dentanalgi', N'Tuýp', 45000),
(N'Vitamin C 1000mg', N'Viên', 3000),
(N'Thuốc nước súc miệng Colgate', N'Chai', 65000),
(N'Chlorhexidine 0.12%', N'Chai', 80000),
(N'Thuốc bôi lợi Periodontol', N'Tuýp', 60000),
(N'Kem bôi chống nhiễm trùng nha khoa', N'Tuýp', 55000),
(N'Hydrogen Peroxide 3%', N'Chai', 40000);

-- Thêm dữ liệu mẫu vào bảng DichVu
INSERT INTO DichVu (tenDichVu, gia) VALUES
(N'Khám và tư vấn nha khoa', 200000),
(N'Điều trị răng sâu', 500000),
(N'Nhổ răng thông thường', 300000),
(N'Nhổ răng khôn đơn giản', 800000),
(N'Nhổ răng khôn phức tạp', 1500000),
(N'Trám răng composite', 400000),
(N'Tẩy trắng răng tại phòng khám', 1500000),
(N'Cạo vôi răng và đánh bóng', 250000),
(N'Điều trị tủy răng một chân', 800000),
(N'Điều trị tủy răng nhiều chân', 1200000),
(N'Niềng răng mắc cài kim loại', 25000000),
(N'Niềng răng mắc cài sứ', 35000000),
(N'Niềng răng khay trong suốt', 45000000),
(N'Bọc răng sứ Zirconia', 4500000),
(N'Bọc răng sứ Ceramill', 5000000),
(N'Cấy ghép Implant', 20000000),
(N'Chụp X-quang răng toàn cảnh', 150000),
(N'Chụp CT Cone Beam', 800000),
(N'Điều trị viêm nha chu', 600000),
(N'Phục hình răng tháo lắp', 3500000);

-- Thêm dữ liệu mẫu vào bảng HoSoBenhAn với ngày quá khứ và hiện tại
INSERT INTO HoSoBenhAn (idBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai) VALUES
(1, N'Sâu răng vùng cổ răng số 16, 26', N'Bệnh nhân có tiền sử đau nhức khi ăn đồ ngọt.', '2024-01-15', N'Hoàn tất'),
(2, N'Viêm nha chu cấp tính', N'Lợi sưng đỏ, chảy máu khi chải răng.', '2024-02-20', N'Hoàn tất'),
(3, N'Răng khôn số 48 mọc lệch, gây viêm nướu', N'Cần nhổ răng khôn số 48.', '2024-03-05', N'Hoàn tất'),
(4, N'Răng cửa số 11 bị gãy 1/3', N'Bệnh nhân bị va đập khi chơi thể thao.', '2024-04-12', N'Hoàn tất'),
(5, N'Sâu răng sâu số 36, cần điều trị tủy', N'Bệnh nhân đau nhức liên tục vào ban đêm.', '2024-05-01', N'Đang điều trị'),
(6, N'Mất răng số 14, 15, 16 cần làm cầu răng', N'Mất răng lâu năm do sâu răng.', '2024-05-10', N'Đang điều trị'),
(7, N'Viêm nướu nhẹ, cao răng nhiều', N'Cần vệ sinh răng miệng định kỳ.', '2024-05-18', N'Mới'),
(8, N'Răng sứt mẻ số 21, 22 cần phục hình', N'Bệnh nhân muốn làm dán sứ veneer.', '2024-05-22', N'Mới'),
(9, N'Răng hô cần niềng răng', N'Bệnh nhân muốn cải thiện thẩm mỹ khuôn mặt.', '2024-05-25', N'Mới'),
(10, N'Loạn khớp thái dương hàm', N'Bệnh nhân có tiếng kêu khi há miệng, đau vùng khớp.', '2024-05-27', N'Mới');

-- Thêm dữ liệu mẫu vào bảng LichHen với những lịch trong quá khứ, hiện tại và tương lai
-- Đảm bảo idBenhNhan từ 1-15 phù hợp với người dùng từ 9-23 (vị trí 9+0, 9+1, 9+2, etc.)
INSERT INTO LichHen (idBacSi, idBenhNhan, ngayHen, idPhongKham, gioHen, trangThai, moTa) VALUES
-- Lịch hẹn quá khứ (đã hoàn thành)
(1, 1, '2024-01-15', 1, '09:00:00', N'Đã hoàn thành', N'Khám và tư vấn điều trị sâu răng'),
(2, 2, '2024-02-20', 2, '10:30:00', N'Đã hoàn thành', N'Khám và điều trị viêm nha chu'),
(3, 3, '2024-03-05', 1, '14:00:00', N'Đã hoàn thành', N'Nhổ răng khôn'),
(4, 4, '2024-04-12', 3, '08:30:00', N'Đã hoàn thành', N'Phục hình răng cửa bị gãy'),
(5, 5, '2024-05-01', 2, '15:30:00', N'Đã hoàn thành', N'Điều trị tủy răng số 36'),

-- Lịch hẹn hiện tại (tháng 5/2025)
(1, 5, '2025-05-06', 1, '09:30:00', N'Đã xác nhận', N'Tái khám sau điều trị tủy'),
(2, 6, '2025-05-06', 2, '11:00:00', N'Đã xác nhận', N'Lấy dấu làm cầu răng'),
(3, 7, '2025-05-07', 1, '14:30:00', N'Đã xác nhận', N'Cạo vôi răng'),
(4, 8, '2025-05-08', 3, '10:00:00', N'Chờ xác nhận', N'Tư vấn làm veneer'),
(5, 9, '2025-05-09', 2, '16:00:00', N'Chờ xác nhận', N'Tư vấn niềng răng'),

-- Lịch hẹn tương lai
(1, 10, '2025-05-15', 1, '09:00:00', N'Đã xác nhận', N'Khám loạn khớp thái dương hàm'),
(2, 1, '2025-05-20', 2, '10:30:00', N'Đã xác nhận', N'Khám định kỳ 6 tháng'),
(3, 2, '2025-05-25', 1, '15:00:00', N'Chờ xác nhận', N'Tái khám sau điều trị viêm nha chu'),
(4, 3, '2025-06-05', 3, '14:00:00', N'Chờ xác nhận', N'Tái khám vết nhổ răng'),
(5, 4, '2025-06-12', 2, '11:30:00', N'Chờ xác nhận', N'Kiểm tra răng phục hình'),

-- Lịch hẹn bị hủy
(1, 11, '2025-05-07', 1, '11:00:00', N'Đã hủy', N'Tư vấn trồng răng Implant'),
(2, 12, '2025-05-08', 2, '09:30:00', N'Đã hủy', N'Khám đau răng cấp tính');

-- Thêm dữ liệu mẫu vào bảng DieuTri
INSERT INTO DieuTri (idHoSo, idBacSi, moTa, ngayDieuTri) VALUES
(1, 1, N'Làm sạch và trám răng số 16, 26 bằng composite.', '2024-01-15'),
(1, 1, N'Tái khám sau trám răng, răng phục hồi tốt.', '2024-01-30'),
(2, 2, N'Cạo vôi răng và kê đơn thuốc kháng sinh cho viêm nha chu.', '2024-02-20'),
(2, 2, N'Tái khám, tình trạng viêm đã giảm, hướng dẫn vệ sinh răng miệng.', '2024-03-05'),
(3, 3, N'Nhổ răng khôn số 48, cắt lợi trùm, khâu vết thương.', '2024-03-05'),
(3, 3, N'Tái khám, cắt chỉ, vết thương lành tốt.', '2024-03-12');
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