# Employee Management System - JWT Security

## 1. Access Token và Refresh Token

Access Token là token có thời gian sống ngắn, thường dùng để truy cập trực tiếp vào các API nghiệp vụ của hệ thống. Token này thường được gửi trong header Authorization theo dạng Bearer Token. Trong bài này, Access Token chứa thông tin username và danh sách quyền của nhân viên. Khi tạo danh sách quyền, hệ thống sử dụng Java Stream API để chuyển đổi danh sách Role thành danh sách quyền trong payload JWT.

Refresh Token là token có thời gian sống dài hơn, dùng để xin cấp lại Access Token mới khi Access Token hết hạn. Refresh Token không nên được dùng để gọi trực tiếp API nghiệp vụ. Vì thời gian sống dài hơn nên Refresh Token cần được bảo vệ nghiêm ngặt hơn, ví dụ lưu trong HttpOnly Cookie hoặc khu vực lưu trữ an toàn phía Client.

## 2. Rủi ro khi bị lộ Token

Nếu Access Token bị lộ, kẻ tấn công có thể dùng token này để gọi API cho đến khi token hết hạn. Tuy nhiên, do Access Token có vòng đời ngắn nên mức độ rủi ro được giới hạn. Ngoài ra, hệ thống còn lưu trạng thái token trong bảng Token, nhờ đó có thể thu hồi Access Token trước thời điểm hết hạn bằng cách cập nhật revoked = true hoặc expired = true.

Nếu Refresh Token bị lộ, rủi ro nghiêm trọng hơn vì kẻ tấn công có thể dùng nó để xin Access Token mới trong thời gian dài. Để giảm rủi ro này, hệ thống lưu Refresh Token trong database và kiểm tra trạng thái revoked, expired trước khi cấp token mới. Khi người dùng đăng xuất, toàn bộ token còn hiệu lực của nhân viên sẽ được cập nhật revoked = true và expired = true thay vì xóa cứng, giúp vừa vô hiệu hóa token, vừa giữ lại dữ liệu phục vụ audit log.

## 3. Cơ chế bảo vệ

Hệ thống sử dụng Spring Security với cơ chế Stateless, không lưu session trên server. Mỗi request gửi đến API nghiệp vụ đều phải có Access Token hợp lệ. Bộ lọc JWT sẽ kiểm tra chữ ký, thời hạn token và trạng thái token trong database. Nếu token không tồn tại, đã bị thu hồi hoặc đã hết hạn, request sẽ bị từ chối với mã lỗi 401 Unauthorized.