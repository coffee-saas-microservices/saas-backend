package org.mss301.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mss301.identityservice.dto.request.SystemAdminRegistrationRequest;
import org.mss301.identityservice.dto.response.SystemAdminRegistrationResponse;
import org.mss301.identityservice.entity.User;

@Mapper(componentModel = "spring")
public interface SystemAdminMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "totalPoint", ignore = true)
    @Mapping(target = "fullname", source = "fullName")
    User toEntity(SystemAdminRegistrationRequest request);
    SystemAdminRegistrationResponse toResponse(User user);
}
