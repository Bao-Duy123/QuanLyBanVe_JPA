-- Chuyển về master để thực hiện kiểm tra và tạo Database
USE [master]
GO

-- 1. Kiểm tra nếu chưa có Database [QuanLyVeTau] thì mới tạo
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'QuanLyVeTau')
BEGIN
    CREATE DATABASE [QuanLyVeTau];
    PRINT N'Đã tạo mới Database QuanLyVeTau.';
END
ELSE
BEGIN
    PRINT N'Database QuanLyVeTau đã tồn tại, bỏ qua bước tạo mới.';
END
GO

USE [QuanLyVeTau]
GO

CREATE TABLE [dbo].[CaLamViec](
	[MaCaLamViec] [int] IDENTITY(1,1) NOT NULL,
	[ThoiGianBatDau] [datetime] NULL,
	[ThoiGianKetThuc] [datetime] NULL,
 CONSTRAINT [PK_CaLamViec] PRIMARY KEY CLUSTERED 
(
	[MaCaLamViec] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ChiTietHoaDon]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ChiTietHoaDon](
	[MaHD] [nvarchar](50) NOT NULL,
	[MaVe] [nvarchar](20) NOT NULL,
	[SoLuong] [int] NOT NULL,
	[DonGia] [decimal](12, 0) NOT NULL,
 CONSTRAINT [PK_ChiTietHoaDon] PRIMARY KEY CLUSTERED 
(
	[MaHD] ASC,
	[MaVe] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ChoDat]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ChoDat](
	[MaCho] [nvarchar](20) NOT NULL,
	[MaToa] [nvarchar](10) NOT NULL,
	[SoCho] [nvarchar](10) NULL,
	[Khoang] [int] NULL,
	[Tang] [int] NULL,
 CONSTRAINT [PK_ChoDat] PRIMARY KEY CLUSTERED 
(
	[MaCho] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ChuyenTau]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ChuyenTau](
	[MaChuyenTau] [nvarchar](50) NOT NULL,
	[MaTuyen] [nvarchar](10) NULL,
	[MaTau] [nvarchar](10) NOT NULL,
	[MaNV] [nvarchar](50) NULL,
	[GaDi] [nvarchar](10) NOT NULL,
	[GaDen] [nvarchar](10) NOT NULL,
	[NgayKhoiHanh] [date] NOT NULL,
	[GioKhoiHanh] [time](7) NULL,
	[NgayDenDuKien] [date] NOT NULL,
	[GioDenDuKien] [time](7) NULL,
	[TrangThai] [nvarchar](50) NULL,
 CONSTRAINT [PK__ChuyenTa__8EB73BB04AA7B38B] PRIMARY KEY CLUSTERED 
(
	[MaChuyenTau] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Ga]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Ga](
	[MaGa] [nvarchar](10) NOT NULL,
	[TenGa] [nvarchar](100) NOT NULL,
	[DiaChi] [nvarchar](255) NULL,
 CONSTRAINT [PK_Ga] PRIMARY KEY CLUSTERED 
(
	[MaGa] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[GA_TRONG_TUYEN]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[GA_TRONG_TUYEN](
	[MaTuyen] [nvarchar](10) NOT NULL,
	[MaGa] [nvarchar](10) NOT NULL,
	[ThuTuGa] [int] NOT NULL,
	[KhoangCachTichLuy] [int] NOT NULL,
	[ThoiGianDung] [int] NULL,
	[ThoiGianDiChuyenToiGaTiepTheo] [int] NULL,
 CONSTRAINT [PK_GaTrongTuyen] PRIMARY KEY CLUSTERED 
(
	[MaTuyen] ASC,
	[MaGa] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HoaDon]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HoaDon](
	[MaHD] [nvarchar](50) NOT NULL,
	[MaKhachHang] [nvarchar](12) NOT NULL,
	[MaNVLap] [nvarchar](50) NOT NULL,
	[MaKM] [varchar](50) NULL,
	[TongCong] [decimal](18, 0) NULL,
	[NgayLap] [datetime] NULL,
	[PhuongThuc] [nvarchar](100) NULL,
	[LoaiHoaDon] [nvarchar](100) NULL,
	[TongTien] [decimal](12, 0) NULL,
	[MaHD_Goc] [nvarchar](50) NULL,
 CONSTRAINT [PK_HoaDon] PRIMARY KEY CLUSTERED 
(
	[MaHD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[KhachHang]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[KhachHang](
	[MaKhachHang] [nvarchar](12) NOT NULL,
	[HoTen] [nvarchar](100) NOT NULL,
	[CCCD] [char](12) NULL,
	[Tuoi] [int] NULL,
	[SoDienThoai] [varchar](15) NULL,
	[GioiTinh] [nvarchar](50) NULL,
	[NgaySinh] [date] NULL,
 CONSTRAINT [PK_KhachHang] PRIMARY KEY CLUSTERED 
(
	[MaKhachHang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[KhuyenMai]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[KhuyenMai](
	[MaKM] [varchar](50) NOT NULL,
	[TenKM] [nvarchar](255) NOT NULL,
	[LoaiKM] [varchar](50) NOT NULL,
	[GiaTriGiam] [decimal](18, 2) NOT NULL,
	[DKApDung] [varchar](50) NOT NULL,
	[GiaTriDK] [decimal](18, 2) NULL,
	[NgayBD] [datetime] NOT NULL,
	[NgayKT] [datetime] NOT NULL,
	[TrangThai] [varchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaKM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[LoaiToa]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[LoaiToa](
	[MaLoaiToa] [nvarchar](20) NOT NULL,
	[TenLoaiToa] [nvarchar](100) NOT NULL,
	[HeSo] [float] NOT NULL,
 CONSTRAINT [PK_LoaiChoDat] PRIMARY KEY CLUSTERED 
(
	[MaLoaiToa] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[LoaiVe]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[LoaiVe](
	[MaLoaiVe] [nvarchar](10) NOT NULL,
	[TenLoaiVe] [nvarchar](50) NOT NULL,
	[MucGiamGia] [float] NULL,
	[TuoiMin] [int] NULL,
	[TuoiMax] [int] NULL,
 CONSTRAINT [PK_LoaiVe] PRIMARY KEY CLUSTERED 
(
	[MaLoaiVe] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[NhanVien]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[NhanVien](
	[MaNV] [nvarchar](50) NOT NULL,
	[HoTen] [nvarchar](100) NOT NULL,
	[SoCCCD] [nvarchar](100) NULL,
	[NgaySinh] [date] NULL,
	[Email] [nvarchar](100) NULL,
	[SDT] [nvarchar](100) NULL,
	[GioiTinh] [nvarchar](50) NULL,
	[DiaChi] [nvarchar](255) NOT NULL,
	[NgayVaoLam] [date] NULL,
	[ChucVu] [nvarchar](100) NULL,
	[CaLamViec] [int] NULL,
 CONSTRAINT [PK_NhanVien] PRIMARY KEY CLUSTERED 
(
	[MaNV] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[TaiKhoan]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TaiKhoan](
	[TenDangNhap] [nvarchar](50) NOT NULL,
	[MaNV] [nvarchar](50) NOT NULL,
	[MatKhau] [nvarchar](255) NOT NULL,
	[NgayTao] [datetime] NULL,
	[TrangThai] [nvarchar](50) NULL,
 CONSTRAINT [PK_TaiKhoan] PRIMARY KEY CLUSTERED 
(
	[TenDangNhap] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Tau]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Tau](
	[SoHieu] [nvarchar](10) NOT NULL,
	[TrangThai] [nvarchar](50) NULL,
 CONSTRAINT [PK_Tau] PRIMARY KEY CLUSTERED 
(
	[SoHieu] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Toa]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Toa](
	[MaToa] [nvarchar](10) NOT NULL,
	[SoHieuTau] [nvarchar](10) NOT NULL,
	[maLoaiToa] [nvarchar](20) NOT NULL,
 CONSTRAINT [PK_Toa] PRIMARY KEY CLUSTERED 
(
	[MaToa] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[TUYEN]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TUYEN](
	[MaTuyen] [nvarchar](10) NOT NULL,
	[TenTuyen] [nvarchar](100) NOT NULL,
	[GaDau] [nchar](10) NOT NULL,
	[GaCuoi] [nchar](10) NOT NULL,
	[DonGiaKm] [int] NOT NULL,
 CONSTRAINT [PK_Tuyen] PRIMARY KEY CLUSTERED 
(
	[MaTuyen] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Ve]    Script Date: 23/12/2025 1:17:08 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Ve](
	[MaVe] [nvarchar](20) NOT NULL,
	[MaChuyenTau] [nvarchar](50) NOT NULL,
	[MaChoDat] [nvarchar](20) NOT NULL,
	[MaNV] [nchar](10) NULL,
	[MaKhachHang] [nvarchar](12) NULL,
	[MaLoaiVe] [nvarchar](10) NOT NULL,
	[GiaVe] [decimal](18, 0) NULL,
	[TrangThai] [nvarchar](50) NULL,
	[ThanhTien] [decimal](18, 0) NULL,
 CONSTRAINT [PK_Ve] PRIMARY KEY CLUSTERED 
(
	[MaVe] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[CaLamViec] ON 

INSERT [dbo].[CaLamViec] ([MaCaLamViec], [ThoiGianBatDau], [ThoiGianKetThuc]) VALUES (1, CAST(N'2025-10-17T08:00:00.000' AS DateTime), CAST(N'2025-10-17T12:00:00.000' AS DateTime))
INSERT [dbo].[CaLamViec] ([MaCaLamViec], [ThoiGianBatDau], [ThoiGianKetThuc]) VALUES (2, CAST(N'2025-10-18T13:00:00.000' AS DateTime), CAST(N'2025-10-18T17:00:00.000' AS DateTime))
INSERT [dbo].[CaLamViec] ([MaCaLamViec], [ThoiGianBatDau], [ThoiGianKetThuc]) VALUES (3, CAST(N'2025-10-17T14:00:00.000' AS DateTime), CAST(N'2025-10-17T18:00:00.000' AS DateTime))
SET IDENTITY_INSERT [dbo].[CaLamViec] OFF
GO
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0121122500010001', N'VE012112250001', 1, CAST(688000 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0121122500010002', N'VE012112250002', 1, CAST(688000 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010001', N'VE012112250001', -1, CAST(688000 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010001', N'VE012212250001', 1, CAST(1052000 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010002', N'VE012212250002', 1, CAST(605440 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010003', N'VE012212250003', 1, CAST(605440 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010004', N'VE012212250004', 1, CAST(605440 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010004', N'VE012212250005', 1, CAST(484352 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010004', N'VE012212250006', 1, CAST(605440 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010004', N'VE012212250007', 1, CAST(454080 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010004', N'VE012212250008', 1, CAST(484352 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010004', N'VE012212250009', 1, CAST(544896 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010005', N'VE012212250012', 1, CAST(1032000 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD0122122500010005', N'VE012212250013', 1, CAST(619200 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD01221225N0010001', N'VE012212250010', 1, CAST(605440 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD01221225N0010002', N'VE012212250011', 1, CAST(605440 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD01231225N0010001', N'VE012312250001', 1, CAST(1487200 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD01231225N0010002', N'VE012312250002', 1, CAST(572000 AS Decimal(12, 0)))
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong], [DonGia]) VALUES (N'HD01231225N0010003', N'VE012312250003', 1, CAST(915200 AS Decimal(12, 0)))
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C01', N'SE1-1', N'01', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C02', N'SE1-1', N'02', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C03', N'SE1-1', N'03', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C04', N'SE1-1', N'04', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C05', N'SE1-1', N'05', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C06', N'SE1-1', N'06', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C07', N'SE1-1', N'07', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C08', N'SE1-1', N'08', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C09', N'SE1-1', N'09', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C10', N'SE1-1', N'10', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C11', N'SE1-1', N'11', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C12', N'SE1-1', N'12', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C13', N'SE1-1', N'13', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C14', N'SE1-1', N'14', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C15', N'SE1-1', N'15', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C16', N'SE1-1', N'16', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C17', N'SE1-1', N'17', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C18', N'SE1-1', N'18', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C19', N'SE1-1', N'19', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C20', N'SE1-1', N'20', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C21', N'SE1-1', N'21', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C22', N'SE1-1', N'22', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C23', N'SE1-1', N'23', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C24', N'SE1-1', N'24', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C25', N'SE1-1', N'25', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C26', N'SE1-1', N'26', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C27', N'SE1-1', N'27', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C28', N'SE1-1', N'28', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C29', N'SE1-1', N'29', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C30', N'SE1-1', N'30', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C31', N'SE1-1', N'31', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C32', N'SE1-1', N'32', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C33', N'SE1-1', N'33', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C34', N'SE1-1', N'34', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C35', N'SE1-1', N'35', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C36', N'SE1-1', N'36', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C37', N'SE1-1', N'37', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C38', N'SE1-1', N'38', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C39', N'SE1-1', N'39', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C40', N'SE1-1', N'40', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C41', N'SE1-1', N'41', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C42', N'SE1-1', N'42', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C43', N'SE1-1', N'43', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C44', N'SE1-1', N'44', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C45', N'SE1-1', N'45', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C46', N'SE1-1', N'46', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C47', N'SE1-1', N'47', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C48', N'SE1-1', N'48', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C49', N'SE1-1', N'49', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C50', N'SE1-1', N'50', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C51', N'SE1-1', N'51', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C52', N'SE1-1', N'52', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C53', N'SE1-1', N'53', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C54', N'SE1-1', N'54', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C55', N'SE1-1', N'55', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C56', N'SE1-1', N'56', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C57', N'SE1-1', N'57', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C58', N'SE1-1', N'58', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C59', N'SE1-1', N'59', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C60', N'SE1-1', N'60', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C61', N'SE1-1', N'61', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C62', N'SE1-1', N'62', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C63', N'SE1-1', N'63', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-1-C64', N'SE1-1', N'64', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C01', N'SE1-2', N'01', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C02', N'SE1-2', N'02', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C03', N'SE1-2', N'03', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C04', N'SE1-2', N'04', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C05', N'SE1-2', N'05', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C06', N'SE1-2', N'06', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C07', N'SE1-2', N'07', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C08', N'SE1-2', N'08', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C09', N'SE1-2', N'09', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C10', N'SE1-2', N'10', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C11', N'SE1-2', N'11', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C12', N'SE1-2', N'12', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C13', N'SE1-2', N'13', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C14', N'SE1-2', N'14', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C15', N'SE1-2', N'15', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C16', N'SE1-2', N'16', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C17', N'SE1-2', N'17', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C18', N'SE1-2', N'18', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C19', N'SE1-2', N'19', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C20', N'SE1-2', N'20', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C21', N'SE1-2', N'21', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C22', N'SE1-2', N'22', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C23', N'SE1-2', N'23', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C24', N'SE1-2', N'24', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C25', N'SE1-2', N'25', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C26', N'SE1-2', N'26', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C27', N'SE1-2', N'27', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C28', N'SE1-2', N'28', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C29', N'SE1-2', N'29', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C30', N'SE1-2', N'30', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C31', N'SE1-2', N'31', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C32', N'SE1-2', N'32', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C33', N'SE1-2', N'33', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C34', N'SE1-2', N'34', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C35', N'SE1-2', N'35', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C36', N'SE1-2', N'36', NULL, NULL)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C37', N'SE1-2', N'37', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C38', N'SE1-2', N'38', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C39', N'SE1-2', N'39', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C40', N'SE1-2', N'40', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C41', N'SE1-2', N'41', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C42', N'SE1-2', N'42', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C43', N'SE1-2', N'43', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C44', N'SE1-2', N'44', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C45', N'SE1-2', N'45', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C46', N'SE1-2', N'46', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C47', N'SE1-2', N'47', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C48', N'SE1-2', N'48', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C49', N'SE1-2', N'49', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C50', N'SE1-2', N'50', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C51', N'SE1-2', N'51', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C52', N'SE1-2', N'52', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C53', N'SE1-2', N'53', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C54', N'SE1-2', N'54', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C55', N'SE1-2', N'55', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C56', N'SE1-2', N'56', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C57', N'SE1-2', N'57', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C58', N'SE1-2', N'58', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C59', N'SE1-2', N'59', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C60', N'SE1-2', N'60', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C61', N'SE1-2', N'61', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C62', N'SE1-2', N'62', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C63', N'SE1-2', N'63', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-2-C64', N'SE1-2', N'64', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C01', N'SE1-3', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C02', N'SE1-3', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C03', N'SE1-3', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C04', N'SE1-3', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C05', N'SE1-3', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C06', N'SE1-3', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C07', N'SE1-3', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C08', N'SE1-3', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C09', N'SE1-3', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C10', N'SE1-3', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C11', N'SE1-3', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C12', N'SE1-3', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C13', N'SE1-3', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C14', N'SE1-3', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C15', N'SE1-3', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C16', N'SE1-3', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C17', N'SE1-3', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C18', N'SE1-3', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C19', N'SE1-3', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C20', N'SE1-3', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C21', N'SE1-3', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C22', N'SE1-3', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C23', N'SE1-3', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C24', N'SE1-3', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C25', N'SE1-3', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C26', N'SE1-3', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C27', N'SE1-3', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C28', N'SE1-3', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C29', N'SE1-3', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C30', N'SE1-3', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C31', N'SE1-3', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C32', N'SE1-3', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C33', N'SE1-3', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C34', N'SE1-3', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C35', N'SE1-3', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C36', N'SE1-3', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C37', N'SE1-3', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C38', N'SE1-3', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C39', N'SE1-3', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C40', N'SE1-3', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C41', N'SE1-3', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-3-C42', N'SE1-3', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C01', N'SE1-4', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C02', N'SE1-4', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C03', N'SE1-4', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C04', N'SE1-4', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C05', N'SE1-4', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C06', N'SE1-4', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C07', N'SE1-4', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C08', N'SE1-4', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C09', N'SE1-4', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C10', N'SE1-4', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C11', N'SE1-4', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C12', N'SE1-4', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C13', N'SE1-4', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C14', N'SE1-4', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C15', N'SE1-4', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C16', N'SE1-4', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C17', N'SE1-4', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C18', N'SE1-4', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C19', N'SE1-4', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C20', N'SE1-4', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C21', N'SE1-4', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C22', N'SE1-4', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C23', N'SE1-4', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C24', N'SE1-4', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C25', N'SE1-4', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C26', N'SE1-4', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C27', N'SE1-4', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C28', N'SE1-4', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C29', N'SE1-4', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C30', N'SE1-4', N'30', 5, 3)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C31', N'SE1-4', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C32', N'SE1-4', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C33', N'SE1-4', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C34', N'SE1-4', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C35', N'SE1-4', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C36', N'SE1-4', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C37', N'SE1-4', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C38', N'SE1-4', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C39', N'SE1-4', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C40', N'SE1-4', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C41', N'SE1-4', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-4-C42', N'SE1-4', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C01', N'SE1-5', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C02', N'SE1-5', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C03', N'SE1-5', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C04', N'SE1-5', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C05', N'SE1-5', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C06', N'SE1-5', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C07', N'SE1-5', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C08', N'SE1-5', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C09', N'SE1-5', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C10', N'SE1-5', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C11', N'SE1-5', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C12', N'SE1-5', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C13', N'SE1-5', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C14', N'SE1-5', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C15', N'SE1-5', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C16', N'SE1-5', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C17', N'SE1-5', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C18', N'SE1-5', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C19', N'SE1-5', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C20', N'SE1-5', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C21', N'SE1-5', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C22', N'SE1-5', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C23', N'SE1-5', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C24', N'SE1-5', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C25', N'SE1-5', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C26', N'SE1-5', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C27', N'SE1-5', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C28', N'SE1-5', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C29', N'SE1-5', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C30', N'SE1-5', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C31', N'SE1-5', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C32', N'SE1-5', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C33', N'SE1-5', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C34', N'SE1-5', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C35', N'SE1-5', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C36', N'SE1-5', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C37', N'SE1-5', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C38', N'SE1-5', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C39', N'SE1-5', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C40', N'SE1-5', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C41', N'SE1-5', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-5-C42', N'SE1-5', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C01', N'SE1-6', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C02', N'SE1-6', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C03', N'SE1-6', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C04', N'SE1-6', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C05', N'SE1-6', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C06', N'SE1-6', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C07', N'SE1-6', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C08', N'SE1-6', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C09', N'SE1-6', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C10', N'SE1-6', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C11', N'SE1-6', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C12', N'SE1-6', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C13', N'SE1-6', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C14', N'SE1-6', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C15', N'SE1-6', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C16', N'SE1-6', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C17', N'SE1-6', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C18', N'SE1-6', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C19', N'SE1-6', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C20', N'SE1-6', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C21', N'SE1-6', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C22', N'SE1-6', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C23', N'SE1-6', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C24', N'SE1-6', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C25', N'SE1-6', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C26', N'SE1-6', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C27', N'SE1-6', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C28', N'SE1-6', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C29', N'SE1-6', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C30', N'SE1-6', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C31', N'SE1-6', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C32', N'SE1-6', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C33', N'SE1-6', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C34', N'SE1-6', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C35', N'SE1-6', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C36', N'SE1-6', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C37', N'SE1-6', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C38', N'SE1-6', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C39', N'SE1-6', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C40', N'SE1-6', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C41', N'SE1-6', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE1-6-C42', N'SE1-6', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C01', N'SE2-1', N'01', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C02', N'SE2-1', N'02', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C03', N'SE2-1', N'03', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C04', N'SE2-1', N'04', NULL, NULL)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C05', N'SE2-1', N'05', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C06', N'SE2-1', N'06', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C07', N'SE2-1', N'07', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C08', N'SE2-1', N'08', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C09', N'SE2-1', N'09', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C10', N'SE2-1', N'10', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C11', N'SE2-1', N'11', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C12', N'SE2-1', N'12', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C13', N'SE2-1', N'13', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C14', N'SE2-1', N'14', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C15', N'SE2-1', N'15', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C16', N'SE2-1', N'16', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C17', N'SE2-1', N'17', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C18', N'SE2-1', N'18', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C19', N'SE2-1', N'19', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C20', N'SE2-1', N'20', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C21', N'SE2-1', N'21', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C22', N'SE2-1', N'22', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C23', N'SE2-1', N'23', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C24', N'SE2-1', N'24', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C25', N'SE2-1', N'25', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C26', N'SE2-1', N'26', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C27', N'SE2-1', N'27', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C28', N'SE2-1', N'28', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C29', N'SE2-1', N'29', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C30', N'SE2-1', N'30', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C31', N'SE2-1', N'31', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C32', N'SE2-1', N'32', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C33', N'SE2-1', N'33', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C34', N'SE2-1', N'34', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C35', N'SE2-1', N'35', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C36', N'SE2-1', N'36', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C37', N'SE2-1', N'37', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C38', N'SE2-1', N'38', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C39', N'SE2-1', N'39', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C40', N'SE2-1', N'40', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C41', N'SE2-1', N'41', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C42', N'SE2-1', N'42', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C43', N'SE2-1', N'43', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C44', N'SE2-1', N'44', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C45', N'SE2-1', N'45', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C46', N'SE2-1', N'46', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C47', N'SE2-1', N'47', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C48', N'SE2-1', N'48', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C49', N'SE2-1', N'49', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C50', N'SE2-1', N'50', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C51', N'SE2-1', N'51', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C52', N'SE2-1', N'52', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C53', N'SE2-1', N'53', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C54', N'SE2-1', N'54', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C55', N'SE2-1', N'55', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C56', N'SE2-1', N'56', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C57', N'SE2-1', N'57', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C58', N'SE2-1', N'58', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C59', N'SE2-1', N'59', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C60', N'SE2-1', N'60', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C61', N'SE2-1', N'61', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C62', N'SE2-1', N'62', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C63', N'SE2-1', N'63', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-1-C64', N'SE2-1', N'64', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C01', N'SE2-2', N'01', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C02', N'SE2-2', N'02', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C03', N'SE2-2', N'03', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C04', N'SE2-2', N'04', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C05', N'SE2-2', N'05', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C06', N'SE2-2', N'06', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C07', N'SE2-2', N'07', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C08', N'SE2-2', N'08', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C09', N'SE2-2', N'09', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C10', N'SE2-2', N'10', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C11', N'SE2-2', N'11', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C12', N'SE2-2', N'12', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C13', N'SE2-2', N'13', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C14', N'SE2-2', N'14', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C15', N'SE2-2', N'15', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C16', N'SE2-2', N'16', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C17', N'SE2-2', N'17', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C18', N'SE2-2', N'18', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C19', N'SE2-2', N'19', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C20', N'SE2-2', N'20', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C21', N'SE2-2', N'21', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C22', N'SE2-2', N'22', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C23', N'SE2-2', N'23', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C24', N'SE2-2', N'24', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C25', N'SE2-2', N'25', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C26', N'SE2-2', N'26', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C27', N'SE2-2', N'27', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C28', N'SE2-2', N'28', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C29', N'SE2-2', N'29', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C30', N'SE2-2', N'30', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C31', N'SE2-2', N'31', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C32', N'SE2-2', N'32', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C33', N'SE2-2', N'33', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C34', N'SE2-2', N'34', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C35', N'SE2-2', N'35', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C36', N'SE2-2', N'36', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C37', N'SE2-2', N'37', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C38', N'SE2-2', N'38', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C39', N'SE2-2', N'39', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C40', N'SE2-2', N'40', NULL, NULL)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C41', N'SE2-2', N'41', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C42', N'SE2-2', N'42', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C43', N'SE2-2', N'43', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C44', N'SE2-2', N'44', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C45', N'SE2-2', N'45', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C46', N'SE2-2', N'46', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C47', N'SE2-2', N'47', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C48', N'SE2-2', N'48', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C49', N'SE2-2', N'49', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C50', N'SE2-2', N'50', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C51', N'SE2-2', N'51', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C52', N'SE2-2', N'52', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C53', N'SE2-2', N'53', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C54', N'SE2-2', N'54', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C55', N'SE2-2', N'55', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C56', N'SE2-2', N'56', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C57', N'SE2-2', N'57', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C58', N'SE2-2', N'58', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C59', N'SE2-2', N'59', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C60', N'SE2-2', N'60', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C61', N'SE2-2', N'61', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C62', N'SE2-2', N'62', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C63', N'SE2-2', N'63', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-2-C64', N'SE2-2', N'64', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C01', N'SE2-3', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C02', N'SE2-3', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C03', N'SE2-3', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C04', N'SE2-3', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C05', N'SE2-3', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C06', N'SE2-3', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C07', N'SE2-3', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C08', N'SE2-3', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C09', N'SE2-3', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C10', N'SE2-3', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C11', N'SE2-3', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C12', N'SE2-3', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C13', N'SE2-3', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C14', N'SE2-3', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C15', N'SE2-3', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C16', N'SE2-3', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C17', N'SE2-3', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C18', N'SE2-3', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C19', N'SE2-3', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C20', N'SE2-3', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C21', N'SE2-3', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C22', N'SE2-3', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C23', N'SE2-3', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C24', N'SE2-3', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C25', N'SE2-3', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C26', N'SE2-3', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C27', N'SE2-3', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C28', N'SE2-3', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C29', N'SE2-3', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C30', N'SE2-3', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C31', N'SE2-3', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C32', N'SE2-3', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C33', N'SE2-3', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C34', N'SE2-3', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C35', N'SE2-3', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C36', N'SE2-3', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C37', N'SE2-3', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C38', N'SE2-3', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C39', N'SE2-3', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C40', N'SE2-3', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C41', N'SE2-3', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-3-C42', N'SE2-3', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C01', N'SE2-4', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C02', N'SE2-4', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C03', N'SE2-4', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C04', N'SE2-4', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C05', N'SE2-4', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C06', N'SE2-4', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C07', N'SE2-4', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C08', N'SE2-4', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C09', N'SE2-4', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C10', N'SE2-4', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C11', N'SE2-4', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C12', N'SE2-4', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C13', N'SE2-4', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C14', N'SE2-4', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C15', N'SE2-4', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C16', N'SE2-4', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C17', N'SE2-4', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C18', N'SE2-4', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C19', N'SE2-4', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C20', N'SE2-4', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C21', N'SE2-4', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C22', N'SE2-4', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C23', N'SE2-4', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C24', N'SE2-4', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C25', N'SE2-4', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C26', N'SE2-4', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C27', N'SE2-4', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C28', N'SE2-4', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C29', N'SE2-4', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C30', N'SE2-4', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C31', N'SE2-4', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C32', N'SE2-4', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C33', N'SE2-4', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C34', N'SE2-4', N'34', 6, 2)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C35', N'SE2-4', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C36', N'SE2-4', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C37', N'SE2-4', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C38', N'SE2-4', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C39', N'SE2-4', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C40', N'SE2-4', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C41', N'SE2-4', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-4-C42', N'SE2-4', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C01', N'SE2-5', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C02', N'SE2-5', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C03', N'SE2-5', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C04', N'SE2-5', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C05', N'SE2-5', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C06', N'SE2-5', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C07', N'SE2-5', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C08', N'SE2-5', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C09', N'SE2-5', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C10', N'SE2-5', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C11', N'SE2-5', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C12', N'SE2-5', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C13', N'SE2-5', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C14', N'SE2-5', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C15', N'SE2-5', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C16', N'SE2-5', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C17', N'SE2-5', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C18', N'SE2-5', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C19', N'SE2-5', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C20', N'SE2-5', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C21', N'SE2-5', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C22', N'SE2-5', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C23', N'SE2-5', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C24', N'SE2-5', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C25', N'SE2-5', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C26', N'SE2-5', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C27', N'SE2-5', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C28', N'SE2-5', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C29', N'SE2-5', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C30', N'SE2-5', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C31', N'SE2-5', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C32', N'SE2-5', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C33', N'SE2-5', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C34', N'SE2-5', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C35', N'SE2-5', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C36', N'SE2-5', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C37', N'SE2-5', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C38', N'SE2-5', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C39', N'SE2-5', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C40', N'SE2-5', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C41', N'SE2-5', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-5-C42', N'SE2-5', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C01', N'SE2-6', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C02', N'SE2-6', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C03', N'SE2-6', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C04', N'SE2-6', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C05', N'SE2-6', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C06', N'SE2-6', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C07', N'SE2-6', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C08', N'SE2-6', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C09', N'SE2-6', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C10', N'SE2-6', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C11', N'SE2-6', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C12', N'SE2-6', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C13', N'SE2-6', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C14', N'SE2-6', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C15', N'SE2-6', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C16', N'SE2-6', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C17', N'SE2-6', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C18', N'SE2-6', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C19', N'SE2-6', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C20', N'SE2-6', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C21', N'SE2-6', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C22', N'SE2-6', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C23', N'SE2-6', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C24', N'SE2-6', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C25', N'SE2-6', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C26', N'SE2-6', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C27', N'SE2-6', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C28', N'SE2-6', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C29', N'SE2-6', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C30', N'SE2-6', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C31', N'SE2-6', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C32', N'SE2-6', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C33', N'SE2-6', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C34', N'SE2-6', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C35', N'SE2-6', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C36', N'SE2-6', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C37', N'SE2-6', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C38', N'SE2-6', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C39', N'SE2-6', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C40', N'SE2-6', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C41', N'SE2-6', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SE2-6-C42', N'SE2-6', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C01', N'SPT2-1', N'01', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C02', N'SPT2-1', N'02', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C03', N'SPT2-1', N'03', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C04', N'SPT2-1', N'04', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C05', N'SPT2-1', N'05', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C06', N'SPT2-1', N'06', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C07', N'SPT2-1', N'07', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C08', N'SPT2-1', N'08', NULL, NULL)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C09', N'SPT2-1', N'09', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C10', N'SPT2-1', N'10', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C11', N'SPT2-1', N'11', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C12', N'SPT2-1', N'12', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C13', N'SPT2-1', N'13', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C14', N'SPT2-1', N'14', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C15', N'SPT2-1', N'15', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C16', N'SPT2-1', N'16', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C17', N'SPT2-1', N'17', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C18', N'SPT2-1', N'18', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C19', N'SPT2-1', N'19', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C20', N'SPT2-1', N'20', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C21', N'SPT2-1', N'21', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C22', N'SPT2-1', N'22', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C23', N'SPT2-1', N'23', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C24', N'SPT2-1', N'24', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C25', N'SPT2-1', N'25', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C26', N'SPT2-1', N'26', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C27', N'SPT2-1', N'27', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C28', N'SPT2-1', N'28', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C29', N'SPT2-1', N'29', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C30', N'SPT2-1', N'30', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C31', N'SPT2-1', N'31', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C32', N'SPT2-1', N'32', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C33', N'SPT2-1', N'33', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C34', N'SPT2-1', N'34', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C35', N'SPT2-1', N'35', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C36', N'SPT2-1', N'36', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C37', N'SPT2-1', N'37', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C38', N'SPT2-1', N'38', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C39', N'SPT2-1', N'39', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C40', N'SPT2-1', N'40', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C41', N'SPT2-1', N'41', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C42', N'SPT2-1', N'42', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C43', N'SPT2-1', N'43', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C44', N'SPT2-1', N'44', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C45', N'SPT2-1', N'45', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C46', N'SPT2-1', N'46', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C47', N'SPT2-1', N'47', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C48', N'SPT2-1', N'48', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C49', N'SPT2-1', N'49', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C50', N'SPT2-1', N'50', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C51', N'SPT2-1', N'51', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C52', N'SPT2-1', N'52', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C53', N'SPT2-1', N'53', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C54', N'SPT2-1', N'54', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C55', N'SPT2-1', N'55', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C56', N'SPT2-1', N'56', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C57', N'SPT2-1', N'57', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C58', N'SPT2-1', N'58', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C59', N'SPT2-1', N'59', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C60', N'SPT2-1', N'60', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C61', N'SPT2-1', N'61', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C62', N'SPT2-1', N'62', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C63', N'SPT2-1', N'63', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-1-C64', N'SPT2-1', N'64', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C01', N'SPT2-2', N'01', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C02', N'SPT2-2', N'02', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C03', N'SPT2-2', N'03', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C04', N'SPT2-2', N'04', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C05', N'SPT2-2', N'05', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C06', N'SPT2-2', N'06', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C07', N'SPT2-2', N'07', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C08', N'SPT2-2', N'08', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C09', N'SPT2-2', N'09', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C10', N'SPT2-2', N'10', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C11', N'SPT2-2', N'11', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C12', N'SPT2-2', N'12', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C13', N'SPT2-2', N'13', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C14', N'SPT2-2', N'14', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C15', N'SPT2-2', N'15', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C16', N'SPT2-2', N'16', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C17', N'SPT2-2', N'17', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C18', N'SPT2-2', N'18', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C19', N'SPT2-2', N'19', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C20', N'SPT2-2', N'20', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C21', N'SPT2-2', N'21', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C22', N'SPT2-2', N'22', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C23', N'SPT2-2', N'23', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C24', N'SPT2-2', N'24', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C25', N'SPT2-2', N'25', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C26', N'SPT2-2', N'26', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C27', N'SPT2-2', N'27', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C28', N'SPT2-2', N'28', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C29', N'SPT2-2', N'29', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C30', N'SPT2-2', N'30', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C31', N'SPT2-2', N'31', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C32', N'SPT2-2', N'32', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C33', N'SPT2-2', N'33', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C34', N'SPT2-2', N'34', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C35', N'SPT2-2', N'35', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C36', N'SPT2-2', N'36', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C37', N'SPT2-2', N'37', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C38', N'SPT2-2', N'38', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C39', N'SPT2-2', N'39', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C40', N'SPT2-2', N'40', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C41', N'SPT2-2', N'41', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C42', N'SPT2-2', N'42', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C43', N'SPT2-2', N'43', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C44', N'SPT2-2', N'44', NULL, NULL)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C45', N'SPT2-2', N'45', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C46', N'SPT2-2', N'46', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C47', N'SPT2-2', N'47', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C48', N'SPT2-2', N'48', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C49', N'SPT2-2', N'49', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C50', N'SPT2-2', N'50', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C51', N'SPT2-2', N'51', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C52', N'SPT2-2', N'52', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C53', N'SPT2-2', N'53', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C54', N'SPT2-2', N'54', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C55', N'SPT2-2', N'55', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C56', N'SPT2-2', N'56', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C57', N'SPT2-2', N'57', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C58', N'SPT2-2', N'58', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C59', N'SPT2-2', N'59', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C60', N'SPT2-2', N'60', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C61', N'SPT2-2', N'61', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C62', N'SPT2-2', N'62', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C63', N'SPT2-2', N'63', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-2-C64', N'SPT2-2', N'64', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C01', N'SPT2-3', N'01', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C02', N'SPT2-3', N'02', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C03', N'SPT2-3', N'03', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C04', N'SPT2-3', N'04', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C05', N'SPT2-3', N'05', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C06', N'SPT2-3', N'06', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C07', N'SPT2-3', N'07', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C08', N'SPT2-3', N'08', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C09', N'SPT2-3', N'09', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C10', N'SPT2-3', N'10', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C11', N'SPT2-3', N'11', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C12', N'SPT2-3', N'12', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C13', N'SPT2-3', N'13', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C14', N'SPT2-3', N'14', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C15', N'SPT2-3', N'15', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C16', N'SPT2-3', N'16', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C17', N'SPT2-3', N'17', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C18', N'SPT2-3', N'18', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C19', N'SPT2-3', N'19', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C20', N'SPT2-3', N'20', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C21', N'SPT2-3', N'21', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C22', N'SPT2-3', N'22', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C23', N'SPT2-3', N'23', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C24', N'SPT2-3', N'24', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C25', N'SPT2-3', N'25', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C26', N'SPT2-3', N'26', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C27', N'SPT2-3', N'27', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C28', N'SPT2-3', N'28', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C29', N'SPT2-3', N'29', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C30', N'SPT2-3', N'30', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C31', N'SPT2-3', N'31', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C32', N'SPT2-3', N'32', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C33', N'SPT2-3', N'33', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C34', N'SPT2-3', N'34', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C35', N'SPT2-3', N'35', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C36', N'SPT2-3', N'36', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C37', N'SPT2-3', N'37', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C38', N'SPT2-3', N'38', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C39', N'SPT2-3', N'39', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C40', N'SPT2-3', N'40', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C41', N'SPT2-3', N'41', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C42', N'SPT2-3', N'42', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C43', N'SPT2-3', N'43', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C44', N'SPT2-3', N'44', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C45', N'SPT2-3', N'45', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C46', N'SPT2-3', N'46', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C47', N'SPT2-3', N'47', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C48', N'SPT2-3', N'48', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C49', N'SPT2-3', N'49', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C50', N'SPT2-3', N'50', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C51', N'SPT2-3', N'51', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C52', N'SPT2-3', N'52', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C53', N'SPT2-3', N'53', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C54', N'SPT2-3', N'54', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C55', N'SPT2-3', N'55', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C56', N'SPT2-3', N'56', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C57', N'SPT2-3', N'57', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C58', N'SPT2-3', N'58', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C59', N'SPT2-3', N'59', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C60', N'SPT2-3', N'60', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C61', N'SPT2-3', N'61', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C62', N'SPT2-3', N'62', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C63', N'SPT2-3', N'63', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-3-C64', N'SPT2-3', N'64', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C01', N'SPT2-4', N'01', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C02', N'SPT2-4', N'02', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C03', N'SPT2-4', N'03', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C04', N'SPT2-4', N'04', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C05', N'SPT2-4', N'05', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C06', N'SPT2-4', N'06', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C07', N'SPT2-4', N'07', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C08', N'SPT2-4', N'08', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C09', N'SPT2-4', N'09', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C10', N'SPT2-4', N'10', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C11', N'SPT2-4', N'11', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C12', N'SPT2-4', N'12', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C13', N'SPT2-4', N'13', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C14', N'SPT2-4', N'14', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C15', N'SPT2-4', N'15', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C16', N'SPT2-4', N'16', NULL, NULL)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C17', N'SPT2-4', N'17', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C18', N'SPT2-4', N'18', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C19', N'SPT2-4', N'19', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C20', N'SPT2-4', N'20', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C21', N'SPT2-4', N'21', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C22', N'SPT2-4', N'22', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C23', N'SPT2-4', N'23', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C24', N'SPT2-4', N'24', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C25', N'SPT2-4', N'25', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C26', N'SPT2-4', N'26', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C27', N'SPT2-4', N'27', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C28', N'SPT2-4', N'28', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C29', N'SPT2-4', N'29', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C30', N'SPT2-4', N'30', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C31', N'SPT2-4', N'31', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C32', N'SPT2-4', N'32', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C33', N'SPT2-4', N'33', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C34', N'SPT2-4', N'34', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C35', N'SPT2-4', N'35', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C36', N'SPT2-4', N'36', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C37', N'SPT2-4', N'37', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C38', N'SPT2-4', N'38', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C39', N'SPT2-4', N'39', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C40', N'SPT2-4', N'40', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C41', N'SPT2-4', N'41', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C42', N'SPT2-4', N'42', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C43', N'SPT2-4', N'43', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C44', N'SPT2-4', N'44', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C45', N'SPT2-4', N'45', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C46', N'SPT2-4', N'46', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C47', N'SPT2-4', N'47', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C48', N'SPT2-4', N'48', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C49', N'SPT2-4', N'49', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C50', N'SPT2-4', N'50', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C51', N'SPT2-4', N'51', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C52', N'SPT2-4', N'52', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C53', N'SPT2-4', N'53', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C54', N'SPT2-4', N'54', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C55', N'SPT2-4', N'55', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C56', N'SPT2-4', N'56', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C57', N'SPT2-4', N'57', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C58', N'SPT2-4', N'58', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C59', N'SPT2-4', N'59', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C60', N'SPT2-4', N'60', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C61', N'SPT2-4', N'61', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C62', N'SPT2-4', N'62', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C63', N'SPT2-4', N'63', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-4-C64', N'SPT2-4', N'64', NULL, NULL)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C01', N'SPT2-5', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C02', N'SPT2-5', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C03', N'SPT2-5', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C04', N'SPT2-5', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C05', N'SPT2-5', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C06', N'SPT2-5', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C07', N'SPT2-5', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C08', N'SPT2-5', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C09', N'SPT2-5', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C10', N'SPT2-5', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C11', N'SPT2-5', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C12', N'SPT2-5', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C13', N'SPT2-5', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C14', N'SPT2-5', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C15', N'SPT2-5', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C16', N'SPT2-5', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C17', N'SPT2-5', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C18', N'SPT2-5', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C19', N'SPT2-5', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C20', N'SPT2-5', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C21', N'SPT2-5', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C22', N'SPT2-5', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C23', N'SPT2-5', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C24', N'SPT2-5', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C25', N'SPT2-5', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C26', N'SPT2-5', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C27', N'SPT2-5', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C28', N'SPT2-5', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C29', N'SPT2-5', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C30', N'SPT2-5', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C31', N'SPT2-5', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C32', N'SPT2-5', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C33', N'SPT2-5', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C34', N'SPT2-5', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C35', N'SPT2-5', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C36', N'SPT2-5', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C37', N'SPT2-5', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C38', N'SPT2-5', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C39', N'SPT2-5', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C40', N'SPT2-5', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C41', N'SPT2-5', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-5-C42', N'SPT2-5', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C01', N'SPT2-6', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C02', N'SPT2-6', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C03', N'SPT2-6', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C04', N'SPT2-6', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C05', N'SPT2-6', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C06', N'SPT2-6', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C07', N'SPT2-6', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C08', N'SPT2-6', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C09', N'SPT2-6', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C10', N'SPT2-6', N'10', 2, 2)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C11', N'SPT2-6', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C12', N'SPT2-6', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C13', N'SPT2-6', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C14', N'SPT2-6', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C15', N'SPT2-6', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C16', N'SPT2-6', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C17', N'SPT2-6', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C18', N'SPT2-6', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C19', N'SPT2-6', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C20', N'SPT2-6', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C21', N'SPT2-6', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C22', N'SPT2-6', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C23', N'SPT2-6', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C24', N'SPT2-6', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C25', N'SPT2-6', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C26', N'SPT2-6', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C27', N'SPT2-6', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C28', N'SPT2-6', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C29', N'SPT2-6', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C30', N'SPT2-6', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C31', N'SPT2-6', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C32', N'SPT2-6', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C33', N'SPT2-6', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C34', N'SPT2-6', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C35', N'SPT2-6', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C36', N'SPT2-6', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C37', N'SPT2-6', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C38', N'SPT2-6', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C39', N'SPT2-6', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C40', N'SPT2-6', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C41', N'SPT2-6', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-6-C42', N'SPT2-6', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C01', N'SPT2-7', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C02', N'SPT2-7', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C03', N'SPT2-7', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C04', N'SPT2-7', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C05', N'SPT2-7', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C06', N'SPT2-7', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C07', N'SPT2-7', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C08', N'SPT2-7', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C09', N'SPT2-7', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C10', N'SPT2-7', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C11', N'SPT2-7', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C12', N'SPT2-7', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C13', N'SPT2-7', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C14', N'SPT2-7', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C15', N'SPT2-7', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C16', N'SPT2-7', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C17', N'SPT2-7', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C18', N'SPT2-7', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C19', N'SPT2-7', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C20', N'SPT2-7', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C21', N'SPT2-7', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C22', N'SPT2-7', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C23', N'SPT2-7', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C24', N'SPT2-7', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C25', N'SPT2-7', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C26', N'SPT2-7', N'26', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C27', N'SPT2-7', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C28', N'SPT2-7', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C29', N'SPT2-7', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C30', N'SPT2-7', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C31', N'SPT2-7', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C32', N'SPT2-7', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C33', N'SPT2-7', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C34', N'SPT2-7', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C35', N'SPT2-7', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C36', N'SPT2-7', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C37', N'SPT2-7', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C38', N'SPT2-7', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C39', N'SPT2-7', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C40', N'SPT2-7', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C41', N'SPT2-7', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-7-C42', N'SPT2-7', N'42', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C01', N'SPT2-8', N'01', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C02', N'SPT2-8', N'02', 1, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C03', N'SPT2-8', N'03', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C04', N'SPT2-8', N'04', 1, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C05', N'SPT2-8', N'05', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C06', N'SPT2-8', N'06', 1, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C07', N'SPT2-8', N'07', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C08', N'SPT2-8', N'08', 2, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C09', N'SPT2-8', N'09', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C10', N'SPT2-8', N'10', 2, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C11', N'SPT2-8', N'11', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C12', N'SPT2-8', N'12', 2, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C13', N'SPT2-8', N'13', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C14', N'SPT2-8', N'14', 3, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C15', N'SPT2-8', N'15', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C16', N'SPT2-8', N'16', 3, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C17', N'SPT2-8', N'17', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C18', N'SPT2-8', N'18', 3, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C19', N'SPT2-8', N'19', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C20', N'SPT2-8', N'20', 4, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C21', N'SPT2-8', N'21', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C22', N'SPT2-8', N'22', 4, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C23', N'SPT2-8', N'23', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C24', N'SPT2-8', N'24', 4, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C25', N'SPT2-8', N'25', 5, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C26', N'SPT2-8', N'26', 5, 1)
GO
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C27', N'SPT2-8', N'27', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C28', N'SPT2-8', N'28', 5, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C29', N'SPT2-8', N'29', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C30', N'SPT2-8', N'30', 5, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C31', N'SPT2-8', N'31', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C32', N'SPT2-8', N'32', 6, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C33', N'SPT2-8', N'33', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C34', N'SPT2-8', N'34', 6, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C35', N'SPT2-8', N'35', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C36', N'SPT2-8', N'36', 6, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C37', N'SPT2-8', N'37', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C38', N'SPT2-8', N'38', 7, 1)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C39', N'SPT2-8', N'39', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C40', N'SPT2-8', N'40', 7, 2)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C41', N'SPT2-8', N'41', 7, 3)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang]) VALUES (N'SPT2-8-C42', N'SPT2-8', N'42', 7, 3)
GO
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_DNA_NTR', N'SE1       ', N'SPT2', N'ADMIN001', N'DNA', N'NTR', CAST(N'2025-12-20' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_DNA_SAG', N'SE1       ', N'SPT2', N'ADMIN001', N'DNA', N'SAG', CAST(N'2025-12-20' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_HAN_DNA', N'SE1       ', N'SPT2', N'ADMIN001', N'HAN', N'DNA', CAST(N'2025-12-20' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-20' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_HAN_HUE', N'SE1       ', N'SPT2', N'ADMIN001', N'HAN', N'HUE', CAST(N'2025-12-20' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-20' AS Date), CAST(N'19:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_HAN_NTR', N'SE1       ', N'SPT2', N'ADMIN001', N'HAN', N'NTR', CAST(N'2025-12-20' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_HAN_SAG', N'SE1       ', N'SPT2', N'ADMIN001', N'HAN', N'SAG', CAST(N'2025-12-20' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_HAN_VIN', N'SE1       ', N'SPT2', N'ADMIN001', N'HAN', N'VIN', CAST(N'2025-12-20' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-20' AS Date), CAST(N'12:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_HUE_DNA', N'SE1       ', N'SPT2', N'ADMIN001', N'HUE', N'DNA', CAST(N'2025-12-20' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-20' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_HUE_NTR', N'SE1       ', N'SPT2', N'ADMIN001', N'HUE', N'NTR', CAST(N'2025-12-20' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_HUE_SAG', N'SE1       ', N'SPT2', N'ADMIN001', N'HUE', N'SAG', CAST(N'2025-12-20' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_NTR_SAG', N'SE1       ', N'SPT2', N'ADMIN001', N'NTR', N'SAG', CAST(N'2025-12-21' AS Date), CAST(N'08:00:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_VIN_DNA', N'SE1       ', N'SPT2', N'ADMIN001', N'VIN', N'DNA', CAST(N'2025-12-20' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-20' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_VIN_HUE', N'SE1       ', N'SPT2', N'ADMIN001', N'VIN', N'HUE', CAST(N'2025-12-20' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-20' AS Date), CAST(N'19:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_VIN_NTR', N'SE1       ', N'SPT2', N'ADMIN001', N'VIN', N'NTR', CAST(N'2025-12-20' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_201225_VIN_SAG', N'SE1       ', N'SPT2', N'ADMIN001', N'VIN', N'SAG', CAST(N'2025-12-20' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_DNA_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'DNA', N'NTR', CAST(N'2025-12-21' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_DNA_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'DNA', N'SAG', CAST(N'2025-12-21' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_HAN_DNA', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'DNA', CAST(N'2025-12-21' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_HAN_HUE', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'HUE', CAST(N'2025-12-21' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'19:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_HAN_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'NTR', CAST(N'2025-12-21' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_HAN_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'SAG', CAST(N'2025-12-21' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_HAN_VIN', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'VIN', CAST(N'2025-12-21' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'12:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_HUE_DNA', N'SE1       ', N'SE1', N'ADMIN001', N'HUE', N'DNA', CAST(N'2025-12-21' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_HUE_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'HUE', N'NTR', CAST(N'2025-12-21' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_HUE_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'HUE', N'SAG', CAST(N'2025-12-21' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_NTR_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'NTR', N'SAG', CAST(N'2025-12-22' AS Date), CAST(N'08:00:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_VIN_DNA', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'DNA', CAST(N'2025-12-21' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_VIN_HUE', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'HUE', CAST(N'2025-12-21' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-21' AS Date), CAST(N'19:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_VIN_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'NTR', CAST(N'2025-12-21' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_211225_VIN_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'SAG', CAST(N'2025-12-21' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_DNA_NTR', N'SE1', N'SPT2', N'NVQL0001', N'DNA', N'NTR', CAST(N'2025-12-23' AS Date), CAST(N'07:45:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'16:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_DNA_SAG', N'SE1', N'SPT2', N'NVQL0001', N'DNA', N'SAG', CAST(N'2025-12-23' AS Date), CAST(N'07:45:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'01:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_HAN_DNA', N'SE1', N'SPT2', N'NVQL0001', N'HAN', N'DNA', CAST(N'2025-12-22' AS Date), CAST(N'15:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'07:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_HAN_HUE', N'SE1', N'SPT2', N'NVQL0001', N'HAN', N'HUE', CAST(N'2025-12-22' AS Date), CAST(N'15:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'04:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_HAN_NTR', N'SE1', N'SPT2', N'NVQL0001', N'HAN', N'NTR', CAST(N'2025-12-22' AS Date), CAST(N'15:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'16:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_HAN_SAG', N'SE1', N'SPT2', N'NVQL0001', N'HAN', N'SAG', CAST(N'2025-12-22' AS Date), CAST(N'15:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'01:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_HAN_VIN', N'SE1', N'SPT2', N'NVQL0001', N'HAN', N'VIN', CAST(N'2025-12-22' AS Date), CAST(N'15:00:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'21:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_HUE_DNA', N'SE1', N'SPT2', N'NVQL0001', N'HUE', N'DNA', CAST(N'2025-12-23' AS Date), CAST(N'04:25:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'07:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_HUE_NTR', N'SE1', N'SPT2', N'NVQL0001', N'HUE', N'NTR', CAST(N'2025-12-23' AS Date), CAST(N'04:25:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'16:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_HUE_SAG', N'SE1', N'SPT2', N'NVQL0001', N'HUE', N'SAG', CAST(N'2025-12-23' AS Date), CAST(N'04:25:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'01:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_NTR_SAG', N'SE1', N'SPT2', N'NVQL0001', N'NTR', N'SAG', CAST(N'2025-12-23' AS Date), CAST(N'17:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'01:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_VIN_DNA', N'SE1', N'SPT2', N'NVQL0001', N'VIN', N'DNA', CAST(N'2025-12-22' AS Date), CAST(N'21:15:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'07:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_VIN_HUE', N'SE1', N'SPT2', N'NVQL0001', N'VIN', N'HUE', CAST(N'2025-12-22' AS Date), CAST(N'21:15:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'04:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_VIN_NTR', N'SE1', N'SPT2', N'NVQL0001', N'VIN', N'NTR', CAST(N'2025-12-22' AS Date), CAST(N'21:15:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'16:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_221225_VIN_SAG', N'SE1', N'SPT2', N'NVQL0001', N'VIN', N'SAG', CAST(N'2025-12-22' AS Date), CAST(N'21:15:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'01:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_DNA_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'DNA', N'NTR', CAST(N'2025-12-23' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_DNA_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'DNA', N'SAG', CAST(N'2025-12-23' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_HAN_DNA', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'DNA', CAST(N'2025-12-23' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_HAN_HUE', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'HUE', CAST(N'2025-12-23' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'19:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_HAN_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'NTR', CAST(N'2025-12-23' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_HAN_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'SAG', CAST(N'2025-12-23' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_HAN_VIN', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'VIN', CAST(N'2025-12-23' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'12:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_HUE_DNA', N'SE1       ', N'SE1', N'ADMIN001', N'HUE', N'DNA', CAST(N'2025-12-23' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_HUE_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'HUE', N'NTR', CAST(N'2025-12-23' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_HUE_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'HUE', N'SAG', CAST(N'2025-12-23' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_NTR_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'NTR', N'SAG', CAST(N'2025-12-24' AS Date), CAST(N'08:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_VIN_DNA', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'DNA', CAST(N'2025-12-23' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_VIN_HUE', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'HUE', CAST(N'2025-12-23' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'19:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_VIN_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'NTR', CAST(N'2025-12-23' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_231225_VIN_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'SAG', CAST(N'2025-12-23' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_DNA_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'DNA', N'NTR', CAST(N'2025-12-24' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_DNA_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'DNA', N'SAG', CAST(N'2025-12-24' AS Date), CAST(N'22:45:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_HAN_DNA', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'DNA', CAST(N'2025-12-24' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_HAN_HUE', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'HUE', CAST(N'2025-12-24' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'19:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_HAN_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'NTR', CAST(N'2025-12-24' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_HAN_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'SAG', CAST(N'2025-12-24' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_HAN_VIN', N'SE1       ', N'SE1', N'ADMIN001', N'HAN', N'VIN', CAST(N'2025-12-24' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'12:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_HUE_DNA', N'SE1       ', N'SE1', N'ADMIN001', N'HUE', N'DNA', CAST(N'2025-12-24' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_HUE_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'HUE', N'NTR', CAST(N'2025-12-24' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_HUE_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'HUE', N'SAG', CAST(N'2025-12-24' AS Date), CAST(N'19:25:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_NTR_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'NTR', N'SAG', CAST(N'2025-12-25' AS Date), CAST(N'08:00:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_VIN_DNA', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'DNA', CAST(N'2025-12-24' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'22:25:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_VIN_HUE', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'HUE', CAST(N'2025-12-24' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'19:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_VIN_NTR', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'NTR', CAST(N'2025-12-24' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'07:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE1_241225_VIN_SAG', N'SE1       ', N'SE1', N'ADMIN001', N'VIN', N'SAG', CAST(N'2025-12-24' AS Date), CAST(N'12:15:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_DNA_HAN', N'SE2       ', N'SE1', N'NVQL0001', N'DNA', N'HAN', CAST(N'2025-12-25' AS Date), CAST(N'23:35:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_DNA_HUE', N'SE2       ', N'SE1', N'NVQL0001', N'DNA', N'HUE', CAST(N'2025-12-25' AS Date), CAST(N'23:35:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'02:35:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_DNA_VIN', N'SE2       ', N'SE1', N'NVQL0001', N'DNA', N'VIN', CAST(N'2025-12-25' AS Date), CAST(N'23:35:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'09:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_HUE_HAN', N'SE2       ', N'SE1', N'NVQL0001', N'HUE', N'HAN', CAST(N'2025-12-26' AS Date), CAST(N'02:45:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_HUE_VIN', N'SE2       ', N'SE1', N'NVQL0001', N'HUE', N'VIN', CAST(N'2025-12-26' AS Date), CAST(N'02:45:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'09:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_NTR_DNA', N'SE2       ', N'SE1', N'NVQL0001', N'NTR', N'DNA', CAST(N'2025-12-25' AS Date), CAST(N'14:15:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'23:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_NTR_HAN', N'SE2       ', N'SE1', N'NVQL0001', N'NTR', N'HAN', CAST(N'2025-12-25' AS Date), CAST(N'14:15:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_NTR_HUE', N'SE2       ', N'SE1', N'NVQL0001', N'NTR', N'HUE', CAST(N'2025-12-25' AS Date), CAST(N'14:15:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'02:35:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_NTR_VIN', N'SE2       ', N'SE1', N'NVQL0001', N'NTR', N'VIN', CAST(N'2025-12-25' AS Date), CAST(N'14:15:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'09:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_SAG_DNA', N'SE2       ', N'SE1', N'NVQL0001', N'SAG', N'DNA', CAST(N'2025-12-25' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'23:15:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_SAG_HAN', N'SE2       ', N'SE1', N'NVQL0001', N'SAG', N'HAN', CAST(N'2025-12-25' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_SAG_HUE', N'SE2       ', N'SE1', N'NVQL0001', N'SAG', N'HUE', CAST(N'2025-12-25' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'02:35:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_SAG_NTR', N'SE2       ', N'SE1', N'NVQL0001', N'SAG', N'NTR', CAST(N'2025-12-25' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'14:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_SAG_VIN', N'SE2       ', N'SE1', N'NVQL0001', N'SAG', N'VIN', CAST(N'2025-12-25' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'09:45:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE2_251225_VIN_HAN', N'SE2       ', N'SE1', N'NVQL0001', N'VIN', N'HAN', CAST(N'2025-12-26' AS Date), CAST(N'10:00:00' AS Time), CAST(N'2025-12-26' AS Date), CAST(N'16:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE3_231225_HAN_NTR', N'SE3', N'SE2', N'ADMIN001', N'HAN', N'NTR', CAST(N'2025-12-23' AS Date), CAST(N'08:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'13:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE3_231225_HAN_SAG', N'SE3', N'SE2', N'ADMIN001', N'HAN', N'SAG', CAST(N'2025-12-23' AS Date), CAST(N'08:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'18:10:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE3_231225_NTR_SAG', N'SE3', N'SE2', N'ADMIN001', N'NTR', N'SAG', CAST(N'2025-12-23' AS Date), CAST(N'13:10:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'18:10:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE3_241225_HAN_NTR', N'SE3', N'SE2', N'ADMIN001', N'HAN', N'NTR', CAST(N'2025-12-24' AS Date), CAST(N'08:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'13:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE3_241225_HAN_SAG', N'SE3', N'SE2', N'ADMIN001', N'HAN', N'SAG', CAST(N'2025-12-24' AS Date), CAST(N'08:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'18:10:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SE3_241225_NTR_SAG', N'SE3', N'SE2', N'ADMIN001', N'NTR', N'SAG', CAST(N'2025-12-24' AS Date), CAST(N'13:10:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'18:10:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SPT1_221225_SAG_PHT       ', N'SPT1', N'SPT2', N'NVQL0001', N'SAG', N'PHT       ', CAST(N'2025-12-22' AS Date), CAST(N'21:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'00:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SPT1_231225_SAG_PHT       ', N'SPT1', N'SPT2', N'NVQL0001', N'SAG', N'PHT       ', CAST(N'2025-12-23' AS Date), CAST(N'21:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'00:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SPT1_241225_SAG_PHT       ', N'SPT1', N'SPT2', N'NVQL0001', N'SAG', N'PHT       ', CAST(N'2025-12-24' AS Date), CAST(N'21:00:00' AS Time), CAST(N'2025-12-25' AS Date), CAST(N'00:00:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SPT2_221225_SAG_PHT', N'SPT2', N'SPT2', NULL, N'SAG', N'PHT', CAST(N'2025-12-22' AS Date), CAST(N'18:00:00' AS Time), CAST(N'2025-12-22' AS Date), CAST(N'21:50:00' AS Time), N'Chờ khởi hành')
GO
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SPT2_231225_SAG_PHT', N'SPT2      ', N'SE1', N'ADMIN001', N'SAG', N'PHT', CAST(N'2025-12-23' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-23' AS Date), CAST(N'09:50:00' AS Time), N'Chờ khởi hành')
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTuyen], [MaTau], [MaNV], [GaDi], [GaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES (N'SPT2_241225_SAG_PHT', N'SPT2      ', N'SE1', N'ADMIN001', N'SAG', N'PHT', CAST(N'2025-12-24' AS Date), CAST(N'06:00:00' AS Time), CAST(N'2025-12-24' AS Date), CAST(N'09:50:00' AS Time), N'Chờ khởi hành')
GO
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'DNA', N'Đà Nẵng', N'Đà Nẵng')
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'HAN', N'Hà Nội', N'Hà Nội')
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'HUE', N'Huế', N'Thừa Thiên Huế')
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'NTR', N'Nha Trang', N'Khánh Hòa')
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'PHT       ', N'Phan Thiết', N'Lâm Đồng')
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'SAG', N'Sài Gòn', N'TP.HCM')
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'VIN', N'Vinh', N'Nghệ An')
GO
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE1', N'DNA', 4, 791, 20, 540)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE1', N'HAN', 1, 0, 0, 360)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE1', N'HUE', 3, 688, 10, 180)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE1', N'NTR', 5, 1315, 15, 480)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE1', N'SAG', 6, 1726, 0, 0)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE1', N'VIN', 2, 319, 15, 420)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE2', N'DNA', 3, 935, 20, 180)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE2', N'HAN', 6, 1726, 0, 0)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE2', N'HUE', 4, 1038, 10, 420)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE2', N'NTR', 2, 411, 15, 540)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE2', N'SAG', 1, 0, 0, 480)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE2', N'VIN', 5, 1407, 15, 360)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE3', N'HAN', 1, 0, 0, 300)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE3', N'NTR', 2, 500, 10, 300)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SE3', N'SAG', 3, 1300, 0, 0)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SPT1', N'PHT       ', 2, 186, 0, 230)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SPT1', N'SAG', 1, 0, 0, 180)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SPT2', N'PHT', 2, 186, 0, 0)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'SPT2', N'SAG', 1, 0, 0, 230)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'TN1', N'HAN', 1, 0, 0, 60)
INSERT [dbo].[GA_TRONG_TUYEN] ([MaTuyen], [MaGa], [ThuTuGa], [KhoangCachTichLuy], [ThoiGianDung], [ThoiGianDiChuyenToiGaTiepTheo]) VALUES (N'TN1', N'SAG', 2, 1300, 0, 0)
GO
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD0121122500010001', N'KH1412250001', N'NVBV0001', NULL, NULL, CAST(N'2025-12-21T13:50:22.300' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(688000 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD0121122500010002', N'KH1412250001', N'NVBV0001', NULL, NULL, CAST(N'2025-12-21T18:07:17.213' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(688000 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD0122122500010001', N'KH1412250001', N'NVBV0001', NULL, NULL, CAST(N'2025-12-22T11:02:09.310' AS DateTime), N'Tiền mặt', N'Đổi vé', CAST(364000 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD0122122500010002', N'KH1412250001', N'NVBV0001', N'KM1225009', NULL, CAST(N'2025-12-22T12:44:09.707' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(605440 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD0122122500010003', N'KH1412250001', N'NVBV0001', N'KM1225009', NULL, CAST(N'2025-12-22T13:01:30.803' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(605440 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD0122122500010004', N'KH1412250002', N'NVBV0001', N'KM1225009', NULL, CAST(N'2025-12-22T13:02:06.840' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(3178560 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD0122122500010005', N'KH1412250001', N'NVBV0001', NULL, NULL, CAST(N'2025-12-22T23:33:40.813' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(1651200 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD01221225N0010001', N'KH1412250001', N'ADMIN001', N'KM1225009', NULL, CAST(N'2025-12-22T14:33:09.757' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(605440 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD01221225N0010002', N'KH1412250001', N'ADMIN001', N'KM1225009', NULL, CAST(N'2025-12-22T14:34:43.507' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(605440 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD01231225N0010001', N'KH1412250001', N'ADMIN001', N'KM1225009', NULL, CAST(N'2025-12-23T00:19:01.073' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(1487200 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD01231225N0010002', N'KH1412250001', N'ADMIN001', N'KM1225009', NULL, CAST(N'2025-12-23T00:20:10.450' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(572000 AS Decimal(12, 0)), NULL)
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongCong], [NgayLap], [PhuongThuc], [LoaiHoaDon], [TongTien], [MaHD_Goc]) VALUES (N'HD01231225N0010003', N'KH1412250001', N'ADMIN001', N'KM1225009', NULL, CAST(N'2025-12-23T00:21:04.373' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp', CAST(915200 AS Decimal(12, 0)), NULL)
GO
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH1412250001', N'Duy', N'060205006764', NULL, N'0332534541', NULL, CAST(N'2005-09-09' AS Date))
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH1412250002', N'Nguyễn Văn A', N'011990000011', NULL, N'0901112222', NULL, CAST(N'1990-01-01' AS Date))
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH1412250003', N'Trần Thị B', N'022050000021', NULL, N'0912345678', NULL, CAST(N'2005-05-15' AS Date))
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH1412250004', N'Lê Văn C', N'031950000031', NULL, N'0332534542', NULL, CAST(N'1995-12-10' AS Date))
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH1412250005', N'Phạm Thị D', N'041965000041', NULL, N'0987654321', NULL, CAST(N'1965-07-20' AS Date))
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH1412250006', N'Vũ Đình E', N'052010000051', NULL, N'0919876545', NULL, CAST(N'2010-11-11' AS Date))
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH1412250007', N'Hoàng Văn F', N'062000000061', NULL, N'0919876543', NULL, CAST(N'2000-03-05' AS Date))
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH1512250001', N'Bảo Duy', N'060205006763', NULL, N'0332534540', NULL, CAST(N'2018-09-09' AS Date))
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH1512250002', N'Duy 2', N'060205006700', NULL, N'0332534544', NULL, CAST(N'2005-10-10' AS Date))
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh], [NgaySinh]) VALUES (N'KH2212250001', N'Tôi là Duy', N'060205006762', NULL, N'0332534540', NULL, CAST(N'2005-09-09' AS Date))
GO
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM0126001', N'Giảm 10 phần trăm từ ngày 19-25', N'PHAN_TRAM_GIA', CAST(10.00 AS Decimal(18, 2)), N'NONE', NULL, CAST(N'2025-12-19T00:00:00.060' AS DateTime), CAST(N'2025-12-25T23:59:59.003' AS DateTime), N'HOAT_DONG')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM0126002', N'Ưu đãi Đầu Năm Mới 5%', N'PHAN_TRAM_GIA', CAST(5.00 AS Decimal(18, 2)), N'NONE', NULL, CAST(N'2026-01-15T00:00:00.000' AS DateTime), CAST(N'2026-02-15T23:59:59.000' AS DateTime), N'KHONG_HOAT_DONG')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM0226005', N'Ưu đãi Lễ Tình Nhân 30%', N'PHAN_TRAM_GIA', CAST(30.00 AS Decimal(18, 2)), N'MIN_SL', CAST(4.00 AS Decimal(18, 2)), CAST(N'2026-02-01T00:00:00.000' AS DateTime), CAST(N'2026-02-14T23:59:59.000' AS DateTime), N'HOAT_DONG')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM1125001', N'Giảm 50K cho Hóa đơn trên 5 Vé', N'CO_DINH', CAST(50000.00 AS Decimal(18, 2)), N'MIN_SL', CAST(5.00 AS Decimal(18, 2)), CAST(N'2025-11-01T00:00:00.000' AS DateTime), CAST(N'2025-11-30T23:59:59.000' AS DateTime), N'HET_HAN')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM1225001', N'Giảm 10% cho Hóa đơn trên 1 Triệu (Đã Sửa) (Đã Sửa)', N'PHAN_TRAM_GIA', CAST(10.00 AS Decimal(18, 2)), N'MIN_GIA', CAST(1000000.00 AS Decimal(18, 2)), CAST(N'2025-12-01T00:00:00.000' AS DateTime), CAST(N'2025-12-31T23:59:59.000' AS DateTime), N'KHONG_HOAT_DONG')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM1225004', N'Giảm 200K Tết Dương Lịch cho hóa đơn trên 2 triệu', N'CO_DINH', CAST(200000.00 AS Decimal(18, 2)), N'MIN_GIA', CAST(2000000.00 AS Decimal(18, 2)), CAST(N'2025-12-15T00:00:00.000' AS DateTime), CAST(N'2026-01-30T23:59:59.000' AS DateTime), N'HOAT_DONG')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM1225006', N'Giảm 10K Cho Mọi Đơn Hàng', N'CO_DINH', CAST(10000.00 AS Decimal(18, 2)), N'NONE', NULL, CAST(N'2025-12-15T00:00:00.000' AS DateTime), CAST(N'2025-12-19T23:59:59.677' AS DateTime), N'HOAT_DONG')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM1225007', N'Ưu đãi 50K cho hóa đơn 100k', N'CO_DINH', CAST(50000.00 AS Decimal(18, 2)), N'MIN_GIA', CAST(100.00 AS Decimal(18, 2)), CAST(N'2025-12-15T00:00:00.180' AS DateTime), CAST(N'2025-12-18T23:59:59.397' AS DateTime), N'HOAT_DONG')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM1225008', N'KM Test Thêm Mới 15%', N'PHAN_TRAM_GIA', CAST(15.00 AS Decimal(18, 2)), N'MIN_GIA', CAST(500000.00 AS Decimal(18, 2)), CAST(N'2025-12-16T14:51:58.850' AS DateTime), CAST(N'2026-01-15T14:51:58.850' AS DateTime), N'KHONG_HOAT_DONG')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM1225009', N'Giảm 12 từ 20/12 đến 24/2=12', N'PHAN_TRAM_GIA', CAST(12.00 AS Decimal(18, 2)), N'NONE', NULL, CAST(N'2025-12-20T00:00:00.827' AS DateTime), CAST(N'2025-12-24T23:59:59.890' AS DateTime), N'HOAT_DONG')
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [LoaiKM], [GiaTriGiam], [DKApDung], [GiaTriDK], [NgayBD], [NgayKT], [TrangThai]) VALUES (N'KM1225010', N'DUy 1234', N'PHAN_TRAM_GIA', CAST(7.00 AS Decimal(18, 2)), N'MIN_GIA', CAST(100000.00 AS Decimal(18, 2)), CAST(N'2025-12-23T00:00:00.093' AS DateTime), CAST(N'2025-12-26T23:59:59.130' AS DateTime), N'KHONG_HOAT_DONG')
GO
INSERT [dbo].[LoaiToa] ([MaLoaiToa], [TenLoaiToa], [HeSo]) VALUES (N'GGNCU', N'Ghế ngồi cứng', 1)
INSERT [dbo].[LoaiToa] ([MaLoaiToa], [TenLoaiToa], [HeSo]) VALUES (N'GHMEM', N'Ghế mềm', 1)
INSERT [dbo].[LoaiToa] ([MaLoaiToa], [TenLoaiToa], [HeSo]) VALUES (N'GINAM', N'Giường nằm', 1.5)
GO
INSERT [dbo].[LoaiVe] ([MaLoaiVe], [TenLoaiVe], [MucGiamGia], [TuoiMin], [TuoiMax]) VALUES (N'VT01', N'Người lớn', 1, 11, 59)
INSERT [dbo].[LoaiVe] ([MaLoaiVe], [TenLoaiVe], [MucGiamGia], [TuoiMin], [TuoiMax]) VALUES (N'VT02', N'Trẻ em', 0.8, 1, 10)
INSERT [dbo].[LoaiVe] ([MaLoaiVe], [TenLoaiVe], [MucGiamGia], [TuoiMin], [TuoiMax]) VALUES (N'VT03', N'Người cao tuổi', 0.75, 60, 999)
INSERT [dbo].[LoaiVe] ([MaLoaiVe], [TenLoaiVe], [MucGiamGia], [TuoiMin], [TuoiMax]) VALUES (N'VT04', N'Sinh viên', 0.9, 18, 25)
GO
INSERT [dbo].[NhanVien] ([MaNV], [HoTen], [SoCCCD], [NgaySinh], [Email], [SDT], [GioiTinh], [DiaChi], [NgayVaoLam], [ChucVu], [CaLamViec]) VALUES (N'ADMIN001', N'Admin Tổng', N'000000000000', CAST(N'1990-01-01' AS Date), N'admin@gmail.com', N'0999888777', NULL, N'Test', NULL, NULL, 1)
INSERT [dbo].[NhanVien] ([MaNV], [HoTen], [SoCCCD], [NgaySinh], [Email], [SDT], [GioiTinh], [DiaChi], [NgayVaoLam], [ChucVu], [CaLamViec]) VALUES (N'NVBV0001', N'Lê Thị B', N'001198000002', CAST(N'1998-10-22' AS Date), N'leoviethung@gmail.com', N'0902345678', N'Nữ', N'200 Trường Chinh, TP. HCM', CAST(N'2020-01-10' AS Date), N'Nhân viên bán vé', 1)
INSERT [dbo].[NhanVien] ([MaNV], [HoTen], [SoCCCD], [NgaySinh], [Email], [SDT], [GioiTinh], [DiaChi], [NgayVaoLam], [ChucVu], [CaLamViec]) VALUES (N'NVQL0001', N'Trần Văn A', N'001195000001', CAST(N'1995-05-15' AS Date), N'nbduy99@gmail.com', N'0901234567', N'Nam', N'100 Quang Trung, Hà Nội', CAST(N'2018-08-20' AS Date), N'Trưởng phòng', NULL)
INSERT [dbo].[NhanVien] ([MaNV], [HoTen], [SoCCCD], [NgaySinh], [Email], [SDT], [GioiTinh], [DiaChi], [NgayVaoLam], [ChucVu], [CaLamViec]) VALUES (N'NVQL0002', N'Phạm Văn C', N'001190000003', CAST(N'1990-01-01' AS Date), N'vanphamc@vetausg.vn', N'0903456789', N'Nam', N'300 Hai Bà Trưng, Đà Nẵng', CAST(N'2015-03-05' AS Date), N'Quản lý', NULL)
GO
INSERT [dbo].[TaiKhoan] ([TenDangNhap], [MaNV], [MatKhau], [NgayTao], [TrangThai]) VALUES (N'ADMIN001', N'ADMIN001', N'admin', CAST(N'2025-12-21T18:58:38.410' AS DateTime), N'Đang hoạt động')
INSERT [dbo].[TaiKhoan] ([TenDangNhap], [MaNV], [MatKhau], [NgayTao], [TrangThai]) VALUES (N'NVBV0001', N'NVBV0001', N'admin', CAST(N'2025-10-17T15:32:05.287' AS DateTime), N'Đang hoạt động')
INSERT [dbo].[TaiKhoan] ([TenDangNhap], [MaNV], [MatKhau], [NgayTao], [TrangThai]) VALUES (N'NVQL0001', N'NVQL0001', N'duy', CAST(N'2025-10-17T15:32:05.287' AS DateTime), N'Đang hoạt động')
INSERT [dbo].[TaiKhoan] ([TenDangNhap], [MaNV], [MatKhau], [NgayTao], [TrangThai]) VALUES (N'NVQL0002', N'NVQL0002', N'admin', CAST(N'2025-12-21T18:58:38.410' AS DateTime), N'Đang hoạt động')
GO
INSERT [dbo].[Tau] ([SoHieu], [TrangThai]) VALUES (N'SE1', N'Đang hoạt động')
INSERT [dbo].[Tau] ([SoHieu], [TrangThai]) VALUES (N'SE2', N'Đang hoạt động')
INSERT [dbo].[Tau] ([SoHieu], [TrangThai]) VALUES (N'SPT2', N'Đang hoạt động')
GO
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE1-1', N'SE1', N'GHMEM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE1-2', N'SE1', N'GHMEM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE1-3', N'SE1', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE1-4', N'SE1', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE1-5', N'SE1', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE1-6', N'SE1', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE2-1', N'SE2', N'GHMEM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE2-2', N'SE2', N'GHMEM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE2-3', N'SE2', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE2-4', N'SE2', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE2-5', N'SE2', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SE2-6', N'SE2', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SPT2-1', N'SPT2', N'GHMEM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SPT2-2', N'SPT2', N'GHMEM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SPT2-3', N'SPT2', N'GHMEM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SPT2-4', N'SPT2', N'GHMEM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SPT2-5', N'SPT2', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SPT2-6', N'SPT2', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SPT2-7', N'SPT2', N'GINAM')
INSERT [dbo].[Toa] ([MaToa], [SoHieuTau], [maLoaiToa]) VALUES (N'SPT2-8', N'SPT2', N'GINAM')
GO
INSERT [dbo].[TUYEN] ([MaTuyen], [TenTuyen], [GaDau], [GaCuoi], [DonGiaKm]) VALUES (N'SE1', N'Tàu Thống Nhất Bắc Nam', N'HAN       ', N'SAG       ', 1000)
INSERT [dbo].[TUYEN] ([MaTuyen], [TenTuyen], [GaDau], [GaCuoi], [DonGiaKm]) VALUES (N'SE2', N'Tàu siêu tốc Nam Bắc', N'SAG       ', N'HAN       ', 1200)
INSERT [dbo].[TUYEN] ([MaTuyen], [TenTuyen], [GaDau], [GaCuoi], [DonGiaKm]) VALUES (N'SE3', N'Hà nội sài gòn', N'HAN       ', N'SAG       ', 1300)
INSERT [dbo].[TUYEN] ([MaTuyen], [TenTuyen], [GaDau], [GaCuoi], [DonGiaKm]) VALUES (N'SPT1', N'Tàu tỉnh Phan Thiết-Sài Gòn', N'PHT       ', N'SAG       ', 1200)
INSERT [dbo].[TUYEN] ([MaTuyen], [TenTuyen], [GaDau], [GaCuoi], [DonGiaKm]) VALUES (N'SPT2', N'Tàu tuyến tình sài gòn phan thiết', N'SAG       ', N'PHT       ', 1500)
INSERT [dbo].[TUYEN] ([MaTuyen], [TenTuyen], [GaDau], [GaCuoi], [DonGiaKm]) VALUES (N'TN1', N'Tàu thống nhất bắc Nam', N'HAN       ', N'SAG       ', 0)
GO
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012112250001', N'SE1_241225_HAN_HUE', N'SE1-1-C17', N'NVBV0001  ', N'KH1412250001', N'VT01', CAST(688000 AS Decimal(18, 0)), N'DA_HUY', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012112250002', N'SE1_241225_HAN_HUE', N'SE1-1-C09', N'NVBV0001  ', N'KH1412250001', N'VT01', CAST(688000 AS Decimal(18, 0)), N'DA-HUY', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250001', N'SE1_241225_HAN_HUE', N'SE1-6-C07', N'NVBV0001  ', N'KH1412250001', N'VT01', CAST(1052000 AS Decimal(18, 0)), N'DA-HUY', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250002', N'SE1_241225_HAN_HUE', N'SE1-1-C17', N'NVBV0001  ', N'KH1412250001', N'VT01', CAST(605440 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250003', N'SE1_221225_HAN_HUE', N'SPT2-1-C09', N'NVBV0001  ', N'KH1412250001', N'VT01', CAST(605440 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250004', N'SE1_221225_HAN_HUE', N'SPT2-1-C07', N'NVBV0001  ', N'KH1412250002', N'VT01', CAST(605440 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250005', N'SE1_221225_HAN_HUE', N'SPT2-1-C08', N'NVBV0001  ', N'KH1412250003', N'VT02', CAST(484352 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250006', N'SE1_221225_HAN_HUE', N'SPT2-1-C05', N'NVBV0001  ', N'KH1412250004', N'VT01', CAST(605440 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250007', N'SE1_221225_HAN_HUE', N'SPT2-1-C49', N'NVBV0001  ', N'KH1412250005', N'VT03', CAST(454080 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250008', N'SE1_221225_HAN_HUE', N'SPT2-1-C06', N'NVBV0001  ', N'KH1412250006', N'VT02', CAST(484352 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250009', N'SE1_221225_HAN_HUE', N'SPT2-1-C03', N'NVBV0001  ', N'KH1412250007', N'VT04', CAST(544896 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250010', N'SE1_221225_HAN_HUE', N'SPT2-1-C45', N'ADMIN001  ', N'KH1412250001', N'VT01', CAST(605440 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250011', N'SE1_221225_HAN_HUE', N'SPT2-1-C13', N'ADMIN001  ', N'KH1412250001', N'VT01', CAST(605440 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250012', N'SE1_231225_HAN_HUE', N'SE1-3-C13', N'NVBV0001  ', N'KH1412250001', N'VT01', CAST(1032000 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012212250013', N'SE1_231225_HAN_HUE', N'SE1-2-C17', N'NVBV0001  ', N'KH2212250001', N'VT04', CAST(619200 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012312250001', N'SE3_231225_HAN_SAG', N'SE2-1-C01', N'ADMIN001  ', N'KH1412250001', N'VT01', CAST(1487200 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012312250002', N'SE3_231225_HAN_NTR', N'SE2-1-C05', N'ADMIN001  ', N'KH1412250001', N'VT01', CAST(572000 AS Decimal(18, 0)), N'DA_BAN', NULL)
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai], [ThanhTien]) VALUES (N'VE012312250003', N'SE3_231225_NTR_SAG', N'SE2-1-C05', N'ADMIN001  ', N'KH1412250001', N'VT01', CAST(915200 AS Decimal(18, 0)), N'DA-HUY', NULL)
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ_KhachHang_CCCD]    Script Date: 23/12/2025 1:17:08 SA ******/
ALTER TABLE [dbo].[KhachHang] ADD  CONSTRAINT [UQ_KhachHang_CCCD] UNIQUE NONCLUSTERED 
(
	[CCCD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ_NhanVien_SoCCCD]    Script Date: 23/12/2025 1:17:08 SA ******/
ALTER TABLE [dbo].[NhanVien] ADD  CONSTRAINT [UQ_NhanVien_SoCCCD] UNIQUE NONCLUSTERED 
(
	[SoCCCD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ_TaiKhoan_MaNV]    Script Date: 23/12/2025 1:17:08 SA ******/
ALTER TABLE [dbo].[TaiKhoan] ADD  CONSTRAINT [UQ_TaiKhoan_MaNV] UNIQUE NONCLUSTERED 
(
	[MaNV] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[ChiTietHoaDon] ADD  DEFAULT ((0)) FOR [DonGia]
GO
ALTER TABLE [dbo].[HoaDon] ADD  CONSTRAINT [DF_HoaDon_NgayLap]  DEFAULT (getdate()) FOR [NgayLap]
GO
ALTER TABLE [dbo].[TaiKhoan] ADD  CONSTRAINT [DF_TaiKhoan_NgayTao]  DEFAULT (getdate()) FOR [NgayTao]
GO
ALTER TABLE [dbo].[TUYEN] ADD  DEFAULT ((0)) FOR [DonGiaKm]
GO
ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD  CONSTRAINT [FK_CTHD_HoaDon] FOREIGN KEY([MaHD])
REFERENCES [dbo].[HoaDon] ([MaHD])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[ChiTietHoaDon] CHECK CONSTRAINT [FK_CTHD_HoaDon]
GO
ALTER TABLE [dbo].[ChiTietHoaDon]  WITH CHECK ADD  CONSTRAINT [FK_CTHD_Ve] FOREIGN KEY([MaVe])
REFERENCES [dbo].[Ve] ([MaVe])
GO
ALTER TABLE [dbo].[ChiTietHoaDon] CHECK CONSTRAINT [FK_CTHD_Ve]
GO
ALTER TABLE [dbo].[ChoDat]  WITH CHECK ADD  CONSTRAINT [FK_ChoDat_Toa] FOREIGN KEY([MaToa])
REFERENCES [dbo].[Toa] ([MaToa])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[ChoDat] CHECK CONSTRAINT [FK_ChoDat_Toa]
GO
ALTER TABLE [dbo].[ChuyenTau]  WITH CHECK ADD  CONSTRAINT [FK_ChuyenTau_GaDen] FOREIGN KEY([GaDen])
REFERENCES [dbo].[Ga] ([MaGa])
GO
ALTER TABLE [dbo].[ChuyenTau] CHECK CONSTRAINT [FK_ChuyenTau_GaDen]
GO
ALTER TABLE [dbo].[ChuyenTau]  WITH CHECK ADD  CONSTRAINT [FK_ChuyenTau_GaDi] FOREIGN KEY([GaDi])
REFERENCES [dbo].[Ga] ([MaGa])
GO
ALTER TABLE [dbo].[ChuyenTau] CHECK CONSTRAINT [FK_ChuyenTau_GaDi]
GO
ALTER TABLE [dbo].[ChuyenTau]  WITH CHECK ADD  CONSTRAINT [FK_ChuyenTau_NhanVien] FOREIGN KEY([MaNV])
REFERENCES [dbo].[NhanVien] ([MaNV])
GO
ALTER TABLE [dbo].[ChuyenTau] CHECK CONSTRAINT [FK_ChuyenTau_NhanVien]
GO
ALTER TABLE [dbo].[ChuyenTau]  WITH CHECK ADD  CONSTRAINT [FK_ChuyenTau_Tau] FOREIGN KEY([MaTau])
REFERENCES [dbo].[Tau] ([SoHieu])
ON UPDATE CASCADE
GO
ALTER TABLE [dbo].[ChuyenTau] CHECK CONSTRAINT [FK_ChuyenTau_Tau]
GO
ALTER TABLE [dbo].[ChuyenTau]  WITH CHECK ADD  CONSTRAINT [FK_ChuyenTau_Tuyen] FOREIGN KEY([MaTuyen])
REFERENCES [dbo].[TUYEN] ([MaTuyen])
GO
ALTER TABLE [dbo].[ChuyenTau] CHECK CONSTRAINT [FK_ChuyenTau_Tuyen]
GO
ALTER TABLE [dbo].[GA_TRONG_TUYEN]  WITH CHECK ADD  CONSTRAINT [FK_GTT_Ga] FOREIGN KEY([MaGa])
REFERENCES [dbo].[Ga] ([MaGa])
GO
ALTER TABLE [dbo].[GA_TRONG_TUYEN] CHECK CONSTRAINT [FK_GTT_Ga]
GO
ALTER TABLE [dbo].[GA_TRONG_TUYEN]  WITH CHECK ADD  CONSTRAINT [FK_GTT_Tuyen] FOREIGN KEY([MaTuyen])
REFERENCES [dbo].[TUYEN] ([MaTuyen])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[GA_TRONG_TUYEN] CHECK CONSTRAINT [FK_GTT_Tuyen]
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD  CONSTRAINT [FK_HoaDon_KhachHang] FOREIGN KEY([MaKhachHang])
REFERENCES [dbo].[KhachHang] ([MaKhachHang])
ON UPDATE CASCADE
GO
ALTER TABLE [dbo].[HoaDon] CHECK CONSTRAINT [FK_HoaDon_KhachHang]
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD  CONSTRAINT [FK_HoaDon_KhuyenMai] FOREIGN KEY([MaKM])
REFERENCES [dbo].[KhuyenMai] ([MaKM])
GO
ALTER TABLE [dbo].[HoaDon] CHECK CONSTRAINT [FK_HoaDon_KhuyenMai]
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD  CONSTRAINT [FK_HoaDon_NhanVienLap] FOREIGN KEY([MaNVLap])
REFERENCES [dbo].[NhanVien] ([MaNV])
GO
ALTER TABLE [dbo].[HoaDon] CHECK CONSTRAINT [FK_HoaDon_NhanVienLap]
GO
ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD  CONSTRAINT [FK_NhanVien_CaLamViec] FOREIGN KEY([CaLamViec])
REFERENCES [dbo].[CaLamViec] ([MaCaLamViec])
GO
ALTER TABLE [dbo].[NhanVien] CHECK CONSTRAINT [FK_NhanVien_CaLamViec]
GO
ALTER TABLE [dbo].[TaiKhoan]  WITH CHECK ADD  CONSTRAINT [FK_TaiKhoan_NhanVien] FOREIGN KEY([MaNV])
REFERENCES [dbo].[NhanVien] ([MaNV])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[TaiKhoan] CHECK CONSTRAINT [FK_TaiKhoan_NhanVien]
GO
ALTER TABLE [dbo].[Toa]  WITH CHECK ADD  CONSTRAINT [FK_Toa_LoaiToa] FOREIGN KEY([maLoaiToa])
REFERENCES [dbo].[LoaiToa] ([MaLoaiToa])
GO
ALTER TABLE [dbo].[Toa] CHECK CONSTRAINT [FK_Toa_LoaiToa]
GO
ALTER TABLE [dbo].[Ve]  WITH CHECK ADD  CONSTRAINT [FK_Ve_ChoDat] FOREIGN KEY([MaChoDat])
REFERENCES [dbo].[ChoDat] ([MaCho])
GO
ALTER TABLE [dbo].[Ve] CHECK CONSTRAINT [FK_Ve_ChoDat]
GO
ALTER TABLE [dbo].[Ve]  WITH CHECK ADD  CONSTRAINT [FK_Ve_ChuyenTau] FOREIGN KEY([MaChuyenTau])
REFERENCES [dbo].[ChuyenTau] ([MaChuyenTau])
ON UPDATE CASCADE
GO
ALTER TABLE [dbo].[Ve] CHECK CONSTRAINT [FK_Ve_ChuyenTau]
GO
ALTER TABLE [dbo].[Ve]  WITH CHECK ADD  CONSTRAINT [FK_Ve_KhachHang] FOREIGN KEY([MaKhachHang])
REFERENCES [dbo].[KhachHang] ([MaKhachHang])
GO
ALTER TABLE [dbo].[Ve] CHECK CONSTRAINT [FK_Ve_KhachHang]
GO
ALTER TABLE [dbo].[Ve]  WITH CHECK ADD  CONSTRAINT [FK_Ve_LoaiVe] FOREIGN KEY([MaLoaiVe])
REFERENCES [dbo].[LoaiVe] ([MaLoaiVe])
GO
ALTER TABLE [dbo].[Ve] CHECK CONSTRAINT [FK_Ve_LoaiVe]
GO
USE [master]
GO
ALTER DATABASE [QuanLyVeTau] SET  READ_WRITE 
GO
