package com.dat_viet_group.datvietgroup.modules.news.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryNewRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 500, message = "Tên danh mục tối đa 500 ký tự")
    private String name;

    // Không gửi thì tự sinh từ tên, vd "Tin thị trường" -> "tin-thi-truong" 
    @Size(max = 200, message = "Slug tối đa 200 ký tự")
    @Pattern(regexp = "^$|^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ gồm chữ thường không dấu, số và dấu gạch ngang")
    private String slug;

    // Không gửi thì mặc định hiển thị
    private Boolean active;
}
