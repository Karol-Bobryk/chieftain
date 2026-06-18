import { api } from "@/auth/axios";
import ErrorMessageLabel from "@/components/ErrorMessageLabel";
import PaginationCart from "@/components/PaginationCart";
import { useState, useEffect } from "react";

interface AdminUserDTO {
  userId: string;
  name: string;
  surname: string;
  emailAddress: string;
  blocked: boolean;
}

interface SystemLogDTO {
  domain: string;
  entityId: string;
  severity: string;
  action: string;
  description: string;
  createdAt: string;
}

interface PaginatedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  page?: {
    totalPages: number;
  };
}

const AdminPanel = () => {
  const [activeTab, setActiveTab] = useState<"users" | "logs">("users");
  const [users, setUsers] = useState<AdminUserDTO[]>([]);
  const [logs, setLogs] = useState<SystemLogDTO[]>([]);
  const [searchQuery, setSearchQuery] = useState("");

  const [logsPage, setLogsPage] = useState(0);
  const [totalLogsPages, setTotalLogsPages] = useState(1);

  const [domain, setDomain] = useState("");
  const [severity, setSeverity] = useState("");
  const [error, setError] = useState("");
  const [reloadUsers, setReloadUsers] = useState(false);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const { data } = await api.get<AdminUserDTO[]>("/api/admin/users", {
          params: { search: searchQuery }
        });
        setUsers(data);
      } catch (err) {
        setError("Failed to fetch global users list.");
      }
    };

    const delayDebounceFn = setTimeout(() => {
      fetchUsers();
    }, 300);

    return () => clearTimeout(delayDebounceFn);
  }, [reloadUsers, searchQuery]);

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const params: Record<string, string | number> = { page: logsPage, size: 10 };
        if (domain) params.domain = domain;
        if (severity) params.severity = severity;

        const { data } = await api.get<PaginatedResponse<SystemLogDTO>>("/api/admin/logs", { params });
        setLogs(data.content);
        setTotalLogsPages(data.totalPages ?? data.page?.totalPages ?? 1);
      } catch (err) {
        setError("Failed to load logs timeline.");
      }
    };
    fetchLogs();
  }, [domain, severity, logsPage]);

  useEffect(() => {
    setLogsPage(0);
  }, [domain, severity]);

  const handleBlockUser = async (userId: string) => {
    setError("");
    try {
      const res = await api.get(`/api/users/${userId}/block`);
      if (res.status === 200 || res.status === 204) {
        setReloadUsers((v) => !v);
      } else {
        throw new Error("Unable to modify user blocking status.");
      }
    } catch (err) {
      setError("An unexpected error occurred while processing the block request.");
    }
  };

  return (
    <div className="mx-auto max-w-6xl p-6 space-y-6">
      <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Site Owner Administration</h1>

      <ErrorMessageLabel error={error} />
      <div className="flex space-x-6 border-b border-slate-200">
        <button
          onClick={() => setActiveTab("users")}
          className={`pb-3 text-sm font-semibold border-b-2 transition-colors ${
            activeTab === "users" ? "border-blue-600 text-blue-600" : "border-transparent text-slate-500 hover:text-slate-800"
          }`}
        >
          User Status Control
        </button>
        <button
          onClick={() => setActiveTab("logs")}
          className={`pb-3 text-sm font-semibold border-b-2 transition-colors ${
            activeTab === "logs" ? "border-blue-600 text-blue-600" : "border-transparent text-slate-500 hover:text-slate-800"
          }`}
        >
          System Activity Inspector
        </button>
      </div>
      {activeTab === "users" && (
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 space-y-4">
          <div className="flex items-center mb-4">
            <input
              type="text"
              placeholder="Search users by name or surname..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full sm:w-96 rounded-lg border border-slate-200 px-4 py-2 text-sm outline-none focus:border-blue-500 transition-colors"
            />
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead>
              <tr className="border-b border-slate-200 text-xs uppercase text-slate-400 tracking-wider">
                <th className="pb-3 px-4">User</th>
                <th className="pb-3 px-4">Email</th>
                <th className="pb-3 px-4">Account Status</th>
                <th className="pb-3 px-4 text-right">Actions</th>
              </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
              {users.length === 0 ? (
                <tr>
                  <td colSpan={4} className="py-8 text-center text-slate-400">No user records available.</td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr key={user.userId} className="hover:bg-slate-50/50 transition-colors">
                    <td className="py-3.5 px-4 font-medium text-slate-900">{user.name} {user.surname}</td>
                    <td className="py-3.5 px-4">{user.emailAddress}</td>
                    <td className="py-3.5 px-4">
                        <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                          user.blocked ? "bg-red-50 text-red-700" : "bg-green-50 text-green-700"
                        }`}>
                          {user.blocked ? "Banned" : "Active"}
                        </span>
                    </td>
                    <td className="py-3.5 px-4 text-right">
                      {!user.blocked && (
                        <button
                          onClick={() => handleBlockUser(user.userId)}
                          className="rounded-lg bg-red-600 px-3 py-1 text-xs font-medium text-white hover:bg-red-700 transition-colors"
                        >
                          Ban User
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              )}
              </tbody>
            </table>
          </div>
        </div>
      )}
      {activeTab === "logs" && (
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 space-y-4">
          <div className="flex flex-wrap gap-4">
            {["Domain", "Severity"].map((label) => {
              const isDomain = label === "Domain";
              return (
                <div key={label} className="flex flex-col gap-1.5">
                  <label className="text-xs font-semibold uppercase text-slate-400 tracking-wider">{label}</label>
                  <select
                    value={isDomain ? domain : severity}
                    onChange={(e) => isDomain ? setDomain(e.target.value) : setSeverity(e.target.value)}
                    className="rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-sm text-slate-700 outline-none focus:border-blue-500 transition-colors"
                  >
                    <option value="">All {isDomain ? "Domains" : "Severities"}</option>
                    {isDomain ? (
                      <>
                        <option value="USER">User</option>
                        <option value="GROUP">Group</option>
                        <option value="ORGANIZATION">Organization</option>
                        <option value="TASK">Task</option>
                      </>
                    ) : (
                      <>
                        <option value="INFO">Info</option>
                        <option value="WARNING">Warning</option>
                        <option value="ERROR">Error</option>
                      </>
                    )}
                  </select>
                </div>
              );
            })}
          </div>
          <div className="max-h-96 overflow-auto border border-slate-200 rounded-lg">
            <table className="w-full text-left text-sm text-slate-600 table-fixed">
              <thead className="sticky top-0 bg-slate-50 border-b border-slate-200 text-xs uppercase text-slate-400 tracking-wider z-10">
              <tr>
                <th className="py-2.5 px-4 w-44">Date Logged</th>
                <th className="py-2.5 px-4 w-32">Domain</th>
                <th className="py-2.5 px-4 w-28">Severity</th>
                <th className="py-2.5 px-4 w-40">Action Token</th>
                <th className="py-2.5 px-4">Description</th>
              </tr>
              </thead>
              <tbody className="font-mono text-xs divide-y divide-slate-100 bg-white">
              {logs.length === 0 ? (
                <tr>
                  <td colSpan={5} className="font-sans py-8 text-center text-slate-400">No matching entries tracked.</td>
                </tr>
              ) : (
                logs.map((log, i) => (
                  <tr key={`${log.entityId}-${i}`} className="hover:bg-slate-50/50 transition-colors">
                    <td className="truncate py-2.5 px-4 text-slate-400">
                      {log.createdAt ? new Date(log.createdAt).toLocaleString() : "N/A"}
                    </td>
                    <td className="truncate py-2.5 px-4 font-semibold text-slate-600">{log.domain}</td>
                    <td className="py-2.5 px-4">
                        <span className={`inline-block rounded px-1.5 py-0.5 font-bold ${
                          log.severity === "ERROR" ? "bg-red-50 text-red-700" :
                            log.severity === "WARNING" ? "bg-amber-50 text-amber-700" : "bg-blue-50 text-blue-700"
                        }`}>
                          {log.severity}
                        </span>
                    </td>
                    <td className="truncate py-2.5 px-4 font-semibold text-slate-700">{log.action}</td>
                    <td className="font-sans py-2.5 px-4 text-slate-800 break-words">{log.description}</td>
                  </tr>
                ))
              )}
              </tbody>
            </table>
          </div>
          <PaginationCart pageNumber={logsPage} setPageNumber={setLogsPage} totalPages={totalLogsPages} />
        </div>
      )}
    </div>
  );
};

export default AdminPanel;