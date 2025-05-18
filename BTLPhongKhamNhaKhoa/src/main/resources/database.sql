-- 1. Quản lý bệnh nhân & lịch hẹn
CREATE TABLE Patients (
    PatientID INT PRIMARY KEY,
    HoTen VARCHAR(100),
    NgaySinh DATE,
    GioiTinh ENUM('M', 'N'),
    SDT VARCHAR(15),
    Email VARCHAR(100),
    DiaChi TEXT,
    NgayDangKy DATE,
    GhiChu TEXT
);

CREATE TABLE Appointments (
    AppointmentID INT PRIMARY KEY,
    PatientID INT,
    DoctorID INT,
    NgayGio DATETIME,
    TrangThai ENUM('Chờ', 'Đã khám', 'Hủy'),
    GhiChu TEXT,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (DoctorID) REFERENCES Employees(EmployeeID)
);

CREATE TABLE MedicalRecords (
    RecordID INT PRIMARY KEY,
    PatientID INT,
    NgayKham DATE,
    ChanDoan TEXT,
    PhuongPhapDieuTri TEXT,
    GhiChu TEXT,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

CREATE TABLE ToothCondition (
    ToothID INT PRIMARY KEY,
    PatientID INT,
    SoRang VARCHAR(10),
    TinhTrang TEXT,
    NgayCapNhat DATE,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

CREATE TABLE EmergencyCases (
    CaseID INT PRIMARY KEY,
    PatientID INT,
    NgayTiepNhan DATETIME,
    TinhTrang TEXT,
    PhuongPhap TEXT,
    TrangThai ENUM('Đang điều trị', 'Hoàn thành'),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

CREATE TABLE OnlineBookings (
    BookingID INT PRIMARY KEY,
    PatientID INT,
    NgayDat DATETIME,
    NgayHen DATETIME,
    TrangThai ENUM('Chờ xác nhận', 'Đã xác nhận', 'Hủy'),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

CREATE TABLE SMSNotifications (
    SMSID INT PRIMARY KEY,
    PatientID INT,
    NoiDung TEXT,
    NgayGui DATETIME,
    TrangThai ENUM('Đã gửi', 'Chưa gửi'),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

-- 2. Quản lý nhân viên & hiệu suất làm việc
CREATE TABLE Employees (
    EmployeeID INT PRIMARY KEY,
    HoTen VARCHAR(100),
    NgaySinh DATE,
    GioiTinh ENUM('M', 'N'),
    SDT VARCHAR(15),
    Email VARCHAR(100),
    DiaChi TEXT,
    ChucVu VARCHAR(50),
    NgayBatDau DATE,
    TrangThai ENUM('Đang làm', 'Nghỉ việc')
);

CREATE TABLE EmployeeRecords (
    RecordID INT PRIMARY KEY,
    EmployeeID INT,
    NgayBatDau DATE,
    LoaiHopDong VARCHAR(50),
    ChungChi VARCHAR(100),
    NgayHetHan DATE,
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
);

CREATE TABLE EmployeeHistory (
    HistoryID INT PRIMARY KEY,
    EmployeeID INT,
    NgayThayDoi DATETIME,
    LoaiThayDoi VARCHAR(50),
    LyDo TEXT,
    GhiChu TEXT,
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
);

CREATE TABLE EmployeeTraining (
    TrainingID INT PRIMARY KEY,
    EmployeeID INT,
    TenKhoaHoc VARCHAR(100),
    NgayBatDau DATE,
    NgayKetThuc DATE,
    ChungChi VARCHAR(100),
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
);

CREATE TABLE DentistPerformance (
    PerformanceID INT PRIMARY KEY,
    DoctorID INT,
    Thang INT,
    Nam INT,
    SoCaDieuTri INT,
    DiemHaiLong DECIMAL(3,1),
    FOREIGN KEY (DoctorID) REFERENCES Employees(EmployeeID)
);

-- 3. Quản lý dịch vụ nha khoa & điều trị
CREATE TABLE DentalServices (
    ServiceID INT PRIMARY KEY,
    TenDichVu VARCHAR(100),
    Gia DECIMAL(10,2),
    MoTa TEXT
);

CREATE TABLE MedicalProcedures (
    ProcedureID INT PRIMARY KEY,
    AppointmentID INT,
    ServiceID INT,
    MoTa TEXT,
    NgayThucHien DATETIME,
    FOREIGN KEY (AppointmentID) REFERENCES Appointments(AppointmentID),
    FOREIGN KEY (ServiceID) REFERENCES DentalServices(ServiceID)
);

CREATE TABLE ToolUsage (
    UsageID INT PRIMARY KEY,
    ToolID INT,
    AppointmentID INT,
    NgaySuDung DATETIME,
    BacSiPhuTrach VARCHAR(100),
    TrangThaiVeSinh ENUM('Đã khử trùng', 'Đang chờ vệ sinh'),
    FOREIGN KEY (AppointmentID) REFERENCES Appointments(AppointmentID)
);

-- 4. Quản lý tài chính & thanh toán
CREATE TABLE Invoices (
    InvoiceID INT PRIMARY KEY,
    PatientID INT,
    NgayThanhToan DATE,
    TongTien DECIMAL(10,2),
    PhuongThuc ENUM('Tiền mặt', 'Thẻ', 'Ví điện tử'),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

CREATE TABLE PaymentHistory (
    PaymentHistoryID INT PRIMARY KEY,
    InvoiceID INT,
    NgayThanhToan DATETIME,
    SoTien DECIMAL(10,2),
    HinhThucThanhToan VARCHAR(50),
    GhiChu TEXT,
    FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID)
);

CREATE TABLE OnlinePayment (
    PaymentID INT PRIMARY KEY,
    PatientID INT,
    NgayThanhToan DATETIME,
    SoTien DECIMAL(10,2),
    HinhThuc ENUM('Momo', 'ZaloPay', 'Thẻ ngân hàng'),
    TrangThai ENUM('Đã thanh toán', 'Chờ xác nhận', 'Thất bại'),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

CREATE TABLE FinancialReports (
    ReportID INT PRIMARY KEY,
    Thang INT,
    Nam INT,
    DoanhThu DECIMAL(15,2),
    ChiPhi DECIMAL(15,2),
    LoiNhuan DECIMAL(15,2)
);

-- 5. Quản lý kho & thiết bị
CREATE TABLE MedicineStock (
    StockID INT PRIMARY KEY,
    TenVatLieu VARCHAR(100),
    SoLuong INT,
    NgayNhap DATE,
    HanSuDung DATE
);

CREATE TABLE EquipmentMaintenance (
    MaintenanceID INT PRIMARY KEY,
    ThietBi VARCHAR(100),
    NgayKiemTra DATE,
    TrangThai ENUM('Hoạt động tốt', 'Cần sửa chữa'),
    GhiChu TEXT
);

-- 6. Chăm sóc khách hàng & tiếp thị
CREATE TABLE LoyaltyProgram (
    LoyaltyID INT PRIMARY KEY,
    PatientID INT,
    DiemTichLuy INT,
    HangThanhVien ENUM('Bạc', 'Vàng', 'Kim cương'),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

CREATE TABLE ReferralProgram (
    ReferralID INT PRIMARY KEY,
    NguoiGioiThieuID INT,
    NguoiDuocGioiThieuID INT,
    NgayDangKy DATETIME,
    Thuong DECIMAL(10,2),
    FOREIGN KEY (NguoiGioiThieuID) REFERENCES Patients(PatientID),
    FOREIGN KEY (NguoiDuocGioiThieuID) REFERENCES Patients(PatientID)
);

CREATE TABLE PatientSatisfactionSurvey (
    SurveyID INT PRIMARY KEY,
    PatientID INT,
    NgayKhaoSat DATE,
    Diem INT,
    NhanXet TEXT,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

CREATE TABLE Complaints (
    ComplaintID INT PRIMARY KEY,
    PatientID INT,
    NgayPhanHoi DATETIME,
    NoiDung TEXT,
    TrangThai ENUM('Đang xử lý', 'Đã xử lý'),
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
);

CREATE TABLE MarketingCampaigns (
    CampaignID INT PRIMARY KEY,
    TenChienDich VARCHAR(100),
    NgayBatDau DATE,
    NgayKetThuc DATE,
    NganSach DECIMAL(15,2),
    HieuQua VARCHAR(50)
);

-- 7. Quản lý chi nhánh & bảo hiểm
CREATE TABLE ClinicBranches (
    BranchID INT PRIMARY KEY,
    TenChiNhanh VARCHAR(100),
    DiaChi TEXT,
    SDT VARCHAR(15),
    QuanLy VARCHAR(100)
);

CREATE TABLE InsuranceContracts (
    ContractID INT PRIMARY KEY,
    CongTyBaoHiem VARCHAR(100),
    GoiBaoHiem VARCHAR(100),
    MucChiTra DECIMAL(5,2),
    GhiChu TEXT
);