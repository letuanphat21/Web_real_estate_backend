# Hướng dẫn code module Jobs

Tài liệu này dành cho bạn đọc logic rồi tự dán code vào repo. Mỗi file có: đường dẫn, phần logic cần hiểu, và code đầy đủ.

> Cập nhật 2026-10-09 theo trạng thái repo hiện tại. Phần Notifications và phần lớn core đã có trong repo nên đã được bỏ khỏi guide này.

**Code Jobs trong tài liệu này đã được kiểm chứng** trên một bản sao tạm của repo: `compile` sạch, và chạy một test tích hợp qua HTTP (Spring Security và JWT thật, DB H2) phủ đủ các API. Test nằm ở phụ lục (mục 4) nếu bạn muốn chạy lại. Từ lúc đó đến nay entity, enum, DTO của Jobs và chữ ký `NotificationService.send()` trong repo **không đổi**, nên code vẫn khớp.

---

## Trạng thái hiện tại của repo

| Phần | Trạng thái | Việc của bạn |
|---|---|---|
| Entity, enum, DTO của Jobs (20 file) | Đã có | Không cần làm |
| Core: `PageResponse`, `ErrorCode` (mã lỗi Jobs, Notification, FILE_NOT_PDF), `Endpoints`, `SecurityConfig` (POST/PATCH/DELETE admin, CORS có PATCH), `GlobalExceptionHandler` (file quá lớn) | Đã có | Không cần làm |
| Module Notifications (controller, service, dao, DTO) và `NotificationService.send()` | Đã có | Không cần làm, chỉ gọi `send()` |
| `CloudinaryService.uploadPdf` (upload CV dạng PDF) | **Chưa có** | Làm ở mục 1 |
| `application.yaml`: giới hạn upload 10MB | **Chưa có** | Làm ở mục 1 |
| Jobs: `dao` (5), `service` (4 interface + 4 impl), `controller` (7) | **Chưa có** | Làm ở mục 2 |

---

## 0. Đọc trước

### 0.1 Quy ước của repo (bám theo, không đổi cấu trúc)
| Chủ đề | Quy ước đang dùng | Nguồn tham chiếu |
|---|---|---|
| Cấu trúc module | `modules/<tên>/{controller, service, service/serviceimpl, dao, dto/request, dto/response, entity, enums}` | module `user` |
| Interface và implementation | `XService` ở `service/`, `XServiceImpl` ở `service/serviceimpl/` | `UserService`, `UserServiceImpl` |
| Repository | đặt ở `dao/`, kế thừa `JpaRepository`, gắn `@Repository` | `UserRepository` |
| Response | mọi API trả `ResponseEntity<ApiResponse<T>>` bằng `ApiResponse.ok / created` | `core/common/ApiResponse` |
| Lỗi | `throw new AppException(ErrorCode.XXX)`, mã lỗi khai báo trong `ErrorCode` | `core/exception` |
| Validate | `@Validated @RequestBody` và message tiếng Việt trong annotation | `LoginRequest` |
| Tham chiếu sang module khác | chỉ lưu `Long userId`, không import entity `User` | `Job.createdBy`, `UserNotification.userId` |
| Lombok | `@RequiredArgsConstructor` để inject, `@Getter/@Setter/@Builder` cho DTO | toàn repo |
| Ba loại field, mỗi loại một việc | `is_active` = ẩn/hiện; `deleted_at` = xóa mềm; `status` = trạng thái nghiệp vụ. Không dùng chồng lên nhau | quy ước mới của team |
| Xóa mềm | entity có `@SQLRestriction("deleted_at IS NULL")`: gán `deletedAt` là mọi truy vấn sau đó tự bỏ qua bản ghi | `Job`, `Application` |
| Gọi service module khác | được phép gọi qua **interface service** (như `JwtAuthFilter` gọi `UserService`) | `core/security` |

Bảng áp dụng cụ thể cho hai module:

| Bảng | `is_active` (ẩn/hiện) | `deleted_at` (xóa mềm) | `status` (nghiệp vụ) |
|---|---|---|---|
| `Notification_Types` | có | có | không |
| `Notification` | không | có | không |
| `user_notifications` | không | `is_deleted` (người dùng tự gỡ) | không |
| `Job_Types` | có | có | không |
| `Jobs` | không | có | `status` (DRAFT, OPEN, PAUSED, CLOSED) |
| `CVs` | không | có | không |
| `Applications` | không | có | `status` (PENDING...WITHDRAWN) |
| `Application_Status_Logs` | không | không (là log, không có thao tác xóa) | không |

Hệ quả: "ẩn" job nghĩa là đổi `status` sang PAUSED/CLOSED; thông báo và CV không có nút ẩn, chỉ xóa.

### 0.2 Cách lấy "ai đang đăng nhập"

Repo chưa có hàm tiện ích, và `JwtAuthFilter` đặt principal là `UserDetails` với `username = email`. Nên mỗi controller làm như sau:

```java
@GetMapping("/something")
public ResponseEntity<...> something(Authentication authentication) {
    Long userId = userService.findByEmailOrPhone(authentication.getName()).getId();
    ...
}
```

### 0.3 Phân quyền admin

Mọi API admin nằm dưới `/api/admin/**`. `SecurityConfig` đã có dòng `hasRole("ADMIN")` cho GET, POST, PUT, PATCH, DELETE. Controller admin **không** cần tự kiểm tra quyền.

### 0.4 Danh sách API Jobs: 29

| # | Method và đường dẫn | Ai dùng | Mô tả |
|---|---|---|---|
| 1 | `GET /api/job-types` | công khai | Loại công việc đang hiện |
| 2 | `GET /api/admin/job-types` | admin | Tất cả loại |
| 3 | `GET /api/admin/job-types/{id}` | admin | Chi tiết |
| 4 | `POST /api/admin/job-types` | admin | Tạo |
| 5 | `PUT /api/admin/job-types/{id}` | admin | Sửa |
| 6 | `DELETE /api/admin/job-types/{id}` | admin | Xóa mềm (chặn nếu còn job đang dùng) |
| 7 | `PATCH /api/admin/job-types/{id}/toggle` | admin | Ẩn/hiện |
| 8 | `GET /api/jobs?keyword=&jobTypeId=&type=&location=&page=&size=` | công khai | Danh sách job đang tuyển, còn hạn |
| 9 | `GET /api/jobs/{id}` | công khai | Chi tiết (không trả job nháp) |
| 10 | `GET /api/admin/jobs?keyword=&status=&page=&size=` | admin | Tất cả job |
| 11 | `GET /api/admin/jobs/{id}` | admin | Chi tiết |
| 12 | `POST /api/admin/jobs` | admin | Tạo (trạng thái DRAFT) |
| 13 | `PUT /api/admin/jobs/{id}` | admin | Sửa |
| 14 | `DELETE /api/admin/jobs/{id}` | admin | Xóa mềm (kèm xóa mềm các đơn của job) |
| 15 | `PATCH /api/admin/jobs/{id}/status` | admin | Đổi trạng thái (đăng, tạm dừng, đóng) |
| 16 | `POST /api/me/cvs` | đăng nhập | Upload CV (PDF, form-data) |
| 17 | `GET /api/me/cvs` | đăng nhập | CV của tôi |
| 18 | `GET /api/me/cvs/{id}` | đăng nhập | Chi tiết |
| 19 | `DELETE /api/me/cvs/{id}` | đăng nhập | Xóa mềm |
| 20 | `POST /api/jobs/{jobId}/applications` | đăng nhập | Nộp đơn |
| 21 | `GET /api/me/applications` | đăng nhập | Đơn của tôi |
| 22 | `GET /api/me/applications/{id}` | đăng nhập | Chi tiết đơn của tôi |
| 23 | `PATCH /api/me/applications/{id}/withdraw` | đăng nhập | Rút đơn |
| 24 | `GET /api/admin/applications?jobId=&status=&keyword=` | admin | Tất cả đơn |
| 25 | `GET /api/admin/jobs/{jobId}/applications` | admin | Đơn theo job |
| 26 | `GET /api/admin/applications/{id}` | admin | Chi tiết |
| 27 | `PATCH /api/admin/applications/{id}/status` | admin | Đổi trạng thái (ghi log, gửi thông báo) |
| 28 | `DELETE /api/admin/applications/{id}` | admin | Xóa mềm |
| 29 | `GET /api/admin/applications/{id}/status-logs` | admin | Lịch sử đổi trạng thái |

Các API Notifications (17 API) đã làm xong, chỉ cần gọi `NotificationService.send(...)` khi cần thông báo.

### 0.5 Những điểm cần chốt với team

1. **Quy ước `is_deleted` của bảng `users` bị đảo so với tên cột** (leader chốt): `is_deleted = true` là tài khoản bình thường, `false` là bị admin khóa. `UserServiceImpl.loadUserByUsername` đang đúng quy ước này, **đừng sửa**. Hệ quả cho bạn: khi tạo user để test (SQL hoặc code) phải đặt `is_deleted = true`, nếu để `false` thì đăng nhập báo "Tài khoản đã bị admin khóa". Quy ước này **chỉ áp dụng cho `users`**. Bảng `user_notifications` vẫn theo nghĩa thường (`is_deleted = false` là còn hiện).
2. **ERD cần cập nhật theo code** (code đã làm, ERD chưa có):
   - `Jobs` thêm hai cột `employment_type` và `experience_level`. DTO có `type` và `experienceLevel` nhưng ERD không có chỗ lưu. Nếu team muốn bám ERD thuần thì bỏ hai trường này khỏi DTO.
   - `Applications.status` có thêm giá trị `WITHDRAWN` (ứng viên rút đơn).
3. **Tên loại thông báo tự động**: `send()` tự tạo loại tên `"Tuyển dụng"` (hằng số `NotificationService.TYPE_JOB`) nếu chưa có. Nếu admin đổi tên loại này thì lần gửi sau sẽ tạo loại mới. Muốn chắc thì tạo sẵn loại này và đừng đổi tên.
4. **Tên role admin phải là `ROLE_ADMIN`** (khớp `hasRole("ADMIN")`). User thường là `ROLE_USER`.
5. **Ẩn loại thông báo = ẩn luôn thông báo của loại đó khỏi người dùng.** Nếu admin ẩn loại "Tuyển dụng" (`is_active = false`) thì thông báo tự động từ Jobs vẫn được tạo, nhưng người dùng không thấy cho tới khi bật lại loại đó.
6. **Mục 1 sửa file dùng chung** (`CloudinaryService`, `CloudinaryServiceImpl`, `application.yaml`). Nên báo team và làm thành một commit riêng để người khác dễ merge.

