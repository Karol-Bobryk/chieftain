import { SystemRole } from "@/enums/SystemRoles";

type UserAcceptListRowProps = {
  userId: string;
  name: string;
  surname: string;
  role: SystemRole;
  onRoleChange?: (role: SystemRole) => void;
  onAccept?: (userId: string, role: SystemRole) => void;
  onReject?: (userId: string, role: SystemRole) => void;
};

const UserAcceptListRow = ({
  userId,
  name,
  surname,
  role,
  onRoleChange,
  onAccept,
  onReject,
}: UserAcceptListRowProps) => {
  return (
    <div className="group flex items-center justify-between rounded-xl border border-slate-100 bg-white px-5 py-3 shadow-sm transition hover:bg-slate-50 hover:shadow-md">
      {/* Left */}
      <div className="flex flex-col sm:flex-row sm:gap-6">
        <span className="font-medium text-slate-900">{name}</span>
        <span className="text-slate-600">{surname}</span>
      </div>

      {/* Right */}
      <div className="flex items-center gap-4 opacity-0 transition-opacity duration-200 group-hover:opacity-100">
        {/* Role dropdown */}
        <select
          value={role}
          onChange={(e) => onRoleChange?.(e.target.value as SystemRole)}
          className="rounded-md border border-slate-200 bg-white px-2 py-1 text-sm text-slate-700 focus:outline-none focus:ring-1 focus:ring-slate-300"
        >
          {[SystemRole.GROUP_USER, SystemRole.TASK_MASTER].map((roleName) => (
            <option key={roleName} value={roleName}>
              {roleName}
            </option>
          ))}
        </select>

        {/* Actions */}
        <button
          onClick={() => onAccept?.(userId, role)}
          className="text-sm font-medium text-green-600 hover:text-green-700 transition"
        >
          Accept
        </button>

        <button
          onClick={() => onReject?.(userId, role)}
          className="text-sm font-medium text-red-500 hover:text-red-600 transition"
        >
          Reject
        </button>
      </div>
    </div>
  );
};

export default UserAcceptListRow;
