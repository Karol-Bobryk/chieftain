package com.chieftain.repositories;

import com.chieftain.models.GroupPrivilegeEntity;
import com.chieftain.models.GroupPrivilegeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupPrivilegeRepository extends JpaRepository<GroupPrivilegeEntity, GroupPrivilegeId> {
    void deleteByUserPkUserIdAndGroupId(UUID userId, UUID groupId);
}
