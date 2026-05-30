package org.mss301.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mss301.identityservice.dto.request.CustomerRegistrationRequest;
import org.mss301.identityservice.dto.response.CustomerResponse;
import org.mss301.identityservice.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toEntity(CustomerRegistrationRequest request);

    @Mapping(source = "id", target = "customerId")
    @Mapping(source = "membershipRank.id", target = "rankId")
    @Mapping(source = "status", target = "status")
    CustomerResponse toResponse(User user);
}
