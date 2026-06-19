import WeekSchedule from "@/components/WeekSchedule";

const GroupDetails = () => {
  return (
    <div className="min-h-screen bg-zinc-50 px-6 py-10">
      <div className="mx-auto space-y-6">
        {/* HEADER */}
        <div className="rounded-3xl border border-zinc-200 bg-white p-6">
          <h1 className="text-2xl font-semibold text-zinc-900">Group Tasks</h1>

          <p className="mt-1 text-sm text-zinc-500">
            Weekly overview of tasks for this group.
          </p>
        </div>

        <WeekSchedule />
      </div>
    </div>
  );
};

export default GroupDetails;
