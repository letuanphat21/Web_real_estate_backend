package com.dat_viet_group.datvietgroup.core.security;

import java.util.List;

public class Endpoints {
    public static final List<String> FRONT_END_HOSTS = List.of(
            "http://localhost:5173");

    public static final String[] PUBLIC_GET_ENDPOINTS = new String[] {
            // --- EVENT ---
            "/api/events",                      // danh sách sự kiện (?status=&keyword=&page=&size=&sort=)
            "/api/events/*",                    // chi tiết sự kiện (kèm ảnh, số người tham gia)
            "/api/events/*/members",            // danh sách thành viên tham gia
            "/api/events/*/comments",           // bình luận của sự kiện (phân trang)
            // --- JOBS ---
            "/api/jobs",                        // danh sách tin tuyển dụng
            "/api/jobs/*",                      // chi tiết tin tuyển dụng
            "/api/job-types",                   // danh sách loại công việc đang hiện
            // --- PROJECT ---
            "/api/projects",                    // danh sách dự án (?page=&size=&sort=)
            "/api/projects/*",                  // chi tiết dự án
            "/api/zones",                       // danh sách phân khu (?projectId=&page=&size=)
            "/api/zones/*",                     // chi tiết phân khu
            "/api/properties",                  // danh sách bất động sản (?zoneId=&status=&page=&size=)
            "/api/properties/*",                // chi tiết bất động sản
            "/api/questions",                   // danh sách câu hỏi của dự án (?projectId=&page=&size=)
            "/api/questions/*",                 // chi tiết câu hỏi
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = new String[] {
            "/api/users/register",
            "/api/users/login",
            "/api/users/refresh-token",
            "/api/users/logout",
            "/api/test/cloudinary/**", 
    };

    public static final String[] PRIVATE_GET_ENDPOINT = new String[] {
            "/api/users/me",                    // thông tin cá nhân của chính người đang đăng nhập
            "/api/posts",                       // danh sách bài viết
            "/api/posts/*",                  // chi tiết bài viết
            "/api/posts/*/reactions",           // tổng lượt thích + trạng thái thích của tôi
            "/api/comments/post/*",             // bình luận gốc của bài viết
            "/api/comments/*/replies",          // trả lời của một bình luận
            // --- SOCIAL: FOLLOW ---
            "/api/follows/*/followers",         // danh sách người theo dõi
            "/api/follows/*/following",         // danh sách đang theo dõi
            "/api/follows/*/stats",             // số follower/following + tôi có theo dõi không
    };

    public static final String[] PRIVATE_POST_ENDPOINT = new String[] {
            "/api/posts",                       // đăng bài (form-data: content, images)
            "/api/posts/*/reactions",           // thích / bỏ thích bài viết
            "/api/comments",                    // bình luận / trả lời (JSON)
            // --- SOCIAL: FOLLOW ---
            "/api/follows/*",                   // theo dõi / bỏ theo dõi
    };

    public static final String[] PRIVATE_PUT_ENDPOINT = new String[] {
            // --- EVENT ---
            "/api/events/*",                    // sửa sự kiện (JSON) — người tạo / ADMIN
            "/api/events/*/status",             // đổi trạng thái (JSON: status) — người tạo / ADMIN
    };

    public static final String[] PRIVATE_DELETE_ENDPOINT = new String[] {
            // --- EVENT ---
            "/api/events/*",                    // xóa sự kiện (kèm ảnh Cloudinary, thành viên, bình luận) — người tạo / ADMIN
            "/api/events/*/images/*",           // xóa 1 ảnh — người tạo / ADMIN
            "/api/events/*/join",               // rời sự kiện
            "/api/events/*/comments/*",         // xóa mềm bình luận — người viết / ADMIN
    };

    public static final String[] ADMIN_PUT_ENDPOINTS = new String[] {
            "/api/admin/**",
    };

    public static final String[] ADMIN_GET_ENDPOINTS = new String[] {
            "/api/admin/**",
    };

    public static final String[] ADMIN_POST_ENDPOINTS = new String[] {
            "/api/admin/**",
    };

    public static final String[] ADMIN_PATCH_ENDPOINTS = new String[] {
            "/api/admin/**",
    };

    public static final String[] ADMIN_DELETE_ENDPOINTS = new String[] {
            "/api/admin/**",
    };
}