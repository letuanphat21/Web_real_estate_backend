package com.dat_viet_group.datvietgroup.modules.Jobs.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationCreateRequest {

    @NotNull(message = "Vui lòng chọn CV")
    private Long cvId;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên tối đa 100 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    private String coverLetter;

    // Biết tin tuyển dụng từ đâu: Facebook, LinkedIn, bạn bè...
    @Size(max = 100, message = "Nguồn tối đa 100 ký tự")
    private String sources;
}
