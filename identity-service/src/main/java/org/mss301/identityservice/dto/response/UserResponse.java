package org.mss301.identityservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long customerId;
    private String username;
    private String fullname;
    private Long rankId;
    private String email;
    private String phone;
    private String address;
    private LocalDate dob;
    private LocalDateTime createdAt;
    private String status;
}
