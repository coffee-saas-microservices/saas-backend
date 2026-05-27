package org.mss301.identityservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRegistrationRequest {
    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50)
    private String username;
    private String fullname;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu tối thiểu phải từ 6 ký tự")
    private String password;

    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng Email không hợp lệ")
    private String email;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
