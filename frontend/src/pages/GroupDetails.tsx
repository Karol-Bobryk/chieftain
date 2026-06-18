import { api } from "@/auth/axios";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import type { RootTaskDisplayDTO } from "@/interfaces/RootTaskDisplayDTO";

export interface GetTasksInGroupResponse {
  tasks: RootTaskDisplayDTO[];
}

const GroupDetails = () => {
  const [tasks, setTasks] = useState<RootTaskDisplayDTO[]>();
  const { groupId } = useParams();
  if (!groupId) return <div>Invalid group</div>;

  const getCurrentWeekRange = () => {
    const now = new Date();

    const day = now.getDay(); // 0 (Sun) - 6 (Sat)

    // convert to Monday-start week
    const diffToMonday = (day + 6) % 7;

    const startOfWeek = new Date(now);
    startOfWeek.setDate(now.getDate() - diffToMonday);
    startOfWeek.setHours(0, 0, 0, 0);

    const endOfWeek = new Date(startOfWeek);
    endOfWeek.setDate(startOfWeek.getDate() + 6);
    endOfWeek.setHours(23, 59, 59, 999);

    return {
      periodStart: startOfWeek.toISOString(),
      periodEnd: endOfWeek.toISOString(),
    };
  };

  const fetchRootTasks = async () => {
    const payload = getCurrentWeekRange();

    const res = await api.get<GetTasksInGroupResponse>(
      `/api/groups/${groupId}/tasks`,
      { params: payload },
    );
    setTasks(res.data.tasks);
  };
  useEffect(() => {
    fetchRootTasks();
  }, [groupId]);

  return (
    <div className="min-h-screen bg-zinc-50 px-6 py-10">
      <div className="mx-auto max-w-5xl space-y-6">
        {/* HEADER */}
        <div className="rounded-3xl border border-zinc-200 bg-white p-6">
          <h1 className="text-2xl font-semibold text-zinc-900">Group Tasks</h1>

          <p className="mt-1 text-sm text-zinc-500">
            Weekly overview of tasks for this group.
          </p>

          <div className="mt-3 text-xs text-zinc-400">Group ID: {groupId}</div>
        </div>

        {/* LOADING */}
        {!tasks && (
          <div className="rounded-3xl border border-zinc-200 bg-white p-10 text-center text-sm text-zinc-500">
            Loading tasks...
          </div>
        )}

        {/* EMPTY STATE */}
        {tasks && tasks.length === 0 && (
          <div className="rounded-3xl border border-zinc-200 bg-white p-10 text-center">
            <h2 className="text-lg font-semibold text-zinc-900">
              No tasks this week
            </h2>

            <p className="mt-1 text-sm text-zinc-500">
              This group has no scheduled tasks in the current week.
            </p>
          </div>
        )}

        {/* TASK LIST */}
        {tasks && tasks.length > 0 && (
          <div className="space-y-4">
            {tasks.map((task) => (
              <div
                key={task.taskId}
                className="rounded-3xl border border-zinc-200 bg-white p-5 transition hover:shadow-sm"
              >
                {/* top row */}
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <h3 className="text-base font-semibold text-zinc-900">
                      {task.name}
                    </h3>

                    <p className="mt-1 text-sm text-zinc-500">
                      {task.description || "No description"}
                    </p>
                  </div>

                  {/* status badge */}
                  <div className="rounded-full bg-zinc-100 px-3 py-1 text-xs font-medium text-zinc-700">
                    {task.status}
                  </div>
                </div>

                {/* metadata */}
                <div className="mt-4 grid grid-cols-1 gap-3 text-sm text-zinc-600 md:grid-cols-3">
                  <div>
                    <p className="text-xs text-zinc-400">Started</p>
                    <p>{new Date(task.startedAt).toLocaleString()}</p>
                  </div>

                  <div>
                    <p className="text-xs text-zinc-400">Deadline</p>
                    <p>{new Date(task.deadline).toLocaleString()}</p>
                  </div>

                  <div>
                    <p className="text-xs text-zinc-400">Assignees</p>
                    <p>{task.assignees.length}</p>
                  </div>
                </div>

                {/* assignees */}
                {task.assignees.length > 0 && (
                  <div className="mt-4 flex flex-wrap gap-2">
                    {task.assignees.map((u) => (
                      <span
                        key={u.userId}
                        className="rounded-full border border-zinc-200 bg-zinc-50 px-3 py-1 text-xs text-zinc-700"
                      >
                        {u.name} {u.surname}
                      </span>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default GroupDetails;
