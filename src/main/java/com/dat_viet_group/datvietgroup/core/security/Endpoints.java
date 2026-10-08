package com.dat_viet_group.datvietgroup.core.security;

import java.util.List;

public class Endpoints {
    public static final List<String> FRONT_END_HOSTS = List.of(
            "http://localhost:5173");

    public static final String[] PUBLIC_GET_ENDPOINTS = new String[] {
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = new String[] {
            "/api/users/register",
            "/api/users/login",
            "/api/test/cloudinary/**",
    };

    public static final String[] PRIVATE_GET_ENDPOINT = new String[] {
            "/api/posts",                       // danh sách bài viết
            "/api/posts/*",                     // chi tiết bài viết
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
    };

    public static final String[] ADMIN_PUT_ENDPOINTS = new String[] {
    };

    public static final String[] ADMIN_GET_ENDPOINTS = new String[] {
    };

}