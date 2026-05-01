
use QuanLyVeTau
DELETE FROM ChoDat;

-- I. CHÈN CHỖ NGỒI GHẾ MỀM (4 Toa x 64 Ghế = 256 Chỗ)
-- Giả sử tồn tại ít nhất 64 hàng trong sys.columns để sinh số
WITH Numbers (STT) AS (
    -- Sinh số thứ tự từ 1 đến 64
    SELECT TOP 64 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) FROM sys.columns c1, sys.columns c2
),
GhếMềm AS (
    SELECT
        STT,
        -- Phân chia 64 ghế vào 16 "Khoang" (khu vực 4 ghế)
        CEILING(CAST(STT AS DECIMAL) / 4) AS Khoang
    FROM Numbers
)
INSERT INTO [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang])
SELECT
    -- MaCho: [STT]-[MaToa]-C, ví dụ: SPT2-1-C01
    t.MaToa + '-' + 'C' + FORMAT(gm.STT, '00') AS MaCho,
    t.MaToa,
    FORMAT(gm.STT, '00') AS SoCho, -- SoCho là số thứ tự 01, 02, ... 64
    NULL AS Khoang,
    NULL AS Tang
FROM GhếMềm gm
-- Áp dụng cho 4 toa ghế mềm
CROSS JOIN (VALUES (N'SPT2-1'), (N'SPT2-2'), (N'SPT2-3'), (N'SPT2-4')) AS t(MaToa);

-- =============================================================
-- II. CHÈN CHỖ NGỒNG GIƯỜNG NẰM (4 Toa x 42 Giường = 168 Chỗ)
-- Giường nằm: 7 Khoang x 6 Giường/Khoang (3 tầng, 2 vị trí A/B)
-- =============================================================

WITH Params AS (
    SELECT 42 AS MaxCho, 'AB' AS SideLetters -- 42 giường tối đa/toa
),
Numbers (N) AS (
    -- Sinh số thứ tự từ 1 đến 42
    SELECT TOP 42 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) FROM sys.columns c1, sys.columns c2
),
GiườngNằm AS (
    SELECT 
        N,
        -- Tính toán Khoang (1-7)
        (N - 1) / 6 + 1 AS Khoang,                      
        -- Tính toán Tầng (1=dưới, 2=giữa, 3=trên)
        ((N - 1) % 6) / 2 + 1 AS Tang,                
        -- Tính toán vị trí (A hoặc B) - Giữ lại để tính toán Khoang/Tầng
        SUBSTRING(SideLetters, ((N - 1) % 2) + 1, 1) AS Side 
    FROM Numbers
    CROSS JOIN Params
)
INSERT INTO [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang])
SELECT
    -- MaCho: [MaToa]-C[STT], ví dụ: SPT2-5-C01
    t.MaToa + N'-C' + FORMAT(gn.N, '00') AS MaCho,
    
    t.MaToa,
    
    -- *** LOGIC TẠO SOCHO ĐÃ SỬA THÀNH SỐ THỨ TỰ (STT) ***
    FORMAT(gn.N, '00') AS SoCho, 
    -- ****************************************************
    gn.Khoang,
    gn.Tang
FROM GiườngNằm gn
-- Áp dụng cho 4 toa giường nằm
CROSS JOIN (VALUES (N'SPT2-5'), (N'SPT2-6'), (N'SPT2-7'), (N'SPT2-8')) AS t(MaToa);

GO

USE [QuanLyVeTau]
GO

-- 1. XÓA DỮ LIỆU CŨ (Nếu cần làm sạch để chạy lại)
-- DELETE FROM ChoDat;
-- GO

-- =============================================================
-- I. CHÈN CHO GHẾ MỀM (SE1: Toa 1, 2) - 64 Ghế/Toa
-- =============================================================
WITH Numbers AS (
    SELECT TOP 64 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS STT FROM sys.columns c1, sys.columns c2
)
INSERT INTO [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang])
SELECT 
    t.MaToa + '-C' + FORMAT(n.STT, '00') AS MaCho,
    t.MaToa,
    FORMAT(n.STT, '00') AS SoCho,
    NULL AS Khoang,
    NULL AS Tang
FROM Numbers n
CROSS JOIN (VALUES (N'SE1-1'), (N'SE1-2')) AS t(MaToa);

-- =============================================================
-- II. CHÈN CHO GHẾ CỨNG (SE2: Toa 1, 2) - 64 Ghế/Toa 
-- (Bạn có thể sửa TOP 64 thành 80 nếu muốn ghế cứng nhiều hơn)
-- =============================================================
WITH Numbers AS (
    SELECT TOP 64 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS STT FROM sys.columns c1, sys.columns c2
)
INSERT INTO [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang])
SELECT 
    t.MaToa + '-C' + FORMAT(n.STT, '00') AS MaCho,
    t.MaToa,
    FORMAT(n.STT, '00') AS SoCho,
    NULL AS Khoang,
    NULL AS Tang
FROM Numbers n
CROSS JOIN (VALUES (N'SE2-1'), (N'SE2-2')) AS t(MaToa);

-- =============================================================
-- III. CHÈN CHO GIƯỜNG NẰM (SE1: Toa 3,4,5,6 & SE2: Toa 3,4,5,6)
-- 42 Giường/Toa: 7 Khoang x 6 Giường (Tầng 1, 2, 3)
-- =============================================================
WITH Numbers AS (
    SELECT TOP 42 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS N FROM sys.columns c1, sys.columns c2
),
GiườngLogic AS (
    SELECT 
        N,
        ((N - 1) / 6) + 1 AS Khoang,
        ((N - 1) % 6) / 2 + 1 AS Tang
    FROM Numbers
)
INSERT INTO [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [Khoang], [Tang])
SELECT 
    t.MaToa + '-C' + FORMAT(gl.N, '00') AS MaCho,
    t.MaToa,
    FORMAT(gl.N, '00') AS SoCho,
    gl.Khoang,
    gl.Tang
FROM GiườngLogic gl
CROSS JOIN (VALUES 
    (N'SE1-3'), (N'SE1-4'), (N'SE1-5'), (N'SE1-6'),
    (N'SE2-3'), (N'SE2-4'), (N'SE2-5'), (N'SE2-6')
) AS t(MaToa);

GO