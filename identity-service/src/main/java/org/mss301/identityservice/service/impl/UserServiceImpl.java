package org.mss301.identityservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.keycloak.KeyCloakAuthClient;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.identityservice.dto.request.UpdateProfileRequest;
import org.mss301.identityservice.dto.request.UserFilter;
import org.mss301.identityservice.dto.response.UserResponse;
import org.mss301.identityservice.entity.User;
import org.mss301.identityservice.entity.enumeration.UserStatus;
import org.mss301.identityservice.entity.enumeration.UserType;
import org.mss301.identityservice.mapper.UserMapper;
import org.mss301.identityservice.repository.UserRepository;
import org.mss301.identityservice.service.UserService;
import org.mss301.identityservice.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final KeyCloakAuthClient keyCloakAuthClient;

    private static final Map<UserType, String> USER_TYPE_TO_KEYCLOAK_ROLE = Map.of(
            UserType.CUSTOMER, "CUSTOMER",
            UserType.EMPLOYEE, "EMPLOYEE",
            UserType.SHOP_ADMIN, "SHOP_ADMIN",
            UserType.SYSTEM_ADMIN, "SYSTEM_ADMIN");

    @Override
    public UserResponse getUserProfile(String keycloakUserId) {
        User user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản"));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(String keycloakUserId, UpdateProfileRequest request) {
        User user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản"));

        if (StringUtils.hasText(request.getFullname())) {
            user.setFullname(request.getFullname());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getAddress())) {
            user.setAddress(request.getAddress());
        }
        if (request.getDob() != null) {
            user.setDob(request.getDob());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
        Long shopId = requireShopId();
        User user = userRepository.findByIdAndShopIdAndStatusNot(id, shopId, UserStatus.DELETED)
                .orElseThrow(() -> new BusinessException("Không tìm thấy khách hàng"));
        return userMapper.toResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(UserFilter filter) {
        Long shopId = requireShopId();

        Specification<User> spec = UserSpecification.filter(shopId, filter);
        Page<UserResponse> page = userRepository.findAll(spec, filter.toPageable())
                .map(userMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        Long shopId = requireShopId();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy khách hàng"));

        if (!user.getShopId().equals(shopId)) {
            throw new BusinessException("Bạn không có quyền xóa khách hàng này");
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException("Khách hàng này đã bị xóa trước đó");
        }

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse assignUserType(Long userId, UserType userType) {
        Long shopId = requireShopId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng"));

        if (!user.getShopId().equals(shopId)) {
            throw new BusinessException("Bạn không có quyền thay đổi role của người dùng này");
        }

        if (user.getUserType() == UserType.SYSTEM_ADMIN) {
            throw new BusinessException("Không thể thay đổi role của tài khoản quản trị hệ thống");
        }

        String keycloakRole = USER_TYPE_TO_KEYCLOAK_ROLE.get(userType);
        if (keycloakRole == null) {
            throw new BusinessException("Loại người dùng không hợp lệ");
        }

        keyCloakAuthClient.updateUserRoles(user.getKeycloakUserId(), List.of(keycloakRole));

        user.setUserType(userType);
        return userMapper.toResponse(userRepository.save(user));
    }

    private Long requireShopId() {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null) {
            throw new BusinessException("Không tìm thấy cửa hàng");
        }
        return shopId;
    }
}
