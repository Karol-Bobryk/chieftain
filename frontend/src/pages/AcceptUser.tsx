import ErrorMessageLabel from "@/components/ErrorMessageLabel";
import SubmitButton from "@/components/SubmitButton";
import { useState, useEffect } from "react";

interface UserDisplayDTO {
  userId: string;
  name: string;
  surname: string;
}

const AcceptUser = () => {
  const [form, setForm] = useState({
    userId: "",
    role: "GROUP_USER",
  });

  const [users, setUsers] = useState<UserDisplayDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchUsers = async () => {
    const token = localStorage.getItem("accessToken");
    const res = await fetch("/api/users/awaiting-acceptance?size=100", {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) {
      const data = await res.json();
      setUsers(data.content || []);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleChange =
    (field: keyof typeof form) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
      setForm((prev) => ({
        ...prev,
        [field]: e.target.value,
      }));
    };

  const submit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    const { userId, role } = form;
    const token = localStorage.getItem("accessToken");

    try {
      const res = await fetch(`/api/users/${userId}/accept`, {
        // TODO: axios please
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ role }),
      });

      if (!res.ok) {
        const errorData = await res.json().catch(() => null);
        console.error("Backend returned an error:", res.status, errorData);
        throw new Error(errorData?.message || `Server error: ${res.status}`);
      }
      await fetchUsers();
      setForm((prev) => ({ ...prev, userId: "" }));
    } catch (err) {
      console.error("Request failed completely:", err);

      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("An unexpected error occurred");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-50 px-6">
      <form
        onSubmit={submit}
        className="w-full max-w-lg space-y-5 rounded-3xl border border-zinc-200 bg-white p-8"
      >
        <div className="space-y-1">
          <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
            Accept User
          </h1>
          <p className="text-sm text-zinc-500">
            Approve a user and assign their role.
          </p>
        </div>

        <select
          value={form.userId}
          onChange={handleChange("userId")}
          required
          className="h-11 w-full appearance-none rounded-xl border border-zinc-200 bg-white px-4 text-sm outline-none focus:border-zinc-400"
        >
          <option value="" disabled>
            Select a user to accept
          </option>
          {users.map((user) => (
            <option key={user.userId} value={user.userId}>
              {user.name} {user.surname}
            </option>
          ))}
        </select>

        <select
          value={form.role}
          onChange={handleChange("role")}
          className="h-11 w-full appearance-none rounded-xl border border-zinc-200 bg-white px-4 text-sm outline-none focus:border-zinc-400"
        >
          <option value="GROUP_USER">Group User</option>
          <option value="TASK_MASTER">Task Master</option>
          <option value="OWNER">Owner</option>
        </select>

        <ErrorMessageLabel error={error} />

        <SubmitButton
          displayedText={loading ? "Accepting..." : "Accept User"}
          isEnabled={loading}
        />
      </form>
    </div>
  );
};

export default AcceptUser;
