import { useState } from "react";
import { useParams } from "react-router-dom";
import { api } from "@/auth/axios";
import { GroupPrivileges } from "@/enums/GroupPrivilege";
import WeekSchedule from "@/components/WeekSchedule";

const GroupDetails = () => {
  const { groupId } = useParams<{ groupId: string }>();
  const [memberIdsInput, setMemberIdsInput] = useState("");
  const [selectedPermissions, setSelectedPermissions] = useState<GroupPrivileges[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const availablePermissions = Object.values(GroupPrivileges);

  const handleTogglePermission = (permission: GroupPrivileges) => {
    setSelectedPermissions((prev) =>
      prev.includes(permission)
        ? prev.filter((p) => p !== permission)
        : [...prev, permission]
    );
  };

  const handleAddMembersSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      const memberIds = memberIdsInput
        .split(",")
        .map((id) => id.trim())
        .filter((id) => id.length > 0);

      if (memberIds.length === 0) {
        throw new Error("Please provide at least one valid user ID.");
      }
      await api.put(`/api/groups/${groupId}/members`, {
        memberIds,
        permissions: selectedPermissions,
      });
      setMemberIdsInput("");
      setSelectedPermissions([]);
      alert("Members successfully added!");
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || "Failed to add members.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-zinc-50 px-6 py-10">
      <div className="mx-auto space-y-6 max-w-6xl">
        {/* HEADER */}
        <div className="rounded-3xl border border-zinc-200 bg-white p-6">
          <h1 className="text-2xl font-semibold text-zinc-900">Group Tasks</h1>
          <p className="mt-1 text-sm text-zinc-500">
            Weekly overview of tasks and group management.
          </p>
        </div>
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <WeekSchedule />
          </div>
          <div className="space-y-6">
            <form onSubmit={handleAddMembersSubmit} className="rounded-3xl border border-zinc-200 bg-white p-6">
              <h2 className="text-xl font-semibold text-zinc-900 mb-4">Add New Members</h2>

              {error && <div className="mb-4 text-sm text-red-600">{error}</div>}

              <div className="mb-4">
                <label className="block text-sm font-medium text-zinc-700 mb-1">
                  User IDs (comma-separated UUIDs)
                </label>
                <input
                  type="text"
                  value={memberIdsInput}
                  onChange={(e) => setMemberIdsInput(e.target.value)}
                  placeholder="e.g. 8fa12b..., 3bc89a..."
                  className="w-full rounded-xl border border-zinc-200 bg-zinc-50 px-4 py-2 text-sm focus:border-zinc-400 focus:outline-none"
                />
              </div>
              <div className="mb-6">
                <span className="block text-sm font-medium text-zinc-700 mb-2">
                  Assign Privileges
                </span>
                <div className="space-y-2">
                  {availablePermissions.map((permission) => (
                    <label key={permission} className="flex items-center space-x-3 text-sm text-zinc-600 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={selectedPermissions.includes(permission)}
                        onChange={() => handleTogglePermission(permission)}
                        className="h-4 w-4 rounded border-zinc-300 text-zinc-900 focus:ring-zinc-900"
                      />
                      <span>{permission.replace(/_/g, " ")}</span>
                    </label>
                  ))}
                </div>
              </div>
              <button
                type="submit"
                disabled={isLoading}
                className="w-full rounded-xl bg-zinc-900 px-4 py-2.5 text-sm font-semibold text-white hover:bg-zinc-800 disabled:bg-zinc-400"
              >
                {isLoading ? "Adding..." : "Add Members"}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GroupDetails;