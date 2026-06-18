import type { TaskEvent } from "@/interfaces/RootTaskDisplayDTO";

type TaskEventCardProps = {
  event: TaskEvent;
};

export const TaskEventCard = ({ event }: TaskEventCardProps) => {
  return (
    <div className="h-full w-full rounded-md px-2 py-1 text-[11px] leading-tight text-blue-950">
      <div className="absolute left-1 top-2 bottom-2 w-[5px] bg-white/90 rounded-full" />

      <div className="font-semibold truncate">{event.title}</div>

      <div className="text-blue-700/80 truncate">
        {event.assignees?.length
          ? event.assignees.map((a) => a.name ?? a.userId).join(", ")
          : "Unassigned"}
      </div>
    </div>
  );
};
