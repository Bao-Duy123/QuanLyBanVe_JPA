-- =====================================================
-- SQL Script: Tạo Database QuanLyVeTau_JPA
-- Database mới cho JPA Project với dữ liệu tiếng Việt
-- =====================================================

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'QuanLyVeTau_JPA')
BEGIN
    ALTER DATABASE QuanLyVeTau_JPA SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyVeTau_JPA;
END
GO

CREATE DATABASE QuanLyVeTau_JPA
COLLATE Vietnamese_CI_AS;
GO

USE QuanLyVeTau_JPA;
GO

-- =====================================================
-- BẢNG Ga (Ga tàu)
-- =====================================================
CREATE TABLE Ga (
    MaGa NVARCHAR(20) PRIMARY KEY,
    TenGa NVARCHAR(100) NOT NULL,
    DiaChi NVARCHAR(255)
);
GO

-- =====================================================
-- BẢNG Tuyen (Tuyến đường)
-- =====================================================
CREATE TABLE Tuyen (
    MaTuyen NVARCHAR(20) PRIMARY KEY,
    TenTuyen NVARCHAR(100) NOT NULL,
    GaDau NVARCHAR(20),
    GaCuoi NVARCHAR(20),
    DonGiaKM INT,
    FOREIGN KEY (GaDau) REFERENCES Ga(MaGa),
    FOREIGN KEY (GaCuoi) REFERENCES Ga(MaGa)
);
GO

-- =====================================================
-- BẢNG NhanVien (Nhân viên)
-- =====================================================
CREATE TABLE NhanVien (
    MaNV NVARCHAR(20) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoCCCD NVARCHAR(20),
    NgaySinh DATE,
    Email NVARCHAR(100),
    SDT NVARCHAR(20),
    GioiTinh NVARCHAR(10),
    DiaChi NVARCHAR(255),
    NgayVaoLam DATE,
    ChucVu NVARCHAR(50)
);
GO

-- =====================================================
-- BẢNG TaiKhoan (Tài khoản)
-- =====================================================
CREATE TABLE TaiKhoan (
    MaNV NVARCHAR(20) PRIMARY KEY,
    TenDangNhap NVARCHAR(50),
    MatKhau NVARCHAR(50),
    NgayTao DATE,
    TrangThai NVARCHAR(50),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);
GO

-- =====================================================
-- BẢNG KhachHang (Khách hàng)
-- =====================================================
CREATE TABLE KhachHang (
    MaKhachHang NVARCHAR(20) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    CCCD NVARCHAR(20) UNIQUE,
    SoDienThoai NVARCHAR(20),
    GioiTinh NVARCHAR(10),
    NgaySinh DATE
);
GO

-- =====================================================
-- BẢNG Tau (Tàu)
-- =====================================================
CREATE TABLE Tau (
    SoHieu NVARCHAR(20) PRIMARY KEY,
    TrangThai NVARCHAR(50)
);
GO

-- =====================================================
-- BẢNG LoaiToa (Loại toa)
-- =====================================================
CREATE TABLE LoaiToa (
    MaLoaiToa NVARCHAR(20) PRIMARY KEY,
    TenLoaiToa NVARCHAR(50),
    HeSo FLOAT
);
GO

-- =====================================================
-- BẢNG Toa (Toa tàu)
-- =====================================================
CREATE TABLE Toa (
    MaToa NVARCHAR(20) PRIMARY KEY,
    MaTau NVARCHAR(20),
    MaLoaiToa NVARCHAR(20),
    FOREIGN KEY (MaTau) REFERENCES Tau(SoHieu),
    FOREIGN KEY (MaLoaiToa) REFERENCES LoaiToa(MaLoaiToa)
);
GO

-- =====================================================
-- BẢNG ChuyenTau (Chuyến tàu)
-- =====================================================
CREATE TABLE ChuyenTau (
    MaChuyenTau NVARCHAR(20) PRIMARY KEY,
    MaTuyen NVARCHAR(20),
    MaTau NVARCHAR(20),
    NgayKhoiHanh DATE,
    GioKhoiHanh TIME,
    GaDi NVARCHAR(20),
    GaDen NVARCHAR(20),
    NgayDenDuKien DATE,
    GioDenDuKien TIME,
    MaNV NVARCHAR(20),
    TrangThai NVARCHAR(50),
    FOREIGN KEY (MaTuyen) REFERENCES Tuyen(MaTuyen),
    FOREIGN KEY (MaTau) REFERENCES Tau(SoHieu),
    FOREIGN KEY (GaDi) REFERENCES Ga(MaGa),
    FOREIGN KEY (GaDen) REFERENCES Ga(MaGa),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);
