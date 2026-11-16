-- =====================================================================
-- CSDL: QUẢN LÝ ĐIỂM & HỌC SINH - SINH VIÊN   (MySQL 8.0+)
-- Chạy toàn bộ file trong MySQL Workbench / phpMyAdmin
-- =====================================================================
DROP DATABASE IF EXISTS qldiem;
CREATE DATABASE qldiem CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qldiem;

-- ---------------------------------------------------------------------
-- 1. HỆ THỐNG
-- ---------------------------------------------------------------------
CREATE TABLE giang_vien (
    ma_gv     VARCHAR(10)  PRIMARY KEY,
    ho_ten    VARCHAR(100) NOT NULL,
    email     VARCHAR(100),
    sdt       VARCHAR(15)
);

CREATE TABLE tai_khoan (
    ma_tk      INT AUTO_INCREMENT PRIMARY KEY,
    ten_dn     VARCHAR(50)  NOT NULL UNIQUE,
    mat_khau   VARCHAR(64)  NOT NULL,                 -- lưu SHA-256, KHÔNG lưu chữ thường
    vai_tro    ENUM('ADMIN','GIAO_VU','GIANG_VIEN') NOT NULL DEFAULT 'GIAO_VU',
    ma_gv      VARCHAR(10)  NULL,                     -- chỉ có với vai trò GIANG_VIEN
    kich_hoat  TINYINT(1)   NOT NULL DEFAULT 1,
    FOREIGN KEY (ma_gv) REFERENCES giang_vien(ma_gv) ON DELETE SET NULL
);

CREATE TABLE cau_hinh (
    ten_cau_hinh VARCHAR(50) PRIMARY KEY,
    gia_tri      VARCHAR(100) NOT NULL,
    mo_ta        VARCHAR(200)
);

-- ---------------------------------------------------------------------
-- 2. DANH MỤC TỔ CHỨC
-- ---------------------------------------------------------------------
CREATE TABLE khoa (
    ma_khoa  VARCHAR(10)  PRIMARY KEY,
    ten_khoa VARCHAR(100) NOT NULL
);

CREATE TABLE nganh (
    ma_nganh  VARCHAR(10)  PRIMARY KEY,
    ten_nganh VARCHAR(100) NOT NULL,
    ma_khoa   VARCHAR(10)  NOT NULL,
    FOREIGN KEY (ma_khoa) REFERENCES khoa(ma_khoa)
);

CREATE TABLE khoa_hoc (                -- niên khóa, ví dụ K65
    ma_khoa_hoc  VARCHAR(10) PRIMARY KEY,
    nam_bat_dau  INT NOT NULL,
    nam_ket_thuc INT NOT NULL
);

CREATE TABLE lop (                     -- lớp sinh hoạt
    ma_lop      VARCHAR(15)  PRIMARY KEY,
    ten_lop     VARCHAR(100) NOT NULL,
    ma_nganh    VARCHAR(10)  NOT NULL,
    ma_khoa_hoc VARCHAR(10)  NOT NULL,
    FOREIGN KEY (ma_nganh)    REFERENCES nganh(ma_nganh),
    FOREIGN KEY (ma_khoa_hoc) REFERENCES khoa_hoc(ma_khoa_hoc)
);

CREATE TABLE sinh_vien (
    ma_sv      VARCHAR(15)  PRIMARY KEY,
    ho_ten     VARCHAR(100) NOT NULL,
    ngay_sinh  DATE,
    gioi_tinh  ENUM('NAM','NU','KHAC') DEFAULT 'NAM',
    email      VARCHAR(100),
    sdt        VARCHAR(15),
    dia_chi    VARCHAR(200),
    ma_lop     VARCHAR(15)  NOT NULL,
    trang_thai ENUM('DANG_HOC','BAO_LUU','THOI_HOC','TOT_NGHIEP') NOT NULL DEFAULT 'DANG_HOC',
    FOREIGN KEY (ma_lop) REFERENCES lop(ma_lop)
);

