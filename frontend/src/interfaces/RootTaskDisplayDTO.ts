import type { GroupDisplayDTO } from "@/interfaces/GroupDisplayDTO";
import type { UserDisplayDTO } from "@/interfaces/UserDisplayDTO";
import type { TaskStatus } from "@/enums/TaskStatus";
import type { Event } from "react-big-calendar";

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

export type TaskEvent = Event & {
  taskId: string;
  description: string;
  assignees: { userId: string; name?: string }[];
  doneAt: string | null;
  status: string;
};

export const toTaskEvent = (task: RootTaskDisplayDTO): TaskEvent => {
  return {
    taskId: task.taskId,
    title: task.name,
    description: task.description || "",
    status: task.status.toString() || "CREATED",
    start: new Date(task.startedAt),
    end: new Date(task.deadline),
    doneAt: task.doneAt,
    assignees: task.assignees ?? [],
  };
};