GO

-- =====================================================
-- BẢNG ChoDat (Chỗ đặt/Ghế)
-- =====================================================
CREATE TABLE ChoDat (
    MaCho NVARCHAR(20) PRIMARY KEY,
    MaToa NVARCHAR(20),
    SoCho NVARCHAR(10),
    Khoang INT,
    Tang INT,
    MaChuyenTau NVARCHAR(20),
    FOREIGN KEY (MaToa) REFERENCES Toa(MaToa),
    FOREIGN KEY (MaChuyenTau) REFERENCES ChuyenTau(MaChuyenTau)
);
GO

-- =====================================================
-- BẢNG LoaiVe (Loại vé)
-- =====================================================
CREATE TABLE LoaiVe (
    MaLoaiVe NVARCHAR(20) PRIMARY KEY,
    TenLoaiVe NVARCHAR(50),
    MucGiamGia FLOAT,
    TuoiMin INT,
    TuoiMax INT
);
GO

-- =====================================================
-- BẢNG KhuyenMai (Khuyến mãi)
-- =====================================================
CREATE TABLE KhuyenMai (
    MaKM NVARCHAR(20) PRIMARY KEY,
    TenKM NVARCHAR(100),
    LoaiKM NVARCHAR(50),
    GiaTriGiam DECIMAL(10,2),
    DKApDung NVARCHAR(50),
    GiaTriDK DECIMAL(10,2),
    NgayBD DATETIME,
    NgayKT DATETIME,
    TrangThai NVARCHAR(50)
);
GO

-- =====================================================
-- BẢNG HoaDon (Hóa đơn)
-- =====================================================
CREATE TABLE HoaDon (
    MaHD NVARCHAR(20) PRIMARY KEY,
    MaKhachHang NVARCHAR(20),
    MaNVLap NVARCHAR(20),
    MaKM NVARCHAR(20),
    TongTien DECIMAL(10,2),
    NgayLap DATETIME,
    PhuongThuc NVARCHAR(50),
    LoaiHoaDon NVARCHAR(50),
    TongCong DECIMAL(10,2),
    MaHoaDon_Goc NVARCHAR(20),
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV),
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
);
GO

-- =====================================================
-- BẢNG Ve (Vé)
-- =====================================================
CREATE TABLE Ve (
    MaVe NVARCHAR(20) PRIMARY KEY,
    MaLoaiVe NVARCHAR(20),
    GiaVe DECIMAL(10,2),
    TrangThai NVARCHAR(50),
    MaKhachHang NVARCHAR(20),
    MaChuyenTau NVARCHAR(20),
    MaChoDat NVARCHAR(20),
    MaNV NVARCHAR(20),
    FOREIGN KEY (MaLoaiVe) REFERENCES LoaiVe(MaLoaiVe),
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    FOREIGN KEY (MaChuyenTau) REFERENCES ChuyenTau(MaChuyenTau),
    FOREIGN KEY (MaChoDat) REFERENCES ChoDat(MaCho),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);
GO

-- =====================================================
-- BẢNG ChiTietHoaDon (Chi tiết hóa đơn)
-- =====================================================
CREATE TABLE ChiTietHoaDon (
    MaHD NVARCHAR(20),
    MaVe NVARCHAR(20),
    DonGia DECIMAL(10,2),
    SoLuong INT,
    PRIMARY KEY (MaHD, MaVe),
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD),
    FOREIGN KEY (MaVe) REFERENCES Ve(MaVe)
);
GO

