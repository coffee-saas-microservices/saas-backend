package org.mss301.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mss301.identityservice.dto.request.CustomerRegistrationRequest;
import org.mss301.identityservice.dto.response.CustomerResponse;
import org.mss301.identityservice.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "password", ignore = true)
    Customer toEntity(CustomerRegistrationRequest request);

    @Mapping(source = "id", target = "customerId")
    @Mapping(source = "membershipRank.id", target = "rankId")
    @Mapping(source = "status", target = "status")
    CustomerResponse toResponse(Customer customer);
}