### 0.6 Thứ tự làm và điểm kiểm tra

Sau mỗi bước chạy `./mvnw -q -DskipTests compile` (trên Windows PowerShell: `.\mvnw.cmd -q -DskipTests compile`). Không in gì ra là sạch.

1. **Mục 1**: Cloudinary `uploadPdf` và `application.yaml` (một commit riêng).
2. **Mục 2**: Jobs, theo thứ tự `dao` → `service` (interface) → `serviceimpl` → `controller`. Làm `JobType` trước, rồi `Job`, rồi `Cv`, cuối cùng `Application` (vì `Application` cần cả Job, Cv và `send()`).
3. **Mục 3**: chạy thử bằng Postman.

---

## 1. Core: chỉ còn hai việc

Các phần core khác (`PageResponse`, `ErrorCode`, `Endpoints`, `SecurityConfig`, `GlobalExceptionHandler`) đã có trong repo. Chỉ còn hai việc dưới đây, làm trước khi viết Jobs.


### `core/cloudinary/service/CloudinaryService.java`  — SỬA (thêm dòng `+`, bỏ dòng `-`)

- Cloudinary hiện chỉ nhận ảnh (kiểm tra `image/*`). CV là PDF nên thêm hàm `uploadPdf`.

```diff
@@ -16,4 +16,7 @@ public interface CloudinaryService {
     List<String> uploadImages(List<MultipartFile> files, String folder);
 
+    /** Upload 1 file PDF (dùng cho CV), trả về URL (https). Xóa bằng deleteByUrl như ảnh. */
+    String uploadPdf(MultipartFile file, String folder);
+
     /** Xóa ảnh theo URL (bỏ qua nếu URL không thuộc Cloudinary). */
     void deleteByUrl(String url);
```

### `core/cloudinary/service/CloudinaryServiceImpl.java`  — SỬA (thêm dòng `+`, bỏ dòng `-`)

- `uploadPdf` kiểm tra file không rỗng, `Content-Type` phải là `application/pdf`, tối đa 5MB, rồi upload với `resource_type = image` (Cloudinary coi PDF là một loại ảnh).
- Vì cùng `resource_type`, hàm `deleteByUrl` có sẵn xóa được PDF mà không phải sửa gì.
- Nếu mở link PDF trên Cloudinary bị lỗi 401, vào Cloudinary Settings > Security và bật tùy chọn cho phép gửi file PDF (tài khoản mới thường tắt mặc định).

```diff
@@ -23,4 +23,5 @@ public class CloudinaryServiceImpl implements CloudinaryService {
 
     private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB / ảnh
+    private static final long MAX_PDF_SIZE = 5L * 1024 * 1024; // 5MB / PDF
 
     private final Cloudinary cloudinary;
@@ -39,4 +40,26 @@ public class CloudinaryServiceImpl implements CloudinaryService {
     }
 
+    @Override
+    public String uploadPdf(MultipartFile file, String folder) {
+        if (file == null || file.isEmpty()) {
+            throw new AppException(ErrorCode.FILE_EMPTY);
+        }
+        if (!"application/pdf".equals(file.getContentType())) {
+            throw new AppException(ErrorCode.FILE_NOT_PDF);
+        }
+        if (file.getSize() > MAX_PDF_SIZE) {
+            throw new AppException(ErrorCode.FILE_TOO_LARGE);
+        }
+        try {
+            // PDF vẫn upload với resource_type "image" để deleteByUrl xóa được như ảnh
+            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
+                    ObjectUtils.asMap("folder", folder, "resource_type", "image"));
+            return (String) result.get("secure_url");
+        } catch (IOException e) {
+            log.error("Upload PDF thất bại: {}", e.getMessage());
+            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
+        }
+    }
+
     @Override
     public List<String> uploadImages(List<MultipartFile> files, String folder) {
```

### `src/main/resources/application.yaml`  — SỬA (thêm dòng `+`, bỏ dòng `-`)

- Nâng giới hạn upload từ 1MB mặc định lên 10MB mỗi file. Nếu không có dòng này, upload ảnh lớn và CV đều bị từ chối.

```diff
@@ -4,2 +4,6 @@ spring:
   profiles:
     active: dev
+  servlet:
+    multipart:
+      max-file-size: 10MB
+      max-request-size: 20MB
```


**Điểm kiểm tra 1:** `./mvnw -q -DskipTests compile` không báo lỗi.

---

## 2. Module Jobs

### Gọi thông báo từ Jobs

`NotificationService` đã có sẵn trong repo, `ApplicationServiceImpl` chỉ cần inject và gọi:

```java
notificationService.send(
        userId,                         // người nhận
        NotificationService.TYPE_JOB,   // tên loại (tab) "Tuyển dụng", tự tạo nếu chưa có
        "Tiêu đề", "Nội dung",
        TargetType.APPLICATION,         // bấm vào thông báo thì mở đối tượng nào
        applicationId,
        "/applications/" + applicationId); // actionUrl, có thể null
```

Tên loại quyết định thông báo nằm ở **tab** nào, còn `TargetType` và `targetId` chỉ là đối tượng mở ra khi bấm.


Luồng dữ liệu cần nhớ:

- `Job` có `status`: `DRAFT` (nháp), `OPEN` (đang tuyển), `PAUSED` (tạm dừng), `CLOSED` (đóng). Public chỉ thấy job `OPEN` còn hạn. "Ẩn" một job nghĩa là đổi `status`, không cần cột riêng.
- `Job` và `Application` có `@SQLRestriction("deleted_at IS NULL")`: xóa mềm bằng cách gán `deletedAt`, mọi truy vấn sau đó tự bỏ qua bản ghi đã xóa.
- Khi ứng viên nộp đơn, hệ thống **chụp lại** thông tin file CV vào `Application` (`cvUrl`, `cvFileName`...). Sau này ứng viên xóa CV thì đơn vẫn còn file cho HR xem.
- Mọi lần đổi trạng thái đơn đều ghi một dòng `ApplicationStatusLog` (kể cả lần nộp đơn đầu tiên và lần rút đơn).
- Admin đổi trạng thái đơn thì ứng viên nhận thông báo (qua `NotificationService.send`).

### 2.1 Entity, enum, DTO

Đã cập nhật sẵn vào repo (20 file trong `modules/Jobs/{entity,enums,dto}`), bỏ qua bước này.

Xóa mềm trong module này:

- `Job`, `Application`, `JobType`, `Cv` có `deletedAt`. Xóa là gán `deletedAt`, truy vấn sau đó tự bỏ qua.
- **Xóa mềm một job thì các đơn của job cũng bị xóa mềm theo.** Nếu không, đơn còn sống sẽ trỏ tới job đã "biến mất" và danh sách đơn văng lỗi 500 (`No row with the given identifier exists for entity Job`). Lỗi này do test bắt được.
- Xóa mềm CV thì gỡ liên kết `cv_id` khỏi các đơn, nhưng đơn vẫn giữ `cvUrl`, và file trên Cloudinary được giữ nguyên.
- Xóa mềm loại công việc chỉ cho phép khi không còn job nào đang dùng.


### 2.2 Repository (`dao`)

### `modules/Jobs/dao/JobTypeRepository.java`  — TẠO MỚI

- `findAllActive`: loại đang hiện, cho bộ lọc phía public.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.Jobs.entity.JobType;

@Repository
public interface JobTypeRepository extends JpaRepository<JobType, Long> {

    /** Chỉ lấy loại đang hiện, dùng cho bộ lọc phía public. */
    @Query("select t from JobType t where t.isActive = true order by t.id")
    List<JobType> findAllActive();
}
```

### `modules/Jobs/dao/JobRepository.java`  — TẠO MỚI

- Kế thừa thêm `JpaSpecificationExecutor` để lọc nhiều điều kiện tùy chọn (từ khóa, loại, địa điểm...) mà không phải viết tổ hợp method.
- `existsByJobTypeId`: còn job nào (chưa xóa mềm) dùng loại này không, để chặn xóa loại.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.Jobs.entity.Job;

// JpaSpecificationExecutor cho phép lọc nhiều điều kiện tùy chọn (từ khóa, loại, địa điểm...)
@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    /** Còn job nào (chưa xóa mềm) đang dùng loại này không. */
    boolean existsByJobTypeId(Long jobTypeId);
}
```

### `modules/Jobs/dao/CvRepository.java`  — TẠO MỚI

- `findByIdAndUserId`: luôn tìm kèm `userId` để người này không đụng được CV của người khác.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.Jobs.entity.Cv;

@Repository
public interface CvRepository extends JpaRepository<Cv, Long> {

    List<Cv> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Tìm theo cả userId để người này không đụng được vào CV của người khác. */
    Optional<Cv> findByIdAndUserId(Long id, Long userId);
}
```

### `modules/Jobs/dao/ApplicationRepository.java`  — TẠO MỚI

- `existsByJobIdAndUserIdAndStatusNot(..., WITHDRAWN)`: chặn nộp trùng, nhưng đơn đã rút thì cho nộp lại.
- `detachCv`: gỡ liên kết CV khỏi mọi đơn trước khi xóa mềm CV, để đơn còn sống không trỏ tới CV đã ẩn. Đơn vẫn giữ `cvUrl`. Dùng SQL thuần để cả đơn đã xóa mềm cũng được gỡ.
- `softDeleteByJobId`: xóa mềm hàng loạt các đơn của một job khi job bị xóa mềm. Cũng cần `flushAutomatically = true` để lệnh update chạy sau khi đã ghi các thay đổi đang chờ, và `clearAutomatically = true` để làm sạch cache. Xem `UserNotificationRepository` trong repo làm mẫu.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.dao;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.Jobs.entity.Application;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ApplicationStatus;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    /** Đã nộp đơn vào job này chưa (đơn đã rút thì được nộp lại). */
    boolean existsByJobIdAndUserIdAndStatusNot(Long jobId, Long userId, ApplicationStatus status);

    Page<Application> findByUserId(Long userId, Pageable pageable);

    /** Tìm theo cả userId để ứng viên chỉ xem/rút được đơn của chính mình. */
    Optional<Application> findByIdAndUserId(Long id, Long userId);

    /**
     * Gỡ liên kết CV khỏi mọi đơn (kể cả đơn đã xóa mềm) trước khi xóa CV.
     * Đơn vẫn giữ nguyên cv_url để HR còn xem được file đã nộp.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "update applications set cv_id = null where cv_id = :cvId", nativeQuery = true)
    void detachCv(@Param("cvId") Long cvId);

    /** Xóa mềm mọi đơn của một job (gọi khi job bị xóa mềm). */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "update applications set deleted_at = :now where job_id = :jobId and deleted_at is null", nativeQuery = true)
    void softDeleteByJobId(@Param("jobId") Long jobId, @Param("now") LocalDateTime now);
}
```

