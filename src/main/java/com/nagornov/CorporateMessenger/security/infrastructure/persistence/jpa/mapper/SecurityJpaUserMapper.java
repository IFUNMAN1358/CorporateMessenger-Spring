package com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.security.domain.model.SecurityRole;
import com.nagornov.CorporateMessenger.security.domain.model.SecuritySession;
import com.nagornov.CorporateMessenger.security.domain.model.SecurityUser;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.entity.SecurityJpaRoleEntity;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.entity.SecurityJpaSessionEntity;
import com.nagornov.CorporateMessenger.security.infrastructure.persistence.jpa.entity.SecurityJpaUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SecurityJpaUserMapper {

    //
    // TO DOMAIN
    //

    @Mapping(target = "roles", source = "entity.roles")
    @Mapping(target = "session", source = "entity.session")
    SecurityUser toDomain(SecurityJpaUserEntity entity);

    default Set<SecurityRole> mapRoles(Set<SecurityJpaRoleEntity> jpaRoleEntities) {
        if (jpaRoleEntities == null) {
            return null;
        }
        return jpaRoleEntities.stream()
                .map(jpaRoleEntity -> new SecurityRole(
                        jpaRoleEntity.getId(),
                        jpaRoleEntity.getName()
                )).collect(Collectors.toSet());
    }

    default SecuritySession mapSession(SecurityJpaSessionEntity jpaSessionEntity) {
        if (jpaSessionEntity == null) {
            return null;
        }
        return new SecuritySession(
                jpaSessionEntity.getId(),
                jpaSessionEntity.getUser().getId(),
                jpaSessionEntity.getAccessToken(),
                jpaSessionEntity.getRefreshToken()
        );
    }

    //
    // TO ENTITY
    //

    @Mapping(target = "roles", source = "user.roles")
    @Mapping(target = "session", source = "user.session")
    SecurityJpaUserEntity toEntity(SecurityUser user);

    default Set<SecurityJpaRoleEntity> toRoleEntities(Set<SecurityRole> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> new SecurityJpaRoleEntity(
                        role.getId(),
                        role.getName(),
                        null
                ))
                .collect(Collectors.toSet());
    }

    default SecurityJpaSessionEntity toSessionEntity(SecuritySession session) {
        if (session == null) {
            return null;
        }
        return new SecurityJpaSessionEntity(
                session.getId(),
                session.getAccessToken(),
                session.getRefreshToken(),
                null
        );
    }
}
