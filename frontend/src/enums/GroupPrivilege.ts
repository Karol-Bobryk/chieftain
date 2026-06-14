export const GroupPrivileges = {
  ADD_TASK: "ADD_TASK",
  REMOVE_TASK: "REMOVE_TASK",
  EDIT_TASK: "EDIT_TASK",
  ADD_USER_TO_GROUP: "ADD_USER_TO_GROUP",
  REMOVE_USER_FROM_GROUP: "REMOVE_USER_FROM_GROUP",
} as const;

export type GroupPrivileges =
  (typeof GroupPrivileges)[keyof typeof GroupPrivileges];
