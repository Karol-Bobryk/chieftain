import { TaskPositional } from "@/helpers/TaskPositional";
import type { RootTaskDisplayDTO } from "@/interfaces/RootTaskDisplayDTO";
import { useEffect, useRef, useState } from "react";

interface TaskCardProperties {
  task: RootTaskDisplayDTO;
}

const TaskCard = ({ task }: TaskCardProperties) => {
  const gridRef = useRef<HTMLDivElement>(null);
  const [columnWidth, setColumnWidth] = useState(80);

  const [positional, setPositional] = useState<TaskPositional>(
    new TaskPositional(task, columnWidth),
  );

  const updateWidth = () => {
    if (!gridRef.current) return;

    const fullWidth = gridRef.current.clientWidth - 80; // subtract time column
    setColumnWidth(fullWidth / 7);
    updateWidth();
  };

  useEffect(() => {
    setPositional(positional);

    const observer = new ResizeObserver(updateWidth);
    if (gridRef.current) observer.observe(gridRef.current);

    return () => observer.disconnect();
  }, []);

  return (
    <div
      className="
        group absolute
        overflow-visible
        rounded-xl border border-blue-200 bg-blue-50 p-2
        transition-all duration-200
        hover:z-50"
      style={{
        position: "absolute",
        top: positional.top,
        left: positional.left,
        width: positional.width,
        height: positional.height,
      }}
    >
      <div className=" hidden group-hover:block absolute left-0 top-0 z-50 w-64 rounded-lg border border-blue-200 bg-white p-2 shadow-lg">
        <div className="truncate text-sm font-semibold text-blue-950  group-hover:visible invisible group-hover:opacity-100 opacity-0 transition-all">
          {task.name}
        </div>

        <div className="mt-1 text-xs text-blue-700">
          {new Date(task.startedAt).toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          })}
          {" - "}
          {new Date(task.deadline).toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          })}
        </div>

        {task.assignees.length > 0 && (
          <div className="mt-2 flex -space-x-2">
            {task.assignees.slice(0, 3).map((user) => (
              <div
                key={user.userId}
                className="flex h-6 w-6 items-center justify-center rounded-full border border-white bg-blue-500 text-[10px] font-medium text-white"
              >
                {user.name?.[0]?.toUpperCase()}
              </div>
            ))}

            {task.assignees.length > 3 && (
              <div className="flex h-6 w-6 items-center justify-center rounded-full border border-white bg-blue-100 text-[10px] font-medium text-blue-700">
                +{task.assignees.length - 3}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default TaskCard;
