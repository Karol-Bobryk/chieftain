import type { GroupDisplayDTO } from "@/interfaces/GroupDisplayDTO";
import type { UserDisplayDTO } from "@/interfaces/UserDisplayDTO";
import type { TaskStatus } from "@/enums/TaskStatus";

export interface RootTaskDisplayDTO {
  taskId: string;

  creatorUser?: UserDisplayDTO;

  group: GroupDisplayDTO;

  name: string;

  startedAt: string;

  createdAt: string;

  doneAt: string | null;

  deadline: string;

  status: TaskStatus;

  description: string | null;

  assignees: UserDisplayDTO[];
}
