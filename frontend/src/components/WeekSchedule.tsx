import { api } from "@/auth/axios";
import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import {
  toTaskEvent,
  type RootTaskDisplayDTO,
  type TaskEvent,
} from "@/interfaces/RootTaskDisplayDTO";

import { Calendar, momentLocalizer } from "react-big-calendar";

import moment from "moment";
import "react-big-calendar/lib/css/react-big-calendar.css";
import getCurrentWeekRange from "@/helpers/getWeekPaginate";
import { TaskEventCard } from "./TaskEventCard";
import { TaskEventMenu } from "./TaskEventMenu";
import deleteTask from "@/helpers/deleteTask";

const localizer = momentLocalizer(moment);

export interface GetTasksInGroupResponse {
  tasks: RootTaskDisplayDTO[];
}

const WeekSchedule = () => {
  const { groupId } = useParams();
  if (!groupId) return <div>Invalid group</div>;

  const [tasks, setTasks] = useState<RootTaskDisplayDTO[]>([]);
  const [events, setEvents] = useState<TaskEvent[]>([]);

  const [selectedEvent, setSelectedEvent] = useState<TaskEvent | null>(null);
  const [menuPosition, setMenuPosition] = useState<{
    x: number;
    y: number;
  } | null>(null);

  const fetchTasks = async () => {
    const payload = getCurrentWeekRange(0);

    const res = await api.get<GetTasksInGroupResponse>(
      `/api/groups/${groupId}/tasks`,
      { params: payload },
    );

    setTasks(res.data.tasks);
  };

  const handleSelectEvent = (event: TaskEvent, e: React.SyntheticEvent) => {
    const nativeEvent = e as unknown as MouseEvent;

    setSelectedEvent(event);
    setMenuPosition({
      x: nativeEvent.clientX,
      y: nativeEvent.clientY,
    });
  };

  // fetch tasks
  useEffect(() => {
    fetchTasks();
  }, [groupId]);

  // when tasks change
  useEffect(() => {
    const mapped: TaskEvent[] = tasks.map((task) => toTaskEvent(task));
    setEvents(mapped);
  }, [tasks]);

  const components = useMemo(
    () => ({
      event: TaskEventCard,
    }),
    [],
  );

  const handleDeleteTask = async (event: TaskEvent) => {
    try {
      await deleteTask(event);
      const index = tasks.findIndex((e) => e.taskId === event.taskId);
      if (index !== -1) {
        fetchTasks();
      }
    } catch {} // TODO: errors
  };

  return (
    <div className="h-screen w-full bg-zinc-50 p-4">
      <div className="h-full rounded-xl border border-zinc-200 bg-white shadow-sm overflow-hidden">
        <Calendar
          localizer={localizer}
          events={events}
          startAccessor="start"
          endAccessor="end"
          defaultView="week"
          views={["week"]}
          step={30}
          timeslots={2}
          popup
          components={components}
          onSelectEvent={handleSelectEvent}
          style={{ height: "100%" }}
          formats={{
            timeGutterFormat: (date, culture, localizer) =>
              localizer?.format(date, "HH:mm", culture) ?? "",
          }}
        />
      </div>

      {selectedEvent && menuPosition && (
        <TaskEventMenu
          event={selectedEvent}
          position={menuPosition}
          onClose={() => {
            setSelectedEvent(null);
            setMenuPosition(null);
          }}
          onEdit={(event) => console.log("edit", event)}
          onDelete={(event) => handleDeleteTask(event)}
        />
      )}
    </div>
  );
};

export default WeekSchedule;
