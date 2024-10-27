package com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.mapper;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthRole;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthSession;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaRoleEntity;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaSessionEntity;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.entity.AuthJpaUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthJpaUserMapper {

    //
    // TO DOMAIN
    //

    @Mapping(target = "roles", source = "entity.roles")
    @Mapping(target = "session", source = "entity.session")
    AuthUser toDomain(AuthJpaUserEntity entity);

    default Set<AuthRole> mapRoles(Set<AuthJpaRoleEntity> jpaRoleEntities) {
        if (jpaRoleEntities == null) {
            return null;
        }
        return jpaRoleEntities.stream()
                .map(jpaRoleEntity -> new AuthRole(
                        jpaRoleEntity.getId(),
                        jpaRoleEntity.getName(),
                        jpaRoleEntity.getCreatedAt()
                )).collect(Collectors.toSet());
    }

    default AuthSession mapSession(AuthJpaSessionEntity jpaSessionEntity) {
        if (jpaSessionEntity == null) {
            return null;
        }
        return new AuthSession(
                jpaSessionEntity.getId(),
                jpaSessionEntity.getUser().getId(),
                jpaSessionEntity.getAccessToken(),
                jpaSessionEntity.getRefreshToken(),
                jpaSessionEntity.getCreatedAt(),
                jpaSessionEntity.getUpdatedAt()
        );
    }

    //
    // TO ENTITY
    //

    @Mapping(target = "roles", source = "user.roles")
    @Mapping(target = "session", source = "user.session")
    AuthJpaUserEntity toEntity(AuthUser user);

    default Set<AuthJpaRoleEntity> toRoleEntities(Set<AuthRole> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> new AuthJpaRoleEntity(
                        role.getId(),
                        role.getName(),
                        role.getCreatedAt(),
                        null
                ))
                .collect(Collectors.toSet());
    }

    default AuthJpaSessionEntity toSessionEntity(AuthSession session) {
        if (session == null) {
            return null;
        }
        return new AuthJpaSessionEntity(
                session.getId(),
                session.getAccessToken(),
                session.getRefreshToken(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                null
        );
    }

}
