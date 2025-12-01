# TodoApp Backend (Express + MongoDB)

Backend cho ứng dụng Todo (API REST) — viết bằng `Node.js`, `Express` và `MongoDB`.

**Mục tiêu**: cung cấp API cho client (ví dụ Android/Kotlin) bao gồm xác thực JWT (access + refresh), CRUD cho tasks, và các tiện ích như validation và phân trang.

**Tính năng chính**

-   **Xác thực JWT**: access token + refresh token, quay vòng (rotate) refresh token khi refresh.
-   **Task CRUD**: chỉ chủ sở hữu mới thao tác được các task của họ.
-   **Validation**: dùng validators để kiểm tra request body.
-   **Pagination / filter / search / sort** cho danh sách tasks.

**Yêu cầu**

-   Node.js 14+
-   MongoDB (local hoặc Atlas)

**Biến môi trường (ví dụ trong `.env`)**

-   `PORT` — port của server (mặc định `3000`).
-   `MONGO_URI` — kết nối MongoDB (ví dụ `mongodb://localhost:27017/tododb`).
-   `JWT_SECRET` — secret cho access token.
-   `JWT_EXPIRES_IN` — thời hạn access token (ví dụ `15m`).
-   `JWT_REFRESH_SECRET` — secret cho refresh token (nên khác `JWT_SECRET`).
-   `JWT_REFRESH_EXPIRES_IN` — thời hạn refresh token (ví dụ `30d`).
-   `SALT_ROUNDS` — số vòng băm bcrypt (mặc định `10`).
-   `NODE_ENV` — `development` | `production`.

Hãy đảm bảo đặt `JWT_SECRET` và `JWT_REFRESH_SECRET` an toàn trên môi trường production.

**Cài đặt & chạy**

1. Cài dependencies:

```powershell
npm install
```

2. Tạo file cấu hình môi trường từ mẫu (nếu có):

```powershell
copy .env.example .env
# rồi chỉnh giá trị trong .env
```

3. Chạy server:

```powershell
node index.js
```

Hoặc dùng `nodemon` trong development nếu cài sẵn.

**Endpoints chính**

-   `POST /api/auth/register` — đăng ký (thường chỉ bật trong môi trường dev). Body: `{ username, password }`.
-   `POST /api/auth/login` — đăng nhập. Body: `{ username, password }`. Trả về `{ token, refreshToken, user }`.
-   `POST /api/auth/refresh` — đổi `refreshToken` lấy access token mới. Body: `{ refreshToken }`. Nếu refresh token hợp lệ, API trả về `{ token, refreshToken }` (rotate refresh token). Nếu refresh token hết hạn/không hợp lệ → trả 401 → client yêu cầu user đăng nhập lại.
-   `POST /api/auth/logout` — revoke refresh token. Body: `{ refreshToken }`.

-   Các endpoint `tasks` (yêu cầu header `Authorization: Bearer <token>`):
    -   `GET /api/tasks` — lấy danh sách (hỗ trợ `page`, `limit`, `completed`, `q`, `sort`).
    -   `POST /api/tasks` — tạo task `{ title, description }`.
    -   `GET /api/tasks/:id` — lấy chi tiết.
    -   `PUT /api/tasks/:id` — cập nhật.
    -   `DELETE /api/tasks/:id` — xóa.

**Luồng refresh token (cách dùng từ client)**

1. Khi client nhận access token, lưu nó trong bộ nhớ (không nên lưu ở localStorage nếu có thể tránh XSS).
2. Khi server trả lỗi 401 với `{ error: "token_expired" }` từ middleware, client gọi `POST /api/auth/refresh` gửi `refreshToken`.
3. Nếu API trả access token mới và refresh token mới → cập nhật token ở client và tiếp tục request.
4. Nếu `refresh` trả 401 (refresh token hết hạn hoặc bị thu hồi) → buộc người dùng đăng nhập lại.

**Lưu ý bảo mật (đề xuất)**

-   Lưu `refreshToken` trong cookie `HttpOnly`, `Secure`, `SameSite` nếu có client trình duyệt để giảm rủi ro XSS.
-   Lưu hash của `refreshToken` trong DB thay vì lưu thẳng token.
-   Giới hạn số refresh token cho mỗi user (vd. tối đa 5 token) và hỗ trợ revoke toàn bộ khi đổi mật khẩu.

**Kiểm thử nhanh**

-   Các file `call_api/*.http` có thể dùng để gửi request mẫu.
-   Ví dụ PowerShell refresh:

```powershell
Invoke-RestMethod -Uri "http://localhost:3000/api/auth/refresh" -Method Post `
    -Body (@{ refreshToken = "<your-refresh-token>" } | ConvertTo-Json) `
    -ContentType "application/json"
```

Nếu nhận 401 thì cần đăng nhập lại.
