import ErrorMessageLabel from "@/components/ErrorMessageLabel";
import SubmitButton from "@/components/SubmitButton";
import TextInput from "@/components/TextInput";
import { useState, useEffect } from "react";
import { GroupPrivileges } from "@/enums/GroupPrivilege";
import { api } from "@/auth/axios";

interface CreateGroupRequestDTO {
  name: string;
  members: string[];
  roles: GroupPrivileges[];
}

interface UserDisplayDTO {
  userId: string;
  name: string;
  surname: string;
}

const CreateGroup = () => {
  const [name, setName] = useState("");
  const [members, setMembers] = useState<string[]>([]);
  const [roles, setRoles] = useState<GroupPrivileges[]>([]);

  const [tempRoles, setTempRoles] = useState<GroupPrivileges[]>([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const [displayMembers, setDisplayMembers] = useState<{name: string, id: string}[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState<UserDisplayDTO[]>([]);
  const [selectedUser, setSelectedUser] = useState<UserDisplayDTO | null>(null);

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

  const togglePermission = (perm: GroupPrivileges) => {
    if (tempRoles.includes(perm)) {
      setTempRoles(tempRoles.filter((r) => r !== perm));
    } else {
      setTempRoles([...tempRoles, perm]);
    }
  };

  const addMember = () => {
    if (!selectedUser) return;
    if (tempRoles.length === 0) {
      setError("Please select at least one permission.");
      return;
    }

    const newMembers = tempRoles.map(() => selectedUser.userId);
    const newDisplay = tempRoles.map(() => ({
      name: `${selectedUser.name} ${selectedUser.surname}`,
      id: selectedUser.userId
    }));

    setMembers([...members, ...newMembers]);
    setRoles([...roles, ...tempRoles]);
    setDisplayMembers([...displayMembers, ...newDisplay]);

    setTempRoles([]);
    setSelectedUser(null);
    setSearchQuery("");
  };

  const submit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const payload: CreateGroupRequestDTO = {
        name,
        members: [],
        roles: [],
      };

      if (members.length > 0) {
        payload.members = members;
        payload.roles = roles;
      }

      await api.put<CreateGroupRequestDTO>("/api/groups/create", payload);
      setSuccessMessage("Group " + name + " created successfully");

      setName("");
      setMembers([]);
      setRoles([]);
      setDisplayMembers([]);

      setTimeout(() => {
        setSuccessMessage("");
      }, 3000);
    } catch {
      setError("Failed to create group");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen -slate-50 px-6 py-12">
      <div className="mx-auto flex w-full max-w-3xl flex-col gap-6 md:flex-row">
        {/* LEFT */}
        <form
          onSubmit={submit}
          className="flex-1 space-y-5 rounded-xl border border-slate-200 bg-white p-6"
        >
          <div className="space-y-1">
            <h1 className="text-xl font-semibold text-slate-900">
              Create Group
            </h1>
            <p className="text-sm text-slate-500">
              Name your group and save it.
            </p>
          </div>

          <TextInput
            type="text"
            placeholder="Group Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />

          <ErrorMessageLabel error={error} />

          <SubmitButton
            displayedText={loading ? "Creating..." : "Create Group"}
            isEnabled={!loading && name !== ""}
          />
          {successMessage && (
            <div className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium text-emerald-700">
              ✓ {successMessage}
            </div>
          )}
        </form>

        {/* RIGHT */}
        <div className="flex-1 space-y-5 rounded-xl border border-slate-200 bg-white p-6">
          <div className="space-y-1">
            <h2 className="text-xl font-semibold text-slate-900">
              Add Members
            </h2>
            <p className="text-sm text-slate-500">Configure permissions.</p>
          </div>

          <div className="relative space-y-2">
            {!selectedUser ? (
              <>
                <TextInput
                  type="text"
                  placeholder="Search user by name..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
                {searchResults.length > 0 && (
                  <ul className="w-full mt-1 max-h-48 overflow-y-auto rounded-md border border-slate-200 bg-white shadow-lg">
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
              <div className="flex items-center justify-between rounded-md border border-emerald-200 bg-emerald-50 px-3 py-2">
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

          <div className="space-y-3">
            <p className="text-sm font-medium text-zinc-700">Permissions</p>

            <div className="flex flex-col gap-2">
              {[
                GroupPrivileges.ADD_TASK,
                GroupPrivileges.REMOVE_TASK,
                GroupPrivileges.EDIT_TASK,
                GroupPrivileges.ADD_USER_TO_GROUP,
                GroupPrivileges.REMOVE_USER_FROM_GROUP,
              ].map((perm) => (
                <label
                  key={perm}
                  className="flex items-center gap-3 rounded-md px-2 py-1 text-sm text-slate-600 transition hover:bg-slate-50 hover:text-slate-900"
                >
                  <input
                    type="checkbox"
                    checked={tempRoles.includes(perm)}
                    onChange={() => togglePermission(perm)}
                    className="h-4 w-4 accent-slate-900"
                  />
                  {perm}
                </label>
              ))}
            </div>
          </div>
          <button
            type="button"
            onClick={addMember}
            className="h-10 w-full rounded-lg bg-zinc-900 text-sm font-medium text-white transition hover:bg-zinc-800"
          >
            Add to List
          </button>

          {members.length > 0 && (
            <ul className="space-y-2">
              {members.map((m, i) => (
                <li
                  key={i}
                  className="flex items-center justify-between rounded-md border border-slate-100 bg-slate-50 px-3 py-2"
                >
                  <span className="font-mono text-xs text-zinc-500">
                    {displayMembers[i]?.name}
                  </span>

                  <span className="text-sm font-medium text-zinc-800">
                    {roles[i]}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
};

export default CreateGroup;
