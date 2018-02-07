package com.softserve.academy.spaced.repetition.security;

import com.softserve.academy.spaced.repetition.domain.enums.Operations;
import com.softserve.academy.spaced.repetition.domain.Permission;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.softserve.academy.spaced.repetition.domain.enums.EntityName.COURSE;
import static com.softserve.academy.spaced.repetition.domain.enums.EntityName.DECK_COMMENT;
import static com.softserve.academy.spaced.repetition.domain.enums.Operations.*;
import static com.softserve.academy.spaced.repetition.domain.enums.AuthorityName.*;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    //Collection for permission matrix
    //In future will be replaced by using DB
    //TODO create entity in DB for Permission matrix
    private static final Map<String, Set<Permission>> permissionMatrix = new HashMap<>();

    static {
        Set<Permission> anonymousPermission = new HashSet<>();
        Set<Permission> userPermission = new HashSet<>();
        Set<Permission> adminPermission = new HashSet<>();

        //Collection of permissions for role ANONYMOUS
        anonymousPermission.add(new Permission(DECK_COMMENT, createMask(READ)));

        //Collection of permissions for role USER
        userPermission.add(new Permission(DECK_COMMENT, createMask(CREATE, READ, UPDATE, DELETE)));
        userPermission.add(new Permission(COURSE, createMask(CREATE, READ, UPDATE, DELETE)));

        //Collection of permissions for role ADMIN
        adminPermission.add(new Permission(DECK_COMMENT, createMask(CREATE, READ, UPDATE, DELETE)));
        userPermission.add(new Permission(COURSE, createMask(CREATE, READ, UPDATE, DELETE)));

        permissionMatrix.put(ROLE_ANONYMOUS.name(), anonymousPermission);
        permissionMatrix.put(ROLE_USER.name(), userPermission);
        permissionMatrix.put(ROLE_ADMIN.name(), adminPermission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return hasPrivilege(authentication, (String) targetDomainObject, Operations.valueOf((String) permission).getMask());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPrivilege(authentication, targetType, Operations.valueOf((String) permission).getMask());
    }

    private boolean hasPrivilege(Authentication authentication, String targetType, int operation) {
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            for (Permission permission : permissionMatrix.get(grantedAuthority.getAuthority())) {
                if (permission.getEntityName().name().equals(targetType) && (permission.getPermissionMask() & operation) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