-- =====================================================
-- BẢNG GA_TRONG_TUYEN (Ga trong tuyến)
-- =====================================================
CREATE TABLE GA_TRONG_TUYEN (
    MaTuyen NVARCHAR(20),
    MaGa NVARCHAR(20),
    ThuTuGa INT,
    KhoangCachTichLuy INT,
    ThoiGianDiChuyenToiGaTiepTheo INT,
    ThoiGianDung INT,
    PRIMARY KEY (MaTuyen, MaGa),
    FOREIGN KEY (MaTuyen) REFERENCES Tuyen(MaTuyen),
    FOREIGN KEY (MaGa) REFERENCES Ga(MaGa)
);
GO

-- =====================================================
-- DỮ LIỆU MẪU - Ga (với tiếng Việt)
-- =====================================================
INSERT INTO Ga (MaGa, TenGa, DiaChi) VALUES
(N'GA001', N'Ga Hà Nội', N'Số 120 Đường Lê Duẩn, Quận Hoàn Kiếm, Hà Nội'),
(N'GA002', N'Ga Đà Nẵng', N'Số 201 Đường Trần Phú, Quận Hải Châu, Đà Nẵng'),
(N'GA003', N'Ga Nha Trang', N'Số 17 Đường Thái Nguyên, Thành phố Nha Trang, Khánh Hòa'),
(N'GA004', N'Ga Sài Gòn', N'Số 1 Đường Nguyễn Thị Nhỳ, Quận 1, TP. Hồ Chí Minh'),
(N'GA005', N'Ga Huế', N'Số 02 Đường Bà Huyện Thanh Quan, Thành phố Huế'),
(N'GA006', N'Ga Quảng Ngãi', N'Số 88 Đường Điện Biên Phủ, Thành phố Quảng Ngãi'),
(N'GA007', N'Ga Vinh', N'Số 159 Đường Lê Lợi, Thành phố Vinh, Nghệ An'),
(N'GA008', N'Ga Phan Thiết', N'Số 01 Đường Nguyễn Tất Thành, Thành phố Phan Thiết, Bình Thuận');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Tuyến
-- =====================================================
INSERT INTO Tuyen (MaTuyen, TenTuyen, GaDau, GaCuoi, DonGiaKM) VALUES
(N'T01', N'Tuyến Bắc - Nam', N'GA001', N'GA004', 800),
(N'T02', N'Tuyến Hà Nội - Đà Nẵng', N'GA001', N'GA002', 1200),
(N'T03', N'Tuyến Sài Gòn - Nha Trang', N'GA004', N'GA003', 900);
GO

-- =====================================================
-- DỮ LIỆU MẪU - Nhân viên (với tiếng Việt)
-- =====================================================
INSERT INTO NhanVien (MaNV, HoTen, SoCCCD, NgaySinh, Email, SDT, GioiTinh, DiaChi, NgayVaoLam, ChucVu) VALUES
(N'NV001', N'Nguyễn Văn Minh', N'079201234567', '1985-03-15', N'minh.nv@vietlai.vn', N'0901234567', N'Nam', N'123 Đường Lê Lợi, Quận 1, TP.HCM', '2020-01-15', N'Quản lý'),
(N'NV002', N'Trần Thị Hương', N'079208765432', '1992-07-22', N'huong.tt@vietlai.vn', N'0902345678', N'Nữ', N'456 Đường Nguyễn Huệ, Quận 1, TP.HCM', '2021-03-20', N'Nhân viên bán vé'),
(N'NV003', N'Lê Hoàng Nam', N'079209876543', '1988-11-10', N'nam.lh@vietlai.vn', N'0903456789', N'Nam', N'789 Đường Đồng Khởi, Quận 1, TP.HCM', '2020-06-01', N'Nhân viên bán vé'),
(N'NV004', N'Phạm Thị Lan', N'079201112223', '1995-05-30', N'lan.pt@vietlai.vn', N'0904567890', N'Nữ', N'321 Đường Hai Bà Trưng, Quận 3, TP.HCM', '2022-01-10', N'Nhân viên bán vé'),
(N'NV005', N'Hoàng Đức Anh', N'079203334445', '1990-09-18', N'anh.hd@vietlai.vn', N'0905678901', N'Nam', N'654 Đường Võ Văn Tần, Quận 3, TP.HCM', '2021-08-15', N'Trưởng phòng');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Tài khoản
-- =====================================================
INSERT INTO TaiKhoan (MaNV, TenDangNhap, MatKhau, NgayTao, TrangThai) VALUES
(N'NV001', N'admin', N'admin', '2020-01-15', N'Đang hoạt động'),
(N'NV002', N'nv002', N'123456', '2021-03-20', N'Đang hoạt động'),
(N'NV003', N'nv003', N'123456', '2020-06-01', N'Đang hoạt động'),
(N'NV004', N'nv004', N'123456', '2022-01-10', N'Đang hoạt động'),
(N'NV005', N'nv005', N'123456', '2021-08-15', N'Đang hoạt động');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Khách hàng (với tiếng Việt)
-- =====================================================
INSERT INTO KhachHang (MaKhachHang, HoTen, CCCD, SoDienThoai, GioiTinh, NgaySinh) VALUES
(N'KH001', N'Võ Thị Mai', N'079295551111', N'0911234567', N'Nữ', '1990-04-12'),
(N'KH002', N'Đặng Minh Tuấn', N'079296662222', N'0912345678', N'Nam', '1985-08-25'),
(N'KH003', N'Lý Thị Hồng', N'079297773333', N'0913456789', N'Nữ', '1998-12-03'),
(N'KH004', N'Bùi Đức Phú', N'079298884444', N'0914567890', N'Nam', '1992-06-18'),
(N'KH005', N'Ngô Thị Thanh', N'079299995555', N'0915678901', N'Nữ', '1996-02-28');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Tàu
-- =====================================================
INSERT INTO Tau (SoHieu, TrangThai) VALUES
(N'TAU01', N'Hoạt động'),
(N'TAU02', N'Hoạt động'),
(N'TAU03', N'Bảo trì');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Loại toa
-- =====================================================
INSERT INTO LoaiToa (MaLoaiToa, TenLoaiToa, HeSo) VALUES
(N'LT01', N'Toa cứng', 1.0),
(N'LT02', N'Toa mềm', 1.5),
(N'LT03', N'Toa giường nằm', 2.0),
(N'LT04', N'Toa VIP', 3.0);
GO

