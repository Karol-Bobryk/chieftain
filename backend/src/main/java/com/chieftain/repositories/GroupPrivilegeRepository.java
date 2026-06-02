package com.chieftain.repositories;

import com.chieftain.models.GroupPrivilegeEntity;
import com.chieftain.models.GroupPrivilegeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPrivilegeRepository extends JpaRepository<GroupPrivilegeEntity, GroupPrivilegeId> {
}
