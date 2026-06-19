import type { GroupDisplayDTO } from "@/interfaces/GroupDisplayDTO";
import { Link } from "react-router-dom";

const GroupCart = (group: GroupDisplayDTO) => {
  return (
    <div
      key={group.groupId}
      className="rounded-3xl border border-zinc-200 bg-white p-6 mb-2 mt-2"
    >
      <div className="border-b border-zinc-100 pb-4">
        <Link to={`/group/${group.groupId}`}>
          <h2 className="text-lg font-semibold text-zinc-900">{group.name}</h2>
        </Link>
        <p className="mt-1 text-sm text-zinc-500">
          {group.members.length} member
          {group.members.length !== 1 ? "s" : ""}
        </p>
      </div>

      <div className="mt-4 space-y-3">
        {group.members.map((member) => (
          <div
            key={member.userId}
            className="flex items-center justify-between rounded-xl border border-zinc-100 bg-zinc-50 px-4 py-3"
          >
            <div>
              <p className="font-medium text-zinc-900">
                {member.name} {member.surname}
              </p>
              <p className="text-sm text-zinc-500">{member.userId}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
export default GroupCart;