### `modules/Jobs/dao/ApplicationStatusLogRepository.java`  — TẠO MỚI

- Lấy lịch sử của một đơn, mới nhất trước.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dat_viet_group.datvietgroup.modules.Jobs.entity.ApplicationStatusLog;

@Repository
public interface ApplicationStatusLogRepository extends JpaRepository<ApplicationStatusLog, Long> {

    /** Lịch sử đổi trạng thái của một đơn, mới nhất trước. */
    List<ApplicationStatusLog> findByApplicationIdOrderByCreatedAtDescIdDesc(Long applicationId);
}
```

### 2.3 Service (interface)

### `modules/Jobs/service/JobTypeService.java`  — TẠO MỚI

- Giống `NotificationTypeService` đã có trong repo (module notification), dùng làm mẫu.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.service;

import java.util.List;

import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobTypeRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.JobTypeResponse;

public interface JobTypeService {

    /** Public: chỉ các loại đang hiện. */
    List<JobTypeResponse> getActiveTypes();

    /** Admin: tất cả loại, kể cả loại đang ẩn. */
    List<JobTypeResponse> getAll();

    JobTypeResponse getById(Long id);

    JobTypeResponse create(JobTypeRequest request);

    JobTypeResponse update(Long id, JobTypeRequest request);

    /** Xóa mềm (gán deleted_at). Chặn nếu còn tin tuyển dụng đang dùng loại này. */
    void delete(Long id);

    /** Đảo is_active (ẩn/hiện). */
    JobTypeResponse toggle(Long id);
}
```

### `modules/Jobs/service/JobService.java`  — TẠO MỚI

- Tách rõ nhóm công khai và nhóm admin. Các tham số lọc đều tùy chọn (`null` là bỏ qua).

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.service;

import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobCreateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobUpdateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.JobResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.EmploymentType;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.JobStatus;

public interface JobService {

    // ===== Public =====

    /** Chỉ job đang tuyển (OPEN) và còn hạn. Các tham số lọc đều tùy chọn (null là bỏ qua). */
    PageResponse<JobResponse> searchPublic(String keyword, Long jobTypeId, EmploymentType type,
            String location, int page, int size);

    /** Chi tiết job, không trả về job nháp. */
    JobResponse getPublicDetail(Long id);

    // ===== Admin =====

    /** Tất cả job (kể cả nháp, tạm dừng, đã đóng). */
    PageResponse<JobResponse> searchAdmin(String keyword, JobStatus status, int page, int size);

    JobResponse getAdminDetail(Long id);

    /** Tạo job ở trạng thái DRAFT. */
    JobResponse create(JobCreateRequest request, Long adminId);

    JobResponse update(Long id, JobUpdateRequest request, Long adminId);

    /** Xóa mềm (gán deleted_at), kèm xóa mềm các đơn ứng tuyển của job. */
    void delete(Long id);

    /** Đổi trạng thái: OPEN là đăng, PAUSED/CLOSED là ẩn khỏi trang public. */
    JobResponse changeStatus(Long id, JobStatus status, Long adminId);
}
```

### `modules/Jobs/service/CvService.java`  — TẠO MỚI

- Mọi hàm đều nhận `userId`: ứng viên chỉ thao tác trên CV của chính mình.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.CvResponse;

public interface CvService {

    /** Upload file PDF lên Cloudinary và lưu CV cho người dùng. title trống thì lấy tên file. */
    CvResponse upload(Long userId, MultipartFile file, String title);

    List<CvResponse> getMine(Long userId);

    CvResponse getMineById(Long userId, Long id);

    /** Xóa mềm (gán deleted_at). Các đơn đã nộp vẫn giữ cv_url của file đã nộp. */
    void delete(Long userId, Long id);
}
```

### `modules/Jobs/service/ApplicationService.java`  — TẠO MỚI

- Nhóm ứng viên (nộp, xem, rút) và nhóm admin (lọc, duyệt, xóa, xem log).

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.service;

import java.util.List;

import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.ApplicationCreateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.ApplicationStatusUpdateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.ApplicationResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.ApplicationStatusLogResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ApplicationStatus;

public interface ApplicationService {

    // ===== Ứng viên =====

    ApplicationResponse apply(Long jobId, Long userId, ApplicationCreateRequest request);

    PageResponse<ApplicationResponse> getMine(Long userId, int page, int size);

    ApplicationResponse getMineById(Long userId, Long id);

    /** Rút đơn: chỉ khi đơn đang PENDING hoặc REVIEWING. */
    ApplicationResponse withdraw(Long userId, Long id);

    // ===== Admin =====

    /** Các tham số lọc đều tùy chọn: jobId (theo job), status, keyword (họ tên, email, SĐT). */
    PageResponse<ApplicationResponse> searchAdmin(Long jobId, ApplicationStatus status, String keyword,
            int page, int size);

    ApplicationResponse getAdminById(Long id);

    /** Đổi trạng thái: ghi lịch sử, lưu ghi chú và gửi thông báo cho ứng viên. */
    ApplicationResponse changeStatus(Long id, Long adminId, ApplicationStatusUpdateRequest request);

    /** Xóa mềm. */
    void delete(Long id);

    List<ApplicationStatusLogResponse> getStatusLogs(Long id);
}
```

### 2.4 Service (implementation)

### `modules/Jobs/service/serviceimpl/JobTypeServiceImpl.java`  — TẠO MỚI

- Cùng cách làm với `NotificationTypeServiceImpl` đã có trong repo, dùng làm mẫu.
- `delete`: xóa mềm. Chặn nếu còn job (chưa xóa) dùng loại này bằng `existsByJobTypeId`; job đã xóa mềm tự bị bỏ qua nên không cản trở.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.JobRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.JobTypeRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobTypeRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.JobTypeResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.entity.JobType;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.JobTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobTypeServiceImpl implements JobTypeService {

    private final JobTypeRepository jobTypeRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobTypeResponse> getActiveTypes() {
        return jobTypeRepository.findAllActive().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobTypeResponse> getAll() {
        return jobTypeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobTypeResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public JobTypeResponse create(JobTypeRequest request) {
        JobType type = new JobType();
        type.setName(request.getName().trim());
        // isActive không gửi lên thì mặc định hiện
        type.setActive(request.getIsActive() == null || request.getIsActive());
        return toResponse(jobTypeRepository.save(type));
    }

    @Override
    @Transactional
    public JobTypeResponse update(Long id, JobTypeRequest request) {
        JobType type = findOrThrow(id);
        type.setName(request.getName().trim());
        if (request.getIsActive() != null) {
            type.setActive(request.getIsActive());
        }
        return toResponse(jobTypeRepository.save(type));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        JobType type = findOrThrow(id);
        // Còn tin tuyển dụng (chưa xóa) dùng loại này thì không xóa, chỉ cho ẩn
        if (jobRepository.existsByJobTypeId(id)) {
            throw new AppException(ErrorCode.JOB_TYPE_IN_USE);
        }
        // Xóa mềm: @SQLRestriction trên entity tự ẩn loại này khỏi mọi truy vấn sau đó
        type.setDeletedAt(LocalDateTime.now());
        jobTypeRepository.save(type);
    }

    @Override
    @Transactional
    public JobTypeResponse toggle(Long id) {
        JobType type = findOrThrow(id);
        type.setActive(!type.isActive());
        return toResponse(jobTypeRepository.save(type));
    }

    private JobType findOrThrow(Long id) {
        return jobTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_TYPE_NOT_FOUND));
    }

    private JobTypeResponse toResponse(JobType type) {
        return JobTypeResponse.builder()
                .id(type.getId())
                .name(type.getName())
                .isActive(type.isActive())
                .createdAt(type.getCreatedAt())
                .build();
    }
}
```

### `modules/Jobs/service/serviceimpl/JobServiceImpl.java`  — TẠO MỚI

