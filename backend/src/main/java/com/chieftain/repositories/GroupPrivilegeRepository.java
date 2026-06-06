package com.chieftain.repositories;

import com.chieftain.models.GroupPrivilegeEntity;
import com.chieftain.models.GroupPrivilegeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface GroupPrivilegeRepository extends JpaRepository<GroupPrivilegeEntity, GroupPrivilegeId> {
    @Modifying
    @Query("delete from GroupPrivilegeEntity g where g.user.pkUserId =:userId and g.group.id =:groupId")
    void deleteByUserPkUserIdAndGroupId(@Param("userId") UUID userId, @Param("groupId") UUID groupId);
}
