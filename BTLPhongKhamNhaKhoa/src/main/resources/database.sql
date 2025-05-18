-- Kiểm tra và tạo cơ sở dữ liệu nếu chưa tồn tại
CREATE DATABASE IF NOT EXISTS PhongKhamNhaKhoa;
USE PhongKhamNhaKhoa;

-- Bảng người dùng
CREATE TABLE nguoi_dung (
    ma_nguoi_dung INT PRIMARY KEY AUTO_INCREMENT,
    so_cccd VARCHAR(12) UNIQUE NOT NULL,
    ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    vai_tro ENUM('Admin', 'Nha sĩ', 'Lễ tân') NOT NULL,
    trang_thai ENUM('Hoạt động', 'Bị khóa') DEFAULT 'Hoạt động'
) ENGINE=InnoDB;

-- Bảng lịch sử đăng nhập
CREATE TABLE lich_su_dang_nhap (
    ma_dang_nhap INT PRIMARY KEY AUTO_INCREMENT,
    ma_nguoi_dung INT NOT NULL,
    thoi_gian_dang_nhap DATETIME DEFAULT CURRENT_TIMESTAMP,
    dia_chi_ip VARCHAR(45),
    FOREIGN KEY (ma_nguoi_dung) REFERENCES nguoi_dung(ma_nguoi_dung) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng bệnh nhân
CREATE TABLE benh_nhan (
    ma_benh_nhan INT PRIMARY KEY AUTO_INCREMENT,
    so_cccd VARCHAR(12) UNIQUE NOT NULL,
    ho_ten NVARCHAR(100) NOT NULL,
    ngay_sinh DATE NOT NULL,
    gioi_tinh ENUM('Nam', 'Nữ', 'Khác') NOT NULL,
    so_dien_thoai VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    dia_chi NVARCHAR(255)
) ENGINE=InnoDB;

-- Bảng lịch hẹn
CREATE TABLE lich_hen (
    ma_lich_hen INT PRIMARY KEY AUTO_INCREMENT,
    ma_benh_nhan INT NOT NULL,
    ma_nha_si INT NULL,
    ngay_gio_hen DATETIME NOT NULL,
    trang_thai ENUM('Đã đặt', 'Đã hoàn thành', 'Đã hủy') DEFAULT 'Đã đặt',
    ghi_chu NVARCHAR(255),
    FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(ma_benh_nhan) ON DELETE RESTRICT,
    FOREIGN KEY (ma_nha_si) REFERENCES nguoi_dung(ma_nguoi_dung) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Bảng dịch vụ điều trị
CREATE TABLE dich_vu_dieu_tri (
    ma_dich_vu INT PRIMARY KEY AUTO_INCREMENT,
    ten_dich_vu NVARCHAR(100) NOT NULL,
    mo_ta NVARCHAR(255),
    gia DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB;

-- Bảng vật tư
CREATE TABLE vat_tu (
    ma_vat_tu INT PRIMARY KEY AUTO_INCREMENT,
    ten_vat_tu NVARCHAR(100) NOT NULL,
    so_luong INT NOT NULL,
    don_vi NVARCHAR(50) NOT NULL
) ENGINE=InnoDB;

-- Bảng hóa đơn
CREATE TABLE hoa_don (
    ma_hoa_don INT PRIMARY KEY AUTO_INCREMENT,
    ma_benh_nhan INT NOT NULL,
    ma_lich_hen INT NULL,
    tong_tien DECIMAL(10,2) NOT NULL,
    trang_thai_thanh_toan ENUM('Chưa thanh toán', 'Đã thanh toán') DEFAULT 'Chưa thanh toán',
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(ma_benh_nhan) ON DELETE RESTRICT,
    FOREIGN KEY (ma_lich_hen) REFERENCES lich_hen(ma_lich_hen) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Bảng dịch vụ - vật tư
CREATE TABLE dich_vu_vat_tu (
    ma_dich_vu INT NOT NULL,
    ma_vat_tu INT NOT NULL,
    so_luong INT NOT NULL,
    PRIMARY KEY (ma_dich_vu, ma_vat_tu),
    FOREIGN KEY (ma_dich_vu) REFERENCES dich_vu_dieu_tri(ma_dich_vu) ON DELETE RESTRICT,
    FOREIGN KEY (ma_vat_tu) REFERENCES vat_tu(ma_vat_tu) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng hóa đơn - dịch vụ
CREATE TABLE hoa_don_dich_vu (
    ma_hoa_don INT NOT NULL,
    ma_dich_vu INT NOT NULL,
    so_luong INT DEFAULT 1,
    don_gia DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (ma_hoa_don, ma_dich_vu),
    FOREIGN KEY (ma_hoa_don) REFERENCES hoa_don(ma_hoa_don) ON DELETE RESTRICT,
    FOREIGN KEY (ma_dich_vu) REFERENCES dich_vu_dieu_tri(ma_dich_vu) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng hóa đơn - vật tư
CREATE TABLE hoa_don_vat_tu (
    ma_hoa_don INT NOT NULL,
    ma_vat_tu INT NOT NULL,
    so_luong INT NOT NULL,
    don_gia DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (ma_hoa_don, ma_vat_tu),
    FOREIGN KEY (ma_hoa_don) REFERENCES hoa_don(ma_hoa_don) ON DELETE RESTRICT,
    FOREIGN KEY (ma_vat_tu) REFERENCES vat_tu(ma_vat_tu) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng quản lý lương
CREATE TABLE luong (
    ma_luong INT PRIMARY KEY AUTO_INCREMENT,
    ma_nha_si INT NOT NULL,
    thang INT NOT NULL,
    nam INT NOT NULL,
    luong_co_ban DECIMAL(10,2) NOT NULL,
    thuong DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (ma_nha_si) REFERENCES nguoi_dung(ma_nguoi_dung) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng thống kê
CREATE TABLE thong_ke (
    ma_thong_ke INT PRIMARY KEY AUTO_INCREMENT,
    ngay DATE NOT NULL,
    tong_benh_nhan INT NOT NULL,
    tong_dich_vu INT NOT NULL,
    doanh_thu DECIMAL(15,2) NOT NULL
) ENGINE=InnoDB;

-- Bảng lịch hẹn - dịch vụ
CREATE TABLE lich_hen_dich_vu (
    ma_lich_hen INT NOT NULL,
    ma_dich_vu INT NOT NULL,
    so_luong INT DEFAULT 1,
    PRIMARY KEY (ma_lich_hen, ma_dich_vu),
    FOREIGN KEY (ma_lich_hen) REFERENCES lich_hen(ma_lich_hen) ON DELETE RESTRICT,
    FOREIGN KEY (ma_dich_vu) REFERENCES dich_vu_dieu_tri(ma_dich_vu) ON DELETE RESTRICT
) ENGINE=InnoDB;