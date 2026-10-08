package com.dat_viet_group.datvietgroup.modules.social.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "Bài viết không được để trống")
    private Long postId;

    /** Không bắt buộc: có = trả lời một bình luận, không có = bình luận gốc. */
    private Long parentId;

    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(max = 500, message = "Nội dung bình luận tối đa 500 ký tự")
    private String content;
}
