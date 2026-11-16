USE qldiem;

-- Danh mục tối thiểu để có thể tạo sinh viên và thử nhập điểm.
-- INSERT IGNORE giúp chạy lại file này không tạo bản ghi trùng.
INSERT IGNORE INTO khoa(ma_khoa,ten_khoa) VALUES ('CNTT','Công nghệ thông tin');
INSERT IGNORE INTO nganh(ma_nganh,ten_nganh,ma_khoa) VALUES ('HTTT','Hệ thống thông tin','CNTT');
INSERT IGNORE INTO khoa_hoc(ma_khoa_hoc,nam_bat_dau,nam_ket_thuc) VALUES ('K2026',2026,2030);
INSERT IGNORE INTO lop(ma_lop,ten_lop,ma_nganh,ma_khoa_hoc) VALUES ('HTTT-K2026','Hệ thống thông tin K2026','HTTT','K2026');

INSERT IGNORE INTO mon_hoc(ma_mon,ten_mon,so_tin_chi,tong_so_buoi,trong_so_cc,trong_so_gk,trong_so_ck)
VALUES ('MH_DEMO','Môn học minh họa',3,15,10,30,60);
INSERT IGNORE INTO hoc_ky(ma_hk,ten_hk,nam_hoc,da_dong_so)
VALUES ('2026_1','Học kỳ 1','2026-2027',0);
INSERT IGNORE INTO giang_vien(ma_gv,ho_ten,email,sdt)
VALUES ('GV_DEMO','Giảng viên minh họa',NULL,NULL);
INSERT IGNORE INTO lop_hoc_phan(ma_lhp,ma_mon,ma_hk,ma_gv,si_so)
VALUES ('LHP_DEMO','MH_DEMO','2026_1','GV_DEMO',0);

-- Tài khoản trình diễn, dùng để thử chức năng giảng viên.
INSERT IGNORE INTO tai_khoan(ten_dn,mat_khau,vai_tro,ma_gv,kich_hoat)
VALUES ('giangvien',SHA2('giangvien123',256),'GIANG_VIEN','GV_DEMO',1);
