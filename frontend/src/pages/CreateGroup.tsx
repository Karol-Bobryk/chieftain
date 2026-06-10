import ErrorMessageLabel from "@/components/ErrorMessageLabel";
import SubmitButton from "@/components/SubmitButton";
import TextInput from "@/components/TextInput";
import { useState } from "react";
import JoinOrganization from "./JoinOrganization";

const CreateGroup = () => {
  const [name, setName] = useState("");
  const [members, setMembers] = useState<string[]>([]);
  const [roles, setRoles] = useState<string[]>([]);

  const [tempMemberId, setTempMemberId] = useState("");
  const [tempRoles, setTempRoles] = useState<string[]>([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const togglePermission = (perm: string) => {
    if (tempRoles.includes(perm)) {
      setTempRoles(tempRoles.filter((r) => r !== perm));
    } else {
      setTempRoles([...tempRoles, perm]);
    }
  };

  const addMember = () => {
    if (!tempMemberId) return;
    if (tempRoles.length === 0) {
      setError("Please select at least one permission.");
      return;
    }

    const newMembers = tempRoles.map(() => tempMemberId);

    setMembers([...members, ...newMembers]);
    setRoles([...roles, ...tempRoles]);

    setTempMemberId("");
    setTempRoles([]);
  };

  const submit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const token = localStorage.getItem("accessToken");
      const payload: any = { name };

      if (members.length > 0) {
        payload.members = members;
        payload.roles = roles;
      }

      const res = await fetch("/api/groups/create", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) throw new Error();
    } catch {
      setError("Failed to create group");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-50 px-6 py-12">
      <div className="flex w-full max-w-4xl flex-col gap-6 md:flex-row">
        <form
          onSubmit={submit}
          className="flex-1 space-y-5 rounded-3xl border border-zinc-200 bg-white p-8"
        >
          <div className="space-y-1">
            <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
              Create Group
            </h1>
            <p className="text-sm text-zinc-500">Name your group and save.</p>
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
            isEnabled={loading}
          />
        </form>

        <div className="flex-1 space-y-5 rounded-3xl border border-zinc-200 bg-white p-8">
          <div className="space-y-1">
            <h3 className="text-2xl font-semibold tracking-tight text-zinc-900">
              Add Members
            </h3>
            <p className="text-sm text-zinc-500">
              Pre-configure member permissions.
            </p>
          </div>

          <TextInput
            type="text"
            placeholder="Member UUID"
            value={tempMemberId}
            onChange={(e) => setTempMemberId(e.target.value)}
          />

          <div className="space-y-3">
            <strong className="text-sm font-medium text-zinc-700">
              Permissions:
            </strong>
            <div className="flex flex-col gap-3">
              {[
                "ADD_TASK",
                "REMOVE_TASK",
                "EDIT_TASK",
                "ADD_USER_TO_GROUP",
                "REMOVE_USER_FROM_GROUP",
              ].map((perm) => (
                <label
                  key={perm}
                  className="flex cursor-pointer items-center gap-3 text-sm text-zinc-600 transition hover:text-zinc-900"
                >
                  <input
                    type="checkbox"
                    checked={tempRoles.includes(perm)}
                    onChange={() => togglePermission(perm)}
                    className="h-4 w-4 rounded border-zinc-300 accent-zinc-900"
                  />
                  {perm}
                </label>
              ))}
            </div>
          </div>

          <button
            type="button"
            onClick={addMember}
            className="h-11 w-full rounded-xl border border-zinc-200 bg-zinc-50 text-sm font-medium text-zinc-900 transition hover:bg-zinc-100"
          >
            Add to List
          </button>

          {members.length > 0 && (
            <ul className="space-y-2 rounded-xl bg-zinc-50 p-4 text-sm text-zinc-600">
              {members.map((m, i) => (
                <li key={i} className="break-all">
                  <span className="font-mono text-xs">
                    {m.substring(0, 8)}...
                  </span>{" "}
                  -{" "}
                  <strong className="font-medium text-zinc-900">
                    {roles[i]}
                  </strong>
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
