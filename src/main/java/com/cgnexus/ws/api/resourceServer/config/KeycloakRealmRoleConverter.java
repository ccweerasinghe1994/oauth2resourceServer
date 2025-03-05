package com.cgnexus.ws.api.resourceServer.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param jwt the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
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
