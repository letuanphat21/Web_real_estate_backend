package com.dat_viet_group.datvietgroup.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
public class LoginRequest {

    @NotBlank(message = "Email hoặc số điện thoại không được để trống") 
    private String emailOrPhone;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    
}
