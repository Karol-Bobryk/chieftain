import type { TaskEvent } from "@/interfaces/RootTaskDisplayDTO";
import { useState } from "react";

interface EditTaskFormProps {
  event: TaskEvent;
  onClose: () => void;
  onSave: (updated: Partial<TaskEvent>) => void;
}

const EditTaskForm = ({ event, onClose, onSave }: EditTaskFormProps) => {
  const [title, setTitle] = useState(event.title);
  const [start, setStart] = useState(event.start);
  const [description, setDescription] = useState(event.description ?? "");
  const [end, setEnd] = useState(event.end);

  const handleSubmit = (e: React.SubmitEvent) => {
    e.preventDefault();

    onSave({
      ...event,
      title,
      start,
      end,
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
