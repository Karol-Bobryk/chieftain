package com.chieftain.services;

import com.chieftain.enums.GroupUserPermission;
import com.chieftain.models.GroupEntity;
import com.chieftain.models.GroupPrivilegeEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.repositories.GroupRepository;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
  private final GroupRepository groupRepository;

  public GroupService(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  public GroupEntity save(GroupEntity groupEntity) {
    return groupRepository.save(groupEntity);
  }

  public GroupPrivilegeEntity addPrivilegesForUser(
      GroupEntity group, UserEntity user, Collection<GroupUserPermission> permissions) {
    GroupPrivilegeEntity privilegeEntity = new GroupPrivilegeEntity();
    privilegeEntity.setGroup(group);
    privilegeEntity.setUser(user);
    return null;
  }
}
