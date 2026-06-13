export const SystemRole = {
  SITE_ADMIN: "SITE_ADMIN",
  OWNER: "OWNER",
  TASK_MASTER: "TASK_MASTER",
  GROUP_USER: "GROUP_USER",
  GUEST: "GUEST",
} as const;

export type SystemRole = (typeof SystemRole)[keyof typeof SystemRole];
