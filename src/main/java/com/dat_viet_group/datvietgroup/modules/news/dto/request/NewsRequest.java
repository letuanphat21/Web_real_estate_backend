package com.dat_viet_group.datvietgroup.modules.news.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Người đăng lấy từ token, không nhận userId từ client
@Getter
@Setter
@NoArgsConstructor
public class NewsRequest {

    @NotNull(message = "Dự án không được để trống")
    @Positive(message = "Mã dự án phải lớn hơn 0")
    private Long projectId;

    @NotNull(message = "Danh mục tin tức không được để trống")
    @Positive(message = "Mã danh mục phải lớn hơn 0")
    private Long categoryId;

    @NotBlank(message = "Tiêu đề tin tức không được để trống")
    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    @NotBlank(message = "Nội dung tin tức không được để trống")
    @Size(max = 500, message = "Nội dung tối đa 500 ký tự")
    private String content;

    // Không gửi thì mặc định hiển thị
    private Boolean active;
}
