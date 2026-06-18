package com.chieftain.enums;

import java.util.Set;

public enum SystemRole {
  SITE_ADMIN,
  OWNER,
  TASK_MASTER,
  GROUP_USER,
  GUEST;

  public boolean canAssign(SystemRole role){
    return switch(this){
      case SITE_ADMIN -> Set.of(OWNER, TASK_MASTER, GROUP_USER).contains(role);
      case OWNER -> Set.of(TASK_MASTER, GROUP_USER).contains(role);
      case TASK_MASTER -> Set.of(GROUP_USER).contains(role);
      default -> false;
    };
  }
}
