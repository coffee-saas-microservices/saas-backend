package org.mss301.identityservice.service;

import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.identityservice.dto.request.UpdateProfileRequest;
import org.mss301.identityservice.dto.request.UserFilter;
import org.mss301.identityservice.dto.response.UserResponse;
import org.mss301.identityservice.entity.enumeration.UserType;

public interface UserService {
    UserResponse getUserProfile(String keycloakUserId);

    UserResponse updateUserProfile(String keycloakUserId, UpdateProfileRequest request);

    UserResponse getUserById(Long id);

    PageResponse<UserResponse> getAllUsers(UserFilter filter);

    void deleteUser(Long id);

    UserResponse assignUserType(Long userId, UserType userType);
}
