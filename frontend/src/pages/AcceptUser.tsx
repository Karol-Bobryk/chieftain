import { api } from "@/auth/axios";
import ErrorMessageLabel from "@/components/ErrorMessageLabel";
import UserAcceptListRow from "@/components/UserAcceptListRow";
import { SystemRole } from "@/enums/SystemRoles";
import { useState, useEffect } from "react";
import type { UserDisplayDTO } from "@/interfaces/UserDisplayDTO";

interface UserAcceptResponse {
  content: UserDisplayDTO[];
  page: {
    size: Number;
    number: Number;
    totalElements: Number;
    totalPages: Number;
  };
}

const AcceptUser = () => {
  const [reload, setReload] = useState(false);
  const [users, setUsers] = useState<
    {
      content: UserDisplayDTO;
      role: SystemRole;
    }[]
  >([]);

  const [error, setError] = useState("");

  const fetchUsers = async () => {
    try {
      const { data } = await api.get<UserAcceptResponse>(
        "/api/users/awaiting-acceptance?size=100",
      );
      const user_data = data.content.map((e) => ({
        content: e,
        role: SystemRole.GROUP_USER,
      }));

      setUsers(user_data);
    } catch (error) {}
  };

  useEffect(() => {
    fetchUsers();
  }, [reload]);

  const acceptUser = async (userId: string, role: SystemRole) => {
    setError("");

    try {
      const res = await api.post(`/api/users/${userId}/accept`, {
        role: role,
      });

      if (res.status !== 200)
        throw new Error(`Server error: ${res.status}, cannot accept the user`);

      await fetchUsers();
      setReload((v) => !v);
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("An unexpected error occurred");
      }
    }
  };

  return (
    <div className="mx-auto max-w-4xl space-y-3 p-6">
      <p className="mb-4 text-sm font-medium text-slate-500">Accept Users</p>
      {users.length === 0 ? (
        <div className="rounded-xl border border-dashed border-slate-200 bg-white py-10 text-center text-sm text-slate-500">
          No users found
        </div>
      ) : (
        users.map(({ content, role }) => (
          <UserAcceptListRow
            key={content.userId}
            userId={content.userId}
            name={content.name}
            surname={content.surname}
            role={role}
            onAccept={acceptUser}
            onReject={() => {}}
          />
        ))
      )}
      <ErrorMessageLabel error={error} />
    </div>
  );
};

export default AcceptUser;
