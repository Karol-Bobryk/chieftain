import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { api } from "@/auth/axios";
import { GroupPrivileges } from "@/enums/GroupPrivilege";
import WeekSchedule from "@/components/WeekSchedule";

const GroupDetails = () => {
  const { groupId } = useParams<{ groupId: string }>();
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState<UserDisplayDTO[]>([]);
  const [selectedUser, setSelectedUser] = useState<UserDisplayDTO | null>(null);
  const [selectedPermissions, setSelectedPermissions] = useState<GroupPrivileges[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const availablePermissions = Object.values(GroupPrivileges);

  useEffect(() => {
    const fetchUsers = async () => {
      if (searchQuery.length < 2) {
        setSearchResults([]);
        return;
      }
      try {
        const res = await api.get<UserDisplayDTO[]>(`/api/users/search?q=${searchQuery}`);
        setSearchResults(res.data);
      } catch (err) {
        console.error("Failed to search users", err);
      }
    };
    const delay = setTimeout(fetchUsers, 300);
    return () => clearTimeout(delay);
  }, [searchQuery]);

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
      if (!selectedUser) {
        throw new Error("Please select a user.");
      }
      const memberIds = [selectedUser.userId];
      await api.put(`/api/groups/${groupId}/members`, {
        memberIds,
        permissions: selectedPermissions,
      });
      setSelectedUser(null);
      setSearchQuery("");
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

              <div className="mb-4 relative space-y-2">
                <label className="block text-sm font-medium text-zinc-700 mb-1">
                  Search User
                </label>
                {!selectedUser ? (
                  <>
                    <input
                      type="text"
                      placeholder="Search user by name..."
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      className="w-full rounded-xl border border-zinc-200 bg-zinc-50 px-4 py-2 text-sm focus:border-zinc-400 focus:outline-none"
                    />
                    {searchResults.length > 0 && (
                      <ul className="absolute z-10 w-full mt-1 max-h-48 overflow-y-auto rounded-md border border-slate-200 bg-white shadow-lg">
                        {searchResults.map((user) => (
                          <li
                            key={user.userId}
                            className="cursor-pointer px-4 py-2 hover:bg-slate-50 border-b border-slate-100 last:border-0"
                            onClick={() => {
                              setSelectedUser(user);
                              setSearchQuery("");
                              setSearchResults([]);
                            }}
                          >
                            <div className="font-medium text-sm text-slate-800">
                              {user.name} {user.surname}
                            </div>
                          </li>
                        ))}
                      </ul>
                    )}
                  </>
                ) : (
                  <div className="flex items-center justify-between rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-2">
                    <div className="text-sm font-medium text-emerald-800">
                      {selectedUser.name} {selectedUser.surname}
                    </div>
                    <button
                      type="button"
                      onClick={() => setSelectedUser(null)}
                      className="text-sm font-medium text-emerald-700 hover:text-emerald-900"
                    >
                      Clear
                    </button>
                  </div>
                )}
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