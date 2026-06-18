import React from "react";

const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

export default function ScheduleGrid() {
  const hours = Array.from({ length: 24 }, (_, i) => i);

  return (
    <div className="min-w-[1000px] -z-10">
      {/* Header */}
      <div
        className="grid"
        style={{
          gridTemplateColumns: "80px repeat(7, 1fr)",
        }}
      >
        <div />

        {days.map((day) => (
          <div
            key={day}
            className="h-14 border-l border-zinc-900/5 flex items-center justify-center text-sm font-medium text-zinc-700"
          >
            {day}
          </div>
        ))}
      </div>

      {/* Body */}
      <div
        className="grid"
        style={{
          gridTemplateColumns: "80px repeat(7, 1fr)",
        }}
      >
        {hours.map((hour) => (
          <React.Fragment key={hour}>
            {/* Time label */}
            <div className="h-16 border-t px-2 text-xs text-zinc-400 flex items-start justify-end pt-1">
              {String(hour).padStart(2, "0")}:00
            </div>

            {/* Day columns */}
            {days.map((day) => (
              <div
                key={`${day}-${hour}`}
                className="relative h-16 border-t border-l border-zinc-900/5"
              />
            ))}
          </React.Fragment>
        ))}
      </div>
    </div>
  );
}