-- =====================================================
-- DỮ LIỆU MẪU - Toa
-- =====================================================
INSERT INTO Toa (MaToa, MaTau, MaLoaiToa) VALUES
(N'TOA01', N'TAU01', N'LT01'),
(N'TOA02', N'TAU01', N'LT02'),
(N'TOA03', N'TAU01', N'LT03'),
(N'TOA04', N'TAU02', N'LT01'),
(N'TOA05', N'TAU02', N'LT04');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Chuyến tàu
-- =====================================================
INSERT INTO ChuyenTau (MaChuyenTau, MaTuyen, MaTau, NgayKhoiHanh, GioKhoiHanh, GaDi, GaDen, NgayDenDuKien, GioDenDuKien, MaNV, TrangThai) VALUES
(N'CT001', N'T01', N'TAU01', '2026-05-01', '06:00:00', N'GA001', N'GA004', '2026-05-02', '05:30:00', N'NV001', N'DANG_MO_BAN_VE'),
(N'CT002', N'T01', N'TAU02', '2026-05-01', '18:00:00', N'GA001', N'GA004', '2026-05-02', '17:30:00', N'NV002', N'DANG_MO_BAN_VE'),
(N'CT003', N'T02', N'TAU01', '2026-05-02', '08:00:00', N'GA001', N'GA002', '2026-05-02', '16:00:00', N'NV001', N'DANG_MO_BAN_VE');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Chỗ đặt
-- =====================================================
INSERT INTO ChoDat (MaCho, MaToa, SoCho, Khoang, Tang, MaChuyenTau) VALUES
(N'C001', N'TOA01', N'A1', 1, 1, N'CT001'),
(N'C002', N'TOA01', N'A2', 1, 1, N'CT001'),
(N'C003', N'TOA01', N'B1', 2, 1, N'CT001'),
(N'C004', N'TOA02', N'C1', 1, 2, N'CT001'),
(N'C005', N'TOA03', N'D1', 1, 1, N'CT001');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Loại vé
-- =====================================================
INSERT INTO LoaiVe (MaLoaiVe, TenLoaiVe, MucGiamGia, TuoiMin, TuoiMax) VALUES
(N'LV01', N'Vé người lớn', 1.0, 12, 100),
(N'LV02', N'Vé trẻ em', 0.5, 5, 11),
(N'LV03', N'Vé sinh viên', 0.7, 18, 25),
(N'LV04', N'Vé người cao tuổi', 0.6, 60, 100);
GO