- `searchPublic`: dựng danh sách điều kiện (`Specification`) rồi gộp bằng `allOf`. Luôn có hai điều kiện: `status = OPEN` và `deadline` rỗng hoặc chưa quá hạn.
- `getPublicDetail`: job nháp trả `JOB_NOT_FOUND` để người ngoài không biết job đó tồn tại. Job `PAUSED` và `CLOSED` vẫn xem được (đường link cũ vẫn mở được).
- `create`: luôn tạo ở trạng thái `DRAFT`, đăng bằng API đổi trạng thái. `currency` mặc định `VND`.
- `update`: chỉ ghi đè trường khác `null`. Muốn xóa trắng một trường chữ thì chưa hỗ trợ (cần quy ước riêng).
- `applyStatus`: lần đầu chuyển sang `OPEN` thì ghi `publishedAt`.
- `checkSalary`: lương tối thiểu không được lớn hơn tối đa.
- `delete`: gán `deletedAt` cho job **và** xóa mềm mọi đơn của job (`softDeleteByJobId`), để không còn đơn trỏ tới job đã biến mất.
- Các hàm `statusLabel`, `typeLabel`, `experienceLabel` đổi enum sang nhãn tiếng Việt. Dùng `switch` không có `default` nên nếu sau này thêm giá trị enum mà quên cập nhật thì **compile báo lỗi ngay** (an toàn hơn trả về chuỗi rỗng).

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.service.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.ApplicationRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.JobRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.JobTypeRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobCreateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobUpdateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.JobResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.entity.Job;
import com.dat_viet_group.datvietgroup.modules.Jobs.entity.JobType;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.EmploymentType;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ExperienceLevel;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.JobStatus;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.JobService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobTypeRepository jobTypeRepository;
    private final ApplicationRepository applicationRepository;

    // ================= PUBLIC =================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> searchPublic(String keyword, Long jobTypeId, EmploymentType type,
            String location, int page, int size) {
        List<Specification<Job>> specs = new ArrayList<>();

        // Public chỉ thấy job đang tuyển và còn hạn nhận hồ sơ
        specs.add((root, query, cb) -> cb.equal(root.get("status"), JobStatus.OPEN));
        specs.add((root, query, cb) -> cb.or(
                cb.isNull(root.get("deadline")),
                cb.greaterThanOrEqualTo(root.<LocalDate>get("deadline"), LocalDate.now())));

        addCommonFilters(specs, keyword, location);
        if (jobTypeId != null) {
            specs.add((root, query, cb) -> cb.equal(root.get("jobType").get("id"), jobTypeId));
        }
        if (type != null) {
            specs.add((root, query, cb) -> cb.equal(root.get("employmentType"), type));
        }

        Page<Job> result = jobRepository.findAll(Specification.allOf(specs), pageable(page, size));
        return PageResponse.from(result, this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getPublicDetail(Long id) {
        Job job = findOrThrow(id);
        // Job nháp chưa đăng thì coi như không tồn tại với người ngoài
        if (job.getStatus() == JobStatus.DRAFT) {
            throw new AppException(ErrorCode.JOB_NOT_FOUND);
        }
        return toResponse(job);
    }

    // ================= ADMIN =================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> searchAdmin(String keyword, JobStatus status, int page, int size) {
        List<Specification<Job>> specs = new ArrayList<>();
        addCommonFilters(specs, keyword, null);
        if (status != null) {
            specs.add((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        Page<Job> result = jobRepository.findAll(Specification.allOf(specs), pageable(page, size));
        return PageResponse.from(result, this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getAdminDetail(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public JobResponse create(JobCreateRequest request, Long adminId) {
        Job job = new Job();
        job.setCreatedBy(adminId);
        job.setTitle(request.getTitle().trim());
        job.setDescription(request.getDescription());
        job.setEmploymentType(request.getType());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setDepartment(request.getDepartment());
        job.setLocation(request.getLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setCurrency(StringUtils.hasText(request.getCurrency()) ? request.getCurrency() : "VND");
        job.setSalaryNegotiable(request.isSalaryNegotiable());
        job.setQuantity(request.getQuantity());
        job.setDeadline(request.getDeadline());
        job.setStatus(JobStatus.DRAFT); // tạo xong là bản nháp, đăng bằng API đổi trạng thái

        if (request.getJobTypeId() != null) {
            job.setJobType(findJobType(request.getJobTypeId()));
        }
        checkSalary(job);
        return toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobResponse update(Long id, JobUpdateRequest request, Long adminId) {
        Job job = findOrThrow(id);

        // Chỉ ghi đè những trường được gửi lên (khác null)
        if (request.getTitle() != null) {
            job.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            job.setDescription(request.getDescription());
        }
        if (request.getJobTypeId() != null) {
            job.setJobType(findJobType(request.getJobTypeId()));
        }
        if (request.getType() != null) {
            job.setEmploymentType(request.getType());
        }
        if (request.getExperienceLevel() != null) {
            job.setExperienceLevel(request.getExperienceLevel());
        }
        if (request.getDepartment() != null) {
            job.setDepartment(request.getDepartment());
        }
        if (request.getLocation() != null) {
            job.setLocation(request.getLocation());
        }
        if (request.getSalaryMin() != null) {
            job.setSalaryMin(request.getSalaryMin());
        }
        if (request.getSalaryMax() != null) {
            job.setSalaryMax(request.getSalaryMax());
        }
        if (request.getCurrency() != null) {
            job.setCurrency(request.getCurrency());
        }
        if (request.getSalaryNegotiable() != null) {
            job.setSalaryNegotiable(request.getSalaryNegotiable());
        }
        if (request.getQuantity() != null) {
            job.setQuantity(request.getQuantity());
        }
        if (request.getDeadline() != null) {
            job.setDeadline(request.getDeadline());
        }
        if (request.getStatus() != null) {
            applyStatus(job, request.getStatus());
        }

        checkSalary(job);
        job.setUpdatedBy(adminId);
        return toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Job job = findOrThrow(id);
        // Xóa mềm: @SQLRestriction trên entity tự ẩn job này khỏi mọi truy vấn sau đó
        LocalDateTime now = LocalDateTime.now();
        job.setDeletedAt(now);
        jobRepository.save(job);
        // Các đơn của job này cũng xóa mềm theo, để không còn đơn trỏ tới một job đã biến mất
        applicationRepository.softDeleteByJobId(id, now);
    }

    @Override
    @Transactional
    public JobResponse changeStatus(Long id, JobStatus status, Long adminId) {
        Job job = findOrThrow(id);
        applyStatus(job, status);
        job.setUpdatedBy(adminId);
        return toResponse(jobRepository.save(job));
    }

    // ================= HÀM PHỤ =================

    /** Đổi trạng thái; lần đầu chuyển sang OPEN thì ghi lại ngày đăng. */
    private void applyStatus(Job job, JobStatus status) {
        job.setStatus(status);
        if (status == JobStatus.OPEN && job.getPublishedAt() == null) {
            job.setPublishedAt(LocalDateTime.now());
        }
    }

    private void checkSalary(Job job) {
        if (job.getSalaryMin() != null && job.getSalaryMax() != null
                && job.getSalaryMin().compareTo(job.getSalaryMax()) > 0) {
            throw new AppException(ErrorCode.INVALID_INPUT_FORMAT, "Lương tối thiểu không được lớn hơn lương tối đa");
        }
    }

    /** Điều kiện lọc dùng chung cho public và admin: từ khóa trong tiêu đề, địa điểm. */
    private void addCommonFilters(List<Specification<Job>> specs, String keyword, String location) {
        if (StringUtils.hasText(keyword)) {
            String like = "%" + keyword.trim().toLowerCase() + "%";
            specs.add((root, query, cb) -> cb.like(cb.lower(root.<String>get("title")), like));
        }
        if (StringUtils.hasText(location)) {
            String like = "%" + location.trim().toLowerCase() + "%";
            specs.add((root, query, cb) -> cb.like(cb.lower(root.<String>get("location")), like));
        }
    }

    private Pageable pageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private Job findOrThrow(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
    }

    private JobType findJobType(Long id) {
        return jobTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_TYPE_NOT_FOUND));
    }

    private JobResponse toResponse(Job job) {
        JobType jobType = job.getJobType();
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .department(job.getDepartment())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .currency(job.getCurrency())
                .salaryNegotiable(job.isSalaryNegotiable())
                .quantity(job.getQuantity())
                .deadline(job.getDeadline())
                .status(job.getStatus())
                .statusLabel(statusLabel(job.getStatus()))
                .jobTypeId(jobType == null ? null : jobType.getId())
                .jobTypeName(jobType == null ? null : jobType.getName())
                .type(job.getEmploymentType())
                .typeLabel(typeLabel(job.getEmploymentType()))
                .experienceLevel(job.getExperienceLevel())
                .experienceLevelLabel(experienceLabel(job.getExperienceLevel()))
                .createdBy(job.getCreatedBy())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .publishedAt(job.getPublishedAt())
                .build();
    }

    private String statusLabel(JobStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case DRAFT -> "Bản nháp";
            case OPEN -> "Đang tuyển";
            case PAUSED -> "Tạm dừng";
            case CLOSED -> "Đã đóng";
        };
    }

    private String typeLabel(EmploymentType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case FULL_TIME -> "Toàn thời gian";
            case PART_TIME -> "Bán thời gian";
            case INTERNSHIP -> "Thực tập";
            case FREELANCE -> "Làm tự do";
        };
    }

    private String experienceLabel(ExperienceLevel level) {
        if (level == null) {
            return null;
        }
        return switch (level) {
            case FRESHER -> "Fresher (dưới 1 năm)";
            case JUNIOR -> "Junior (1-2 năm)";
            case MIDDLE -> "Middle (2-4 năm)";
            case SENIOR -> "Senior (4-7 năm)";
            case LEAD -> "Lead (trên 7 năm)";
        };
    }
}
```

### `modules/Jobs/service/serviceimpl/CvServiceImpl.java`  — TẠO MỚI

- `upload`: upload lên Cloudinary trước, rồi lưu DB. Lưu DB lỗi thì xóa file vừa upload để khỏi để rác.
- `delete`: gỡ liên kết CV khỏi các đơn (`detachCv`), rồi xóa mềm CV. File trên Cloudinary được giữ nguyên (đơn vẫn dùng `cvUrl`, và xóa mềm thì có thể khôi phục).

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.cloudinary.service.CloudinaryService;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.ApplicationRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.CvRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.CvResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.entity.Cv;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.CvService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CvServiceImpl implements CvService {

    private static final String CV_FOLDER = "cvs"; // thư mục trên Cloudinary

    private final CvRepository cvRepository;
    private final ApplicationRepository applicationRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public CvResponse upload(Long userId, MultipartFile file, String title) {
        // Hàm này tự kiểm tra file rỗng, không phải PDF, quá dung lượng và ném AppException
        String url = cloudinaryService.uploadPdf(file, CV_FOLDER);

        Cv cv = new Cv();
        cv.setUserId(userId);
        cv.setTitle(StringUtils.hasText(title) ? title.trim() : file.getOriginalFilename());
        cv.setPdfUrl(url);
        try {
            return toResponse(cvRepository.save(cv));
        } catch (RuntimeException e) {
            cloudinaryService.deleteByUrl(url); // lưu DB lỗi thì dọn file vừa upload
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CvResponse> getMine(Long userId) {
        return cvRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CvResponse getMineById(Long userId, Long id) {
        return toResponse(findMine(userId, id));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        Cv cv = findMine(userId, id);

        // Gỡ liên kết CV khỏi các đơn đã nộp: đơn vẫn giữ cv_url nên HR còn xem được file
        applicationRepository.detachCv(cv.getId());

        // Xóa mềm: giữ nguyên file trên Cloudinary, @SQLRestriction tự ẩn CV khỏi mọi truy vấn sau đó
        cv.setDeletedAt(LocalDateTime.now());
        cvRepository.save(cv);
    }

    private Cv findMine(Long userId, Long id) {
        return cvRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.CV_NOT_FOUND));
    }

    private CvResponse toResponse(Cv cv) {
        return CvResponse.builder()
                .id(cv.getId())
                .title(cv.getTitle())
                .pdfUrl(cv.getPdfUrl())
                .createdAt(cv.getCreatedAt())
                .build();
    }
}
```

### `modules/Jobs/service/serviceimpl/ApplicationServiceImpl.java`  — TẠO MỚI

- `apply`: kiểm tra theo thứ tự job tồn tại, job đang `OPEN` và chưa quá hạn, chưa nộp trùng, CV đúng chủ. Sau đó chụp thông tin CV vào đơn và ghi log đầu tiên.
- `withdraw`: chỉ rút được khi đơn đang `PENDING` hoặc `REVIEWING`.
- `changeStatus`: không cho đặt trùng trạng thái hiện tại, không cho admin đặt `WITHDRAWN`. Ghi `reviewedBy`, `statusChangedAt`, `adminNote`, ghi log, rồi gửi thông báo cho ứng viên.
- `searchAdmin`: hai API (#24 và #25) dùng chung hàm này, khác nhau ở chỗ `jobId` đến từ query string hay từ đường dẫn.
- Gọi `notificationService.send` ngay trong cùng transaction. Nếu việc gửi thông báo lỗi thì cả việc đổi trạng thái bị hủy; chấp nhận được vì cả hai đều là ghi DB nội bộ.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.service.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.ApplicationRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.ApplicationStatusLogRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.CvRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dao.JobRepository;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.ApplicationCreateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.ApplicationStatusUpdateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.ApplicationResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.ApplicationStatusLogResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.entity.Application;
import com.dat_viet_group.datvietgroup.modules.Jobs.entity.ApplicationStatusLog;
import com.dat_viet_group.datvietgroup.modules.Jobs.entity.Cv;
import com.dat_viet_group.datvietgroup.modules.Jobs.entity.Job;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ApplicationStatus;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.JobStatus;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.ApplicationService;
import com.dat_viet_group.datvietgroup.modules.notification.enums.TargetType;
import com.dat_viet_group.datvietgroup.modules.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusLogRepository statusLogRepository;
    private final JobRepository jobRepository;
    private final CvRepository cvRepository;
    private final NotificationService notificationService; // gửi thông báo cho ứng viên

    // ================= ỨNG VIÊN =================

    @Override
    @Transactional
    public ApplicationResponse apply(Long jobId, Long userId, ApplicationCreateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        // Chỉ nhận đơn khi job đang tuyển và chưa quá hạn
        boolean expired = job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now());
        if (job.getStatus() != JobStatus.OPEN || expired) {
            throw new AppException(ErrorCode.JOB_NOT_OPEN);
        }
        // Mỗi người một đơn cho một job (đơn đã rút thì được nộp lại)
        if (applicationRepository.existsByJobIdAndUserIdAndStatusNot(jobId, userId, ApplicationStatus.WITHDRAWN)) {
            throw new AppException(ErrorCode.APPLICATION_EXISTED);
        }
        // CV phải là của chính người nộp
        Cv cv = cvRepository.findByIdAndUserId(request.getCvId(), userId)
                .orElseThrow(() -> new AppException(ErrorCode.CV_NOT_FOUND));

        Application application = new Application();
        application.setJob(job);
        application.setUserId(userId);
        application.setCv(cv);
        application.setFullName(request.getFullName().trim());
        application.setEmail(request.getEmail().trim());
        application.setPhone(request.getPhone().trim());
        application.setCoverLetter(request.getCoverLetter());
        application.setSources(request.getSources());
        // Chụp lại thông tin file CV tại thời điểm nộp, sau này ứng viên xóa CV thì đơn vẫn còn file
        application.setCvUrl(cv.getPdfUrl());
        application.setCvFileName(cv.getTitle());
        application.setCvMimeType("application/pdf");
        application.setStatus(ApplicationStatus.PENDING);
        application.setStatusChangedAt(LocalDateTime.now());
        application = applicationRepository.save(application);

        writeLog(application, userId, null, ApplicationStatus.PENDING.name(), "Nộp đơn ứng tuyển");
        return toResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> getMine(Long userId, int page, int size) {
        Page<Application> result = applicationRepository.findByUserId(userId, pageable(page, size));
        return PageResponse.from(result, this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getMineById(Long userId, Long id) {
        return toResponse(findMine(userId, id));
    }

    @Override
    @Transactional
    public ApplicationResponse withdraw(Long userId, Long id) {
        Application application = findMine(userId, id);

        // Đơn đã phỏng vấn, chấp nhận, từ chối hoặc đã rút rồi thì không rút được nữa
        ApplicationStatus current = application.getStatus();
        if (current != ApplicationStatus.PENDING && current != ApplicationStatus.REVIEWING) {
            throw new AppException(ErrorCode.APPLICATION_CANNOT_WITHDRAW);
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        application.setStatusChangedAt(LocalDateTime.now());
        application = applicationRepository.save(application);

        writeLog(application, userId, current.name(), ApplicationStatus.WITHDRAWN.name(), "Ứng viên rút đơn");
        return toResponse(application);
    }

    // ================= ADMIN =================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> searchAdmin(Long jobId, ApplicationStatus status, String keyword,
            int page, int size) {
        List<Specification<Application>> specs = new ArrayList<>();
        if (jobId != null) {
            specs.add((root, query, cb) -> cb.equal(root.get("job").get("id"), jobId));
        }
        if (status != null) {
            specs.add((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (StringUtils.hasText(keyword)) {
            String like = "%" + keyword.trim().toLowerCase() + "%";
            specs.add((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.<String>get("fullName")), like),
                    cb.like(cb.lower(root.<String>get("email")), like),
                    cb.like(cb.lower(root.<String>get("phone")), like)));
        }
        Page<Application> result = applicationRepository.findAll(Specification.allOf(specs), pageable(page, size));
        return PageResponse.from(result, this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getAdminById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public ApplicationResponse changeStatus(Long id, Long adminId, ApplicationStatusUpdateRequest request) {
        Application application = findOrThrow(id);
        ApplicationStatus from = application.getStatus();
        ApplicationStatus to = request.getStatus();

        if (to == ApplicationStatus.WITHDRAWN) {
            throw new AppException(ErrorCode.INVALID_INPUT_FORMAT, "Chỉ ứng viên mới được rút đơn");
        }
        if (to == from) {
            throw new AppException(ErrorCode.APPLICATION_STATUS_UNCHANGED);
        }

        application.setStatus(to);
        application.setReviewedBy(adminId);
        application.setStatusChangedAt(LocalDateTime.now());
        if (request.getNote() != null) {
            application.setAdminNote(request.getNote());
        }
        application = applicationRepository.save(application);

        writeLog(application, adminId, from == null ? null : from.name(), to.name(), request.getNote());

        // Báo cho ứng viên biết đơn của họ vừa đổi trạng thái
        if (application.getUserId() != null) {
            notificationService.send(
                    application.getUserId(),
                    NotificationService.TYPE_JOB,
                    "Cập nhật đơn ứng tuyển",
                    "Đơn ứng tuyển vị trí \"" + application.getJob().getTitle()
                            + "\" của bạn đã chuyển sang trạng thái: " + statusLabel(to) + ".",
                    TargetType.APPLICATION,
                    application.getId(),
                    null);
        }
        return toResponse(application);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Application application = findOrThrow(id);
        // Xóa mềm: @SQLRestriction trên entity tự ẩn đơn này khỏi mọi truy vấn sau đó
        application.setDeletedAt(LocalDateTime.now());
        applicationRepository.save(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationStatusLogResponse> getStatusLogs(Long id) {
        findOrThrow(id); // đảm bảo đơn tồn tại, chưa bị xóa
        return statusLogRepository.findByApplicationIdOrderByCreatedAtDescIdDesc(id).stream()
                .map(log -> ApplicationStatusLogResponse.builder()
                        .id(log.getId())
                        .changedBy(log.getChangedBy())
                        .fromStatus(log.getFromStatus())
                        .toStatus(log.getToStatus())
                        .note(log.getNote())
                        .createdAt(log.getCreatedAt())
                        .build())
                .toList();
    }

    // ================= HÀM PHỤ =================

    private void writeLog(Application application, Long changedBy, String from, String to, String note) {
        ApplicationStatusLog log = new ApplicationStatusLog();
        log.setApplication(application);
        log.setChangedBy(changedBy);
        log.setFromStatus(from);
        log.setToStatus(to);
        log.setNote(note);
        statusLogRepository.save(log);
    }

    private Pageable pageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private Application findOrThrow(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    private Application findMine(Long userId, Long id) {
        return applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    private ApplicationResponse toResponse(Application a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .jobId(a.getJob().getId())
                .jobTitle(a.getJob().getTitle())
                .userId(a.getUserId())
                .cvId(a.getCv() == null ? null : a.getCv().getId())
                .fullName(a.getFullName())
                .email(a.getEmail())
                .phone(a.getPhone())
                .cvUrl(a.getCvUrl())
                .cvFileName(a.getCvFileName())
                .coverLetter(a.getCoverLetter())
                .sources(a.getSources())
                .status(a.getStatus())
                .statusLabel(statusLabel(a.getStatus()))
                .adminNote(a.getAdminNote())
                .reviewedBy(a.getReviewedBy())
                .statusChangedAt(a.getStatusChangedAt())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private String statusLabel(ApplicationStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PENDING -> "Chờ xem xét";
            case REVIEWING -> "Đang xem xét";
            case INTERVIEWED -> "Đã phỏng vấn";
            case ACCEPTED -> "Đã chấp nhận";
            case REJECTED -> "Từ chối";
            case WITHDRAWN -> "Đã rút đơn";
        };
    }
}
```

### 2.5 Controller

### `modules/Jobs/controller/JobTypeController.java`  — TẠO MỚI

- API #1 (công khai).

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.JobTypeResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.JobTypeService;

import lombok.RequiredArgsConstructor;

/** Public: các loại công việc đang hiện, dùng cho bộ lọc ở trang tuyển dụng. Không cần đăng nhập. */
@RestController
@RequestMapping("/api/job-types")
@RequiredArgsConstructor
public class JobTypeController {

    private final JobTypeService jobTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobTypeResponse>>> getActiveTypes() {
        return ApiResponse.ok("Lấy danh sách loại công việc thành công", jobTypeService.getActiveTypes());
    }
}
```

### `modules/Jobs/controller/AdminJobTypeController.java`  — TẠO MỚI

- API #2 đến #7.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobTypeRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.JobTypeResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.JobTypeService;

import lombok.RequiredArgsConstructor;

/** Admin quản lý loại công việc. Quyền ADMIN do SecurityConfig kiểm tra qua /api/admin/**. */
@RestController
@RequestMapping("/api/admin/job-types")
@RequiredArgsConstructor
public class AdminJobTypeController {

    private final JobTypeService jobTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobTypeResponse>>> getAll() {
        return ApiResponse.ok("Lấy danh sách loại công việc thành công", jobTypeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobTypeResponse>> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy chi tiết loại công việc thành công", jobTypeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobTypeResponse>> create(@Validated @RequestBody JobTypeRequest request) {
        return ApiResponse.created("Tạo loại công việc thành công", jobTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobTypeResponse>> update(@PathVariable Long id,
            @Validated @RequestBody JobTypeRequest request) {
        return ApiResponse.ok("Cập nhật loại công việc thành công", jobTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        jobTypeService.delete(id);
        return ApiResponse.ok("Xóa loại công việc thành công");
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<JobTypeResponse>> toggle(@PathVariable Long id) {
        return ApiResponse.ok("Đổi trạng thái ẩn/hiện thành công", jobTypeService.toggle(id));
    }
}
```

### `modules/Jobs/controller/JobController.java`  — TẠO MỚI

- API #8 và #9 (công khai, đã khai báo trong `Endpoints.PUBLIC_GET_ENDPOINTS`).
- `type` là `EmploymentType`: gửi sai giá trị (ví dụ `type=ABC`) Spring trả 400.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.JobResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.EmploymentType;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.JobService;

import lombok.RequiredArgsConstructor;

/** Public: xem tin tuyển dụng, không cần đăng nhập (đã khai báo trong Endpoints.PUBLIC_GET_ENDPOINTS). */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /** Các tham số lọc đều tùy chọn. */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long jobTypeId,
            @RequestParam(required = false) EmploymentType type,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy danh sách tin tuyển dụng thành công",
                jobService.searchPublic(keyword, jobTypeId, type, location, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getDetail(@PathVariable Long id) {
        return ApiResponse.ok("Lấy chi tiết tin tuyển dụng thành công", jobService.getPublicDetail(id));
    }
}
```

### `modules/Jobs/controller/AdminJobController.java`  — TẠO MỚI

- API #10 đến #15.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobCreateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobStatusRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.JobUpdateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.JobResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.JobStatus;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.JobService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

/** Admin quản lý tin tuyển dụng. Quyền ADMIN do SecurityConfig kiểm tra qua /api/admin/**. */
@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class AdminJobController {

    private final JobService jobService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy danh sách tin tuyển dụng thành công",
                jobService.searchAdmin(keyword, status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getDetail(@PathVariable Long id) {
        return ApiResponse.ok("Lấy chi tiết tin tuyển dụng thành công", jobService.getAdminDetail(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> create(@Validated @RequestBody JobCreateRequest request,
            Authentication authentication) {
        return ApiResponse.created("Tạo tin tuyển dụng thành công",
                jobService.create(request, currentUserId(authentication)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> update(@PathVariable Long id,
            @Validated @RequestBody JobUpdateRequest request, Authentication authentication) {
        return ApiResponse.ok("Cập nhật tin tuyển dụng thành công",
                jobService.update(id, request, currentUserId(authentication)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        jobService.delete(id);
        return ApiResponse.ok("Xóa tin tuyển dụng thành công");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<JobResponse>> changeStatus(@PathVariable Long id,
            @Validated @RequestBody JobStatusRequest request, Authentication authentication) {
        return ApiResponse.ok("Đổi trạng thái tin tuyển dụng thành công",
                jobService.changeStatus(id, request.getStatus(), currentUserId(authentication)));
    }

    // authentication.getName() là email của người đăng nhập (do JwtAuthFilter đặt vào)
    private Long currentUserId(Authentication authentication) {
        return userService.findByEmailOrPhone(authentication.getName()).getId();
    }
}
```

### `modules/Jobs/controller/CvController.java`  — TẠO MỚI

- API #16 đến #19. Upload dùng `multipart/form-data`: trường `file` (File) và `title` (Text, tùy chọn). Giống kiểu `CloudinaryTestController` đang dùng.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.CvResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.CvService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

/** CV của người dùng đang đăng nhập. */
@RestController
@RequestMapping("/api/me/cvs")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;
    private final UserService userService;

    /** form-data: file (File, chỉ PDF), title (Text, tùy chọn). */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CvResponse>> upload(@RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String title, Authentication authentication) {
        return ApiResponse.created("Upload CV thành công",
                cvService.upload(currentUserId(authentication), file, title));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CvResponse>>> getMine(Authentication authentication) {
        return ApiResponse.ok("Lấy danh sách CV thành công", cvService.getMine(currentUserId(authentication)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CvResponse>> getById(@PathVariable Long id, Authentication authentication) {
        return ApiResponse.ok("Lấy chi tiết CV thành công", cvService.getMineById(currentUserId(authentication), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication authentication) {
        cvService.delete(currentUserId(authentication), id);
        return ApiResponse.ok("Xóa CV thành công");
    }

    // authentication.getName() là email của người đăng nhập (do JwtAuthFilter đặt vào)
    private Long currentUserId(Authentication authentication) {
        return userService.findByEmailOrPhone(authentication.getName()).getId();
    }
}
```

### `modules/Jobs/controller/ApplicationController.java`  — TẠO MỚI

- API #20 đến #23. Controller này không có `@RequestMapping` ở cấp class vì đường dẫn nộp đơn là `/api/jobs/{jobId}/applications` còn phần còn lại là `/api/me/applications`.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.ApplicationCreateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.ApplicationResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.ApplicationService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

/** Ứng viên nộp đơn và theo dõi đơn của mình. Cần đăng nhập. */
@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    @PostMapping("/api/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(@PathVariable Long jobId,
            @Validated @RequestBody ApplicationCreateRequest request, Authentication authentication) {
        return ApiResponse.created("Nộp đơn ứng tuyển thành công",
                applicationService.apply(jobId, currentUserId(authentication), request));
    }

    @GetMapping("/api/me/applications")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> getMine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ApiResponse.ok("Lấy danh sách đơn ứng tuyển thành công",
                applicationService.getMine(currentUserId(authentication), page, size));
    }

    @GetMapping("/api/me/applications/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getMineById(@PathVariable Long id,
            Authentication authentication) {
        return ApiResponse.ok("Lấy chi tiết đơn ứng tuyển thành công",
                applicationService.getMineById(currentUserId(authentication), id));
    }

    @PatchMapping("/api/me/applications/{id}/withdraw")
    public ResponseEntity<ApiResponse<ApplicationResponse>> withdraw(@PathVariable Long id,
            Authentication authentication) {
        return ApiResponse.ok("Rút đơn ứng tuyển thành công",
                applicationService.withdraw(currentUserId(authentication), id));
    }

    // authentication.getName() là email của người đăng nhập (do JwtAuthFilter đặt vào)
    private Long currentUserId(Authentication authentication) {
        return userService.findByEmailOrPhone(authentication.getName()).getId();
    }
}
```

### `modules/Jobs/controller/AdminApplicationController.java`  — TẠO MỚI

- API #24 đến #29. Cấp class là `/api/admin` vì có cả `/applications/...` và `/jobs/{jobId}/applications`.

```java
package com.dat_viet_group.datvietgroup.modules.Jobs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.core.common.PageResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.request.ApplicationStatusUpdateRequest;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.ApplicationResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.dto.response.ApplicationStatusLogResponse;
import com.dat_viet_group.datvietgroup.modules.Jobs.enums.ApplicationStatus;
import com.dat_viet_group.datvietgroup.modules.Jobs.service.ApplicationService;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

/** Admin duyệt đơn ứng tuyển. Quyền ADMIN do SecurityConfig kiểm tra qua /api/admin/**. */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    /** Tất cả đơn; lọc tùy chọn theo jobId, status, keyword (họ tên, email, SĐT). */
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> search(
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy danh sách đơn ứng tuyển thành công",
                applicationService.searchAdmin(jobId, status, keyword, page, size));
    }

    /** Đơn của một tin tuyển dụng. */
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> searchByJob(@PathVariable Long jobId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy danh sách đơn theo tin tuyển dụng thành công",
                applicationService.searchAdmin(jobId, status, keyword, page, size));
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy chi tiết đơn ứng tuyển thành công", applicationService.getAdminById(id));
    }

    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> changeStatus(@PathVariable Long id,
            @Validated @RequestBody ApplicationStatusUpdateRequest request, Authentication authentication) {
        Long adminId = userService.findByEmailOrPhone(authentication.getName()).getId();
        return ApiResponse.ok("Đổi trạng thái đơn ứng tuyển thành công",
                applicationService.changeStatus(id, adminId, request));
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return ApiResponse.ok("Xóa đơn ứng tuyển thành công");
    }

    @GetMapping("/applications/{id}/status-logs")
    public ResponseEntity<ApiResponse<List<ApplicationStatusLogResponse>>> getStatusLogs(@PathVariable Long id) {
        return ApiResponse.ok("Lấy lịch sử trạng thái thành công", applicationService.getStatusLogs(id));
    }
}
```

**Điểm kiểm tra 2:** `./mvnw -q -DskipTests compile` sạch. Hết phần code.

---

## 3. Chạy thử bằng Postman

### 3.1 Chuẩn bị dữ liệu

1. Chạy app một lần để Hibernate tạo bảng. Bảng `role` đang trống, và `register` cần dòng `ROLE_USER`:

```sql
INSERT INTO role (name, is_active) VALUES ('ROLE_USER', true), ('ROLE_ADMIN', true);
```

2. Đăng ký hai tài khoản bằng `POST /api/users/register` (một làm admin, một làm ứng viên). Hàm kích hoạt qua email chưa làm xong nên kích hoạt tay:

```sql
-- is_deleted = true nghĩa là tài khoản bình thường (quy ước của bảng users, xem 0.5)
UPDATE users SET is_active = true, is_deleted = true WHERE email IN ('admin@test.com', 'cand@test.com');
UPDATE users SET role_id = (SELECT id FROM role WHERE name = 'ROLE_ADMIN') WHERE email = 'admin@test.com';
```

3. Đăng nhập hai tài khoản bằng `POST /api/users/login`, lấy `data.token` của từng người. Mọi request sau gắn header `Authorization: Bearer <token>`. 

### 3.2 Kịch bản chạy theo thứ tự

| Bước | Request | Kết quả mong đợi |
|---|---|---|
| 1 | Admin: `POST /api/admin/job-types` `{"name":"Kỹ thuật"}` | 201 |
| 2 | Ứng viên: `POST /api/admin/job-types` | 403 (không phải admin) |
| 3 | Admin: `POST /api/admin/jobs` `{"title":"Java Dev","type":"FULL_TIME","jobTypeId":1,"experienceLevel":"JUNIOR","salaryMin":10000000,"salaryMax":20000000,"location":"Hà Nội","quantity":2}` | 201, `status = DRAFT` |
| 4 | Không token: `GET /api/jobs` | 200, danh sách rỗng (job còn nháp) |
| 5 | Admin: `PATCH /api/admin/jobs/1/status` `{"status":"OPEN"}` | 200 |
| 6 | Không token: `GET /api/jobs?keyword=java` | 200, thấy job |
| 7 | Ứng viên: `POST /api/me/cvs` form-data `file` = file PDF, `title` = "CV của tôi" | 201 |
| 8 | Ứng viên: `POST /api/jobs/1/applications` `{"cvId":1,"fullName":"A","email":"a@x.com","phone":"0900000002"}` | 201, `status = PENDING` |
| 9 | Ứng viên: nộp lại lần nữa | 400 (nộp trùng) |
| 10 | Admin: `PATCH /api/admin/applications/1/status` `{"status":"REVIEWING","note":"Hồ sơ tốt"}` | 200 |
| 11 | Admin: `GET /api/admin/applications/1/status-logs` | 2 dòng log |
| 12 | Ứng viên: `GET /api/me/notifications/unread-count` | `total = 1` |
| 13 | Ứng viên: `GET /api/me/notifications` | thấy thông báo "Cập nhật đơn ứng tuyển" |
| 14 | Ứng viên: `PATCH /api/me/notifications/1/read` | 200, `unread-count` về 0 |
| 15 | Admin: `POST /api/admin/notification-types` `{"name":"Sự kiện"}` rồi `POST /api/admin/notifications` `{"notificationTypeId":2,"title":"Xin chào","content":"Gửi tất cả"}` | 201, mọi người nhận được |
| 16 | Ứng viên: `PATCH /api/me/applications/1/withdraw` | 200, `status = WITHDRAWN` |
| 17 | Admin: `PATCH /api/admin/notification-types/1/toggle` (ẩn loại "Tuyển dụng") rồi ứng viên `GET /api/me/notifications/unread-count` | `total = 0`, thông báo biến mất; bật lại thì hiện lại |
| 18 | Ứng viên: `DELETE /api/me/notifications/{id}` (id của chính mình), gọi lần 2 | lần 1: 200, lần 2: 404 |

### 3.3 Cách đọc lỗi

Mọi lỗi có dạng `{"success": false, "code": 4xx, "message": "..."}`. Lỗi validate (thiếu trường bắt buộc) trả 400 kèm message của trường đầu tiên sai.

---

## 4. Phụ lục: test tự động (không bắt buộc)

Nếu muốn chạy lại phép kiểm chứng tôi đã làm. Test chạy trên DB H2 trong bộ nhớ, **không đụng DB thật**, gọi qua HTTP có Spring Security và JWT thật, Cloudinary được giả lập.

1. Thêm vào `pom.xml`, trong `<dependencies>`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

2. Tạo file mới (giữ nguyên `DatvietgroupApplicationTests.java` cũ):


`src/test/java/com/dat_viet_group/datvietgroup/JobsNotificationFlowTests.java`

```java
package com.dat_viet_group.datvietgroup;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.dat_viet_group.datvietgroup.core.cloudinary.service.CloudinaryService;
import com.dat_viet_group.datvietgroup.modules.user.dao.RoleRepository;
import com.dat_viet_group.datvietgroup.modules.user.dao.UserRepository;
import com.dat_viet_group.datvietgroup.modules.user.entity.Role;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.JWTService;

@SpringBootTest(properties = {
        "spring.profiles.active=test",
        "spring.datasource.url=jdbc:h2:mem:t;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.mail.host=localhost",
        "jwt.secret=dGVzdHRlc3R0ZXN0dGVzdHRlc3R0ZXN0dGVzdHRlc3R0ZXN0dGVzdHRlc3Q=",
        "jwt.expiration=3600000",
        "jwt.refresh-expiration=7200000",
        "cloudinary.url=cloudinary://k:s@cloud"
})
@AutoConfigureMockMvc
class JobsNotificationFlowTests {

    @Autowired MockMvc mvc;
    @Autowired RoleRepository roleRepository;
    @Autowired UserRepository userRepository;
    @Autowired JWTService jwtService;
    @MockitoBean CloudinaryService cloudinaryService;

    private User mkUser(Role role, String email, String phone) {
        User u = new User();
        u.setEmail(email); u.setPhone(phone); u.setFullName(email); u.setPassword("x");
        u.setActive(true); u.setDeleted(true); u.setRole(role);
        return userRepository.save(u);
    }

    private MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder b, String token) {
        return token == null ? b : b.header("Authorization", "Bearer " + token);
    }

    private ResultActions json(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        return mvc.perform(auth(b, token).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String token) throws Exception {
        return mvc.perform(auth(b, token));
    }

    @Test
    void fullFlow() throws Exception {
        Role ru = new Role(); ru.setName("ROLE_USER"); ru.setActive(true); ru = roleRepository.save(ru);
        Role ra = new Role(); ra.setName("ROLE_ADMIN"); ra.setActive(true); ra = roleRepository.save(ra);
        mkUser(ra, "admin@x.com", "0900000001");
        mkUser(ru, "cand@x.com", "0900000002");
        mkUser(ru, "other@x.com", "0900000003");
        String admin = jwtService.generateToken("admin@x.com");
        String cand = jwtService.generateToken("cand@x.com");
        String other = jwtService.generateToken("other@x.com");

        // ---- Phân quyền ----
        call(get("/api/admin/job-types"), null).andExpect(status().isUnauthorized());
        json(post("/api/admin/job-types"), cand, "{\"name\":\"Kỹ thuật\"}").andExpect(status().isForbidden());
        call(patch("/api/admin/job-types/1/toggle"), cand).andExpect(status().isForbidden());
        call(delete("/api/admin/job-types/1"), cand).andExpect(status().isForbidden());

        // ---- Job types ----
        json(post("/api/admin/job-types"), admin, "{\"name\":\"Kỹ thuật\"}").andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.isActive").value(true));
        json(put("/api/admin/job-types/1"), admin, "{\"name\":\"Kỹ thuật 2\"}").andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Kỹ thuật 2"));
        call(get("/api/job-types"), null).andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1));
        call(patch("/api/admin/job-types/1/toggle"), admin).andExpect(jsonPath("$.data.isActive").value(false));
        call(get("/api/job-types"), null).andExpect(jsonPath("$.data.length()").value(0));
        call(get("/api/admin/job-types"), admin).andExpect(jsonPath("$.data.length()").value(1));
        call(patch("/api/admin/job-types/1/toggle"), admin);

        // ---- Jobs ----
        String jobBody = "{\"title\":\"Java Dev\",\"type\":\"FULL_TIME\",\"jobTypeId\":1,\"experienceLevel\":\"JUNIOR\","
                + "\"salaryMin\":10,\"salaryMax\":20,\"location\":\"Ha Noi\",\"quantity\":2}";
        json(post("/api/admin/jobs"), admin, jobBody).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.jobTypeName").value("Kỹ thuật 2"))
                .andExpect(jsonPath("$.data.typeLabel").value("Toàn thời gian"));
        json(post("/api/admin/jobs"), admin, "{\"title\":\"x\",\"type\":\"FULL_TIME\",\"salaryMin\":30,\"salaryMax\":20}")
                .andExpect(status().isBadRequest());
        call(get("/api/jobs"), null).andExpect(status().isOk()).andExpect(jsonPath("$.data.totalElements").value(0));
        call(get("/api/jobs/1"), null).andExpect(status().isNotFound());
        call(get("/api/admin/jobs"), admin).andExpect(jsonPath("$.data.totalElements").value(1));
        json(patch("/api/admin/jobs/1/status"), admin, "{\"status\":\"OPEN\"}").andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusLabel").value("Đang tuyển"));
        call(get("/api/jobs?keyword=java&type=FULL_TIME&jobTypeId=1&location=ha"), null)
                .andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/api/jobs?keyword=python"), null).andExpect(jsonPath("$.data.totalElements").value(0));
        call(get("/api/jobs/1"), null).andExpect(status().isOk()).andExpect(jsonPath("$.data.title").value("Java Dev"));
        json(put("/api/admin/jobs/1"), admin, "{\"location\":\"HCM\"}").andExpect(jsonPath("$.data.location").value("HCM"))
                .andExpect(jsonPath("$.data.title").value("Java Dev"));
        call(get("/api/admin/jobs/1"), admin).andExpect(jsonPath("$.data.title").value("Java Dev"));
        call(get("/api/admin/job-types/1"), admin).andExpect(jsonPath("$.data.name").value("Kỹ thuật 2"));
        call(delete("/api/admin/job-types/1"), admin).andExpect(status().isBadRequest()); // đang được dùng

        // ---- CV + nộp đơn ----
        when(cloudinaryService.uploadPdf(any(), any())).thenReturn("https://res.cloudinary.com/c/image/upload/v1/cvs/a.pdf");
        MockMultipartFile pdf = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[] { 1, 2 });
        mvc.perform(multipart("/api/me/cvs").file(pdf).param("title", "CV của tôi").header("Authorization", "Bearer " + cand))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.title").value("CV của tôi"));
        call(get("/api/me/cvs"), cand).andExpect(jsonPath("$.data.length()").value(1));
        call(get("/api/me/cvs"), other).andExpect(jsonPath("$.data.length()").value(0));
        call(get("/api/me/cvs/1"), other).andExpect(status().isNotFound());

        String apply = "{\"cvId\":1,\"fullName\":\"A\",\"email\":\"a@x.com\",\"phone\":\"0900000002\"}";
        json(post("/api/jobs/1/applications"), null, apply).andExpect(status().isUnauthorized());
        json(post("/api/jobs/1/applications"), other, apply).andExpect(status().isNotFound()); // CV của người khác
        json(post("/api/jobs/1/applications"), cand, apply).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
        json(post("/api/jobs/1/applications"), cand, apply).andExpect(status().isBadRequest()); // nộp trùng
        call(get("/api/me/applications"), cand).andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/api/me/applications/1"), other).andExpect(status().isNotFound());

        // ---- Admin duyệt đơn ----
        call(get("/api/admin/applications"), admin).andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/api/admin/applications?keyword=a@x&status=PENDING&jobId=1"), admin)
                .andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/api/admin/jobs/1/applications"), admin).andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/api/admin/applications/1"), cand).andExpect(status().isForbidden());
        json(patch("/api/admin/applications/1/status"), admin, "{\"status\":\"PENDING\"}").andExpect(status().isBadRequest());
        json(patch("/api/admin/applications/1/status"), admin, "{\"status\":\"REVIEWING\",\"note\":\"ok\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("REVIEWING"))
                .andExpect(jsonPath("$.data.adminNote").value("ok"));
        call(get("/api/admin/applications/1/status-logs"), admin).andExpect(jsonPath("$.data.length()").value(2));

        // ---- Thông báo tự động từ Jobs ----
        call(get("/api/me/notifications/unread-count"), cand).andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.byType.1").value(1));
        // Ẩn loại "Tuyển dụng": thông báo thuộc loại này biến mất khỏi người dùng, bật lại thì hiện lại
        call(patch("/api/admin/notification-types/1/toggle"), admin).andExpect(jsonPath("$.data.isActive").value(false));
        call(get("/api/me/notifications/unread-count"), cand).andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.byType.1").doesNotExist());
        call(get("/api/me/notifications"), cand).andExpect(jsonPath("$.data.totalElements").value(0));
        call(get("/api/me/notifications?typeId=1"), cand).andExpect(jsonPath("$.data.totalElements").value(0));
        call(patch("/api/me/notifications/1/read"), cand).andExpect(status().isNotFound());
        call(delete("/api/me/notifications/1"), cand).andExpect(status().isNotFound());
        call(patch("/api/me/notifications/read-all"), cand).andExpect(status().isOk()); // không đụng tới loại đang ẩn
        call(patch("/api/admin/notification-types/1/toggle"), admin).andExpect(jsonPath("$.data.isActive").value(true));
        call(get("/api/me/notifications/unread-count"), cand).andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.byType.1").value(1)); // vẫn chưa đọc sau khi bật lại
        call(get("/api/me/notifications"), cand).andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].notificationTypeName").value("Tuyển dụng"))
                .andExpect(jsonPath("$.data.content[0].read").value(false))
                .andExpect(jsonPath("$.data.content[0].notificationTypeId").value(1));
        call(get("/api/me/notifications?typeId=1"), cand).andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/api/me/notifications?typeId=99"), cand).andExpect(jsonPath("$.data.totalElements").value(0));
        call(get("/api/me/notifications"), other).andExpect(jsonPath("$.data.totalElements").value(0));
        call(patch("/api/me/notifications/1/read"), other).andExpect(status().isNotFound());
        call(patch("/api/me/notifications/1/read"), cand).andExpect(status().isOk());
        call(get("/api/me/notifications/unread-count"), cand).andExpect(jsonPath("$.data.total").value(0));
        call(get("/api/notification-types"), cand).andExpect(jsonPath("$.data.length()").value(1));

        // ---- Rút đơn ----
        call(patch("/api/me/applications/1/withdraw"), other).andExpect(status().isNotFound());
        call(patch("/api/me/applications/1/withdraw"), cand).andExpect(jsonPath("$.data.status").value("WITHDRAWN"));
        call(patch("/api/me/applications/1/withdraw"), cand).andExpect(status().isBadRequest());
        json(post("/api/jobs/1/applications"), cand, apply).andExpect(status().isCreated()); // rút rồi nộp lại được
        json(patch("/api/admin/applications/2/status"), admin, "{\"status\":\"ACCEPTED\"}").andExpect(status().isOk());
        call(patch("/api/me/applications/2/withdraw"), cand).andExpect(status().isBadRequest());

        // ---- Admin: loại thông báo + thông báo ----
        json(post("/api/admin/notification-types"), admin, "{\"name\":\"Sự kiện\"}").andExpect(status().isCreated());
        call(get("/api/admin/notification-types"), admin).andExpect(jsonPath("$.data.length()").value(2));
        call(get("/api/admin/notification-types/2"), admin).andExpect(jsonPath("$.data.name").value("Sự kiện"));
        json(put("/api/admin/notification-types/2"), admin, "{\"name\":\"Sự kiện\"}").andExpect(status().isOk());
        call(get("/api/admin/notification-types/99"), admin).andExpect(status().isNotFound());
        call(patch("/api/admin/notification-types/2/toggle"), admin).andExpect(jsonPath("$.data.isActive").value(false));
        call(get("/api/notification-types"), cand).andExpect(jsonPath("$.data.length()").value(1));
        call(patch("/api/admin/notification-types/2/toggle"), admin);
        json(post("/api/admin/notifications"), admin, "{\"notificationTypeId\":2,\"title\":\"Hello\",\"content\":\"All\"}")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.notificationTypeName").value("Sự kiện"));
        json(post("/api/admin/notifications"), admin,
                "{\"notificationTypeId\":2,\"title\":\"Riêng\",\"content\":\"One\",\"userIds\":[3,999]}")
                .andExpect(status().isCreated());
        json(post("/api/admin/notifications"), admin, "{\"notificationTypeId\":99,\"title\":\"x\",\"content\":\"y\"}")
                .andExpect(status().isNotFound());
        // cand: 2 thông báo Jobs (đổi REVIEWING + ACCEPTED) + 1 broadcast = 3; other(id3): broadcast + riêng = 2
        call(get("/api/me/notifications"), cand).andExpect(jsonPath("$.data.totalElements").value(3));
        call(get("/api/me/notifications"), other).andExpect(jsonPath("$.data.totalElements").value(2));
        call(get("/api/me/notifications?typeId=2&page=0&size=1"), other).andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(2));
        call(get("/api/me/notifications/unread-count"), other).andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.byType.2").value(2));
        call(patch("/api/me/notifications/read-all"), other).andExpect(status().isOk());
        call(get("/api/me/notifications/unread-count"), other).andExpect(jsonPath("$.data.total").value(0));
        call(get("/api/admin/notifications?typeId=2"), admin).andExpect(jsonPath("$.data.totalElements").value(2));
        json(put("/api/admin/notifications/3"), admin, "{\"title\":\"Sửa\"}").andExpect(jsonPath("$.data.title").value("Sửa"))
                .andExpect(jsonPath("$.data.content").value("All"));
        call(delete("/api/admin/notification-types/2"), admin).andExpect(status().isBadRequest()); // đang dùng
        call(delete("/api/admin/notifications/3"), admin).andExpect(status().isOk());
        call(get("/api/admin/notifications/3"), admin).andExpect(status().isNotFound());
        call(get("/api/me/notifications"), other).andExpect(jsonPath("$.data.totalElements").value(1));
        call(delete("/api/admin/notifications/4"), admin).andExpect(status().isOk());
        call(delete("/api/admin/notification-types/2"), admin).andExpect(status().isOk()); // hết thông báo thì xóa được
        call(get("/api/admin/notification-types/2"), admin).andExpect(status().isNotFound()); // đã xóa mềm
        call(get("/api/admin/notification-types"), admin).andExpect(jsonPath("$.data.length()").value(1));
        call(get("/api/admin/notifications/4"), admin).andExpect(status().isNotFound());

        // ---- Thông báo có ảnh + người dùng tự xóa thông báo của mình ----
        json(post("/api/admin/notifications"), admin,
                "{\"notificationTypeId\":1,\"title\":\"T\",\"content\":\"c\",\"image\":\"http://i/x.png\",\"userIds\":[3]}")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.image").value("http://i/x.png"));
        json(post("/api/admin/notifications"), admin, "{\"notificationTypeId\":1,\"title\":\"T\",\"content\":\" \"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Nội dung không được để trống"));
        call(get("/api/me/notifications"), other).andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].image").value("http://i/x.png"));
        call(delete("/api/me/notifications/7"), cand).andExpect(status().isNotFound());   // của người khác
        call(delete("/api/me/notifications/7"), null).andExpect(status().isUnauthorized());
        call(delete("/api/me/notifications/7"), other).andExpect(status().isOk());
        call(delete("/api/me/notifications/7"), other).andExpect(status().isNotFound());  // xóa rồi
        call(get("/api/me/notifications"), other).andExpect(jsonPath("$.data.totalElements").value(0));
        call(get("/api/me/notifications/unread-count"), other).andExpect(jsonPath("$.data.total").value(0));
        call(get("/api/admin/notifications/5"), admin).andExpect(status().isOk()); // thông báo gốc vẫn còn

        // ---- Xóa CV: đơn vẫn giữ cv_url ----
        when(cloudinaryService.uploadPdf(any(), any())).thenReturn("https://res.cloudinary.com/c/image/upload/v1/cvs/b.pdf");
        mvc.perform(multipart("/api/me/cvs").file(pdf).header("Authorization", "Bearer " + other)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("cv.pdf"));
        call(delete("/api/me/cvs/1"), cand).andExpect(status().isOk());
        call(get("/api/me/applications/2"), cand).andExpect(jsonPath("$.data.cvUrl").value("https://res.cloudinary.com/c/image/upload/v1/cvs/a.pdf"))
                .andExpect(jsonPath("$.data.cvId").doesNotExist());

        // ---- Xóa mềm ----
        call(delete("/api/admin/applications/2"), admin).andExpect(status().isOk());
        call(get("/api/admin/applications/2"), admin).andExpect(status().isNotFound());
        call(delete("/api/admin/jobs/1"), admin).andExpect(status().isOk());
        call(get("/api/jobs/1"), null).andExpect(status().isNotFound());
        // Đơn của job đã xóa mềm cũng bị ẩn theo, danh sách không được văng lỗi
        call(get("/api/admin/applications"), admin).andExpect(status().isOk()).andExpect(jsonPath("$.data.totalElements").value(0));
        call(get("/api/me/applications"), cand).andExpect(status().isOk()).andExpect(jsonPath("$.data.totalElements").value(0));
        call(delete("/api/admin/job-types/1"), admin).andExpect(status().isOk()); // hết job thì xóa mềm được
        call(get("/api/admin/job-types/1"), admin).andExpect(status().isNotFound());
        call(get("/api/job-types"), null).andExpect(jsonPath("$.data.length()").value(0));
        json(patch("/api/admin/jobs/1/status"), admin, "{\"status\":\"OPEN\"}").andExpect(status().isNotFound());
    }
}
```

3. Chạy: `./mvnw -q test -Dtest=JobsNotificationFlowTests`.

---

---

## 5. Những điều cần biết thêm

- **`update` chỉ ghi đè trường khác `null`**: chưa có cách xóa trắng một trường chữ (ví dụ bỏ `department`). Nếu Figma cần, thêm quy ước riêng (chuỗi rỗng là xóa).
- **Gửi thông báo cho tất cả người dùng** đang tạo một dòng `UserNotification` cho mỗi tài khoản trong cùng một transaction. Với vài nghìn tài khoản thì ổn; nếu lên hàng trăm nghìn cần chuyển sang xử lý nền.
- **Thông báo đẩy (FCM)** chưa làm: cần module user hoàn thiện API đăng ký token thiết bị (`user_device`). Khi đó chỉ cần gọi thêm FCM bên trong `NotificationServiceImpl`, các nơi gọi `send()` không phải sửa.
- **Test trên PostgreSQL**: test tự động chạy trên H2. Các câu SQL thuần (`select exists(...)`, `update ... set cv_id = null`) là SQL chuẩn chạy được trên cả hai, nhưng vẫn nên chạy Postman một lượt trên DB thật.
- **Xóa mềm giữ nguyên file CV trên Cloudinary**, nên file không bao giờ bị dọn tự động. Nếu cần dọn định kỳ thì làm job riêng cho các CV có `deleted_at` đã lâu.
- **Không có API khôi phục** bản ghi đã xóa mềm. Muốn khôi phục thì sửa tay `deleted_at = NULL` trong DB (với job thì nhớ khôi phục cả các đơn của nó).

