import type { TaskEvent } from "@/interfaces/RootTaskDisplayDTO";
import { useState } from "react";

interface EditTaskFormProps {
  event: TaskEvent;
  availableUsers?: { userId: string; name?: string }[];
  onClose: () => void;
  onSave: (updated: Partial<TaskEvent>) => void;
}

const EditTaskForm = ({ event, availableUsers = [], onClose, onSave }: EditTaskFormProps) => {
  const [title, setTitle] = useState(event.title);
  const [start, setStart] = useState(event.start);
  const [description, setDescription] = useState(event.description ?? "");
  const [end, setEnd] = useState(event.end);
  const [assignees, setAssignees] = useState<{ userId: string; name?: string }[]>(event.assignees || []);

  const handleSubmit = (e: React.SubmitEvent) => {
    e.preventDefault();

    onSave({
      ...event,
      title,
      start,
      end,
      description,
      assignees,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-3">
      <input
        className="border p-2 rounded"
        value={title?.toString()}
        onChange={(e) => setTitle(e.target.value)}
      />
      <textarea
        className="border p-2 rounded resize-none h-20"
        placeholder="Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <div className="border rounded p-2 max-h-32 overflow-y-auto flex flex-col gap-1 bg-white">
        <span className="text-xs font-semibold text-zinc-500 mb-1">Assignees</span>
        {availableUsers.map((user) => {
          const isAssigned = assignees.some((a) => a.userId === user.userId);
          return (
            <label
              key={user.userId}
              className="flex items-center gap-2 text-sm text-zinc-700 hover:bg-zinc-50 p-1 rounded cursor-pointer select-none"
            >
              <input
                type="checkbox"
                checked={isAssigned}
                onChange={() => {
                  if (isAssigned) {
                    setAssignees(assignees.filter((a) => a.userId !== user.userId));
                  } else {
                    setAssignees([...assignees, user]);
                  }
                }}
                className="rounded border-zinc-300 text-blue-600 focus:ring-blue-500 w-4 h-4 cursor-pointer"
              />
              <span>{user.name || user.userId}</span>
            </label>
          );
        })}
        {availableUsers.length === 0 && (
          <span className="text-xs text-zinc-400 italic">No group members available</span>
        )}
      </div>
      <input
        type="datetime-local"
        className="border p-2 rounded"
        value={start?.toISOString().slice(0, 16)}
        onChange={(e) => setStart(new Date(e.target.value))}
      />

      <input
        type="datetime-local"
        className="border p-2 rounded"
        value={end?.toISOString().slice(0, 16)}
        onChange={(e) => setEnd(new Date(e.target.value))}
      />

      <div className="flex justify-end gap-2">
        <button type="button" onClick={onClose} className="text-gray-500">
          Cancel
        </button>
        <button
          type="submit"
          className="bg-blue-500 text-white px-3 py-1 rounded"
        >
          Save
        </button>
      </div>
    </form>
  );
};
export default EditTaskForm;
