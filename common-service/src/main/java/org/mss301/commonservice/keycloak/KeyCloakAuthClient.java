package org.mss301.commonservice.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.exception.BusinessException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeyCloakAuthClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RestClient.Builder restClientBuilder;
    private final KeyCloakClientProperties properties;

    public KeyCloakTokenResponse login(String username, String password) {
        MultiValueMap<String, String> form = baseClientForm();
        form.add("username", username);
        form.add("password", password);
        form.add("grant_type", "password");
        return postToken(form);
    }

    public void logout(String refreshToken) {
        MultiValueMap<String, String> form = baseClientForm();
        form.add("refresh_token", refreshToken);

        execute(() -> restClientBuilder.build()
                .post()
                .uri(properties.logoutEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toBodilessEntity());
    }

    public String createUser(
            @NonNull String username,
            @NonNull String email,
            String fullName,
            String phone,
            String address,
            String dob,
            String created,
            @NonNull String password,
            List<String> realmRoles) {
        String adminToken = fetchAdminAccessToken();

        Map<String, Object> payload = new HashMap<>();
        payload.put("enabled", true);
        payload.put("username", username);
        payload.put("email", email);
        payload.put("emailVerified", false);
        payload.put("credentials", List.of(Map.of(
                "type", "password",
                "value", password,
                "temporary", false)));

        Map<String, List<String>> attributes = new HashMap<>();
        if (phone != null && !phone.isBlank()) {
            attributes.put("phone", List.of(phone));
        }
        if (fullName != null && !fullName.isBlank()) {
            attributes.put("fullname", List.of(fullName));
        }
        if (address != null && !address.isBlank()) {
            attributes.put("address", List.of(address));
        }
        if (dob != null && !dob.isBlank()) {
            attributes.put("dob", List.of(dob));
        }
        if (created != null && !created.isBlank()) {
            attributes.put("created", List.of(created));
        }
        if (!attributes.isEmpty()) {
            payload.put("attributes", attributes);
        }

        URI location = execute(() -> restClientBuilder.build()
                .post()
                .uri(properties.adminUsersEndpoint())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity())
                .getHeaders()
                .getLocation();

        if (location == null) {
            throw new BusinessException("keycloak.create.user.failed");
        }

        String keycloakUserId = extractUserIdFromLocation(location.toString());
        assignRealmRoles(adminToken, keycloakUserId, realmRoles);
        return keycloakUserId;
    }

    public void deleteUser(String keycloakUserId) {
        String adminToken = fetchAdminAccessToken();
        execute(() -> restClientBuilder.build()
                .delete()
                .uri(properties.adminUserByIdEndPoint(keycloakUserId))
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .toBodilessEntity());
    }

    public KeyCloakTokenResponse loginAdmin() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", properties.getAdminClientId());
        formData.add("client_secret", properties.getAdminClientSecret());

        return restClientBuilder.build().post()
                .uri(properties.adminTokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(KeyCloakTokenResponse.class);
    }

    public String createUserWithAttributes(
            String username, String email, String fullName, String phone,
            String password, List<String> realmRoles, Map<String, List<String>> attributes
    ) {
        String adminToken = getAdminToken();
        Map<String, Object> userFields = new HashMap<>();
        userFields.put("username", username);
        userFields.put("email", email);
        userFields.put("enabled", true);

        // Build attributes map (fullname, phone, and any extra attributes)
        Map<String, List<String>> allAttributes = new HashMap<>();
        if (attributes != null) {
            allAttributes.putAll(attributes);
        }
        if (fullName != null && !fullName.isBlank()) {
            allAttributes.put("fullname", List.of(fullName));
        }
        if (phone != null && !phone.isBlank()) {
            allAttributes.put("phone", List.of(phone));
        }
        if (!allAttributes.isEmpty()) {
            userFields.put("attributes", allAttributes);
        }

        userFields.put("credentials", List.of(Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        )));

        URI location = execute(() -> restClientBuilder.build().post()
                .uri(properties.adminUsersEndpoint())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userFields)
                .retrieve()
                .toBodilessEntity())
                .getHeaders()
                .getLocation();

        if (location == null) {
            throw new BusinessException("keycloak.create.user.failed");
        }

        String keycloakUserId = extractUserIdFromLocation(location.toString());
        assignRealmRoles(adminToken, keycloakUserId, realmRoles);
        log.info("Đã tạo xong tài khoản Keycloak cho user: {} (id: {})", username, keycloakUserId);
        return keycloakUserId;
    }

    private String getAdminToken() {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "client_credentials");
            formData.add("client_id", properties.getAdminClientId());
            formData.add("client_secret", properties.getAdminClientSecret());

            log.debug("Admin token endpoint: {}", properties.adminTokenEndpoint());
            KeyCloakTokenResponse response = restClientBuilder.build().post()
                    .uri(properties.adminTokenEndpoint())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(KeyCloakTokenResponse.class);
            if (response == null || response.getAccessToken() == null) {
                throw new BusinessException("KEYCLOAK_TOKEN_EMPTY");
            }
            return response.getAccessToken();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get admin token from Keycloak: {}", e.getMessage(), e);
            throw new BusinessException("KEYCLOAK_ADMIN_AUTH_FAILED");
        }
    }

    private String fetchAdminAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", properties.getAdminClientId());
        form.add("client_secret", properties.getAdminClientSecret());
        form.add("grant_type", "client_credentials");

        KeyCloakTokenResponse tokenResponse = execute(() -> restClientBuilder.build()
                .post()
                .uri(properties.adminTokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(KeyCloakTokenResponse.class));

        if (tokenResponse == null || tokenResponse.getAccessToken() == null
                || tokenResponse.getAccessToken().isBlank()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "keycloak.admin.token.failed");
        }
        return tokenResponse.getAccessToken();
    }

    private void assignRealmRoles(String adminToken, String keycloakUserId, List<String> realmRoles) {
        if (realmRoles == null || realmRoles.isEmpty()) {
            return;
        }

        List<Map<String, Object>> roleRepresentations = new ArrayList<>();
        for (String roleName : realmRoles) {
            String roleUrl = properties.adminRolesByNameEndPoint(roleName);
            Map<String, Object> roleRepresentation = execute(() -> restClientBuilder.build()
                    .get()
                    .uri(roleUrl)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    }));
            roleRepresentations.add(roleRepresentation);
        }

        String mappingUrl = properties.adminUserRealmRoleMappingEndpoint(keycloakUserId);
        execute(() -> restClientBuilder.build()
                .post()
                .uri(mappingUrl)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(roleRepresentations)
                .retrieve()
                .toBodilessEntity());
    }

    private String extractUserIdFromLocation(String location) {
        int idx = location.lastIndexOf('/');
        if (idx < 0 || idx == location.length() - 1) {
            throw new BusinessException("keycloak.invalid.user.location");
        }
        return location.substring(idx + 1);
    }

    private KeyCloakTokenResponse postToken(MultiValueMap<String, String> form) {
        return execute(() -> restClientBuilder.build()
                .post()
                .uri(properties.tokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(KeyCloakTokenResponse.class));
    }

    private MultiValueMap<String, String> baseClientForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", properties.getClientId());
        if (properties.getClientSecret() != null && !properties.getClientSecret().isBlank()) {
            form.add("client_secret", properties.getClientSecret());
        }
        return form;
    }

    private static final Map<Integer, HttpStatus> STATUS_MAP = Map.of(
            400, HttpStatus.BAD_REQUEST,
            401, HttpStatus.UNAUTHORIZED,
            403, HttpStatus.FORBIDDEN,
            404, HttpStatus.NOT_FOUND,
            409, HttpStatus.CONFLICT);

    private <T> T execute(CheckedSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RestClientResponseException ex) {
            int status = ex.getStatusCode().value();
            String msg = parseKeycloakErrorMessage(ex.getResponseBodyAsString());
            throw new BusinessException(STATUS_MAP.getOrDefault(status, HttpStatus.BAD_REQUEST), msg);
        }
    }

    private String parseKeycloakErrorMessage(String body) {
        if (body == null || body.isBlank())
            return "Lỗi không xác định từ Keycloak";
        try {
            JsonNode json = objectMapper.readTree(body);
            return json.has("errorMessage") ? json.get("errorMessage").asText()
                    : json.has("error_description") ? json.get("error_description").asText()
                            : json.has("error") ? json.get("error").asText()
                                    : body;
        } catch (Exception e) {
            return body;
        }
    }

    @FunctionalInterface
    private interface CheckedSupplier<T> {
        T get();
    }
}
