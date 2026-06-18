import type { TaskEvent } from "@/interfaces/RootTaskDisplayDTO";

type TaskEventMenuProps = {
  event: TaskEvent;
  position: { x: number; y: number };
  onClose: () => void;
  onEdit?: (event: TaskEvent) => void;
  onDelete?: (event: TaskEvent) => void;
};

export const TaskEventMenu = ({
  event,
  position,
  onClose,
  onEdit,
  onDelete,
}: TaskEventMenuProps) => {
  return (
    <>
      {/* backdrop */}
      <div className="fixed inset-0 z-40" onClick={onClose} />

      {/* menu */}
      <div
        className="fixed z-50 w-52 rounded-lg bg-white shadow-xl border border-slate-200 p-2"
        style={{
          top: position.y,
          left: position.x,
        }}
      >
        <div className="text-sm font-semibold truncate">{event.title}</div>

        <div className="text-xs text-slate-500 mt-1 truncate">
          {event.assignees?.length
            ? event.assignees.map((a) => a.name ?? a.userId).join(", ")
            : "Unassigned"}
        </div>

        <div className="mt-2 flex flex-col gap-1 text-xs">
          <button
            onClick={() => onEdit?.(event)}
            className="text-left hover:bg-slate-100 p-1 rounded"
          >
            Edit task
          </button>

          <button
            onClick={() => onDelete?.(event)}
            className="text-left hover:bg-slate-100 p-1 rounded text-red-500"
          >
            Delete
          </button>
        </div>
      </div>
    </>
  );
};
