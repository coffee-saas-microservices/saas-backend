package org.mss301.identityservice.dto.response;

import lombok.Builder;
import lombok.Data;
import org.mss301.identityservice.entity.enumeration.UserStatus;

@Data
@Builder
public class SystemAdminRegistrationResponse {

    private Long id;
    private String username;
    private String email;
    private String fullname;
    private String phone;
    private UserStatus status;
}