-- ---------------------------------------------------------------------
-- 3. MÔN HỌC & KHUNG CHƯƠNG TRÌNH
-- ---------------------------------------------------------------------
CREATE TABLE mon_hoc (
    ma_mon       VARCHAR(10)  PRIMARY KEY,
    ten_mon      VARCHAR(100) NOT NULL,
    so_tin_chi   INT NOT NULL CHECK (so_tin_chi > 0),
    tong_so_buoi INT NOT NULL DEFAULT 15,
    trong_so_cc  INT NOT NULL DEFAULT 10,   -- % chuyên cần
    trong_so_gk  INT NOT NULL DEFAULT 30,   -- % giữa kỳ
    trong_so_ck  INT NOT NULL DEFAULT 60,   -- % cuối kỳ
    CONSTRAINT chk_trong_so CHECK (trong_so_cc + trong_so_gk + trong_so_ck = 100)
);

CREATE TABLE khung_ctdt (                -- khung chương trình đào tạo
    ma_nganh         VARCHAR(10) NOT NULL,
    ma_khoa_hoc      VARCHAR(10) NOT NULL,
    ma_mon           VARCHAR(10) NOT NULL,
    loai_mon         ENUM('BAT_BUOC','TU_CHON') NOT NULL DEFAULT 'BAT_BUOC',
    hoc_ky_du_kien   INT,
    PRIMARY KEY (ma_nganh, ma_khoa_hoc, ma_mon),
    FOREIGN KEY (ma_nganh)    REFERENCES nganh(ma_nganh),
    FOREIGN KEY (ma_khoa_hoc) REFERENCES khoa_hoc(ma_khoa_hoc),
    FOREIGN KEY (ma_mon)      REFERENCES mon_hoc(ma_mon)
);

CREATE TABLE yeu_cau_tot_nghiep (        -- ngưỡng xét tốt nghiệp theo ngành/khóa
    ma_nganh          VARCHAR(10) NOT NULL,
    ma_khoa_hoc       VARCHAR(10) NOT NULL,
    tc_bat_buoc       INT NOT NULL,      -- tổng TC bắt buộc phải đạt
    tc_tu_chon        INT NOT NULL,      -- tổng TC tự chọn tối thiểu
    gpa_toi_thieu     DECIMAL(3,2) NOT NULL DEFAULT 2.00,
    PRIMARY KEY (ma_nganh, ma_khoa_hoc),
    FOREIGN KEY (ma_nganh)    REFERENCES nganh(ma_nganh),
    FOREIGN KEY (ma_khoa_hoc) REFERENCES khoa_hoc(ma_khoa_hoc)
);

-- ---------------------------------------------------------------------
-- 4. HỌC KỲ, LỚP HỌC PHẦN, ĐIỂM, ĐIỂM DANH
-- ---------------------------------------------------------------------
CREATE TABLE hoc_ky (
    ma_hk       VARCHAR(10)  PRIMARY KEY,        -- vd: 2025_1
    ten_hk      VARCHAR(50)  NOT NULL,
    nam_hoc     VARCHAR(9)   NOT NULL,           -- vd: 2025-2026
    da_dong_so  TINYINT(1)   NOT NULL DEFAULT 0  -- 1 = khóa không cho sửa điểm
);

CREATE TABLE lop_hoc_phan (
    ma_lhp  VARCHAR(20) PRIMARY KEY,
    ma_mon  VARCHAR(10) NOT NULL,
    ma_hk   VARCHAR(10) NOT NULL,
    ma_gv   VARCHAR(10),
    si_so   INT DEFAULT 0,
    FOREIGN KEY (ma_mon) REFERENCES mon_hoc(ma_mon),
    FOREIGN KEY (ma_hk)  REFERENCES hoc_ky(ma_hk),
    FOREIGN KEY (ma_gv)  REFERENCES giang_vien(ma_gv) ON DELETE SET NULL
);

CREATE TABLE quy_doi_diem (              -- bảng quy đổi hệ 10 -> chữ -> hệ 4
    diem_chu   VARCHAR(2)   PRIMARY KEY,
    tu_diem    DECIMAL(3,1) NOT NULL,
    den_diem   DECIMAL(3,1) NOT NULL,
    diem_he4   DECIMAL(2,1) NOT NULL,
    xep_loai   VARCHAR(20)  NOT NULL
);