-- =====================================================
-- DỮ LIỆU MẪU - Khuyến mãi (với tiếng Việt)
-- =====================================================
INSERT INTO KhuyenMai (MaKM, TenKM, LoaiKM, GiaTriGiam, DKApDung, GiaTriDK, NgayBD, NgayKT, TrangThai) VALUES
(N'KM001', N'Giảm giá mùa hè', N'PHAN_TRAM_GIA', 15.00, N'MIN_GIA', 500000.00, '2026-05-01', '2026-08-31', N'Hoạt động'),
(N'KM002', N'Khuyến mãi Tết', N'PHAN_TRAM_GIA', 20.00, N'NONE', 0.00, '2026-01-20', '2026-02-15', N'Hết hạn'),
(N'KM003', N'Giảm giá cố định', N'CO_DINH', 50000.00, N'NONE', 0.00, '2026-01-01', '2026-12-31', N'Hoạt động');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Hóa đơn
-- =====================================================
INSERT INTO HoaDon (MaHD, MaKhachHang, MaNVLap, MaKM, TongTien, NgayLap, PhuongThuc, LoaiHoaDon, TongCong, MaHoaDon_Goc) VALUES
(N'HD001', N'KH001', N'NV002', N'KM001', 850000.00, '2026-04-28 10:30:00', N'Tiền mặt', N'Vé lẻ', 1000000.00, NULL),
(N'HD002', N'KH002', N'NV003', NULL, 700000.00, '2026-04-28 14:15:00', N'Chuyển khoản', N'Vé lẻ', 700000.00, NULL),
(N'HD003', N'KH003', N'NV002', N'KM003', 450000.00, '2026-04-29 09:00:00', N'Tiền mặt', N'Vé lẻ', 500000.00, NULL);
GO

-- =====================================================
-- DỮ LIỆU MẪU - Vé
-- =====================================================
INSERT INTO Ve (MaVe, MaLoaiVe, GiaVe, TrangThai, MaKhachHang, MaChuyenTau, MaChoDat, MaNV) VALUES
(N'VE001', N'LV01', 500000.00, N'Đã bán', N'KH001', N'CT001', N'C001', N'NV002'),
(N'VE002', N'LV01', 500000.00, N'Đã bán', N'KH002', N'CT001', N'C002', N'NV003'),
(N'VE003', N'LV02', 250000.00, N'Đã bán', N'KH003', N'CT002', N'C004', N'NV002');
GO

-- =====================================================
-- DỮ LIỆU MẪU - Chi tiết hóa đơn
-- =====================================================
INSERT INTO ChiTietHoaDon (MaHD, MaVe, DonGia, SoLuong) VALUES
(N'HD001', N'VE001', 500000.00, 1),
(N'HD002', N'VE002', 700000.00, 1),
(N'HD003', N'VE003', 500000.00, 1);
GO

-- =====================================================
-- DỮ LIỆU MẪU - Ga trong Tuyến
-- =====================================================
INSERT INTO GA_TRONG_TUYEN (MaTuyen, MaGa, ThuTuGa, KhoangCachTichLuy, ThoiGianDiChuyenToiGaTiepTheo, ThoiGianDung) VALUES
(N'T01', N'GA001', 1, 0, 0, 0),
(N'T01', N'GA007', 2, 300, 360, 10),
(N'T01', N'GA005', 3, 600, 300, 15),
(N'T01', N'GA002', 4, 900, 360, 15),
(N'T01', N'GA006', 5, 1100, 240, 10),
(N'T01', N'GA003', 6, 1400, 360, 15),
(N'T01', N'GA008', 7, 1700, 300, 10),
(N'T01', N'GA004', 8, 1900, 0, 0),
(N'T02', N'GA001', 1, 0, 0, 0),
(N'T02', N'GA007', 2, 300, 360, 10),
(N'T02', N'GA005', 3, 600, 300, 15),
(N'T02', N'GA002', 4, 900, 0, 0);
GO

PRINT N'Database QuanLyVeTau_JPA đã được tạo thành công với dữ liệu tiếng Việt!';
GO
