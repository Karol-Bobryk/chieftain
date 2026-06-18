interface GroupListHeaderProps {
  value: number;
  onChange: React.ChangeEventHandler<HTMLSelectElement>;
}

const GroupListHeaders = ({ value, onChange }: GroupListHeaderProps) => {
  return (
    <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
          Groups
        </h1>
        <p className="text-sm text-zinc-500">
          Browse organization groups and members.
        </p>
      </div>

      <div className="flex items-center gap-3">
        <span className="text-sm text-zinc-500">Page size</span>

        <select
          value={value}
          onChange={onChange}
          className="rounded-xl border border-zinc-200 bg-white px-3 py-2 text-sm text-zinc-700 outline-none transition focus:border-zinc-300"
        >
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
      </div>
    </div>
  );
};

export default GroupListHeaders;
