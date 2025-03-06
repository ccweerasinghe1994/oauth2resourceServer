package com.cgnexus.ws.api.resourceServer.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is a custom converter that extracts realm roles from a Keycloak JWT and converts them
 * into a collection of {@link GrantedAuthority} objects for Spring Security authorization.
 * It expects the JWT to have a 'realm_access' claim containing a 'roles' collection, which is
 * the standard structure for Keycloak realm roles. Each role is prefixed with 'ROLE_' to conform
 * to Spring Security's role naming convention.
 */
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    /**
     * Converts a Keycloak JWT (JSON Web Token) into a collection of {@link GrantedAuthority} objects.
     * It extracts the 'realm_access' claim from the JWT, then retrieves the 'roles' from within it.
     * Each role name is then prefixed with 'ROLE_' and converted to a {@link SimpleGrantedAuthority}.
     *
     * @param jwt the source object to convert, which must be a Keycloak JWT.
     *            It is expected to contain a 'realm_access' claim with 'roles' within it.
     *            Must not be {@code null}.
     * @return a collection of {@link GrantedAuthority} objects representing the realm roles from the JWT.
     * Returns an empty collection if 'realm_access' or 'roles' claims are missing or empty.
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type (although this implementation
     *                                  does not explicitly throw this exception, it's part of the {@link Converter} contract).
     */
    @Override
    @SuppressWarnings("unchecked") // Suppress unchecked cast warning - safe in this context
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        final Map<String, Collection<String>> realmAccess = (Map<String, Collection<String>>) jwt.getClaims().get("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return List.of();
        }
        Collection<String> realmRoles = realmAccess.get("roles");

        if (realmRoles == null || realmRoles.isEmpty()) {
            return List.of();
        }

        return realmRoles.stream()
                .map(roleName -> "ROLE_" + roleName) // Prefix roles with ROLE_
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
