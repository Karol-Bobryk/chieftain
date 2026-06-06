package com.chieftain.repositories;

import com.chieftain.enums.SystemRole;
import com.chieftain.models.RoleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
  Optional<RoleEntity> findByRoleName(SystemRole roleName);
}
