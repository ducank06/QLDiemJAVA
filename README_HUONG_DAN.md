# Project quản lý điểm và sinh viên

Project Java Swing dùng Apache NetBeans Ant project và MySQL. Các form NetBeans `MainMenu`, `QuanLy`, `ThongKe`, `HeThong` được giữ lại; `BangDiem` là JPanel Java Swing được thêm để nhập điểm.

## Chức năng đã nối

- Đăng nhập theo `tai_khoan`, kiểm tra mật khẩu SHA-256 và trạng thái kích hoạt.
- Đổi mật khẩu, đăng xuất; admin quản lý tài khoản.
- Menu Quản lý mở màn hình trong vùng nội dung của cửa sổ chính, gồm danh mục và bảng điểm.
- Thêm, sửa, xóa, tìm kiếm sinh viên, lớp và môn học; các bảng lấy dữ liệu MySQL.
- Nhập và sửa điểm chuyên cần/giữa kỳ/cuối kỳ theo trọng số của môn; tính điểm tổng kết, điểm chữ và điểm hệ 4.
- Không cho sửa hoặc xóa điểm khi học kỳ đã đóng sổ; trigger trong schema cũng kiểm tra điều kiện này.
- Thống kê tín chỉ và GPA tích lũy, xếp loại; có thể in bảng báo cáo.
- Vai trò `ADMIN`, `GIAO_VU`, `GIANG_VIEN`; giảng viên chỉ thấy lớp học phần được phân công và không sửa danh mục.

## Cài đặt

1. Mở MySQL Workbench, mở `QLDiem_schema.sql`, kiểm tra đúng máy chủ/schema rồi chạy. **File schema có lệnh xóa và tạo lại database `qldiem`; nếu đã có dữ liệu cần giữ, hãy sao lưu trước và bỏ phần `DROP DATABASE`/`CREATE DATABASE` trước khi chạy.**
2. Chạy `seed_demo_data.sql` để tạo khoa, ngành, khóa, lớp, môn, học kỳ và lớp học phần minh họa. Không có sinh viên giả; thêm sinh viên trong ứng dụng.
3. Mở thư mục project trong Apache NetBeans. Thư viện MySQL Connector/J đã nằm trong `lib` và được khai báo trong project.
4. Tài khoản quản trị ban đầu: `admin` / `admin123` (do schema tạo). Tài khoản giảng viên minh họa: `giangvien` / `giangvien123`.
5. Tài khoản MySQL mặc định trong ứng dụng là `root` không mật khẩu. Nếu cấu hình MySQL khác, đặt VM Options cho project:

   ```text
   -Dqldiem.user=root -Dqldiem.password=MAT_KHAU_MYSQL
   ```

   Có thể cấu hình URL qua `-Dqldiem.url=jdbc:mysql://localhost:3306/qldiem?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh`.

## Lưu ý

- Form môn học hiện chưa có ô nhập số tín chỉ; môn mới dùng mặc định 3 tín chỉ. Có thể sửa số tín chỉ trực tiếp trong MySQL hoặc bổ sung trường giao diện sau.
- Quy đổi điểm dùng bảng `quy_doi_diem` trong schema. Điều chỉnh bảng này theo quy chế của trường.
- Tính năng chuyên cần tự động cấm thi, import/export Excel, PDF chuyên nghiệp và xét tốt nghiệp là phần nâng cao, chưa triển khai.