CREATE TABLE bang_diem (                 -- 1 dòng = 1 sinh viên trong 1 lớp học phần
    ma_lhp    VARCHAR(20) NOT NULL,
    ma_sv     VARCHAR(15) NOT NULL,
    diem_cc   DECIMAL(4,2) NULL CHECK (diem_cc BETWEEN 0 AND 10),
    diem_gk   DECIMAL(4,2) NULL CHECK (diem_gk BETWEEN 0 AND 10),
    diem_ck   DECIMAL(4,2) NULL CHECK (diem_ck BETWEEN 0 AND 10),
    diem_tk   DECIMAL(4,2) NULL,         -- hệ 10, Java tính theo trọng số
    diem_chu  VARCHAR(2)   NULL,
    diem_he4  DECIMAL(2,1) NULL,
    cam_thi   TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (ma_lhp, ma_sv),
    FOREIGN KEY (ma_lhp) REFERENCES lop_hoc_phan(ma_lhp) ON DELETE CASCADE,
    FOREIGN KEY (ma_sv)  REFERENCES sinh_vien(ma_sv)     ON DELETE CASCADE,
    FOREIGN KEY (diem_chu) REFERENCES quy_doi_diem(diem_chu)
);

CREATE TABLE diem_danh (                 -- 1 dòng = 1 buổi học của 1 sinh viên
    ma_lhp     VARCHAR(20) NOT NULL,
    ma_sv      VARCHAR(15) NOT NULL,
    buoi_so    INT         NOT NULL,
    ngay_hoc   DATE,
    trang_thai ENUM('CO_MAT','VANG_CO_PHEP','VANG_KHONG_PHEP') NOT NULL DEFAULT 'CO_MAT',
    PRIMARY KEY (ma_lhp, ma_sv, buoi_so),
    FOREIGN KEY (ma_lhp, ma_sv) REFERENCES bang_diem(ma_lhp, ma_sv) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 5. TRIGGER: KHÓA SỬA ĐIỂM KHI HỌC KỲ ĐÃ ĐÓNG SỔ
-- ---------------------------------------------------------------------
DELIMITER //
CREATE TRIGGER trg_diem_khoa_insert BEFORE INSERT ON bang_diem
FOR EACH ROW
BEGIN
    IF (SELECT h.da_dong_so FROM lop_hoc_phan l JOIN hoc_ky h ON h.ma_hk = l.ma_hk
        WHERE l.ma_lhp = NEW.ma_lhp) = 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Hoc ky da dong so, khong duoc nhap diem';
    END IF;
END//

CREATE TRIGGER trg_diem_khoa_update BEFORE UPDATE ON bang_diem
FOR EACH ROW
BEGIN
    IF (SELECT h.da_dong_so FROM lop_hoc_phan l JOIN hoc_ky h ON h.ma_hk = l.ma_hk
        WHERE l.ma_lhp = OLD.ma_lhp) = 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Hoc ky da dong so, khong duoc sua diem';
    END IF;
END//
DELIMITER ;

-- ---------------------------------------------------------------------
-- 6. VIEW HỖ TRỢ CÁC CHỨC NĂNG TỰ ĐỘNG
-- ---------------------------------------------------------------------
-- 6.1 Tỷ lệ nghỉ -> phục vụ "quét cấm thi"
CREATE VIEW v_ty_le_nghi AS
SELECT d.ma_lhp, d.ma_sv, sv.ho_ten,
       SUM(d.trang_thai <> 'CO_MAT')                         AS so_buoi_nghi,
       m.tong_so_buoi,
       ROUND(SUM(d.trang_thai <> 'CO_MAT') * 100 / m.tong_so_buoi, 1) AS ty_le_nghi,
       (SUM(d.trang_thai <> 'CO_MAT') * 100 / m.tong_so_buoi
            > (SELECT CAST(gia_tri AS DECIMAL(5,2)) FROM cau_hinh WHERE ten_cau_hinh = 'NGUONG_NGHI')
       ) AS vuot_nguong
FROM diem_danh d
JOIN sinh_vien sv   ON sv.ma_sv = d.ma_sv
JOIN lop_hoc_phan l ON l.ma_lhp = d.ma_lhp
JOIN mon_hoc m      ON m.ma_mon = l.ma_mon
GROUP BY d.ma_lhp, d.ma_sv, sv.ho_ten, m.tong_so_buoi;

-- 6.2 Kết quả tốt nhất của từng sinh viên theo từng môn (xử lý học lại)
CREATE VIEW v_ket_qua_mon AS
SELECT bd.ma_sv, l.ma_mon, MAX(bd.diem_he4) AS diem_he4
FROM bang_diem bd
JOIN lop_hoc_phan l ON l.ma_lhp = bd.ma_lhp
WHERE bd.diem_he4 IS NOT NULL
GROUP BY bd.ma_sv, l.ma_mon;

-- 6.3 GPA tích lũy toàn khóa (hệ 4)
CREATE VIEW v_gpa_tich_luy AS
SELECT kq.ma_sv,
       SUM(m.so_tin_chi)                                       AS tong_tin_chi,
       ROUND(SUM(kq.diem_he4 * m.so_tin_chi) / SUM(m.so_tin_chi), 2) AS gpa
FROM v_ket_qua_mon kq
JOIN mon_hoc m ON m.ma_mon = kq.ma_mon
GROUP BY kq.ma_sv;

-- 6.4 Xét điều kiện tốt nghiệp
CREATE VIEW v_xet_tot_nghiep AS
SELECT sv.ma_sv, sv.ho_ten, l.ma_nganh, l.ma_khoa_hoc,
       y.tc_bat_buoc, y.tc_tu_chon, y.gpa_toi_thieu,
       COALESCE(SUM(CASE WHEN k.loai_mon = 'BAT_BUOC' AND kq.diem_he4 >= 1.0
                         THEN m.so_tin_chi END), 0) AS tc_bat_buoc_dat,
       COALESCE(SUM(CASE WHEN k.loai_mon = 'TU_CHON'  AND kq.diem_he4 >= 1.0
                         THEN m.so_tin_chi END), 0) AS tc_tu_chon_dat,
       ROUND(SUM(kq.diem_he4 * m.so_tin_chi) /
             NULLIF(SUM(CASE WHEN kq.diem_he4 IS NOT NULL THEN m.so_tin_chi END), 0), 2) AS gpa
FROM sinh_vien sv
JOIN lop l                ON l.ma_lop = sv.ma_lop
JOIN yeu_cau_tot_nghiep y ON y.ma_nganh = l.ma_nganh AND y.ma_khoa_hoc = l.ma_khoa_hoc
LEFT JOIN khung_ctdt k    ON k.ma_nganh = l.ma_nganh AND k.ma_khoa_hoc = l.ma_khoa_hoc
LEFT JOIN mon_hoc m       ON m.ma_mon = k.ma_mon
LEFT JOIN v_ket_qua_mon kq ON kq.ma_sv = sv.ma_sv AND kq.ma_mon = k.ma_mon
GROUP BY sv.ma_sv, sv.ho_ten, l.ma_nganh, l.ma_khoa_hoc,
         y.tc_bat_buoc, y.tc_tu_chon, y.gpa_toi_thieu;
-- Đủ điều kiện khi: tc_bat_buoc_dat >= tc_bat_buoc
--               và tc_tu_chon_dat  >= tc_tu_chon
--               và gpa             >= gpa_toi_thieu

-- ---------------------------------------------------------------------
-- 7. DỮ LIỆU MẪU TỐI THIỂU
-- ---------------------------------------------------------------------
INSERT INTO quy_doi_diem VALUES
 ('A' , 8.5, 10.0, 4.0, 'Xuat sac / Gioi'),
 ('B+', 8.0,  8.4, 3.5, 'Kha'),
 ('B' , 7.0,  7.9, 3.0, 'Kha'),
 ('C+', 6.5,  6.9, 2.5, 'Trung binh'),
 ('C' , 5.5,  6.4, 2.0, 'Trung binh'),
 ('D+', 5.0,  5.4, 1.5, 'Yeu'),
 ('D' , 4.0,  4.9, 1.0, 'Yeu'),
 ('F' , 0.0,  3.9, 0.0, 'Kem');          -- chỉnh lại theo quy chế trường bạn

INSERT INTO cau_hinh VALUES
 ('NGUONG_NGHI', '20', 'Ty le % vang toi da, vuot qua se bi cam thi');

INSERT INTO tai_khoan (ten_dn, mat_khau, vai_tro)
VALUES ('admin', SHA2('admin123', 256), 'ADMIN');
