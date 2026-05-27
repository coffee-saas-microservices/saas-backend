package org.mss301.commonservice.keycloak;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeyCloakClientProperties {

    private String authServerUrl;
    private String realm;
    private String adminRealm;
    private String clientId;
    private String clientSecret;
    private String adminClientId;
    private String adminClientSecret;

    public String tokenEndpoint() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    public String adminTokenEndpoint() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    public String logoutEndpoint() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
    }

    public String adminUsersEndpoint() {
        return authServerUrl + "/admin/realms/" + realm + "/users";
    }

    public String adminUserByIdEndPoint(String id) {
        return adminUsersEndpoint() + "/" + id;
    }

    public String adminRolesByNameEndPoint(String roleName) {
        return authServerUrl + "/admin/realms/" + realm + "/roles/" + roleName;
    }

    public String adminUserRealmRoleMappingEndpoint(String id) {
        return adminUsersEndpoint() + "/" + id + "/role-mappings/realm";
    }
}
