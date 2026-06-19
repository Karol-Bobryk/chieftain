import { api } from "@/auth/axios";
import type { TaskEvent } from "@/interfaces/RootTaskDisplayDTO";

const deleteTask = async (event: TaskEvent) => {
  await api.delete(`/api/tasks/${event.taskId}/delete`);
};

export default deleteTask;
