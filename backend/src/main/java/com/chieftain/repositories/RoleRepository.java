package com.chieftain.repositories;

import com.chieftain.enums.SystemRole;
import com.chieftain.models.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByRoleName(SystemRole roleName);
}
